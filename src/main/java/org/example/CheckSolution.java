package org.example;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import javax.swing.*;

public class CheckSolution {
    private int cellW;
    private int cellH;

    public CheckSolution(BufferedImage mazeImage, Color pathColor, int delayMs, JPanel displayPanel, JButton checkSolutionButton, int cols, int rows) {
        cellW = mazeImage.getWidth()/cols;
        cellH = mazeImage.getWidth()/rows;
        if (mazeImage == null) return;

        // 1. הגדרת נקודת התחלה קבועה (0,0)
        int startCol = 0;
        int startRow = 0;

        // 2. חישוב כמות השורות והעמודות
        int totalCols = mazeImage.getWidth() / cellW;
        int totalRows = mazeImage.getHeight() / cellH;

        int endCol = totalCols - 1;
        int endRow = totalRows - 1;

        // 3. אתחול מבני הנתונים לאלגוריתם הסריקה (BFS)
        boolean[][] visited = new boolean[totalRows][totalCols];
        Queue<Point> queue = new LinkedList<>();
        Map<Point, Point> parentMap = new HashMap<>();

        Point startPoint = new Point(startRow, startCol);
        Point endPoint = null;

        queue.add(startPoint);
        visited[startRow][startCol] = true;

        // מערכי עזר לתנועה ב-4 כיוונים (למעלה, למטה, ימינה, שמאלה)
        int[] dRow = {-1, 1, 0, 0};
        int[] dCol = {0, 0, 1, -1};

        // 4. הרצת אלגוריתם החיפוש
        while (!queue.isEmpty()) {
            Point current = queue.poll();

            // הגעה ליעד (פינה ימנית למטה)
            if (current.getRow() == endRow && current.getCol() == endCol) {
                endPoint = current;
                break;
            }

            for (int i = 0; i < 4; i++) {
                int nextRow = current.getRow() + dRow[i];
                int nextCol = current.getCol() + dCol[i];

                if (nextRow >= 0 && nextRow < totalRows && nextCol >= 0 && nextCol < totalCols) {
                    if (!visited[nextRow][nextCol]) {

                        // =========================================================================
                        // החישוב המדויק: דגימה בול במרכז המשבצת כדי לא לגעת בקווי הרשת הצהובים!
                        // גודל כל קובייה הוא 16 פיקסלים, לכן האמצע הטהור שלה הוא + 8 פיקסלים בדיוק
                        // =========================================================================
                        int pixelX = (nextCol * cellW) + (cellW / 2);
                        int pixelY = (nextRow * cellH) + (cellH / 2);

                        Color pixelColor = new Color(mazeImage.getRGB(pixelX, pixelY));

                        // אם המשבצת הבאה היא שביל לבן פנוי - נתקדם אליה
                        if (pixelColor.equals(Color.WHITE)) {
                            Point nextPoint = new Point(nextRow, nextCol);
                            visited[nextRow][nextCol] = true;
                            parentMap.put(nextPoint, current);
                            queue.add(nextPoint);
                        }
                    }
                }
            }
        }

        // 5. בדיקה האם נמצא מסלול או שהמבוך אינו פתיר
        if (endPoint != null) {
            // שחזור המסלול מהסוף להתחלה
            List<Point> finalPath = new ArrayList<>();
            Point curr = endPoint;

            while (curr != null) {
                finalPath.add(curr);
                curr = parentMap.get(curr);
            }

            // הפיכת המסלול כדי שירוץ מההתחלה לסוף
            Collections.reverse(finalPath);

            // הרצת האנימציה ב-Thread נפרד כדי למנוע קפיאה של הממשק
            new Thread(() -> {
                Graphics2D g2d = mazeImage.createGraphics();
                g2d.setColor(pathColor);

                for (Point p : finalPath) {
                    int x = p.getCol() * cellW;
                    int y = p.getRow() * cellH;

                    // צביעת משבצת ה-16x16 בצבע המסלול הנבחר
                    g2d.fillRect(x, y, cellW, cellH);

                    // עדכון התצוגה על המסך בלייב
                    if (displayPanel != null) {
                        displayPanel.repaint();
                    }

                    // השהייה בין משבצת למשבצת
                    try {
                        Thread.sleep(delayMs);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                // שחרור החסימה של הכפתור בסיום האנימציה
                checkSolutionButton.setEnabled(true);
                g2d.dispose();
            }).start();

        } else {
            // אם לא נמצא פתרון - הקפצת הודעה למשתמש ושחרור הכפתור
            checkSolutionButton.setEnabled(true);
            javax.swing.SwingUtilities.invokeLater(() -> {
                javax.swing.JOptionPane.showMessageDialog(
                        displayPanel,
                        "לא נמצא מסלול פתיר למבוך זה! נקודת הסוף חסומה.",
                        "אין פתרון",
                        javax.swing.JOptionPane.WARNING_MESSAGE
                );
            });
        }
    }
}