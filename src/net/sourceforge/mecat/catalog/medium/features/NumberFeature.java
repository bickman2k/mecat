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
 * Created on Jul 19, 2004
 * @author Stephan Richard Palm
 * 
 * All media have this feature it is sort of an obligation
 */
package net.sourceforge.mecat.catalog.medium.features;

import net.sourceforge.mecat.catalog.medium.Medium;
import net.sourceforge.mecat.catalog.medium.features.TextFeature;

public abstract class NumberFeature extends TextFeature {

	public NumberFeature(final Medium medium, final String attributeName) {
		super(medium, attributeName, true, false);
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.mecat.catalog.medium.features.TextFeature#set(java.lang.String)
	 */
	public boolean set(String pos) {
		if ((pos == null) || (pos.length() == 0)) {
			medium.entry.setAttribute(attributeName, null);
			return true;
		}
		try {
			int val = Integer.valueOf(pos).intValue();
			medium.entry.setAttribute(attributeName, String.valueOf(val));
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	public boolean set(Integer val) {
		medium.entry.setAttribute(attributeName, String.valueOf(val));
		return true;
	}
	
	public Integer getInt() {
		if (medium.entry.getAttribute(attributeName) == null)
			return null;
		try {
			return Integer.parseInt(medium.entry.getAttribute(attributeName));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public String get() {
		return medium.entry.getAttribute(attributeName);
	}

}
