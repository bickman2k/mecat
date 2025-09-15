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

import java.util.List;


public class SubEntryEvent extends EntryEvent {

    public enum SubEntryEventType {
        /**
         * One sub entry created
         */
        Create, 
        /**
         * One sub entry removed
         */
        Remove, 
        /**
         * All sub entries with given name removed
         */
        RemoveAll
    }

    /**
     * Type of the Event, either 'Create' or 'Remove'
     */
    final SubEntryEventType type;
    /**
     * Entry for which a new subentry has been created or 
     * an existing subentry has been removed
     */
    final Entry source;
    
    /**
     * If the type is Create then then this field 
     * contains the name of the created sub-entry.
     * 
     * If the type is RemoveAll then this field
     * contains the name of the sub entries that are removed.
     * 
     * If the type is neither Create nor RemoveAll the name is null.
     */
    final String name;

    /**
     * Sub-entry that has been Created or Removed.
     *
     * If the type is neither Create nor Remove 
     * then this field will be null.
     */
    final Entry entry;
    
    /**
     * Sub entries that have been removed.
     * 
     * if the type is not RemoveAll then 
     * this field will benull.
     */
    final List<? extends Entry> entries;
    
    /**
     * Event for creating a new subentry
     * @param source
     * @param type
     * @param name
     * @param entry
     */
    public SubEntryEvent(final Entry source, final SubEntryEventType type, final String name, final Entry entry, final List<? extends Entry> entries) {
        this.source = source;
        this.type = type;
        this.name = name;
        this.entry = entry;
        this.entries = entries;
    }

    /**
     * Event for creating a new subentry
     * @param source
     * @param name
     */
    public SubEntryEvent(final Entry source, final String name, final List<? extends Entry> entries) {
        this(source, SubEntryEventType.RemoveAll, name, null, entries);
    }

    /**
     * Event for creating a new subentry
     * @param source
     * @param name
     * @param entry
     */
    public SubEntryEvent(final Entry source, final String name, final Entry entry) {
        this(source, SubEntryEventType.Create, name, entry, null);
    }

    /**
     * Event for removing an existing event
     * @param source
     * @param entry
     */
    public SubEntryEvent(final Entry source, final Entry entry){
        this(source, SubEntryEventType.Remove, null, entry, null);
    }
    
    public Entry getEntry() {
        return entry;
    }
    public List<? extends Entry> getEntries() {
        return entries;
    }
    public String getName() {
        return name;
    }
    public Entry getSource() {
        return source;
    }
    public SubEntryEventType getType() {
        return type;
    }
    
    
    

}
