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
    private BufferedImage copyImage;

    private static final int CELL_SIZE = 16;

    //    /**
//     * פונקציה ראשית שמוצאת את המסלול מהפינה השמאלית העליונה לימנית התחתונה,
//     * צובעת אותו באנימציה ומקפיצה הודעה אם אין פתרון.
//     *
//     * @param mazeImage    תמונת המבוך
//     * @param wallColor    צבע הקיר החסום (מתקבל מהשרת)
//     * @param pathColor    הצבע שבו צריך לצבוע את המסלול הפתור (מתקבל מהשרת)
//     * @param delayMs      זמן המתנה במילישניות בין משבצת למשבצת (מתקבל מהשרת)
//     * @param displayPanel הפאנל הגרפי שמציג את המבוך (לצורך ביצוע repaint בלייב)
//     */
    public CheckSolution(BufferedImage mazeImage, Color pathColor, int delayMs, JPanel displayPanel, JButton checkSolutionButton) {
        this.copyImage=mazeImage;

        if (mazeImage == null) return;

        // 1. הגדרת נקודת התחלה קבועה (0,0)
        int startCol = 0;
        int startRow = 0;

        // 2. חישוב כמות השורות והעמודות, וקביעת נקודת הסוף לפינה הימנית התחתונה
        int totalCols = mazeImage.getWidth() / CELL_SIZE;
        int totalRows = mazeImage.getHeight() / CELL_SIZE;

        int endCol = totalCols - 1;
        int endRow = totalRows - 1;

        // 3. אתחול מבני הנתונים לאלגוריתם הסריקה (BFS)
        boolean[][] visited = new boolean[totalRows][totalCols];
        Queue<Point> queue = new LinkedList<>();
        Map<Point, Point> parentMap = new HashMap<>(); // מפת האבות לשחזור המסלול הנקי

        Point startPoint = new Point(startRow, startCol);
        Point endPoint = null;

        queue.add(startPoint);
        visited[startRow][startCol] = true;

        // מערכי עזר לתנועה ב-4 כיוונים
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

                        // דגימת פיקסל האמצע של המשבצת לבדיקת צבעה
                        int pixelX = (nextCol * CELL_SIZE) + (CELL_SIZE / 2);
                        int pixelY = (nextRow * CELL_SIZE) + (CELL_SIZE / 2);
                        Color pixelColor = new Color(mazeImage.getRGB(pixelX, pixelY));

                        // אם זה לא קיר, נרשום את האבא של המשבצת ונכניס לתור
                        // בדיקה האם המשבצת הבאה היא נתיב פנוי (במבוך שחזר מהשרת, נתיב פנוי הוא תמיד לבן)
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
                Graphics2D g2d =copyImage.createGraphics();
                g2d.setColor(pathColor);

                for (Point p : finalPath) {
                    int x = p.getCol() * CELL_SIZE;
                    int y = p.getRow() * CELL_SIZE;

                    // צביעת משבצת ה-16x16 בצבע המסלול הנבחר
                    g2d.fillRect(x, y, CELL_SIZE, CELL_SIZE);

                    // עדכון התצוגה על המסך בלייב
                    if (displayPanel != null) {
                        displayPanel.repaint();
                    }

                    // השהייה מוגדרת מראש בין משבצת למשבצת
                    try {
                        Thread.sleep(delayMs);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                checkSolutionButton.setEnabled(true);
                g2d.dispose();
            }).start();

        } else {
            // אם לא נמצא פתרון - הקפצת הודעה למשתמש
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