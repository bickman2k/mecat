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
 * Created on Nov 1, 2006
 * @author Stephan Richard Palm
 * 
 */
package net.sourceforge.mecat.catalog.datamanagement.changelog;

import static net.sourceforge.mecat.catalog.datamanagement.changelog.PreservedEvent.PreservedEventType.Entry_AttributeEvent;

import java.awt.Component;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.tree.TreeModel;

import net.sourceforge.mecat.catalog.datamanagement.Catalog;
import net.sourceforge.mecat.catalog.datamanagement.CatalogEvent;
import net.sourceforge.mecat.catalog.datamanagement.CatalogListener;
import net.sourceforge.mecat.catalog.datamanagement.Connection;
import net.sourceforge.mecat.catalog.datamanagement.EntryEvent;
import net.sourceforge.mecat.catalog.datamanagement.Util;
import net.sourceforge.mecat.catalog.option.Options;

public class ChangeLog {

    /**
     * This is the number that the next transaction will get.
     * By the way this is of course the number of transactions
     * that have been opened.
     */
    int transActionCounter = 0;
    /**
     * A stack where all open transactions are included.
     */
    Stack<Transaction> openTransactions = new Stack<Transaction>();
    /**
     * Main transaction. This is the mother of all transactions.
     * All transactions are part of this transaction.
     */
    Transaction mainTransaction = new Transaction(-2, Options.getI18N(ChangeLog.class).getString("Main"), false, true);
    /**
     * This is main node, any node is a subnode of this node.
     * This node represents the main transaction.
     */
    ChangeTreeTransactionNode mainNode = null;

    /**
     * This node is the node that is currently in use.
     * If a  new event comes in or a new transaction is opened,
     * it will become a child of this transaction.
     */
    ChangeTreeTransactionNode currentNode = null;

    /**
     * Mapping between working catalog and backups for
     * reconstruction/undo.
     *
     */
    Map<Catalog, Catalog> backups = new LinkedHashMap<Catalog, Catalog>();
    
    /**
     * Listeners that the changelog uses to get the changes of the catalogs
     */
    Map<Catalog, CatalogListener> listeners = new LinkedHashMap<Catalog, CatalogListener>();

    /**
     * Listeners that Listings use to get informed of an undo
     */
    Map<Catalog, List<UndoListener>> undoListeners = new LinkedHashMap<Catalog, List<UndoListener>>();

    /**
     * Listeners that the gui uses to see a change of open and user changed catalogs.
     */
    Vector<SaveStateListener> saveStateListeners = new Vector<SaveStateListener>();

    /**
     * Last save wile having lastSave amount of steps
     */
    int lastSave = 0;
    
    
    public ChangeLog() {
        init();
    }

    protected void init() {
        mainNode = new ChangeTreeTransactionNode(null, mainTransaction);
        currentNode = mainNode;
        openTransactions.push(mainTransaction);
        lastSave = 0;
    }
    
    
    public void clear() {
        // Remove listeners from the catalogs
        for (Map.Entry<Catalog, Catalog> entry : backups.entrySet()) 
            entry.getValue().removeCatalogListener(listeners.get(entry.getValue()));
        
        // Remove all stored information
        backups.clear();
        listeners.clear();
        undoListeners.clear();
        openTransactions.clear();

        init();
        
    }
    
    public void addUndoListener(Catalog catalog, UndoListener undoListener) {
        List<UndoListener> list = undoListeners.get(catalog);
        if (list == null) {
            list = new Vector<UndoListener>();
            undoListeners.put(catalog, list);
        }
        list.add(undoListener);
    }
    
    
    public void addSaveStateListener(SaveStateListener saveStateListener) {
        saveStateListeners.add(saveStateListener);
    }
    
    public void removeSaveStateListener(SaveStateListener saveStateListener) {
        saveStateListeners.remove(saveStateListener);
    }
    
    public void fireSaveStateChanged(){
        for (SaveStateListener saveStateListener : saveStateListeners)
            saveStateListener.saveStateChanged();
    }
    
    /**
     * Opens a new transaction and returns an id for the transaction.
     * 
     * @param name
     * @return
     */
    public int openTransaction(String name, boolean atom, boolean userInvoked){
        // remember the counter we want to take for the new transaction
        int ret = transActionCounter;

        // increase the counter for the next transaction
        transActionCounter++;
        
        // Children can only be user invoked if there parents are
        // user invoked
        if (!currentNode.transaction.userInvoked)
            userInvoked = false;
        
        // The new transaction that is opened
        Transaction trns = new Transaction(ret, name, atom, userInvoked);
        
        // push a transaction with the "id" ret and the "name" name
        // on the open transaction stack
        openTransactions.push(trns);

        // Create a new node as child of the current node
        ChangeTreeTransactionNode oldNode = currentNode;
        currentNode = new ChangeTreeTransactionNode(oldNode, trns);
        oldNode.add(currentNode);

        // return the id corresponding to the transaction
        // now at the head of the open transaction stack
        return ret;
    }
    public void closeTransaction(int id) {
        // Remove the closed transaction from the stacke
        Transaction tr = openTransactions.pop();

        // Check if the closed transaction corresponds to the
        // the transaction taken from the stacke
        // else some debuggin will be necessary
        if (id != tr.id)
            System.err.println(Options.getI18N(ChangeLog.class).getString("Transaction error (Id:[ID], Name:[NAME], Atom:[ATOM], UserInvoked:[USER])")
                    .replaceAll("\\[ID\\]", "" + tr.id)
                    .replaceAll("\\[NAME\\]", tr.name)
                    .replaceAll("\\[ATOM\\]", "" + tr.atom)
                    .replaceAll("\\[USER\\]", "" + tr.userInvoked));

        // Simplify the closed transaction
        simplify(currentNode);
        
        // Remove transaction if transaction is empty
        if (currentNode.isEmpty())
            currentNode.getParentNode().remove(currentNode);
        
        // The parent node of the current node should be the node that corresponds
        // to the transaction that now is the head of the open transactions stack
        currentNode = currentNode.getParentNode();
    }
    
    private void simplify(ChangeTreeTransactionNode tNode) {
        for (int i = 0; i < tNode.size() - 1; i++) {
            ChangeTreeNode n1 = tNode.get(i);
            ChangeTreeNode n2 = tNode.get(i + 1);
            if (!(n1 instanceof ChangeTreeEventNode))
                continue;
            if (!(n2 instanceof ChangeTreeEventNode))
                continue;
            ChangeTreeEventNode e1 = (ChangeTreeEventNode) n1;
            ChangeTreeEventNode e2 = (ChangeTreeEventNode) n2;
            if (sameAttributeEvent(e1.getEvent(), e2.getEvent())){
                e1.event.attributeNewValue = e2.event.attributeNewValue;
                tNode.remove(i + 1);
                // Check the current position again, to see if it can
                // be merged with another one.
                i--;
            }
        }
    }

    protected boolean sameAttributeEvent(PreservedEvent e1, PreservedEvent e2) {
        // Are both an attribute event
        if (e1.type != Entry_AttributeEvent)
            return false;
        if (e2.type != Entry_AttributeEvent)
            return false;
        // Are they both from the same entry
        if (!e1.entry.equals(e2.entry))
            return false;
        // Are the attribute names the same
        if (!e1.attributeName.equals(e2.attributeName))
            return false;
        // Aret the attributes for the same language
        if (e1.attributeLanguage == null && e2.attributeLanguage == null)
            return true;
        if (e1.attributeLanguage == null)
            return false;
        if (e2.attributeLanguage == null)
            return false;
        return e1.attributeLanguage.equals(e2.attributeLanguage);
    }
    
    public void addCatalog(final Catalog catalog) {
        if (backups.containsKey(catalog))
            return;
        
        CatalogListener listener = getCatalogListener(catalog);
        catalog.addCatalogListener(listener);
        Catalog backupCatalog = Options.stdFactory.createCatalog((Component)null);
        Util.copyCatalog(catalog, backupCatalog);
        backups.put(catalog, backupCatalog);
        listeners.put(catalog, listener);
    }
    
    protected CatalogListener getCatalogListener(final Catalog catalog) {
        return new CatalogListener(){
            public void entryAdded(CatalogEvent event) {
                currentNode.add(new ChangeTreeEventNode(currentNode, catalog, new PreservedEvent(event)));
                if (currentNode.getTransaction().isUserInvoked())
                    fireSaveStateChanged();
            }

            public void entryRemoved(CatalogEvent event) {
                currentNode.add(new ChangeTreeEventNode(currentNode, catalog, new PreservedEvent(event)));
                if (currentNode.getTransaction().isUserInvoked())
                    fireSaveStateChanged();
            }

            public void entriesRemoved(CatalogEvent event) {
                currentNode.add(new ChangeTreeEventNode(currentNode, catalog, new PreservedEvent(event)));
                if (currentNode.getTransaction().isUserInvoked())
                    fireSaveStateChanged();
            }

            public void optionAdded(CatalogEvent event) {
                currentNode.add(new ChangeTreeEventNode(currentNode, catalog, new PreservedEvent(event)));
                if (currentNode.getTransaction().isUserInvoked())
                    fireSaveStateChanged();
            }

            public void optionRemoved(CatalogEvent event) {
                currentNode.add(new ChangeTreeEventNode(currentNode, catalog, new PreservedEvent(event)));
                if (currentNode.getTransaction().isUserInvoked())
                    fireSaveStateChanged();
            }

            public void entryChanged(EntryEvent event) {
                currentNode.add(new ChangeTreeEventNode(currentNode, catalog, new PreservedEvent(event)));
                if (currentNode.getTransaction().isUserInvoked())
                    fireSaveStateChanged();
            }

            public void optionsRemoved(CatalogEvent event) {
                currentNode.add(new ChangeTreeEventNode(currentNode, catalog, new PreservedEvent(event)));
                if (currentNode.getTransaction().isUserInvoked())
                    fireSaveStateChanged();
            }
        };
    }
    
    
    public TreeModel getTreeModel(){
        return new ChangeLogTreeModel(mainNode);
    }


    public int getNumberSteps() {
        return mainNode.getNumberSteps();
    }


    
    public void undo(int numSteps) {
        // If an undo removes more steps then done since last save
        // then its saveable until next save
        if (getNumberSteps() - numSteps < lastSave)
            lastSave = -1;
        
        Map<Catalog, Catalog> newBackups = new LinkedHashMap<Catalog, Catalog>();
        Map<Catalog, Catalog> oldToNew = new LinkedHashMap<Catalog, Catalog>();
        Vector<UndoEvent> events = new Vector<UndoEvent>();
        
        for (Map.Entry<Catalog, Catalog> entry : backups.entrySet()) {

            // Remove the catalog listener that listens for this catalog.
            entry.getKey().removeCatalogListener(listeners.get(entry.getKey()));
            listeners.remove(entry.getKey());
            
            // Set catalog to selected configuration
            Catalog catalog = Options.stdFactory.createCatalog((Component)null);
            Util.copyCatalog(entry.getValue(), catalog);
            
            // Get the connection assigned to the catalog
            // the connection is no subject of undo
            // therefor the connection of the current catalog is taken.
            catalog.setConnection(entry.getKey().getConnection());
            
//            ShowCatalogFrontend.showShowCatalogFrontend(null, catalog, null);

            // Redo the steps for the catalog copy
            redo(entry.getKey(), catalog, 0, getNumberSteps() - numSteps);

            // Add listener to the new catalog
            CatalogListener listener = getCatalogListener(catalog);
            catalog.addCatalogListener(listener);
            listeners.put(catalog, listener);
            
            // Put map between new catalog and the old backup
            newBackups.put(catalog, entry.getValue());
            
            oldToNew.put(entry.getKey(), catalog);
        }

        // Adapt the changeLog
        mainNode = copyRek(mainNode, null, oldToNew, 0, getNumberSteps() - numSteps);
        currentNode = mainNode;
        
        // Associate the new changelog to the new catalog
        backups = newBackups;
        
        for (Map.Entry<Catalog, Catalog> entry : oldToNew.entrySet()) {
            UndoEvent event = new UndoEvent(entry.getKey(), entry.getValue());
            List<UndoListener> listeners = undoListeners.get(event.getOldCatalog());
            if (listeners != null)
                for (UndoListener undoListener : listeners)
                    undoListener.undone(event);
            // Remove those listeners since they are not used anymore
            undoListeners.remove(event.getOldCatalog());
        }
        
        openTransactions.clear();
        openTransactions.push(mainTransaction);

        fireSaveStateChanged();
    }

    public boolean unsavedChanges() {
        // We already know that we can save
        if (lastSave == -1)
            return true;
        
        // We are at the same state as the last save
        // therefor there is no reason to save
        if (lastSave == getNumberSteps())
            return false;

        // There have been steps after the last save
        // the question is: do the steps are user invoked?
        return mainNode.hasUserInvoked(lastSave);
    }
    
    public List<Catalog> getChangedCatalogs() {
        // We already know that we can save
        if (lastSave == -1)
            return mainNode.getCatalogs();
            
        // We are at the same state as the last save
        // therefor there is no reason to save
        if (lastSave == getNumberSteps())
            return new Vector<Catalog>();

        // There have been steps after the last save
        // the question is: do the steps are user invoked?
        return mainNode.getCatalogs(lastSave);
    }
    
    /**
     * Return true if all catalogs controlled by the changelog,
     * have been saved correctly.
     * 
     * @param component
     * @return
     */
    public boolean save(Component component) throws UnclosedTransactionException {
        
        List<Catalog> catalogs = getChangedCatalogs();

        if (currentNode != mainNode)
            throw new UnclosedTransactionException(
                    Options.getI18N(ChangeLog.class).getString("All transaction need to be closed. [TRANSACTION] still open.")
                                                    .replaceAll("\\[TRANSACTION\\]", openTransactions.peek().name));
        
        // The first loop checks wheter everyone has a connection.
        for (Catalog catalog : catalogs)
            if (!catalog.canSave()) {
                JOptionPane.showMessageDialog(component, 
                        Options.getI18N(ChangeLog.class).getString("Will not save, because catalog [CONNECTION] would not be saved. Check your permission.")
                                                                    .replaceAll("\\[CONNECTION\\]", "" + catalog.getConnection()), 
                        Options.getI18N(ChangeLog.class).getString("Saveing prevented."), 
                        JOptionPane.ERROR_MESSAGE);
                return false;
            }
                

        for (Catalog catalog : catalogs)
            if (!catalog.saveCatalog()) {
                JOptionPane.showMessageDialog(component, 
                        Options.getI18N(ChangeLog.class).getString("The catalog [CONNECTION] could not be saved. Check your permission.")
                                                                    .replaceAll("\\[CONNECTION\\]", "" + catalog.getConnection()), 
                        Options.getI18N(ChangeLog.class).getString("Error while saving."), 
                        JOptionPane.ERROR_MESSAGE);
                return false;
            }
        
        lastSave = getNumberSteps();
        
        fireSaveStateChanged();
        
        return true;
    }
    
    /**
     * Reinvokes the events that have been recorded.
     * Starting with the event at position start until
     * the position end (exclusive the position end).
     * @param catalog
     * @param start
     * @param end
     */
    protected void redo(Catalog source, Catalog dest, int start, int end) {
        redoRek(source, dest, mainNode, start, end);
    }

    
    protected void redoRek(Catalog source, Catalog dest, ChangeTreeNode node, int start, int end) {

        // This if construct allows to decend in the tree structure.
        if (node instanceof ChangeTreeTransactionNode) {
            ChangeTreeTransactionNode tNode = (ChangeTreeTransactionNode) node;
            
            if (tNode.transaction.isAtom()) {
                for (ChangeTreeNode childNode : tNode) 
                    redoRek(source, dest, childNode, 0, childNode.getNumberSteps());
                return;
            }
            
            int pos = 0;
            for (ChangeTreeNode childNode : tNode) {
                // If every step needed is already done
                // end the for loop
                if (end <= pos)
                    break;

                // Amount of steps that can be done in the subnode
                int size = childNode.getNumberSteps();
                
                // Rekusive if for the childnode there are steps that must be done
                if (start < pos + size)
                    redoRek(source, dest, childNode, start - pos, Math.min(end - pos, size));

                // Shift the position after the childNode
                pos += size;

            }
            return;
        } 

        if (node instanceof ChangeTreeEventNode) {
            ChangeTreeEventNode eNode = (ChangeTreeEventNode) node;
            if (start > 0)
                return;
            if (end <= 0)
                return;
            
            // Only execute the steps that are part of the right catalog
            if (eNode.getSource().equals(source))
                eNode.execute(dest);
            
//            ShowCatalogFrontend.showShowCatalogFrontend(null, dest, null);
        }
    }
    
    protected <T extends ChangeTreeNode> T copyRek(T source, ChangeTreeTransactionNode parent, Map<Catalog, Catalog> mapping, int start, int end) {

        if (source instanceof ChangeTreeTransactionNode) {
            ChangeTreeTransactionNode tNode = (ChangeTreeTransactionNode)source;
            ChangeTreeTransactionNode tNodeCopy = new ChangeTreeTransactionNode(parent, tNode.getTransaction());

            // Atoms are copied at whole  
            if (tNode.getTransaction().isAtom()) {
                for (ChangeTreeNode childNode : tNode) {
                    ChangeTreeNode childNodeCopy = copyRek(childNode, tNodeCopy, mapping, 0, childNode.getNumberSteps());
                    tNodeCopy.add(childNodeCopy);
                }
                return (T) tNodeCopy;
            }
            
            int pos = 0;

            for (ChangeTreeNode childNode : tNode) {
                // If every step needed is already there
                // end the for loop
                if (end <= pos)
                    break;

                // Amount of steps that are in the subnode
                int size = childNode.getNumberSteps();

                // Rekusive if for the childnode there are steps that will be copied
                if (start < pos + size) {
                    ChangeTreeNode childNodeCopy = copyRek(childNode, tNodeCopy, mapping, start - pos, Math.min(end - pos, size));
                    tNodeCopy.add(childNodeCopy);
                }

                // Shift the position after the childNode
                pos += size;
            }
            return (T) tNodeCopy;
        }
        
        if (source instanceof ChangeTreeEventNode) {
            ChangeTreeEventNode eNode = (ChangeTreeEventNode) source;
            return (T) new ChangeTreeEventNode(parent, mapping.get(eNode.getSource()), eNode.getEvent());
        }
        
        return null;
    }


    
    public int indexOf(ChangeTreeNode node) {
        ChangeTreeTransactionNode pNode = node.getParentNode();
        
        if (pNode == null)
            return 0;
        
        int index = indexOf(pNode);
        for (ChangeTreeNode child : pNode) {
            if (child == node)
                return index;
            index += child.getNumberSteps();
        }
        
        return index;
    }

    public Catalog getCatalog(Connection externCatalog) {
        for (Map.Entry<Catalog, Catalog> entry : backups.entrySet()) {
            Connection connection = entry.getKey().getConnection();
            if (connection != null && connection.equals(externCatalog))
                return entry.getKey();
        }
        return null;
    }
    
}
