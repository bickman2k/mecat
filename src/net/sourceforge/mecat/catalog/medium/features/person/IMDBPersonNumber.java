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
 * Created on Jul 22, 2004
 * @author Stephan Richard Palm
 */
package net.sourceforge.mecat.catalog.medium.features.person;

import java.text.DecimalFormat;

import net.sourceforge.mecat.catalog.medium.Medium;
import net.sourceforge.mecat.catalog.medium.features.NumberFeature;
import net.sourceforge.mecat.catalog.option.Options;
import net.sourceforge.mecat.catalog.sort.ByNumberFromTextFeature;
import net.sourceforge.mecat.catalog.sort.ConfigurableComparator;

public class IMDBPersonNumber extends NumberFeature {

	public IMDBPersonNumber(Medium medium) {
		super(medium, "IMDBPersonNumber");
	}

	public static ConfigurableComparator getComparator() {
		return new ByNumberFromTextFeature(IMDBPersonNumber.class);	
	}

	public String getText() {
        if (get() == null)
            return Options.getI18N(IMDBPersonNumber.class).getString("The reference number to the imdb is not set.");
        return Options.getI18N(IMDBPersonNumber.class).getString("The number for the person at the imdb is [NUMBER].").replaceAll("\\[NUMBER\\]", get());
	}
    public String getTextHTML(int availableWidth) {
        DecimalFormat df = new DecimalFormat("0000000");
        
        if (get() == null)
            return Options.getI18N(IMDBPersonNumber.class).getString("The reference number to the imdb is not set.");
        return Options.getI18N(IMDBPersonNumber.class).getString("The number for the person at the imdb is [NUMBER].").replaceAll("\\[NUMBER\\]", 
                "<a href=\"http://www.imdb.com/name/nm" +  df.format(getInt()) + "/\">" + get() + "</a>");
    }
}
