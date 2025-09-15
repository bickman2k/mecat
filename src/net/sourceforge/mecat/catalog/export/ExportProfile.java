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
 * Created on Sep 24, 2004
 * @author Stephan Richard Palm
 */
package net.sourceforge.mecat.catalog.export;

import javax.swing.JPanel;

import net.sourceforge.mecat.catalog.datamanagement.PersistentThroughEntry;
import net.sourceforge.mecat.catalog.gui.ShowListing;


public interface ExportProfile extends PersistentThroughEntry  {

    /**
     * Returns an export instance for this profile.
     * The export will use all settings set in the
     * export profile.
     * 
     * @return export for this profile
     */
	public abstract Export getExport();
	
    /**
     * Returns true if the export profile has options.
     * @return true iff the export profile has options
     */
    public boolean hasOptions();
    
    /**
     * Returns a panel for editing the export profile's options.
     * 
     * @param showListing The list that is currently used. 
     * This option is used to help the user in making decessions.
     * @return a Panel for graphical editing of the options
     */
	public JPanel options(final ShowListing showListing);
	
    /**
     * Returns a panel for fast adaptation options.
     * Here are all options that will not be stored and
     * are likely to be changed just in time.
     * 
     * @return Customisation panel
     */
    public JPanel customize();
	
}
