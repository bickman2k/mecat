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

import java.io.DataInputStream;
import java.io.IOException;

import net.sourceforge.mecat.catalog.filesystem.CountingStream;
import net.sourceforge.mecat.catalog.filesystem.DetailReader;

public class TagReader implements DetailReader {

    public Tag getDetail(DataInputStream data, int size) throws IOException {

        // The version of tagging detail
        int version = data.readInt();
        
        // The type of the tag
        int tagTypeOrdinal = data.readInt();
        TagType type = TagType.fromStaticOrdinal(tagTypeOrdinal);
        if (type == null) {
            data.skip(size - 16); //* Size */ + 4 /* Type */ + 4 /* Version */ + 4 /* Tag type */
            return null;
        }
        
        // The position of the tag
        int position = data.readInt();

        // Read the tag
        CountingStream countingStream = new CountingStream(data, size - 20); //* Size */ + 4 /* Type */ + 4 /* Version */ + 4 /* Tag type */ + 4 /* Position */
        Tag ret = type.getTagFinder().readTag(countingStream);
        countingStream.skipRest();
        
        return ret;
    }

}
