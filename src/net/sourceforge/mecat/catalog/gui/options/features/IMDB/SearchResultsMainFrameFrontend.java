/*
 *  MeCat - Media Catalog more information on
 *  http://mecat.sourceforge.net/
 *  Copyright (C) 2005, Stephan Richard Palm, All Rights Reserved.
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
 * Created on Jun 22, 2005
 * @author Stephan Richard Palm
 */
package net.sourceforge.mecat.catalog.gui.options.features.IMDB;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.Timer;

import net.sourceforge.mecat.catalog.gui.SelectMediaBackend;
import net.sourceforge.mecat.catalog.gui.SelectMediaFrontend;
import net.sourceforge.mecat.catalog.gui.ToolBarUtils;

public class SearchResultsMainFrameFrontend extends SelectMediaFrontend {
    Timer timer;
    
    final static ImageIcon working = ToolBarUtils.loadImage("working.png", "working");;
    public boolean stillWorking = true;
    int lastPosition = 0;

    public SelectMediaBackend newBackend(){
        return new SearchResultMediaBackend(this);
    }
    
    public SearchResultsMainFrameFrontend() {
        init();
    }

    public SearchResultsMainFrameFrontend(Dialog dialog) {
        super(dialog);
        init();
    }
        
    public SearchResultsMainFrameFrontend(Frame frame) {
        super(frame);
        init();
    }
    
	public void init() {
        
        timer = new Timer(20, new ActionListener(){
            public void actionPerformed(ActionEvent arg0) {
                Graphics g = getGraphics();
                if (g == null) {
                    System.out.println("Graphics null");
                    return;
                }
                if (working == null) {
                    System.out.println("working null");
                    return;
                }
                Insets insets = getInsets();
                if (insets == null) {
                    System.out.println("Insets null");
                    return;
                }
                
                
                int available_width = getSize().width - insets.left - insets.right;
                int available_space = 64; //getBackend().spaceRightOfToolbarAndMenu();
                
                if (available_space <= 0)
                    return;
                
                long time;
                
                if (stillWorking) {
                    time = System.currentTimeMillis();
                    time >>= 5;
                    time %= 120;
                    lastPosition = (int) time;
                } else
                    time = lastPosition;
                
                int col = (int)time % 12;
                int row = (int)time / 12;
                if (available_space > 64)
                    available_space = 64;
                g.drawImage(working.getImage(), available_width - available_space ,   insets.top, 
                                                            available_width,         insets.top + 64, 
                                                            col*64, row*64, col*64 + available_space, (row+1)*64, null);
            }
        });
        timer.start();
    
    }
	
}
