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

public enum IFD_InteroperabilityAttributeInformation implements IFD_Enum {
    //////////////////////
    // Interoperability Attribute Information
    // A, Tag Relating to Interoperability
    InteroperabilityIndex              (0x0001, IFD_Type.ASCII,      4),
    // More Tags found on
    // http://park2.wakwak.com/~tsuruzoh/Computer/Digicams/exif-e.html
    InteroperabilityVersion            (0x0002, IFD_Type.UNDEFINED,  4),
    RelatedImageFileFormat             (0x1000, IFD_Type.ASCII,      -1),
    RelatedImageWidth                  (0x1001, IFD_Type.LONG,       1),
    RelatedImageLength                 (0x1002, IFD_Type.LONG,       1);

    final int markerCode;
    final IFD_Type type;
    final int count;
    final Object defaultValue;

    IFD_InteroperabilityAttributeInformation(final int markerCode, final IFD_Type type, final int count) {
        this(markerCode, type, count, null);
    }

    IFD_InteroperabilityAttributeInformation(final int markerCode, final IFD_Type type, final int count, final Object defaultValue) {
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

    public static IFD_InteroperabilityAttributeInformation getMarkerFromCode(int markerCode) {
        for (IFD_InteroperabilityAttributeInformation marker : values())
            if (markerCode == marker.markerCode)
                return marker;
        return null;
    }

    final static TagIdentifier tagIdentifier = new TagIdentifier(){
        public IFD_Enum getMarkerFromCode(int markerCode) {
            return IFD_InteroperabilityAttributeInformation.getMarkerFromCode(markerCode);
        }
    };
}
