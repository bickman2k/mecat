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
 * Created on May 28, 2006
 * @author Stephan Richard Palm
 * 
 */
package net.sourceforge.mecat.catalog.gui.features;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.text.NumberFormat;
import java.util.ResourceBundle;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;

import net.sourceforge.mecat.catalog.filesystem.store.MD5Thread;
import net.sourceforge.mecat.catalog.filesystem.store.ResultFromReality;
import net.sourceforge.mecat.catalog.filesystem.store.ResultFromRealityEvent;
import net.sourceforge.mecat.catalog.filesystem.store.ResultFromRealityListener;
import net.sourceforge.mecat.catalog.medium.Medium;
import net.sourceforge.mecat.catalog.medium.MediumListener;
import net.sourceforge.mecat.catalog.option.LocalListener;
import net.sourceforge.mecat.catalog.option.LocalListenerEvent;
import net.sourceforge.mecat.catalog.option.Options;
import net.sourceforge.mecat.catalog.option.WeakLocalListener;

public class RomFileListProgress extends JFrame implements ResultFromRealityListener, LocalListener {

    class MediumAndLocaleChangeListener implements MediumListener, LocalListener {
        // Invoke the medium name Change at least once
        {
            nameChanged(medium);
        }

        public void nameChanged(Medium medium) {
            setTitle(res.getString("Rom file list creation progress for [MEDIUM].").replaceAll("\\[MEDIUM\\]", medium.toString()));
        }

        public void mediumChanged(Medium medium) {}

        public void stateChanged(LocalListenerEvent event) {
            nameChanged(medium);
        }
    }
    
    
    final static NumberFormat format = NumberFormat.getIntegerInstance();
    
    
    static {
        format.setMaximumFractionDigits(3);
    }
    
    final ResourceBundle res = Options.getI18N(RomFileListProgress.class);

    /**
     * How many files have been found till know
     */
    int files = 0;
    /**
     * How mayn dirs have been found till know
     */
    int dirs = 0;
    /**
     * For how many files the md5 hash has been computed
     */
    int md5Files = 0;
    /**
     * For how many files the tags have been searched
     */
    int findTagFiles = 0;

    /**
     * Button for toggeling between check md5 and don't check.
     * Essentialy this allows the user to skip the rest of the md5 sum computation.
     */
    final JButton md5 = new JButton();
    
    /**
     * Button for toggeling between search tags and don't search.
     * Essentialy this allows the user to skip the rest of the tag seaching.
     */
    final JButton tag = new JButton();
    
    /**
     * Button that allows to stop the process while processing 
     * and to close the windows if the process is finished.
     * The label indicates which one it will do.
     */
    final JButton cancelCloseButton = new JButton();
    boolean close = false;
    
    /**
     * This Panel containts statistics about the ongoing process
     */
    final JPanel stats = new JPanel();
    /*
     * The following Labels containe part of the statitics
     */
    final JLabel dirsPanel = new JLabel();
    
    final JLabel filesPanel = new JLabel();
    final JLabel md5FilesPanel = new JLabel();
    final JLabel findTagFilesPanel = new JLabel();

    final JLabel bytesPanel = new JLabel();
    final JLabel md5BytesPanel = new JLabel();
    /* 
     * The following Labels are just filled
     * with fix labels (only change with the language of the MeCat)
     */
    final JLabel structureLabel = new JLabel();
    final JLabel md5Label = new JLabel();
    final JLabel tagsLabel = new JLabel();
    final JLabel directoriesLabel = new JLabel();
    final JLabel filesLabel = new JLabel();
    final JLabel bytesLabel = new JLabel();
    
    
    /**
     * This editor pane shows a log of special event.
     * Special events are failures, process interuption and the end of the process.
     */
    final JEditorPane log = new JEditorPane();
    final StringBuffer logBuffer = new StringBuffer("<HTML><BODY>");

    /**
     * For this ResultFromReality the progess is shown.
     */
    final ResultFromReality resultFromReality;

    /**
     * Thread for the process. 
     * This allows to force the process to stop.
     */
    Thread t = null;
    
    /**
     * This is essentialy used for the title
     */
    final Medium medium;
    
    final ActionListener cancelActionListener = new ActionListener(){
        public void actionPerformed(ActionEvent arg0) {
            fireCancel();
        }
    };
    final ActionListener closeActionListener = new ActionListener(){
        public void actionPerformed(ActionEvent arg0) {
            close();
        }
    };
    
    
    protected void close() {
//        super.processWindowEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSED));
        setVisible(false);
        dispose();
    }
    
    protected void processWindowEvent(WindowEvent e) {
        if (e.getID() != WindowEvent.WINDOW_CLOSING) 
            super.processWindowEvent(e);
     
    }

    public RomFileListProgress(final ResultFromReality resultFromReality) {
        this(resultFromReality, null);
    }
    
    public RomFileListProgress(final ResultFromReality resultFromReality, final Medium medium) {
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        
        this.resultFromReality = resultFromReality;
        this.medium = medium;
        
        log.setContentType("text/html");

        setLayout(new BorderLayout());
        
        md5.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent arg0) {
                resultFromReality.setCheckMD5(!resultFromReality.isCheckMD5());
                checkMD5Button();
            }
        });
        checkMD5Button();
        tag.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent arg0) {
                resultFromReality.setFindTag(!resultFromReality.isFindTag());
                checkTagButton();
            }
        });
        checkTagButton();
        
        cancelCloseButton.addActionListener(cancelActionListener);
        
        stats.setLayout(new GridLayout(4, 4) {{ setHgap(20); }} );

        structureLabel.setText(res.getString("Structure"));
        md5Label.setText(res.getString("MD5"));
        tagsLabel.setText(res.getString("Tags"));
        directoriesLabel.setText(res.getString("Directories"));
        filesLabel.setText(res.getString("Files"));
        bytesLabel.setText(res.getString("Bytes"));

        stats.add(new JPanel());
        stats.add(structureLabel);
        stats.add(md5Label);
        stats.add(tagsLabel);
        
        stats.add(directoriesLabel);
        stats.add(dirsPanel);
        stats.add(new JPanel());
        stats.add(new JPanel());
        
        stats.add(filesLabel);
        stats.add(filesPanel);
        stats.add(md5FilesPanel);
        stats.add(findTagFilesPanel);

        stats.add(bytesLabel);
        stats.add(bytesPanel);
        stats.add(md5BytesPanel);
        stats.add(new JPanel());

        add(new JPanel(){{add(md5); add(tag); add(stats); add(cancelCloseButton);}}, BorderLayout.NORTH);
        add(new JScrollPane(log));
        
        setResizable(false);
        setSize(new Dimension(800, 600));
        
        // The other case (Medium == null) is consider in setLabels
        if (medium != null) {
            // Keep track of name changes from the medium
            // this allows the user to see which medium
            // is connected to this. And it changes the title in order 
            // to adapt to language changes.
            MediumAndLocaleChangeListener listener = new MediumAndLocaleChangeListener();
            
            medium.addMediumListener(listener);
            Options.addLocalListener(listener);
        }
            
        resultFromReality.addResultFromRealityListener(this);

        Options.addLocalListener(new WeakLocalListener(this));
        // Set labels to the current locale
        setLabels();
    }

    public void stateChanged(LocalListenerEvent event) {
        setLabels();
    }
    
    public void setLabels() {
        checkMD5Button();
        checkTagButton();
        if (close)
            cancelCloseButton.setText(res.getString("Close"));
        else
            cancelCloseButton.setText(res.getString("STOP"));

        structureLabel.setText(res.getString("Structure"));
        md5Label.setText(res.getString("MD5"));
        tagsLabel.setText(res.getString("Tags"));
        directoriesLabel.setText(res.getString("Directories"));
        filesLabel.setText(res.getString("Files"));
        bytesLabel.setText(res.getString("Bytes"));

        if (medium == null)
            setTitle(res.getString("Rom file list creation progress"));
    }

    protected void checkMD5Button() {
        if (resultFromReality.isCheckMD5())
            md5.setText(res.getString("MD5"));
        else
            md5.setText(res.getString("No MD5"));
    }
    
    protected void checkTagButton() {
        if (resultFromReality.isFindTag())
            tag.setText(res.getString("Tags"));
        else
            tag.setText(res.getString("No tags"));
    }
    
    
    public void directoryFound(ResultFromRealityEvent event) {
        dirs++;
        dirsPanel.setText(format.format(dirs));
    }

    public void fileFound(ResultFromRealityEvent event) {
        files++;
        filesPanel.setText(format.format(files));
    }

    public void sizeChanged(ResultFromRealityEvent event) {
        bytesPanel.setText(format.format(event.getPosition()));
    }

    public void md5Started(ResultFromRealityEvent event) {
        // TODO Auto-generated method stub
        
    }

    public void md5Finished(ResultFromRealityEvent event) {
        // TODO Auto-generated method stub
        
    }

    public void md5FileFinished(ResultFromRealityEvent event) {
        md5Files++;
        md5FilesPanel.setText(format.format(md5Files));
    }

    public void md5SizeChanged(ResultFromRealityEvent event) {
        md5BytesPanel.setText(format.format(event.getPosition()));
    }

    public void md5IOException(ResultFromRealityEvent event) {
        addLogEntry(Options.getI18N(MD5Thread.class).getString("Error while computing md5 hash sum for [FILE].").replaceAll("\\[FILE\\]", event.getFile().toString()));
    }

    public void md5Interupted(ResultFromRealityEvent event) {
        addLogEntry(Options.getI18N(MD5Thread.class).getString("MD5 hash computation interupted."));        
    }

    public void finishingStarted(ResultFromRealityEvent event) {
        // TODO Auto-generated method stub
        
    }

    public void finishingSteped(ResultFromRealityEvent event) {
        // TODO Auto-generated method stub
        
    }

    public void finishingFinished(ResultFromRealityEvent event) {
        // TODO Auto-generated method stub
        
    }
    public void resultComputed(ResultFromRealityEvent event) {
        addLogEntry(res.getString("Finished reading file list."));
        md5.setEnabled(false);
        tag.setEnabled(false);
        close = true;
        cancelCloseButton.removeActionListener(cancelActionListener);
        cancelCloseButton.setText(res.getString("Close"));
        cancelCloseButton.addActionListener(closeActionListener);
    }

    public void interupted(ResultFromRealityEvent event) {
        addLogEntry(res.getString("Reading file list interupted."));
        md5.setEnabled(false);
        tag.setEnabled(false);
        close = true;
        cancelCloseButton.removeActionListener(cancelActionListener);
        cancelCloseButton.setText(res.getString("Close"));
        cancelCloseButton.addActionListener(closeActionListener);
    }

    public void logEntry(ResultFromRealityEvent event) {
        addLogEntry(event.getLog());
    }
    
    public void addLogEntry(String str) {
        logBuffer.append(str + "<br>");
        log.setText(logBuffer.toString() + "</BODY></HTML>");
    }
    
/*    Vector<CancelListener> cancelListeners = new Vector<CancelListener>();
    
    public void addCancelListener(CancelListener cancelListener) {
        cancelListeners.add(cancelListener);
    }
    
    public void removeCancelListener(CancelListener cancelListener) {
        cancelListeners.add(cancelListener);
    }*/
    
    protected synchronized void fireCancel() {
/*        for (CancelListener cancelListener : cancelListeners)
            cancelListener.canceled();*/
        Thread thread = new Thread() {
            public void run() {
                if (!t.isAlive())
                    return;
                
                try {
                    resultFromReality.stop();
                    t.join(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (t.isAlive()) {
                    t.interrupt();
                    addLogEntry(res.getString("Reading file list does not react, try to kill."));
                    while (t.isAlive())
                        try {
                            t.join(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    interupted(null);
                }
            }
        };
        thread.start();
    }

    public void start() {
        t = new Thread(resultFromReality);
        t.start();
        setVisible(true);
    }

    public void findTagFileFinished(ResultFromRealityEvent event) {
        findTagFiles++;
        findTagFilesPanel.setText(format.format(findTagFiles));
    }

    public void findTagFinished(ResultFromRealityEvent event) {
        // TODO Auto-generated method stub
        
    }

    public void findTagStarted(ResultFromRealityEvent event) {
        // TODO Auto-generated method stub
        
    }


    
}
