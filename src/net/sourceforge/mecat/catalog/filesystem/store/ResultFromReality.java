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
 * Created on May 27, 2006
 * @author Stephan Richard Palm
 * 
 */
package net.sourceforge.mecat.catalog.filesystem.store;

import java.io.File;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;

import net.sourceforge.mecat.catalog.filesystem.Comparators;
import net.sourceforge.mecat.catalog.filesystem.DetailList;
import net.sourceforge.mecat.catalog.filesystem.ModifiableDirectoryEntry;
import net.sourceforge.mecat.catalog.filesystem.ModifiableFileEntry;
import net.sourceforge.mecat.catalog.filesystem.ModifiableResult;
import net.sourceforge.mecat.catalog.filesystem.mime.Tag;
import net.sourceforge.mecat.catalog.filesystem.mime.TagUtils;
import net.sourceforge.mecat.catalog.option.Options;

public class ResultFromReality implements Runnable{

    final File dir;
    final DirectoryEntry parent;
    final String overrideRootName;
    boolean checkMD5 = true;
    boolean findTag = true;
    boolean stop = false;
    
    long sizeAllFiles = 0;
    long md5Position = 0;

    MD5Thread md5Thread = null;
    TagUtils tagUtils = new TagUtils();
    

    Result result;
    
    public ResultFromReality(File dir) {
        this(dir, null, null);
    }

    public ResultFromReality(File dir, String overrideRootName) {
        this(dir, null, overrideRootName);
    }
    
    public ResultFromReality(File dir, DirectoryEntry parent, String overrideRootName) {
        this.dir = dir;
        this.parent = parent;
        this.overrideRootName = overrideRootName;
/*        MD5Thread md5Thread;
        try {
            md5Thread = new MD5Thread();
        } catch (NoSuchAlgorithmException e) {
            md5Thread = null;
            e.printStackTrace();
        }*/
    }
    
    
    
    public boolean isCheckMD5() {
        return checkMD5;
    }

    public void setCheckMD5(boolean checkMD5) {
        this.checkMD5 = checkMD5;
        MD5Thread md5 = md5Thread;
        if (!checkMD5 && md5 != null)
            md5.stop = true;
            
    }

    public boolean isFindTag() {
        return findTag;
    }

    public void setFindTag(boolean findTag) {
        this.findTag = findTag;
    }


    
/*    public long getPosition() {
        return sizeAllFiles;// + md5Position;
    }*/
    
    public synchronized void run() {
        if (this.result != null)
            return;
            
        Result result = fromReality(dir, parent, overrideRootName);
        
        if (stop) {
            fireInterupted();
            return;
        }

        if (checkMD5)
            try {
                getMD5Sums(result);
            } catch (NoSuchAlgorithmException e) {
                fireLogEntry(e.getLocalizedMessage());
            }
        
        if (findTag)
            findTags(result);
            
        if (stop) {
            fireInterupted();
            return;
        }

        finish(result);
        this.result = result;
        
        if (stop) {
            fireInterupted();
            return;
        }
        
        fireResultComputed();
    }

    protected void findTags(Result result) {
        fireFindTagStarted();

        for (ModifiableFileEntry file : result.files) {
//            file.setDetails(findTagThread.findTag(((FileEntry)file).getOriginal()));
            List<Tag> tag = tagUtils.findTags(((FileEntry)file).getOriginal());
            if (tag.size() == 1)
                file.setDetail(tag.get(0));
            else if (tag.size() > 1) {
                DetailList detailList = new DetailList(tag);
                file.setDetail(detailList);
            }
            
            fireFindTagFileFinished();
            if (!findTag || stop)
                break;
        }
        
        if (findTag && !stop)
            fireFindTagFinished();
    }

    protected void getMD5Sums(Result result) throws NoSuchAlgorithmException {
        fireMD5Started();

        md5Thread = new MD5Thread();
        md5Thread.addMD5ThreadListener(new MD5ThreadListener(){

            public void sizeChanged(MD5ThreadEvent event) {
                fireMD5SizeChanged(event, md5Position);
            }

            public void ioException(MD5ThreadEvent event) {
                fireMD5IOException(event);
            }

            public void interupted(MD5ThreadEvent event) {
                fireMD5Interupted(event);
            }
            
        });
        md5Thread.start();

        for (ModifiableFileEntry file : result.files) {
            file.setMD5SUM(md5Thread.getMD5SUM(((FileEntry)file).getOriginal()));
            fireMD5FileFinished();
            md5Position += file.getSize();
            fireMD5SizeChanged(null, md5Position);
            if (!checkMD5 || stop)
                break;
        }
        
        if (Options.DEBUG) {
            fireLogEntry("IO: " + md5Thread.getIO_TIME());
            fireLogEntry("MD5: " + md5Thread.getMD5_TIME());
            fireLogEntry("EVENT: " + md5Thread.getEVENT_TIME());
            fireLogEntry("BUFFER: " + md5Thread.maxBufferUsed());
        }

        md5Thread.stop = true;
        md5Thread = null;
        
        if (checkMD5 && !stop)
            fireMD5Finished();
    }
    
/*    public long getMD5Position() {
        return md5Position;
    }*/
    
    protected Result fromReality(File dir, DirectoryEntry parent, String overrideRootName) {
        DirectoryEntry directory;
        if (parent == null && overrideRootName != null)
            directory = new DirectoryEntry(overrideRootName, parent);
        else
            directory = new DirectoryEntry(dir.getName(), parent);

        Vector<ModifiableDirectoryEntry> dirs = new Vector<ModifiableDirectoryEntry>();
        Vector<ModifiableFileEntry> files = new Vector<ModifiableFileEntry>();
    
        fireDirectoryFound();
        dirs.add(directory);
        
        File[] allFiles = dir.listFiles();
        Vector<File> subDirs = new Vector<File>(allFiles.length);
        Vector<File> _files = new Vector<File>(allFiles.length);
        
        for (File file : allFiles)
            if (file.isDirectory())
                subDirs.add(file);
            else
                _files.add(file);
//        File[] subDirs = dir.listFiles(Options.dirFilter);
//        File[] _files =  dir.listFiles(Options.fileFilter);

        Collections.sort(_files, new Comparator<File>(){
            public int compare(File file0, File file1) {
                return file0.getName().compareToIgnoreCase(file1.getName());
            }
            
        });
        Collections.sort(subDirs, new Comparator<File>(){
            public int compare(File file0, File file1) {
                return file0.getName().compareToIgnoreCase(file1.getName());
            }
            
        });
        
        // Get list of all files in this directory
        if (_files != null) {
            for (File file : _files) {
                if (stop)
                    return null;

                fireFileFound();
                FileEntry f = new FileEntry(file, directory);
                
                if (!file.exists())
                    System.err.println("[Phase 1] File does not exist " + file.toString());

                if (stop)
                    return null;
                
//                md5Position = 0;
                sizeAllFiles += file.length();
                fireSizeChanged(sizeAllFiles);

                files.add(f);
            }
            for (int i = 0; i < files.size() - 1; i++)
                files.get(i).setNextSibling(files.get(i + 1));
            if (!files.isEmpty())
                directory.setFirstFileChild(files.firstElement());
            directory.setNumFiles(_files.size());
        } else
            directory.setNumFiles(0);
        
        

        // Get the sub directories with all files
        if (subDirs != null) {
            ModifiableDirectoryEntry last = null;
            for (int i = 0; i < subDirs.size(); i++){
                
                if (stop)
                    return null;

                // Rekursive call for sub directories
                Result subResult = fromReality(subDirs.get(i), directory, overrideRootName);

                if (stop)
                    return null;

                // Make links from this directory to child directories
                if (i == 0)
                    directory.setFirstDirectoryChild(subResult.getDirectory());
                // and between child directories
                if (last != null)
                    last.setNextSibling(subResult.getDirectory());
                last = subResult.getDirectory();

                // Add subdirectories and files to the list of all files
                dirs.addAll(subResult.getDirs());
                files.addAll(subResult.getFiles());
            }
            directory.setNumDirs(subDirs.size());
        } else
            directory.setNumDirs(0);
        
        
        return new Result(directory, dirs, files);
    }
    
    /**
     * Returns the Result for the parameters given with the constructor.
     * Returns null as long as the result has not yet been computed.
     * @return
     */
    public Result getResult() {
        return result;
    }

    /**
     * Converts a raw result into a full fledged result with links and 
     * orders all over the place
     * @param result
     */
    public void finish(ModifiableResult result)
    {
        fireFinishingStarted();
        
        if (result.getFiles().isEmpty())
            return;
        
        Vector<ModifiableFileEntry> fileSize = new Vector<ModifiableFileEntry>(result.getFiles());
        Vector<ModifiableFileEntry> fileAlph = new Vector<ModifiableFileEntry>(result.getFiles());
        Vector<ModifiableFileEntry> fileDate = new Vector<ModifiableFileEntry>(result.getFiles());
        Vector<ModifiableFileEntry> fileMD5 = new Vector<ModifiableFileEntry>(result.getFiles());
        
        // Sort the files
        Collections.sort(fileSize, Comparators.SizeComparator);
        
        fireFinishingSteped(1);
        
        Collections.sort(fileAlph, Comparators.NameComparator);
        
        fireFinishingSteped(2);
        
        Collections.sort(fileDate, Comparators.DateComparator);
        
        fireFinishingSteped(3);
        
        Collections.sort(fileMD5, Comparators.MD5Comparator);
        
        fireFinishingSteped(4);
        
        // Store ordering
        for (int i = 0; i < fileSize.size() - 1 ; i++) 
            fileSize.get(i).setNextSize(fileSize.get(i + 1));

        for (int i = 0; i < fileAlph.size() - 1 ; i++) 
            fileAlph.get(i).setNextAlph(fileAlph.get(i + 1));

        for (int i = 0; i < fileDate.size() - 1 ; i++) 
            fileDate.get(i).setNextDate(fileDate.get(i + 1));

        for (int i = 0; i < fileMD5.size() - 1 ; i++) 
            fileMD5.get(i).setNextMD5(fileMD5.get(i + 1));

        result.setFirstAlph(fileAlph.firstElement());
        result.setFirstDate(fileDate.firstElement());
        result.setFirstSize(fileSize.firstElement());
        result.setFirstMD5(fileMD5.firstElement());
        
        fireFinishingFinished();
    }
    
    Vector<ResultFromRealityListener> resultFromRealityListeners = new Vector<ResultFromRealityListener>();
    
    public void addResultFromRealityListener(ResultFromRealityListener resultFromRealityListener) {
        resultFromRealityListeners.add(resultFromRealityListener);
    }
    
    public void removeResultFromRealityListener(ResultFromRealityListener resultFromRealityListener) {
        resultFromRealityListeners.remove(resultFromRealityListener);
    }
    
    protected void fireDirectoryFound() {
        for (ResultFromRealityListener resultFromRealityListener : resultFromRealityListeners)
            resultFromRealityListener.directoryFound(new ResultFromRealityEvent(ResultFromRealityEvent.ResultFromRealityEventType.DirectoryFound));
    }
    
    protected void fireFileFound() {
        for (ResultFromRealityListener resultFromRealityListener : resultFromRealityListeners)
            resultFromRealityListener.fileFound(new ResultFromRealityEvent(ResultFromRealityEvent.ResultFromRealityEventType.FileFound));
    }
    
    protected void fireSizeChanged(long size) {
        for (ResultFromRealityListener resultFromRealityListener : resultFromRealityListeners)
            resultFromRealityListener.sizeChanged(new ResultFromRealityEvent(ResultFromRealityEvent.ResultFromRealityEventType.SizeChanged, size));
    }
    
    protected void fireMD5FileFinished() {
        for (ResultFromRealityListener resultFromRealityListener : resultFromRealityListeners)
            resultFromRealityListener.md5FileFinished(new ResultFromRealityEvent(ResultFromRealityEvent.ResultFromRealityEventType.MD5FileFinished));
    }

    
    protected void fireMD5SizeChanged(MD5ThreadEvent event, long size) {
        for (ResultFromRealityListener resultFromRealityListener : resultFromRealityListeners)
            resultFromRealityListener.md5SizeChanged(new ResultFromRealityEvent(event, size));
    }
    
    protected void fireMD5Interupted(MD5ThreadEvent event) {
        for (ResultFromRealityListener resultFromRealityListener : resultFromRealityListeners)
            resultFromRealityListener.md5Interupted(new ResultFromRealityEvent(event));
    }
    
    protected void fireMD5IOException(MD5ThreadEvent event) {
        for (ResultFromRealityListener resultFromRealityListener : resultFromRealityListeners)
            resultFromRealityListener.md5Interupted(new ResultFromRealityEvent(event));
    }
    
    

    protected void fireFindTagStarted() {
        for (ResultFromRealityListener resultFromRealityListener : resultFromRealityListeners)
            resultFromRealityListener.findTagStarted(new ResultFromRealityEvent(ResultFromRealityEvent.ResultFromRealityEventType.FindTagStarted));
    }
    protected void fireFindTagFileFinished() {
        for (ResultFromRealityListener resultFromRealityListener : resultFromRealityListeners)
            resultFromRealityListener.findTagFileFinished(new ResultFromRealityEvent(ResultFromRealityEvent.ResultFromRealityEventType.FindTagFileFinished));
    }
    protected void fireFindTagFinished() {
        for (ResultFromRealityListener resultFromRealityListener : resultFromRealityListeners)
            resultFromRealityListener.findTagFinished(new ResultFromRealityEvent(ResultFromRealityEvent.ResultFromRealityEventType.FindTagFinished));
    }
    
    
    protected void fireMD5Started() {
        for (ResultFromRealityListener resultFromRealityListener : resultFromRealityListeners)
            resultFromRealityListener.md5Started(new ResultFromRealityEvent(ResultFromRealityEvent.ResultFromRealityEventType.MD5Started));
    }
    protected void fireMD5Finished() {
        for (ResultFromRealityListener resultFromRealityListener : resultFromRealityListeners)
            resultFromRealityListener.md5Finished(new ResultFromRealityEvent(ResultFromRealityEvent.ResultFromRealityEventType.MD5Finished));
    }

    protected void fireFinishingStarted() {
        for (ResultFromRealityListener resultFromRealityListener : resultFromRealityListeners)
            resultFromRealityListener.finishingStarted(new ResultFromRealityEvent(ResultFromRealityEvent.ResultFromRealityEventType.FinishingStarted, 0, 5));
    }
    protected void fireFinishingSteped(int finishingPosition) {
        for (ResultFromRealityListener resultFromRealityListener : resultFromRealityListeners)
            resultFromRealityListener.finishingSteped(new ResultFromRealityEvent(ResultFromRealityEvent.ResultFromRealityEventType.FinishingSteped, finishingPosition, 5));
    }
    protected void fireFinishingFinished() {
        for (ResultFromRealityListener resultFromRealityListener : resultFromRealityListeners)
            resultFromRealityListener.finishingFinished(new ResultFromRealityEvent(ResultFromRealityEvent.ResultFromRealityEventType.FinishingFinished, 5, 5));
    }

    protected void fireResultComputed() {
        for (ResultFromRealityListener resultFromRealityListener : resultFromRealityListeners)
            resultFromRealityListener.resultComputed(new ResultFromRealityEvent(ResultFromRealityEvent.ResultFromRealityEventType.ResultComputed));
    }
    
    protected void fireLogEntry(String log) {
        for (ResultFromRealityListener resultFromRealityListener : resultFromRealityListeners)
            resultFromRealityListener.logEntry(new ResultFromRealityEvent(log));
    }

    protected void fireInterupted() {
        for (ResultFromRealityListener resultFromRealityListener : resultFromRealityListeners)
            resultFromRealityListener.interupted(new ResultFromRealityEvent(ResultFromRealityEvent.ResultFromRealityEventType.Interupted));
    }


    public void stop() {
        stop = true;
    }

}
