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

import java.awt.Component;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

import javax.swing.JOptionPane;

import net.sourceforge.mecat.catalog.datamanagement.Catalog;
import net.sourceforge.mecat.catalog.datamanagement.Connection;
import net.sourceforge.mecat.catalog.datamanagement.Entry;
import net.sourceforge.mecat.catalog.datamanagement.Identifier;
import net.sourceforge.mecat.srp.utils.ConnectionPanel;

public class JDBCCatalog extends Catalog {

	/**
	 * The name for the table that stores the information
	 * where every type, i.e. into wich table, is stored.
	 * I like to call it 3T. ;)
	 * 
	 * I.e. Table where you can find the translation from
	 * type to table name.
	 */
	final String typeToTable = "TypeToTable";
	final String typeToTable_Type = "Type";
	final String typeToTable_Table = "TableName";
	
//	Set<String> types = new TreeSet<String>();
	Map<String, String> types = new HashMap<String, String>();
	
	/**
	 * Name of all tables
	 */
	Set<String> tables = new TreeSet<String>();
	
	/**
	 * SQL Connection to the db. The connection will be open all the time.
	 */
	java.sql.Connection sqlCon = null;
	JDBCConnection connection = null;

	
	void getTypeToTable() {
		Statement stmt = null;
		ResultSet rs = null;
		String cmd = "select * from " + typeToTable + ";";
		
		try {
			stmt = sqlCon.createStatement();
			rs = stmt.executeQuery(cmd);
			
			while (rs.next()) {
				String type = rs.getString(typeToTable_Type);
				String table = rs.getString(typeToTable_Table);
				types.put(type, table);
				tables.add(table);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {rs.close();} catch (Exception e1) {}
		}
	}
	
	protected JDBCCatalog(JDBCConnection connection) {
		try{			
			Class.forName(connection.Driver);
			sqlCon = DriverManager.getConnection(connection.DSN, connection.Username, connection.Password);
		} catch (SQLException e) {
			e.printStackTrace();
			try {sqlCon.close();} catch (Exception e1) {}
			return;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			try {sqlCon.close();} catch (Exception e1) {}
			return;
		}
		
		getTypeToTable();
		tables.add(typeToTable);
		
		this.connection = connection;
	}
    
    public void setConnection(Connection connection) {
        
    }
	
	public boolean saveCatalog() {
		if (connection == null)
			return false;
		return true;
	}

	public boolean saveAsCatalog(Connection connection) {
		// TODO Auto-generated method stub
		if (connection == null)
			return false;

		return false;
	}

	public boolean canSave() {
		return false;
	}

	public Connection getSaveCatalogConnection(Component parentComponent) {
		ConnectionPanel cPanel = new ConnectionPanel(connection.Driver, connection.DSN, connection.Username, connection.Password, false);
		if (!(JOptionPane.showConfirmDialog(parentComponent, cPanel, "DBConnection", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION))
			return null;
		Properties Props = new Properties();
		return new JDBCConnection(cPanel.SetPropertiesFromTextFields(Props));
	}

	public Connection getConnection() {
		return connection;
	}

//	public void setLanguage(Locale language) {
//		// TODO Auto-generated method stub
//	}
//	public Locale getLanguage() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//	public void setDescription(String Description, Locale language) {
//		// TODO Auto-generated method stub
//	}
//	public String getDescription(Locale language) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//	public void setName(String name, Locale language) {
//		// TODO Auto-generated method stub
//	}
//	public String getName(Locale language) {
//		// TODO Auto-generated method stub
//		return null;
//	}

	boolean letter(char c) {
		if ((c >= 'a') && (c <= 'z'))
			return true;
		
		if ((c >= 'A') && (c <= 'A'))
			return true;
		
		return false;
	}
	
	boolean number(char c) {
		if ((c >= '0') && (c <= '9'))
			return true;
		
		return false;
	}
	
	boolean letnum(char c) {
		return letter(c) || number(c);
	}
	
	boolean allowedChar(char c) {
		return letnum(c);
	}
	
	String ansi(String in){
		StringBuffer ret = new StringBuffer();
		for (int i = 0; i < in.length(); i++) 
			if (allowedChar(in.charAt(i)))
				ret.append(in.charAt(i));			
		if (ret.length() == 0)
			ret.append("_");
		return ret.toString();
	}
	
	String newType(String type) {
		Statement stmt = null;
		ResultSet rs = null;
				
		String base_name = ansi(type);
		String name = base_name;
		
		for (int i = 0; tables.contains(name); i++) 
			name = base_name + "_" + String.valueOf(i);
				
		String cmd = "insert into " + typeToTable + " (" + typeToTable_Type + ", " + typeToTable_Table 
						+ ") VALUES(" + type + "," + name + ");";
		
		String cmd2 = "create table " + name + " (entry_id NUMBER, PRIMARY KEY(entry_id));";
		String cmd3 = "create table " + name + "_attr (name VARCHAR, type NUMBER, ref_table VARCHAR);";
		
		try {
			stmt = sqlCon.createStatement();
			stmt.execute(cmd);
			stmt = sqlCon.createStatement();
			stmt.execute(cmd2);
			stmt = sqlCon.createStatement();
			stmt.execute(cmd3);
			tables.add(name);
			tables.add(name + "_attr");
			return name;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {rs.close();} catch (Exception e1) {}
		}
		return null;
	}
	
	String ensureType(String type) {
		String tableName = types.get(type);
		if (tableName == null) 
			tableName = newType(type);
		return tableName;
	}	
	
	public Entry createEntry(String type) {
		String tableName = ensureType(type);
		// TODO Auto-generated method stub
		return null;
	}
	public void removeEntry(Entry entry) {
		// TODO Auto-generated method stub
	}

	public Iterator<? extends Entry> getIterator(){
		return null;
	}
	public Entry createOption(String name) {
		// TODO Auto-generated method stub
		return null;
	}
	public void removeOption(Entry option) {
		// TODO Auto-generated method stub
	}
	public Vector<? extends Entry> getOptions(String name){
		return null;
	}
	public Iterator<? extends Entry> getOptionIterator(){
		return null;
	}
	public void removeOption(String name) {
		// TODO Auto-generated method stub
	}
	public Entry getOption(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * This is a database connection therefore there are no unsaved changes
	 * @return false
	 */
	public boolean unsavedChanges() {
		return false;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.mecat.catalog.datamanagement.Catalog#forgetSaveCatalogConnection()
	 */
	public void forgetSaveCatalogConnection() {
		this.connection = null;
	}

    @Override
    public int getVersion() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void setVersion(int i) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setUnchanged() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public Entry getEntry(Identifier identifier) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Entry getOption(Identifier identifier) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Entry getGeneralInformationEntry() {
        // TODO Auto-generated method stub
        return null;
    }

}
