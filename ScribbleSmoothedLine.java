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
public class ScribbleSmoothedLine implements ScribbleShape {
    private final List<Point2D> rawPoints = new ArrayList<>();
    private final List<Point2D> smoothedPoints = new ArrayList<>();
    private float strokeWidth = 1;
    private Color color = Color.BLACK;
    private final Color xorModeColor = new Color(200, 200, 255);
    private int numberOfPointsForSmoothing = 1;
    private int pointsThreshold = 3;
    int totalPoints = 0;
    
    //==============================================
    public ScribbleSmoothedLine setStrokeWidth(float strokeWidth) {
        this.strokeWidth = strokeWidth;
        return this;
    }

    //==============================================
    public ScribbleSmoothedLine setColor(Color color) {
        this.color = color;
        return this;
    }
    
    //==============================================
    public ScribbleSmoothedLine setNrOfPointsToSmooth(int nr) {
        this.numberOfPointsForSmoothing = nr;
        if (!smoothedPoints.isEmpty()) {
            recalculateSmoothedPoints();
        }
        return this;
    }
    
    //==============================================
    public ScribbleSmoothedLine setPointThreshold(int threshold) {
        pointsThreshold = threshold;
        return this;
    }
    
    //==============================================
    @Override
    public void addPoint(Point2D p) {
        totalPoints++;
        if (totalPoints % pointsThreshold != 0) return;
        rawPoints.add(p);
        if (rawPoints.size() >= numberOfPointsForSmoothing) {
            smoothedPoints.add(getSmoothedPoint(rawPoints.size()));
        }
    }
    
    //==============================================
    @Override
    public void draw(Graphics g, boolean updateOnly, boolean drawInXorMode) {
        if (smoothedPoints.size() < 2) return;
        Color originalColor = g.getColor();
        Graphics2D g2d = ScribbleShape.getSuitableGraphics2D(g, drawInXorMode);
        Stroke originalStroke = g2d.getStroke();
        g2d.setColor(color);
        g2d.setStroke(new BasicStroke(strokeWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        
        if (updateOnly) {
            Point2D p1 = smoothedPoints.get(smoothedPoints.size() - 2);
            Point2D p2 = smoothedPoints.get(smoothedPoints.size() - 1);
            g2d.draw(new Line2D.Double(p1, p2));
        }
        else {
            Point2D old = smoothedPoints.get(0);
            for (int i = 1; i < smoothedPoints.size(); i++) {
                Point2D p = smoothedPoints.get(i);
                g2d.draw(new Line2D.Double(old, p));
                old = p;
            }
        }
        g2d.setColor(originalColor);
        g2d.setStroke(originalStroke);
    }
    
    //==============================================
    private Point2D getSmoothedPoint(int rawPointIndex) {
        if (rawPoints.size() < numberOfPointsForSmoothing) return null;
        double sumX = 0, sumY = 0;
        int startIndex = rawPointIndex - numberOfPointsForSmoothing;
        int endIndex = rawPointIndex - 1;
        for (int i = startIndex; i <= endIndex; i++) {
            Point2D p = rawPoints.get(i);
            sumX += p.getX();
            sumY += p.getY();
        }
        return new Point2D.Double(
                1.0 * sumX / numberOfPointsForSmoothing,
                1.0 * sumY / numberOfPointsForSmoothing
        );
    }
    
    //==============================================
    private void recalculateSmoothedPoints() {
        if (smoothedPoints.isEmpty()) return;
        smoothedPoints.clear();
        for (int i = numberOfPointsForSmoothing; i <= rawPoints.size(); i++) {
            smoothedPoints.add(getSmoothedPoint(i));
        }
    }

    @Override
    public boolean canDrawInXorMode() {
        return false;
    }
}

