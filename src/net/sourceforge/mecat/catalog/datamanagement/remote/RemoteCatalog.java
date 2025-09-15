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
 * Created on May 9, 2006
 * @author Stephan Richard Palm
 * 
 */
package net.sourceforge.mecat.catalog.datamanagement.remote;

import java.awt.Component;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.Vector;
import java.util.concurrent.LinkedBlockingQueue;

import net.sourceforge.mecat.catalog.datamanagement.AttributeEvent;
import net.sourceforge.mecat.catalog.datamanagement.Catalog;
import net.sourceforge.mecat.catalog.datamanagement.CatalogEvent;
import net.sourceforge.mecat.catalog.datamanagement.CatalogListener;
import net.sourceforge.mecat.catalog.datamanagement.Connection;
import net.sourceforge.mecat.catalog.datamanagement.Entry;
import net.sourceforge.mecat.catalog.datamanagement.EntryClearedEvent;
import net.sourceforge.mecat.catalog.datamanagement.EntryEvent;
import net.sourceforge.mecat.catalog.datamanagement.EntryListener;
import net.sourceforge.mecat.catalog.datamanagement.Identifier;
import net.sourceforge.mecat.catalog.datamanagement.SetAttributeEvent;
import net.sourceforge.mecat.catalog.datamanagement.SubEntryEvent;
import net.sourceforge.mecat.catalog.datamanagement.remote.event.RemoteAttributeEvent;
import net.sourceforge.mecat.catalog.datamanagement.remote.event.RemoteCatalogEvent;
import net.sourceforge.mecat.catalog.datamanagement.remote.event.RemoteEntryEvent;
import net.sourceforge.mecat.catalog.datamanagement.remote.event.RemoteEvent;
import net.sourceforge.mecat.catalog.datamanagement.remote.event.RemoteSetAttributeEvent;
import net.sourceforge.mecat.catalog.datamanagement.remote.event.RemoteSubEntryEvent;
import net.sourceforge.mecat.catalog.option.Options;

public class RemoteCatalog extends Catalog {

    final Catalog cacheCatalog;
    final RemoteNetworkConnection remoteNetworkConnection;
    final Map<Long, /*Remote*/Entry> entries = new HashMap<Long, /*Remote*/Entry>();
    final Map</*Remote*/Entry, Long> ids = new HashMap</*Remote*/Entry, Long>();
    
    final Vector<RemoteEvent> eventStack = new Vector<RemoteEvent>();

    final Queue<Object> eventQueue = new LinkedBlockingQueue<Object>();
    
    
    protected synchronized void proceedEvents() {
        Object o = eventQueue.poll();
        if (o instanceof RemoteEvent) {
            processRemoteEvent((RemoteEvent) o );
            return;
        }
        processLocalEvent(o);
        
    }
    
    protected static boolean sameValue(String str1, String str2) {
        if (str1 == null)
            if (str2 == null)
                return true;
            else
                return false;
        if (str2 == null)
            return false;
        return str1.equals(str2);
        
    }
    
    protected void processRemoteEvent(RemoteEvent event) {
        // Remember this event for later check
        eventStack.add(event);
        
        // Is it a CatalogEvent ?
        if (event instanceof RemoteCatalogEvent) {
            RemoteCatalogEvent remoteCatalogEvent = (RemoteCatalogEvent) event;
            long id = remoteCatalogEvent.getEntryID();
            switch (remoteCatalogEvent.getType()) {
            case EntryAdded:
            {
                Entry entry = remoteNetworkConnection.getEntry(id);
                Entry cacheEntry = createEntry(entry.getTypeClassName());
                ids.put(cacheEntry, id);
                entries.put(id, cacheEntry);
                break;
            }
            case EntryRemoved: 
            {
                Entry cacheEntry = entries.get(id);
                removeEntry(cacheEntry);
                break;
            }
            case EntriesRemoved: 
            {
                // TODO repair this
                Entry cacheEntry = entries.get(id);
                removeEntry(cacheEntry);
                break;
            }
            case EntryChanged:
                // This is no primary event,
                // this event is trigged by some other event
                // no need to do anything
            }

            // No need to check for other types of event
            return;
        }
        
        // Is it a EntryEvent ?
        if (event instanceof RemoteEntryEvent) {
            RemoteEntryEvent remoteEntryEvent = (RemoteEntryEvent) event;

            long id = remoteEntryEvent.getSourceID();
            Entry cacheEntry = entries.get(id);
            

            if (remoteEntryEvent instanceof RemoteAttributeEvent) {
                RemoteAttributeEvent attributeEvent = (RemoteAttributeEvent) remoteEntryEvent;

                synchronized (cacheEntry.getAttributeSynchronizationObject(attributeEvent.getName())) {
                    String oldValue = cacheEntry.getAttribute(attributeEvent.getName(), attributeEvent.getLanguage());

                    if (sameValue(oldValue, attributeEvent.getOldValue())) {
                        eventStack.add(attributeEvent);
                        cacheEntry.setAttribute(attributeEvent.getName(), attributeEvent.getNewValue(), attributeEvent.getLanguage());

                        // No need to check for other types of event
                        return;
                    }
                    
                    if (remoteNetworkConnection instanceof ServerRemoteNetworkConnection) {
                        // The server is allways right. 
                        // A difference between the value from the server and the
                        // oldvalue from the event means, that the client is wrong.
                        // But the client will see this by him self, so there is 
                        // nothing more to do.
                        // TODO notify the user about concurrent modification
                        return;
                    } else if (remoteNetworkConnection instanceof ClientRemoteNetworkConnection) {
                        if (sameValue(oldValue, attributeEvent.getNewValue())) {
                            // The server did at the same time the same changement.
                            // Since the server is going to ignore the changerequest from the client.
                            // And since both now have the same data, there is nothing more to do.
                            // TODO notify the user about concurrent modification
                            return;
                        }

                        // Since the value on the clientside is wrong, we could as well say
                        // the server told us to change our value to the right one,
                        // without the need to communicate with the server.
                        RemoteAttributeEvent rae = new RemoteAttributeEvent(attributeEvent.getSourceID(), attributeEvent.getName(), 
                                attributeEvent.getLanguage(), oldValue, attributeEvent.getNewValue());
                        eventStack.add(rae);
                        cacheEntry.setAttribute(attributeEvent.getName(), attributeEvent.getNewValue(), attributeEvent.getLanguage());
                        // TODO notify the user about concurrent modification
                        return;
                    }
                }
                
                return;
                
            } else if (remoteEntryEvent instanceof RemoteSetAttributeEvent) {
                RemoteSetAttributeEvent setAttributeEvent = (RemoteSetAttributeEvent) remoteEntryEvent;

            } else if (remoteEntryEvent instanceof RemoteSubEntryEvent) {
                RemoteSubEntryEvent subEntryEvent = (RemoteSubEntryEvent) remoteEntryEvent;
                
            } else
                (new Exception(Options.getI18N(RemoteCatalog.class).getString("Unknown remote entry event type."))).printStackTrace();
            
            
            
            
            
            // No need to check for other types of event
            return;
        }
        
    }
    
    protected void processLocalEvent(Object o) {

        if (o instanceof CatalogEvent) {
            CatalogEvent event = (CatalogEvent) o;

            // What entry are we talking about ?
            Entry entry = event.getEntry();
            
            // Get id for entry
            Long id = ids.get(entry);
            
            // If the entry has no id, this means
            // the entry was created localy.
            // This means we have to ask the server
            // for a id.
            if (id == null) {
                if (event.getType() == CatalogEvent.CatalogEventType.EntryAdded) {
                    id = remoteNetworkConnection.getNewID();
                    ids.put(entry, id);
                    entries.put(id, entry);
                } else {
                    (new Exception(Options.getI18N(RemoteCatalog.class).getString("Id for a entry event is wrong."))).printStackTrace();
                    return;
                }
            }
            
            // Create remote Event.
            RemoteEvent remoteEvent = new RemoteCatalogEvent(event.getType(), id);
            
            // Did we recieve the event from the network
            if (eventStack.contains(remoteEvent))
                eventStack.remove(remoteEvent);
            else
                remoteNetworkConnection.fireEvent(remoteEvent);
            
            if (event.getType() == CatalogEvent.CatalogEventType.EntryRemoved) {
                ids.remove(id);
                entries.remove(entry);
            }
            
            // No need to check for other types of event
            return;
        }
        
        if (o instanceof EntryEvent) {
            EntryEvent entryEvent = (EntryEvent) o;
            

            // What entry are we talking about ?
            Entry entry = entryEvent.getSource();
            
            // Get id for entry
            Long id = ids.get(entry);
            
            // If the entry has no id, this means
            // something is wrong
            if (id == null) {
                (new Exception(Options.getI18N(RemoteCatalog.class).getString("Id for a entry event is wrong."))).printStackTrace();
                System.err.println("This can only happen if at the same time this client removed an entry, it has been changed somewhere else.");
                return;
            }

            RemoteEvent remoteEvent = null;
            
            if (o instanceof AttributeEvent) {
                AttributeEvent event = (AttributeEvent) o;

                // Create remote Event.
                remoteEvent = new RemoteAttributeEvent(id, event.getName(), event.getLanguage(), event.getOldValue(), event.getNewValue());
            } else if (o instanceof SetAttributeEvent) {
                SetAttributeEvent event = (SetAttributeEvent) o;

                // Create remote Event.
                remoteEvent = new RemoteSetAttributeEvent(event.getType(), id, event.getName(), event.getLanguage(), event.getValue());
            } else if (o instanceof SubEntryEvent) {
                SubEntryEvent event = (SubEntryEvent) o;

                // Get id for sub-entry
                Long subID = ids.get(event.getEntry());

                // If the entry has no id, this means
                // the sub entry was created localy.
                // This means we have to ask the server
                // for an id.
                if (subID == null) {
                    subID = remoteNetworkConnection.getNewID();
                    ids.put(event.getEntry(), subID);
                    entries.put(subID, event.getEntry());
                }

                // Create remote Event.
                if (event.getType() == SubEntryEvent.SubEntryEventType.Create)
                    remoteEvent = new RemoteSubEntryEvent(id, event.getName(), subID);
                else
                    remoteEvent = new RemoteSubEntryEvent(id, subID);
            } else
                (new Exception(Options.getI18N(RemoteCatalog.class).getString("Unknown entry event type."))).printStackTrace();
            
            // Did we recieve the event from the network
            if (eventStack.contains(remoteEvent))
                eventStack.remove(remoteEvent);
            else
                remoteNetworkConnection.fireEvent(remoteEvent);

            // No need to check for other types of event
            return;
        }
    }
    
    final EntryListener entryListener = new EntryListener() {

        public void setAttributeAdded(SetAttributeEvent event) {
            eventQueue.add(event);
            proceedEvents();
        }

        public void setAttributeRemoved(SetAttributeEvent event) {
            eventQueue.add(event);
            proceedEvents();
        }

        public void subEntryCreated(SubEntryEvent event) {
            event.getEntry().addEntryListener(entryListener);

            eventQueue.add(event);
            proceedEvents();
        }

        public void subEntryRemoved(SubEntryEvent event) {
            event.getEntry().removeEntryListener(entryListener);

            eventQueue.add(event);
            proceedEvents();
        }

        public void subEntriesRemoved(SubEntryEvent event) {
            for (Entry entry : event.getEntries())
                entry.removeEntryListener(entryListener);

            eventQueue.add(event);
            proceedEvents();
        }

        public void attributeSet(AttributeEvent event) {
            eventQueue.add(event);
            proceedEvents();
        }

        public void setAttributeCleared(SetAttributeEvent event) {
            eventQueue.add(event);
            proceedEvents();
        }

        public void entryCleared(EntryClearedEvent event) {
            // TODO Auto-generated method stub
            
        }
        
    };

    public RemoteCatalog(final Catalog cacheCatalog, final RemoteNetworkConnection remoteNetworkConnection) {
        this.cacheCatalog = cacheCatalog;
        this.remoteNetworkConnection = remoteNetworkConnection;
        
        remoteNetworkConnection.addRemoteEventListener(new RemoteEventListener() {
            public void eventOccurred(RemoteEvent event) {
                eventQueue.add(event);
                proceedEvents();
                
            }
        });

        
        cacheCatalog.addCatalogListener(new CatalogListener() {

            public void entryAdded(CatalogEvent event) {
                eventQueue.add(event);
                proceedEvents();
                
                event.getEntry().addEntryListener(entryListener);
            }

            public void entryRemoved(CatalogEvent event) {
                eventQueue.add(event);
                proceedEvents();

                event.getEntry().removeEntryListener(entryListener);
            }

            public void entriesRemoved(CatalogEvent event) {
                eventQueue.add(event);
                proceedEvents();

                event.getEntry().removeEntryListener(entryListener);
            }

            public void optionAdded(CatalogEvent event) {
                eventQueue.add(event);
                proceedEvents();
                
                event.getEntry().addEntryListener(entryListener);
            }

            public void optionRemoved(CatalogEvent event) {
                eventQueue.add(event);
                proceedEvents();

                event.getEntry().removeEntryListener(entryListener);
            }

            public void optionsRemoved(CatalogEvent event) {
                eventQueue.add(event);
                proceedEvents();

                event.getEntry().removeEntryListener(entryListener);
            }

            public void entryChanged(EntryEvent event) {
                eventQueue.add(event);
                proceedEvents();
            }
            
        });
    }
    
    
    
    
    
    @Override
    public void forgetSaveCatalogConnection() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public boolean saveCatalog() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean saveAsCatalog(Connection connection) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean canSave() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean unsavedChanges() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public RemoteConnection getSaveCatalogConnection(Component parentComponent) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public RemoteConnection getConnection() {
        // TODO Auto-generated method stub
        return null;
    }

    
    
    @Override
    public void setConnection(Connection connection) {
        // TODO Auto-generated method stub
        
    }

//    @Override
//    public void setLanguage(Locale language) {
//        // TODO Auto-generated method stub
//        
//    }
//
//    @Override
//    public Locale getLanguage() {
//        // TODO Auto-generated method stub
//        return null;
//    }
//
//    @Override
//    public void setDescription(String Description, Locale language) {
//        // TODO Auto-generated method stub
//        
//    }
//
//    @Override
//    public String getDescription(Locale language) {
//        // TODO Auto-generated method stub
//        return null;
//    }
//
//    @Override
//    public void setName(String name, Locale language) {
//        // TODO Auto-generated method stub
//        
//    }
//
//    @Override
//    public String getName(Locale language) {
//        // TODO Auto-generated method stub
//        return null;
//    }

    @Override
    public Entry getGeneralInformationEntry() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public /*Remote*/Entry createEntry(String type) {
        
        Entry entry = cacheCatalog.createEntry(type);

        if (entry == null)
            return null;
        
        return entry;
    }

    @Override
    public void removeEntry(Entry entry) {

        cacheCatalog.removeEntry(entry);
        
    }

    @Override
    public Iterator< ? extends Entry> getIterator() {

        return cacheCatalog.getIterator();
    }

    @Override
    public RemoteEntry createOption(String name) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void removeOption(Entry option) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void removeOption(String name) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public Vector< ? extends Entry> getOptions(String name) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Entry getOption(String name) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Iterator< ? extends Entry> getOptionIterator() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getVersion() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void setVersion(int i) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setUnchanged() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public Entry getEntry(Identifier identifier) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Entry getOption(Identifier identifier) {
        // TODO Auto-generated method stub
        return null;
    }


}
