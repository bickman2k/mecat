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
 * Created on Sep 24, 2004
 * @author Stephan Richard Palm
 */
package net.sourceforge.mecat.catalog.export.shared.list;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ResourceBundle;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JToolBar;

import net.sourceforge.mecat.catalog.export.latex.LaTeXExportProfileVisualisation;
import net.sourceforge.mecat.catalog.gui.MainFrameBackend;
import net.sourceforge.mecat.catalog.gui.ShowListing;
import net.sourceforge.mecat.catalog.gui.ToolBarUtils;
import net.sourceforge.mecat.catalog.option.LocalListener;
import net.sourceforge.mecat.catalog.option.LocalListenerEvent;
import net.sourceforge.mecat.catalog.option.Options;
import net.sourceforge.mecat.catalog.option.WeakLocalListener;

public abstract class MultiListVisualisation<TYPE extends Enum, EXPORTDEF extends ExportDef<TYPE>, EXPORTCOL extends ExportCol<?, EXPORTDEF>, 
                                            LISTDEFINITION extends ListDefinition<TYPE, EXPORTDEF, EXPORTCOL>, 
                                            LISTDEFINITIONVISUALISATION extends ListDefinitionVisualisation<?, ?, ?, ?, LISTDEFINITION>> extends JPanel implements LocalListener {

    // We only need ont ListDefinitionVisualisation too
    final LISTDEFINITIONVISUALISATION listDefinitionVisualisation = getListDefinitionVisualisation();
    
    abstract protected LISTDEFINITIONVISUALISATION getListDefinitionVisualisation();
    
    MultiList<LISTDEFINITION> multiList = null;
    ShowListing showListing = null;
    
    ResourceBundle res = Options.getI18N(LaTeXExportProfileVisualisation.class);

    final JButton nextButton = ToolBarUtils.makeButton("next", "", Options.getI18N(MainFrameBackend.class).getString("Next"), Options.getI18N(MainFrameBackend.class).getString("Next"), new ActionListener(){ 
        public void actionPerformed(ActionEvent arg0) {
            next();
        }});
    final JButton prevButton = ToolBarUtils.makeButton("prev", "", Options.getI18N(MainFrameBackend.class).getString("Previous"), Options.getI18N(MainFrameBackend.class).getString("Previous"), new ActionListener(){ 
        public void actionPerformed(ActionEvent arg0) {
            prev();
        }});
    final JButton lessButton = ToolBarUtils.makeButton("minus", "", Options.getI18N(MainFrameBackend.class).getString("Less"), Options.getI18N(MainFrameBackend.class).getString("Less"), new ActionListener(){ 
        public void actionPerformed(ActionEvent arg0) {
            less();
        }});
    final JButton moreButton = ToolBarUtils.makeButton("plus", "", Options.getI18N(MainFrameBackend.class).getString("More"), Options.getI18N(MainFrameBackend.class).getString("More"), new ActionListener(){ 
        public void actionPerformed(ActionEvent arg0) {
            more();
        }});
    
    final PropertyChangeListener sizeChangeListener = new PropertyChangeListener(){
        public void propertyChange(PropertyChangeEvent arg0) {
            firePropertyChange("size", null, null);
        }
    };

    public MultiListVisualisation() {
        
        // Make Panel that will contain the lists
        setLayout(new BorderLayout());

        add(listDefinitionVisualisation);
        add(listToolbar, BorderLayout.EAST);

        // Make Toolbar for lists
        listToolbar.setOrientation(JToolBar.VERTICAL);
        listToolbar.add(nextButton);
        listToolbar.add(prevButton);
        listToolbar.add(moreButton);
        listToolbar.add(lessButton);
        listToolbar.setFloatable(false);
        
        listDefinitionVisualisation.addPropertyChangeListener("size", sizeChangeListener);

        Options.addLocalListener(new WeakLocalListener(this));
        // Set labels to the current locale
        setLabels();
    }

    public void stateChanged(LocalListenerEvent event) {
        setLabels();
    }
    
    public void setLabels() {
        nextButton.setToolTipText(Options.getI18N(MainFrameBackend.class).getString("Next"));
        prevButton.setToolTipText(Options.getI18N(MainFrameBackend.class).getString("Previous"));
        lessButton.setToolTipText(Options.getI18N(MainFrameBackend.class).getString("Less"));
        moreButton.setToolTipText(Options.getI18N(MainFrameBackend.class).getString("More"));
    }

    public void setMultiList(final MultiList<LISTDEFINITION> multiList, final ShowListing showListing) {
        this.multiList = multiList;
        this.showListing = showListing;
        actualizeList();
    }
    
    
    private void actualizeList() {
        listDefinitionVisualisation.setListDefinition(multiList.getListDefinition(multiList.getSelectedList()), multiList.getSelectedList() + 1, multiList.getListDefinitionsSize(), showListing);
        
        nextButton.setEnabled(multiList.getSelectedList() < multiList.getListDefinitionsSize() - 1);
        prevButton.setEnabled(multiList.getSelectedList() > 0);
        lessButton.setEnabled(multiList.getListDefinitionsSize() > 1);
    }

    protected void more() {
        multiList.more();
        actualizeList();
    }

    protected void less() {
        multiList.less();
        actualizeList();
    }

    protected void prev() {
        multiList.prev();
        actualizeList();
    }

    protected void next() {
        multiList.next();
        actualizeList();
    }

    final JToolBar listToolbar = new JToolBar();
    final JPanel list = new JPanel();
	
}
