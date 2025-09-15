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
 * Created on Jun 30, 2006
 * @author Stephan Richard Palm
 * 
 */
package net.sourceforge.mecat.catalog.gui.features.desktop.hardcoded;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

import net.sourceforge.mecat.catalog.export.ExportChoice;
import net.sourceforge.mecat.catalog.i18n.util.SimpleLocalButton;
import net.sourceforge.mecat.catalog.option.Options;

public class ShowHardCodedDesktop extends JDialog {

    final JButton accept = new SimpleLocalButton(Options.getI18N(ExportChoice.class), "OK");
    final JButton cancel = new SimpleLocalButton(Options.getI18N(ExportChoice.class), "Cancel");

    final HardCodedDesktop desktop;
    boolean accepted = false;
    
    public static boolean showHardCodedDesktop(Component component, HardCodedDesktop desktop, String title) {
        return showHardCodedDesktop(component, desktop, title, true);
    }

    public static boolean showHardCodedDesktop(Component component, HardCodedDesktop desktop, String title, boolean showCancel) {
        ShowHardCodedDesktop selector;

        while (component != null && !(component instanceof Frame) && !(component instanceof Dialog))
            component = component.getParent();

        if (component == null)
            selector = new ShowHardCodedDesktop(desktop, title, showCancel);
        else if (component instanceof Dialog)
            selector = new ShowHardCodedDesktop((Dialog)component, desktop, title, showCancel);
        else
            selector = new ShowHardCodedDesktop((Frame)component, desktop, title, showCancel);
        
        selector.setVisible(true);

        return selector.accepted;
    }

    public ShowHardCodedDesktop(HardCodedDesktop desktop, String title, boolean showCancel) throws HeadlessException {
        super();
        this.desktop = desktop;
        init(title, showCancel);
    }

    public ShowHardCodedDesktop(Dialog arg0, HardCodedDesktop desktop, String title, boolean showCancel) throws HeadlessException {
        super(arg0);
        this.desktop = desktop;
        init(title, showCancel);
    }

    public ShowHardCodedDesktop(Frame arg0, HardCodedDesktop desktop, String title, boolean showCancel) throws HeadlessException {
        super(arg0);
        this.desktop = desktop;
        init(title, showCancel);
    }
    
    private void init(String title, boolean showCancel) {
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(accept);
        if (showCancel)
            buttonPanel.add(cancel);

        accept.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                accepted = true;
                setVisible(false);
            }
        });
        cancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                setVisible(false);
            }
        });
        
        setLayout(new BorderLayout());
        add(desktop.getDesktop());
        add(buttonPanel, BorderLayout.SOUTH);

        setTitle(title);
        setSize(new Dimension(300,400));
        setModal(true);

    }
    
    
}
