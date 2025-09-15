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
package net.sourceforge.mecat.catalog.filesystem.store;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import net.sourceforge.mecat.catalog.filesystem.Detail;

/**
 * 
 * Created on May 24, 2006
 *
 * @author Stephan Richard Palm
 *
 * This class stores a filesystem snapshot 
 * into a file. The file has the following structure
 * 
 * 
 * 
 * 
Version 0:


0. Versionumber (4 bytes)
1. Number of directories (4 bytes)
2. Number of files (4 bytes)
3. Link to first file in alphabetic ordering (4 bytes)
4. Link to first file in date ordering (4 bytes)
5. Link to first file in size ordering (4 bytes)
6. Link to first file in md5 ordering (4 bytes)
7. Directories (20 bytes each)
8. Files (64 bytes each)
9. Names of directories (variable length depending on the lenght of the name - String ended with 0)
10. Name of the files (variable length depending on the length of the name - String ended with 0)
    and file detail (variable length depending on the length of the detail - Detail)


7.
A directory has the following structure
I. Link to the name (4 bytes)
II. Link to the parent directory (4 bytes)
III. Link to the next sibling (4 bytes)
IV. Link to the first child directory (4 bytes)
V. Link to the first child file (4 bytes)

8.
A file has the following structure
I. Link to the name (4 bytes)
II. Link to the parent (4 bytes)
III. Link to the next sibling (4 bytes)
IV. Link to the next file alphabetic ordering (4 bytes)
V. Link to the next file date ordering (4 bytes)
VI. Link to the next file size ordering (4 bytes)
VII. Link to tne next file MD5 ordering (4 bytes)
VIII. Link to a file details (4 bytes)
IX. Date as Long (8 bytes)
X. Size as Long  (8 bytes)
XI. MD5 SUM (16 bytes)


Version 1:




0. Versionumber (4 bytes)
1. Number of directories (4 bytes)
2. Number of files (4 bytes)
3. Link to first file in alphabetic ordering (4 bytes)
4. Link to first file in date ordering (4 bytes)
5. Link to first file in size ordering (4 bytes)
6. Link to first file in md5 ordering (4 bytes)
7. Directories (20 bytes each)
8. Files (64 bytes each)
9. Names of directories (variable length depending on the lenght of the name - String ended with 0)
10. Name of the files (variable length depending on the length of the name - String ended with 0)
11. File detail (variable length depending on the length of the detail - undefined)


7.
A directory has the following structure
I. Link to the name (4 bytes)
II. Link to the parent directory (4 bytes)
III. Link to the next sibling (4 bytes)
IV. Link to the first child directory (4 bytes)
V. Link to the first child file (4 bytes)
VI. Number of sub directories
- Since all sub directories are stored one after the other this allows fast list access
VII. Number of files
- Since all files are stored one after the other this allows fast list access

8.
A file has the following structure
I. Link to the name (4 bytes)
II. Link to the parent (4 bytes)
III. Link to the next sibling (4 bytes)
IV. Link to the next file alphabetic ordering (4 bytes)
V. Link to the next file date ordering (4 bytes)
VI. Link to the next file size ordering (4 bytes)
VII. Link to tne next file MD5 ordering (4 bytes)
VIII. Link to a file details (4 bytes)
IX. Date as Long (8 bytes)
X. Size as Long  (8 bytes)
XI. MD5 SUM (16 bytes)



 *
 *
 */
public class FileSystemStoreShallow {

    public static class ReadProperties {
        public final int lengthHeader;
        public final int lengthEntryDir;
        public final int lengthEntryFile;

        public ReadProperties(int lengthHeader, int lengthEntryDir, int lengthEntryFile) {
            this.lengthHeader = lengthHeader;
            this.lengthEntryDir = lengthEntryDir;
            this.lengthEntryFile = lengthEntryFile;
        }
        
    }

    public static final ReadProperties[] VERSIONS = {
        new ReadProperties( // VERSION 0
            /* LENGTH_HEADER     = */   7 * 4,
            /* LENGTH_ENTRY_DIR  = */   5 * 4,               // 5 Links
            /* LENGTH_ENTRY_FILE = */   8 * 4 + 2 * 8 + 16   // 8 Links (name, parent, sibling, 4*next, detail) + 2 Long + 1 MD5
        ),
        new ReadProperties( // VERSION 1
            /* LENGTH_HEADER     = */   7 * 4,
            /* LENGTH_ENTRY_DIR  = */   7 * 4,               // 5 Links + 2 Longs for the number of sub dirs and files
            /* LENGTH_ENTRY_FILE = */   8 * 4 + 2 * 8 + 16   // 8 Links (name, parent, sibling, 4*next, detail) + 2 Long + 1 MD5
        )
    };

    public static int currentVersionNumber = 1;
    public static ReadProperties VERSION = VERSIONS[currentVersionNumber];
    

    public static class LengthOfModifiedUTF8 {
        int count = 0;
        DataOutputStream data = new DataOutputStream(new OutputStream(){
            @Override
            public void write(int arg0) throws IOException {
                count++;
            }
        });
        public void reset() {
            count = 0;
        }
        public int len() {
            return count;
        }
        public int length(String str) {
            reset();
            try {
                data.writeUTF(str);
            } catch (IOException e) {
                e.printStackTrace();
                return 1;
            }
            return len();
        }
    }
    
    static LengthOfModifiedUTF8 modUTF8 = new LengthOfModifiedUTF8();
    
    /**
     * 
     * @param file The file where to store the information.
     * @param directory The root directory
     * @param dirs All directories that need to be stored.
     * For every file in files there should be a directory.
     * And for every directory the parent directory should be here as well.
     * @param files All files that need to be stored.
     */
    public static void store(File file, net.sourceforge.mecat.catalog.filesystem.Result result) {
        Vector<net.sourceforge.mecat.catalog.filesystem.DirectoryEntry> fastAccessOrderDir = new Vector<net.sourceforge.mecat.catalog.filesystem.DirectoryEntry>(result.getDirs());
        Vector<net.sourceforge.mecat.catalog.filesystem.FileEntry> fastAccessOrderFiles = new Vector<net.sourceforge.mecat.catalog.filesystem.FileEntry>(result.getFiles());
        
        Map<net.sourceforge.mecat.catalog.filesystem.DirectoryEntry, Integer> fastAccessOrderDirPosition = new HashMap<net.sourceforge.mecat.catalog.filesystem.DirectoryEntry, Integer>();
        Map<net.sourceforge.mecat.catalog.filesystem.FileEntry, Integer> fastAccessOrderFilesPosition = new HashMap<net.sourceforge.mecat.catalog.filesystem.FileEntry, Integer>();
        
        Collections.sort(fastAccessOrderDir, new Comparator<net.sourceforge.mecat.catalog.filesystem.DirectoryEntry>(){
            public int compare(net.sourceforge.mecat.catalog.filesystem.DirectoryEntry dir1, net.sourceforge.mecat.catalog.filesystem.DirectoryEntry dir2) {
                int d1 = dir1.depht();
                int d2 = dir2.depht();
                if (d1 < d2)
                    return -1;
                if (d1 > d2)
                    return 1;
                // getParent() is allways different from null
                // because there is only one directory with null for parent
                int path = dir1.getParent().getFullName().compareTo(dir2.getParent().getFullName());
                if (path != 0)
                    return path;
                return dir1.getName().compareToIgnoreCase(dir2.getName());
            }});
        Collections.sort(fastAccessOrderFiles, new Comparator<net.sourceforge.mecat.catalog.filesystem.FileEntry>(){
            public int compare(net.sourceforge.mecat.catalog.filesystem.FileEntry file1, net.sourceforge.mecat.catalog.filesystem.FileEntry file2) {
                int d1 = file1.depht();
                int d2 = file2.depht();
                if (d1 < d2)
                    return -1;
                if (d1 > d2)
                    return 1;

                int path = file1.getParent().getFullName().compareTo(file2.getParent().getFullName());
                if (path != 0)
                    return path;
                return file1.getName().compareToIgnoreCase(file2.getName());
            }});
        
        for (int i = 0; i < fastAccessOrderDir.size(); i++) 
            fastAccessOrderDirPosition.put(fastAccessOrderDir.get(i), i);

        for (int i = 0; i < fastAccessOrderFiles.size(); i++)  
            fastAccessOrderFilesPosition.put(fastAccessOrderFiles.get(i), i);


        int positionNames = VERSION.lengthHeader + VERSION.lengthEntryDir * fastAccessOrderDir.size()
                                                 + VERSION.lengthEntryFile * fastAccessOrderFiles.size();
        
        try {
            DataOutputStream output = new DataOutputStream(new FileOutputStream(file));

            output.writeInt(currentVersionNumber);
            output.writeInt(fastAccessOrderDir.size());
            output.writeInt(fastAccessOrderFiles.size());

            // Make a link to the first element in alphabetic order
            if (result.getFirstAlph() != null)
                output.writeInt(fastAccessOrderFilesPosition.get(result.getFirstAlph()));
            // or -1 for a null point if there is no element
            else
                output.writeInt(-1);

            if (result.getFirstDate() != null)
                output.writeInt(fastAccessOrderFilesPosition.get(result.getFirstDate()));
            else
                output.writeInt(-1);
            
            if (result.getFirstSize() != null)
                output.writeInt(fastAccessOrderFilesPosition.get(result.getFirstSize()));
            else
                output.writeInt(-1);
            
            if (result.getFirstMD5() != null)
                output.writeInt(fastAccessOrderFilesPosition.get(result.getFirstMD5()));
            else
                output.writeInt(-1);
            
            for (int i = 0; i < fastAccessOrderDir.size(); i++) {
                net.sourceforge.mecat.catalog.filesystem.DirectoryEntry dir = fastAccessOrderDir.get(i);
                
                output.writeInt(positionNames);
                positionNames += modUTF8.length(dir.getName());

                if (dir.getParent() != null)
                    output.writeInt(fastAccessOrderDirPosition.get(dir.getParent()));
                else
                    output.writeInt(-1);
                if (dir.getNextSibling() != null)
                    output.writeInt(fastAccessOrderDirPosition.get(dir.getNextSibling()));
                else
                    output.writeInt(-1);
                if (dir.getFirstDirectoryChild() != null)
                    output.writeInt(fastAccessOrderDirPosition.get(dir.getFirstDirectoryChild()));
                else
                    output.writeInt(-1);
                if (dir.getFirstFileChild() != null) 
                    output.writeInt(fastAccessOrderFilesPosition.get(dir.getFirstFileChild()));
                else
                    output.writeInt(-1);
                output.writeInt(dir.getNumDirs());
                output.writeInt(dir.getNumFiles());
            }
            for (int i = 0; i < fastAccessOrderFiles.size(); i++) {
                net.sourceforge.mecat.catalog.filesystem.FileEntry f = fastAccessOrderFiles.get(i);

                output.writeInt(positionNames);
                positionNames += modUTF8.length(f.getName());
                
                if (f.getParent() != null)
                    output.writeInt(fastAccessOrderDirPosition.get(f.getParent()));
                else
                    output.writeInt(-1);
                if (f.getNextSibling() != null)
                    output.writeInt(fastAccessOrderFilesPosition.get(f.getNextSibling()));
                else
                    output.writeInt(-1);
                if (f.getNextAlph() != null)
                    output.writeInt(fastAccessOrderFilesPosition.get(f.getNextAlph()));
                else
                    output.writeInt(-1);
                if (f.getNextDate() != null)
                    output.writeInt(fastAccessOrderFilesPosition.get(f.getNextDate()));
                else
                    output.writeInt(-1);
                if (f.getNextSize() != null)
                    output.writeInt(fastAccessOrderFilesPosition.get(f.getNextSize()));
                else
                    output.writeInt(-1);
                if (f.getNextMD5() != null)
                    output.writeInt(fastAccessOrderFilesPosition.get(f.getNextMD5()));
                else
                    output.writeInt(-1);
                
                // Write Link for detail
                Detail detail = f.getDetail();
                if (detail == null)
                    output.writeInt(0);
                else {
                    output.writeInt(positionNames);
                    positionNames += detail.getType().getDetailWriter().getLength(detail);
                }

                // Values
                output.writeLong(f.getDate());
                output.writeLong(f.getSize());
                output.write(f.getMD5SUM());
            }
            for (int i = 0; i < fastAccessOrderDir.size(); i++) {
                net.sourceforge.mecat.catalog.filesystem.DirectoryEntry dir = fastAccessOrderDir.get(i);
                output.writeUTF(dir.getName());
            }
            for (int i = 0; i < fastAccessOrderFiles.size(); i++) {
                net.sourceforge.mecat.catalog.filesystem.FileEntry f = fastAccessOrderFiles.get(i);
                // Write name
                output.writeUTF(f.getName());
                // Write detail
                Detail detail = f.getDetail();
                if (detail != null) 
                    detail.getType().getDetailWriter().writeDetail(detail, output);
            }
            
            output.flush();
            output.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
           
    }
}
