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
 * Created on Dec 1, 2005
 * @author Stephan Richard Palm
 * 
 */
package net.sourceforge.mecat.srp.utils;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import net.sourceforge.mecat.catalog.option.Options;

public class ButtonBorder implements Border {

    final static int edgeDistance = 10;
    final static int space = 3;
    final TitledBorder titledBorder;
    final JButton buttons[];
    final Rectangle rects[];
    JComponent destinationComponent = null;
    
    int buttonRolloverIndex = -1;
    Rectangle paintPosition = null;
    
    final int buttonHeight;

    final MouseListener mouseListener = new MouseListener(){
        public void mouseClicked(MouseEvent event) {

            // Only react on left mouse click
            if (event.getButton() != MouseEvent.BUTTON1)
                return;
            
            // Get the location of the click
            Point p = event.getPoint();

            // Look if one of the Buttons has to do something
            for (int i = 0; i < buttons.length; i++) 
                if (buttons[i].isEnabled() && distance(p.x - rects[i].getCenterX(), p.y - rects[i].getCenterY()) < rects[i].height / 2)
                    for (ActionListener actionListener : buttons[i].getActionListeners())
                        actionListener.actionPerformed(new ActionEvent(buttons[i], ActionEvent.ACTION_PERFORMED, ""));
        }
        public void mousePressed(MouseEvent arg0) {}
        public void mouseReleased(MouseEvent arg0) {}
        public void mouseEntered(MouseEvent arg0) {}
        public void mouseExited(MouseEvent arg0) {
            if (buttonRolloverIndex != -1) {
                buttonRolloverIndex = -1;
                repaint();
                return;
            }
        }
    };
    final MouseMotionListener mouseMotionListener = new MouseMotionListener(){
        public void mouseDragged(MouseEvent event) {}
        public void mouseMoved(MouseEvent event) {
            if (rects.length == 0)
                return;
            // Get the location of the mouse
            Point p = event.getPoint();
            int i = -1;
            try {
            for (i = 0; i < buttons.length; i++) 
                if (buttons[i].isEnabled() && buttons[i].isRolloverEnabled() && distance(p.x - rects[i].getCenterX(), p.y - rects[i].getCenterY()) < rects[i].height / 2) {
                    if (buttonRolloverIndex != i) {
                        buttonRolloverIndex = i;
                        repaint();
                    } 
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            if (buttonRolloverIndex != -1) {
                buttonRolloverIndex = -1;
                repaint();
                return;
            }
                
        }
    };
    

    PropertyChangeListener repaintListener = new PropertyChangeListener(){
        public void propertyChange(PropertyChangeEvent event) {
            if (Options.DEBUG && Options.verbosity >= 2)
                System.out.println(event.getPropertyName());
            repaint();
        }
    };
    
    public static double distance(double x, double y){
        return Math.sqrt(x*x + y*y);
    }

    public ButtonBorder(final JComponent destinationComponent, final TitledBorder titledBorder, final JButton ... buttons) {
        this.titledBorder = titledBorder;
        this.buttons = buttons;
        rects = new Rectangle[buttons.length];
        
        int tmp = 0;
        for (JButton button : buttons) {
            button.addPropertyChangeListener(repaintListener);
            tmp = Math.max(button.getIcon().getIconHeight(), tmp);
        }
        buttonHeight = tmp;
        
        changeDestinationComponent(destinationComponent);
    }
    
    public void changeDestinationComponent(final JComponent destinationComponent) {
        if (this.destinationComponent != null) {
            this.destinationComponent.removeMouseListener(mouseListener);
            this.destinationComponent.removeMouseMotionListener(mouseMotionListener);
        }
        
        this.destinationComponent = destinationComponent;

        if (destinationComponent == null)
            return;
        
        destinationComponent.setBorder(this);
        destinationComponent.addMouseListener(mouseListener);
        destinationComponent.addMouseMotionListener(mouseMotionListener);
    }
    
    protected void repaint(){
        destinationComponent.repaint();
    }
    
    public Insets getBorderInsets(Component component) {
        Insets insets = titledBorder.getBorderInsets(component);
        if (insets.top < buttonHeight + 1)
            insets.set(buttonHeight + 1, insets.left, insets.bottom, insets.right);
        return insets;
    }

    public boolean isBorderOpaque() {
        return titledBorder.isBorderOpaque();
    }

    public void paintBorder(Component component, Graphics g, int x, int y, int w, int h) {
        paintPosition = new Rectangle(x,y,w,h);
        titledBorder.paintBorder(component, g, x, y, w, h);
        int pos = x+w-edgeDistance;
        g.setColor(Color.BLACK);

        for (int i = buttons.length - 1; i >= 0; i--) {
            Icon icon;
            if (buttons[i].isEnabled())
                if (i == buttonRolloverIndex)
                    icon = buttons[i].getRolloverIcon();
                else
                    icon = buttons[i].getIcon();
            else
                icon = buttons[i].getDisabledIcon();
            
            pos -= icon.getIconWidth() + space;
            g.setColor(Color.WHITE);
            g.fillArc(pos, 0, icon.getIconWidth(), icon.getIconHeight(), 0, 360);
            icon.paintIcon(destinationComponent, g, pos, 0);
            g.setColor(Color.BLACK);
            g.drawArc(pos, 0, icon.getIconWidth(), icon.getIconHeight(), 0, 360);
            rects[i] = new Rectangle(pos, y, icon.getIconWidth(), icon.getIconHeight());
        }
    }
}
