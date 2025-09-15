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
package net.sourceforge.mecat.catalog.datamanagement.jdbc;

import java.util.List;
import java.util.Locale;
import java.util.Iterator;
import java.util.Set;

import net.sourceforge.mecat.catalog.datamanagement.Catalog;
import net.sourceforge.mecat.catalog.datamanagement.Entry;
import net.sourceforge.mecat.catalog.datamanagement.Identifier;

public class JDBCEntry extends Entry {

	/* (non-Javadoc)
	 * @see net.sourceforge.mecat.catalog.datamanagement.Entry#createSubEntry(java.lang.String)
	 */
	public JDBCEntry createSubEntry(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.mecat.catalog.datamanagement.Entry#getSubEntry(java.lang.String)
	 */
	public Entry getSubEntry(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<JDBCEntry> getSubEntries(String name) {
		return null;
	}
	public boolean removeSubEntry(Entry entry) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
    public void removeSubEntries(String attributeName) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.mecat.catalog.datamanagement.Entry#getTypeClassName()
	 */
	public String getTypeClassName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
    public Object getAttributeSynchronizationObject(String Name) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.mecat.catalog.datamanagement.Entry#setAttribute(java.lang.String, java.lang.String)
	 */
	public String setAttribute(String Name, String Value) {
		// TODO Auto-generated method stub
        return null;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.mecat.catalog.datamanagement.Entry#setAttribute(java.lang.String, java.lang.String, java.util.Locale)
	 */
	public String setAttribute(String Name, String Value, Locale language) {
		// TODO Auto-generated method stub
	    return null;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.mecat.catalog.datamanagement.Entry#getAttribute(java.lang.String)
	 */
	public String getAttribute(String Name) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.mecat.catalog.datamanagement.Entry#getAttribute(java.lang.String, java.util.Locale)
	 */
	public String getAttribute(String Name, Locale language) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.mecat.catalog.datamanagement.Entry#addSetAttribute(java.lang.String, java.lang.String)
	 */
	public boolean addSetAttribute(String Name, String Value) {
		// TODO Auto-generated method stub
	    return false;
	}

    @Override
    public Object getSetAttributeSynchronizationObject(String Name) {
        // TODO Auto-generated method stub
        return null;
    }

	/* (non-Javadoc)
	 * @see net.sourceforge.mecat.catalog.datamanagement.Entry#addSetAttribute(java.lang.String, java.lang.String, java.util.Locale)
	 */
	public boolean addSetAttribute(String Name, String Value, Locale language) {
		// TODO Auto-generated method stub
	    return false;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.mecat.catalog.datamanagement.Entry#removeSetAttribute(java.lang.String, java.lang.String)
	 */
	public boolean removeSetAttribute(String Name, String Value) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.mecat.catalog.datamanagement.Entry#removeSetAttribute(java.lang.String, java.lang.String, java.util.Locale)
	 */
	public boolean removeSetAttribute(String Name, String Value, Locale language) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.mecat.catalog.datamanagement.Entry#removeSetAttribute(java.lang.String)
	 */
	public void clearSetAttribute(String Name) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see net.sourceforge.mecat.catalog.datamanagement.Entry#removeSetAttribute(java.lang.String, java.util.Locale)
	 */
	public void clearSetAttribute(String Name, Locale language) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see net.sourceforge.mecat.catalog.datamanagement.Entry#existsSetAttribute(java.lang.String, java.lang.String)
	 */
	public boolean existsSetAttribute(String Name, String Value) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.mecat.catalog.datamanagement.Entry#existsSetAttribute(java.lang.String, java.lang.String, java.util.Locale)
	 */
	public boolean existsSetAttribute(String Name, String Value, Locale language) {
		// TODO Auto-generated method stub
		return false;
	}

	
	public Iterator<String> getSetIterator(String name, Locale language) {
		return null;
	}
	public Iterator<String> getSetIterator(String name) {
		return null;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.mecat.catalog.datamanagement.Entry#getCatalog()
	 */
	public Catalog getCatalog() {
		// TODO Auto-generated method stub
		return null;
	}

    @Override
    public List<JDBCEntry> getSubEntries() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Set<String> getAttributes() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Set<Locale> getAttributeLanguages(final String name) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Set<String> getSetAttributes() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Set<Locale> getSetAttributeLanguages(final String name) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Identifier getIdentifier() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void clear() {
        // TODO Auto-generated method stub
        
    }
}
