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
 * Created on Sep 2, 2005
 * @author Stephan Richard Palm
 * 
 */
package net.sourceforge.mecat.catalog.filter.design;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Vector;

import javax.swing.JComponent;

import net.sourceforge.mecat.catalog.filter.AndFilter;
import net.sourceforge.mecat.catalog.filter.EntryFilter;
import net.sourceforge.mecat.catalog.filter.Filter;
import net.sourceforge.mecat.catalog.filter.FilterListener;
import net.sourceforge.mecat.catalog.filter.FilterListenerEvent;
import net.sourceforge.mecat.catalog.filter.NotFilter;
import net.sourceforge.mecat.catalog.filter.OrFilter;
import net.sourceforge.mecat.catalog.filter.FilterListenerEvent.FilterListenerEventType;

public class VisualisationNode implements List<VisualisationNode>, VisualisationNodeListener, FilterListener{

    final static int vSpace = 10;
    final static int hSpace = 10;
    
    JComponent component;
    Filter filter;
    
    final Vector<VisualisationNode> subs = new Vector<VisualisationNode>();
    RowParams rows[] = null;
    
    // This flag is set true if the cached depth is valid
    boolean depth_valid = false;
    int depth = -1;
    
    // This flag is set true if the cached depth is valid
    boolean minX_valid = false;
    int minX = -1;
    
    // This flag is set true if the cached depth is valid
    boolean maxX_valid = false;
    int maxX = -1;
    
    // This flag is set true if the cached depth is valid
    boolean height_valid = false;
    int height = -1;
    
    // This flag is set true if the cached postitions are valid
    boolean positions_valid = false;
    int positions[] = null;
    
    // This flag is set true if the cached postitions are valid
    boolean every_position_valid = false;
    Map<Rectangle, VisualisationNode> every_position = new LinkedHashMap<Rectangle, VisualisationNode>();
    Map<VisualisationNode, Rectangle> every_position_back = new LinkedHashMap<VisualisationNode, Rectangle>();
    
//    // This flag is set true if the cached postitions are valid
//    boolean parents_valid = false;
//    Map<VisualisationNode, VisualisationNode> parents = new LinkedHashMap<VisualisationNode, VisualisationNode>();
    final VisualisationNode parent;
    
    public Rectangle getPositionOf(final VisualisationNode node) {
        return every_position_back.get(node);
    }
    
    
    public VisualisationNode(final Filter filter) {
        this(filter, null);
    }
    public VisualisationNode(final Filter filter, final VisualisationNode parent) {
        this.parent = parent;
        setFilter(filter);
    }

    public int size() {
        return subs.size();
    }

    public boolean isEmpty() {
        return subs.isEmpty();
    }

    public boolean contains(Object arg0) {
        return subs.contains(arg0);
    }

    public Iterator<VisualisationNode> iterator() {
        return new Iterator<VisualisationNode>(){
            Iterator<VisualisationNode> org = subs.iterator();
            VisualisationNode node = null;
            
            public boolean hasNext() {
                return org.hasNext();
            }

            public VisualisationNode next() {
                return node = org.next();
            }

            public void remove() {
                org.remove();
                node.removeVisualisationNodeListener(VisualisationNode.this);
                fireChange();
            }
        };
    }

    Vector<VisualisationNodeListener> visualisationNodeListeners = new Vector<VisualisationNodeListener>();
    
    public void addVisualisationNodeListener(final VisualisationNodeListener visualisationNodeListener) {
        visualisationNodeListeners.add(visualisationNodeListener);
    }
    
    public void removeVisualisationNodeListener(final VisualisationNodeListener visualisationNodeListener) {
        visualisationNodeListeners.remove(visualisationNodeListener);
    }
    
    protected void fireChange() {
//        rebuild();
        for (VisualisationNodeListener visualisationNodeListener : visualisationNodeListeners)
            visualisationNodeListener.changed();
    }

    /**
     * Returns the depth of the VisualisationTree from this node.
     * @return 1 if it has no children else the max depth of the children + 1
     */
    protected int depth() {
        if (depth_valid)
            return depth;

        int max = 0;
        for (VisualisationNode node : subs) {
            int depth = node.depth();
            if (depth > max)
                max = depth;
        }
        depth = max + 1;
        depth_valid = true;
        
        return depth;
    }

    protected int getMinX() {
        if (minX_valid)
            return minX;

        int min = 0;
        for (RowParams row : rows) 
            if (row.getX1() < min)
                min = row.getX1();

        minX = min;
        minX_valid = true;
        
        return minX;
    }
    
    protected int getMaxX() {
        if (maxX_valid)
            return maxX;

        int max = 0;
        for (RowParams row : rows) 
            if (row.getX2() > max)
                max = row.getX2();

        maxX = max;
        maxX_valid = true;
        
        return maxX;
    }
    
    protected int getWidth() {
        return getMaxX() - getMinX() + 2 * hSpace;
    }
    
    protected int getHeight() {
        if (height_valid)
            return height;

        int h = vSpace;
        for (RowParams row : rows) 
            h += row.getY() + vSpace;

        height = h;
        height_valid = true;
        
        return height;
    }
    
    public Dimension getPreferredSize() {
        return new Dimension(getWidth(), getHeight());
    }
    
    /**
     * Rebuild does not fires a change.
     * If you want to inform about a change
     * you have to do so manually.
     * Why? Because this way there is more controll 
     * over the situation. Say, we now allready that the
     * parent will be informed about the change, if we would
     * inform him to that could potentially lead to quadratic 
     * time consume.
     *
     */
    protected void rebuild() {
        minX_valid = false;
        maxX_valid = false;
        height_valid = false;
        depth_valid = false;
        positions_valid = false;
        every_position_valid = false;
//        parents_valid = false;
        
        rows = new RowParams[depth()];
        positions = new int[subs.size()];
        
        int pos = 0;
        int max_depth = 0;
        
        for (int i = 0; i < rows.length; i++)
            rows[i] = new RowParams(0,0,0);

        for (int j = 0; j < subs.size(); j++){
            VisualisationNode node = subs.get(j);
            int minDist = hSpace;
            int dist;
            for (int i = 0; (i < rows.length - 1) && (i < node.rows.length); i++) {
                dist = rows[i + 1].getX2() - node.rows[i].getX1() + hSpace;
                if (dist > minDist)
                    minDist = dist;
            }
            pos = minDist;
            for (int i = 0; (i < rows.length - 1) && (i < node.rows.length); i++) {
                rows[i + 1].setX2(node.rows[i].getX2() + pos);
                if (i >= max_depth) {
                    rows[i + 1].setX1(node.rows[i].getX1() + pos);
                    max_depth++;
                }
                if (node.rows[i].getY() > rows[i + 1].getY())
                    rows[i + 1].setY(node.rows[i].getY());
            }
            positions[j] = pos;
        }
        int position_this = 0;
        if (positions.length > 0)
            position_this = (positions[0] + positions[positions.length - 1]) / 2;
        for (int i = 1; i < rows.length; i++) {
            rows[i].setX1(rows[i].getX1() - position_this);
            rows[i].setX2(rows[i].getX2() - position_this);
        }
        for (int j = 0; j < subs.size(); j++)
            positions[j] -= position_this;
        rebuildRow0();
        
        positions_valid = true;
    }

    private void rebuildRow0() {
        rows[0].setX1(-component.getPreferredSize().width / 2);
        rows[0].setX2(component.getPreferredSize().width / 2);
        rows[0].setY(component.getPreferredSize().height);
    }

    public Object[] toArray() {
        return subs.toArray();
    }

    public <T> T[] toArray(T[] arg0) {
        return subs.toArray(arg0);
    }

    public boolean add(VisualisationNode arg0) {
        boolean ret = subs.add(arg0);
        arg0.addVisualisationNodeListener(this);
        fireChange();
        return ret;
    }

    public boolean remove(Object arg0) {
        boolean ret = subs.remove(arg0);
        if (arg0 instanceof VisualisationNode)
            ((VisualisationNode)arg0).removeVisualisationNodeListener(this);
        fireChange();
        return ret;
    }

    public boolean containsAll(Collection< ? > arg0) {
        return subs.containsAll(arg0);
    }

    public boolean addAll(Collection< ? extends VisualisationNode> arg0) {
        boolean ret = subs.addAll(arg0);
        for (VisualisationNode node : arg0)
            node.addVisualisationNodeListener(this);
        fireChange();
        return ret;
    }

    public boolean addAll(int arg0, Collection< ? extends VisualisationNode> arg1) {
        boolean ret = subs.addAll(arg0, arg1);
        for (VisualisationNode node : arg1)
            node.addVisualisationNodeListener(this);
        fireChange();
        return ret;
    }

    public boolean removeAll(Collection< ? > arg0) {
        boolean ret = subs.removeAll(arg0);
        for (Object node : arg0)
            if (node instanceof VisualisationNode)
                ((VisualisationNode)node).removeVisualisationNodeListener(this);
        fireChange();
        return ret;
    }

    public boolean retainAll(Collection< ? > arg0) {
        for (VisualisationNode node : subs)
            node.removeVisualisationNodeListener(this);
        boolean ret = subs.retainAll(arg0);
        for (VisualisationNode node : subs)
            node.addVisualisationNodeListener(this);
        fireChange();
        return ret;
    }

    public void clear() {
        for (VisualisationNode node : subs)
            node.removeVisualisationNodeListener(this);
        subs.clear();
        fireChange();
    }

    public VisualisationNode get(int arg0) {
        return subs.get(arg0);
    }

    public VisualisationNode set(int arg0, VisualisationNode arg1) {
        subs.get(arg0).removeVisualisationNodeListener(this);
        VisualisationNode ret = subs.set(arg0, arg1);
        arg1.addVisualisationNodeListener(this);
        fireChange();
        return ret;
    }

    public void add(int arg0, VisualisationNode arg1) {
        subs.add(arg0, arg1);
        arg1.addVisualisationNodeListener(this);
        fireChange();
    }

    public VisualisationNode remove(int arg0) {
        subs.get(arg0).removeVisualisationNodeListener(this);
        VisualisationNode ret = subs.remove(arg0);
        fireChange();
        return ret;
    }

    public int indexOf(Object arg0) {
        int ret = subs.indexOf(arg0);
        fireChange();
        return ret;
    }

    public int lastIndexOf(Object arg0) {
        int ret = subs.lastIndexOf(arg0);
        fireChange();
        return ret;
    }

    public ListIterator<VisualisationNode> listIterator() {
        return new VisualisationNodeListIterator(subs.listIterator(), this);
    }

    public ListIterator<VisualisationNode> listIterator(int arg0) {
        return new VisualisationNodeListIterator(subs.listIterator(arg0), this);
    }

    public List<VisualisationNode> subList(int arg0, int arg1) {
        return subs.subList(arg0, arg1);
    }

    public void changed() {
        rebuild();
        fireChange();
    }

    
    
    public VisualisationNode getVisualisationNodeForPosition(final int x, final int y) {
        if (!every_position_valid)
            calculate_positions();
        for (Map.Entry<Rectangle, VisualisationNode> entry : every_position.entrySet()) {
            if (x > entry.getKey().getMinX() && x < entry.getKey().getMaxX() && y > entry.getKey().getMinY() && y < entry.getKey().getMaxY())
                return entry.getValue();
        }
        return null;
    }
    
    
    public void calculate_positions() {
        every_position.clear();
        every_position_back.clear();
        calculate_positions(every_position, -getMinX() + hSpace, vSpace, rows, 0);
        for (Map.Entry<Rectangle, VisualisationNode> entry : every_position.entrySet()) 
            every_position_back.put(entry.getValue(), entry.getKey());
        every_position_valid = true;
    }

    private void calculate_positions(final Map<Rectangle, VisualisationNode> every_pos, final int posX, final int posY, final RowParams[] rowParams, final int off) {
        int row_Height = rowParams[off].getY();
        int row_Y_off = (row_Height - component.getPreferredSize().height) / 2;
        
        for (int j = 0; j < subs.size(); j++)
            subs.get(j).calculate_positions(every_pos, posX + positions[j], posY + row_Height + vSpace, rowParams, off + 1);

        every_pos.put(new Rectangle(posX + rows[0].getX1(), posY + row_Y_off, component.getPreferredSize().width, component.getPreferredSize().height), this);
    }
    
    
    public void paint(final Graphics g) {
        // Center to the position used by the variables produced by rebuild
        g.translate(-getMinX() + hSpace, vSpace);
        // paint everything
//        paint(g, rows, 0);
        paintDirect(g);
        // set the original position
        g.translate(getMinX() - hSpace, -vSpace);
    }

    /**
     * Paints on the Position (0,0) most of the time this is not wanted
     * so use paint instead
     * @param g
     */
    public void paintDirect(final Graphics g) {
        // paint everything
        paint(g, rows, 0);
    }

    private void paint(final Graphics g, final RowParams[] rowParams, final int off) {
        int row_Height = rowParams[off].getY();
        int row_Y_off = (row_Height - component.getPreferredSize().height) / 2;
        // Move to the left start of this component

        for (int j = 0; j < subs.size(); j++){
            // Draw line to show connection from parent to child nodes
            g.drawLine(0,row_Height/2,positions[j],row_Height + vSpace + rowParams[off + 1].getY()/2);
            // Go to the X coordinate of the subelement
            g.translate(positions[j], row_Height + vSpace);
            // Paint the subelement
            subs.get(j).paint(g, rowParams, off + 1);
            // Go to the original X coordinate
            g.translate(-positions[j], - (row_Height + vSpace));
        }

        g.translate(rows[0].getX1(), row_Y_off);
        // Paint this component
        g.setColor(Color.WHITE);
        g.fillRect(0,0, component.getPreferredSize().width, component.getPreferredSize().height);
        g.setColor(Color.BLACK);
        g.drawRect(-1,-1, component.getPreferredSize().width + 1, component.getPreferredSize().height + 1);

        component.setSize(component.getPreferredSize());
        component.paint(g);
        // Set to the starting position
        g.translate(-rows[0].getX1(), -row_Y_off);
    }


    /**
     * Durch die benutzung dieser Funktion kann 
     * die Struktur eines Filters gebaut werden 
     * ohne das der Orginalfilter dadurch geaendert wird.
     * D.h. am Ende muss aber der Filter neu gebaut werden damit er wieder funktioniert.
     * 
     * Dieses Verhalten wird noch geaendert.
     * Der uebergeordnete Filter wird dann direkt angepasst.
     * Das verhindert merkwuerdige Ergebnisse.
     * 
     * @param filter
     */
    public void setFilter(final Filter filter) {
        if (this.filter != null)
            this.filter.removeFilterListener(this);
        if (filter != null)
            filter.addFilterListener(this);
        
        for (VisualisationNode node : subs)
            node.removeVisualisationNodeListener(this);
        subs.clear();
        
        
        this.filter = filter;
        while (this.filter.getClass().equals(EntryFilter.class)) {
            EntryFilter entryFilter = (EntryFilter) this.filter;
            this.filter = entryFilter.getFilter();
        }
        if (this.filter.getClass().equals(AndFilter.class)) {
            AndFilter andFilter = (AndFilter) this.filter;
            subs.add(new VisualisationNode(andFilter.getLeft(), this));
            subs.add(new VisualisationNode(andFilter.getRight(), this));
            // After delibratly avoiding a rebuild the two lines before
            // we now manually trigger the rebuild, this way only
            // one rebuild is done
        }
        if (this.filter.getClass().equals(OrFilter.class)) {
            OrFilter orFilter = (OrFilter) this.filter;
            subs.add(new VisualisationNode(orFilter.getLeft(), this));
            subs.add(new VisualisationNode(orFilter.getRight(), this));
            // After delibratly avoiding a rebuild the two lines before
            // we now manually trigger the rebuild, this way only
            // one rebuild is done
        }
        if (this.filter.getClass().equals(NotFilter.class)) {
            NotFilter notFilter = (NotFilter) this.filter;
            subs.add(new VisualisationNode(notFilter.getFilter(), this));
            // After delibratly avoiding a rebuild the line before
            // we now manually trigger the rebuild, this way only
            // one rebuild is done
        }
        this.component = filter.visualisation();
        for (VisualisationNode node : subs)
            node.addVisualisationNodeListener(this);
        rebuild();
        fireChange();
    }


    public Filter getFilter() {
        return filter;
    }


    public void changed(FilterListenerEvent event) {
        // We are only interested if it comes directly from
        // our filter otherwise our subs will inform us 
        // when the time is rigth
        if (!event.getSource().equals(filter))
            return;
        if (event.getType() == FilterListenerEventType.VALUE_CHANGED) {
            // Only the first of all row has to be rebuild
            rebuildRow0();
            // but some values are now corrupt too
            // the sizes have changed and the y-positions changed too
            // the x-positions, the parents and the depth don't change
            minX_valid = false;
            maxX_valid = false;
            height_valid = false;
            every_position_valid = false;
            
            fireChange();
            // We are done no need to go any further
            return;
        }
        if (event.getType() == FilterListenerEventType.STRUCTURE_CHANGED) {
            if (this.filter.getClass().equals(AndFilter.class)) {
                AndFilter andFilter = (AndFilter) this.filter;
                if (!subs.get(0).getFilter().equals(andFilter.getLeft())) {
                    subs.get(0).setFilter(andFilter.getLeft());
                    // We are done no need to go any further
                    return;
                }
                if (!subs.get(1).getFilter().equals(andFilter.getRight())) {
                    subs.get(1).setFilter(andFilter.getRight());
                    // We are done no need to go any further
                    return;
                }
            }
            if (this.filter.getClass().equals(OrFilter.class)) {
                OrFilter orFilter = (OrFilter) this.filter;
                if (!subs.get(0).getFilter().equals(orFilter.getLeft())) {
                    subs.get(0).setFilter(orFilter.getLeft());
                    // We are done no need to go any further
                    return;
                }
                if (!subs.get(1).getFilter().equals(orFilter.getRight())) {
                    subs.get(1).setFilter(orFilter.getRight());
                    // We are done no need to go any further
                    return;
                }
            }
            if (this.filter.getClass().equals(NotFilter.class)) {
                NotFilter notFilter = (NotFilter) this.filter;
                subs.get(0).setFilter(notFilter.getFilter());
                // We are done no need to go any further
                return;
            }
        }
    }


    public void exchangeFilter(final VisualisationNode sel, final Filter newFilter) {
        if (sel == this) {
            setFilter(newFilter);
            return;
        }
        VisualisationNode parent = sel.parent;
        if (parent.getFilter().getClass().equals(AndFilter.class)) {
            AndFilter andFilter = (AndFilter) parent.getFilter();
            if (parent.subs.get(0) == sel)
                andFilter.setLeft(newFilter);
            if (parent.subs.get(1) == sel)
                andFilter.setRight(newFilter);
        }
        if (parent.getFilter().getClass().equals(OrFilter.class)) {
            OrFilter orFilter = (OrFilter) parent.getFilter();
            if (parent.subs.get(0) == sel)
                orFilter.setLeft(newFilter);
            if (parent.subs.get(1) == sel)
                orFilter.setRight(newFilter);
        }
        if (parent.getFilter().getClass().equals(NotFilter.class)) {
            NotFilter notFilter = (NotFilter) parent.getFilter();
            notFilter.setFilter(newFilter);
        }
    }


   
}
