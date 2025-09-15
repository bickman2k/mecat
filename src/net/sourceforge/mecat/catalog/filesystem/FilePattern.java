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
 * Created on Jun 26, 2006
 * @author Stephan Richard Palm
 * 
 */
package net.sourceforge.mecat.catalog.filesystem;

import java.util.Date;
import java.util.Vector;
import java.util.regex.Pattern;

import net.sourceforge.mecat.catalog.filesystem.store.ResultFromReality;

public class FilePattern {

    String name;
    Pattern namePattern;
    
    long olderThan;
    long youngerThan;
    long biggerThan;
    long smallerThan;
    
    public FilePattern(String name) {
        setName(name);
        this.olderThan = 0;
        this.youngerThan = System.currentTimeMillis();
        this.biggerThan = 0;
        this.smallerThan = -1;
    }

    public FilePattern(String name, long olderThan, long youngerThan, long biggerThan, long smallerThan) {
        setName(name);
        this.olderThan = olderThan;
        this.youngerThan = youngerThan;
        this.biggerThan = biggerThan;
        this.smallerThan = smallerThan;
    }

    public long getBiggerThan() {
        return biggerThan;
    }

    public void setBiggerThan(long biggerThan) {
        if (biggerThan < 0)
            this.biggerThan = 0;
        else
            this.biggerThan = biggerThan;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        namePattern = Pattern.compile(name, Pattern.DOTALL /*| Pattern.UNICODE_CASE*/);
    }

    public long getOlderThan() {
        return olderThan;
    }

    public void setOlderThan(long olderThan) {
        this.olderThan = olderThan;
    }

    public long getSmallerThan() {
        return smallerThan;
    }

    public void setSmallerThan(long smallerThan) {
        if (smallerThan < 0)
            this.smallerThan = -1;
        else
            this.smallerThan = smallerThan;
    }

    public long getYoungerThan() {
        return youngerThan;
    }

    public void setYoungerThan(long youngerThan) {
        this.youngerThan = youngerThan;
    }

    
    public String toString() {
        return "[" + name + ", " + new Date(olderThan) + ", " + new Date(youngerThan) + ", " + biggerThan + ", " + smallerThan + "]";
    }

    public Result filterResult(final Result result) {
        ModifiableDirectoryEntry root = filterDirectory(result.getDirectory(), null);
        ModifiableResult ret = new net.sourceforge.mecat.catalog.filesystem.store.Result(root);
        // Make all next alph, size, date and md5
        (new ResultFromReality(null)).finish(ret);
        
        return ret;
    }

    private ModifiableDirectoryEntry filterDirectory(DirectoryEntry directory, ModifiableDirectoryEntry parent) {
        net.sourceforge.mecat.catalog.filesystem.store.DirectoryEntry ret 
          = new net.sourceforge.mecat.catalog.filesystem.store.DirectoryEntry(directory.getName(), parent);

        // Check the subdirectories
        Vector<ModifiableDirectoryEntry> subs = new Vector<ModifiableDirectoryEntry>();
        for (DirectoryEntry dir : directory.getAllDirs()) {
            ModifiableDirectoryEntry subDir = filterDirectory(dir, ret);
            // If the directory would be empty none is returned
            if (subDir != null)
                subs.add(subDir);
        }

        // Check the files
        Vector<ModifiableFileEntry> files = new Vector<ModifiableFileEntry>();
        for (FileEntry file : directory.getAllFiles()) 
            if (accept(file))
                files.add(net.sourceforge.mecat.catalog.filesystem.store.Result.rawCopy(file, ret));

        // Check if the directory would be empty
        if (subs.size() == 0 && files.size() == 0)
            return null;

        // Create subdirectory and files structure
        ret.setNumDirs(subs.size());
        ret.setNumFiles(files.size());
        
        if (subs.size() > 0) {
            ret.setFirstDirectoryChild(subs.firstElement());
            for (int i = 0; i < subs.size() - 1; i++)
                subs.get(i).setNextSibling(subs.get(i + 1));
            subs.get(subs.size() - 1).setNextSibling(null);
        } else
            ret.setFirstDirectoryChild(null);
        
        if (files.size() > 0) {
            ret.setFirstFileChild(files.firstElement());
            for (int i = 0; i < files.size() - 1; i++)
                files.get(i).setNextSibling(files.get(i + 1));
            files.get(files.size() - 1).setNextSibling(null);
        } else
            ret.setFirstFileChild(null);
        
        return ret;
    }

    private boolean accept(FileEntry file) {
        
        
        if (file.getDate() < olderThan)
            return false;
        if (file.getDate() > youngerThan)
            return false;
        if (file.getSize() < biggerThan)
            return false;
        if (smallerThan != -1 && file.getSize() > smallerThan)
            return false;
        
        if (!namePattern.matcher(file.getName()).matches())
            return false;

        return true;
    }

    public FileEntry search(Result result) {
        FileEntry entry = Util.getFirstFileLeftRecursive(result);
        while (entry != null && !accept(entry))
            entry = Util.getNextFileLeftRecursive(entry);
        return entry;
    }
    
    public FileEntry search(FileEntry lastFound) {
        if (lastFound == null)
            return null;
        FileEntry entry = Util.getNextFileLeftRecursive(lastFound);
        while (entry != null && !accept(entry))
            entry = Util.getNextFileLeftRecursive(entry);
        return entry;
    }
    
}
