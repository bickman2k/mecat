/*
 *  MeCat - Media Catalog more information on
 *  http://mecat.sourceforge.net/
 *  Copyright (C) 2005, Stephan Richard Palm, All Rights Reserved.
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
 * Created on Aug 28, 2005
 * @author Stephan Richard Palm
 * Copyright by Stephan Richard Palm
 * 
 */
package net.sourceforge.mecat.catalog.gui;

import java.awt.event.ActionListener;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import javax.swing.ImageIcon;
import javax.swing.JButton;

import net.sourceforge.mecat.catalog.option.Options;
import net.sourceforge.mecat.srp.utils.animatedbutton.AnimatedToggleButton;

import java.awt.Color;
import java.awt.Insets;

public class ToolBarUtils {
    
    static Map<String, ImageIcon> cache = new HashMap<String, ImageIcon>();
    static boolean debug = Options.DEBUG;

    protected static boolean exists(final String imageName) {
         return exists(ToolBarUtils.class, imageName);
    }
    public static boolean exists(final Class c, final String imageName) {
        return loadImage(c, imageName + ".png", "") != null;
    }
    
    public static ImageIcon loadImage(final String imageName, final String altText) {
        return loadImage(ToolBarUtils.class, imageName, altText);
    }

    public static ImageIcon loadImage(final Class c, final String imageName, final String altText) {
        return loadImage(c, imageName, altText, true);
    }
    
    public static ImageIcon loadImage(final Class c, final String imageName, final String altText, final boolean assertMissing ) {
        String imgLocation = imageName;
//        if (!(imageName.endsWith("gif") || imageName.endsWith("png")))
//            imgLocation += ".gif";

        String cacheStr = c.getName() + ":" + imageName;
        if (cache.containsKey(cacheStr))
            return cache.get(cacheStr);

        URL imageURL = c.getResource(imgLocation);

        if (imageURL != null) {
            cache.put(cacheStr, new ImageIcon(imageURL, altText));
        } else {
            cache.put(cacheStr, null);
            if (debug && assertMissing) {
                System.err.println("Resource not found: " + imgLocation + " at " + imgLocation);
                (new Exception()).printStackTrace();
            }
        }
        
        return cache.get(cacheStr);
   }

    public static ImageIcon loadImage(final String imageName, final int size, final String altText) {
        return loadImage(ToolBarUtils.class, imageName, size, altText);
    }

    public static ImageIcon loadImage(final Class c, final String imageName, final int size, final String altText) {
        ImageIcon image = loadImage(c, imageName + ".png", altText);
        if (image == null)
            return null;
        return new ImageIcon(ImageProcessing.getImage(image, size), altText);
    }

        /*
    public static JButton makeNavigationButton(final String imageName, final String disableImageName, final String actionCommand, final String toolTipText, final String altText, final ActionListener listener) {
        return makeNavigationButton(ToolBarUtils.class, imageName, disableImageName, actionCommand, toolTipText, altText, listener);
    }
    
    public static JButton makeNavigationButton(final String imageName, final String actionCommand, final String toolTipText, final String altText, final ActionListener listener) {
        return makeNavigationButton(ToolBarUtils.class, imageName, actionCommand, toolTipText, altText, listener);
    }
    
    public static JButton makeNavigationButton(final Class c, final String imageName, final String disableImageName, final String actionCommand, final String toolTipText, final String altText, final ActionListener listener) {
        JButton ret = makeNavigationButton(c, imageName, actionCommand, toolTipText, altText, listener);
        final ImageIcon image = loadImage(c, disableImageName, altText);
        ret.setDisabledIcon(image);
        return ret;
    }
    
    public static JButton makeNavigationButton(final Class c, final String imageName, final String actionCommand, final String toolTipText, final String altText, final ActionListener listener) {
        final ImageIcon image = loadImage(c, imageName, altText);
        
        JButton button = new JButton();
        button.setActionCommand(actionCommand);
        button.setToolTipText(toolTipText);
        button.addActionListener(listener);
        button.setMargin(new Insets(0,0,0,0));
//        button.setBorder(null);

        if (image != null)
            button.setIcon(image);
        else
            button.setText(altText);
        
        return button;
    }*/
    
    public static JButton makeBlankButton() {
        JButton button = new JButton();
        
        button.setMargin(new Insets(0,0,0,0));
        button.setBorder(null);
        button.setContentAreaFilled(false);
        button.setBackground(new Color(0,0,0,0));
        button.setOpaque(false);
        button.setEnabled(false);
        button.setIcon(new ImageIcon(ImageProcessing.getBlankButtonIcon()));
        
        return button;
    }
    
    public static JButton makeButton(final String imageName, final String actionCommand, final String toolTipText, final String altText, final ActionListener listener) {
        return makeButton(ToolBarUtils.class, imageName, actionCommand, toolTipText, altText, listener);
    }
    
    public static JButton makeButton(final Class c, final String imageName, final String actionCommand, final String toolTipText, final String altText, final ActionListener listener) {
        
        JButton button = new JButton();
        button.setActionCommand(actionCommand);
        if (toolTipText != null && toolTipText.length() > 0)
            button.setToolTipText(toolTipText);
        if (listener != null)
            button.addActionListener(listener);

        addImages(button, c, imageName, altText);
        
        return button;
    }
    
    public static void addImages(JButton button, final Class c, final String imageName, final String altText) {
        final ImageIcon chart = loadImage(c, imageName + ".png", altText);
        final ImageIcon disabled = loadImage(c, imageName + "_trans.png", altText, false);
        final ImageIcon border = loadImage(c, imageName + "_border_for_32.png", altText);

        button.setMargin(new Insets(0,0,0,0));
        button.setBorder(null);
        button.setContentAreaFilled(false);
        button.setBackground(new Color(0,0,0,0));
        button.setOpaque(false);
        
        if (chart != null) {
            button.setIcon(new ImageIcon(ImageProcessing.getButtonIcon(chart)));
            if (border != null) {
                button.setRolloverEnabled(true);
                button.setRolloverIcon(new ImageIcon(ImageProcessing.getButtonRolloverIcon(chart, border)));
                button.setPressedIcon(new ImageIcon(ImageProcessing.getButtonPressedIcon(chart, border)));
            }
        } else
            button.setText(altText);
        if (disabled != null)
            button.setDisabledIcon(new ImageIcon(ImageProcessing.getButtonIcon(disabled)));
    }
    
    public static JButton makeMiniButton(final String imageName, final String actionCommand, final String toolTipText, final String altText, final ActionListener listener) {
        return makeMiniButton(ToolBarUtils.class, imageName, actionCommand, toolTipText, altText, listener);
    }
    
    public static JButton makeMiniButton(final Class c, final String imageName, final String actionCommand, final String toolTipText, final String altText, final ActionListener listener) {
        final ImageIcon chart = loadImage(c, imageName + ".png", altText);
        final ImageIcon disabled = loadImage(c, imageName + "_trans.png", altText, false);
        final ImageIcon border = loadImage(c, imageName + "_border_for_32.png", altText);
        
        JButton button = new JButton();
        button.setActionCommand(actionCommand);
        button.setToolTipText(toolTipText);
        if (listener != null)
            button.addActionListener(listener);
        button.setMargin(new Insets(0,0,0,0));
        button.setBorder(null);
        button.setContentAreaFilled(false);
        button.setBackground(new Color(0,0,0,0));
        button.setOpaque(false);

        if (chart != null) {
            button.setIcon(new ImageIcon(ImageProcessing.getMiniButtonIcon(chart)));
            if (border != null) {
                button.setRolloverEnabled(true);
                button.setRolloverIcon(new ImageIcon(ImageProcessing.getMiniButtonRolloverIcon(chart, border)));
                button.setPressedIcon(new ImageIcon(ImageProcessing.getMiniButtonPressedIcon(chart, border)));
            }
        } else
            button.setText(altText);
        if (disabled != null)
            button.setDisabledIcon(new ImageIcon(ImageProcessing.getMiniButtonIcon(disabled)));
        
        return button;
    }
    
    public static JButton makeAnimatedToggleButton(final int columns, final int rows, final String imageName, final String actionCommand, ResourceBundle res, String toolTipKey, final ActionListener listener) {
        return makeAnimatedToggleButton(ToolBarUtils.class, columns, rows, imageName, actionCommand, res, toolTipKey, listener);
    }
 
    public static JButton makeAnimatedToggleButton(final Class c, final int columns, final int rows, final String imageName, final String actionCommand, ResourceBundle res, String toolTipKey, final ActionListener listener) {
        final ImageIcon chart = loadImage(c, imageName + ".png", null);
        final ImageIcon border_press = loadImage(c, imageName + "_border_pressed.png", null);
        final ImageIcon border_roll = loadImage(c, imageName + "_border_rollover.png", null);
        
        AnimatedToggleButton button = new AnimatedToggleButton(res, toolTipKey, chart, columns, rows, 20, 20);
        button.setActionCommand(actionCommand);
//        if (toolTipText != null && toolTipText.length() > 0)
//            button.setToolTipText(toolTipText);
        if (listener != null)
            button.addActionListener(listener);
        button.setMargin(new Insets(0,0,0,0));
        button.setBorder(null);
        button.setContentAreaFilled(false);
        button.setBackground(new Color(0,0,0,0));
        button.setOpaque(false);

        if (chart != null) {
            button.setIcon(chart, columns, rows);
            if (border_roll != null) {
                button.setRolloverEnabled(true);
                button.setRolloverIcon(border_roll, columns, rows);
            }
            if (border_press != null) 
                button.setPressedIcon(border_press, columns, rows);
        } else
            button.setText(res.getString(toolTipKey));
        
        return button;
    }
}
