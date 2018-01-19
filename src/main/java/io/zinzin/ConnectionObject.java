package io.zinzin;

import java.sql.Connection;
import java.sql.DriverManager;

public class ConnectionObject
{
    private static Connection conn = null;

    public static Connection getConnection()
    {
        String url = "jdbc:oracle:thin:@localhost:1521:orcl";
        try
        {
            Class.forName("oracle.jdbc.driver.OracleDriver").newInstance();
            conn = DriverManager.getConnection(url, "system", "123456");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return conn;
    }
}