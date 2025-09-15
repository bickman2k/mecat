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

import java.awt.Cursor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Vector;

import javax.swing.JTree;
import javax.swing.tree.TreePath;

import net.sourceforge.mecat.catalog.filter.Filter;
import static net.sourceforge.mecat.catalog.option.Options.filterTreeModel;

public class DragDropFilterTree extends JTree implements DragGestureListener, DragSourceListener {

    Vector<DragDropFilterTreeListener> dragDropFilterTreeListeners = new Vector<DragDropFilterTreeListener>();
    
    public void addDragDropFilterTreeListener(final DragDropFilterTreeListener dragDropFilterTreeListener){
        dragDropFilterTreeListeners.add(dragDropFilterTreeListener);
    }
    
    public void removeDragDropFilterTreeListener(final DragDropFilterTreeListener dragDropFilterTreeListener){
        dragDropFilterTreeListeners.remove(dragDropFilterTreeListener);
    }
    
    protected void fireSelectionChanged(){
        for (DragDropFilterTreeListener dragDropFilterTreeListener : dragDropFilterTreeListeners)
            dragDropFilterTreeListener.selectionChanged();
    }
    
    public DragDropFilterTree() {
        super(filterTreeModel);
        DragSource.getDefaultDragSource().createDefaultDragGestureRecognizer(this, DnDConstants.ACTION_COPY_OR_MOVE, this);
        addMouseListener(new MouseListener(){
            public void mouseClicked(MouseEvent event) {
                if (event.getButton() != MouseEvent.BUTTON1)
                    return;
                if (event.getClickCount() != 2)
                    return;
                if (getSelectedFilter() == null)
                    return;
                
                fireSelectionChanged();
            }
            public void mousePressed(MouseEvent arg0) {}
            public void mouseReleased(MouseEvent arg0) {}
            public void mouseEntered(MouseEvent arg0) {}
            public void mouseExited(MouseEvent arg0) {}
        });
    }
    
    public Filter getSelectedFilter() {
        TreePath path = getSelectionPath();
        if (path == null)
            return null;
        Object last = path.getLastPathComponent();
        if (last instanceof Filter)
            return ( Filter ) last;
        return null;
    }
    
    public void dragGestureRecognized(DragGestureEvent e) {
        Filter filter = getSelectedFilter();
        if (filter == null)
            return;
        Cursor cursor = selectCursor (e.getDragAction());
        DragSource.getDefaultDragSource().startDrag(e, cursor, filter, this);
    }

    private Cursor selectCursor (int action) {
        return (action == DnDConstants.ACTION_MOVE) ?
                DragSource.DefaultMoveDrop : DragSource.DefaultCopyDrop;
    }

    // DragSourceListening not yet done
    public void dragEnter(DragSourceDragEvent arg0) {
        // TODO Auto-generated method stub
        
    }

    // DragSourceListening not yet done
    public void dragOver(DragSourceDragEvent arg0) {
        // TODO Auto-generated method stub
        
    }

    // DragSourceListening not yet done
    public void dropActionChanged(DragSourceDragEvent arg0) {
        // TODO Auto-generated method stub
        
    }

    // DragSourceListening not yet done
    public void dragExit(DragSourceEvent arg0) {
        // TODO Auto-generated method stub
        
    }

    // DragSourceListening not yet done
    public void dragDropEnd(DragSourceDropEvent arg0) {
        // TODO Auto-generated method stub
        
    }
    

}
