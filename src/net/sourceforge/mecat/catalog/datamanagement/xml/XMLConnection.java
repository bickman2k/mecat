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
 * Created on Aug 11, 2004
 * @author Stephan Richard Palm
 */
package net.sourceforge.mecat.catalog.datamanagement.xml;

import java.awt.Graphics;
import java.io.File;
import java.net.URL;

import net.sourceforge.mecat.catalog.datamanagement.CatalogFactory;
import net.sourceforge.mecat.catalog.datamanagement.Connection;
import net.sourceforge.mecat.catalog.datamanagement.Entry;
import net.sourceforge.mecat.catalog.datamanagement.Util;

public final class XMLConnection extends Connection {

	/**
	 * Static reference to a XMLCatalogFactory
	 * this variable is return when {@link #getCatalogFactory()} is invoked.
	 */
	static CatalogFactory catalogFactory = new XMLCatalogFactory();
	/**
	 * This is the heart of the connection.
	 * This URL says where to find the Catalog.
	 */
	protected URL pos;
	
	public XMLConnection() {
		this(null);
	}
	
	public XMLConnection(URL pos){
		this.pos = pos;
	}
	
	public CatalogFactory getCatalogFactory() {
		return catalogFactory;
	}
	
	/**
	 * Returns the URL of the Catalog behind the Connection.
	 */
	public String toString() {
		return pos.toString();
	}

	
	public boolean equals(Object o) {
		return (compareTo(o) == 0);
	}

	public int compareTo(Object o) {
		if (!(o instanceof XMLConnection))
			return o.getClass().getName().compareTo(this.getClass().getName());
		
		XMLConnection con = (XMLConnection)o;
		return con.pos.toString().compareTo(this.pos.toString());
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.mecat.catalog.datamanagement.Connection#loadFromEntry(net.sourceforge.mecat.catalog.datamanagement.Entry)
	 */
	public boolean loadFromEntry(Entry con) {
		return super.loadFromEntry(con);
	}

	/**
	 * Here is the reason why this class is final.
	 * It is final because it uses Util.addArgument.
	 * This will propably break if there is a subclass.
	 */
	public void saveToEntry(Entry con) {
		super.saveToEntry(con);
		Util.addArgument(con, new Util.Argument(0, null, pos));
	}

    @Override
    public String getNameCutToSize(int width, Graphics g) {
        String all = pos.toString();
        String ar[] = pos.getFile().split("/|\\\\");
        String file = ar[ar.length - 1];
        return abbreviation(width, all.substring(0, all.length() - file.length()), file, g);
    }
}
