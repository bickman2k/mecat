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
 * Created on May 24, 2006
 * @author Stephan Richard Palm
 * 
 */
package net.sourceforge.mecat.catalog.medium.features.impl;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import net.sourceforge.mecat.catalog.datamanagement.Entry;
import net.sourceforge.mecat.catalog.filesystem.Result;
import net.sourceforge.mecat.catalog.filesystem.load.FileSystemLoadShallow;
import net.sourceforge.mecat.catalog.filesystem.load.VersionException;
import net.sourceforge.mecat.catalog.filesystem.store.FileSystemStoreShallow;
import net.sourceforge.mecat.catalog.filesystem.store.ResultFromReality;
import net.sourceforge.mecat.catalog.filesystem.store.ResultFromRealityEvent;
import net.sourceforge.mecat.catalog.filesystem.store.ResultFromRealityListener;
import net.sourceforge.mecat.catalog.gui.features.RomFileListProgress;
import net.sourceforge.mecat.catalog.gui.features.RomFileListFeaturePanel.FinishReadingFileList;
import net.sourceforge.mecat.catalog.gui.options.features.RomFileListFeatureOptionPanel;
import net.sourceforge.mecat.catalog.i18n.util.SimpleLocalTitledBorder;
import net.sourceforge.mecat.catalog.medium.features.Feature;
import net.sourceforge.mecat.catalog.medium.features.option.FeatureOption;
import net.sourceforge.mecat.catalog.medium.features.option.impl.RomFileListFeatureOption;
import net.sourceforge.mecat.catalog.medium.hidden.Rom;
import net.sourceforge.mecat.catalog.option.Options;

public class ImplRomFileList extends AbstractRomFileList {


    public ImplRomFileList(Rom rom) {
        super(rom);
    }
    
    @Override
    public JPanel getFeatureOptionPanel(FeatureOption featureOption) {
        if (!(featureOption instanceof RomFileListFeatureOption))
            return null;

        RomFileListFeatureOption romFileListFeatureOption = ( RomFileListFeatureOption ) featureOption;

        JPanel panel = new RomFileListFeatureOptionPanel(romFileListFeatureOption);

        if (panel == null)
            return null;
        
        panel.setBorder(new SimpleLocalTitledBorder(Options.getI18N(ImplRomFileList.class), "Rom file list storage option"));

        return panel;
    }

/*    @Override
    public boolean hasOptions() {
        return true;
    }
    
    @Override
    public JPanel getOptionPanel() {
        return new RomFileListFeatureOptionPanel((RomFileListFeatureOption)getFeatureOption());
    }*/

    @Override
    public void copyTo(Feature feature) {
        // TODO Copy FileSystemFile

    }

    public File getFileListFile() {
        RomFileListFeatureOption romFileListFeatureOption = (RomFileListFeatureOption)getFeatureOption();
        URL location = romFileListFeatureOption.getDirLocation();
        File file = new File(location.getFile().toString() + System.getProperty("file.separator") + this.medium.getFeature(Ident.class).getUUID().toString() + System.getProperty("file.separator") + "RomFileList.rfl");
        return file;
    }
    
    public boolean hasResult() {
        File file = getFileListFile();
        if (!file.exists() || !file.isFile())
            return false;
        return true;
    }

    public Result getResult() {
        File file = getFileListFile();
        if (!file.exists() || !file.isFile())
            return null;
        try {
            return FileSystemLoadShallow.load(file);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (VersionException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void removeResult() {
        File file = getFileListFile();
        if (!file.exists())
            return;
        file.delete();
    }

    protected void setPath(String path){
        
        Entry option = Options.persistent.getOption("ImplRomFileList");
        if (option == null)
            option = Options.persistent.createOption("ImplRomFileList");

        option.setAttribute("Path", path);
    }
    
    protected String getPath() {
        Entry option = Options.persistent.getOption("ImplRomFileList");

        // If the option entry does not exist then use home folder
        if (option == null)
            return System.getProperty("user.home");

        // If the attribute has not yet been set then use home folder
        String path = option.getAttribute("Path");
        if (path == null)
            return System.getProperty("user.home");
         
        return path;
    }

    public void readFileList(JComponent parent, final FinishReadingFileList finish) {
        JFileChooser choose = new JFileChooser(getPath());
        choose.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int returnVal = choose.showOpenDialog(parent);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
            setPath(choose.getCurrentDirectory().toString());

            final File file = getFileListFile();
            if (!Options.ensureDirectory(file.getParent()))
                JOptionPane.showMessageDialog(parent, Options.getI18N(Options.class).getString("CouldNotCreateDir").replaceAll("\\[DIR\\]", file.getPath()), 
                        Options.getI18N(Options.class).getString("CouldNotCreateDir").replaceAll("\\[DIR\\]", file.getPath()), JOptionPane.ERROR_MESSAGE);

            final ResultFromReality resultFromReality = new ResultFromReality(choose.getSelectedFile(), getMedium().getClass().getSimpleName());
            resultFromReality.setCheckMD5(((RomFileListFeatureOption) getFeatureOption()).isAquireMD5Sum());
            final RomFileListProgress progress = new RomFileListProgress(resultFromReality, getMedium());
            
            resultFromReality.addResultFromRealityListener(new ResultFromRealityListener(){
                public void resultComputed(ResultFromRealityEvent event) {
                    Result result = resultFromReality.getResult();
                    FileSystemStoreShallow.store(file, result);

                    finish.regularFinished(resultFromReality.getResult());
                }
                public void directoryFound(ResultFromRealityEvent event) {}
                public void fileFound(ResultFromRealityEvent event) {}
                public void sizeChanged(ResultFromRealityEvent event) {}
                public void finishingStarted(ResultFromRealityEvent event) {}
                public void finishingSteped(ResultFromRealityEvent event) {}
                public void finishingFinished(ResultFromRealityEvent event) {}
                public void logEntry(ResultFromRealityEvent event) {}
                public void interupted(ResultFromRealityEvent event) {}
                public void md5Started(ResultFromRealityEvent event) {}
                public void md5Finished(ResultFromRealityEvent event) {}
                public void md5FileFinished(ResultFromRealityEvent event) {}
                public void md5SizeChanged(ResultFromRealityEvent event) {}
                public void md5IOException(ResultFromRealityEvent event) {}
                public void md5Interupted(ResultFromRealityEvent event) {}
                public void findTagFileFinished(ResultFromRealityEvent event) {}
                public void findTagFinished(ResultFromRealityEvent event) {}
                public void findTagStarted(ResultFromRealityEvent event) {}
            });
            
            progress.start();
        }
    }

    public boolean isRemovable() {
        return true;
    }
}
