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
 * Created on Jun 4, 2006
 * @author Stephan Richard Palm
 * 
 */
package net.sourceforge.mecat.catalog.filesystem.virtual;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import net.sourceforge.mecat.catalog.filesystem.Comparators;
import net.sourceforge.mecat.catalog.filesystem.DirectoryEntry;
import net.sourceforge.mecat.catalog.filesystem.FileEntry;

public class VirtualDirectory extends VirtualEntry implements DirectoryEntry {

    /**
     * Those mount points that are at a sub level
     */
    final List<Mount> relevantMounts;

    /**
     * List of all Directory Entries that are merged for creating this directory
     */
    final List<DirectoryEntry> dirs;

    /**
     * Name of the directory. 
     */
    final String name;
    

    /** 
     * Beeing part of the follwing virtual result
     */
//    final VirtualResult virtualResult;
    
    /**
     * Haveing this for the next sibling
     */
    VirtualDirectory nextSibling;

    /**
     * Cache for the file child.
     */
    List<VirtualFile> fileChildren = null;
    
    
    /**
     * Cache for the sub directories.
     */
    List<VirtualDirectory> dirChildren = null;
    
    
    public VirtualDirectory(VirtualDirectory parent, List<Mount> relevantMounts, String name/*, VirtualResult virtualResult*/) {
        super(parent);

        this.relevantMounts = relevantMounts;
        this.dirs = null;
        this.name = name;
//        this.virtualResult = virtualResult;
    }

    public VirtualDirectory(VirtualDirectory parent, List<Mount> relevantMounts, final DirectoryEntry dir/*, VirtualResult virtualResult*/) {
        this(parent, relevantMounts, new Vector<DirectoryEntry>(){{ add(dir); }}/*, virtualResult*/);
    }
    
    public VirtualDirectory(VirtualDirectory parent, List<Mount> relevantMounts, List<DirectoryEntry> dirs/*, VirtualResult virtualResult*/) {
        super(parent);

        this.relevantMounts = relevantMounts;
        this.dirs = dirs;
        this.name = dirs.get(0).getName();
//        this.virtualResult = virtualResult;
    }

    
    public void add(DirectoryEntry entry) {
        dirs.add(entry);
    }
    
    public void setNextSibling(VirtualDirectory nextSibling) {
        this.nextSibling = nextSibling;
    }

    public VirtualDirectory getNextSibling() {
        return nextSibling;
    }

    public VirtualDirectory getFirstDirectoryChild() {
        if (dirChildren == null)
            createDirectoryChildren();
        
        if (dirChildren.isEmpty())
            return null;
        
        return dirChildren.get(0);
    }

    public FileEntry getFirstFileChild() {
        if (fileChildren == null)
            createFileChildren();
        
        if (fileChildren.isEmpty())
            return null;
        
        return fileChildren.get(0);
    }
    
    protected void createFileChildren() {

        String path = getFullName();
        List<FileEntry> mounts = new Vector<FileEntry>();
        if (relevantMounts != null)
            for (Mount mount : relevantMounts) {
                if (mount.getEntry() instanceof FileEntry) {
                    String relativePos = mount.getPosition().substring(path.length() + System.getProperty("file.separator").length());
                    if (!relativePos.contains(System.getProperty("file.separator")))
                        mounts.add(new MountFile((FileEntry) mount.getEntry(), relativePos));
                }
            }
        
        Vector<VirtualFile> files = new Vector<VirtualFile>();
        
        Vector<List<? extends FileEntry>> fileLists = new Vector<List<? extends FileEntry>>();

        Comparator<List<? extends FileEntry>> listComparator = new Comparator<List<? extends FileEntry>>() {
            public int compare(List< ? extends FileEntry> list1, List< ? extends FileEntry> list2) {
                return Comparators.TotalComparator.compare(list1.get(0), list2.get(0));
            }
        };

        if (!mounts.isEmpty())
            fileLists.add(mounts);
        
        if (dirs != null)
            for (DirectoryEntry dir : dirs) {
                List<? extends FileEntry> list = dir.getAllFiles();
                if (!list.isEmpty())
                    fileLists.add(new Vector<FileEntry>(list));
            }
        
        for (List<? extends FileEntry> list : fileLists)
            Collections.sort(list, Comparators.TotalComparator);

        
        while (!fileLists.isEmpty()) {
            Collections.sort(fileLists, listComparator);

            if (fileLists.size() == 1) {
                for (FileEntry fileEntry : fileLists.get(0))
                    files.add(new VirtualFile(this, fileEntry));
                break;
            }

            VirtualFile entry = new VirtualFile(this, fileLists.get(0).get(0));
            fileLists.get(0).remove(0);
            
            int num = 0;
            do {
                while (!fileLists.get(num).isEmpty() && Comparators.TotalComparator.compare(entry, fileLists.get(num).get(0)) == 0) {
                    entry.add(fileLists.get(num).get(0));
                    fileLists.get(num).remove(0);
                }
                num++;
            } while (num < fileLists.size() && Comparators.TotalComparator.compare(entry, fileLists.get(num).get(0)) == 0);

            for (int i = num - 1; i >= 0; i--)
                if (fileLists.get(i).isEmpty())
                    fileLists.remove(i);
            
            files.add(entry);
        }

        Collections.sort(files, Comparators.NameComparator);
        
        for (int i = 0; i < files.size() - 1; i++)
            files.get(i).setNextSibling(files.get(i + 1));
        
        fileChildren = files;
    }

    protected void createDirectoryChildren() {

        // Full path of this directory
        // this will be used to determine what part of the
        // mount points need to be considered
        String path = getFullName();

        // List of all directories that are mounted as direct subdirectories of this directory
        // All element of this list need to be merged into this directory
        List<DirectoryEntry> mounts = new Vector<DirectoryEntry>();
        
        // List of all mounts further into subdirectories
        // those mounts will be passed along
        Map<String, List<Mount>> sortedMounts = new HashMap<String, List<Mount>>();

        // Sort and evaluate Mountspoints
        if (relevantMounts != null)
            for (Mount mount : relevantMounts) {
                String relativePos = mount.getPosition().substring(path.length() + System.getProperty("file.separator").length());
                
                if (mount.getEntry() instanceof DirectoryEntry) 
                    if (!relativePos.contains(System.getProperty("file.separator")))
                        // Use Mount directory to rename the directory with the name of the mountpoint
                        mounts.add(new MountDirectory((DirectoryEntry) mount.getEntry(), relativePos));
                
                int index = relativePos.indexOf(System.getProperty("file.separator"));
                if (index != -1) {
                    String sub = relativePos.substring(0, index);
                    
                    if (!sortedMounts.containsKey(sub))
                        sortedMounts.put(sub, new Vector<Mount>());
                    
                    sortedMounts.get(sub).add(mount);
                }
                
            }

        // Create a list of all directories that need to be there in order to be able
        // to mount everything.
        Vector<String> implicitDirectories = new Vector<String>(sortedMounts.keySet());
        Collections.sort(implicitDirectories, new Comparator<String>(){
            public int compare(String s0, String s1) {
                return s0.compareTo(s1);
            }
        });

        // This list is going to contain all sub-directories,
        // this list will be returned eventualy
        Vector<VirtualDirectory> directories = new Vector<VirtualDirectory>();

        // This is going to contain a list of all directory-list that will be merged
        // to form this directory. It will gather the following 3 lists:
        // 1. Directory-List of the directories, the virtual directory was created with
        // 2. Direct sub-directories that will be mounted
        // 3. Implicit directories needed to mount things in the subdirectories
        Vector<List<? extends DirectoryEntry>> dirLists = new Vector<List<? extends DirectoryEntry>>();

        // This comparator is used to order the lists.
        // This will allow to take the element from the first list(s).
        Comparator<List<? extends DirectoryEntry>> listComparator = new Comparator<List<? extends DirectoryEntry>>() {
            public int compare(List< ? extends DirectoryEntry> list1, List< ? extends DirectoryEntry> list2) {
                return list1.get(0).getName().compareTo(list2.get(0).getName());
            }
        };

        // If there are any direct subdirectories to mount at the list of direct sub-directories
        if (!mounts.isEmpty())
            dirLists.add(mounts);

        // If the virtual directory is based on directories take there lists
        if (dirs != null)
            for (DirectoryEntry dir : dirs) {
                List<? extends DirectoryEntry> list = dir.getAllDirs();
                if (!list.isEmpty())
                    // make static copy, this allows to sort it later on in the process
                    dirLists.add(new Vector<DirectoryEntry>(list));
            }

        // Sort the elements for all list before merging
        // This way we only need to consider the first elements
        // of the lists in order to find the next element.
        for (List<? extends DirectoryEntry> list : dirLists)
            Collections.sort(list, Comparators.NameComparatorCaseSensitiv);

        
        while (!dirLists.isEmpty()) {
            // Sort the order of the lists,
            // this way we can take the first element of the first list
            // and it is the next element
            Collections.sort(dirLists, listComparator);

/*          
 * This optimisation would still need handling for implicit directories  
            if (dirLists.size() == 1) {
                for (DirectoryEntry dirEntry : dirLists.get(0))
                    directories.add(new VirtualDirectory(this, sortedMounts.get(dirEntry.getName()), dirEntry));
                break;
            }
            */

            // Make implicit needed directories needed for mounting
            // at not root mount points

            // We first take the name of the next explicit directory
            // this name is need to compare it with the implicit
            // if the implicit belongs before the explicit, then
            // we would need to create the implicit before continuing
            String nameExplicit = dirLists.get(0).get(0).getName();
            
            if (!implicitDirectories.isEmpty()) {
                // Name of the next implicit directory
                String nameImplicit = implicitDirectories.firstElement();
            
                // Compare next explicit and implicit directory
                int c = nameImplicit.compareToIgnoreCase(nameExplicit);
                // If an implicit directory has no explicit directory
                // it gets still a virtual directory
                if (c < 0) 
                    directories.add(new VirtualDirectory(this, sortedMounts.get(implicitDirectories.firstElement()), implicitDirectories.firstElement()));
                // Remove implicit directory that are served
                // the ones with < 0 are served above and the
                // ones with = 0 will be served this iteration
                if (c <= 0)
                    implicitDirectories.remove(0);
            }
            
            // dirLists.get(0) will return a valid list because otherwise the while loop would teminate
            // dirLists.get(0).get(0) will return a valid element because any empty list is removed before
            // reentering the loop
            // Since all elements in the lists are ordered and the lists are ordered as well 
            // the element dirLists.get(0).get(0) is the next element.
            VirtualDirectory entry = new VirtualDirectory(this, sortedMounts.get(nameExplicit), dirLists.get(0).get(0));
            dirLists.get(0).remove(0);

            // Get all elements that are equal
            int num = 0;

            do {
                while (!dirLists.get(num).isEmpty() && Comparators.NameComparatorCaseSensitiv.compare(entry, dirLists.get(num).get(0)) == 0) {
                    entry.add(dirLists.get(num).get(0));
                    dirLists.get(num).remove(0);
                }
                num++;
            } while (num < dirLists.size() && Comparators.NameComparatorCaseSensitiv.compare(entry, dirLists.get(num).get(0)) == 0);
            
/*            while (num < dirLists.size() && Comparators.NameComparatorCaseSensitiv.compare(entry, dirLists.get(num).get(0)) == 0) {
                entry.add(dirLists.get(num).get(0));
                dirLists.get(num).remove(0);
                num++;
            }*/

            // Remove all empty lists
            for (int i = num - 1; i >= 0; i--)
                if (dirLists.get(i).isEmpty())
                    dirLists.remove(i);

            // Add the directory created to the list of directories
            directories.add(entry);
        }
        
        // Make all implicit directories that follow after all explicit are allready made
        while (!implicitDirectories.isEmpty()) {
            directories.add(new VirtualDirectory(this, sortedMounts.get(implicitDirectories.firstElement()), implicitDirectories.firstElement()));
            implicitDirectories.remove(0);
        }

        // Sort the result by name
        Collections.sort(directories, Comparators.NameComparator);
        
        // Make the links between the directories, such that getNextSibling will return a valid value
        for (int i = 0; i < directories.size() - 1; i++)
            directories.get(i).setNextSibling(directories.get(i + 1));

        // Set the cache of child directories to the computed list
        dirChildren = directories;
    }

    public List< ? extends DirectoryEntry> getAllDirs() {
        if (dirChildren == null)
            createDirectoryChildren();

        return dirChildren;
    }

    public List< ? extends FileEntry> getAllFiles() {
        if (fileChildren == null)
            createFileChildren();

        return fileChildren;
    }

    public String getName() {
        return name;
    }

    public int getNumDirs() {
        return getAllDirs().size();
    }

    public int getNumFiles() {
        return getAllFiles().size();
    }

}
