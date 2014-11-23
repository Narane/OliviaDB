package ece356;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class UserDBAO {
    //public static final String url = "jdbc:mysql://localhost:3306/";
    public static final String url = "jdbc:mysql://eceweb.uwaterloo.ca:3306/";
    //public static final String user = "root";        
    public static final String user = "user_h86kim";
    //public static final String pwd = "root";
    public static final String pwd = "user_h86kim";
    public static final String schema = "ece356_22_2014";
    
    public static ResultSet getColumns(String table_name)
        throws ClassNotFoundException, SQLException {
            String query = "show columns from " +
                    table_name +
                    " in ece356_22_2014;";
            Connection con;
            Statement stmt;
            
            Class.forName("com.mysql.jdbc.Driver");
            
            con = DriverManager.getConnection(url, user, pwd);
            
            stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            
            //con.close();
            
            return rs;
    }
    
    public static ResultData getPatientInfo(String doctor_username,
            String f_name, String l_name, String p_num, String v_date, String username)
        throws ClassNotFoundException, SQLException {
            String query = "";
            
            query = "select 	* from ( 	select 		U.FirstName,         U.LastName,         P.PatientNumber,         V.* 	from 		ece356_22_2014.Patient as P 	inner join 		ece356_22_2014.Visits as V 	inner join 		ece356_22_2014.User as U 	on 		V.PatientUsername = P.PatientUsername 	and 		U.Username = P.PatientUsername ) as PV where 	PatientUsername 	in 	( 	select 		PatientUsername 	from 		ece356_22_2014.DoctorPatientAccess 	where 		DoctorUsername = \'" + username + "\' 	)";
            
            if (f_name != null && !f_name.trim().isEmpty())
                query += "and PV.FirstName" + " like" + "(\'" + f_name + "\') ";
            if (l_name != null && !l_name.trim().isEmpty())
                query += "and PV.LastName" + " like" + "(\'" + l_name + "\') ";
            if (p_num != null && !p_num.trim().isEmpty())
                query += "and PV.PatientNumber" + " like" + "(\'" + p_num + "\') ";
            if (v_date != null && !v_date.trim().isEmpty())
                query += "and PV.EndTime" + " like" + "(\'" + v_date +  "\') ";
            
            Connection con;
            Statement stmt;
            
            Class.forName("com.mysql.jdbc.Driver");
            
            con = DriverManager.getConnection(url, user, pwd);
            
            stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            ArrayList l = new ArrayList();
            ResultSetMetaData md = rs.getMetaData();
            int c_count = md.getColumnCount();
            
            for (int i = 1; i < c_count; i++) {
                l.add(md.getColumnLabel(i));
            }
            
            ResultData rd = new ResultData();
            rd.l = l;
            rd.rs = rs;

            //con.close();
            
            return rd;
    }
    
    public static ResultSet getPatientUsername(String doctor_username)
        throws ClassNotFoundException, SQLException {
            String query = "select PatientUsername from" +
                    "ece358_22_2014.DoctorPatientAccess" +
                    "where DoctorUsername like \'" +
                    doctor_username +
                    "\'";
            Connection con;
            Statement stmt;
            
            Class.forName("com.mysql.jdbc.Driver");
            
            con = DriverManager.getConnection(url, user, pwd);
            
            stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            
            con.close();
            
            return rs;
    }
    
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
        
        ResultSet rs = stmt.executeQuery(query); 
        //con.close();
        return rs;
    }
    
    public static int executeUpdate(String query)
        // use this when performing data manipulation queries i.e. UPDATE
        throws ClassNotFoundException, SQLException {
        Statement stmt;
        Connection con;
        Class.forName("com.mysql.jdbc.Driver");
        con = DriverManager.getConnection(url, user, pwd);
        stmt = con.createStatement();
        
        int rowCount =  stmt.executeUpdate(query); 
        con.close();
        return rowCount;
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
