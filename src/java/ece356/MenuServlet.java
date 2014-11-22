package ece356;
import java.io.*;
import java.util.Date;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.SQLException;

public class MenuServlet extends HttpServlet {
    public void doPost(HttpServletRequest req, HttpServletResponse res)
        throws ServletException,  IOException {
        
        HttpSession session = req.getSession();
        Boolean loggedIn = (Boolean) session.getAttribute("loggedIn");
        String username = (String) session.getAttribute("username");
        String password = (String) session.getAttribute("password");
        Date lastVisit = (Date) session.getAttribute("lastVisit");;
        
        try{
            if(!UserDBAO.securityCheck(req, res)){
                res.sendRedirect("LoginServlet");
                return;
            }
        }   
        catch(Exception e){
            req.setAttribute("exception", e);
            // Set the name of jsp to be displayed if connection fails
            String url = "/error.jsp";
            getServletContext().getRequestDispatcher(url).forward(req, res);
        }
        
        //Example functionality to get user back
        //Need to catch the errors that come from a called function...
        //If we don't have try/catch here, we're gonna need to put the catch
        // errors from the function but that's going to override return type
        // of this function; that is NOT POSSIBLE
        String role = "SOMETHING BROKE PLEASE FIX";
        try{
            role = UserDBAO.getRole(username);
        } catch(Exception e){
            req.setAttribute("exception", e);
            // Set the name of jsp to be displayed if connection fails
            String url = "/error.jsp";
            getServletContext().getRequestDispatcher(url).forward(req, res);
        }

        res.setContentType("text/html");
        ServletOutputStream out = res.getOutputStream();
        out.println(MarkupHelper.HeadOpen("Main Menu", role));
        
        if(loggedIn.booleanValue() == true) {
            out.println("<p>Welcome back, " + username);
            out.println("<p>You belong to group: " + role);
            out.println("<p>You last visited on " + lastVisit);
            lastVisit = new Date();
            session.setAttribute("lastVisit", lastVisit);
        }
        
        out.println(MarkupHelper.HeadClose());
    }

    public void doGet(HttpServletRequest req, HttpServletResponse res)
        throws ServletException, IOException {
        doPost(req, res);
    }
}
