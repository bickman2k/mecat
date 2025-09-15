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
 * Created on Dec 7, 2005
 * @author Stephan Richard Palm
 * 
 */
package net.sourceforge.mecat.catalog.gui.options.features.imagefeature;

import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

import javax.swing.JComponent;
import javax.swing.JPanel;

import net.sourceforge.mecat.catalog.datamanagement.Entry;
import net.sourceforge.mecat.catalog.datamanagement.PersistentThroughEntry;
import net.sourceforge.mecat.catalog.datamanagement.Util;
import net.sourceforge.mecat.catalog.option.Options;

public class ImageInformation implements PersistentThroughEntry {

    int height = -1;
    int width = -1;
    long filesize = -1;
    URL fileName = null;
    long last_check = -1;
    
    public long getFilesize() {
        return filesize;
    }

    public void setFilesize(long filesize) {
        this.filesize = filesize;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public URL getFileName() {
        return fileName;
    }

    public void setFileName(URL fileName) {
        this.fileName = fileName;
    }

    
    
    public long getLast_check() {
        return last_check;
    }

    public ImageInformation() {
        
    }


    final static JComponent dummy = new JPanel();
    

    /**
     * Checks if the information are correct.
     * And if they are not then replaces them.
     * @return false means could not verify.
     * @throws ImageGone when the image can not be found.
     */
    public boolean checkSize() throws ImageGone {
        int len = 0;
        try {
            URLConnection con = fileName.openConnection();
            con.connect();
            len = con.getContentLength();
        } catch (IOException e) {
            if (Options.DEBUG)
                e.printStackTrace();
            throw new ImageGone();
        }
/*        File file = new File(fileName.getFile());
        if (!file.exists())
            throw new ImageGone();
        if (filesize == file.length()) {
            last_check = System.currentTimeMillis();
            return true;
        }*/
        Image image = Toolkit.getDefaultToolkit().createImage(fileName);
        MediaTracker mediaTracker = new MediaTracker(dummy);
        mediaTracker.addImage(image, 0);
        try {
            mediaTracker.waitForAll();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }
        height = image.getHeight(null);
        width = image.getWidth(null);
//        filesize = file.length();
        filesize = len;
        last_check = System.currentTimeMillis();
        return true;
    }
    
    public ImageInformation(Long filesize, Integer height, Integer width, URL fileName, Long last_check) {
        if (filesize != null)
            this.filesize = filesize;
        if (height != null)
            this.height = height;
        if (width != null)
            this.width = width;
        this.fileName = fileName;
        this.last_check = last_check;
    }

    public boolean loadFromEntry(Entry entry) {
        return true;
    }

    public void saveToEntry(Entry entry) {
        Util.addArgument(entry, new Util.Argument(0, Long.class, filesize));
        Util.addArgument(entry, new Util.Argument(1, Integer.class, height));
        Util.addArgument(entry, new Util.Argument(2, Integer.class, width));
        Util.addArgument(entry, new Util.Argument(3, URL.class, fileName));
        Util.addArgument(entry, new Util.Argument(4, Long.class, last_check));
    }

    public String toString() {
        return filesize + ", " + height + ", " + width + ", " + fileName + ", " + last_check;
    }
    
}
