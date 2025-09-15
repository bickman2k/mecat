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
package net.sourceforge.mecat.catalog.datamanagement.remote.event;

import net.sourceforge.mecat.catalog.datamanagement.SubEntryEvent.SubEntryEventType;

public class RemoteSubEntryEvent extends RemoteEntryEvent {

    final SubEntryEventType type;
    final long sourceID;
    final String name;
    final long entryID;
    
    /**
     * Event for creating a new subentry
     * @param source
     * @param name
     * @param entry
     */
    public RemoteSubEntryEvent(final long sourceID, final String name, final long entryID) {
        type = SubEntryEventType.Create;
        this.sourceID = sourceID;
        this.name = name;
        this.entryID = entryID;
    }
    /**
     * Event for removing an existing event
     * @param source
     * @param entry
     */
    public RemoteSubEntryEvent(final long sourceID, final long entryID){
        type = SubEntryEventType.Remove;
        this.sourceID = sourceID;
        this.name = null;
        this.entryID = entryID;
    }
    public int compareTo(RemoteEvent e) {
        if (!(e instanceof RemoteSubEntryEvent))
            return getClass().getName().compareTo(e.getClass().getName());
        
        RemoteSubEntryEvent remoteSubEntryEvent = (RemoteSubEntryEvent) e;

        // Check wheter the type is the same
        if (type != remoteSubEntryEvent.type)
            return type.ordinal() - remoteSubEntryEvent.type.ordinal();

        // Check whether it comes from the same entry
        if (sourceID < remoteSubEntryEvent.sourceID)
            return -1;
        if (sourceID > remoteSubEntryEvent.sourceID)
            return 1;

        // Check whether it comes from the same entry
        if (entryID < remoteSubEntryEvent.entryID)
            return -1;
        if (entryID > remoteSubEntryEvent.entryID)
            return 1;

        // Check whether the name is the same
        if (name == null && remoteSubEntryEvent.name != null)
            return -1;
        if (name != null && remoteSubEntryEvent.name == null)
            return 1;
        if (name != null && remoteSubEntryEvent.name != null && !name.equals(remoteSubEntryEvent.name))
            name.toString().compareTo(remoteSubEntryEvent.name.toString());

        // At this point everything turned out to be the same.
        return 0;
    }

    
    public long getEntryID() {
        return entryID;
    }
    public String getName() {
        return name;
    }
    public long getSourceID() {
        return sourceID;
    }
    public SubEntryEventType getType() {
        return type;
    }
}
