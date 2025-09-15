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
 * Created on Jan 3, 2006
 * @author Stephan Richard Palm
 * 
 */
package net.sourceforge.mecat.catalog.export;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import net.sourceforge.mecat.catalog.option.Options;

public class ExportProgressVisualisation extends JDialog implements ExportProgressListener {

    final JButton button = new JButton();
    final ResourceBundle res = Options.getI18N(ExportProgressVisualisation.class);
    final JPanel progressPanel = new JPanel();
    final Export source;
    boolean running = false;
    
    Vector<JLabel> labels = new Vector<JLabel>();
    Vector<JProgressBar> progressBars = new Vector<JProgressBar>();
    
    public ExportProgressVisualisation(Export export) {
        this.source = export;
        this.source.addExportProgressListener(this);
        progressPanel.setLayout(new GridLayout(0, 1));
        setModal(true);
        
        button.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent arg0) {
                processButton();
            }
        });
        
        setLayout(new BorderLayout());
        add(progressPanel);
        add(button, BorderLayout.SOUTH);
    }

    protected void processButton() {
        if (running) {
            button.setText(res.getString("Stopping..."));
            button.setEnabled(false);
            source.stopExport();
        } else
            setVisible(false);
    }

    public void ExportStarted(Export source, int depth) {
        if (source != this.source)
            return;
        
        labels.clear();
        progressBars.clear();
        progressPanel.removeAll();
        
        for (int i = 0; i < depth; i++) {
            JLabel label = new JLabel();
            JProgressBar pBar = new JProgressBar();
            labels.add(label);
            progressBars.add(pBar);
            progressPanel.add(label);
            progressPanel.add(pBar);
        }
        
        pack();
        setSize(getSize().width * 2, getSize().height * 2);

        button.setText(res.getString("STOP"));
        running = true;
        Thread t = new Thread(){ public void run() {
            setVisible(true);
        }};
        t.start();
    }

    public void ExportFinished(Export source) {
        if (source != this.source)
            return;

        running = false;
        button.setText(res.getString("Close"));
        button.setEnabled(true);
    }

    public void ExportProgessSetTitle(Export source, int depth, String title) {
        if (source != this.source)
            return;

        if (title == null) {
            labels.elementAt(depth).setText("");
            progressBars.elementAt(depth).setString("");
            progressBars.elementAt(depth).setEnabled(false);
        } else {
            labels.elementAt(depth).setText(title);
            progressBars.elementAt(depth).setEnabled(true);
        }
    }

    public void ExportProgessSetMessage(Export source, int depth, String message) {
        if (source != this.source)
            return;
        
        if (message == null) {
            progressBars.elementAt(depth).setString("");
            progressBars.elementAt(depth).setStringPainted(false);
        } else {
            progressBars.elementAt(depth).setString(message);
            progressBars.elementAt(depth).setStringPainted(true);
        } 
    }

    public void ExportUpdate(Export source, int depth, int offset, int size) {
        if (source != this.source)
            return;

        progressBars.elementAt(depth).setValue(offset);
        progressBars.elementAt(depth).setMaximum(size);
    }

    public void ExportStopped(Export source) {
        if (source != this.source)
            return;

        setVisible(false);
    }

    public void ExportShowsPrintOption(final Export source) {
        if (source != this.source)
            return;

        if (Options.verbosity > 2)
            System.out.println(res.getString("[ExpVisu]") + " " + res.getString("Hide for print options."));

        setVisible(false);
    }

    public void ExportStoppedShowingPrintOption(final Export source) {
        if (source != this.source)
            return;
        
        if (Options.verbosity > 2)
            System.out.println(res.getString("[ExpVisu]") + " " + res.getString("Unhide after print options are gone."));

        Thread t = new Thread(){ public void run() {
            setVisible(true);
        }};
        t.start();
    }


}
