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
package net.sourceforge.mecat.catalog.datamanagement.remote;

import java.util.Vector;

import net.sourceforge.mecat.catalog.datamanagement.Entry;
import net.sourceforge.mecat.catalog.datamanagement.remote.event.RemoteEvent;

public abstract class RemoteNetworkConnection {
    
    final Vector<RemoteEventListener> remoteEventListeners = new Vector<RemoteEventListener>();
    
    public void addRemoteEventListener(RemoteEventListener remoteEventListener) {
        remoteEventListeners.add(remoteEventListener);
    }
    public void removeRemoteEventListener(RemoteEventListener remoteEventListener) {
        remoteEventListeners.add(remoteEventListener);
    }
    public void fireEvent(RemoteEvent remoteEvent) {
        // TODO Transport Event through Network
    }
    /**
     * 
     * This function returns an unused ID
     * The server creates it himself
     * while the clients ask the server.
     *
     * @return unique id for entry
     */
    public abstract long getNewID();
    
    public Entry getEntry(final long entryID) {
        // Network transfer of Entry as XMLEntry
        return null;
    }
}
