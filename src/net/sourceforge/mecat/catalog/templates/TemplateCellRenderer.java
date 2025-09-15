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
 * Created on Jul 13, 2006
 * @author Stephan Richard Palm
 * 
 */
package net.sourceforge.mecat.catalog.templates;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.util.Vector;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import net.sourceforge.mecat.catalog.gui.ToolBarUtils;
import net.sourceforge.mecat.catalog.medium.Medium;
import net.sourceforge.mecat.catalog.option.Options;

public class TemplateCellRenderer implements ListCellRenderer {

    JList dummy = new JList();
    static class TemplateEditorPane extends JEditorPane{

        boolean isSelected = false;
        boolean cellHasFocus = false;
        Template template = null;
        Vector<JButton> buttons = new Vector<JButton>();
        
        public TemplateEditorPane() {
            setContentType("text/html");
            setOpaque(false);
        }
        
        
        public void setSelected(boolean isSelected) {
            this.isSelected = isSelected;
            repaint();
        }
        
        public void setCellHasFocus(boolean cellHasFocus) {
            this.cellHasFocus = cellHasFocus;
            repaint();
        }

        public void setTemplate(Template template) {
            this.template = template;
            setText("<HTML><BODY><TABLE border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" height=\"100%\">" 
                    +"<tr><td align=\"center\" valign=\"middle\" height=\"80px\"><font size=\"+2\" face=\"Avalon,Wide Latin\"><b>"
                    + template.getTitleHTML() + "</b></font></td></tr></table></CENTER></BODY></HTML>");

            buttons.clear();
            for (Class<? extends Medium> medium : template.media) {
                String name = Options.getI18N(medium).getString(medium.getSimpleName());
                buttons.add(ToolBarUtils.makeButton(medium, medium.getSimpleName(), "", name, name, null));
            }
        }


        @Override
        public void paintComponent(Graphics g) {
            
            int arcWidth = getWidth() / 8;
            int arcHeight = getHeight() / 6;
            
            if (isSelected) {
                g.setColor(new Color(200,200,255));
                g.fillRoundRect(3, 3, getWidth()-8, getHeight()-8, arcWidth, arcHeight);
            }

            if (isSelected) 
                g.setColor(Color.green);
            else
                g.setColor(Color.gray);
            g.drawRoundRect(2, 2, getWidth()-6, getHeight()-6, arcWidth, arcHeight);
            g.drawRoundRect(3, 3, getWidth()-8, getHeight()-8, arcWidth, arcHeight);

            Icon icon;
            switch (buttons.size()) {
            case 0:
                break;
            case 1:
                if (isSelected) 
                    buttons.firstElement().getPressedIcon().paintIcon(this, g, 10, 30);
                else 
                    buttons.firstElement().getIcon().paintIcon(this, g, 10, 30);
                break;
            case 2:
                icon = (isSelected) ? buttons.get(0).getPressedIcon() : buttons.get(0).getIcon();
                icon.paintIcon(this, g, 10, 30);
                icon = (isSelected) ? buttons.get(1).getPressedIcon() : buttons.get(1).getIcon();
                icon.paintIcon(this, g, this.getWidth() -  icon.getIconWidth() - 10, 30);
                break;
            case 3:
                icon = (isSelected) ? buttons.get(0).getPressedIcon() : buttons.get(0).getIcon();
                icon.paintIcon(this, g, 35, 10);
                icon = (isSelected) ? buttons.get(1).getPressedIcon() : buttons.get(1).getIcon();
                icon.paintIcon(this, g, 10, 55);
                icon = (isSelected) ? buttons.get(2).getPressedIcon() : buttons.get(2).getIcon();
                icon.paintIcon(this, g, this.getWidth() -  icon.getIconWidth() - 10, 30);
                break;
            default:
                icon = (isSelected) ? buttons.get(0).getPressedIcon() : buttons.get(0).getIcon();
                icon.paintIcon(this, g, 35, 10);
                icon = (isSelected) ? buttons.get(1).getPressedIcon() : buttons.get(1).getIcon();
                icon.paintIcon(this, g, 10, 55);
                icon = (isSelected) ? buttons.get(2).getPressedIcon() : buttons.get(2).getIcon();
                icon.paintIcon(this, g, this.getWidth() -  icon.getIconWidth() - 35, 55);
                icon = (isSelected) ? buttons.get(3).getPressedIcon() : buttons.get(3).getIcon();
                icon.paintIcon(this, g, this.getWidth() -  icon.getIconWidth() - 10, 10);

            }

            super.paintComponent(g);
            
            if (cellHasFocus) 
                g.setColor(Color.blue);
            else
                g.setColor(Color.white);
            g.drawRoundRect(0, 0, getWidth()-2, getHeight()-2, arcWidth, arcHeight);
            g.drawRoundRect(1, 1, getWidth()-4, getHeight()-4, arcWidth, arcHeight);

            if (template == null)
                return;
            
        }

        
        
    };
    
    TemplateEditorPane textfield = new TemplateEditorPane();
    
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        if (!(value instanceof Template))
            return dummy.getCellRenderer().getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        Template template = (Template) value;

        textfield.setSelected(isSelected);
        textfield.setCellHasFocus(cellHasFocus);
        textfield.setTemplate(template);
        
        
        return textfield;
    }

}
