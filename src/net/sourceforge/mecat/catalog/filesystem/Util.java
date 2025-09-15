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
 * Created on Jun 13, 2006
 * @author Stephan Richard Palm
 * 
 */
package net.sourceforge.mecat.catalog.filesystem;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

import javax.swing.JEditorPane;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;

import net.sourceforge.mecat.catalog.filesystem.store.MD5Thread;

public class Util {

    public static boolean equals(Result result0, Result result1) {
        return equals(result0.getDirectory(), result1.getDirectory());
    }

    public static boolean equals(DirectoryEntry directory0, DirectoryEntry directory1) {
        
        if (!directory0.getName().equals(directory1.getName()))
            return false;

        Vector<DirectoryEntry> childDirs0 = new Vector<DirectoryEntry>(directory0.getAllDirs());
        Vector<DirectoryEntry> childDirs1 = new Vector<DirectoryEntry>(directory1.getAllDirs());

        Vector<FileEntry> childFiles0 = new Vector<FileEntry>(directory0.getAllFiles());
        Vector<FileEntry> childFiles1 = new Vector<FileEntry>(directory1.getAllFiles());

        if (childDirs0.size() != childDirs1.size())
            return false;

        if (childFiles0.size() != childFiles1.size())
            return false;

        // Sorting the files makes the test faster
        Collections.sort(childDirs0, new Comparator<DirectoryEntry>(){
            public int compare(DirectoryEntry dir0, DirectoryEntry dir1) {
                return dir0.getName().compareToIgnoreCase(dir1.getName());
            }
        });
        Collections.sort(childDirs1, new Comparator<DirectoryEntry>(){
            public int compare(DirectoryEntry dir0, DirectoryEntry dir1) {
                return dir0.getName().compareToIgnoreCase(dir1.getName());
            }
        });
        Collections.sort(childFiles0, new Comparator<FileEntry>(){
            public int compare(FileEntry file0, FileEntry file1) {
                return file0.getName().compareToIgnoreCase(file1.getName());
            }
        });
        Collections.sort(childFiles1, new Comparator<FileEntry>(){
            public int compare(FileEntry file0, FileEntry file1) {
                return file0.getName().compareToIgnoreCase(file1.getName());
            }
        });

        
        loop0:
        for (DirectoryEntry sub0 : childDirs0) {
            for (DirectoryEntry sub1 : childDirs1) {
                if (equals(sub0, sub1)) {
                    childDirs1.remove(sub1);
                    continue loop0;
                }
            }
            return false;
        }

        loop1:
        for (FileEntry file0 : childFiles0) {
            for (FileEntry file1 : childFiles1) {
                if (equals(file0, file1)) {
                    childFiles1.remove(file1);
                    continue loop1;
                }
            }
            return false;
        }
        
        return true;
    }

    public static boolean equals(FileEntry file0, FileEntry file1) {
        if (!file0.getName().equals(file1.getName()))
            return false;
        
        if (file0.getSize() != file1.getSize())
            return false;
        
        if (!MD5Thread.compareMD5IgnoreFalse(file0.getMD5SUM(), file1.getMD5SUM()))
            return false;

        return true;
    }

    public static FileEntry getFirstFileLeftRecursive(Result result) {
        return getFirstFileLeftRecursive(result.getDirectory());
    }
    
    public static FileEntry getFirstFileLeftRecursive(DirectoryEntry dir) {
        FileEntry entry = dir.getFirstFileChild();
        
        // If there is a file in this directory take it
        if (entry != null)
            return entry;

        // else take recursivly the file from the first subdirectory that has one 
        for (DirectoryEntry sub : dir.getAllDirs()) {
            entry = getFirstFileLeftRecursive(sub);
            if (entry != null)
                return entry;
        }
        
        return null;
    }
    
    public static FileEntry getNextFileLeftRecursive(FileEntry entry) {
        // Take next in the same directory
        FileEntry ret = entry.getNextSibling();
        
        if (ret != null)
            return ret;
        
        DirectoryEntry dir = entry.getParent();
        
        // take from a subdirectory if available
        for (DirectoryEntry sub : dir.getAllDirs()) {
            ret = getFirstFileLeftRecursive(sub);

            if (ret != null)
                return ret;
        }

        do {
            // check siblings of the parent directory
            while (dir.getNextSibling() != null) {
                dir = dir.getNextSibling();
                ret = getFirstFileLeftRecursive(dir);
                
                if (ret != null)
                    return ret;
            }
            // for all parent directories
            dir = dir.getParent();
        } while (dir != null);
        
        
        return null;
    }
    
    
    public static void showDetails(FileEntry fileEntry, Component parent) {
        Detail detail = fileEntry.getDetail();
        if (detail == null)
            return;
        JEditorPane txt = new JEditorPane();
        txt.setContentType("text/html");
        txt.setText("<html><body>" + detail.getHTMLInfo() + "</body></html>");
        txt.setEditable(false);
        JOptionPane.showMessageDialog(parent, new JScrollPane(txt){
        @Override
        public Dimension getPreferredSize() {
            Dimension dim = super.getPreferredSize();
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            return new Dimension(Math.min((int)(screenSize.width * 0.9) - 100, dim.width), Math.min((int)(screenSize.height * 0.9) - 100, dim.height));
        }}, "Details", JOptionPane.INFORMATION_MESSAGE);
    }
}
