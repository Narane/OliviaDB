/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ece356;

import ece356.QueryResult.QueryRow;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Date;
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
public class CreateVisitationServlet extends SecureHTTPServlet {

    @Override
    public String pageTitle() {
        return "Create New Visitation Entry";
    }
    
    @Override
    public void innerFunction(HttpServletRequest req, 
        HttpServletResponse res, 
        ServletOutputStream out)
            throws ServletException, IOException {
        
        HttpSession session = req.getSession();
        Boolean loggedIn = (Boolean) session.getAttribute("loggedIn");
        String username = (String) session.getAttribute("username");
        String password = (String) session.getAttribute("password");
        Date lastVisit = (Date) session.getAttribute("lastVisit");;
        
        //Need to catch the errors that come from a called function...
        //If we don't have try/catch here, we're gonna need to put the catch
        // errors from the function but that's going to override return type
        // of this function; that is NOT POSSIBLE
        String role = "";
        String msg = "";
        QueryResult qRes;
        try {
            if(UserDBAO.securityCheck(req, res)){
                role = UserDBAO.getRole(username);
            };
       
            // UserFinder returns a particular user from UserTypes, then any
            //  functions can be called upon that returned user type

            if (loggedIn == null) {
                loggedIn = new Boolean(false);
            }

            if(!loggedIn){
                res.sendRedirect("LoginServlet");
            }

            res.setContentType("text/html");

            out.println("<p>Do not leave fields marking with * empty!</p>");
            
            out.println("<form method=\"post\">\n");
            // query to get patients that staff has access to through doctor
            String query = "select P.PatientUsername from " + UserDBAO.schema + ".Patient as P "
                + "where P.DoctorUsername in "
                + "(select DSA.DoctorUsername from " + UserDBAO.schema + ".DoctorStaffAccess as DSA "
                + "where DSA.StaffUsername = '" + username +"')";
            qRes = UserDBAO.executeQuery(query);
            out.println("Patient username: <select name=\"patient\">\n");
            out.println("<option selected value=\"\"></option>\n");
            for (QueryRow qRow: qRes.getResultSet()) {
                String pUsername = qRow.getString("PatientUsername");
                out.println("<option value=\"" + pUsername + "\">" + pUsername + "</option>\n");
            }
            out.println("</select><b>*</b>\n");
            
            out.println("<h3> Visitation Information</h3>\n");
            //visitDate
            out.println("Date: <input type=\"text\" name=\"MyDate1\" class=\"datepicker\"><b>*</b>\n");
            out.println("Start Time: <input type=\"time\" name=\"visitStart\"><b>*</b>\n");
            out.println("End Time: <input type=\"time\" name=\"visitEnd\"><b>*</b>\n");
            out.println("<br />\n");
             
            // query to get available procedures
            query = "select ProcedureName from " + UserDBAO.schema + ".Costs";
            qRes = UserDBAO.executeQuery(query);
            out.println("Procedure: <select name=\"procedureName\">\n");
            out.println("<option selected value=\"\"></option>\n");
            for (QueryRow qRow: qRes.getResultSet()) {
                String procedureName = qRow.getString("ProcedureName");
                out.println("<option value=\"" + procedureName + "\">" + procedureName + "</option>\n");
            }
            out.println("</select>\n");
            out.println("Procedure Time: <input type=\"time\" name=\"procedureTime\">\n");
            out.println("Current Status: <input type=\"text\" name=\"currentStatus\">\n");
            
            out.println("<h3> Prescription Information</h3>\n");
            out.println("Prescription: <input type=\"text\" name=\"prescription\">\n");
            out.println("Diagnosis: <input type=\"text\" name=\"diagnosis\">\n");
            out.println("<br />\n");
            //prescStartDate
            out.println("Start Date: <input type=\"text\" name=\"MyDate2\" class=\"datepicker\">\n");
            out.println("Start Time: <input type=\"time\" name=\"prescStartTime\">\n");
            out.println("<br />\n");
            //prescEndDate
            out.println("End Date: <input type=\"text\" name=\"MyDate3\" class=\"datepicker\">\n");
            out.println("End Time: <input type=\"time\" name=\"prescEndTime\">\n");
            out.println("<br />\n");
            
            out.println("<h3>Comments</h3>\n");
            out.println("100 character limit\n");
            out.println("<br />\n");
            out.println("<textarea rows=\"4\" cols=\"50\" name=\"comments\"></textarea>\n");
            out.println("<br /><br />\n");
            
            out.println("<input type=\"submit\" value=\"Submit\" name=\"submitAction\">\n");
            out.println("</form>\n");
            
            out.println(msg);
            
            String submitAction = req.getParameter("submitAction");
            Boolean submitting = submitAction != null && submitAction.equals("Submit");
            if(submitting) {
                createVisitation(req);
            }
           
        }
        
        catch (Exception e) {
            req.setAttribute("exception", e);
            // Set the name of jsp to be displayed if connection fails
            String url = "/error.jsp";
            getServletContext().getRequestDispatcher(url).forward(req, res);
        }   
    }
    
    private void createVisitation(HttpServletRequest req) {
        String patientUsername = req.getParameter("patient");
        String visitDate = req.getParameter("MyDate1");
        String visitStart = req.getParameter("visitStart");
        String visitEnd = req.getParameter("visitEnd");
        String procedureName = req.getParameter("procedureName");
        String procedureTime = req.getParameter("procedureTime");
        String currentStatus = req.getParameter("currentStatus");
        String prescription = req.getParameter("prescription");
        String diagnosis = req.getParameter("diagnosis");
        String prescriptionStartDate = req.getParameter("MyDate2");
        String prescriptionStartTime = req.getParameter("prescStartTime");
        String prescriptionEndDate = req.getParameter("MyDate3");
        String prescriptionEndTime = req.getParameter("prescEndTime");
        String comments = req.getParameter("comments");
        
        String test = "";
        
        
        
        
    }

}
