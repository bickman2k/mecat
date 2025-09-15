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
 * Created on Jun 10, 2006
 * @author Stephan Richard Palm
 * 
 */
package net.sourceforge.mecat.catalog.filesystem.gui.doppelganger;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.sourceforge.mecat.catalog.export.ExportProgressVisualisation;
import net.sourceforge.mecat.catalog.i18n.util.SimpleLocalButton;
import net.sourceforge.mecat.catalog.i18n.util.SimpleLocalLabel;
import net.sourceforge.mecat.catalog.option.Options;

public class DoppelgangerPanel extends JPanel {

    final List<DoppelgangerGroup> groups;
    final JTable groupsTable;
    final JTable groupTable;
    final JPanel groupPanel = new JPanel();
    final JLabel noGroupSelected = new SimpleLocalLabel(Options.getI18N(DoppelgangerPanel.class), "No group selected.");

    public static void showDoppelganger(Component component, List<DoppelgangerGroup> groups) {
        DoppelgangerPanel panel = new DoppelgangerPanel(groups);

        while (component != null && !(component instanceof Frame) && !(component instanceof Dialog))
            component = component.getParent();

        JDialog tmp;
        
        if (component == null)
            tmp = new JDialog();
        else if (component instanceof Dialog)
            tmp = new JDialog((Dialog)component);
        else
            tmp = new JDialog((Frame)component);
        
        final JDialog dialog = tmp;
        
        JPanel buttonPanel = new JPanel();
        JButton close = new SimpleLocalButton(Options.getI18N(ExportProgressVisualisation.class), "Close");
        buttonPanel.add(close);
        close.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent arg0) {
                dialog.setVisible(false);
            }
        });
        
        dialog.setLayout(new BorderLayout());
        dialog.add(panel);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.setSize(new Dimension(800, 600));
        
        dialog.setModal(true);
        dialog.setVisible(true);
    }
    
    public DoppelgangerPanel(List<DoppelgangerGroup> groups) {

        this.groups = groups;
        
        groupPanel.setLayout(new BorderLayout());

        groupTable = new JTable();

        groupsTable = new JTable(new GroupsListModel(groups));
        groupsTable.setDefaultRenderer(String.class, new GroupsColorCellRenderer(groups));
        groupsTable.getSelectionModel().addListSelectionListener(new ListSelectionListener(){

            public void valueChanged(ListSelectionEvent arg0) {
                int rowIndex = groupsTable.getSelectedRow();
                showGroupTable(rowIndex);
            }
            
        });
        
        setLayout(new BorderLayout());
        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        split.add(new JScrollPane(groupsTable), JSplitPane.TOP);
        split.add(new JScrollPane(groupPanel), JSplitPane.BOTTOM);
        add(split);
    }

    protected void showGroupTable(int rowIndex) {
        groupPanel.removeAll();
        if (rowIndex == -1) {
            groupPanel.add(noGroupSelected);
        } else {
            groupTable.setDefaultRenderer(String.class, new GroupColorCellRenderer(groups.get(rowIndex)));
            groupTable.setModel(new GroupListModel(groups.get(rowIndex)));
            groupPanel.add(groupTable);
            groupPanel.revalidate();
        }
    }
    
    
    
}
