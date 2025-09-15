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
 * Created on Jul 23, 2006
 * @author Stephan Richard Palm
 * 
 */
package net.sourceforge.mecat.catalog.filesystem.mime;

import java.io.DataOutputStream;
import java.io.IOException;

import net.sourceforge.mecat.catalog.filesystem.Detail;

public interface Tag extends Detail {

    /**
     * Copies the tag from the file "file" into the output stream "ostream".
     * This makes a binary copy of the tag from the source file "file" to the 
     * outputstream "ostream".
     * 
     * @param tag
     * @param ostream
     * @throws IOException 
     */
    public abstract void copyTag(/*File file, */DataOutputStream ostream) throws IOException;
    
    /**
     * Length of the tag in file. 
     * This is the amount of space a tag need if it is copied with copyTag
     * 
     * @return length of tag
     */
    public abstract int getLength();
    
    /**
     * Starting position of the tag.
     * A positiv number is the relative position from the beginning of the file.
     * A negative number is the relative position from the end of the file.
     * @return position of tag in file
     */
    public abstract int getStartPosition();

    public abstract TagType getTagType();
}
