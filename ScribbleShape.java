/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scribble_version_51_smoothing_version_01;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;

/**
 *
 * @author Sylvia
 */
public interface ScribbleShape {
    
    // is the color when drawing in XORMoe
    public final Color XORMODECOLOR = new Color(200, 200, 255);
    
    // abstract methods
    public abstract void addPoint(Point2D p);
    public abstract void draw(Graphics g, boolean updateOnly, boolean drawInXorMode);
    public abstract boolean canDrawInXorMode();
    
    // creates a Graphics2D object with partially filled in settings
    public static Graphics2D getSuitableGraphics2D(Graphics g, boolean drawInXorMode) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(
            RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON
        );
        if (drawInXorMode) g2d.setXORMode(XORMODECOLOR);
        return g2d;
    }
}

