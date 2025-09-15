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
package net.sourceforge.mecat.catalog.filesystem.mime.jpg.ifd.makerNote;

import net.sourceforge.mecat.catalog.filesystem.mime.jpg.ifd.IFD_Enum;
import net.sourceforge.mecat.catalog.filesystem.mime.jpg.ifd.IFD_Type;
import net.sourceforge.mecat.catalog.filesystem.mime.jpg.ifd.TagIdentifier;

/**
 * For Nikon of type CS-E990 and Nikon D1
 * Created on Aug 19, 2006
 *
 *http://park2.wakwak.com/~tsuruzoh/Computer/Digicams/exif-e.html
 *
 * @author Stephan Richard Palm
 *
 */
public enum NikonAlternative implements IFD_Enum {
    //////////////////////
    // http://park2.wakwak.com/~tsuruzoh/Computer/Digicams/exif-e.html
//  Unknown                  (0x0001, IFD_Type.UNDEFINED,  4),
    ISOSetting               (0x0002, IFD_Type.SHORT,      2),
    ColorMode                (0x0003, IFD_Type.ASCII,      -1),
    Quality                  (0x0004, IFD_Type.ASCII,      -1),
    WhiteBalance             (0x0005, IFD_Type.ASCII,      -1),
    ImageSharpening          (0x0006, IFD_Type.ASCII,      -1),
    FocusModence             (0x0007, IFD_Type.ASCII,      -1),
    FlashSetting             (0x0008, IFD_Type.ASCII,      -1),
//  Unknown                  (0x000a, IFD_Type.RATIONAL,   1),
    ISOSelection             (0x000f, IFD_Type.ASCII,      -1),
    ImageAdjustment          (0x0080, IFD_Type.ASCII,      -1),
    Adapter                  (0x0082, IFD_Type.ASCII,      -1),
    ManualFocusDistance      (0x0085, IFD_Type.RATIONAL,   1),
    DigitalZoom              (0x0086, IFD_Type.RATIONAL,   1),
    AFFocusPosition          (0x0088, IFD_Type.UNDEFINED,  4),
    DataDump                 (0x0010, IFD_Type.UNDEFINED,  174);

    final int markerCode;
    final IFD_Type type;
    final int count;
    final Object defaultValue;

    NikonAlternative(final int markerCode, final IFD_Type type, final int count) {
        this(markerCode, type, count, null);
    }

    NikonAlternative(final int markerCode, final IFD_Type type, final int count, final Object defaultValue) {
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

    public static NikonAlternative getMarkerFromCode(int markerCode) {
        for (NikonAlternative marker : values())
            if (markerCode == marker.markerCode)
                return marker;
        return null;
    }

    public final static TagIdentifier tagIdentifier = new TagIdentifier(){
        public IFD_Enum getMarkerFromCode(int markerCode) {
            return NikonAlternative.getMarkerFromCode(markerCode);
        }
    };
}
