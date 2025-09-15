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
package net.sourceforge.mecat.catalog.filesystem.virtual;

import java.util.List;
import java.util.Vector;

import net.sourceforge.mecat.catalog.filesystem.DirectoryEntry;
import net.sourceforge.mecat.catalog.filesystem.FileEntry;
import net.sourceforge.mecat.catalog.filesystem.FileSystemEntry;
import net.sourceforge.mecat.catalog.filesystem.Result;

public class VirtualResult implements Result {

    final List<Mount> mounts;
    final VirtualDirectory root;
    final public static String rootName = "ROOT";
    
    public VirtualResult(final Mount mount) {
        this(new Vector<Mount>(){{ 
            add(mount); 
        }});
    }
    
    public VirtualResult(List<Mount> mounts) {
        this.mounts = new Vector<Mount>(mounts);
        
        // Get those mounts that are relative to the root
        Vector<Mount> rootRelative = new Vector<Mount>();
        for (Mount mount : mounts)
            if (mount.getPosition().startsWith(rootName + System.getProperty("file.separator")))
                rootRelative.add(mount);

        // Get those mounts directly at the root
        Vector<DirectoryEntry> rootMounts = new Vector<DirectoryEntry>();
        for (Mount mount : mounts)
            if (mount.getPosition().equals(rootName) && mount.entry instanceof DirectoryEntry)
                rootMounts.add((DirectoryEntry) mount.entry);

        // Create root directory
        if (rootMounts.isEmpty())
            root = new VirtualDirectory(null, rootRelative, "ROOT");
        else
            root = new VirtualDirectory(null, rootRelative, rootMounts);
    }

    public DirectoryEntry getDirectory() {
        return root;
    }

    public Vector< ? extends DirectoryEntry> getDirs() {
        // TODO Auto-generated method stub
        return null;
    }

    public Vector< ? extends FileEntry> getFiles() {
        // TODO Auto-generated method stub
        return null;
    }

    public FileEntry getFirstAlph() {
        // TODO Auto-generated method stub
        return null;
    }

    public FileEntry getFirstDate() {
        // TODO Auto-generated method stub
        return null;
    }

    public FileEntry getFirstMD5() {
        // TODO Auto-generated method stub
        return null;
    }

    public FileEntry getFirstSize() {
        // TODO Auto-generated method stub
        return null;
    }

    public int getNumFiles() {
        // TODO Auto-generated method stub
        return 0;
    }

    public int getNumDirs() {
        // TODO Auto-generated method stub
        return 0;
    }

    public FileSystemEntry getFileSystemEntryForRef(String refPosition) {
        // TODO Auto-generated method stub
        return null;
    }

    public String getRefForFileSystemEntry(FileSystemEntry fileSystemEntry) {
        // TODO Auto-generated method stub
        return null;
    }

}
