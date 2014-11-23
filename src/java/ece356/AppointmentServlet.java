package ece356;
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
        } catch(Exception e){
            req.setAttribute("exception", e);
            // TODO: Change error functionality to do something other than display error
            String url = "/error.jsp";
            getServletContext().getRequestDispatcher(url).forward(req, res);
        }
        
        StringBuilder sb = new StringBuilder(128);
        if(role.equals("patient")){
            //View past and current appointments
            //Update personal info
            sb.append("Check your appointments, " + username);
        }
        else if(role.equals("doctor")){
            //View current appointments
            sb.append("Check your appointments, Doctor " + username);
            
        }
        else if(role.equals("staff")){
            //Enter, delete, update appointments
            //Update patient info
            sb.append("Check your appointments, " + username);
        }
        out.println(sb.toString());
        
        //do stuff from here on out...
    }
}