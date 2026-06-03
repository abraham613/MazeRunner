package org.example;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class SetupFrame extends JFrame {

    // רכיבים להצגת ההגדרות מהשרת
    private JLabel wallColorLabel;
    private JLabel pathColorLabel;
    private JLabel drawGridLabel;
    private JLabel gridColorLabel;
    private JLabel delayLabel;

    // שדות קלט למימדים
    private JTextField widthField;
    private JTextField heightField;

    // כפתורים
    private JButton refreshButton;
    private JButton getMazeButton;

    public SetupFrame() {
        // הגדרות בסיסיות של החלון
        setTitle("חלון הגדרות");
        setSize(350, 480); // הגדלנו מעט את הגובה כדי שהכל יישב בנוח
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // הגדרת סידור אנכי (מלמעלה למטה) ישירות על החלון הראשי
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        // הוספת שוליים פנימיים מסביב לחלון (Padding) כדי שהרכיבים לא ייצמדו לקצוות
        ((JPanel)getContentPane()).setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // --- חלק א': כותרת ותצוגת ההגדרות מהשרת ---
        JLabel configTitle = new JLabel("הגדרות ציור מהשרת:");
        configTitle.setFont(new Font("Arial", Font.BOLD, 14));
        configTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(configTitle);
        add(Box.createVerticalStrut(10)); // רווח קטן מתחת לכותרת

        // אתחול והוספת תוויות הנתונים
        wallColorLabel = new JLabel("צבע קירות: אין נתון");
        pathColorLabel = new JLabel("צבע נתיב: אין נתון");
        drawGridLabel = new JLabel("האם לצייר רשת: אין נתון");
        gridColorLabel = new JLabel("צבע רשת: אין נתון");
        delayLabel = new JLabel("זמן המתנה באנימציה: אין נתון");

        // מרכוש הרכיבים והוספתם אחד מתחת לשני
        wallColorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        pathColorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        drawGridLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        gridColorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        delayLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        add(wallColorLabel);
        add(Box.createVerticalStrut(5));
        add(pathColorLabel);
        add(Box.createVerticalStrut(5));
        add(drawGridLabel);
        add(Box.createVerticalStrut(5));
        add(gridColorLabel);
        add(Box.createVerticalStrut(5));
        add(delayLabel);
        add(Box.createVerticalStrut(10));

        // כפתור רענון הגדרות
        refreshButton = new JButton("Refresh Config");
        refreshButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(refreshButton);

        // רווח גדול שמפריד בין החלק של השרת לחלק של קלט המשתמש
        add(Box.createVerticalStrut(25));

        // --- חלק ב': כותרת ושדות קלט של המשתמש ---
        JLabel inputTitle = new JLabel("בחירת גודל המבוך:");
        inputTitle.setFont(new Font("Arial", Font.BOLD, 14));
        inputTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(inputTitle);
        add(Box.createVerticalStrut(10));

        // תווית ושדה עבור רוחב (Width)
        JLabel widthLabel = new JLabel("width");
        widthLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(widthLabel);
        add(Box.createVerticalStrut(5));

        widthField = new JTextField("30", 10);
        widthField.setMaximumSize(new Dimension(100, 25)); // מגביל את גודל השדה שלא יימתח לכל רוחב המסך
        widthField.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(widthField);
        add(Box.createVerticalStrut(10));

        // תווית ושדה עבור גובה (Height)
        JLabel heightLabel = new JLabel("height");
        heightLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(heightLabel);
        add(Box.createVerticalStrut(5));

        heightField = new JTextField("30", 10);
        heightField.setMaximumSize(new Dimension(100, 25));
        heightField.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(heightField);

        // רווח גדול לפני כפתור השיגור הסופי
        add(Box.createVerticalStrut(25));

        // --- חלק ג': כפתור GET MAZE ---
        getMazeButton = new JButton("GET MAZE");
        getMazeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        getMazeButton.setFont(new Font("Arial", Font.BOLD, 14));
        add(getMazeButton);
        fetchRenderConfig();
    }
//    שלב 2)

    private void fetchRenderConfig() {
        // יצירת Thread חדש בעזרת למדא (בלי ה-public run הישן)
        new Thread(() -> {
            try {
                // 1. יצירת לקוח ה-HTTP ובניית הבקשה לכתובת המבוקשת
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("https://backend-qcf9.onrender.com/fm1/get-render-config"))
                        .GET()
                        .build();

                // 2. שליחת הבקשה וקבלת התגובה כטקסט
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                // 3. בדיקה שהשרת החזיר קוד תשובה תקין (200 OK)
                if (response.statusCode() == 200) {
                    String jsonResponse = response.body();

                    // 4. פענוח ה-JSON באמצעות הספרייה מה-POM
                    JSONObject jsonObject = new JSONObject(jsonResponse);

                    String wallCellColor = jsonObject.getString("wallCellColor");
                    String pathColor = jsonObject.getString("pathColor");
                    boolean drawGrid = jsonObject.getBoolean("drawGrid");
                    String gridColor = jsonObject.getString("gridColor");
                    int animationDelayMs = jsonObject.getInt("animationDelayMs");

                    // 5. עדכון הרכיבים הגרפיים בבטחה בעזרת למדא
                    getMazeButton.addActionListener((event) -> {
                        wallColorLabel.setText("צבע קירות: " + wallCellColor);
                        pathColorLabel.setText("צבע נתיב: " + pathColor);
                        drawGridLabel.setText("האם לצייר רשת: " + (drawGrid ? "כן" : "לא"));
                        gridColorLabel.setText("צבע רשת: " + gridColor);
                        delayLabel.setText("זמן המתנה באנימציה: " + animationDelayMs + " מילישניות");
                    });
                } else {
                    showError("שגיאה בקבלת נתונים מהשרת. קוד: " + response.statusCode());
                }

            } catch (Exception e) {
                e.printStackTrace();
                showError("נכשל החיבור לשרת הרשת. ודא שיש אינטרנט.");
            }
        }).start();
    }

    // פונקציית עזר להצגת שגיאות
    private void showError(String message) {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(SetupFrame.this, message, "שגיאת תקשורת", JOptionPane.ERROR_MESSAGE);
        });
    }

}