package db;

import model.Food;

import java.util.List;

public class DBTest {
    public static void main(String[] args) {
        DBManager db = new DBManager(); // DB 연결

        // 음식 전체 조회
        List<Food> foodList = db.getAllFoods();

        // 결과 출력
        System.out.println("=== 음식 목록 ===");
        for (Food food : foodList) {
            System.out.println("ID: " + food.getId()
                    + ", 이름: " + food.getName()
                    + ", 식당: " + food.getRestaurant()
                    + ", 카테고리: " + food.getCategory()
                    + ", 좋아요: " + food.getPreference().isLiked());
        }
    }
}
