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
 * Created on Jul 31, 2006
 * @author Stephan Richard Palm
 * 
 */
package net.sourceforge.mecat.catalog.filesystem;

import java.util.Collection;
import java.util.Vector;


public class DetailList extends Vector<Detail> implements Detail {

    
    
    public DetailList() {
        super();
    }

    public DetailList(Collection< ? extends Detail> arg0) {
        super(arg0);
    }

    public DetailList(int arg0, int arg1) {
        super(arg0, arg1);
    }

    public DetailList(int arg0) {
        super(arg0);
    }

    public String getHTMLInfo() {
        StringBuffer buf = new StringBuffer();
        
        for (Detail detail : this) 
            buf.append(detail.getHTMLInfo());
        
        return buf.toString();
    }

    public DetailType getType() {
        return DetailType.ListOfDetails;
    }

}
