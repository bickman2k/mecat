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
 * Created on May 27, 2006
 * @author Stephan Richard Palm
 * 
 */
package net.sourceforge.mecat.catalog.filesystem.store;

import java.io.File;
import java.io.IOException;

public class MD5ThreadEvent {

    enum MD5ThreadEventType {
        SizeChanged,
        Interupted,
        IOException
    }
    
    final MD5ThreadEventType type;
    
    final IOException ioException;
    
    final long position;
    
    final File file;

    public MD5ThreadEvent(IOException ioException, File file) {
        type = MD5ThreadEventType.IOException;
        this.ioException = ioException;
        this.position = -1;
        this.file = file;
    }
    
    public MD5ThreadEvent(MD5ThreadEventType type, long position) {
        this.type = type;
        this.ioException = null;
        this.position = position;
        this.file = null;
    }

    public MD5ThreadEvent(MD5ThreadEventType type) {
        this.type = type;
        this.ioException = null;
        this.position = -1;
        this.file = null;
    }

    public MD5ThreadEventType getType() {
        return type;
    }

    public IOException getIOException() {
        return ioException;
    }
    
    public long getPosition() {
        return position;
    }
    
}
