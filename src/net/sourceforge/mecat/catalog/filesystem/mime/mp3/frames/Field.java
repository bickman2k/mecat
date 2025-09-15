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
 * Created on Jul 18, 2006
 * @author Stephan Richard Palm
 * 
 */
package net.sourceforge.mecat.catalog.filesystem.mime.mp3.frames;

public class Field {
    final String name;
    final FieldType type;
    final int size;
    
    String encodingOverride = null;
    
    // TERMINOLOGY:
    // A terminated String is a string that ends at the first 0 byte.
    public enum FieldType {
        STRING, // A terminated Strings or a String that ends with the frame 
        // is indicated with a size of -1
        // If a String is the last element in a frame it only is as long as the frame
        // In any case the String ends with with the first null byte
        STRINGS, // A list of terminated Strings. This ends with the end of the frame.
        BINARY, // Binary data
        // Types for "Event timing codes"
        TIMESTAMPFORMAT, // Time Stamp format
        TYPEOFEVENT, // Event type
        CONTENTTYPE, 
        PICTURETYPE,
        NUMBER // Bytes that give a number
    }

    public Field(String name, FieldType type, int size) {
        this.name = name;
        this.type = type;
        this.size = size;
    }

    public Field(String name, FieldType type) {
        this.name = name;
        this.type = type;
        this.size = -1;
    }

    public String getName() {
        return name;
    }

    public FieldType getType() {
        return type;
    }

    public int getSize() {
        return size;
    }

    public String getEncodingOverride() {
        return encodingOverride;
    }

    public void setEncodingOverride(String encodingOverride) {
        this.encodingOverride = encodingOverride;
    }
    
    
    
}
