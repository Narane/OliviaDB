package ece356;
import java.io.*;
import java.util.Date;
import javax.servlet.*;
import javax.servlet.http.*;

public class MenuServlet extends SecureHTTPServlet {
    
    //This is what you need to edit to do stuff
    @Override
    public void innerFunction(
            HttpServletRequest req, 
            HttpServletResponse res, 
            ServletOutputStream out)
        throws ServletException,  IOException {
        
        HttpSession session = req.getSession();
        Boolean loggedIn = (Boolean) session.getAttribute("loggedIn");
        String username = (String) session.getAttribute("username");
        String firstName = "ERROR USER";
        Date lastVisit = (Date) session.getAttribute("lastVisit");;
        String role = "SOMETHING BROKE PLEASE FIX";
        
        if(loggedIn.booleanValue() == true) {
            try{
                role = UserDBAO.getRole(username);
                firstName = UserDBAO.getFirstName(username);
            }
            catch(Exception e){
                req.setAttribute("exception", e);
                // Set the name of jsp to be displayed if connection fails
                String url = "/error.jsp";
                getServletContext().getRequestDispatcher(url).forward(req, res);
            }
            
            out.println("<p>Welcome back, " + firstName + "</p>");
            out.println("<p>You belong to group: " + role + "</p>");
            out.println("<p>You last visited on " + lastVisit + "</p>");
            lastVisit = new Date();
            session.setAttribute("lastVisit", lastVisit);
        }
    }
}
