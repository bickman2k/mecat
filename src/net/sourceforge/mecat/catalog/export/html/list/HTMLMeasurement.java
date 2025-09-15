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
 * Created on Sep 23, 2004
 * @author Stephan Richard Palm
 */
package net.sourceforge.mecat.catalog.export.html.list;

import java.awt.Component;
import java.util.ResourceBundle;

import javax.swing.JOptionPane;

import net.sourceforge.mecat.catalog.datamanagement.Entry;
import net.sourceforge.mecat.catalog.datamanagement.PersistentThroughEntry;
import net.sourceforge.mecat.catalog.datamanagement.Util;
import net.sourceforge.mecat.catalog.option.Options;

public class HTMLMeasurement implements PersistentThroughEntry {
    
    static protected ResourceBundle res = Options.getI18N(HTMLMeasurement.class);
    
	public HTMLMeasurementType type;
	public double collen = 0;
	
	public HTMLMeasurement(HTMLMeasurementType type) {
		this.type = type;
	}
	public HTMLMeasurement(HTMLMeasurementType type, Double collen) {
		this(type, collen.doubleValue());
	}
	public HTMLMeasurement(HTMLMeasurementType type, double collen) {
		this.collen = collen;
		this.type = type;
	}
    
    public static HTMLMeasurement aquireHTMLMeasurement(Component parentComponent) {
        return aquireHTMLMeasurement(parentComponent, null);
    }
    public static HTMLMeasurement aquireHTMLMeasurement(Component parentComponent, HTMLMeasurement measurement) {
        HTMLMeasurementType t = (HTMLMeasurementType)JOptionPane.showInputDialog(parentComponent, 
                res.getString("Type of the column."),
                res.getString("Choose the type of the column."), 
                JOptionPane.PLAIN_MESSAGE,
                null, 
                HTMLMeasurementType.values(),
                (measurement != null && measurement.type != null)?measurement.type:HTMLMeasurementType.FIXEDLEN);

        if (t == null)
            return null;
        
        double len = 0;

        String question = null;
        String stdVal = null;

        switch (t) {
        case FIXEDLEN:
            question = res.getString("Please input widths.");
            stdVal = "200";
            break;
        case PERCENT : 
            question = res.getString("Please input percentage.");
            stdVal = "50";
            break;
        case PROPORTION : 
            question = res.getString("Please input proportion.");
            stdVal = "1";
            break;
        default :
            return null;
        }
        
        do {
            String inputValue = JOptionPane.showInputDialog(parentComponent, question, 
                    (measurement != null && measurement.type != null)?String.valueOf(measurement.collen):stdVal);
            try {
                len = Double.valueOf(inputValue);
            } catch (NumberFormatException  ex) {
                len = 0;
            }
        } while (len <= 0);
        
        return new HTMLMeasurement(t, len);
    }
    
    public String toString() {
        switch (type) {
        case FIXEDLEN:
            return Options.getI18N(HTMLMeasurementType.class).getString("FixLenOf").replaceAll("\\[WIDTH\\]", String.valueOf(collen));
        case PERCENT : 
            return Options.getI18N(HTMLMeasurementType.class).getString("Percent").replaceAll("\\[WIDTH\\]", String.valueOf(collen));
        case PROPORTION : 
            return Options.getI18N(HTMLMeasurementType.class).getString("Proportion").replaceAll("\\[WIDTH\\]", String.valueOf(collen));
        default :
            return Options.getI18N(HTMLMeasurementType.class).getString("Unspecified");
        }
    }

	public boolean loadFromEntry(Entry entry) {
		return true;
	}

	public void saveToEntry(Entry entry) {
		Util.addArgument(entry, new Util.Argument(0, null, type));
		Util.addArgument(entry, new Util.Argument(1, null, Double.valueOf(collen)));
	}
}