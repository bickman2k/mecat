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

public interface SetAttributeListener {

    /**
     * This function will be invoked whenever a set-attribute gets an additional value.
     * The function provides the name of the attribute the language of the attribute and the 
     * added value.
     */
    public void setAttributeAdded(SetAttributeEvent event);

    /**
     * This function will be invoked whenever a set-attribute will be cut by a value.
     * The function provides the name of the attribute the language of the attribute and the 
     * added value.
     */
    public void setAttributeRemoved(SetAttributeEvent event);

    /**
     * This function will be invoked whenever a set-attribute was be cleared.
     * This means all elements of the set-attribute are now gone.
     * 
     * @param event
     */
    public void setAttributeCleared(SetAttributeEvent event);
}
