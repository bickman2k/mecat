/*
 *  MeCat - Media Catalog more information on
 *  http://mecat.sourceforge.net/
 *  Copyright (C) 2003, Stephan Richard Palm, All Rights Reserved.
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
 * Created on 08.12.2003
 * @author Stephan Richard Palm
 */
package net.sourceforge.mecat.srp.utils;

import java.awt.Color;
import java.awt.GridLayout;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import net.sourceforge.mecat.catalog.i18n.util.SimpleLocalLabel;
import net.sourceforge.mecat.catalog.i18n.util.SimpleLocalTitledBorder;
import net.sourceforge.mecat.catalog.option.Options;

public class ConnectionPanel extends JPanel {

	private static String ToolTipDriver = "Driver class name, ex. sun.jdbc.odbc.JdbcOdbcDriver";
	private static String ToolTipDSN = "Name of the Data Source to be reached";
	private static String ToolTipUsername = "Username to establish connection";
	private static String ToolTipPassword = "Password for the spezified Username";

    ResourceBundle res = Options.getI18N(ConnectionPanel.class);
    
	private JTextField DriverTxt;
	private JTextField DSNTxt;
	private JTextField UsernameTxt;
	private JTextField PasswordTxt;

	public ConnectionPanel(String Driver, String DSN, String Username, String Password){
		this(Driver, DSN, Username, Password, true);
	}
	public ConnectionPanel(String Driver, String DSN, String Username, String Password, boolean HasBorder){
		this(HasBorder);
		DriverTxt.setText(Driver);
		DSNTxt.setText(DSN);
		UsernameTxt.setText(Username);
		PasswordTxt.setText(Password);
	}

	/*
	 * Takes the Value "Driver", "DSN", "Username" and "Password" out
	 * of in instance of the class Properties.
	 */
	public ConnectionPanel(Properties Props){
		this(Props, true);
	}
	public ConnectionPanel(Properties Props, boolean HasBorder){
		this(HasBorder);
		SetTextFieldsFromProperties(Props);
	}

	public ConnectionPanel(){
		this(true);
	}
	
	public ConnectionPanel(boolean HasBorder){
		this.setLayout(new GridLayout(8,1));

		JLabel DriverLbl = new SimpleLocalLabel(res, "Driver:", ToolTipDriver);
		JLabel DSNLbl = new SimpleLocalLabel(res, "Data Source Name (DSN):", ToolTipDSN);
		JLabel UsernameLbl = new SimpleLocalLabel(res, "Username:", ToolTipUsername);
		JLabel PasswordLbl = new SimpleLocalLabel(res, "Password:", ToolTipPassword);
		DriverTxt = new JTextField();
		DSNTxt = new JTextField();
		UsernameTxt = new JTextField();
		PasswordTxt = new JPasswordField();

		DriverTxt.setToolTipText(res.getString(ToolTipDriver));
		DSNTxt.setToolTipText(res.getString(ToolTipDSN));
		UsernameTxt.setToolTipText(res.getString(ToolTipUsername));
		PasswordTxt.setToolTipText(res.getString(ToolTipPassword));

		add(DriverLbl);
		add(DriverTxt);
		add(DSNLbl);
		add(DSNTxt);
		add(UsernameLbl);
		add(UsernameTxt);
		add(PasswordLbl);
		add(PasswordTxt);
		
		if (HasBorder)
			this.setBorder(new SimpleLocalTitledBorder(res,  "Connection"));
		setToolTipText(res.getString("The four options in this Pane define the connection."));
	}

	public void SetTextFieldsFromProperties(Properties Props){
		DriverTxt.setText(Props.getProperty("Driver"));
		DSNTxt.setText(Props.getProperty("DSN"));
		UsernameTxt.setText(Props.getProperty("Username"));
		PasswordTxt.setText(Props.getProperty("Password"));
	}
	public Properties SetPropertiesFromTextFields(Properties Props){
		Props.put("Driver", DriverTxt.getText());
		Props.put("DSN", DSNTxt.getText());
		Props.put("Username", UsernameTxt.getText());
		Props.put("Password", PasswordTxt.getText());
		
		return Props;
	}
}
