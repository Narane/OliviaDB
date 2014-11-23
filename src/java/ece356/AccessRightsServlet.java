/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ece356;

import static ece356.UserDBAO.pwd;
import static ece356.UserDBAO.schema;
import static ece356.UserDBAO.url;
import static ece356.UserDBAO.user;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author bleskows
 */
public class AccessRightsServlet extends SecureHTTPServlet {
    private String dbName = UserDBAO.schema + ".DoctorPatientAccess";
    
    
    public String pageTitle(){
        return "Grant patient access rights";
    }
    
    @Override
    public void innerFunction(
            HttpServletRequest req, 
            HttpServletResponse res, 
            ServletOutputStream out)
        throws ServletException,  IOException {
        
        HttpSession session = req.getSession();
        String username = (String) session.getAttribute("username");
        String role = "SOMETHING BROKE PLEASE FIX";
        String resultString = "";
        
        try{
            role = UserDBAO.getRole(username);
            if(!(role.equals("doctor") || role.equals("superuser"))){
                session.invalidate();
                res.sendRedirect("LoginServlet");
            }
            
            //Work on the request
            resultString = grantAccess(username, req);
        } catch (Exception e){
            req.setAttribute("exception", e);
            // Set the name of jsp to be displayed if connection fails
            String url = "/error.jsp";
            getServletContext().getRequestDispatcher(url).forward(req, res);
        }
        
        out.println("<p>Update</p>");
        out.println(
            "<form method=\"post\">" +
            "Remove access: <input type=\"checkbox\" name=\"deletemode\" value=\"deletemode\"><br>" +
            "  Doctor username: <input type=\"text\" value=\"\" SIZE=30 name=\"doctorname\"><br>" +
            "  Patient username: <input type=\"text\" value=\"\" SIZE=30 name=\"patientname\"><br>" +
            resultString +
            "<input type=\"submit\" value=\"Grant access\"></form>");
        
        try{
            QueryResult que = UserDBAO.executeQuery("SELECT DoctorUsername AS \"Doctor\", PatientUsername AS \"Patient\" FROM " + dbName +
                    " AS acc WHERE EXISTS (SELECT * FROM " + schema + ".Patient AS pa" +
                    " WHERE pa.DoctorUsername = \"" + username + 
                    "\" and acc.PAtientUsername = pa.PatientUsername);");
           out.println("<br>You have given these acceses to other doctors:<br>" + UserDBAO.generateTable(que));         
            
            que = UserDBAO.executeQuery("SELECT PatientUsername AS \"Patients\" FROM " + dbName + 
                    " WHERE DoctorUsername = \"" + username + "\"");
            out.println("<br>Other doctors have granted you granted access to the following patients:<br>" + UserDBAO.generateTable(que));
        }
        catch (Exception e) {
            req.setAttribute("exception", e);
            // Set the name of jsp to be displayed if connection fails
            String url = "/error.jsp";
            getServletContext().getRequestDispatcher(url).forward(req, res);
        }
    }

    private String grantAccess(String username, HttpServletRequest req) 
            throws ClassNotFoundException, SQLException{
        
            String doctorName = req.getParameter("doctorname");
            String patientName = req.getParameter("patientname");
        
            if (doctorName == null) {
                // NOT SUBMITTED?
                return "";
            }
            
            doctorName = doctorName.trim();
            patientName = patientName.trim();
            
            if (doctorName.equals("") || patientName.equals("")) {
                // NEED BOTH FIELDS
                return "<p><font color=\"red\">Both inputs are required</font></p>";
            }
            
            //Manipulate DB
            Statement stmt;
            Connection con;
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection(url, user, pwd);
            stmt = con.createStatement();
            String query = "";
            String compareRole = "";
            
            StringBuilder sb = new StringBuilder(128);
            //First check that the doctor exists in the DB
            compareRole = "doctor";
            query = "SELECT role FROM " + schema +".User " +
                   "WHERE Username = \"" + doctorName + "\"";
            ResultSet rs = stmt.executeQuery(query);
            if (!(rs.next() && compareRole.equals((String)rs.getString("role")))) {
                //Input doctor isn't actually a doctor... or user doesn't exist
                sb.append("Invalid doctor username");
            }
            
            //Now check for the patient in the DB
            compareRole = "patient";
            query = "SELECT role FROM " + schema +".User " +
                   "WHERE Username = \"" + patientName + "\"";
            rs = stmt.executeQuery(query);
            if (!(rs.next() && compareRole.equals((String)rs.getString("role")))) {
                //Input doctor isn't actually a doctor... or user doesn't exist
                if(sb.length() > 0){
                    sb.append("<br>");
                }
                sb.append("Invalid patient username");
            }
            
            if(sb.length() > 0){
                return("<p><font color=\"red\">" + sb.toString() + "</font></p>");
            }
            
            //Check that the doctor actually has the rights to the patient
            query = "SELECT * FROM " + schema + ".Patient " +
                    "WHERE PatientUsername = \"" + patientName + "\"" +
                    "AND DoctorUsername = \"" + username + "\"";
            rs = stmt.executeQuery(query);
            if (!rs.next()) {
                return("<p><font color=\"red\">User " + username + " has no access to patient " + patientName + "</font></p>");
            }
            
            //Also check that the entry isn't duplicate in the access chart
            if(req.getParameter("deletemode") != null){
                query = "DELETE FROM " + dbName +
                        " WHERE PatientUsername = \"" + patientName + "\"" +
                        "AND DoctorUsername = \"" + doctorName + "\"";
                int ret = stmt.executeUpdate(query);
                if(ret > 0){
                    return("<p><font color=\"green\">Deletion successful</font></p>");
                } else {
                    return("<p><font color=\"red\">Deletion failed; no entry found</font></p>");
                }
            }
            else{
                query = "SELECT * FROM " + dbName +
                        " WHERE PatientUsername = \"" + patientName + "\"" +
                        "AND DoctorUsername = \"" + doctorName + "\"";
                rs = stmt.executeQuery(query);
                if (rs.next()) {
                    return("<p><font color=\"red\">Duplicate entry found</font></p>");
                }
            }


            
            //Do the insert now that it's confirmed to be safe
            query = "INSERT INTO " + dbName + "(PatientUsername, DoctorUsername) " +
                    "VALUES('" + patientName + "', '" + doctorName + "');";
            stmt.executeUpdate(query);
            con.close();
            
            return "<p><font color=\"green\">Update successful</font></p>";
    }
    
}
