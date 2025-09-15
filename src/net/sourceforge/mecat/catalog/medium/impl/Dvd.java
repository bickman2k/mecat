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
 * Created on Jul 15, 2004
 * @author Stephan Richard Palm
 */
package net.sourceforge.mecat.catalog.medium.impl;

import net.sourceforge.mecat.catalog.datamanagement.Entry;
import net.sourceforge.mecat.catalog.medium.Listing;
import net.sourceforge.mecat.catalog.medium.features.impl.AudioLanguages;
import net.sourceforge.mecat.catalog.medium.features.impl.CoverImages;
import net.sourceforge.mecat.catalog.medium.features.impl.Regioncode;
import net.sourceforge.mecat.catalog.medium.features.impl.Title;
import net.sourceforge.mecat.catalog.medium.hidden.Movie;

public class Dvd extends Movie {
	
//	public final static String XML_Name = "dvd";
	
	public Dvd(Entry entry, Listing listing) {
		super(entry, listing);
		
		// Audiolanguages come direct after the title
        addFeature(getFeaturePosition(Title.class) + 1, new AudioLanguages(this, true));
		// The RegionCode comes before the description and before the ExtraImagesBag.
        addFeature(features.size() - 2, new Regioncode(this));
        addFeature(features.size() - 2, new CoverImages(this));
	}

	
	public String toString() {
		String ret = super.toString();

		return ret;
	}
	
}
