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

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

public class ResultModel implements TreeModel {

    final Result result;
    
    public ResultModel(final Result result) {
        this.result = result;
    }
    
    public Object getRoot() {
        return result.getDirectory();
    }

    public Object getChild(Object obj, int index) {
        if (!(obj instanceof DirectoryEntry))
            return null;
        DirectoryEntry dir = (DirectoryEntry) obj;
        if ((index >= 0) && (index < dir.getAllDirs().size()))
            return dir.getAllDirs().get(index);
        if ((index >= dir.getAllDirs().size()) && (index < dir.getAllFiles().size() + dir.getAllDirs().size()))
            return dir.getAllFiles().get(index - dir.getAllDirs().size());
        return null;
    }

    public int getChildCount(Object obj) {
        if (!(obj instanceof DirectoryEntry))
            return 0;
        DirectoryEntry dir = (DirectoryEntry) obj;
        return dir.getAllDirs().size() + dir.getAllFiles().size();
    }

    public boolean isLeaf(Object obj) {
        if (obj instanceof DirectoryEntry) 
            return false;
        return true;
    }

    public void valueForPathChanged(TreePath arg0, Object arg1) {
        // Tree Model does not change
    }

    public int getIndexOfChild(Object parent, Object child) {
        if (!(parent instanceof DirectoryEntry))
            return -1;
        DirectoryEntry dir = (DirectoryEntry) parent;
        if (child instanceof DirectoryEntry) {
            DirectoryEntry childDir = (DirectoryEntry) child;
            return dir.getAllDirs().indexOf(childDir);
        }
        if (child instanceof FileEntry) {
            FileEntry childFile = (FileEntry) child;
            int index = dir.getAllFiles().indexOf(childFile);
            if (index == -1)
                return -1;
            return index + dir.getAllDirs().size();
        }

        return -1;
    }

    public void addTreeModelListener(TreeModelListener arg0) {
        // Tree Model does not change
    }

    public void removeTreeModelListener(TreeModelListener arg0) {
        // Tree Model does not change
    }

}
