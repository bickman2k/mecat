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
 * Created on Nov 22, 2005
 * @author Stephan Richard Palm
 * 
 */
package net.sourceforge.mecat.catalog.medium;

import javax.swing.event.ListDataEvent;

public interface ListingListener {

    // See ListDataListener
    public void intervalRemoved(ListDataEvent listDataEvent);
    // See ListDataListener
    public void intervalAdded(ListDataEvent listDataEvent);
    
    // This function is invoked at the beginning and 
    // if the behavior of the data changes but not 
    // the data itself. (Example: current language changed)
    public void refreshed();
    
}
