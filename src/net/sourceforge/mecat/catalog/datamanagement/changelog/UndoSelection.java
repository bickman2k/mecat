/*
 *  MeCat - Media Catalog more information on
 *  http://mecat.sourceforge.net/
 *  Copyright (C) 2005, Stephan Richard Palm, All Rights Reserved.
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
 * Created on Jun 22, 2005
 * @author Stephan Richard Palm
 */
package net.sourceforge.mecat.catalog.datamanagement.changelog;

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
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;

import net.sourceforge.mecat.catalog.export.ExportChoice;
import net.sourceforge.mecat.catalog.gui.MainFrameBackend;
import net.sourceforge.mecat.catalog.i18n.util.SimpleLocalButton;
import net.sourceforge.mecat.catalog.option.LocalListener;
import net.sourceforge.mecat.catalog.option.LocalListenerEvent;
import net.sourceforge.mecat.catalog.option.Options;
import net.sourceforge.mecat.catalog.option.WeakLocalListener;

public class UndoSelection extends JDialog implements LocalListener {

    final JButton undo = new SimpleLocalButton(Options.getI18N(MainFrameBackend.class), "Undo");
    final JButton cancel = new SimpleLocalButton(Options.getI18N(ExportChoice.class), "Cancel");

    final ChangeLog changeLog;
    
    boolean undoExecuted = false;
    
    public static boolean showUndoSelection(Component component, ChangeLog changeLog) {
        UndoSelection selector;

        while (component != null && !(component instanceof Frame) && !(component instanceof Dialog))
            component = component.getParent();

        if (component == null)
            selector = new UndoSelection(changeLog);
        else if (component instanceof Dialog)
            selector = new UndoSelection((Dialog)component, changeLog);
        else
            selector = new UndoSelection((Frame)component, changeLog);
        
        selector.setVisible(true);
        
        return selector.undoExecuted;
    }

    public UndoSelection(ChangeLog changeLog) {
        this.changeLog = changeLog;
        init();
    }

    public UndoSelection(Dialog dialog, ChangeLog changeLog) {
        super(dialog);
        this.changeLog = changeLog;
        init();
    }
        
    public UndoSelection(Frame frame, ChangeLog changeLog) {
        super(frame);
        this.changeLog = changeLog;
        init();
    }
        
    private void init() {
            
        this.setSize(new Dimension(640, 480));
        this.setModal(true);

        setLayout(new BorderLayout());

        final JTree tree = new JTree(changeLog.getTreeModel());

        undo.setEnabled(false);
        tree.addTreeSelectionListener(new TreeSelectionListener(){
            public void valueChanged(TreeSelectionEvent event) {
                TreePath path = tree.getSelectionPath();
                if (path == null) {
                    undo.setEnabled(false);
                    return;
                }
                
                Object o = path.getLastPathComponent();
                if (!(o instanceof ChangeTreeNode)) {
                    undo.setEnabled(false);
                    return;
                }

                if (o instanceof ChangeTreeTransactionNode) {
                    ChangeTreeTransactionNode tNode = (ChangeTreeTransactionNode)o;
                    if (tNode.getTransaction().userInvoked) {
                        undo.setEnabled(true);
                        undo.setText(Options.getI18N(UndoSelection.class).getString("Undo [START]-[END]")
                                .replaceAll("\\[START\\]", "" + (changeLog.indexOf(tNode) + 1))
                                .replaceAll("\\[END\\]", "" + changeLog.getNumberSteps()));
                    } else {
                        undo.setText(Options.getI18N(UndoSelection.class).getString("Undo"));
                        undo.setEnabled(false);
                    }
                    return;
                }
                if (o instanceof ChangeTreeEventNode) {
                    ChangeTreeEventNode eNode = (ChangeTreeEventNode)o;
                    ChangeTreeTransactionNode tNode = eNode.getParentNode();
                    
                    if (tNode != null) {
                        if (tNode.getTransaction().userInvoked) {
                            undo.setEnabled(true);
                            undo.setText(Options.getI18N(UndoSelection.class).getString("Undo [START]-[END]")
                                    .replaceAll("\\[START\\]", "" + (changeLog.indexOf(eNode) + 1))
                                    .replaceAll("\\[END\\]", "" + changeLog.getNumberSteps()));
                        } else {
                            undo.setText(Options.getI18N(UndoSelection.class).getString("Undo"));
                            undo.setEnabled(false);
                        }
                        return;
                    }
                }
                
                undo.setEnabled(false);
                return;
            }
        });
        
        undo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                TreePath path = tree.getSelectionPath();
                ChangeTreeNode node = (ChangeTreeNode) path.getLastPathComponent();
                int index = changeLog.indexOf(node);
                changeLog.undo(changeLog.getNumberSteps() - index);
                setVisible(false);
                undoExecuted = true;
            }
        });
        cancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                setVisible(false);
            }
        });
        
        tree.setCellRenderer(new TreeCellRenderer(){

            JTree dummyTree = new JTree();
            
            public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
                Component comp = dummyTree.getCellRenderer().getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
                
                if (!(value instanceof ChangeTreeTransactionNode)) {
                    comp.setFont(comp.getFont().deriveFont(java.awt.Font.PLAIN));
                    return comp;
                }
                
                ChangeTreeTransactionNode tNode = (ChangeTreeTransactionNode) value;
                
                
                if (tNode.getTransaction().isAtom()) {
                    comp.setFont(comp.getFont().deriveFont(java.awt.Font.BOLD));
                } else {
                    comp.setFont(comp.getFont().deriveFont(java.awt.Font.PLAIN));
                }
                if (!tNode.getTransaction().isUserInvoked()) {
                    comp.setFont(comp.getFont().deriveFont(comp.getFont().getStyle() | java.awt.Font.ITALIC));
                }
                
                return comp;
            }
            
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(undo);
        buttonPanel.add(cancel);
        
        add(new JScrollPane(tree));
        add(buttonPanel, BorderLayout.SOUTH);

        Options.addLocalListener(new WeakLocalListener(this));
        // Set labels to the current locale
        setLabels();
    }

    public void stateChanged(LocalListenerEvent event) {
        setLabels();
    }
    
    public void setLabels() {
        this.setTitle(Options.getI18N(UndoSelection.class).getString("Select the first step to be undone."));
    }


}
