package ece356;
import java.io.*;
import java.util.Date;
import javax.servlet.*;
import javax.servlet.http.*;
import UserTypes.*;

public class MenuServlet extends HttpServlet {
     
    public void doPost(HttpServletRequest req, HttpServletResponse res)
        throws ServletException, IOException {

        HttpSession session = req.getSession();
        Boolean loggedIn = (Boolean) session.getAttribute("loggedIn");
        String username = (String) session.getAttribute("username");
        String password = (String) session.getAttribute("password");
        Date lastVisit = (Date) session.getAttribute("lastVisit");;
        
        int userTestIndex = 0;
        if(username != null && username.contains("doctor")){
            userTestIndex = 1;
        }
        String testText = UserFinder.GetUser(userTestIndex).GetTestValue();
        
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
            out.println("<p>You last visited on " + lastVisit);
            lastVisit = new Date();
            session.setAttribute("lastVisit", lastVisit);
            
            ///////////////////////////whoa
            out.println("");
            out.println("You seem to belong to: " + testText);
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
