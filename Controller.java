/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scribble_version_51_smoothing_version_01;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JToggleButton;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author Sylvia
 */
public class Controller {
    
    private JFrame controllerFrame;
    private ScribblePanelsViewer showPanelsFrame;
    private float strokeWidth;
    private Color currentColor;
    private String currentShape;
    private int numberOfPointsForSmoothing;
    private int useOnlyEveryXRawPoints;
    private static Map<String, Supplier<ScribbleShape>> shapeMap;
    JFileChooser jfc;
    private Color lightblue, darkerblue;
    Color startingBackground = new Color(200, 255, 200);
    
    //===============================================
    // constructor
    //===============================================
    public Controller() {
        
        initializeVariables();
        createShapeMap();
        createControllerFrame();
    }
    
    //********************************************************************
    // public methods
    //********************************************************************
    public ScribbleShape getShape() {
        if (currentShape == null || currentShape.isEmpty()) return null;
        return shapeMap.getOrDefault(
                currentShape, () -> createScribbleSmoothedLine() 
            )
        .get();
    }
    
    //==============================================
    public Color getColor() {
        return currentColor;
    }
    
    //********************************************************************
    // private methods
    //********************************************************************
    private void initializeVariables() {
        strokeWidth = 1;
        currentColor = Color.BLACK;
        currentShape = "ScribbleSmoothedLine";
        numberOfPointsForSmoothing = 5;
        useOnlyEveryXRawPoints = 3;
        showPanelsFrame = new ScribblePanelsViewer(500, 500, this);
        showPanelsFrame.setBackground(startingBackground);
        lightblue = new Color(220, 220, 255);
        darkerblue = new Color(200, 200, 255);
        
        if (shapeMap != null) {
            shapeMap = new LinkedHashMap<>();
            shapeMap.put("Smoothed Line", () -> createScribbleSmoothedLine());
            shapeMap.put("Line Of Circles", () -> createLineOfCircles());
            shapeMap.put("Raw Curve", () -> createRawCurve());
            shapeMap.put("Rectangle", () -> createScribbleRectangle());
            shapeMap.put("Ellipse", () -> createScribbleEllipse());
        }
        jfc = new JFileChooser();
        String s = "jpg, jpeg, gif, png";
        FileNameExtensionFilter f = new FileNameExtensionFilter(s, "jpg", "jpeg", "gif", "png");
        jfc.setFileFilter(f);
    }
    
    //==============================================
    private void createShapeMap() {
        if (shapeMap != null) return;
        shapeMap = new LinkedHashMap<>();
        shapeMap.put("Smoothed Line", () -> createScribbleSmoothedLine());
        shapeMap.put("Line Of Circles", () -> createLineOfCircles());
        shapeMap.put("Raw Curve", () -> createRawCurve());
        shapeMap.put("Rectangle", () -> createScribbleRectangle());
        shapeMap.put("Ellipse", () -> createScribbleEllipse());
    }
    
    //==============================================
    private void createControllerFrame() {
        controllerFrame = new JFrame("Control Frame");
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.PAGE_AXIS));
        controllerFrame.setContentPane(content);
        
        createChooseColorSection();
        createChooseBackgroundSection();
        createLineThicknessSection();
        createShapeChooserSection();
        createClearSection();
        createImageSection();
        createPanelSection();
        createSaveSection();
        
        controllerFrame.pack();
        controllerFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        int left = d.width - 2 * controllerFrame.getWidth();
        int top = (d.height - controllerFrame.getHeight()) / 2;
        controllerFrame.setLocation(left, top);
        controllerFrame.setVisible(true);
    }
    
    //==============================================
    private void createChooseColorSection() {
        int width = 32, height = 32;
        final ButtonGroup group = new ButtonGroup();
        
        ActionListener al = e -> {
            ScribbleColorRadioButton radio = (ScribbleColorRadioButton) e.getSource();
            currentColor = radio.getColor();
        };
        
        //fixed colors
        JPanel fixedColorsPanel = new JPanel(new GridLayout(0, 3, 2, 2));
        fixedColorsPanel.setBackground(lightblue);
        fixedColorsPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        
        // creating and adding the color radiobuttons
        ScribbleColorRadioButton black = ScribbleColorRadioButton.createColorRadioButton(
            width, height, Color.BLACK, al, group, false
        );
        
        ScribbleColorRadioButton red = ScribbleColorRadioButton.createColorRadioButton(
            width, height, Color.RED, al, group, false
        );
        ScribbleColorRadioButton blue = ScribbleColorRadioButton.createColorRadioButton(
            width, height, Color.BLUE, al, group, false
        );
        
        ScribbleColorRadioButton yellow = ScribbleColorRadioButton.createColorRadioButton(
            width, height, Color.YELLOW, al, group, false
        );
        
        ScribbleColorRadioButton cyan = ScribbleColorRadioButton.createColorRadioButton(
            width, height, Color.CYAN, al, group, false
        );
        
        ScribbleColorRadioButton grey = ScribbleColorRadioButton.createColorRadioButton(
            width, height, Color.GRAY, al, group, false
        );
        
        final ScribbleColorRadioButton user = ScribbleColorRadioButton.createColorRadioButton(
            width, height, Color.GREEN, al, group, false
        );
           
        black.setSelected(true);
        
        ActionListener chooseColor = e -> {
            Color newColor = JColorChooser.showDialog(controllerFrame, "Choose color", user.getColor());
            if (newColor == null) return;
            user.changeColor(newColor);
            user.setSelected(true);
            currentColor = newColor;
        };
    
        JButton chooseButton = new JButton("Other Color");
        chooseButton.addActionListener(chooseColor);
        
        fixedColorsPanel.add(black);
        fixedColorsPanel.add(red);
        fixedColorsPanel.add(blue);
        fixedColorsPanel.add(yellow);
        fixedColorsPanel.add(cyan);
        fixedColorsPanel.add(grey);
        fixedColorsPanel.add(chooseButton);
        fixedColorsPanel.add(user);
        
        JPanel total = new JPanel(new GridLayout(0, 1, 5, 5));
        Border title = BorderFactory.createEtchedBorder();
        total.setBorder(BorderFactory.createTitledBorder(title, "Choose Colors", TitledBorder.CENTER, TitledBorder.TOP));
        
        total.add(fixedColorsPanel);
//        total.add(userChoicePanel);
        
        controllerFrame.add(total);
    }
    
    //==============================================
    private void createChooseBackgroundSection() {
        
        JPanel backgroundPanel = new JPanel(new GridLayout(0, 2, 2, 2));
        backgroundPanel.setBackground(darkerblue);
        Border title = BorderFactory.createEtchedBorder();
        backgroundPanel.setBorder(BorderFactory.createTitledBorder(title, "Choose Backgroud", TitledBorder.CENTER, TitledBorder.TOP));
        
        JButton label = new JButton();
        label.setBackground(startingBackground);
        
        ActionListener chooseColor = e -> {
            Color newColor = JColorChooser.showDialog(controllerFrame, "Choose color", label.getBackground());
            if (newColor == null) return;
            label.setBackground(newColor);
            showPanelsFrame.setBackground(newColor);
        };
    
        JButton chooseButton = new JButton("Choose Color");
        chooseButton.addActionListener(chooseColor);
        
        backgroundPanel.add(chooseButton);
        backgroundPanel.add(label);
        
        controllerFrame.add(backgroundPanel);
    }
    
    //==============================================
    private void createLineThicknessSection() {
        JPanel lineThickness = new JPanel(new GridLayout(1, 0, 5, 5));
        Border title = BorderFactory.createEtchedBorder();
        lineThickness.setBorder(BorderFactory.createTitledBorder(title, "Line Thickness", TitledBorder.CENTER, TitledBorder.TOP));
        lineThickness.setBackground(lightblue);
        
        String[] values = {"1", "2", "3", "4", "5", "6"};
        JComboBox combo = new JComboBox(values);
        combo.setSelectedItem("1");
        combo.addActionListener(e -> {
            JComboBox c = (JComboBox) e.getSource();
            strokeWidth = Integer.parseInt((String) c.getSelectedItem());
        });
        lineThickness.add(combo);
        controllerFrame.add(Box.createRigidArea(new Dimension(0, 10)));
        controllerFrame.add(lineThickness);
    }
    
    //==============================================
    private void createShapeChooserSection() {
        JPanel shapesPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        Border title = BorderFactory.createEtchedBorder();
        shapesPanel.setBorder(BorderFactory.createTitledBorder(title, "Choose Shape", TitledBorder.CENTER, TitledBorder.TOP));
        shapesPanel.setBackground(darkerblue);
        
        String[] shapes = shapeMap.keySet().toArray(new String[0]);
        JComboBox combo = new JComboBox(shapes);
        combo.setSelectedIndex(0);
        ActionListener al = e -> {
            JComboBox c = (JComboBox) e.getSource();
            currentShape = (String) c.getSelectedItem();
        };
        combo.addActionListener(al);
        shapesPanel.add(combo);
        controllerFrame.add(Box.createRigidArea(new Dimension(0, 10)));
        controllerFrame.add(shapesPanel);
    }
    
    //==============================================
    private void createClearSection() {
        JPanel clearPanel = new JPanel();
        Border title = BorderFactory.createEtchedBorder();
        clearPanel.setBorder(BorderFactory.createTitledBorder(title, "Clear", TitledBorder.CENTER, TitledBorder.TOP));
        clearPanel.setBackground(lightblue);
        clearPanel.setLayout(new GridLayout(0, 2, 2, 2));
        
        JButton clearAll = new JButton("Current Panel");
        clearAll.addActionListener(e -> {
            showPanelsFrame.clear();
        });
        clearAll.setAlignmentX(1f);
        
        JButton clearShapes = new JButton("All Shapes");
        clearShapes.addActionListener( e -> {
            showPanelsFrame.clearAllCurves();
        });
        clearShapes.setAlignmentX(1f);
        
        JButton clearMostRecent = new JButton("Last Shape");
        clearMostRecent.addActionListener( e -> {
            showPanelsFrame.clearMostRecentCurve();
        });
        clearMostRecent.setAlignmentX(1f);
        
        clearPanel.add(clearAll);
        clearPanel.add(clearShapes);
        clearPanel.add(clearMostRecent);
        
        controllerFrame.add(clearPanel);
    }
    
    //==============================================
    private void createImageSection() {
        JPanel imagePanel = new JPanel();
        Border title = BorderFactory.createEtchedBorder();
        imagePanel.setBorder(BorderFactory.createTitledBorder(title, "Image", TitledBorder.CENTER, TitledBorder.TOP));
        imagePanel.setBackground(darkerblue);
        JButton button = new JButton("Load Image");
        button.addActionListener(e -> {
            File f = loadImage();
            if (f == null) return;
            showPanelsFrame.addImage(f);
        });
        
        JButton screenshot = new JButton("Take Screenshot");
        screenshot.addActionListener(e -> {
            boolean showPanels = showPanelsFrame.isVisible();
            showPanelsFrame.setVisible(false);
            controllerFrame.setVisible(false);
            try {
                Toolkit tk = Toolkit.getDefaultToolkit(); 
                Dimension d = tk.getScreenSize();
                Rectangle rec = new Rectangle(0, 0, d.width, d.height);  
                Robot ro = new Robot();
                Thread.sleep(200);
                BufferedImage img = ro.createScreenCapture(rec);
                showPanelsFrame.addImage(img);
            } 
            catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
            finally {
                controllerFrame.setVisible(true);
                showPanelsFrame.setVisible(showPanels);
            }
        });
        
        JToggleButton show = new JToggleButton("Resize/Drag Image");
        show.addActionListener(e -> {
            if (show.isSelected()) {
                showPanelsFrame.setStatusToDragOrResizeImage();
            }
            else {
                showPanelsFrame.setStatusToDrawingShapes();
            }
        });
        
        imagePanel.setLayout(new GridLayout(0, 2, 2, 2));
        imagePanel.add(button);
        imagePanel.add(screenshot);
        imagePanel.add(show);
        
        controllerFrame.add(imagePanel);
    }
    
    //==============================================
    private void createPanelSection() {
        JPanel panel = new JPanel();
        Border title = BorderFactory.createEtchedBorder();
        panel.setBorder(BorderFactory.createTitledBorder(title, "Panels", TitledBorder.CENTER, TitledBorder.TOP));
        panel.setBackground(lightblue);
        panel.setLayout(new GridLayout(0, 2, 2, 0));
        
        JButton show = new JButton("Show");
        show.addActionListener(e -> showPanelsFrame.setVisible(true));
        
        JButton close = new JButton("Hide");
        close.addActionListener(e -> showPanelsFrame.setVisible(false));
        
        JButton newPanel = new JButton("New");
        newPanel.addActionListener(e -> showPanelsFrame.addPanel());
        
        JButton delPanel = new JButton("Delete");
        delPanel.addActionListener(e -> showPanelsFrame.remove());
        
        panel.add(show);
        panel.add(close);
        panel.add(newPanel);
        panel.add(delPanel);
        
        controllerFrame.add(panel);

    }
    
    //==============================================
    private void createSaveSection() {
        JPanel panel = new JPanel();
        Border title = BorderFactory.createEtchedBorder();
        panel.setBorder(BorderFactory.createTitledBorder(title, "Save", TitledBorder.CENTER, TitledBorder.TOP));
        panel.setBackground(darkerblue);
        panel.setLayout(new GridLayout(0, 2, 2, 0));
        
        JButton saveOnePanel = new JButton("Panel");
        JButton saveAllPanels = new JButton("All Panels");
        
        saveOnePanel.addActionListener(e -> showPanelsFrame.save());
        saveAllPanels.setEnabled(false);
        
        panel.add(saveOnePanel);
        panel.add(saveAllPanels);
        
        controllerFrame.add(panel);
    }
    
    //==============================================
    private ScribbleShape createScribbleSmoothedLine() {
        ScribbleSmoothedLine curve = new ScribbleSmoothedLine()
                .setColor(currentColor)
                .setStrokeWidth(strokeWidth)
                .setNrOfPointsToSmooth(numberOfPointsForSmoothing)
                .setPointThreshold(useOnlyEveryXRawPoints)
        ;
        return curve;
    }
    
    //==============================================
    private ScribbleShape createLineOfCircles() {
        LineOfCircles curve = new LineOfCircles()
                    .setColor(currentColor)
                    .setStrokeWidth(strokeWidth);
        ;
        return curve;
    }
    
    //==============================================
    private ScribbleShape createRawCurve() {
        RawCurve rawcurve = new RawCurve()
                .setColor(currentColor)
                .setStrokeWidth(strokeWidth)
        ;
        return rawcurve;
    }
    
    //==============================================
    private ScribbleShape createScribbleRectangle() {
        ScribbleRectangle r = new ScribbleRectangle()
                .setColor(currentColor)
                .setStrokeWidth(strokeWidth)
        ;
        return r;
    }
    
    //==============================================
    private ScribbleShape createScribbleEllipse() {
        ScribbleEllipse e = new ScribbleEllipse()
                .setColor(currentColor)
                .setStrokeWidth(strokeWidth)
        ;
        return e;
    }
    
    //==============================================
    private File loadImage() {
        int x = jfc.showOpenDialog(controllerFrame);
        if (x != JFileChooser.APPROVE_OPTION) return null;
        File f = jfc.getSelectedFile();
        return f.exists() ? f : null;
    }
 
}
