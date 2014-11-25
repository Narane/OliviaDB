package ece356;
import static ece356.UserDBAO.schema;
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

/**
 *
 * @author aarchen
 */
public class AppointmentServlet extends SecureHTTPServlet {
    
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
        Boolean loggedIn = (Boolean) session.getAttribute("loggedIn");
        String username = (String) session.getAttribute("username");
        String role = "";
        try{
            role = UserDBAO.getRole(username);
            
            if(!(role.equals("patient") || role.equals("doctor") || role.equals("staff") || role.equals("superuser"))){
                session.invalidate();
                res.sendRedirect("LoginServlet");
            }
        
            StringBuilder sb = new StringBuilder(128);
            if(role.equals("patient")){

                //Display most current appointment
                String currentAppointmentQuery = "select FirstName as \"Doctor First Name\", LastName as \"Doctor Last Name\", min(StartTime) as \"Appointment Time (yyyy-mm-dd hh:mm:ss)\" from " +
                        "(" + schema + ".appointment natural join " + schema + ".user)"+
                        "where PatientUsername = \"" + username + "\" and DoctorUsername = Username and StartTime > SYSDATE()";

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
                
                String futureAppointmentsQuery = "select FirstName as \"Doctor First Name\", LastName as \"Doctor Last Name\", StartTime as \"Appointment Time (yyyy-mm-dd hh:mm:ss)\" from " +
                        "(" + schema + ".appointment natural join " + schema + ".user)"+
                        "where PatientUsername = \"" + username + "\" and DoctorUsername = Username and StartTime > SYSDATE()" +
                        "order by StartTime desc";

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
                String pastAppointmentsQuery = "select FirstName as \"Doctor First Name\", LastName as \"Doctor Last Name\", StartTime as \"Appointment Time (yyyy-mm-dd hh:mm:ss)\" from " +
                        "(" + schema + ".Appointment natural join " + schema + ".User)"+
                        "where PatientUsername = \"" + username + "\" and DoctorUsername = Username and StartTime < SYSDATE()" +
                        "order by StartTime desc";

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
                String currentAppointmentQuery = "select FirstName as \"Patient First Name\", LastName as \"Patient Last Name\", min(StartTime) as \"Appointment Time (yyyy-mm-dd hh:mm:ss)\" from " +
                        "(" + schema + ".Appointment natural join " + schema + ".User)"+
                        "where DoctorUsername = \"" + username + "\" and PatientUsername = Username and StartTime > SYSDATE()";

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
                
                String futureAppointmentsQuery = "select FirstName as \"Patient First Name\", LastName as \"Patient Last Name\", StartTime as \"Appointment Time (yyyy-mm-dd hh:mm:ss)\" from " +
                        "(" + schema + ".Appointment natural join " + schema + ".User)"+
                        "where DoctorUsername = \"" + username + "\" and PatientUsername = Username and StartTime > SYSDATE()" +
                        "order by StartTime desc";

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
                String pastAppointmentsQuery = "select FirstName as \"Patient First Name\", LastName as \"Patient Last Name\", StartTime as \"Appointment Time (yyyy-mm-dd hh:mm:ss)\" from " +
                        "(" + schema + ".Appointment natural join " + schema + ".User)"+
                        "where DoctorUsername = \"" + username + "\" and PatientUsername = Username and StartTime < SYSDATE()" +
                        "order by StartTime desc";

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
                sb.append("Check your appointments, " + username);
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