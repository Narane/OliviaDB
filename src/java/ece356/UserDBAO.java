package ece356;

import java.sql.*;

public class UserDBAO {
    public static final String url = "jdbc:mysql://localhost:3306/";
    //public static final String url = "jdbc:mysql://eceweb.uwaterloo.ca:3306/";
    public static final String user = "user_h86kim";
    public static final String pwd = "user_h86kim";
    
    public static String getRole(String username)
        throws ClassNotFoundException, SQLException {
        Statement stmt;
        Connection con;
        Class.forName("com.mysql.jdbc.Driver");
        con = DriverManager.getConnection(url, user, pwd);
        stmt = con.createStatement();
        
        String query = "select role " +
                   "from ece356_22_2014.User " +
                   "where Username = \"" + username + "\"";
        
        ResultSet rs = stmt.executeQuery(query);
        String returnval = "";
        while (rs.next()) {
            returnval = (String)rs.getString("role");
            con.close();
            return returnval;
        }
        return "";
    }
   
}
