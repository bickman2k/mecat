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
 * Created on Jun 30, 2006
 * @author Stephan Richard Palm
 * 
 */
package net.sourceforge.mecat.catalog.gui.search;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ResourceBundle;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import net.sourceforge.mecat.catalog.export.ExportChoice;
import net.sourceforge.mecat.catalog.i18n.util.SimpleLocalButton;
import net.sourceforge.mecat.catalog.option.LocalListener;
import net.sourceforge.mecat.catalog.option.LocalListenerEvent;
import net.sourceforge.mecat.catalog.option.Options;
import net.sourceforge.mecat.catalog.option.WeakLocalListener;

public class SearchDialog extends JDialog implements LocalListener {

    final ResourceBundle res = Options.getI18N(SearchDialog.class);
    
    JRadioButton level0 = new JRadioButton(res.getString("Search everything, hidden types included."));
    JRadioButton level1 = new JRadioButton(res.getString("Search all visible media including those that are hidden by the current filter."));
    JRadioButton level2 = new JRadioButton(res.getString("Search in the shown selection."));
    JTextField txtField  = new JTextField();
    
    final JButton accept = new SimpleLocalButton(Options.getI18N(ExportChoice.class), "OK");
    final JButton cancel = new SimpleLocalButton(Options.getI18N(ExportChoice.class), "Cancel");

    SearchParameters result = null;
    
    public static SearchParameters showSearchDialog(Component component) {
        SearchDialog selector;

        while (component != null && !(component instanceof Frame) && !(component instanceof Dialog))
            component = component.getParent();

        if (component == null)
            selector = new SearchDialog();
        else if (component instanceof Dialog)
            selector = new SearchDialog((Dialog)component);
        else
            selector = new SearchDialog((Frame)component);
        
        selector.setVisible(true);

        return selector.getResult();
    }

    public SearchDialog() throws HeadlessException {
        super();
        init();
    }

    public SearchDialog(Dialog arg0) throws HeadlessException {
        super(arg0);
        init();
    }

    public SearchDialog(Frame arg0) throws HeadlessException {
        super(arg0);
        init();
    }
    
    private void init() {
        level1.setSelected(true);
        ButtonGroup group = new ButtonGroup();
        group.add(level0);
        group.add(level1);
        group.add(level2);
        JPanel radioButtonPanel = new JPanel();
        radioButtonPanel.setLayout(new GridLayout(0,1));
        radioButtonPanel.add(level0);
        radioButtonPanel.add(level1);
        radioButtonPanel.add(level2);
        JPanel all = new JPanel();
        all.setLayout(new BorderLayout());
        all.add(radioButtonPanel, BorderLayout.NORTH);
        all.add(txtField);
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(accept);
        buttonPanel.add(cancel);

        accept.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                if (txtField.getText() == null) {
                    setVisible(false);
                    return;
                }
                
                if (txtField.getText().length() == 0){
                    setVisible(false);
                    return;
                }
                
                if (level0.isSelected())
                    result = new SearchParameters(0, txtField.getText(), false);
                else if (level1.isSelected())
                    result = new SearchParameters(1, txtField.getText(), false);
                else
                    result = new SearchParameters(2, txtField.getText(), false);
                
                setVisible(false);
            }
        });
        accept.setEnabled(false);
        cancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                setVisible(false);
            }
        });
        txtField.addKeyListener(new KeyListener(){
            public void keyPressed(KeyEvent arg0) {
                checkOk();
            }
            public void keyReleased(KeyEvent arg0) {
                checkOk();
            }
            public void keyTyped(KeyEvent arg0) {
                checkOk();
            }
        });
        txtField.addMouseListener(new MouseListener(){
            public void mouseClicked(MouseEvent arg0) {
                checkOk();
            }
            public void mouseEntered(MouseEvent arg0) {
                checkOk();
            }
            public void mouseExited(MouseEvent arg0) {
                checkOk();
            }
            public void mousePressed(MouseEvent arg0) {
                checkOk();
            }
            public void mouseReleased(MouseEvent arg0) {
                checkOk();
            }
        });
        
        setLayout(new BorderLayout());
        add(all);
        add(buttonPanel, BorderLayout.SOUTH);
    
        pack();
        setResizable(false);
        setModal(true);

        Options.addLocalListener(new WeakLocalListener(this));
        // Set labels to the current locale
        setLabels();

        txtField.requestFocusInWindow();
    }

    protected boolean hasSearchStringValue() {
        if (txtField.getText() == null) 
            return false;
        
        if (txtField.getText().length() == 0)
            return false;
        
        return true;
    }
    
    protected void checkOk() {
        accept.setEnabled(hasSearchStringValue());
    }

    public void stateChanged(LocalListenerEvent event) {
        setLabels();
    }
    
    public void setLabels() {
        setTitle(res.getString("Create search"));
        level0.setToolTipText(res.getString("Finds the most entries, but may result in to many entries."));
        level1.setToolTipText(res.getString("If you don't know what to chose you want this."));
        level2.setToolTipText(res.getString("If you want to keep the filter active for the search."));
        txtField.setToolTipText(res.getString("Place your search string here."));
    }

    public SearchParameters getResult() {
        return result;
    }
    
    
}
