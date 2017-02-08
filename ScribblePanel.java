/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scribble_version_51_smoothing_version_01;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

/**
 *
 * @author Sylvia
 */
class ScribblePanel extends JPanel {

    final private Controller controller;
    Color color;
    boolean updateOnly = true;
    boolean redrawEverything = !updateOnly;
    boolean dataHasChanged = false;
    boolean drawInXORMode = true;
    boolean drawInNormalMode = !drawInXORMode;

    MouseAdapter mouseAdapterForShapes;

    // and the Shapedata 
    ScribbleShape currentShape;
    List<ScribbleShape> shapes = new ArrayList<>();
    
   // and a reference to itself, to save some typing
    private ScribblePanel mySelf = this;

    //======================================================
    public ScribblePanel(Controller controller, int width, int height) {
        this.controller = controller;
        this.setLayout(null);  // we're doing it all by ourselves!!!

        setSize(new Dimension(width, height));

        mouseAdapterForShapes = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent m) {
                currentShape = controller.getShape();
                color = controller.getColor();
                currentShape.addPoint(m.getPoint());
                shapes.add(currentShape);
            }

            @Override
            public void mouseDragged(MouseEvent m) {
                if (currentShape.canDrawInXorMode()) {
                    currentShape.draw(mySelf.getGraphics(), redrawEverything, drawInXORMode);
                }
                currentShape.addPoint(m.getPoint());
                Graphics g = mySelf.getGraphics();
                g.setColor(color);
                currentShape.draw(
                        g, updateOnly, 
                        currentShape.canDrawInXorMode() ? drawInXORMode : drawInNormalMode
                );
                g.dispose();
                mySelf.setDataHasChanged(true);
            }

            @Override
            public void mouseReleased(MouseEvent m) {
                if (currentShape.canDrawInXorMode()) {
                    currentShape.draw(mySelf.getGraphics(), redrawEverything, drawInNormalMode);
                }
            }
        };

        this.addMouseListener(mouseAdapterForShapes);
        this.addMouseMotionListener(mouseAdapterForShapes);
    }

    //=====================================================
    // changing the background
    public void processBackgroundColorChange(Color newBackgroundColor) {
        this.setBackground(newBackgroundColor);
    }
    
    //======================================================
    public boolean save(File f) {
        Dimension d = this.getSize();
        BufferedImage buf = new BufferedImage(d.width, d.height, BufferedImage.TYPE_INT_ARGB);
        Graphics g = buf.getGraphics();
        this.paint(g);
        try {
            ImageIO.write(buf, "png", f);
            return true;
        }
        catch (IOException ioe) {
            System.out.println("Saving failed!!!!");
            return false;
        }
    }
    
    //====================================================== 
    @Override
    public void paint(Graphics g) {
        super.paint(g);   // clears to the background 
        // drawing all the shapes 
        shapes.stream().forEach(shape -> shape.draw(g, redrawEverything, drawInNormalMode));
    }

    //====================================================== 
    public void clear() {
        shapes.clear();
        ScribbleLabel.deleteAllImages(this);
        this.setDataHasChanged(false);
        repaint();
    }
    
    //==========================================================
    public void loadImage(File f) {
        ScribbleLabel p = new ScribbleLabel(f);
        this.add(p);
        p.determineLocation();
        p.bringToTop();
    }
    
    //==========================================================
    public void loadImage(BufferedImage buf) {
        ScribbleLabel p = new ScribbleLabel(buf);
        this.add(p);
        p.determineLocation();
        p.bringToTop();
    }
//==========================================================
    private void disableDrawingShapes() {
        this.removeMouseListener(mouseAdapterForShapes);
        this.removeMouseMotionListener(mouseAdapterForShapes);
        ScribbleLabel.enableMouseListener(this);
    }

    //======================================================
    private void enableDrawingShapes() {
        this.addMouseListener(mouseAdapterForShapes);
        this.addMouseMotionListener(mouseAdapterForShapes);
        ScribbleLabel.disableMouseListener(this);
    }

    //======================================================
    public void setStateToImageSelect() {
        disableDrawingShapes();
    }
    
    //======================================================
    public void setStateToDrawing() {
        enableDrawingShapes();
    }
    
    //======================================================
    public boolean getDataHasChanged() {
        return dataHasChanged || ScribbleLabel.getDataHasChanged(this);
    }

    //======================================================
    public void setDataHasChanged(boolean b) {
        dataHasChanged = b;
        ScribbleLabel.setDataHasChanged(this, b);
    }

    //======================================================
    public void clearAllCurves() {
        shapes.clear();
        repaint();
    }

    //======================================================
    public void clearMostRecentCurve() {
        if (shapes.isEmpty()) {
            return;
        }
        shapes.remove(shapes.size() - 1);
        repaint();
    }
    
    //======================================================
    
}   // end of class ScribblePanel