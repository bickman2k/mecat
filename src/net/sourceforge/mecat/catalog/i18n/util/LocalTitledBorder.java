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
 * Created on Sep 20, 2006
 * @author Stephan Richard Palm
 * 
 */
package net.sourceforge.mecat.catalog.i18n.util;

import javax.swing.border.TitledBorder;

import net.sourceforge.mecat.catalog.option.LocalListener;
import net.sourceforge.mecat.catalog.option.LocalListenerEvent;
import net.sourceforge.mecat.catalog.option.Options;
import net.sourceforge.mecat.catalog.option.WeakLocalListener;

public abstract class LocalTitledBorder extends TitledBorder implements LocalListener {

/*    public LocalTitledBorder(Border border, String title, int titleJustification, int titlePosition, Font titleFont, Color titleColor) {
        super(border, title, titleJustification, titlePosition, titleFont, titleColor);
        Options.addLocalListener(new WeakLocalListener(this));
    }

    public LocalTitledBorder(Border border, String title, int titleJustification, int titlePosition, Font titleFont) {
        super(border, title, titleJustification, titlePosition, titleFont);
        Options.addLocalListener(new WeakLocalListener(this));
    }

    public LocalTitledBorder(Border border, String title, int titleJustification, int titlePosition) {
        super(border, title, titleJustification, titlePosition);
        Options.addLocalListener(new WeakLocalListener(this));
    }

    public LocalTitledBorder(Border border, String title) {
        super(border, title);
        Options.addLocalListener(new WeakLocalListener(this));
    }

    public LocalTitledBorder(Border border) {
        super(border);
        Options.addLocalListener(new WeakLocalListener(this));
    }

    public LocalTitledBorder(String title) {
        super(title);
        Options.addLocalListener(new WeakLocalListener(this));
    }*/

    public LocalTitledBorder() {
        super("");
        setTitle(getLocalTitle());
        Options.addLocalListener(new WeakLocalListener(this));
    }

    public void stateChanged(LocalListenerEvent event) {
        setTitle(getLocalTitle());
    }

    protected abstract String getLocalTitle();
}
