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
 * Created on Jan 3, 2006
 * @author Stephan Richard Palm
 * 
 */
package net.sourceforge.mecat.catalog.gui.about;

import java.awt.BorderLayout;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.sourceforge.mecat.catalog.export.ExportChoice;
import net.sourceforge.mecat.catalog.gui.MainFrameBackend;
import net.sourceforge.mecat.catalog.gui.splasher.Splasher;
import net.sourceforge.mecat.catalog.i18n.util.SimpleLocalButton;
import net.sourceforge.mecat.catalog.option.LocalListener;
import net.sourceforge.mecat.catalog.option.LocalListenerEvent;
import net.sourceforge.mecat.catalog.option.Options;
import net.sourceforge.mecat.catalog.option.WeakLocalListener;

public class AboutFrame extends JDialog implements LocalListener {

    static String version = "DEV";
    final String text = Options.getI18N(AboutFrame.class).getString("TEXT").replaceAll("\\[VERSION\\]", version);
//        "Copyright (C) 2004, Stephan Richard Palm, <br> All Rights Reserved. <br> For more information see: <br> <a href=\"http://mecat.sourceforge.net/\"> http://mecat.sourceforge.net/ </a>";
    
//    final static Font atomic = new Font("Atomic", Font.PLAIN, 26);

    static {
        URL versionFile = AboutFrame.class.getResource("version");

        if (versionFile != null) {
            Properties versionInfo = new Properties();
            try {
                versionInfo.load(versionFile.openStream());
                if (versionInfo.getProperty("Version") != null)
                    version = versionInfo.getProperty("Version");
            } catch (IOException e) {
            }
        }
    }
    
    final JButton okButton = new SimpleLocalButton(Options.getI18N(ExportChoice.class), "OK");
    
    public AboutFrame() {
        final Image image = Toolkit.getDefaultToolkit().createImage(Splasher.class.getResource("Logo.png"));
        MediaTracker mt = new MediaTracker(this);
        mt.addImage(image,0);
        try {
            mt.waitForID(0);
        } catch(InterruptedException ie){}

        okButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent arg0) {
                AboutFrame.this.setVisible(false);
            }
        });
        
        setLayout(new BorderLayout());
        add(new JLabel(){{  setIcon(new ImageIcon(image.getScaledInstance(image.getWidth(null) / 2, image.getHeight(null) / 2, Image.SCALE_SMOOTH)));   }}, BorderLayout.NORTH);
        add(new JEditorPane() {{ /*setFont(atomic);*/ setEditable(false); setBackground(AboutFrame.this.getBackground());  setContentType("text/html"); setText(text); }});
        add(new JPanel() {{ add(okButton); }}, BorderLayout.SOUTH);


        setResizable(false);
//        setUndecorated(true);
//        getRootPane().setWindowDecorationStyle(JRootPane.FRAME);

        setLocation(GraphicsEnvironment.getLocalGraphicsEnvironment().getCenterPoint().x - getWidth() / 2, 
                    GraphicsEnvironment.getLocalGraphicsEnvironment().getCenterPoint().y - getHeight() / 2);
        
        setModal(true);

        Options.addLocalListener(new WeakLocalListener(this));
        // Set labels to the current locale
        setLabels();
    }

    public void stateChanged(LocalListenerEvent event) {
        setLabels();
    }
    
    public void setLabels() {
        setTitle(Options.getI18N(MainFrameBackend.class).getString("About MeCat"));

        pack();
        setSize(getPreferredSize());
    }

}
