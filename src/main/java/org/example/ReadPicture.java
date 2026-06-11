package org.example;

import java.awt.Color; // ייבוא מחלקת Color
 // ייבוא מחלקת Color
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import javax.imageio.ImageIO;

public class ReadPicture {
    private BufferedImage image;
    private boolean[][] isWhitePix; // השדה לאחסון מידע הפיקסלים

    // שדות הקונפיגורציה הוסרו - כעת הם מועברים כפרמטרים לבנאי ולמתודות הפרטיות.

    // הבנאי מקבל קובץ תמונה ואת כל הגדרות העיבוד הנדרשות
    public ReadPicture(BufferedImage imageFile, Color wallColor, Color gridColor, boolean drawGrid) {
        // הפרמטרים pathColor ו-animationDelayMinutes אינם משמשים לעיבוד התמונה במחלקה זו,
        // ולכן אינם נכללים בבנאי. אם הם נדרשים, יש לנהל אותם במחלקה הקוראת.
        int cellSize = 16;
        this.image=imageFile;

            int width = image.getWidth();
            int height = image.getHeight();

            this.isWhitePix = new boolean[height][width];

            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int pixel = image.getRGB(x, y);
                     if(pixel == Color.WHITE.getRGB()){
                         this.isWhitePix[y][x]=true;
                         }

//                        image.setRGB(x, y, wallColor.getRGB());
                }
            }

            // קריאה למתודות הפרטיות עם הפרמטרים שהתקבלו בבנאי
            paintWalls(wallColor);
            drawGridLines(gridColor, drawGrid,cellSize);


    }

    public BufferedImage getImage() {
        return image;
    }

    // מתודת setImage הוסרה - האובייקט הוא Immutable.
    // אם יש צורך לעבד תמונה אחרת, יש ליצור אובייקט ReadPicture חדש.


    // מתודות getter/setter עבור שדות הקונפיגורציה הוסרו, מכיוון שהם אינם שדות במחלקה זו.

    // מתודה פרטית לצביעת פיקסלים שאינם לבנים בצבע הקיר
    private void paintWalls(Color wallColor) { // מקבלת wallColor כפרמטר
        if (image == null || isWhitePix == null) {
            return;
        }

        int width = image.getWidth();
        int height = image.getHeight();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (!isWhitePix[y][x]&& wallColor!=null) {
                    image.setRGB(x, y, wallColor.getRGB());
                }
            }
        }
    }


    // מתודה פרטית לצביעת קווי רשת לפי גבולות הקוביות
    private void drawGridLines(Color gridColor, boolean drawGrid,int cellSize) { // מקבלת gridColor ו-drawGrid כפרמטרים
        if (image == null || !drawGrid || isWhitePix == null || gridColor==null) {
            return; // אין מה לצייר אם אין תמונה, drawGrid הוא false, או מפת הפיקסלים לא אותחלה
        }

//        int gridColorRGB = gridColor.getRGB();

        for (int i = 0; i <this.image.getHeight() ; i=i+cellSize) {
            for (int j = 0; j <this.image.getWidth() ; j++) {
                this.image.setRGB(j, i, gridColor.getRGB());
            }
        }

        for (int i = 0; i <this.image.getHeight() ; i++) {
            for (int j = 0; j <this.image.getWidth() ; j=j+cellSize) {
                this.image.setRGB(j, i, gridColor.getRGB());
            }
        }


    }
}