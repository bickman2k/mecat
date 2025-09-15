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

import net.sourceforge.mecat.catalog.datamanagement.SetAttributeEvent.SetAttributeEventType;

/**
 * Network version of @link{net.sourceforge.mecat.catalog.datamanagement.AttributeListener}.
 */
public class RemoteSetAttributeEvent extends RemoteEntryEvent{
    
    final SetAttributeEventType type;
    final long sourceID;
    final String name;
    final Locale language;
    final String value;
    
    public RemoteSetAttributeEvent(SetAttributeEventType type, long sourceID, String name, Locale language, String value) {
        this.type = type;
        this.sourceID = sourceID;
        this.name = name;
        this.language = language;
        this.value = value;
    }

    public int compareTo(RemoteEvent e) {
        if (!(e instanceof RemoteSetAttributeEvent))
            return getClass().getName().compareTo(e.getClass().getName());
        
        RemoteSetAttributeEvent remoteSetAttributeEvent = (RemoteSetAttributeEvent) e;

        // Check wheter the type is the same
        if (type != remoteSetAttributeEvent.type)
            return type.ordinal() - remoteSetAttributeEvent.type.ordinal();

        // Check whether it comes from the same entry
        if (sourceID < remoteSetAttributeEvent.sourceID)
            return -1;
        if (sourceID > remoteSetAttributeEvent.sourceID)
            return 1;

        // Check wheter the attribute has the same name
        if (!name.equals(remoteSetAttributeEvent.name))
            return name.compareTo(remoteSetAttributeEvent.name);

        // Check whether the language option is the same
        if (language == null && remoteSetAttributeEvent.language != null)
            return -1;
        if (language != null && remoteSetAttributeEvent.language == null)
            return 1;
        if (language != null && remoteSetAttributeEvent.language != null && !language.equals(remoteSetAttributeEvent.language))
            language.toString().compareTo(remoteSetAttributeEvent.language.toString());

        // Check whether the value is the same
        if (value == null && remoteSetAttributeEvent.value != null)
            return -1;
        if (value != null && remoteSetAttributeEvent.value == null)
            return 1;
        if (value != null && remoteSetAttributeEvent.value != null && !value.equals(remoteSetAttributeEvent.value))
            value.toString().compareTo(remoteSetAttributeEvent.value.toString());

        // At this point everything turned out to be the same.
        return 0;
    }

    public Locale getLanguage() {
        return language;
    }

    public String getName() {
        return name;
    }

    public long getSourceID() {
        return sourceID;
    }

    public SetAttributeEventType getType() {
        return type;
    }

    public String getValue() {
        return value;
    }
}
