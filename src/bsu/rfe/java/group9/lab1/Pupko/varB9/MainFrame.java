package bsu.rfe.java.group9.lab1.Pupko.varB9;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

@SuppressWarnings("serial")
public class MainFrame extends JFrame{

    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;

    private JFileChooser fileChooser = null;

    private JCheckBoxMenuItem showAxisMenuItem;
    private JCheckBoxMenuItem showGridMenuItem;
    private JCheckBoxMenuItem showMarkersMenuItem;

    private GraphicsDisplay display = new GraphicsDisplay();

    private boolean fileLoader = false;

    public MainFrame(){

        super("Graphics drawing on early prepared files ");

        setSize(WIDTH, HEIGHT);
        Toolkit kit = Toolkit.getDefaultToolkit();

        setLocation((kit.getScreenSize().width - WIDTH) / 2,
                (kit.getScreenSize().height - HEIGHT) / 2);

        setExtendedState(MAXIMIZED_BOTH);

        display.setBackground(Color.PINK);

        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        JMenu fileMenu = new JMenu("File");
        menuBar.add(fileMenu);

        Action openGraphicsAction = new AbstractAction("OpenFile with graphic") {

            public void actionPerformed(ActionEvent event) {

                if(fileChooser == null){
                    fileChooser = new JFileChooser();
                    fileChooser.setCurrentDirectory(new File("."));
                }

                if(fileChooser.showOpenDialog(MainFrame.this) ==
                        JFileChooser.APPROVE_OPTION)
                    openGraphics(fileChooser.getSelectedFile());

            }
        };

        fileMenu.add(openGraphicsAction);

        JMenu graphicsMenu = new JMenu("Graphic");
        menuBar.add(graphicsMenu);

        Action showAxisAction = new AbstractAction("Show axis") {

            public void actionPerformed(ActionEvent event) {

                display.setShowAxis(showAxisMenuItem.isSelected());
            }
        };

        showAxisMenuItem = new JCheckBoxMenuItem(showAxisAction);
        graphicsMenu.add(showAxisMenuItem);
        showAxisMenuItem.setSelected(true);

        Action showGridAction = new AbstractAction("Show grid") {

            public void actionPerformed(ActionEvent event) {

                display.setShowGrid(showGridMenuItem.isSelected());
            }
        };

        showGridMenuItem = new JCheckBoxMenuItem(showGridAction);
        graphicsMenu.add(showGridMenuItem);
        showGridMenuItem.setSelected(true);

        Action showMarkersAction = new AbstractAction("Show Markers") {

            public void actionPerformed(ActionEvent event) {

                display.setShowMarkers(showMarkersMenuItem.isSelected());
            }
        };

        showMarkersMenuItem = new JCheckBoxMenuItem(showMarkersAction);
        graphicsMenu.add(showMarkersMenuItem);
        showMarkersMenuItem.setSelected(true);

        graphicsMenu.addMenuListener(new GraphicsMenuListener());

        getContentPane().add(display, BorderLayout.CENTER);
    }

    protected void openGraphics(File selectedFile){

        try{

            DataInputStream in = new DataInputStream(new
                    FileInputStream(selectedFile));

            Double[][] graphicsData = new
                    Double[in.available()/(Double.SIZE/8)/2][];

            int i = 0;
            while (in.available() > 0){

                Double x = in.readDouble();
                Double y = in.readDouble();
                graphicsData[i++] = new Double[]{x, y};
            }

            if(graphicsData != null && graphicsData.length > 0){

                fileLoader = true;
                display.showGraphics(graphicsData);
                in.close();
            }
        }catch (FileNotFoundException exception){

            JOptionPane.showMessageDialog(MainFrame.this,
                    "Mentioned file not found", "Data set mistake",
                    JOptionPane.WARNING_MESSAGE);

            return;
        }catch (IOException exception){

            JOptionPane.showMessageDialog(MainFrame.this,
                    "Reading cordinat from file mistake", "Data load mistake",
                    JOptionPane.WARNING_MESSAGE);

            return;
        }
    }

    public static void main(String[] args){

        MainFrame frame = new MainFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private class GraphicsMenuListener implements MenuListener{

        public void menuSelected(MenuEvent event){

            showAxisMenuItem.setEnabled(fileLoader);
            showMarkersMenuItem.setEnabled(fileLoader);
            showGridMenuItem.setEnabled(fileLoader);
        }

        public void menuDeselected(MenuEvent event){

        }

        public void menuCanceled(MenuEvent event){

        }
    }

}
