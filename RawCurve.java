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
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Sylvia
 */
class RawCurve implements ScribbleShape {
    List<Point2D> points = new ArrayList<>();
    private float strokeWidth = 1;
    private Color color = Color.BLACK;
    
    //==============================================
    public RawCurve setColor(Color c) {
        color = c;
        return this;
    }
    
    //==============================================
    public RawCurve setStrokeWidth(double strokeWidth) {
        this.strokeWidth = (float) strokeWidth;
        return this;
    }

    //==============================================
    @Override
    public void addPoint(Point2D p) {
        points.add(p);
    }

    //==============================================
    @Override
    public void draw(Graphics g, boolean updateOnly, boolean drawInXorMode) {
        if (points.size() < 2) return;
        Color originalColor = g.getColor();
        Graphics2D g2d = ScribbleShape.getSuitableGraphics2D(g, drawInXorMode);
        g2d.setColor(color);
        Stroke originalStroke = g2d.getStroke();
        g2d.setStroke(new BasicStroke(strokeWidth));
        if (updateOnly) {
            Point2D p1 = points.get(points.size() - 2);
            Point2D p2 = points.get(points.size() - 1);
            g2d.draw(new Line2D.Double(p1, p2));
        } 
        else {
            Point2D oldP = points.get(0);
            for (int i = 1; i < points.size(); i++) {
                Point2D p = points.get(i);
                g2d.draw(new Line2D.Double(oldP, p));
                oldP = p;
            }
        }
    }

    @Override
    public boolean canDrawInXorMode() {
        return false;
    }
}
