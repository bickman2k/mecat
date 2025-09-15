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
 * Created on Sep 15, 2006
 * @author Stephan Richard Palm
 * 
 */
package net.sourceforge.mecat.catalog.medium.features.option.merger;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

import net.sourceforge.mecat.catalog.i18n.util.SimpleLocalButton;
import net.sourceforge.mecat.catalog.medium.features.Feature;
import net.sourceforge.mecat.catalog.option.LocalListener;
import net.sourceforge.mecat.catalog.option.LocalListenerEvent;
import net.sourceforge.mecat.catalog.option.Options;
import net.sourceforge.mecat.catalog.option.WeakLocalListener;

public class MergeProcessGUI extends JDialog implements LocalListener{

    final JPanel mainPanel = new JPanel();
    final JPanel buttonPanel = new JPanel();
    final MergeProcess process;

    Class<? extends Feature> currentFeature = null;
    MergeUserInterface currentMUI = null;
    
    ResourceBundle res = Options.getI18N(MergeProcessGUI.class);
    int position = 0;
    
    
    final JButton prev = new SimpleLocalButton(res, "Prev");
    final JButton next = new SimpleLocalButton(res, "Next");
    final JButton reset = new SimpleLocalButton(res, "Reset");
    final JButton finish = new SimpleLocalButton(res, "Import");
    final JButton cancel = new SimpleLocalButton(res, "Cancel");

    public boolean finishedCorrectly = false;
    
    
    public MergeProcessGUI(MergeProcess process) throws HeadlessException {
        this.process = process;
        init();
    }

    public MergeProcessGUI(Dialog dialog, MergeProcess process) throws HeadlessException {
        super(dialog);
        this.process = process;
        init();
    }

    public MergeProcessGUI(Frame frame, MergeProcess process) throws HeadlessException {
        super(frame);
        this.process = process;
        init();
    }

    public void init() {
        buttonPanel.add(prev);
        buttonPanel.add(next);
        buttonPanel.add(reset);
        buttonPanel.add(finish);
        buttonPanel.add(cancel);
        
        prev.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent arg0) {
                setPosition(getPosition() - 1);
            }
        });
        next.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent arg0) {
                setPosition(getPosition() + 1);
            }
        });
        reset.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent arg0) {
                MergeProcessGUI.this.currentMUI.restoreDefault();
                setPosition(getPosition());
            }
        });
        finish.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent arg0) {
                finishedCorrectly = true;
                MergeProcessGUI.this.setVisible(false);
            }
        });
        cancel.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent arg0) {
                MergeProcessGUI.this.setVisible(false);
            }
        });
        

        setLayout(new BorderLayout());
        mainPanel.setLayout(new BorderLayout());
        
        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
        setPosition(0);
        
        setSize(new Dimension(800, 600));

        Options.addLocalListener(new WeakLocalListener(this));
        // Set labels to the current locale
        setLabels();
    }

    public void stateChanged(LocalListenerEvent event) {
        setLabels();
    }
    
    public void setLabels() {
        setTitle(res.getString("Step [POSITION] of [SIZE]").replaceAll("\\[POSITION\\]", "" + (getPosition() + 1)).replaceAll("\\[SIZE\\]", "" + process.showInterface.size()));
    }

    public int getPosition() {
        return position;
    }
    
    public void setPosition(int pos) {
        setTitle(res.getString("Step [POSITION] of [SIZE]").replaceAll("\\[POSITION\\]", "" + (pos + 1)).replaceAll("\\[SIZE\\]", "" + process.showInterface.size()));
        
        currentFeature = process.showInterface.get(pos);
        currentMUI = process.interfaces.get(currentFeature);
        
        mainPanel.removeAll();
        mainPanel.add(currentMUI.getGUI());
        
        prev.setEnabled(position > 0);
        next.setEnabled(position < process.showInterface.size() - 1 && currentMUI.getStatus() == MergeUserInterfaceStatus.Finished);

        finish.setEnabled(true);
        for (Class<? extends Feature> feature : process.showInterface) {
            if (process.interfaces.get(feature).getStatus() == MergeUserInterfaceStatus.Open) {
                finish.setEnabled(false);
                break;
            }
        }
        
        position = pos;
        
        mainPanel.updateUI();
    }
    
    
}
