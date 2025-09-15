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
 * Created on May 24, 2006
 * @author Stephan Richard Palm
 * 
 */
package net.sourceforge.mecat.catalog.filesystem.store;

import java.util.Vector;

import net.sourceforge.mecat.catalog.filesystem.ModifiableDirectoryEntry;
import net.sourceforge.mecat.catalog.filesystem.ModifiableFileEntry;

//import net.sourceforge.mecat.catalog.filesystem.FileEntry;

public class DirectoryEntry extends FileSystemEntry implements net.sourceforge.mecat.catalog.filesystem.ModifiableDirectoryEntry  {

    ModifiableDirectoryEntry nextSibling;
    ModifiableFileEntry firstFileChild;
    ModifiableDirectoryEntry firstDirectoryChild;
    
    
    public DirectoryEntry(String name, ModifiableDirectoryEntry parent) {
        super(name, parent);
    }


    public ModifiableDirectoryEntry getFirstDirectoryChild() {
        return firstDirectoryChild;
    }


    public void setFirstDirectoryChild(ModifiableDirectoryEntry firstDirectoryChild) {
        this.firstDirectoryChild = firstDirectoryChild;
    }


    public ModifiableFileEntry getFirstFileChild() {
        return firstFileChild;
    }


    public void setFirstFileChild(ModifiableFileEntry firstFileChild) {
        this.firstFileChild = firstFileChild;
    }


    public ModifiableDirectoryEntry getNextSibling() {
        return nextSibling;
    }


    public void setNextSibling(ModifiableDirectoryEntry nextSibling) {
        this.nextSibling = nextSibling;
    }


    
    
    /**
     * This variable contains a cached version of the 
     * elements one gets from getFirstDirectoryChild.
     * Warning: The functions depending on this
     * @link #getAllDirs()
     * 
     * should only be used if the structure does not change anymore.
     */
    Vector<ModifiableDirectoryEntry> cacheSubDirs = null;
    /**
     * This variable contains a cached version of the 
     * elements one gets from getFirstFileChild.
     * Warning: The functions depending on this
     * @link #getAllFiles()
     * should only be used if the structure does not change anymore.
     */
    Vector<ModifiableFileEntry> cacheFiles = null;
    
    private int numFiles = -1;
    private int numDirs = -1;

    /**
     * Only use this function if the structure does not change
     * anymore.
     * @return all files in this directory
     */
    public Vector<ModifiableDirectoryEntry> getAllDirs() {
        if (cacheSubDirs == null)
            createCacheSubDirs();
        return cacheSubDirs;
    }

    private void createCacheSubDirs() {
        cacheSubDirs = new Vector<ModifiableDirectoryEntry>();
        ModifiableDirectoryEntry dir = getFirstDirectoryChild();
        while (dir != null) {
            cacheSubDirs.add(dir);
            dir = dir.getNextSibling();
        }
    }

    /**
     * Only use this function if the structure does not change
     * anymore.
     * @return all files in this directory
     */
    public Vector<ModifiableFileEntry> getAllFiles() {
        if (cacheFiles == null)
            createCacheFiles();
        return cacheFiles;
    }

    private void createCacheFiles() {
        cacheFiles = new Vector<ModifiableFileEntry>();
        ModifiableFileEntry file = getFirstFileChild();
        while (file != null) {
            cacheFiles.add(file);
            file = file.getNextSibling();
        }
    }


    public void setNumFiles(int numFiles) {
        this.numFiles = numFiles;
    }


    public void setNumDirs(int numDirs) {
        this.numDirs = numDirs;
    }


    public int getNumDirs() {
        if (numDirs != -1)
            return numDirs;
        
        return getAllDirs().size();
    }


    public int getNumFiles() {
        if (numFiles != -1)
            return numFiles;
        
        return getAllFiles().size();
    }
    
    
}
