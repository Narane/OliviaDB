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
        Date lastVisit;
        res.setContentType("text/html");
        ServletOutputStream out = res.getOutputStream();
        out.println("<html>");
        out.println("<body>");
        out.print("<head><title>Login Page</title>");
        if (loggedIn.booleanValue() == true) {
            // user is already logged in
            res.sendRedirect("MenuServlet");
            out.print("<meta http-equiv=\"refresh\" content=\"1;url=\"MenuServlet\"></head>");
            out.println("");
            
        } else {
            if (username == null) {
                // user has not submitted the form required to log in
                out.println("<h1>Log In</h1>");
                out.println("<form action=\"LoginServlet\" " +
                    "method=\"POST\">");
                out.println("<p>Username:");
                out.println("<input type=\"text\" name=\"username\" " +
                    "value=\"\" SIZE=30>");
                out.println("<p>Password:");
                out.println("<input type=\"password\" name=\"password\" " +
                    "value=\"\" SIZE=30>");
                out.println("<p><input type=\"submit\" value=\"log in\">");
                out.println("</form>");
            } else {
                // user has submitted the log in form
                out.println("Logging in " + username);
                session.setAttribute("username", username);
                session.setAttribute("password", password);
                session.setAttribute("loggedIn", new Boolean(true));
                session.setAttribute("lastVisit", new Date());
                out.println("<a href=\"MenuServlet\">Reload Page</a>");
                res.sendRedirect("MenuServlet");
            }
            out.println("</body>");
            out.println("</html>");
        }
    }

    public void doGet(HttpServletRequest req, HttpServletResponse res)
        throws ServletException, IOException {

        doPost(req, res);
    }
}
