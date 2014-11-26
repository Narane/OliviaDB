package ece356;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

/**
 *
 * @author Heesang
 * peter get back to work
 */
public class MarkupHelper {
    
    public static String HeadOpen(String pageTitle, String role){
        StringBuilder sb = new StringBuilder(256);
        sb.append("<!DOCTYPE html><html><head><title>");
        sb.append(pageTitle);
        sb.append("</title>\n   <link rel=\"stylesheet\" href=\"styles.css\">\n" +
                "   <link rel=\"stylesheet\" href=\"jquery-ui.css\">\n" +
                "   <script src=\"jquery-1.10.2.js\"></script>\n" +
                "   <script src=\"jquery-ui.js\"></script>\n" +
                "   <script> $(function() { $( \"input[name^=MyDate]\" ).datepicker({ dateFormat: \"yy-mm-dd\" }); });</script>\n" +
                "</head><body><div class=\"wrapper\">");
        sb.append(buildSidebar(role));
        sb.append("<section>");
        sb.append("<span class=\"octicon octicon-link\"></span><h2>");
        sb.append(pageTitle);
        sb.append("</h2>");
        return sb.toString();
        
    }
    
    public static String HeadClose(){
        StringBuilder sb = new StringBuilder(32);
        sb.append("</section></div></body></html>");
        
        return sb.toString();
    }
    
    private static String buildSidebar(String role){
        StringBuilder sb = new StringBuilder(256);
        sb.append("<sidebar>");
        
        sb.append(buildSidebarHelper("MenuServlet", "Main Menu"));
        
        if(role.equals("superuser")){
            sb.append(buildSidebarHelper("AuditTrailServlet", "Audit trail"));
        }
        
        if(role.equals("patient")){
            sb.append(buildSidebarHelper("UpdatePersonalInfoServlet", "Update personal information"));
        }
        
        if(role.equals("staff") || role.equals("superuser")){
            sb.append(buildSidebarHelper("CreateVisitationServlet", "Create visitation records"));
        }
        
        if(role.equals("patient")){
            sb.append(buildSidebarHelper("ViewVisitationServlet", "View visitation records"));
        }
 
        if(role.equals("patient") || role.equals("doctor") || role.equals("staff") || role.equals("superuser")){
            sb.append(buildSidebarHelper("AppointmentServlet", "View appointments"));
        }
        
        if(role.equals("patient")){
            sb.append(buildSidebarHelper("UsersServlet", "View your profile"));
        }
        else if(role.equals("doctor") || role.equals("superuser")){
            sb.append(buildSidebarHelper("UsersServlet", "View hospital profiles"));
        }
        else if(role.equals("staff")){
            sb.append(buildSidebarHelper("UsersServlet", "View associates"));
        }
        
        if(role.equals("staff") || role.equals("superuser")){
            sb.append(buildSidebarHelper("PatientAssignmentServlet", "Assign patients"));
        }
        
        if(role.equals("doctor") || role.equals("superuser")){
            sb.append(buildSidebarHelper("AccessRightsServlet", "Patient log access rights"));
        }
        
        if(role.equals("finance") || role.equals("superuser")){
            sb.append(buildSidebarHelper("FinanceServlet", "Finance hub"));
        }
        
        if(role.equals("superuser")) {
            sb.append(buildSidebarHelper("ProcedureServlet", "Hospital Procedures"));
        }
        
        if(role.equals("staff")) {
            sb.append(buildSidebarHelper("CreatePatientServlet", "New Patient"));
        }
        
        sb.append("<b>" + buildSidebarHelper("LogoutServlet", "Log Out") + "</b>");
        
        sb.append("</sidebar>");
        return sb.toString();
    }
    
    private static String buildSidebarHelper(String servletName, String description){
        return("<p><a href=\"" + servletName + "\">" + description + "</a></p>");
    }
    
    public static ArrayList<String> generateTimes(int deltaMinutes) 
        throws ParseException {
        int MINUTES_PER_DAY = 1440;
        ArrayList<String> times = new ArrayList<String>(); 
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH);
        cal.setTime(sdf.parse("00:00:00"));
        for (int i = 0; i < MINUTES_PER_DAY; i = i + deltaMinutes) {
            times.add(sdf.format(cal.getTime()));
            cal.add(Calendar.MINUTE, deltaMinutes);
        }
        return times;
    }
}
