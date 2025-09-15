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

import net.sourceforge.mecat.catalog.filesystem.mime.jpg.ifd.makerNote.*;

public enum IFD_Liste {
    EXIF(IFD_ExifAttributeInformation.tagIdentifier, IFD_ExifSpecific.Exif),
    GPS(IFD_GPSAttributeInformation.tagIdentifier, IFD_ExifSpecific.GPS),
    INTEROPERABILITY(IFD_InteroperabilityAttributeInformation.tagIdentifier, IFD_ExifSpecific.Interoperability),
    TIFF(IFD_TiffAttributeInformation.tagIdentifier),
    CANON(Canon.tagIdentifier),
    CASIO(Casio.tagIdentifier),
    FUJIFILM(Fujifilm.tagIdentifier),
    NIKON(Nikon.tagIdentifier),
    NIKONALTERNATIVE(NikonAlternative.tagIdentifier),
    OLYMPUS(Olympus.tagIdentifier);

    final TagIdentifier tIdentifier;
    final IFD_ExifSpecific exifSpecific;

    IFD_Liste(TagIdentifier tIdentifier) {
        this(tIdentifier, null);
    }
    IFD_Liste(TagIdentifier tIdentifier, IFD_ExifSpecific exifSpecific) {
        this.tIdentifier = tIdentifier;
        this.exifSpecific = exifSpecific;
    }

    public TagIdentifier getTagIdentifier() {
        return tIdentifier;
    }
    
    public boolean isExifSpecific() {
        return exifSpecific != null;
    }

    public IFD_ExifSpecific getExifSpecific() {
        return exifSpecific;
    }

}
