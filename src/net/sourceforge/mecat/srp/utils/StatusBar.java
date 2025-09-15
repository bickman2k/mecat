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
 * Created on Sep 27, 2004
 * @author Stephan Richard Palm
 */
package net.sourceforge.mecat.srp.utils;

import java.awt.BorderLayout;

import javax.swing.JSplitPane;
import javax.swing.border.BevelBorder;
import javax.swing.JPanel;

import net.sourceforge.mecat.catalog.i18n.util.SimpleLocalLabel;
import net.sourceforge.mecat.catalog.option.Options;

public class StatusBar extends JPanel {

	/**
	 * Number of Splits in Statusbar
	 */
	int Num_Status_Panels = 4;
	/**
	 * Acces to the Statuspanels
	 */
	JPanel statusPanels[] = new JPanel[Num_Status_Panels];
	/**
	 * Acces to the Splits in order to save and restore session.
	 */
	JSplitPane statusSplits[] = new JSplitPane[Num_Status_Panels - 1];
	
	public StatusBar(int num_panels){
		this.Num_Status_Panels = num_panels;
		statusPanels = new JPanel[Num_Status_Panels];
		statusSplits = new JSplitPane[Num_Status_Panels - 1];
		
//		JPanel jp_status = new JPanel();
		setLayout(new BorderLayout());
		
		for (int i = 0; i < statusPanels.length; i++) {
			statusPanels[i] = new JPanel();
			statusPanels[i].setLayout(new BorderLayout());
			statusPanels[i].add(new SimpleLocalLabel(Options.getI18N(StatusBar.class), "Empty"));
		}
		
		for (int i = 0; i < statusSplits.length; i++) {
			statusSplits[i] = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
			statusSplits[i].add(statusPanels[i], JSplitPane.LEFT);
			if (i > 0)
				statusSplits[i-1].add(statusSplits[i], JSplitPane.RIGHT);
		}
		statusSplits[statusSplits.length-1].add(statusPanels[statusSplits.length], JSplitPane.RIGHT);

		add(statusSplits[0], BorderLayout.CENTER);

		setBorder(new BevelBorder(BevelBorder.LOWERED));
	}
	
	public int getDividerLocation(int pos) {
		if (pos < 0)
			return -1;
		if (pos >= statusSplits.length)
			return -1;
		return statusSplits[pos].getDividerLocation();
	}

	public void setDividerLocation(int pos, int dividerLocation) {
		if (pos < 0)
			return;
		if (pos >= statusSplits.length)
			return;
		
		statusSplits[pos].setDividerLocation(dividerLocation);
	}

	public int getDividerNumber() {
		return statusSplits.length;
	}
	
	public JPanel getStatusPanel(int pos) {
		return statusPanels[pos];
	}

	public int getPanelsNumber() {
		return statusPanels.length;
	}
}
