package init;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * 개발용 H2 초기화 클래스
 * - 최초 한 번 실행하여 테이블을 생성
 * - 필요 시 Main에서 호출해서 실행
 */
public class H2InitConfig {

    public static void initialize() {
        Connection conn = null;

        try {
            // 1. JDBC URL 설정
            String url = "jdbc:h2:./matdori";  // 파일 기반 H2 DB
            String user = "sa";
            String password = "";

            // 2. 연결 시도
            conn = DriverManager.getConnection(url, user, password);
            System.out.println("H2 데이터베이스 연결 성공!");

            // 3. 테이블 생성 SQL
            String sql = """
                    CREATE TABLE IF NOT EXISTS meal_log (
                        id IDENTITY PRIMARY KEY,
                        food_name VARCHAR(100),
                        category VARCHAR(50),
                        date DATE,
                        meal_type VARCHAR(10)
                    );
                    """;

            Statement stmt = conn.createStatement();
            stmt.execute(sql);
            System.out.println("테이블 생성 완료");

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) conn.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
}
