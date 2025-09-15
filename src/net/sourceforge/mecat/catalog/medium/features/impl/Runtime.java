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

public class Runtime extends NumberFeature {

    public static enum RuntimeUnit {
        Minutes,
        Seconds
    }

    final RuntimeUnit runtimeUnit;
    
	public Runtime(Medium medium) {
        this(medium, RuntimeUnit.Minutes);
    }
    public Runtime(Medium medium, RuntimeUnit runtimeUnit) {
		super(medium, "Runtime");
        this.runtimeUnit = runtimeUnit;
	}

	public static ConfigurableComparator getComparator() {
		return new ByNumberFromTextFeature(Runtime.class);	
	}

	String twoDigits(int num) {
	    if (num < 10)
            return "0" + num;
        return "" + num;
    }
        
	@Override
    public String getShortText() {
        if (getInt() == null)
            return null;
        
        if (runtimeUnit == RuntimeUnit.Seconds) {
            int time = getInt();
            
            return Options.getI18N(Runtime.class).getString("[MIN]'[SEC].")
                .replaceAll("\\[MIN\\]", "" + (time / 60))
                .replaceAll("\\[SEC\\]", twoDigits(time % 60));
        }

        return super.getShortText();
    }

    @Override
    public String getShortTextHTML() {
        return getShortText();
    }

    public String getText() {
		if (getInt() == null)
			return Options.getI18N(Runtime.class).getString("The runtime is not set.");
        
        int time = getInt();
        
        switch (runtimeUnit) {
        case Minutes:
            return Options.getI18N(Runtime.class).getString("The medium's content has a duration of [TIME] minutes.").replaceAll("\\[TIME\\]", "" + time);
        case Seconds:
            return Options.getI18N(Runtime.class).getString("The medium's content has a duration of [MIN]'[SEC].")
                .replaceAll("\\[MIN\\]", "" + (time / 60))
                .replaceAll("\\[SEC\\]", twoDigits(time % 60));
        }
        
        return Options.getI18N(Runtime.class).getString("The medium's content has a duration of [TIME].")
            .replaceAll("\\[TIME\\]", "" + time);
	}
    public String getTextHTML(int availableWidth) {
        if (getInt() == null)
            return Options.getI18N(Runtime.class).getString("The runtime is not set.");
        
        int time = getInt();
        
        switch (runtimeUnit) {
        case Minutes:
            return Options.getI18N(Runtime.class).getString("The medium's content has a duration of [TIME] minutes.").replaceAll("\\[TIME\\]", "<strong>" + time + "</strong>");
        case Seconds:
            return Options.getI18N(Runtime.class).getString("The medium's content has a duration of [MIN]'[SEC].")
                .replaceAll("\\[MIN\\]", "<strong>" + (time / 60) + "</strong>")
                .replaceAll("\\[SEC\\]", "<strong>" + twoDigits(time % 60) + "</strong>");
        }
        
        return Options.getI18N(Runtime.class).getString("The medium's content has a duration of [TIME].")
            .replaceAll("\\[TIME\\]", "<strong>" + time + "</strong>");
    }
}
