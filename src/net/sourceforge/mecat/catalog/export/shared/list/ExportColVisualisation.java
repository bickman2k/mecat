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
 * Created on Nov 7, 2005
 * @author Stephan Richard Palm
 * 
 */
package net.sourceforge.mecat.catalog.export.shared.list;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.ListCellRenderer;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.sourceforge.mecat.catalog.gui.ShowListing;
import net.sourceforge.mecat.catalog.gui.ToolBarUtils;
import net.sourceforge.mecat.catalog.gui.utils.PopupMouseListener;
import net.sourceforge.mecat.catalog.option.LocalListener;
import net.sourceforge.mecat.catalog.option.LocalListenerEvent;
import net.sourceforge.mecat.catalog.option.Options;
import net.sourceforge.mecat.catalog.option.WeakLocalListener;
import net.sourceforge.mecat.srp.utils.ButtonBorder;

public abstract class ExportColVisualisation<TYPE extends Enum, EXPORTDEF extends ExportDef<TYPE>, EXPORTCOL extends ExportCol<?, EXPORTDEF>> extends JPanel /*implements DropTargetListener*/ implements LocalListener {

    public final ShowListing showListing;
    
    final Class<TYPE> type;
    
    final static ArrayListTransferHandler arrayListTransferHandler = new ArrayListTransferHandler();
    
    protected ResourceBundle res = Options.getI18N(getClass());
    
    final LineBorder lineBorderBlue;
    final LineBorder lineBorderBlack;
    final TitledBorder titledBorder;
    final EXPORTCOL exportCol;
    final JList jList;
    final ExportColJListListModel<EXPORTDEF, EXPORTCOL> exportColJListListModel;
    final JPopupMenu menu;
    final JMenu menu_InsertItem;
    final JMenuItem menu_RemoveItem;
    
    Rectangle killRect = new Rectangle(0,0,0,0);
    Rectangle nextRect = new Rectangle(0,0,0,0);
    Rectangle prevRect = new Rectangle(0,0,0,0);
    
    boolean nextEnabled = true, prevEnabled = true, killEnabled = true;

    /**
     * Should return a static instance of ExportColListCellRenderer.
     * See LaTeX export to get an idea.
     * @return a list cell renderer
     */
    protected abstract ListCellRenderer getListCellRenderer();
    
    /**
     * Should return a static instance containing an ImageIcon for every TYPE.
     * See LaTeX export to get an idea.
     * @return mapping of types to image icons
     */
    protected abstract Map<TYPE, ImageIcon> getIcons();
    
    Vector<ExportColVisualisationListener> exportColVisualisationListeners = new Vector<ExportColVisualisationListener>();
    
    public void addExportColVisualisationListener(ExportColVisualisationListener exportColVisualisationListener) {
        exportColVisualisationListeners.add(exportColVisualisationListener);
    }
    
    public void removeExportColVisualisationListener(ExportColVisualisationListener exportColVisualisationListener) {
        exportColVisualisationListeners.remove(exportColVisualisationListener);
    }
    
    protected void firePrevEvent() {
        for (ExportColVisualisationListener exportColVisualisationListener : exportColVisualisationListeners)
            exportColVisualisationListener.prevClicked();
    }

    protected void fireNextEvent() {
        for (ExportColVisualisationListener exportColVisualisationListener : exportColVisualisationListeners)
            exportColVisualisationListener.nextClicked();
    }

    protected void fireKillEvent() {
        for (ExportColVisualisationListener exportColVisualisationListener : exportColVisualisationListeners)
            exportColVisualisationListener.killClicked();
    }
    
    protected void fireEditEvent() {
        for (ExportColVisualisationListener exportColVisualisationListener : exportColVisualisationListeners)
            exportColVisualisationListener.editClicked();
    }
    
    public void setNextEnabled(boolean enabled){
        if (enabled == nextEnabled)
            return;
        nextEnabled = enabled;
        updateUI();
    }
    
    public boolean isNextEnabled(){
        return nextEnabled;
    }
    
    public void setPrevEnabled(boolean enabled){
        if (enabled == prevEnabled)
            return;
        prevEnabled = enabled;
        updateUI();
    }

    public boolean isPrevEnabled(){
        return prevEnabled;
    }

    public void setKillEnabled(boolean enabled){
        if (enabled == killEnabled)
            return;
        killEnabled = enabled;
        updateUI();
    }
    
    public boolean isKillEnabled(){
        return killEnabled;
    }

    public ExportColVisualisation(final Class<TYPE> type, final EXPORTCOL exportCol, ShowListing showListing) {
        this(type, exportCol, true, true, true, showListing);
    }
    
/*    public static double distance(double x, double y){
        return Math.sqrt(x*x + y*y);
    }*/
    
    JButton edit = null, kill = null, next = null, prev = null;
    
    public ExportColVisualisation(final Class<TYPE> type, final EXPORTCOL exportCol, final boolean nextEnabled, final boolean prevEnabled, final boolean killEnabled, final ShowListing showListing) {
        this.showListing = showListing;
        this.type = type;
        this.nextEnabled = nextEnabled;
        this.prevEnabled = prevEnabled;
        this.killEnabled = killEnabled;
        this.exportCol = exportCol;
        lineBorderBlue = new LineBorder(Color.BLACK);
        lineBorderBlack = new LineBorder(Color.BLACK);
        titledBorder = BorderFactory.createTitledBorder(lineBorderBlack, exportCol.measurement.toString());
        setLayout(new BorderLayout());
        Vector<JButton> buttons = new Vector<JButton>();
        buttons.add(edit = ToolBarUtils.makeMiniButton("edit", "", null, res.getString("Edit"), new ActionListener(){
            public void actionPerformed(ActionEvent arg0) {
                fireEditEvent();
            }
        }));
        if (killEnabled)
            buttons.add(kill = ToolBarUtils.makeMiniButton("kill", "", null, res.getString("Remove column"), new ActionListener(){
                public void actionPerformed(ActionEvent arg0) {
                    fireKillEvent();
                }
            }));
        if (nextEnabled)
            buttons.add(next = ToolBarUtils.makeMiniButton("next", "", null, res.getString("Switch with column on the right"), new ActionListener(){
                public void actionPerformed(ActionEvent arg0) {
                    fireNextEvent();
                }
            }));
        if (prevEnabled)
            buttons.add(prev = ToolBarUtils.makeMiniButton("prev", "", null, res.getString("Switch with column on the left"), new ActionListener(){
                public void actionPerformed(ActionEvent arg0) {
                    firePrevEvent();
                }
            }));
        
        ButtonBorder buttonBorder = new ButtonBorder(this, titledBorder, buttons.toArray(new JButton[buttons.size()]));

        exportColJListListModel = new ExportColJListListModel<EXPORTDEF, EXPORTCOL>(exportCol);
        jList = new JList(exportColJListListModel);
        jList.setCellRenderer(getListCellRenderer());
        jList.setDragEnabled(true);
        jList.setTransferHandler(arrayListTransferHandler);
//        DropTarget dropTarget = new DropTarget(this, this);
//        jList.setDropTarget(dropTarget);
        
        add(jList);

        
        
        menu = new JPopupMenu();
        menu_InsertItem = new JMenu();
        for (final TYPE t : type.getEnumConstants() ) {
            JMenuItem menu_InsertItem_ = new JMenuItem(t.toString());
            menu_InsertItem_.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e) {
                    insertItem(t);
                }});
            menu_InsertItem_.setIcon(getIcons().get(t));
            menu_InsertItem.add(menu_InsertItem_);
        }
        menu_RemoveItem = new JMenuItem();
        menu_RemoveItem.setEnabled(false);
        menu_RemoveItem.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent arg0) {
                removeItem();
            }});
        menu.add(menu_InsertItem); menu.add(menu_RemoveItem);
        jList.addMouseListener(new PopupMouseListener.DefaultPopupMouseListener(menu));
        jList.addListSelectionListener(new ListSelectionListener(){
            public void valueChanged(ListSelectionEvent arg0) {
                menu_RemoveItem.setEnabled(jList.getSelectedIndex() != -1);
            }
        });
        addMouseListener(new PopupMouseListener.DefaultPopupMouseListener(menu));
        
        Options.addLocalListener(new WeakLocalListener(this));
        setLabels();
    }
    
    public void stateChanged(LocalListenerEvent event) {
        setLabels();
    }
    
    public void setLabels() {
        if (edit != null)
            edit.setToolTipText(res.getString("Edit"));
        if (kill != null)
            kill.setToolTipText(res.getString("Remove column"));
        if (next != null)
            next.setToolTipText(res.getString("Switch with column on the right"));
        if (prev != null)
            prev.setToolTipText(res.getString("Switch with column on the left"));
        menu_InsertItem.setText(res.getString("Insert Item"));
        menu_RemoveItem.setText(res.getString("Remove Item"));
    }

    public void removeItem() {
        if (jList.getSelectedIndex() == -1)
            return;
        
        exportColJListListModel.remove(jList.getSelectedIndex());
        
        ExportColVisualisation.this.firePropertyChange("size", null, null);
    }

    /**
     * Should call insertItemItem.
     * @param t
     */
    public abstract EXPORTDEF aquireExportDef(TYPE t);    
    
    public void insertItem(TYPE t){
        EXPORTDEF def = aquireExportDef(t);
        if (def == null)
            return;

        int index;
        if (jList.getSelectedIndex() == -1 || jList.getSelectedIndex() >= exportCol.size() || jList.getSelectedIndex() == exportColJListListModel.getSize() - 1) {
            exportColJListListModel.add(def);
            index = exportColJListListModel.getSize() - 1;
        } else {
            index = jList.getSelectedIndex();
            exportColJListListModel.add(jList.getSelectedIndex() + 1, def);
        }
        jList.setSelectedIndex(index);

        ExportColVisualisation.this.firePropertyChange("size", null, null);
    }
    
    
    public void setSelected(final boolean selected) {
        titledBorder.setBorder((selected)?lineBorderBlue:lineBorderBlack);
        titledBorder.setTitleColor((selected)?Color.BLUE:Color.BLACK);
    }
}
