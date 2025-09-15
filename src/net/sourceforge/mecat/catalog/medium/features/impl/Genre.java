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
import net.sourceforge.mecat.catalog.medium.features.AbstractFeature;
import net.sourceforge.mecat.catalog.medium.features.MultiChoiceFeature;
import net.sourceforge.mecat.catalog.sort.ByChoiceFromMultiChoiceFeature;
import net.sourceforge.mecat.catalog.sort.ConfigurableComparator;

/**
 * 
 * Created on Aug 9, 2005
 *
 * @author Stephan Richard Palm
 *
 * Genre is declared final because it extends MultiChoiceFeature and sets the attributeName.
 * All classes extending Format would save there information on the same spot.
 */
public class Genre extends MultiChoiceFeature {

	public Genre(Medium medium) {
        super(medium, AbstractFeature.getEasyClassName(Genre.class.getName()));
	}	

	static public ConfigurableComparator getComparator(){
		return new ByChoiceFromMultiChoiceFeature(Genre.class);
	}

}
