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

import java.io.DataOutputStream;
import java.io.IOException;

import net.sourceforge.mecat.catalog.filesystem.Detail;
import net.sourceforge.mecat.catalog.filesystem.DetailWriter;

public class TagWriter implements DetailWriter {

    public int writeDetail(Detail detail, DataOutputStream data) throws IOException {
        if (!(detail instanceof Tag))
            return -1;
        
        Tag tag = (Tag) detail;

        int size = getLength(tag);
        
        // Write size, including everything.
        data.writeInt(size);
        
        // Write type is a tag
        data.writeInt(tag.getType().staticOrdinal());
        
        // Write the version of the tag
        // in version 0 all tags are the same
        data.writeInt(0);
        
        // Write tag type
        data.writeInt(tag.getTagType().staticOrdinal());
        
        // Position where the tag belongs
        data.writeInt(tag.getStartPosition());
        
        // Write tag information
        tag.copyTag(data);
        
        return size;
    }

    public int getLength(Detail detail) {
        if (!(detail instanceof Tag))
            return -1;
        
        Tag tag = (Tag) detail;
        
        return getLength(tag);
    }

    public int getLength(Tag tag) {
        return tag.getLength() + 4 /* Size */ + 4 /* Type */ + 4 /* Version */ + 4 /* Tag type */ + 4 /* Position */;
    }

    
}
