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
 * Created on Sep 21, 2005
 * @author Stephan Richard Palm
 * 
 */
package net.sourceforge.mecat.catalog.gui.features.desktop.image;

import static java.lang.Math.max;
import static java.lang.Math.min;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import net.sourceforge.mecat.catalog.medium.Listing;
import net.sourceforge.mecat.catalog.medium.Medium;
import net.sourceforge.mecat.catalog.medium.features.impl.Ident;
import net.sourceforge.mecat.catalog.medium.features.impl.Location;
import net.sourceforge.mecat.catalog.medium.features.impl.Position;
import net.sourceforge.mecat.catalog.medium.features.impl.Title;
import net.sourceforge.mecat.catalog.option.Options;
import net.sourceforge.mecat.catalog.sort.Comparing;

public class ImageDesktopCanvas extends JPanel implements ListDataListener {

    ResourceBundle res = Options.getI18N(ImageDesktopCanvas.class);
    final Vector<MediumFocusListener> mediumFocusListeners = new Vector<MediumFocusListener>();
    
    public void addMediumFocusListener(final MediumFocusListener mediumFocusListener) {
        mediumFocusListeners.add(mediumFocusListener);
    }
    
    public void removeMediumFocusListener(final MediumFocusListener mediumFocusListener) {
        mediumFocusListeners.remove(mediumFocusListener);
    }
    
    public void fireFocusChanged(final Medium medium) {
        for (MediumFocusListener mediumFocusListener : mediumFocusListeners)
            mediumFocusListener.FocusChanged(medium);
    }
    
    Medium currentFocus = null;
    
    public void setCurrentFocus(final Medium medium) {
        if (medium != currentFocus)
            fireFocusChanged(medium);
        currentFocus = medium;
    }
    
    
    private class InsertPosition{
        final String location;
        final int position;
        final Rectangle rectangle;
        // before this medium
        final Medium before;
        
        public InsertPosition(final String location, final int position, final Rectangle rectangle, final Medium before) {
            this.location = location;
            this.position = position;
            this.rectangle = rectangle;
            this.before = before;
        }

        public String getLocation() {
            return location;
        }

        public int getPosition() {
            return position;
        }
        
        public Rectangle getRectangle() {
            return rectangle;
        }
        
        public Medium getBefore() {
            return before;
        }
    }
    
    
    private static final int SPACE_X = 7;
    private static final int SPACE_Y = SPACE_X;

    final Listing listing;
    
    // For every location this holds a sorted list of all containted media
    final Map<String, SortedSet<Medium>> rows = new HashMap<String, SortedSet<Medium>>();
    // For every location this a image is stored of the complete rom
    final Map<String, Image> rowImages = new HashMap<String, Image>();
    // For every row the exact position is stored within this mapping
    final Map<String, Rectangle> rowPositions = new HashMap<String, Rectangle>();
    // For every Medium we store it exact position relativ to the row 
    final Map<Medium, Rectangle> mediumPositions = new HashMap<Medium, Rectangle>();
    // A list of all current loctions
    final SortedSet<String> locations = new TreeSet<String>();
    // Image of all media, used for faster drawing
    Image media = null;
    // Image of all media and the current selection
    Image selectionImage = null;
    
    // Memory buffer for images
    Image memoryAll = null;
    
    
    // The list of the current selected media in the Image Desktop Canvas
    final Vector<Medium> selection = new Vector<Medium>();
 
    /* 
     * Comparator for the media in one row
     * Sorts for the position then for the title and
     * at the end for the Ident Number. This way
     * the media are sorted for Position and not 
     * thrown out if the have the same position.
     */    
    final Comparator<Medium> rowInternComparator = new Comparing(){{
        add(Position.getComparator());
        add(Title.getComparator());
        add(Ident.getComparator());
    }};
    
    final Comparator<Medium> positionComparator = Position.getComparator();
    
    /*
     * This flag is set to true if
     * a medium has changed enough 
     * for a full recalculation of 
     * the shown contents
     */
    protected boolean hasChanged = false;
    /*
     * Has the value -1 if the height is currently
     * uncalculated else containts the height of this canvas
     */
    protected int height = -1;
    /*
     * Has the value -1 if the width is currently
     * uncalculated else containts the width of this canvas
     */
    protected int width = -1;
    

    /*
     * Backup list for the listing.
     * This allows better understanding of
     * the incoming events. 
     */
    final Vector<Medium> bakup;

    

    protected void clearBuffer() {
        height = -1;
        width = -1;
        media = null;
        selectionImage = null;
        memoryAll = null;
    }
    
    protected void setChanged(final boolean changed) {
        if (changed) {
            clearBuffer();
            revalidate();
            repaint();
        }
        hasChanged = changed;
    }
    
    public boolean hasChanged() {
        return hasChanged;
    }
    

    Point currentMousePosition = new Point(0,0);
    Point dragStart = null;
    Rectangle dragRect = null;
    // A list of all Boxes for visual representation of the mousepointer
    // while dragging a selection
    Vector<Rectangle> mousePointerRects = null;
    InsertPosition insertPosition = null;
    
    protected void calcDragRect() {
        if (dragStart != null) {
            int x = max(min(dragStart.x, currentMousePosition.x), 0);
            int y = max(min(dragStart.y, currentMousePosition.y), 0);

            int X = min(max(dragStart.x, currentMousePosition.x), getWidth() - 1);
            int Y = min(max(dragStart.y, currentMousePosition.y), getHeight() - 1);

            int w = X - x;
            int h = Y - y;

            if (w > 2 || h > 2) {
                dragRect = new Rectangle(x,y,w,h);
            }
        }
    }
    
    public ImageDesktopCanvas(final Listing listing) {
        this.listing = listing;
        bakup = new Vector<Medium>();
        contentsChanged(null);
        this.addMouseListener(new MouseListener(){
            public void mouseClicked(MouseEvent e) {
                Medium medium = mediumAtPoint(e.getPoint());
                if (medium != null) {
                    selection.clear();
                    selection.add(medium);
                    selectionImage = null;
                }
            }

            public void mousePressed(MouseEvent e) {
                // Look if a medium is hit
                Medium medium = mediumAtPoint(e.getPoint());
                
                // If there is no medium or the medium is no
                // part of the current selection then 
                // make a new selection
                if (medium == null || !selection.contains(medium)) {
                    dragStart = e.getPoint();
                    return;
                }
                
                mousePointerRects = new Vector<Rectangle>();
                for (Medium med : selection) {
                    // Find the location for the medium
                    Location loc = med.getFeature(Location.class);
                    String l = loc.get();
                    
                    Rectangle rowPos = rowPositions.get(l);
                    Rectangle medPos = new Rectangle(mediumPositions.get(med));

                    // Get the absolut coordinates
                    medPos.translate(rowPos.x, rowPos.y);
                    // Make the mouse point the (0,0) coordinate
                    medPos.translate(-e.getPoint().x, -e.getPoint().y);
                    
                    mousePointerRects.add(medPos);
                }
            }

            public void mouseReleased(MouseEvent e) {
                if (dragRect != null)
                    selectRectangle(dragRect);
                if (mousePointerRects != null)
                    dropSelection(insertPosition);
                
                dragStart = null;
                dragRect = null;
                
                mousePointerRects = null;
                insertPosition = null;
                
                repaint();
            }

            public void mouseEntered(MouseEvent e) {
            }

            public void mouseExited(MouseEvent e) {
            }
        });
        this.addMouseMotionListener(new MouseMotionListener(){

            public void mouseDragged(MouseEvent e) {
                currentMousePosition = e.getPoint();
                if (mousePointerRects != null)
                    insertPosition = getInsertForPoint(e.getPoint());
                if (dragStart != null)
                    calcDragRect();
                repaint();
            }

            public void mouseMoved(MouseEvent e) {
                currentMousePosition = e.getPoint();
                setCurrentFocus(mediumAtPoint(e.getPoint()));
            }
            
        });
    }

    protected void dropSelection(final InsertPosition insertPosition) {
        if (insertPosition == null)
            return;
        
        
        SortedSet<Medium> move = new TreeSet<Medium>(rowInternComparator);
        move.addAll(selection);

        if  (move.isEmpty()) {
            JOptionPane.showMessageDialog(ImageDesktopCanvas.this, res.getString("Nothing to drop."), res.getString("Drop Error"), JOptionPane.ERROR_MESSAGE);
            return;
        }
        SortedSet<String> locs = new TreeSet<String>();
        SortedSet<Integer> poss = new TreeSet<Integer>();
        boolean hasNull = false;
        for (Medium medium : move) {
            locs.add(medium.getFeature(Location.class).get());
            if (medium.getFeature(Position.class).getInt() == 0)
                hasNull = true;
            else
                poss.add(medium.getFeature(Position.class).getInt());
        }
        if (locs.size() > 1) {
            JOptionPane.showMessageDialog(ImageDesktopCanvas.this, res.getString("Can not move elements from more than one line."), res.getString("Drop Error"), JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (hasNull)
            poss.add(poss.first() - 1);

        
        
        
        // Original row
        SortedSet<Medium> row = rows.get(insertPosition.getLocation());
        
        // Row without those elements moved (in case the elements come from the same row)
        SortedSet<Medium> rowCut = new TreeSet<Medium>(row);
        rowCut.removeAll(move);

        Medium rowCutArray[] = new Medium[rowCut.size()];
        rowCut.toArray(rowCutArray);
        

        
        
        // List of those elements after the insertion point
        SortedSet<Medium> tail;

        // Get all media after the insertion point
        // because those have to be moved
        
        // if there is no element after the the insertPosition
        // the tail is empty
        if (insertPosition.getBefore() == null)
            tail = new TreeSet<Medium>();
        else 
            tail = rowCut.tailSet(insertPosition.getBefore());
  
        
        
        

        
        int position = 0;
        
        // If it is not insert at the first position then 
        // get the element right before and add one to his position
        if (tail.size() < rowCut.size()) {
            // Get the element before the insertion  
            Medium prev = rowCutArray[rowCut.size() - tail.size() - 1];
            Integer prevPos = prev.getFeature(Position.class).getInt();
            // If the previous position is null 
            // then the position 0 is allready good
            if (prevPos != null)
                position = prevPos + 1;
            
        }

        Map<Medium, Integer> mapToNewPositions = new HashMap<Medium, Integer>();
        
        
        while (!move.isEmpty()) {
            // Get the position for the first element in the remaining elements to move
            Integer currentPositionInMove = move.first().getFeature(Position.class).getInt();
            // Get all elements to move with this number
            while (!move.isEmpty() && move.first().getFeature(Position.class).getInt() == currentPositionInMove) {
                mapToNewPositions.put(move.first(), position);
                move.remove(move.first());
            }            
            position++;
        }
        

        
        Vector<Medium> tailCopy = new Vector<Medium>(tail);
        while (!tailCopy.isEmpty()) {
            // Get the position for the first element in the remaining elements to move
            Integer currentPositionInTail = tailCopy.firstElement().getFeature(Position.class).getInt();
            // Get all elements to move with this number
            while (!tailCopy.isEmpty() && tailCopy.firstElement().getFeature(Position.class).getInt() == currentPositionInTail) {
                mapToNewPositions.put(tailCopy.firstElement(), position);
                tailCopy.remove(0);
            }            
            position++;
        }

        
        // Move everyone
        SortedSet<String> dirtyRows = new TreeSet<String>();
        for (Map.Entry<Medium, Integer> entry : mapToNewPositions.entrySet()) {
            Location location = entry.getKey().getFeature(Location.class);
            // Mark rows that have changed
            dirtyRows.add(location.get());
            // Move elements from old to the new location
            if (location.get().equals(insertPosition.getLocation())) {
                // Remove the medium from the old row
                rows.get(location.get()).remove(entry.getKey());
                // if the row now is empty then 
                if (rows.get(location.get()).isEmpty()) {
                    // don't rebuild it
                    dirtyRows.remove(location.get());
                    // remove it from the list of activ locations/rows
                    rows.remove(location.get());
                    locations.remove(location.get());
                }
                
                // Change the location in the intern representation
                location.set(insertPosition.getLocation());
                // Add the medium to the new row
                row.add(entry.getKey());
            }
            // at the new position
            entry.getKey().getFeature(Position.class).set(entry.getValue());
        }
        
        // Make sure the row where the media have been inserted
        // is well ordered again.
        SortedSet<Medium> tmp = new TreeSet<Medium>(row.comparator());
        tmp.addAll(new Vector<Medium>(row));
        rows.put(insertPosition.getLocation(), tmp);
        
        for (String l : dirtyRows) {
            rebuildRow(l);
        }

        setChanged(true);
    }

    protected String rowAtPoint(final Point point) {
        for (String location : locations) {
            Rectangle rowPos = rowPositions.get(location);
            if (!((rowPos.y < point.y) && (rowPos.y + rowPos.height > point.y)))
                continue;
            
            return location;
        }
        return null;
    }
    
    protected Medium mediumAtPoint(final Point point) {
        for (String location : locations) {
            Rectangle rowPos = rowPositions.get(location);
            if (!((rowPos.y < point.y) && (rowPos.y + rowPos.height > point.y)))
                continue;
            for (Medium medium : rows.get(location)) {
                Rectangle medPos = new Rectangle(mediumPositions.get(medium));
                medPos.translate(rowPos.x, rowPos.y);
                if (!((medPos.x < point.x) && (medPos.x + medPos.width > point.x)))
                    continue;
                if ((medPos.y < point.y) && (medPos.y + medPos.height > point.y))
                    return medium;
                // By reaching this position there is no hope left
                return null;
            }
            // By reaching this position there is no hope left
            return null;
            
        }
        return null;
        
    }
    
    protected void selectRectangle(final Rectangle rectangle) {
        selection.clear();
        for (String location : locations) {
            Rectangle rowPos = rowPositions.get(location);
            if ((rowPos.y + rowPos.height > rectangle.y) && (rowPos.y < rectangle.y + rectangle.height)) {
                for (Medium medium : rows.get(location)) {
                    Rectangle medPos = mediumPositions.get(medium);
                    int x1 = medPos.x + rowPos.x;
                    int x2 = x1 + medPos.width;
                    if ((rectangle.x + rectangle.width > x1) && (rectangle.x < x2))
                        selection.add(medium);
                }
            }
        }
        selectionImage = null;
    }
    
    public void intervalAdded(ListDataEvent e) {
        int start = e.getIndex0();
        int end = e.getIndex1();
        
        Set<String> dirtyRows = new HashSet<String>();
        
        for (int i = start; i <= end; i++) {
            Medium medium = listing.getElementAt(i);
            // Make Bakup list up to date
            bakup.add(i, medium);
            
            // Only consider those who pass this test
            if (!isConsiderable(medium))
                continue;
            
            // Find the location for the medium
            Location loc = medium.getFeature(Location.class);
            String l = loc.get();
            
            // Mark the location for a redraw
            dirtyRows.add(l);
            
            // Get the rowinformation for the location
            SortedSet<Medium> row = rows.get(l);
            // If there is none then create a new one
            if (row == null) {
                row = new TreeSet<Medium>(rowInternComparator);
                rows.put(l, row);
                locations.add(l);
            }
            row.add(medium);
        }
        // Rebuild all rows that are concerned by the interval added
        for (String location : dirtyRows)
            rebuildRow(location);

        // It only is changed if any of the elements has been added to the visualisation
        if (!dirtyRows.isEmpty())
            setChanged(true);
    }

    public void intervalRemoved(ListDataEvent e) {  
        int start = e.getIndex0();
        int end = e.getIndex1();
        
        Set<String> dirtyRows = new HashSet<String>();
        
        for (int i = start; i <= end; i++) {
            Medium medium = listing.getElementAt(i);
            // Make Bakup list up to date
            bakup.remove(start);
            
            // Only consider those who pass this test
            if (!isConsiderable(medium))
                continue;
            
            // Find the location for the medium
            Location loc = medium.getFeature(Location.class);
            String l = loc.get();
            
            // Mark the location for a redraw
            dirtyRows.add(l);
            
            // Get the rowinformation for the location
            SortedSet<Medium> row = rows.get(l);
            // If there is none we have a serious problem
            if (row == null) 
                (new Exception(res.getString("Internal Error. Inconsistent state for the Image Desktop Canvas."))).printStackTrace();
            row.remove(medium);
            // If the row now is empty
            if (row.isEmpty()) {
                // remove the row
                locations.remove(l);
                rows.remove(row);
                // and don't try to repaint it
                dirtyRows.remove(l);
            }
        }
        // Rebuild all rows that are concerned by the interval added
        for (String location : dirtyRows)
            rebuildRow(location);

        setChanged(true);
    }
    
    protected boolean isConsiderable(final Medium medium){
        Location loc = medium.getFeature(Location.class);
        if (loc == null)
            return false;
        Position pos = medium.getFeature(Position.class);
        if (pos == null)
            return false;
        return true;
    }

    public void contentsChanged(ListDataEvent arg0) {
        rows.clear();
        locations.clear();

        for (Medium medium : listing) {
            // Only consider those who pass this test
            if (!isConsiderable(medium))
                continue;
            // The following two statement will not be
            // null because the previous test wouldn't pass
            Location loc = medium.getFeature(Location.class);
            Position pos = medium.getFeature(Position.class);
            
            // Find the location for the medium
            String l = loc.get();
            
            // Get the rowinformation for the location
            SortedSet<Medium> row = rows.get(l);
            // If there is none then create a new one
            if (row == null) {
                row = new TreeSet<Medium>(rowInternComparator);
                rows.put(l, row);
                locations.add(l);
            }
            row.add(medium);
        }
        for (String location : locations)
            rebuildRow(location);

        setChanged(true);
    }
    
    public void rebuildRow(final String location) {
        SortedSet<Medium> row = rows.get(location);
        int w = row.size()*(10 + SPACE_X), h = 30;
        int posX = 0;
        
        Image image = new BufferedImage(w, h + 1, BufferedImage.SCALE_DEFAULT);

        Graphics g = image.getGraphics();
        g.setColor(Color.white);
        g.fillRect(0,0, w, h + 1);

        Medium media[] = new Medium[row.size()];
        row.toArray(media);
        
        for (int i = 0; i < media.length; i++) {
            g.setColor(Color.black);
            g.drawRect(posX, 0, 10, h);
            // Store positioning information
            mediumPositions.put(media[i], new Rectangle(posX, 0, 10, h));

            if (i + 1 < media.length && positionComparator.compare(media[i], media[i + 1]) == 0) {
                g.setColor(Color.red);
                g.drawLine(posX + 10, h/2, posX + 10 + SPACE_X, h/2);
            }
            
            g.setColor(Color.green);
            g.drawRect(posX + 1, 1, 8, h - 2);
            posX += 10 + SPACE_X;
        }
        
        rowImages.put(location, image);
    }

    
    
    @Override
    public synchronized int getHeight() {
        if (this.height != -1)
            return this.height;
        int height = SPACE_Y;
        for (String location : locations) {
            Image image = rowImages.get(location);
            height += image.getHeight(null);
            height += SPACE_Y;
        }
        this.height = height;
        return height;
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(getWidth(), getHeight());
    }

    @Override
    public Dimension getSize() {
        return new Dimension(getWidth(), getHeight());
    }

    @Override
    public synchronized int getWidth() {
        if (width != -1)
            return width;
        int maxW = 0;
        for (String location : locations) {
            Image image = rowImages.get(location);
            if (image.getWidth(null) > maxW)
                maxW = image.getWidth(null); 
        }
        width = maxW + 2 * SPACE_X;
        return maxW;
    }

//    @Override
//    public void update(Graphics g) {
//        // If the contents has changed size
//        // then let the superclass make a clear
//        if (hasChanged()) 
//            super.update(g);
//        // else simply overwrite the old 
//        else
//            paint(g);
//    }

    public InsertPosition getInsertForPoint(final Point point){
        String row = rowAtPoint(point);
        Integer pos = -1;
        Rectangle medPos = new Rectangle(0,0,0,0);
        
        
        if (row != null) {
            Rectangle rowPos = rowPositions.get(row);
            for (Medium medium : rows.get(row)) {
                medPos = mediumPositions.get(medium);
                pos = medium.getFeature(Position.class).getInt();
                if (pos == null)
                    pos = -1;

                if (rowPos.x + medPos.x + (medPos.width / 2) < point.x)
                    continue;
                
                return new InsertPosition(row, pos, new Rectangle(rowPos.x + medPos.x - SPACE_X/2 - 1, rowPos.y, 1, rowPos.height), medium);
            }
            // If it is after all entries we add one to the last position
            return new InsertPosition(row, pos + 1, new Rectangle(rowPos.x + medPos.x + medPos.width + SPACE_X/2, rowPos.y, 1, rowPos.height), null);
        }
        return null;
    }
    

    
    @Override
    protected void paintComponent(Graphics g) {
//        // TODO Auto-generated method stub
//        super.paintComponent(arg0);
//    }
//
//    @Override
//    public void paint(Graphics g) {

        if (media == null) {
            media = new BufferedImage(getWidth(), getHeight(), BufferedImage.SCALE_DEFAULT);
            Graphics mg = media.getGraphics();
            mg.setColor(Color.white);
            mg.fillRect(0,0, getWidth(), getHeight());
            
            int posY = SPACE_Y;
            int maxW = 0; 
            for (String location : locations) {
                Image image = rowImages.get(location);
                // Store positioning information
                rowPositions.put(location, new Rectangle(SPACE_X, posY, image.getWidth(null), image.getHeight(null)));

                mg.drawImage(image, SPACE_X, posY, null, null);
                posY += image.getHeight(null);
                posY += SPACE_Y;

            }
        }
        
        Image sel = media;
        if (!selection.isEmpty()) {
            if (selectionImage == null) {
                selectionImage = new BufferedImage(getWidth(), getHeight(), BufferedImage.SCALE_DEFAULT);
                Graphics sg = selectionImage.getGraphics();
                sg.drawImage(media, 0, 0, null, null);
                for (Medium medium : selection) {
                    
                    // Find the location for the medium
                    Location loc = medium.getFeature(Location.class);
                    String l = loc.get();
                    
                    Rectangle pos = new Rectangle(mediumPositions.get(medium));
                    Rectangle rowPos = rowPositions.get(l);
                    pos.translate(rowPos.x, rowPos.y);
                    
                    sg.setColor(Color.blue);
                    sg.drawRect(pos.x - 1, pos.y - 1, pos.width + 2, pos.height + 2);
//                    sg.drawRect(pos.x - 2, pos.y - 2, pos.width + 4, pos.height + 4);
                }
            }
            sel = selectionImage;
        }

        Image all = sel;

        // If the current visualisation needs to show more then 
        // the selection a new Image is created as buffer.
        if (dragRect != null || mousePointerRects != null) {
            if (memoryAll == null)
                memoryAll = new BufferedImage(getWidth(), getHeight(), BufferedImage.SCALE_DEFAULT);
            all = memoryAll;
            Graphics ag = all.getGraphics();
            ag.drawImage(sel, 0, 0, null, null);
        }

        // Draw the selection rectangle if a new selection will be made
        if (dragRect != null) {
            Graphics ag = all.getGraphics();
            ag.setColor(Color.blue);
            ag.drawRect(dragRect.x, dragRect.y, dragRect.width, dragRect.height);
            ag.drawRect(dragRect.x + 1, dragRect.y + 1, dragRect.width - 2, dragRect.height - 2);
        }
        // Draw all selected medium as gray boxes around the mousepointer 
        // this allow the user to see, that he moves the selection
        if (mousePointerRects != null) {
            Graphics ag = all.getGraphics();
            ag.setColor(Color.gray);
            for (Rectangle rect : mousePointerRects)
                ag.drawRect(rect.x + currentMousePosition.x, rect.y + currentMousePosition.y, rect.width, rect.height);

//            Integer row = rowAtPoint(currentMousePosition);
//            if (row != null) {
//                Rectangle rowPos = rowPositions.get(row);
//                for (Medium medium : rows.get(row)) {
//                    Rectangle medPos = mediumPositions.get(medium);
//                    if (rowPos.x + medPos.x + (medPos.width / 2) < currentMousePosition.x)
//                        continue;
//                    ag.setColor(Color.red);
//                    ag.drawRect(rowPos.x + medPos.x - SPACE_X/2 - 1, rowPos.y, 1, rowPos.height);
//                    break;
//                }
//            }
            if (insertPosition != null) {
                Rectangle rect = insertPosition.getRectangle();
                ag.setColor(Color.red);
                ag.drawRect(rect.x, rect.y, rect.width, rect.height);
            }
            
        }
        
        g.drawImage(all, 0,0, null, null);
        

        setChanged(false);
    }
    

}
