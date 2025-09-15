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
 * Created on Dec 7, 2005
 * @author Stephan Richard Palm
 * 
 */
package net.sourceforge.mecat.catalog.gui.options.features.imagefeature;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.Vector;

import net.sourceforge.mecat.catalog.datamanagement.Catalog;
import net.sourceforge.mecat.catalog.datamanagement.Entry;
import net.sourceforge.mecat.catalog.datamanagement.PersistentThroughEntry;
import net.sourceforge.mecat.catalog.datamanagement.Util;
import net.sourceforge.mecat.catalog.medium.Medium;
import net.sourceforge.mecat.catalog.medium.features.impl.Ident;
import net.sourceforge.mecat.catalog.option.Options;

public class ImageInformationServer {

    protected static final ImageInformationServer defaultImageInformationServer = new ImageInformationServer();
    
    Set<String> directories = new HashSet<String>();
    Set<String> checkedDirs = new HashSet<String>();
    Map<URL, ImageInformation> infos = new HashMap<URL, ImageInformation>();    
    
    ResourceBundle res = Options.getI18N(ImageInformationServer.class);
    
    public void saveTo(Catalog catalog) {
        for (Map.Entry<URL, ImageInformation> entry : infos.entrySet())
            Util.saveToEntry(entry.getValue(), catalog.createEntry("ImageInformation"));
    }
    
    public void loadFrom(Catalog catalog) {
        for (Iterator<? extends Entry> i = catalog.getIterator(); i.hasNext(); ) {
            Entry entry = i.next();
            PersistentThroughEntry pte = Util.loadFromEntry(entry);
            if (pte instanceof ImageInformation) {
                ImageInformation info = ( ImageInformation ) pte;
                infos.put(info.getFileName(), info);
            }
        }
    }
    
    public ImageInformation getInfo(Medium medium, URL picURL) {
        if (picURL == null)
            return null;        
        
        if (infos.containsKey(picURL))
            return infos.get(picURL);

        String fileName = picURL.getFile();
        if (fileName.indexOf(medium.getFeature(Ident.class).getUUID().toString()) != -1) 
            for (String dir : checkedDirs) 
                if (fileName.startsWith(dir))
                    return null;
        
        ImageInformation info = new ImageInformation();
        info.setFileName(picURL);
        try {
            if (info.checkSize()) {
                infos.put(picURL, info);
                return info;
            }
            infos.put(picURL, null);
            return null;
        } catch (ImageGone e) {
            // If there is no image
            // nothing to do
            return null;
        }
    }
    
    public ImageInformationServer() {
    }

    protected void checkDir(String name) {
//        System.out.println("[ImgServ] Checking dir " + name);
        int num = 0;
        String dirName = name + System.getProperty("file.separator", "/");
        File dir = new File(dirName);
        if (!dir.exists()) {
            return;
        }
        if (!dir.isDirectory()){
            return;
        }
        File dirs[] = dir.listFiles(Options.dirFilter);
            
        if (dirs != null)
        for (File f : dirs) {
            File findit[] = new File(f.toString()).listFiles(Options.imgFilter);
            if (findit != null)
                for (File find : findit) {
                    if (Options.verbosity > 4)
                        System.out.println(res.getString("[ImgServ]") + " " + res.getString("Check file [FILE]").replaceAll("\\[FILE\\]", "" + find));
                    URL url;
                    try {
                        url = find.toURL();
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                        continue;
                    }
                    checkImage(url);
                }
            checkDir(f.toString());
        }
    }
    
    protected void checkImage(URL url) {
        if (infos.containsKey(url))
            return;
        ImageInformation info = new ImageInformation();
        info.setFileName(url);
        try {
            if (info.checkSize())
                infos.put(url, info);
            else
                infos.put(url, null);
        } catch (ImageGone e) {
            // If there is no image
            // nothing to do
        }
    }
    
    public void addImage(final URL url) {
        if (Options.verbosity > 2)
            System.out.println(res.getString("[ImgServ]") + " " + res.getString("Add image [URL]").replaceAll("\\[URL\\]", ""  + toString()));
        infos.remove(url);
        checkImage(url);
    }

    public void addDir(final String directory) {
        if (directories.contains(directory))
            return;
        if (Options.verbosity > 2)
            System.out.println(res.getString("[ImgServ]") + " " + res.getString("Add dir [DIR]").replaceAll("\\[DIR\\]", directory));
        directories.add(directory);
        Thread t = new Thread() {
            public void run(){
                checkDir(directory);
                checkedDirs.add(directory);
            }
        };
        t.start();
    }
    
    // How often should the files be checked
    long defaultInterval = 12 * 3600; /* every 12 HOURS*/
    
    // Special interval for every protocol
    // if the protocol is not in the map
    // the defautl interval has to be set
    Map<String, Long> protocolInterval = new HashMap<String, Long>(){{
        put("file", defaultInterval);
        put("http", (long)-1); /* never */
    }};
    
    // For every interval in the protocolInterval map
    // which is set to -1 we an entry here
    // this map decides how long we going to keep
    // information that is not updated
    Map<String, Long> forgetTimer = new HashMap<String, Long>(){{
        put("http", (long) 48 * 3600 ); /* forget after two days */
    }};    
    
    long defaultForgetTime = 0;
    
    /**
     * This function checks for all know images, 
     * if they still exist and changes information
     * about them as they change.
     *
     * Images will not be checked 
     * every time, they will be checked depending on 
     * the time since the last check.
     * 
     */
    public void checkAll() {
        for (ImageInformation info : new Vector<ImageInformation>(infos.values())) {
            try {
                String protocol = info.fileName.getProtocol();
                Long interval = protocolInterval.get(protocol);
                if (interval == null)
                    interval = defaultInterval;
                
                long timeSinceLastCheck = System.currentTimeMillis() - info.last_check;
                
                if (interval == -1) {
                    Long forget = forgetTimer.get(protocol);
                    if (forget == 0)
                        forget = defaultForgetTime;
                    if (forget < timeSinceLastCheck)
                        infos.remove(info.getFileName());
                    
                    return;
                }
                
                if (interval < timeSinceLastCheck)
                    info.checkSize();
                
            } catch (ImageGone e) {
                e.printStackTrace();
                infos.remove(info.getFileName());
            }
        }
    }

    public static ImageInformationServer getDefaultImageInformationServer() {
        return defaultImageInformationServer;
    }
    
}
