/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ece356;

import static ece356.UserDBAO.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author bleskows
 */
public class PatientAssignmentServlet extends SecureHTTPServlet {

    private String dbName = UserDBAO.schema + ".DoctorPatientAccess";
    
    
    public String pageTitle(){
        return "Assign patients to doctors";
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
            if(!(role.equals("staff") || role.equals("superuser"))){
                session.invalidate();
                res.sendRedirect("LoginServlet");
            }
            
            //Work on the request
            resultString = assignDoctor(req);
        } catch (Exception e){
            req.setAttribute("exception", e);
            // Set the name of jsp to be displayed if connection fails
            String url = "/error.jsp";
            getServletContext().getRequestDispatcher(url).forward(req, res);
        }
        
        String doctorName = req.getParameter("doctorname");
        String patientName = req.getParameter("patientname");
        if(doctorName == null){doctorName = "";}
        if(patientName == null){patientName = "";}
        
        out.println("<p>Enter patient ID and doctor ID to search for assignments or assign the patient to the doctor.</p>");
        out.println(
            "<form method=\"post\">" +
            "  Doctor username: <input type=\"text\" value=\"" + doctorName + "\" SIZE=30 name=\"doctorname\"><br>" +
            "  Patient username: <input type=\"text\" value=\"" + patientName + "\" SIZE=30 name=\"patientname\"><br>" +
            resultString +
            "<input type=\"submit\" value=\"Search\" name=\"submitAction\"> <input type=\"submit\" value=\"Assign\" name=\"submitAction\"></form>");
        
        try{         
            String submitAction= req.getParameter("submitAction");
            Boolean searching = (submitAction != null && submitAction.equals("Search"));
            Boolean assigning = (submitAction != null && submitAction.equals("Assign"));
            if((assigning || searching) && (doctorName != null || patientName != null)){
                StringBuilder querySB = new StringBuilder(128);
                querySB.append("SELECT DoctorUsername AS \"Doctor\", PatientUsername AS \"Assigned Patient\" FROM " + schema + ".Patient ");

                if(!patientName.equals("") && !doctorName.equals("")){
                    //Search with both criteria
                    querySB.append("WHERE PatientUsername = \"" + patientName + "\" AND DoctorUsername = \"" + doctorName + "\";");
                } else if(!patientName.equals("")){
                    //Search with just patient name
                    querySB.append("WHERE PatientUsername = \"" + patientName + "\";");
                } else{
                    //Search with just doctor name
                    querySB.append("WHERE DoctorUsername = \"" + doctorName + "\";");
                }

                QueryResult que = UserDBAO.executeQuery(querySB.toString());
                out.println("<br>Search results:<br>" + UserDBAO.generateTable(que));
            }
        }
        catch (Exception e) {
            req.setAttribute("exception", e);
            // Set the name of jsp to be displayed if connection fails
            String url = "/error.jsp";
            getServletContext().getRequestDispatcher(url).forward(req, res);
        }
    }

    private String assignDoctor(HttpServletRequest req) 
            throws ClassNotFoundException, SQLException{
        
            String submitAction= req.getParameter("submitAction");
            String doctorName = req.getParameter("doctorname");
            String patientName = req.getParameter("patientname");
            Boolean searching = (submitAction != null && submitAction.equals("Search"));
            Boolean assigning = (submitAction != null && submitAction.equals("Assign"));
        
            if (doctorName == null || (!searching && !assigning)) {
                // NOT SUBMITTED ENTIRELY
                return "";
            }
            
            doctorName = doctorName.trim();
            patientName = patientName.trim();
            
            if(assigning){
               if (doctorName.equals("") || patientName.equals("")) {
                    // NEED BOTH FIELDS
                    return "<p><font color=\"red\">Both inputs are required for assignment</font></p>";
               }
            }
            else if(searching){
               if (doctorName.equals("") && patientName.equals("")) {
                    // NEED BOTH FIELDS
                    return "<p><font color=\"red\">Either input is required for searching</font></p>";
               }
            }
            
            //Manipulate DB
            Statement stmt;
            Connection con;
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection(url, user, pwd);
            stmt = con.createStatement();
            String query = "";
            
            if(!doctorName.equals("")){
                String compareRole = "doctor";
                query = "SELECT role FROM " + schema +".User " +
                       "WHERE Username = \"" + doctorName + "\"";
                ResultSet rs = stmt.executeQuery(query);
                if (!(rs.next() && compareRole.equals((String)rs.getString("role")))) {
                    //Input doctor isn't actually a doctor... or user doesn't exist
                    return("<p><font color=\"red\">Invalid doctor username</font></p>");
                }
            }
            
            if(!patientName.equals("")){
                String compareRole = "patient";
                query = "SELECT role FROM " + schema +".User " +
                       "WHERE Username = \"" + patientName + "\"";
                ResultSet rs = stmt.executeQuery(query);
                if (!(rs.next() && compareRole.equals((String)rs.getString("role")))) {
                    //Input doctor isn't actually a doctor... or user doesn't exist
                    return("<p><font color=\"red\">Invalid patient username</font></p>");
                }
            }
            
            if(assigning){
                query = "UPDATE " + schema +".Patient set " +
                       "DoctorUsername = \"" + doctorName + "\"" +
                       "WHERE PatientUsername = \"" + patientName + "\"";
                stmt.executeUpdate(query);
                
                return("<p><font color=\"green\">Update successful</font></p>");
            }          
            
            return "";
    }

}
