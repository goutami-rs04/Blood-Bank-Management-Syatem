package bloodbanksytem;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnecton {

  public static Connection getConnection() 
                           throws SQLException {
    String url  = "jdbc:mysql://10.41.191.143/bloodbank";
    String user = "root";
    String pass = "A#Hegde123";
    return DriverManager.getConnection(url, user, pass);
  }
}