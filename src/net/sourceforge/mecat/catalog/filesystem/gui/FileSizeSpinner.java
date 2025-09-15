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
 * Created on Jun 27, 2006
 * @author Stephan Richard Palm
 * 
 */
package net.sourceforge.mecat.catalog.filesystem.gui;

import java.util.Vector;

import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

import net.sourceforge.mecat.catalog.i18n.fromCatalog.LayeredResourceBundle;
import net.sourceforge.mecat.catalog.option.LocalListener;
import net.sourceforge.mecat.catalog.option.LocalListenerEvent;
import net.sourceforge.mecat.catalog.option.Options;
import net.sourceforge.mecat.catalog.option.WeakLocalListener;

public class FileSizeSpinner extends JSpinner implements LocalListener {

    final boolean includeInfinite;
    final NumberField numberField;
    
    Object feedBack = new Object();
    boolean noFeedback = false;
    
    public static final boolean isNumber(final String s) {
        for (char c : s.toCharArray()) 
            if ((c < '0') || (c > '9')) 
                return false; 
        
        return true;
      }

    public /*static */class NumberField extends JTextField {
    
        protected Document createDefaultModel() {
            return new PlainDocument() {
                
                
                @Override
                public void replace(int arg0, int arg1, String arg2, AttributeSet arg3) throws BadLocationException {
                    // Don't use the overriden remove while replacing a whole string
                    super.remove(arg0, arg1);
                    insertString(arg0, arg2, arg3);
                }

                @Override
                public synchronized void remove(int off, int len) throws BadLocationException {
                    // If it is no number removing of one character will 
                    // remove the whole
                    // If there is an empty string this means the same as 0
                    if (getLength() == len || !isNumber(getText(off, len))) {
                        super.remove(0, getLength());
                        super.insertString(0, "0", null);
                        return;
                    }
                    super.remove(off, len);
                }

                public synchronized void insertString(int offs, String str, AttributeSet a) 
                throws BadLocationException {
                    /*if (includeInfinite && str.equals("-1")) {
                        super.remove(0, getLength());
                        super.insertString(offs, Options.getI18N(FilePatternGui.class).getString("infinite"), a);
                        return;
                    }*/
                    if (isNumber(str)) {
                        if (!isNumber(getText(0, getLength()))) {
                            super.remove(0, getLength());
                            super.insertString(0, str, a);
                        } else
                            super.insertString(offs, str, a);
                        return;
                    } 
                    if (includeInfinite) {
                        super.remove(0, getLength());
                        super.insertString(0, Options.getI18N(FilePatternGui.class).getString("infinite"), a);
                    }
                }
            };
        }
    }

    public long getLongFromString(String str) {
        if (str.equals(Options.getI18N(FilePatternGui.class).getString("infinite")))
            return -1;
        else
            try {
                return Long.parseLong(str);
            } catch (Exception e) { return 0; }
    }
    
    public FileSizeSpinner(final boolean includeInfinite) {
        this.includeInfinite = includeInfinite;
        numberField = new NumberField();
        setModel(new SpinnerModel(){

            long value = (includeInfinite)?-1:0;
            
            public Object getValue() {
                return value;
            }

            public void setValue(Object o) {
                if (!(o instanceof Long))
                    return;

                // will the value change
                boolean change = value != (Long) o;
                value = (Long) o;
                // if the value has changed fire a change
                if (change)
                    fireChange();
            }

            public Object getNextValue() {
                return value + 1;
            }

            public Object getPreviousValue() {
                if (includeInfinite) {
                    if (value < 0)
                        return -1;
                } else {
                    if (value <= 0)
                        return 0;
                }
                return value - 1;
            }

            Vector<ChangeListener> changeListeners = new Vector<ChangeListener>();
            
            public void addChangeListener(ChangeListener changeListener) {
                changeListeners.add(changeListener);
            }

            public void removeChangeListener(ChangeListener changeListener) {
                changeListeners.remove(changeListener);
            }
            
            public void fireChange() {
                for (ChangeListener changeListener : changeListeners)
                    changeListener.stateChanged(new ChangeEvent(this));
            }
            
        });
        getModel().addChangeListener(new ChangeListener(){
            public void stateChanged(ChangeEvent arg0) {
                if (noFeedback) 
                    return;

                synchronized (feedBack) {
                    if (noFeedback)
                        return;

                    noFeedback = true;
                    String str = "" + getValue();
                    if (!str.equals(numberField.getText()))
                        numberField.setText(str);
                    noFeedback = false;
                }
            }
        });
        numberField.getDocument().addDocumentListener(new DocumentListener(){

            public void update() {
                if (noFeedback) 
                    return;
                
                synchronized (feedBack) {
                    if (noFeedback) 
                        return;

                    long textVal = getLongFromString(numberField.getText());
                    if (textVal == (Long)getValue()) 
                        return;

                    noFeedback = true;
                    setValue(textVal);
                    noFeedback = false;
                }
                
            }
            
            public void insertUpdate(DocumentEvent e) {
                update();
            }

            public void removeUpdate(DocumentEvent arg0) {
                update();
            }

            public void changedUpdate(DocumentEvent arg0) {
                update();
            }
            
        });
        setEditor(numberField);
        numberField.setText("" + getValue());

        Options.addLocalListener(new WeakLocalListener(this));
    }

    public void stateChanged(LocalListenerEvent event) {
        LayeredResourceBundle res = Options.getI18N(FileSizeSpinner.class);
        LayeredResourceBundle oldRes = res.getBundle(event.getOldLocale());
        
        if (numberField.getText().equals(oldRes.getString("infinite")))
            numberField.setText(res.getString("infinite"));
    }

}
