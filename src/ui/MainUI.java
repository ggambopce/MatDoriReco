package ui;

import db.DBManager;
import model.Food;
import model.MealLog;
import service.Recommender;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class MainUI extends JFrame {

    private JComboBox<String> mealTypeCombo;
    private JButton recommendButton, likeButton, dislikeButton, eatenButton;
    private JLabel resultLabel;
    private JTextArea logArea;

    private DBManager dbManager;
    private Recommender recommender;

    public MainUI() {
        Font emojiFont = new Font("Noto Color Emoji", Font.PLAIN, 14);
        UIManager.put("Button.font", emojiFont);
        UIManager.put("Label.font", emojiFont);

        dbManager = new DBManager();
        recommender = new Recommender();

        setTitle("\uD83C\uDF7D DJU Matdori");
        setSize(500, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // 화면 중앙 정렬

        // 전체 레이아웃: BoxLayout (Y축)
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        // ===== 헤더 패넣 =====
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        headerPanel.setBackground(new Color(240, 240, 240));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        JLabel titleLabel = new JLabel("\uD83C\uDF71 대전대 맛도리 추천기");
        titleLabel.setFont(new Font("Noto Color Emoji", Font.PLAIN, 20));
        headerPanel.add(titleLabel);
        add(headerPanel);

        // ===== 콤보박스 + 추천 버튼 (가로) =====
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        mealTypeCombo = new JComboBox<>(new String[]{"점심", "저녁"});
        recommendButton = new JButton("추천받기");
        topPanel.add(mealTypeCombo);
        topPanel.add(recommendButton);
        add(topPanel);

        // ===== 추천 결과 라벨 =====
        resultLabel = new JLabel("오늘의 추천: ");
        resultLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 16));
        resultLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(resultLabel);

        add(Box.createVerticalStrut(10)); // 간격

        // ===== 버튼 3개 (좋아요/싫어요/먹었어요) =====
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        likeButton = new JButton("👍 좋아요");
        dislikeButton = new JButton("👎 싫어요");
        eatenButton = new JButton("🍴 오늘 먹었어요");
        buttonPanel.add(likeButton);
        buttonPanel.add(dislikeButton);
        buttonPanel.add(eatenButton);
        add(buttonPanel);

        add(Box.createVerticalStrut(10)); // 간격

        // ===== 최근 추천 로그 =====
        logArea = new JTextArea(5, 40);
        logArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(logArea);
        add(scrollPane);

        // ===== 추천받기 동작 구현 =====
        recommendButton.addActionListener((ActionEvent e) -> {
            List<Food> foods = dbManager.getAllFoods();
            List<MealLog> logs = dbManager.getAllMealLog();
            Food recommendation = recommender.recommendBaseLikedAnd3Day(foods, logs);

            if (recommendation != null) {
                resultLabel.setText("오늘의 추천: " + recommendation.getName()
                        + " (" + recommendation.getRestaurant() + ")");
                logArea.append("추천: " + recommendation.getName()
                        + " (" + recommendation.getCategory() + ") by "
                        + recommendation.getRestaurant() + "\n");
            } else {
                resultLabel.setText("추천할 음식이 없습니다.");
            }
        });

        setVisible(true);
    }

    public static void main(String[] args) {
        new MainUI();
    }
}
