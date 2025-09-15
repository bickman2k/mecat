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
 * Created on Jun 4, 2006
 * @author Stephan Richard Palm
 * 
 */
package net.sourceforge.mecat.catalog.filesystem.load;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Vector;

public class SubDirList implements List<DirectoryEntry> {

    final FileSystemLoadShallow load;
    final DirectoryEntry directory;
    final int firstDirChildLnk;
    final int numSubDirs;
    
    public SubDirList(FileSystemLoadShallow load, DirectoryEntry directory, int firstDirChildLnk, int numSubDirs) {
        this.load = load;
        this.directory = directory;
        this.firstDirChildLnk = firstDirChildLnk;
        this.numSubDirs = numSubDirs;
    }

    public int size() {
        return numSubDirs;
    }

    public boolean isEmpty() {
        return numSubDirs == 0;
    }

    public boolean contains(Object o) {
        if (!(o instanceof DirectoryEntry))
            return false;

        DirectoryEntry entry = (DirectoryEntry) o;

        if (entry.getParent().equals(directory))
            return true;

        return false;
    }

    public Iterator<DirectoryEntry> iterator() {
        return new Iterator<DirectoryEntry>(){

            int pos = 0;
            
            public boolean hasNext() {
                return (pos < numSubDirs);
            }

            public DirectoryEntry next() {
                return get(pos++);
            }

            /**
             * This list is read only
             */
            public void remove() {
            }
            
        };
    }

    public DirectoryEntry[] toArray() {
        DirectoryEntry entries[] = new DirectoryEntry[numSubDirs];
        load.getAllDirs(firstDirChildLnk, numSubDirs, entries);
        return entries;
    }

    public <T> T[] toArray(T[] array) {
        load.getAllDirs(firstDirChildLnk, numSubDirs, array);
            
        return array;
    }

    /**
     * This list is read only
     * @param arg0
     * @return
     */
    public boolean add(DirectoryEntry arg0) {
        return false;
    }

    /**
     * This list is read only
     */
    public boolean remove(Object arg0) {
        return false;
    }

    public boolean containsAll(Collection< ? > collection) {
        for (Object o : collection)
            if (!contains(o))
                return false;

        return true;
    }

    /**
     * This list is read only
     */
    public boolean addAll(Collection< ? extends DirectoryEntry> arg0) {
        return false;
    }

    /**
     * This list is read only
     */
    public boolean addAll(int arg0, Collection< ? extends DirectoryEntry> arg1) {
        return false;
    }

    /**
     * This list is read only
     */
    public boolean removeAll(Collection< ? > arg0) {
        return false;
    }

    /**
     * This list is read only
     */
    public boolean retainAll(Collection< ? > arg0) {
        return false;
    }

    /**
     * This list is read only
     */
    public void clear() {
    }

    public DirectoryEntry get(int index) {
        return load.getDirectory(firstDirChildLnk + index);
    }

    /**
     * This list is read only
     */
    public DirectoryEntry set(int arg0, DirectoryEntry arg1) {
        return null;
    }

    /**
     * This list is read only
     */
    public void add(int arg0, DirectoryEntry arg1) {
        
    }

    /**
     * This list is read only
     */
    public DirectoryEntry remove(int arg0) {
        return null;
    }

    public int indexOf(Object o) {
        if (!contains(o))
            return -1;

        for (int i = firstDirChildLnk, j = 0; j < numSubDirs; i++, j++) {
            DirectoryEntry dir = load.getDirectoryIfLoadAlready(i);
            if (dir != null && dir.equals(o))
                return j;
        }
        
        return -1;
    }

    public int lastIndexOf(Object o) {
        if (!contains(o))
            return -1;

        for (int i = firstDirChildLnk + numSubDirs - 1, j = numSubDirs - 1; j >= 0; i++, j++) {
            DirectoryEntry dir = load.getDirectoryIfLoadAlready(i);
            if (dir != null && dir.equals(o))
                return j;
        }
        
        return -1;
    }

    public ListIterator<DirectoryEntry> listIterator() {
        return listIterator(0);
    }

    public ListIterator<DirectoryEntry> listIterator(final int startPosition) {
        return new ListIterator<DirectoryEntry>(){

            int pos = startPosition;
            
            public boolean hasNext() {
                return (pos < numSubDirs);
            }

            public DirectoryEntry next() {
                return get(pos++);
            }

            /**
             * This list is read only
             */
            public void remove() {
            }

            public boolean hasPrevious() {
                return pos > 0;
            }

            public DirectoryEntry previous() {
                return get(--pos);
            }

            public int nextIndex() {
                return pos;
            }

            public int previousIndex() {
                return pos - 1;
            }

            /**
             * This list is read only
             */
            public void set(DirectoryEntry arg0) {
            }

            /**
             * This list is read only
             */
            public void add(DirectoryEntry arg0) {
            }
            
        };

    }

    /**
     * This function behaves bad concerning performance
     */
    public List<DirectoryEntry> subList(int arg0, int arg1) {
        return (new Vector<DirectoryEntry>(this)).subList(arg0, arg1);
    }

}
