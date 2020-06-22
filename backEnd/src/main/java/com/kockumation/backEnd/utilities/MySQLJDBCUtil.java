package com.kockumation.backEnd.utilities;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class MySQLJDBCUtil {

    /**
     * Get database connection
     *
     * @return a Connection object
     * @throws SQLException
     */

    public static Connection getConnection() throws SQLException {
        Connection conn = null;
    // Configure mysql timezone to work with UTC time zone
    // SET GLOBAL time_zone = '+00:00';
        try  {
            Properties props = new Properties();
            //load a properties file from class path, inside static method
            props.load(MySQLJDBCUtil.class.getClassLoader().getResourceAsStream("application.properties"));
            // assign db parameters
            String url = props.getProperty("url");
            String user = props.getProperty("user");
            String password = props.getProperty("password");

            // create a connection to the database
            conn = DriverManager.getConnection(url, user, password);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }








        /*  try {
            // db parameters
            String url       = pros.getProperty("url");;
            String user      = "root";
            String password  = "secret";

            // create a connection to the database
            conn = DriverManager.getConnection(url, user, password);
            // more processing here
            // ...
        } catch(SQLException e) {
            System.out.println(e.getMessage());
        }

        return conn;*/



}
