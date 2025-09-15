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
 * Created on Dec 6, 2005
 * @author Stephan Richard Palm
 * 
 */
package net.sourceforge.mecat.srp.utils.animatedbutton;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.Timer;

import net.sourceforge.mecat.catalog.i18n.util.SimpleLocalButton;

public class AnimatedToggleButton extends SimpleLocalButton {

    Icon[] icons = null;
    Icon[] pressedIcons = null;
    Icon[] rolloverIcons = null;

    final int delay;
    final int millisecondsPerFrame;
    
    int current_position = 0;
    
    
    
    ActionListener actionListener = new ActionListener() {
        
        long animation_start_time = -1;
        int animation_direction = 0;

        public void actionPerformed(ActionEvent arg0) {
            if (isSelected()) {
                if (current_position < icons.length - 1) {
                    // Store the time entering the animation draw sequence
                    long now = System.currentTimeMillis();
                    long past_time = now - animation_start_time;

                    switch (animation_direction) {
                    case 0: // Animation has just begun
                        animation_start_time = System.currentTimeMillis();
                        break;
                    case 1: // Animation already runs in the right direction
                        // do nothing
                        break;
                    case -1: // Animation already runs, but in the wrong direction
                        // Reset the animation_start_time to match the same position from the other end of the animation
                        animation_start_time = now -  (( millisecondsPerFrame * (icons.length - 1) ) - past_time);
                    }
                    // In every case the direction now is the following
                    animation_direction = 1;
                    // For the case that the animation_start_time was not set right before the switch
                    past_time = now - animation_start_time;

                    current_position = (int) (past_time / millisecondsPerFrame);
                    
                    if (current_position >= icons.length)
                        current_position = icons.length - 1;
                    
                    if (current_position < 0)
                        current_position = 0;
                    
                    setIcon(icons[current_position]);
                    if (pressedIcons != null)
                        setPressedIcon(pressedIcons[current_position]);
                    if (rolloverIcons != null)
                        setRolloverIcon(rolloverIcons[current_position]);
                    
                    if (current_position == icons.length -1)
                        animation_direction = 0;
                } else
                    animation_direction = 0;
            } else {
                if (current_position > 0) {
                    // Store the time entering the animation draw sequence
                    long now = System.currentTimeMillis();
                    long past_time = now - animation_start_time;

                    switch (animation_direction) {
                    case 0: // Animation has just begun
                        animation_start_time = System.currentTimeMillis();
                        break;
                    case 1: // Animation already runs, but in the wrong direction
                        // Reset the animation_start_time to match the same position from the other end of the animation
                        animation_start_time = now -  (( millisecondsPerFrame * (icons.length - 1) ) - past_time);
                        break;
                    case -1: // Animation already runs in the right direction
                        // do nothing
                    }
                    // In every case the direction now is the following
                    animation_direction = -1;
                    // For the case that the animation_start_time was not set right before the switch
                    past_time = now - animation_start_time;

                    current_position = (icons.length - 1) - (int) (past_time / millisecondsPerFrame);
                    
                    if (current_position >= icons.length)
                        current_position = icons.length - 1;
                    
                    if (current_position < 0)
                        current_position = 0;
                    
                    setIcon(icons[current_position]);
                    if (pressedIcons != null)
                        setPressedIcon(pressedIcons[current_position]);
                    if (rolloverIcons != null)
                        setRolloverIcon(rolloverIcons[current_position]);
                    
                    if (current_position == 0)
                        animation_direction = 0;
                }
            }
        }
    };

    protected static Icon[] deconstructChart(ImageIcon chart, int columns, int rows) {
        // Calculate the width of one image from the size of the whole chart 
        int width = chart.getIconWidth() / columns;
        // Same for the height
        int height = chart.getIconHeight() / rows;
        // Make the array that stores the icons
        Icon[] ret = new Icon[columns*rows];
        // Fill the icons
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < columns; j++) {
                // Create a image with alpha channel to store the bitmap
                BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
                // Fill it from the chart
                image.getGraphics().drawImage(chart.getImage(), 0, 0, width - 1, height - 1, j * width, i * height, (j + 1) * width - 1, (i + 1) * height - 1,  null);
                // put it at the right position in the array
                ret[i*columns + j] = new ImageIcon(image);
            }
        
        return ret;
    }
    
    
    public AnimatedToggleButton(ResourceBundle res, String toolTextKey, ImageIcon chart, int columns, int rows, final int delay, final int millisecondsPerFrame) {
        this(res, toolTextKey, deconstructChart(chart, columns, rows), delay, millisecondsPerFrame);
    }
    public AnimatedToggleButton(ResourceBundle res, String toolTextKey, Icon[] icons, final int delay, final int millisecondsPerFrame) {
        super(res, null, toolTextKey);
        setIcon(icons);
        super.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent arg0) {
                setSelected(!isSelected());
                fireAction(arg0);
            }
        });
        this.delay = delay;
        this.millisecondsPerFrame = millisecondsPerFrame;
        Timer timer = new Timer(delay, actionListener);
        timer.start();
    }
    
    /**
     * The chart given by the parameter icons must contain the same amount of images for Icon, PressedIcon and RolloverIcon
     * @param icons
     */
    public void setIcon(ImageIcon chart, int columns, int rows) {
        setIcon(deconstructChart(chart, columns, rows));
    }
    
    /**
     * The chart given by the parameter icons must contain the same amount of images for Icon, PressedIcon and RolloverIcon
     * @param icons
     */
    public void setPressedIcon(ImageIcon chart, int columns, int rows) {
        setPressedIcon(deconstructChart(chart, columns, rows));
    }
    
    /**
     * The chart given by the parameter icons must contain the same amount of images for Icon, PressedIcon and RolloverIcon
     * @param icons
     */
    public void setRolloverIcon(ImageIcon chart, int columns, int rows) {
        setRolloverIcon(deconstructChart(chart, columns, rows));
    }

    /**
     * The size of the list given by the parameter icons must be the same for Icon, PressedIcon and RolloverIcon
     * @param icons
     */
    public void setIcon(Icon[] icons) {
        this.icons = icons;
        setIcon(icons[current_position]);
    }
    
    /**
     * The size of the list given by the parameter icons must be the same for Icon, PressedIcon and RolloverIcon
     * @param icons
     */
    public void setPressedIcon(Icon[] icons) {
        this.pressedIcons = icons;
        setPressedIcon(pressedIcons[current_position]);
    }
    
    /**
     * The size of the list given by the parameter icons must be the same for Icon, PressedIcon and RolloverIcon
     * @param icons
     */
    public void setRolloverIcon(Icon[] icons) {
        this.rolloverIcons = icons;
        setRolloverIcon(rolloverIcons[current_position]);
    }

    Vector<ActionListener> listeners = new Vector<ActionListener>();

    @Override
    public void addActionListener(ActionListener arg0) {
        listeners.add(arg0);
    }


    @Override
    public void removeActionListener(ActionListener arg0) {
        listeners.remove(arg0);
    }
    
    protected void fireAction(ActionEvent event) {
        for (ActionListener listener : listeners)
            listener.actionPerformed(event);
    }

    
    
}
