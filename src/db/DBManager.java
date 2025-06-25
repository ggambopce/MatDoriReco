package db;

import model.Food;

import java.sql.*;
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
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return list;
    }



}
