package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class MazePanel extends JPanel {
    private final int cellSize = 16;
    private BufferedImage mazeImage;

    public MazePanel(BufferedImage downloadedImage, Color wallColor, Color gridColor, boolean drawGrid) {
        ReadPicture readPicture = new ReadPicture(downloadedImage, wallColor, gridColor, drawGrid);
        this.mazeImage = readPicture.getImage();
        updatePanelSize(); // קריאה לפונקציית עדכון הגודל
    }

    // פונקציה פנימית שמגדירה את הגודל לפי מידות התמונה הנוכחית
    private void updatePanelSize() {
        if (mazeImage != null) {
            int panelWidth = mazeImage.getWidth();
            int panelHeight = mazeImage.getHeight();
            Dimension size = new Dimension(panelWidth, panelHeight);

            setPreferredSize(size);
            setMinimumSize(size); // דואג שפסי הגלילה יבינו את הגודל המינימלי
        }
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
        updatePanelSize(); // עדכון מידות הפאנל במקרה שהחלפת תמונה
        revalidate();      // גורם ל-JScrollPane לעדכן את פסי הגלילה בזמן אמת!
    }
}