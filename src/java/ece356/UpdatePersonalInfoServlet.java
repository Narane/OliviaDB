/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ece356;

import java.io.IOException;
import java.sql.ResultSet;
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
            
            int updateCount = updatePersonalInfo(username, req);

            res.setContentType("text/html");

            // get fields to propage text boxes with current patient info
            String query = "select * from ( select * from " + UserDBAO.schema 
                    + ".Patient inner join " + UserDBAO.schema
                    + ".User where Patient.PatientUsername = User.Username) "
                    + "as t where t.PatientUsername = \"" + username + "\""; 
            ResultSet rs = UserDBAO.executeQuery(query);
            rs.next();
        
            out.println("<p>Welcome to the UpdatePersonalInfo page</p>");
            out.println(
                "<form method=\"post\">\n" +
                "  First name: <input type=\"text\" value=\"" + rs.getString("FirstName") + "\" SIZE=30 name=\"fname\"><br>\n" +
                "  Last name: <input type=\"text\" value=\"" + rs.getString("LastName") + "\" SIZE=30 name=\"lname\"><br>\n" +
                "  Cell Phone: <input type=\"text\" value=\"" + rs.getString("CellNumber") + "\" SIZE=30 name=\"cellNumber\"><br>\n" +
                "  Home Phone: <input type=\"text\" value=\"" + rs.getString("HomeNumber") + "\" SIZE=30 name=\"homeNumber\"><br>\n" +
                "  Address: <input type=\"text\" value=\"" + rs.getString("Address") + "\" SIZE=30 name=\"address\"><br>\n" +
                "  <input type=\"submit\" value=\"Update info\">\n" +
                "</form> ");
            
            if (updateCount > 0) {
                out.println("Personal information successfully updated");
            }
            
        }
        catch (Exception e) {
            req.setAttribute("exception", e);
            // Set the name of jsp to be displayed if connection fails
            String url = "/error.jsp";
            getServletContext().getRequestDispatcher(url).forward(req, res);
        }   
    }

        private int updatePersonalInfo(String username, HttpServletRequest request) 
            throws ClassNotFoundException, SQLException{
            String fName = request.getParameter("fname");
            String lName = request.getParameter("lname");
            String cellNumber = request.getParameter("cellNumber");
            String homeNumber = request.getParameter("homeNumber");
            String address = request.getParameter("address");
           
            if (fName == null && lName == null && cellNumber == null 
                    && homeNumber == null && address == null) {
                // all fields are null -> don't call UPDATE
                return 0;
            }
            boolean hasPrevCondition;
            int patientUpdateResult = 0;
            int userUpdateResult = 0;
            if (validUpdatedText(cellNumber) || validUpdatedText(homeNumber)
                || validUpdatedText(address)) {
                // at least one field is valid for update
                StringBuilder patientQuery = new StringBuilder();
                hasPrevCondition = false;
                patientQuery.append("Update " + UserDBAO.schema + ".Patient set ");
                if (cellNumber != null && !cellNumber.trim().isEmpty()) {
                    patientQuery.append("CellNumber = " + cellNumber);
                    hasPrevCondition = true;
                }
                if (homeNumber != null && !homeNumber.trim().isEmpty()) {
                    if (hasPrevCondition) {
                        patientQuery.append(", ");
                    }
                    patientQuery.append("HomeNumber = " + homeNumber);
                    hasPrevCondition = true;
                }
                if (address != null && !address.trim().isEmpty()) {
                    if (hasPrevCondition) {
                        patientQuery.append(", ");
                    }
                    patientQuery.append("Address = \"" + address + "\"");
                }
                patientQuery.append(" where PatientUsername = \"" + username + "\"");
                patientUpdateResult = UserDBAO.executeUpdate(patientQuery.toString());
            }
           
            if (validUpdatedText(fName) || validUpdatedText(lName)) {
                // at least one field is valid for update
                StringBuilder userQuery = new StringBuilder();
                hasPrevCondition = false;
                userQuery.append("Update " + UserDBAO.schema + ".User set ");
                if (fName != null && !fName.trim().isEmpty()) {
                    userQuery.append("FirstName = \"" + fName + "\"");
                    hasPrevCondition = true;
                }
                if (lName != null && !lName.trim().isEmpty()) {
                    if (hasPrevCondition) {
                        userQuery.append(", ");
                    }
                    userQuery.append("LastName = \"" + lName + "\"");
                }
                userQuery.append(" where Username = \"" + username + "\"");
                userUpdateResult = UserDBAO.executeUpdate(userQuery.toString());
            }
            return patientUpdateResult + userUpdateResult;
            
       }
        
        private boolean validUpdatedText(String text) {
            return (text != null && !text.trim().isEmpty());
        }

}
