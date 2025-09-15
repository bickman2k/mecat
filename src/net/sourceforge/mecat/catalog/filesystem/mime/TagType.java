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
package net.sourceforge.mecat.catalog.filesystem.mime;

import java.util.LinkedHashMap;
import java.util.Map;

import net.sourceforge.mecat.catalog.filesystem.mime.jpg.EXIF_TagFinder;
import net.sourceforge.mecat.catalog.filesystem.mime.jpg.TIFF_TagFinder;
import net.sourceforge.mecat.catalog.filesystem.mime.mp3.ID3V1_TagFinder;
import net.sourceforge.mecat.catalog.filesystem.mime.mp3.ID3V2_TagFinder;

public enum TagType {
    ID3V1(0, new ID3V1_TagFinder()), 
    ID3V2(1, new ID3V2_TagFinder()),
    TIFF(2, new TIFF_TagFinder()),
    EXIF(3, new EXIF_TagFinder());
    
    final int ordinal;
    final TagFinder tagFinder;
    
    static Map<Integer, TagType> mapping;
    
    TagType(int ordinal, TagFinder tagFinder) {
        this.ordinal = ordinal;
        this.tagFinder = tagFinder;
    }
    
    public int staticOrdinal() {
        return ordinal;
    }
    
    public TagFinder getTagFinder() {
        return tagFinder;
    }
    
    public static TagType fromStaticOrdinal(int subTypeOrdinal) {
        if (mapping == null) {
            mapping = new LinkedHashMap<Integer, TagType>();
            for (TagType type : values())
                mapping.put(type.staticOrdinal(), type);
        }

        return mapping.get(subTypeOrdinal);
    }
    
}
