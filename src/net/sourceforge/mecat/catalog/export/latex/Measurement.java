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
package net.sourceforge.mecat.catalog.export.latex;

import net.sourceforge.mecat.catalog.datamanagement.Entry;
import net.sourceforge.mecat.catalog.datamanagement.PersistentThroughEntry;
import net.sourceforge.mecat.catalog.datamanagement.Util;
import net.sourceforge.mecat.catalog.option.Options;

public class Measurement implements PersistentThroughEntry {
	/**
	 * NOLINEBREAK does what it says. This column will have
	 * no entrys with a linebreak.
	 * 
	 * FIXEDLEN the columnwidth is exactly the value of 'collen'
	 * 
	 * VARLEN over all columns with VARLEN, the rest of the posible len
	 * will be split. The size of every segment will be relative to 'collen'.
	 */
	public enum MeasurementType{ NOLINEBREAK, FIXEDLEN, VARLEN }
	public MeasurementType type;
	public double collen = 0;
	
	public Measurement(MeasurementType type) {
		this.type = type;
	}
	public Measurement(MeasurementType type, Double collen) {
		this(type, collen.doubleValue());
	}
	public Measurement(MeasurementType type, double collen) {
		this.collen = collen;
		this.type = type;
	}
    
    public String toString() {
        switch (type) {
        case FIXEDLEN:
            return Options.getI18N(Measurement.class).getString("FixLenOf").replaceAll("\\[WIDTH\\]", String.valueOf(collen));
        case NOLINEBREAK : 
            return Options.getI18N(Measurement.class).getString("NoLineBreak");
        case VARLEN : 
            return Options.getI18N(Measurement.class).getString("VariableLength");
        default :
            return Options.getI18N(Measurement.class).getString("Unspecified");
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