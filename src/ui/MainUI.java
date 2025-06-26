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

    private Food currentRecommendedFood;  // 현재 추천된 음식
    private JComboBox<String> strategyComboBox;
    private JLabel strategyDescriptionLabel;


    public void appendLog(String message) {
        logArea.append(message + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }
    // 생성자 주입 생성자
    public MainUI(DBManager dbManager, Recommender recommender) {
        this.dbManager = dbManager;           // 주입받은 인스턴스 저장
        this.recommender = recommender;       // 주입받은 인스턴스 저장

        Font emojiFont = new Font("Noto Color Emoji", Font.PLAIN, 14);
        UIManager.put("Button.font", emojiFont);
        UIManager.put("Label.font", emojiFont);

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
        strategyComboBox = new JComboBox<>(new String[]{"LIST", "SET", "MAP", "QUEUE", "STACK"});
        recommendButton = new JButton("추천받기");
        topPanel.add(mealTypeCombo);
        topPanel.add(strategyComboBox);
        topPanel.add(recommendButton);
        add(topPanel);

        // ===== 추천 결과 라벨 =====
        resultLabel = new JLabel("오늘의 추천: ");
        resultLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 16));
        resultLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(resultLabel);

        // ===== 전략 설명 라벨 =====
        strategyDescriptionLabel = new JLabel("추천 전략을 선택해주세요.");
        strategyDescriptionLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 13));
        strategyDescriptionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);  // BoxLayout 정렬
        strategyDescriptionLabel.setHorizontalAlignment(SwingConstants.CENTER);  // 텍스트 정렬
        add(strategyDescriptionLabel);

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
            String strategy = (String) strategyComboBox.getSelectedItem();  // 전략 선택

            Food recommendation = null;

            switch (strategy) {
                case "LIST":
                    recommendation = recommender.recommendBaseLikedAnd3Day(foods, logs);
                    break;
                case "SET":
                    recommendation = recommender.recommendBySet(foods);
                    break;
                case "MAP":
                    recommendation = recommender.recommendByMap(foods);
                    break;
                case "QUEUE":
                    recommendation = recommender.recommendByQueue(foods, logs);
                    break;
                case "STACK":
                    recommendation = recommender.recommendByStack(foods, logs);
                    break;
            }

            if (recommendation != null) {
                currentRecommendedFood = recommendation;
                updateResultLabel();
                logArea.append("추천(" + strategy + "): " + recommendation.getName()
                        + " (" + recommendation.getCategory() + ") from "
                        + recommendation.getRestaurant() + "\n");
            } else {
                resultLabel.setText("추천할 음식이 없습니다.");
            }
        });

        // ===== 좋아요 싫어요 동작 구현 =====
        likeButton.addActionListener(e -> {
            if (currentRecommendedFood != null) {
                currentRecommendedFood.like(); // 모델 메서드 호출
                updateResultLabel();
                logArea.append("👍 좋아요 선택됨: " + currentRecommendedFood.getName() + "\n");
            } else {
                logArea.append("추천된 음식이 없습니다.\n");
            }
        });

        dislikeButton.addActionListener(e -> {
            if (currentRecommendedFood != null) {
                currentRecommendedFood.dislike();
                updateResultLabel();
                logArea.append("👎 싫어요 선택됨: " + currentRecommendedFood.getName() + "\n");
            } else {
                logArea.append("추천된 음식이 없습니다.\n");
            }
        });

        // ===== 오늘 먹었어요 동작 구현 =====
        eatenButton.addActionListener(e -> {
            if (currentRecommendedFood == null) {
                logArea.append("추천된 음식이 없습니다.\n");
                return;
            }

            // 1. 콤보박스에서 현재 선택된 식사 유형 (점심/저녁)
            String selected = (String) mealTypeCombo.getSelectedItem();
            String mealType = selected.equals("점심") ? "LUNCH" : "DINNER";

            // 2. MealLog 객체 생성
            MealLog log = new MealLog(
                    0,                                // ID는 auto_increment
                    currentRecommendedFood.getId(),  // 음식 ID
                    java.time.LocalDate.now(),       // 오늘 날짜
                    mealType                         // 식사 유형
            );

            // 3. DB에 저장
            dbManager.saveMealLog(log);

            // 4. 로그 표시
            logArea.append("🍴 오늘 먹은 것으로 기록됨: " + currentRecommendedFood.getName() + " (" + mealType + ")\n");
        });

        strategyComboBox.addActionListener(e -> {
            String selected = (String) strategyComboBox.getSelectedItem();
            String description;

            switch (selected) {
                case "LIST":
                    description = "<html><div style='text-align:center;'><b>LIST :</b> 순차적 저장, 인덱스 접근<br>최근 2일간 먹은 음식 제외</div></html>";
                    break;
                case "SET":
                    description = "<html><div style='text-align:center;'><b>SET :</b> 순서없음, 중복 제거<br>싫어요 표시한 음식 제외, 중복 회피</div></html>";
                    break;
                case "MAP":
                    description = "<html><div style='text-align:center;'><b>MAP :</b> 키-값 쌍, Key는 중복 불가<br>편식 방지, 싫어요가 많은 카테고리 음식 추천</div></html>";
                    break;
                case "QUEUE":
                    description = "<html><div style='text-align:center;'><b>QUEUE :</b> 선입선출 (FIFO)<br>적게 먹은 음식 우선 추천</div></html>";
                    break;
                case "STACK":
                    description = "<html><div style='text-align:center;'><b>STACK :</b> 후입선출 (LIFO)<br>최근 먹은 음식 제외, 다른 카테고리 추천</div></html>";
                    break;
                default:
                    description = "";
            }



            strategyDescriptionLabel.setText(description);
        });


        setVisible(true);
    }

    // 추천 결과 라벨에 👍👎 포함해서 업데이트
    private void updateResultLabel() {
        if (currentRecommendedFood == null) return;

        String icon = "";
        if (currentRecommendedFood.getPreference() != null) {
            icon = currentRecommendedFood.getPreference().isLiked() ? " 👍" : " 👎";
        }

        resultLabel.setFont(new Font("Noto Color Emoji", Font.PLAIN, 16));
        resultLabel.setText("오늘의 추천: " + currentRecommendedFood.getName()
                + " (" + currentRecommendedFood.getRestaurant() + ")" + icon);
    }
}
