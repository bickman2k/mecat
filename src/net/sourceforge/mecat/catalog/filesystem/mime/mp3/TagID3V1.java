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
 * Created on Jul 15, 2006
 * @author Stephan Richard Palm
 * 
 */
package net.sourceforge.mecat.catalog.filesystem.mime.mp3;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ResourceBundle;

import net.sourceforge.mecat.catalog.filesystem.mime.AbstractTag;
import net.sourceforge.mecat.catalog.filesystem.mime.TagType;
import net.sourceforge.mecat.catalog.option.Options;

public class TagID3V1 extends AbstractTag {

    ResourceBundle res = Options.getI18N(TagID3V1.class);

    final byte[] b;
    
    public String trim(String str) {
        int index = str.indexOf(0);
        if (index == -1)
            return str;
        return str.substring(0, index);
    }
    
    // Parse TagID3V1 from byte array of length 128
    public TagID3V1(byte[] b) {
        songtitle = trim(new String(b, 3, 30));
        interpret = trim(new String(b, 33, 30));
        album = trim(new String(b, 63, 30));
//        year = b[93] << 24 | b[94] << 16 | b[95] << 8 | b[96];
        try {
            year = Long.valueOf(trim(new String(b, 93, 4)));
        } catch (Exception e) {
            year = 0;
        }
        comment = trim(new String(b, 3, 30));
        genre = b[127];
        if (b[125] == 0) 
            titlenumber = b[126];
        this.b = b;
    }

    /**
     * Songtitel 30 bytes in tag
     */
    String songtitle;
    /**
     * Artist/Interpret 30 bytes in tag
     */
    String interpret;
    /**
     * Album name 30 bytes in tag
     */
    String album;
    /**
     * Year 4 bytes in tag
     */
    long year;
    /**
     * Comment 30 bytes in tag
     */
    String comment;
    /**
     * Genre 1 byte in tag
     */
    byte genre;
    
    /** 
     * Titlenumber 1 byte
     */
    byte titlenumber = 0;
    
    
    public String toString() {
        return 
            ((songtitle.length() > 0)   ?   (res.getString("Songtitle") + ": " + songtitle + ", ")       :"") +
            ((interpret.length() > 0)   ?   (res.getString("Interpret") + ": " + interpret + ", ")       :"") +
            ((album.length() > 0)       ?   (res.getString("Album") + ": " + album + ", ")               :"") +
            ((year > 0)                 ?   (res.getString("Year") + ": " + year + ", ")                 :"") +
            ((comment.length() > 0)     ?   (res.getString("Comment") + ": " + comment + ", ")            :"") +
            ((genre != -1)              ?   (res.getString("Genre") + ": " + genre + ", ")               :"") +
            ((titlenumber > 0)          ?   (res.getString("Titlenumber") + ": " + titlenumber)   :"");
    }

    public void copyTag(DataOutputStream ostream) throws IOException {
        ostream.write(b);
    }

    public int getLength() {
        return 128;
    }

    public int getStartPosition() {
        return -128;
    }

    public String getHTMLInfo() {
        return "<h2>" + "id3 v1" + ((genre != -1)? ".1" : "") + "</h2>" +
        ((songtitle.length() > 0)   ?   ("<h3>" + res.getString("Songtitle") + "</h3>" + songtitle)       :"") +
        ((interpret.length() > 0)   ?   ("<h3>" + res.getString("Interpret") + "</h3>" + interpret)       :"") +
        ((album.length() > 0)       ?   ("<h3>" + res.getString("Album") + "</h3>" + album)               :"") +
        ((year > 0)                 ?   ("<h3>" + res.getString("Year") + "</h3>" + year)                 :"") +
        ((comment.length() > 0)     ?   ("<h3>" + res.getString("Comment") + "</h3>" + comment)            :"") +
        ((genre != -1)              ?   ("<h3>" + res.getString("Genre") + "</h3>" + genre)               :"") +
        ((titlenumber > 0)          ?   ("<h3>" + res.getString("Titlenumber") + "</h3>" + titlenumber)   :"");
    }

    public TagType getTagType() {
        return TagType.ID3V1;
    }
}
