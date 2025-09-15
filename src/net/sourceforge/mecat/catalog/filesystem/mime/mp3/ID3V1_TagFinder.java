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
package net.sourceforge.mecat.catalog.filesystem.mime.mp3;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import net.sourceforge.mecat.catalog.filesystem.mime.Tag;
import net.sourceforge.mecat.catalog.filesystem.mime.TagFinder;

public class ID3V1_TagFinder implements TagFinder {

    public TagID3V1 getTag(File file) {
        FileInputStream istream = null;
        
        try {
            istream = new FileInputStream(file);

            if (file.length() < 128){
                istream.close();
                return null;
            }

            istream.skip(file.length() - 128);

            byte b[] = new byte[128];
            if (istream.read(b) != 128){
                istream.close();
                return null;
            }
            
            istream.close();

            String str = new String(b, 0, 3);
            if (!str.equalsIgnoreCase("TAG")){
                istream.close();
                return null;
            }

            istream.close();
            return new TagID3V1(b);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            istream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Tag readTag(InputStream iStream) throws IOException {
        byte b[] = new byte[128];

        if (iStream.read(b) != 128)
            return null;
        
        String str = new String(b, 0, 3);
        if (!str.equalsIgnoreCase("TAG"))
            return null;

        return new TagID3V1(b);
    }
}
