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

import java.util.Collections;
import java.util.Vector;

import net.sourceforge.mecat.catalog.filesystem.Comparators;
import net.sourceforge.mecat.catalog.filesystem.FileSystemEntry;
import net.sourceforge.mecat.catalog.filesystem.ModifiableDirectoryEntry;
import net.sourceforge.mecat.catalog.filesystem.ModifiableFileEntry;
import net.sourceforge.mecat.catalog.filesystem.ModifiableResult;

public class Result implements ModifiableResult {
    ModifiableDirectoryEntry directory;
    Vector<ModifiableDirectoryEntry> dirs;
    Vector<ModifiableFileEntry> files;
    
    ModifiableFileEntry firstAlph;
    ModifiableFileEntry firstDate;
    ModifiableFileEntry firstSize;
    ModifiableFileEntry firstMD5;
    
    static public Result goodCopy(net.sourceforge.mecat.catalog.filesystem.Result result) {
        Result ret = rawCopy(result);
        (new ResultFromReality(null)).finish(ret);
        return ret;
    }
    
    static public Result rawCopy(net.sourceforge.mecat.catalog.filesystem.Result result) {
        Vector<ModifiableDirectoryEntry> dirs = new Vector<ModifiableDirectoryEntry>();
        Vector<ModifiableFileEntry> files = new Vector<ModifiableFileEntry>();
        
        DirectoryEntry dir = rawCopy(result.getDirectory(), null, dirs, files);
        dirs.add(dir);
        
        return new Result(dir, dirs, files);
    }
    
    
    /**
     * 
     * Copy all information that were read from the file system into a new DirectoryEntry.
     * All extra information are gone. As for the order by alph, size, data or md5.
     * Those information have to be rebuild.
     * 
     * @param dir
     * @param parent
     * @param dirs This is an accumulator of all directories from one result.
     * @param files This is an accumulator of all files from one result.
     * @return
     */
    static public DirectoryEntry rawCopy(net.sourceforge.mecat.catalog.filesystem.DirectoryEntry dir, DirectoryEntry parent, Vector<ModifiableDirectoryEntry> dirs, Vector<ModifiableFileEntry> files) {

        DirectoryEntry ret = new DirectoryEntry(dir.getName(), parent);

        Vector<DirectoryEntry> childDirsConverted = new Vector<DirectoryEntry>();
        Vector<FileEntry> childFilesConverted = new Vector<FileEntry>();

        // Iterate through the original sub directories
        net.sourceforge.mecat.catalog.filesystem.DirectoryEntry childDir = dir.getFirstDirectoryChild();

        int numDirs = 0;
        while (childDir != null) {
            numDirs++;
            DirectoryEntry childDirConverted = rawCopy(childDir, ret, dirs, files);
            dirs.add(childDirConverted);
            childDirsConverted.add(childDirConverted);
            
            childDir = childDir.getNextSibling();
        } 

        // Iterate through the original file directories
        net.sourceforge.mecat.catalog.filesystem.FileEntry childFile = dir.getFirstFileChild();

        int numFiles = 0;
        while (childFile != null) {
            numFiles++;
            FileEntry childFileConverted = rawCopy(childFile, ret);
            files.add(childFileConverted);
            childFilesConverted.add(childFileConverted);
            
            childFile = childFile.getNextSibling();
        }         

        // Sort files and subdirectories by name
        Collections.sort(childDirsConverted, Comparators.NameComparator);
        Collections.sort(childFilesConverted, Comparators.NameComparator);

        // Set first child directory and file
        // Set the sibling relations between sub-directories and files
        ret.setNumDirs(childDirsConverted.size());
        
        if (!childDirsConverted.isEmpty()) {
            ret.setFirstDirectoryChild(childDirsConverted.get(0));
            
            for (int i = 0; i < childDirsConverted.size() - 1; i++)
                childDirsConverted.get(i).setNextSibling(childDirsConverted.get(i + 1));
            
            childDirsConverted.get(childDirsConverted.size() - 1).setNextSibling(null);
        } else
            ret.setFirstDirectoryChild(null);
        
        ret.setNumFiles(childFilesConverted.size());
        
        if (!childFilesConverted.isEmpty()) {
            ret.setFirstFileChild(childFilesConverted.get(0));
            
            for (int i = 0; i < childFilesConverted.size() - 1; i++)
                childFilesConverted.get(i).setNextSibling(childFilesConverted.get(i + 1));

            childFilesConverted.get(childFilesConverted.size() - 1).setNextSibling(null);
        } else
            ret.setFirstFileChild(null);
        
        return ret;
    }
    
    static public FileEntry rawCopy(net.sourceforge.mecat.catalog.filesystem.FileEntry dir, ModifiableDirectoryEntry parent) {
        return new FileEntry(dir.getName(), parent, dir.getSize(), dir.getDate(), dir.getMD5SUM());
    }
    
    public Result(ModifiableDirectoryEntry directory) {
        this.directory = directory;
        dirs = new Vector<ModifiableDirectoryEntry>();
        files = new Vector<ModifiableFileEntry>();
        if (directory != null)
            addRecursive(directory);
    }
    
    private void addRecursive(ModifiableDirectoryEntry directory) {
        dirs.add(directory);
        files.addAll(directory.getAllFiles());
        for (ModifiableDirectoryEntry dir : directory.getAllDirs())
            addRecursive(dir);
    }

    public Result(ModifiableDirectoryEntry directory, Vector<ModifiableDirectoryEntry> dirs, Vector<ModifiableFileEntry> files) {
        this.directory = directory;
        this.dirs = dirs;
        this.files = files;
    }

    public ModifiableDirectoryEntry getDirectory() {
        return directory;
    }

    public Vector<ModifiableDirectoryEntry> getDirs() {
        return dirs;
    }

    public Vector<ModifiableFileEntry> getFiles() {
        return files;
    }

    public ModifiableFileEntry getFirstAlph() {
        return firstAlph;
    }

    public void setFirstAlph(ModifiableFileEntry firstAlph) {
        this.firstAlph = firstAlph;
    }

    public ModifiableFileEntry getFirstDate() {
        return firstDate;
    }

    public void setFirstDate(ModifiableFileEntry firstDate) {
        this.firstDate = firstDate;
    }

    public ModifiableFileEntry getFirstMD5() {
        return firstMD5;
    }

    public void setFirstMD5(ModifiableFileEntry firstMD5) {
        this.firstMD5 = firstMD5;
    }

    public ModifiableFileEntry getFirstSize() {
        return firstSize;
    }

    public void setFirstSize(ModifiableFileEntry firstSize) {
        this.firstSize = firstSize;
    }

    public int getNumFiles() {
        return files.size();
    }

    public int getNumDirs() {
        return dirs.size();
    }

    public static net.sourceforge.mecat.catalog.filesystem.FileSystemEntry getFileSystemEntryForRef(ModifiableDirectoryEntry directory, String refPosition) {
        for (ModifiableDirectoryEntry dir : directory.getAllDirs()) {
            String dirName = dir.getName();
            if (!refPosition.startsWith(dirName))
                continue;
            if (refPosition.length() == dirName.length())
                return dir;
            if (System.getProperty("file.separator").equals("" + refPosition.charAt(dirName.length())))
                return getFileSystemEntryForRef(dir, refPosition.substring(dirName.length() + 1));
        }

        for (ModifiableFileEntry file : directory.getAllFiles()) 
            if (refPosition.equals(file.getName()))
                return file;

        return null;
    }

    public FileSystemEntry getFileSystemEntryForRef(String refPosition) {
        String dirName = getDirectory().getName();
        if (!refPosition.startsWith(dirName))
            return null;
        if (dirName.length() == refPosition.length())
            return getDirectory();
        if (!System.getProperty("file.separator").equals("" + refPosition.charAt(dirName.length())))
            return null;
        
        return getFileSystemEntryForRef(getDirectory(), refPosition.substring(dirName.length() + 1));
    }

    public String getRefForFileSystemEntry(FileSystemEntry fileSystemEntry) {
        return fileSystemEntry.getFullName();
    }
}
