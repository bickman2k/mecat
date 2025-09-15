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
 * Created on Aug 4, 2004
 * @author Stephan Richard Palm
 */
package net.sourceforge.mecat.catalog.gui.utils;

import javax.swing.JList;
import javax.swing.ListModel;
import java.util.Vector;

public class JListValued<T> extends JList implements Valued<T>{

	public JListValued(T value) { super();  this.value = value; }
	public JListValued(T value, ListModel dataModel) { super(dataModel);  this.value = value; }
	public JListValued(T value, Object[] listData) { super(listData);  this.value = value; }
	public JListValued(T value, Vector<?> listData) { super(listData);  this.value = value; }
	
	T value;
	public T getValue() {
		return value;
	}
}
