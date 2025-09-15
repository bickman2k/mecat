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
 * Created on Aug 20, 2004
 * @author Stephan Richard Palm
 */
package net.sourceforge.mecat.catalog.datamanagement.jdbc;

import java.awt.Graphics;
import java.util.Properties;

import net.sourceforge.mecat.catalog.datamanagement.CatalogFactory;
import net.sourceforge.mecat.catalog.datamanagement.Connection;
import net.sourceforge.mecat.catalog.datamanagement.Entry;
import net.sourceforge.mecat.catalog.datamanagement.Util;

public class JDBCConnection extends Connection {

	static CatalogFactory catalogFactory = new JDBCCatalogFactory();

	String Driver;
	String DSN;
	String Username;
	String Password;
	
	
	public JDBCConnection(Properties Prop) {
		this((String)Prop.get("Driver"),   (String)Prop.get("DSN"), 
			 (String)Prop.get("Username"), (String)Prop.get("Password"));
	}
	
	public JDBCConnection(String Driver, String DSN, String Username, String Password){
		this.Driver = Driver;
		this.DSN = DSN;
		this.Username = Username;
		this.Password = Password;
	}
	
	
	public CatalogFactory getCatalogFactory() {
		return catalogFactory;
	}

	public boolean loadFromEntry(Entry entry) {
		return true;
	}

	public void saveToEntry(Entry entry) {
		Util.addArgument(entry, new Util.Argument(0, String.class, Driver));
		Util.addArgument(entry, new Util.Argument(1, String.class, DSN));
		Util.addArgument(entry, new Util.Argument(2, String.class, Username));
		Util.addArgument(entry, new Util.Argument(3, String.class, Password));
	}

	public String toString() {
		return Driver + ":" + Username + "@" + DSN;
	}
	
	public boolean equals(Object o) {
		return (compareTo(o) == 0);
	}

	public int compareTo(Object o) {
		if (!(o instanceof JDBCConnection))
			return o.getClass().getName().compareTo(this.getClass().getName());
		
		JDBCConnection con = (JDBCConnection)o;
		
		if (con.Driver.compareTo(this.Driver) != 0)
			return con.Driver.compareTo(this.Driver);
		if (con.DSN.compareTo(this.DSN) != 0)
			return con.DSN.compareTo(this.DSN);
		if (con.Username.compareTo(this.Username) != 0)
			return con.Username.compareTo(this.Username);
		if (con.Password.compareTo(this.Password) != 0)
			return con.Password.compareTo(this.Password);
		
		return 0;
	}
	
    @Override
    public String getNameCutToSize(int width, Graphics g) {
        String abr = Username + ":" + Password;
        String rest = DSN;
        return abbreviation(width, abr, rest, g);
    }
}
