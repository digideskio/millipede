package ch.cyberduck.core.cloud;

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

import ch.cyberduck.core.i18n.Locale;

/**
 * @version  $Id: Distribution.java 5736 2010-01-14 18:15:49Z dkocher $
 */
public class Distribution {

    /**
	 * Configuration sucessfully applied and distributed
	 * @uml.property  name="deployed"
	 */
    private boolean deployed;
    /**
	 * Deployment enabled
	 * @uml.property  name="enabled"
	 */
    private boolean enabled;
    /**
	 * Logging enabled
	 * @uml.property  name="logging"
	 */
    private boolean logging;
    /**
	 * @uml.property  name="url"
	 */
    private String url;
    /**
	 * @uml.property  name="status"
	 */
    private String status;
    /**
	 * @uml.property  name="cnames" multiplicity="(0 -1)" dimension="1"
	 */
    private String cnames[];

    /**
	 * @uml.property  name="method"
	 * @uml.associationEnd  
	 */
    private Method method;

    public static interface Method {
        public abstract String toString();

        public abstract String getProtocol();

        public abstract String getContext();
    }

    /**
	 * @uml.property  name="dOWNLOAD"
	 * @uml.associationEnd  
	 */
    public static final Method DOWNLOAD = new Method() {
        public String toString() {
            return Locale.localizedString("Download (HTTP)", "S3");
        }

        public String getProtocol() {
            return "http://";
        }

        public String getContext() {
            return "";
        }
    };

    /**
	 * @uml.property  name="sTREAMING"
	 * @uml.associationEnd  
	 */
    public static final Method STREAMING = new Method() {
        public String toString() {
            return Locale.localizedString("Streaming (RTMP)", "S3");
        }

        public String getProtocol() {
            return "rtmp://";
        }

        public String getContext() {
            return "/cfx/st";
        }
    };

    /**
     *
     */
    public Distribution() {
        this(false, false, null, null, new String[]{}, false);
    }

    /**
     * @param enabled
     * @param url
     * @param status
     */
    public Distribution(boolean enabled, String url, String status) {
        this(enabled, url, status, new String[]{});
    }

    /**
     *
     * @param enabled
     * @param url
     * @param status
     * @param logging
     */
    public Distribution(boolean enabled, String url, String status, boolean logging) {
        this(enabled, enabled, url, status, new String[]{}, logging);
    }

    /**
     * @param enabled    Deployment Enabled
     * @param url        Where to find this distribution
     * @param status     Status Message about Deployment Status
     * @param cnames     Multiple CNAME aliases of this distribution
     */
    public Distribution(boolean enabled, String url, String status, String[] cnames) {
        this(enabled, enabled, url, status, cnames);
    }

    /**
     * @param enabled    Deployment Enabled
     * @param deployed Deployment Status is about to be changed
     * @param url        Where to find this distribution
     * @param status     Status Message about Deployment Status
     * @param cnames     Multiple CNAME aliases of this distribution
     */
    public Distribution(boolean enabled, boolean deployed, String url, String status, String[] cnames) {
        this(enabled, deployed, url, status, cnames, false);
    }

    /**
     * @param enabled    Deployment Enabled
     * @param deployed Deployment Status is about to be changed
     * @param url        Where to find this distribution
     * @param status     Status Message about Deployment Status
     * @param cnames     Multiple CNAME aliases of this distribution
     * @param logging
     */
    public Distribution(boolean enabled, boolean deployed, String url, String status, String[] cnames, boolean logging) {
        this(enabled, deployed, url, status, cnames, logging, DOWNLOAD);
    }

    /**
     *
     * @param enabled
     * @param deployed
     * @param url
     * @param status
     * @param cnames
     * @param logging
     * @param method
     */
    public Distribution(boolean enabled, boolean deployed, String url, String status, String[] cnames, boolean logging, Method method) {
        this.enabled = enabled;
        this.deployed = deployed;
        this.url = url;
        this.status = status;
        this.cnames = cnames;
        this.logging = logging;
        this.method = method;
    }

    /**
	 * @return  True if distribution is enabled
	 * @uml.property  name="enabled"
	 */
    public boolean isEnabled() {
        return enabled;
    }

    /**
	 * @return
	 * @uml.property  name="deployed"
	 */
    public boolean isDeployed() {
        return deployed;
    }

    /**
	 * @return
	 * @uml.property  name="logging"
	 */
    public boolean isLogging() {
        return logging;
    }

    /**
	 * @param logging
	 * @uml.property  name="logging"
	 */
    public void setLogging(boolean logging) {
        this.logging = logging;
    }

    /**
	 * @return  Null if not available
	 * @uml.property  name="url"
	 */
    public String getUrl() {
        return url;
    }

    /**
	 * @return  "Unknown" if distribution status is not known
	 * @uml.property  name="status"
	 */
    public String getStatus() {
        if(null == status) {
            return Locale.localizedString("Unknown");
        }
        return status;
    }

    /**
     * @return Empty array if no CNAMEs configured for this distribution
     */
    public String[] getCNAMEs() {
        if(null == cnames) {
            return new String[]{};
        }
        return cnames;
    }

    /**
	 * @return
	 * @uml.property  name="method"
	 */
    public Method getMethod() {
        return method;
    }

    /**
	 * @param method
	 * @uml.property  name="method"
	 */
    public void setMethod(Method method) {
        this.method = method;
    }
}
