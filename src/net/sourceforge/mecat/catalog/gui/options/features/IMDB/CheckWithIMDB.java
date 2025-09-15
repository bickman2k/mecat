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
 * Created on Mar 23, 2006
 * @author Stephan Richard Palm
 * 
 */
package net.sourceforge.mecat.catalog.gui.options.features.IMDB;

import java.awt.BorderLayout;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;

import net.sourceforge.mecat.catalog.filter.FilterListing;
import net.sourceforge.mecat.catalog.gui.options.features.IMDB.CheckOptionDialog.AlwaysNeverAsk;
import net.sourceforge.mecat.catalog.gui.options.features.IMDB.CheckOptionDialog.AutoOrAsk;
import net.sourceforge.mecat.catalog.gui.options.features.IMDB.CheckOptionDialog.FullEngagement;
import net.sourceforge.mecat.catalog.medium.Listing;
import net.sourceforge.mecat.catalog.medium.Medium;
import net.sourceforge.mecat.catalog.medium.features.impl.Genre;
import net.sourceforge.mecat.catalog.medium.features.impl.IMDBTitleNumber;
import net.sourceforge.mecat.catalog.medium.features.impl.Title;
import net.sourceforge.mecat.catalog.medium.features.impl.Year;
import net.sourceforge.mecat.catalog.medium.hidden.Movie;
import net.sourceforge.mecat.catalog.option.Options;

public class CheckWithIMDB {
    final JComponent parent;
    final Listing listing;
    final ResourceBundle res = Options.getI18N(CheckWithIMDB.class);
    Listing _listing;
    
    final int concurrentThreads = 4;
    
    
    public CheckWithIMDB(final JComponent parent, final Listing listing){
        this.parent = parent;
        this.listing = listing;
    }
    
    final static char[] removeChar =  {
        // Spaces
        ' ', '\t', '\r', '\n', 
        // punctuation marks
        '-', ',', ';', '.', ':', '?', '¿', '!',
        // other glue characters
        '&', '(', ')', '"', '\'', '`' 
        
    };
    
    static protected boolean remove(char c) {
        for (char ch : removeChar)
            if (c == ch)
                return true;
        return false;
    }
    
    static protected String simpleNormalForm(String title) {
        StringBuffer buf = new StringBuffer();
        
        for (char c : title.toCharArray()) 
            if (!remove(c))
                buf.append(c);
        
        return buf.toString();
    }
    
    public static boolean similar(String title1, String title2, Locale l) {
        if (l.equals(Locale.GERMAN))
            return similarGerman(title1, title2);
        
        return simpleNormalForm(title1).equalsIgnoreCase(simpleNormalForm(title2));
    }
    
    static protected boolean similarGerman(String title1, String title2) {
        String sim1 = simpleNormalForm(title1);
        String sim2 = simpleNormalForm(title2);
        
        sim1 = sim1.replaceAll("ß", "ss");
        sim2 = sim2.replaceAll("ß", "ss");
        
        return sim1.equalsIgnoreCase(sim2);
    }


    protected void checkMovie(Movie movie, CheckOptionDialog option,
            Map<Movie, Movie> moviesWrongTitle, Map<Movie, Movie> moviesWrongAkas, 
            Map<Movie, Movie> moviesWrongYear, Map<Movie, Movie> moviesWrongGenres) {
        
        Integer imdbNumber = movie.getFeature(IMDBTitleNumber.class).getInt();
        
        if (imdbNumber == null || imdbNumber == 0)
            return;
        
        Movie imdbMovie = _listing.create(Movie.class);
        
        imdbMovie.getFeature(IMDBTitleNumber.class).set(movie.getFeature(IMDBTitleNumber.class).getInt());
        imdbMovie.getFeature(IMDBTitleNumber.class).getYearTitleGenresAkasFromIMDB(parent);

        if (option.checkTitle() ) {
            String title = movie.getFeature(Title.class).get(Locale.ENGLISH);
            String titleIMDB = imdbMovie.getFeature(Title.class).get(Locale.ENGLISH);
            
            if (title == null) title = "";
            if (titleIMDB == null) titleIMDB = "";
            
            if (option.engageTitleCapitalisation() == FullEngagement.Ignore) {
                if (!similar(title, titleIMDB, Locale.ENGLISH)) 
                    moviesWrongTitle.put(movie, imdbMovie);
            } else
                if (!title.equals(titleIMDB)) 
                    moviesWrongTitle.put(movie, imdbMovie);
        }                                        
        
        if (option.checkAkas() ) {
            Set<Locale> langs = movie.getFeature(Title.class).getLanguages();
            Set<Locale> langsIMDB = imdbMovie.getFeature(Title.class).getLanguages();
            
            // Since ENGLISH is no part of the akas remove it
            langs.remove(Locale.ENGLISH);
            langsIMDB.remove(Locale.ENGLISH);
            
            if (langs.containsAll(langsIMDB) && langsIMDB.containsAll(langs)) {

                for (Locale l : langs) {
                    String title = movie.getFeature(Title.class).get(l);
                    String titleIMDB = imdbMovie.getFeature(Title.class).get(l);
                    
                    if (title == null) title = "";
                    if (titleIMDB == null) titleIMDB = "";
                    
                    if (option.engageAkasCapitalisation() == FullEngagement.Ignore) {
//                        if (!title.equalsIgnoreCase(titleIMDB)) {
                        if (!similar(title, titleIMDB, l)) {
                            moviesWrongAkas.put(movie, imdbMovie);
                            break;
                        }
                    } else
                        if (!title.equals(titleIMDB)) {
                            moviesWrongAkas.put(movie, imdbMovie);
                            break;
                        }
                }
            } else
                moviesWrongAkas.put(movie, imdbMovie);
            
        
        }
        
        if (option.checkYear()) {
            int year = movie.getFeature(Year.class).getInt();
            int yearImdb = imdbMovie.getFeature(Year.class).getInt();
            if (year != yearImdb) 
                moviesWrongYear.put(movie, imdbMovie);
        }
        if (option.checkGenres()) {
            List<String> selection = movie.getFeature(Genre.class).getSelection();
            List<String> selectionIMDB = imdbMovie.getFeature(Genre.class).getSelection();
            
            if (!selection.containsAll(selectionIMDB) || !selectionIMDB.containsAll(selection))
                moviesWrongGenres.put(movie, imdbMovie);
        }
    }
    
    protected void checkTitle(Movie movie, Movie imdb, Locale l, FullEngagement similarities, AlwaysNeverAsk replace, String offset, StringBuffer buf) {
        String orgAka = movie.getFeature(Title.class).get(l);
        String imdbAka = imdb.getFeature(Title.class).get(l);

        if (orgAka == null) orgAka = "";
        if (imdbAka == null) imdbAka = "";

        // If it is equal then return
        if (orgAka.equals(imdbAka)) 
            return;

        // If it is similar and similar will be ignored return 
        if (similarities == FullEngagement.Ignore && similar(orgAka, imdbAka, l)) 
            return;

        String outputName;
        if (l.equals(Locale.ENGLISH))
            outputName = res.getString("title");
        else
            outputName = res.getString("aka for language [language]").replaceAll("\\[language\\]", l.getDisplayLanguage(Options.getCurrentLocale()));

        if (!l.equals(Locale.ENGLISH))
            buf.append(res.getString("Local [title/aka] \"[local name of movie]\" does not match \"[imdb name of movie]\" from the imdb.")
                .replaceAll("\\[title/aka\\]", outputName)
                .replaceAll("\\[local name of movie\\]", "" + movie.getFeature(Title.class).get(l))
                .replaceAll("\\[imdb name of movie\\]", "" + imdb.getFeature(Title.class).get(l)) 
            + "<br>");

        switch (similarities) {
        
        case Fail: 
            break;
            
        case Ignore :
            if (similar(orgAka, imdbAka, l)) {
                // This point will not be reached, 
                // but for consistency I wrote it down anyways.
                buf.append(res.getString("(Trivial difference ignored)") + "<br>");
                return;
            }
            break;
            
        case Ask :
            if (!similar(orgAka, imdbAka, l)) 
                break;
            
            if (JOptionPane.showConfirmDialog(parent, 
                    res.getString("Correct trivial difference [title/aka] from \"[local name of movie]\" to \"[imdb name of movie]\"?")
                            .replaceAll("\\[title/aka\\]", outputName)
                            .replaceAll("\\[local name of movie\\]", "" + movie.getFeature(Title.class).get(l))
                            .replaceAll("\\[imdb name of movie\\]", "" + imdb.getFeature(Title.class).get(l))
                    , res.getString("Proceed \"[name of movie]\" [offset]")
                            .replaceAll("\\[offset\\]", offset)
                            .replaceAll("\\[name of movie\\]", movie.toString())
                    , JOptionPane.YES_NO_OPTION) != JOptionPane.OK_OPTION) {
                buf.append(res.getString("(User denied correction of trivial difference of [title/aka])")
                        .replaceAll("\\[title/aka\\]", outputName)
                        + "<br>");
                break;
            } 
            buf.append(res.getString("(User allowed correction of trivial difference of [title/aka])")
                    .replaceAll("\\[title/aka\\]", outputName)
            + "<br>");
            movie.getFeature(Title.class).set(imdb.getFeature(Title.class).get(l), l);
            // Title is now correct proceed with next aka
            return;
            
        case Auto :
            if (!similar(orgAka, imdbAka, l)) 
                break;
            
            buf.append(res.getString("(Autocorrect trivial difference of [title/aka])")
                    .replaceAll("\\[title/aka\\]", outputName)
            + "<br>");
            movie.getFeature(Title.class).set(imdb.getFeature(Title.class).get(l), l);
            // Title is now correct proceed with next aka
            return;
        }
        
        if (replace == AlwaysNeverAsk.Never) {
            buf.append(res.getString("(Correction of [title/aka] failed)")
                    .replaceAll("\\[title/aka\\]", outputName)
            + "<br>");
            return;
        }

        if (replace == AlwaysNeverAsk.Ask) {
            if (JOptionPane.showConfirmDialog(parent, 
                    res.getString("Replace [title/aka] \"[local name of movie]\" with \"[imdb name of movie]\"?")
                        .replaceAll("\\[title/aka\\]", outputName)
                        .replaceAll("\\[local name of movie\\]", "" + movie.getFeature(Title.class).get(l))
                        .replaceAll("\\[imdb name of movie\\]", "" + imdb.getFeature(Title.class).get(l))
                    , res.getString("Proceed \"[name of movie]\" [offset]")
                            .replaceAll("\\[offset\\]", offset)
                            .replaceAll("\\[name of movie\\]", movie.toString())
                    , JOptionPane.YES_NO_OPTION) != JOptionPane.OK_OPTION) {
                buf.append(res.getString("(User denied replacement of [title/aka])")
                        .replaceAll("\\[title/aka\\]", outputName)
                + "<br>");
                return;
            } else
                buf.append(res.getString("(User allowed replacement of [title/aka])")
                        .replaceAll("\\[title/aka\\]", outputName)
                + "<br>");
        } else
            buf.append(res.getString("(Auto replace [title/aka])")
                    .replaceAll("\\[title/aka\\]", outputName)
            + "<br>");

        movie.getFeature(Title.class).set(imdb.getFeature(Title.class).get(l), l);
    }
    
    public void runCheck() {
        final CheckOptionDialog option = new CheckOptionDialog();
        option.setVisible(true);
        
        if (!option.proceed())
            return;

        final FilterListing filterListing = new FilterListing(listing){

            @Override
            protected boolean eval(Medium medium) {
                return (medium instanceof Movie);
            }

            @Override
            protected boolean filterEnabled() {
                return true;
            }
            
        };
        
        final JDialog dialog = new JDialog();
        final JProgressBar progress = new JProgressBar();
        progress.setMaximum(filterListing.size());
        progress.setStringPainted(true);
        progress.setString("0/" + filterListing.size());
        
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().add(progress);
        dialog.setModal(true);
        dialog.pack();
        dialog.setSize(dialog.getSize().width * 2, dialog.getSize().height * 2);
        
        Thread t = new Thread() {
            public void run(){
                
                // Dummy listing to create new media to fill from imdb
                _listing = new Listing(Options.getSimpleCatalog());

                final Map<Movie, Movie> moviesWrongTitle = new LinkedHashMap<Movie, Movie>();
                final Map<Movie, Movie> moviesWrongAkas = new LinkedHashMap<Movie, Movie>();
                final Map<Movie, Movie> moviesWrongYear = new LinkedHashMap<Movie, Movie>();
                final Map<Movie, Movie> moviesWrongGenres = new LinkedHashMap<Movie, Movie>();
                
                Vector<Thread> threads = new Vector<Thread>();
                
                int i = 0;
                for (Medium medium : filterListing) {
                    // Well, this can not happen
                    // but it looks complete this way
                    if (!(medium instanceof Movie))
                        continue;
                    
                    final Movie movie = (Movie) medium;
 
                    Thread t = new Thread() {
                        public void run() {
                            checkMovie(movie, option, moviesWrongTitle, moviesWrongAkas, moviesWrongYear, moviesWrongGenres);
                        }
                    };
                    t.start();
                    threads.add(t);

                    do {
                        // For all ended threads
                        // - remove them from the list of threads
                        // - increase counter
                        for (Iterator<Thread> j = threads.iterator(); j.hasNext() ; ) {
                            Thread thread = j.next();
                            if (!thread.isAlive()) {
                                i++;
                                progress.setValue(i);
                                progress.setString(i + "/" + filterListing.size());
                                j.remove();
                            }
                        }
                        if (threads.size() >= concurrentThreads) {
                            try {
                                threads.firstElement().join(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    } while (threads.size() >= concurrentThreads);
                }
                while (!threads.isEmpty()) {
                    try {
                        threads.firstElement().join(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    // For all ended threads
                    // - remove them from the list of threads
                    // - increase counter
                    for (Iterator<Thread> j = threads.iterator(); j.hasNext() ; ) {
                        Thread thread = j.next();
                        if (!thread.isAlive()) {
                            i++;
                            progress.setValue(i);
                            progress.setString(i + "/" + filterListing.size());
                            j.remove();
                        }
                    }
                }

            
                dialog.setVisible(false);

                if (moviesWrongTitle.size() > 0 || moviesWrongAkas.size() > 0
                        || moviesWrongYear.size() > 0 || moviesWrongGenres.size() > 0) {
                    StringBuffer buf = new StringBuffer();

                    Set<Movie> allWrong = new LinkedHashSet<Movie>();
                    
                    allWrong.addAll(moviesWrongTitle.keySet());
                    allWrong.addAll(moviesWrongAkas.keySet());
                    allWrong.addAll(moviesWrongYear.keySet());
                    allWrong.addAll(moviesWrongGenres.keySet());
                    
                    
                    i = 0;
                    for (Movie movie : allWrong) {
                        i++;
                        String offset = "(" + i + "/" + allWrong.size() + ")";
                        buf.append("<h3>" + movie.toString() + " (" + i + "/" + allWrong.size() + ")</h3>");
                        Movie imdb = moviesWrongTitle.get(movie);
                        if (imdb != null) {
                            String title = movie.getFeature(Title.class).get(Locale.ENGLISH);
                            String titleIMDB = imdb.getFeature(Title.class).get(Locale.ENGLISH);
                            
                            if (title == null) title = "";
                            if (titleIMDB == null) titleIMDB = "";

                            buf.append(res.getString("Local [title/aka] \"[local name of movie]\" does not match \"[imdb name of movie]\" from the imdb.")
                                    .replaceAll("\\[title/aka\\]", res.getString("title"))
                                    .replaceAll("\\[local name of movie\\]", "" + movie.getFeature(Title.class).get(Locale.ENGLISH))
                                    .replaceAll("\\[imdb name of movie\\]", "" + imdb.getFeature(Title.class).get(Locale.ENGLISH))
                                + "<br>");
                            
                            if (title.equals(""))
                                for (Locale l : movie.getFeature(Title.class).getLanguages()) {
                                    // No reason to look for the right title in english
                                    if (l.equals(Locale.ENGLISH))
                                        continue;

                                    // Get title for alternative language
                                    String _title = movie.getFeature(Title.class).get(l);
                                    // _title should not be null but just to be sure
                                    if (_title == null) 
                                        continue;

                                    if (similar(_title, titleIMDB, Locale.ENGLISH)) {
                                        // If fail is selected for trivial differences
                                        // then do fail here.
                                        if (option.engageTitleCapitalisation() == FullEngagement.Fail)
                                            continue;                                        
                                        
                                        buf.append("(" + l.getDisplayLanguage(Options.getCurrentLocale()) + ") " + _title + " == " + titleIMDB);
                                        buf.append("<br>");
                                        if (option.correctTitleLocale()) {
                                            if (option.engageTitleLocale() == AutoOrAsk.Ask) {
                                                if (JOptionPane.showConfirmDialog(parent, 
                                                     res.getString("Change language of ([language]) [name of movie] to english?")
                                                        .replaceAll("\\[language\\]", l.getDisplayLanguage(Options.getCurrentLocale()))
                                                        .replaceAll("\\[name of movie\\]", _title)
                                                     , res.getString("Proceed \"[name of movie]\" [offset]")
                                                        .replaceAll("\\[offset\\]", offset)
                                                        .replaceAll("\\[name of movie\\]", movie.toString())
                                                     , JOptionPane.YES_NO_OPTION) != JOptionPane.OK_OPTION) {
                                                    buf.append(res.getString("(User denied language correction to english)") + "<br>");
                                                    continue;
                                                } else
                                                    buf.append(res.getString("(User allowed language correction to english)") + "<br>");
                                            } else
                                                buf.append(res.getString("(Autocorrect language to english)") + "<br>");
                                            
                                            movie.getFeature(Title.class).set(movie.getFeature(Title.class).get(l), Locale.ENGLISH);
                                            movie.getFeature(Title.class).set(null, l);
                                            title = _title;
                                            break;
                                        }
                                    }
                                }
                            CheckWithIMDB.this.checkTitle(movie, imdb, Locale.ENGLISH, option.engageTitleCapitalisation(), option.engageReplaceTitle(), offset, buf);

                        }
                        imdb = moviesWrongAkas.get(movie);
                        if (imdb != null) {
                            Set<Locale> langs = movie.getFeature(Title.class).getLanguages();
                            Set<Locale> langsIMDB = imdb.getFeature(Title.class).getLanguages();

                            // Since ENGLISH is no part of the akas remove it
                            langs.remove(Locale.ENGLISH);
                            langsIMDB.remove(Locale.ENGLISH);

                            // Those that are exclusive to original
                            Set<Locale> onlyOriginal = new LinkedHashSet<Locale>(langs);
                            onlyOriginal.removeAll(langsIMDB);

                            // Those that are exclusive to imdb
                            Set<Locale> onlyIMDB = new LinkedHashSet<Locale>(langsIMDB);
                            onlyIMDB.removeAll(langs);
                            
                            // Intersection between both
                            Set<Locale> intersection = new LinkedHashSet<Locale>(langs);
                            intersection.retainAll(langsIMDB);
                            
                            for (Locale l : onlyOriginal) {
                                buf.append(
                                    res.getString("[title/aka] \"[local name of movie]\" onyl exists for the local entry.")
                                        .replaceAll("\\[title/aka\\]", res.getString("aka for language [language]").replaceAll("\\[language\\]", l.getDisplayLanguage(Options.getCurrentLocale())))
                                        .replaceAll("\\[local name of movie\\]", "" + movie.getFeature(Title.class).get(l))
                                    + "<br>");

                                if (option.engageRemoveAkas() == AlwaysNeverAsk.Never) 
                                    continue;

                                if (option.engageRemoveAkas() == AlwaysNeverAsk.Ask) {
                                    if (JOptionPane.showConfirmDialog(parent, 
                                         res.getString("Remove [title/aka] \"[local name of movie]\"?")
                                             .replaceAll("\\[title/aka\\]", res.getString("aka for language [language]").replaceAll("\\[language\\]", l.getDisplayLanguage(Options.getCurrentLocale())))
                                             .replaceAll("\\[local name of movie\\]", "" + movie.getFeature(Title.class).get(l))
                                         , res.getString("Proceed \"[name of movie]\" [offset]")
                                             .replaceAll("\\[offset\\]", offset)
                                             .replaceAll("\\[name of movie\\]", movie.toString())
                                         , JOptionPane.YES_NO_OPTION) != JOptionPane.OK_OPTION) {
                                        buf.append(res.getString("(User denied removale of aka)") + "<br>");
                                        continue;
                                    } else
                                        buf.append(res.getString("(User allowed removale of aka)") + "<br>");
                                } else
                                    buf.append(res.getString("(Auto remove aka)") + "<br>");
                                movie.getFeature(Title.class).set(null, l);
                            }

                            for (Locale l : onlyIMDB) {
                                buf.append(
                                    res.getString("[title/aka] \"[imdb name of movie]\" onyl exists in imdb.")
                                        .replaceAll("\\[title/aka\\]", res.getString("Aka for language [language]").replaceAll("\\[language\\]", l.getDisplayLanguage(Options.getCurrentLocale())))
                                        .replaceAll("\\[imdb name of movie\\]", "" + imdb.getFeature(Title.class).get(l))
                                    + "<br>");

                                if (option.engageAddAkas() == AlwaysNeverAsk.Never) 
                                    continue;

                                if (option.engageAddAkas() == AlwaysNeverAsk.Ask) {
                                    if (JOptionPane.showConfirmDialog(parent, 
                                         res.getString("Add missing [title/aka] \"[imdb name of movie]\"?")
                                             .replaceAll("\\[title/aka\\]", res.getString("Aka for language [language]").replaceAll("\\[language\\]", l.getDisplayLanguage(Options.getCurrentLocale())))
                                             .replaceAll("\\[imdb name of movie\\]", "" + imdb.getFeature(Title.class).get(l))
                                         , res.getString("Proceed \"[name of movie]\" [offset]")
                                            .replaceAll("\\[offset\\]", offset)
                                            .replaceAll("\\[name of movie\\]", movie.toString())
                                         , JOptionPane.YES_NO_OPTION) != JOptionPane.OK_OPTION) {
                                        buf.append(res.getString("(User denied addition of aka)") + "<br>");
                                        continue;
                                    } else
                                        buf.append(res.getString("(User allowed addition of aka)") + "<br>");
                                } else
                                    buf.append(res.getString("(Auto add aka)") + "<br>");
                                movie.getFeature(Title.class).set(imdb.getFeature(Title.class).get(l), l);
                            }

                            for (Locale l : intersection) 
                                CheckWithIMDB.this.checkTitle(movie, imdb, l, option.engageAkasCapitalisation(), option.engageReplaceAkas(), offset, buf);
                            
                        }
                        imdb = moviesWrongYear.get(movie);
                        if (imdb != null) {
                            buf.append(
                                    res.getString("Local year \"[local year]\" is different then \"[imdb year]\" from the imdb.")
                                        .replaceAll("\\[local year\\]", "" + movie.getFeature(Year.class).get())
                                        .replaceAll("\\[imdb year\\]", "" + imdb.getFeature(Year.class).get())
                                    + "<br>");

                            if (option.engageYear() == AlwaysNeverAsk.Never) {
                                buf.append(res.getString("(Did not change year)") + "<br>");
                                continue;
                            }

                            if (option.engageYear() == AlwaysNeverAsk.Ask) {
                                if (JOptionPane.showConfirmDialog(parent, 
                                    res.getString("Replace year \"[local year]\" with \"[imdb year]\" from imdb?")
                                        .replaceAll("\\[local year\\]", "" + movie.getFeature(Year.class).get())
                                        .replaceAll("\\[imdb year\\]", "" + imdb.getFeature(Year.class).get())
                                    , res.getString("Proceed \"[name of movie]\" [offset]")
                                       .replaceAll("\\[offset\\]", offset)
                                       .replaceAll("\\[name of movie\\]", movie.toString())
                                    , JOptionPane.YES_NO_OPTION) != JOptionPane.OK_OPTION) {
                                    buf.append(res.getString("(User denied replacement of year)") + "<br>");
                                    continue;
                                } else
                                    buf.append(res.getString("(User allowed replacement of year)") + "<br>");
                            } else
                                buf.append(res.getString("(Autocorrect year)") + "<br>");

                            movie.getFeature(Year.class).set(imdb.getFeature(Year.class).get());
                        }
                        imdb = moviesWrongGenres.get(movie);
                        if (imdb != null) {
                            buf.append(
                                res.getString("Local genres \"[local genres]\" are different then \"[imdb genres]\" from the imdb.")
                                    .replaceAll("\\[local genres\\]", "" + movie.getFeature(Genre.class).getShortTextHTML())
                                    .replaceAll("\\[imdb genres\\]", "" + imdb.getFeature(Genre.class).getShortTextHTML())
                                + "<br>");

                            if (option.engageGenres() == AlwaysNeverAsk.Never) {
                                buf.append(res.getString("(Did not change genres)") + "<br>");
                                continue;
                            }

                            if (option.engageGenres() == AlwaysNeverAsk.Ask) {
                                if (JOptionPane.showConfirmDialog(parent, 
                                     res.getString("Replace genres \"[local genres]\" with \"[imdb genres]\" from imdb?")
                                         .replaceAll("\\[local genres\\]", "" + movie.getFeature(Genre.class).getShortTextHTML())
                                         .replaceAll("\\[imdb genres\\]", "" + imdb.getFeature(Genre.class).getShortTextHTML())
                                     , res.getString("Proceed \"[name of movie]\" [offset]")
                                         .replaceAll("\\[offset\\]", offset)
                                         .replaceAll("\\[name of movie\\]", movie.toString())
                                    , JOptionPane.YES_NO_OPTION) != JOptionPane.OK_OPTION) {
                                    buf.append(res.getString("(User denied replacement of genres)") + "<br>");
                                    continue;
                                } else
                                    buf.append(res.getString("(User allowed replacement of genres)") + "<br>");
                            } else
                                buf.append(res.getString("(Autocorrect genres)") + "<br>");
                            
                            movie.getFeature(Genre.class).clear();
                            for (String str : imdb.getFeature(Genre.class).getSelection())
                                movie.getFeature(Genre.class).add(str);
                        }
                        buf.append("<br>");
                    }

                    JDialog dialog = new JDialog();
                    JLabel txt = new JLabel("<html>" + buf.toString() + "</html>");
                    dialog.setLayout(new BorderLayout());
                    dialog.getContentPane().add(new JScrollPane(txt));
                    dialog.pack();
                    dialog.setSize(640, 480);
                    dialog.setTitle(res.getString("Found differences."));
                    dialog.setVisible(true);
                } else
                    JOptionPane.showMessageDialog(parent, new JLabel("<html>" + res.getString("No differences.") + "<br>" + res.getString("No differences from the IMDB where found.") + "</html>"), res.getString("No differences."), JOptionPane.INFORMATION_MESSAGE);
            }
        };
        t.start();

        dialog.setVisible(true);
    }
    
}
