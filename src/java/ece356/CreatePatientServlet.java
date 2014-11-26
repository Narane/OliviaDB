/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ece356;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Peter
 */
public class CreatePatientServlet extends SecureHTTPServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    public void innerFunction(
            HttpServletRequest req,
            HttpServletResponse res,
            ServletOutputStream out)
            throws ServletException, IOException {
        
        String username = "";
        String password = "";
        String passwordRepeat = "";
        String firstName = "";
        String lastName = "";
        String cellNumber = "";
        String homeNumber = "";
        String address = "";
        String sin = "";
        
        int error = 0;

        try {
            if (req.getParameter("submit") != null) {
                username = req.getParameter("username");
                password = req.getParameter("password");
                passwordRepeat = req.getParameter("passwordRepeat");
                firstName = req.getParameter("firstName");
                lastName = req.getParameter("lastName");
                cellNumber = req.getParameter("cellNumber");
                homeNumber = req.getParameter("homeNumber");
                address = req.getParameter("address");
                sin = req.getParameter("sin");

                if (username.equals("")) {
                    error = 1;
                } 
                if (password.equals("")) {
                    error += 0x2;
                } 
                if (!password.equals(passwordRepeat)) {
                    error += 0x4;
                } 
                if (firstName.equals("")) {
                    error += 0x8;
                }
                if (lastName.equals("")) {
                    error += 0x10;
                }
                
                if (intTryParse(cellNumber) == null) {
                    error += 0x40;
                }
                
                if (intTryParse(homeNumber) == null) {
                    error += 0x80;
                }
                
                if (intTryParse(sin) == null) {
                    error += 0x100;
                }
                
                
                if (error == 0) {
                    
                    String query = "select count(Username) as count from " + UserDBAO.schema + ".User " +
                            "where Username='" + username.trim() + "';";
                    
                    QueryResult qr = UserDBAO.executeQuery(query);
                    
                    String results = qr.getResultSet().get(0).getString("count");
                    
                    if (results.equals("0")) { 
                    
                        query = "insert into " + UserDBAO.schema + ".User ("
                                + "Username, FirstName, LastName, Password, Role) values("
                                + "'" + username + "','" + firstName + "','" + lastName + "','"
                                + password + "','patient');";

                        UserDBAO.executeUpdate(query);

                        query = "insert into " + UserDBAO.schema + ".Patient ("
                                + "PatientUsername,CellNumber,HomeNumber,Address,SIN) "
                                + "values('" + username + "','" + cellNumber + "','"
                                + homeNumber + "','" + address + "','" + sin + "');";
                        UserDBAO.executeUpdate(query);
                        
                        error = 0x10000;
                    } else {
                        error = 0x20;
                    }
                }
            }
        } catch (Exception e) {
            req.setAttribute("exception", e);
            getServletContext().getRequestDispatcher("/error.jsp").forward(req, res);
        }
        
        out.print("<form method='post' action='CreatePatientServlet'>");

        out.print("<label>Username: </label><br>");
        out.print("\t <input name='username' type='text' value='" + username + "'>");
        if ((error & 0x1) == 0x1) {
            out.print("<p style=\"display:inline\"><font color=\"red\">  Invalid Username</font></p>");
        } else if ((error & 0x20) == 0x20) {
            out.print("<p style=\"display:inline\"><font color=\"red\">  Username taken</font></p>");
        }
        out.print("<br>");
        
        
        
        out.print("<label>Password: </label><br>");
        out.print("\t <input name='password' type='password'>");
        if ((error & 0x2) == 0x2) {
            out.print("<p style=\"display:inline\"><font color=\"red\">  Need a password</font></p>");
        }
        out.print("<br>");

        out.print("<label>Repeat Password: </label><br>");
        out.print("\t <input name='passwordRepeat' type='password'>");
        if ((error & 0x4) == 0x4) {
            out.print("<p style=\"display:inline\"><font color=\"red\">  Passwords to not match</font></p>");
        }
        out.print("<br>");

        out.print("<label>First Name:</label><br>");
        out.print("\t <input name='firstName' type='text' value='" + firstName + "'>");
        if ((error & 0x8) == 0x8) {
            out.print("<p style=\"display:inline\"><font color=\"red\">  Need first name</font></p>");
        }
        out.print("<br>");
        
        out.print("<label>Last Name</label><br>");
        out.print("\t <input name='lastName' type='text' value='" + lastName + "'>");
        if ((error & 0x10) == 0x10) {
            out.print("<p style=\"display:inline\"><font color=\"red\">  Passwords to not match</font></p>");
        }
        out.print("<br>");
        
        out.print("<label>Cellphone Number</label><br>");
        out.print("\t <input name='cellNumber' type='text' value='" + cellNumber + "'>");
        if ((error & 0x40) == 0x40) {
            out.print("<p style=\"display:inline\"><font color=\"red\">  Cellphone number must be a number</font></p>");
        }
        out.print("<br>");
        
        out.print("<label>Home Phone Number</label><br>");
        out.print("\t <input name='homeNumber' type='text' value='" + homeNumber + "'>");
        if ((error & 0x40) == 0x40) {
            out.print("<p style=\"display:inline\"><font color=\"red\">  Home Phone number must be a number</font></p>");
        }
        out.print("<br>");
        
        out.print("<label>Address</label><br>");
        out.print("\t <input name='address' type='text' value='" + address + "'><br>");

        out.print("<label>Ontario Health Insurance Number</label><br>");
        out.print("\t <input name='sin' type='text' value='" + sin + "'>");
        if ((error & 0x40) == 0x40) {
            out.print("<p style=\"display:inline\"><font color=\"red\">  Must be a number</font></p>");
        }
        out.print("<br>");
        
        out.print("<input name='submit' type='submit' value='submit'><br>");

        out.print("</form>");
        
        if (error == 0x10000) {
            out.print("<p><font color=\"green\">Added patient sucessfully</font></p>");           
        }
        out.print("<br>");

    }
    
    protected Integer intTryParse(String s) {
        Integer result;
        try {
            result = Integer.parseInt(s);
        } catch (NumberFormatException  e) {
            result = null;
        }
        
        return result;
    }

}
