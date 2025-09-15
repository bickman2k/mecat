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
 * Created on Jul 29, 2006
 * @author Stephan Richard Palm
 * 
 */
package net.sourceforge.mecat.catalog.filesystem;

import java.io.DataOutputStream;
import java.io.IOException;

public class ListWriter implements DetailWriter {

    public int writeDetail(Detail detail, DataOutputStream data) throws IOException {
        
        if (!(detail instanceof DetailList))
            return -1;
        
        DetailList detailList = (DetailList) detail;

        int len = getLength(detailList);
        
        // Write len of the list detail
        data.writeInt(len);
        
        // Write len of the list detail
        data.writeInt(detail.getType().staticOrdinal());
   
        // Write List version
        data.writeInt(0);
        
        // Write the size of the list
        data.writeInt(detailList.size());

        // Write list entries
        for (Detail subDetail : detailList){
            int subLen = subDetail.getType().getDetailWriter().getLength(subDetail);
            if (subLen != -1) 
                subDetail.getType().getDetailWriter().writeDetail(subDetail, data);
        }
        
        
        return len;
    }

    public int getLength(Detail detail) {
        
        if (!(detail instanceof DetailList))
            return -1;
        
        DetailList detailList = (DetailList) detail;

        return getLength(detailList);
    }

    public int getLength(DetailList detailList) {
        int len = 4 /* 4 for size */ +  4 /*for the type*/ +  4 /*for version and*/ + 4 /*for the list size*/;
        
        for (Detail subDetail : detailList){
            int subLen = subDetail.getType().getDetailWriter().getLength(subDetail);
            if (subLen != -1)
                len += subLen;
        }
        
        return len;
    }

}
