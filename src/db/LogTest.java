package db;

import model.MealLog;

import java.util.List;

public class LogTest {
    public static void main(String[] args) {
        DBManager db = new DBManager(); // DB 연결

        // 식사 기록 전체 조회
        List<MealLog> logs = db.getAllMealLog();

        // 결과 출력
        System.out.println("=== 식사 기록 목록 ===");
        for (MealLog log : logs) {
            System.out.println("ID: " + log.getId()
                    + ", 음식 ID: " + log.getFoodId()
                    + ", 날짜: " + log.getEatenDate()
                    + ", 종류: " + log.getMealType());
        }
    }
}
