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
import java.util.ResourceBundle;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import net.sourceforge.mecat.catalog.gui.customMenu.CustomMenuRegister;
import net.sourceforge.mecat.catalog.gui.customMenu.MenuFactory;
import net.sourceforge.mecat.catalog.gui.options.features.IMDB.CheckWithIMDB;
import net.sourceforge.mecat.catalog.medium.Listing;
import net.sourceforge.mecat.catalog.medium.features.impl.IMDBTitleNumber;
import net.sourceforge.mecat.catalog.option.Options;

public class IMDBTitleNumberMenu {
    public static void registerMenu() {
        final ResourceBundle res = Options.getI18N(IMDBTitleNumber.class);
        CustomMenuRegister.addFeatureMenu(IMDBTitleNumber.class, new MenuFactory(){
            public JMenuItem createMenu(final Listing listing) {
                final JMenu menu = new JMenu(res.getString("IMDBTitleNumber"));
                JMenuItem checkTitle = new JMenuItem(res.getString("Check with IMDB")){{
                    addActionListener(new ActionListener(){
                        public void actionPerformed(ActionEvent arg0) {
                            CheckWithIMDB check = new CheckWithIMDB(menu, listing);
                            
                            check.runCheck();
                            

                        }});
                }};
                menu.add(checkTitle);
                return menu;
            }

            public boolean allwaysVisible() {
                return false;
            }});
    }

}
