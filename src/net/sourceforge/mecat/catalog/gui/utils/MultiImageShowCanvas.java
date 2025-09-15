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
 * Created on Sep 11, 2004
 * @author Stephan Richard Palm
 */
package net.sourceforge.mecat.catalog.gui.utils;

import static java.lang.Math.max;
import static java.lang.Math.min;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.ImageObserver;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JPanel;


public class MultiImageShowCanvas extends JPanel {

    // All images that shall be drawn
    final Vector<Image> images;
    // Those images that have been loaded allready
    // and are ready to be shown
    final Vector<Image> drawableImages = new Vector<Image>();
    // Those images couldn't be loaded 
    final Vector<Image> brokenImages = new Vector<Image>();
    // The maximum dimension for the Panel
    Dimension max;
    // Width and Height of the Images all together
    // Caution: this variables are computed during
    // every invokation of getScale()
    int Width = 0;
    int Height = 0;
	
	public MultiImageShowCanvas(final Vector<Image> images) {
        this.images = images;

        for (Image image : images)
            Toolkit.getDefaultToolkit().prepareImage(image, -1, -1, this);
	}
    
    @Override
    protected synchronized void paintComponent(Graphics g) {
        if (brokenImages.size() == images.size())
            super.paintComponent(g);
        
        double scale = getScale();
        int posX = 0;
        
        for (Image image : drawableImages) {
            
            int w = ((int)( image.getWidth(null) * scale ));
            int h = ((int)( image.getHeight(null) * scale ));
            
            if (image != null)
    			g.drawImage(image, posX, 0, w, h, this);
            
            posX += w;
        }
	}

    @Override
    public synchronized boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
		if (infoflags == ImageObserver.ALLBITS) {
			Width = width; Height = height;

            int i = 0;
            for (Image image : images) {
                if (i == drawableImages.size()) {
                    drawableImages.add(img);
                    break;
                }
                if (img == image) {
                    drawableImages.add(i, img);
                    break;
                }                    
                if (drawableImages.get(i) == image)
                    i++;
                    
            }

            revalidate();
		} else if (infoflags == ImageObserver.ABORT || infoflags == ImageObserver.ERROR) {
		    brokenImages.add(img);
            if (brokenImages.size() == images.size()) {
                setLayout(new BorderLayout());
                add(new JLabel("No image available."));
                revalidate();
            }
        }
		return true;
	}


    public synchronized double getScale() {
        Width = 0;
        Height = 0;
        
        for (Image image : drawableImages) {
            int w = image.getWidth(null);
            int h = image.getHeight(null);
            
            Width += w;
            Height = max (Height, h);
            
        }
        
        if (Width == 0 && Height == 0)
            return 1;
        if (max == null)
            return 0;
        if (Height == 0)
            return max.width / Width;
        if (Width == 0)
            return max.height / Height;
        
        
        double w = (double)max.width / (double)Width;
        double h = (double)max.height / (double)Height;
        
        return min(w, h);
   }
    
    @Override
    public int getWidth() {
        if (brokenImages.size() == images.size())
            return super.getWidth();
        double scale = getScale();
        return ((int)(Width * scale));
    }
    
    @Override
    public int getHeight() {
        if (brokenImages.size() == images.size())
            return super.getHeight();
        double scale = getScale();
        return ((int)(Height * scale));
    }

    @Override
    public Dimension getSize() {
        if (brokenImages.size() == images.size())
            return super.getSize();
        return new Dimension(getWidth(), getHeight());
    }
    
    @Override
    public Dimension getSize(Dimension dim) {
        if (brokenImages.size() == images.size())
            return super.getSize(dim);
        if (dim == null)
            return getSize();
        dim.width = getWidth();
        dim.height = getHeight();
        return dim;
    }

    @Override
    public void setMaximumSize(final Dimension dim) {
        this.max = dim;
        revalidate();
    }
    
    @Override
    public Dimension getPreferredSize() {
        if (brokenImages.size() == images.size())
            return super.getPreferredSize();
        return new Dimension(getWidth(), getHeight());
    }
}
