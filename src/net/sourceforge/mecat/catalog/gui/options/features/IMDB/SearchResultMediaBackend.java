/*
 *  MeCat - Media Catalog more information on
 *  http://mecat.sourceforge.net/
 *  Copyright (C) 2005, Stephan Richard Palm, All Rights Reserved.
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
 * Created on Jun 22, 2005
 * @author Stephan Richard Palm
 */
package net.sourceforge.mecat.catalog.gui.options.features.IMDB;

import net.sourceforge.mecat.catalog.datamanagement.Util;
import net.sourceforge.mecat.catalog.gui.SelectMediaBackend;
import net.sourceforge.mecat.catalog.medium.features.option.MultiImageFeatureOption;
import net.sourceforge.mecat.catalog.medium.features.option.SubEntryFeatureOption;
import net.sourceforge.mecat.catalog.medium.features.impl.Actor;
import net.sourceforge.mecat.catalog.medium.features.impl.ExtraImagesBag;
import net.sourceforge.mecat.catalog.option.preferences.CatalogPreferences;
import net.sourceforge.mecat.catalog.option.preferences.TotalPreferences;

public class SearchResultMediaBackend extends SelectMediaBackend {

    public SearchResultMediaBackend(Display display) {
        super(display);
        
        // Set image options that fit the need for
        // showing IMDB front images.
        // Main reason for this is that we only want
        // to show the IMDB images not to store them
        setImageOptions();
        setActorOptions();
        setLevel0FilterEnabled(false);
    }
    
    private void setImageOptions() {
        TotalPreferences totalPreferences = liste.getTotalPreferences();
        CatalogPreferences catalogPreferences = liste.getCatalogPreferences();

        // Get current wanted option or empty if there is no current wanted
        MultiImageFeatureOption  org = (MultiImageFeatureOption)totalPreferences.getFeaturesOption().getOption(ExtraImagesBag.class);
        if (org == null)
            org = new MultiImageFeatureOption();
        
        // Make copy of the current wanted option
        MultiImageFeatureOption option = Util.copyPTE(org);
        
        // Only one option for all images
        option.setSplitOption(false);

        // And the images are taken directly from the www
        // this way they are only loaded if they are needed
        option.getSharedOption().setDirStorage(false);
        
        // Set maximum scale to 1
        option.setMaxScale(1.0);

        // Store the information for the result catalog
        catalogPreferences.getFeaturesOption().setOption(ExtraImagesBag.class, option);
    }

    private void setActorOptions() {
        TotalPreferences totalPreferences = liste.getTotalPreferences();
        CatalogPreferences catalogPreferences = liste.getCatalogPreferences();

        
        // Get current wanted option or empty if there is no current wanted
        SubEntryFeatureOption orgSubEntry = (SubEntryFeatureOption)totalPreferences.getFeaturesOption().getOption(Actor.class);
        if (orgSubEntry == null)
            orgSubEntry = new SubEntryFeatureOption();
        
        // Make copy of the current wanted option
        SubEntryFeatureOption optionSubEntry = Util.copyPTE(orgSubEntry);

        // Don't use links, this way there allways already exists an actor
        // and everything is keept in one catalog
        optionSubEntry.setPreferLink(false);

        // Store the information for the result catalog
        catalogPreferences.getFeaturesOption().setOption(Actor.class, optionSubEntry);
        
    }
    
	
}
