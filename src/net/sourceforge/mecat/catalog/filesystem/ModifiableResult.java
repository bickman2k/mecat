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
package net.sourceforge.mecat.catalog.filesystem;

import java.util.Vector;

public interface ModifiableResult extends Result{
    /**
     * Returns the root directory. From this directory any directory and file is accessable.
     * @return root directory
     */
    public ModifiableDirectoryEntry getDirectory();
    
    /**
     * Get a list of all directories, including the root directory and all sub directories recursivly.
     * 
     * @return all direct sub-directories
     */
    public Vector<? extends ModifiableDirectoryEntry> getDirs();
    
    /**
     * Get a list of all files.
     * 
     * @return all files in file list
     */
    public Vector<? extends ModifiableFileEntry> getFiles();
    
    /**
     * Get the first File in alphabetic order
     * @return first file in alphabetic order
     */
    public ModifiableFileEntry getFirstAlph();
    public void setFirstAlph(ModifiableFileEntry firstAlph);
    
    /**
     * Get the first file in date order
     * @return first file in date order
     */
    public ModifiableFileEntry getFirstDate();
    public void setFirstDate(ModifiableFileEntry firstDate);
    
    /**
     * Get the first file in md5 order.
     * This functionality is interessting for the fast identification of Doppelganger 
     * 
     * @return first file in md5 order
     */
    public ModifiableFileEntry getFirstMD5();
    public void setFirstMD5(ModifiableFileEntry firstMD5);
    
    /**
     * Get the first file in size order
     * @return first file in size order
     */
    public ModifiableFileEntry getFirstSize();
    public void setFirstSize(ModifiableFileEntry firstSize);

    /**
     * This function returns the number of files stored in the result.
     * Using this function for getting the number of files can be 
     * a lot faster then getFiles().size() and take less amount of memory.
     * Therefor this function is better for getting the number of files the
     * getFiles().size().
     * @return number of files in the result
     */
    public int getNumFiles();
    
    /**
     * This function returns the number of diretories stored in the result
     * Using this function for getting the number of files can be 
     * a lot faster then getDirs().size() and take less amount of memory.
     * Therefor this function is better for getting the number of files the
     * getDirs().size().
     * @return number of directories in the result
     */
    public int getNumDirs();
}
