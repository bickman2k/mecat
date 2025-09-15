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
 * Created on Jun 12, 2006
 * @author Stephan Richard Palm
 * 
 */
package net.sourceforge.mecat.catalog.gui.log;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import net.sourceforge.mecat.catalog.export.ExportProgressVisualisation;
import net.sourceforge.mecat.catalog.option.LocalListener;
import net.sourceforge.mecat.catalog.option.LocalListenerEvent;
import net.sourceforge.mecat.catalog.option.Options;
import net.sourceforge.mecat.catalog.option.WeakLocalListener;

public class LogDialog extends JDialog implements Log, LocalListener {

    final Logable logable;
    boolean finished = false;

    final JEditorPane log = new JEditorPane();
    final StringBuffer logBuffer = new StringBuffer("<HTML><BODY>");

    JButton close = new JButton();
    
    public static void showLogDialog(Component component, final Logable logable) {
        LogDialog dialog;
        

        while (component != null && !(component instanceof Frame) && !(component instanceof Dialog))
            component = component.getParent();

        if (component instanceof Dialog)
            dialog = new LogDialog((Dialog)component, logable);
        else
            dialog = new LogDialog((Frame)component, logable);

        Thread t = new Thread(logable);
        t.start();
        dialog.setVisible(true);
    }

    protected LogDialog(Dialog dialog, Logable logable) {
        super(dialog);
        this.logable = logable;
        init();
    }

    protected LogDialog(Frame frame, Logable logable) {
        super(frame);
        this.logable = logable;
        init();
    }

    private void init() {
        logable.addLog(this);
        
        setSize(new Dimension(800, 600));
        setResizable(false);
        setModal(true);
        setResizable(false);
        log.setContentType("text/html");
        JPanel buttonPanel = new JPanel();
        
        buttonPanel.add(close);
        close.setEnabled(false);
        
        setLayout(new BorderLayout());
        add(new JScrollPane(log));
        add(buttonPanel, BorderLayout.SOUTH);
        
        close.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent arg0) {
                setVisible(false);
            }
        });

        Options.addLocalListener(new WeakLocalListener(this));
        // Set labels to the current locale
        setLabels();
    }

    public void stateChanged(LocalListenerEvent event) {
        setLabels();
    }
    
    public void setLabels() {
        close.setText(Options.getI18N(ExportProgressVisualisation.class).getString("Close"));
    }

    private void addLogEntry(String str) {
        logBuffer.append(str + "<br>");
        log.setText(logBuffer.toString() + "</BODY></HTML>");
    }

    public void message(String message) {
        addLogEntry(message);
    }

    public void finished() {
        finished = true;
        close.setEnabled(true);
    }
    
    protected void processWindowEvent(WindowEvent e) {
        if (e.getID() == WindowEvent.WINDOW_CLOSING)
            if (!finished)
                return;
        super.processWindowEvent(e);
    }
    
    
}
