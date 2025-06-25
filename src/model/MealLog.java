package model;

import java.time.LocalDate;

public class MealLog {
    // 추천 로직용 필드
    int id;
    int foodId;
    LocalDate eatenDate;
    String mealType;            // LUNCH or DINNER

    // 기본 생성자(JDBC)
    public MealLog() {}

    // 전체 필드를 매개변수로 받는 생성자
    public MealLog(int id, int foodId, LocalDate eatenDate, String mealType) {
        this.id = id;
        this.foodId = foodId;
        this.eatenDate = eatenDate;
        this.mealType = mealType;
    }

    // 자료구조 내 사용 getter,setter
    public int getId() {return id;}
    public void setId(int id) {this.id = id;}

    public int getFoodId() {return foodId;}
    public void setFoodId(int foodId) {this.foodId = foodId;}

    public LocalDate getEatenDate() {return eatenDate;}
    public void setEatenDate(LocalDate eatenDate) {this.eatenDate = eatenDate;}

    public String getMealType() {return mealType;}
    public void setMealType(String mealType) {this.mealType = mealType;}

    // 로그 디버깅
    @Override
    public String toString() {
        return "[MealLog] foodId=" + foodId + ", date=" + eatenDate + ", type=" + mealType;
    }

}


