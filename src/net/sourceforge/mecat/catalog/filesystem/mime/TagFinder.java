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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public interface TagFinder<T extends Tag> {

    /**
     * Returns a tag object containing the information
     * 
     * @param file
     * @return a tag for the given file, if there is any
     */
    public T getTag(File file);

    /**
     * This function is invoked, 
     * at the point where the exact position is 
     * known. (Also used to put multiple tags in one file)
     * 
     * @param iStream
     * @return the read tag
     * @throws IOException 
     */
    public T readTag(InputStream iStream) throws IOException;

}
