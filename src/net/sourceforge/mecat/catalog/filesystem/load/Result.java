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

import java.util.Vector;

import net.sourceforge.mecat.catalog.filesystem.FileSystemEntry;

public class Result implements net.sourceforge.mecat.catalog.filesystem.Result {
    final DirectoryEntry directory;
//    Vector<DirectoryEntry> dirs = null;
//    Vector<FileEntry> files = null;
    
    FileEntry firstAlph = null;
    FileEntry firstDate = null;
    FileEntry firstSize = null;
    FileEntry firstMD5 = null;
    
    final FileSystemLoadShallow load;
    
    public Result(FileSystemLoadShallow load) {
        this.load = load;
        
        this.directory = load.getDirectory(0);
    }

    public DirectoryEntry getDirectory() {
        return directory;
    }

    public Vector<DirectoryEntry> getDirs() {
/**
 * This produces hard links and thereby takes a lot of memory
        if (dirs != null)
            return dirs;
        dirs = load.getAllDirs();
        return dirs;
 */
        return load.getAllDirs();
    }

    public Vector<FileEntry> getFiles() {
        /**
         * This produces hard links and thereby takes a lot of memory
        if (files != null)
            return files;
        files = load.getAllFiles();
        return files;
        */
        return load.getAllFiles();
    }

    public FileEntry getFirstAlph() {
        if (firstAlph != null)
            return firstAlph;
        firstAlph = load.getFile(load.getFirstAlphLnk());
        return firstAlph;
    }

    public FileEntry getFirstDate() {
        if (firstDate != null)
            return firstDate;
        firstDate = load.getFile(load.getFirstDateLnk());
        return firstDate;
    }

    public FileEntry getFirstMD5() {
        if (firstMD5 != null)
            return firstMD5;
        firstMD5 = load.getFile(load.getFirstMD5Lnk());
        return firstMD5;
    }

    public FileEntry getFirstSize() {
        if (firstSize != null)
            return firstSize;
        firstSize = load.getFile(load.getFirstSizeLnk());
        return firstSize;
    }

    public int getNumFiles() {
        return load.numFiles;
    }

    public int getNumDirs() {
        return load.numDirs;
    }

    public FileSystemEntry getFileSystemEntryForRef(String refPosition) {
        String dirName = getDirectory().getName();
        if (!refPosition.startsWith(dirName))
            return null;
        if (dirName.length() == refPosition.length())
            return getDirectory();
        if (!System.getProperty("file.separator").equals("" + refPosition.charAt(dirName.length())))
            return null;
        
        return getDirectory().getFileSystemEntryForRef(refPosition.substring(dirName.length() + 1));
    }

    public String getRefForFileSystemEntry(FileSystemEntry fileSystemEntry) {
        return fileSystemEntry.getFullName();
    }


}
