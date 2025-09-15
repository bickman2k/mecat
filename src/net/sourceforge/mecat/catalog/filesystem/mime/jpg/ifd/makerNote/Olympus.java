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

public enum Olympus implements IFD_Enum {
    //////////////////////
    // http://park2.wakwak.com/~tsuruzoh/Computer/Digicams/exif-e.html
    SpecialMode              (0x0200, IFD_Type.LONG,      3),
    JpegQual                 (0x0201, IFD_Type.SHORT,  1),
    Macro                    (0x0202, IFD_Type.SHORT,      1),
//  Unknown                  (0x0203, IFD_Type.SHORT,       1),
    DigiZoom                 (0x0204, IFD_Type.RATIONAL,  1),
//  Unknown                  (0x0205, IFD_Type.RATIONAL,       1),
//  Unknown                  (0x0206, IFD_Type.SSHORT,       6),
    SoftwareRelease          (0x0207, IFD_Type.ASCII,  5),
    PictInfo                 (0x0208, IFD_Type.ASCII,      52),
    CameraID                 (0x0209, IFD_Type.UNDEFINED,       32),
    DataDump                 (0x0f00, IFD_Type.LONG,       30);

    final int markerCode;
    final IFD_Type type;
    final int count;
    final Object defaultValue;

    Olympus(final int markerCode, final IFD_Type type, final int count) {
        this(markerCode, type, count, null);
    }

    Olympus(final int markerCode, final IFD_Type type, final int count, final Object defaultValue) {
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

    public static Olympus getMarkerFromCode(int markerCode) {
        for (Olympus marker : values())
            if (markerCode == marker.markerCode)
                return marker;
        return null;
    }

    public final static TagIdentifier tagIdentifier = new TagIdentifier(){
        public IFD_Enum getMarkerFromCode(int markerCode) {
            return Olympus.getMarkerFromCode(markerCode);
        }
    };
}
