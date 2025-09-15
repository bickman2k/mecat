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
package net.sourceforge.mecat.catalog.filesystem.store;

import java.io.File;

import net.sourceforge.mecat.catalog.filesystem.Detail;
import net.sourceforge.mecat.catalog.filesystem.ModifiableDirectoryEntry;
import net.sourceforge.mecat.catalog.filesystem.ModifiableFileEntry;

public class FileEntry extends FileSystemEntry  implements net.sourceforge.mecat.catalog.filesystem.ModifiableFileEntry {
    
    /**
     * Next File in the same directory
     * 
     * null if there is no next file
     */
    ModifiableFileEntry nextSibling;

    
    /**
     * Next file in alphabetic order
     * This allows fast order in size.
     * 
     * null if there is no next file
     */
    ModifiableFileEntry nextAlph;
    /**
     * Next File in order by last changed last.
     * This allows fast order in alphabetic order.
     * 
     * null if there is no next file
     */
    ModifiableFileEntry nextDate;
    /**
     * Next bigger file
     * 
     * null if there is no next bigger file
     */
    ModifiableFileEntry nextSize;
    /**
     * Next File in order by the md5sum.
     * This value allows fast search for Doppelganger.
     * 
     * null if there is no next file
     */
    ModifiableFileEntry nextMD5;

    Detail detail = null;

    /**
     * Date of the last change of the file
     */
    final long date;
    /**
     * Size of the file
     */
    final long size;
    /**
     * 128 bit long MD5 sum. Sum computed as descriped in RFC 1321.
     */
    byte MD5SUM[] = new byte[16];
    
    
    /**
     * This memory slot is for the creation of the list
     * and has no further use.
     */
    final File original;
    
    
//    public FileEntry(String name, DirectoryEntry parent, Serializable details, long date, long size, byte[] md5sum) {
    public FileEntry(final File original, DirectoryEntry parent) {
        super(original.getName(), parent);

        this.size = original.length();
        this.date = original.lastModified();
        this.original = original;
    }
    
    public FileEntry(String name, ModifiableDirectoryEntry parent, long size, long date, byte md5[]) {
        super(name, parent);
        
        this.size = size;
        this.date = date;
        this.MD5SUM = md5;
        original = null;
    }


    public ModifiableFileEntry getNextAlph() {
        return nextAlph;
    }


    public void setNextAlph(ModifiableFileEntry nextAlph) {
        this.nextAlph = nextAlph;
    }


    public ModifiableFileEntry getNextDate() {
        return nextDate;
    }


    public void setNextDate(ModifiableFileEntry nextDate) {
        this.nextDate = nextDate;
    }


    public ModifiableFileEntry getNextMD5() {
        return nextMD5;
    }


    public void setNextMD5(ModifiableFileEntry nextMD5) {
        this.nextMD5 = nextMD5;
    }


    public ModifiableFileEntry getNextSibling() {
        return nextSibling;
    }


    public void setNextSibling(ModifiableFileEntry nextSibling) {
        this.nextSibling = nextSibling;
    }


    public ModifiableFileEntry getNextSize() {
        return nextSize;
    }


    public void setNextSize(ModifiableFileEntry nextSize) {
        this.nextSize = nextSize;
    }


    public long getDate() {
        return date;
    }


    public Detail getDetail() {
        return detail;
    }

    public void setDetail(Detail detail) {
        this.detail = detail;
    }

    public byte[] getMD5SUM() {
        return MD5SUM;
    }

    public void setMD5SUM(byte[] md5) {
        MD5SUM = md5;
    }

    public long getSize() {
        return size;
    }


    public File getOriginal() {
        return original;
    }

    
    
    
    
}
