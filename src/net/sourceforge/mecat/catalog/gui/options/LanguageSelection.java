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
 * Created on Jul 23, 2004
 * @author Stephan Richard Palm
 */
package net.sourceforge.mecat.catalog.gui.options;

import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.Locale;
import java.util.Vector;

import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class LanguageSelection extends JPanel {

	private JList list;
    
    protected Vector<ActionListener> actionListeners = new Vector<ActionListener>();
    
    public void addActionListener(final ActionListener actionListener) {
        actionListeners.add(actionListener);
    }
    
    public void removeActionListener(final ActionListener actionListener) {
        actionListeners.remove(actionListener);
    }

	public static class Languag implements Comparable<Languag> {
		public Languag(Locale locale) {
            this.locale=locale;
        }
		public Locale locale;

		public int compareTo(Languag o) {
			if (o instanceof Languag)
				return toString().compareTo(o.toString());
			return 0;
		}
		
        @Override
		public String toString() {
            if (locale == null)
                return null;
			return locale.getDisplayName(locale);
		}

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Languag))
                return false;
            
            Languag lang = ( Languag ) o;

            if (locale == null && lang.locale == null)
                return true;
            
            if (locale == null)
                return false;
            
            return locale.equals(lang.locale);
        }

        @Override
        public int hashCode() {
            if (locale == null)
                return 0;
            return locale.hashCode();
        }
	}

	Vector<Languag> choices = new Vector<Languag>(Locale.getISOLanguages().length);

    public LanguageSelection() {
		for (int i = 0; i < Locale.getISOLanguages().length; i++) {
			Locale l = new Locale(Locale.getISOLanguages()[i]);
			choices.add(new Languag(l));
		}
		Collections.sort(choices);
		setLayout(new BorderLayout());
		add(new JScrollPane(list = new JList(choices)));
        
        list.addListSelectionListener(new ListSelectionListener(){
            public void valueChanged(ListSelectionEvent arg0) {
                for (ActionListener actionListener : actionListeners)
                    actionListener.actionPerformed(null);
            }
        });
	}
	
	public Locale getLocale() {
		if (list.getSelectedIndex() != -1)
			return ((Languag)list.getSelectedValue()).locale;
		return null;
	}

    public void setLocale(final Locale locale) {
        list.setSelectedIndex(choices.indexOf(new Languag(locale)));
    }

}
