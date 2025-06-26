package util;

import db.DBManager;
import model.Food;
import model.MealLog;
import service.Recommender;
import ui.MainUI;

import java.time.LocalTime;
import java.util.List;

// Scheduler 클래스
public class Scheduler extends Thread {

    private Recommender recommender;
    private DBManager dbManager;
    private MainUI mainUI;  // UI에 접근하기 위한 필드

    public Scheduler(Recommender recommender, DBManager dbManager, MainUI mainUI) {
        this.recommender = recommender;
        this.dbManager = dbManager;
        this.mainUI = mainUI;
    }

    @Override
    public void run() {
        while (true) {
            LocalTime now = LocalTime.now();
            if (now.getHour() == 11 || now.getHour() == 17) {
                List<Food> foods = dbManager.getAllFoods();
                List<MealLog> logs = dbManager.getAllMealLog();
                Food recommendation = recommender.recommendBaseLikedAnd3Day(foods, logs);

                if (recommendation != null) {
                    mainUI.appendLog("⏰ 스케쥴 추천: " + recommendation.getName()
                            + " (" + recommendation.getCategory() + ") from "
                            + recommendation.getRestaurant());
                } else {
                    mainUI.appendLog("⏰ 스케쥴 추천할 음식이 없습니다.");
                }

                try {
                    Thread.sleep(60 * 60 * 1000); // 1시간 대기
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            try {
                Thread.sleep(30 * 1000); // 30초마다 시간 체크
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

