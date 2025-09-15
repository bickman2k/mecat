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

public enum IFD_ExifSpecific implements IFD_Enum {
    // Exif-specific IFD  -- as found in: Exif2-2  page 15
    Exif(0x8769, IFD_Type.LONG, IFD_Liste.EXIF, IFD_ExifAttributeInformation.tagIdentifier, 1),
    GPS(0x8825, IFD_Type.LONG, IFD_Liste.GPS, IFD_GPSAttributeInformation.tagIdentifier, 1),
    Interoperability(0xA005, IFD_Type.LONG, IFD_Liste.INTEROPERABILITY, IFD_InteroperabilityAttributeInformation.tagIdentifier, 1);

    final TagIdentifier tIdentifier;
    final int markerCode;
    final IFD_Type type;
    final int count;
    final Object defaultValue;
    final IFD_Liste ifdListe;

    IFD_ExifSpecific(final int markerCode, final IFD_Type type, IFD_Liste ifdListe, TagIdentifier tIdentifier, final int count) {
        this(markerCode, type, ifdListe, tIdentifier, count, null);
    }

    IFD_ExifSpecific(final int markerCode, final IFD_Type type, IFD_Liste ifdListe, TagIdentifier tIdentifier, final int count, final Object defaultValue) {
        this.markerCode = markerCode;
        this.type = type;
        this.count = count;
        this.defaultValue = defaultValue;
        this.tIdentifier = tIdentifier;
        this.ifdListe = ifdListe;
    }

    public IFD_Liste convertToIFD_Liste() {
        return ifdListe;
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

    public static IFD_ExifSpecific getMarkerFromCode(int markerCode) {
        for (IFD_ExifSpecific marker : values())
            if (markerCode == marker.markerCode)
                return marker;
        return null;
    }
    
    final static TagIdentifier tagIdentifier = new TagIdentifier(){
        public IFD_Enum getMarkerFromCode(int markerCode) {
            return IFD_ExifSpecific.getMarkerFromCode(markerCode);
        }
    };

    public TagIdentifier getTagIdentifier() {
        return tIdentifier;
    }

}
