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
package net.sourceforge.mecat.catalog.filesystem.load;

import java.util.List;
import java.util.Vector;

public class DirectoryEntry extends FileSystemEntry implements net.sourceforge.mecat.catalog.filesystem.DirectoryEntry {

    int nextSiblingLnk;
    int firstFileChildLnk;
    int firstDirectoryChildLnk;
    
    /**
     * Number of sub directories
     * @since version 1 of the filesystem
     */
    int numSubDirs;
    /**
     * Number of files
     * @since version 1 of the filesystem
     */
    int numFiles;
    
    
    public DirectoryEntry(FileSystemLoadShallow load, int nameLnk, int parentLnk) {
        super(load, nameLnk, parentLnk);
        numSubDirs = -1;
        numFiles = -1;
    }


    public DirectoryEntry getFirstDirectoryChild() {
        return load.getDirectory(firstDirectoryChildLnk);
    }


    public FileEntry getFirstFileChild() {
        return load.getFile(firstFileChildLnk);
    }

    public DirectoryEntry getNextSibling() {
        return load.getDirectory(nextSiblingLnk);
    }


    public void setFirstDirectoryChildLnk(int firstDirectoryChildLnk) {
        this.firstDirectoryChildLnk = firstDirectoryChildLnk;
    }


    public void setFirstFileChildLnk(int firstFileChildLnk) {
        this.firstFileChildLnk = firstFileChildLnk;
    }


    public void setNextSiblingLnk(int nextSiblingLnk) {
        this.nextSiblingLnk = nextSiblingLnk;
    }



    
    /**
     * This variable contains a cached version of the 
     * elements one gets from getFirstDirectoryChild.
     * Warning: The functions depending on this
     * @link #getAllDirs()
     * 
     * should only be used if the structure does not change anymore.
     */
    List<DirectoryEntry> cacheSubDirs = null;
    /**
     * This variable contains a cached version of the 
     * elements one gets from getFirstFileChild.
     * Warning: The functions depending on this
     * @link #getAllFiles()
     * should only be used if the structure does not change anymore.
     */
    List<FileEntry> cacheFiles = null;

    /**
     * Only use this function if the structure does not change
     * anymore.
     * @return all files in this directory
     */
    public List<DirectoryEntry> getAllDirs() {
        if (cacheSubDirs == null)
            createCacheSubDirs();
        
        return cacheSubDirs;
    }

    private void createCacheSubDirs() {
        if (numSubDirs >= 0) {
            cacheSubDirs = new SubDirList(load, this, firstDirectoryChildLnk, numSubDirs);
            return;
        }
        
        cacheSubDirs = new Vector<DirectoryEntry>();
        DirectoryEntry dir = getFirstDirectoryChild();
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
    public List<FileEntry> getAllFiles() {
        if (cacheFiles == null)
            createCacheFiles();

        return cacheFiles;
    }

    private void createCacheFiles() {
        if (numSubDirs >= 0) {
            cacheFiles = new FilesList(load, this, firstFileChildLnk, numFiles);
            return;
        }
        
        cacheFiles = new Vector<FileEntry>();
        FileEntry file = getFirstFileChild();
        while (file != null) {
            cacheFiles.add(file);
            file = file.getNextSibling();
        }
    }
    
    public FileSystemEntry getFileSystemEntryForRef(String refPosition) {
        for (DirectoryEntry dir : getAllDirs()) {
            String dirName = dir.getName();
            if (!refPosition.startsWith(dirName))
                continue;
            if (refPosition.length() == dirName.length())
                return dir;
            if (System.getProperty("file.separator").equals("" + refPosition.charAt(dirName.length())))
                return dir.getFileSystemEntryForRef(refPosition.substring(dirName.length() + 1));
        }

        for (FileEntry file : getAllFiles()) 
            if (refPosition.equals(file.getName()))
                return file;

        return null;
    }


    public void setNumDirs(int numDirs) {
        this.numSubDirs = numDirs;
        
    }


    public void setNumFiles(int numFiles) {
        this.numFiles = numFiles;
        
    }


    public int getNumDirs() {
        if (numSubDirs != -1)
            return numSubDirs;
        
        return getAllDirs().size();
    }


    public int getNumFiles() {
        if (numFiles != -1)
            return numFiles;

        return getAllFiles().size();
    }
    
}
