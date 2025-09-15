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
package net.sourceforge.mecat.catalog.filesystem.load;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import net.sourceforge.mecat.catalog.filesystem.Detail;
import net.sourceforge.mecat.catalog.filesystem.DetailType;
import net.sourceforge.mecat.catalog.filesystem.store.FileSystemStoreShallow;
import net.sourceforge.mecat.catalog.filesystem.store.FileSystemStoreShallow.ReadProperties;
import net.sourceforge.mecat.catalog.option.Options;

public class FileSystemLoadShallow {

    final File file;

    final int version;
    final ReadProperties VERSION;
    
    final int numDirs;
    final int numFiles;
    
    final int firstAlphLnk;
    final int firstDateLnk;
    final int firstSizeLnk;
    final int firstMD5Lnk;
    
    final Map <Integer, SoftReference<FileEntry>> files = new HashMap<Integer, SoftReference<FileEntry>>();
    final Map <Integer, SoftReference<DirectoryEntry>> dirs = new HashMap<Integer, SoftReference<DirectoryEntry>>();
    
    static public boolean isCurrent(final File file) throws IOException, VersionException  {
        FileSystemLoadShallow fsload = new FileSystemLoadShallow(file);
        return (fsload.getVersion() == FileSystemStoreShallow.currentVersionNumber);
    }
    

    static public net.sourceforge.mecat.catalog.filesystem.Result load(final File file) throws IOException, VersionException {
        FileSystemLoadShallow fsload = new FileSystemLoadShallow(file);
        net.sourceforge.mecat.catalog.filesystem.Result result = new Result(fsload);

        // Repair broken md5 from version 0
        if (fsload.version == 0) 
            result = net.sourceforge.mecat.catalog.filesystem.store.Result.goodCopy(result);
        
        return result;
    }
    

    // TODO Visibility of constructor should be protected
    public FileSystemLoadShallow(final File file) throws IOException, VersionException {
        this.file = file;
        FileInputStream inputStream = new FileInputStream(file);
        DataInputStream data = new DataInputStream(inputStream);
        version = data.readInt();
        if (version > FileSystemStoreShallow.VERSIONS.length)
            throw new VersionException();
        VERSION = FileSystemStoreShallow.VERSIONS[version];

        numDirs = data.readInt();
        numFiles = data.readInt();
        
        firstAlphLnk = data.readInt();
        firstDateLnk = data.readInt();
        firstSizeLnk = data.readInt();
        firstMD5Lnk = data.readInt();
        
        DirectoryEntry root = getDirectoryFromDataStream(data);
        dirs.put(0, new SoftReference<DirectoryEntry>(root));
        data.close();
        inputStream.close();
    }
    
    public String getName(int nameLnk) {
        try {
            FileInputStream inputStream = new FileInputStream(file);
            inputStream.skip(nameLnk);
            DataInputStream data = new DataInputStream(inputStream);
            String name =  data.readUTF();
            data.close();
            inputStream.close();
            return name;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    public Detail getDetail(int detailLnk) {
        if (detailLnk <= 0)
            return null;
        
        try {
            FileInputStream inputStream = new FileInputStream(file);
            inputStream.skip(detailLnk);
            DataInputStream data = new DataInputStream(inputStream);
            
            // get size of detail
            int size = data.readInt();
            
            // get type of detail
            int typeOrdinal = data.readInt();
            DetailType type = DetailType.fromStaticOrdinal(typeOrdinal);
            if (type == null) {
                data.close();
                inputStream.close();
                return null;
            }
            
            // get detail
            Detail detail = type.getDetailReader().getDetail(data, size);
            
            data.close();
            inputStream.close();
            
            return detail;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void getAllDirs(int indexStart, int amount, Object[] entries) {
        try {
            FileInputStream inputStream = new FileInputStream(file);
            inputStream.skip(VERSION.lengthHeader + VERSION.lengthEntryDir * indexStart);
            DataInputStream data = new DataInputStream(inputStream);
    
            for (int i = indexStart, j = 0; j < amount; i++, j++) {
                if (dirs.containsKey(i)) {
                    entries[j] = dirs.get(i);
                    data.skip(VERSION.lengthEntryDir);
                } else {
                    DirectoryEntry dirEntry = getDirectoryFromDataStream(data);
                    entries[j] = dirEntry;
                    dirs.put(i, new SoftReference<DirectoryEntry>(dirEntry));
                }
            }

            data.close();
            inputStream.close();
            
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Vector<DirectoryEntry> getAllDirs() {
        Vector<DirectoryEntry> directories = new Vector<DirectoryEntry>();
        try {
            FileInputStream inputStream = new FileInputStream(file);
            inputStream.skip(VERSION.lengthHeader);
            DataInputStream data = new DataInputStream(inputStream);
    
            for (int i = 0; i < numDirs; i++) {
                DirectoryEntry dirEntry = null;
                if (dirs.containsKey(i) && (dirEntry = dirs.get(i).get()) != null) {
                    directories.add(dirEntry);
                    data.skip(VERSION.lengthEntryDir);
                } else {
                    dirEntry = getDirectoryFromDataStream(data);
                    directories.add(dirEntry);
                    dirs.put(i, new SoftReference<DirectoryEntry>(dirEntry));
                }
            }

            data.close();
            inputStream.close();
            
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return directories;
    }
    
    public DirectoryEntry getDirectoryIfLoadAlready(int directoryLnk) {
        if (dirs.containsKey(directoryLnk))
            return dirs.get(directoryLnk).get();
        return null;
    }
    public DirectoryEntry getDirectory(int directoryLnk) {
        if (directoryLnk == -1)
            return null;
        DirectoryEntry dirEntry = null;
        if (dirs.containsKey(directoryLnk) && (dirEntry = dirs.get(directoryLnk).get()) != null)
            return dirEntry;
        try {
            FileInputStream inputStream = new FileInputStream(file);
            inputStream.skip(VERSION.lengthHeader + directoryLnk * VERSION.lengthEntryDir);
            DataInputStream data = new DataInputStream(inputStream);

            dirEntry = getDirectoryFromDataStream(data);
            dirs.put(directoryLnk, new SoftReference<DirectoryEntry>(dirEntry));

            data.close();
            inputStream.close();
            
            return dirEntry;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    protected DirectoryEntry getDirectoryFromDataStream(DataInputStream data) {
        int readBytes = 0;
        try {
            DirectoryEntry dirEntry = new DirectoryEntry(this, data.readInt(), data.readInt());
            dirEntry.setNextSiblingLnk(data.readInt());
            dirEntry.setFirstDirectoryChildLnk(data.readInt());
            dirEntry.setFirstFileChildLnk(data.readInt());

            readBytes += 5 * 4;
            
            if (version > 0) {
                dirEntry.setNumDirs(data.readInt());
                dirEntry.setNumFiles(data.readInt());
                
                readBytes += 2 * 4;
            }
            
            if (readBytes < VERSION.lengthEntryDir) {
                System.out.println(Options.getI18N(FileSystemLoadShallow.class)
                        .getString("Read file from later version, skipping [BYTES] bytes.")
                        .replaceAll("\\[BYTES\\]", "" + (VERSION.lengthEntryDir - readBytes)));
                data.skip(VERSION.lengthEntryDir - readBytes);
            }
            
            return dirEntry;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void getAllFiles(int indexStart, int amount, Object[] entries) {
        try {
            FileInputStream inputStream = new FileInputStream(file);
            inputStream.skip(VERSION.lengthHeader + VERSION.lengthEntryDir * numDirs + VERSION.lengthEntryFile * indexStart);
            DataInputStream data = new DataInputStream(inputStream);
    
            for (int i = indexStart, j = 0; j < amount; i++, j++) {
                if (files.containsKey(i)) {
                    entries[j] = files.get(i);
                    data.skip(VERSION.lengthEntryFile);
                } else {
                    FileEntry fileEntry = getFileFromDataStream(data);
                    entries[j] = fileEntry;
                    files.put(i, new SoftReference<FileEntry>(fileEntry));
                }
            }
            
            data.close();
            inputStream.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public Vector<FileEntry> getAllFiles() {
        Vector<FileEntry> fs = new Vector<FileEntry>();
        try {
            FileInputStream inputStream = new FileInputStream(file);
            inputStream.skip(VERSION.lengthHeader + VERSION.lengthEntryDir * numDirs);
            DataInputStream data = new DataInputStream(inputStream);
    
            for (int i = 0; i < numFiles; i++) {
                FileEntry fileEntry = null;
                if (files.containsKey(i) && (fileEntry = files.get(i).get()) != null) {
                    fs.add(fileEntry);
                    data.skip(VERSION.lengthEntryFile);
                } else {
                    fileEntry = getFileFromDataStream(data);
                    fs.add(fileEntry);
                    files.put(i, new SoftReference<FileEntry>(fileEntry));
                }
            }
            
            data.close();
            inputStream.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fs;
    }
    
    public FileEntry getFileIfLoadAlready(int fileLnk) {
        FileEntry fileEntry = null;
        if (files.containsKey(fileLnk) && (fileEntry = files.get(fileLnk).get()) != null)
            return fileEntry;
        return null;
    }
    public FileEntry getFile(int fileLnk) {
        if (fileLnk == -1)
            return null;
        FileEntry fileEntry = null;
        if (files.containsKey(fileLnk) && (fileEntry = files.get(fileLnk).get()) != null)
            return fileEntry;
        try {
            FileInputStream inputStream = new FileInputStream(file);
            inputStream.skip(VERSION.lengthHeader + VERSION.lengthEntryDir * numDirs + fileLnk * VERSION.lengthEntryFile);
            DataInputStream data = new DataInputStream(inputStream);

            fileEntry = getFileFromDataStream(data);
            files.put(fileLnk, new SoftReference<FileEntry>(fileEntry));

            data.close();
            inputStream.close();

            return fileEntry;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    protected FileEntry getFileFromDataStream(DataInputStream data) {
        try {
            byte buf[] = new byte[16];
            
            FileEntry fileEntry = new FileEntry(this, data.readInt(), data.readInt());
            
            fileEntry.setNextSiblingLnk(data.readInt());
            fileEntry.setNextAlphLnk(data.readInt());
            fileEntry.setNextDateLnk(data.readInt());
            fileEntry.setNextSizeLnk(data.readInt());
            fileEntry.setNextMD5Lnk(data.readInt());
            
            // Details
            fileEntry.setDetailsLnk(data.readInt());
            
            fileEntry.setDate(data.readLong());
            fileEntry.setSize(data.readLong());
            if (data.read(buf) != 16)
                throw new IOException(Options.getI18N(FileSystemLoadShallow.class)
                        .getString("Needed to read 16 bytes."));
            fileEntry.setMD5SUM(buf);

            return fileEntry;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public int getFirstAlphLnk() {
        return firstAlphLnk;
    }

    public int getFirstDateLnk() {
        return firstDateLnk;
    }

    public int getFirstMD5Lnk() {
        return firstMD5Lnk;
    }

    public int getFirstSizeLnk() {
        return firstSizeLnk;
    }

    public int getNumDirs() {
        return numDirs;
    }

    public int getNumFiles() {
        return numFiles;
    }

    public int getVersion() {
        return version;
    }
}
