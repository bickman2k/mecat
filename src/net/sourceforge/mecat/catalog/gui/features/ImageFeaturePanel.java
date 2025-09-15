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
 * Created on Aug 28, 2004
 * @author Stephan Richard Palm
 */
package net.sourceforge.mecat.catalog.gui.features;

import static java.lang.Math.max;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Insets;
import java.util.Vector;

import javax.swing.JLabel;

import net.sourceforge.mecat.catalog.gui.features.desktop.FeatureDesktop;
import net.sourceforge.mecat.catalog.gui.utils.ImageResizeListener;
import net.sourceforge.mecat.catalog.gui.utils.ImageShowCanvas;
import net.sourceforge.mecat.catalog.medium.features.Feature;
import net.sourceforge.mecat.catalog.medium.features.ImageFeature;

/**
 * 
 * @deprecated
 *
 */
public class ImageFeaturePanel extends FeaturePanel<ImageFeature> implements ImageResizeListener{

	final protected Image image;
	final JLabel label = new JLabel("No Image available.");;
	final ImageShowCanvas canvas;
	
	public ImageFeaturePanel(final ImageFeature feature, FeatureDesktop desktop, final boolean border) 
	{
		super(feature, desktop, border, "Image");

        image = feature.getImage();
		if (image != null) {
			canvas = new ImageShowCanvas(image);
			add(canvas);
		}
		else {
            canvas = null;
            add(label);
        }
	}

    @Override
	public Dimension getPreferredSize() {
		if (canvas != null) {
            if (getBorder() != null) {
                Dimension cd = canvas.getPreferredSize();
                Insets in = getBorder().getBorderInsets(this);
                System.out.println("ImageFeaturePanel("+hashCode()+").getPreferredSize() returns new Dimension(" + (cd.width + in.left + in.right) + ", " + (cd.height + in.top + in.bottom) + ")");
    			return new Dimension(cd.width + in.left + in.right, cd.height + in.top + in.bottom);
            } else {
                System.out.println("ImageFeaturePanel("+hashCode()+").getPreferredSize() returns new Dimension(" + canvas.getPreferredSize().width + ", " + canvas.getPreferredSize().height + ")");
                return canvas.getPreferredSize();
            }
        } else {
            //  If there is no image, then smalest size possible
            System.out.println("ImageFeaturePanel("+hashCode()+").getPreferredSize() returns new Dimension(0, 0)");
			return new Dimension(0,0);
        }
	}
    
//    @Override
//    public void setSize(int width, int height) {
//        if (canvas != null) {
//            if (getBorder() != null) {
//                Insets in = getBorder().getBorderInsets(this);
//                canvas.setSize(max(width - in.left - in.right, 0), max(height - in.top - in.bottom, 0));
//            } else 
//                canvas.setSize(width, height);
//        } else
//            super.setSize(width, height);
//    }
	
    @Override
    public void setMaximumSize(Dimension dim) {
        System.out.println("ImageFeaturePanel("+hashCode()+").setMaximumSize(Dimension(" + dim.width + ", " + dim.height + ")");
        if (canvas != null) {
            if (getBorder() != null) {
                Insets in = getBorder().getBorderInsets(this);
                canvas.setMaximumSize(new Dimension(max(dim.width - in.left - in.right, 0), max(dim.height - in.top - in.bottom, 0)));
            } else
                canvas.setMaximumSize(dim);
        } 
    }
    
    @Override
    public Dimension getMinimumSize() {
        return new Dimension(0,0);
    }

    @Override
    public int getWidth(){
        if (canvas != null) {
            if (getBorder() != null) {
                Insets in = getBorder().getBorderInsets(this);
                return canvas.getWidth() + in.left + in.right;
            } else
                return canvas.getWidth();
        } else 
            return super.getWidth();
    }
    
    @Override
    public int getHeight(){
        if (canvas != null) {
            if (getBorder() != null){
                Insets in = getBorder().getBorderInsets(this);
                return canvas.getHeight() + in.top + in.bottom;
            } else
                return canvas.getHeight();
        } else 
            return super.getHeight();
    }
    
    
	

	public void ImageResize(ImageShowCanvas source) {
//		System.out.println("IRL-ImageShowCanvas");
		for (ImageResizeListener irl : irls) {
//			System.out.println("ImageFeature IRL:" + irl);
			irl.ImageResize(source);
		}
//		updateUI();
	}

	Vector<ImageResizeListener> irls = new Vector<ImageResizeListener>();
	
	public void addImageResizeListener(ImageResizeListener listener) {
//		System.out.println("ImageFeature_reg");
		irls.add(listener);
	}

    public void featureValueChanged(Feature source) {
        // TODO Auto-generated method stub
        
    }

}
