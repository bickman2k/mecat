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

import static java.lang.Math.min;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.ImageObserver;
import java.util.Vector;


public class ImageShowCanvas extends Canvas/*JLabel*/ {

	Image image = null;
	ImageObserver observer;
    final Orientation orientation;
	
    public ImageShowCanvas() {
        this(null, null);
    }
    
    @Deprecated
	public ImageShowCanvas(final Orientation orientation) {
		this(orientation, null);
//        this.addComponentListener(new ComponentListener(){
//            public void componentResized(ComponentEvent e) {
//                System.out.println("componentResized(Width " + ImageShowCanvas.super.getWidth() + ", height " + ImageShowCanvas.super.getHeight() + ", currentScale " + currentScale + ")");
//            }
//
//            public void componentMoved(ComponentEvent arg0) {
//                // TODO Auto-generated method stub
//                
//            }
//
//            public void componentShown(ComponentEvent arg0) {
//                // TODO Auto-generated method stub
//                
//            }
//
//            public void componentHidden(ComponentEvent arg0) {
//                // TODO Auto-generated method stub
//                
//            }});
	}
	
    public ImageShowCanvas(final Image image) {
        this(null, image);
        
    }
    @Deprecated
	public ImageShowCanvas(final Orientation orientation, final Image image) {
        this.orientation = orientation;
////		super("Please Wait.");
//
//		// Make sure images get started.
		Toolkit.getDefaultToolkit().prepareImage(image, -1, -1, this);
		setImage(image);
	}
	
	public void setImage(final Image image) {
		this.image = image;
	}
	
///*	public void update(Graphics g) {
//		super.update(g);
//		System.out.println("update");
//	}*/
	
	public void paint(Graphics g) {
        System.out.println("ImageShowCanvas("+hashCode()+").paint()[Max=" + max.width + "," + max.height + ", SuperWidth=" + super.getWidth() + ", SuperHeight =" + super.getHeight() 
                + ", Width=" + Width + ", Height=" + Height + ", getWidth=" + getWidth() + ", getHeight=" + getHeight() + ", Scale=" + getScale() + "]");
		if (image != null)
			g.drawImage(image, 0,0, getWidth(), getHeight(), this);
	}
	
	public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
		if (infoflags == 32) {
			System.out.print("ImageShowCanvas("+hashCode()+").imageUpdate(");
			System.out.print(infoflags + ", ");
			System.out.print(x + ", ");
			System.out.print(y + ", ");
			System.out.print(width + ", ");
			System.out.print(height + ");");

			Width = width; Height = height;
			Pref_Width = width;
			Pref_Height = height;
            
            System.out.println("[SuperWidth=" + super.getWidth() + ", SuperHeight =" + super.getHeight() + ", Scale=" + getScale() + "]");
            

//			this.setSize(new Dimension(Width, Height));
////			repaint();
			for (ImageResizeListener irl : irls) {
////				System.out.println("IRL:" + irl);
				irl.ImageResize(this);
				repaint();
			}
				
		}
		return true;
	}

    // Width and Height of the image
	int Width = 0;
	int Height = 0;
    
	int Pref_Width = Width;
	int Pref_Height = Height;

//    double currentScale;
    
    public double getScale() {
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
        
//        Dimension dim = super.getSize();
//        switch(orientation) {
//        case Horizontal :
//            if (Width == 0)
//                return 1;
//            return (double)dim.width / (double)Width;
//        case Vertical :
//            if (Height == 0)
//                return 1;
//            return (double)dim.height / (double)Height;
//        }
//        return 0;
   }
    
    @Override
    public int getWidth() {
//        if (orientation == Orientation.Horizontal)
//            return super.getWidth();
        return ((int)(Width * getScale()));
        
    }
    
    @Override
    public int getHeight() {
//        if (orientation == Orientation.Vertical)
//            return super.getHeight();
        return ((int)(Height * getScale()));
    }
    
//    @Override
//    public void setSize(Dimension dim) {
//        switch(orientation) {
//        case Horizontal :
//            currentScale = (double)dim.width / (double)Width;
//            break;
//        case Vertical :
//            currentScale = (double)dim.height / (double)Height;
//            break;
//        }
//        super.setSize(dim);
//        System.out.print("ImageShowCanvas("+hashCode()+").setSize(" + dim.width + ", " + dim.height + ")");
//    }

//    @Override
//    public void setSize(int width, int height) {
//        setSize(new Dimension(width, height));
//    }
//    

    Dimension max;

    @Override
    public void setMaximumSize(final Dimension dim) {
        System.out.println("ImageShowCanvas("+hashCode()+").setMaximumSize(Dimension(" + dim.width + ", " + dim.height + "))");
        this.max = dim;
    }
    
    public Dimension getImageSize() {
        System.out.println("ImageShowCanvas("+hashCode()+").getImageSize() returns new Dimension(" + Width + ", " + Height + ")");
          return new Dimension(Width, Height);
//        return this.getMaximumSize();
      }
    
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(getWidth(), getHeight());
    }
    
//    public Dimension getPreferredSize() {
//      System.out.println("ImageShowCanvas("+hashCode()+").getPreferredSize() returns new Dimension(" + Pref_Width + ", " + Pref_Height + ")");
//        return new Dimension(Pref_Width, Pref_Height);
////      return this.getMaximumSize();
//    }
    
    
///*	public Dimension getSize() {
//		System.out.println("getSize()");
//		return new Dimension(Width, Height);
//	}
//
//	public int getWidth() {
//		System.out.println("getWidth()");
//		return Width;
//	}
//	
//	public int getHeight() {
//		System.out.println("getHeigth()");
//		return Height;
//	}*/

//	/*
//	 * TODO 
//	 * Has to be replaced by public void scale(double factor)
//	 */
///*	public void setWidth(int width) {
//		if (width == 0)
//			return;
//		System.out.println("Set to " + width + "x" + ((int)((Pref_Height * width)/Pref_Width)));
//		setSize(new Dimension(width, ((int)((Pref_Height * width)/Pref_Width))));
//	}*/
	
//	synchronized public void scale(final double factor) {
//		System.out.print("ImageShowCanvas("+hashCode()+").scale(" + factor + ");");
//		System.out.println("Pref_Width:" + Pref_Width + "Pref_Height:" + Pref_Height);
//		setSize(new Dimension((int)(Pref_Width*factor), (int)(Pref_Height*factor)));
//	}
//	
//	public void setImageSize(Dimension dim) {
//		setSize(dim);
//	}
//	public void setMaxImageSize(Dimension dim) {
//		setSize(new Dimension(
//				(getWidth() < dim.width)?getWidth():dim.width,
//				(getHeight() < dim.height)?getHeight():dim.height
//				));
//	}
	
	
	
	
	Vector<ImageResizeListener> irls = new Vector<ImageResizeListener>();
	
	public void addImageResizeListener(ImageResizeListener listener) {
		irls.add(listener);
	}

}
