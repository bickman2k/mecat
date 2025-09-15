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
 * Created on Sep 4, 2005
 * @author Stephan Richard Palm
 * 
 */
package net.sourceforge.mecat.catalog.filter.design;

import java.awt.Component;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.ComboBoxEditor;
import javax.swing.ComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

public class ButtonComboBox extends JComboBox{

    private void init() { 
        this.setEditable(true);
        this.setEditor(new ComboBoxEditor(){
            Object value;
            public Component getEditorComponent() {
                value = getSelectedItem();
                if (value instanceof JButton)
                    return (JButton) value;
                
                return new JLabel("" + value);
            }
            
            public void setItem(Object value) {
                this.value = value;
            }
            
            public Object getItem() {
                return value;
            }
            
            public void selectAll() {
            }
            
            public void addActionListener(ActionListener arg0) {
            }
            
            public void removeActionListener(ActionListener arg0) {
            }
        });
        this.setRenderer(new ListCellRenderer(){
            JComboBox dummy = new JComboBox();
            public Component getListCellRendererComponent(JList list, Object value, int arg2, boolean arg3, boolean arg4) {
                if (value instanceof JButton)
                    return (JButton)value;
                return dummy.getRenderer().getListCellRendererComponent(list, value, arg2, arg3, arg4);
            }}); 
    }
    
    public ButtonComboBox() {
        super();
        init();
    }
    
    public ButtonComboBox(ComboBoxModel arg0) {
        super(arg0);
        init();
    }
    
    public ButtonComboBox(Object[] arg0) {
        super(arg0);
        init();
    }

    public ButtonComboBox(Vector< ? > arg0) {
        super(arg0);
        init();
    }}