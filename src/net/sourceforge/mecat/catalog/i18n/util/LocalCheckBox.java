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
 * Created on Sep 21, 2006
 * @author Stephan Richard Palm
 * 
 */
package net.sourceforge.mecat.catalog.i18n.util;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JCheckBox;

import net.sourceforge.mecat.catalog.option.LocalListener;
import net.sourceforge.mecat.catalog.option.LocalListenerEvent;
import net.sourceforge.mecat.catalog.option.Options;
import net.sourceforge.mecat.catalog.option.WeakLocalListener;

public abstract class LocalCheckBox extends JCheckBox implements LocalListener {

    public LocalCheckBox() {
        super();
        update();
        Options.addLocalListener(new WeakLocalListener(this));
    }

    public LocalCheckBox(Action a) {
        super(a);
        update();
        Options.addLocalListener(new WeakLocalListener(this));
    }

    public LocalCheckBox(Icon icon, boolean selected) {
        super(icon, selected);
        update();
        Options.addLocalListener(new WeakLocalListener(this));
    }

    public LocalCheckBox(Icon icon) {
        super(icon);
        update();
        Options.addLocalListener(new WeakLocalListener(this));
    }

    public LocalCheckBox(boolean selected) {
        super();
        setSelected(selected);
        update();
        Options.addLocalListener(new WeakLocalListener(this));
    }

    public void stateChanged(LocalListenerEvent event) {
        update();
    }
    
    public void update() {
        setLocalText();
        setToolTip();
    }

    public void setLocalText() {
        String text = getLocalText();
        if (text != null)
            setText(text);
    }
    
    public void setToolTip() {
        String toolTip = getToolTip();
        if (toolTip != null)
            this.setToolTipText(toolTip);
    }

    protected abstract String getLocalText();

    
    protected String getToolTip() {
        return null;
    }
}
