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
    private JButton showMazeButton; // ** נוסף: כפתור ייעודי לפתיחת חלון המבוך **

    // משתנה התמונה שנשמר במחלקה
    private java.awt.image.BufferedImage loadedMazeImage;

    // משתנים לשמירת ההגדרות מהשרת (שלבים 2+3)
    private String wallCellColor;
    private String pathColor;
    private boolean drawGrid;
    private String gridColor;
    private int animationDelayMs;

    private Color newWallColor;
    private Color newPathColor;
    private Color newGridColor;


    // משתנים לשמירת מידות המבוך בפועל (שלב 4)
    private int actualWidth;
    private int actualHeight;

    public SetupFrame() {
        // הגדרות בסיסיות של החלון - בדיוק כמו במקור שלך!
        setTitle("חלון הגדרות");
        setSize(350, 560); // הגבהתי מעט (מ-520 ל-560) רק כדי לפנות מקום לכפתור החדש
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // שימוש ב-BoxLayout אנכי ישירות על החלון, בדיוק כמו שהיה לך לפני השינויים
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        ((JPanel)getContentPane()).setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // --- חלק א': כותרת ותצוגת ההגדרות מהשרת (שלב 1) ---
        JLabel configTitle = new JLabel("הגדרות ציור מהשרת:");
        configTitle.setFont(new Font("Arial", Font.BOLD, 14));
        configTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(configTitle);
        add(Box.createVerticalStrut(10));

        // אתחול והוספת תוויות הנתונים
        wallColorLabel = new JLabel("צבע קירות: טוען...");
        pathColorLabel = new JLabel("צבע נתיב: טוען...");
        drawGridLabel = new JLabel("האם לצייר רשת: טוען...");
        gridColorLabel = new JLabel("צבע רשת: טוען...");
        delayLabel = new JLabel("זמן המתנה באנימציה: טוען...");

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

        // כפתור רענון הגדרות (שלב 3)
        refreshButton = new JButton("Refresh Config");
        refreshButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(refreshButton);

        add(Box.createVerticalStrut(25));

        // --- חלק ב': כותרת ושדות קלט של המשתמש (שלב 4) ---
        JLabel inputTitle = new JLabel("בחירת גודל המבוך:");
        inputTitle.setFont(new Font("Arial", Font.BOLD, 14));
        inputTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(inputTitle);
        add(Box.createVerticalStrut(10));

        JLabel widthLabel = new JLabel("width");
        widthLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(widthLabel);
        add(Box.createVerticalStrut(5));

        widthField = new JTextField("30", 10);
        widthField.setMaximumSize(new Dimension(100, 25));
        widthField.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(widthField);
        add(Box.createVerticalStrut(10));

        JLabel heightLabel = new JLabel("height");
        heightLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(heightLabel);
        add(Box.createVerticalStrut(5));

        heightField = new JTextField("30", 10);
        heightField.setMaximumSize(new Dimension(100, 25));
        heightField.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(heightField);

        add(Box.createVerticalStrut(25));

        // --- חלק ג': כפתור GET MAZE ושליפת התמונה (שלבים 4 ו-5) ---
        getMazeButton = new JButton("GET MAZE");
        getMazeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        getMazeButton.setFont(new Font("Arial", Font.BOLD, 14));
        add(getMazeButton);

        add(Box.createVerticalStrut(10)); // מרווח קטן בין הכפתורים

        // ** נוסף: יצירת כפתור SHOW MAZE מתחת ל-GET MAZE **
        showMazeButton = new JButton("FIRST ENTER GET MAZE");
        showMazeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        showMazeButton.setFont(new Font("Arial", Font.BOLD, 14));
        showMazeButton.setEnabled(false); // חסום ללחיצה בהתחלה (ייפתח רק כשיש תמונה)
        add(showMazeButton);

        fetchRenderConfig();
        startButtons();
    }

    private void startButtons() {
        refreshButton.addActionListener((event) -> {
            fetchRenderConfig();
        });

        getMazeButton.addActionListener((event) -> {
            actualWidth = validateDimension(widthField.getText());
            actualHeight = validateDimension(heightField.getText());

            widthField.setText(String.valueOf(actualWidth));
            heightField.setText(String.valueOf(actualHeight));

            getMazeButton.setText("טוען מבוך...");
            getMazeButton.setEnabled(false);
            showMazeButton.setEnabled(false); // חוסם את כפתור ההצגה בזמן טעינה חדשה

            new Thread(() -> {
                try {
                    String url = "https://backend-qcf9.onrender.com/fm1/get-maze-image?width=" + actualWidth + "&height=" + actualHeight;

                    HttpClient client = HttpClient.newHttpClient();
                    HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
                    HttpResponse<java.io.InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());

                    if (response.statusCode() == 200) {
//                        pickcherW = loadedMazeImage.getWidth() / widthField;
//                        pickcherH = loadedMazeImage.getHeight() / heightField;
                        java.awt.image.BufferedImage mazeImage = javax.imageio.ImageIO.read(response.body());

                        SwingUtilities.invokeLater(() -> {
                            getMazeButton.setText("GET MAZE");
                            getMazeButton.setEnabled(true);

                            // שמירת התמונה שהורדה לשימוש עתידי בלחיצה על SHOW MAZE
                            SetupFrame.this.loadedMazeImage = mazeImage;

                            // ** הפעלת הכפתור החדש! כעת המשתמש יכול לפתוח את חלון המבוך **
                            showMazeButton.setEnabled(true);
                            showMazeButton.setText("SHOW MAZE");

//                            JOptionPane.showMessageDialog(SetupFrame.this,
//                                    "המבוך נטען בהצלחה מהשרת!\nלחץ על SHOW MAZE כדי לצפות בו.",
//                                    "הצלחה", JOptionPane.INFORMATION_MESSAGE);
                        });
                    } else {
                        showError("הקישור של קבלת המבוך כנראה לא תקין " + response.statusCode());
                        SwingUtilities.invokeLater(() -> {
                            getMazeButton.setText("GET MAZE");
                            getMazeButton.setEnabled(true);
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    showError("נכשל חיבור לשרת בעת קבלת המבוך. וודא שיש אינטרנט");
                    SwingUtilities.invokeLater(() -> {
                        getMazeButton.setText("GET MAZE");
                        getMazeButton.setEnabled(true);
                    });
                }
            }).start();
        });

        // ** נוסף: האזנה ללחיצה על כפתור SHOW MAZE לפתיחת החלון החדש **
        showMazeButton.addActionListener((event) -> {
            getMazeButton.setEnabled(false);
            showMazeButton.setEnabled(false);

            MazeFrame mazeFrame = new MazeFrame(loadedMazeImage,newWallColor,newGridColor,drawGrid,newPathColor,animationDelayMs,actualWidth,actualHeight);
            mazeFrame.setVisible(true);

            mazeFrame.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosed(java.awt.event.WindowEvent e) {
                    getMazeButton.setEnabled(true);
                    // 2. שחרור הכפתור בחזרה ברגע שהחלון נסגר לחלוטין
                    showMazeButton.setEnabled(true);
                }
            });
        });
    }

    private int validateDimension(String input) {
        try {
            int value = Integer.parseInt(input.trim());
            if (value >= 5 && value <= 100) {
                return value;
            }
        } catch (NumberFormatException e) {
        }
        return 30;
    }

    private void fetchRenderConfig() {
        new Thread(() -> {
            try {
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("https://backend-qcf9.onrender.com/fm1/get-render-config"))
                        .GET()
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    String jsonResponse = response.body();
                    JSONObject jsonObject = new JSONObject(jsonResponse);

                    wallCellColor = jsonObject.getString("wallCellColor");
                    pathColor = jsonObject.getString("pathColor");
                    drawGrid = jsonObject.getBoolean("drawGrid");
                    gridColor = jsonObject.getString("gridColor");
                    animationDelayMs = jsonObject.getInt("animationDelayMs");

                    newWallColor = Color.decode(wallCellColor);
                    newPathColor = Color.decode(pathColor);
                    newGridColor = Color.decode(gridColor);


                    SwingUtilities.invokeLater(() -> {
                        wallColorLabel.setText("צבע קירות: " + wallCellColor);
                        pathColorLabel.setText("צבע נתיב: " + pathColor);
                        drawGridLabel.setText("האם לצייר רשת: " + (drawGrid ? "כן" : "לא"));
                        gridColorLabel.setText("צבע רשת: " + gridColor);
                        delayLabel.setText("זמן המתנה באנימציה: " + animationDelayMs + " מילישניות");
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
                showError("נכשל החיבור לשרת הרשת. ודא שיש אינטרנט.");
            }
        }).start();
    }

    private void showError(String message) {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(SetupFrame.this, message, "שגיאת תקשורת", JOptionPane.ERROR_MESSAGE);
        });
    }

//    public vol
}