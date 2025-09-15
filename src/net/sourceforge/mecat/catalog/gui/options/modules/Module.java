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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Vector;
import java.util.regex.Pattern;

/**
 * 
 * Created on 13.01.2007
 *
 * @author Stephan Richard Palm
 *
 *  Structure of a module file
 *  A module file consist of groups.
 *  A group is defined as a grouptype like
 *  [Classpath]
 *  and an arbitrary number of lines
 *  Every line consisting out of [.*] is a grouptype,
 *  i.e. any line that starts with [ and the last not white space is ].
 *
 *
 *  It is unlikely that this will grow behind classpaths, the modules main path, 
 *  the unique name and a version number information.
 *  This is because additional information like description, ...
 *  should be stored within the modules jar file.
 */
public class Module {
    
    Pattern option = Pattern.compile("\\[.*\\]");

    
    /**
     * A list of all classpath needed by the module
     */
    final Vector<String> cps = new Vector<String>();
    
    /**
     * A class path to the main module storage position
     */
    String main;

    
    String uniqueName;

    String version;
    
    
    enum GroupsType {
        Classpath, // Indicates a list of class paths needed for the module
        Main, // Points to the main jar
        UniqueName, // The unique name of the module
        Version // The version of the module
    }
    
    
    public Module(File moduleFile) {
        try {
            GroupsType currentType = null;
            String line = null;
            BufferedReader r = new BufferedReader(new FileReader(moduleFile));
            while ((line = r.readLine()) != null) {
                if (option.matcher(line.trim()).matches()) {
                    String optionStr = line.trim();
                    optionStr = optionStr.substring(1, optionStr.length() - 1);
                    currentType = GroupsType.valueOf(optionStr);
                    continue;
                }
                if (currentType == null)
                    continue;

                switch (currentType) {
                case Classpath:
                    cps.add(line.trim());
                case Main:
                    main = line.trim();
                case UniqueName:
                    uniqueName = line.trim();
                case Version:
                    version = line.trim();
                }
            }
            r.close();
            
            if (!cps.contains(main))
                cps.add(main);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public String getMain() {
        return main;
    }
    
    public List<String> getClasspaths() {
        return cps;
    }

    public URL[] getClasspathsURLs() {
        Vector<URL> ret = new Vector<URL>(cps.size());
        for (String str : cps)
            try {
                URL url = new URL("file", null, str); 
                ret.add(url);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            
        return ret.toArray(new URL[ret.size()]);
    }

    public boolean equals(Object o) {
        if (!(o instanceof Module))
            return false;
        Module module = (Module) o;
        if (!sameModule(module))
            return false;

        if (version == null)
            return false;
        if (module.version == null)
            return false;
        if (!module.version.equals(version))
            return false;
        return true;
    }
    
    /**
     * Returns true if it's the same module, i.e.
     * it has the same unique name. But it can different version number.
     * If you want version number taken into account you'll take {@link #equals(Object)}
     * @param module
     * @return
     */
    public boolean sameModule(Module module) {
        if (uniqueName == null)
            return false;
        if (module.uniqueName == null)
            return false;
        if (!module.uniqueName.equals(uniqueName))
            return false;
        return true;
    }

    public int compareVersion(Module oldModule) {
        if (version == null)
            return 0;
        if (oldModule.version == null)
            return 0;
        return version.compareTo(oldModule.version);
    }
    
    
    public String toString () {
        return uniqueName + " " + version;
    }
}
