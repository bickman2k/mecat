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
 * Created on Jul 6, 2006
 * @author Stephan Richard Palm
 * 
 */
package net.sourceforge.mecat.catalog.templates;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.sourceforge.mecat.catalog.datamanagement.Catalog;
import net.sourceforge.mecat.catalog.export.ExportChoice;
import net.sourceforge.mecat.catalog.i18n.util.SimpleLocalButton;
import net.sourceforge.mecat.catalog.option.LocalListener;
import net.sourceforge.mecat.catalog.option.LocalListenerEvent;
import net.sourceforge.mecat.catalog.option.Options;
import net.sourceforge.mecat.catalog.option.WeakLocalListener;

public class NewWizard extends JDialog implements LocalListener {

    public static Catalog showNewWizard(Component component) {
        NewWizard selector;

        while (component != null && !(component instanceof Frame) && !(component instanceof Dialog))
            component = component.getParent();

        if (component == null)
            selector = new NewWizard();
        else if (component instanceof Dialog)
            selector = new NewWizard((Dialog)component);
        else
            selector = new NewWizard((Frame)component);
        
        selector.setVisible(true);

        return selector.getResult();
    }

    final JList list = new JList();
    final JButton accept = new SimpleLocalButton(Options.getI18N(ExportChoice.class), "OK");
    final JButton cancel = new SimpleLocalButton(Options.getI18N(ExportChoice.class), "Cancel");
    Catalog result = null;
    
    
    
    public NewWizard() throws HeadlessException {
        super();
        init();
    }

    public NewWizard(Dialog arg0) throws HeadlessException {
        super(arg0);
        init();
    }

    public NewWizard(Frame arg0) throws HeadlessException {
        super(arg0);
        init();
    }

    public void init() {
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setListData(Options.templates);
        setLayout(new BorderLayout());
        add(new JScrollPane(list));
        
        list.addListSelectionListener(new ListSelectionListener(){
            public void valueChanged(ListSelectionEvent arg0) {
                accept.setEnabled(list.getSelectedValue() instanceof Template);
            }
        });
        list.addMouseListener(new MouseListener(){

            Template lastClickTemplate = null;
            
            // Get double click for take this template
            public synchronized void mouseClicked(MouseEvent event) {

                if (event.getClickCount() > 1 && 
                        lastClickTemplate != null && 
                        lastClickTemplate.equals(list.getSelectedValue()))
                    NewWizard.this.accept();
                
                if (list.getSelectedValue() instanceof Template)
                    lastClickTemplate = (Template) list.getSelectedValue();
                else
                    lastClickTemplate = null;
            }

            public void mouseEntered(MouseEvent arg0) {}
            public void mouseExited(MouseEvent arg0) {}
            public void mousePressed(MouseEvent arg0) {}
            public void mouseReleased(MouseEvent arg0) {}
        });
        
        list.setCellRenderer(new TemplateCellRenderer());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(accept);
        buttonPanel.add(cancel);

        accept.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                NewWizard.this.accept();
            }
        });
        accept.setEnabled(false);
        cancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                setVisible(false);
            }
        });
        
        add(buttonPanel, BorderLayout.SOUTH);
    
        setSize(new Dimension(300,400));
        setResizable(false);
        setModal(true);

        Options.addLocalListener(new WeakLocalListener(this));
        // Set labels to the current locale
        setLabels();
    }

    protected void accept() {
        result = ((Template)list.getSelectedValue()).getCatalog(this);
        setVisible(false);
    }
    
    public void stateChanged(LocalListenerEvent event) {
        setLabels();
    }
    
    public void setLabels() {
        setTitle(Options.getI18N(NewWizard.class).getString("Choose catalog template"));
    }
    
    private Catalog getResult() {
        return result;
    }
    
}
