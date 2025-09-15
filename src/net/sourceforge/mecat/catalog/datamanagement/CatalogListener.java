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
 * Created on Oct 6, 2005
 * @author Stephan Richard Palm
 * 
 */
package net.sourceforge.mecat.catalog.datamanagement;

public interface CatalogListener {
    
    /**
     * Event for the case where an entry has been added to the catalog.
     * @param event
     */
    public void entryAdded(final CatalogEvent event);
    
    /**
     * Event for the case where an entry has been removed from the catalog.
     * @param event
     */
    public void entryRemoved(final CatalogEvent event);
    
    /**
     * All entries with the same have been removed at one.
     * @param event
     */
    public void entriesRemoved(final CatalogEvent event);
    
    /**
     * Event for the case where an option has been added to the catalog.
     * @param event
     */
    public void optionAdded(final CatalogEvent event);

    /**
     * Event for the case where an option has been removed from the catalog.
     * @param event
     */
    public void optionRemoved(final CatalogEvent event);

    /**
     * All options with the same have been removed at one.
     * @param event
     */
    public void optionsRemoved(final CatalogEvent event);
    
    /**
     * Event for the case that an entry has changed in any way.
     * ( This event is thrown for options too )
     * This gives the same information as collecting the events from
     * all entries together, but it is a lot faster and takes less memory.
     * (Well depeding on the implementation, i.e. for XML that is)
     * @param event
     */
    public void entryChanged(final EntryEvent event);

}
