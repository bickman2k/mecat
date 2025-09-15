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

import net.sourceforge.mecat.catalog.filesystem.Detail;
import net.sourceforge.mecat.catalog.filesystem.DetailList;
import net.sourceforge.mecat.catalog.filesystem.FileEntry;

public class VirtualFile extends VirtualEntry implements FileEntry {

    final List<FileEntry> fileList;
    VirtualFile nextSibling = null;
    
    public VirtualFile(VirtualDirectory parent, List<FileEntry> fileList) {
        super(parent);
        this.fileList = fileList;
    }

    public VirtualFile(VirtualDirectory parent, FileEntry entryFile) {
        super(parent);
        this.fileList = new Vector<FileEntry>();
        fileList.add(entryFile);
    }

    public FileEntry getNextAlph() {
        // TODO Auto-generated method stub
        return null;
    }

    public FileEntry getNextDate() {
        // TODO Auto-generated method stub
        return null;
    }

    public FileEntry getNextMD5() {
        // TODO Auto-generated method stub
        return null;
    }

    public void setNextSibling(VirtualFile nextSibling) {
        this.nextSibling = nextSibling;
    }
    
    public VirtualFile getNextSibling() {
        return nextSibling;
    }

    public FileEntry getNextSize() {
        // TODO Auto-generated method stub
        return null;
    }

    public long getDate() {
        return fileList.get(0).getDate();
    }

    public Detail getDetail() {
        DetailList detailList = new DetailList();
        for (FileEntry file : fileList) {
            Detail detail = file.getDetail();
            if (detail != null)
                detailList.add(detail);
        }
        if (detailList.size() == 0)
            return null;
        return detailList;
    }

    public byte[] getMD5SUM() {
        return fileList.get(0).getMD5SUM();
    }

    public long getSize() {
        return fileList.get(0).getSize();
    }

    public String getName() {
        return fileList.get(0).getName();
    }

    public void add(FileEntry entry) {
        fileList.add(entry);
    }
    
    
}
