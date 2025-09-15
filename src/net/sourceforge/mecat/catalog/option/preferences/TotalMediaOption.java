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
 * Created on Jan 15, 2006
 * @author Stephan Richard Palm
 * 
 */
package net.sourceforge.mecat.catalog.option.preferences;

import net.sourceforge.mecat.catalog.gui.features.desktop.FeatureDesktop;
import net.sourceforge.mecat.catalog.medium.Listing;
import net.sourceforge.mecat.catalog.medium.Medium;
import net.sourceforge.mecat.catalog.medium.features.Feature;
import net.sourceforge.mecat.catalog.option.Options;

public class TotalMediaOption implements MediaOption {

    final Listing listing;
    
    public TotalMediaOption(final Listing listing) {
        this.listing = listing;
    }
    
    
    public boolean isWanted(Class< ? extends Medium> mediumClass) {
        if (Options.AppPrefs.getGeneralOption().isUseCatalogOption() && listing.getCatalogPreferences().getMediaOption().isMediaOptionOverriden())
            return listing.getCatalogPreferences().getMediaOption().isWanted(mediumClass);
        if (Options.AppPrefs.getMediaOption().isMediaOptionOverriden())
            return Options.AppPrefs.getMediaOption().isWanted(mediumClass);
        return DefaultPreferences.defaultPreferences.getMediaOption().isWanted(mediumClass);
    }

    public boolean isWanted(Class< ? extends Medium> mediumClass,
            Class< ? extends Feature> featureClass,
            Class< ? extends FeatureDesktop> desktopClass) {
        if (Options.AppPrefs.getGeneralOption().isUseCatalogOption() && listing.getCatalogPreferences().getMediaOption().isMediumOptionOverriden(mediumClass))
            return listing.getCatalogPreferences().getMediaOption().isWanted(mediumClass, featureClass, desktopClass);
        if (Options.AppPrefs.getMediaOption().isMediumOptionOverriden(mediumClass))
            return Options.AppPrefs.getMediaOption().isWanted(mediumClass, featureClass, desktopClass);
        return DefaultPreferences.defaultPreferences.getMediaOption().isWanted(mediumClass, featureClass, desktopClass);
    }

}
