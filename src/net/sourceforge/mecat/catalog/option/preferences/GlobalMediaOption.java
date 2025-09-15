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
 */
package net.sourceforge.mecat.catalog.option.preferences;

import net.sourceforge.mecat.catalog.gui.features.desktop.FeatureDesktop;
import net.sourceforge.mecat.catalog.medium.Medium;
import net.sourceforge.mecat.catalog.medium.features.Feature;
import net.sourceforge.mecat.catalog.option.Options;
import net.sourceforge.mecat.srp.utils.NiceClass;

public class GlobalMediaOption extends AbstractMediaOption {

    @Override
    public void setMediaOptionOverriden(boolean overriden) {
        // Set default from Options if the options
        // becomes available now.
        if (!mediaOptionOverriden && overriden) {
            wantedMedia.clear();

            MediaOption mo = DefaultPreferences.defaultPreferences.getMediaOption();

            for (Class<? extends Medium> mediumClass : getMedia())
                setWanted(mediumClass, mo.isWanted(mediumClass));
        }
        // Set the new status for overriding media options
        mediaOptionOverriden = overriden;
    }

    @Override
    public void setMediumOptionOverrriden(Class <? extends Medium> mediumClass, boolean overriden) {
        // Set default from Options if the options
        // becomes available now.
        if (!isMediumOptionOverriden(mediumClass) && overriden) {
            
            MediaOption mo = DefaultPreferences.defaultPreferences.getMediaOption();

            for (Class<? extends Feature> featureClass : getFeatures(mediumClass)) 
                for (NiceClass<FeatureDesktop> desktopClass : Options.desktops) 
                    setWanted(mediumClass, featureClass, desktopClass.getClasstype(), 
                                mo.isWanted(mediumClass, featureClass, desktopClass.getClasstype()));
        }
        // Set the new status for overriding media options
        mediumOverriden.put(mediumClass, overriden);
    }

/*    public boolean loadFromEntry(Entry entry) {
        for (Class<? extends Medium> mediumClass : getMedia()) {
            Entry mediumEntry = entry.getSubEntry(mediumClass.getName());
            if (mediumEntry == null)
                continue;
            String vis = mediumEntry.getAttribute("Visible");
            if (vis != null) 
                wantedMedia.put(mediumClass, vis.equalsIgnoreCase("true"));
            
            for (Class<? extends Feature> featureClass : getFeatures(mediumClass)) {
                Entry featureEntry = mediumEntry.getSubEntry(featureClass.getName());
                if (featureEntry == null)
                    continue;
                for (NiceClass<FeatureDesktop> desktopClass : Options.desktops) {
                    String desktopVis = featureEntry.getAttribute(desktopClass.getClasstype().getName());
                    if (desktopVis != null) 
                        wantedFeatures.get(mediumClass).get(featureClass).put(desktopClass.getClasstype(), desktopVis.equalsIgnoreCase("true"));
                }
            }
        }
        return true;
    }

    public void saveToEntry(Entry entry) {
        for (Class<? extends Medium> mediumClass : getMedia()) {
            Entry mediumEntry = entry.createSubEntry(mediumClass.getName());
            mediumEntry.setAttribute("Visible", wantedMedia.get(mediumClass).toString());
            
            for (Class<? extends Feature> featureClass : getFeatures(mediumClass)) {
                Entry featureEntry = mediumEntry.createSubEntry(featureClass.getName());
                for (NiceClass<FeatureDesktop> desktopClass : Options.desktops)
                    featureEntry.setAttribute(desktopClass.getClasstype().getName(), wantedFeatures.get(mediumClass).get(featureClass).get(desktopClass.getClasstype()).toString());
            }
        }
    }*/
}
