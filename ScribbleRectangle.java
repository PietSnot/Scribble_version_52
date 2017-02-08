/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scribble_version_51_smoothing_version_01;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Sylvia
 */
public class ScribbleRectangle implements ScribbleShape {
    
    private final List<Point2D> points = new ArrayList<>();
    private float strokeWidth = 1;
    private Color color = Color.BLACK;
    
    //==============================================
    // Factory methodes
    //==============================================
    public ScribbleRectangle setStrokeWidth(double strokeWidth) {
        this.strokeWidth = (float) strokeWidth;
        return this;
    }
    
    public ScribbleRectangle setColor(Color color) {
        this.color = color;
        return this;
    }
    
    //==============================================
    @Override
    public void addPoint(Point2D p) {
        if (points.size() < 2) points.add(p);
        else points.set(1, p);
    }
    
    //==============================================
    @Override
    public void draw(Graphics g, boolean updateOnly, boolean drawInXorMode) {
        if (points.size() < 2) return;
        Graphics2D g2d = ScribbleShape.getSuitableGraphics2D(g, drawInXorMode);
        Color origCol = g2d.getColor();
        Stroke origStr = g2d.getStroke();
        g2d.setColor(color);
        g2d.setStroke(new BasicStroke(strokeWidth));
        Point2D p1 = points.get(0), p2 = points.get(1);
        Rectangle2D r = Utils.calculateRectangle(p1, p2);
        g2d.draw(Utils.calculateRectangle(p1, p2));
    } 

    @Override
    public boolean canDrawInXorMode() {
        return true;
    }
}
