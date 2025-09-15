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
 * Created on Aug 5, 2004
 * @author Stephan Richard Palm
 */
package net.sourceforge.mecat.catalog.i18n;

import java.util.*;

import net.sourceforge.mecat.catalog.option.Options;

public class LocaleResourceBundle extends ResourceBundle {

	/**
	 * I had no encounter with Enumeration befor
	 * therefore I created my own class to deal with it
	 * there is perhabs a better way, but i'm not aware of it.
	 * 
	 * What this class is does is Simple, it implments the
	 * Enumration interface and returns the the ISOLanguages 
	 * from Locale through the Enumeration interface.
	 * @author Stephan Richard Palm
	 *
	 * To change the template for this generated type comment go to
	 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
	 */
	class LanguageEnumeration implements Enumeration<String> {

		Iterator<String> iterator;
		
		public LanguageEnumeration() {
			Vector<String> vec = new Vector<String>();
			for (String s : Locale.getISOLanguages())
				vec.add(s);
			
			this.iterator = vec.iterator();
		}
		
		public boolean hasMoreElements() {
			return iterator.hasNext();
		}

		public String nextElement() {
			return iterator.next();
		}
		
	}
	
	protected Object handleGetObject(String key) {
		if (key == null)
			return null;
		Locale locale = new Locale(key);
		if (locale != null)
			return locale.getDisplayLanguage(Options.getCurrentLocale());//getLocale());
		else
			return key;
	}

	public Enumeration<String> getKeys() {
		return (new LanguageEnumeration());
	}

}
