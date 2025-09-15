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
 * Created on Jan 21, 2006
 * @author Stephan Richard Palm
 * 
 */
package net.sourceforge.mecat.catalog.option.preferences;

import net.sourceforge.mecat.catalog.datamanagement.Entry;
import net.sourceforge.mecat.catalog.datamanagement.PersistentThroughEntry;
import net.sourceforge.mecat.catalog.datamanagement.Util;

public abstract class AbstractGeneralOption implements PersistentThroughEntry, GeneralOption {

    boolean overrideLanguagesOption = false;
    LanguagesOption languagesOption = new LanguagesOption();
    
    public LanguagesOption getLanguagesOption() {
        return languagesOption;
    }
    
    public boolean isOverrideLanguagesOption() {
        return overrideLanguagesOption;
    }
    
    public abstract void setOverrideLanguagesOption(boolean override);

    public boolean loadFromEntry(Entry entry) {
        Entry languagesEntry = entry.getSubEntry("Languages");
        if (languagesEntry != null) {
            PersistentThroughEntry pte = Util.loadFromEntry(languagesEntry);
            if (pte instanceof LanguagesOption) {
                languagesOption = ( LanguagesOption ) pte;
                overrideLanguagesOption = true;
            }
        }
        return true;
    }

    public void saveToEntry(Entry entry) {
        if (overrideLanguagesOption)
            Util.saveToEntry(languagesOption, entry.createSubEntry("Languages"));
    }

}
