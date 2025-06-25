package model;

public class Food {
    // 필드 생성
    String name;
    String restaurant;
    Category category;
    Preference preference;

    // 자료구조내 사용 getter,setter
    public String getName() {return name;}
    public void setName(String name) {this.name = name;}

    public String getRestaurant() {return restaurant;}
    public void setRestaurant(String restaurant) {this.restaurant = restaurant;}

    public Category getCategory() {return category;}
    public void setCategory(Category category) {this.category = category;}

    public Preference getPreference() {return preference;}
    public void setPreference(Preference preference) {this.preference = preference;}

    // 로그 디버깅
    @Override
    public String toString() {return "Food{" + "name='" + name + '\'' + ", restaurant='" + restaurant + '\'' + ", category=" + category + ", preference=" + preference + '}';}

    // 기본 생성자 생성(JDBC 매핑)
    public Food() {}

    // 생성자 생성
    public Food(String name, String restaurant, Category category, Preference preference){
        this.name = name;
        this.restaurant = restaurant;
        this.category = category;
        this.preference = preference;
    }

    public enum Category { KOREAN, JAPANESE, CHINESE, WESTERN, SNACK }

    // 선호 추가를 위한 중첩클래스
    public static class  Preference {
        boolean liked;

        public Preference(boolean liked) {
            this.liked = liked;
        }

        public boolean isLiked() {return liked;}
        public void setLiked(boolean liked) {this.liked = liked;}

    }
}
