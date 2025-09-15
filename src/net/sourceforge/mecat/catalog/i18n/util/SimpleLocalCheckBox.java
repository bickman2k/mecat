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

import java.util.ResourceBundle;

public class SimpleLocalCheckBox extends LocalCheckBox {
    
    final ResourceBundle res;
    final String key;
    final String toolTipKey;

    
    public SimpleLocalCheckBox(final ResourceBundle res, final String key, boolean selected) {
        this(res, key, null, selected);
    }
    
    public SimpleLocalCheckBox(final ResourceBundle res, final String key) {
        this(res, key, null);
    }
    
    public SimpleLocalCheckBox(final ResourceBundle res, final String key, final String toolTipKey) {
        this(res, key, toolTipKey, false);
    }

    public SimpleLocalCheckBox(final ResourceBundle res, final String key, final String toolTipKey, boolean selected) {
        this.res = res;
        this.key = key;
        this.toolTipKey = toolTipKey;
        setSelected(selected);
        update();
    }

    @Override
    protected String getLocalText() {
        if (res == null)
            return "";
        if (key == null)
            return "";
        return res.getString(key);
    }

    @Override
    protected String getToolTip() {
        if (res == null)
            return null;
        if (toolTipKey == null)
            return null;
        return res.getString(toolTipKey);
    }
}
