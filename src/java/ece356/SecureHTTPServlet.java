package ece356;
import java.io.*;
import java.util.Date;
import javax.servlet.*;
import javax.servlet.http.*;

public class SecureHTTPServlet extends HttpServlet {
    
    //This is what you need to edit to do stuff
    public void innerFunction(
            HttpServletRequest req, 
            HttpServletResponse res, 
            ServletOutputStream out)
        throws ServletException,  IOException {
        
        out.println("There should have been something here, but it's not overriden!");
        
    }
    
    public void doPost(HttpServletRequest req, HttpServletResponse res)
        throws ServletException,  IOException {
        
        HttpSession session = req.getSession();
        String username = (String) session.getAttribute("username");
        String role = "SOMETHING BROKE PLEASE FIX";
        
        try{
            if(!UserDBAO.securityCheck(req, res)){
                session.setAttribute("sessionExpired", new Boolean(true));
                res.sendRedirect("LoginServlet");
                return;
            }
            role = UserDBAO.getRole(username);
        }   
        catch(Exception e){
            req.setAttribute("exception", e);
            String url = "/error.jsp";
            getServletContext().getRequestDispatcher(url).forward(req, res);
        }

        res.setContentType("text/html");
        ServletOutputStream out = res.getOutputStream();
        out.println(MarkupHelper.HeadOpen("Main Menu", role));
        
        innerFunction(req, res, out);
        
        out.println(MarkupHelper.HeadClose());
    }

    public void doGet(HttpServletRequest req, HttpServletResponse res)
        throws ServletException, IOException {
        doPost(req, res);
    }
}
