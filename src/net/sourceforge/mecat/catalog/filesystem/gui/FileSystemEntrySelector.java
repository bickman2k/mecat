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
 * Created on Jun 9, 2006
 * @author Stephan Richard Palm
 * 
 */
package net.sourceforge.mecat.catalog.filesystem.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

import net.sourceforge.mecat.catalog.export.ExportChoice;
import net.sourceforge.mecat.catalog.filesystem.FileSystemEntry;
import net.sourceforge.mecat.catalog.filesystem.Result;
import net.sourceforge.mecat.catalog.filesystem.ResultModel;
import net.sourceforge.mecat.catalog.option.LocalListener;
import net.sourceforge.mecat.catalog.option.LocalListenerEvent;
import net.sourceforge.mecat.catalog.option.Options;
import net.sourceforge.mecat.catalog.option.WeakLocalListener;

public class FileSystemEntrySelector extends JDialog implements LocalListener {

/*    static enum StateType {
        Ok, Cancel
    }*/
    
    final JButton accept = new JButton();
    final JButton cancel = new JButton();
    final JTree tree;
//    StateType state = StateType.Cancel;
    FileSystemEntry result = null;

    public static FileSystemEntry showFileSystemEntrySelector(Component component, final Result result) {
        FileSystemEntrySelector selector;

        while (component != null && !(component instanceof Frame) && !(component instanceof Dialog))
            component = component.getParent();

        if (component instanceof Dialog)
            selector = new FileSystemEntrySelector((Dialog)component, result);
        else
            selector = new FileSystemEntrySelector((Frame)component, result);
        
        selector.setVisible(true);

        return selector.result;
    }
    
    protected FileSystemEntrySelector(Frame frame, final Result result) {
        super(frame);
        tree = new JTree(new ResultModel(result));
        init();
    }
    
    protected FileSystemEntrySelector(Dialog dialog, final Result result) {
        super(dialog);
        tree = new JTree(new ResultModel(result));
        init();
    }
    
    public FileSystemEntry getFileSystemEntry() {
        TreePath path = tree.getSelectionPath();
        if (path == null)
            return null;
        Object last = path.getLastPathComponent();
        if (last instanceof FileSystemEntry)
            return ( FileSystemEntry ) last;
        return null;
    }

    protected void init() {
        accept.setEnabled(false);
        
        final TreeSelectionListener treeSelectionListenern = new TreeSelectionListener(){
            public void valueChanged(TreeSelectionEvent event) {
                FileSystemEntry entry = getFileSystemEntry();
                accept.setEnabled(entry != null);
            }

        };
        tree.addTreeSelectionListener(treeSelectionListenern);  
        
        accept.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                result = getFileSystemEntry();
                setVisible(false);
            }
        });
        cancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                setVisible(false);
            }
        });
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(accept);
        buttonPanel.add(cancel);

        setLayout(new BorderLayout());
        add(tree);
        add(buttonPanel, BorderLayout.SOUTH);
        
        setSize(new Dimension(640, 480));
        setModal(true);
        
        
        Options.addLocalListener(new WeakLocalListener(this));
        // Set labels to the current locale
        setLabels();
    }

    public void stateChanged(LocalListenerEvent event) {
        setLabels();
    }
    
    public void setLabels() {
        accept.setText(Options.getI18N(ExportChoice.class).getString("OK"));
        cancel.setText(Options.getI18N(ExportChoice.class).getString("Cancel"));

        setTitle(Options.getI18N(FileSystemEntrySelector.class).getString("Choose file list entry"));
    }
    
}
