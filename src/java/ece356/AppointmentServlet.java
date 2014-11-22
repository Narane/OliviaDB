package ece356;
import java.io.*;
import java.util.Date;
import javax.servlet.*;
import javax.servlet.http.*;

/**
 *
 * @author aarchen
 */
public class AppointmentServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        
        HttpSession session = req.getSession();
        Boolean loggedIn = (Boolean) session.getAttribute("loggedIn");
        String username = (String) session.getAttribute("username");
        
        String role = "";
        try{
            role = UserDBAO.getRole(username);
        } catch(Exception e){
            req.setAttribute("exception", e);
            // TODO: Change error functionality to do something other than display error
            String url = "/error.jsp";
            getServletContext().getRequestDispatcher(url).forward(req, res);
        }
        
        if(loggedIn == null){
            loggedIn = new Boolean(false);
        }
        
        if(loggedIn == false){
            res.sendRedirect("LoginServlet");
        }
        
        res.setContentType("text/html");
        ServletOutputStream out = res.getOutputStream();
        out.println(MarkupHelper.HeadOpen("Main Menu", role));
        out.println("<html>");
        out.println("<body>");
       
        if(loggedIn.booleanValue()==true && role == "patient"){
            //View past and current appointments
            //Update personal info
            out.println("Check your appointments, " + username);
            
        }
        
        else if(loggedIn.booleanValue()==true && role == "doctor"){
            //View current appointments
            out.println("Check your appointments, Doctor " + username);
            
        }
        
        else if(loggedIn.booleanValue()==true && role == "staff"){
            //Enter, delete, update appointments
            //Update patient info
            out.println("Check your appointments, " + username);
            
        }
        
        else{
            /* Get out of here! */
        }
        
        out.println(MarkupHelper.HeadClose());
        
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
