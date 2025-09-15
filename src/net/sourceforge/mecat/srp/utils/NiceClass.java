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
 * Created on Aug 20, 2004
 * @author Stephan Richard Palm
 */
package net.sourceforge.mecat.srp.utils;

import java.util.List;
import java.util.Vector;

import javax.swing.event.ChangeEvent;

import net.sourceforge.mecat.catalog.importCatalog.Import;
import net.sourceforge.mecat.catalog.option.LocalListener;
import net.sourceforge.mecat.catalog.option.LocalListenerEvent;
import net.sourceforge.mecat.catalog.option.Options;
import net.sourceforge.mecat.catalog.option.WeakLocalListener;

public class NiceClass<T> implements LocalListener{
	final Class<? extends T> type;
	final String name;
    String displayName;
    
    public NiceClass(final Class<? extends T> type) {
        this(type, null);
    }
    
	public NiceClass(final Class<? extends T> type, final String name) {
		this.type = type;
        if (name == null)
            this.name = type.getSimpleName();
        else
            this.name = name;
        displayName = name;
        if (type.getName().startsWith("net.sourceforge.mecat")) {
            displayName = Options.getI18N(type).getString(NiceClass.this.name);
            Options.addLocalListener(new WeakLocalListener(this));
        }
	}
	
	public void stateChanged(LocalListenerEvent event) {
	    displayName = Options.getI18N(type).getString(NiceClass.this.name);
	}
	public Class<? extends T> getClasstype() {
		return type;
	}
    
    @Override
	public String toString() {
		return displayName;
	}
	
/*	public int compareTo(Object o) {
		if (!(o instanceof NiceClass))
			return 0;
        
        if (equals(o))
            return 0;
		
		return name.compareTo(((NiceClass)o).name);
	}*/
	
    @Override
	public boolean equals(Object o) {
		if (!(o instanceof NiceClass))
			return false;
		
		return type.equals(((NiceClass)o).type);
	}

    @Override
    public int hashCode() {
        return type.hashCode();
    }

    public static List<NiceClass> convertList(List<Class> cls) {
        Vector<NiceClass> ret = new Vector<NiceClass>(cls.size());
        
        for (Class c : cls)
            ret.add(new NiceClass(c));
        
        return ret;
        
    }

    public static List<NiceClass> convertList(List<Class> cls, String name) {
        Vector<NiceClass> ret = new Vector<NiceClass>(cls.size());
        
        for (Class c : cls)
            ret.add(new NiceClass(c, name));
        
        return ret;
        
    }

    
}
