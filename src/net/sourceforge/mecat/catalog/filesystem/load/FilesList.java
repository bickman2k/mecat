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

public class FilesList implements List<FileEntry> {

    final FileSystemLoadShallow load;
    final DirectoryEntry directory;
    final int firstFileChildLnk;
    final int numFiles;
    
    public FilesList(FileSystemLoadShallow load, DirectoryEntry directory, int firstFileChildLnk, int numFiles) {
        this.load = load;
        this.directory = directory;
        this.firstFileChildLnk = firstFileChildLnk;
        this.numFiles = numFiles;
    }

    public int size() {
        return numFiles;
    }

    public boolean isEmpty() {
        return numFiles == 0;
    }

    public boolean contains(Object o) {
        if (!(o instanceof FileEntry))
            return false;

        FileEntry entry = (FileEntry) o;

        if (entry.getParent().equals(directory))
            return true;

        return false;
    }

    public Iterator<FileEntry> iterator() {
        return new Iterator<FileEntry>(){

            int pos = 0;
            
            public boolean hasNext() {
                return (pos < numFiles);
            }

            public FileEntry next() {
                pos++;
                return get(pos - 1);
            }

            /**
             * This list is read only
             */
            public void remove() {
            }
            
        };
    }

    public FileEntry[] toArray() {
        FileEntry entries[] = new FileEntry[numFiles];
        load.getAllFiles(firstFileChildLnk, numFiles, entries);
        return entries;
    }

    public <T> T[] toArray(T[] array) {
        load.getAllFiles(firstFileChildLnk, numFiles, array);
            
        return array;
    }

    /**
     * This list is read only
     * @param arg0
     * @return
     */
    public boolean add(FileEntry arg0) {
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
    public boolean addAll(Collection< ? extends FileEntry> arg0) {
        return false;
    }

    /**
     * This list is read only
     */
    public boolean addAll(int arg0, Collection< ? extends FileEntry> arg1) {
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

    public FileEntry get(int index) {
        return load.getFile(firstFileChildLnk + index);
    }

    /**
     * This list is read only
     */
    public FileEntry set(int arg0, FileEntry arg1) {
        return null;
    }

    /**
     * This list is read only
     */
    public void add(int arg0, FileEntry arg1) {
        
    }

    /**
     * This list is read only
     */
    public FileEntry remove(int arg0) {
        return null;
    }

    public int indexOf(Object o) {
        if (!contains(o))
            return -1;

        for (int i = firstFileChildLnk, j = 0; j < numFiles; i++, j++) {
            FileEntry file = load.getFileIfLoadAlready(i);
            if (file != null && file.equals(o))
                return j;
        }
        
        return -1;
    }

    public int lastIndexOf(Object o) {
        if (!contains(o))
            return -1;

        for (int i = firstFileChildLnk + numFiles - 1, j = numFiles - 1; j >= 0; i++, j++) {
            FileEntry file = load.getFileIfLoadAlready(i);
            if (file != null && file.equals(o))
                return j;
        }
        
        return -1;
    }

    public ListIterator<FileEntry> listIterator() {
        return listIterator(0);
    }

    public ListIterator<FileEntry> listIterator(final int startPosition) {
        return new ListIterator<FileEntry>(){

            int pos = startPosition;
            
            public boolean hasNext() {
                return (pos < numFiles);
            }

            public FileEntry next() {
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

            public FileEntry previous() {
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
            public void set(FileEntry arg0) {
            }

            /**
             * This list is read only
             */
            public void add(FileEntry arg0) {
            }
            
        };

    }

    /**
     * This function behaves bad concerning performance
     */
    public List<FileEntry> subList(int arg0, int arg1) {
        return (new Vector<FileEntry>(this)).subList(arg0, arg1);
    }

}
