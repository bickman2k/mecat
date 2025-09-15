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

import java.util.Locale;

/**
 * Network version of @link{net.sourceforge.mecat.catalog.datamanagement.AttributeListener}.
 */
public class RemoteAttributeEvent extends RemoteEntryEvent {

    final long sourceID;
    final String name;
    final Locale language;
    final String oldValue;
    final String newValue;
    
    public RemoteAttributeEvent(final long sourceID, final String name, final Locale language, final String oldValue, final String newValue){
        this.sourceID = sourceID;
        this.name = name;
        this.language = language;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    public int compareTo(RemoteEvent e) {
        if (!(e instanceof RemoteAttributeEvent))
            return getClass().getName().compareTo(e.getClass().getName());
        
        RemoteAttributeEvent remoteAttributeEvent = (RemoteAttributeEvent) e;

        // Check whether it comes from the same entry
        if (sourceID < remoteAttributeEvent.sourceID)
            return -1;
        if (sourceID > remoteAttributeEvent.sourceID)
            return 1;

        // Check wheter the attribute has the same name
        if (!name.equals(remoteAttributeEvent.name))
            return name.compareTo(remoteAttributeEvent.name);

        // Check whether the language option is the same
        if (language == null && remoteAttributeEvent.language != null)
            return -1;
        if (language != null && remoteAttributeEvent.language == null)
            return 1;
        if (language != null && remoteAttributeEvent.language != null && !language.equals(remoteAttributeEvent.language))
            language.toString().compareTo(remoteAttributeEvent.language.toString());

        // Check whether the oldValue is the same
        if (oldValue == null && remoteAttributeEvent.oldValue != null)
            return -1;
        if (oldValue != null && remoteAttributeEvent.oldValue == null)
            return 1;
        if (oldValue != null && remoteAttributeEvent.oldValue != null && !oldValue.equals(remoteAttributeEvent.oldValue))
            oldValue.toString().compareTo(remoteAttributeEvent.oldValue.toString());

        // Check whether the newValue is the same
        if (newValue == null && remoteAttributeEvent.newValue != null)
            return -1;
        if (newValue != null && remoteAttributeEvent.newValue == null)
            return 1;
        if (newValue != null && remoteAttributeEvent.newValue != null && !newValue.equals(remoteAttributeEvent.newValue))
            newValue.toString().compareTo(remoteAttributeEvent.newValue.toString());

        // At this point everything turned out to be the same.
        return 0;
    }

    public Locale getLanguage() {
        return language;
    }

    public String getName() {
        return name;
    }

    public String getNewValue() {
        return newValue;
    }

    public String getOldValue() {
        return oldValue;
    }

    public long getSourceID() {
        return sourceID;
    }
}
