/*
 *  MeCat - Media Catalog more information on
 *  http://mecat.sourceforge.net/
 *  Copyright (C) 2004, Stephan Richard Palm, All Rights Reserved.
 *
 *  This program is free software; you can redistribute it and/or modify 
 *  it under the terms of the GNU General Public License as published by 
 *  the Free Software Foundation; either version 2 of the License, or 
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but 
 *  WITHOUT ANY WARRANTY; without even the implied warranty of 
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU 
 *  General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License 
 *  along with this program; if not, write to the Free Software Foundation, 
 *  Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

/**
 * Created on 13.01.2007
 * @author Stephan Richard Palm
 * 
 */
package net.sourceforge.mecat.catalog.gui.options.modules;

import java.io.File;
import java.io.FileFilter;
import java.util.Vector;
import java.util.regex.Pattern;

public class Modules {

    static String mecatJar = "mecat(-[0-9\\.]*)?.jar"; 
    static Pattern mecatJarPattern = Pattern.compile("(.*(/|\\\\)|\\A)" + mecatJar);
    static String confDirs[] = {
            "/usr/share/mecat/modules"
    };

    static FileFilter moduleFilter = new FileFilter(){
        Pattern pattern = Pattern.compile(".*.mecat.module");

        public boolean accept(File file) {
            if (!pattern.matcher(file.getName()).matches())
                return false;
            if (mecatJarPattern.matcher(file.toString()).matches())
                return false;
            return true;
        }
    };
    
    Vector<Module> modules = new Vector<Module>();
    
    public Vector<Module> getModules() {
        return modules;
    }

    public Modules() {
        String paths[] = System.getProperty("java.class.path").split(System.getProperty("path.separator"));
        String apolloPath = null;
        // The mecat lib starts with mecat 1. directly at the beginning \A or 2. after a / 3. or after a \
        // ==> (.*(/|\\\\)|\A)
        // The name of the jar can contain a version number 
        // ==> (-[0-9\\.]*)?
        for (String str : paths) {
            if (mecatJarPattern.matcher(str).matches()) {
                String split[] = str.split(mecatJar);
                String path = "";
                if (split.length > 0)
                    path = split[0];
                investigateDirectory(path);
            }
        }
        for (String dir : confDirs)
        if (new File(dir).exists())
            investigateDirectory(dir);
    }
    
    public void investigateDirectory(String path){
        if (path.length() == 0)
            path = ".";
        File file = new File(path);
modulesLoop:
        for (File f : file.listFiles(moduleFilter))  {
            Module module = new Module(f);
            if (modules.contains(module))
                continue;
            for (Module oldModule : modules)
                if (oldModule.sameModule(module)) {
                    if (module.compareVersion(oldModule) > 0) {
                        modules.remove(oldModule);
                        break;
                    } else 
                        continue modulesLoop;
                }
                        
            modules.add(module);
        }
    }
}
