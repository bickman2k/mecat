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
package net.sourceforge.mecat.catalog.filesystem.load;

import java.io.Serializable;

import net.sourceforge.mecat.catalog.filesystem.Detail;

public class FileEntry extends FileSystemEntry  implements net.sourceforge.mecat.catalog.filesystem.FileEntry {
    
    /**
     * Link to next File in the same directory
     * 
     * -1 if there is no next file
     */
    int nextSiblingLnk;

    
    /**
     * Link to next file in alphabetic order
     * This allows fast order in size.
     * 
     * -1 if there is no next file
     */
    int nextAlphLnk;
    
    /**
     * Link to next File in order by last changed last.
     * This allows fast order in alphabetic order.
     * 
     * -1 if there is no next file
     */
    int nextDateLnk;

    /**
     * Link to next bigger file
     * 
     * -1 if there is no next bigger file
     */
    int nextSizeLnk;

    /**
     * Link to next File in order by the md5sum.
     * This value allows fast search for Doppelganger.
     * 
     * -1 if there is no next file
     */
    int nextMD5Lnk;

    /**
     * Link to details
     * 0 if there is no detail
     */
    int detailsLnk;

    /**
     * Date of the last change of the file
     */
    long date;

    /**
     * Size of the file
     */
    long size;
    /**
     * 128 bit long MD5 sum. Sum computed as descriped in RFC 1321.
     */
    byte MD5SUM[];
    
    
    public FileEntry(FileSystemLoadShallow load, int nameLnk, int parentLnk/*, int detailsLnk, long date, long size, byte[] md5sum*/) {
        super(load, nameLnk, parentLnk);

/*        this.detailsLnk = detailsLnk;
        this.size = size;
        this.date = date;
        MD5SUM = md5sum;*/
    }


    public FileEntry getNextAlph() {
        return load.getFile(nextAlphLnk);
    }


    public FileEntry getNextDate() {
        return load.getFile(nextDateLnk);
    }


    public FileEntry getNextMD5() {
        return load.getFile(nextMD5Lnk);
    }


    public FileEntry getNextSibling() {
        return load.getFile(nextSiblingLnk);
    }


    public FileEntry getNextSize() {
        return load.getFile(nextSizeLnk);
    }


    public long getDate() {
        return date;
    }


    public Detail getDetail() {
        return load.getDetail(detailsLnk);
    }


    public byte[] getMD5SUM() {
        return MD5SUM;
    }


    public long getSize() {
        return size;
    }


    public void setDate(long date) {
        this.date = date;
    }


    public void setDetailsLnk(int detailsLnk) {
        this.detailsLnk = detailsLnk;
    }


    public void setMD5SUM(byte[] md5sum) {
        MD5SUM = md5sum;
    }


    public void setNextAlphLnk(int nextAlphLnk) {
        this.nextAlphLnk = nextAlphLnk;
    }


    public void setNextDateLnk(int nextDateLnk) {
        this.nextDateLnk = nextDateLnk;
    }


    public void setNextMD5Lnk(int nextMD5Lnk) {
        this.nextMD5Lnk = nextMD5Lnk;
    }


    public void setNextSiblingLnk(int nextSiblingLnk) {
        this.nextSiblingLnk = nextSiblingLnk;
    }


    public void setNextSizeLnk(int nextSizeLnk) {
        this.nextSizeLnk = nextSizeLnk;
    }


    public void setSize(long size) {
        this.size = size;
    }
    
    
    
    
}
