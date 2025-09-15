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
 * Created on May 9, 2006
 * @author Stephan Richard Palm
 * 
 */
package net.sourceforge.mecat.catalog.datamanagement.remote;

import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import net.sourceforge.mecat.catalog.datamanagement.Entry;
import net.sourceforge.mecat.catalog.datamanagement.Identifier;

public class RemoteEntry extends Entry {

    @Override
    public RemoteCatalog getCatalog() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public RemoteEntry createSubEntry(String name) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public RemoteEntry getSubEntry(String name) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<RemoteEntry> getSubEntries(String name) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<RemoteEntry> getSubEntries() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean removeSubEntry(Entry entry) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void removeSubEntries(String attributeName) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public String getTypeClassName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Object getAttributeSynchronizationObject(String Name) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String setAttribute(String Name, String Value) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String setAttribute(String Name, String Value, Locale language) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getAttribute(String Name) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getAttribute(String Name, Locale language) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Object getSetAttributeSynchronizationObject(String Name) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean addSetAttribute(String Name, String Value) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean addSetAttribute(String Name, String Value, Locale language) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Iterator<String> getSetIterator(String Name) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Iterator<String> getSetIterator(String Name, Locale language) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean removeSetAttribute(String Name, String Value) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean removeSetAttribute(String Name, String Value, Locale language) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void clearSetAttribute(String Name) {
        // TODO Auto-generated method stub

    }

    @Override
    public void clearSetAttribute(String Name, Locale language) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean existsSetAttribute(String Name, String Value) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean existsSetAttribute(String Name, String Value, Locale language) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Set<String> getAttributes() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Set<Locale> getAttributeLanguages(String name) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Set<String> getSetAttributes() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Set<Locale> getSetAttributeLanguages(String name) {
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
