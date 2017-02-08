/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scribble_version_51_smoothing_version_01;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeListener;

/**
 *
 * @author Sylvia
 */
public class ScribblePanelsViewer {
    
    JTabbedPane tabbedPane;
    JFrame viewer;
    Controller controller;
    int nextTabNumber;
    JFileChooser jfc;
    Color panelBackground;
    boolean dragImage;
    
    //==============================================
    // constructor
    //==============================================
    public ScribblePanelsViewer(int width, int height, Controller controller) {
       
        nextTabNumber = 1;
        tabbedPane = new JTabbedPane();
        this.controller = controller;
        tabbedPane.setPreferredSize(new Dimension(width, height));
        jfc = new JFileChooser();
        dragImage = false;
        viewer = new JFrame("ScribblePanel Viewer!!!");
        viewer.add(tabbedPane);
        viewer.pack();
        viewer.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        Dimension d = Utils.getScreenSize();
        int y = (d.height - viewer.getHeight()) / 2;
        viewer.setLocation(100, y);
        viewer.setVisible(true);
    }
    
    //==============================================
    // public methodes
    //==============================================
    public void addPanel() {
        this.createNewScribblePanel();
    }
    
    //==============================================
    public void clear() {
        ScribblePanel sp = (ScribblePanel) tabbedPane.getSelectedComponent();
        sp.clear();
    }
    
    //==============================================
    public void clearAllCurves() {
        ScribblePanel sp = (ScribblePanel) tabbedPane.getSelectedComponent();
        sp.clearAllCurves();
    }
    
    //==============================================
    public void clearMostRecentCurve() {
        ScribblePanel sp = (ScribblePanel) tabbedPane.getSelectedComponent();
        sp.clearMostRecentCurve();
    }
    
    //==============================================
    public void addImage(BufferedImage buf) {
        if (tabbedPane.getSelectedIndex() < 0) return;
        ScribblePanel sp = (ScribblePanel) tabbedPane.getSelectedComponent();
        sp.loadImage(buf);
    }
    
    //==============================================
    public void addImage(File f) {
        if (tabbedPane.getSelectedIndex() < 0) return;
        ScribblePanel sp = (ScribblePanel) tabbedPane.getSelectedComponent();
        sp.loadImage(f);
    }
    
    //==============================================
    public boolean isVisible() {
        return viewer == null ? false : viewer.isVisible();
    }
    
    //==============================================
    public void setVisible(boolean visible) {
        if (viewer == null) return;
        viewer.setVisible(visible);
    }
    
    //==============================================
    public void setStatusToDragOrResizeImage() {
        dragImage = true;
        if (tabbedPane.getSelectedIndex() < 0) return;
        for (Component comp: tabbedPane.getComponents()) {
            ((ScribblePanel) comp).setStateToImageSelect();
        }
    }
    
    //==============================================
    public void setStatusToDrawingShapes() {
        dragImage = false;
        if (tabbedPane.getSelectedIndex() < 0) return;
        for (Component comp: tabbedPane.getComponents()) {
            ((ScribblePanel) comp).setStateToDrawing();
        }
    }
    
    //==============================================
    public void remove() {
        if (tabbedPane.getSelectedIndex() < 0) return;
        tabbedPane.remove(tabbedPane.getSelectedIndex());
    }
    
    //==============================================
    public void save() {
        if (tabbedPane.getSelectedIndex() < 0) return;
        
        int x = jfc.showSaveDialog(null);
        if (x != JFileChooser.APPROVE_OPTION) return;
        String s = jfc.getSelectedFile().getAbsolutePath();
        File f = ensureFileDoesNotExist(s);
        ((ScribblePanel) tabbedPane.getSelectedComponent()).save(f);
    }
    
    public void setBackground(Color c) {
        this.panelBackground = c;
        if (tabbedPane.getSelectedIndex() < 0) return;
        ((ScribblePanel) tabbedPane.getSelectedComponent()).setBackground(c);
    }
    
    //==============================================
    // private methods
    //==============================================
    private ScribblePanel createNewScribblePanel() {
        ScribblePanel sp = new ScribblePanel(controller, tabbedPane.getWidth(), tabbedPane.getHeight());
        sp.setBackground(panelBackground);
        if (dragImage) {
            sp.setStateToImageSelect();
        }
        else {
            sp.setStateToDrawing();
        }
        String name = "" + nextTabNumber;
        tabbedPane.addTab(name, sp);
        nextTabNumber++;
        tabbedPane.setSelectedComponent(sp);
        return sp;
    }

    //==========================================================
    private File ensureFileDoesNotExist(String givenFilename) {
        String s = ensureFilenameEndsWithPNG(givenFilename);
        File f = new File(s);
        if (!f.exists()) {
            return f;
        }
        String withoutExtension = s.substring(0, s.lastIndexOf(".")) + "(%d).png";
        int attempt = 1;
        File result = new File(String.format(withoutExtension, attempt));
        while (result.exists() && attempt < 100) {
            attempt++;
            result = new File(String.format(withoutExtension, attempt));
        }
        return attempt == 100 ? null : result;
    }

    //==============================================
    private String ensureFilenameEndsWithPNG(String givenFilename)
            throws IllegalArgumentException {
        if (givenFilename == null || givenFilename.isEmpty()) {
            throw new IllegalArgumentException("Given filename is null or empty!!");
        }
        String s = givenFilename;
        if (!(s.toLowerCase().endsWith(".png"))) {
            s += ".png";
        }
        return s;
    }
}
