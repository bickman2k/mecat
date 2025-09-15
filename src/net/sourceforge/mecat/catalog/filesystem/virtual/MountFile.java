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
 * Created on Jun 10, 2006
 * @author Stephan Richard Palm
 * 
 */
package net.sourceforge.mecat.catalog.filesystem.virtual;

import net.sourceforge.mecat.catalog.filesystem.Detail;
import net.sourceforge.mecat.catalog.filesystem.DirectoryEntry;
import net.sourceforge.mecat.catalog.filesystem.FileEntry;

public class MountFile implements FileEntry {

    final FileEntry file;
    final String mountName;
    
    public MountFile(FileEntry file, String mountName) {
        this.file = file;
        this.mountName = mountName;
    }

    public FileEntry getNextAlph() {
        return file.getNextAlph();
    }

    public FileEntry getNextDate() {
        return file.getNextDate();
    }

    public FileEntry getNextMD5() {
        return file.getNextMD5();
    }

    public FileEntry getNextSibling() {
        return file.getNextSibling();
    }

    public FileEntry getNextSize() {
        return file.getNextSize();
    }

    public long getDate() {
        return file.getDate();
    }

    public Detail getDetail() {
        return file.getDetail();
    }

    public byte[] getMD5SUM() {
        return file.getMD5SUM();
    }

    public long getSize() {
        return file.getSize();
    }

    public String getName() {
        return mountName;
    }

    public DirectoryEntry getParent() {
        return file.getParent();
    }

    public String getFullName() {
        if (getParent() == null)
            return getName();
        return getParent().getFullName() + System.getProperty("file.separator") + getName();
    }

    public int depht() {
        return file.depht();
    }

}
