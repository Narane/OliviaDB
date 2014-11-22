/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ece356;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author bleskows
 */
public class UpdatePersonalInfoServlet extends HttpServlet {

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
        String password = (String) session.getAttribute("password");
        Date lastVisit = (Date) session.getAttribute("lastVisit");;
        
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
        
        // UserFinder returns a particular user from UserTypes, then any
        //  functions can be called upon that returned user type
        
        if (loggedIn == null) {
            loggedIn = new Boolean(false);
        }
        
        if(!loggedIn){
            res.sendRedirect("LoginServlet");
        }

        res.setContentType("text/html");
        ServletOutputStream out = res.getOutputStream();
        out.println(MarkupHelper.HeadOpen("Update Personal Info", role));
        
        ResultSet rs = null;
        try {
            rs = UserDBAO.executeQuery("select top 1 from " + UserDBAO.schema +
                ".Patient where username == '" + username + "'");
        
                
        out.println(
            "<form method=\"post\"\n" +
            "  First name: <input type=\"text\" value=\"" + rs.getString("FirstName") + " \" name=\"fname\"><br>\n" +
            "  Last name: <input type=\"text\" value=\"" + rs.getString("FirstName") + " \" name=\"lname\"><br>\n" +
            "  Cell phone: <input type=\"text\"" + rs.getString("cellNumber") + "\" name=\"cellNumber\"><br>\n" +
            "  Home phone: <input type=\"text\" " + rs.getString("homeNumber") + "\" name=\"homeNumber\"><br>\n" +
            "  Address: <input type=\"text\" " + rs.getString("address") + "\" name=\"address\"><br>\n" +
            "  <input type=\"submit\" value=\"Update info\">\n" +
            "</form> ");
        }
        catch (Exception e) {
            req.setAttribute("exception", e);
            // Set the name of jsp to be displayed if connection fails
            String url = "/error.jsp";
            getServletContext().getRequestDispatcher(url).forward(req, res);
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
        
        // execute db update
        
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
