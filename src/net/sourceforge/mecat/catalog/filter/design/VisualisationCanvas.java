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
 * Created on Sep 4, 2005
 * @author Stephan Richard Palm
 * 
 */
package net.sourceforge.mecat.catalog.filter.design;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.event.ChangeListener;

import net.sourceforge.mecat.catalog.filter.FilterUtils;

public class VisualisationCanvas extends JPanel {
    Point current_mousePosition = null;
    VisualisationNode current_position;
    VisualisationNode node;
    VisualisationNode current_selected;
    FilterDropTarget filterDropTarget = new FilterDropTarget();
    
    Vector<ChangeListener> selectionChangeListener = new Vector<ChangeListener>();
    
    public void addSelectionChangeListener(ChangeListener changeListener) {
        selectionChangeListener.add(changeListener);
    }
    
    public void removeSelectionChangeListener(ChangeListener changeListener) {
        selectionChangeListener.remove(changeListener);
    }
    
    protected void fireSelectionChanged() {
        for (ChangeListener changeListener : selectionChangeListener)
            changeListener.stateChanged(null);
    }
    
    public VisualisationNode getCurrentSelection() {
        return current_selected;
    }
    
    public void setCurrentSelection(VisualisationNode node) {
        current_selected = node;
        fireSelectionChanged();
    }
    
    public void setVisualisationNode(final VisualisationNode node){
        current_position = null;
        current_selected = null;
        this.node = node;
        
        node.addVisualisationNodeListener(new VisualisationNodeListener(){
            public void changed() {
                revalidate();
                repaint();
            }
        });
        
        fireSelectionChanged();

//        revalidate();
//        repaint();
    }
    
    public VisualisationCanvas(final VisualisationNode node){
        setVisualisationNode(node);
        addMouseMotionListener(new MouseMotionListener(){
            public void mouseDragged(MouseEvent arg0) {}
            public void mouseMoved(MouseEvent e) {
                current_mousePosition = e.getPoint();
                VisualisationNode old_position = current_position;
                current_position = node.getVisualisationNodeForPosition(e.getPoint().x, e.getPoint().y);
                if (old_position != current_position || filterDropTarget.current_visualisation != null)
                    repaint();
                
            }});
        addMouseListener(new MouseListener(){
            public void mouseClicked(MouseEvent event) {
                
                if (event.getButton() != MouseEvent.BUTTON1) {
                    filterDropTarget.setCondition(null);
                    repaint();
                    return;
                }
                
                // Copy the pointers locally for threadsafty
                VisualisationNode sel = current_selected;
                VisualisationNode pos = current_position;
                VisualisationNode drop = filterDropTarget.current_visualisation;
                
                if (drop == null) {
                    current_selected = pos;
                } else {
                    if (pos == null) 
                        return;
                    current_selected = null;
                    
                    node.exchangeFilter(pos, FilterUtils.copyFilter(drop.getFilter()));
//                    repaint();
                }
                    
                repaint();
                fireSelectionChanged();
            }
            public void mousePressed(MouseEvent arg0) {}
            public void mouseReleased(MouseEvent arg0) {}
            public void mouseEntered(MouseEvent arg0) {}
            public void mouseExited(MouseEvent arg0) {}
            
        });
        
        setDropTarget(filterDropTarget);
        filterDropTarget.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent arg0) {
                current_mousePosition = getMousePosition();
                repaint();
            }
        });
    }
    
    @Override
    protected void paintComponent(Graphics arg) {
//        // TODO Auto-generated method stub
//        super.paintComponent(arg0);
//    }
//
//    public void paint(Graphics arg) {
        arg.clearRect(0,0, this.getWidth(), this.getHeight());
        node.paint(arg);
        // Copy the pointer locally for threadsafty
        VisualisationNode sel = current_selected;
        if (sel != null) {
            Rectangle rect = node.getPositionOf(sel);
            arg.setColor(Color.green);
            arg.drawRect(rect.x-2, rect.y-2, rect.width + 3, rect.height + 3);
            arg.drawRect(rect.x-3, rect.y-3, rect.width + 5, rect.height + 5);
        }
        VisualisationNode vis = current_position;
        if (vis != null) {
            Rectangle rect = node.getPositionOf(vis);
            arg.setColor(Color.blue);
            arg.drawRect(rect.x-2, rect.y-2, rect.width + 3, rect.height + 3);
        }
        VisualisationNode drop = filterDropTarget.current_visualisation;
        Point mPos = current_mousePosition;
        if (drop != null && mPos != null) {
            arg.translate(mPos.x, mPos.y);
            drop.paintDirect(arg);
            arg.translate(-mPos.x, -mPos.y);
        }
    }
    public Dimension getPreferredSize() {
        return node.getPreferredSize();
    }
//    public int getWidth() {
//        return max(super.getWidth(), node.getPreferredSize().width);
//    }
//    public int getHeight() {
//        return max(super.getHeight(), node.getPreferredSize().height);
//    }
};