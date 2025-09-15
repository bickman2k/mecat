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
import net.sourceforge.mecat.catalog.medium.features.Feature;
import net.sourceforge.mecat.catalog.medium.features.TextFeature;
import net.sourceforge.mecat.catalog.medium.features.impl.Artist;
import net.sourceforge.mecat.catalog.medium.features.impl.Position;
import net.sourceforge.mecat.catalog.medium.features.impl.Title;
import net.sourceforge.mecat.catalog.medium.features.impl.AudioLanguages;
import net.sourceforge.mecat.catalog.medium.features.impl.Runtime;
import net.sourceforge.mecat.catalog.medium.impl.Cd;


public class Song extends Medium {

	/**
	 * @param entry
	 * @param listing
	 */
	public Song(Entry entry, Listing listing) {
		super(entry, listing);
        addFeature(new Position(this));
        addFeature(new Title(this));
        addFeature(new Artist(this));
        addFeature(new AudioLanguages(this, false));
        addFeature(new Runtime(this, Runtime.RuntimeUnit.Seconds));
	}
	
    @Override
    public String displayName() {
        Cd cd = (Cd) getParentMedium();
        String ret = "";
        
        String position = ((TextFeature)getFeature(Position.class)).getShortText();
        if (position != null && position.length() > 0) 
            ret += position + ". ";

        String title = ((TextFeature)getFeature(Title.class)).getShortText();
        if (title != null && title.length() > 0)
            ret += title;
        
        String artist = ((TextFeature)getFeature(Artist.class)).getShortText();
        if ((artist == null || artist.length() == 0) && (cd != null))
            artist = cd.getFeature(Artist.class).getShortText();
        if (artist != null && artist.length() > 0) 
            ret += " - " + artist;

        String audioLanguages = ((Feature)getFeature(AudioLanguages.class)).getShortText();
        if ((audioLanguages == null || audioLanguages.length() == 0) && cd != null) 
            audioLanguages = cd.getFeature(AudioLanguages.class).getShortText();
        if (audioLanguages != null && audioLanguages.length() > 0) 
            ret += " (" + audioLanguages + ")";
        
        String runtime = getFeature(Runtime.class).getShortText();
        if (runtime != null)
            ret += " " + runtime;

        return ret;
    }

}
