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
 * Created on Jul 15, 2006
 * @author Stephan Richard Palm
 * 
 * The only purpose of this class is
 * to debug in command line mode
 * 
 */
package net.sourceforge.mecat.catalog.filesystem.mime.jpg;

import java.io.File;

import net.sourceforge.mecat.catalog.filesystem.mime.TagUtils;
import net.sourceforge.mecat.catalog.option.Options;

public class Tagfinder {

    EXIF_TagFinder exif = new EXIF_TagFinder();
    
    public static void main(String[] args) {
        Tagfinder tagfinder = new Tagfinder();
        for (String str : args) {
            File file = new File(str);
            if (file.isFile())
                tagfinder.getFile(file);
            if (file.isDirectory())
                tagfinder.getDir(file);
        }
    }
    
    void getDir(File _file) {
        File list[] = _file.listFiles();
        for (File file : list){
            if (file.isFile() && file.toString().endsWith(".jpg"))
                getFile(file);
            if (file.isDirectory())
                getDir(file);
        }
    }

    void getFile(File file) {
        if (file.exists()) {
            System.out.println(file.toString());
            System.out.println(" >> " + exif.getTag(file));
            System.out.println();
        } else {
            System.err.println(Options.getI18N(TagUtils.class).getString("File does not exist: [FILE]").replaceAll("\\[FILE\\]", "" + file));
            System.err.println();
        }
    }
    

    
}
