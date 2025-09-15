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

public enum IFD_ExifAttributeInformation implements IFD_Enum {
    //////////////////////////////
    // Exif IFD Attribute Information
    // A, Tags Relating to Version
    ExifVersion                  (0x9000, IFD_Type.UNDEFINED/*ASCII*/,   4, "0220"),
    FlashpixVersion              (0xA000, IFD_Type.UNDEFINED/*ASCII*/,   4, "0100"),

    // B. Tags Relating to Image Data Characteristics
    ColorSpace                   (0xA001, IFD_Type.SHORT,     1),

    // C. Tags Relating to Image Configuration
//TODO write mapping see page 27 from Exif2-2
    ComponentsConfiguration      (0x9101, IFD_Type.UNDEFINED, 4),
    CompressedBitsPerPixel       (0x9102, IFD_Type.RATIONAL,  1),
    PixelXDimension              (0xA002, IFD_Type.LONG,      1),
    PixelYDimension              (0xA003, IFD_Type.LONG,      1),

    // D. Tags Relating to User Information
    MakerNote                    (0x927C, IFD_Type.UNDEFINED, -1),
    UserComment                  (0x9286, IFD_Type.UNDEFINED, -1),

    // E. Tag Relating to Related File Information
    RelatedSoundFile             (0xA004, IFD_Type.ASCII,     13),

    // F. Tags Relating to Date and Time
    DateTimeOriginal             (0x9003, IFD_Type.ASCII,    20),
    DateTimeDigitized            (0x9004, IFD_Type.ASCII,    20),
    SubSecTime                   (0x9290, IFD_Type.ASCII,    -1),
    SubSecTimeOriginal           (0x9291, IFD_Type.ASCII,    -1),
    SubSecTimeDigitzed           (0x9292, IFD_Type.ASCII,    -1),

    // G. Tags Relating to Picture Condittions
    ExposureTime                 (0x829A, IFD_Type.RATIONAL,  1),
    FNumber                      (0x829D, IFD_Type.RATIONAL,  1),
    ExposureProgram              (0x8822, IFD_Type.SHORT,     1),
    SpectralSensitivity          (0x8824, IFD_Type.ASCII,     -1),
    ISOSpeedRatings              (0x8827, IFD_Type.SHORT,     -1),
    OECF                         (0x8828, IFD_Type.UNDEFINED, -1),
    ShutterSpeedValue            (0x9201, IFD_Type.SRATIONAL, 1),
    ApertureValue                (0x9202, IFD_Type.RATIONAL,  1),
    BrightnessValue              (0x9203, IFD_Type.SRATIONAL, 1),
    ExposureBiasValue            (0x9204, IFD_Type.SRATIONAL, 1),
    MaxApertureValue             (0x9205, IFD_Type.RATIONAL,  1),
    SubjectDistance              (0x9206, IFD_Type.RATIONAL,  1),
    MeteringMode                 (0x9207, IFD_Type.SHORT,     1),
    LightSource                  (0x9208, IFD_Type.SHORT,     1),
    Flash                        (0x9209, IFD_Type.SHORT,     1),
    FocalLength                  (0x920A, IFD_Type.RATIONAL,  1),
    // Size 2 or 3 or 4 // Hm how do i do that
    SubjectArea                  (0x9214, IFD_Type.SHORT,    -1),
    FlashEnergy                  (0xA20B, IFD_Type.RATIONAL,   1),
    SpatialFrequencyResponse     (0xA20C, IFD_Type.UNDEFINED,  -1),
    FocalPlaneXResolution        (0xA20E, IFD_Type.RATIONAL,   1),
    FocalPlaneYResolution        (0xA20F, IFD_Type.RATIONAL,   1),
    FocalPlaneResolutionUnit     (0xA210, IFD_Type.SHORT,      1),
    SubjectLocation              (0xA214, IFD_Type.SHORT,      2),
    ExposureIndex                (0xA215, IFD_Type.RATIONAL,   1),
    SensingMethode               (0xA217, IFD_Type.SHORT,      1),
    FileSource                   (0xA300, IFD_Type.UNDEFINED,  1),
    SceneType                    (0xA301, IFD_Type.UNDEFINED,  1),
    CFAPattern                   (0xA302, IFD_Type.UNDEFINED,  -1),
    CustomRendered               (0xA401, IFD_Type.SHORT,      1),
    ExposureMode                 (0xA402, IFD_Type.SHORT,      1),
    WhiteBalance                 (0xA403, IFD_Type.SHORT,      1),
    DigitalZoomRatio             (0xA404, IFD_Type.RATIONAL,   1),
    FocalLengthIn35mmFilm        (0xA405, IFD_Type.SHORT,      1),
    SceneCaptureType             (0xA406, IFD_Type.SHORT,      1),
    GainControl                  (0xA407, IFD_Type.RATIONAL,   1),
    Contrast                     (0xA408, IFD_Type.SHORT,      1),
    Saturation                   (0xA409, IFD_Type.SHORT,      1),
    Sharpness                    (0xA40A, IFD_Type.SHORT,      1),
    DeviceSettingDescription     (0xA40B, IFD_Type.UNDEFINED,  -1),
    SubjectDistanceRange         (0xA40C, IFD_Type.SHORT,      1),

    // H. Other Tags
    ImageUniqzeID                (0xA420, IFD_Type.ASCII,    33);

    final int markerCode;
    final IFD_Type type;
    final int count;
    final Object defaultValue;

    IFD_ExifAttributeInformation(final int markerCode, final IFD_Type type, final int count) {
        this(markerCode, type, count, null);
    }

    IFD_ExifAttributeInformation(final int markerCode, final IFD_Type type, final int count, final Object defaultValue) {
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

    public static IFD_ExifAttributeInformation getMarkerFromCode(int markerCode) {
        for (IFD_ExifAttributeInformation marker : values())
            if (markerCode == marker.markerCode)
                return marker;
        return null;
    }

    
    final static TagIdentifier tagIdentifier = new TagIdentifier(){
        public IFD_Enum getMarkerFromCode(int markerCode) {
            return IFD_ExifAttributeInformation.getMarkerFromCode(markerCode);
        }
    };
}
