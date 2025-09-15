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
 * Created on Jan 8, 2006
 * @author Stephan Richard Palm
 * 
 */
package net.sourceforge.mecat.catalog.option.preferences;

import net.sourceforge.mecat.catalog.datamanagement.Entry;
import net.sourceforge.mecat.catalog.datamanagement.PersistentThroughEntry;
import net.sourceforge.mecat.catalog.datamanagement.Util;

public abstract class AbstractPreferences<GENERAL extends AbstractGeneralOption, MEDIA extends AbstractMediaOption, FEATURES extends AbstractFeaturesOption> implements PersistentThroughEntry, Preferences<GENERAL, MEDIA, FEATURES> {

    GENERAL generalOption;
    MEDIA mediaOption;
    FEATURES featuresOption;

    abstract GENERAL getNewGeneralOption();
    abstract MEDIA getNewMediaOption();
    abstract FEATURES getNewFeaturesOption();

    public AbstractPreferences() {
        generalOption = getNewGeneralOption();
        mediaOption = getNewMediaOption();
        featuresOption = getNewFeaturesOption();
    }
    
    public boolean loadFromEntry(Entry entry) {
        Entry generalOptionEntry = entry.getSubEntry("GeneralOption");
        if (generalOptionEntry != null) {
            PersistentThroughEntry pte = Util.loadFromEntry(generalOptionEntry);
            if (pte instanceof AbstractGeneralOption)
                generalOption = (GENERAL) pte;
        }
        
        Entry mediaOptionEntry = entry.getSubEntry("MediaOption");
        if (mediaOptionEntry != null) {
            PersistentThroughEntry pte = Util.loadFromEntry(mediaOptionEntry);
            if (pte instanceof AbstractMediaOption)
                mediaOption = (MEDIA) pte;
        }
        
        Entry featuresOptionEntry = entry.getSubEntry("FeaturesOption");
        if (featuresOptionEntry != null) {
            PersistentThroughEntry pte = Util.loadFromEntry(featuresOptionEntry);
            if (pte instanceof AbstractFeaturesOption)
                featuresOption = (FEATURES) pte;
        }

        return true;
    }

    public void saveToEntry(Entry entry) {
        Util.saveToEntry(generalOption, entry.createSubEntry("GeneralOption"));
        Util.saveToEntry(mediaOption, entry.createSubEntry("MediaOption"));
        Util.saveToEntry(featuresOption, entry.createSubEntry("FeaturesOption"));

    }

    public GENERAL getGeneralOption() {
        return generalOption;
    }

    public MEDIA getMediaOption() {
        return mediaOption;
    }
    
    public FEATURES getFeaturesOption() {
        return featuresOption;
    }
    

}
