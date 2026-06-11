package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class MazePanel extends JPanel {
    private final int cellSize = 16;
    private  BufferedImage mazeImage;

    public MazePanel(BufferedImage downloadedImage, Color wallColor, Color gridColor, boolean drawGrid) {
        ReadPicture readPicture = new ReadPicture(downloadedImage, wallColor, gridColor, drawGrid);
        this.mazeImage = readPicture.getImage();
        if (mazeImage != null) {
            int panelWidth = mazeImage.getWidth();
            int panelHeight = mazeImage.getHeight();
            setPreferredSize(new Dimension(panelWidth, panelHeight));
        }
//        else {
//            setPreferredSize(new Dimension(100, 100));
//        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (mazeImage != null) {
            int scaledWidth = mazeImage.getWidth();
            int scaledHeight = mazeImage.getHeight();
            g.drawImage(this.mazeImage, 0, 0, scaledWidth, scaledHeight, null);
        }
    }

    public void setMazeImage(BufferedImage mazeImage) {
        this.mazeImage = mazeImage;
    }
}