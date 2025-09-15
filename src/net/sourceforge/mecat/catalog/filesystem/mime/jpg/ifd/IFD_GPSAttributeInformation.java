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

public enum IFD_GPSAttributeInformation implements IFD_Enum {
    //////////////////////
    // GPS Attribute Information
    // A, Tag Relating to DPS
    GPSVersionID                 (0x00, IFD_Type.BYTE,       4),
    GPSLatitudeRef               (0x01, IFD_Type.ASCII,      2),
    GPSLatitude                  (0x02, IFD_Type.RATIONAL,   3),
    GPSLongitudeRef              (0x03, IFD_Type.ASCII,      2),
    GPSLongitude                 (0x04, IFD_Type.RATIONAL,   3),
    GPSAltitudeRef               (0x05, IFD_Type.BYTE,       1),
    GPSAltitude                  (0x06, IFD_Type.RATIONAL,   1),
    GPSTimeStamp                 (0x07, IFD_Type.RATIONAL,   3),
    GPSSatallites                (0x08, IFD_Type.ASCII,      -1),
    GPSStatus                    (0x09, IFD_Type.ASCII,      2),
    GPSMeasureMode               (0x0A, IFD_Type.ASCII,      2),
    GPSDOP                       (0x0B, IFD_Type.RATIONAL,   1),
    GPSSpeedRef                  (0x0C, IFD_Type.ASCII,      2),
    GPSSpeed                     (0x0D, IFD_Type.RATIONAL,   1),
    GPSTrackRef                  (0x0E, IFD_Type.ASCII,      2),
    GPSTrack                     (0x0F, IFD_Type.RATIONAL,   1),
    GPSImgDirectionRef           (0x10, IFD_Type.ASCII,      2),
    GPSImgDirection              (0x11, IFD_Type.RATIONAL,   1),
    GPSMapDatum                  (0x12, IFD_Type.ASCII,      -1),
    GPSDestLatitudeRef           (0x13, IFD_Type.ASCII,      2),
    GPSDestLatitude              (0x14, IFD_Type.RATIONAL,   3),
    GPSDestLongitudeRef          (0x15, IFD_Type.ASCII,      2),
    GPSDestLongitude             (0x16, IFD_Type.RATIONAL,   3),
    GPSDestBearingRef            (0x17, IFD_Type.ASCII,      2),
    GPSDestBearing               (0x18, IFD_Type.RATIONAL,   1),
    GPSDestDistanceRef           (0x19, IFD_Type.ASCII,      2),
    GPSDestDistance              (0x1A, IFD_Type.UNDEFINED,  1),
    GPSProcessingMethode         (0x1B, IFD_Type.UNDEFINED,  -1),
    GPSAreaInformation           (0x1C, IFD_Type.RATIONAL,   -1),
    GPSDataStamp                 (0x1D, IFD_Type.ASCII,      11),
    GPSDifferential              (0x1E, IFD_Type.SHORT,      1);

    final int markerCode;
    final IFD_Type type;
    final int count;
    final Object defaultValue;

    IFD_GPSAttributeInformation(final int markerCode, final IFD_Type type, final int count) {
        this(markerCode, type, count, null);
    }

    IFD_GPSAttributeInformation(final int markerCode, final IFD_Type type, final int count, final Object defaultValue) {
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

    public static IFD_GPSAttributeInformation getMarkerFromCode(int markerCode) {
        for (IFD_GPSAttributeInformation marker : values())
            if (markerCode == marker.markerCode)
                return marker;
        return null;
    }

    final static TagIdentifier tagIdentifier = new TagIdentifier(){
        public IFD_Enum getMarkerFromCode(int markerCode) {
            return IFD_GPSAttributeInformation.getMarkerFromCode(markerCode);
        }
    };
}
