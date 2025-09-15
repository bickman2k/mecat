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
package net.sourceforge.mecat.catalog.filesystem;

import java.util.Comparator;

public class Comparators {
    
    public final static Comparator<FileEntry> SizeComparator = new Comparator<FileEntry>(){
        public int compare(FileEntry entry1, FileEntry entry2) {
            if (entry1.getSize() == entry2.getSize())
                return 0;
            if (entry1.getSize() < entry2.getSize())
                return -1;
            return 1;
        }
    };
    
    public final static Comparator<FileSystemEntry> NameComparator = new Comparator<FileSystemEntry>(){
        public int compare(FileSystemEntry entry1, FileSystemEntry entry2) {
            return entry1.getName().compareToIgnoreCase(entry2.getName());
        }
    };
    
    public final static Comparator<FileSystemEntry> NameComparatorCaseSensitiv = new Comparator<FileSystemEntry>(){
        public int compare(FileSystemEntry entry1, FileSystemEntry entry2) {
            return entry1.getName().compareTo(entry2.getName());
        }
    };
    
    public final static Comparator<FileEntry> DateComparator = new Comparator<FileEntry>(){
        public int compare(FileEntry entry1, FileEntry entry2) {
            if (entry1.getDate() == entry2.getDate())
                return 0;
            if (entry1.getDate() < entry2.getDate())
                return -1;
            return 1;
        }
    };
    
    public final static Comparator<FileEntry> MD5Comparator = new Comparator<FileEntry>(){
        public int compare(FileEntry entry1, FileEntry entry2) {
            for (int i = 0; i < 16; i++) {
                if (entry1.getMD5SUM()[i] < entry2.getMD5SUM()[i])
                    return -1;
                if (entry1.getMD5SUM()[i] > entry2.getMD5SUM()[i])
                    return 1;
            }
            return 0;
        }
    };

    protected static boolean isMD5Null(byte[] b) {
        for (int i = 0; i < 16; i++)
            if (b[i] != 0)
                return false;
        return true;
    }

/*    public final static Comparator<FileEntry> DoppelgangerSearchComparator = new Comparator<FileEntry>(){
        public int compare(FileEntry entry1, FileEntry entry2) {
            for (int i = 0; i < 16; i++) {
                if (entry1.getMD5SUM()[i] < entry2.getMD5SUM()[i])
                    return -1;
                if (entry1.getMD5SUM()[i] > entry2.getMD5SUM()[i])
                    return 1;
            }
            return 0;
        }
    };*/

    /**
     * If possible compare by md5 
     * otherwise compare by name and size.
     * 
     * Two files are the same if the md5 sum matches or one is different from 0.
     * And the name and size are equal.
     * 
     */
    public final static Comparator<FileEntry> TotalComparator = new Comparator<FileEntry>(){
        public int compare(FileEntry entry1, FileEntry entry2) {
            // Compare with MD5 if a difference can be assured
            // MD5 is used first because it is a secure determination
            if (!isMD5Null(entry1.getMD5SUM()) && !isMD5Null(entry2.getMD5SUM())) {
                for (int i = 0; i < 16; i++) {
                    if (entry1.getMD5SUM()[i] < entry2.getMD5SUM()[i])
                        return -1;
                    if (entry1.getMD5SUM()[i] > entry2.getMD5SUM()[i])
                        return 1;
                }
            }
            // Go for the name if MD5 could not be used
            // files with different names are assumed to be different
            int c = entry1.getName().compareTo(entry2.getName());
            if (c != 0)
                return c;
            // If the name did not help use the size
            // files with different size are assumed to be different
            if (entry1.getSize() < entry2.getSize())
                return -1;
            if (entry1.getSize() > entry2.getSize())
                return 1;
            return 0;
        }
    };
    
}
