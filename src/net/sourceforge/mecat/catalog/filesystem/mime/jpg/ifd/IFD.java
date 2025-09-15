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

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import net.sourceforge.mecat.catalog.filesystem.mime.jpg.ifd.makerNote.MakeNote;

public class IFD {

    /**
     * Type of the IFD.
     * null for Type Tiff Attribute Information.
     */
    final IFD_Liste type;

    Map<IFD_Enum, IFD_Value> tagMap = new HashMap<IFD_Enum, IFD_Value>();
    List<IFD_Value> tags = new Vector<IFD_Value>();
    IFD next = null;
    
    public IFD(final IFD_Liste type) {
        this.type = type;
    }

    public List<IFD_Value> getTags() {
        return tags;
    }

    /**
     * Search the key in this ifd and all ifd related with next relation
     * 
     * @param key
     * @return
     */
    public IFD_Value getTag(IFD_Enum key) {
        IFD next = this;
        while (next != null) {
            if (tagMap.containsKey(key))
                    return tagMap.get(key);
            next = next.next;
        }
        return null;
    }

    public IFD_Liste getType() {
        return type;
    }

    public void putTag(IFD_Value tag) {
        tags.add(tag);
        if (tag.tagType != null)
            tagMap.put(tag.tagType, tag);
    }

    public TagIdentifier getTagIdentifier() {
        if (type != null)
            return type.getTagIdentifier();
        // Should not happen anymore
        else
            return IFD_TiffAttributeInformation.tagIdentifier;
    }

    public IFD getNext() {
        return next;
    }

    public void setNext(IFD next) {
        this.next = next;
    }
    
    public String toString() {
        StringBuffer ret = new StringBuffer();
        
        if (type != null) {
            ret.append(System.getProperty("line.separator"));
            ret.append(" >> " + type + " << ");
        }
        
/*        for (IFD_Value value : tags) {
            ret.append(System.getProperty("line.separator"));
            ret.append(value);
        }*/
        IFD next = this;
        while (next != null) {
            for (IFD_Value value : next.tags) {
                ret.append(System.getProperty("line.separator"));
                ret.append(value);
            }
            next = next.next;
            
        }
        
        return ret.toString();
    }

    public String getHTMLInfo(IFD_Value make) {
        StringBuffer ret = new StringBuffer();

//        if (type != null)
//            ret.append("<h3>" + type + "</h3>");
        ret.append("<table border=\"1\">");

        IFD next = this;
        if (type == null /* meaning IFD_TiffAttributeInformation*/) {
            make = this.getTag(IFD_TiffAttributeInformation.Make);
        }
        
/*        for (IFD_Value value : tags) {
            ret.append("<tr><th>");
            if (value.getTagType() != null)
                ret.append(value.getTagType());
            else
                ret.append(value.getTagTypeID());
            ret.append("</th><th>");
            if (value.getValue() instanceof IFD)
                ret.append(((IFD)value.getValue()).getHTMLInfo());
            else
                ret.append(value.valueToString());
            ret.append("</th></tr>");
        }*/
        while (next != null) {
            for (IFD_Value value : next.tags) {
                ret.append("<tr><th>");
                if (value.getTagType() != null)
                    ret.append(value.getTagType());
                else
                    ret.append(value.getTagTypeID());
                ret.append("</th><th>");
                if (value.getValue() instanceof IFD)
                    ret.append(((IFD)value.getValue()).getHTMLInfo(make));
                else if (value.getTagType() == IFD_ExifAttributeInformation.MakerNote)
                    ret.append(MakeNote.show(value, make));
                else
                    ret.append(value.valueToString());
                ret.append("</th></tr>");
            }
            next = next.next;
        }

        ret.append("</table>");

        return ret.toString();
    }

    public long copyTag(DataOutputStream data, long initialOffset, boolean littleEndian) throws IOException {
        byte b4[] = new byte[4];

        IFD_Type.SHORT.store(b4, 0, tags.size(), littleEndian);
        data.write(b4, 0, 2);
        
        // At this point we define the starting point for data from this IFD
        // this will also contain starting points for further IFDs
        long dataOffset = initialOffset + 2 /* Count */ + tags.size() * 12 /* TagDefinition */ + 4 /* Offset for next */;
        long writeDataOffset = dataOffset;
        
        for (IFD_Value tag : tags) {
            long mem = tag.ifdType.size * tag.count;

            IFD_Type.SHORT.store(b4, 0, tag.getTagTypeID(), littleEndian);
            data.write(b4, 0, 2);
            IFD_Type.SHORT.store(b4, 0, tag.getIfdType().getMarkerCode(), littleEndian);
            data.write(b4, 0, 2);
            IFD_Type.LONG.store(b4, 0, tag.getCount(), littleEndian);
            data.write(b4, 0, 4);
            
            // If it uses less or equal then 4 bytes
            // write it in the place of the offset (Exif 2.2)
            if (tag.getTagType() instanceof IFD_ExifSpecific) {
                IFD_Type.LONG.store(b4, 0, dataOffset, littleEndian);
                dataOffset += ((IFD)tag.getValue()).getSize();
            } else if (mem > 4){
                IFD_Type.LONG.store(b4, 0, dataOffset, littleEndian);
                dataOffset += mem;
            } else
                tag.getIfdType().storeArray(b4, 0, tag.getValue(), littleEndian);
        }
        
        if (next == null)
            IFD_Type.LONG.store(b4, 0, 0, littleEndian);
        else
            IFD_Type.LONG.store(b4, 0, dataOffset, littleEndian);
        data.write(b4, 0, 2);
        
        for (IFD_Value tag : tags) {
            long mem = tag.ifdType.size * tag.count;

            if (tag.getTagType() instanceof IFD_ExifSpecific) {
                writeDataOffset += ((IFD)tag.getValue()).copyTag(data, writeDataOffset, littleEndian);
            } else if (mem > 4){
                tag.getIfdType().storeArray(b4, 0, tag.getValue(), littleEndian, (int)tag.getCount());
                writeDataOffset += mem;
            } else {
                // Do nothing, allready stored the information in the offset value                
            }
        }
        
        writeDataOffset += next.copyTag(data, writeDataOffset, littleEndian);

        // All together including the next
        return writeDataOffset - initialOffset;
    }

    public int getSize() {
        long size = 2 /* Count */ + tags.size() * 12 /* TagDefinition */ + 4 /* Offset for next */;
        
        for (IFD_Value tag : tags) {
            long mem = tag.ifdType.size * tag.count;

            // If it uses less or equal then 4 bytes
            // write it in the place of the offset (Exif 2.2)
            if (tag.getTagType() instanceof IFD_ExifSpecific) {
                size += ((IFD)tag.getValue()).getSize();
            } else if (mem > 4){
                size += mem;
            } else
                ; // do nothing
        }
        
        if (next != null)
            size = next.getSize();

        // All together including the next
        return (int) size;
    }

    
}
