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
 * Created on Jun 29, 2006
 * @author Stephan Richard Palm
 * 
 */
package net.sourceforge.mecat.catalog.medium.features.menu;

import static net.sourceforge.mecat.catalog.filesystem.store.MD5Thread.isNull;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import net.sourceforge.mecat.catalog.filesystem.Comparators;
import net.sourceforge.mecat.catalog.filesystem.FileEntry;
import net.sourceforge.mecat.catalog.filesystem.Result;
import net.sourceforge.mecat.catalog.filesystem.gui.Explorer;
import net.sourceforge.mecat.catalog.filesystem.gui.ExplorerDialog;
import net.sourceforge.mecat.catalog.filesystem.gui.doppelganger.Doppelganger;
import net.sourceforge.mecat.catalog.filesystem.gui.doppelganger.DoppelgangerGroup;
import net.sourceforge.mecat.catalog.filesystem.gui.doppelganger.DoppelgangerPanel;
import net.sourceforge.mecat.catalog.filesystem.load.FileSystemLoadShallow;
import net.sourceforge.mecat.catalog.filesystem.load.VersionException;
import net.sourceforge.mecat.catalog.filesystem.store.FileSystemStoreShallow;
import net.sourceforge.mecat.catalog.filesystem.virtual.Mount;
import net.sourceforge.mecat.catalog.filesystem.virtual.VirtualResult;
import net.sourceforge.mecat.catalog.gui.customMenu.CustomMenuRegister;
import net.sourceforge.mecat.catalog.gui.customMenu.MenuFactory;
import net.sourceforge.mecat.catalog.gui.log.AbstractLogable;
import net.sourceforge.mecat.catalog.gui.log.LogDialog;
import net.sourceforge.mecat.catalog.medium.Listing;
import net.sourceforge.mecat.catalog.medium.Medium;
import net.sourceforge.mecat.catalog.medium.features.impl.ImplRomFileList;
import net.sourceforge.mecat.catalog.medium.features.impl.RomFileList;
import net.sourceforge.mecat.catalog.medium.features.impl.Title;
import net.sourceforge.mecat.catalog.medium.features.impl.VirtualRomFileList;
import net.sourceforge.mecat.catalog.medium.hidden.Rom;
import net.sourceforge.mecat.catalog.option.Options;

public class RomFileListMenu {

    static void updateOldVersions(final Component component, final Listing listing) {
        final ResourceBundle res = Options.getI18N(VirtualRomFileList.class);
        
        class LogableRunnable extends AbstractLogable  {

            public void run() {
                Vector<Rom> roms = new Vector<Rom>();
                for (Medium medium : listing) 
                    if (medium instanceof Rom)
                        roms.add((Rom) medium);

                int numDirs = 0;
                int numFiles = 0;
                
                for (Rom rom : roms) {
                    RomFileList romFileList = rom.getRomFileList();
                    
                    if (!(romFileList instanceof ImplRomFileList))
                        continue;

                    if (!romFileList.hasResult())
                        continue;

                    ImplRomFileList implRomFileList = (ImplRomFileList) romFileList;

                    final File file = implRomFileList.getFileListFile();

                    if (file == null)
                        continue;
                    
                    try {
                        if (FileSystemLoadShallow.isCurrent(file))
                            continue;
                    } catch (IOException e) {
                        e.printStackTrace();
                        continue;
                    } catch (VersionException e) {
                        e.printStackTrace();
                        continue;
                    }
                    
                    fireMessage(res.getString("The rom [ROM] has a file system with old version, rebuilding it...").replaceAll("\\[ROM\\]", rom.toString()));
                    
                    FileSystemStoreShallow.store(file, romFileList.getResult());
                    
                    fireMessage(res.getString("[ROM] has been rebuild.").replaceAll("\\[ROM\\]", rom.toString()));
                    
                }
                
                fireFinished();
            }
            
        };
        
        LogableRunnable update = new LogableRunnable();
        LogDialog.showLogDialog(component, update);
        
        
    };

    static void getStats(Component component, Listing listing) {
        Vector<Rom> roms = new Vector<Rom>();
        for (Medium medium : listing) 
            if (medium instanceof Rom)
                roms.add((Rom) medium);

        int numDirs = 0;
        int numFiles = 0;
        
        for (Rom rom : roms) {
            RomFileList romFileList = rom.getRomFileList();

            if (!romFileList.hasResult())
                continue;

            Result result = romFileList.getResult();

            numDirs += result.getNumDirs();
            numFiles += result.getNumFiles();
        }
        
        JOptionPane.showMessageDialog(component, "Dirs: " + numDirs + "  Files: " + numFiles, "Dirs" + numDirs + " Files:" + numFiles, JOptionPane.INFORMATION_MESSAGE);
    };

    static void showAllInOne(final Listing listing, final boolean mergeRoot, Component component) {
        Vector<Mount> mounts = new Vector<Mount>();
        for (Medium medium : listing) {
            if (!(medium instanceof Rom))
                continue;
            
            Rom rom = (Rom) medium;
            RomFileList romFileList = rom.getRomFileList();
            // Only take those rom file list that actualy contain a list
            if (!romFileList.hasResult())
                continue;
            
            Result result = romFileList.getResult();
 
            String root = VirtualResult.rootName;

            if (!mergeRoot)
                root += System.getProperty("file.separator") + rom.getFeature(Title.class).get();  

            Mount mount  = new Mount(result.getDirectory(), root);  
            mounts.add(mount);
        }
        VirtualResult allInOne = new VirtualResult(mounts);

        ExplorerDialog.showExplorer(allInOne, component);
    }
    
    static void searchForDoppelganger(final Component parent, final Listing listing) {
        Vector<Rom> roms = new Vector<Rom>();
        for (Medium medium : listing) 
            if (medium instanceof Rom)
                roms.add((Rom) medium);
        
        searchForDoppelganger(parent, roms);
    }
    
    static void searchForDoppelganger(final Component parent, final List<Rom> roms) {
        Vector<FileEntry> entriesMD5 = new Vector<FileEntry>();
        for (Rom rom : roms) {
            RomFileList romFileList = rom.getRomFileList();

            if (!romFileList.hasResult())
                continue;

            Result result = romFileList.getResult();
            FileEntry entry = result.getFirstMD5();
            if (entry != null)
                entriesMD5.add(entry);

/*            Class cls[] = new Class[] {
                    net.sourceforge.mecat.catalog.filesystem.store.Result.class,
                    net.sourceforge.mecat.catalog.filesystem.load.Result.class,
                    net.sourceforge.mecat.catalog.filesystem.virtual.VirtualResult.class,
                    Result.class,
                    String.class
            };

            for (Class c : cls) {
                System.out.println(c.getName());
                if (c.getSigners() != null) 
                    for (Object o : Result.class.getSigners()) 
                        System.out.println(o);
            }
            
            System.out.println();*/
        }

        Vector<FileEntry> enrtiesMD5NotNull = new Vector<FileEntry>();
        // Go to a point where we can compare with md5
        for (FileEntry entry : entriesMD5) {
            while (entry != null && isNull(entry.getMD5SUM()))
                entry = entry.getNextMD5();
            if (entry != null)
                enrtiesMD5NotNull.add(entry);
        }

        Vector<Vector<FileEntry>> doppelgangers = new Vector<Vector<FileEntry>>();
        
        while (!enrtiesMD5NotNull.isEmpty()) {
            Vector<FileEntry> doppelganger = new Vector<FileEntry>();
            
            // Sort the entry chains
            Collections.sort(enrtiesMD5NotNull, Comparators.MD5Comparator);
            // Get representativ first element
            FileEntry entry = enrtiesMD5NotNull.get(0);

            int num = 0;
            do {
                FileEntry possibleMatch = enrtiesMD5NotNull.get(num);
                while (possibleMatch != null && Comparators.MD5Comparator.compare(entry, possibleMatch) == 0) {
                    // Found entry with the same md5
                    doppelganger.add(possibleMatch);

                    // Replace entry with the next from the same result
                    // first remove the current entry
                    enrtiesMD5NotNull.remove(num);
                    // get the next entry
                    possibleMatch = possibleMatch.getNextMD5();
                    // if the next entry is not null add it again
                    // at the same position the previous element was
                    if (possibleMatch != null)
                        enrtiesMD5NotNull.add(num, possibleMatch);
                    // if it is null then don't readd anything
                    // but tell the do while loop to look 
                    // at the right position (num--)
                    else
                        num--;                        
                }
                
                // Go to the next entry chain
                num++;
            } while (num < enrtiesMD5NotNull.size() && Comparators.MD5Comparator.compare(entry, enrtiesMD5NotNull.get(num)) == 0 );

            if (doppelganger.size() > 1) {
                doppelgangers.add(doppelganger);
            }
            
        }
        System.out.println("Found " + doppelgangers.size() + " doppelgangers.");
        Vector<DoppelgangerGroup> groups = new Vector<DoppelgangerGroup>(doppelgangers.size());
        for (Vector<FileEntry> doppelganger : doppelgangers) {
            DoppelgangerGroup group = new DoppelgangerGroup(DoppelgangerGroup.DoppelgangerGroupType.SureOnly);
            
            for (FileEntry entry : doppelganger)
                group.add(new Doppelganger(entry, Doppelganger.DoppelgangerType.Sure));
            
            groups.add(group);
        }
        
        
        DoppelgangerPanel.showDoppelganger(parent, groups);
    }
    
    final static private MenuFactory menuFactory = new MenuFactory(){
        public JMenuItem createMenu(final Listing listing) {
            final ResourceBundle res = Options.getI18N(VirtualRomFileList.class);
            final JMenu menu = new JMenu(res.getString("VirtualRomFileList"));
            JMenuItem allInOne = new JMenuItem(res.getString("Show roms as all in medium")){{
                addActionListener(new ActionListener(){
                    public void actionPerformed(ActionEvent arg0) {
                        showAllInOne(listing, false, menu);
                    }});
            }};
            JMenuItem allInOneMerge = new JMenuItem(res.getString("Show roms as all in medium with merged root")){{
                addActionListener(new ActionListener(){
                    public void actionPerformed(ActionEvent arg0) {
                        showAllInOne(listing, true, menu);
                    }});
            }};
            JMenuItem searchDoppelganger = new JMenuItem(res.getString("Search for doppelganger")){{
                addActionListener(new ActionListener(){
                    public void actionPerformed(ActionEvent arg0) {
                        searchForDoppelganger(menu, listing);
                    }});
            }};
            JMenuItem getStats = new JMenuItem(res.getString("Get global stats")){{
                addActionListener(new ActionListener(){
                    public void actionPerformed(ActionEvent arg0) {
                        getStats(menu, listing);
                    }});
            }};
            JMenuItem updateOldVersions = new JMenuItem(res.getString("Update file system with older versions")){{
                addActionListener(new ActionListener(){
                    public void actionPerformed(ActionEvent arg0) {
                        updateOldVersions(menu, listing);
                    }});
            }};
            menu.add(allInOne);
            menu.add(allInOneMerge);
            menu.add(searchDoppelganger);
            menu.add(getStats);
            menu.add(updateOldVersions);
            return menu;
        }

        public boolean allwaysVisible() {
            return false;
        }};
    
    public static void registerMenu() {
        // Add to all implementation of RomFileList
        CustomMenuRegister.addFeatureMenu(ImplRomFileList.class, menuFactory);
        CustomMenuRegister.addFeatureMenu(VirtualRomFileList.class, menuFactory);
    }
}
