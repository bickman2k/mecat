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
 * Created on Nov 2, 2006
 * @author Stephan Richard Palm
 * 
 */
package net.sourceforge.mecat.catalog.datamanagement.changelog;

import java.util.List;
import java.util.Vector;

import net.sourceforge.mecat.catalog.datamanagement.Catalog;
import net.sourceforge.mecat.catalog.option.Options;

public class ChangeTreeEventNode extends AbstractChangeTreeNode {

    final PreservedEvent event;
    final Catalog source;

    public ChangeTreeEventNode(final ChangeTreeTransactionNode parent, final Catalog source, final PreservedEvent event) {
        super(parent);
        this.event = event;
        this.source = source;
    }

    public PreservedEvent getEvent() {
        return event;
    }
    
    public Catalog getSource() {
        return source;
    }
    
    public String toString() {
        return event.toString();
    }

    public void execute(Catalog catalog) {
        if (Options.DEBUG && Options.verbosity > 1)
            System.out.println(Options.getI18N(ChangeTreeEventNode.class).getString("Redo: [STEP]").replaceAll("\\[STEP\\]", "" + this));
        event.execute(catalog);
    }

    public List<Catalog> getCatalogs(int start) {
        List<Catalog> ret = new Vector<Catalog>(1);
        
        if (start > 0)
            return ret;

        ret.add(source);
        return ret;
    }
}
