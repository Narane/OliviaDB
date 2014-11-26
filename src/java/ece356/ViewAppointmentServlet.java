package ece356;
import static ece356.UserDBAO.schema;
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

/**
 *
 * @author aarchen
 */
public class ViewAppointmentServlet extends SecureHTTPServlet {
    
    public String pageTitle(){
        return "View Appointments";
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
            
            if(!(role.equals("patient") || role.equals("doctor") || role.equals("staff") || role.equals("superuser") || role.equals("legal"))){
                session.invalidate();
                res.sendRedirect("LoginServlet");
            }
        
            StringBuilder sb = new StringBuilder(128);
            if(role.equals("legal")){
                String allAppointmentQuery = "select * "
                        + " from " + schema + ".Appointment";
                QueryResult allAppointment = UserDBAO.executeQuery(allAppointmentQuery).removeNullRows();
                if(allAppointment.getResultSet().size() <= 0){
                    sb.append("<br><h3>There are currently no appointments</h3></br>");
                }
                else{
                    sb.append("<br><h3>All Appointments:</h3></br>" + UserDBAO.generateTable(allAppointment));
                }
            }
            else if(role.equals("patient")){

                //Display most current appointment
                String currentAppointmentQuery = "select DoctorUsername as \"Doctor Username\", FirstName as \"Doctor First Name\", LastName as \"Doctor Last Name\", min(StartTime) as \"Appointment Time (yyyy-mm-dd hh:mm:ss)\""
                        + " from " + schema + ".Appointment join " + schema + ".User on Appointment.DoctorUsername = User.Username"
                        + " where PatientUsername = \"" + username + "\" and StartTime > SYSDATE() and " + schema + ".Appointment.Active = 1";

                QueryResult currentAppointment = UserDBAO.executeQuery(currentAppointmentQuery).removeNullRows();
                if(currentAppointment.getResultSet().size() <= 0){
                    sb.append("<br><h3>You currently have no upcoming appointments</h3></br>");
                }
                else{
                    sb.append("<br><h3>Your Upcoming Appointment:</h3></br>" + UserDBAO.generateTable(currentAppointment));
                }

                //Display future appointments
                //sb.append("<form><input type=\"button\" value=\"View Future Appointments\"></form>");
                
                sb.append("<form method=\"post\">");



                
                sb.append("<div class='grid fluid'>\n<div class='row'>\n<div class='span4'>\n");
                sb.append("<input type=\"submit\" name=\"appointmentName\" value=\"View Past Appointments\">");
                sb.append("</div><div class='span4'>");
                sb.append("<input type=\"submit\" name=\"appointmentName\" value=\"View Future Appointments\">");
                sb.append("</div></div></div>");
                
                sb.append("</form>");
                
                String appName = req.getParameter("appointmentName");
                
                Boolean past = (appName != null && appName.equals("View Past Appointments"));
                Boolean future = (appName != null && appName.equals("View Future Appointments"));

                String futureAppointmentsQuery = "select DoctorUsername as \"Doctor Username\", FirstName as \"Doctor First Name\", LastName as \"Doctor Last Name\", StartTime as \"Appointment Time (yyyy-mm-dd hh:mm:ss)\""
                        + " from " + schema + ".Appointment join " + schema + ".User on Appointment.DoctorUsername = User.Username"
                        + " where PatientUsername = \"" + username + "\" and StartTime > SYSDATE() and " + schema + ".Appointment.Active = 1"
                        + " order by StartTime desc";
                
                QueryResult futureAppointments = UserDBAO.executeQuery(futureAppointmentsQuery);
                if(future){              
                    if(futureAppointments.getResultSet().size() <= 0){
                        sb.append("<br><h3>You currently have no future appointments</h3></br>");
                    }
                    else{
                        sb.append("<br><h3>Your Future Appointments:</h3></br>" + UserDBAO.generateTable(futureAppointments));
                    }
                }

                //Display past appointments            
                String pastAppointmentsQuery = "select DoctorUsername as \"Doctor Username\", FirstName as \"Doctor First Name\", LastName as \"Doctor Last Name\", StartTime as \"Appointment Time (yyyy-mm-dd hh:mm:ss)\""
                        + " from " + schema + ".Appointment join " + schema + ".User on Appointment.DoctorUsername = User.Username"
                        + " where PatientUsername = \"" + username + "\" and StartTime < SYSDATE() and " + schema + ".Appointment.Active = 1"
                        + " order by StartTime desc";
                
                QueryResult pastAppointments = UserDBAO.executeQuery(pastAppointmentsQuery);
                if(past){ 
                    if(pastAppointments.getResultSet().size() <= 0){
                        sb.append("<br><h3>You currently have no past appointments</h3></br>");
                    }
                    else{
                        sb.append("<br><h3>Your Past Appointments:</h3></br>" + UserDBAO.generateTable(pastAppointments));
                    }
                }
            }
            else if(role.equals("doctor")){
                //View current appointments
                //Display most current appointment
                String currentAppointmentQuery = "select PatientUsername as \"Patient Username\", FirstName as \"Patient First Name\", LastName as \"Patient Last Name\", min(StartTime) as \"Appointment Time (yyyy-mm-dd hh:mm:ss)\""
                        + " from " + schema + ".Appointment join " + schema + ".User on Appointment.PatientUsername = User.Username"
                        + " where DoctorUsername = \"" + username + "\" and StartTime > SYSDATE() and " + schema + ".Appointment.Active = 1";
                
                QueryResult currentAppointment = UserDBAO.executeQuery(currentAppointmentQuery);
                if(currentAppointment.getResultSet().size() <= 0){
                    sb.append("<br><h3>You currently have no upcoming appointments</h3></br>");
                }
                else{
                    sb.append("<br><h3>Your Upcoming Appointment:</h3></br>" + UserDBAO.generateTable(currentAppointment));
                }

                //Display future appointments
                sb.append("<form method=\"post\">");



                
                
                sb.append("<div class='grid fluid'>\n<div class='row'>\n<div class='span4'>\n");
                sb.append("<input type=\"submit\" name=\"appointmentName\" value=\"View Past Appointments\">");
                sb.append("</div><div class='span4'>");
                sb.append("<input type=\"submit\" name=\"appointmentName\" value=\"View Future Appointments\">");
                sb.append("</div></div></div>");
                
                sb.append("</form>");
                
                String appName = req.getParameter("appointmentName");
                
                Boolean past = (appName != null && appName.equals("View Past Appointments"));
                Boolean future = (appName != null && appName.equals("View Future Appointments"));

                String futureAppointmentsQuery = "select PatientUsername as \"Patient Username\", FirstName as \"Patient First Name\", LastName as \"Patient Last Name\", StartTime as \"Appointment Time (yyyy-mm-dd hh:mm:ss)\""
                        + " from " + schema + ".Appointment join " + schema + ".User on Appointment.PatientUsername = User.Username"
                        + " where DoctorUsername = \"" + username + "\" and StartTime > SYSDATE() and " + schema + ".Appointment.Active = 1"
                        + " order by StartTime desc";
                
                QueryResult futureAppointments = UserDBAO.executeQuery(futureAppointmentsQuery);
                if(future){              
                    if(futureAppointments.getResultSet().size() <= 0){
                        sb.append("<br><h3>You currently have no future appointments</h3></br>");
                    }
                    else{
                        sb.append("<br><h3>Your Future Appointments:</h3></br>" + UserDBAO.generateTable(futureAppointments));
                    }
                }

                //Display past appointments            
                String pastAppointmentsQuery = "select PatientUsername as \"Patient Username\", FirstName as \"Patient First Name\", LastName as \"Patient Last Name\", StartTime as \"Appointment Time (yyyy-mm-dd hh:mm:ss)\""
                        + " from " + schema + ".Appointment join " + schema + ".User on Appointment.PatientUsername = User.Username"
                        + " where DoctorUsername = \"" + username + "\" and StartTime < SYSDATE() and " + schema + ".Appointment.Active = 1"
                        + " order by StartTime desc";
                
                QueryResult pastAppointments = UserDBAO.executeQuery(pastAppointmentsQuery);
                if(past){ 
                    if(pastAppointments.getResultSet().size() <= 0){
                        sb.append("<br><h3>You currently have no past appointments</h3></br>");
                    }
                    else{
                        sb.append("<br><h3>Your Past Appointments:</h3></br>" + UserDBAO.generateTable(pastAppointments));
                    }
                }

            }
            else if(role.equals("staff")){
                //Enter, delete, update appointments
                //Update patient info
                
                sb.append("<hr />");
                    
                sb.append("Enter old appointment to be replaced");
                sb.append("<form method=\"post\">");
                sb.append("<br />Doctor Username*: <input type=\"text\" name=\"updateDoctor\">");
                sb.append("<br />Patient Username*: <input type=\"text\" name=\"updatePatient\">");
                sb.append("</select><br />");
                sb.append("Start Date*: &nbsp;&nbsp;&nbsp;&nbsp;<input type=\"text\" name=\"MyDate1\" class=\"datepicker\">"
                        + "&nbsp;&nbsp;Start Time*:&nbsp;&nbsp;&nbsp;&nbsp;<select name=\"updateAppStart\">");
                for (String t: MarkupHelper.generateTimes(30)) {
                    sb.append("<option value=\"" + t + "\">" + t + "</option>\n");
                }
                sb.append("</select><br />");
                sb.append("End Date*: &nbsp;&nbsp;&nbsp;&nbsp;<input type=\"text\" name=\"MyDate2\" class=\"datepicker\">"
                        + "&nbsp;&nbsp;End Time*:&nbsp;&nbsp;&nbsp;&nbsp;<select name=\"updateAppEnd\">");
                for (String t: MarkupHelper.generateTimes(30)) {
                    sb.append("<option value=\"" + t + "\">" + t + "</option>\n");
                }
                sb.append("</select><br />");  
                
                sb.append("Enter new data appointment to be replace to (ignore for deletion)");
                sb.append("<form method=\"post\">");
                sb.append("<br />Doctor Username*: <input type=\"text\" name=\"updateDoctor2\">");
                sb.append("<br />Patient Username*: <input type=\"text\" name=\"updatePatient2\">");
                sb.append("</select><br />");
                sb.append("Start Date*: &nbsp;&nbsp;&nbsp;&nbsp;<input type=\"text\" name=\"MyDate3\" class=\"datepicker\">"
                        + "&nbsp;&nbsp;Start Time*:&nbsp;&nbsp;&nbsp;&nbsp;<select name=\"updateAppStart2\">");
                for (String t: MarkupHelper.generateTimes(30)) {
                    sb.append("<option value=\"" + t + "\">" + t + "</option>\n");
                }
                sb.append("</select><br />");
                sb.append("End Date*: &nbsp;&nbsp;&nbsp;&nbsp;<input type=\"text\" name=\"MyDate4\" class=\"datepicker\">"
                        + "&nbsp;&nbsp;End Time*:&nbsp;&nbsp;&nbsp;&nbsp;<select name=\"updateAppEnd2\">");
                for (String t: MarkupHelper.generateTimes(30)) {
                    sb.append("<option value=\"" + t + "\">" + t + "</option>\n");
                }
                sb.append("</select><br />"); 

                sb.append("<input type=\"submit\" name=\"update\" value=\"Update\">");
                sb.append("<input type=\"submit\" name=\"update\" value=\"Delete\">");
                sb.append("</form>");
                
                String update = req.getParameter("update"); 
                String updateDoctor = req.getParameter("updateDoctor"); 
                String updatePatient = req.getParameter("updatePatient"); 
                String updateStartTime = req.getParameter("MyDate1") + " " + req.getParameter("updateAppStart"); 
                String updateEndTime = req.getParameter("MyDate2") + " " + req.getParameter("updateAppEnd");
                String updateDoctor2 = req.getParameter("updateDoctor2"); 
                String updatePatient2 = req.getParameter("updatePatient2"); 
                String updateStartTime2 = req.getParameter("MyDate3") + " " + req.getParameter("updateAppStart2"); 
                String updateEndTime2 = req.getParameter("MyDate3") + " " + req.getParameter("updateAppEnd2");
                
                Boolean updateBool = (update != null && update.equals("Update")); 
                Boolean deleteBool = (update != null && update.equals("Delete")); 
                
                if(updateBool)
                {
                    StringBuilder inputCheck = new StringBuilder(128);
                    inputCheck.append("SELECT * "
                    + "from " + schema + ".Appointment "
                    + "WHERE DoctorUsername= \"" + updateDoctor + "\" and StartTime= \"" + updateStartTime + "\"");
                    QueryResult inputCheckResult = UserDBAO.executeQuery(inputCheck.toString());
                    if(inputCheckResult.getResultSet().size() > 0){
                        StringBuilder updateInfo = new StringBuilder(128);
                        StringBuilder updateInfo2 = new StringBuilder(128);
                        updateInfo.append("UPDATE " + schema + ".Appointment SET Active=\"0\" WHERE DoctorUsername= \"" + updateDoctor + "\" and StartTime= \"" + updateStartTime + "\" ");
                        updateInfo2.append("INSERT INTO " + schema + ".Appointment ");
                        updateInfo2.append("(DoctorUsername, PatientUsername, StartTime, EndTime");
                        updateInfo2.append(") VALUES ");
                        updateInfo2.append("(\"" + updateDoctor2 + "\",\"" + updatePatient2 + "\",\"" + updateStartTime2 + "\",\"" + updateEndTime2 + "\")");
                        UserDBAO.executeUpdate(updateInfo.toString());
                        UserDBAO.executeUpdate(updateInfo2.toString());
                        sb.append("Appointment updated to Doctor " + updateDoctor2 + " and Patient " + updatePatient2 + " at " + updateStartTime2 + " to " + updateEndTime2 + " was successful");
                        sb.append("<br />It replaced appointment between Doctor " + updateDoctor + " and Patient " + updatePatient + " at " + updateStartTime + " to " + updateEndTime + " was successful");
                    }
                    else{
                        sb.append("The listed \"existing appointment\" does not exist");
                    }
                }
                else if(deleteBool){
                    StringBuilder inputCheck = new StringBuilder(128);
                    inputCheck.append("SELECT * "
                    + "from " + schema + ".Appointment "
                    + "WHERE DoctorUsername= \"" + updateDoctor + "\" and StartTime= \"" + updateStartTime + "\"");
                    QueryResult inputCheckResult = UserDBAO.executeQuery(inputCheck.toString());
                    if(inputCheckResult.getResultSet().size() > 0){
                        StringBuilder updateInfo = new StringBuilder(128);
                        updateInfo.append("UPDATE " + schema + ".Appointment SET Active=\"0\" WHERE DoctorUsername= \"" + updateDoctor + "\" and StartTime= \"" + updateStartTime + "\" ");
                        UserDBAO.executeUpdate(updateInfo.toString());
                        sb.append("Deleted appointment where Doctor " + updateDoctor2 + " and Patient " + updatePatient2 + " at " + updateStartTime2 + " to " + updateEndTime2 + " was successful");
                    }
                    else{
                        sb.append("The listed \"existing appointment\" does not exist");
                    }
                }
                
                
                sb.append("<hr />");
                sb.append("<hr />");
                
                String doctorUsernameApp = req.getParameter("doctorUsernameApp");
                if(doctorUsernameApp == null){doctorUsernameApp = "";}                
                sb.append("<h2>Enter doctor name to view his/her appointments</h2>");
                sb.append("<form method=\"post\">");
                sb.append("Doctor username: <input type=\"text\" name=\"doctorUsernameApp\" value=\"" + doctorUsernameApp + "\">");
                sb.append("<br /><input type=\"submit\" name=\"updateQuery\" value=\"View Past Appointments\">");
                sb.append(" <input type=\"submit\" name=\"updateQuery\" value=\"View Future Appointments\">");
                sb.append("</form>");
             
                String updateQuery = req.getParameter("updateQuery");
                Boolean past = (updateQuery != null && updateQuery.equals("View Past Appointments"));
                Boolean future = (updateQuery != null && updateQuery.equals("View Future Appointments"));

                StringBuilder inputCheck = new StringBuilder(128);
                inputCheck.append("SELECT DoctorUsername "
                    + "from " + schema + ".Doctor "
                    + "where DoctorUsername = \"" + doctorUsernameApp + "\"");
                QueryResult inputCheckResult = UserDBAO.executeQuery(inputCheck.toString());
                
                //Display future appointments
                String futureAppointmentsQuery = "select PatientUsername as \"Patient Username\", FirstName as \"Patient First Name\", LastName as \"Patient Last Name\", StartTime as \"Appointment Time (yyyy-mm-dd hh:mm:ss)\", EndTime as \"Appointment Endtime (yyyy-mm-dd hh:mm:ss)\""
                        + " from " + schema + ".Appointment join " + schema + ".User on Appointment.DoctorUsername = User.Username"
                        + " where DoctorUsername = \"" + doctorUsernameApp + "\" and StartTime > SYSDATE() and " + schema + ".Appointment.Active = 1"
                        + " order by StartTime desc";               
                QueryResult futureAppointments = UserDBAO.executeQuery(futureAppointmentsQuery);              
                if(future && !doctorUsernameApp.equals("") && doctorUsernameApp != null && inputCheckResult.getResultSet().size() > 0){              
                    if(futureAppointments.getResultSet().size() <= 0){
                        sb.append("<br><h3>You currently have no future appointments</h3></br>");
                    }
                    else{
                        sb.append("<br><h3>Your Future Appointments:</h3></br>" + UserDBAO.generateTable(futureAppointments));
                    }              
                }
                else if(future){
                    sb.append("Please enter a doctor username");
                }
                
                

                //Display past appointments
                String pastAppointmentsQuery = "select PatientUsername as \"Patient Username\", FirstName as \"Patient First Name\", LastName as \"Patient Last Name\", StartTime as \"Appointment Time (yyyy-mm-dd hh:mm:ss)\", EndTime as \"Appointment Endtime (yyyy-mm-dd hh:mm:ss)\""
                        + " from " + schema + ".Appointment join " + schema + ".User on Appointment.DoctorUsername = User.Username"
                        + " where DoctorUsername = \"" + doctorUsernameApp + "\" and StartTime < SYSDATE() and " + schema + ".Appointment.Active = 1"
                        + " order by StartTime desc";
                
                QueryResult pastAppointments = UserDBAO.executeQuery(pastAppointmentsQuery);
                if(past && !doctorUsernameApp.equals("") && doctorUsernameApp != null && inputCheckResult.getResultSet().size() > 0){ 
                    if(pastAppointments.getResultSet().size() <= 0){
                        sb.append("<br><h3>" + doctorUsernameApp + " currently has no past appointments</h3></br>");
                    }
                    else{
                        sb.append("<br><h3>" + doctorUsernameApp + "'s Past Appointments:</h3></br>" + UserDBAO.generateTable(pastAppointments));
                    }
                }
                else if(past){
                    sb.append("Please enter a doctor username");
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
        
        //do stuff from here on out...
    }
}