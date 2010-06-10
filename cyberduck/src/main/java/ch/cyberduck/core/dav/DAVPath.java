package ch.cyberduck.core.dav;

/*
 *  Copyright (c) 2008 David Kocher. All rights reserved.
 *  http://cyberduck.ch/
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  Bug fixes, suggestions and comments should be sent to:
 *  dkocher@cyberduck.ch
 */

import ch.cyberduck.core.*;
import ch.cyberduck.core.i18n.Locale;
import ch.cyberduck.core.io.service.BandwidthThrottleService;
import ch.cyberduck.core.io.IOResumeException;
import java.io.FileInputStream;

import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.apache.webdav.lib.WebdavResource;
import org.apache.webdav.lib.methods.DepthSupport;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.MessageFormat;

/**
 * @version $Id: $
 */
public class DAVPath extends Path {
    private static Logger log = Logger.getLogger(DAVPath.class);

    static {
        PathFactory.addFactory(Protocol.WEBDAV, new Factory());
    }

    private static class Factory extends PathFactory<DAVSession> {
        @Override
        protected Path create(DAVSession session, String path, int type) {
            return new DAVPath(session, path, type);
        }

        @Override
        protected Path create(DAVSession session, String parent, String name, int type) {
            return new DAVPath(session, parent, name, type);
        }

        @Override
        protected Path create(DAVSession session, String path, Local file) {
            return new DAVPath(session, path, file);
        }

        @Override
        protected <T> Path create(DAVSession session, T dict) {
            return new DAVPath(session, dict);
        }
    }

    /**
	 * @uml.property  name="session"
	 * @uml.associationEnd  
	 */
    private final DAVSession session;

    protected DAVPath(DAVSession s, String parent, String name, int type) {
        super(parent, name, type);
        this.session = s;
    }

    protected DAVPath(DAVSession s, String path, int type) {
        super(path, type);
        this.session = s;
    }

    protected DAVPath(DAVSession s, String parent, Local file) {
        super(parent, file);
        this.session = s;
    }

    protected <T> DAVPath(DAVSession s, T dict) {
        super(dict);
        this.session = s;
    }

    /**
	 * @return
	 * @throws ConnectionCanceledException
	 * @uml.property  name="session"
	 */
    @Override
    public DAVSession getSession() throws ConnectionCanceledException {
        if(null == session) {
            throw new ConnectionCanceledException();
        }
        return session;
    }

    @Override
    public void readSize() {
        try {
            this.getSession().check();
//            this.getSession().message(MessageFormat.format(Locale.localizedString("Getting size of {0}", "Status"),
//                    this.getName()));

            this.getSession().getClient().setPath(this.attributes.isDirectory() ?
                    this.getAbsolute() + Path.DELIMITER : this.getAbsolute());

            this.getSession().getClient().setProperties(WebdavResource.BASIC, DepthSupport.DEPTH_1);
            attributes.setSize(this.getSession().getClient().getGetContentLength());
        }
        catch(IOException e) {
            this.error("Cannot read file attributes", e);
        }
    }

    @Override
    public void readTimestamp() {
        try {
            this.getSession().check();
//            this.getSession().message(MessageFormat.format(Locale.localizedString("Getting timestamp of {0}", "Status"),
//                    this.getName()));

            this.getSession().getClient().setPath(this.attributes.isDirectory() ?
                    this.getAbsolute() + Path.DELIMITER : this.getAbsolute());

            this.getSession().getClient().setProperties(WebdavResource.BASIC, DepthSupport.DEPTH_1);
            attributes.setModificationDate(this.getSession().getClient().getGetLastModified());
        }
        catch(IOException e) {
            this.error("Cannot read file attributes", e);
        }
    }


    @Override
    public void readPermission() {
        ;
    }

    @Override
    public void delete() {
        log.debug("delete:" + this.toString());
        try {
            this.getSession().check();
//            this.getSession().message(MessageFormat.format(Locale.localizedString("Deleting {0}", "Status"),
//                    this.getName()));

            if(!this.getSession().getClient().deleteMethod(this.getAbsolute())) {
                throw new IOException(this.getSession().getClient().getStatusMessage());
            }
        }
        catch(IOException e) {
            if(this.attributes.isFile()) {
                this.error("Cannot delete file", e);
            }
            if(this.attributes.isDirectory()) {
                this.error("Cannot delete folder", e);
            }
        }
    }


    @Override
    public AttributedList<Path> list() {
        final AttributedList<Path> childs = new AttributedList<Path>();
        try {
            this.getSession().check();
            this.getSession().message(MessageFormat.format(Locale.localizedString("Listing directory {0}", "Status"),
                    this.getName()));

            this.getSession().setWorkdir(this);
            this.getSession().getClient().setContentType("text/xml");
            WebdavResource[] resources = this.getSession().getClient().listWebdavResources();

            for(final WebdavResource resource : resources) {
                boolean collection = false;
                if(null != resource.getResourceType()) {
                    collection = resource.getResourceType().isCollection();
                }
                Path p = PathFactory.createPath(this.getSession(), resource.getPath(),
                        collection ? Path.DIRECTORY_TYPE : Path.FILE_TYPE);
                p.setParent(this);

                p.attributes.setOwner(resource.getOwner());
                if(resource.getGetLastModified() > 0) {
                    p.attributes.setModificationDate(resource.getGetLastModified());
                }
                if(resource.getCreationDate() > 0) {
                    p.attributes.setCreationDate(resource.getCreationDate());
                }
                p.attributes.setSize(resource.getGetContentLength());

                childs.add(p);
            }
        }
        catch(IOException e) {
            childs.attributes().setReadable(false);
            this.error("Listing directory failed", e);
        }
        return childs;
    }

    @Override
    public void mkdir(boolean recursive) {
        log.debug("mkdir:" + this.getName());
        try {
            if(recursive) {
                if(!this.getParent().exists()) {
                    this.getParent().mkdir(recursive);
                }
            }
            this.getSession().check();
//            this.getSession().message(MessageFormat.format(Locale.localizedString("Making directory {0}", "Status"),
//                    this.getName()));

            this.getSession().getClient().setContentType("text/xml");
            if(!this.getSession().getClient().mkcolMethod(this.getAbsolute())) {
                throw new IOException(this.getSession().getClient().getStatusMessage());
            }
        }
        catch(IOException e) {
            this.error("Cannot create folder", e);
        }

    }

    @Override
    public boolean isWritePermissionsSupported() {
        return false;
    }

    @Override
    public void writePermissions(Permission perm, boolean recursive) {
//            log.debug("changePermissions:" + perm);
//            try {
//                this.getSession().check();
//                this.getSession().message(Locale.localizedString("Changing permission of {0} to {1}", "Status", "") + " " + perm.getOctalString() + " (" + this.getName() + ")");
//                this.getSession().getClient().aclMethod(this.getAbsolute(), new Ace[]{});
//            }
//            catch(IOException e) {
//                this.error("Cannot change permissions", e);
//            }
    }

    @Override
    public boolean isWriteModificationDateSupported() {
        return false;
    }

    @Override
    public void writeModificationDate(long millis) {
        ;
    }

    @Override
    public void rename(AbstractPath renamed) {
        log.debug("rename:" + renamed);
        try {
            this.getSession().check();
//            this.getSession().message(MessageFormat.format(Locale.localizedString("Renaming {0} to {1}", "Status"),
//                    this.getName(), renamed.getName()));

            if(!this.getSession().getClient().moveMethod(this.getAbsolute(), renamed.getAbsolute())) {
                throw new IOException(this.getSession().getClient().getStatusMessage());
            }
            this.setPath(renamed.getAbsolute());
        }
        catch(IOException e) {
            if(attributes.isFile()) {
                this.error("Cannot rename file", e);
            }
            if(attributes.isDirectory()) {
                this.error("Cannot rename folder", e);
            }
        }
    }

    @Override
    public void copy(AbstractPath copy) {
        try {
            this.getSession().check();
//            this.getSession().message(MessageFormat.format(Locale.localizedString("Copying {0} to {1}", "Status"),
//                    this.getName(), copy));

            if(!this.getSession().getClient().copyMethod(this.getAbsolute(), copy.getAbsolute())) {
                throw new IOException(this.getSession().getClient().getStatusMessage());
            }
        }
        catch(IOException e) {
            if(this.attributes.isFile()) {
                this.error("Cannot copy file", e);
            }
            if(this.attributes.isDirectory()) {
                this.error("Cannot copy folder", e);
            }
        }
    }

    @Override
    public void download(final BandwidthThrottleService throttle, final StreamListener listener, final boolean check) {
        if(attributes.isFile()) {
            OutputStream out = null;
            InputStream in = null;
            try {
                if(check) {
                    this.getSession().check();
                }
                if(this.getStatus().isResume()) {
//                    this.getSession().getClient().addRequestHeader("Range", "bytes=" + this.getStatus().getCurrent() + "-");
                }
//                this.getSession().getClient().addRequestHeader("Accept-Encoding", "gzip");
                in = this.getSession().getClient().getMethodData(this.getAbsolute());
                if(null == in) {
                    throw new IOException("Unable opening data stream");
                }
                if(!this.getSession().getClient().isResume()) {
                    getStatus().setCurrent(0);
                }
                out = new Local.OutputStream(this.getLocal(), this.getStatus().isResume());

                this.download(in, out, throttle, listener);
            }
            catch(IOException e) {
                this.error("Download failed", e);
            }
            finally {
                IOUtils.closeQuietly(in);
                IOUtils.closeQuietly(out);
            }
        }
        if(attributes.isDirectory()) {
            this.getLocal().mkdir(true);
        }
    }

    @Override
    public void upload(final BandwidthThrottleService throttle, final StreamListener listener, final Permission p, final boolean check) {
        try {
            if(check) {
                this.getSession().check();
            }
            if(attributes.isFile()) {
//                this.getSession().message(MessageFormat.format(Locale.localizedString("Uploading {0}", "Status"),
//                        this.getName()));

//                final InputStream in = new Local.InputStream(this.getLocal());
                final InputStream in = new FileInputStream(this.getLocal().getAbsolute());
                try {
                    final Status stat = this.getStatus();
                    if(stat.isResume()) {
//                        this.getSession().getClient().addRequestHeader("Content-Range", "bytes "
//                                + stat.getCurrent()
//                                + "-" + (this.getLocal().attributes.getSize() - 1)
//                                + "/" + this.getLocal().attributes.getSize()
//                        );
                        long skipped = in.skip(stat.getCurrent());
                        log.info("Skipping " + skipped + " bytes");
                        if(skipped < stat.getCurrent()) {
                            throw new IOResumeException("Skipped " + skipped + " bytes instead of " + stat.getCurrent());
                        }
                    }
                    if(!this.getSession().getClient().putMethod(this.getAbsolute(),
                            new InputStreamRequestEntity(in, this.getLocal().attributes.getSize() - stat.getCurrent(), this.getLocal().getMimeType()) {
                                boolean requested = false;

                                @Override
                                public void writeRequest(OutputStream out) throws IOException {
                                    if(requested) {
                                        in.reset();
                                        stat.reset();
                                        stat.setCurrent(0);
                                    }
                                    try {
                                        DAVPath.this.upload(out, in, throttle, listener);
                                    }
                                    finally {
                                        requested = true;
                                    }
                                }

                                @Override
                                public boolean isRepeatable() {
                                    return true;
                                }
                            })) {
                        // Upload failed
                        throw new IOException(this.getSession().getClient().getStatusMessage());
                    }
                }
                finally {
                    IOUtils.closeQuietly(in);
                }
            }
            if(attributes.isDirectory()) {
                this.mkdir();
            }
        }
        catch(IOException e) {
            this.error("Upload failed", e);
        }
    }

    @Override
    public String toHttpURL() {
        return this.toURL();
    }
}