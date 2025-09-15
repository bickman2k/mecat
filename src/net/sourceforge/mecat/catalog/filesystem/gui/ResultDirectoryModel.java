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
package net.sourceforge.mecat.catalog.filesystem.gui;

import java.util.List;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import net.sourceforge.mecat.catalog.filesystem.DirectoryEntry;
import net.sourceforge.mecat.catalog.filesystem.Result;

public class ResultDirectoryModel implements TreeModel {

    final Result result;
    
    public ResultDirectoryModel(final Result result) {
        this.result = result;
    }
    
    public Object getRoot() {
        return result.getDirectory();
    }

    public Object getChild(Object obj, int index) {
        if (!(obj instanceof DirectoryEntry))
            return null;
        DirectoryEntry dir = (DirectoryEntry) obj;
        if (index < 0)
            return null;
        List<? extends DirectoryEntry> list = dir.getAllDirs();
        if (index >= list.size())
            return null;
        return list.get(index);
    }

    public int getChildCount(Object obj) {
        if (!(obj instanceof DirectoryEntry))
            return 0;
        DirectoryEntry dir = (DirectoryEntry) obj;
        List<? extends DirectoryEntry> list = dir.getAllDirs();
        return list.size();
    }

    public boolean isLeaf(Object obj) {
        if (obj instanceof DirectoryEntry) 
            return false;
        DirectoryEntry dir = (DirectoryEntry) obj;
        return dir.getFirstDirectoryChild() != null;
    }

    public void valueForPathChanged(TreePath arg0, Object arg1) {
        // Tree Model does not change
    }

    public int getIndexOfChild(Object parent, Object child) {
        if (!(parent instanceof DirectoryEntry))
            return -1;
        if (!(child instanceof DirectoryEntry))
            return -1;

        DirectoryEntry dir = (DirectoryEntry) parent;
        DirectoryEntry childDir = (DirectoryEntry) child;
        List<? extends DirectoryEntry> list = dir.getAllDirs();

        return list.indexOf(childDir);
    }

    public void addTreeModelListener(TreeModelListener arg0) {
        // Tree Model does not change
    }

    public void removeTreeModelListener(TreeModelListener arg0) {
        // Tree Model does not change
    }

}
