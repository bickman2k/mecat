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
 * Created on Jun 15, 2005
 * @author Stephan Richard Palm
 */
package net.sourceforge.mecat.catalog.gui.options.features.IMDB;

import static javax.swing.text.html.HTML.Attribute.HREF;
import static javax.swing.text.html.HTML.Tag.A;
import static javax.swing.text.html.HTML.Tag.HTML;
import static javax.swing.text.html.HTML.Tag.OL;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.HTML.Tag;

import net.sourceforge.mecat.catalog.medium.Listing;
import net.sourceforge.mecat.catalog.medium.Medium;
import net.sourceforge.mecat.catalog.medium.features.impl.Genre;
import net.sourceforge.mecat.catalog.medium.features.impl.IMDBTitleNumber;
import net.sourceforge.mecat.catalog.medium.features.impl.Year;
import net.sourceforge.mecat.catalog.medium.features.option.impl.IMDBFeatureOption;
import net.sourceforge.mecat.catalog.medium.hidden.Movie;
import net.sourceforge.mecat.catalog.option.Options;
import net.sourceforge.mecat.srp.utils.ParserGetter;

public class IMDBTitleSearchParser extends HTMLEditorKit.ParserCallback{
	
	enum MatchTyp {
		Popular("Popular Titles"),
		Exact("Exact Matches"),
		Partial("Partial Matches"),
		Approx("Approx Matches");
		
		final public String exp;

		MatchTyp(final String exp) {
			this.exp = exp;
		}
	}
	enum Position {
		NoSpecialPlace, FoundTitleText, WithinTitleList
	}
	enum PositionWithInMatchList {
		NoSpecialPlace, WithInTagA, AfterTagA 
	}

	MatchTyp typ;
	Position pos;
	PositionWithInMatchList posInMatch;
	
	int number = -1;
	String foundTitle = "";
	String yearStr = "";

	
	final public String searchForTitle;
	final public Integer searchForYear;
	final public List<String> searchForGenres;
	
//	final IMDBTitleNumber feature;
    final IMDBFeatureOption imdbFeatureOption;
    
    final JComponent parent;
    final Listing listing;
    
	public IMDBTitleSearchParser(final JComponent parent, final IMDBTitleNumber feature, final Listing listing, final String title, 
			                     final Integer year, final List<String> genres) {
        this.parent = parent;
//		this.feature = feature;
        this.listing = listing;
		this.imdbFeatureOption = (IMDBFeatureOption) feature.getFeatureOption();
		searchForTitle = title;
		searchForYear = year;
		searchForGenres = genres;
//		mainFrameMatch = new SearchResultsMainFrame(feature, this);
	}
	
//	public SearchResultsMainFrame mainFrameMatch = null;

	public static Integer findYear(final String yearStr) {
		String split[] = yearStr.split("\\(|\\)");
		for (String str : split)
			if (str.trim().length() >= 4)
				try {
					return Integer.valueOf(str.trim().substring(0,4));
				} catch (Exception e) {
					// Catching Number Parsing Exception
					// This happens when there is a comment after the movie that is not the year
					// Don't care
				}
		return null;
	}
	
	
	protected void addTitle(){
		Integer year = findYear(yearStr);
		
		if (number == -1)
			return;
				
		if ((searchForYear != null)  && (year != null) && ((int)year != (int)searchForYear))
			return;
		
		Movie movie = listing.create(Movie.class);
//		movie.getFeature(Title.class).set(foundTitle, Locale.ENGLISH);
        IMDBGenresParser.addTitle(movie, imdbFeatureOption, Locale.ENGLISH, foundTitle);
		if (year != null)
			movie.getFeature(Year.class).set(year);
		movie.getFeature(IMDBTitleNumber.class).set(number);
	}
	
	
	
	public void handleText(char[] text, int position) {
		String txt = new String(text);
		for (MatchTyp typ : MatchTyp.values())
			if (txt.indexOf(typ.exp) != -1) {
				this.typ = typ;
				pos = Position.FoundTitleText;
			}
//		if (txt.indexOf("Exact Matches") != -1)
//			pos = Position.FoundTitleText;
		if ((pos == Position.WithinTitleList) && (posInMatch == PositionWithInMatchList.WithInTagA)) {
			foundTitle = txt;
		}
		if ((pos == Position.WithinTitleList) && (posInMatch == PositionWithInMatchList.AfterTagA)) {
			yearStr = txt;
			addTitle();
			posInMatch = PositionWithInMatchList.NoSpecialPlace;
		}

//		System.out.println(pos);
	}
	
	public void handleEndTag(Tag tag, int position) {
		if ((tag == OL) && (pos == Position.WithinTitleList))
			pos = Position.NoSpecialPlace;
		if ((tag == A) && (pos == Position.WithinTitleList) 
				       && (posInMatch == PositionWithInMatchList.WithInTagA)) 
			posInMatch = PositionWithInMatchList.AfterTagA;
		if ((tag == HTML))
			flush();
	}
	
	// This is a flag that makes sure whatever happens.
	// We only flush it once.
	boolean flushFirst = true;
    private boolean stopParsing = false;
	
	public void flush() {
		if (flushFirst) {
			flushFirst = false;
            // Don't need to thread this anymore
//			Thread t = new Thread(){
//				public void run(){
					retrieveIMDBInfos();
//				}
//			};
//			t.start();
		}
	}
	
	public void retrieveIMDBInfos(){
		retrieveLoop:
		for (Medium medium : new Vector<Medium>(listing)) {
			if (stopParsing)
				return;
			if (medium instanceof Movie) {
				final Movie movie = (Movie) medium;
				boolean gotGenres = retrieveRuntimeGenresAkasImageAndActorsFromIMDB(parent, movie, imdbFeatureOption, null);
				if (gotGenres && searchForGenres != null) {
					Genre genres = movie.getFeature(Genre.class);
					for (String genre : searchForGenres)
						if (!genres.exists(genre)) {
//							System.out.println("Remove " + medium.getFeature(Title.class).get(Locale.ENGLISH));
//							System.out.println("Did not find the genre " + genre + ".");
//							System.out.println("Only the genres: " + genres.getShortText());
                            listing.remove(medium);
							continue retrieveLoop;
//							mainFrameMatch.getListing().remove(medium);
//							return;
						}
				}
				retrievePlotSummaryFromIMDB(parent, movie, null);
			}
		}
    // TODO if there are still open threads, then wait from them here!
	}
	
	/**
	 * @param movie The movie one wants to get the information for. If this movie does not has any IMDB number this will result in an error.
	 * @param errorGathering if Initialised with a Mapping the mapping will contain all occured errors after the method is done. This happens
	 * without removing any errors that are allready stored for this movie.
	 * if it is initialised with null. The errors will be printed on the command line.
	 * @return true if no errors occured
	 */
	public static boolean retrievePlotSummaryFromIMDB(final JComponent parent, final Movie movie, final Map<Movie, List<Exception>> errorGathering) {
		return retrieveFromIMDB(parent, new IMDBPlotParser(movie), "plotsummary", movie, errorGathering);
	}

    /**
     * @param movie The movie one wants to get the information for. If this movie does not has any IMDB number this will result in an error.
     * @param errorGathering if Initialised with a Mapping the mapping will contain all occured errors after the method is done. This happens
     * without removing any errors that are allready stored for this movie.
     * if it is initialised with null. The errors will be printed on the command line.
     * @return true if no errors occured
     */
    public static boolean retrieveTitleFromIMDB(final JComponent parent, final Movie movie, final IMDBFeatureOption imdbFeatureOption, final Map<Movie, List<Exception>> errorGathering) {
        return retrieveFromIMDB(parent, new IMDBGenresParser(movie, imdbFeatureOption, false, false, true, false, false, false, false), "", movie, errorGathering);
    }

	/**
	 * @param movie The movie one wants to get the information for. If this movie does not has any IMDB number this will result in an error.
	 * @param errorGathering if Initialised with a Mapping the mapping will contain all occured errors after the method is done. This happens
	 * without removing any errors that are allready stored for this movie.
	 * if it is initialised with null. The errors will be printed on the command line.
	 * @return true if no errors occured
	 */
	public static boolean retrieveGenresFromIMDB(final JComponent parent, final Movie movie, final IMDBFeatureOption imdbFeatureOption, final Map<Movie, List<Exception>> errorGathering) {
		return retrieveFromIMDB(parent, new IMDBGenresParser(movie, imdbFeatureOption, false, true, false, false, false, false, false), "", movie, errorGathering);
	}

    /**
     * @param movie The movie one wants to get the information for. If this movie does not has any IMDB number this will result in an error.
     * @param errorGathering if Initialised with a Mapping the mapping will contain all occured errors after the method is done. This happens
     * without removing any errors that are allready stored for this movie.
     * if it is initialised with null. The errors will be printed on the command line.
     * @return true if no errors occured
     */
    protected static boolean retrieveRuntimeGenresAkasImageAndActorsFromIMDB(final JComponent parent, final Movie movie, final IMDBFeatureOption imdbFeatureOption, final Map<Movie, List<Exception>> errorGathering) {
        return retrieveFromIMDB(parent, new IMDBGenresParser(movie, imdbFeatureOption, false, true, false, true, true, true, true), "", movie, errorGathering);
    }

    /**
     * @param movie The movie one wants to get the information for. If this movie does not has any IMDB number this will result in an error.
     * @param errorGathering if Initialised with a Mapping the mapping will contain all occured errors after the method is done. This happens
     * without removing any errors that are allready stored for this movie.
     * if it is initialised with null. The errors will be printed on the command line.
     * @return true if no errors occured
     */
    public static boolean retrieveYearTitleGenresAkasFromIMDB(final JComponent parent, final Movie movie, final IMDBFeatureOption imdbFeatureOption, final Map<Movie, List<Exception>> errorGathering) {
        return retrieveFromIMDB(parent, new IMDBGenresParser(movie, imdbFeatureOption, true, true, true, true, false, false, false), "", movie, errorGathering);
    }

    /**
	 * @param movie The movie one wants to get the information for. If this movie does not has any IMDB number this will result in an error.
	 * @param errorGathering if Initialised with a Mapping the mapping will contain all occured errors after the method is done. This happens
	 * without removing any errors that are allready stored for this movie.
	 * if it is initialised with null. The errors will be printed on the command line.
	 * @return true if no errors occured
	 */
	public static boolean retrieveYearFromIMDB(final JComponent parent, final Movie movie, final IMDBFeatureOption imdbFeatureOption, final Map<Movie, List<Exception>> errorGathering) {
		return retrieveFromIMDB(parent, new IMDBGenresParser(movie, imdbFeatureOption, true, false, false, false, false, false, false), "", movie, errorGathering);
	}

    /**
     * @param movie The movie one wants to get the information for. If this movie does not has any IMDB number this will result in an error.
     * @param errorGathering if Initialised with a Mapping the mapping will contain all occured errors after the method is done. This happens
     * without removing any errors that are allready stored for this movie.
     * if it is initialised with null. The errors will be printed on the command line.
     * @return true if no errors occured
     */
    public static boolean retrieveRuntimeFromIMDB(final JComponent parent, final Movie movie, final IMDBFeatureOption imdbFeatureOption, final Map<Movie, List<Exception>> errorGathering) {
        return retrieveFromIMDB(parent, new IMDBGenresParser(movie, imdbFeatureOption, false, false, false, false, false, false, true), "", movie, errorGathering);
    }

    /**
     * @param movie The movie one wants to get the information for. If this movie does not has any IMDB number this will result in an error.
     * @param errorGathering if Initialised with a Mapping the mapping will contain all occured errors after the method is done. This happens
     * without removing any errors that are allready stored for this movie.
     * if it is initialised with null. The errors will be printed on the command line.
     * @return true if no errors occured
     */
    public static boolean retrieveAkasFromIMDB(final JComponent parent, final Movie movie, final IMDBFeatureOption imdbFeatureOption, final Map<Movie, List<Exception>> errorGathering) {
        return retrieveFromIMDB(parent, new IMDBGenresParser(movie, imdbFeatureOption, false, false, false, true, false, false, false), "", movie, errorGathering);
    }

    /**
     * @param movie The movie one wants to get the information for. If this movie does not has any IMDB number this will result in an error.
     * @param errorGathering if Initialised with a Mapping the mapping will contain all occured errors after the method is done. This happens
     * without removing any errors that are allready stored for this movie.
     * if it is initialised with null. The errors will be printed on the command line.
     * @return true if no errors occured
     */
    public static boolean retrieveActorsFromIMDB(final JComponent parent, final Movie movie, final IMDBFeatureOption imdbFeatureOption, final Map<Movie, List<Exception>> errorGathering) {
        return retrieveFromIMDB(parent, new IMDBGenresParser(movie, imdbFeatureOption, false, false, false, false, false, true, false), "", movie, errorGathering);
    }

	/**
	 * @param parserCallBack This defines what parser will be uesd.
	 * @param movie The movie one wants to get the information for. If this movie does not has any IMDB number this will result in an error.
	 * @param errorGathering if Initialised with a Mapping the mapping will contain all occured errors after the method is done. This happens
	 * without removing any errors that are allready stored for this movie.
	 * if it is initialised with null. The errors will be printed on the command line.
	 * @return true if no errors occured
	 */
	public static boolean retrieveFromIMDB(final JComponent parent, final HTMLEditorKit.ParserCallback parserCallBack, final String subURL, final Movie movie, final Map<Movie, List<Exception>> errorGathering) {
		Integer num = movie.getFeature(IMDBTitleNumber.class).getInt();
		if (num == null) {
			Exception e = new Exception(Options.getI18N(IMDBTitleSearchParser.class).getString("There is no number."));
			if (errorGathering == null) {
				e.printStackTrace();
				return false;
			}
			if (errorGathering.get(movie) == null)
				errorGathering.put(movie, new Vector<Exception>());
			errorGathering.get(movie).add(e);
			return false;
		}
		try {
            DecimalFormat df = new DecimalFormat("0000000");
//            URL fullURL = new URL("file:/tmp/imdb/akas.imdb.com/title/tt" +  df.format(num) + "/index.html");
			URL fullURL = new URL("http://akas.imdb.com/title/tt" + num + "/" + subURL);
			retrieveFromIMDBFullURL(parent, parserCallBack, fullURL, movie, errorGathering);
			return true;
		
		} catch (MalformedURLException e) {
			if (errorGathering == null) {
				e.printStackTrace();
				return false;
			}
			if (errorGathering.get(movie) == null) {
				errorGathering.put(movie, new Vector<Exception>());
			}
			errorGathering.get(movie).add(e);
		}
		return false;
	}
	
	public static boolean retrieveFromIMDBFullURL(final JComponent parent, final HTMLEditorKit.ParserCallback parserCallBack, final URL fullURL, final Movie movie, final Map<Movie, List<Exception>> errorGathering) {
		try {
//			URL urlPlot = new URL("http://akas.imdb.com/title/tt" + num + "/" + subURL);
			InputStreamReader r = null;
			int tryConnectionCount = 0;
			while (true) {
				try {
/*                    PrintStream encountered = new PrintStream(new FileOutputStream("/tmp/marked", true));
                    encountered.println(fullURL.toString());
                    if (true)
                        return true;*/
                    URLConnection conn = fullURL.openConnection(/*new Proxy(Proxy.Type.HTTP,(new Socket("localhost", 3128)).getRemoteSocketAddress())*/);
                    conn.connect();
					r = new InputStreamReader(new BufferedInputStream(conn.getInputStream() /*fullURL.openStream()*/));
					break; 
				} catch (IOException e) {
                    String msg = e.getMessage();
                    // Error Message 400 indicates wrong imdb number.
                    // therefor there is no need to try it again
                    if (msg.contains("code") && msg.contains(" 400 "))
                        throw e;
                    
                    // As for the rest (espacialy 503)
                    // try it again
					tryConnectionCount++;
					if (tryConnectionCount == 5){
                        if (JOptionPane.showConfirmDialog(parent, 
                                IMDBTitleNumber.transformMessage(e), 
                                Options.getI18N(IMDBTitleNumber.class).getString("Do you want to retry?"), 
                                JOptionPane.ERROR_MESSAGE, JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION)
                            throw e;
                        else 
                            tryConnectionCount = 0;
                    }
				}
			}
			ParserGetter getter = new ParserGetter();
			HTMLEditorKit.Parser parser = getter.getParser();
			parser.parse(r, parserCallBack, true);
			return true;
//		} catch (MalformedURLException e) {
//			if (errorGathering == null) {
//				e.printStackTrace();
//				return false;
//			}
//			if (errorGathering.get(movie) == null) {
//				errorGathering.put(movie, new Vector<Exception>());
//			}
//			errorGathering.get(movie).add(e);
		} catch (IOException e) {
			if (errorGathering == null) {
				e.printStackTrace();
				return false;
			}
			if (errorGathering.get(movie) == null) {
				errorGathering.put(movie, new Vector<Exception>());
			}
			errorGathering.get(movie).add(e);
		}
		return false;
	}
	
	
	public void handleSimpleTag(Tag arg0, MutableAttributeSet arg1, int arg2) {
	}
	public void handleComment(char[] arg0, int arg1) {
	}
	public void handleEndOfLineString(String arg0) {
	}
	public void handleError(String arg0, int arg1) {
	}
	public void handleStartTag(Tag tag, MutableAttributeSet attributes, int position) {
		if ((tag == OL) && (pos == Position.FoundTitleText))
			pos = Position.WithinTitleList;
		if ((tag == A) && (pos == Position.WithinTitleList)) {
			Object valHREF = attributes.getAttribute(HREF);
			if (!(valHREF instanceof String)) {
				System.err.println(Options.getI18N(IMDBTitleNumber.class).getString("The type of the attribute is [TYPE].").replaceAll("\\[TYPE\\]", valHREF.getClass().getSimpleName()));
				System.err.println(Options.getI18N(IMDBTitleNumber.class).getString("But I only know how to handle Strings."));
				number = -1;
			}
			else {
				String strVal = (String) valHREF;
				String split[] = strVal.split("/");
				for (String str : split)
					if (str.startsWith("tt"))
						number = Integer.parseInt(str.substring(2).trim());
			}
			posInMatch = PositionWithInMatchList.WithInTagA;
		}
	}

    public void stopParsing() {
        stopParsing  = true;
    }
}
