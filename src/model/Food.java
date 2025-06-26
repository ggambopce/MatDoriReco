package model;

public class Food {
    // 필드 생성
    int id;
    String name;
    String restaurant;
    Category category;
    Preference preference;

    // 기본 생성자 생성(JDBC 매핑)
    public Food() {}

    // db 조회용 생성자 생성
    public Food(int id, String name, String restaurant, Category category, Preference preference){
        this.id = id;
        this.name = name;
        this.restaurant = restaurant;
        this.category = category;
        this.preference = preference;
    }

    // 자료구조내 사용 getter,setter
    public int getId() {return id;}

    public void setId(int id) {this.id = id;}
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

    // 좋아요/싫어요 제어 메서드 추가
    public void like() {
        if (this.preference == null) {
            this.preference = new Preference(true);
        } else {
            this.preference.setLiked(true);
        }
    }

    public void dislike() {
        if (this.preference == null) {
            this.preference = new Preference(false);
        } else {
            this.preference.setLiked(false);
        }
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

        @Override
        public String toString() {
            return "Preference{liked=" + liked + "}";
        }


    }
}
