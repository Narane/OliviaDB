package ece356;

import java.io.IOException;
import java.sql.*;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class UserDBAO {
    public static final String url = "jdbc:mysql://eceweb.uwaterloo.ca:3306/";
    //public static final String url = "jdbc:mysql://eceweb.uwaterloo.ca:3306/";
    public static final String user = "user_h86kim";
    public static final String pwd = "user_h86kim";
    public static final String schema = "ece356_22_2014";
    
    public static String getRole(String username)
        throws ClassNotFoundException, SQLException {
        Statement stmt;
        Connection con;
        Class.forName("com.mysql.jdbc.Driver");
        con = DriverManager.getConnection(url, user, pwd);
        stmt = con.createStatement();
        
        String query = "select role from " + schema +".User " +
                   "where Username = \"" + username + "\"";
        
        ResultSet rs = stmt.executeQuery(query);
        String returnval = "";
        while (rs.next()) {
            returnval = (String)rs.getString("role");
            con.close();
            return returnval;
        }
        return "error";
    }
    
    public static ResultSet executeQuery(String query) 
        throws ClassNotFoundException, SQLException {
        Statement stmt;
        Connection con;
        Class.forName("com.mysql.jdbc.Driver");
        con = DriverManager.getConnection(url, user, pwd);
        stmt = con.createStatement();
        
        return stmt.executeQuery(query); 
    }
   
    public static Boolean securityCheck(HttpServletRequest req, HttpServletResponse res)
        throws SQLException, ClassNotFoundException {
        Statement stmt;
        Connection con;
        Class.forName("com.mysql.jdbc.Driver");
        con = DriverManager.getConnection(url, user, pwd);
        stmt = con.createStatement();
        
        HttpSession session = req.getSession();
        String username = (String) session.getAttribute("username");
        if(username == null){
            username = req.getParameter("username");
        }
        String password = (String) session.getAttribute("password");
        if(password == null){
            password = req.getParameter("password");
        }
        
        String query = "select password from " + schema +".User " +
                   "where Username = \"" + username + "\"";
        
        ResultSet rs = stmt.executeQuery(query);
        while (rs.next()) {
            if(password.equals((String)rs.getString("password"))){
                con.close();
                return true;
            }
        }
        
        con.close();
        return false;
    }
}
