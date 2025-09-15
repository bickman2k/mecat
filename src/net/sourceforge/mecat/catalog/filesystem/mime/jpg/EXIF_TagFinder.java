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

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import net.sourceforge.mecat.catalog.filesystem.mime.Tag;
import net.sourceforge.mecat.catalog.filesystem.mime.TagFinder;

/**
 * TODO make support for *.tiff and *.eps
 * Created on Aug 17, 2006
 *
 * @author Stephan Richard Palm
 *
 */
public class EXIF_TagFinder implements TagFinder {

    // Exif 2-2 page 58
    public enum SegmentMarker {
        SIO(0xFFD8),
        APP1(0xFFE1),
        APP2(0xFFE2),
        DQT(0xFFDB),
        DHT(0xFFC4),
        DRI(0xFFDD),
        SOF(0xFFC0),
        SOS(0xFFDA),
        EOI(0xFFD9);

        
        final int markerCode;
        
        SegmentMarker(final int markerCode) {
            this.markerCode = markerCode;
        }
        
        public int getMarkerCode() {
            return markerCode;
        }
        
        public boolean isMarker(int markerCode) {
            return markerCode == this.markerCode;
        }
        
        public SegmentMarker getMarkerFromCode(int markerCode) {
            for (SegmentMarker marker : values())
                if (markerCode == this.markerCode)
                    return marker;
            return null;
        }
        
    }
    
    
    
    public Tag getTag(File file) {
        FileInputStream istream = null;
        DataInputStream data = null;
        try {
            istream = new FileInputStream(file);
            data = new DataInputStream(istream);

            
            // read and check for SIO
            int sio = data.readUnsignedShort();

            data.close();
            istream.close();
            
            if (!SegmentMarker.SIO.isMarker(sio))
                return null;
            
            istream = new FileInputStream(file);
            TagExif exif = readTag(istream);
            istream.close();
            
            return exif;
            
        } catch (IOException e) {
            e.printStackTrace();
            if (data != null)
                try {data.close();} catch (IOException e1) {e1.printStackTrace();}
            if (istream != null)
                try {istream.close();} catch (IOException e1) {e1.printStackTrace();}
        }
        
        
        return null;
    }

    public TagExif readTag(InputStream iStream) throws IOException {
        DataInputStream data = null;
        try {
            TagExif exif = new TagExif();
            data = new DataInputStream(iStream);

            // read and check for SIO
            int marker = data.readUnsignedShort();
            if (!SegmentMarker.SIO.isMarker(marker)) {
                data.close();
                return null;
            }

            // read and check for APP1
            marker = data.readUnsignedShort();
            if (!SegmentMarker.APP1.isMarker(marker)) {
                data.close();
                return null;
            }
            
            // Exif2-2 page 64 - length of the rest of the tag
            int size = data.readUnsignedShort();
            
            byte content[] = new byte[size];
            data.read(content);
            String exifStr = new String(content, 0, 4, "ASCII");

            // Now there has to be a EXIF
            if (!exifStr.toUpperCase().startsWith("EXIF")){
                data.close();
                return null;
            }
            
            exif.setAPP1(content);
            
            marker = data.readUnsignedShort();
            if (!SegmentMarker.APP2.isMarker(marker)) {
                data.close();
                return exif;
            }
            
            // Exif2-2 page 65 - length of the rest of the tag
            size = data.readUnsignedShort();
            
            content = new byte[size];
            data.read(content);
            String fpxrStr = new String(content, 0, 4, "ASCII");
            
            data.close();

            // Now there has to be a FPXR
            if (!exifStr.toUpperCase().startsWith("FPXR"))
                return exif;
            
            exif.setAPP2(content);
            
            return exif;
            
        } catch (IOException e) {
            e.printStackTrace();
            if (data != null)
                try {data.close();} catch (IOException e1) {e1.printStackTrace();}
        }
        return null;
    }

}
