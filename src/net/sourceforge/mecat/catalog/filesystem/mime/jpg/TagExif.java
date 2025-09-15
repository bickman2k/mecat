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
 * Created on Aug 4, 2006
 * @author Stephan Richard Palm
 * 
 */
package net.sourceforge.mecat.catalog.filesystem.mime.jpg;

import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.sourceforge.mecat.catalog.filesystem.mime.AbstractTag;
import net.sourceforge.mecat.catalog.filesystem.mime.Tag;
import net.sourceforge.mecat.catalog.filesystem.mime.TagType;
import net.sourceforge.mecat.catalog.filesystem.mime.jpg.ifd.IFD;
import net.sourceforge.mecat.catalog.option.Options;

public class TagExif extends AbstractTag {

    public void copyTag(DataOutputStream ostream) throws IOException {
        if (app1Buffer == null)
            return;

        DataOutputStream data = new DataOutputStream(ostream);
        ostream.write(new byte[]{/*HEADER JPG*/(byte)0xFF, (byte)0xD8, /*HEADER APP1*/(byte)0xFF, (byte)0xE1});
        data.writeShort(app1Buffer.length);
        data.write(app1Buffer);
        
        if (app2Buffer == null)
            return;
        
        ostream.write(new byte[]{(byte)0xFF, (byte)0xE2});
        data.writeShort(app2Buffer.length);
        data.write(app2Buffer);
        
        data.close();
    }

    public int getLength() {
        if (app1Buffer == null)
            return -1;
        if (app2Buffer == null)
            return 2 + 4 + app1Buffer.length; // +2 for JPEG header +4 for rest of APP1 header 
        return 2 + 4 + app1Buffer.length + 4 + app2Buffer.length; // +2 for JPEG header +4 for rest of APP1 header +4 for rest of APP2 header 
    }

    public int getStartPosition() {
        return 0;
    }

    public TagType getTagType() {
        return TagType.EXIF;
    }

    public String getHTMLInfo() {
        StringBuffer ret = new StringBuffer();

        ret.append("<h2>" + Options.getI18N(TagExif.class).getString("EXIF information") + "</h2>");

        if (tagTiff.getIfd() != null) 
            ret.append(tagTiff.getIfd().getHTMLInfo(null));

        return ret.toString();
    }

    byte app1Buffer[] = null;
    byte app2Buffer[] = null;
    
    TagTiff tagTiff = null;
    
    public void setAPP1(byte[] content)  {

        app1Buffer = content;
        
        ByteArrayInputStream iStream = new ByteArrayInputStream(content);
        
        // Skip APP1 header, because it is not usefull anymore
        iStream.skip(6);
        
        try {
            tagTiff = (TagTiff) TagType.TIFF.getTagFinder().readTag(iStream);
        } catch (IOException e) {
            // Should not happen since it is a byte array
            e.printStackTrace();
        }
        
    }

    public void setAPP2(byte[] content) {
        
        app2Buffer = content;
        
    }

    public String toString() {
        StringBuffer ret = new StringBuffer();
        
        ret.append(Options.getI18N(TagExif.class).getString("EXIF information"));
        ret.append(System.getProperty("line.separator"));
        
        if (tagTiff.getIfd() != null) 
            ret.append(tagTiff.getIfd().toString());

        return ret.toString();
    }

}
