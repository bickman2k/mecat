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
 * Created on Aug 14, 2006
 * @author Stephan Richard Palm
 * 
 */
package net.sourceforge.mecat.catalog.filesystem.mime.jpg.ifd;

import net.sourceforge.mecat.catalog.datamanagement.Catalog;
import net.sourceforge.mecat.catalog.i18n.fromCatalog.CatalogResource;
import net.sourceforge.mecat.catalog.option.Options;


public class IFD_Value<T, E extends IFD_Enum> {

    final E tagType;
    final int tagTypeID; // Stored for those, that are not defined by Exif 2.2
    final IFD_Type ifdType;
    final long count;
    final long valOffset;
    // This value is required for the makerNote
    // because the makerNote is build real-time for showing
    final boolean littleEndian;
    
    T value;
    
    public IFD_Value(final E tagType, final int tagTypeID, final IFD_Type ifdType, final long count, final long valOffset, final boolean littleEndian) {
        this.tagType = tagType;
        this.tagTypeID = tagTypeID;
        this.ifdType = ifdType;
        this.count = count;
        this.valOffset = valOffset;
        this.littleEndian = littleEndian;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public long getCount() {
        return count;
    }

    public IFD_Type getIfdType() {
        return ifdType;
    }

    public E getTagType() {
        return tagType;
    }

    public int getTagTypeID() {
        return tagTypeID;
    }

    public long getValOffset() {
        return valOffset;
    }
    
    public boolean getLittleEndian() {
        return littleEndian;
    }


    public String toString() {
        StringBuffer ret = new StringBuffer();
        if (tagType == null)
            ret.append(tagTypeID);
        else
            ret.append(tagType);
        ret.append(" " + ifdType + " " + count + " " + valOffset);

        String value = valueToString();
        
        if (value == null)
            return ret.toString();

        ret.append(" \"" + value + "\"");
        return ret.toString();
    }
    
    public String valueToString() {
        CatalogResource i18n = null;
        if (tagType != null) {
            Catalog catalog = Options.getCatalog(tagType.getClass());
            if (catalog != null) {
                i18n = new CatalogResource(catalog, tagType.toString());
                if (!i18n.getKeys().hasMoreElements())
                    i18n = null;
            }
        }
        
        String value = valueToStringNoTranslate();
        
        if (value == null)
            return null;

        if (i18n == null) 
            return value;
        
        return i18n.getString(valueToStringNoTranslate());
    }
    
    private String valueToStringNoTranslate() 
    {
        if (value == null)
            return "";
        
        if (!value.getClass().isArray())
            return value.toString();
        
        StringBuffer ret = new StringBuffer();
        
        int len = ((Object[])value).length;
        if (len > 20)
            len = 20;
        
        for (int i = 0; i < len; i++) {
            Object o = ((Object[])value)[i];
            ret.append(o.toString());
            if (i < len - 1)
                ret.append(", ");
        }
        if (((Object[])value).length > len)
            ret.append(", ...");

        return ret.toString();
    }
    
}
