/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ece356;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import metro.Metro;

/**
 *
 * @author bleskows
 */
public class CreateUserServlet extends SecureHTTPServlet {
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
        String role = "";
        
        int error = 0;
        String msg = "";

        try {
            if (req.getParameter("submit") != null) {
                username = req.getParameter("username");
                password = req.getParameter("password");
                passwordRepeat = req.getParameter("passwordRepeat");
                firstName = req.getParameter("firstName");
                lastName = req.getParameter("lastName");
                role = req.getParameter("role");
               
                
                if (username.equals("") || password.equals("") || 
                    passwordRepeat.equals("") || firstName.equals("") ||
                    lastName.equals("") || role.equals("")) {
                    msg = "Please provide all values!";
                }
                else if(!password.equals(passwordRepeat)) {
                    msg = "Passwords do not match!";
                }
                else if(doesUserExist(username)) {
                    msg = "User already exists!";
                }
                else {
                    
                    String query = "INSERT INTO " + UserDBAO.schema + ".User " +
                        "(Username, " +
                        "FirstName, " +
                        "LastName, " +
                        "Password, " +
                        "Role) " +
                        "VALUES " +
                        "( " +
                        "\"" + username + "\", " +
                        "\"" + firstName + "\", " +
                        "\"" + lastName + "\", " +
                        "\"" + password + "\", " +
                        "\"" + role + "\" " +
                        ")";
                    // add to Users table
                    UserDBAO.executeUpdate(query);
                    if (role.equals("doctor")) {
                        query = "INSERT INTO " + UserDBAO.schema + ".Doctor" +
                            "(DoctorUsername) " +
                            "VALUES " +
                            "( " +
                            "\"" + username + "\"" +
                            ")";
                        // add to Doctor table
                        UserDBAO.executeUpdate(query);
                        
                    }
                    else if (role.equals("staff")) {
                        query = "INSERT INTO " + UserDBAO.schema + ".Staff " +
                            "(StaffUsername) " +
                            "VALUES " +
                            "( " +
                            "\"" + username + "\"" +
                            " )";
                        // add to Staff table
                        UserDBAO.executeUpdate(query);
                    }
                    
                    msg = "Successfully added new user";
                }
                
                
            }
        } 
        catch (Exception e) {
            req.setAttribute("exception", e);
            getServletContext().getRequestDispatcher("/error.jsp").forward(req, res);
        }
        
        out.println("<h4>Enter information about a new user</h4>");
        out.println("<form method='post'>");

        out.println(Metro.label("Username:"));
        out.print(Metro.inputForm("username", "text", username, "Username", ""));

        out.println(Metro.label("Password:"));
        out.print(Metro.inputForm("password", "password", "", "Password", ""));

        out.println(Metro.label("Repeat Password:"));
        out.print(Metro.inputForm("passwordRepeat", "password", "", "Repeat Password", ""));

        out.println(Metro.label("First Name:"));
        out.print(Metro.inputForm("firstName", "text", firstName, "First Name", ""));

        out.println(Metro.label("Last Name:"));
        out.print(Metro.inputForm("lastName", "text", lastName, "Last Name", ""));

        out.println(Metro.label("Role:"));
        out.println("<select name=\"role\">");
        out.println("<option value=\"legal\">legal</option>");
        out.println("<option value=\"finance\">finance</option>");
        out.println("<option value=\"staff\">staff</option>");
        out.println("<option value=\"doctor\">doctor</option>");
        out.println("<option value=\"superuser\">superuser</option>");
        out.println("</select>");
        
        out.println(Metro.submitButton());    
        out.print("</form>");
        out.println(msg);
           
    }
    
    private boolean doesUserExist(String username) 
        throws ClassNotFoundException, SQLException {
        String query = "SELECT * FROM " + UserDBAO.schema + ".User " +
                "WHERE Username = \"" + username + "\" AND Active = TRUE";
        QueryResult qr = UserDBAO.executeQuery(query);
        
        return !qr.getResultSet().isEmpty();
        
    }
}