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
import java.sql.Time;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import metro.Metro;

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
        Date lastVisit = (Date) session.getAttribute("lastVisit");
        
        //Need to catch the errors that come from a called function...
        //If we don't have try/catch here, we're gonna need to put the catch
        // errors from the function but that's going to override return type
        // of this function; that is NOT POSSIBLE
        // store, so that we can keep values in case of page reload
        String patientUsername = req.getParameter("patient");
        if (patientUsername == null) {patientUsername = "";}
        String visitDate = req.getParameter("MyDate1");
        if (visitDate == null) {visitDate = "";}
        String visitStart = req.getParameter("visitStart");
        if (visitStart == null) {visitStart = "";}
        String visitEnd = req.getParameter("visitEnd");
        if (visitEnd == null) {visitEnd = "";}
        String procedureName = req.getParameter("procedureName");
        if (procedureName == null) {procedureName = "";}
        String procedureTime = req.getParameter("procedureTime");
        if (procedureTime == null) {procedureTime = "";}
        String currentStatus = req.getParameter("currentStatus");
        if (currentStatus == null) {currentStatus = "";}
        String prescription = req.getParameter("prescription");
        if (prescription == null) {prescription = "";}
        String diagnosis = req.getParameter("diagnosis");
        if (diagnosis == null) {diagnosis = "";}
        String prescriptionStartDate = req.getParameter("MyDate2");
        if (prescriptionStartDate == null) {prescriptionStartDate = "";}
        String prescriptionStartTime = req.getParameter("prescStartTime");
        if (prescriptionStartTime == null) {prescriptionStartTime = "";}
        String prescriptionEndDate = req.getParameter("MyDate3");
        if (prescriptionEndDate == null) {prescriptionEndDate = "";}
        String prescriptionEndTime = req.getParameter("prescEndTime");
        if (prescriptionEndTime == null) {prescriptionEndTime = "";}
        String comments = req.getParameter("comments");
        if (comments == null) {comments = "";}
        
        String role = "";
        String msg = "";
        String query;
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

            out.println("<p>Except for Comments, all fields MUST be specified!</p>");
            
            out.println("<form method=\"post\" />\n");
            
            out.println(Metro.label("Patient Username:"));
            out.println(Metro.inputForm("patient", "text", patientUsername, "Patient Username", ""));
            //out.println("Patient username: <input type=\"text\" name=\"patient\" value=\""+ patientUsername +"\"/><br />");
            
            out.println("<h3>Visitation Information</h3>\n");
            //visitDate
                       
            out.print("<div class='grid fluid'>\n<div class='row'>\n<div class='span6'>\n");
            out.println("Date: <input type=\"text\" name=\"MyDate1\" class=\"datepicker\" value=\""+ visitDate +"\"/><br>\n");
            out.println("Start Time: <select name=\"visitStart\" value=\"" + visitStart + "\"/>\n"); 
            for (String t : MarkupHelper.generateTimes(30)) {
                out.println("<option value=\"" + t + "\">" + t + "</option>\n");
            }  
            out.println("</select><br>");
            
            // query to get available procedures
            query = "select ProcedureName from " + UserDBAO.schema + ".Costs";
            qRes = UserDBAO.executeQuery(query);
            out.println("Procedure: <select name=\"procedureName\" />\n");
            for (QueryRow qRow: qRes.getResultSet()) {
                String procName = qRow.getString("ProcedureName");
                out.println("<option value=\"" + procName + "\">" + procName + "</option>\n");
            }
            out.println("</select>\n");

            out.println("</div><div class='span6'>");
            out.println("<br>");
            out.println("End Time: &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<select name=\"visitEnd\" value=\"" + visitEnd + "\"/><br />\n");
            for (String t : MarkupHelper.generateTimes(30)) {
                out.println("<option value=\"" + t + "\">" + t + "</option>\n");
            }
            out.println("</select><br>");
            out.println("Procedure Time: <select name=\"procedureTime\" />\n");
            for (String t: MarkupHelper.generateTimes(30)) {
                out.println("<option value=\"" + t + "\">" + t + "</option>\n");
            }
            out.println("</select>");
            
            out.println("</div></div></div>");

            
            out.println(Metro.label("Current Status:"));
            out.println(Metro.inputForm("currentStatus", "text", "", "Current Status", ""));
            //out.println("Current Status: <input type=\"text\" name=\"currentStatus\" /><br />\n");
            
            out.println("<h3> Prescription Information</h3>\n");
            
            out.println(Metro.label("Prescription:"));
            out.println(Metro.inputForm("prescription", "text", prescription, "Prescription", ""));
            
            //out.println("Prescription: <input type=\"text\" name=\"prescription\" value=\""+ prescription +"\"/><br />\n");
            
            out.println(Metro.label("Diagnossis:"));
            out.println(Metro.inputForm("diagnosis", "text", diagnosis, "Diagnosis", ""));
            //out.println("Diagnosis: <input type=\"text\" name=\"diagnosis\" value=\""+ diagnosis +"\"/><br />\n");
            out.println("<br />\n");
            
            
            
            out.print("<div class='grid fluid'>\n<div class='row'>\n<div class='span6'>\n");

            out.println("Start Date: <input type=\"text\" name=\"MyDate2\" class=\"datepicker\" value=\"" + prescriptionStartDate + "\"/><br>\n");
            out.println("End Date: &nbsp;&nbsp;<input type=\"text\" name=\"MyDate3\" class=\"datepicker\" value=\"" + prescriptionEndDate + "\"/>\n");
            out.println("</div><div class='span6'>");

            out.println("Start Time: <select name=\"prescStartTime\" /><br />\n");
            for (String t : MarkupHelper.generateTimes(30)) {
                out.println("<option value=\"" + t + "\">" + t + "</option>\n");
            }
            out.println("</select>");
            out.println("<br />\n");
            out.println("End Time: &nbsp;<select name=\"prescEndTime\" /><br />\n");
            for (String t : MarkupHelper.generateTimes(30)) {
                out.println("<option value=\"" + t + "\">" + t + "</option>\n");
            }
            out.println("</select>");

            out.println("<br />\n");
            out.println("</div></div></div>");
            
            out.println("<h3>Comments</h3>\n");
            out.println("100 character limit\n");
            
            out.println("<br />\n");
            out.println(Metro.textArea("comments", comments));
            
            //out.println("<textarea rows=\"4\" cols=\"50\" name=\"comments\">" + 
            //    comments + "</textarea>\n");
            out.println("<br />\n");
            
            out.println("<input type=\"submit\" value=\"Submit\" name=\"submitAction\" />\n");
            out.println("</form>\n");
            
            String submitAction = req.getParameter("submitAction");
            Boolean submitting = submitAction != null && submitAction.equals("Submit");
            if(submitting) {
                msg = createVisitation(req);
            }
            
            out.println(msg);
           
        }
        
        catch (Exception e) {
            req.setAttribute("exception", e);
            // Set the name of jsp to be displayed if connection fails
            String url = "/error.jsp";
            getServletContext().getRequestDispatcher(url).forward(req, res);
        }   
    }
    
    private String createVisitation(HttpServletRequest req) 
        throws ClassNotFoundException, SQLException {
        String patientUsername = req.getParameter("patient");
        String doctorUsername = (String)req.getSession().getAttribute("username");
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
        
        if (patientUsername == null || patientUsername.trim().isEmpty()
                || visitDate == null || visitDate.trim().isEmpty()
                || visitStart == null || visitStart.trim().isEmpty()
                || visitEnd == null || visitEnd.trim().isEmpty()
                || procedureName == null || procedureName.trim().isEmpty()
                || procedureTime == null || procedureTime.trim().isEmpty()
                || currentStatus == null || currentStatus.trim().isEmpty()
                || prescription == null || prescription.trim().isEmpty()
                || diagnosis == null || diagnosis.trim().isEmpty()
                || prescriptionStartDate == null || prescriptionStartDate.trim().isEmpty()
                || prescriptionStartTime == null || prescriptionStartTime.trim().isEmpty()
                || prescriptionEndDate == null || prescriptionEndDate.trim().isEmpty()
                || prescriptionEndTime == null || prescriptionEndTime.trim().isEmpty()
                ) {
            return "Error! Please enter values!";
        }
        String query = "SELECT PatientUsername, DoctorUsername FROM " + 
                UserDBAO.schema + ".Patient";
        QueryResult qRes = UserDBAO.executeQuery(query);
        if (qRes.getResultSet().isEmpty() || !qRes.getRow(0).getString("DoctorUsername").equals(doctorUsername)
                || !qRes.getRow(0).getString("PatientUsername").equals(patientUsername)) {
            return "Error! You are not the assigned doctor of this user and cannot create a Visitation record";
        }
        
        query = "INSERT INTO " + UserDBAO.schema +".Visits\n" +
            "(PatientUsername, " +
            "StartTime," +
            "ProcedureName, " +
            "DoctorUsername, " +
            "EndTime, " +
            "CurrentStatus, " +
            "PrescriptionStart, " +
            "PrescriptionEnd, " +
            "Diagnosis, " +
            "Prescription, " +
            "Comments, " +
            "ProcedureTime )" +
            "VALUES " +
            "( " +
            "'" + patientUsername + "' , " +
            "'" + visitDate + " " + visitStart + "', " +
            "'" + procedureName + "', " +
            "'" + doctorUsername + "', " +
            "'" + visitDate + " " + visitEnd + "', " +
            "'" + currentStatus + "', " +
            "'" + prescriptionStartDate + " " + prescriptionStartTime + "', " +
            "'" + prescriptionEndDate + " " + prescriptionEndTime + "', " +
            "'" + diagnosis + "', " +
            "'" + prescription + "', " +
            "'" + comments + "', " +
            "'" + visitDate + " " + procedureTime + "'" + 
            ");";
        
        int returnValue = UserDBAO.executeUpdate(query);
        if (returnValue > 0) {
            return "Successfully created visitation";
        }
        else {
            return "Error! Could not create visitation";
        }
    }

}
