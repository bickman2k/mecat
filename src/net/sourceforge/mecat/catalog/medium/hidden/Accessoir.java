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
 * Created on Oct 6, 2004
 * @author Stephan Richard Palm
 */
package net.sourceforge.mecat.catalog.medium.hidden;

import net.sourceforge.mecat.catalog.datamanagement.Entry;
import net.sourceforge.mecat.catalog.medium.Listing;
import net.sourceforge.mecat.catalog.medium.Medium;
import net.sourceforge.mecat.catalog.medium.features.AbstractChoiceFeature;
import net.sourceforge.mecat.catalog.medium.features.TextFeature;
import net.sourceforge.mecat.catalog.medium.features.impl.AccessoirType;
import net.sourceforge.mecat.catalog.medium.features.impl.Format;
import net.sourceforge.mecat.catalog.medium.features.impl.Description;

public class Accessoir extends Medium {

	/**
	 * @param entry
	 * @param listing
	 */
	public Accessoir(Entry entry, Listing listing) {
		super(entry, listing);
		addFeature(new AccessoirType(this));
		addFeature(new Format(this));
		addFeature(new Description(this));
	}
	
    @Override
    public String displayName() {
        String ret = "";
        
        if (((AbstractChoiceFeature)getFeature(Format.class)).getShortText() != null)
            ret += ((AbstractChoiceFeature)getFeature(Format.class)).getShortText();
        
        if (((AbstractChoiceFeature)getFeature(AccessoirType.class)).getShortText() != null)
            ret += " " + ((AbstractChoiceFeature)getFeature(AccessoirType.class)).getShortText();

        if (((TextFeature)getFeature(Description.class)).get() != null)
            ret += " " + ((TextFeature)getFeature(Description.class)).get();

        return ret;
    }

}
