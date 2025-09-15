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
 * Created on Mar 20, 2006
 * @author Stephan Richard Palm
 * 
 */
package net.sourceforge.mecat.catalog.gui.options.features.IMDB;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.sourceforge.mecat.catalog.export.ExportChoice;
import net.sourceforge.mecat.catalog.i18n.util.SimpleLocalButton;
import net.sourceforge.mecat.catalog.i18n.util.SimpleLocalCheckBox;
import net.sourceforge.mecat.catalog.i18n.util.SimpleLocalLabel;
import net.sourceforge.mecat.catalog.i18n.util.SimpleLocalTitledBorder;
import net.sourceforge.mecat.catalog.option.LocalListener;
import net.sourceforge.mecat.catalog.option.LocalListenerEvent;
import net.sourceforge.mecat.catalog.option.Options;
import net.sourceforge.mecat.catalog.option.WeakLocalListener;

public class CheckOptionDialog extends JDialog implements LocalListener {

    public static enum FullEngagement {
        Ask, Auto, Ignore, Fail
    }
    
    public static enum AutoOrAsk {
        Ask, Auto
    }
    
    public static enum AlwaysNeverAsk {
        Always, Never, Ask
    }
    
    static class EngagementRule<T extends Enum<T>> extends JButton {

        final Class<? extends Enum> engagementClass;
        T state;
        
        public EngagementRule(T init) {
            super(init.toString());
            engagementClass = init.getClass();
            state = init;
            
            addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent arg0) {
                    T[] constants = (T[]) engagementClass.getEnumConstants();
                    for (int i = 0; i < constants.length; i++) {
                        if (state.equals(constants[i])) {
                            setState(constants[(i + 1) % constants.length]);
                            break;
                        }
                    }
                }
            });
        }
        
        public T getState() {
            return state;
        }

        public void setState(T state) {
            this.state = state;
            setText(state.toString());
        }
        
        
    }
    
    
    final ResourceBundle res = Options.getI18N(CheckOptionDialog.class);
    
    /*
     * The following swing components are a list of settings
     */
    final JCheckBox checkTitle = new SimpleLocalCheckBox(res, "Check title with title from IMDB", true);
    final JLabel correctCapitalisation = new SimpleLocalLabel(res, "Trivial differences");
    final EngagementRule<FullEngagement> engageCapitalisation = new EngagementRule<FullEngagement>(FullEngagement.Auto);
    final JCheckBox correctLocale = new SimpleLocalCheckBox(res, "Find IMDB title with wrong Language", true);
    final EngagementRule<AutoOrAsk> engageLocale = new EngagementRule<AutoOrAsk>(AutoOrAsk.Auto);
    final JLabel correctReplaceTitle = new SimpleLocalLabel(res, "Replace title that are diffent in the IMDB");
    final EngagementRule<AlwaysNeverAsk> engageReplaceTitle = new EngagementRule<AlwaysNeverAsk>(AlwaysNeverAsk.Ask);
    final JPanel titlePanel = new JPanel();

    final JCheckBox checkAkas = new SimpleLocalCheckBox(res, "Check akas with akas from IMDB", true);
    final JLabel correctAkasCapitalisation = new SimpleLocalLabel(res, "Trivial differences");
    final EngagementRule<FullEngagement> engageAkasCapitalisation = new EngagementRule<FullEngagement>(FullEngagement.Auto);
    final JLabel correctAddAkas = new SimpleLocalLabel(res, "Add missing akas");
    final EngagementRule<AlwaysNeverAsk> engageAddAkas = new EngagementRule<AlwaysNeverAsk>(AlwaysNeverAsk.Always);
    final JLabel correctRemoveAkas = new SimpleLocalLabel(res, "Remove akas that are not in the IMDB");
    final EngagementRule<AlwaysNeverAsk> engageRemoveAkas = new EngagementRule<AlwaysNeverAsk>(AlwaysNeverAsk.Ask);
    final JLabel correctReplaceAkas = new SimpleLocalLabel(res, "Replace akas that are diffent in the IMDB");
    final EngagementRule<AlwaysNeverAsk> engageReplaceAkas = new EngagementRule<AlwaysNeverAsk>(AlwaysNeverAsk.Ask);
    final JPanel akasPanel = new JPanel();

    final JCheckBox checkYear = new SimpleLocalCheckBox(res, "Check year with year from IMDB", true);
    final JLabel correctYear = new SimpleLocalLabel(res, "Replace year with the year from IMDB");
    final EngagementRule<AlwaysNeverAsk> engageYear = new EngagementRule<AlwaysNeverAsk>(AlwaysNeverAsk.Ask);
    final JPanel yearPanel = new JPanel();

    final JCheckBox checkGenres = new SimpleLocalCheckBox(res, "Check genres with genres from IMDB", true);
    final JLabel correctGenres = new SimpleLocalLabel(res, "Replace genres with the genres from IMDB");
    final EngagementRule<AlwaysNeverAsk> engageGenres = new EngagementRule<AlwaysNeverAsk>(AlwaysNeverAsk.Ask);
    final JPanel genresPanel = new JPanel();

    final JPanel buttonPanel = new JPanel();
    final JButton okButton = new SimpleLocalButton(Options.getI18N(ExportChoice.class), "OK");
    final JButton cancelButton = new SimpleLocalButton(Options.getI18N(ExportChoice.class), "Cancel");

    boolean proceed = false;
    
    public boolean proceed() {
        return proceed;
    }

    public boolean checkTitle() {                           return checkTitle.isSelected();                     }
    public FullEngagement engageTitleCapitalisation() {     return engageCapitalisation.getState();             }
    public boolean correctTitleLocale() {                   return correctLocale.isSelected();                  }
    public AutoOrAsk engageTitleLocale() {                  return engageLocale.getState();                     }
    public AlwaysNeverAsk engageReplaceTitle() {            return engageReplaceTitle.getState();               }

    public boolean checkAkas() {                            return checkAkas.isSelected();                      }
    public FullEngagement engageAkasCapitalisation() {      return engageAkasCapitalisation.getState();         }
    public AlwaysNeverAsk engageAddAkas() {                 return engageAddAkas.getState();                    }
    public AlwaysNeverAsk engageRemoveAkas() {              return engageRemoveAkas.getState();                 }
    public AlwaysNeverAsk engageReplaceAkas() {             return engageReplaceAkas.getState();                }
    
    public boolean checkYear() {                            return checkYear.isSelected();                      }
    public AlwaysNeverAsk engageYear() {                    return engageYear.getState();                       }
    
    public boolean checkGenres() {                          return checkGenres.isSelected();                    }
    public AlwaysNeverAsk engageGenres() {                  return engageGenres.getState();                     }

    
    protected GridBagConstraints ONE() {
        GridBagConstraints ONE = new GridBagConstraints();
        ONE.fill = GridBagConstraints.BOTH;
        ONE.weightx = 1; ONE.weighty = 1;
        ONE.gridwidth = GridBagConstraints.REMAINDER;

        return ONE;
    }

    public GridBagConstraints PRE_ASK() {
        GridBagConstraints PRE_ASK = ONE();
        PRE_ASK.gridwidth = GridBagConstraints.RELATIVE;
        
        return PRE_ASK;
    }

    public GridBagConstraints ASK() {
        GridBagConstraints ASK = ONE();
        ASK.weighty = 0.3;
        
        return ASK;
    }
    
    public GridBagConstraints LAST(GridBagConstraints c) {
        c.gridheight = GridBagConstraints.REMAINDER;
        return c;
    }
    
    public CheckOptionDialog() {

        titlePanel.setBorder(new SimpleLocalTitledBorder(res, "Title"));
        titlePanel.setLayout(new GridBagLayout());
        titlePanel.add(checkTitle, ONE());
        titlePanel.add(correctCapitalisation, PRE_ASK());
        titlePanel.add(engageCapitalisation, ASK());
        titlePanel.add(correctLocale, PRE_ASK());
        titlePanel.add(engageLocale, ASK());
        titlePanel.add(correctReplaceTitle, LAST(PRE_ASK()));
        titlePanel.add(engageReplaceTitle, LAST(ASK()));

        akasPanel.setBorder(new SimpleLocalTitledBorder(res, "Akas"));
        akasPanel.setLayout(new GridBagLayout());
        akasPanel.add(checkAkas, ONE());
        akasPanel.add(correctAkasCapitalisation, PRE_ASK());
        akasPanel.add(engageAkasCapitalisation, ASK());
        akasPanel.add(correctAddAkas, PRE_ASK());
        akasPanel.add(engageAddAkas, ASK());
        akasPanel.add(correctRemoveAkas, PRE_ASK());
        akasPanel.add(engageRemoveAkas, ASK());
        akasPanel.add(correctReplaceAkas, LAST(PRE_ASK()));
        akasPanel.add(engageReplaceAkas, LAST(ASK()));
        
        yearPanel.setBorder(new SimpleLocalTitledBorder(res, "Year"));
        yearPanel.setLayout(new GridBagLayout());
        yearPanel.add(checkYear, ONE());
        yearPanel.add(correctYear, LAST(PRE_ASK()));
        yearPanel.add(engageYear, LAST(ASK()));

        genresPanel.setBorder(new SimpleLocalTitledBorder(res, "Genres"));
        genresPanel.setLayout(new GridBagLayout());
        genresPanel.add(checkGenres, ONE());
        genresPanel.add(correctGenres, LAST(PRE_ASK()));
        genresPanel.add(engageGenres, LAST(ASK()));
        
        ActionListener consistentcy = new ActionListener(){
            public void actionPerformed(ActionEvent arg0) {
                consistentcy();
            }
        };
        
        checkTitle.addActionListener(consistentcy);
        correctLocale.addActionListener(consistentcy);
        checkAkas.addActionListener(consistentcy);
        checkYear.addActionListener(consistentcy);
        checkGenres.addActionListener(consistentcy);

        okButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent arg0) {
                setVisible(false);
                proceed = true;
            }});
        cancelButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent arg0) {
                setVisible(false);
            }});
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        
        JPanel optionsPanel = new JPanel();
        optionsPanel.setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1; c.weighty = 1;
        c.gridwidth = GridBagConstraints.REMAINDER;

        optionsPanel.add(titlePanel, c);
        optionsPanel.add(akasPanel, c);
        optionsPanel.add(yearPanel, c);
        optionsPanel.add(genresPanel, c);
        c.gridheight = GridBagConstraints.REMAINDER;
        optionsPanel.add(buttonPanel, c);
        
        setLayout(new BorderLayout());
        setContentPane(optionsPanel);
        
        pack();
        setModal(true);

        Options.addLocalListener(new WeakLocalListener(this));
        // Set labels to the current locale
        setLabels();
    }

    public void stateChanged(LocalListenerEvent event) {
        setLabels();
    }
    
    public void setLabels() {
         setTitle(res.getString("Options used for check"));
    }

    protected void consistentcy() {
        correctCapitalisation.setEnabled(checkTitle.isSelected());
        correctLocale.setEnabled(checkTitle.isSelected());
        engageCapitalisation.setEnabled(checkTitle.isSelected());
        engageLocale.setEnabled(checkTitle.isSelected() && correctLocale.isSelected());
        
        correctAkasCapitalisation.setEnabled(checkAkas.isSelected());
        correctAddAkas.setEnabled(checkAkas.isSelected());
        correctRemoveAkas.setEnabled(checkAkas.isSelected());
        correctReplaceAkas.setEnabled(checkAkas.isSelected());
        engageAkasCapitalisation.setEnabled(checkAkas.isSelected());
        engageAddAkas.setEnabled(checkAkas.isSelected());
        engageRemoveAkas.setEnabled(checkAkas.isSelected());
        engageReplaceAkas.setEnabled(checkAkas.isSelected());
        
        correctYear.setEnabled(checkYear.isSelected());
        engageYear.setEnabled(checkYear.isSelected());

        correctGenres.setEnabled(checkGenres.isSelected());
        engageGenres.setEnabled(checkGenres.isSelected());
        
        // Only allow to proceed if at least one thing is checked
        okButton.setEnabled(checkTitle.isSelected() || checkAkas.isSelected() || checkYear.isSelected() || checkGenres.isSelected());
    }
    
}
