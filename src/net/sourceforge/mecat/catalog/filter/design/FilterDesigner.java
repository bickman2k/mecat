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

import static net.sourceforge.mecat.catalog.option.Options.getI18N;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.sourceforge.mecat.catalog.export.shared.list.ExportColVisualisation;
import net.sourceforge.mecat.catalog.filter.AndFilter;
import net.sourceforge.mecat.catalog.filter.FalseFilter;
import net.sourceforge.mecat.catalog.filter.FeatureFilter;
import net.sourceforge.mecat.catalog.filter.Filter;
import net.sourceforge.mecat.catalog.filter.FilterUtils;
import net.sourceforge.mecat.catalog.filter.NotFilter;
import net.sourceforge.mecat.catalog.filter.OrFilter;
import net.sourceforge.mecat.catalog.filter.TrueFilter;
import net.sourceforge.mecat.catalog.gui.ToolBarUtils;
import net.sourceforge.mecat.catalog.medium.features.Feature;
import net.sourceforge.mecat.catalog.option.LocalListener;
import net.sourceforge.mecat.catalog.option.LocalListenerEvent;
import net.sourceforge.mecat.catalog.option.Options;
import net.sourceforge.mecat.catalog.option.WeakLocalListener;
import net.sourceforge.mecat.catalog.option.preferences.TotalPreferences;

public class FilterDesigner extends JPanel implements VisualisationNodeListener, DragDropFilterTreeListener, LocalListener{
    final JToolBar toolbar = new JToolBar();
    final DragDropFilterTree filterTree = new DragDropFilterTree();
    VisualisationNode node;
    JTextField field;
    VisualisationCanvas graph;

    TotalPreferences totalPreferences = null;
    
    protected Vector<ActionListener> actionListeners = new Vector<ActionListener>();
    
    final JButton featureButton, editButton, andButton, orButton, notButton, trueButton, falseButton;
    
    
    public void addActionListener(final ActionListener actionListener) {
        actionListeners.add(actionListener);
    }
    
    public void removeActionListener(final ActionListener actionListener) {
        actionListeners.remove(actionListener);
    }
    
    protected void fireChange() {
        for (ActionListener actionListener : actionListeners)
            actionListener.actionPerformed(null);
    }
    
    public Filter getDesignedFilter() {
        return node.getFilter();
    }
    
    static public Filter getFilter(final Filter filter, final TotalPreferences totalPreferences) {
        FilterDesigner filterDesigner = new FilterDesigner(FilterUtils.copyFilter(filter));
        filterDesigner.setTotalPreferences(totalPreferences);

        ShowDialog dialog = new ShowDialog(filterDesigner, getI18N(FilterDesigner.class).getString("FilterDesigner"));
        
        dialog.setModal(true);
//        dialog.pack();
        Dimension dim = dialog.getPreferredSize();
        dialog.setSize(new Dimension(Math.max(800, dim.width), Math.max(600, dim.height)));
        dialog.setVisible(true);

        if (dialog.accepted) {
            if (filter != null)
                Options.addRecentFilter(filter);
            return filterDesigner.node.getFilter();
        }

        return null;
    }
    
    public void setFilter(final Filter filter){
        if (filter == null)
            node.setFilter(TrueFilter.TRUE);
        else
            node.setFilter(filter);
    }

    public void setTotalPreferences(final TotalPreferences totalPreferences) {
        this.totalPreferences = totalPreferences;
    }
    
    public FilterDesigner(){
        this(TrueFilter.TRUE);
    }
    
    public void checkButtonState() {
        // Edit button is only enabled if a feature filter is selected
        // this is because it only edits feature options
        editButton.setEnabled(graph.getCurrentSelection() != null && graph.getCurrentSelection().getFilter() instanceof FeatureFilter);
        
        featureButton.setEnabled(graph.getCurrentSelection() != null);
        andButton.setEnabled(graph.getCurrentSelection() != null);
        orButton.setEnabled(graph.getCurrentSelection() != null);
        notButton.setEnabled(graph.getCurrentSelection() != null);
        trueButton.setEnabled(graph.getCurrentSelection() != null);
        falseButton.setEnabled(graph.getCurrentSelection() != null);
    }
    
    public FilterDesigner(final Filter filter) {
        
        node = new VisualisationNode(filter);
        graph = new VisualisationCanvas(node);
        field = new JTextField(filter.getCondition());
        
        graph.addSelectionChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent arg0) {
                checkButtonState();
            }
        });

        field.setEditable(false);

        featureButton = ToolBarUtils.makeButton(FeatureFilter.class, "Feature", "", null, getI18N(Feature.class).getString("Feature"), new ActionListener(){ 
            public void actionPerformed(ActionEvent arg0) {
                VisualisationNode sel = graph.getCurrentSelection();
                if (sel == null) 
                    return;
                Filter featureFilter = SimpleFeatureFilterDesigner.getFeatureFilter(totalPreferences);
                if (featureFilter == null)
                    return;
                graph.setCurrentSelection(null);
                
                node.exchangeFilter(sel, featureFilter);
                graph.repaint();
                FilterDesigner.this.revalidate();
            }

        });
        toolbar.add(featureButton);
        toolbar.addSeparator();
        toolbar.addSeparator();
        toolbar.addSeparator();
//        toolbar.add(ToolBarUtils.makeNavigationButton(LanguageFilter.class, "Language", "", "Language", "Language", new ActionListener(){ 
//            public void actionPerformed(ActionEvent arg0) {
//            }}));
        editButton = ToolBarUtils.makeButton("edit", "", null, Options.getI18N(ExportColVisualisation.class).getString("Edit"), new ActionListener(){ 
            public void actionPerformed(ActionEvent arg0) {
                if (graph.getCurrentSelection() == null) 
                    return;
                VisualisationNode sel = graph.getCurrentSelection();
                Filter filter = sel.getFilter();
                if (!(filter instanceof FeatureFilter))
                    return;
                FeatureFilter oldFeatureFilter = ( FeatureFilter ) filter;

                Filter featureFilter = SimpleFeatureFilterDesigner.getFeatureFilter(totalPreferences, oldFeatureFilter);
                if (featureFilter == null)
                    return;
                graph.setCurrentSelection(null);
                
                node.exchangeFilter(sel, featureFilter);
                graph.repaint();
                FilterDesigner.this.revalidate();
            
            }});
        editButton.setEnabled(false);
        toolbar.add(editButton);
        andButton = ToolBarUtils.makeButton(AndFilter.class, "And", "", null, getI18N(AndFilter.class).getString("And"), new ActionListener(){ 
            public void actionPerformed(ActionEvent arg0) {
                if (graph.getCurrentSelection() == null) 
                    return;
                VisualisationNode sel = graph.getCurrentSelection();
                graph.setCurrentSelection(null);
                
                node.exchangeFilter(sel, new AndFilter(sel.getFilter(), TrueFilter.TRUE));
            }});
        toolbar.add(andButton);
        orButton = ToolBarUtils.makeButton(OrFilter.class, "Or", "", null, getI18N(OrFilter.class).getString("Or"), new ActionListener(){ 
            public void actionPerformed(ActionEvent arg0) {
                if (graph.getCurrentSelection() == null) 
                    return;
                VisualisationNode sel = graph.getCurrentSelection();
                graph.setCurrentSelection(null);

                node.exchangeFilter(sel, new OrFilter(sel.getFilter(), FalseFilter.FALSE));
            }});
        toolbar.add(orButton);
        notButton = ToolBarUtils.makeButton(NotFilter.class, "Not", "", null, getI18N(NotFilter.class).getString("Not"), new ActionListener(){ 
            public void actionPerformed(ActionEvent arg0) {
                if (graph.getCurrentSelection() == null) 
                    return;
                VisualisationNode sel = graph.getCurrentSelection();
                graph.setCurrentSelection(null);
                
                node.exchangeFilter(sel, new NotFilter(sel.getFilter()));
            }});
        toolbar.add(notButton);
        trueButton = ToolBarUtils.makeButton(TrueFilter.class, "True", "", null, getI18N(TrueFilter.class).getString("True"), new ActionListener(){ 
            public void actionPerformed(ActionEvent arg0) {
                if (graph.getCurrentSelection() == null) 
                    return;
                VisualisationNode sel = graph.getCurrentSelection();
                graph.setCurrentSelection(null);
                
                node.exchangeFilter(sel, TrueFilter.TRUE);
            }});
        toolbar.add(trueButton);
        falseButton = ToolBarUtils.makeButton(FalseFilter.class, "False", "", null, getI18N(FalseFilter.class).getString("False"), new ActionListener(){ 
            public void actionPerformed(ActionEvent arg0) {
                if (graph.getCurrentSelection() == null) 
                    return;
                VisualisationNode sel = graph.getCurrentSelection();
                graph.setCurrentSelection(null);
                
                node.exchangeFilter(sel, FalseFilter.FALSE);
            }});
        toolbar.add(falseButton);
//        JPanel buttons = new JPanel();
//        buttons.add(new JButton(getI18N(ExportChoice.class).getString("OK")){{
//            addActionListener(new ActionListener(){
//                public void actionPerformed(ActionEvent arg0) {
//                    accepted = true;
//                    FilterDesigner.this.setVisible(false);
//                }
//            });
//        }});
//        buttons.add(new JButton(getI18N(ExportChoice.class).getString("Cancel")){{
//            addActionListener(new ActionListener(){
//                public void actionPerformed(ActionEvent arg0) {
//                    FilterDesigner.this.setVisible(false);
//                }
//            });
//        }});
        
        JPanel contentPane = new JPanel();
        JSplitPane splitPane = new JSplitPane();
        JPanel editPanel = new JPanel();
        
        editPanel.setLayout(new BorderLayout());
        editPanel.add(new JScrollPane(graph));
        editPanel.add(toolbar, BorderLayout.PAGE_START);
        editPanel.add(field, BorderLayout.SOUTH);
        
        splitPane.add(filterTree, JSplitPane.LEFT);
        splitPane.add(editPanel, JSplitPane.RIGHT);

//        contentPane.setLayout(new BorderLayout());
//        contentPane.add(splitPane);
//        contentPane.add(buttons, BorderLayout.SOUTH);
//        
//        setContentPane(contentPane);
        setLayout(new BorderLayout());
        add(splitPane);
        
        filterTree.addDragDropFilterTreeListener(this);
        node.addVisualisationNodeListener(this);

        checkButtonState();
        
        Options.addLocalListener(new WeakLocalListener(this));
        // Set labels to the current locale
        setLabels();
    }

    public void stateChanged(LocalListenerEvent event) {
        setLabels();
        
        graph.repaint();
    }
    
    public void setLabels() {
        featureButton.setText(getI18N(Feature.class).getString("Feature"));
        editButton.setText(Options.getI18N(ExportColVisualisation.class).getString("Edit"));
        andButton.setText(Options.getI18N(AndFilter.class).getString("And"));
        orButton.setText(Options.getI18N(OrFilter.class).getString("Or"));
        notButton.setText(Options.getI18N(NotFilter.class).getString("Not"));
        trueButton.setText(Options.getI18N(TrueFilter.class).getString("True"));
        falseButton.setText(Options.getI18N(FalseFilter.class).getString("False"));
    }

    public void changed() {
        field.setText(node.getFilter().getCondition());
        fireChange();
    }

    public void selectionChanged() {
        Filter filter = filterTree.getSelectedFilter();
        node.exchangeFilter(node, FilterUtils.copyFilter(filter));
        graph.repaint();
        changed();
    }
}
