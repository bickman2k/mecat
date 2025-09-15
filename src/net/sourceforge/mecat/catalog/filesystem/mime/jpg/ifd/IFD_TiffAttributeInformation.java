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
 * Created on Aug 12, 2006
 * @author Stephan Richard Palm
 * 
 */
package net.sourceforge.mecat.catalog.filesystem.mime.jpg.ifd;

import java.util.Vector;

import net.sourceforge.mecat.catalog.filesystem.mime.jpg.Rational;

public enum IFD_TiffAttributeInformation implements IFD_Enum {
    // Tiff IFDs  -- as found in: Exif2-2  page 16 and following
    
    
    // A. Tags relating to image data structure
    ImageWidth                   (0x100, IFD_Type.LONG,     1),
    ImageLength                  (0x101, IFD_Type.LONG,     1),
    BitsPerSample                (0x102, IFD_Type.SHORT,    3, new Vector<Integer>(){{add(8);add(8);add(8);}}),
    Compression                  (0x103, IFD_Type.SHORT,    1),
    PhotometricInterpretation    (0x106, IFD_Type.LONG,     1),
    Orientation                  (0x112, IFD_Type.SHORT,    1, 1),
    SamplesPerPixel              (0x115, IFD_Type.SHORT,    1, 3),
    PlanarConfiguration          (0x11C, IFD_Type.SHORT,    1),
    YCbCrSubSampling             (0x212, IFD_Type.SHORT,    2),
    YCbCrPositioning             (0x213, IFD_Type.SHORT,    1, 1),
    XResolution                  (0x11A, IFD_Type.RATIONAL, 1, new Rational<Long>((long)72,(long)1)),
    YResolution                  (0x11B, IFD_Type.RATIONAL, 1, new Rational<Long>((long)72,(long)1)),
    ResolutionUnit               (0x128, IFD_Type.SHORT,    1, 2),

    
    // B. Tags relating to recording offset
//  Count depends on PlanarConfiguration value see Exif2-2 or TIFF6
//  And has undefined length number
    StripOffsets                 (0x111, IFD_Type.LONG,     -1),
    RowsPerStrip                 (0x116, IFD_Type.LONG,     1),
//  Count depends on PlanarConfiguration value see Exif2-2 or TIFF6
//  And has undefined length number
    StripByteCounts              (0x8117, IFD_Type.LONG,    -1),
    JPEGInterchangeFormat        (0x201, IFD_Type.LONG,     1),
    JPEGInterchangeFormatLength  (0x202, IFD_Type.LONG,     1),

    
    // C. Tags Relating to Image Data Characteristics
    TransferFunction             (0x12D, IFD_Type.SHORT,    3*256),
    WhitePoint                   (0x13E, IFD_Type.RATIONAL, 2),
    PrimaryChromaticities        (0x13F, IFD_Type.RATIONAL, 6),
//Default value not set because i did not want to search for it        
    YCbCrCoefficients            (0x211, IFD_Type.RATIONAL, 3),
//Default not set because it depends on the value of PhotometricInterpretation
    ReferenceBlackWhite          (0x214, IFD_Type.RATIONAL, 6),

    
    // D. Other Tags
    DataTime                     (0x132, IFD_Type.ASCII,    20),
    ImageDescription             (0x10E, IFD_Type.ASCII,    -1), // -1 = Any
    Make                         (0x10F, IFD_Type.ASCII,    -1),
    Model                        (0x110, IFD_Type.ASCII,    -1),
    Software                     (0x131, IFD_Type.ASCII,    -1),
    Artist                       (0x13B, IFD_Type.ASCII,    -1),
    Copyright                    (0x8298,IFD_Type.ASCII,    -1);

    final int markerCode;
    final IFD_Type type;
    final int count;
    final Object defaultValue;

    IFD_TiffAttributeInformation(final int markerCode, final IFD_Type type, final int count) {
        this(markerCode, type, count, null);
    }

    IFD_TiffAttributeInformation(final int markerCode, final IFD_Type type, final int count, final Object defaultValue) {
        this.markerCode = markerCode;
        this.type = type;
        this.count = count;
        this.defaultValue = defaultValue;
    }

    public int getCount() {
        return count;
    }



    public Object getDefaultValue() {
        return defaultValue;
    }



    public IFD_Type getType() {
        return type;
    }



    public int getMarkerCode() {
        return markerCode;
    }

    public boolean isMarker(int markerCode) {
        return markerCode == this.markerCode;
    }

    public static IFD_TiffAttributeInformation getMarkerFromCode(int markerCode) {
        for (IFD_TiffAttributeInformation marker : values())
            if (markerCode == marker.markerCode)
                return marker;
        return null;
    }

    public final static TagIdentifier tagIdentifier = new TagIdentifier(){
        public IFD_Enum getMarkerFromCode(int markerCode) {
            return IFD_TiffAttributeInformation.getMarkerFromCode(markerCode);
        }
    };
}
