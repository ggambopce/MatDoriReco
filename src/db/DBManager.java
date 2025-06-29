package db;

import model.Food;
import model.MealLog;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DBManager {

    private Connection conn;

    // DB 연결 초기화 생성자
    public DBManager() {
        // H2 데이터베이스에 연결 (파일 기반)
        try {
            conn = DriverManager.getConnection("jdbc:h2:file:C:/Work/Java/MatDoriReco/matdori", "sa", "");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 음식 전체 조회 리스트 생성
    public List<Food> getAllFoods() {
        List<Food> list = new ArrayList<>();
        String sql = "SELECT * FROM food";
        Statement stmt = null;
        ResultSet rs = null;

        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);

            while (rs.next()) {
                Food food = new Food(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("restaurant"),
                        Food.Category.valueOf(rs.getString("category")),
                        new Food.Preference(rs.getBoolean("liked"))
                );
                list.add(food);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            // 자원 해제
            try {
                if (rs != null) rs.close(); // rs가 null이 아니면 닫기
                if (stmt != null) stmt.close(); // stmt가 null이 아니면 닫기
            } catch (SQLException e) {
                e.printStackTrace(); // 닫기 실패 시 예외 출력
            }
        }
        return list;
    }

    // 모든 식사 기록을 log테이블에서 조회
    public List<MealLog> getAllMealLog() {
        List<MealLog> logs = new ArrayList<>();
        String sql = "SELECT * FROM meal_log";
        Statement stmt = null;
        ResultSet rs = null;

        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);

            while (rs.next()) {
                int id = rs.getInt("id");
                int foodId = rs.getInt("food_id");
                LocalDate eatenDate = rs.getDate("eaten_date").toLocalDate();
                String mealType = rs.getString("meal_type");

                MealLog log = new MealLog(id, foodId, eatenDate, mealType);
                    logs.add(log); // 리스트에 추가
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            // 자원 해제
            try {
                if (rs != null) rs.close(); // rs가 null이 아니면 닫기
                if (stmt != null) stmt.close(); // stmt가 null이 아니면 닫기
            } catch (SQLException e) {
                e.printStackTrace(); // 닫기 실패 시 예외 출력
            }
        }
        return logs;
    }

    // 오늘 먹었어요 식사 기록 저장 메서드
    public void saveMealLog(MealLog log) {
        String insertSql = "INSERT INTO meal_log (food_id, eaten_date, meal_type) VALUES (?, ?, ?)";
        PreparedStatement pstmt = null;

        try {
            pstmt = conn.prepareStatement(insertSql);
            pstmt.setInt(1, log.getFoodId());
            pstmt.setDate(2, Date.valueOf(log.getEatenDate()));
            pstmt.setString(3, log.getMealType());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            // 중복 삽입 등 예외 무시하거나 로깅
            if (e.getErrorCode() == 23505) {  // H2의 Unique 제약 위반
                System.out.println("이미 등록된 식사 기록입니다.");
            } else {
                e.printStackTrace();
            }
        } finally {
            try {
                if (pstmt != null) pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }




}
