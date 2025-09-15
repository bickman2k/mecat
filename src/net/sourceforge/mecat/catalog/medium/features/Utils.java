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
 * Created on Jul 26, 2004
 * @author Stephan Richard Palm
 */
package net.sourceforge.mecat.catalog.medium.features;

import java.util.ResourceBundle;
import java.util.Vector;

import net.sourceforge.mecat.catalog.option.Options;

public class Utils {

    static ResourceBundle res = Options.getI18N(Utils.class);
    
/*    static { res allready uses the actual locale
        Options.addLocalListener(new LocalListener(){
            public void stateChanged(LocalListenerEvent event) {
                res = Options.getI18N(Utils.class);
            }
        });
    }*/
    
    
	public static String natList(Vector<String> list) {
		if (list.size() == 0)
			return "";
		
		StringBuffer ret = new StringBuffer();
		
		for (int i = 0; i < list.size() - 2; i++) {
			ret.append(list.get(i));
			ret.append(", ");
		}
		
		if (list.size() > 1) {
			ret.append(list.get(list.size() - 2));
			ret.append(" " + res.getString("and") + " ");
		}

		ret.append(list.get(list.size() - 1));

		return ret.toString();
	}
}
