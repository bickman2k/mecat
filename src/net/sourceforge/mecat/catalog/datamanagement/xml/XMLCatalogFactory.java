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

package net.sourceforge.mecat.catalog.datamanagement.xml;

import java.awt.Component;
import java.net.MalformedURLException;

import org.apache.xerces.dom.CoreDocumentImpl;
import org.apache.xerces.parsers.DOMParser;
import org.w3c.dom.*;

import net.sourceforge.mecat.catalog.datamanagement.Catalog;
import net.sourceforge.mecat.catalog.datamanagement.CatalogFactory;
import net.sourceforge.mecat.catalog.datamanagement.Connection;
import net.sourceforge.mecat.catalog.datamanagement.Util;

import javax.swing.JFileChooser;

/**
 * Created on July 27, 2004
 * @author Stephan Richard Palm
 * 
 * TODO change from Connection=String Connection=URL/URI
 */
public class XMLCatalogFactory implements CatalogFactory
{

    public final static XMLCatalogFactory catalogFactory = new XMLCatalogFactory();
    

	/* (non-Javadoc)
	 * @see net.sourceforge.mecat.catalog.datamanagement.CatalogFactory#createCatalog()
	 */
	public Catalog createCatalog(Component parentComponent) {
        return createCatalog();
    }

    public Catalog createCatalog() {
        Document doc = new CoreDocumentImpl();
		Element catalogElement = doc.createElement("Catalog");
		doc.appendChild(catalogElement);

		XMLCatalog catalog = new XMLCatalog(catalogElement);

        Util.initCatalog(catalog);
        
        return catalog;
    }

    /* (non-Javadoc)
     * @see net.sourceforge.mecat.catalog.datamanagement.CatalogFactory#createCatalog()
     */
    public Catalog createCatalog(Connection connection) {
        Catalog catalog = createCatalog((Component) null);
        catalog.saveAsCatalog(connection);
        return catalog;
    }


	public Connection getOpenCatalogConnection(Component parentComponent) {
		JFileChooser choose = new JFileChooser();
		choose.setFileFilter(new XMLFiles());
		int returnVal = choose.showOpenDialog(null);
		if(returnVal == JFileChooser.APPROVE_OPTION) {
			try {
				return new XMLConnection(choose.getSelectedFile().toURL());
			} catch (MalformedURLException e) {
				return null;
			}
		}
		return null;
	}


	/* (non-Javadoc)
	 * @see net.sourceforge.mecat.catalog.datamanagement.CatalogFactory#openCatalog(net.sourceforge.mecat.catalog.datamanagement.CatalogFactory.Connection)
	 */
	public Catalog openCatalog(Connection connection) {
		if (!(connection instanceof XMLConnection))
			return null;
		XMLConnection con = (XMLConnection)connection;
		
		DOMParser parser = new DOMParser();
		
		try {
			parser.parse(con.pos.toString());
		} catch (org.xml.sax.SAXException e) {
			return null;
		} catch (java.io.IOException e) {
			return null;
		}
		Document doc = parser.getDocument();
		if (doc == null)
			return null;
		
		if (doc.getDocumentElement().getNodeName().compareTo("Catalog") != 0) {
			return null;
		}
		
        XMLCatalog catalog = new XMLCatalog(doc.getDocumentElement(), con);
        
        Util.updateCatalog(catalog);
        
		return catalog;
	}
}
