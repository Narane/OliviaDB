package ece356;
import java.io.*;
import java.util.Date;
import javax.servlet.*;
import javax.servlet.http.*;

public class LoginServlet extends HttpServlet {
     
    public void doPost(HttpServletRequest req, HttpServletResponse res)
        throws ServletException, IOException {

        HttpSession session = req.getSession();
        Boolean loggedIn = (Boolean) session.getAttribute("loggedIn");
        if (loggedIn == null) {
            loggedIn = new Boolean(false);
        }
        
        String username = req.getParameter("username");
        String password = req.getParameter("password");
        res.setContentType("text/html");
        ServletOutputStream out = res.getOutputStream();
        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.print("<head><title>Login Page</title>");
        out.print("<link href=\"css/metro-bootstrap.css\" rel=\"stylesheet\">");
        out.print("<link href=\"styles.css\" rel=\"stylesheet\">");
        
        if (loggedIn.booleanValue() == true) {
            // user is already logged in
            res.sendRedirect("MenuServlet");
            out.print("<meta http-equiv=\"refresh\" content=\"1;url=\"MenuServlet\"></head>");
            out.println("<body>");
            
        } else {
            if (username == null) {
                // user has not submitted the form required to log in
                out.println("</head>");
                out.println("<body class=\"metro\"><div class=\"container\"><div class=\"grid\"><div class=\"row\"><div class=\"span1\"></div>");
                out.println("<div class=\"span10\">");
                out.println("<legend>Log In</legend>");
                out.println("<form action=\"LoginServlet\" " +
                    "method=\"POST\"><fieldset>");
                
                out.println("<label>Username</label>");
                out.println("<div class=\"input-control text\" data-role=\"input-control\">");
                out.println("<input type=\"text\" name=\"username\" " +
                                    "value=\"\" SIZE=30>");
                out.println("</div>");

                out.println("<label>Password</label>");
                out.println("<div class=\"input-control text\" data-role=\"input-control\">");
                out.println("<input type=\"password\" name=\"password\" " +
                    "value=\"\" SIZE=30>");
                out.println("</div>");
                
                out.println("<p><input type=\"submit\" value=\"log in\">");

                out.println("</fieldset></form>");
                
                Boolean failedLogin = (Boolean) session.getAttribute("failedLogin");
                Boolean sessionExpired = (Boolean) session.getAttribute("sessionExpired");
                if (failedLogin != null && failedLogin.booleanValue() == true){
                    out.println("<p><font color=\"red\">Invalid login information</font></p>");
                }
                else if(sessionExpired != null && sessionExpired.booleanValue() == true){
                    out.println("<p><font color=\"red\">Session has expired</font></p>");
                    session.setAttribute("sessionExpired", new Boolean(false));
                }
                
            } else {
                // user has submitted the log in form
                out.println("Logging in " + username);
                session.setAttribute("username", username);
                session.setAttribute("password", password);
                try{
                    if(UserDBAO.securityCheck(req, res)){
                        session.setAttribute("loggedIn", new Boolean(true));
                        session.setAttribute("lastVisit", new Date());
                        session.setAttribute("failedLogin", new Boolean(false));
                        out.println("<a href=\"MenuServlet\">Reload Page</a>");
                        res.sendRedirect("MenuServlet");
                    }
                    else{
                        session.setAttribute("failedLogin", new Boolean(true));
                        res.sendRedirect("LoginServlet");
                    }
                }
                catch(Exception e){
                    req.setAttribute("exception", e);
                    // Set the name of jsp to be displayed if connection fails
                    String url = "/error.jsp";
                    getServletContext().getRequestDispatcher(url).forward(req, res);
                }
            }
            out.println("</div></div></div></div></body>");
            out.println("</html>");
        }
    }

    public void doGet(HttpServletRequest req, HttpServletResponse res)
        throws ServletException, IOException {

        doPost(req, res);
    }
}
