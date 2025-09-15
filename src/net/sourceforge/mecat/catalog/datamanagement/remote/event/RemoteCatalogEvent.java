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
 * Created on May 9, 2006
 * @author Stephan Richard Palm
 * 
 */
package net.sourceforge.mecat.catalog.datamanagement.remote.event;

import net.sourceforge.mecat.catalog.datamanagement.CatalogEvent.CatalogEventType;

public class RemoteCatalogEvent implements RemoteEvent {

    final CatalogEventType type;
    final long entryID;
    
    
    public RemoteCatalogEvent(final CatalogEventType type, final long entryID) {
        this.entryID = entryID;
        this.type = type;
    }


    public CatalogEventType getType() {
        return type;
    }
    
    public long getEntryID() {
        return entryID;
    }


    public int compareTo(RemoteEvent e) {
        
        if (!(e instanceof RemoteCatalogEvent))
            return getClass().getName().compareTo(e.getClass().getName());
        
        RemoteCatalogEvent remoteCatalogEvent = (RemoteCatalogEvent) e;

        // Check wheter the type is the same
        if (type != remoteCatalogEvent.type)
            return type.ordinal() - remoteCatalogEvent.type.ordinal();
        
        if (entryID < remoteCatalogEvent.entryID)
            return -1;
        if (entryID > remoteCatalogEvent.entryID)
            return 1;
        
        return 0;
    }
    
}
