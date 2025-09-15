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

import java.net.URL;
import java.util.Iterator;
import java.util.LinkedHashMap;

import net.sourceforge.mecat.catalog.datamanagement.Catalog;
import net.sourceforge.mecat.catalog.datamanagement.Entry;
import net.sourceforge.mecat.catalog.datamanagement.xml.XMLCatalogFactory;
import net.sourceforge.mecat.catalog.datamanagement.xml.XMLConnection;
import net.sourceforge.mecat.catalog.gui.features.desktop.FeatureDesktop;
import net.sourceforge.mecat.catalog.medium.Medium;
import net.sourceforge.mecat.catalog.medium.features.Feature;
import net.sourceforge.mecat.catalog.medium.features.impl.Ident;
import net.sourceforge.mecat.catalog.option.Options;
import net.sourceforge.mecat.srp.utils.NiceClass;

public class DefaultMediaOption implements MediaOption {
    
    LinkedHashMap<Class<? extends Medium>, Boolean> wantedMedia = new LinkedHashMap<Class<? extends Medium>, Boolean>();
    LinkedHashMap<Class<? extends Medium>, LinkedHashMap<Class<? extends Feature>, LinkedHashMap<Class<? extends FeatureDesktop>, Boolean>>> wantedFeatures = new LinkedHashMap<Class<? extends Medium>, LinkedHashMap<Class<? extends Feature>, LinkedHashMap<Class<? extends FeatureDesktop>, Boolean>>>();

    
    public DefaultMediaOption() {
        // Initialise with default values
        for (Class<? extends Medium> mediumClass : AbstractMediaOption.getMedia()) {
            LinkedHashMap<Class<? extends Feature>, LinkedHashMap<Class<? extends FeatureDesktop>, Boolean>> mediumMap = new LinkedHashMap<Class<? extends Feature>, LinkedHashMap<Class<? extends FeatureDesktop>, Boolean>>();
            wantedFeatures.put(mediumClass, mediumMap);
            for (Class<? extends Feature> featureClass : AbstractMediaOption.getFeatures(mediumClass)) {
                LinkedHashMap<Class<? extends FeatureDesktop>, Boolean> featureMap = new LinkedHashMap<Class<? extends FeatureDesktop>, Boolean>();
                mediumMap.put(featureClass, featureMap);
                for (NiceClass<FeatureDesktop> desktopClass : Options.desktops) {
                    if (featureClass.equals(Ident.class))
                        featureMap.put(desktopClass.getClasstype(), false);
                    else
                        featureMap.put(desktopClass.getClasstype(), true);
                }
            }
        }
    }
    
    protected void aquireFeatureOptions(Class< ? extends Medium> mediumClass, Entry entry) {
        if (entry == null){
            wantedMedia.put(mediumClass, false);
            return;
        }

        String visible = entry.getAttribute("Visible");
        if (visible != null && visible.equalsIgnoreCase("true"))
            wantedMedia.put(mediumClass, true);
        else
            wantedMedia.put(mediumClass, false);

        for (NiceClass<FeatureDesktop> desktopClass : Options.desktops) {
            Entry desktopEntry = entry.getSubEntry(desktopClass.getClasstype().getName());
            if (desktopEntry == null)
                continue;
            for (Iterator<String> hidden = desktopEntry.getSetIterator("Hidden"); hidden.hasNext();) {
                String val = hidden.next();
                try {
                    Class c = Class.forName(val);
                    if (Feature.class.isAssignableFrom(c))
                        wantedFeatures.get(mediumClass).get(c).put(desktopClass.getClasstype(), false);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    protected void aquireOption(Class< ? extends Medium> mediumClass) {
        URL url = mediumClass.getResource(mediumClass.getSimpleName() + ".preferences.xml");
        if (url == null) {
            wantedMedia.put(mediumClass, false);
            return;
        }
        Catalog catalog = XMLCatalogFactory.catalogFactory.openCatalog(new XMLConnection(url));
        if (catalog == null) {
            wantedMedia.put(mediumClass, false);
            return;
        }

        aquireFeatureOptions(mediumClass, catalog.getOption(mediumClass.getSimpleName()));
    }

    public boolean isWanted(Class< ? extends Medium> mediumClass) {
        // Load options at the time they are needed
        if (!wantedMedia.containsKey(mediumClass))
            aquireOption(mediumClass);

        return wantedMedia.get(mediumClass);
    }

    public boolean isWanted(Class< ? extends Medium> mediumClass, Class< ? extends Feature> featureClass, Class< ? extends FeatureDesktop> desktopClass) {
        // Load options at the time they are needed
        if (!wantedMedia.containsKey(mediumClass))
            aquireOption(mediumClass);

        return wantedFeatures.get(mediumClass).get(featureClass).get(desktopClass);
    }

}
