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
 * Created on Sep 25, 2004
 * @author Stephan Richard Palm
 */
package net.sourceforge.mecat.catalog.medium.impl;

import net.sourceforge.mecat.catalog.datamanagement.Entry;
import net.sourceforge.mecat.catalog.medium.Listing;
import net.sourceforge.mecat.catalog.medium.Medium;
import net.sourceforge.mecat.catalog.medium.features.TextFeature;
import net.sourceforge.mecat.catalog.medium.features.impl.Artist;
import net.sourceforge.mecat.catalog.medium.features.impl.AudioLanguages;
import net.sourceforge.mecat.catalog.medium.features.impl.CoverImages;
import net.sourceforge.mecat.catalog.medium.features.impl.Location;
import net.sourceforge.mecat.catalog.medium.features.impl.Position;
import net.sourceforge.mecat.catalog.medium.features.impl.Songs;
import net.sourceforge.mecat.catalog.medium.features.impl.Title;
import net.sourceforge.mecat.catalog.medium.features.impl.Year;

public class Cd extends Medium {

//	public final static String XML_Name = "book";

	public Cd(Entry entry, Listing listing) {
		super(entry, listing);
        addFeature(new Title(this));	
        addFeature(new Songs(this));
        addFeature(new AudioLanguages(this, true));
        addFeature(new Artist(this));	
        addFeature(new Year(this));
        addFeature(new Location(this));
        addFeature(new Position(this));
        addFeature(new CoverImages(this));
	}

    @Override
    public String displayName() {
        String ret = "";

        if (((TextFeature)getFeature(Title.class)).get() != null)
            ret += ((TextFeature)getFeature(Title.class)).get();

        return ret;
    }
	
}
