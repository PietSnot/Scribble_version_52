/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scribble_version_52;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import javax.imageio.ImageIO;
import javax.swing.JDialog;
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
    JDialog viewer;
    Controller controller;
    int nextTabNumber;
    JFileChooser jfc;
    Color panelBackground;
    Color drawingColor;
    float brushSize;
    String currentShape;
    boolean dragImage;
    int numberOfPointsForSmoothing;
    int useOnlyEveryXRawPoints;
    private Map<String, Supplier<ScribbleShape>> shapeMap;
    
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
        
        numberOfPointsForSmoothing = 5;
        useOnlyEveryXRawPoints = 2;
        
        createShapeMap();
        
        viewer = new JDialog(controller.getFrame(), "ScribblePanel Viewer!!!", false);
        viewer.add(tabbedPane);
        viewer.pack();
        viewer.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        
        Dimension d = Utils.getScreenSize();
        int y = (d.height - viewer.getHeight()) / 2;
        viewer.setLocation(100, y);
        
        this.addPanel();
        
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
    
    //==============================================
    public void setBackground(Color c) {
        this.panelBackground = c;
        if (tabbedPane.getSelectedIndex() < 0) return;
        ((ScribblePanel) tabbedPane.getSelectedComponent()).setBackground(c);
    }
    
    //==============================================
    public void setDrawingColor(Color drawColor) {
        drawingColor = drawColor;
    }
    
    //==============================================
    public void setBrushsize(float brushsize) {
        this.brushSize = brushsize;
    }
    
    //==============================================
    public ScribbleShape getShape() {
        return shapeMap.get(currentShape).get();
    }
    
    //==============================================
    public Color getColor() {
        return drawingColor;
    }
    //==============================================
    public void setShape(String shape) {
        currentShape = shape;
    }
    
    //==============================================
    public void setNumberOfPointsForSmoothing(int number) {
        numberOfPointsForSmoothing = number;
    }
    
    //==============================================
    public void setuseOnlyEveryXRawPoints(int number) {
        useOnlyEveryXRawPoints = number;
    }
              
    //==============================================
    // private methods
    //==============================================
    private void createShapeMap() {
        if (shapeMap != null) return;
        shapeMap = new LinkedHashMap<>();
        shapeMap.put("Smoothed Line", () -> createScribbleSmoothedLine());
        shapeMap.put("Line Of Circles", () -> createLineOfCircles());
        shapeMap.put("Raw Curve", () -> createRawCurve());
        shapeMap.put("Rectangle", () -> createScribbleRectangle());
        shapeMap.put("Ellipse", () -> createScribbleEllipse());
        
        String[] shapes = shapeMap.keySet().toArray(new String[0]);
        controller.setAllShapes(shapes);
    }
    
    //==============================================
    private ScribblePanel createNewScribblePanel() {
        ScribblePanel sp = new ScribblePanel(this, tabbedPane.getWidth(), tabbedPane.getHeight());
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

    //==============================================
    private ScribbleShape createScribbleSmoothedLine() {
        ScribbleSmoothedLine curve = new ScribbleSmoothedLine()
                .setColor(drawingColor)
                .setStrokeWidth(brushSize)
                .setNrOfPointsToSmooth(numberOfPointsForSmoothing)
                .setPointThreshold(useOnlyEveryXRawPoints)
        ;
        return curve;
    }
    
    //==============================================
    private ScribbleShape createLineOfCircles() {
         LineOfCircles curve = new LineOfCircles()
                    .setColor(drawingColor)
                    .setStrokeWidth(brushSize)
        ;
        return curve;
    }
    
    //==============================================
    private ScribbleShape createRawCurve() {
        RawCurve rawcurve = new RawCurve()
                .setColor(drawingColor)
                .setStrokeWidth(brushSize)
        ;
        return rawcurve;
    }
    
    //==============================================
    private ScribbleShape createScribbleRectangle() {
        ScribbleRectangle r = new ScribbleRectangle()
                .setColor(drawingColor)
                .setStrokeWidth(brushSize)
        ;
        return r;
    }
    
    //==============================================
    private ScribbleShape createScribbleEllipse() {
        ScribbleEllipse e = new ScribbleEllipse()
                .setColor(drawingColor)
                .setStrokeWidth(brushSize)
        ;
        return e;
    }
    
    //==============================================
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
