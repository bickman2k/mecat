/*
 * Created on 16.01.2007
 * @author Stephan Richard Palm
 * 
 * 
 * Just like a JTextField but it only takes positive Integer values.
 */
package net.sourceforge.mecat.srp.utils;

import javax.swing.JFormattedTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultFormatter;
import javax.swing.text.DocumentFilter;

public class IntegerField extends JFormattedTextField {

    
    static class IntegerDocumentFilter extends DocumentFilter {

        IntegerField integerField = null;
        private boolean keepZero;

        public IntegerDocumentFilter(boolean keepZero){
            this.keepZero = keepZero;
        }
        
        public void insertString(FilterBypass fb, int offset,
                String string, AttributeSet attr)
                throws BadLocationException {
            super.insertString(fb, offset, numberOnly(string), attr);
        }

        public void remove(FilterBypass fb, int offset, int length)
                throws BadLocationException {
            super.remove(fb, offset, length);

            if (integerField != null){
                if (keepZero && integerField.getText().length() == 0)
                integerField.setText("0");
            }
        }

        public void replace(FilterBypass fb, int offset,
                int length, String text, AttributeSet attrs)
                throws BadLocationException {

            super.replace(fb, offset, length, numberOnly(text), attrs);

            if (integerField != null){
                if (keepZero && integerField.getText().length() == 0)
                integerField.setText("0");
            }
        }
        
        private String numberOnly(String str) {
            String numberOnly = "";
            for (int i = 0; i < str.length(); i++) {
                char ch = str.charAt(i);
                if (ch >= '0' && ch <= '9')
                    numberOnly += str.charAt(i);
            }
            return numberOnly;
        }

        public void setIntegerField(IntegerField field) {
            integerField = field;
        }

        public void setKeepZero(boolean keepZero) {
            this.keepZero = keepZero;
        }
    };
    
    static class IntegerFormatter extends DefaultFormatter {
        final IntegerDocumentFilter integerDocumentFilter;

        public IntegerFormatter(boolean keepZero){
            setAllowsInvalid(false);
            integerDocumentFilter = new IntegerDocumentFilter(keepZero);
        }

        protected DocumentFilter getDocumentFilter() {
            return integerDocumentFilter;
        }
        
        public void setIntegerField(IntegerField field) {
            integerDocumentFilter.setIntegerField(field);
        }
        
        public void setKeepZero(boolean keepZero) {
            integerDocumentFilter.setKeepZero(keepZero);
        }
    }
    
    /**
     * same as {@link #IntegerField(int, boolean)} with number 0
     * and keepZero true
     */
    public IntegerField() {
        this(0, true);
    }

    /**
     * same as {@link #IntegerField(int, boolean)} with keepZero true
     * @param number The initial number of the integer field.
     */
    public IntegerField(int number) {
        this(number, true);
    }

    /**
     * same as {@link #IntegerField(int, boolean)} with number 0
     * @param keepZero setting this flag to true there will be a number at this field
     * all the time. If the last number is deleted a zero will replace it.
     */
    public IntegerField(boolean keepZero) {
        this(0, keepZero);
    }

    /**
     * 
     * @param number The initial number of the integer field.
     * @param keepZero setting this flag to true there will be a number at this field
     * all the time. If the last number is deleted a zero will replace it.
     */
    public IntegerField(int number, boolean keepZero) {
        this(number, new IntegerFormatter(keepZero));
    }
    
    private IntegerField(int number, IntegerFormatter formatter) {
        super(formatter);
        formatter.setIntegerField(this);
        setText(number + "");
    }

    public void setNumber(int i) {
        setText("" + i);
    }
    
    public int getNumber() {
        try {
            return Integer.parseInt(getText());
        } catch (Exception e) {
            return 0;
        }
    }
}
