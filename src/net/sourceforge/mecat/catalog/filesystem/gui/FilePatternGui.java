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
 * Created on Jun 26, 2006
 * @author Stephan Richard Palm
 * 
 */
package net.sourceforge.mecat.catalog.filesystem.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Date;
import java.util.ResourceBundle;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;

import net.sourceforge.mecat.catalog.filesystem.FilePattern;
import net.sourceforge.mecat.catalog.i18n.util.SimpleLocalTitledBorder;
import net.sourceforge.mecat.catalog.medium.features.validators.TextFeatureValidator;
import net.sourceforge.mecat.catalog.option.LocalListener;
import net.sourceforge.mecat.catalog.option.LocalListenerEvent;
import net.sourceforge.mecat.catalog.option.Options;
import net.sourceforge.mecat.catalog.option.WeakLocalListener;

public class FilePatternGui extends JPanel implements LocalListener {

    public static FilePattern getFilePattern(Component component, FilePattern filePattern) {
        FilePatternDialog dialog;
        
        while (component != null && !(component instanceof Frame) && !(component instanceof Dialog))
            component = component.getParent();

        if (component == null)
            dialog = new FilePatternDialog(filePattern);
        else if (component instanceof Dialog)
            dialog = new FilePatternDialog((Dialog)component, filePattern);
        else
            dialog = new FilePatternDialog((Frame)component, filePattern);


        dialog.setVisible(true);

        if (!dialog.accept)
            return null;
        
        return dialog.gui.getFilePattern();
    }
    
/*    
    public static void main(String[] args) {
        JDialog frame = new JDialog();
        FilePatternGui gui = new FilePatternGui(new FilePattern("Hallo Welt"));
        frame.add(gui);
        frame.pack();
        frame.setModal(true);
        frame.setVisible(true);

        System.out.println(gui.getFilePattern());
        
    }*/
    
    ResourceBundle res = Options.getI18N(FilePatternGui.class);
    
    TextFeatureValidator name = new TextFeatureValidator();
//    JTextField name = new JTextField("");
    
    JSpinner older = new JSpinner(new SpinnerDateModel());
    JSpinner.DateEditor olderDateEditor = new JSpinner.DateEditor(older);
    JSpinner younger = new JSpinner(new SpinnerDateModel());
    
    JSpinner bigger = new FileSizeSpinner(false);
    JSpinner smaller = new FileSizeSpinner(true);
    
    JLabel olderThanLabel = new JLabel();
    JLabel youngerThanLabel = new JLabel();

    JPanel olderYoungerPanel = new JPanel();
    JPanel biggerSmallerPanel  = new JPanel();
    
    public FilePatternGui(FilePattern pattern) {
        if (pattern.getName() != null)
            name.setValidation(pattern.getName(), null);

        older.setEditor(olderDateEditor);
        younger.setEditor(new JSpinner.DateEditor(younger));
        
        older.setValue(new java.util.Date(pattern.getOlderThan()));
        younger.setValue(new java.util.Date(pattern.getYoungerThan()));

        bigger.setValue(pattern.getBiggerThan());
        smaller.setValue(pattern.getSmallerThan());
        

        GridBagConstraints c0 = new GridBagConstraints();
        c0.fill = GridBagConstraints.BOTH;
        GridBagConstraints c1 = new GridBagConstraints();
        c1.fill = GridBagConstraints.BOTH;
        c1.gridwidth = GridBagConstraints.REMAINDER;
        GridBagConstraints c2 = new GridBagConstraints();
        c2.fill = GridBagConstraints.BOTH;
        c2.gridheight = GridBagConstraints.REMAINDER;
        GridBagConstraints c3 = new GridBagConstraints();
        c3.fill = GridBagConstraints.BOTH;
        c3.gridwidth = GridBagConstraints.REMAINDER;
        c3.gridheight = GridBagConstraints.REMAINDER;

        olderYoungerPanel.setLayout(new GridBagLayout());
        olderYoungerPanel.add(olderThanLabel, c0);
        olderYoungerPanel.add(older, c1);
        olderYoungerPanel.add(youngerThanLabel, c2);
        olderYoungerPanel.add(younger, c3);
        
        setLayout(new GridBagLayout());
        add(name, c1);
        add(olderYoungerPanel, c1);
        add(biggerSmallerPanel, c3);

        biggerSmallerPanel.setLayout(new BorderLayout());
        JPanel biggerPanel = new JPanel();
        biggerPanel.setLayout(new BorderLayout());
        biggerPanel.add(new JLabel(">="), BorderLayout.WEST);
        biggerPanel.add(bigger);
        JPanel smallerPanel = new JPanel();
        smallerPanel.setLayout(new BorderLayout());
        smallerPanel.add(new JLabel("<="), BorderLayout.WEST);
        smallerPanel.add(smaller);
        biggerSmallerPanel.add(biggerPanel, BorderLayout.NORTH);
        biggerSmallerPanel.add(smallerPanel);
        
        name.setBorder(new SimpleLocalTitledBorder(Options.getI18N(ResultFileModel.class), "Name"));
        olderYoungerPanel.setBorder(new SimpleLocalTitledBorder(Options.getI18N(ResultFileModel.class), "Date"));
        biggerSmallerPanel.setBorder(new SimpleLocalTitledBorder(Options.getI18N(ResultFileModel.class), "Size"));
        
        Options.addLocalListener(new WeakLocalListener(this));
        // Set labels to the current locale
        setLabels();
    }

    public void stateChanged(LocalListenerEvent event) {
        setLabels();
    }
    
    public void setLabels() {
        olderThanLabel.setText(res.getString("older than"));
        youngerThanLabel.setText(res.getString("younger than"));
    }
    
    public FilePattern getFilePattern() {
        return new FilePattern(name.getValidation(), ((Date)older.getValue()).getTime(), ((Date)younger.getValue()).getTime(), (Long) bigger.getValue(), (Long) smaller.getValue());
    }
    
}
