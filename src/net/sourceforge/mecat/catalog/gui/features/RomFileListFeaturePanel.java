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
 * Created on Oct 6, 2004
 * @author Stephan Richard Palm
 */
package net.sourceforge.mecat.catalog.gui.features;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.border.TitledBorder;
import javax.swing.tree.TreePath;

import net.sourceforge.mecat.catalog.filesystem.FileEntry;
import net.sourceforge.mecat.catalog.filesystem.Result;
import net.sourceforge.mecat.catalog.filesystem.ResultModel;
import net.sourceforge.mecat.catalog.filesystem.Util;
import net.sourceforge.mecat.catalog.filesystem.gui.ExplorerDesktop;
import net.sourceforge.mecat.catalog.filesystem.gui.ExplorerDesktopFrame;
import net.sourceforge.mecat.catalog.filesystem.gui.ExplorerDialog;
import net.sourceforge.mecat.catalog.gui.ToolBarUtils;
import net.sourceforge.mecat.catalog.gui.features.desktop.ChainableFeatureDesktop;
import net.sourceforge.mecat.catalog.gui.features.desktop.FeatureDesktop;
import net.sourceforge.mecat.catalog.gui.utils.PopupMouseListener;
import net.sourceforge.mecat.catalog.medium.features.Feature;
import net.sourceforge.mecat.catalog.medium.features.impl.RomFileList;
import net.sourceforge.mecat.catalog.option.Options;
import net.sourceforge.mecat.srp.utils.ButtonBorder;

public class RomFileListFeaturePanel extends FeaturePanel<RomFileList> {

    public static interface FinishReadingFileList {
        public void regularFinished(Result result);
        public void brokeFinished();
    }
    
    JComponent component;

	public RomFileListFeaturePanel(final RomFileList feature, final FeatureDesktop desktop, boolean border) 
	{
		super(feature, desktop, border, feature.getClass().getSimpleName());
        
        // Make an upgrade to the allready existing border
        if (border) {
            JButton buttons[] = new JButton[feature.isRemovable() ? 2 : 1];
            buttons[0] = ToolBarUtils.makeMiniButton("plus", "", "", "", new ActionListener(){
                public void actionPerformed(ActionEvent arg0) {
                    openExlporerWindow(RomFileListFeaturePanel.this, desktop, feature);
                }

            });
            if (feature.isRemovable())
                buttons[1] = ToolBarUtils.makeMiniButton("kill", "", "", "", new ActionListener(){
                    public void actionPerformed(ActionEvent arg0) {
                        deleteFileList();
                    }
    
                });
            TitledBorder titledBorder = (TitledBorder) getBorder();
            ButtonBorder buttonBorder = new ButtonBorder(this, titledBorder, buttons);
            setBorder(buttonBorder);
        }

        change();
	}
    
    public static void openExlporerWindow(Component component, FeatureDesktop desktop, RomFileList rfl) {
        if (desktop instanceof ChainableFeatureDesktop) {
            ExplorerDesktopFrame frame = new ExplorerDesktopFrame((ChainableFeatureDesktop)desktop, rfl);
            frame.setVisible(true);
        } else {
            ExplorerDialog.showExplorer(rfl.getResult(), component);
        }
    }

    protected void change()  {
        if (component != null)
            remove(component);
        if (feature.hasResult()) {
            JTree tree = getTree();
            add(tree);
            component = tree;
        } else {
            JButton button = getButton();
            add(button);
            component = button;
        }
    }
    
    protected JTree getTree() {
        Result result = feature.getResult();
        final JTree tree = new JTree(new ResultModel(result));
        
        tree.addMouseListener(new PopupMouseListener(){
            public JPopupMenu getPopupMenu() {
                return RomFileListFeaturePanel.this.getJPopupMenu();
            }
        });
        tree.addMouseListener(new MouseListener(){

            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2)
                    openDetail(tree);
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
        
        return tree;
    }
    
    protected void openDetail(JTree tree) {
        TreePath path = tree.getSelectionPath();
        if (path == null)
            return;
        Object last = path.getLastPathComponent();
        if (!(last instanceof FileEntry))
            return;

        Util.showDetails((FileEntry) last, this);
    }

    protected JButton getButton() {
        final JButton button = new JButton(res.getString("Read file list"));
        button.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent arg0) {
                button.setEnabled(false);

                feature.readFileList(RomFileListFeaturePanel.this, new FinishReadingFileList() {

                    public void regularFinished(Result result) {
                        change();
                        fireRebuild();
                    }

                    public void brokeFinished() {
                        button.setEnabled(false);
                    }
                    
                });
                
            }
        });
        
        return button;
    }
    
    protected void deleteFileList() {
        int ret = JOptionPane.showConfirmDialog(this, res.getString("Do you realy want to remove all information about the files an the media?"), res.getString("Delete file list"), JOptionPane.YES_NO_OPTION);
        if (ret == JOptionPane.YES_OPTION) {
            remove(component);
            feature.removeResult();
            change();
            fireRebuild();
        }
    }

	protected JPopupMenu getJPopupMenu() {
		JPopupMenu menu = super.getJPopupMenu();
        
        final JMenuItem remove = new JMenuItem(Options.getI18N(RomFileListFeaturePanel.class).getString("Delete"));

		remove.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {/*LOOK*/ deleteFileList(); /*HERE*/}});
        
        menu.addSeparator();
		menu.add(remove);
		
		return menu;
	}
    public void requestFocus() {
        component.requestFocus();
    }
    
    public boolean hasFocus() {
        return component.hasFocus();
    }

    public void featureValueChanged(Feature source) {
        // TODO Auto-generated method stub
        
    }
}
