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
 * Created on Jan 25, 2006
 * @author Stephan Richard Palm
 * 
 */
package net.sourceforge.mecat.catalog.medium.features.validators;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.regex.Pattern;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.sourceforge.mecat.catalog.datamanagement.Entry;
import net.sourceforge.mecat.catalog.i18n.util.SimpleLocalCheckBox;
import net.sourceforge.mecat.catalog.option.Options;
import net.sourceforge.mecat.catalog.option.preferences.TotalPreferences;

public class TextFeatureValidator extends JPanel implements FeatureValidator {

    final JCheckBox regExp = new SimpleLocalCheckBox(Options.getI18N(TextFeatureValidator.class), "Regular expression", true);
    final JCheckBox caseSensitive = new SimpleLocalCheckBox(Options.getI18N(TextFeatureValidator.class), "Case sensitve", false);
    
    final static String UNICODE_CASE = "(?u)";
    final JTextField textField = new JTextField();
    
    public TextFeatureValidator() {
        regExp.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent arg0) {
                if (regExp.isSelected()) {
                    textField.setText(getRegularExpressionFor(textField.getText(), caseSensitive.isSelected()));
                    caseSensitive.setEnabled(false);
                } else {
                    String simple = getSimpleString(textField.getText());
                    if (simple != null) {
                        caseSensitive.setSelected(!textField.getText().startsWith(UNICODE_CASE));
                        textField.setText(simple);
                    }
                    caseSensitive.setEnabled(true);
                }
            }});
        
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.weighty = 1;
        c.gridwidth = GridBagConstraints.REMAINDER;
        add(regExp, c);
        add(caseSensitive, c);
        c.weightx = 1;
        c.gridheight = GridBagConstraints.REMAINDER;
        add(textField, c);
    }
    
    static String unquote(String str) {
        StringBuffer ret = new StringBuffer();
        
        int start = str.indexOf("\\Q") + 2;
        int end = str.indexOf("\\E", start);
        
        // Quoted strings must allways start with quote-start
        if (start != 2)
            return null;
        
        // Quotation has to end eventually
        if (end == -1)
            return null;

        while (true) {
            ret.append(str.substring(start, end));
            
            if (end == str.length() - 2)
                return ret.toString();

            start = end + 2;
            while (str.substring(start).startsWith("\\\\E")) {
                ret.append("\\E");
                start += 3;
            }
            
            // Another quotation should be here
            if (!str.substring(start).startsWith("\\Q"))
                return null;
            start += 2;

            end = str.indexOf("\\E", start);
            // Quotation has to end eventually
            if (end == -1)
                return null;
        }
    }
    
    public JPanel getPanel() {
        return this;
    }

    public void setValidation(final String val, final TotalPreferences totalPreferences) {
        if (val == null) {
            regExp.setSelected(false);
            textField.setText("");
            return;
        }
        String unquoted = getSimpleString(val);
        if (unquoted != null) {
            regExp.setSelected(false);
            textField.setText(unquoted);
            caseSensitive.setSelected(!val.startsWith(UNICODE_CASE));
            caseSensitive.setEnabled(true);
        } else {
            regExp.setSelected(true);
            textField.setText(val);
            caseSensitive.setEnabled(false);
        }
    }

    static String getSimpleString(String regExp) {
        if (regExp.startsWith(UNICODE_CASE))
            return getSimpleString(regExp.substring(4));
        if (!regExp.startsWith(".*"))
            return null;
        if (!regExp.endsWith(".*"))
            return null;
        return unquote(regExp.substring(2, regExp.length() - 2));
    }
    
    public static String getRegularExpressionFor(String str, boolean caseSensitive) {
        return ((caseSensitive)?"":UNICODE_CASE) + ".*" + Pattern.quote(str) + ".*";
    }

    public String getValidation() {
        if (regExp.isSelected())
            return textField.getText();
        else
            return getRegularExpressionFor(textField.getText(), caseSensitive.isSelected());
    }

    public boolean loadFromEntry(Entry entry) {
        return true;
    }

    public void saveToEntry(Entry entry) {
    }
    
}