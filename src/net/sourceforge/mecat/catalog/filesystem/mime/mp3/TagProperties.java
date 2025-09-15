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
 * Created on Jul 20, 2006
 * @author Stephan Richard Palm
 * 
 */
package net.sourceforge.mecat.catalog.filesystem.mime.mp3;

public class TagProperties {

    final public static TagProperties version[] = {
        null, // There is no version 2.0 
        null, // or 2.1
        new TagProperties(4,-1, -1, 3,3, 6), // Tag version 2.2
        new TagProperties(4, 4,  4, 4,4, 10), // Tag version 2.3 
        new TagProperties(4, 4,  4, 4,4, 10)  // Tag version 2.4 
    };
    
    final int tagSizeSize;
    final int extendedHeaderSizeSize;
    final int paddingSizeSize;
    
    final int frameIdentifierSize;
    final int frameSizeSize;
    final int frameHeaderSize;
    
    public TagProperties(final int tagSizeSize, final int extendedHeaderSizeSize, final int paddingSizeSize, 
                         final int frameIdentifierSize, final int frameSizeSize, final int frameHeaderSize) {
        this.tagSizeSize = tagSizeSize;
        this.extendedHeaderSizeSize = extendedHeaderSizeSize;
        this.paddingSizeSize = paddingSizeSize;
        this.frameIdentifierSize = frameIdentifierSize;
        this.frameSizeSize = frameSizeSize;
        this.frameHeaderSize = frameHeaderSize;
    }

    public int getFrameIdentifierSize() {
        return frameIdentifierSize;
    }

    public int getFrameSizeSize() {
        return frameSizeSize;
    }

    public int getTagSizeSize() {
        return tagSizeSize;
    }
    

    public int getExtendedHeaderSizeSize() {
        return extendedHeaderSizeSize;
    }
    
    public int getPaddingSizeSize() {
        return paddingSizeSize;
    }

    public int getFrameHeaderSize() {
        return frameHeaderSize;
    }
    
    
    
}
