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
 * Created on Jan 6, 2006
 * @author Stephan Richard Palm
 * 
 * 
 * In this class the information are stored
 * if for a given catalog the MediaOptions are overriden
 * and how they are overriden.
 */
package net.sourceforge.mecat.catalog.option.preferences;

import net.sourceforge.mecat.catalog.gui.features.desktop.FeatureDesktop;
import net.sourceforge.mecat.catalog.medium.Medium;
import net.sourceforge.mecat.catalog.medium.features.Feature;
import net.sourceforge.mecat.catalog.option.Options;
import net.sourceforge.mecat.srp.utils.NiceClass;

public class CatalogMediaOption extends AbstractMediaOption {

    public void setMediaOptionOverriden(boolean overriden) {
        // Set default from Options if the options
        // becomes available now.
        if (!mediaOptionOverriden && overriden) {
            wantedMedia.clear();
            MediaOption mo = null;
            
            if (Options.AppPrefs.getMediaOption().isMediaOptionOverriden())
                mo = Options.AppPrefs.getMediaOption();
            else
                mo = DefaultPreferences.defaultPreferences.getMediaOption();

            for (Class<? extends Medium> mediumClass : getMedia())
                setWanted(mediumClass, mo.isWanted(mediumClass));
        }
        // Set the new status for overriding media options
        mediaOptionOverriden = overriden;
    }
    
    public void setMediumOptionOverrriden(Class <? extends Medium> mediumClass, boolean overriden) {
        // Set default from Options if the options
        // becomes available now.
        if (!isMediumOptionOverriden(mediumClass) && overriden) {
            
            MediaOption mo = null;
            
            if (Options.AppPrefs.getMediaOption().isMediumOptionOverriden(mediumClass))
                mo = Options.AppPrefs.getMediaOption();
            else
                mo = DefaultPreferences.defaultPreferences.getMediaOption();

            for (Class<? extends Feature> featureClass : getFeatures(mediumClass)) 
                for (NiceClass<FeatureDesktop> desktopClass : Options.desktops) 
                    setWanted(mediumClass, featureClass, desktopClass.getClasstype(), 
                                mo.isWanted(mediumClass, featureClass, desktopClass.getClasstype()));
        }
        // Set the new status for overriding media options
        mediumOverriden.put(mediumClass, overriden);
    }

}
