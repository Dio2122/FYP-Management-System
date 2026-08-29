package fyp;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    
    private static final String URL = "jdbc:sqlserver://brandon:1433;instanceName=brand;databaseName=fyp_management;integratedSecurity=true;encrypt=true;trustServerCertificate=true;";

    private DBConnection() {}

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            return DriverManager.getConnection(URL);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new SQLException("SQL Server Driver not found.");
        }
    }
}