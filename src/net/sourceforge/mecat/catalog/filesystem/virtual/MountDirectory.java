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
 * Created on Jun 9, 2006
 * @author Stephan Richard Palm
 * 
 */
package net.sourceforge.mecat.catalog.filesystem.virtual;

import java.util.List;

import net.sourceforge.mecat.catalog.filesystem.DirectoryEntry;
import net.sourceforge.mecat.catalog.filesystem.FileEntry;

public class MountDirectory extends Object implements DirectoryEntry {

    final DirectoryEntry directory;
    final String mountName;
    
    public MountDirectory(DirectoryEntry directory, String mountName) {
        this.directory = directory;
        this.mountName = mountName;
    }

    public DirectoryEntry getNextSibling() {
        return directory.getNextSibling();
    }

    public DirectoryEntry getFirstDirectoryChild() {
        return directory.getFirstDirectoryChild();
    }

    public FileEntry getFirstFileChild() {
        return directory.getFirstFileChild();
    }

    public List< ? extends DirectoryEntry> getAllDirs() {
        return directory.getAllDirs();
    }

    public List< ? extends FileEntry> getAllFiles() {
        return directory.getAllFiles();
    }

    public String getName() {
        return mountName;
    }

    public DirectoryEntry getParent() {
        return directory.getParent();
    }

    public String getFullName() {
        if (getParent() == null)
            return getName();
        return getParent().getFullName() + System.getProperty("file.separator") + getName();
    }

    public int depht() {
        return directory.depht();
    }

    public int getNumDirs() {
        return directory.getNumDirs();
    }

    public int getNumFiles() {
        return directory.getNumFiles();
    }

}
