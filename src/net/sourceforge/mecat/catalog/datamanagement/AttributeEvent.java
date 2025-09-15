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
 * This event is thrown if an Attribute has changed.
 * If there was no value before then oldValue is null.
 * If newValue is null this means the Attribute has been removed.
 */
public class AttributeEvent extends EntryEvent {


    /**
     * Entry for which the attribute has changed
     */
    final Entry source;
    /**
     * Name of the attribute that has changed.
     */
    final String name;
    /**
     * Language for which the attribute has changed. 
     * Null if the attribute is language independent.
     */
    final Locale language;
    /**
     * Value before the change
     * If there was no value before then oldValue is null.
     */
    final String oldValue;
    /**
     * Value after the change
     * If newValue is null this means the Attribute has been removed.
     */
    final String newValue;
    
    public AttributeEvent(final Entry source, final String name, final Locale language, final String oldValue, final String newValue){
        this.source = source;
        this.name = name;
        this.language = language;
        this.oldValue = oldValue;
        this.newValue = newValue;
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

    public Entry getSource() {
        return source;
    }

    
    
}
