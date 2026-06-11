package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class MazeFrame extends JFrame {
    private MazePanel mazePanel;


    public MazeFrame(BufferedImage downloadedImage, Color wallColor, Color gridColor, boolean drawGrid,Color pathColor, int delayMs) {
        setTitle("המבוך הסופי");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainContainer = new JPanel();
        mainContainer.setLayout(new BoxLayout(mainContainer, BoxLayout.Y_AXIS));
        mainContainer.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        mazePanel = new MazePanel( downloadedImage, wallColor, gridColor, drawGrid);
        mazePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainContainer.add(mazePanel);
        mainContainer.add(Box.createVerticalStrut(5));

        JButton checkSolutionButton = new JButton("Check Solution");
        checkSolutionButton.setAlignmentX(Component.CENTER_ALIGNMENT);


           checkSolutionButton.addActionListener(e -> {
               checkSolutionButton.setEnabled(false);

               ReadPicture readPicture = new ReadPicture(downloadedImage, wallColor, gridColor, drawGrid);

               CheckSolution checkSolution = new CheckSolution(readPicture.getImage(),pathColor,1,mazePanel,checkSolutionButton);

           });

           mainContainer.add(checkSolutionButton);
           add(mainContainer);
           pack();
           setLocationRelativeTo(null);

    }
}