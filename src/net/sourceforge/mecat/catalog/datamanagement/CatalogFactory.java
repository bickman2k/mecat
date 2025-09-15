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

/**  Created on Jul 26, 2004
 *  @author Stephan Richard Palm
 * 
 * This class is neccessary to create and open catalogs.
 * While creating a new catalog it has to use Util.initialize befor returning it.
 * And while opening it has to give the catalog to Util.update befor returning it.
 * 
 * This is the only class referenced directly by the rest of the programm.
 * If you want to create a new choice for storing the information
 * this class is the way to deal with it.
 * 
 * For a working implementation of this class you have to implement the 3 following
 * functions:
 * <ol>
 * 	<li>{@link #createCatalog()}</li>
 *  <li>{@link #getOpenCatalogConnection()}</li>
 *  <li>{@link #openCatalog(Connection)}</li>
 * </ol>
 * General information on how to create a datamanagement look at the description in
 * {@link net.sourceforge.mecat.catalog.datamanagement}
 */

package net.sourceforge.mecat.catalog.datamanagement;

import java.awt.Component;

public interface CatalogFactory {


	/**
	 * The implementation of this function should create and return a new Catalog.
	 * @return a new empty Catalog
	 */
	public abstract Catalog createCatalog(Component parentComponent);
    /**
     * The implementation of this function should create and return a new Catalog
     * at the position defined by connection.
     * @return a new empty Catalog
     */
    public abstract Catalog createCatalog(Connection connection);
	/**
	 * The implementation of this function should gather all information 
	 * neccessary to open an existent Catalog. Most likely this will be
	 * an OpenFileDialog. The information gathered will be return.
	 * @return a Connection to an existing Catalog
	 */
	public abstract Connection getOpenCatalogConnection(Component parentComponent);
	/**
	 * The implementation of this function should open the catalog
	 * specified by the connection value submitted. If it is not possible to
	 * open the specified Catalog it should return a null.
	 * 
	 * @param connection The connection leading to the catalog that should be opened
	 * @return the catalog that belongs to connection if possible else null
	 */
	public abstract Catalog openCatalog(Connection connection);
	
}
