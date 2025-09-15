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
 * Created on Dec 19, 2006
 * @author Stephan Richard Palm
 */
package net.sourceforge.mecat.catalog.medium.hidden;

import net.sourceforge.mecat.catalog.datamanagement.Entry;
import net.sourceforge.mecat.catalog.medium.Listing;
import net.sourceforge.mecat.catalog.medium.Medium;
import net.sourceforge.mecat.catalog.medium.features.TextFeature;
import net.sourceforge.mecat.catalog.medium.features.impl.Translator;
import net.sourceforge.mecat.catalog.medium.features.impl.WritenLanguage;
import net.sourceforge.mecat.catalog.medium.features.person.Name;

public class Translation extends Medium {
	
	public Translation(Entry entry, Listing listing) {
		super(entry, listing);
        addFeature(new Translator(this));	
        addFeature(new WritenLanguage(this)); 
	}

    @Override
    public String displayName() {
        String ret = "";
        
        Person translator = getFeature(Translator.class).getSubEntryMedium();
        if (translator != null) {
            if (((TextFeature)translator.getFeature(Name.class)).get() != null)
                ret += ((TextFeature)translator.getFeature(Name.class)).get() + " -> ";
        }

        if ((getFeature(WritenLanguage.class)).hasValue())
            ret += (getFeature(WritenLanguage.class)).getShortText();

        return ret;
    }

}
