package ece356;
import java.io.*;
import java.util.Date;
import javax.servlet.*;
import javax.servlet.http.*;
import UserTypes.*;
import java.sql.SQLException;

public class MenuServlet extends HttpServlet {
    public void doPost(HttpServletRequest req, HttpServletResponse res)
        throws ServletException,  IOException {

        HttpSession session = req.getSession();
        Boolean loggedIn = (Boolean) session.getAttribute("loggedIn");
        String username = (String) session.getAttribute("username");
        String password = (String) session.getAttribute("password");
        Date lastVisit = (Date) session.getAttribute("lastVisit");;
        
        //Example functionality to get user back
        //Need to catch the errors that come from a called function...
        //If we don't have try/catch here, we're gonna need to put the catch
        // errors from the function but that's going to override return type
        // of this function; that is NOT POSSIBLE
        String role = "";
        try{
            role = UserDBAO.getRole(username);
        } catch(Exception e){
            req.setAttribute("exception", e);
            // Set the name of jsp to be displayed if connection fails
            String url = "/error.jsp";
            getServletContext().getRequestDispatcher(url).forward(req, res);
        }
        
        //Example on calling a specific user function based on role passed in.
        // UserFinder returns a particular user from UserTypes, then any
        //  functions can be called upon that returned user type
        String testText = UserFinder.GetUser(role).GetTestValue();
        
        if (loggedIn == null) {
            loggedIn = new Boolean(false);
        }
        
        if(loggedIn == false){
            res.sendRedirect("LoginServlet");
        }

        res.setContentType("text/html");
        ServletOutputStream out = res.getOutputStream();
        out.println("<html>");
        out.println("<body>");
        
        if(loggedIn.booleanValue() == true) {
            out.println("<head><title>Main Menu</title></head>");

            out.println("<p>Welcome back, " + username);
            out.println("<p>You belong to group: " + role);
            out.println("<p>You last visited on " + lastVisit);
            lastVisit = new Date();
            session.setAttribute("lastVisit", lastVisit);
            
            ///////////////////////////whoa
            out.println("<p>Your message of the day: " + testText);
        }
        else
        {
            out.println("<head><title>Main Menu</title><meta http-equiv=\"refresh\" content=\"1;url=\"LoginServlet\"></head>");
        }
        
        out.println("</body>");
        out.println("</html>");
    }

    public void doGet(HttpServletRequest req, HttpServletResponse res)
        throws ServletException, IOException {
        doPost(req, res);
    }
}
