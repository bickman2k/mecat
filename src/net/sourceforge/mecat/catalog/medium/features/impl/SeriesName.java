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
 * Created on Aug 27, 2004
 * @author Stephan Richard Palm
 */
package net.sourceforge.mecat.catalog.medium.features.impl;

import java.util.Locale;

import net.sourceforge.mecat.catalog.medium.Medium;
import net.sourceforge.mecat.catalog.medium.features.TextFeature;
import net.sourceforge.mecat.catalog.option.Options;
import net.sourceforge.mecat.catalog.sort.ByTextFromTextFeature;
import net.sourceforge.mecat.catalog.sort.ConfigurableComparator;

public class SeriesName extends TextFeature {

	public SeriesName(Medium medium) {
		super(medium, "SeriesName", true, true);
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.mecat.catalog.medium.features.TextFeature#get()
	 */
	public String get() {
		if (medium == null)
			return null;
		if (medium.entry == null)
			return null;
			
		String Title = medium.entry.getAttribute("SeriesName", Options.getCurrentLocale());
		
		if (Title != null)
			return Title;
		
		if (medium.getListing() != null)	
			Title = medium.entry.getAttribute("SeriesName", medium.getListing().catalog.getLanguage());
		
		if (Title != null)
			return Title;

		for (String s : Locale.getISOLanguages())
			if ((Title = medium.entry.getAttribute("SeriesName", new Locale(s))) != null)
				return Title;
		
		return null;
	}


	public String getText() {
		if (get() == null)
			return Options.getI18N(SeriesName.class).getString("The series name is not set.");
		return Options.getI18N(SeriesName.class).getString("The medium is from the [SERIESNAME] series.").replaceAll("\\[SERIESNAME\\]", get());
	}

    public String getTextHTML(int availableWidth) {
        if (get() == null)
            return Options.getI18N(SeriesName.class).getString("The series name is not set.");
        return Options.getI18N(SeriesName.class).getString("The medium is from the [SERIESNAME] series.").replaceAll("\\[SERIESNAME\\]", "<strong>" + get() + "</strong>");
    }

    static public ConfigurableComparator getComparator(){
        return new ByTextFromTextFeature(SeriesName.class);
    }
}
