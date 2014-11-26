/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ece356;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author bleskows
 */
public class UpdatePersonalInfoServlet extends SecureHTTPServlet {
    
    @Override
    public String pageTitle() {
        return "Update Personal Information";
    }
    
    @Override
    public void innerFunction(HttpServletRequest req, 
        HttpServletResponse res, 
        ServletOutputStream out)
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
        String msg = "";
        try {
            if(UserDBAO.securityCheck(req, res)){
                role = UserDBAO.getRole(username);
            };
       
            // UserFinder returns a particular user from UserTypes, then any
            //  functions can be called upon that returned user type

            if (loggedIn == null) {
                loggedIn = new Boolean(false);
            }

            if(!loggedIn){
                res.sendRedirect("LoginServlet");
            }

            res.setContentType("text/html");

            if (req.getParameter("submitAction") != null && req.getParameter("submitAction").equals("Update info")) {
                msg = updatePersonalInfo(username, req);
            }
            
            String firstName = "";
            String lastName = "";
            String cellNumber = "";
            String homeNumber = "";
            String address = "";
            QueryResult qr = null;
            if (req.getParameter("submitAction") != null && req.getParameter("submitAction").equals("Display current info")) {
                qr = displayCurrentInfo(username);
            }
            if (qr != null && !qr.getResultSet().isEmpty()) {
                firstName = qr.getRow(0).getString("FirstName");
                lastName = qr.getRow(0).getString("LastName");
                cellNumber = qr.getRow(0).getString("CellNumber");
                homeNumber = qr.getRow(0).getString("HomeNumber");
                address = qr.getRow(0).getString("Address");
            }
            
            out.println("<p>Welcome to the UpdatePersonalInfo page</p>");
            out.println(
                "<form method=\"post\">\n" +
                "  First name: <input type=\"text\" value=\"" + firstName + "\" SIZE=30 name=\"fname\"><br>\n" +
                "  Last name: <input type=\"text\" value=\"" + lastName + "\" SIZE=30 name=\"lname\"><br>\n" +
                "  Cell Phone: <input type=\"text\" value=\"" + cellNumber + "\" SIZE=30 name=\"cellNumber\"><br>\n" +
                "  Home Phone: <input type=\"text\" value=\"" + homeNumber + "\" SIZE=30 name=\"homeNumber\"><br>\n" +
                "  Address: <input type=\"text\" value=\"" + address + "\" SIZE=30 name=\"address\"><br>\n" +
                "  <input type=\"submit\" value=\"Update info\" name=\"submitAction\">\n" +
                "  <input type=\"submit\" value=\"Display current info\" name=\"submitAction\">\n" +
                "</form> ");
            
            
            out.println(msg);
            
        }
        catch (Exception e) {
            req.setAttribute("exception", e);
            // Set the name of jsp to be displayed if connection fails
            String url = "/error.jsp";
            getServletContext().getRequestDispatcher(url).forward(req, res);
        }   
    }
        
        private QueryResult displayCurrentInfo(String username)
            throws ClassNotFoundException, SQLException {
        
            String query = "SELECT * FROM " + UserDBAO.schema +".User AS U " +
                "JOIN " + UserDBAO.schema + ".Patient as P " +
                "WHERE U.Username = P.PatientUsername " +
                "AND U.Username = \"" + username + "\"" +
                "AND U.Active = P.Active " + 
                "AND U.Active = TRUE";
            
            QueryResult qr = UserDBAO.executeQuery(query);
            return qr;
        }

        private String updatePersonalInfo(String username, HttpServletRequest request) 
            throws ClassNotFoundException, SQLException{
            String fName = request.getParameter("fname");
            String lName = request.getParameter("lname");
            String cellNumber = request.getParameter("cellNumber");
            String homeNumber = request.getParameter("homeNumber");
            String address = request.getParameter("address");
           
            if (fName == null || fName.trim().isEmpty() || 
                lName == null || lName.trim().isEmpty() || 
                cellNumber == null || cellNumber.trim().isEmpty() || 
                homeNumber == null || homeNumber.trim().isEmpty() || 
                address == null || address.trim().isEmpty()) {
                    return "Need all fields to update. Try pressing \"Display current info\" first";
            }
            
            // get copy of info to be updated
            String query = "SELECT * FROM " + UserDBAO.schema +".User AS U " +
                "JOIN " + UserDBAO.schema + ".Patient as P " +
                "WHERE U.Username = P.PatientUsername " +
                "AND U.Username = \"" + username + "\"" +
                "AND U.Active = P.Active " + 
                "AND U.Active = TRUE";
            QueryResult qr = UserDBAO.executeQuery(query);
            
            // invalidate existing User entry
            query = "UPDATE " + UserDBAO.schema + ".User " +
                "SET " +
                "Active = FALSE " +
                "WHERE Username = '" + username + "' AND Active = TRUE";
            UserDBAO.executeUpdate(query);
            
            // insert new User entry
            query = "INSERT INTO " + UserDBAO.schema + ".User " +
                "(Username, " +
                "FirstName, " +
                "LastName, " +
                "Password, " +
                "Role ) " +
                "VALUES " +
                "( " +
                "\"" + username + "\", " +
                "\"" + fName + "\", " +
                "\"" + lName + "\", " +
                "\"" + qr.getRow(0).getString("Password") + "\", " +
                "\"" + qr.getRow(0).getString("Role") + "\""+
                " )";
            UserDBAO.executeUpdate(query);
            
            // invalidate existing Patient entry
            query = "UPDATE " + UserDBAO.schema + ".Patient " +
                "SET " +
                "Active = FALSE " +
                "WHERE PatientUsername = '" + username + "' AND Active = TRUE";
            UserDBAO.executeUpdate(query);
            
            // insert new Patient entry
            query = "INSERT INTO " + UserDBAO.schema +".Patient " +
                "(PatientUsername, " +
                "DoctorUsername, " +
                "CellNumber, " +
                "HomeNumber, " +
                "PatientNumber, " +
                "Address, " +
                "SIN ) " +
                "VALUES " +
                "( " +
                "\"" + qr.getRow(0).getString("PatientUsername") + "\", " +
                "\"" + qr.getRow(0).getString("DoctorUsername") + "\", " +
                "\"" + cellNumber + "\", " +
                "\"" + homeNumber + "\", " +
                "\"" + qr.getRow(0).getString("PatientNumber") + "\", " +
                "\"" + address + "\", " +
                "\"" + qr.getRow(0).getString("SIN") + "\"" +
                " )";
            UserDBAO.executeUpdate(query);
            
            return "Personal info successfully updated";
            
       }
        
        private boolean validUpdatedText(String text) {
            return (text != null && !text.trim().isEmpty());
        }

}
