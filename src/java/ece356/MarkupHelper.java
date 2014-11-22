package ece356;

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
        sb.append("</title><link rel=\"stylesheet\" href=\"styles.css\"></head><body><div class=\"wrapper\">");
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
    
    public static String buildSidebar(String role){
        StringBuilder sb = new StringBuilder(256);
        sb.append("<sidebar>");
        
        sb.append("<p><a href=\"MenuServlet\">");
        sb.append("Main Menu");
        sb.append("</a></p>");
        
        if(role.equals("superuser")){
            sb.append("<p><a href=\"AuditTrailServlet\">");
            sb.append("Audit trail");
            sb.append("</a></p>");
        }
        
        if(role.equals("patient") || role.equals("doctor") || role.equals("staff") || role.equals("superuser")){
            sb.append("<p><a href=\"UpdatePersonalInfoServlet\">");
            sb.append("Update personal information");
            sb.append("</a></p>");
        }

        if(role.equals("patient") || role.equals("doctor") || role.equals("legal") || role.equals("superuser")){
            sb.append("<p><a href=\"VisitationServlet\">");
            sb.append("View visitation records");
            sb.append("</a></p>");
        }
 
        if(role.equals("patient") || role.equals("doctor") || role.equals("staff") || role.equals("superuser")){
            sb.append("<p><a href=\"AppointmentServlet\">");
            sb.append("View appointments");
            sb.append("</a></p>");
        }
        
        sb.append("<p><a href=\"UsersServlet\">");
        if(role.equals("patient")){
            sb.append("View your profile");
        }
        else if(role.equals("doctor") || role.equals("superuser")){
            sb.append("View hospital profiles");
        }
        else if(role.equals("staff")){
            sb.append("View associates");
        }
        sb.append("</a></p>");
        
        if(role.equals("staff") || role.equals("superuser")){
            sb.append("<p><a href=\"PatientAssignmentServlet\">");
            sb.append("Assign patients");
            sb.append("</a></p>");
        }
        
        if(role.equals("doctor") || role.equals("superuser")){
            sb.append("<p><a href=\"AccessRightsServlet\">");
            sb.append("Change peer access rights");
            sb.append("</a></p>");
        }
        
        if(role.equals("finance") || role.equals("superuser")){
            sb.append("<p><a href=\"FinanceServlet\">");
            sb.append("Finance hub");
            sb.append("</a></p>");
        }        
        
        sb.append("</sidebar>");
        return sb.toString();
    }
}
