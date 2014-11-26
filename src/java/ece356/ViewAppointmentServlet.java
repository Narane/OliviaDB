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
                        + " where PatientUsername = \"" + username + "\" and StartTime > SYSDATE()";

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
                sb.append("<input type=\"submit\" name=\"appointmentName\" value=\"View Past Appointments\">");
                sb.append("<input type=\"submit\" name=\"appointmentName\" value=\"View Future Appointments\">");
                sb.append("</form>");
                
                String appName = req.getParameter("appointmentName");
                
                Boolean past = (appName != null && appName.equals("View Past Appointments"));
                Boolean future = (appName != null && appName.equals("View Future Appointments"));

                String futureAppointmentsQuery = "select DoctorUsername as \"Doctor Username\", FirstName as \"Doctor First Name\", LastName as \"Doctor Last Name\", StartTime as \"Appointment Time (yyyy-mm-dd hh:mm:ss)\""
                        + " from " + schema + ".Appointment join " + schema + ".User on Appointment.DoctorUsername = User.Username"
                        + " where PatientUsername = \"" + username + "\" and StartTime > SYSDATE()"
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
                        + " where PatientUsername = \"" + username + "\" and StartTime < SYSDATE()"
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
                        + " where DoctorUsername = \"" + username + "\" and StartTime > SYSDATE()";
                
                QueryResult currentAppointment = UserDBAO.executeQuery(currentAppointmentQuery);
                if(currentAppointment.getResultSet().size() <= 0){
                    sb.append("<br><h3>You currently have no upcoming appointments</h3></br>");
                }
                else{
                    sb.append("<br><h3>Your Upcoming Appointment:</h3></br>" + UserDBAO.generateTable(currentAppointment));
                }

                //Display future appointments
                sb.append("<form method=\"post\">");
                sb.append("<input type=\"submit\" name=\"appointmentName\" value=\"View Past Appointments\">");
                sb.append("<input type=\"submit\" name=\"appointmentName\" value=\"View Future Appointments\">");
                sb.append("</form>");
                
                String appName = req.getParameter("appointmentName");
                
                Boolean past = (appName != null && appName.equals("View Past Appointments"));
                Boolean future = (appName != null && appName.equals("View Future Appointments"));

                String futureAppointmentsQuery = "select PatientUsername as \"Patient Username\", FirstName as \"Patient First Name\", LastName as \"Patient Last Name\", StartTime as \"Appointment Time (yyyy-mm-dd hh:mm:ss)\""
                        + " from " + schema + ".Appointment join " + schema + ".User on Appointment.PatientUsername = User.Username"
                        + " where DoctorUsername = \"" + username + "\" and StartTime > SYSDATE()"
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
                        + " where DoctorUsername = \"" + username + "\" and StartTime < SYSDATE()"
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
                //Enter doctor name to view table for that doctor
                String doctorUsernameApp = null;
                String appName = null;
                
                String savedDoctorUsernameApp = (String)session.getAttribute("savedDoctorUsernameApp");
                String savedAppName = (String)session.getAttribute("savedAppName");
              
                if (savedDoctorUsernameApp != null) {
                    doctorUsernameApp = savedDoctorUsernameApp;
                } else {
                    doctorUsernameApp = req.getParameter("doctorUsernameApp");
                }
                
                if(savedAppName != null){
                    appName = savedAppName;
                } else {
                    appName = req.getParameter("appointmentName");
                }
               
                
                sb.append("<h2>Enter doctor name to view his/her appointments</h2>");
                sb.append("<form method=\"post\">");
                sb.append("Doctor username: <input type=\"text\" name=\"doctorUsernameApp\">");
                sb.append("<br /><input type=\"submit\" name=\"appointmentName\" value=\"View Past Appointments\">");
                sb.append(" <input type=\"submit\" name=\"appointmentName\" value=\"View Future Appointments\">");
                sb.append("</form>");
             
                session.setAttribute("savedDoctorUsernameApp", doctorUsernameApp);
                session.setAttribute("savedAppName", appName);
                
                Boolean past = (appName != null && appName.equals("View Past Appointments"));
                Boolean future = (appName != null && appName.equals("View Future Appointments"));

                StringBuilder inputCheck = new StringBuilder(128);
                inputCheck.append("SELECT DoctorUsername "
                    + "from " + schema + ".Doctor "
                    + "where DoctorUsername = \"" + doctorUsernameApp + "\"");
                QueryResult inputCheckResult = UserDBAO.executeQuery(inputCheck.toString());
                
                //Display future appointments
                String futureAppointmentsQuery = "select PatientUsername as \"Patient Username\", FirstName as \"Patient First Name\", LastName as \"Patient Last Name\", StartTime as \"Appointment Time (yyyy-mm-dd hh:mm:ss)\", EndTime as \"Appointment Endtime (yyyy-mm-dd hh:mm:ss)\""
                        + " from " + schema + ".Appointment join " + schema + ".User on Appointment.DoctorUsername = User.Username"
                        + " where DoctorUsername = \"" + doctorUsernameApp + "\" and StartTime > SYSDATE()"
                        + " order by StartTime desc";
                
                QueryResult futureAppointments = UserDBAO.executeQuery(futureAppointmentsQuery);
                if(future && doctorUsernameApp != null && inputCheckResult.getResultSet().size() > 0){              
                    if(futureAppointments.getResultSet().size() <= 0){
                        sb.append("<br><h3>" + doctorUsernameApp + " currently has no future appointments</h3></br>");
                    }
                    else{
                        //String updatePatient = req.getParameter("updatePatient");
                        //String updateStartTime = req.getParameter("MyDate1");
                        //String updateEndTime = req.getParameter("MyDate2");
                        String updateStartTime = null;
                        String updateEndTime = null;
                        String updatePatient = null;
                        String updateQuery = null;
                        
                        String savedUpdatePatient = (String)session.getAttribute("savedUpdatePatient");
                        String savedStartTime = (String)session.getAttribute("savedStartTime");
                        String savedEndTime = (String)session.getAttribute("savedEndTime");
                        String savedUpdateQuery = (String)session.getAttribute("savedUpdateQuery");
                        
                        if(savedUpdatePatient != null){
                            updatePatient = savedUpdatePatient;
                        } else {
                            updatePatient = req.getParameter("updatePatient");
                        }
                        if(savedStartTime != null){
                            updateStartTime = savedStartTime;
                        } else {
                            updateStartTime = req.getParameter("MyDate1");
                        }
                        if(savedEndTime != null){
                            updateEndTime = savedEndTime;
                        } else {
                            updateEndTime = req.getParameter("MyDate2");
                        }
                        if(savedUpdateQuery != null){
                            updateQuery = savedUpdateQuery;
                        } else {
                            updateQuery = req.getParameter("updateQuery");
                        }
                        //if(updatePatient == null){updatePatient = "";}
                        //if(updateStartTime == null){updateStartTime = "";}
                        //if(updateEndTime == null){updateEndTime = "";}
                        sb.append("Query appointment for this update/deletion");
                        req.setAttribute("appointmentName", "View Future Appointments");
                        sb.append("<form method=\"post\">");
                        sb.append("<br />Patient Username*: <input type=\"text\" name=\"updatePatient\" value=\"" + updatePatient + "\">");
                        sb.append("</select><br />");
                        sb.append("Start Date*: &nbsp;&nbsp;&nbsp;&nbsp;<input type=\"text\" name=\"MyDate1\" class=\"datepicker\" value=\"" + updateStartTime + "\">"
                                + "&nbsp;&nbsp;Start Time*:&nbsp;&nbsp;&nbsp;&nbsp;<select name=\"updateAppStart\">");
                        for (String t: MarkupHelper.generateTimes(30)) {
                            sb.append("<option value=\"" + t + "\">" + t + "</option>\n");
                        }
                        sb.append("</select><br />");
                        sb.append("End Date*: &nbsp;&nbsp;&nbsp;&nbsp;<input type=\"text\" name=\"MyDate2\" class=\"datepicker\" value=\"" + updateEndTime + "\">"
                                + "&nbsp;&nbsp;End Time*:&nbsp;&nbsp;&nbsp;&nbsp;<select name=\"updateAppEnd\">");
                        for (String t: MarkupHelper.generateTimes(30)) {
                            sb.append("<option value=\"" + t + "\">" + t + "</option>\n");
                        }
                        sb.append("</select><br />");                     
                        
                        sb.append("<input type=\"submit\" name=\"updateQuery\" value=\"Search\">");
                        sb.append("</form>");
                        
                        String submitAction = req.getParameter("updateQuery");
                        Boolean updateQueryBool = (updateQuery != null && updateQuery.equals(submitAction));

                        
                            session.setAttribute("savedUpdateQuery", savedUpdateQuery);
                            session.setAttribute("savedStartTime", savedStartTime);
                            session.setAttribute("savedEndTime", savedEndTime);
                        if(submitAction != null){
                            session.setAttribute("savedUpdatePatient", savedUpdatePatient);
                        }

                        if(updateQueryBool){
                            
                            StringBuilder inputCheck2 = new StringBuilder(128);
                            inputCheck2.append("SELECT * "
                                + "from " + schema + ".Appointment "
                                + "where DoctorUsername = \"" + doctorUsernameApp + "\" and PatientUsername = \"" + updatePatient + "\""
                                + " and StartTime = \"" + req.getParameter("MyDate1") + " " + req.getParameter("updateAppStart") + "\""
                                + " and EndTime = \"" + req.getParameter("MyDate2") + " " + req.getParameter("updateAppEnd") + "\""
                                + " and Active = true");
                            QueryResult inputCheckResult2 = UserDBAO.executeQuery(inputCheck2.toString());
                            
                            //updateStartTime = req.getParameter("MyDate1") + " " + req.getParameter("updateAppStart");
                            //updateEndTime = req.getParameter("MyDate2") + " " + req.getParameter("updateAppEnd");
                            //req.setAttribute("updateStartTime", updateStartTime);
                            //req.setAttribute("updateEndTime", updateEndTime);
                            if(inputCheckResult2.getResultSet().size() > 0){
                            
                                StringBuilder potentialUpdate = new StringBuilder(128);
                                potentialUpdate.append("UPDATE " + schema + ".Appointment SET Active=\"0\" WHERE DoctorUsername= \"" + doctorUsernameApp + "\" and StartTime = \"updateStartTime\" ");
                                
                                
                                sb.append("<form method=\"post\">");
                                sb.append("<input type=\"submit\" name=\"updateQuery\" value=\"Update\">");
                                sb.append("<input type=\"submit\" name=\"updateQuery\" value=\"Delete\">");
                                sb.append("</form>");
                            
                                String update = req.getParameter("update");
                                String delete = req.getParameter("delete");
                                Boolean updateBool = (update != null && update.equals("Update"));
                                Boolean deleteBool = (delete != null && delete.equals("Delete"));
                            
                                if(updateBool){
                                    StringBuilder updateApp = new StringBuilder(128);
                                    updateApp.append(potentialUpdate.toString());
                                    //Insert "updated" entry
                                    updateApp.append("INSERT INTO " + schema + ".Appointment ");
                                    updateApp.append("(DoctorUsername, PatientUsername, StartTime, EndTime");
                                    updateApp.append(") VALUES ");
                                    updateApp.append("('" + doctorUsernameApp + "','" + updatePatient + "','" + updateStartTime + "','" + updateEndTime + "')");
                                    UserDBAO.executeUpdate(updateApp.toString());
                                    session.setAttribute("savedUpdateQuery", null);
                                    
                                    sb.append("Appointment update to Doctor " + doctorUsernameApp + " and Patient " + updatePatient + " at Start Time " + updateStartTime + " and End Time " + updateEndTime + " was successful");
                                    session.setAttribute("savedUpdateQuery", null);
                                    session.setAttribute("savedStartTime", null);
                                    session.setAttribute("savedEndTime", null);
                                }
                                else if(deleteBool){
                                    StringBuilder updateApp = new StringBuilder(128);
                                    updateApp.append(potentialUpdate.toString());
                                    UserDBAO.executeUpdate(updateApp.toString());
                                }
                            }
                        }
                        else if(updateQueryBool){
                            sb.append("Query does not exist");
                        }
                        
                        
                        sb.append("<br><h3>" + doctorUsernameApp + "'s Future Appointments:</h3></br>" + UserDBAO.generateTable(futureAppointments));
                    }
                }
                else if(future){
                    sb.append("Please enter a doctor username");
                }

                //Display past appointments
                String pastAppointmentsQuery = "select PatientUsername as \"Patient Username\", FirstName as \"Patient First Name\", LastName as \"Patient Last Name\", StartTime as \"Appointment Time (yyyy-mm-dd hh:mm:ss)\", EndTime as \"Appointment Endtime (yyyy-mm-dd hh:mm:ss)\""
                        + " from " + schema + ".Appointment join " + schema + ".User on Appointment.DoctorUsername = User.Username"
                        + " where DoctorUsername = \"" + doctorUsernameApp + "\" and StartTime < SYSDATE()"
                        + " order by StartTime desc";
                
                QueryResult pastAppointments = UserDBAO.executeQuery(pastAppointmentsQuery);
                if(past && !doctorUsernameApp.equals("") && !doctorUsernameApp.equals(null) && inputCheckResult.getResultSet().size() > 0){ 
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