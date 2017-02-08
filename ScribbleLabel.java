/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scribble_version_51_smoothing_version_01;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.border.Border;

/**
 *
 * @author Sylvia
 */
public class ScribbleLabel extends JLabel {
        //==========================================================
    // members
    // =========================================================
    private BufferedImage fullImage;
    private double initialMaxWidth = 300, initialMaxHeight = 300;
    private int accuracy = 10;
    private MouseAdapter mouseAdapter;
    private Point originalLabelOrigin, dragStart, fixedPoint, resizePoint;
    private Border border = BorderFactory.createLineBorder(Color.YELLOW, 3, true);
    private static final Map<Container, Boolean> changedContainers = new HashMap<>();

    /**
     * enum DragOrResizeType
     * gives information about the dragging or resizing. The possible values 
     * are: NONE, RESIZING_HOR, RESIZING_VERT, RESIZING_BOTH, DRAGGING
     */
    enum DragOrResizeType {
        NONE(false),
        RESIZING_HOR(true),
        RESIZING_VERT(true),
        RESIZING_BOTH(true),
        DRAGGING(false);

        private final boolean isResizingType;

        DragOrResizeType(boolean b) {
            isResizingType = b;
        }

        public boolean isResizingType() {
            return isResizingType;
        }
    }
    private DragOrResizeType dragOrResizeType;

    /**
     * enum LabelArea. The possible values are NONE, TOP, TOPLEFT, LEFT,
     * BOTTOMLEFT, BOTTOM, BOTTOMRIGHT, RIGHT, CENTER
     */
    enum LabelArea {
        NONE(Cursor.DEFAULT_CURSOR),
        TOP(Cursor.N_RESIZE_CURSOR),
        TOPLEFT(Cursor.NW_RESIZE_CURSOR),
        LEFT(Cursor.W_RESIZE_CURSOR),
        BOTTOMLEFT(Cursor.SW_RESIZE_CURSOR),
        BOTTOM(Cursor.S_RESIZE_CURSOR),
        BOTTOMRIGHT(Cursor.SE_RESIZE_CURSOR),
        RIGHT(Cursor.E_RESIZE_CURSOR),
        TOPRIGHT(Cursor.NE_RESIZE_CURSOR),
        CENTER(Cursor.MOVE_CURSOR);

        private int cursorType;

        LabelArea(int cursor) {
            cursorType = cursor;
        }

        private Cursor getCursor() {
            return Cursor.getPredefinedCursor(cursorType);
        }

        //======================================================
        /**
         * This method determines in what part of the label the MouseEvent took
         * place.
         * @param label the label for which the MouseEvent took place
         * @param m the MouseEvent
         * @param accuracy in pixels. If the MouseEvent is within <pixels>
         * distance from a side, it counts as if it was a click on that side
         * @return a LabelArea. See LabelArea for the possible values.
         */
        public static LabelArea getLabelArea(JLabel label, MouseEvent m, int accuracy) {
            if (label != m.getSource()) {
                return NONE;
            }
            int w = label.getWidth();
            int h = label.getHeight();
            Point mouse = m.getPoint();

            boolean intop = 0 <= mouse.y && mouse.y <= accuracy;
            boolean inleft = 0 <= mouse.x && mouse.x <= accuracy;
            boolean inbottom = mouse.y >= h - accuracy && mouse.y < h;
            boolean inright = mouse.x >= w - accuracy && mouse.x < w;

            if (intop) {
                if (inleft) {
                    return TOPLEFT;
                } else if (inright) {
                    return TOPRIGHT;
                } else {
                    return TOP;
                }
            } else if (inleft) {
                if (inbottom) {
                    return BOTTOMLEFT;
                } else {
                    return LEFT;
                }
            } else if (inbottom) {
                if (inright) {
                    return BOTTOMRIGHT;
                } else {
                    return BOTTOM;
                }
            } else if (inright) {
                return RIGHT;
            } else {
                return CENTER;
            }
        }

        /**
         * Given a label and a MouseEvent, determines what cursor hould be 
         * displayed. This is for the user to get a clear indication of what
         * part of the label can be dragged
         * @param label the JLabel in question
         * @param m the MouseEvent
         * @param accuracy in pixels. A MouseEvent location within <pixels>
         * dirtance from a side will be seen as vbeing on the side
         * @return the suitable Cursor
         */
        public static Cursor getCursor(JLabel label, MouseEvent m, int accuracy) {
            LabelArea area = getLabelArea(label, m, accuracy);
            return area.getCursor();
        }

        /**
         * given a LabelArea, this method gives whether we are resizing or
         * dragging, and if resizing whether this is only horizontally, 
         * vertically or both
         * @param area a LabelArea
         * @return the one of the possible enum values of DragOrResizeType
         */
        public static DragOrResizeType getDragOrResizeType(LabelArea area) {
            switch (area) {
                case LEFT:
                case RIGHT:
                    return DragOrResizeType.RESIZING_HOR;
                case TOP:
                case BOTTOM:
                    return DragOrResizeType.RESIZING_VERT;
                case TOPLEFT:
                case TOPRIGHT:
                case BOTTOMLEFT:
                case BOTTOMRIGHT:
                    return DragOrResizeType.RESIZING_BOTH;
                case CENTER:
                    return DragOrResizeType.DRAGGING;
                default:
                    String s = "Unknown parameter area supplied!!!";
                    throw new IllegalArgumentException(s);
            }
        }

        /**
         * given a JLabel and a LabelArea, gives the two corner points of
         * the JLabel that are involved in the dragging or resizing
         * @param label
         * @param area
         * @return 
         */
        public static List<Point> getInvolvedResizingPoints(JLabel label, LabelArea area) {
            Rectangle rect = label.getBounds();
            Point topleft = rect.getLocation();
            Point bottomleft = new Point(topleft.x, topleft.y + rect.height - 1);
            Point bottomright = new Point(bottomleft.x + rect.width - 1, bottomleft.y);
            Point topright = new Point(bottomright.x, topleft.y);
            switch (area) {
                case TOP:
                case TOPLEFT:
                case LEFT:
                    return Arrays.asList(topleft, bottomright);
                case BOTTOM:
                case BOTTOMRIGHT:
                case RIGHT:
                    return Arrays.asList(bottomright, topleft);
                case BOTTOMLEFT:
                    return Arrays.asList(bottomleft, topright);
                case TOPRIGHT:
                    return Arrays.asList(topright, bottomleft);
                default:
                    String s = "Unknown parameter area in InvolvedResizingPoints!!!";
                    throw new IllegalArgumentException(s);
            }
        }
    }

    //==========================================================
    // PLabel constructor
    //==========================================================
    /** 
     * constructor
     * @param file the file that holds the image. If the file is not a lega;
     * image (jpg, gif or png) then a default PLabel is created
     */
    public ScribbleLabel(File file) {
        if (!isImage(file)) {
            String error = "The given file has unknown type (not gif, png ot jpeg";
            throw new IllegalArgumentException(error);
        }

        try {
            fullImage = ImageIO.read(file);
        } 
        catch (IOException e) {
            System.out.println("Cannot read image!!!");
            setIcon(createDefaultIcon());
            setSize((int) initialMaxWidth, (int) initialMaxHeight);
            return;
        }

        commonConstructorPart();
    }
    
    //==========================================================
    public ScribbleLabel(BufferedImage buf) {
        fullImage = buf;
        commonConstructorPart();
    }
    
    //==========================================================
    private void commonConstructorPart() {
        mouseAdapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent m) {
                initializeDragOrResize(m, accuracy);
            }

            public void mouseReleased(MouseEvent m) {
                if (dragOrResizeType.isResizingType) {
                    setIconToSmooth();
                }
            }

            @Override
            public void mouseDragged(MouseEvent m) {
                if (dragOrResizeType.isResizingType) {
                    processResizing(m);
                } else {
                    processDrag(m);
                }
            }

            @Override
            public void mouseEntered(MouseEvent m) {
                setCursor(LabelArea.getCursor(ScribbleLabel.this, m, accuracy));
            }

            @Override
            public void mouseExited(MouseEvent m) {
                setCursor(Cursor.getDefaultCursor());
            }

            @Override
            public void mouseMoved(MouseEvent m) {
                setCursor(LabelArea.getCursor(ScribbleLabel.this, m, accuracy));
            }
        };

        Point2D size = determineSizeOfLabel(initialMaxWidth, initialMaxHeight);
        Image temp = getScaledInstanceSmooth(size);
        Icon icon = new ImageIcon(temp);
        setIcon(icon);
        setSize((int) size.getX(), (int) size.getY());
    }

    //==========================================================
    // public methods
    //==========================================================
    /**
     * This method returns the MouseAdapter that this PLabel is using.
     * @return the MouseAdapter
     */
    public MouseAdapter getMouseListener() {
        return mouseAdapter;
    }

    //==========================================================
    /**
     * This method enables the MouseListener for all the PietsLabels that
     * are contained in the given container
     * @param container the Container that holds the PietsLabels
     */
    public static void enableMouseListener(Container container) {
        Arrays.stream(container.getComponents())
                .filter(e -> e instanceof ScribbleLabel)
                .map(e -> (ScribbleLabel) e)
                .forEach(e
                        -> {
                    e.addMouseListener(e.mouseAdapter);
                    e.addMouseMotionListener(e.mouseAdapter);
                }
                );
    }

    //==========================================================
    /**
     * This method disables the MouseListener of all the PietsLabels that
     * are contained within the given container.
     * @param container the Container involved
     */
    public static void disableMouseListener(Container container) {
        Arrays.stream(container.getComponents())
                .filter(e -> e instanceof ScribbleLabel)
                .map(e -> (ScribbleLabel) e)
                .forEach(e
                        -> {
                    e.removeMouseListener(e.mouseAdapter);
                    e.removeMouseMotionListener(e.mouseAdapter);
                    e.setBorder(null);
                }
                );
    }

    //==========================================================
    /**
     * given this new PLabel, determines the location where it should be
     * placed. In first instance the placement is centered in its container,
     * but then offset by a random amount, so that consequetive new PietsLabels
     * will not be placed right above each other. Since the container is not
     * given, this PLabel must first be added to the container, before this
     * method can be invoked!
     */
    public void determineLocation() {
        Container container = this.getParent();
        Random r = new Random();
        int startX = (container.getWidth() - this.getWidth()) / 2;
        int startY = (container.getHeight() - this.getHeight()) / 2;
        int deltaX = r.nextInt(60) - 30;
        int deltaY = r.nextInt(60) - 30;
        this.setLocation(startX + deltaX, startY + deltaY);
    }

    //==========================================================
    /**
     * given a new proposed width and heigt of this PLabel, determines 
     * the actual size. This actual size is based on the size of the image.
     * The image is scaled such that, keeping the aspect ratio of the image,
     * there is a maximum fit with the given width and heigt. Note that the
     * actual calculated widtg and heigt may differ from the parameter values.
     * @param newWidth the proposed new width of this PLabel
     * @param newHeight the proposed new height of this PLabel
     * @param smooth boolean. If true, the scaling of the image will be of
     * hight quality, if false the scaling will be low qualu=ity, but with
     * optimal speed
     */
    public void setNewSize(double newWidth, double newHeight, boolean smooth) {
        Point2D size = determineSizeOfLabel(newWidth, newHeight);
        Image temp = smooth ? getScaledInstanceSmooth(size) : getScaledInstanceQuick(size);
        Icon icon = new ImageIcon(temp);
        setIcon(icon);
        setSize((int) size.getX(), (int) size.getY());
    }

    //==========================================================
    /**
     * makes a high quality scaled iamge for this PLabel
     */
    public void setIconToSmooth() {
        Rectangle rect = this.getBounds();
        setNewSize(rect.width, rect.height, true);
    }

    //==========================================================
    /**
     * This method sets the accuracy that determines within how many pixels
     * from a border the mouse click will be considered to be on the border.
     * @param accuracy the accuracy in pixels
     */
    public void setAccuracy(int accuracy) {
        this.accuracy = accuracy;
    }

    //==========================================================
    /**
     * returns the current value of the field accuracy
     * @return integer, the accuracy in pixels
     */
    public int getAccuracy() {
        return accuracy;
    }

    //==========================================================
    /**
     * brings this PLabel on top of all other PietsLabels
     */
    public void bringToTop() {
        Container container = this.getParent();
        if (container == null) {
            return;
        }
        container.remove(this);
        container.add(this, 0);
        this.repaint();
    }
    
    //==========================================================
    /**
     * this method deletes all the PietsLabels tha are contained within the
     * container.
     * @param container the Container involved (usually a JPanel
     */
    public static void deleteAllImages(Container container) {
        Arrays.stream(container.getComponents())
                .filter(e -> e instanceof ScribbleLabel)
                .forEach(e -> container.remove(e))
        ;
    }
    
    //==========================================================
    /**
     * The data of the container involved can change for two reasons: either
     * there was a change in curves, or there has been a change in one of the
     * PietsLabels. This class holds a list of all containers involved and
     * whether there has been any change in one of the PietsLabels. However,
     * all the data involved can be saved by the container, outside of this
     * cllass. It should then invoke this method to let this class know that
     * the data is safe. Otherwise this class would still report about unsafe
     * data
     * @param container the Container involved
     * @param b boolean, true when change, false when safe
     */
    public static void setDataHasChanged(Container container, boolean b) {
        changedContainers.put(container, b);
    }
    
    //==========================================================
    /**
     * gives for this container whether the PietsLabels in it have changed
     * the last saf or not.
     * @param container the Container (usually a JPanel)
     * @return 
     */
    public static boolean getDataHasChanged(Container container) {
        return changedContainers.getOrDefault(container, Boolean.FALSE);
    }

    //==========================================================
    // private methods
    //==========================================================
    private void initializeDragOrResize(MouseEvent m, int accuracy) {
        removeExistingBorders();
        setBorder(border);
        Rectangle rect = this.getBounds();
        LabelArea area = LabelArea.getLabelArea(this, m, accuracy);
        dragOrResizeType = LabelArea.getDragOrResizeType(area);
        originalLabelOrigin = this.getLocation();
        dragStart = m.getLocationOnScreen();
        if (dragOrResizeType.isResizingType) {
            List<Point> list = LabelArea.getInvolvedResizingPoints(this, area);
            resizePoint = list.get(0);
            fixedPoint = list.get(1);
        } else {
            resizePoint = null;
            fixedPoint = null;
        }
        this.bringToTop();
        this.repaint();
    }

    //==========================================================
    private void removeExistingBorders() {
        Container container = this.getParent();
        Arrays.stream(container.getComponents())
                .filter(e -> e instanceof ScribbleLabel)
                .map(e -> (ScribbleLabel) e)
                .forEach(e -> {
                    e.setBorder(null);
                    e.repaint();
                });
    }

    //==========================================================
    private void processResizing(MouseEvent m) {
        Point screenPoint = m.getLocationOnScreen();
        int deltaX = screenPoint.x - dragStart.x;
        int deltaY = screenPoint.y - dragStart.y;
        if (dragOrResizeType == DragOrResizeType.RESIZING_HOR) {
            deltaY = 0;
        }
        if (dragOrResizeType == DragOrResizeType.RESIZING_VERT) {
            deltaX = 0;
        }
        Point resize = new Point(resizePoint.x + deltaX, resizePoint.y + deltaY);
        Rectangle rect = calculateRectangle(resize, fixedPoint);
        setNewSize(rect.width, rect.height, false);
        setBounds(rect);
    }

    //==========================================================
    private void processDrag(MouseEvent m) {
        Point screenPoint = m.getLocationOnScreen();
        int deltaX = screenPoint.x - dragStart.x;
        int deltaY = screenPoint.y - dragStart.y;
        setLocation(originalLabelOrigin.x + deltaX, originalLabelOrigin.y + deltaY);
        setDataChanged(true);
    }

    //==========================================================
    private Rectangle calculateRectangle(Point p1, Point p2) {
        int leftX = p1.x <= p2.x ? p1.x : p2.x;
        int topY = p1.y <= p2.y ? p1.y : p2.y;
        int width = Math.abs(p1.x - p2.x);
        int height = Math.abs(p1.y - p2.y);
        width = Math.max(width, 10);
        height = Math.max(height, 10);
        return new Rectangle(leftX, topY, width, height);
    }

    //==========================================================
    private static boolean isImage(File file) {
        if (file == null) {
            return false;
        }
        String fileName = file.getName();
        int lastDot = fileName.lastIndexOf('.');
        if (lastDot < 0) {
            return false;
        }
        String suffix = fileName.substring(lastDot + 1).toLowerCase();
        switch (suffix) {
            case "jpg":
            case "jpeg":
            case "png":
            case "gif":
                return true;
            default:
                return false;
        }
    }

    //==========================================================
    private Point2D determineSizeOfLabel(double maxWidth, double maxHeight) {
        double imageW = fullImage.getWidth();
        double imageH = fullImage.getHeight();
        if (imageW <= maxWidth && imageH <= maxHeight) {
            // no scaling needed
            return new Point2D.Double(imageW, imageH);
        }

        double scaleFactor = Math.max(
                imageH / maxHeight,
                imageW / maxWidth
        );

        return new Point2D.Double(
                imageW / scaleFactor,
                imageH / scaleFactor
        );
    }

    //===========================================================
    private Icon createDefaultIcon() {
        BufferedImage image = new BufferedImage(
                (int) initialMaxWidth,
                (int) initialMaxHeight,
                BufferedImage.TYPE_INT_RGB
        );
        Graphics2D g2d = image.createGraphics();
        BasicStroke stroke = new BasicStroke(10.f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
        g2d.setColor(Color.RED);
        g2d.setStroke(stroke);
        int startX = (int) (image.getWidth() * .25);
        int startY = (int) (image.getHeight() * .25);
        int eindX = (int) (image.getWidth() * .75);
        int eindY = (int) (image.getHeight() * .75);
        g2d.drawLine(startX, startY, eindX, eindY);
        g2d.drawLine(startX, eindY, eindX, startY);
        g2d.dispose();
        return new ImageIcon(image);
    }

    //==========================================================
    private Image getScaledInstanceSmooth(Point2D size) {
        Image temp = fullImage.getScaledInstance(
                (int) size.getX(),
                (int) size.getY(),
                Image.SCALE_SMOOTH
        );
        return temp;
    }

    //==========================================================
    private Image getScaledInstanceQuick(Point2D size) {
        Image temp = fullImage.getScaledInstance(
                (int) size.getX(),
                (int) size.getY(),
                Image.SCALE_FAST
        );
        return temp;
    }
    
    //==========================================================
    private void setDataChanged(boolean b) {
        changedContainers.put(this.getParent(), b);
    }

    //==========================================================

}
