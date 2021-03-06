/*
 *  Copyright (C) 2010 reuillon
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.openmole.misc.pluginmanager.internal;

import java.io.File;
import java.io.FileFilter;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.collections15.BidiMap;
import org.apache.commons.collections15.bidimap.DualHashBidiMap;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.service.packageadmin.ExportedPackage;
import org.openmole.misc.exception.InternalProcessingError;
import org.openmole.misc.pluginmanager.IPluginManager;

public class PluginManager implements IPluginManager {

    final String defaultPatern = ".*\\.jar";
    BidiMap<Bundle, File> files = new DualHashBidiMap<Bundle, File>();
    Map<Bundle, Iterable<Bundle>> resolvedBundles = new HashMap<Bundle, Iterable<Bundle>>();

    @Override
    public synchronized Bundle load(File path) throws InternalProcessingError {
        try {
            Bundle b = Activator.getContext().installBundle(path.toURI().toString());

            if (!resolvedBundles.containsKey(b)) {
                b.start();
                files.put(b, path);
                resolvedBundles.put(b, getDependancies(b));
            }
            return b;
            //allBundles.add(b);

        } catch (BundleException ex) {
            throw new InternalProcessingError(ex, ex.getLocalizedMessage());
        }
    }

    @Override
    public List<Bundle> loadDir(File path, final String pattern) throws InternalProcessingError {
        return loadDir(path, new FileFilter() {

            @Override
            public boolean accept(File file) {
                return file.isFile() && file.exists() && file.getName().matches(pattern);
            }
        });
    }

    @Override
    public synchronized List<Bundle> loadDir(File path, FileFilter fiter) throws InternalProcessingError {
        if (!path.exists() || !path.isDirectory()) {
            return Collections.EMPTY_LIST;
        }

        try {
            List<Bundle> bundles = new LinkedList<Bundle>();

            for (File f : path.listFiles(fiter)) {
                Bundle b = Activator.getContext().installBundle(f.toURI().toString());

                if (!resolvedBundles.containsKey(b)) {
                    bundles.add(b);
                    files.put(b, f);
                    resolvedBundles.put(b, Collections.EMPTY_LIST);
                }
            }

            for (Bundle b : bundles) {
                b.start();
                resolvedBundles.put(b, getDependancies(b));
            }

            /*for(Bundle b :  Activator.getContext().getBundles()) {
            System.out.println(b.getLocation());
            }*/

            return bundles;
            //allBundles.add(b);

        } catch (BundleException ex) {
            throw new InternalProcessingError(ex, ex.getLocalizedMessage());
        }
    }

    Set<Bundle> getDependancies(Bundle b) {
        Set<Bundle> dependencies = new HashSet<Bundle>();

        //TODO find a way to do that faster
        for (Bundle bundle : resolvedBundles.keySet()) {
            for (Bundle ib : getDependingBundles(bundle)) {
                if (ib.equals(b)) {
                    dependencies.add(bundle);
                    for (Bundle depBun : getDependencies(bundle)) {
                        dependencies.add(depBun);
                    }
                }
            }
        }
        return dependencies;
    }

    //FIXME May not work with class in jars mentioned in Bundle-ClassPath : check it
   /* Iterable<String> getClassNames(Bundle b) {
    Collection<String> ret = new LinkedList<String>();

    Stack<String> toExplore = new Stack<String>();
    toExplore.add("");

    while (!toExplore.isEmpty()) {

    String currentDir = toExplore.pop();


    Enumeration e = b.getEntryPaths(currentDir);

    while (e.hasMoreElements()) {
    String cur = e.nextElement().toString();

    if(cur.endsWith("/")) {
    toExplore.push(cur);
    } else {
    if(cur.endsWith(".class")){
    String className = cur.substring(0, cur.length()-6).replace('/', '.');
    ret.add(className);
    }
    }

    }
    }
    return ret;
    }*/
    public synchronized Iterable<Bundle> getDependencies(Bundle b) {
        if (resolvedBundles.containsKey(b)) {
            return resolvedBundles.get(b);
        } else {
            return Collections.EMPTY_LIST;
        }
    }

    @Override
    public synchronized File getPluginForClass(Class c) {
        Bundle b = Activator.getPackageAdmin().getBundle(c);
        return files.get(b);
    }

    @Override
    public synchronized Iterable<File> getPluginAndDependanciesForClass(Class c) {
        Bundle b = Activator.getPackageAdmin().getBundle(c);

        if (b == null) {
            return Collections.EMPTY_LIST;
        }

        Collection<File> ret = new LinkedList<File>();

        File plugin = files.get(b);
        if (plugin != null) {
            ret.add(plugin);
        }

        for (Bundle dep : getDependencies(b)) {
            File depPlugin = files.get(dep);
            if (depPlugin != null) {
                ret.add(depPlugin);
            }
        }

        return ret;
    }

    @Override
    public Bundle load(String path) throws InternalProcessingError {
        return load(new File(path));
    }

    @Override
    public boolean isClassProvidedByAPlugin(Class c) {
        boolean ret = resolvedBundles.containsKey(Activator.getPackageAdmin().getBundle(c));
        return ret;
    }

    @Override
    public synchronized void unload(File path) throws InternalProcessingError {
        Bundle b = getBundle(path);

        for (Bundle db : getDependingBundles(b)) {
            unloadBundle(db);
        }

        unloadBundle(b);
    }

    private void unloadBundle(Bundle bundle) throws InternalProcessingError {
        if (bundle != null) {
            try {
                bundle.uninstall();
                files.remove(bundle);
                resolvedBundles.remove(bundle);
            } catch (BundleException ex) {
                throw new InternalProcessingError(ex);
            }
        }
    }

    @Override
    public synchronized Bundle getBundle(File path) throws InternalProcessingError {
        return files.getKey(path);
    }

    private Iterable<Bundle> getDependingBundles(Bundle b) {
        Collection<Bundle> ret = new LinkedList<Bundle>();

        ExportedPackage[] exportedPackages = Activator.getPackageAdmin().getExportedPackages(b);

        if (exportedPackages != null) {
            for (ExportedPackage exportedPackage : exportedPackages) {
                for (Bundle ib : exportedPackage.getImportingBundles()) {
                    ret.add(ib);
                }
            }
        }

        return ret;
    }

    @Override
    public void unload(String path) throws InternalProcessingError {
        unload(new File(path));
    }

    @Override
    public Collection<Bundle> loadDir(File path) throws InternalProcessingError {
        return loadDir(path, defaultPatern);
    }

    @Override
    public Collection<Bundle> loadDir(String path) throws InternalProcessingError {
        return loadDir(new File(path));
    }
}


