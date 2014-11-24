/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ece356;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.text.DecimalFormat;

/**
 *
 * @author bleskows
 */
public class ProcedureServlet extends SecureHTTPServlet {

   @Override
    public String pageTitle() {
        return "Create, Modify and Delete Procedures";
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
        String status = "";
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
            out.println("<p>Please input Procedure Name and Cost in decimal format to define/update a new procedure.</p>");
            out.println(
                "<form method=\"post\">\n" +
                "  Procedure Name:   <input type=\"text\" SIZE=30 name=\"pname\"><br>\n" +
                "  Cost ($): <input type=\"text\"  SIZE=30 name=\"cost\"><br>\n" +
                "  <input type=\"submit\" value=\"Create\" name=\"submitAction\">\n" +
                "  <input type=\"submit\" value=\"Delete\" name=\"submitAction\">\n" +
                "</form> ");
            
            int updateCount = 0;
            String submitAction = req.getParameter("submitAction");
            Boolean modifying = (submitAction != null && submitAction.equals("Create"));
            Boolean deleting = (submitAction != null && submitAction.equals("Delete"));
            try {
                if (req.getParameter("cost") != null && deleting) {
                    updateCount = deleteProcedure(req);
                }
                else {
                    updateCount = createProcedure(req);
                }
            }
            catch (SQLException e) {
                if (!e.getMessage().contains("Duplicate entry")) {
                   status = e.getMessage(); 
                }
                // swallow Duplicate entry exceptions, since we will update anyway
                
            }
            
            if (updateCount > 0) {
                if (deleting) {
                    status = "Successfully deleted procedure \"" + 
                        req.getParameter("pname") + "\""; 
                }
                else {
                    BigDecimal decimalCost = convertStringToMonetaryFormBigDecimal(req.getParameter("cost"));
                    status = "Successfully created procedure \"" + 
                        req.getParameter("pname") + "\" with cost $" + decimalCost.toString();
                }
            }
            
            else if (modifying && updateCount == 0){
                // Found duplicate. Will update entry instead
                updateProcedure(req);
                BigDecimal decimalCost = convertStringToMonetaryFormBigDecimal(req.getParameter("cost"));
                status = "Successfully updated procedure \"" + 
                    req.getParameter("pname") + "\" with cost $" + decimalCost.toString();
            }

            out.println(status);
            
            out.println("<br />");
            String query = "SELECT * FROM " + UserDBAO.schema + ".Costs";
            QueryResult qRes = UserDBAO.executeQuery(query);
            out.println(UserDBAO.generateTable(qRes));
        }
            
        catch (Exception e) {
            req.setAttribute("exception", e);
            // Set the name of jsp to be displayed if connection fails
            String url = "/error.jsp";
            getServletContext().getRequestDispatcher(url).forward(req, res);
        }   
    }
    
    private boolean testBigDecimalEquality(BigDecimal bd1, BigDecimal bd2) {
        return bd1.longValueExact() == bd2.longValueExact();
    }
    private BigDecimal convertStringToMonetaryFormBigDecimal(String input) {
        DecimalFormat df = new DecimalFormat("#.00");
        df.setMaximumFractionDigits(2);
        BigDecimal decimalCost = new BigDecimal(df.format(Double.parseDouble(input)));
        
        return decimalCost;
        
    }
    private int updateProcedure(HttpServletRequest req)
        throws ClassNotFoundException, SQLException {
        String pName = req.getParameter("pname");
        String cost = req.getParameter("cost");
        
        if ((pName == null || pName.trim().isEmpty()) || (cost == null || cost.trim().isEmpty())) {
            return -1;
        }
        
        DecimalFormat df = new DecimalFormat("#.00");
        df.setMaximumFractionDigits(2);
        BigDecimal decimalCost = new BigDecimal(df.format(Double.parseDouble(cost)));
        
        String query = "UPDATE " + UserDBAO.schema + ".Costs SET `Cost` = " + 
                decimalCost.toString() + " WHERE `ProcedureName` = \"" +  pName +"\"";        
        int returnValue = UserDBAO.executeUpdate(query);
        
        return returnValue;
    }
    
    private int deleteProcedure(HttpServletRequest req)
        throws ClassNotFoundException, SQLException {
        String pName = req.getParameter("pname");
        
        if (pName == null || pName.trim().isEmpty()) {
            return -1;
        }
        
        String query = "DELETE FROM " + UserDBAO.schema + ".Costs "
            + "WHERE ProcedureName = \"" + pName + "\"";
        
        int returnValue = UserDBAO.executeUpdate(query);
        return returnValue;
    }
    private int createProcedure(HttpServletRequest req) 
        throws ClassNotFoundException, SQLException {
        String pName = req.getParameter("pname");
        String cost = req.getParameter("cost");
        
        if ((pName == null || pName.trim().isEmpty()) || (cost == null || cost.trim().isEmpty())) {
            return -1;
        }
       
        DecimalFormat df = new DecimalFormat("#.00");
        df.setMaximumFractionDigits(2);
        BigDecimal decimalCost = new BigDecimal(df.format(Double.parseDouble(cost)));
        String query = "INSERT INTO " + UserDBAO.schema + ".Costs " +
            "(`ProcedureName`, `Cost`) VALUES " +
            "('" + pName +"', " + decimalCost.toString() + ")";
        
        int returnValue = UserDBAO.executeUpdate(query);
        
        return returnValue;
        
    }

}
