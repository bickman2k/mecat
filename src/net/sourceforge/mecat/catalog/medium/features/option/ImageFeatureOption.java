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
 * Created on Sep 13, 2006
 * @author Stephan Richard Palm
 * 
 */
package net.sourceforge.mecat.catalog.medium.features.option;

import java.net.MalformedURLException;
import java.net.URL;

import net.sourceforge.mecat.catalog.datamanagement.Entry;
import net.sourceforge.mecat.catalog.datamanagement.Util;
import net.sourceforge.mecat.catalog.datamanagement.Util.Argument;
import net.sourceforge.mecat.catalog.gui.options.features.imagefeature.ImageInformationServer;
import net.sourceforge.mecat.catalog.option.Options;

public class ImageFeatureOption implements FeatureOption {

    final String optionsName;
    boolean dirStorage = true;
    URL dirLocation = null;

    public ImageFeatureOption(final String optionsName, final URL dirLocation) {
        this(optionsName);
        if (dirLocation == null) {
            dirStorage = false;
            return;
        }
        dirStorage = true;                
        this.dirLocation = dirLocation;
    }

    public ImageFeatureOption(final String optionsName) {
        this.optionsName = optionsName;
        try {
            this.dirLocation =  new URL("file", null, Options.USER_OPTION_DIR);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
    
    public boolean isDirStorage() {
        return dirStorage;
    }
    
    public void setDirStorage(boolean dirStorage) {
        this.dirStorage = dirStorage;
    }

    public URL getDirLocation() {
        return dirLocation;
    }
    
    public void setDirLocation(URL location) {
        this.dirLocation = location;
    }

    public boolean loadFromEntry(Entry entry) {

        if (dirStorage) {
            URL dir = getDirLocation();
            if (dir != null)
                ImageInformationServer.getDefaultImageInformationServer().addDir(dir.getFile());
        }
        
        return true;
    }

    public void saveToEntry(Entry entry) {
        Util.addArgument(entry, new Argument(0, String.class, optionsName));
        if (dirStorage) {
            try {
                URL defaultLocation =  new URL("file", null, Options.USER_OPTION_DIR);
                if (!defaultLocation.sameFile(dirLocation))
                    Util.addArgument(entry, new Argument(1, URL.class, dirLocation));
            } catch (MalformedURLException e) {
                e.printStackTrace();
                Util.addArgument(entry, new Argument(1, URL.class, dirLocation));
            }
            
        }
        else
            Util.addArgument(entry, new Argument(1, URL.class, null));
    }

    public String getName() {
        return optionsName;
    }
    
}
