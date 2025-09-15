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
 * Created on Aug 1, 2006
 * @author Stephan Richard Palm
 * 
 */
package net.sourceforge.mecat.catalog.filesystem;

import java.io.InputStream;
import java.io.IOException;

public class CountingStream extends InputStream {

    final InputStream sourceStream;
    final long size;
    long pos = 0;
    
    public CountingStream(InputStream sourceStream, long size) {
        this.sourceStream = sourceStream;
        this.size = size;
    }

    @Override
    public int read() throws IOException {
        if (pos >= size) 
            return 0;
        
        pos++;
        return sourceStream.read();
    }

    @Override
    public long skip(long n) throws IOException {
        long jump = n;
        if (size - pos < jump)
            jump = size - pos;
        
        pos += jump;
        return sourceStream.skip(jump);
    }
    
    
    public void skipRest() throws IOException {
        if (pos >= size) 
            return;
        
        pos = size;
        sourceStream.skip(size - pos);
    }
    

}
