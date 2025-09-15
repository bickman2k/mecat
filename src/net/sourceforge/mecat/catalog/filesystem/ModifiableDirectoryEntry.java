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

import java.util.List;

public interface ModifiableDirectoryEntry extends DirectoryEntry {

    /**
     * Get the next Directory that is in the same directory as this directory
     * @return the next sibling of this directory
     */
    public abstract ModifiableDirectoryEntry getNextSibling();    
    public void setNextSibling(ModifiableDirectoryEntry nextSibling);

    /**
     * Get the first Directory which is a subdirectory of this directoy 
     * @return first directory in directory
     */
    public abstract ModifiableDirectoryEntry getFirstDirectoryChild();
    public void setFirstDirectoryChild(ModifiableDirectoryEntry firstDirectoryChild);

    /**
     * Get the first files which is lies in this directoy 
     * @return first file in directory
     */
    public abstract ModifiableFileEntry getFirstFileChild();
    public void setFirstFileChild(ModifiableFileEntry firstFileChild);

    
    
    
    /**
     * Only use this function if the structure does not change
     * anymore.
     * @return all files in this directory
     */
    public List<? extends ModifiableDirectoryEntry> getAllDirs();
    /**
     * Only use this function if the structure does not change
     * anymore.
     * @return all files in this directory
     */
    public List<? extends ModifiableFileEntry> getAllFiles();
}
