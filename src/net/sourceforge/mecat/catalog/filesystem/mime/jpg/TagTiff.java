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
 * Created on Aug 5, 2006
 * @author Stephan Richard Palm
 * 
 */
package net.sourceforge.mecat.catalog.filesystem.mime.jpg;

import java.io.DataOutputStream;
import java.io.IOException;

import net.sourceforge.mecat.catalog.filesystem.mime.AbstractTag;
import net.sourceforge.mecat.catalog.filesystem.mime.TagType;
import net.sourceforge.mecat.catalog.filesystem.mime.jpg.ifd.IFD;
import net.sourceforge.mecat.catalog.option.Options;

import static net.sourceforge.mecat.catalog.filesystem.mime.jpg.ifd.IFD_Type.*;

public class TagTiff extends AbstractTag {

    boolean littleEndian = true;
    IFD ifd = null;
    
    public boolean isLittleEndian() {
        return littleEndian;
    }

    public void setLittleEndian(boolean littleEndian) {
        this.littleEndian = littleEndian;
    }

    public IFD getIfd() {
        return ifd;
    }

    public void setIfd(IFD ifd) {
        this.ifd = ifd;
    }

    public void copyTag(DataOutputStream data) throws IOException {

        if (littleEndian)
            data.writeShort(SegmentMarker.TIFF_HEADER_LITTLE_ENDIAN.getMarkerCode());
        else
            data.writeShort(SegmentMarker.TIFF_HEADER_BIG_ENDIAN.getMarkerCode());

        byte _4[] = new byte[4];
        SHORT.store(_4, 0, 42, littleEndian);
        data.write(_4, 0, 2);

        // Write offset for first tag.
        LONG.store(_4, 0, 8, littleEndian);
        data.write(_4, 0, 2);
        
        ifd.copyTag(data, 8, littleEndian);

    }

    public int getLength() {
        return ifd.getSize();
    }

    public int getStartPosition() {
        return 0;
    }

    public TagType getTagType() {
        return TagType.TIFF;
    }

    public String getHTMLInfo() {
        StringBuffer ret = new StringBuffer();

        ret.append("<h2>" + Options.getI18N(TagExif.class).getString("TIFF information") + "</h2>");

        if (ifd != null) 
            ret.append(ifd.getHTMLInfo(null));

        return ret.toString();
    }
    
    public String toString() {
        StringBuffer ret = new StringBuffer();
        
        ret.append(Options.getI18N(TagExif.class).getString("TIFF information"));
        ret.append(System.getProperty("line.separator"));
        
        if (ifd != null) 
            ret.append(ifd.toString());

        return ret.toString();
    }

}
