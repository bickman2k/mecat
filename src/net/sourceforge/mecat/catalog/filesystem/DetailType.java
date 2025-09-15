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
 * Created on Jul 29, 2006
 * @author Stephan Richard Palm
 * 
 */
package net.sourceforge.mecat.catalog.filesystem;

import java.util.LinkedHashMap;
import java.util.Map;

import net.sourceforge.mecat.catalog.filesystem.mime.TagReader;
import net.sourceforge.mecat.catalog.filesystem.mime.TagWriter;

public enum DetailType {
    Tag(0, new TagReader(), new TagWriter()), // Contains all information about a tag including the content of course
    ListOfDetails(1, new ListReader(), new ListWriter()); // List of details, just recursive list of details.
    
    final int ordinal;
    final DetailReader detailReader;
    final DetailWriter detailWriter;
    
    static Map<Integer, DetailType> mapping;
    
    DetailType(int ordinal, DetailReader detailReader, DetailWriter detailWriter) {
        this.ordinal = ordinal;
        this.detailReader = detailReader;
        this.detailWriter = detailWriter;
    }
    
    public int staticOrdinal() {
        return ordinal;
    }
    
    public DetailReader getDetailReader() {
        return detailReader;
    }
    
    public DetailWriter getDetailWriter() {
        return detailWriter;
    }

    public static DetailType fromStaticOrdinal(int subTypeOrdinal) {
        if (mapping == null) {
            mapping = new LinkedHashMap<Integer, DetailType>();
            for (DetailType type : values())
                mapping.put(type.staticOrdinal(), type);
        }

        return mapping.get(subTypeOrdinal);
    }
    
}
