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
 * Created on Nov 21, 2005
 * @author Stephan Richard Palm
 * 
 */
package net.sourceforge.mecat.catalog.datamanagement;

public interface SubEntryListener {

    /**
     * This function will be invoked whenever a new sub-entry is created.
     * The function provides the the name of the sub-entry created and the
     * sub-entry itself.
     */
    public void subEntryCreated(SubEntryEvent event);
    
    /**
     * This function will be invoked whenever a sub-entry has been removed.
     * The function provides the entry that now is no more part of the catalog.
     */
    public void subEntryRemoved(SubEntryEvent event);
    
    /**
     * This function will be invoked whenever all sub-entries with a given name have been removed.
     * The function provides the name of the entries that now are no more part of the catalog.
     */
    void subEntriesRemoved(SubEntryEvent event);

}
