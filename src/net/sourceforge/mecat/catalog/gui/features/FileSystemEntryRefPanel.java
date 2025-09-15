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
package net.sourceforge.mecat.catalog.gui.features;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import net.sourceforge.mecat.catalog.filesystem.FileSystemEntry;
import net.sourceforge.mecat.catalog.filesystem.Result;
import net.sourceforge.mecat.catalog.filesystem.gui.FileSystemEntrySelector;
import net.sourceforge.mecat.catalog.filter.MediumFilter;
import net.sourceforge.mecat.catalog.gui.MediaListCellRenderer;
import net.sourceforge.mecat.catalog.gui.SelectMediaFrontend;
import net.sourceforge.mecat.catalog.gui.ToolBarUtils;
import net.sourceforge.mecat.catalog.gui.features.desktop.FeatureDesktop;
import net.sourceforge.mecat.catalog.medium.Medium;
import net.sourceforge.mecat.catalog.medium.features.BadCondition;
import net.sourceforge.mecat.catalog.medium.features.Feature;
import net.sourceforge.mecat.catalog.medium.features.impl.FileSystemEntryRef;
import net.sourceforge.mecat.catalog.medium.features.impl.RomFileList;
import net.sourceforge.mecat.catalog.medium.hidden.Rom;

public class FileSystemEntryRefPanel extends FeaturePanel<FileSystemEntryRef> {

    JPanel mediumPanel = new JPanel();
    JPanel fileSystemEntryPanel = new JPanel();
    
    MediaListCellRenderer mediaListCellRenderer = new MediaListCellRenderer();
    
    static final JList dummylist = new JList();
    
    public FileSystemEntryRefPanel(final FileSystemEntryRef feature, FeatureDesktop desktop,
            boolean Border) {
        super(feature, desktop, Border, feature.getClass().getSimpleName());
        // TODO Auto-generated constructor stub
        setLayout(new GridLayout(0,1));

        mediumPanel.setLayout(new BorderLayout());
        fileSystemEntryPanel.setLayout(new BorderLayout());

        add(mediumPanel);
        add(fileSystemEntryPanel);
        
        change();
    }
    
    protected void change() {
        mediumPanel.removeAll();
        if (feature.getRefRom() == null) {
            mediumPanel.add(new JButton(res.getString("Choose rom")){{
                addActionListener(new ActionListener(){
                    public void actionPerformed(ActionEvent arg0) {
                        chooseRom();
                    }
                });
            }});
        } else {
            mediumPanel.add(mediaListCellRenderer.getListCellRendererComponent(dummylist, feature.getRefRom(), -1, false, false));
            mediumPanel.add(ToolBarUtils.makeButton("kill", "", "", "", new ActionListener(){
                public void actionPerformed(ActionEvent arg0) {
                    feature.setRefRom(null);
                    change();
                }
            }), BorderLayout.EAST);
        }
        
        fileSystemEntryPanel.removeAll();
        if (feature.getFileSystemEntry() == null) {
            fileSystemEntryPanel.add(new JButton(res.getString("Choose file system entry")){{
                addActionListener(new ActionListener(){
                    public void actionPerformed(ActionEvent arg0) {
                        chooseFileSystemEntry();
                    }
                });
                setEnabled(feature.getRefRom() != null);
            }});
        } else {
            fileSystemEntryPanel.add(new JLabel(feature.getFileSystemEntry().getFullName()));
            fileSystemEntryPanel.add(ToolBarUtils.makeButton("kill", "", "", "", new ActionListener(){
                public void actionPerformed(ActionEvent arg0) {
                    feature.setFileSystemEntry(null);
                    change();
                }
            }), BorderLayout.EAST);
        }

        fileSystemEntryPanel.setLayout(new GridLayout(0,1));
        this.revalidate();
    }

    protected void chooseRom() {
/*        SelectMediaFrontend select = new SelectMediaFrontend(); 
        select.setCatalog(feature.medium.getListing().catalog);
        select.setFilter(new MediumFilter(Rom.class){

            @Override
            public boolean eval(Medium medium) throws BadCondition {
                return (medium instanceof Rom);
            }
            
        });
        
        select.setVisible(true);
        Medium medium = select.getMedium();*/
        
        Medium medium = SelectMediaFrontend.showSelectMediaFrontend(this, feature.medium.getListing().catalog,
                new MediumFilter(Rom.class){
                    @Override
                    public boolean eval(Medium medium) throws BadCondition {
                        return (medium instanceof Rom);
                    }});
        
        if (medium == null)
            return;
        if (!(medium instanceof Rom))
            return;
        feature.setRefRom((Rom) medium);
        change();
    }

    protected void chooseFileSystemEntry() {
        Rom rom = feature.getRefRom();
        if (rom == null)
            return;
        RomFileList romFileList = rom.getRomFileList();
        if (!romFileList.hasResult())
            JOptionPane.showMessageDialog(this, res.getString("There is no file list for the selected rom."));

        Result result = romFileList.getResult();

        FileSystemEntry fileSystemEntry = FileSystemEntrySelector.showFileSystemEntrySelector(this, result);

        if (fileSystemEntry == null)
            return;
        
        feature.setFileSystemEntry(fileSystemEntry);
        
        change();
        
    }

    public void featureValueChanged(Feature source) {
        // TODO Auto-generated method stub
        
    }
}
