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
 * Created on Oct 25, 2005
 * @author Stephan Richard Palm
 * 
 */
package net.sourceforge.mecat.catalog.export.shared.list;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import net.sourceforge.mecat.catalog.gui.ListingProperties;
import net.sourceforge.mecat.catalog.gui.ListingPropertiesVisualisation;
import net.sourceforge.mecat.catalog.gui.ShowListing;
import net.sourceforge.mecat.catalog.gui.ToolBarUtils;
import net.sourceforge.mecat.catalog.i18n.fromCatalog.LayeredResourceBundle;
import net.sourceforge.mecat.catalog.medium.features.Feature;
import net.sourceforge.mecat.catalog.medium.features.MultiChoiceFeature;
import net.sourceforge.mecat.catalog.option.LocalListener;
import net.sourceforge.mecat.catalog.option.LocalListenerEvent;
import net.sourceforge.mecat.catalog.option.Options;
import net.sourceforge.mecat.catalog.option.WeakLocalListener;
import net.sourceforge.mecat.srp.utils.ButtonBorder;
import net.sourceforge.mecat.srp.utils.NiceClass;

public abstract class ListDefinitionVisualisation<TYPE extends Enum, EXPORTDEF extends ExportDef<TYPE>, EXPORTCOL extends ExportCol<?, EXPORTDEF>, 
                                        EXPORTCOLVISUALISATION extends ExportColVisualisation<TYPE, EXPORTDEF, EXPORTCOL>,
                                        LISTDEFINITION extends ListDefinition<TYPE, EXPORTDEF, EXPORTCOL>> extends JPanel implements LocalListener{


    public ShowListing showListing;
    
    final ListingPropertiesVisualisation listingPropertiesVisualisation = new ListingPropertiesVisualisation();
    
    LISTDEFINITION listDefinition;
    
    final PropertyChangeListener sizeChangeListener = new PropertyChangeListener(){
        public void propertyChange(PropertyChangeEvent arg0) {
            firePropertyChange("size", null, null);
        }
    };

    protected LayeredResourceBundle res = Options.getI18N(ListDefinitionVisualisation.class);

    final JComboBox mcfsChoice;
    final JTextField numMcfs = new JTextField("0");

    final JPanel generalOptions = new JPanel();
    final JPanel texPanel = new JPanel();
    final JPanel columnPanel = new JPanel();
    final JPanel listPanel = new JPanel();

    JLabel generalOptionsMCFLabel = new JLabel(); // label set at setLabels

    final JButton removeButton = new JButton();

    final JCheckBox override = new JCheckBox();
    final JButton editListSettings = ToolBarUtils.makeButton("edit",null, null, res.getString("Edit"),  null);// new JButton(res.getString("Edit"));
    final JLabel listSettingsLabel = new JLabel();
    
    JList selectedJList;
    
    final JTextField TeX = new JTextField(res.getString("List"));

    TitledBorder titledBorder = BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.BLACK), ""); // Title set at setLabels
    
    JLabel[] labels = new JLabel[0];
    
    protected void setLabels(int index, int size) {
        String[] strs = res.getString("NumberOfSize").replaceAll("\\[NUMBER\\]", String.valueOf(index)).replaceAll("\\[SIZE\\]", String.valueOf(size)).split("\\s");
        labels = new JLabel[strs.length];
        for (int i = 0; i < strs.length; i++)
            labels[i] = new JLabel(strs[i]);
    }
    
    final TitledBorder columnTitledBorder = BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.BLACK), "");

    public void setListDefinition(final LISTDEFINITION listDefinition, final int index, final int size, final ShowListing showListing) {
        this.listDefinition = listDefinition;
        this.showListing = showListing;
        listingPropertiesVisualisation.setShowListing(showListing);
        setLabels(index, size);
        InitPanel();
    }

    public ListDefinitionVisualisation() {
        setBorder(new Border(){
            final int edgeDistance = 10;
            final int space = 3;
            final int textSpace = 4;
            
            public Insets getBorderInsets(Component arg0, Insets arg1) {
                return titledBorder.getBorderInsets(arg0, arg1);
            }

            public Insets getBorderInsets(Component arg0) {
                return titledBorder.getBorderInsets(arg0);
            }

            public Rectangle getInteriorRectangle(Component arg0, int arg1, int arg2, int arg3, int arg4) {
                return titledBorder.getInteriorRectangle(arg0, arg1, arg2, arg3, arg4);
            }

            public boolean isBorderOpaque() {
                return titledBorder.isBorderOpaque();
            }

            public void paintBorder(Component component, Graphics g, int x, int y, int w, int h) {
                titledBorder.paintBorder(component, g, x, y, w, h);
                int pos = x+w-edgeDistance;
                g.setColor(Color.BLACK);
                
                for (int i = labels.length - 1; i >= 0; i--) {
                    // First tell the label it has the size it wants to
                    // else it can not be drawn
                    labels[i].setSize(labels[i].getPreferredSize());
                    // Calculate the dimension of the field includind
                    // space for the arcs
                    Dimension dim = new Dimension(labels[i].getWidth() + 2*textSpace, labels[i].getHeight());
                    // If the the width is to small then
                    // make it the same as the height
                    if (dim.width < dim.height)
                        dim.width = dim.height;
                    pos -= dim.width + space;

                    // Clear the part where the label will be drawn
                    g.setColor(getBackground());
                    g.clearRect(pos, 0, dim.width, dim.height);
                    g.setColor(Color.BLACK);
                    // Draw the text
                    g.translate(pos + textSpace, 0);
                    labels[i].paint(g);
                    g.translate(-(pos + textSpace), 0);
                    // Draw the surrounding
                    g.drawArc(pos, 0, dim.height, dim.height, 90, 180);
                    if (dim.width > dim.height) {
                        g.drawLine(pos + dim.height/2, 0, pos + dim.width - dim.height/2, 0);
                        g.drawLine(pos + dim.height/2, dim.height, pos + dim.width - dim.height/2, dim.height);
                    }
                    g.drawArc(pos + dim.width - dim.height, 0, dim.height, dim.height, 270, 180);
                }
            }
            
        });

        Vector<NiceClass<MultiChoiceFeature>> mcfs = new Vector<NiceClass<MultiChoiceFeature>>();
        for (NiceClass<Feature> f : Options.features) {
            Class c = f.getClasstype();
            if (MultiChoiceFeature.class.isAssignableFrom(c))
                mcfs.add(new NiceClass<MultiChoiceFeature>((Class<? extends MultiChoiceFeature>)c));
        }

        mcfsChoice = new JComboBox(mcfs);
        // Add ActionListener to new Combobox
        
        texPanel.setLayout(new BorderLayout());
        texPanel.add(TeX);
        
        generalOptions.setLayout(new GridLayout());
        generalOptions.add(generalOptionsMCFLabel, BorderLayout.WEST);
        generalOptions.add(mcfsChoice, BorderLayout.CENTER);
        generalOptions.add(numMcfs, BorderLayout.EAST);
        
//        columnPanel.addMouseListener(new PopupMouseListener(getJPopupMenu()));
        columnPanel.setLayout(new GridLayout());
        JButton plusButton = ToolBarUtils.makeMiniButton("plus", "", null, "", new ActionListener(){
            public void actionPerformed(ActionEvent arg0) {
                addColumn();
            }
        });
        ButtonBorder buttonBorder = new ButtonBorder(columnPanel, columnTitledBorder, plusButton);

        listPanel.setLayout(new BorderLayout());
        listPanel.add(override, BorderLayout.PAGE_START);
        listPanel.add(listSettingsLabel, BorderLayout.CENTER);
        listPanel.add(editListSettings, BorderLayout.EAST);
        
        mcfsChoice.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) { /*LOOK*/setMcfFromCombobox();/*HERE*/}});

        override.addActionListener(new ActionListener(){
            // List settings are only enabled if the list settings are overriden
            public void actionPerformed(ActionEvent arg0) {
                checkOverride();
            }
        });
        
        editListSettings.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                doEditListSettings();
            }});

        // Add KeyListener to new TextField
        numMcfs.addKeyListener(new KeyListener(){
            public void keyPressed(KeyEvent arg0) {setMcfNumberFromTextField();}
            public void keyReleased(KeyEvent arg0) {setMcfNumberFromTextField();}
            public void keyTyped(KeyEvent arg0) {setMcfNumberFromTextField();}
        });
        
        TeX.addKeyListener(new KeyListener(){
            void update(){ listDefinition.listName = TeX.getText(); }
            public void keyPressed(KeyEvent arg0) {update();}
            public void keyReleased(KeyEvent arg0) {update();}
            public void keyTyped(KeyEvent arg0) {update();}
        });
        

        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;
        c.gridwidth = GridBagConstraints.REMAINDER;
        add(texPanel, c);
        add(generalOptions, c);
        add(columnPanel, c);
        c.weighty = 1.0;
        c.gridheight = GridBagConstraints.REMAINDER;
        add(listPanel, c);

        Options.addLocalListener(new WeakLocalListener(this));
        // Set labels to the current locale
        setLabels(null);
    }

    public void stateChanged(LocalListenerEvent event) {
/*        // Set res to actual local _ allready uses the actual locale
        res = Options.getI18N(ListDefinitionVisualisation.class);*/
        
        setLabels(event);

    }
    
    public void setLabels(LocalListenerEvent event) {
        override.setText(res.getString("Override list options"));
        editListSettings.setToolTipText(res.getString("Edit"));
        removeButton.setText(res.getString("Remove column"));    
        titledBorder.setTitle(res.getString("List settings:"));
        columnTitledBorder.setTitle(res.getString("Export settings:"));

        texPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.BLACK), 
                res.getString("ListTitle") + ":"));
        generalOptions.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.BLACK), 
                res.getString("General Options:")));

        generalOptionsMCFLabel.setText(Options.getI18N(MultiChoiceFeature.class).getString("MultiChoiceFeature") + ":");

        listPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.BLACK), 
                res.getString("List Options:")));
        
        if (event != null) {
            if (TeX.getText().equals(res.getBundle(event.getOldLocale()).getString("List")))
                TeX.setText(res.getString("List"));
        }
    }

    protected void checkOverride() {
        listDefinition.override = override.isSelected();
        editListSettings.setEnabled(override.isSelected());
        listSettingsLabel.setEnabled(override.isSelected());
    }
    
    protected void InitPanel() {
        mcfsChoice.setSelectedItem(listDefinition.mcfeature);
        numMcfs.setText(String.valueOf(listDefinition.maxChoices));
        mcfsChoice.setEnabled(listDefinition.maxChoices > 0);
        TeX.setText(listDefinition.listName);
        override.setSelected(listDefinition.override);
        listSettingsLabel.setText(listDefinition.listProperties.getHTML());
        actualizeColumnPanel();
        checkOverride();
        updateUI();
    }
    
    protected void doEditListSettings() {
        // Make copy from the listingProperties
        // this one will be edited
        ListingProperties copy = listDefinition.listProperties.getCopy();
        listingPropertiesVisualisation.setListingProperties(copy);
        // Show the user the editor
        int ret = JOptionPane.showConfirmDialog(ListDefinitionVisualisation.this,  listingPropertiesVisualisation, res.getString("Edit ListProperties"), JOptionPane.OK_CANCEL_OPTION);
        // Look if the user wants to keep the changes
        if (ret == JOptionPane.OK_OPTION) {
            // Use the copy with the changes
            listDefinition.listProperties = copy;
            // Show the new listingProperties
            listSettingsLabel.setText(listDefinition.listProperties.getHTML());
            // Update visual
            firePropertyChange("size", null, null);
        }
    }

    void setMcfFromCombobox() {
        if (!(mcfsChoice.getSelectedItem() instanceof NiceClass))
            return;
        listDefinition.mcfeature = (NiceClass)mcfsChoice.getSelectedItem();
    }
    
    void setMcfNumberFromTextField() {
        if (numMcfs.getText().compareTo("") == 0) {
            listDefinition.maxChoices = 0;
            mcfsChoice.setEnabled(false);
            return;
        }
        
        try {
            int num = Integer.valueOf(numMcfs.getText()).intValue();
            listDefinition.maxChoices = num;
        } catch (Exception e) {
            numMcfs.setText(String.valueOf(listDefinition.maxChoices));
        }
        mcfsChoice.setEnabled(listDefinition.maxChoices != 0);
        
    }

    final Map<EXPORTCOL, ExportColVisualisation> exportColVisualisations = new LinkedHashMap<EXPORTCOL, ExportColVisualisation>();
    
    /**
     * Should return a new ExportColVisualisation.
     * 
     * @param column
     * @param next Should it have a next button
     * @param prev Should it have a previous button
     * @param kill Should it have a destroy/remove button
     * @return the new instance of ExportColVisualisation
     */
    abstract protected EXPORTCOLVISUALISATION getExportColVisualisation(EXPORTCOL column, boolean next, boolean prev, boolean kill);
    
    public void actualizeColumnPanel() {
        columnPanel.removeAll();
        exportColVisualisations.clear();
        
        for (final EXPORTCOL column : listDefinition.columns) {
            EXPORTCOLVISUALISATION exportColVisualisation = getExportColVisualisation(column, listDefinition.columns.lastElement() != column, listDefinition.columns.firstElement() != column, listDefinition.columns.size() > 1);
            exportColVisualisations.put(column, exportColVisualisation);
            exportColVisualisation.addPropertyChangeListener("size", sizeChangeListener);
            exportColVisualisation.addExportColVisualisationListener(new ExportColVisualisationListener(){
                public void nextClicked() {
                    listDefinition.moveColumnForward(column);
                    actualizeColumnPanel();
                }

                public void prevClicked() {
                    listDefinition.moveColumnBackwards(column);
                    actualizeColumnPanel();
                }

                public void killClicked() {
                    listDefinition.removeColumn(column);
                    actualizeColumnPanel();
                }
                
                public void editClicked() {
                    column.editColumn(ListDefinitionVisualisation.this);
                    actualizeColumnPanel();
                }
                
            });
            columnPanel.add(exportColVisualisation);
        }
            
        firePropertyChange("size", null, null);
        columnPanel.updateUI();
    }
    
    abstract protected EXPORTCOL aquireNewColumn();
    
    public void addColumn() {
        EXPORTCOL col = aquireNewColumn();
        if (col == null)
            return;
        listDefinition.addColumn(col);
        actualizeColumnPanel();
    }
}
