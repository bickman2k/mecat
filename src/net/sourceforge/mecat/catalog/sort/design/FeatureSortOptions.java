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
 * Created on Jul 23, 2004
 * @author Stephan Richard Palm
 */
package net.sourceforge.mecat.catalog.sort.design;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.sourceforge.mecat.catalog.medium.Listing;
import net.sourceforge.mecat.catalog.medium.features.Feature;
import net.sourceforge.mecat.catalog.gui.ToolBarUtils;
import net.sourceforge.mecat.catalog.option.LocalListener;
import net.sourceforge.mecat.catalog.option.LocalListenerEvent;
import net.sourceforge.mecat.catalog.option.Options;
import net.sourceforge.mecat.catalog.option.WeakLocalListener;
import net.sourceforge.mecat.catalog.option.preferences.AbstractMediaOption;
import net.sourceforge.mecat.catalog.sort.ByMedium;
import net.sourceforge.mecat.catalog.sort.Comparing;
import net.sourceforge.mecat.catalog.sort.ConfigurableComparator;

public class FeatureSortOptions extends JPanel implements LocalListener {

	JList selection, choice;
    JCheckBox showAll = new JCheckBox();
	List<ConfigurableComparator> choicesAll = new Vector<ConfigurableComparator>();
    List<ConfigurableComparator> choicesSelection = new Vector<ConfigurableComparator>();
	public Comparing select = new Comparing();
    Listing listing = null;

    final JButton addButton = ToolBarUtils.makeButton("prev", "", null, Options.getI18N(FeatureSortOptions.class).getString("Add"), new ActionListener(){
        public void actionPerformed(ActionEvent arg0) {
            add();
        }
    });
    final JButton removeButton = ToolBarUtils.makeButton("kill", "", null, Options.getI18N(FeatureSortOptions.class).getString("Remove"), new ActionListener(){
        public void actionPerformed(ActionEvent arg0) {
            remove();
        }
    });

    protected Vector<ActionListener> actionListeners = new Vector<ActionListener>();
    
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

    public Comparing getSorting() {
        return select;
    }

    public void setSorting(final ConfigurableComparator comparator) {
        if (comparator instanceof Comparing)
            setSorting((Comparing) comparator);
        else
            setSorting(new Comparing(){{ 
                if (comparator != null)
                    add(comparator); 
            }});
    }
    
    public void setSorting(Comparing comparing) {
        select = new Comparing(comparing);
        selection.setListData(select);
    }
    
    public void setListing(Listing listing) {
        this.listing = listing;
        calculateSelection();
        showAll.setEnabled(true);
        showAll.setSelected(false);
    }
    
    void calculateSelection() {
        if (listing == null)
            return;
        // Only need the features directly accessable 
        // therefor use getFeatures and not getAllFeatures
        choicesSelection = choicesFromFeatures(AbstractMediaOption.getFeatures(listing.getTypes()));
        choice.setListData(choicesSelection.toArray());
    }
    
    List<ConfigurableComparator> choicesFromFeatures(Collection<Class<? extends Feature>> features) {
        Vector<ConfigurableComparator> choices = new Vector<ConfigurableComparator>();
        for (Class<? extends Feature> f : features)
            try {
                ConfigurableComparator cc = (ConfigurableComparator)(f.getMethod("getComparator", new Class[]{}).invoke(null, new Object[]{}));
                if (cc != null)
                    choices.add(cc);
            } catch (IllegalArgumentException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            } catch (SecurityException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            } catch (IllegalAccessException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            } catch (InvocationTargetException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            } catch (NoSuchMethodException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }       
            
        choices.add(new ByMedium());
        Collections.sort(choices, new Comparator<ConfigurableComparator>(){
            public int compare(ConfigurableComparator cc1, ConfigurableComparator cc2) {
                return cc1.toString().compareTo(cc2.toString());
            }
        });
        return choices;
    }

    public FeatureSortOptions(Listing listing) {
        this();
        setListing(listing);
    }
    
	public FeatureSortOptions() {
        
        JPanel buttons = new JPanel();
        buttons.setLayout(new GridLayout(0,1));
        addButton.setEnabled(false);
        removeButton.setEnabled(false);
        buttons.add(addButton);
        buttons.add(removeButton);

        choicesAll = choicesFromFeatures(AbstractMediaOption.getFeatures());

		selection = new JList(select);
		choice = new JList(choicesAll.toArray());

		choice.addMouseListener(new MouseListener(){
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() > 1) 
                    add();
			}
			public void mouseEntered(MouseEvent e) {}
			public void mouseExited(MouseEvent e) {}
			public void mousePressed(MouseEvent e) {}
			public void mouseReleased(MouseEvent e) {}});
        choice.addListSelectionListener(new ListSelectionListener(){
            public void valueChanged(ListSelectionEvent arg0) {
                addButton.setEnabled(choice.getSelectedIndex() != -1);
            }
        });
		selection.addMouseListener(new MouseListener(){
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() > 1) 
                    remove();
			}
			public void mouseEntered(MouseEvent e) {}
			public void mouseExited(MouseEvent e) {}
			public void mousePressed(MouseEvent e) {}
			public void mouseReleased(MouseEvent e) {}});
        selection.addListSelectionListener(new ListSelectionListener(){
            public void valueChanged(ListSelectionEvent arg0) {
                removeButton.setEnabled(selection.getSelectedIndex() != -1);
            }
        });


        setLayout(new GridBagLayout());
        {
            GridBagConstraints c = new GridBagConstraints();
            c.fill = GridBagConstraints.BOTH;
            c.gridwidth = GridBagConstraints.REMAINDER;
            add(showAll, c);
        }{
            GridBagConstraints c = new GridBagConstraints();
            c.fill = GridBagConstraints.BOTH;
            c.weightx = 5;
            add(new JScrollPane(selection), c);
        }{
            GridBagConstraints c = new GridBagConstraints();
            c.fill = GridBagConstraints.BOTH;
            c.weightx = 1;
            add(buttons, c);
        }{
            GridBagConstraints c = new GridBagConstraints();
            c.fill = GridBagConstraints.BOTH;
            c.weightx = 5; 
            c.gridwidth = GridBagConstraints.REMAINDER;
            add(new JScrollPane(choice), c);
        }

        // Show all is at the beginning 
        // while there is no listing hard set to selected
        showAll.setSelected(true);
        showAll.setEnabled(false);
        showAll.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                if (showAll.isSelected())
                    choice.setListData(choicesAll.toArray());
                else
                    choice.setListData(choicesSelection.toArray());
            }
        });

        Options.addLocalListener(new WeakLocalListener(this));
        // Set labels to the current locale
        setLabels();
    }

    public void stateChanged(LocalListenerEvent event) {
        setLabels();
    }
    
    public void setLabels() {
        showAll.setText(Options.getI18N(FeatureSortOptions.class).getString("Show sorting for all features"));
        addButton.setToolTipText(Options.getI18N(FeatureSortOptions.class).getString("Add"));
        removeButton.setToolTipText(Options.getI18N(FeatureSortOptions.class).getString("Remove"));
	}

    protected void add() {
        select.add((ConfigurableComparator)choice.getSelectedValue());
//        select.add(choices.elementAt(choice.getSelectedIndex()));
        selection.setListData(select);
        fireChange();
    }
    
    protected void remove() {
//        select.remove(selection.getSelectedIndex());
        select.remove(selection.getSelectedValue());
        selection.setListData(select);
        fireChange();
    }
    
    
}
