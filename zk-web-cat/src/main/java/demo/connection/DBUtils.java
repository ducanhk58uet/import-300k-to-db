package demo.connection;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * Created by GEMVN on 2/2/2018.
 */
public class DBUtils
{
    private static Connection conn = null;

    public static Connection getConnection(){
        String url = "jdbc:oracle:thin:@//localhost:1521/orcl";
        try{
            Class.forName("oracle.jdbc.driver.OracleDriver").newInstance();
            conn = DriverManager.getConnection(url,"system","123456");
        }catch(Exception e){
            e.printStackTrace();
        }
        return conn;
    }
}
