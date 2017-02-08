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
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Sylvia
 */
public class LineOfCircles implements ScribbleShape {
    
    private final List<Point2D> points = new ArrayList<>() ;
    private float strokeWidth = 1;
    private Color color = Color.BLACK;
    
    //==================================================
    // Factory
    //==================================================
    public LineOfCircles setStrokeWidth(double strokeWidth) {
        this.strokeWidth = (float) strokeWidth;
        return this;
    }
    
    public LineOfCircles setColor(Color color) {
        this.color = color;
        return this;
    }
    
    //==================================================
    public void draw(Graphics g, boolean updateOnly, boolean drawInXorMode) {
        if (points.size() < 2) return;
        Color originalColor = g.getColor();
        Graphics2D g2d = ScribbleShape.getSuitableGraphics2D(g, drawInXorMode);
        Stroke originalStroke = g2d.getStroke();
        g2d.setStroke(new BasicStroke(strokeWidth));
        if (updateOnly) {
            Point2D p = points.get(points.size() - 1);
            Ellipse2D ellips = new Ellipse2D.Double(
                p.getX(), p.getY(), strokeWidth, strokeWidth
            );
            g2d.draw(ellips);
        } 
        else {
            points.stream()
                .map(p -> new Ellipse2D.Double(p.getX(), p.getY(), strokeWidth, strokeWidth))
                .forEach(e -> g2d.draw(e))
            ;
        }
    }

    //==============================================
    @Override
    public void addPoint(Point2D p) {
        points.add(p);
    }

    @Override
    public boolean canDrawInXorMode() {
        return false;
    }
}
