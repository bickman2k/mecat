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
 * Created on 22.01.2007
 * @author Stephan Richard Palm
 * 
 */
package net.sourceforge.mecat.catalog.filesystem.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.WindowEvent;

import javax.swing.JDialog;

import net.sourceforge.mecat.catalog.filesystem.Result;

public class ExplorerDialog extends JDialog {

    Explorer explorer = new Explorer();
    final Result result;

    static public void showExplorer(final Result result, Component component) {
        
        ExplorerDialog dialog;
        
        while (component != null && !(component instanceof Frame) && !(component instanceof Dialog))
            component = component.getParent();

        if (component == null)
            dialog = new ExplorerDialog(result);
        else if (component instanceof Dialog)
            dialog = new ExplorerDialog((Dialog)component, result);
        else
            dialog = new ExplorerDialog((Frame)component, result);

        dialog.setModal(true);
        dialog.setSize(new Dimension(800,600));
        
        dialog.setVisible(true);
    }
    
    public ExplorerDialog(final Result result) {
        this.result = result;
        init();
    }
    public ExplorerDialog(Dialog dialog, final Result result) {
        super(dialog);
        this.result = result;
        init();
    }
    public ExplorerDialog(Frame frame, final Result result) {
        super(frame);
        this.result = result;
        init();
    }
    
    protected void init(){
        setLayout(new BorderLayout());
        explorer.setResult(result);
        add(explorer.getToolBar(this), BorderLayout.PAGE_START);
        add(explorer);
    }
    
    protected void processWindowEvent(WindowEvent e) {
        if (e.getID() == WindowEvent.WINDOW_OPENED) {
            explorer.split.setDividerLocation(350);
        }
        super.processWindowEvent(e);
    }
}
