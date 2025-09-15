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

package net.sourceforge.mecat.catalog.gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.sourceforge.mecat.catalog.datamanagement.Catalog;
import net.sourceforge.mecat.catalog.datamanagement.CatalogFactory;
import net.sourceforge.mecat.catalog.datamanagement.Connection;
import net.sourceforge.mecat.catalog.datamanagement.Entry;
import net.sourceforge.mecat.catalog.datamanagement.PersistentThroughEntry;
import net.sourceforge.mecat.catalog.datamanagement.Util;
import net.sourceforge.mecat.catalog.datamanagement.changelog.ChangeLog;
import net.sourceforge.mecat.catalog.datamanagement.changelog.SaveStateListener;
import net.sourceforge.mecat.catalog.datamanagement.changelog.UnclosedTransactionException;
import net.sourceforge.mecat.catalog.datamanagement.changelog.UndoSelection;
import net.sourceforge.mecat.catalog.export.ExportChoice;
import net.sourceforge.mecat.catalog.filter.FeatureFilter;
import net.sourceforge.mecat.catalog.filter.Filter;
import net.sourceforge.mecat.catalog.filter.FilterListing;
import net.sourceforge.mecat.catalog.filter.LanguageFilter;
import net.sourceforge.mecat.catalog.filter.MediumFilter;
import net.sourceforge.mecat.catalog.filter.OrFilter;
import net.sourceforge.mecat.catalog.filter.TrueFilter;
import net.sourceforge.mecat.catalog.filter.design.FilterDesigner;
import net.sourceforge.mecat.catalog.gui.about.AboutFrame;
import net.sourceforge.mecat.catalog.gui.catalogDetails.CatalogDetails;
import net.sourceforge.mecat.catalog.gui.customMenu.CustomMenuRegister;
import net.sourceforge.mecat.catalog.gui.features.desktop.FeatureDesktop;
import net.sourceforge.mecat.catalog.gui.features.desktop.hardcoded.HardCodedDesktop;
import net.sourceforge.mecat.catalog.gui.features.desktop.image.ImageDesktopFrame;
import net.sourceforge.mecat.catalog.gui.features.desktop.view.ViewDesktop;
import net.sourceforge.mecat.catalog.gui.options.LanguageSelection;
import net.sourceforge.mecat.catalog.gui.options.OptionDialog;
import net.sourceforge.mecat.catalog.gui.search.SearchDialog;
import net.sourceforge.mecat.catalog.gui.search.SearchParameters;
import net.sourceforge.mecat.catalog.gui.utils.JMenuItemValued;
import net.sourceforge.mecat.catalog.i18n.fromCatalog.CatalogResource;
import net.sourceforge.mecat.catalog.i18n.util.LocalLabel;
import net.sourceforge.mecat.catalog.i18n.util.SimpleLocalButton;
import net.sourceforge.mecat.catalog.importCatalog.Import;
import net.sourceforge.mecat.catalog.importCatalog.StandardImport;
import net.sourceforge.mecat.catalog.medium.Listing;
import net.sourceforge.mecat.catalog.medium.Medium;
import net.sourceforge.mecat.catalog.medium.features.BadCondition;
import net.sourceforge.mecat.catalog.medium.features.Feature;
import net.sourceforge.mecat.catalog.medium.features.impl.Ident;
import net.sourceforge.mecat.catalog.medium.features.option.ChoiceFeatureOption;
import net.sourceforge.mecat.catalog.medium.features.option.FeatureOption;
import net.sourceforge.mecat.catalog.medium.features.option.merger.MergeProcess;
import net.sourceforge.mecat.catalog.medium.features.validators.ChoiceFeatureValidator;
import net.sourceforge.mecat.catalog.medium.features.validators.FeatureValidator;
import net.sourceforge.mecat.catalog.medium.features.validators.TextFeatureValidator;
import net.sourceforge.mecat.catalog.option.FromCommandLine;
import net.sourceforge.mecat.catalog.option.Options;
import net.sourceforge.mecat.catalog.option.preferences.AbstractMediaOption;
import net.sourceforge.mecat.catalog.option.preferences.CatalogPreferences;
import net.sourceforge.mecat.catalog.option.preferences.DefaultPreferences;
import net.sourceforge.mecat.catalog.option.preferences.GlobalPreferences;
import net.sourceforge.mecat.catalog.option.preferences.Preferences;
import net.sourceforge.mecat.catalog.sort.Comparing;
import net.sourceforge.mecat.catalog.sort.ConfigurableComparator;
import net.sourceforge.mecat.catalog.sort.SortedListing;
import net.sourceforge.mecat.catalog.sort.SortedListingListener;
import net.sourceforge.mecat.catalog.sort.design.CreateSortDialog;
import net.sourceforge.mecat.catalog.sort.design.SortManager;
import net.sourceforge.mecat.catalog.templates.NewWizard;
import net.sourceforge.mecat.srp.utils.NiceClass;
import net.sourceforge.mecat.srp.utils.StatusBar;

/**
 * Created on Jul 15, 2004
 * @author Stephan Richard Palm
 * 
 * Backend for MainFrame and other Windows containing a list of media and a desktop.
 * 
 * Functionalities like creating the menu and the toolbar are stored here
 * 
 */
public class MainFrameBackend implements SaveStateListener {


    
    protected final Display display;
    
    public static interface Display {
    
        public Window getWindow();
        public void setTitle(String title);
        public void setJMenuBar(JMenuBar titleMenu);
        public JMenuBar getJMenuBar();
        public Container getContentPane();
        public void setContentPane(Container contentPane);
        public void setExtendedState(int state);
        public int getExtendedState();

    }
	
/**
 * Following now are elements for the GUI
 */
    ResourceBundle menuResources = Options.getI18N(MainFrameBackend.class);
    final String mecat =  menuResources.getString("MeCat");
  

    // The things you see on the gui
	protected JSplitPane splitListAndFeatures = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
	protected JPanel featurePanel = new JPanel();

    protected JPanel contentPane;
    protected BorderLayout borderLayout1 = new BorderLayout();

    protected JMenuBar titleMenu;

    protected JToolBar toolbar = new JToolBar();

    
    /**
     * The statusbar that is shown on the south end of the frame
     */
    protected StatusBar statusBar = new StatusBar(5);
    
    /**
     * First of 6 elements of the statusBar.
     * It shows the number of Entries that are in the current catalog
     */
    LocalLabel numberOfEntries = null;
    
    /**
     * Second of 6 elements of the statusBar.
     * It shows the number of Entries from the current catalog
     * that fulfil condition of the filter.
     */
    LocalLabel filteredLevel0Entries = null;
    
    /**
     * Third of 6 elements of the statusBar.
     * It shows the number of Entries from the current catalog
     * that fulfil condition of the filter.
     */
    LocalLabel filteredLevel1Entries = null;
    
    /**
     * Fourth of 6 elements of the statusBar. (Is not shown)
     * It shows the number of Entries from the current catalog
     * that fulfil condition of the filter.
     */
    LocalLabel filteredLevel2Entries = null;
    
    /**
     * Fifth of 6 elements of the statusBar.
     * It shows the filter that is applied to the current catalog
     */
    LocalLabel filterStatus = null;
    
    /**
     * Sixth of 6 elements of the statusBar.
     * It shows the sorting that is applied to the current catalog
     */
    LocalLabel sortStatus = null;
    
	
/**
 * Following now are the neccessary merory elements
 */

/**	This listing containts all mediums */
	protected Listing liste = null;
//	protected Listing liste = new Listing(Options.stdFactory.createCatalog());
/**	This listing only contains the medium after using the filter */
	final protected FilterListing filterListeLevel0;// defined as = new FilterListing(liste);
    final protected FilterListing filterListeLevel1;// defined as = new FilterListing(liste);
    final protected FilterListing filterListeLevel2;// defined as = new FilterListing(liste);

	final protected SortedListing sortedListe;// defined as = new SortedListing(filterListe);
	
	final protected ShowListing showedList; // defined as = new ShowListing(sortedListe);

    final protected ChangeLog changeLog = new ChangeLog();
    
	protected JList list;

    FeatureDesktop desktop = new ViewDesktop();
    
    protected Medium selectedMedium = null;
    
    JButton lockButton;
    
/**
 * Variables containing the copy buffer
 */    
    protected Catalog copyBuffer = null;
    /**
     * edit and paste menu elements global availabe makes them changebel
     */
    JMenuItem editMenu_Paste;   
    JMenuItem editMenu_Copy;
    JMenuItem editMenu_Undo;
    
//    ChangeLog changeLog = null;
    int editMediumTransaction = -1;
    Medium editMediumTransactionMedium = null;
    
    /**
     * This function returns the amount of space between
     * the menubar or toolbar and the right border.
     * The shorter distance of both will be returned.
     * @return
     */
    public int spaceRightOfToolbarAndMenu() {
        int available_width = display.getWindow().getSize().width - display.getWindow().getInsets().left - display.getWindow().getInsets().right;
        int menu_width = titleMenu.getPreferredSize().width;
        int toolbar_width = toolbar.getPreferredSize().width;
        
        return available_width - Math.max(menu_width, toolbar_width);
    }
    
    public void setCatalog(Catalog catalog) {
        // Clear changelog
        changeLog.clear();

        liste.setCatalog(catalog);

        // Construct level null filter that shows only those
        // medium types that are visible
        makeLevel0Filter();
        
        // Catalog stats are influenced by the result from makeLevel0Filter
        catalogStats();
        updateToolbar();

        // Changing the catalog has the ability to change the menu
        // therefore we have to recreate the Menu
        InitM();
        
        // The title line may change
        updateTitle();
    }

    boolean level0FilterEnabled = true;
    
    protected void makeLevel0Filter(){
        if (!level0FilterEnabled)
            return;
        
        Vector<MediumFilter> mediumFilters = new Vector<MediumFilter>();
        for (Class<? extends Medium> mediumClass : AbstractMediaOption.getMedia()) 
            if (liste.getTotalPreferences().getMediaOption().isWanted(mediumClass))
                mediumFilters.add(new MediumFilter(mediumClass));
        filterListeLevel0.setFilter(OrFilter.ors(mediumFilters));
    }
    
    public void setLevel0FilterEnabled(boolean enabled) {
        level0FilterEnabled = enabled;
        if (enabled)
            makeLevel0Filter();
        else
            filterListeLevel0.setFilter(TrueFilter.TRUE);
    }
    
    public void setFilter(Filter filter) {
        filterListeLevel1.setFilter(filter);
    }
   
    public void setSearch(Filter filter) {
        filterListeLevel2.setFilter(filter);
    }
   
    public Listing getListing() {
        return liste;
    }
    
/**
 * Funktions to build the gui
 *
 */

	protected void fileNew() {
        // Make sure the changes are secured before creating a new catalog
        if (!secureChanges())
            return;

        Catalog catalog = NewWizard.showNewWizard(display.getWindow());

        // If the catalog is null then we don't need
        // to proceed any further because this means
        // the user canceled the new operation
        if (catalog == null) 
            return;

        // Unset current selection, prevents errors later
        list.setSelectedIndices(new int[]{});
        
//        // Clear changelog
//        changeLog.clear();

        /*liste.*/setCatalog(catalog);

//        // There has been no change from the user up to this point
//        catalog.setUnchanged();

        if (catalog.getConnection() != null)
            Options.recent_connections.add(catalog.getConnection());

//        // Changing the catalog has the ability to change the menu
//        // therefore we have to recreate the Menu
//        makeLevel0Filter();
//        InitM();
//        updateToolbar();
//        catalogStats();
    }
    
    // TODO make one with functionality for Non-standart catalogs
    protected void fileOpen() {
        // Make sure the changes are secured before opening another catalog
        if (!secureChanges())
            return;
        Connection connection = Options.stdFactory.getOpenCatalogConnection(display.getWindow());
        if (connection != null) {
            // Unset current selection, prevents errors later
            list.setSelectedIndices(new int[]{});

            Catalog catalog = Options.stdFactory.openCatalog(connection);
            if (catalog == null) {
                JOptionPane.showMessageDialog(display.getWindow(), menuResources.getString("The catalog [CATALOG] could not be opened.").replaceAll("\\[CATALOG\\]", connection.toString()));
                return;
            }
            
//            // Clear changelog
//            changeLog.clear();

            /*liste.*/setCatalog(catalog);

//            // There has been no change from the user up to this point
//            catalog.setUnchanged();

            Options.recent_connections.add(connection);
        }
//        // Changing the catalog has the ability to change the menu
//        // therefore we have to recreate the Menu
//        makeLevel0Filter();
//        InitM();
//        updateToolbar();
//        catalogStats();
    }
    
    protected void importCatalog(Class<? extends Import> importCls) {
        try {
            Import importInstance = importCls.newInstance();

            Catalog importCatalog = importInstance.getCatalog(display.getWindow());
            Listing importListing = new Listing(importCatalog);

            MergeProcess mergeProcess = new MergeProcess(liste, importListing, display.getWindow());
            mergeProcess.merge();

            // Changing the catalog has the ability to change the menu
            // therefore we have to recreate the Menu
            InitM();
            updateToolbar();
            catalogStats();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        
//        Connection connection = Options.stdFactory.getOpenCatalogConnection(display.getWindow());
//        if (connection != null) {
//            // Unset current selection, prevents errors later
//            list.setSelectedIndices(new int[]{});
//
//            Catalog importCatalog = Options.stdFactory.openCatalog(connection);
//            Listing importListing = new Listing(importCatalog);
//
//            MergeProcess mergeProcess = new MergeProcess(liste, importListing, display.getWindow());
//            mergeProcess.merge();
//        }
//        // Changing the catalog has the ability to change the menu
//        // therefore we have to recreate the Menu
//        InitM();
//        updateToolbar();
//        catalogStats();
    }
    

    protected void fileSaveButton() {
        if (liste.catalog.canSave())
            saveCatalog();
        else
            saveCatalogAs();
        
        // Changing the catalog has the ability to change the menu
        // therefore we have to recreate the Menu
        InitM();
    }
    
    protected void print() {
        ExportChoice exportChoice = new ExportChoice(showedList, /*statusBar.getStatusPanel(2),*/ Options.getPrintExportProfile());
        exportChoice.setVisible(true);
    }
    
	/**
	 * Entries in the Filemenu
	 */ 
	JMenu makeFileMenu() {
		JMenu fileMenu = new JMenu(menuResources.getString("File"));

		JMenuItem fileMenu_New = new JMenuItem(menuResources.getString("New catalog"));
		fileMenu_New.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
		fileMenu_New.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
                fileNew();
			}});
		JMenuItem fileMenu_Open = new JMenuItem(menuResources.getString("Open catalog") + "...");
		fileMenu_Open.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
		fileMenu_Open.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
                fileOpen();
			}});
        JMenuItem fileMenu_Import = new JMenuItem(menuResources.getString("Import catalog") + "...");
        if (Options.imports.size() == 1) {
            fileMenu_Import.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e) {
                    importCatalog(StandardImport.class);
                }});
        } else {
            fileMenu_Import.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e) {
                    List<NiceClass> ncls = NiceClass.convertList((List)Options.imports, "Title");
                    NiceClass nc = (NiceClass)JOptionPane.showInputDialog(display.getWindow(), menuResources.getString("Choose import method"), "Import", JOptionPane.QUESTION_MESSAGE, null, ncls.toArray(), new NiceClass(StandardImport.class));
                    if (nc != null)
                        importCatalog(nc.getClasstype());
                }});
        }
		
        
        
		JMenu fileMenu_nonStandardCatalogs = new JMenu(menuResources.getString("NonStdCatalog"));
		for (final NiceClass<CatalogFactory> ncfc: Options.catalogFactories) {
			JMenu subMenu = new JMenu(ncfc.toString());
			JMenuItem _fileMenu_New = new JMenuItem(menuResources.getString("New catalog"));
			_fileMenu_New.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					// Make sure the changes are secured before opening another catalog
					if (!secureChanges())
						return;
					CatalogFactory cf;
					try {
						cf = ncfc.getClasstype().newInstance();
					} catch (Exception ex){ ex.printStackTrace(); return;}
                    // Create catalog with the aquired catalog.
					Catalog catalog = cf.createCatalog(display.getWindow());
                    // fill the catalog with the default options.
                    Util.copyCatalog(Options.getSimpleCatalog(), catalog);

                    if (catalog != null) {
                        // Unset current selection, prevents errors later
                        list.setSelectedIndices(new int[]{});

//                        // Clear changelog
//                        changeLog.clear();

						/*liste.*/setCatalog(catalog);

//                        // There has been no change from the user up to this point
//                        catalog.setUnchanged();

                        Options.recent_connections.add(catalog.getConnection());
					}
//					// Changing the catalog has the ability to change the menu
//					// therefore we have to recreate the Menu
//                    makeLevel0Filter();
//					InitM();
//                    updateToolbar();
//					catalogStats();
				}});
			subMenu.add(_fileMenu_New);
			JMenuItem _fileMenu_Open = new JMenuItem(menuResources.getString("Open catalog") + "...");
			_fileMenu_Open.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
                    // Make sure the changes are secured before opening another catalog
                    if (!secureChanges())
                        return;
                    CatalogFactory cf;
                    try {
                        cf = ncfc.getClasstype().newInstance();
                    } catch (Exception ex){ ex.printStackTrace(); return;}
                    Connection connection = cf.getOpenCatalogConnection(display.getWindow());
                    if (connection != null) {
                        // Unset current selection, prevents errors later
                        list.setSelectedIndices(new int[]{});

                        Catalog catalog = cf.openCatalog(connection);
                        if (catalog == null) {
                            JOptionPane.showMessageDialog(display.getWindow(), menuResources.getString("The catalog [CATALOG] could not be opened.").replaceAll("\\[CATALOG\\]", connection.toString()));
                            return;
                        }

//                        // Clear changelog
//                        changeLog.clear();

                        /*liste.*/setCatalog(catalog);

//                        // There has been no change from the user up to this point
//                        catalog.setUnchanged();

                        Options.recent_connections.add(connection);
                    }
//                    // Changing the catalog has the ability to change the menu
//                    // therefore we have to recreate the Menu
//                    makeLevel0Filter();
//                    InitM();
//                    updateToolbar();
//                    catalogStats();
				}});
			subMenu.add(_fileMenu_Open);
			fileMenu_nonStandardCatalogs.add(subMenu);
		}
		
		JMenu fileMenu_OpenRecent = new JMenu(menuResources.getString("Recent catalog"));
		for (final Connection c : Options.recent_connections) {
			JMenuItem fileMenu_OpenRecent_ = new JMenuItem(c.toString());
			fileMenu_OpenRecent.add(fileMenu_OpenRecent_);
			fileMenu_OpenRecent_.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					// Make sure the changes are secured before opening another catalog
					if (!secureChanges())
						return;
					try {						
                        Catalog catalog = c.getCatalogFactory().openCatalog(c);
                        if (catalog == null) {
                            JOptionPane.showMessageDialog(display.getWindow(), menuResources.getString("The catalog [CATALOG] could not be opened.").replaceAll("\\[CATALOG\\]", c.toString()));
                            Options.recent_connections.remove(c);
                            InitM();
                            updateToolbar();
                            catalogStats();
                            return;
                        }
                        // Unset current selection, prevents errors later
                        list.setSelectedIndices(new int[]{});

//                        // Clear changelog
//                        changeLog.clear();

						/*liste.*/setCatalog(catalog);

//                        // There has been no change from the user up to this point
//                        catalog.setUnchanged();

                        getPositionFromConnection(c);
					} catch (Exception ex){
						ex.printStackTrace();
					}
//					// Changing the catalog has the ability to change the menu
//					// therefore we have to recreate the Menu
//                    makeLevel0Filter();
//					InitM();
//                    updateToolbar();
//					catalogStats();
				}});
		}

		JMenuItem fileMenu_Save = new JMenuItem(menuResources.getString("Save catalog"));
		if (liste == null)
			fileMenu_Save.setEnabled(false);
		else if (liste.catalog == null)
			fileMenu_Save.setEnabled(false);
		else
            fileMenu_Save.setEnabled(changeLog.unsavedChanges());
//			fileMenu_Save.setEnabled(liste.catalog.canSave());
		fileMenu_Save.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
		fileMenu_Save.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				saveCatalog();
				// Changing the catalog has the ability to change the menu
				// therefore we have to recreate the Menu
				InitM();
			}});
		
		JMenuItem fileMenu_SaveAs = new JMenuItem(menuResources.getString("Save catalog as") + "...");
		if (liste == null)
			fileMenu_SaveAs.setEnabled(false);
		else if (liste.catalog == null)
			fileMenu_SaveAs.setEnabled(false);
		else
			fileMenu_SaveAs.setEnabled(true);
		fileMenu_SaveAs.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				saveCatalogAs();
				// Changing the catalog has the ability to change the menu
				// therefore we have to recreate the Menu
				InitM();
			}});
		
        JMenuItem fileMenu_Print = new JMenuItem(menuResources.getString("Print") + "...");
        fileMenu_Print.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, ActionEvent.CTRL_MASK));
        fileMenu_Print.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                print();
            }});

        JMenuItem fileMenu_Exports = new JMenuItem(menuResources.getString("Export") + "...");
        fileMenu_Exports.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                ExportChoice exportChoice = new ExportChoice(showedList /*,statusBar.getStatusPanel(2)*/);
                exportChoice.setVisible(true);
            }});

		JMenuItem fileMenu_Quit = new JMenuItem(menuResources.getString("Quit"));
		fileMenu_Quit.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) { if (exit()) System.exit(0); }});
		fileMenu_Quit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.CTRL_MASK));

		fileMenu.add(fileMenu_New);	fileMenu.add(fileMenu_Open); fileMenu.add(fileMenu_Import);
// TODO check usefullness of non standard catalogs        
//		fileMenu.add(fileMenu_nonStandardCatalogs);
		fileMenu.add(fileMenu_OpenRecent);
		fileMenu.add(fileMenu_Save); fileMenu.add(fileMenu_SaveAs);	

        fileMenu.addSeparator(); 
        fileMenu.add(fileMenu_Print);
        fileMenu.add(fileMenu_Exports);
		
        fileMenu.addSeparator(); 
        fileMenu.add(fileMenu_Quit);
		
		return fileMenu;
	}

	protected void createMedium(Class<? extends Medium> c) {
        // Close transaction from previous editings.
        closeCurrentTransaction();
        // Open new transaction
        editMediumTransaction = changeLog.openTransaction(menuResources.getString("Create new [TYPE].").replaceAll("\\[TYPE\\]", Options.getI18N(c).getString(c.getSimpleName())), false, true);

        Medium medium = liste.create(c);

        // Set the new created medium as the medium of the transaction
        editMediumTransactionMedium = medium;
        
        list.setSelectedValue(medium, true);
        desktop.requestFocus();
        catalogStats();
        InitM();
    }

    
    protected void doCopy() {
/*        Iterator<? extends Entry> i = copyBuffer.getIterator();
        while (i.hasNext() ) {
            Entry entry = i.next();
            copyBuffer.removeEntry(entry);
        }*/
        copyBuffer = Options.getDefaultCatalog(display.getWindow());
        
        Medium medium = getSelection();
        Entry entry = copyBuffer.createEntry(medium.entry.getTypeClassName());
        Util.copyEntry(medium.entry, entry);
        
        editMenu_Paste.setEnabled(copyBuffer.getIterator().hasNext());
    }
    
    protected void doPaste() {
        // Make the paste action one step only
        closeCurrentTransaction();
        int transId = changeLog.openTransaction(menuResources.getString("Copy entry"), true, true);

        Medium medium = null;
        
        Iterator<? extends Entry> i = copyBuffer.getIterator();
        while (i.hasNext() ) {
            Entry entry = i.next();
            Entry pasteEntry = liste.catalog.createEntry(entry.getTypeClassName());
            Util.copyEntry(entry, pasteEntry);
            Ident.individualizeEntry(pasteEntry);
            medium = liste.getMediumForEntry(pasteEntry);
        }

        // Make the paste action one step only
        changeLog.closeTransaction(transId);
        
        // Set selection to new created medium
        if (medium != null)
            restoreSelection(medium);

        // Update catalog statistics and menu
        catalogStats();
        InitM();
    }
    
	/**
	 * Entries in the Editmenu
	 */ 
	JMenu makeEditMenu() {
		JMenu editMenu = new JMenu(menuResources.getString("Edit"));

		JMenu editMenu_Create = new JMenu(menuResources.getString("Create") + " ");
		for (final Class<? extends Medium> c : Options.media) {
            // Only put those media into the menu that are wanted
		    if (!getCurrentPreferences().getMediaOption().isWanted(c)) 
		        continue;
		    
			JMenuItem editMenu_Create_ = new JMenuItem(Options.getI18N(c).getString(c.getSimpleName()));
			editMenu_Create.add(editMenu_Create_);
			editMenu_Create_.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
				    createMedium(c);
				}});
		}
        editMenu_Create.setEnabled(lockButton.isSelected());
		editMenu.add(editMenu_Create);
		
		JMenuItem editMenu_Remove = new JMenuItem(menuResources.getString("Remove"));
		editMenu_Remove.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
		editMenu_Remove.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
                int index = list.getSelectedIndex();
				liste.remove(getSelection());
                if (index >= showedList.getSize())
                    index = showedList.getSize() - 1;
                list.setSelectedIndices(new int[]{});
                list.setSelectedIndex(index);
				catalogStats();
                InitM();
			}});
        editMenu_Remove.setEnabled(lockButton.isSelected());
		editMenu.add(editMenu_Remove);
		
        editMenu_Paste = new JMenuItem(menuResources.getString("Paste"));
        editMenu_Paste.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.CTRL_MASK));
        editMenu_Paste.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                doPaste();
            }});
        // Only allow this operation if there really is something to paste
        editMenu_Paste.setEnabled(copyBuffer.getIterator().hasNext());

        editMenu_Copy = new JMenuItem(menuResources.getString("Copy"));
		editMenu_Copy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));
		editMenu_Copy.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
                doCopy();
			}});

        editMenu_Copy.setEnabled(lockButton.isSelected());
        editMenu.add(editMenu_Copy);
        editMenu_Paste.setEnabled(editMenu_Paste.isEnabled() && lockButton.isSelected());
        editMenu.add(editMenu_Paste);
		
        
        editMenu.addSeparator();

        JMenuItem editMenu_Search = new JMenuItem(menuResources.getString("Search..."));
        editMenu_Search.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, ActionEvent.CTRL_MASK));
        editMenu_Search.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                createSearch();
            }});
        editMenu.add(editMenu_Search);

        editMenu.addSeparator();

        editMenu_Undo = new JMenuItem(menuResources.getString("Undo"));
        editMenu_Undo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, ActionEvent.CTRL_MASK));
        editMenu_Undo.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                undo();
            }});
        editMenu.add(editMenu_Undo);

        editMenu.addSeparator();

		JMenuItem editMenu_Next = new JMenuItem(menuResources.getString("Next"));
		editMenu_Next.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, ActionEvent.CTRL_MASK));
		editMenu_Next.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
                if (list.getSelectedIndex() < showedList.getSize() - 1)
                    list.setSelectedIndex(list.getSelectedIndex() + 1);
			}});
		editMenu.add(editMenu_Next);

		JMenuItem editMenu_Prev = new JMenuItem(menuResources.getString("Previous"));
		editMenu_Prev.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_UP, ActionEvent.CTRL_MASK));
		editMenu_Prev.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
                if (list.getSelectedIndex() > 0)
                    list.setSelectedIndex(list.getSelectedIndex() - 1);
			}});
		editMenu.add(editMenu_Prev);
		

        return editMenu;
	}

    protected void undo() {
        list.setSelectedIndices(new int[]{});

        closeCurrentTransaction();

        Medium medium = getSelection();
        
        changeLog.undo(1);
        
        desktop.requestFocus();
        catalogStats();
    }

    public void showOptions(Object selection) {
        GlobalPreferences pref = Util.copyPTE(Options.AppPrefs);

        OptionDialog optionDialog = new OptionDialog(pref);
        optionDialog.setSelection(selection);
        optionDialog.setVisible(true);
        
        if (optionDialog.isAccepted()) {
            Options.AppPrefs = pref;
            makeLevel0Filter();
            // Catalog stats are influenced by the result from makeLevel0Filter
            catalogStats();
            InitM();
            updateToolbar();
        }
    }
    public void showCatalogOptions(Object selection) {
        CatalogPreferences pref = Util.copyPTE(liste.getCatalogPreferences());

        OptionDialog optionDialog = new OptionDialog(pref);
        optionDialog.setSelection(selection);
        optionDialog.setVisible(true);
        
        if (optionDialog.isAccepted()) {
            liste.setPreferences(pref);
            makeLevel0Filter();
            // Catalog stats are influenced by the result from makeLevel0Filter
            catalogStats();
            InitM();
            updateToolbar();
        }
    }
    
    
	/**
	 * Entries in the Optionsmenu
	 */ 
	JMenu makeOptionMenu() {
		JMenu optionMenu = new JMenu(menuResources.getString("Option"));

        JMenuItem optionMenu_Preferences = new JMenuItem(menuResources.getString("Preferences..."));
        optionMenu_Preferences.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                showOptions(null);
            }});
        optionMenu.add(optionMenu_Preferences);

        optionMenu.addSeparator();
        
        JMenu optionMenu_Feature = new JMenu(menuResources.getString("Feature"));
		for (final NiceClass<Feature> c : Options.features) {
		    if (liste.getTotalPreferences().getFeaturesOption().getOption(c.getClasstype()) != null) {
		        JMenuItem optionMenu_Feature_ = new JMenuItem(c.toString()){{
		            addActionListener(new ActionListener(){
		                public void actionPerformed(ActionEvent e) {
		                    showOptions(c);
		                }});
		        }};
		        optionMenu_Feature.add(optionMenu_Feature_);
		    }
		}
		optionMenu.add(optionMenu_Feature);

		JMenuItem optionMenu_SelectLanguage = new JMenuItem(menuResources.getString("Select Language"));
		optionMenu_SelectLanguage.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				LanguageSelection panel = new LanguageSelection();
				JOptionPane.showConfirmDialog(display.getWindow(), panel, menuResources.getString("Select Language"),  JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
				// If a language has been choosen
				// set the global language to new choosen one
				if (panel.getLocale() != null)
					Options.setCurrentLocale(panel.getLocale());
                // Remember the current selected medium
                Medium selection = getSelection();
                // Changing the language causes some trouble
				// We have to recreate the Menu
				InitM();
				// and to refresh all Things related to the orignal list
				// because the Title Feature depends on the language as well
				liste.refresh();
				// and refresh the shown features for the current medium
				// therefor we have to change the value to another value and back
				// another posibility is to extend JList and hack into the 
				// fireSelectionValueChanged protected method
				for (ListSelectionListener l : list.getListSelectionListeners())
					l.valueChanged(new ListSelectionEvent(list, list.getMinSelectionIndex(), list.getMaxSelectionIndex(), false));
                // Set the title of the Mainframe to the new language
                updateTitle();
                // Set the current selected medium to the medium selected before language change
                restoreSelection(selection);
                
                catalogStats();
			}});
		optionMenu.add(optionMenu_SelectLanguage);
		
		return optionMenu;
	}

    /**
     * Entries in the Optionsmenu
     */ 
    JMenu makeCatalogMenu() {
        JMenu catalogMenu = new JMenu(Options.getI18N(Catalog.class).getString("Catalog"));

        JMenuItem catalogMenu_Preferences = new JMenuItem(menuResources.getString("Preferences..."));
        catalogMenu_Preferences.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                showCatalogOptions(null);
            }});
        catalogMenu.add(catalogMenu_Preferences);

        catalogMenu.addSeparator();
        
        JMenu catalogMenu_Feature = new JMenu(menuResources.getString("Feature"));
        for (final NiceClass<Feature> c : Options.features) {
            if (liste.getTotalPreferences().getFeaturesOption().getOption(c.getClasstype()) != null) {
                JMenuItem catalogMenu_Feature_ = new JMenuItem(c.toString()){{
                    addActionListener(new ActionListener(){
                        public void actionPerformed(ActionEvent e) {
                            showCatalogOptions(c);
                        }});
                }};
                catalogMenu_Feature.add(catalogMenu_Feature_);
            }
        }
        catalogMenu.add(catalogMenu_Feature);

        catalogMenu.addSeparator();
        JMenuItem catalogMenu_GeneralInformation = new JMenuItem(menuResources.getString("General information..."));
        catalogMenu_GeneralInformation.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                showGeneralInformation();
            }});
        catalogMenu.add(catalogMenu_GeneralInformation);
        
        return catalogMenu;
    }

    protected void showGeneralInformation(){
        CatalogDetails.showCatalogDetails(display.getWindow(), liste);
    }
    
    protected Medium getSelection() {
        try {
            return (Medium) list.getSelectedValue();
        } catch (Exception e) {
            if (Options.DEBUG)
                e.printStackTrace();
        }
        return null;
    }
    
    protected void restoreSelection(Object selection) {
        if (selection != null && showedList.contains(selection)) 
            list.setSelectedValue(selection, true);
        else {
//            System.out.println("Set index to -1");
            list.setSelectedIndices(new int[]{});
//            System.out.println(list.getSelectedIndex());
        }
    }
    
    protected void createSorting() {
/*        FeatureSortOptions panel = new FeatureSortOptions(liste);
        if (JOptionPane.showConfirmDialog(display.getWindow(), panel, menuResources.getString("Create Sorting"),  JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION)
            setComparator(panel.getSorting());*/
        ConfigurableComparator comparator = CreateSortDialog.showCreateSortDialog(display.getWindow(), liste);
        if (comparator != null)
            setComparator(comparator);
        catalogStats();
   }
    
    protected void setComparator(ConfigurableComparator comparator) {
        sortedListe.setComparator(comparator);
    }
    
    protected void createSearch() {
//        boolean caseSensitive = false;
//     
//        JRadioButton level0 = new JRadioButton(menuResources.getString("Search everything, hidden types included."));
//        level0.setToolTipText(menuResources.getString("Finds the most entries, but may result in to many entries."));
//        JRadioButton level1 = new JRadioButton(menuResources.getString("Search all visible media including those that are hidden by the current filter."));
//        level1.setToolTipText(menuResources.getString("If you don't know what to chose you want this."));
//        JRadioButton level2 = new JRadioButton(menuResources.getString("Search in the shown selection."));
//        level2.setToolTipText(menuResources.getString("If you want to keep the filter active for the search."));
//        JTextField txtField  = new JTextField();
//        txtField.setToolTipText(menuResources.getString("Place your search string here."));
//
//        level1.setSelected(true);
//        ButtonGroup group = new ButtonGroup();
//        group.add(level0);
//        group.add(level1);
//        group.add(level2);
//        JPanel buttonPanel = new JPanel();
//        buttonPanel.setLayout(new GridLayout(0,1));
//        buttonPanel.add(level0);
//        buttonPanel.add(level1);
//        buttonPanel.add(level2);
//        JPanel all = new JPanel();
//        all.setLayout(new BorderLayout());
//        all.add(buttonPanel, BorderLayout.NORTH);
//        all.add(txtField);
//        txtField.requestFocusInWindow();
//
//        if (JOptionPane.showConfirmDialog(display.getWindow(), all, menuResources.getString("Create search"), JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE)
//                != JOptionPane.OK_OPTION)
//            return;
//        
//        if (txtField.getText() == null)
//            return;
//        
//        if (txtField.getText().length() == 0)
//            return;
        SearchParameters parameters = SearchDialog.showSearchDialog(display.getWindow());
        if (parameters == null)
            return;
        
        Vector<FeatureFilter> filters = new Vector<FeatureFilter>();
        for (Class<? extends Feature> feature : AbstractMediaOption.getFeatures())
            if (DefaultPreferences.defaultPreferences.getFeaturesOption().hasValidator(feature)) {
                FeatureValidator validator = DefaultPreferences.defaultPreferences.getFeaturesOption().getValidator(feature);

                if (validator instanceof TextFeatureValidator) {
                    TextFeatureValidator txt = (TextFeatureValidator) validator;
                    String regExp = TextFeatureValidator.getRegularExpressionFor(parameters.getSearchString(), parameters.isCaseSensitive());

                    try {
                        filters.add(new FeatureFilter(feature, regExp));
                    } catch (BadCondition e) {
                        e.printStackTrace();
                    }
                }
                if (validator instanceof ChoiceFeatureValidator) {
                    FeatureOption featureOption = liste.getTotalPreferences().getFeaturesOption().getOption(feature);
                    if (featureOption instanceof ChoiceFeatureOption) {
                        // Get Resources for the i18n of the Choices
                        CatalogResource choiceResources = ((ChoiceFeatureOption)featureOption).getCatalogResource().getBundle(Options.getCurrentLocale());

                        // Make filter for every choice that matches the condition
                        for (String key : choiceResources.keys) {

                            // If the key matches not the condition 
                            // try the next key
                            if (parameters.isCaseSensitive()) {
                                if (!choiceResources.getString(key).contains(parameters.getSearchString()))
                                    continue;
                            } else {
                                if (!choiceResources.getString(key).toUpperCase().contains(parameters.getSearchString().toUpperCase()))
                                    continue;
                            }

                            // Add the key to the list of searched Feature Filter
                            try {
                                filters.add(new FeatureFilter(feature, key));
                            } catch (BadCondition e) {
                                e.printStackTrace();
                            }
                        }



                    }
                }
            }

        switch (parameters.getSearchLevel()) {
        case 0:
            ShowCatalogFrontend.showShowCatalogFrontend(display.getWindow(), liste.catalog, null, OrFilter.ors(filters), sortedListe.getComparator(), false);
            break;
        case 1:
            ShowCatalogFrontend.showShowCatalogFrontend(display.getWindow(), liste.catalog, OrFilter.ors(filters), sortedListe.getComparator());
            break;
        case 2:
            ShowCatalogFrontend.showShowCatalogFrontend(display.getWindow(), liste.catalog, filterListeLevel1.getFilter(), OrFilter.ors(filters), sortedListe.getComparator());
            break;
        }
//        if (level0.isSelected())
//            ShowCatalogFrontend.showShowCatalogFrontend(display.getWindow(), liste.catalog, null, OrFilter.ors(filters), sortedListe.getComparator(), false);
//        else if (level1.isSelected())
//            ShowCatalogFrontend.showShowCatalogFrontend(display.getWindow(), liste.catalog, OrFilter.ors(filters), sortedListe.getComparator());
//        else
//            ShowCatalogFrontend.showShowCatalogFrontend(display.getWindow(), liste.catalog, filterListeLevel1.getFilter(), OrFilter.ors(filters), sortedListe.getComparator());
            
        
        
//        this.setFilter(OrFilter.ors(filters));
//        catalogStats();
    }

    public void noSorting() {
        Medium selection = getSelection();
        sortedListe.setComparator(null);
        restoreSelection(selection);
        catalogStats();
    }
    
	/**
	 * Entries in the SortingMenu
	 */
	JMenu makeSortMenu() {
		JMenu sortMenu = new JMenu(menuResources.getString("Sorting"));

		JMenuItem sortMenu_Manager = new JMenuItem(menuResources.getString("Sorting Manager"));
        sortMenu_Manager.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                Medium selection = getSelection();
                ConfigurableComparator comparator = SortManager.showSortManager(display.getWindow(), sortedListe.getComparator(), liste);
                setComparator(comparator);
                restoreSelection(selection);
                catalogStats();
            }});
		JMenuItem sortMenu_Create = new JMenuItem(menuResources.getString("Create Sorting"));
		sortMenu_Create.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
                Medium selection = getSelection();
                createSorting();
                restoreSelection(selection);
                catalogStats();
			}});
		JMenuItem NoSort = new JMenuItem(menuResources.getString("No sorting"));
		NoSort.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
                noSorting();
			}});

        JMenu sortMenu_Predefined = new JMenu(menuResources.getString("Predefined Sortings"));
        for (final Comparing c : Options.predefComparing) {
			JMenuItem tmp = new JMenuItem(c.toString());
			sortMenu_Predefined.add(tmp);
			tmp.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
                    Medium selection = getSelection();
					sortedListe.setComparator(c);
                    restoreSelection(selection);
                    catalogStats();
				}});
		}	
		
		sortMenu.add(sortMenu_Manager);
		sortMenu.add(sortMenu_Create);
        sortMenu.add(NoSort);
		sortMenu.addSeparator();
		sortMenu.add(sortMenu_Predefined);
		
		return sortMenu;
	}

    protected void openFilterManager(){
        Medium selection = getSelection();
        Filter newFilter = FilterDesigner.getFilter(filterListeLevel1.getFilter(), liste.getTotalPreferences());
        if (newFilter != null)
            filterListeLevel1.setFilter(newFilter);
        restoreSelection(selection);
        catalogStats();
    }
    
    protected void noFilter() {
        Medium selection = getSelection();
        filterListeLevel1.setFilter((Filter)null);
        restoreSelection(selection);
        catalogStats();
    }
    
	/**
	 * Entries in the FilterMenu
	 */
	JMenu makeFilterMenu(){
		JMenu filterMenu = new JMenu(menuResources.getString("Filter"));

		JMenuItem filterMenu_Manager = new JMenuItem(menuResources.getString("Filtering Manager"));
		filterMenu_Manager.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
                openFilterManager();
                catalogStats();
			}});
		JMenuItem filterMenu_Select = new JMenuItem(menuResources.getString("Select Filtering"));
		JMenuItem filterMenu_NoFilter = new JMenuItem(menuResources.getString("No filtering"));
        filterMenu_NoFilter.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
                noFilter();
			}});
        JMenu filterMenu_Predefined = new JMenu(menuResources.getString("Predefined Filterings"));
		JMenu filteMenu_Predefined_Only = new JMenu(menuResources.getString("Only"));
		filterMenu_Predefined.add(filteMenu_Predefined_Only);
		for (final Class<? extends Medium> c : Options.media) {
            // Only put those media into the menu that are wanted
            if (!getCurrentPreferences().getMediaOption().isWanted(c)) 
                continue;
            
			JMenuItem filteMenu_Predefined_Only_ = new JMenuItem(Options.getI18N(c).getString(c.getSimpleName()));
			filteMenu_Predefined_Only.add(filteMenu_Predefined_Only_);
			filteMenu_Predefined_Only_.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					try {
                        Medium selection = getSelection();
						filterListeLevel1.setFilter(new MediumFilter(c));
                        restoreSelection(selection);
                        catalogStats();
					} catch (Exception ex){
						ex.printStackTrace();
					}
				}});
		}

		
		filterMenu_Predefined.addSeparator();
		JMenu filteMenu_Predefined_Language = new JMenu(menuResources.getString("Language"));
		filterMenu_Predefined.add(filteMenu_Predefined_Language);
		for (final Locale l : Options.languages) {
			JMenuItemValued<Locale> filteMenu_Predefined_Language_ = new JMenuItemValued<Locale>(l, l.getDisplayName(Options.getCurrentLocale()));
			filteMenu_Predefined_Language.add(filteMenu_Predefined_Language_);
			filteMenu_Predefined_Language_.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					try {
                        Medium selection = getSelection();
                        filterListeLevel1.setFilter(new LanguageFilter(l));
                        restoreSelection(selection);
                        catalogStats();
					} catch (Exception ex){
						ex.printStackTrace();
					}
				}});
		}

		filterMenu.add(filterMenu_Manager);
		filterMenu.add(filterMenu_Select);
        filterMenu.add(filterMenu_NoFilter);
		filterMenu.addSeparator();
		filterMenu.add(filterMenu_Predefined);

		return filterMenu;
	}
	
	
	/**
	 * Entries in the FilterMenu
	 */
	JMenu makeExtraMenu(){

		JMenu extraMenu = new JMenu(menuResources.getString("Extras"));

		JMenuItem imageDisplayItem = new JMenuItem(menuResources.getString("Image display"));
		imageDisplayItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
                ImageDesktopFrame frame = new ImageDesktopFrame(liste);
                frame.pack();
                frame.setVisible(true);
			}});
        
        JMenuItem showChangeLogItem = new JMenuItem(menuResources.getString("Show change log"));
        showChangeLogItem.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                closeCurrentTransaction();
                
                boolean undoExecuted = UndoSelection.showUndoSelection(display.getWindow(), changeLog);
                if (undoExecuted)
                    list.setSelectedIndices(new int[]{});
            }
        });

		extraMenu.add(imageDisplayItem);
        extraMenu.add(showChangeLogItem);

		return extraMenu;
	}

    
    protected JMenu makeCustomFeatureMenu(){
        return CustomMenuRegister.getFeatureMenu(liste);
    }
    
    protected JMenu makeHelpMenu() {
        JMenu helpMenu = new JMenu(menuResources.getString("Help"));
        
        JMenuItem helpMenuAbout = new JMenuItem(menuResources.getString("About MeCat"));
        helpMenuAbout.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent arg0) {
                AboutFrame aboutFrame = new AboutFrame();
                aboutFrame.setVisible(true);
            }
        });
        helpMenu.add(helpMenuAbout);
        
        return helpMenu;
    }
    
    protected JMenu makeTestMenu() {
        JMenu testMenu = new JMenu(menuResources.getString("Test"));

        try {
            final Class AutoFillListing = Class.forName("net.sourceforge.mecat.catalog.test.AutoFillListing");

            JMenuItem testMenuFillListing = new JMenuItem(menuResources.getString("Auto fill listing"));
            testMenuFillListing.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent arg0) {
                    try {
                        Object autoFillListing = AutoFillListing.getConstructor(new Class[]{Listing.class}).newInstance(new Object[]{ getListing() });
                        Method autoFill = AutoFillListing.getDeclaredMethod("autoFill", new Class[]{});
                        autoFill.invoke(autoFillListing, new Object[]{});
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            testMenu.add(testMenuFillListing);
        } catch (ClassNotFoundException e) {
            // This means the testenvironment is not sane
            return null;
        }

        return testMenu;
    }
    
	/**
	 * This function builds the MenuBar.
	 */
	void InitM() {
		titleMenu = new JMenuBar();

		JMenu fileMenu    = makeFileMenu();
		JMenu editMenu    = makeEditMenu();
		JMenu optionMenu  = makeOptionMenu();
        JMenu catalogMenu = makeCatalogMenu();
		JMenu sortMenu    = makeSortMenu();
		JMenu filterMenu  = makeFilterMenu();
		JMenu extraMenu   = makeExtraMenu();
        JMenu customFeatureMenu = makeCustomFeatureMenu();
        JMenu helpMenu    = makeHelpMenu();
        JMenu testMenu    = makeTestMenu();
        
		titleMenu.add(fileMenu);
		titleMenu.add(editMenu);
		titleMenu.add(optionMenu);
        if (Options.AppPrefs.getGeneralOption().isUseCatalogOption())
            titleMenu.add(catalogMenu);
		titleMenu.add(sortMenu);
		titleMenu.add(filterMenu);
		titleMenu.add(extraMenu);
        titleMenu.add(customFeatureMenu);
        titleMenu.add(helpMenu);
        if (testMenu != null)
            titleMenu.add(testMenu);
		
        display.setJMenuBar(titleMenu);
        display.getJMenuBar().updateUI();
	}
	



	JButton saveButton = null;
    Vector<JButton> toolbarButtonsFile = new Vector<JButton>();
    HashMap<Class<? extends Medium>, JButton> toolbarButtonsMedia = new HashMap<Class<? extends Medium>, JButton>();
    Vector<JButton> toolbarButtonsFilterSortLock = new Vector<JButton>();
    
    protected Preferences getCurrentPreferences() {
        if (liste != null)
            return liste.getTotalPreferences();
        return Options.AppPrefs;
    }
    
    void updateToolbar() {
        
        JToolBar tmp = new JToolBar();
        tmp.removeAll();
        
        for (JButton button : toolbarButtonsFile)
            tmp.add(button);
        
        for (final Class<? extends Medium> medium : Options.media) {
            // Only put those media into the menu that are wanted
            if (toolbarButtonsMedia.containsKey(medium) && getCurrentPreferences().getMediaOption().isWanted(medium)) {
                JButton button = toolbarButtonsMedia.get(medium);
                button.setEnabled(lockButton.isSelected());
                tmp.add(button);
            }
        }
        
        for (JButton button : toolbarButtonsFilterSortLock)
            tmp.add(button);
        
        if (changeLog == null)
            saveButton.setEnabled(false);
        else if (saveButton != null)
            saveButton.setEnabled(changeLog.unsavedChanges());
        
        contentPane.remove(toolbar);
//        display.getContentPane().remove(toolbar);
        toolbar = tmp;
        contentPane.add(toolbar, BorderLayout.PAGE_START);
//        display.getContentPane().add(toolbar, BorderLayout.PAGE_START);
    }
    
    protected void setPreferredDesktopWidth() {
        if (desktop != null) 
            desktop.setPreferredDesktopWidth(splitListAndFeatures.getRightComponent().getWidth());
    }
    
    void makeToolBar() {
        JButton button = new SimpleLocalButton(menuResources, null, "New catalog");
        ToolBarUtils.addImages(button, MainFrameBackend.class, "new", button.getToolTipText());
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                fileNew();
            }
        });
        toolbar.add(button);
        toolbarButtonsFile.add(button);

        Options.time("MainFrameBackend: Toolbar new button");

        button = new SimpleLocalButton(menuResources, null, "Open catalog");
        ToolBarUtils.addImages(button, MainFrameBackend.class, "open", button.getToolTipText());
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                fileOpen();
            }
        });
        toolbar.add(button);
        toolbarButtonsFile.add(button);

        Options.time("MainFrameBackend: Toolbar open button");

        saveButton = new SimpleLocalButton(menuResources, null, "Save catalog");
        ToolBarUtils.addImages(saveButton, MainFrameBackend.class, "save", saveButton.getToolTipText());
        saveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                fileSaveButton();
            }
        });
        toolbar.add(saveButton);
        toolbarButtonsFile.add(saveButton);

        Options.time("MainFrameBackend: Toolbar save button");

        button = new SimpleLocalButton(menuResources, null, "Print");
        ToolBarUtils.addImages(button, MainFrameBackend.class, "print", button.getToolTipText());
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                print();
            }
        });
        toolbar.add(button);
        toolbarButtonsFile.add(button);

        Options.time("MainFrameBackend: Toolbar print button");

        // Ordinary separators don't look god on certain os (debian, ...)
        button = ToolBarUtils.makeBlankButton();
        toolbar.add(button);
        toolbarButtonsFile.add(button);

        for (final Class<? extends Medium> medium : Options.media) {
            String easyName = medium.getSimpleName();
            if (ToolBarUtils.exists(medium, easyName)) {
//                String name = Options.getI18N(medium).getString(easyName);

                JButton _button = new SimpleLocalButton(Options.getI18N(medium), null, easyName);
                ToolBarUtils.addImages(_button, medium, easyName, button.getToolTipText());
                _button.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent arg0) {
                        createMedium(medium);
                    }
                });

                toolbarButtonsMedia.put(medium, _button);
                toolbar.add(_button);

                Options.time("MainFrameBackend: Toolbar " + Options.getI18N(medium).getString(medium.getSimpleName())+ " button");
            }
        }
        // Ordinary separators don't look god on certain os (debian, ...)
        button = ToolBarUtils.makeBlankButton();
        toolbar.add(button);
        toolbarButtonsFilterSortLock.add(button);

        button = new SimpleLocalButton(Options.getI18N(SearchDialog.class), null, "Search");
        ToolBarUtils.addImages(button, ToolBarUtils.class, "search", button.getToolTipText());
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                createSearch();
            }
        });
        toolbar.add(button);
        toolbarButtonsFilterSortLock.add(button);

        Options.time("MainFrameBackend: Toolbar search button");

        button = new SimpleLocalButton(menuResources, null, "Filter");
        ToolBarUtils.addImages(button, ToolBarUtils.class, "funel", button.getToolTipText());
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                openFilterManager();
            }
        });
        toolbar.add(button);
        toolbarButtonsFilterSortLock.add(button);

        Options.time("MainFrameBackend: Toolbar filter button");

        button = new SimpleLocalButton(menuResources, null, "No filtering");
        ToolBarUtils.addImages(button, ToolBarUtils.class, "funelNo", button.getToolTipText());
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                noFilter();
            }
        });
        toolbar.add(button);
        toolbarButtonsFilterSortLock.add(button);

        Options.time("MainFrameBackend: Toolbar no filter button");

        button = new SimpleLocalButton(menuResources, null, "Sorting");
        ToolBarUtils.addImages(button, ToolBarUtils.class, "number", button.getToolTipText());
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                createSorting();
            }
        });
        toolbar.add(button);
        toolbarButtonsFilterSortLock.add(button);

        Options.time("MainFrameBackend: Toolbar sorting button");

        button = new SimpleLocalButton(menuResources, null, "No sorting");
        ToolBarUtils.addImages(button, ToolBarUtils.class, "numberNo", button.getToolTipText());
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                noSorting();
            }
        });
        toolbar.add(button);
        toolbarButtonsFilterSortLock.add(button);

        Options.time("MainFrameBackend: Toolbar no sorting button");

        // Ordinary separators don't look god on certain os (debian, ...)
        button = ToolBarUtils.makeBlankButton();
        toolbar.add(button);
        toolbarButtonsFilterSortLock.add(button);

        lockButton = ToolBarUtils.makeAnimatedToggleButton(ToolBarUtils.class, 10, 23, "padlock", "", menuResources, "Switch between edit and view mode", null);
        lockButton.addActionListener(new ActionListener(){ 
            public void actionPerformed(ActionEvent arg0) {
                featurePanel.removeAll();
                if (lockButton.isSelected())
                    desktop = new HardCodedDesktop();
                else
                    desktop = new ViewDesktop();
                
                featurePanel.add(desktop.getDesktop());
                // Set the available size
                setPreferredDesktopWidth();

                checkEditTransaction("Edit");
                
                desktop.setMedium(selectedMedium);
                
                // redraw the feature panel
                featurePanel.updateUI();
                
                InitM();
                updateToolbar();
            }});
        toolbar.add(lockButton);
        toolbarButtonsFilterSortLock.add(lockButton);

        Options.time("MainFrameBackend: Toolbar padlock button");
        
        
        contentPane.add(toolbar, BorderLayout.PAGE_START);
    }
    
    // Close transaction from previous editings.
    protected void closeCurrentTransaction() {
        if (editMediumTransaction != -1) {
            changeLog.closeTransaction(editMediumTransaction); 
            editMediumTransaction = -1;
            editMediumTransactionMedium = null;
        }
    }
    
    protected void checkEditTransaction(String actionName) {
        if (editMediumTransactionMedium != null 
                && selectedMedium != null
                && editMediumTransactionMedium.equals(selectedMedium) 
                && desktop instanceof HardCodedDesktop) {
            // We don't need to end one transaction to open
            // another with the same medium
            return;
        }
            
        // Invoke selection changed
        // Close transaction from previous editings.
        closeCurrentTransaction();
        
        // Add new transaction if in edit mode
        if (selectedMedium != null && desktop instanceof HardCodedDesktop) {
            editMediumTransactionMedium = selectedMedium;
            editMediumTransaction = changeLog.openTransaction(actionName + ": " + editMediumTransactionMedium.toString(), false, true);
        }
    }
    
	private void jbInit() throws Exception  {
//        setIconImage(ToolBarUtils.loadImage(Splasher.class, "Logo_32.png", "Logo").getImage());
        
        Options.time("MainFrameBackend: start");

        splitListAndFeatures.addPropertyChangeListener(new PropertyChangeListener(){
            public void propertyChange(PropertyChangeEvent arg0) {
                setPreferredDesktopWidth();
            }
        });
			
		contentPane = new JPanel(); //(JPanel) this.getContentPane();
		contentPane.setLayout(borderLayout1);

        makeToolBar();
        Options.time("MainFrameBackend: Toolbar finish");

        display.getWindow().setSize(new Dimension(400, 300));
        updateTitle();
        Options.time("MainFrameBackend: Title");

		list = new JList();
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setModel(showedList);
        Options.time("MainFrameBackend: Listing");

//		sortedListe.setComparator(Year.getComparator());

        featurePanel.setLayout(new BorderLayout());
        featurePanel.add(desktop.getDesktop());
        Options.time("MainFrameBackend: Desktop");
        
        
        sortedListe.addSortedListingListener(new SortedListingListener(){
            public void contentsChanged(SortedListing listing, int index0, int index1) {
                // nothing to do
            }

            public void mediumMoved(SortedListing listing, int prevPosition, int newPosition) {
                int index = list.getSelectedIndex();
                // Case 1 : the medium that is selected moved
                // result : move the selection to the new position of the medium
                if (index == prevPosition) {
                    list.setSelectedIndex(newPosition);
                    return;
                }
                // Case 2 : the medium jumped from a position before the selection to a position after the selection
                // result : the medium selected moves one to the beginning of the list
                if ((prevPosition < index) && (newPosition > index)) {
                    list.setSelectedIndex(index - 1);
                    return;
                }
                // Case 2 : the medium jumped from a position after the selection to a position before the selection
                // result : the medium selected moves one to the end of the list
                if ((newPosition < index) && (prevPosition > index)) {
                    list.setSelectedIndex(index + 1);
                    return;
                }
            }

            public void mediaRemoved(SortedListing listing, List<Integer> positions) {
                // nothing to do
            }

            public void mediaAdded(SortedListing listing, List<Integer> positions) {
                // nothing to do
            }
        });
        
        
        list.getInputMap().put(KeyStroke.getKeyStroke("ctrl C"), new ActionListener(){
            public void actionPerformed(ActionEvent arg0) {
                doCopy();
            }
        });
        
        list.getInputMap().put(KeyStroke.getKeyStroke("ctrl V"), new ActionListener(){
            public void actionPerformed(ActionEvent arg0) {
                doPaste();
            }
        });

        list.addListSelectionListener(new ListSelectionListener(){
			public void valueChanged(ListSelectionEvent arg0) {
                Medium medium = getSelection();
                if (medium == selectedMedium)
                    return;
                selectedMedium = medium;
                
                checkEditTransaction("Edit");

                desktop.setMedium(medium);
				
				// If there is a connection to the actual catalog, we're going to store the position into it.
				storePositionIntoConnection();
			}
		});
        
        list.setCellRenderer(new MediaListCellRenderer());

		contentPane.add(splitListAndFeatures, BorderLayout.CENTER);
		splitListAndFeatures.add(new JScrollPane(list), JSplitPane.LEFT);
		splitListAndFeatures.add(featurePanel, JSplitPane.RIGHT);

        setStatusbarEnabled(true);
        
		display.setContentPane(contentPane);

        Options.time("MainFrameBackend: build GUI");
        
		reOpen();
        Options.time("MainFrameBackend: Reopen catalog");

        updateToolbar();
		InitM();
        // make the status bar
        buildStatusBar();
		catalogStats();
        Options.time("MainFrameBackend: GUI initialised");
	}

    public void buildStatusBar() {
        numberOfEntries = new LocalLabel(){
            
            @Override
            protected String getLocalText() {
                return "" + liste.size();
            }

            @Override
            protected String getToolTip() {
                // Does get a different Tooltip for every language
                return menuResources.getString("Number of entries");
            }
            
        };
        filteredLevel0Entries = new LocalLabel(){
            
            @Override
            protected String getLocalText() {
                return "" + filterListeLevel0.size();
            }

            @Override
            protected String getToolTip() {
                // Does get a different Tooltip for every language
                return menuResources.getString("Number of entries with visible type.");
            }
            
        };
        filteredLevel1Entries = new LocalLabel(){
            
            @Override
            protected String getLocalText() {
                return "" + filterListeLevel1.size();
            }

            @Override
            protected String getToolTip() {
                // Does get a different Tooltip for every language
                return menuResources.getString("Number of entries that fulfil the filter condition.");
            }
            
        };
        filteredLevel2Entries = new LocalLabel(){
            
            @Override
            protected String getLocalText() {
                return "" + filterListeLevel2.size();
            }

            @Override
            protected String getToolTip() {
                // Does get a different Tooltip for every language
                return menuResources.getString("Number of entries that fulfil the search criteria.");
            }
            
        };
        filterStatus = new LocalLabel(){
            
            @Override
            protected String getLocalText() {
                // Does not get a different text for any language
                Filter filter = filterListeLevel1.getFilter();
                if (filter == null || filter == TrueFilter.TRUE)
                    return menuResources.getString("No filter set");
                return filter.toString();
            }

            @Override
            protected String getToolTip() {
                // Does get a different Tooltip for every language
                return menuResources.getString("Used filter");
            }
            
        };
        sortStatus = new LocalLabel(){
            
            @Override
            protected String getLocalText() {
                // Does not get a different text for any language

                // Get the comparator used
                ConfigurableComparator comparator = sortedListe.getComparator();

                // If the comparator is null then there is no sorting
                if (comparator == null)
                    return menuResources.getString("No sorting set");
                
                // If the comparator is a comparing with no entries then there is no sorting
                if (comparator instanceof Comparing) {
                    Comparing comparing = (Comparing) comparator;
                    if (comparing.isEmpty())
                        return menuResources.getString("No sorting set");
                }

                return comparator.toString();
            }

            @Override
            protected String getToolTip() {
                // Does get a different Tooltip for every language
                return menuResources.getString("Used sorting");
            }
            
        };
        statusBar.getStatusPanel(0).add(numberOfEntries);
        statusBar.getStatusPanel(1).add(filteredLevel0Entries);
        statusBar.getStatusPanel(2).add(filteredLevel1Entries);
        // Don't show this information
        //        statusBar.getStatusPanel(1).add(filteredLevel2Entries);
        statusBar.getStatusPanel(3).add(sortStatus);
        statusBar.getStatusPanel(4).add(filterStatus);
    }
    
    public void setStatusbarEnabled(boolean enabled) {
        if (enabled)
            contentPane.add(statusBar, BorderLayout.SOUTH);
        else
            contentPane.remove(statusBar);
    }
	
	protected void catalogStats() {
        // Stop if the components are not yet initlialized
        if (sortStatus == null)
            return;
            
        numberOfEntries.update();
        filteredLevel0Entries.update();
        filteredLevel1Entries.update();
        filteredLevel2Entries.update();
        filterStatus.update();
        sortStatus.update();
	}

	
	
	private boolean getCatalog(){
		Entry e = Options.persistent.getOption("OpenConnection");

		if (e == null)
			return false;
		
		PersistentThroughEntry pte = Util.loadFromEntry(e);
		
		if (pte == null)
			return false;
		
		if (!(pte instanceof Connection))
			return false;
		
		Connection con = (Connection)pte;
		
        Catalog catalog = con.getCatalogFactory().openCatalog(con); 
        
        if (catalog == null)
            return false;
        
//        // Clear changelog
//        changeLog.clear();

		/*liste.*/setCatalog(catalog);

//        catalog.setUnchanged();
//        makeLevel0Filter();

		return true;
	}

	private boolean getSorting(){
		Entry e = Options.persistent.getOption("Sorting");

		if (e == null)
			return false;

		PersistentThroughEntry pte = Util.loadFromEntry(e);
		
		if (pte == null)
			return false;

		if (!(pte instanceof ConfigurableComparator))
			return false;
		
		ConfigurableComparator comp = (ConfigurableComparator)pte;

		sortedListe.setComparator(comp);

		return true;
	}

	// This Function is obsolete
/*	private boolean getPosition(){
		Entry e = Options.persistent.getOption("OpenPosition");

		if (e == null)
			return false;
		
		if (e.getAttribute("ListPosition") == null)
			return false;

		try {
			list.setSelectedIndex(Integer.valueOf(e.getAttribute("ListPosition")));
		} catch (Exception ex) {
			return false;
		}
		
		return true;
	}*/
	
	private boolean getFilter(){
		Entry e = Options.persistent.getOption("CurrentFilter");

		if (e == null)
			return false;
		
		if (e.getAttribute("Condition") == null)
			return false;

		try {
			filterListeLevel1.setFilter(e.getAttribute("Condition"));
		} catch (Exception ex) {
			return false;
		}
		
		return true;
	}

	protected void reOpen() {
		if (!getCatalog())
			return;
	
		getFilter();
		getSorting();

//		if (!getPosition())
//			return;
//  We're going to do this instead
		restorePosition();
		
	}
	
	private boolean restorePosition() {
		if (liste == null)
			return false;
		
		if (liste.catalog == null)
			return false;
		
		if (liste.catalog.getConnection() == null)
			return false;
		
		getPositionFromConnection(liste.catalog.getConnection());

		return true;
	}
	
	
	public void setGUIoptions() {
        if (FromCommandLine.screenshot) {
            int inW = display.getWindow().getInsets().left + display.getWindow().getInsets().right;
            int inH = display.getWindow().getInsets().top + display.getWindow().getInsets().bottom;
            display.getWindow().setSize(640 /*- 1*/ + inW, 480 + 3   /*- 5*/ + inH);
            splitListAndFeatures.setDividerLocation(300);
            return;
        }
        
		Entry gui = Options.persistent.getOption("GUI");

		if (gui == null)
			return;

        Integer extStateInteger = Integer.valueOf(gui.getAttribute("ExtendedState"));
        if (extStateInteger != null) {
            int extState = extStateInteger;
    
            // Windows: Setting the size results in not showing the statusbar
            if (System.getProperty("os.name").contains("Win"))
                display.setExtendedState(Frame.MAXIMIZED_BOTH);
            else if (((extState | Frame.MAXIMIZED_VERT) == extState ) 
                    || ((extState | Frame.MAXIMIZED_HORIZ) == extState ) )
                display.setExtendedState(extState);
            else {
                display.getWindow().setSize(Integer.valueOf(gui.getAttribute("width")),
                        Integer.valueOf(gui.getAttribute("height")));
                display.getWindow().setLocation(Integer.valueOf(gui.getAttribute("posX")),
                        Integer.valueOf(gui.getAttribute("posY")));
            }
        }

		splitListAndFeatures.setDividerLocation(Integer.valueOf(gui.getAttribute("split")));
	}

	/**
	 * This Methods look's if there exists a connection for the current catalog and
	 * if there is a connection for the current catalog the method sets it 
	 * Positional Value to the Position from the gui.
	 *
	 */
	protected void storePositionIntoConnection() {
		if (liste.catalog.getConnection() != null)
			liste.catalog.getConnection().Position = list.getSelectedIndex();
	}
	
	protected boolean getPositionFromConnection(Connection con) {
		if (con == null)
			return false;

		if (liste.getSize() >= con.Position)
            // Show the entry that was previously selected
			list.setSelectedValue(liste.get(con.Position), true);
		else
			return false;
		
		return true;
	}
	
	protected boolean saveCatalog() {
//        if (!liste.catalog.saveCatalog()) {
//             JOptionPane.showMessageDialog(display.getWindow(), menuResources.getString("Could not save catalog."),menuResources.getString("Could not save catalog."), JOptionPane.ERROR_MESSAGE);
//             return false;
//        }   
		try {
            closeCurrentTransaction();
            return changeLog.save(display.getWindow());
        } catch (UnclosedTransactionException e) {
            e.printStackTrace();
            return false;
        }
	}

	protected boolean saveCatalogAs() {
		Connection connection = liste.catalog.getSaveCatalogConnection(display.getWindow());
		
		if (connection == null) 
			return false;

//		if (!liste.catalog.saveAsCatalog(connection)){
//		     JOptionPane.showMessageDialog(display.getWindow(), menuResources.getString("Could not save catalog."),menuResources.getString("Could not save catalog."), JOptionPane.ERROR_MESSAGE);
//		     return false;
//		}
        try {
            closeCurrentTransaction();
            liste.catalog.setConnection(connection);
            if (!changeLog.save(display.getWindow()))
                return false;
        } catch (UnclosedTransactionException e) {
            e.printStackTrace();
            return false;
        }
		
		Options.recent_connections.add(connection);
		storePositionIntoConnection();
		return true;
	}
	
	protected boolean secureChanges() {
		if (liste == null) {
			System.out.println("[!ERROR!] liste == null and should not be.");
			return true;
		}
		
		if (liste.catalog == null) {
			return true;
		}
		
//		if (!liste.catalog.unsavedChanges())
//			return true;
        if (!changeLog.unsavedChanges())
            return true;
		
		switch (JOptionPane.showConfirmDialog(display.getWindow(), new JLabel(menuResources.getString("Do you want to save the changes?")), menuResources.getString("Save changes?"),  JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE)) {
		case JOptionPane.YES_OPTION:	
			if (liste.catalog.getConnection() != null)
				return saveCatalog();					
			else 
				return saveCatalogAs();
		case JOptionPane.NO_OPTION:
			return true;
		case JOptionPane.CANCEL_OPTION:
			return false;
		}
		
		// Something is wrong, don't quit.
		System.out.println("[!ERROR!] secureChanges broken.");
		return false;

	}
	
	/*	JMenuItem fileMenu_Save = new JMenuItem(menuResources.getString("Save catalog"));
	fileMenu_Save.setEnabled(liste.catalog.canSave());
	fileMenu_Save.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
	fileMenu_Save.addActionListener(new ActionListener(){
		public void actionPerformed(ActionEvent e) {
			liste.catalog.saveCatalog();
			// Changing the catalog has the ability to change the menu
			// therefore we have to recreate the Menu
			InitM();
		}});
	JMenuItem fileMenu_SaveAs = new JMenuItem(menuResources.getString("Save catalog as"));
	fileMenu_SaveAs.addActionListener(new ActionListener(){
		public void actionPerformed(ActionEvent e) {
			Connection connection = liste.catalog.getSaveCatalogConnection();
			if (connection != null) {
				liste.catalog.saveAsCatalog(connection);
				Options.recent_connections.add(connection);
			}
			// Changing the catalog has the ability to change the menu
			// therefore we have to recreate the Menu
			InitM();
		}});
*/
	protected boolean exit() {

		// Make sure the user saves the changes
		// or discardes them willingly
		if (!secureChanges())
			return false;
			
		
		if (Options.isSaveOnExit()) { 
		
			Entry gui = Options.persistent.getOption("GUI");

			if (gui == null)
				gui = Options.persistent.createOption("GUI");

			gui.setAttribute("width", String.valueOf(display.getWindow().getWidth()));
			gui.setAttribute("height", String.valueOf(display.getWindow().getHeight()));
			gui.setAttribute("posX", String.valueOf(display.getWindow().getX()));
			gui.setAttribute("posY", String.valueOf(display.getWindow().getY()));
			gui.setAttribute("height", String.valueOf(display.getWindow().getSize().height));
			gui.setAttribute("split", String.valueOf(splitListAndFeatures.getDividerLocation()));
			
			gui.setAttribute("ExtendedState", String.valueOf(display.getExtendedState()));

			
			saveCatalogConnection();
			saveSorting();
			saveFilter();
			savePosition();
			
			Options.saveOptions();
		}
		
		return true;
	}
	
	private void savePosition(){
		Options.persistent.removeOption("OpenPosition");
		Entry pos = Options.persistent.createOption("OpenPosition");
		pos.setAttribute("ListPosition", String.valueOf(list.getSelectedIndex()));
	}
	
	private void saveFilter(){
		Options.persistent.removeOption("CurrentFilter");

		if (filterListeLevel1.getFilter() == null)
			return;
		
		Entry filter = Options.persistent.createOption("CurrentFilter");
		filter.setAttribute("Condition", filterListeLevel1.getFilter().getCondition());
	}
	
	private void saveSorting(){
		// Remove previous OpenConnection
		Options.persistent.removeOption("Sorting");
		// If a catalog is opened and a connection exists then
		if (sortedListe.getComparator() == null)
			return;
		// store the Connection Information
		Entry sort = Options.persistent.createOption("Sorting");
		Util.saveToEntry(sortedListe.getComparator(), sort);					
	}

	private void saveCatalogConnection(){
		// Remove previous OpenConnection
		Options.persistent.removeOption("OpenConnection");
		// If a catalog is opened and a connection exists then
		if (liste.catalog == null)
			return;
		if (liste.catalog.getConnection() == null)
			return;

		// store the Connection Information
		Entry oCon = Options.persistent.createOption("OpenConnection");
		Util.saveToEntry(liste.catalog.getConnection(), oCon);					
	}
	
	public MainFrameBackend(final Display display){
        this.display = display;
        liste = new Listing(Options.getDefaultCatalog(display.getWindow()));
        liste.setChangeLog(changeLog);
        changeLog.addSaveStateListener(this);        
        copyBuffer = Options.getDefaultCatalog(display.getWindow());
        filterListeLevel0 = new FilterListing(liste);
        filterListeLevel1 = new FilterListing(filterListeLevel0);
        filterListeLevel2 = new FilterListing(filterListeLevel1);
        makeLevel0Filter();
        sortedListe = new SortedListing(filterListeLevel2);
        showedList = new ShowListing(sortedListe);
		try {
			jbInit();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	protected void executeExit() {
		System.exit(0);
	}

    public void saveStateChanged() {
        InitM();
        updateToolbar();
        updateTitle();
    }
    
    protected void updateTitle() {
        if (changeLog != null && changeLog.unsavedChanges())
            display.setTitle("* " + getCatalogTitle());
        else
            display.setTitle(getCatalogTitle());
    }

    protected String getCatalogTitle() {
        Connection con = liste.catalog.getConnection();
        String name = liste.catalog.getGeneralInformationEntry().getAttribute("Name", Options.getCurrentLocale());
        if (name == null) {
            Set<Locale> langs = liste.catalog.getGeneralInformationEntry().getAttributeLanguages("Name");
            if (langs.size() > 0)
                name = liste.catalog.getGeneralInformationEntry().getAttribute("Name", langs.iterator().next());
        }
        if (con == null && name == null)
            return mecat;
        if (con == null)
            return name + " - " + mecat;
        if (name == null)
            return con.toString() + " - " + mecat;
        return name + " - " + con.toString() + " - " + mecat;
    }
	
    /*
	protected void processWindowEvent(WindowEvent e) {
		if (e.getID() == WindowEvent.WINDOW_CLOSING) {
			if (!exit())
				return;
			super.processWindowEvent(e);
			executeExit();
		}
		if (e.getID() == WindowEvent.WINDOW_OPENED) {
			setGUIoptions();
		}
		super.processWindowEvent(e);
	}*/
}

