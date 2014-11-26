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


import metro.Metro;

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
        
        String usernameState = "";
        String passwordState = "";
        String passwordRepeatState = "";
        String firstNameState = "";
        String lastNameState = "";
        String cellNumberState = "";
        String homeNumberState = "";
        String addressState = "";
        String sinState = "";
        
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
                    usernameState = "error";
                    error = 1;
                } 
                if (password.equals("")) {
                    passwordState = "error";
                    error = 1;
                } 
                if (!password.equals(passwordRepeat)) {
                    passwordRepeat = "error";
                    error = 1;
                } 
                if (firstName.equals("")) {
                    firstNameState = "error";
                    error = 1;
                }
                if (lastName.equals("")) {
                    lastNameState = "error";
                    error = 1;
                }
                
                if (longTryParse(cellNumber) == null) {
                    cellNumberState = "error";
                    error = 1;
                }
                
                if (longTryParse(homeNumber) == null) {
                    homeNumberState = "error";
                    error = 1;
                }
                
                if (longTryParse(sin) == null) {
                    sinState = "error";
                    error = 1;
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
                        usernameState = "error";
                        error = 0x20;
                    }
                }
            }
        } catch (Exception e) {
            req.setAttribute("exception", e);
            getServletContext().getRequestDispatcher("/error.jsp").forward(req, res);
        }
        
        out.println("<form method='post' action='CreatePatientServlet'>");

            out.println(Metro.label("Username:"));
            out.print(Metro.inputBoxes("username", "text", username, "Username", usernameState));
            
            out.println(Metro.label("Password:"));
            out.print(Metro.inputBoxes("password", "password", "", "Password", passwordState));
            
            out.println(Metro.label("Repeat Password:"));
            out.print(Metro.inputBoxes("passwordRepeat", "password", "", "Repeat Password", passwordRepeatState));

            out.println(Metro.label("First Name:"));
            out.print(Metro.inputBoxes("firstName", "text", firstName, "First Name", firstNameState));
            
            out.println(Metro.label("Last Name:"));
            out.print(Metro.inputBoxes("lastName", "text", lastName, "Last Name", lastNameState));
            
            out.println(Metro.label("Cell Phone Number:"));
            out.print(Metro.inputBoxes("cellNumber", "text", cellNumber, "Cell Phone Number", cellNumberState));
            
            out.println(Metro.label("Home Phone Number"));
            out.print(Metro.inputBoxes("homeNumber", "text", homeNumber, "Home Phone Number", homeNumberState));
            
           
            out.println(Metro.label("Ontario Health Insurance Number"));
            out.println(Metro.inputBoxes("sin", "text", sin, "Ontario Health Insurance Number", sinState));
            
            out.println(Metro.label("Address:"));
            out.println(Metro.textArea("address", ""));
            
            out.println(Metro.submitButton());
            
            out.print("</form>");
             
            if (error != 0) {
                if (username.equals("")) {
                    out.print("<p style=\"display:inline\"><font color=\"red\">  Need Username</font></p><br>");
                } else if ((error & 0x20) == 0x20) {
                    out.print("<p style=\"display:inline\"><font color=\"red\">  Username taken</font></p><br>");
                }

                if (password.equals("")) {
                    out.print("<p style=\"display:inline\"><font color=\"red\">  Need a password</font></p><br>");
                }

                if (!password.equals(passwordRepeat)) {
                    out.print("<p style=\"display:inline\"><font color=\"red\">  Passwords to not match</font></p><br>");
                }

                if (firstName.equals("")) {
                    out.print("<p style=\"display:inline\"><font color=\"red\">  Need first name</font></p><br>");
                }

                if (lastName.equals("")) {
                    out.print("<p style=\"display:inline\"><font color=\"red\">  Need last name</font></p><br>");
                }

                if (longTryParse(cellNumber) == null) {
                    out.print("<p style=\"display:inline\"><font color=\"red\">  Cellphone number must be a number</font></p><br>");
                }

                if (longTryParse(homeNumber) == null) {
                    out.print("<p style=\"display:inline\"><font color=\"red\">  Home Phone number must be a number</font></p><br>");
                }

                if (longTryParse(sin) == null) {
                    out.print("<p style=\"display:inline\"><font color=\"red\">  Must be a number</font></p><br>");
                }

                if (error == 0x10000) {
                    out.print("<p><font color=\"green\">Added patient sucessfully</font></p><br>");           
                }
            }

    }
    
    protected Long longTryParse(String s) {
        Long result;
        try {
            result = Long.parseLong(s);
        } catch (NumberFormatException  e) {
            result = null;
        }
        
        return result;
    }

}
