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
 * Created on Jun 5, 2006
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
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

import net.sourceforge.mecat.catalog.filesystem.DirectoryEntry;
import net.sourceforge.mecat.catalog.filesystem.FileEntry;
import net.sourceforge.mecat.catalog.filesystem.FilePattern;
import net.sourceforge.mecat.catalog.filesystem.FileSystemEntry;
import net.sourceforge.mecat.catalog.filesystem.Result;
import net.sourceforge.mecat.catalog.filesystem.Util;
import net.sourceforge.mecat.catalog.gui.MainFrameBackend;
import net.sourceforge.mecat.catalog.gui.ToolBarUtils;
import net.sourceforge.mecat.catalog.i18n.util.SimpleLocalButton;
import net.sourceforge.mecat.catalog.option.Options;

public class Explorer extends JPanel  {

    final JTree tree;
    final JTable grid = new JTable();
    final JSplitPane split = new JSplitPane();

    Result result;
    FilePattern filter = null;
    Result filterResult;
    FilePattern search = null;
    FileEntry searchLast = null;
    
    public JButton getButton(ResourceBundle res, String key, String imageName, final Component parent) {
        // Only update the tooltip
        final SimpleLocalButton button = new SimpleLocalButton(res, null, key);
        ToolBarUtils.addImages(button, ToolBarUtils.class, imageName, button.getToolTip());
        return button;
    }
    
    /**
     * 
     * @param parent Toolbar can be placed on a different Dialog or Frame
     * therefor the real head-window is needed
     * @return
     */
    public JToolBar getToolBar(final Component parent) {
        JToolBar toolbar = new JToolBar();
        ResourceBundle menuResources = Options.getI18N(MainFrameBackend.class);

        JButton button = getButton(menuResources, "Filter", "funel", parent);
        button.addActionListener(new ActionListener(){ 
            public void actionPerformed(ActionEvent arg0) {
                openFilterManager(parent);
            }});
        toolbar.add(button);
        
        button = getButton(menuResources, "Search", "search", parent);
        button.addActionListener(new ActionListener(){ 
            public void actionPerformed(ActionEvent arg0) {
                openSearch(parent);
            }});
        toolbar.add(button);

        button = getButton(menuResources, "Detail", "tag", parent);
        button.addActionListener(new ActionListener(){ 
            public void actionPerformed(ActionEvent arg0) {
                openDetail();
            }});
        toolbar.add(button);

        return toolbar;
        
    }
    
    public void openFilterManager(Component component) {

        FilePattern pattern = FilePatternGui.getFilePattern(component, filter);
        if (pattern == null)
            return;
        
        filter = pattern;
        filterResult = filter.filterResult(result);
        tree.setModel(new ResultDirectoryModel(filterResult));
        gridChange();
    }
    
    public void openSearch(Component component) {
        FilePattern pattern = FilePatternGui.getFilePattern(component, search);
        if (pattern == null)
            return;
        search = pattern;
        
        searchLast = search.search(result);
        if (searchLast == null)
            return;
        
        select(searchLast);
    }
    
    public void select(FileEntry file) {
        FileSystemEntry entry = file;
        Vector<DirectoryEntry> pathObj = new Vector<DirectoryEntry>();
        while (entry.getParent() != null) {
            pathObj.add(0, entry.getParent());
            entry = entry.getParent();
        }
        TreePath path = new TreePath(pathObj.toArray());
        
        tree.setSelectionPath(path);
        final int index = ((ResultFileModel)grid.getModel()).getRowOf(searchLast);
        if (index == -1)
            return;
        
        grid.setRowSelectionInterval(index, index);
        SwingUtilities.invokeLater(new Runnable() {
            public void run(){
              grid.requestFocusInWindow();
              grid.changeSelection(index, index, false, false);
            }
          });
        }
    
    public void continueSearch() {
        if (searchLast == null || search == null)
            return;
        searchLast = search.search(searchLast);
        if (searchLast == null)
            return;

        select(searchLast);
    }
    
    public Explorer(){
        
        tree = new JTree();
        tree.addTreeSelectionListener(new TreeSelectionListener(){
            public void valueChanged(TreeSelectionEvent event) {
                gridChange();
            }
        });
        
        grid.setShowHorizontalLines(false);
        grid.addMouseListener(new MouseListener(){

            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2)
                    openDetail();
            }

            public void mouseEntered(MouseEvent arg0) {
                // TODO Auto-generated method stub
                
            }

            public void mouseExited(MouseEvent arg0) {
                // TODO Auto-generated method stub
                
            }

            public void mousePressed(MouseEvent arg0) {
                // TODO Auto-generated method stub
                
            }

            public void mouseReleased(MouseEvent arg0) {
                // TODO Auto-generated method stub
                
            }
            
        });
        
        split.add(new JScrollPane(tree), JSplitPane.LEFT);
        split.add(new JScrollPane(grid), JSplitPane.RIGHT);
        
        setLayout(new BorderLayout());
        add(split);
        
        
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0, false), "F3");
        getActionMap().put("F3", new AbstractAction(){
            public void actionPerformed(ActionEvent arg0) {
                continueSearch();
            }
        });
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_F, KeyEvent.CTRL_DOWN_MASK, false), "CTRL-F");
        getActionMap().put("CTRL-F", new AbstractAction(){
            public void actionPerformed(ActionEvent arg0) {
                openSearch(Explorer.this);
            }
        });
    }
    
    protected void openDetail() {
        int rowIndex = grid.getSelectedRow();
        if (rowIndex == -1)
            return;
        FileEntry fileEntry = ((ResultFileModel) grid.getModel()).getFile(rowIndex);
        if (fileEntry == null)
            return;
        
        Util.showDetails(fileEntry, this);
    }



    public void setResult(Result result) {
        this.result = result;
        this.filterResult = result;
        tree.setModel(new ResultDirectoryModel(this.filterResult));
        gridChange();
    }

    public void gridChange() {
        grid.setModel(new ResultFileModel(result, getSelectedDirectory()));
    }

    public DirectoryEntry getSelectedDirectory() {
        TreePath path = tree.getSelectionPath();
        if (path == null)
            return null;
        Object last = path.getLastPathComponent();
        if (last instanceof DirectoryEntry)
            return ( DirectoryEntry ) last;
        return null;
    }
    
    
}
