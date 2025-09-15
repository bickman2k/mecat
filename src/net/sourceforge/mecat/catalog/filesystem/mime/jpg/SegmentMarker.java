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
 * Created on Aug 15, 2006
 * @author Stephan Richard Palm
 * 
 */
package net.sourceforge.mecat.catalog.filesystem.mime.jpg;

public enum SegmentMarker {
    TIFF_HEADER_LITTLE_ENDIAN(0x4949),
    TIFF_HEADER_BIG_ENDIAN(0x4D4D);

    
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
    
    public static SegmentMarker getMarkerFromCode(int markerCode) {
        for (SegmentMarker marker : values())
            if (markerCode == marker.markerCode)
                return marker;
        return null;
    }
    
}
