package ece356;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class UserDBAO {
    public static final String url = "jdbc:mysql://localhost:3306/";
    //public static final String url = "jdbc:mysql://eceweb.uwaterloo.ca:3306/";
    public static final String user = "root";        
    //public static final String user = "user_kmyin";
    public static final String pwd = "root";
    //public static final String pwd = "user_kmyin";
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
            String f_name, String l_name, String p_num, String v_date,
            String diagnosis, String comment, String prescription,
            String surgery, String username)
        throws ClassNotFoundException, SQLException {
            String query = "";
            query = "select  * from (  select   U.FirstName, "
                    + "U.LastName, P.PatientNumber, V.PatientUsername, "
                    + "V. StartTime, V. ProcedureName, V. DoctorUsername, "
                    + "V. EndTime, V. CurrentStatus, V. PrescriptionStart, "
                    + "V. PrescriptionEnd, V. Diagnosis, V. Prescription, "
                    + "V. Comments, V. ProcedureTime from "
                    + "ece356_22_2014.Patient as P  inner join "
                    + "ece356_22_2014.Visits as V  inner join "
                    + "ece356_22_2014.User as U  on   V.PatientUsername "
                    + "= P.PatientUsername  and  U.Username = P.PatientUsername "
                    + ") as PV where  PatientUsername  in  (  select  "
                    + "PatientUsername  from   ece356_22_2014.Patient "
                    + "where   DoctorUsername = 'kmyin' )";
            
            if (f_name != null && !f_name.trim().isEmpty())
                query += "and PV.FirstName" + " like" + "(\'%" + f_name + "%\') ";
            if (l_name != null && !l_name.trim().isEmpty())
                query += "and PV.LastName" + " like" + "(\'%" + l_name + "%\') ";
            if (p_num != null && !p_num.trim().isEmpty())
                query += "and PV.PatientNumber" + " like" + "(\'%" + p_num + "%\') ";
            if (v_date != null && !v_date.trim().isEmpty())
                query += "and DATE_FORMAT(PV.EndTime, \'%Y-%m-%d\')" + " =" + "(\'" + v_date +  "\') ";
            if (diagnosis != null && !diagnosis.trim().isEmpty())
                query += "and PV.Diagnosis" + " like" + "(\'%" + diagnosis +  "%\') ";
            if (comment != null && !comment.trim().isEmpty())
                query += "and PV.Comments" + " like" + "(\'%" + comment +  "%\') ";
            if (prescription != null && !prescription.trim().isEmpty())
                query += "and PV.Prescription" + " like" + "(\'%" + prescription +  "%\') ";
            if (surgery != null && !surgery.trim().isEmpty())
                query += "and PV.ProcedureName" + " like" + "(\'%" + surgery +  "%\') ";
            
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
            String query = "select PatientUsername from " +
                    schema + ".DoctorPatientAccess " +
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
    
    public static QueryResult getAppointments(String patient_username)
        throws ClassNotFoundException, SQLException {
            String query = "select DoctorUsername, StartTime from " +
                    schema + ".appointment " +
                    "where PatientUsername = \"" +
                    patient_username +
                    "\"";
                       
            Connection con;
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection(url, user, pwd);
            
            Statement stmt;
            stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            
            QueryResult qResult = new QueryResult(rs);
            
            rs.close();
            stmt.close();
            con.close();
            
            return qResult;
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
        
    public static QueryResult executeQuery(String query) 
        throws ClassNotFoundException, SQLException {
        Statement stmt;
        Connection con;
        Class.forName("com.mysql.jdbc.Driver");
        con = DriverManager.getConnection(url, user, pwd);
        stmt = con.createStatement();
        
        ResultSet rs = stmt.executeQuery(query); 
        QueryResult qResult = new QueryResult(rs);
        
        rs.close();
        stmt.close();
        con.close();
        
        return qResult;
    }
    
    public static int executeUpdate(String query)
        // use this when performing data manipulation queries i.e. UPDATE
        throws ClassNotFoundException, SQLException {
        Statement stmt;
        Connection con;
        Class.forName("com.mysql.jdbc.Driver");
        con = DriverManager.getConnection(url, user, pwd);
        stmt = con.createStatement();
        
        int rowCount = 0; 
        try {
            rowCount =  stmt.executeUpdate(query); 
        }
        catch (SQLException e) {
            if (e.getMessage().contains("Duplicate entry")) {
                rowCount = 0;
            }
            else {
                throw e;
            }
        }
        
        stmt.close();
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
    
    public static String generateTable(QueryResult que){
        if(que.getResultSet().size() <= 0){
            return "";
        }
        
        StringBuilder sb = new StringBuilder(256);
        
        QueryResult.QueryRow row = null;
        ArrayList header;
        ArrayList r_data;
        
        sb.append("<table border=1><tr>");
        
        ArrayList result = que.getResultSet();
        row = (QueryResult.QueryRow) result.get(0);
        header = row.getHeader();
        int c_count = header.size();
        int r_count = result.size();

        //print headers
        for (int j = 0; j < c_count; j++) {
            sb.append("<th>");
            sb.append((String) header.get(j));
        }
        sb.append("</tr>");
        
        for (int i = 0; i < r_count; i++) {
                row = (QueryResult.QueryRow) result.get(i);
                r_data = row.getRow();
                sb.append("<tr>");
                for (int j = 0; j < c_count; j++) {

                    sb.append("<td>");
                    sb.append((String) r_data.get(j));
                    
                }
                sb.append("</tr>");
            }
            
            sb.append("</table>");
        
        return sb.toString();
    }
}
