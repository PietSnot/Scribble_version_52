/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scribble_version_51_smoothing_version_01;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JRadioButton;

/**
 *
 * @author Sylvia
 */
public class ScribbleColorRadioButton extends JRadioButton {
    
    //==============================================
    // private constructor and a Factory method
    //==============================================
    private ScribbleColorRadioButton() {
        super();
    }
    
    public static ScribbleColorRadioButton createColorRadioButton(int width, int height, Color c, ActionListener al, ButtonGroup b, boolean select) {
        ScribbleColorRadioButton radio = new ScribbleColorRadioButton();
        radio.setForeground(c);
        Icon on = createColorIcon(width, height, c, true);
        Icon off = createColorIcon(width, height, c, false);
        radio.setSelected(select);
        radio.setIcon(off);
        radio.setSelectedIcon(on);
        radio.addActionListener(al);
        b.add(radio);
        return radio;
    }
    
    //==============================================
    public Color getColor() {
        return this.getForeground();
    }
    
    //==============================================
    public void changeColor(Color newColor) {
        int width = this.getIcon().getIconWidth();
        int height = this.getIcon().getIconHeight();
        Icon on = createColorIcon(width, height, newColor, true);
        Icon off = createColorIcon(width, height, newColor, false);
        this.setSelectedIcon(on);
        this.setIcon(off);
        this.setForeground(newColor);
    }
    
   //==============================================
    private static ImageIcon createColorIcon(int width, int height, Color c, boolean selected) {
        BufferedImage buf = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = buf.getGraphics();
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(0, 0, width, height);
        g.setColor(selected ? Color.BLACK : Color.WHITE);
        g.drawLine(0, 0, width, 0);
        g.drawLine(0, 0, 0, height);
        g.drawLine(1, 1, width - 1, 1);
        g.drawLine(1, 1, 1, height - 1);
        g.setColor(selected ? Color.WHITE : Color.BLACK);
        g.drawLine(1, height - 1, width - 1, height - 1);
        g.drawLine(2, height - 2, width - 2, height - 2);
        g.drawLine(width - 1, 1, width - 1, height);
        g.drawLine(width - 2, 2, width - 2, height - 2);
        int sizeHor = (int) (width * .4), sizeVert = (int) (height * .4);
        g.setColor(c);
        g.fillRect((width - sizeHor) / 2, (height - sizeVert) / 2, sizeHor, sizeVert);
        g.dispose();
        ImageIcon im = new ImageIcon(buf);
        return im;
    }

}  // end of class

