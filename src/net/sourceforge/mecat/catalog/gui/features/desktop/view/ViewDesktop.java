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
 * Created on Dec 6, 2005
 * @author Stephan Richard Palm
 * 
 */
package net.sourceforge.mecat.catalog.gui.features.desktop.view;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;

import javax.swing.DefaultButtonModel;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.Element;
import javax.swing.text.StyleConstants;

import net.sourceforge.mecat.catalog.gui.features.RomFileListFeaturePanel;
import net.sourceforge.mecat.catalog.gui.features.desktop.FeatureDesktop;
import net.sourceforge.mecat.catalog.medium.Medium;
import net.sourceforge.mecat.catalog.medium.MediumListener;
import net.sourceforge.mecat.catalog.medium.features.Feature;
import net.sourceforge.mecat.catalog.medium.features.SubEntryListFeature;
import net.sourceforge.mecat.catalog.medium.features.TextFeature;
import net.sourceforge.mecat.catalog.medium.features.impl.Ident;
import net.sourceforge.mecat.catalog.medium.features.impl.RomFileList;
import net.sourceforge.mecat.catalog.medium.features.option.SubEntryListFeatureOption;
import net.sourceforge.mecat.catalog.option.LocalListener;
import net.sourceforge.mecat.catalog.option.LocalListenerEvent;
import net.sourceforge.mecat.catalog.option.Options;
import net.sourceforge.mecat.catalog.option.WeakLocalListener;
import net.sourceforge.mecat.catalog.option.preferences.TotalPreferences;

public class ViewDesktop implements FeatureDesktop, LocalListener {

    boolean showOtherLanguages = true;
    
    int preferredWidth = -1;
    int internalPreferredWidth = -1;
    
    JEditorPane textfield = new JEditorPane(){
/*        @Override
        public Dimension getPreferredSize() {
            if (preferredWidth == -1)
                return super.getPreferredSize();
                
            Dimension dim = new Dimension(internalPreferredWidth, super.getPreferredSize().height);

            return dim;
        }*/

        @Override
        public boolean getScrollableTracksViewportWidth() {
            return true;
        }

    };
    
    JScrollPane scrollPane = new JScrollPane(textfield, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    Medium medium = null;

    public void setShowOtherLanguage(final boolean show) {
        this.showOtherLanguages = show;
    }
    
    public boolean getShowOtherLanguages() {
        return showOtherLanguages;
    }
    
    MediumListener viewMediumListener = new MediumListener(){

        public void nameChanged(Medium medium) {
            // Nothing to do
        }

        public void mediumChanged(Medium medium) {
            rebuildTextField();
        }
    };
    
    public ViewDesktop() {
        textfield.setContentType("text/html");
        textfield.setEditable(false);
        textfield.addHyperlinkListener (
                new HyperlinkListener () {
                    public void hyperlinkUpdate(HyperlinkEvent e) {
                        System.out.println(e.getEventType());
                        // TODO start browser
                    }
                });
        
        scrollPane.addComponentListener(new ComponentListener(){

            public void componentResized(ComponentEvent arg0) {
                rebuildTextField();
            }

            public void componentMoved(ComponentEvent arg0) {
                // if the component get moved the size does not change
            }

            public void componentShown(ComponentEvent arg0) {
                // if the component is shown the size can change
                // due to the editpane appearing
                rebuildTextField();
            }

            public void componentHidden(ComponentEvent arg0) {
                // if the component hiddes it will show up againg
            }
            
        });
        Options.addLocalListener(new WeakLocalListener(this));
    }

    public void stateChanged(LocalListenerEvent event) {
        // If the language has changed the simplest way to
        // change the language is to rebuild the textfield
        rebuildTextField();
    }

    public JComponent getDesktop() {
        return scrollPane;
    }

    public void setMedium(Medium medium) {
        if (this.medium != null)
            this.medium.removeMediumListener(viewMediumListener);
        this.medium = medium;
        if (medium != null)
            medium.addMediumListener(viewMediumListener);
        rebuildTextField();
    }

    public Map<String, ActionListener> getButtonMap(Medium medium) {
        Map<String, ActionListener> buttonMap = new HashMap<String, ActionListener>();
        
        TotalPreferences totalPreferences = medium.getListing().getTotalPreferences();
        for (Feature feature : medium.getFeatures()) {
            if (!feature.hasValue())
                continue;
            
            if (!totalPreferences.getMediaOption().isWanted(medium.getClass(), feature.getClass(), ViewDesktop.class))
                continue;
            
            if (feature instanceof RomFileList) {
                final RomFileList rfl = (RomFileList) feature;
                
                buttonMap.put("RomFileList", new ActionListener(){
                    public void actionPerformed(ActionEvent e) {
                        RomFileListFeaturePanel.openExlporerWindow(textfield, ViewDesktop.this, rfl);
                    }
                });
                continue;
            }
        }

        return buttonMap;
    }
    
    public static String getHTML(Medium medium, int availableWidth, int titleIdentation) {
        return getHTML(medium, availableWidth, titleIdentation, false);
    }
    
    public static String getHTML(Medium medium, int availableWidth, int titleIdentation, boolean buttons) {
        String h1 = "<H" + (titleIdentation  + 1) + ">";
        String _h1 = "</H" + (titleIdentation  + 1) + ">";

        String h2 = "<H" + (titleIdentation  + 2) + ">";
        String _h2 = "</H" + (titleIdentation  + 2) + ">";
        
        TotalPreferences totalPreferences = medium.getListing().getTotalPreferences();
        StringBuffer html = new StringBuffer();
        html.append(h1 + medium.toString() + _h1);
        for (Feature feature : medium.getFeatures()) {
            if (!feature.hasValue())
                continue;
            
            if (!totalPreferences.getMediaOption().isWanted(medium.getClass(), feature.getClass(), ViewDesktop.class))
                continue;
            
            if (feature instanceof RomFileList) {
                html.append(h2 + Options.getI18N(feature.getClass()).getString(feature.getClass().getSimpleName()) + ":" + _h2);
                html.append(feature.getTextHTML(availableWidth));
                if (buttons)
                    html.append("<form action=\"\"><input type=\"submit\" name=\"RomFileList\" value=\"" 
                            + Options.getI18N(ViewDesktop.class).getString("Show file list") + "\"></form>");

                continue;
            }
            
            // For SubEntryListFeature we use this function recursiv
            if (feature instanceof SubEntryListFeature) {
                SubEntryListFeature subs = ( SubEntryListFeature ) feature;
                SubEntryListFeatureOption option = subs.getSubEntryListFeatureOption();

                Vector<? extends Medium> media = subs.getMediaUnpointed();
                if (option.getShowSorting() != null)
                    Collections.sort(media, option.getShowSorting());
                    
                if (media.size() == 0)
                    continue;
                
                if (option != null && option.isUseShortVersionForView()) {
                    html.append(h2 + Options.getI18N(feature.getClass()).getString(feature.getClass().getSimpleName()) + ":" + _h2);
                    for (Medium sub : media) {
                        html.append(sub.toString());
                        html.append("<BR>");
                    }
                } else {
                    html.append(h2 + Options.getI18N(feature.getClass()).getString(feature.getClass().getSimpleName()) + ":" + _h2);
                    html.append("<table border=\"1\">");
                    
                    for (Medium sub : media) {
                        html.append("<tr>");
                        html.append(getHTML(sub, availableWidth - 15, titleIdentation + 1));
                        html.append("</tr>");
                    }
                    
                    html.append("</table>");
                }
                
                continue;
            }
            if (feature instanceof Ident)
                continue;
            String htmlTxt = feature.getTextHTML(availableWidth);
            if (htmlTxt == null) 
                continue;

            html.append(h2 + Options.getI18N(feature.getClass()).getString(feature.getClass().getSimpleName()) + ":" + _h2);
            html.append(feature.getTextHTML(availableWidth));

            // For TextFeature other Languages are shown too
            // TODO make optional
            if (feature instanceof TextFeature) {
                TextFeature txt = (TextFeature) feature;
                if (txt.localized) {
                    String current = txt.get();
                    for (Locale l : txt.getLanguages()) {
                        if (l.equals(Options.getCurrentLocale())) 
                            continue;
                        String localizedStr = txt.get(l);
                        if (localizedStr == null || localizedStr.length() == 0 || localizedStr.equals(current))
                            continue;
                        html.append("<br>( <em>" + l.getDisplayLanguage(Options.getCurrentLocale()) + "</em> ) " + localizedStr);
                    }
                            
                }
            }
        }
        return html.toString();
    }
    
    static protected void addActionToButton(Element element, Map<String, ActionListener> buttonMap) {
        AttributeSet atts = element.getAttributes();
        Object type = atts.getAttribute(HTML.Attribute.TYPE);
        Object name = atts.getAttribute(HTML.Attribute.NAME);

        if (type != null && name != null
                && type.equals("submit") 
                && buttonMap.containsKey(name)){
            DefaultButtonModel button = (DefaultButtonModel) atts.getAttribute(StyleConstants.ModelAttribute);
            button.addActionListener(buttonMap.get(name));
        }
        
        for (int i = 0; i < element.getElementCount(); i++){
            Element child = element.getElement(i);
            addActionToButton(child, buttonMap);
        }
    }
    
    protected void rebuildTextField() {
        int internalPreferredWidth = scrollPane.getViewport().getWidth() - textfield.getInsets().left - textfield.getInsets().right;

        if (medium != null) {
            textfield.setText("<HTML><BODY>" + getHTML(medium, internalPreferredWidth, 1, true) + "</BODY></HTML>");
            Map<String, ActionListener> buttonMap = getButtonMap(medium);
            for (Element element : ((HTMLDocument) textfield.getDocument()).getRootElements())
                addActionToButton(element, buttonMap);
        } else
            textfield.setText("<HTML><BODY><H1>" + Options.getI18N(ViewDesktop.class).getString("No medium selected.") + "</H1></BODY></HTML>");
    }

    public void saveSettings() {
        // TODO Auto-generated method stub

    }

    public void loadSettings() {
        // TODO Auto-generated method stub

    }

    public void setPreferredDesktopWidth(int width) {
//        preferredWidth = width;
//        Insets scrollPaneInsets = scrollPane.getInsets(); 
//        Insets textFieldInsets = textfield.getInsets();
//        int scrollPaneDiff = textFieldInsets.left + textFieldInsets.right + scrollPaneInsets.left + scrollPaneInsets.right + ((scrollPane.getVerticalScrollBar().isVisible())?scrollPane.getVerticalScrollBar().getWidth():0);
//        internalPreferredWidth = preferredWidth - scrollPaneDiff;
//        rebuildTextField();
    }

    public void Rebuild() {
        // TODO Auto-generated method stub

    }

    public void requestFocus() {
        // Does not take focus at this time
    }

}
