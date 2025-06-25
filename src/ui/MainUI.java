package ui;

import db.DBManager;
import model.Food;
import model.MealLog;
import service.Recommender;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class MainUI extends JFrame {

    private JComboBox<String> mealTypeCombo;
    private JButton recommendButton;
    private JLabel resultLabel;

    private DBManager dbManager;
    private Recommender recommender;

    public MainUI() {
        // DB 및 추천기 초기화
        dbManager = new DBManager();
        recommender = new Recommender();

        setTitle("🍱 나만의 점심/저녁 추천기");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout());

        // 콤보박스: 점심 / 저녁 선택
        mealTypeCombo = new JComboBox<>(new String[]{"점심", "저녁"});
        add(mealTypeCombo);

        // 추천받기 버튼
        recommendButton = new JButton("추천받기");
        add(recommendButton);

        // 추천 결과를 표시할 라벨
        resultLabel = new JLabel("오늘의 추천: ");
        add(resultLabel);

        // 버튼 클릭 이벤트 처리
        recommendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                List<Food> allFoods = dbManager.getAllFoods();
                List<MealLog> allLogs = dbManager.getAllMealLog();

                Food recommendation = recommender.recommendBaseLikedAnd3Day(allFoods, allLogs);

                if (recommendation != null) {
                    resultLabel.setText("오늘의 추천: " + recommendation.getName());
                } else {
                    resultLabel.setText("추천할 음식이 없습니다.");
                }
            }
        });

        setVisible(true);
    }

    public static void main(String[] args) {
        new MainUI();
    }
}
