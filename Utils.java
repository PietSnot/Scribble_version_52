/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scribble_version_51_smoothing_version_01;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Map;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import static javax.swing.UIManager.getDefaults;

/**
 *
 * @author Sylvia
 */
public class Utils {

   public static void main(String... args) {
        LookAndFeelInfo[] laf = UIManager.getInstalledLookAndFeels();
        System.out.println(Arrays.toString(laf));
        getUIDefaults();
    }
    
    public static Rectangle2D calculateRectangle(Point2D p1, Point2D p2) {
        double left = Math.min(p1.getX(), p2.getX());
        double top = Math.min(p1.getY(), p2.getY());
        double width = Math.abs(p2.getX() - p1.getX());
        double height = Math.abs(p2.getY() - p1.getY());
        return new Rectangle2D.Double(left, top, width, height);
    }

    /**
     * this function sets the LookAndFeel according to the supplied String[].
     * This String[] could be, for instance, { "windows classic", "nimbus",
     * "windows", "metal", "cde/motif" } to mention a few. Variables:
     *
     * @preferences String[] containing the preferred L&F names, in this order
     * @systemLAF boolean: if no L&F of 'preferences' could be set, then if this
     * variable is true, then the systemL&F will be set; if it is false then no
     * attempt will be made to set any L&F at all. In that case the default will
     * be set by the system
     */
    static void setPreferenceLookAndFeel(boolean systemLAF, String... preferences) {
        if (preferences.length == 0) {
            if (systemLAF) {
                try {
                    String system = UIManager.getSystemLookAndFeelClassName();
                    UIManager.setLookAndFeel(system);
                } catch (Exception e) {
                }
            }
            return;  // we do nothing if systemLAF == false
        }  // eind if missing or empty voorkeurslijst

        // we gonna try to set a preference
        UIManager.LookAndFeelInfo[] temp = UIManager.getInstalledLookAndFeels();
        ArrayList<String> naam = new ArrayList<>();
        for (int i = 0; i < temp.length; i++) {
            naam.add(temp[i].getName().toLowerCase());
        }
        // checking
        boolean succes = false;
        for (int i = 0; i < preferences.length; i++) {
            if (naam.contains(preferences[i].toLowerCase())) {
                int j = naam.indexOf(preferences[i].toLowerCase());
                try {
                    UIManager.setLookAndFeel(temp[j].getClassName());
                    succes = true;
                    break;
                } catch (Exception e) {
                }
            }
        }
        if (!succes) {
            if (systemLAF) {
                try {
                    String system = UIManager.getSystemLookAndFeelClassName();
                    UIManager.setLookAndFeel(system);
                } catch (Exception e) {
                }
            }
        }
    }  // eind setPLAF
    
    //==============================================
    public static void getUIDefaults() {
        Hashtable<Object, Object> uidef = UIManager.getDefaults();
    }
    
    //==============================================
    public static Icon loadIcon(URL url) {
        return new ImageIcon(url);
    }
    
    //==============================================
    public static void showAvailableLookAndFeels() {
        LookAndFeelInfo[] laf = UIManager.getInstalledLookAndFeels();
        for (LookAndFeelInfo lafi: laf) System.out.println(lafi.getName());
    }
            
    //==============================================
    public static Dimension getScreenSize() {
        return Toolkit.getDefaultToolkit().getScreenSize();
    }
    
    //==============================================
    
    
}  // einde class

