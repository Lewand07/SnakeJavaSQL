/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pkg1stgame;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author lewan
 */
public class Main {

    private static Connection conn; 
    static int userID;
    
    public static void main(String[] args) {
        connection();
    
        LoginWindow lwindow = new LoginWindow(getConn());
        lwindow.setVisible(true);
        
    }
    
    public static void connection(){
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:base.db");
            DatabaseMetaData meta= getConn().getMetaData();  
            Statement st = getConn().createStatement();
            String sql;
            sql="CREATE TABLE IF NOT EXISTS user(id integer PRIMARY KEY,login VARCHAR(60),password VARCHAR(60),type tinyint, UNIQUE(login));";
            st.execute(sql);
            sql="INSERT OR IGNORE INTO user(login,password,type) VALUES ('admin', 'admin', 1);";
            st.execute(sql);
            sql="CREATE TABLE IF NOT EXISTS scoreboard(scoreid integer PRIMARY KEY, login VARCHAR(60), score INTEGER, date DATE); ";
            st.execute(sql);
        } catch (SQLException | ClassNotFoundException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void tables(){
    }

    /**
     * @return the conn
     */
    public static Connection getConn() {
        return conn;
    }
    
    
}
