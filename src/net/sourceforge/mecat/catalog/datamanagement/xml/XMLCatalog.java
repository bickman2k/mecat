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
 * Created on July 27, 2004
 * @author Stephan Richard Palm
 */
package net.sourceforge.mecat.catalog.datamanagement.xml;

import java.awt.Component;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;

import javax.swing.JFileChooser;

import net.sourceforge.mecat.catalog.datamanagement.Catalog;
import net.sourceforge.mecat.catalog.datamanagement.Connection;
import net.sourceforge.mecat.catalog.datamanagement.Entry;
import net.sourceforge.mecat.catalog.datamanagement.Identifier;
import net.sourceforge.mecat.catalog.datamanagement.IdentifierType;

import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XMLCatalog extends Catalog {

    
    
	/**
	 * This flag indicates the return value for 
	 * the function 
	 * {@link #unsavedChanges()}
	 */
	protected boolean dirty = false;
	
	
	/**
	 * Tag for the options.
	 * 
	 * All options must be a child of this tag.
	 */
	static String OptionsTag = "Options";
	/**
	 * Tag for the entrys.
	 * 
	 * All entrys must be a child of this tag.
	 */
	static String EntrysTag = "Entrys";
    /**
     * Tag for the entrys.
     * 
     * All entrys must be a child of this tag.
     */
    static String GeneralInformationTag = "GeneralInformation";
	
	
	/**
	 * Connection to store the file,
	 * means the filename where to place the xml
	 */
	protected XMLConnection connection = null;

	/**
	 * Representation of the Catalog entries in the dom-xml-tree.
	 */
	Node entriesNode = null;
	/**
	 * Representation of the Options in the dom-xml-tree.
	 */
	Node optionsNode = null;
    /**
     * Representation of the General Information in the dom-xml-tree.
     */
    Node generalInformationNode = null;
	/**
	 * Reverence to the main doc for the dom-xml-tree.
	 */
	Document doc;

	/**
	 * In order to avoid redundent functions
	 * and because it is logically alright
	 * we asume the Catalog to be an Entry too
	 * and use the attribute functions of the entry
	 */
	XMLEntry entry;
    
    /**
     * The Entry for the general information does not change
     * therefor we keep it once here.
     */
	XMLEntry generalInformation;
    
    /**
     * This ensures that for every entry-node there will be only one XML Entry.
     * And this allows a fast index for the entries.
     */
    Map<String, List<XMLEntry>> entryLists = new HashMap<String, List<XMLEntry>>();
    List<XMLEntry> allEntries = null;
//    protected Map<Node, XMLEntry> entriesMap = new LinkedHashMap<Node, XMLEntry>();
    
    /**
     * This ensures that for every option-node there will be only one XML Entry.
     * And this allows a fast index for the entries.
     */
    Map<String, List<XMLEntry>> optionLists = new HashMap<String, List<XMLEntry>>();
    List<XMLEntry> allOptions = null;
    
    /**
     * This function ensures that all entry lists are created.
     */
    public void fillEntryLists() {
        if (allEntries != null)
            return;
        allEntries = new Vector<XMLEntry>();
        
        NodeList childs = entriesNode.getChildNodes();
        for(int i = 0; i < childs.getLength(); i++) {
            Node node = childs.item(i);
            String name = node.getNodeName();
            if (name.equals("#text"))
                continue;
            if (!entryLists.containsKey(name))
                entryLists.put(name, new Vector<XMLEntry>());
            List<XMLEntry> entryList = entryLists.get(name);

            XMLEntry entry = new XMLEntry(this, null, node, entryList.size(), IdentifierType.Entry);
            
            entryList.add(entry);
            allEntries.add(entry);
//            entriesMap.put(node, entry);
        }
    }

    /**
     * This function ensures that all option lists are created.
     */
    public void fillOptionLists() {
        if (allOptions != null)
            return;
        allOptions = new Vector<XMLEntry>();
        
        NodeList childs = optionsNode.getChildNodes();
        for(int i = 0; i < childs.getLength(); i++) {
            Node node = childs.item(i);
            String name = node.getNodeName();
            if (name.equals("#text"))
                continue;
            if (!optionLists.containsKey(name))
                optionLists.put(name, new Vector<XMLEntry>());
            List<XMLEntry> optionList = optionLists.get(name);

            XMLEntry entry = new XMLEntry(this, null, node, optionList.size(), false, IdentifierType.Option);
            optionList.add(entry);
            allOptions.add(entry);
        }
    }

	/**
	 * The XMLCatalog is created upon a XMLNode where the Catalog can 
	 * place all entrys.
	 * 
	 * @param node ParentNode for all entries.
	 */
	protected XMLCatalog(Node catalog_node) {
		this(catalog_node, null);
	}


	
	public XMLConnection getConnection() {
		return connection;
	}
    
    public void setConnection(Connection connection) {
        if (connection instanceof XMLConnection)
            this.connection = (XMLConnection) connection;
    }
	
	
	
	
	public boolean canSave() {
        if (this.connection == null)
            return false;
        
        File file = new File(connection.pos.getFile());
        
        // Check wheter the file exists and is writeable
        // or can be created.
        if (file.exists())
            return file.canWrite();
        else {
            try {
                if (file.createNewFile()) {
                    file.delete();
                    return true;
                } else
                    return false;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
	}


	public boolean saveCatalog() {
		return saveAsCatalog(this.connection);
	}
	
	public Connection getSaveCatalogConnection(Component parentComponent) {
		JFileChooser choose = new JFileChooser();
		choose.setFileFilter(new XMLFiles());
		int returnVal = choose.showSaveDialog(null);
		
		if(returnVal == JFileChooser.APPROVE_OPTION) {
			try {
				if (choose.getSelectedFile().toString().endsWith(".catalog.xml"))
					return new XMLConnection(choose.getSelectedFile().toURL());
				else
					return new XMLConnection(new URL(choose.getSelectedFile().toURL().toString() + ".catalog.xml"));
			} catch (Exception e) {
				// The programm should not get here there make a stacktrace.
				e.printStackTrace();
				// Nevertheless the programm will go one when returned null;
				return null;
			}
		}

		return null;
	}
	
	public boolean saveAsCatalog(Connection connection) {
		if (!(connection instanceof XMLConnection))
			return false;
		
		XMLConnection con = (XMLConnection)connection;
		try{
			Writer writer = new OutputStreamWriter(new FileOutputStream(con.pos.getFile()), "UTF8");
			printNode(writer, doc, "");
			writer.flush();
			writer.close();
			this.connection = con;
		} catch (Exception e){
			// The programm should not get here there make a stacktrace.
			e.printStackTrace();
			// Nevertheless the programm will go one when returned null;
			return false;
		}
		// Database now is saved again
		dirty = false;
		// Everthing worked fine
		return true;
	}
	
	
	
	
	
	
	
	
	
	
	/**
	 * The XMLCatalog is created upon a XMLNode where the Catalog can 
	 * place all entrys.
	 * 
	 * @param node ParentNode for all entries.
	 * @param connection The connection for this catalog.
	 */
	protected XMLCatalog(Node catalog_node, Connection connection) {
		doc = catalog_node.getOwnerDocument();

		NodeList catalog_childs = catalog_node.getChildNodes();
		for(int i = 0; i < catalog_childs.getLength(); i++) {
			Node node = catalog_childs.item(i);
			if (node.getNodeName().compareTo(EntrysTag) == 0) 
				entriesNode = node;
			if (node.getNodeName().compareTo(OptionsTag) == 0) 
				optionsNode = node;
            if (node.getNodeName().compareTo(GeneralInformationTag) == 0) 
                generalInformationNode = node;
		}

		if (this.entriesNode == null) {
			Element entriesNode = doc.createElement(EntrysTag);
			catalog_node.appendChild(entriesNode);
			this.entriesNode = entriesNode;
		}
		if (this.optionsNode == null) {
			Element optionsNode = doc.createElement(OptionsTag);
			catalog_node.appendChild(optionsNode);
			this.optionsNode = optionsNode;
		}
        if (this.generalInformationNode == null) {
            Element generalInformationNode = doc.createElement(GeneralInformationTag);
            catalog_node.appendChild(generalInformationNode);
            this.generalInformationNode = generalInformationNode;
        }
        
		if (connection instanceof XMLConnection)
			this.connection = (XMLConnection)connection;

		entry = new XMLEntry(this, null, doc.getDocumentElement(), 0, false, null);
        generalInformation = new XMLEntry(this, null, generalInformationNode, 0, false, IdentifierType.GeneralInformation);
        
        fillEntryLists();
        fillOptionLists();
	}

    
//	/* (non-Javadoc)
//	 * @see net.sourceforge.mecat.catalog.datamanagement.Catalog#setLanguage(java.util.Locale)
//	 */
//	public void setLanguage(Locale language) {
//		entry.setAttribute("language", language.getLanguage());
//		// Language has been changed. => dirty
//		dirty = true;
//	}
//
//	/* (non-Javadoc)
//	 * @see net.sourceforge.mecat.catalog.datamanagement.Catalog#getLanguage()
//	 */
//	public Locale getLanguage() {
//		if (entry.getAttribute("language") == null)
//			return null;
//			
//		return new Locale(entry.getAttribute("language"));
//	}
//
//	/* (non-Javadoc)
//	 * @see net.sourceforge.mecat.catalog.datamanagement.Catalog#setDescription(java.lang.String, java.util.Locale)
//	 */
//	public void setDescription(String description, Locale language) {
//		entry.setAttribute("description", description, language);
//		// Description has been changed. => dirty
//		dirty = true;
//	}
//
//	/* (non-Javadoc)
//	 * @see net.sourceforge.mecat.catalog.datamanagement.Catalog#getDescription(java.util.Locale)
//	 */
//	public String getDescription(Locale language) {
//		if (entry.getAttribute("description", language) == null)
//			return null;
//		
//		return (entry.getAttribute("description", language).toString());
//	}

    @Override
    public Entry getGeneralInformationEntry() {
        return generalInformation;
    }

    /* (non-Javadoc)
     * @see net.sourceforge.mecat.catalog.datamanagement.Catalog#setDescription(java.lang.String, java.util.Locale)
     */
    public void setVersion(int version) {
        entry.setAttribute("version", "" + version);
        // Description has been changed. => dirty
        dirty = true;
    }

    /* (non-Javadoc)
     * @see net.sourceforge.mecat.catalog.datamanagement.Catalog#getDescription(java.util.Locale)
     */
    public int getVersion() {
        if (entry.getAttribute("version") == null)
            return 0;
        
        try {
            return (Integer.parseInt(entry.getAttribute("version")));
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
        
    }

	/* (non-Javadoc)
	 * @see net.sourceforge.mecat.catalog.datamanagement.Catalog#setName(java.lang.String, java.util.Locale)
	 */
	public void setName(String name, Locale language) {
		entry.setAttribute("name", name, language);
		// Language has been changed. => dirty
		dirty = true;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.mecat.catalog.datamanagement.Catalog#getName(java.util.Locale)
	 */
	public String getName(Locale language) {
		if (entry.getAttribute("name", language) == null)
			return null;
		
		return (entry.getAttribute("name", language).toString());
	}

	public synchronized Entry createEntry(String type) {
		// Return null if type is undefined
		if (Entry.getEasyClassName(type) == null)
			return null;			
		Element element = doc.createElement(Entry.getEasyClassName(type).replaceAll("#", ""));
		entriesNode.appendChild(element);
		
		// New Entry. => dirty
		dirty = true;

        //XMLEntry entry = getEntry(element);
        if (!entryLists.containsKey(element.getNodeName()))
            entryLists.put(element.getNodeName(), new Vector<XMLEntry>());
        List<XMLEntry> entryList = entryLists.get(element.getNodeName());
        
        XMLEntry entry = new XMLEntry(this, null, element, entryList.size(), IdentifierType.Entry);
        entryList.add(entry);
        allEntries.add(entry);
//        entriesMap.put(element, entry);
        
        fireEntryAdded(entry, type);
        
        return entry;
	}

	public synchronized void removeEntry(Entry entry) {
		if (!(entry instanceof XMLEntry))
			return;
        XMLEntry e = (XMLEntry) entry;

        // Get entry list related to this entry
        List<XMLEntry> entryList = entryLists.get(e.node.getNodeName());

        // Remove from entryLists
        int index = entryList.indexOf(e);
        entryList.remove(index);
        for (int i = index; i < entryList.size(); i++)
            entryList.get(i).setNumber(i);
        
        // Remove from catalog
        entriesNode.removeChild(e.node);
        
        // Entry removed. => dirty
        dirty = true;

        // Remove from all entries list (cache)
        allEntries.remove(e);
        
        // Remove from entries mapping (cache)
//        entriesMap.remove(e.node);

        fireEntryRemoved(e);
	}

	public synchronized Iterator<XMLEntry> getIterator() {
        return (new Vector<XMLEntry>(allEntries)).iterator();
	}

	
	
	public synchronized Entry createOption(String name) {
		Element element = doc.createElement(name);
		optionsNode.appendChild(element);

		// New option. => dirty
		dirty = true;
		
        if (!optionLists.containsKey(element.getNodeName()))
            optionLists.put(element.getNodeName(), new Vector<XMLEntry>());
        List<XMLEntry> optionList = optionLists.get(element.getNodeName());
        
        XMLEntry entry = new XMLEntry(this, null, element, optionList.size(), false, IdentifierType.Option);
        optionList.add(entry);
        allOptions.add(entry);
        
        fireOptionAdded(entry, name);
        
        return entry;
	}

	public synchronized void removeOption(Entry entry) {
		if (!(entry instanceof XMLEntry))
			return;
        XMLEntry e = (XMLEntry) entry;

        // Get option list related to this entry
        List<XMLEntry> optionList = optionLists.get(e.node.getNodeName());

        // Remove from optionLists
        int index = optionList.indexOf(e);
        optionList.remove(index);
        for (int i = index; i < optionList.size(); i++)
            optionList.get(i).setNumber(i);
        
        // Remove from catalog
        optionsNode.removeChild(e.node);
        
        // removed option. => dirty
        dirty = true;

        // Remove from all options list (cache)
        allOptions.remove(e);

        fireOptionRemoved(e);
    }

	public synchronized void removeOption(String name) {
        // Get option list related to this name
        List<XMLEntry> optionList = optionLists.get(name);
        
        // Don't do anything if there are no options
        if (optionList == null)
            return;

        // Remove options from catalog
        for (XMLEntry entry : optionList)
            optionsNode.removeChild(entry.node);
            
        // removed options. => dirty
        dirty = true;

        // Remove options from option lists (cache)
        optionLists.put(name, new Vector<XMLEntry>());
        
        // Remove option from all options list (cache)
        allOptions.removeAll(optionList);
        
        fireOptionsRemoved(name);
	}
	
	public synchronized XMLEntry getOption(String name) {
		List<XMLEntry> ops = getOptions(name);
        if (ops == null)
            return null;
		if (ops.size() == 0)
			return null;
		return ops.get(0);
	}

	public synchronized List<XMLEntry> getOptions(String name) {
        List<XMLEntry> list = optionLists.get(name);
        if (list == null)
            return new Vector<XMLEntry>();
        else
            return list;
            
	}

	public synchronized Iterator<XMLEntry> getOptionIterator() {
        return allOptions.iterator();
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	// TODO finde schon von anderem bereitgestellte L?sung hierf?r
	/**
	 * This function prints an XMLNode to an PrintStream
	 * @param out where to put the output
	 * @param node where to get the information from
	 * @param ident tabular String
	 */
	private static void printNode(Writer out, Node node, String ident) throws IOException {
	  switch (node.getNodeType()) {
		case Node.DOCUMENT_NODE:
		  out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n");
		  //recursion over all children
		  NodeList nodes = node.getChildNodes();
		  if (nodes != null) {
			for (int i=0; i < nodes.getLength();i++)  {
			  printNode(out, nodes.item(i), "");
			}
		  }
		  break;
		case Node.ELEMENT_NODE:

		  String name = node.getNodeName();
		  out.write(ident + "<" + name);

		  NamedNodeMap attributes = node.getAttributes();
		  for (int i=0; i<attributes.getLength(); i++) {
			Node current = attributes.item(i);
			out.write(" " + current.getNodeName() +
							 "=\"" + current.getNodeValue() +
							 "\"");
		  }

		  out.write(">");

		  //Rekursion ?ber alle Kinder
		  NodeList children = node.getChildNodes();
		  if (children != null) {
			for (int i=0; i < children.getLength();i++)  {
			  printNode(out, children.item(i), ident);
			}
		  }

		  out.write(ident + "</" + name + ">");

		  break;
		case Node.TEXT_NODE:
		case Node.CDATA_SECTION_NODE:
		  out.write(convert(node.getNodeValue()));
		  break;

		case Node.PROCESSING_INSTRUCTION_NODE:
		  out.write("<?" + node.getNodeName() +
							 " " + node.getNodeValue() +
							 "?>");
		  break;

		case Node.ENTITY_REFERENCE_NODE:
		  break;

		case Node.DOCUMENT_TYPE_NODE:
		  DocumentType docType = (DocumentType)node;
		  out.write("<!DOCTYPE " + docType.getName());

		  if (docType.getPublicId() != null) 
			out.write(" PUBLIC \"" +	 docType.getPublicId() + "\" ");
		  
		  if (docType.getSystemId() != null)
			out.write(" SYSTEM \"" + docType.getSystemId() + "\"");
		  
		  out.write(">\r\n");
		  break;
	  }
	}

	/**
	 * Converts a String to an UTF-8-XML conform String.
     * See the Predefined Entities at <a href="http://www.w3.org/TR/2004/REC-xml11-20040204/">http://www.w3.org/TR/2004/REC-xml11-20040204/</a>
	 * @param Value input String
	 * @return converted String
	 */
	public static String convert(String Value) {
      StringBuffer ret = new StringBuffer();  
	  for(int i = 0; i < Value.length(); i++)
          switch (Value.charAt(i)) {
          case '<':
              ret.append("&lt;");
              break;
          case '>':
              ret.append("&gt;");
              break;
          case '&':
              ret.append("&amp;");
              break;
          case '\'':
              ret.append("&apos;");
              break;
          case '"':
              ret.append("&quot;");
              break;
          default:
              ret.append(Value.charAt(i));
          }
      
      return ret.toString();
	}



	public boolean unsavedChanges() {
		return dirty;
	}

    @Override
    public void setUnchanged() {
        dirty = false;
    }


	/* (non-Javadoc)
	 * @see net.sourceforge.mecat.catalog.datamanagement.Catalog#forgetSaveCatalogConnection()
	 */
	public void forgetSaveCatalogConnection() {
		connection = null;
	}

    @Override
    public XMLEntry getEntry(Identifier identifier) {
        if (!(identifier instanceof XMLIdentifier))
            return null;
        
        XMLIdentifier id = (XMLIdentifier) identifier;
        List<XMLEntry> entryList = entryLists.get(id.get(0).name);
        if (entryList == null)
            return null;
        if (entryList.size() <= id.get(0).number)
            return null;
        
        XMLEntry entry = entryList.get(id.get(0).number);
        if (id.size() == 1)
            return entry;
        
        return entry.getEntry(id);
    }

    @Override
    public XMLEntry getOption(Identifier identifier) {
        if (!(identifier instanceof XMLIdentifier))
            return null;
        
        XMLIdentifier id = (XMLIdentifier) identifier;
        List<XMLEntry> optionList = optionLists.get(id.get(0).name);
        if (optionList == null)
            return null;
        if (optionList.size() <= id.get(0).number)
            return null;
        
        XMLEntry entry = optionList.get(id.get(0).number);
        if (id.size() == 1)
            return entry;
        
        return entry.getEntry(id);
    }
}
