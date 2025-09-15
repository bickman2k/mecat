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
package net.sourceforge.mecat.catalog.medium.features.impl;

import net.sourceforge.mecat.catalog.medium.Medium;
import net.sourceforge.mecat.catalog.medium.features.NumberFeature;
import net.sourceforge.mecat.catalog.option.Options;
import net.sourceforge.mecat.catalog.sort.ByNumberFromTextFeature;
import net.sourceforge.mecat.catalog.sort.ConfigurableComparator;

public class Year extends NumberFeature {

	public Year(Medium medium) {
		super(medium, "Year");
	}

	public static ConfigurableComparator getComparator() {
		return new ByNumberFromTextFeature(Year.class);	
	}

	public String getText() {
		if (get() == null)
			return Options.getI18N(Year.class).getString("The year is not set.");
		return Options.getI18N(Year.class).getString("The medium's content was produced in [YEAR].").replaceAll("\\[YEAR\\]", "" + get());
	}
    public String getTextHTML(int availableWidth) {
        if (get() == null)
            return Options.getI18N(Year.class).getString("The year is not set.");
        return Options.getI18N(Year.class).getString("The medium's content was produced in [YEAR].").replaceAll("\\[YEAR\\]", "<strong>" + get() + "</strong>");
    }
}
