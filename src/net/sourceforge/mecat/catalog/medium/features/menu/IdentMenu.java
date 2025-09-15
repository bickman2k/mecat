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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.UUID;

import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;

import net.sourceforge.mecat.catalog.gui.customMenu.CustomMenuRegister;
import net.sourceforge.mecat.catalog.gui.customMenu.MenuFactory;
import net.sourceforge.mecat.catalog.medium.Listing;
import net.sourceforge.mecat.catalog.medium.Medium;
import net.sourceforge.mecat.catalog.medium.features.impl.Ident;
import net.sourceforge.mecat.catalog.option.Options;

public class IdentMenu {
    public static void registerMenu() {
        final ResourceBundle res = Options.getI18N(Ident.class);
        CustomMenuRegister.addFeatureMenu(Ident.class, new MenuFactory(){
            public JMenuItem createMenu(final Listing listing) {
                final JMenu menu = new JMenu(res.getString("Ident"));
                menu.add(new JMenuItem(res.getString("Check consistency.")){{
                    this.setToolTipText(res.getString("Checks whether two entries have the same Ident (UUID)."));
                    addActionListener(new ActionListener(){
                        public void actionPerformed(ActionEvent arg0) {
                            Map<UUID, Set<Medium>> UUIDs = new LinkedHashMap<UUID, Set<Medium>>(listing.getSize());
                            for (Medium medium : listing)  {
                                UUID uuid = medium.getFeature(Ident.class).getUUID();
                                Set<Medium> set = UUIDs.get(uuid);
                                if  (set == null) {
                                    set = new LinkedHashSet<Medium>();
                                    UUIDs.put(uuid, set);
                                }
                                set.add(medium);
                            }
                            StringBuffer log = new StringBuffer();
                            for (Map.Entry<UUID, Set<Medium>> entry : UUIDs.entrySet()) {
                                if (entry.getValue().size() > 1) {
                                    System.out.println(res.getString("FoundEntriesWrongID").replaceAll("\\[NUMBER\\]", String.valueOf(entry.getValue().size())).replaceAll("\\[UUID\\]", entry.getKey().toString()));
                                    log.append(res.getString("FoundEntriesWrongID").replaceAll("\\[NUMBER\\]", String.valueOf(entry.getValue().size())).replaceAll("\\[UUID\\]", entry.getKey().toString()) + "<br>");
                                    for (Medium medium : entry.getValue()) {
                                        System.out.println(medium);
                                        log.append(medium.toString() + "<br>");
                                    }
                                }
                            }
                            if (log.length() > 0) {
                                JLabel txt = new JLabel("<html>" + log.toString() + "</html>");
                                JOptionPane.showMessageDialog(menu, new JScrollPane(txt), res.getString("Found consistency problems."), JOptionPane.ERROR_MESSAGE);
                            } else
                                JOptionPane.showMessageDialog(menu, new JLabel("<html>" + res.getString("No consistency problems where found.") + "<br>" + res.getString("Every media has its own Id.") + "</html>"), res.getString("No consistency problems."), JOptionPane.INFORMATION_MESSAGE);
                            
                                
                        }
                    });
                }});
                menu.add(new JMenuItem(res.getString("Restore consistency.")){{
                    this.setToolTipText(res.getString("Every entries becomes an individual Ident (UUID)."));
                    addActionListener(new ActionListener(){
                        public void actionPerformed(ActionEvent arg0) {
                            Map<UUID, Set<Medium>> UUIDs = new LinkedHashMap<UUID, Set<Medium>>(listing.getSize());
                            for (Medium medium : listing)  {
                                UUID uuid = medium.getFeature(Ident.class).getUUID();
                                Set<Medium> set = UUIDs.get(uuid);
                                if  (set == null) {
                                    set = new LinkedHashSet<Medium>();
                                    UUIDs.put(uuid, set);
                                }
                                set.add(medium);
                            }
                            for (Map.Entry<UUID, Set<Medium>> entry : UUIDs.entrySet()) {
                                if (entry.getValue().size() > 1) {
                                    for (Medium medium : entry.getValue())
                                        medium.getFeature(Ident.class).setUUID(UUID.randomUUID());
                                }
                            }
                                
                        }
                    });
                }});
                return menu;
            }

            public boolean allwaysVisible() {
                return false;
            }});
    }
}
