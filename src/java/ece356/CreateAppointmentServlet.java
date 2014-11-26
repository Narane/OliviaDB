/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ece356;

import static ece356.UserDBAO.schema;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import metro.Metro;

/**
 *
 * @author Aaron
 */
public class CreateAppointmentServlet extends SecureHTTPServlet {

    public String pageTitle(){
        return "Create Appointments";
    }

    @Override
    public void innerFunction(
            HttpServletRequest req, 
            HttpServletResponse res, 
            ServletOutputStream out)
        throws ServletException,  IOException {
        
        HttpSession session = req.getSession();
        String username = (String) session.getAttribute("username");
        String role = "";
        try{
            role = UserDBAO.getRole(username);
            
            if(!(role.equals("staff") || !role.equals("superuser"))){
                session.invalidate();
                res.sendRedirect("LoginServlet");
            }
        
            StringBuilder sb = new StringBuilder(128);
            sb.append("<h2>Enter a new appointment (* is required)</h2>");
                
            String doctorUsername = req.getParameter("doctorUsername");
            String patientUsername = req.getParameter("patientUsername");
            String startTime = req.getParameter("MyDate1");
            String endTime = req.getParameter("MyDate2");
            if(doctorUsername == null){doctorUsername = "";}
            if(patientUsername == null){patientUsername = "";}
            if(startTime == null){startTime = "";}
            if(endTime == null){endTime = "";}
            //Enter new appointment
            sb.append("<form method='post'>");
            
            sb.append(Metro.label("Doctor Username*:") + "\n");
            sb.append(Metro.inputForm("doctorUsername", "text", doctorUsername, "Doctor Username", "") + "\n");
            sb.append(Metro.label("Patient Username*:") + "\n");
            sb.append(Metro.inputForm("patientUsername", "text", patientUsername, "Patient Username", ""));
            
            /*
            sb.append("Doctor username*: <input type=\"text\" name=\"doctorUsername\"><br />");
            sb.append("Patient username*: <input type=\"text\" name=\"patientUsername\"><br />");
            */
            
            sb.append("Start Date*: &nbsp;<input type=\"text\" name=\"MyDate1\" class=\"datepicker\"> "
                    + "&nbsp;&nbsp;Start Time*:&nbsp;&nbsp;<select name=\"appStart\">");
            for (String t: MarkupHelper.generateTimes(30)) {
                sb.append("<option value=\"" + t + "\">" + t + "</option>\n");
            }
            sb.append("</select><br />");
            sb.append("End Date*: &nbsp;&nbsp;&nbsp;&nbsp;<input type=\"text\" name=\"MyDate2\" class=\"datepicker\"> "
                    + "&nbsp;&nbsp;End Time:&nbsp;&nbsp;&nbsp;&nbsp;<select name=\"appEnd\">");
            for (String t: MarkupHelper.generateTimes(30)) {
                sb.append("<option value=\"" + t + "\">" + t + "</option>\n");
            }
            sb.append("</select><br />");
            sb.append("<p>(default to 1 hour after start)</p>");
            sb.append("<input type=\"submit\" value=\"Enter\" name=\"enterNewApp\">");
            sb.append("</form>");

            String enterNewApp = req.getParameter("enterNewApp");
            Boolean enterBool = (enterNewApp != null && enterNewApp.equals("Enter"));
            
            StringBuilder inputCheck = new StringBuilder(128);
            inputCheck.append("SELECT PatientUsername, DoctorUsername "
                    + "from " + schema + ".Patient "
                    + "where DoctorUsername = \"" + doctorUsername + "\" and PatientUsername = \"" + patientUsername + "\"");
            QueryResult inputCheckResult = UserDBAO.executeQuery(inputCheck.toString());

            // CHECK DOCTORS AND PATIENT USER NAMES TO BE VALID
            if(enterBool && (doctorUsername != null && patientUsername != null && startTime != null && endTime != null)){

                if(!doctorUsername.equals("") && !patientUsername.equals("") && !startTime.equals("") && !endTime.equals("")){
                    startTime = req.getParameter("MyDate1") + " " + req.getParameter("appStart");
                    endTime = req.getParameter("MyDate2") + " " + req.getParameter("appEnd");
                    
                    if(inputCheckResult.getResultSet().size() > 0){
                      //Search with both criteria
                      StringBuilder update = new StringBuilder(128);
                      update.append("INSERT INTO " + schema + ".Appointment ");
                      update.append("(DoctorUsername, PatientUsername, StartTime, EndTime");
                      update.append(") VALUES ");
                      update.append("('" + doctorUsername + "','" + patientUsername + "','" + startTime + "','" + endTime + "'");
                      /*
                      if(!endTime.equals("")){
                          update.append(",'" + endTime +"'");
                      }
                      else{
                          update.append(",'ADDDATE('" + startTime + "', INTERVAL 1 HOUR)");
                      }
                      */
                      update.append(")");
                      UserDBAO.executeUpdate(update.toString());
                      sb.append("Appointment between Doctor " + doctorUsername + " and Patient " + patientUsername + " at " + startTime + " was successful");
                    }
                    else{
                        sb.append("<font color=\"red\">Doctor and/or Patient usernames are incorrect.</font>");
                    }
                    
                }
                else{
                    sb.append("<font color=\"red\">Incorrect data entry. Appointment not added.</font>");
                }
            }
            out.println(sb.toString());
        }
        catch(Exception e){
            req.setAttribute("exception", e);
            // TODO: Change error functionality to do something other than display error
            String url = "/error.jsp";
            getServletContext().getRequestDispatcher(url).forward(req, res);
        }
    }

}
