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
 * Created on Nov 26, 2005
 * @author Stephan Richard Palm
 * 
 * 
 * This class is used to produce Button and
 * Images for classes that have an image chart
 * and border images.
 * 
 * The images are stored in charts in order to 
 * have a overviewable size of files.
 * What is a chart? A chart is a assortment of images.
 * Actually it is allways the same image in different 
 * sizes. This way there will be no expensive resizeing
 * during runtime. In order to save space and make the
 * charts look nice the have a special design.
 * Here is an example how it looks for 5 image sizes.
 * 5555555544444
 * 5555555544444
 * 5555555544444
 * 5555555544444
 * 5555555544444
 * 5555555533322
 * 5555555533322
 * 555555553331X
 * 
 * Border images are composed images. They are composed
 * from images of the size 40x40. Together with the scaled 
 * image 32x32 from a chart they can be used to make the
 * rollover icon and pressed icon for a button.
 */
package net.sourceforge.mecat.catalog.gui;

import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.net.URL;

import javax.swing.ImageIcon;

import net.sourceforge.mecat.catalog.option.Options;


public class ImageProcessing {

    // At the moment the programm works under the
    // assumtion that all charts have the same
    // amount of scaled images.
    // This way the positions and sizes can be computed 
    // one time.
    
    static final int chartsize = 7;
    static final Rectangle[] chartPositions = new Rectangle[chartsize];
    
    static {
        // Calculate the sizes for the scaled images
        for (int i = 0; i < chartsize; i++) {
            chartPositions[i] = new Rectangle();
            if (i == 0) {
                chartPositions[i].height = 16;
                chartPositions[i].width = 16;
                continue;
            }
            if (i == 1) {
                chartPositions[i].height = 32;
                chartPositions[i].width = 32;
                continue;
            }
            chartPositions[i].height = chartPositions[i-1].height + chartPositions[i-2].height;
            chartPositions[i].width = chartPositions[i-1].width + chartPositions[i-2].width;
        }
        // Calculate the positions for the scaled images
        int xOff = 0, yOff = 0;
        boolean orientation = (chartsize % 2) == 1;
        for (int i = chartsize - 1; i >= 0; i--) {
            chartPositions[i].x = xOff;
            chartPositions[i].y = yOff;
            if (orientation)
                xOff += chartPositions[i].width;
            else
                yOff += chartPositions[i].height;
            orientation = !orientation;
        }
        for (int i = 0; i < chartsize; i++) 
            if (Options.verbosity > 2)
                System.out.println("[ImgProc] " + i + " at (" + chartPositions[i].x + " ," + chartPositions[i].y + ") size " + chartPositions[i].width + "x" + chartPositions[i].height);
    }
    
    static void getChart(final URL imageURL) {
        ImageIcon icon = new ImageIcon(imageURL);
        
    }
    
    public static Image getImage(final ImageIcon chart, final int size) {
        Image image = new BufferedImage(chartPositions[size].width, chartPositions[size].height, BufferedImage.TYPE_4BYTE_ABGR);
        image.getGraphics().drawImage(chart.getImage(), 0,0, chartPositions[size].width, chartPositions[size].height, 
                                                        chartPositions[size].x, chartPositions[size].y, 
                                                        chartPositions[size].x + chartPositions[size].width - 1, chartPositions[size].y + chartPositions[size].height - 1, null);
        return image;
    }
    
    static BufferedImage getBlankButtonIcon() {
        BufferedImage image = new BufferedImage(40, 40, BufferedImage.TYPE_4BYTE_ABGR);
        return image;
    }
    
    static Image getButtonRolloverIcon(final ImageIcon chart, final ImageIcon borders) {
        BufferedImage image = getBlankButtonIcon();
        image.getGraphics().drawImage(chart.getImage(), 4,4, 35, 35, chartPositions[1].x, chartPositions[1].y, chartPositions[1].x + chartPositions[1].width - 1, chartPositions[1].y + chartPositions[1].height - 1, null);
        image.getGraphics().drawImage(borders.getImage(), 0, 0, 39, 39, 40, 0, 79, 39, null);
        return image;
    }
    static Image getButtonPressedIcon(final ImageIcon chart, final ImageIcon borders) {
        BufferedImage image = getBlankButtonIcon();
        image.getGraphics().drawImage(chart.getImage(), 4,4, 35, 35, chartPositions[1].x, chartPositions[1].y, chartPositions[1].x + chartPositions[1].width - 1, chartPositions[1].y + chartPositions[1].height - 1, null);
        image.getGraphics().drawImage(borders.getImage(), 0, 0, 39, 39, 0, 0, 39, 39, null);
        return image;
    }

    /** 
     * This function can be used to get a image in the right size for a button.
     * @param chart The image chart from which the scaled image shall be taken.
     * @return the new Image with the size 40x40.
     */
    static Image getButtonIcon(final ImageIcon chart) {
        BufferedImage image = getBlankButtonIcon();
        image.getGraphics().drawImage(chart.getImage(), 4,4, 35, 35, chartPositions[1].x, chartPositions[1].y, chartPositions[1].x + chartPositions[1].width - 1, chartPositions[1].y + chartPositions[1].height - 1, null);
        return image;
    }
    

    static Image getMiniButtonRolloverIcon(final ImageIcon chart, final ImageIcon borders) {
        Image image = new BufferedImage(20, 20, BufferedImage.TYPE_4BYTE_ABGR);
        image.getGraphics().drawImage(chart.getImage(), 2,2, 17, 17, chartPositions[0].x, chartPositions[0].y, chartPositions[0].x + chartPositions[0].width - 1, chartPositions[0].y + chartPositions[0].height - 1, null);
        image.getGraphics().drawImage(borders.getImage(), 0, 0, 19, 19, 40, 0, 79, 39, null);
        return image;
    }
    static Image getMiniButtonPressedIcon(final ImageIcon chart, final ImageIcon borders) {
        Image image = new BufferedImage(20, 20, BufferedImage.TYPE_4BYTE_ABGR);
        image.getGraphics().drawImage(chart.getImage(), 2,2, 17, 17, chartPositions[0].x, chartPositions[0].y, chartPositions[0].x + chartPositions[0].width - 1, chartPositions[0].y + chartPositions[0].height - 1, null);
        image.getGraphics().drawImage(borders.getImage(), 0, 0, 19, 19, 0, 0, 39, 39, null);
        return image;
    }
    /** 
     * This function can be used to get a image in the right size for a button.
     * @param chart The image chart from which the scaled image shall be taken.
     * @return the new Image with the size 40x40.
     */
    static Image getMiniButtonIcon(final ImageIcon chart) {
        Image image = new BufferedImage(20, 20, BufferedImage.TYPE_4BYTE_ABGR);
        image.getGraphics().drawImage(chart.getImage(), 2,2, 17, 17, chartPositions[0].x, chartPositions[0].y, chartPositions[0].x + chartPositions[0].width - 1, chartPositions[0].y + chartPositions[0].height - 1, null);
        return image;
    }
}
