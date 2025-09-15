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

import java.util.Locale;

/**
 */
public class SetAttributeEvent extends EntryEvent {

    public enum SetAttributeEventType { Add, Remove, Clear };
    
    /**
     * Type of the Event, either 'Add' or 'Remove'
     */
    final SetAttributeEventType type;
    /**
     * Entry for which the set-attribute has changed
     */
    final Entry source;
    /**
     * Name of the set-attribute.
     */
    final String name;
    /**
     * Language for which the attribute has changed. 
     * Null if the attribute is language independent.
     */
    final Locale language;
    /**
     * Value that has been added or removed (depending the type) to or from the set-attribute.
     * Null if the type is clear
     */
    final String value;
    
    public SetAttributeEvent(SetAttributeEventType type, Entry source, String name, Locale language, String value) {
        this.type = type;
        this.source = source;
        this.name = name;
        this.language = language;
        this.value = value;
    }

    public Locale getLanguage() {
        return language;
    }

    public String getName() {
        return name;
    }

    public Entry getSource() {
        return source;
    }

    public SetAttributeEventType getType() {
        return type;
    }

    public String getValue() {
        return value;
    }
    
    
    

}
