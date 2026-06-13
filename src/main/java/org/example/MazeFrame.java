package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class MazeFrame extends JFrame {
    private MazePanel mazePanel;

    public MazeFrame(BufferedImage downloadedImage, Color wallColor, Color gridColor, boolean drawGrid, Color pathColor, int delayMs ,int cols ,int rows) {
        setTitle("המבוך הסופי");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // משתמשים ב-BorderLayout שהוא מושלם למטרה הזו
        setLayout(new BorderLayout());

        // יצירת פאנל המבוך
        mazePanel = new MazePanel(downloadedImage, wallColor, gridColor, drawGrid);

        // עטיפת המבוך בתוך JScrollPane - פסי הגלילה יופיעו רק כשבאמת צריך!
        JScrollPane scrollPane = new JScrollPane(mazePanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        // הוספת פאנל הגלילה למרכז החלון
        add(scrollPane, BorderLayout.CENTER);

        // יצירת פאנל תחתון קטן עבור הכפתור כדי שיישאר נעוץ למטה
        JPanel buttonContainer = new JPanel();
        buttonContainer.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JButton checkSolutionButton = new JButton("Check Solution");
        buttonContainer.add(checkSolutionButton);
        add(buttonContainer, BorderLayout.SOUTH);

        // -----------------------------------------------------------------
        // התיקון המרכזי: הגבלת הגודל המקסימלי של החלון שלא יברח מהמסך
        // -----------------------------------------------------------------
        // נקבל את רזולוציית המסך הפיזי של המחשב של המשתמש
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        // נקבע שהחלון לא יוכל להיות גדול יותר מ-85% מגודל המסך (כדי שלא יתחבא מאחורי שורת המשימות)
        int maxWidth = (int) (screenSize.width * 0.85);
        int maxHeight = (int) (screenSize.height * 0.85);

        // נגדיר את הגבולות לחלון
        setMaximumSize(new Dimension(maxWidth, maxHeight));

        // קריאה ל-pack גורמת לחלון להתאים את עצמו בדיוק למבוך קטן, או להתרחב עד הגבול המקסימלי במבוך גדול
        pack();

        // בדיקה קריטית: אם ה-pack חרג מהגודל המקסימלי שהגדרנו, נכווץ אותו ידנית לגודל המקסימלי
        if (getWidth() > maxWidth || getHeight() > maxHeight) {
            setSize(Math.min(getWidth(), maxWidth), Math.min(getHeight(), maxHeight));
        }

        // מרכז את החלון על המסך
        setLocationRelativeTo(null);

        // קוד ה-ActionListener של הכפתור נשאר בדיוק אותו דבר...
        checkSolutionButton.addActionListener(e -> {
            checkSolutionButton.setEnabled(false);
            BufferedImage freshCopy = new BufferedImage(downloadedImage.getWidth(), downloadedImage.getHeight(), downloadedImage.getType());
            Graphics2D g = freshCopy.createGraphics();
            g.drawImage(downloadedImage, 0, 0, null);
            g.dispose();

            ReadPicture readPicture = new ReadPicture(freshCopy, wallColor, gridColor, drawGrid);
            mazePanel.setMazeImage(readPicture.getImage());
            mazePanel.repaint();

            CheckSolution checkSolution = new CheckSolution(readPicture.getImage(), pathColor, delayMs, mazePanel, checkSolutionButton, cols, rows);
        });
    }
}