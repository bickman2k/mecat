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

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import net.sourceforge.mecat.catalog.filesystem.mime.Tag;
import net.sourceforge.mecat.catalog.filesystem.mime.TagFinder;
import net.sourceforge.mecat.catalog.filesystem.mime.jpg.ifd.IFD;
import net.sourceforge.mecat.catalog.filesystem.mime.jpg.ifd.IFD_Enum;
import net.sourceforge.mecat.catalog.filesystem.mime.jpg.ifd.IFD_ExifSpecific;
import net.sourceforge.mecat.catalog.filesystem.mime.jpg.ifd.IFD_Liste;
import net.sourceforge.mecat.catalog.filesystem.mime.jpg.ifd.IFD_Type;
import net.sourceforge.mecat.catalog.filesystem.mime.jpg.ifd.IFD_Value;
import net.sourceforge.mecat.catalog.filesystem.mime.jpg.ifd.TagIdentifier;
import net.sourceforge.mecat.catalog.option.Options;

public class TIFF_TagFinder implements TagFinder {

    
    public Tag getTag(File file) {
        FileInputStream istream = null;
        DataInputStream data = null;
        try {
            istream = new FileInputStream(file);
            data = new DataInputStream(istream);

            // read TIFF HEADER
            int header = data.readUnsignedShort();

            data.close();
            istream.close();

            // check TIFF HEADER
            if (!SegmentMarker.TIFF_HEADER_LITTLE_ENDIAN.isMarker(header)
                    && !SegmentMarker.TIFF_HEADER_BIG_ENDIAN.isMarker(header))
                return null;
            
            istream = new FileInputStream(file);
            TagTiff tiff = readTag(istream);
            istream.close();
            
            return tiff;
            
        } catch (IOException e) {
            e.printStackTrace();
            if (data != null)
                try {data.close();} catch (IOException e1) {e1.printStackTrace();}
            if (istream != null)
                try {istream.close();} catch (IOException e1) {e1.printStackTrace();}
        }
        
        
        return null;
    }

    public static IFD readIFD(BufferedInputStream bStream, long markOffset, long offset, IFD_Liste exifType, boolean littleEndian) throws IOException {
        IFD ret = new IFD(exifType);
        TagIdentifier tagIdentifier = ret.getTagIdentifier();
        
        bStream.reset();
        bStream.mark(65536);
        bStream.skip(offset - markOffset);
        
        Map<IFD_Value<IFD, IFD_ExifSpecific>, Long> idfMap = new HashMap<IFD_Value<IFD, IFD_ExifSpecific>, Long>();
        
        int tagCount = (int)(long) ((Long) IFD_Type.SHORT.get(bStream, 1, littleEndian));;

        for (int i = 0; i < tagCount; i++) {
            
            // p.13 of Exif2-2
            // read tag id
            int tagId = (int)(long) ((Long) IFD_Type.SHORT.get(bStream, 1, littleEndian));
            IFD_ExifSpecific exifSpecific =  IFD_ExifSpecific.getMarkerFromCode(tagId);
            IFD_Enum attributeInformation = tagIdentifier.getMarkerFromCode(tagId);
            IFD_Enum tagType = attributeInformation;
            if (tagType == null) 
                tagType =  exifSpecific;
            
            
            // type of the tag
            int type = (int)(long) ((Long) IFD_Type.SHORT.get(bStream, 1, littleEndian));
            IFD_Type ifdType = IFD_Type.getMarkerFromCode(type);
            
            // number of values from the type "type"
            long count = ((Long) IFD_Type.LONG.get(bStream, 1, littleEndian));
            
            // value offset
            long valOffset = ((Long) IFD_Type.LONG.get(bStream, 1, littleEndian));
            
            if (Options.DEBUG && Options.verbosity > 1)
                System.out.println(((tagType == null)?tagId:tagType) + " " + ifdType + " " + count + " " + valOffset);
            if (exifSpecific != null) {
                IFD_Value<IFD, IFD_ExifSpecific> ifdValue = new IFD_Value<IFD, IFD_ExifSpecific>(exifSpecific, tagId, ifdType, count, valOffset, littleEndian);
            
                idfMap.put(ifdValue, valOffset);
                ret.putTag(ifdValue);
            } else 
                ret.putTag(new IFD_Value(tagType, tagId, ifdType, count, valOffset, littleEndian));
            
        }

        long offsetIFD = ((Long) IFD_Type.LONG.get(bStream, 1, littleEndian));

        for (Map.Entry<IFD_Value<IFD, IFD_ExifSpecific>, Long> entry : idfMap.entrySet()) {
            if (Options.DEBUG && Options.verbosity > 1)
                System.out.println("# Get " + entry.getKey() + " at " + entry.getValue());
            entry.getKey().setValue(readIFD(bStream, markOffset, entry.getValue(), entry.getKey().getTagType().convertToIFD_Liste(), littleEndian));
        }
        
        for (IFD_Value value : ret.getTags()) {
            if (value.getTagType() instanceof IFD_ExifSpecific)
                continue;
  
            value.setValue(value.getIfdType().getSecure(bStream, markOffset, (int)value.getCount(), value.getValOffset(), littleEndian));
        }
        
        if (Options.DEBUG && Options.verbosity > 1)
            System.out.println("# Next at " + offsetIFD);
        if (offsetIFD != 0)
            ret.setNext(readIFD(bStream, markOffset, offsetIFD, exifType, littleEndian));
        
        return ret;
    }
    
    public TagTiff readTag(InputStream iStream) throws IOException {
        BufferedInputStream  data = null;
        try {
            TagTiff tiff = new TagTiff();
            data = new BufferedInputStream(iStream);
            data.mark(65536);
//            data = new DataInputStream(iStream);

            // read and check for TIFF HEADER
            int marker = (int)(long) ((Long) IFD_Type.SHORT.get(data, 1, false));
            if (!SegmentMarker.TIFF_HEADER_LITTLE_ENDIAN.isMarker(marker)
                    && !SegmentMarker.TIFF_HEADER_BIG_ENDIAN.isMarker(marker)){
                data.close();
                return null;
            }

            boolean littleEndian = SegmentMarker.TIFF_HEADER_LITTLE_ENDIAN.isMarker(marker);
            tiff.setLittleEndian(littleEndian);

            int fortyTwo = (int)(long) ((Long) IFD_Type.SHORT.get(data, 1, littleEndian));

            // Fixed value 42 seee p.10 from Exif 2-2
            if (fortyTwo != 42) {
                data.close();
                return null;
            }
            
            // Number of tags to read
            long offSet = ((Long) IFD_Type.LONG.get(data, 1, littleEndian));

            IFD ifd = readIFD(data, 0, offSet, null, littleEndian);
            
            data.close();
            
            tiff.setIfd(ifd);
            
            return tiff;
            
        } catch (IOException e) {
            e.printStackTrace();
            if (data != null)
                try {data.close();} catch (IOException e1) {e1.printStackTrace();}
        }
        return null;
    }

}
