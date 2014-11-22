package ece356;
import java.io.*;
import java.util.Date;
import javax.servlet.*;
import javax.servlet.http.*;

public class AuditTrailServlet extends HttpServlet {
    public void doPost(HttpServletRequest req, HttpServletResponse res)
        throws ServletException,  IOException {
        
        HttpSession session = req.getSession();
        Boolean loggedIn = (Boolean) session.getAttribute("loggedIn");
        String username = (String) session.getAttribute("username");
        Date lastVisit = (Date) session.getAttribute("lastVisit");;
        String role = "SOMETHING BROKE PLEASE FIX";
        
        try{
            if(!UserDBAO.securityCheck(req, res)){
                res.sendRedirect("LoginServlet");
                return;
            }
            role = UserDBAO.getRole(username);
        }   
        catch(Exception e){
            req.setAttribute("exception", e);
            // Set the name of jsp to be displayed if connection fails
            String url = "/error.jsp";
            getServletContext().getRequestDispatcher(url).forward(req, res);
        }

        res.setContentType("text/html");
        ServletOutputStream out = res.getOutputStream();
        out.println(MarkupHelper.HeadOpen("Main Menu", role));
        
        if(loggedIn.booleanValue() == true) {
            //things go here
        }
        
        out.println(MarkupHelper.HeadClose());
    }

    public void doGet(HttpServletRequest req, HttpServletResponse res)
        throws ServletException, IOException {
        doPost(req, res);
    }
}
