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
package net.sourceforge.mecat.catalog.gui.options;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Collection;
import java.util.Collections;
import java.util.Locale;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.sourceforge.mecat.catalog.gui.ToolBarUtils;
import net.sourceforge.mecat.catalog.gui.options.LanguageSelection.Languag;
import net.sourceforge.mecat.catalog.option.LocalListener;
import net.sourceforge.mecat.catalog.option.LocalListenerEvent;
import net.sourceforge.mecat.catalog.option.Options;
import net.sourceforge.mecat.catalog.option.WeakLocalListener;
import net.sourceforge.mecat.catalog.option.preferences.LanguagesOption;
import net.sourceforge.mecat.catalog.sort.design.FeatureSortOptions;

public class LanguagesOptionPanel extends JPanel implements LocalListener {

    final static Vector<Languag> choices = new Vector<Languag>(Locale.getISOLanguages().length);
    static {
        for (int i = 0; i < Locale.getISOLanguages().length; i++) {
            Locale l = new Locale(Locale.getISOLanguages()[i]);
            choices.add(new Languag(l));
        }
        Collections.sort(choices);
    }

    final JList selectionList, choiceList;
    
    final Vector<Languag> select = new Vector<Languag>();
    final LanguagesOption languagesOption;

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

    public void setSelection(Collection<Locale> selection) {
        select.clear();
        languagesOption.clear();
        
        for (Locale l : selection)
            select.add(new Languag(l));
        languagesOption.addAll(selection);
        
        selectionList.setListData(select);
    }

    public LanguagesOptionPanel(LanguagesOption languageOption) {
        this.languagesOption = languageOption;
        for (Locale l : languagesOption)
            select.add(new Languag(l));

        JPanel buttons = new JPanel();
        buttons.setLayout(new GridLayout(0,1));
        addButton.setEnabled(false);
        removeButton.setEnabled(false);
        buttons.add(addButton);
        buttons.add(removeButton);

		selectionList = new JList(select);
		choiceList = new JList(choices);

		choiceList.addMouseListener(new MouseListener(){
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() > 1) 
                    add();
			}
			public void mouseEntered(MouseEvent e) {}
			public void mouseExited(MouseEvent e) {}
			public void mousePressed(MouseEvent e) {}
			public void mouseReleased(MouseEvent e) {}});
        choiceList.addListSelectionListener(new ListSelectionListener(){
            public void valueChanged(ListSelectionEvent arg0) {
                addButton.setEnabled(choiceList.getSelectedIndex() != -1);
            }
        });
		selectionList.addMouseListener(new MouseListener(){
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() > 1) 
                    remove();
			}
			public void mouseEntered(MouseEvent e) {}
			public void mouseExited(MouseEvent e) {}
			public void mousePressed(MouseEvent e) {}
			public void mouseReleased(MouseEvent e) {}});
        selectionList.addListSelectionListener(new ListSelectionListener(){
            public void valueChanged(ListSelectionEvent arg0) {
                removeButton.setEnabled(selectionList.getSelectedIndex() != -1);
            }
        });


        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.gridheight = GridBagConstraints.REMAINDER;
        c.weightx = 1;
		add(new JScrollPane(selectionList), c);
        c.weightx = 0.1;
        add(buttons, c);
        c.weightx = 1; 
        c.gridwidth = GridBagConstraints.REMAINDER;
		add(new JScrollPane(choiceList), c);

        Options.addLocalListener(new WeakLocalListener(this));
        // Set labels to the current locale
        setLabels();
    }

    public void stateChanged(LocalListenerEvent event) {
        setLabels();
    }
    
    public void setLabels() {
        addButton.setToolTipText(Options.getI18N(FeatureSortOptions.class).getString("Add"));
        removeButton.setToolTipText(Options.getI18N(FeatureSortOptions.class).getString("Remove"));
	}

    protected void add() {
        Languag l = choices.elementAt(choiceList.getSelectedIndex());

        if (!select.contains(l))
            select.add(l);
        languagesOption.add(l.locale);
        
        selectionList.setListData(select);
        
        fireChange();
    }
    
    protected void remove() {
        languagesOption.remove(select.get(selectionList.getSelectedIndex()).locale);
        select.remove(selectionList.getSelectedIndex());
        
        selectionList.setListData(select);
        
        fireChange();
    }
    
    
}
