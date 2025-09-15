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
 * Created on May 11, 2006
 * @author Stephan Richard Palm
 * 
 */
package net.sourceforge.mecat.catalog.datamanagement.remote;

import java.awt.Component;

import net.sourceforge.mecat.catalog.datamanagement.AttributeEvent;
import net.sourceforge.mecat.catalog.datamanagement.Catalog;
import net.sourceforge.mecat.catalog.datamanagement.Entry;
import net.sourceforge.mecat.catalog.datamanagement.EntryClearedEvent;
import net.sourceforge.mecat.catalog.datamanagement.EntryListener;
import net.sourceforge.mecat.catalog.datamanagement.SetAttributeEvent;
import net.sourceforge.mecat.catalog.datamanagement.SubEntryEvent;
import net.sourceforge.mecat.catalog.datamanagement.xml.XMLCatalogFactory;

public class RemoteTest {

    public static class TestThread extends Thread {
        
        final Entry entry;
        final String[] lst;
        final String name;

        /**
         * 
         * @param entry Entry for which to run the test
         * @param lst List of values to use for the test.
         * @param name Name of the attribute,
         */
        public TestThread(final Entry entry, final String[] lst, final String name) {
            this.entry = entry;
            this.lst = lst;
            this.name = name;
        }
        
        public void run() {
            for (String ch : lst) {
                
                synchronized (entry.getAttributeSynchronizationObject(name)) {
                    String oldValue = entry.getAttribute(name);
                    String newValue = ch;
                    
                    long time = System.currentTimeMillis();
                    while (time + 100 > System.currentTimeMillis())
                        ;

                    entry.setAttribute(name, newValue);
                    System.out.println("THREAD " + name + " : " + oldValue + " -> " + newValue);
                }
            }
        }
    };
    
    
    /**
     * @param args
     */
    public static void main(String[] args) {
        XMLCatalogFactory factory = new XMLCatalogFactory();
        Catalog catalog = factory.createCatalog((Component)null);
        final Entry entry = catalog.createEntry("TestEntry");

        entry.addEntryListener(new EntryListener(){

            public void attributeSet(AttributeEvent event) {
                System.out.println("EVENT  " + event.getName() + " : " + event.getOldValue() + " -> " + event.getNewValue());
            }

            public void setAttributeAdded(SetAttributeEvent event) {
                // TODO Auto-generated method stub
                
            }

            public void setAttributeRemoved(SetAttributeEvent event) {
                // TODO Auto-generated method stub
                
            }

            public void subEntryCreated(SubEntryEvent event) {
                // TODO Auto-generated method stub
                
            }

            public void subEntryRemoved(SubEntryEvent event) {
                // TODO Auto-generated method stub
                
            }

            public void setAttributeCleared(SetAttributeEvent event) {
                // TODO Auto-generated method stub
                
            }

            public void subEntriesRemoved(SubEntryEvent event) {
                // TODO Auto-generated method stub
                
            }

            public void entryCleared(EntryClearedEvent event) {
                // TODO Auto-generated method stub
                
            }});
        
        final String name = "TestAttribute";
        final String lst[] = new String[26];
        final String lst2[] = new String[26];
        for (int i = 0; i < 26; i++)
            lst[i] = "" + (char)('a' + i);
        for (int i = 0; i < 26; i++)
            lst2[i] = "" + (char)('A' + i);

        final Thread t1 = new TestThread(entry, lst, name);
        final Thread t2 = new TestThread(entry, lst2, name);
        t1.start();
        t2.start();
        Thread state = new Thread(){
            public void run() {
                while (t1.isAlive() || t2.isAlive()) {
                    System.out.println("STATE  " + name + " : " + entry.getAttribute(name));
                    long time = System.currentTimeMillis();
                    while (time + 300 > System.currentTimeMillis())
                        ;
                }
                System.out.println("BOTH THREADS ARE DEAD.");
            }
        };
        state.start();
    }

}
