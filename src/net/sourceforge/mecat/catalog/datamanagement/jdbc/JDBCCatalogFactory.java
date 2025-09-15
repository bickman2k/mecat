/**
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
 * Created on Aug 20, 2004
 * @author Stephan Richard Palm
 */
package net.sourceforge.mecat.catalog.datamanagement.jdbc;

import java.awt.Component;
import java.util.Properties;

import javax.swing.JOptionPane;

import net.sourceforge.mecat.srp.utils.ConnectionPanel;

import net.sourceforge.mecat.catalog.datamanagement.Catalog;
import net.sourceforge.mecat.catalog.datamanagement.CatalogFactory;
import net.sourceforge.mecat.catalog.datamanagement.Connection;
import net.sourceforge.mecat.catalog.datamanagement.Util;

public class JDBCCatalogFactory implements CatalogFactory {

	public Catalog createCatalog(Component parentComponent) {
        JDBCConnection connection = getOpenCatalogConnection(parentComponent);
        
        JDBCCatalog catalog = new JDBCCatalog(connection);
        
        Util.initCatalog(catalog);
        
        return catalog;
    }

    public Catalog createCatalog(Connection connection) {
        if (!(connection instanceof JDBCConnection))
            return null;
        JDBCConnection jdbcConnection = (JDBCConnection) connection;
        
        JDBCCatalog catalog = new JDBCCatalog(jdbcConnection);
        
        Util.initCatalog(catalog);
        
        return catalog;
    }

	public JDBCConnection getOpenCatalogConnection(Component parentComponent) {
		ConnectionPanel cPanel = new ConnectionPanel(false);
		if (!(JOptionPane.showConfirmDialog(parentComponent, cPanel, "DBConnection", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION))
			return null;
		Properties Props = new Properties();
		return new JDBCConnection(cPanel.SetPropertiesFromTextFields(Props));
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.mecat.catalog.datamanagement.CatalogFactory#openCatalog(net.sourceforge.mecat.catalog.datamanagement.Connection)
	 */
	public Catalog openCatalog(Connection connection) {
		if (!(connection instanceof JDBCConnection))
            return null;

        JDBCCatalog catalog = new JDBCCatalog((JDBCConnection)connection);
        
        Util.updateCatalog(catalog);
        // TODO set change listener to no changes made
        
        return catalog;
	}

}
