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
 * Created on Jul 16, 2004
 * @author Stephan Richard Palm
 */
package net.sourceforge.mecat.catalog.medium.features.impl;

import net.sourceforge.mecat.catalog.medium.Medium;
import net.sourceforge.mecat.catalog.medium.features.ChoiceFeature;
import net.sourceforge.mecat.catalog.sort.ByChoiceFromChoiceFeature;
import net.sourceforge.mecat.catalog.sort.ConfigurableComparator;

/**
 * 
 * Created on Aug 9, 2005
 *
 * @author Stephan Richard Palm
 *
 * Location is declared final because it extends ChoiceFeature and sets the attributeName.
 * All classes extending Format would save there information on the same spot.
 */
public class Location extends ChoiceFeature {

    final static ConfigurableComparator comparator = new ByChoiceFromChoiceFeature(Location.class);/*{ 
        public int compare(Medium medium0, Medium medium1){
            int res = super.compare(medium0, medium1);
            System.out.println(medium0 + " will be compared to " + medium1 + " with result " + res);
            return res;
        
    }};*/
    
	public Location(Medium medium) {
        super(medium, getEasyClassName(Location.class.getName()));
	}

	static public ConfigurableComparator getComparator(){
		return comparator;
	}
}
