/*
 *  MeCat - Media Catalog more information on
 *  http://mecat.sourceforge.net/
 *  Copyright (C) 2005, Stephan Richard Palm, All Rights Reserved.
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
 * Created on Jun 14, 2005
 * @author Stephan Richard Palm
 */
package net.sourceforge.mecat.catalog.gui.customMenu;

import javax.swing.JMenuItem;
import net.sourceforge.mecat.catalog.medium.Listing;

public interface MenuFactory {

	/**
	 * This function should create a new menu.
	 * Purpose of this function is to recieve as many menu instance as necessary
	 * to present them as many times as wanted in the menu.
	 * If an implementation would return allways the same instance this could
	 * result in missing item's in the menu.
	 * @return a new Instance of the menu.
	 */
	public JMenuItem createMenu(final Listing listing);
    
    
    /**
     * This function return true if the menu shall be shown 
     * even though there is no entry of the corresponding 
     * media in the listing. And false in the else case.
     * @return
     */
    public boolean allwaysVisible();
	
}
