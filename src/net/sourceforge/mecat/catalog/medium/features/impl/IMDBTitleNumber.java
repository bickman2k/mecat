/*
 *  MeCat - Media Catalog more information on
 *  http://mecat.sourceforge.net/
 *  Copyright (C) 2005, Stephan Richard Palm, All Rights Reserved.
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
 * Created on Jun 17, 2005
 * @author Stephan Richard Palm
 */
package net.sourceforge.mecat.catalog.medium.features.impl;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.text.html.HTMLEditorKit;

import net.sourceforge.mecat.catalog.gui.features.FeaturePanel;
import net.sourceforge.mecat.catalog.gui.features.desktop.FeatureDesktop;
import net.sourceforge.mecat.catalog.gui.features.impl.IMDBTitleNumberFeaturePanel;
import net.sourceforge.mecat.catalog.gui.options.features.IMDB.CheckWithIMDB;
import net.sourceforge.mecat.catalog.gui.options.features.IMDB.IMDBGenresParser;
import net.sourceforge.mecat.catalog.gui.options.features.IMDB.IMDBTitleSearchParser;
import net.sourceforge.mecat.catalog.gui.options.features.IMDB.SearchResultsMainFrameFrontend;
import net.sourceforge.mecat.catalog.i18n.util.SimpleLocalCheckBox;
import net.sourceforge.mecat.catalog.medium.Medium;
import net.sourceforge.mecat.catalog.medium.PointedMedium;
import net.sourceforge.mecat.catalog.medium.features.NumberFeature;
import net.sourceforge.mecat.catalog.medium.features.menu.IMDBTitleNumberMenu;
import net.sourceforge.mecat.catalog.medium.features.option.FeatureOption;
import net.sourceforge.mecat.catalog.medium.features.option.impl.IMDBFeatureOption;
import net.sourceforge.mecat.catalog.medium.hidden.BagImage;
import net.sourceforge.mecat.catalog.medium.hidden.Movie;
import net.sourceforge.mecat.catalog.medium.hidden.Role;
import net.sourceforge.mecat.catalog.option.Options;
import net.sourceforge.mecat.catalog.sort.ByNumberFromTextFeature;
import net.sourceforge.mecat.catalog.sort.ConfigurableComparator;
import net.sourceforge.mecat.srp.utils.ParserGetter;

public class IMDBTitleNumber extends NumberFeature {

//	protected final JButton search;
//    protected final JButton getAkas;
//	protected final JButton getYear;
//	protected final JButton getGenres;
//	protected final JButton getSummary;
//    protected final JButton getActors;
//	protected final JPanel buttonPanel;

    
    
    @Override
    public JPanel getFeatureOptionPanel(FeatureOption featureOption) {
        final ResourceBundle res = Options.getI18N(IMDBTitleNumber.class);

        if  (featureOption == null)
            return super.getFeatureOptionPanel(null);

        final IMDBFeatureOption imdbFeatureOption = (IMDBFeatureOption) featureOption;
        
        final JCheckBox articleBox = new SimpleLocalCheckBox(res, "Move comma separated article from german title.", imdbFeatureOption.articleReposition);
        final JCheckBox quotesBox = new SimpleLocalCheckBox(res, "Remove surrounding quotes.", imdbFeatureOption.articleReposition);
        articleBox.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent arg0) {
                imdbFeatureOption.articleReposition = articleBox.isSelected();
            }
        });
        quotesBox.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent arg0) {
                imdbFeatureOption.removeQuotes = quotesBox.isSelected();
            }
        });

        JPanel panel = new JPanel();
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.weighty = 0;
        panel.setLayout(new GridBagLayout());
        panel.add(articleBox, c);
        panel.add(quotesBox, c);
        c.weighty = 1;
        c.gridheight = GridBagConstraints.REMAINDER;
        panel.add(new JPanel(), c);
        
        return panel;
    }
    
    static {
        IMDBTitleNumberMenu.registerMenu();        
    }

    public static String transformMessage(Exception e) {
        StringBuffer txt = new StringBuffer();
        String msg = e.getMessage();

        txt.append(txt + msg);
        txt.append(System.getProperty("line.separator"));
        if (e instanceof IOException) {
            if (msg.contains("code") && msg.contains(" 400 ")) {
                txt.append(Options.getI18N(IMDBTitleNumber.class).getString("Error most likely caused by wrong IMDB number."));
                txt.append(System.getProperty("line.separator"));
            } else {
                txt.append(Options.getI18N(IMDBTitleNumber.class).getString("Error most likely caused by error in connection to imdb webcite."));
                txt.append(System.getProperty("line.separator"));
            }
        }
        
        return txt.toString();
    }
	
    protected void showErrors(final JComponent parent, final Map<Movie, List<Exception>> errorGathering) {
        StringBuffer buf = new StringBuffer();
        
        if (errorGathering.get(this.medium) != null) {
            for (Exception e : errorGathering.get(this.medium)) {
                buf.append(transformMessage(e));
            }
            JOptionPane.showMessageDialog(parent, buf.toString(), Options.getI18N(IMDBTitleNumber.class).getString("Error"), JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public void getYearTitleGenresAkasFromIMDB(final JComponent parent) {
        final Map<Movie, List<Exception>> errorGathering = new HashMap<Movie, List<Exception>>();

        // The cast is no risk because IMDBTitleNumber is limited to Movies
        IMDBTitleSearchParser.retrieveYearTitleGenresAkasFromIMDB(parent, (Movie)this.medium, (IMDBFeatureOption) getFeatureOption(), errorGathering);

        showErrors(parent, errorGathering);
    }
    
    public void getAkasFromIMDB(final JComponent parent) {
        final Map<Movie, List<Exception>> errorGathering = new HashMap<Movie, List<Exception>>();

        startTransaction(Options.getI18N(IMDBTitleNumber.class).getString("Copy information from IMDB"), true, true);

        // The cast is no risk because IMDBTitleNumber is limited to Movies
        IMDBTitleSearchParser.retrieveAkasFromIMDB(parent, (Movie)this.medium, (IMDBFeatureOption) getFeatureOption(), errorGathering);

        stopTransaction();
        
        showErrors(parent, errorGathering);
    }
    public void getTitleFromIMDB(final JComponent parent) {
        final Map<Movie, List<Exception>> errorGathering = new HashMap<Movie, List<Exception>>();

        startTransaction(Options.getI18N(IMDBTitleNumber.class).getString("Copy information from IMDB"), true, true);

        // The cast is no risk because IMDBTitleNumber is limited to Movies
        IMDBTitleSearchParser.retrieveTitleFromIMDB(parent, (Movie)this.medium, (IMDBFeatureOption) getFeatureOption(), errorGathering);

        stopTransaction();

        showErrors(parent, errorGathering);
    }
    public void getYearFromIMDB(final JComponent parent) {
        final Map<Movie, List<Exception>> errorGathering = new HashMap<Movie, List<Exception>>();

        startTransaction(Options.getI18N(IMDBTitleNumber.class).getString("Copy information from IMDB"), true, true);

        // The cast is no risk because IMDBTitleNumber is limited to Movies
		IMDBTitleSearchParser.retrieveYearFromIMDB(parent, (Movie)this.medium, (IMDBFeatureOption) getFeatureOption(), errorGathering);

        stopTransaction();

        showErrors(parent, errorGathering);
	}
    public void getRuntimeFromIMDB(final JComponent parent) {
        final Map<Movie, List<Exception>> errorGathering = new HashMap<Movie, List<Exception>>();

        startTransaction(Options.getI18N(IMDBTitleNumber.class).getString("Copy information from IMDB"), true, true);

        // The cast is no risk because IMDBTitleNumber is limited to Movies
        IMDBTitleSearchParser.retrieveRuntimeFromIMDB(parent, (Movie)this.medium, (IMDBFeatureOption) getFeatureOption(), errorGathering);

        stopTransaction();

        showErrors(parent, errorGathering);
    }
    public void getGenresFromIMDB(final JComponent parent){
        final Map<Movie, List<Exception>> errorGathering = new HashMap<Movie, List<Exception>>();

        startTransaction(Options.getI18N(IMDBTitleNumber.class).getString("Copy information from IMDB"), true, true);

        // The cast is no risk because IMDBTitleNumber is limited to Movies
		IMDBTitleSearchParser.retrieveGenresFromIMDB(parent, (Movie)this.medium, (IMDBFeatureOption) getFeatureOption(), errorGathering);

        stopTransaction();

        showErrors(parent, errorGathering);
	}
    public void getSummaryFromIMDB(final JComponent parent){
        final Map<Movie, List<Exception>> errorGathering = new HashMap<Movie, List<Exception>>();

        startTransaction(Options.getI18N(IMDBTitleNumber.class).getString("Copy information from IMDB"), true, true);

        // The cast is no risk because IMDBTitleNumber is limited to Movies
		IMDBTitleSearchParser.retrievePlotSummaryFromIMDB(parent, (Movie)this.medium, errorGathering);

        stopTransaction();

        showErrors(parent, errorGathering);
	}
    public void getActorsFromIMDB(final JComponent parent){
        final Map<Movie, List<Exception>> errorGathering = new HashMap<Movie, List<Exception>>();

        startTransaction(Options.getI18N(IMDBTitleNumber.class).getString("Copy information from IMDB"), true, true);

        // The cast is no risk because IMDBTitleNumber is limited to Movies
        IMDBTitleSearchParser.retrieveActorsFromIMDB(parent, (Movie)this.medium, (IMDBFeatureOption) getFeatureOption(), errorGathering);

        stopTransaction();

        showErrors(parent, errorGathering);
    }
	
	public IMDBTitleNumber(Medium medium) {
		super(medium, "IMDBTitleNumber");
//		ResourceBundle res = Options.getI18N(getClass());
//
//		search = new SimpleLocalButton(res, "Search");
//		search.addActionListener(new ActionListener(){
//			public void actionPerformed(ActionEvent arg0) {
//				search(search);
//			}});
//        getAkas = new SimpleLocalButton(res, "get Akas");
//        getAkas.addActionListener(new ActionListener(){
//            public void actionPerformed(ActionEvent arg0) {
//                getAkasFromIMDB(getAkas);
//            }});
//		getYear = new SimpleLocalButton(res, "get Year");
//		getYear.addActionListener(new ActionListener(){
//			public void actionPerformed(ActionEvent arg0) {
//				getYearFromIMDB(getYear);
//			}});
//		getGenres = new SimpleLocalButton(res, "get Genres");
//		getGenres.addActionListener(new ActionListener(){
//			public void actionPerformed(ActionEvent arg0) {
//				getGenresFromIMDB(getGenres);
//			}});
//		getSummary = new SimpleLocalButton(res, "get Summary");
//		getSummary.addActionListener(new ActionListener(){
//			public void actionPerformed(ActionEvent arg0) {
//				getSummaryFromIMDB(getSummary);
//			}});
//        getActors = new SimpleLocalButton(res, "get cast");
//        getActors.addActionListener(new ActionListener(){
//            public void actionPerformed(ActionEvent arg0) {
//                getActorsFromIMDB(getActors);
//            }});
//		buttonPanel = new JPanel();
//		buttonPanel.setLayout(new GridLayout(2,3));
//		buttonPanel.add(search);
//        buttonPanel.add(getAkas);
//		buttonPanel.add(getYear);
//		buttonPanel.add(getGenres);
//		buttonPanel.add(getSummary);
//        buttonPanel.add(getActors);
//		updateButtonState();
	}
	
//	protected void updateButtonState(){
//		if (getInt() != null) {
//            getAkas.setEnabled(true);
//			getYear.setEnabled(true);
//			getGenres.setEnabled(true);
//			getSummary.setEnabled(true);
//		} else {
//            getAkas.setEnabled(false);
//			getYear.setEnabled(false);
//			getGenres.setEnabled(false);
//			getSummary.setEnabled(false);
//		}
//	}
	

	public String getText() {
		if (get() == null)
			return Options.getI18N(IMDBTitleNumber.class).getString("The reference number to the imdb is not set.");
		return Options.getI18N(IMDBTitleNumber.class).getString("The number for the movie at the imdb is NUMBER.").replaceAll("\\[NUMBER\\]", get());
	}

    public String getTextHTML(int availableWidth) {
        DecimalFormat df = new DecimalFormat("0000000");
        
        if (get() == null)
            return Options.getI18N(IMDBTitleNumber.class).getString("The reference number to the imdb is not set.");
        return Options.getI18N(IMDBTitleNumber.class).getString("The number for the movie at the imdb is NUMBER.").replaceAll("\\[NUMBER\\]", 
                "<a href=\"http://www.imdb.com/title/tt" +  df.format(getInt()) + "/\">" + get() + "</a>");
    }

	public static ConfigurableComparator getComparator() {
		return new ByNumberFromTextFeature(IMDBTitleNumber.class);	
	}

    @Override
	public FeaturePanel getPanel(FeatureDesktop desktop, boolean border) {
//        final FeaturePanel numberPanel = super.getPanel(desktop, false);
//		FeaturePanel ret = new FeaturePanel<IMDBTitleNumber>(this, desktop, border, attributeName){
//            public void requestFocus() {
//                numberPanel.requestFocus();
//            }
//            
//            public boolean hasFocus() {
//                return numberPanel.hasFocus();
//            }
//
//            public void featureValueChanged(Feature source) {
//                // number Panel does everything now necessary
//                // TODO later display only buttons that are usefull
//                
//            }
//        };
//		
//		ret.add(numberPanel);
//		ret.add(buttonPanel, BorderLayout.SOUTH);
//		
//		return ret;
        return new IMDBTitleNumberFeaturePanel(this, desktop, border);
	}
	
    
    
	public void search(final JComponent parent) {
        final ResourceBundle res = Options.getI18N(getClass());

        JPanel panel = new JPanel();
//		JCheckBox title = new JCheckBox("Use Title");
		JCheckBox year = new SimpleLocalCheckBox(res, "Use year");
		JCheckBox genres = new SimpleLocalCheckBox(res, "Use genres");
		
		year.setEnabled(medium.getFeature(Year.class).get() != null);
		genres.setEnabled(medium.getFeature(Genre.class).getChoices().size() > 0);
		
		panel.setLayout(new GridLayout(0, 1));
//		panel(title);
		panel.add(year);
		panel.add(genres);
		
        // Get the title for the search
        String tmp = medium.getFeature(Title.class).get(Locale.ENGLISH);
        if (tmp == null || tmp.length() == 0) 
            tmp = medium.getFeature(Title.class).get();
        // Search title
        final String title = tmp;

        if (title == null || title.length() == 0) {
            JOptionPane.showMessageDialog(parent, res.getString("Can't search without title!"), res.getString("Can't search without title!"), JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (JOptionPane.showConfirmDialog(parent, panel, res.getString("Options used to search"), JOptionPane.YES_NO_OPTION) == JOptionPane.OK_OPTION) {

//			JOptionPane.showMessageDialog(null, 
//				"Search for " + title +
//				((year.isSelected())?" from " + medium.getFeature(Year.class).get():"") +
//				((genres.isSelected())?" with genres " + medium.getFeature(Genres.class).getShortText():""), 
//				"Search", JOptionPane.INFORMATION_MESSAGE);
			// Parser
			// the parser holds all information about the search

            
            
            ////////////////////////////////////
            // Make frontend for list from imdb
            SearchResultsMainFrameFrontend selector;
            
            Component component = parent;
            while (component != null && !(component instanceof Frame) && !(component instanceof Dialog))
                component = component.getParent();

            if (component == null)
                selector = new SearchResultsMainFrameFrontend();
            else if (component instanceof Dialog)
                selector = new SearchResultsMainFrameFrontend((Dialog)component);
            else
                selector = new SearchResultsMainFrameFrontend((Frame)component);

            final SearchResultsMainFrameFrontend frontend = selector;
            
            final IMDBTitleSearchParser imdbParser = new IMDBTitleSearchParser(parent, this, selector.getBackend().getListing(),
                    title, 
                    ((year.isSelected())?medium.getFeature(Year.class).getInt() : null),
                    ((genres.isSelected())?medium.getFeature(Genre.class).getSelection() : null));
            
            ///////////////////////////////////////
            // Make thread for retrieving data from imdb
            Thread t = new Thread(){
                public void run(){
			try {
                String searchStr = "http://akas.imdb.com/find?q=" + URLEncoder.encode(title, "UTF-8") + ";tt=on;mx=20";
				URL searchURL =  new URL(searchStr);
				URLConnection urlConnection = searchURL.openConnection();
				urlConnection.getHeaderFields();
//				for (Map.Entry<String, List<String>> entry : urlConnection.getHeaderFields().entrySet()) 
//					for (String val : entry.getValue())
//						System.out.println("[" + entry.getKey() + "] : " + val);
//				System.out.println(urlConnection.getURL());

				// IMDB Searches sometime directly show one result
				// in this case we have to processed differently
				if (!urlConnection.getURL().equals(searchURL)) {
					// Find the number from the title in the url
					Integer number = null;
					String split[] = urlConnection.getURL().toString().split("/");
					for (String str : split)
						if (str.startsWith("tt"))
							number = Integer.parseInt(str.substring(2).trim());
					if (number == null) {
						Exception e = new Exception(res.getString("Could not get the number from URL.").replaceAll("\\[URL\\]", urlConnection.getURL().toString()));
						return;
					}
					// Create one movie from the direct result
					Movie movie = frontend.getBackend().getListing().create(Movie.class);
					movie.getFeature(IMDBTitleNumber.class).set(number);
					// Use the allready open connection to recieve the Genres and the Year
					IMDBTitleSearchParser.retrieveFromIMDBFullURL(parent, new IMDBGenresParser(movie, (IMDBFeatureOption) getFeatureOption()), searchURL, movie, null);
					// Make a new connection to recieve the plot summary
					IMDBTitleSearchParser.retrievePlotSummaryFromIMDB(parent, movie, null);

                    // Stop status icon that indicates working
                    frontend.stillWorking = false;

                    return;
				}
				
				InputStreamReader r = new InputStreamReader(new BufferedInputStream(urlConnection.getInputStream()));
//				System.out.println(urlConnection.getURL());
//				for (Map.Entry<String, List<String>> entry : urlConnection.getHeaderFields().entrySet()) 
//					for (String val : entry.getValue())
//						System.out.println("[" + entry.getKey() + "] : " + val);
				ParserGetter getter = new ParserGetter();
				HTMLEditorKit.Parser parser = getter.getParser();
				parser.parse(r, imdbParser, true);

                // Stop status icon that indicates working
                frontend.stillWorking = false;
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
                }
            };
            t.start();

            // Show frontend
            selector.setVisible(true);

            // Get result
            Movie result = (Movie) selector.getResult();
            // result is equal to null if the user cancel the operation
            if (result == null)
                return;
            
            imdbParser.stopParsing();

            //////////////////////////////////
            // Transfer information
            transferInformation(parent, result, imdbParser);
            
        }
	}
	
    protected void transferInformation(final Component component, final Movie movie, final IMDBTitleSearchParser searchParser) {
        ResourceBundle res = Options.getI18N(this.getClass());

        JPanel panel = new JPanel();
        JCheckBox number = new SimpleLocalCheckBox(res, "Take IMDB number");
        JCheckBox title = new SimpleLocalCheckBox(res, "Take english title");
        JCheckBox akas = new SimpleLocalCheckBox(res, "Take akas");
        JCheckBox year = new SimpleLocalCheckBox(res, "Take year");
        JCheckBox runtime = new SimpleLocalCheckBox(res, "Take runtime");
        JCheckBox genres = new SimpleLocalCheckBox(res, "Take genres");
        JCheckBox summary = new SimpleLocalCheckBox(res, "Take summary");
        JCheckBox frontImage = new SimpleLocalCheckBox(res, "Take front image");
        JCheckBox actors = new SimpleLocalCheckBox(res, "Take actors");

        // If the imdb has no number then we can't take it
        if (movie.getFeature(IMDBTitleNumber.class).getInt() == null)
            number.setEnabled(false);
        // There is currently no imdb-number therefor suggest to take the one from the imdb
        else if (getInt() == null)
            number.setSelected(true);
        // Compare current imdb-number with the imdb-number selected
        else if (getInt().intValue() == movie.getFeature(IMDBTitleNumber.class).getInt().intValue())
            number.setEnabled(false);
        // There is a differnt number, don't want to suggest to override it
        else
            number.setSelected(false);
        
        if (searchParser.searchForTitle.equals(movie.getFeature(Title.class).get(Locale.ENGLISH)))
            title.setEnabled(false);
        else {
            title.setEnabled(true);
            if (CheckWithIMDB.similar(searchParser.searchForTitle, movie.getFeature(Title.class).get(Locale.ENGLISH), Locale.ENGLISH)) 
                title.setSelected(true);
            else
                title.setSelected(false);
        }
        
        if (movie.getFeature(Title.class).getLanguages().size() > 1)
            akas.setSelected(true);
        else
            akas.setEnabled(false);
        
        // If the imdb has no year we can't take it
        if (movie.getFeature(Year.class).getInt() == null)
            year.setEnabled(false);
        // There is currently no year therefor suggest to take the one from the imdb
        else if (medium.getFeature(Year.class).getInt() == null)
            year.setSelected(true);
        // Compare current year with the year from the imdb
        else if (medium.getFeature(Year.class).getInt().intValue() == movie.getFeature(Year.class).getInt().intValue())
            year.setEnabled(false);
        // There is a differnt year, don't want to suggest to override it
        else
            year.setSelected(false);

        // If the imdb has no runtime we can't take it
        if (movie.getFeature(Runtime.class).getInt() == null)
            runtime.setEnabled(false);

        if (searchParser.searchForGenres == null)
            genres.setSelected(true);
        else
            genres.setEnabled(false);
        
        // If there is no description we can't take a description
        if (movie.getFeature(Description.class).get(Locale.ENGLISH) == null)
            summary.setEnabled(false);
        // If we already have the description there is no reason either
        else if (medium.getFeature(Description.class).get(Locale.ENGLISH) != null
                && medium.getFeature(Description.class).get(Locale.ENGLISH).contains(movie.getFeature(Description.class).get(Locale.ENGLISH)))
            summary.setEnabled(false);
        // Preselect to take if there is no description at the moment
        else if (medium.getFeature(Description.class).get(Locale.ENGLISH) == null)
            summary.setSelected(true);
        // Don't preselect if there is a description at the moment
        else
            summary.setSelected(true);
        
/*        if (movie.getFeature(CoverImages.class).imgFeatures.firstElement().getImageURL() == null)
            frontImage.setEnabled(false);
        else
            frontImage.setSelected(true);*/
        
        if (movie.getFeature(ExtraImagesBag.class).getBagImages().size() == 0)
            frontImage.setEnabled(false);
        else
            frontImage.setSelected(true);
        
        actors.setEnabled(false);
        List<PointedMedium<Role>> roles = medium.getFeature(Roles.class).getMedia();
        for (PointedMedium<Role> role : movie.getFeature(Roles.class).getMedia()) {
            if (roles.contains(role)) 
                continue;

            actors.setEnabled(true);
            actors.setSelected(true);

            break;
        }
            
        panel.setLayout(new GridLayout(0, 1));
        panel.add(number);
        panel.add(title);
        panel.add(akas);
        panel.add(year);
        panel.add(runtime);
        panel.add(genres);
        panel.add(summary);
        panel.add(frontImage);
        panel.add(actors);
        
        if (JOptionPane.showConfirmDialog(component, panel, res.getString("Take information along."), JOptionPane.YES_NO_OPTION) == JOptionPane.OK_OPTION) {
            startTransaction(res.getString("Copy information from IMDB"), true, true);
            
            if (number.isSelected())
                movie.getFeature(IMDBTitleNumber.class).copyTo(this);
                //feature.set(movie.getFeature(IMDBTitleNumber.class).get());
            if (title.isSelected())
                medium.getFeature(Title.class).set(movie.getFeature(Title.class).get(Locale.ENGLISH), Locale.ENGLISH);
//              movie.getFeature(Title.class).copyTo(medium.getFeature(Title.class));
            if (akas.isSelected())
                for (Locale l : movie.getFeature(Title.class).getLanguages())
                    if (!l.equals(Locale.ENGLISH))
                        medium.getFeature(Title.class).set(movie.getFeature(Title.class).get(l), l);
            if (year.isSelected())
                movie.getFeature(Year.class).copyTo(medium.getFeature(Year.class));
            if (runtime.isSelected())
                movie.getFeature(Runtime.class).copyTo(medium.getFeature(Runtime.class));
            if (genres.isSelected())
                movie.getFeature(Genre.class).copyTo(medium.getFeature(Genre.class));
            if (summary.isSelected()) {
                // If there is no description at the moment we can just put it in
                if (medium.getFeature(Description.class).get(Locale.ENGLISH) == null)
                    medium.getFeature(Description.class).set(movie.getFeature(Description.class).get(Locale.ENGLISH), Locale.ENGLISH);
                // If the description from the imdb containtes the current description
                // the current description will be erased
                else if (movie.getFeature(Description.class).get(Locale.ENGLISH).contains(medium.getFeature(Description.class).get(Locale.ENGLISH)))
                    medium.getFeature(Description.class).set(movie.getFeature(Description.class).get(Locale.ENGLISH), Locale.ENGLISH);
                // Append the new to the old
                else
                    medium.getFeature(Description.class).set(medium.getFeature(Description.class).get(Locale.ENGLISH) + movie.getFeature(Description.class).get(Locale.ENGLISH), Locale.ENGLISH);
            }
            if (frontImage.isSelected()) {
                for (BagImage bagImage : movie.getFeature(ExtraImagesBag.class).getBagImages()) {
                    bagImage.getFeature(BagImageFeature.class).getImageURL();
                    BagImage newBagImage = medium.getFeature(ExtraImagesBag.class).createBagImage();
                    newBagImage.getFeature(BagImageFeature.class).setImage( bagImage.getFeature(BagImageFeature.class).getImageURL() );
                }
                
//                medium.getFeature(CoverImages.class).imgFeatures.firstElement().setImage(
//                        movie.getFeature(CoverImages.class).imgFeatures.firstElement().getImageURL());
            }
            
            if (actors.isSelected()) {
//                List<Role> roles = medium.getFeature(Roles.class).getMedia();
                for (PointedMedium<Role> role : movie.getFeature(Roles.class).getMedia()) {
                    if (roles.contains(role)) 
                        continue;

                    Role newRole = medium.getFeature(Roles.class).newMedium().getMedium();
                    role.getMedium().copyTo(newRole);
                }
            }
            
            stopTransaction();
        }
    }
    
    @Override
    public boolean validate(String condition) {
        // If the condition is smaller then 2
        // the user propably wants to get all medium with empty imdb number
        if (condition == null || condition.length() < 2)
            return super.validate(null);
        else
            return super.validate(condition);
    }
    
    
    
}
