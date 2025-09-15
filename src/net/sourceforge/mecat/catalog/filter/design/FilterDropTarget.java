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

import java.awt.HeadlessException;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.TooManyListenersException;
import java.util.Vector;

import net.sourceforge.mecat.catalog.filter.Filter;
import net.sourceforge.mecat.catalog.filter.Parser;
import net.sourceforge.mecat.catalog.medium.features.BadCondition;

public class FilterDropTarget extends DropTarget {

    Transferable current_transferable = null;
    String current_condition = null;
    VisualisationNode current_visualisation = null;

    Vector<ActionListener> listeners = new Vector<ActionListener>();
    
    public void addActionListener(final ActionListener listener) {
        listeners.add(listener);
    }
    
    public void removeActionListener(final ActionListener listener) {
        listeners.remove(listener);
    }
    
    protected void fireAction(){
        for (ActionListener listener : listeners)
            listener.actionPerformed(null);
    }
    
    
    public void setVisualisationNode(final VisualisationNode node) {
        current_visualisation = node;
    }
    
    
    public void setCondition(final String condition) {
        // If the current and the new condition both are null
        // then there is no change
        if (condition == null && current_condition == null)
            return;
        
        if (condition == null) {
            current_condition = null;
            setVisualisationNode(null);
            return;
        }

        // The condition is the same
        if (current_condition != null && condition.equals(current_condition))
            return;

        current_condition = condition;
        
        Parser parser = new Parser(condition);
        try {
            Filter filter = parser.parse();
            setVisualisationNode(new VisualisationNode(filter));
        } catch (BadCondition e) {
            // We got something that is no filter 
            // could be some gives us a wrong one
            // or some only gives us text from
            // another application.
            // In both cases we have no 
            // droped filter.
            setVisualisationNode(null);
        }
    }
    
    public void setTransferable(final Transferable transferable){
        if (current_transferable == transferable)
            return;
        try {
            current_transferable = transferable;
            if (current_transferable == null || !transferable.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                setCondition(null);
                return;
            }
                
            setCondition( (String) transferable.getTransferData(DataFlavor.stringFlavor) );
            
        } catch (UnsupportedFlavorException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    {
        try {
            addDropTargetListener(new DropTargetListener(){
                public void dragEnter(DropTargetDragEvent event) {
                    setTransferable(event.getTransferable());
                }
                
                public void dragOver(DropTargetDragEvent event) {
                    setTransferable(event.getTransferable());
                }
                
                public void dropActionChanged(DropTargetDragEvent arg0) {
                    // TODO Auto-generated method stub
                    
                }
                
                public void dragExit(DropTargetEvent arg0) {
                    setTransferable(null);
                }
                
                public void drop(DropTargetDropEvent event) {
                    setTransferable(event.getTransferable());
                    fireAction();
                }});
        } catch (HeadlessException e) {
            e.printStackTrace();
        } catch (TooManyListenersException e) {
            e.printStackTrace();
        }
    }
}
