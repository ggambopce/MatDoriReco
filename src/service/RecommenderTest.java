package service;

import db.DBManager;
import model.Food;
import model.MealLog;

import java.util.List;

public class RecommenderTest {
    public static void main(String[] args) {

        // DB에서 음식 목록과 식사 기록 불러오기
        DBManager db = new DBManager();
        List<Food> foodList = db.getAllFoods();
        List<MealLog> logList = db.getAllMealLog();

        // 추천기 생성
        Recommender recommender = new Recommender();

        // 추천 로직 호출
        Food recommended = recommender.recommendByList(foodList);

        // 결과 출력
        if (recommended != null) {
            System.out.println("오늘의 추천 음식:");
            System.out.println("ID: " + recommended.getId());
            System.out.println("이름: " + recommended.getName());
            System.out.println("식당: " + recommended.getRestaurant());
            System.out.println("카테고리: " + recommended.getCategory());
        } else {
            System.out.println("추천할 음식이 없습니다.");
        }
    }
}
