import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBconnection {
    private static Connection connection;

    private static final String URL = AppProperties.getInstance().getValue("medicinekit.db.url");
    private static final String USERNAME = AppProperties.getInstance().getValue("medicinekit.db.username");
    private static final String PASSWORD = AppProperties.getInstance().getValue("medicinekit.db.password");

    public DBconnection(){
        try{
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            System.out.println("--Подключение к базе установлено");
            System.out.println("------");
        }
        catch (Exception e){
            System.out.println("--Подключения к базе не произошло");
        }
    }

    public static Connection getConnection() {
        return connection;
    }

    public void closeConnection(){
        try {
            connection.close();
        } catch (SQLException e) {
            System.out.println("--Проблема с закрытием потока");
        }
    }
}
