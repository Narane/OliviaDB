/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ece356;

import java.io.IOException;
import java.io.PrintWriter;
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

        out.print("<form method='post' action='CreatePatientServlet'>");

        out.print("<label>Username: </label><br>");
        out.print("\t <input name='username' type='text'><br>");

        out.print("<label>Password: </label><br>");
        out.print("\t <input name='password' type='password'><br>");

        out.print("<label>Repeat Password: </label><br>");
        out.print("\t <input name='passwordRepeat' type='password'><br>");

        out.print("<label>First Name:</label><br>");
        out.print("\t <input name='firstName' type='text'><br>");

        out.print("<label>Last Name</label><br>");
        out.print("\t <input name='lastName' type='text'><br>");

        out.print("<label>Cellphone Number</label><br>");
        out.print("\t <input name='cellNumber' type='text'><br>");

        out.print("<label>Home Phone Number</label><br>");
        out.print("\t <input name='homeNumber' type='text'><br>");

        out.print("<label>Address</label><br>");
        out.print("\t <input name='address' type='text'><br>");

        out.print("<label>Ontario Health Insurance Number</label><br>");
        out.print("\t <input name='sin' type='text'><br>");

        out.print("<input name='submit' type='submit' value='submit'><br>");

        out.print("</form>");

        try {
            if (req.getParameter("submit") != null) {
                String username = req.getParameter("username");
                String password = req.getParameter("password");
                String passwordRepeat = req.getParameter("passwordRepeat");
                String firstName = req.getParameter("firstName");
                String lastName = req.getParameter("lastName");
                int cellNumber = Integer.parseInt(req.getParameter("cellNumber"));
                int homeNumber = Integer.parseInt(req.getParameter("homeNumber"));
                String address = req.getParameter("address");
                int sin = Integer.parseInt(req.getParameter("sin"));

                if (username.equals("") || password.equals("") || !password.equals(passwordRepeat)
                        || firstName.equals("") || lastName.equals("")) {
                    out.print("<p>Incorrect info</p>");
                } else {

                    String query = "insert into " + UserDBAO.schema + ".User ("
                            + "Username, FirstName, LastName, Password, Role) values("
                            + "'" + username + "','" + firstName + "','" + lastName + "','"
                            + password + "','patient');";

                    UserDBAO.executeUpdate(query);

                    query = "insert into " + UserDBAO.schema + ".Patient ("
                            + "PatientUsername,CellNumber,HomeNumber,Address,SIN) "
                            + "values('" + username + "','" + cellNumber + "','"
                            + homeNumber + "','" + address + "','" + sin + "');";

                    UserDBAO.executeUpdate(query);

                }
                out.print("<p>Patient Added</p>");
            }
        } catch (Exception e) {
            req.setAttribute("exception", e);
            getServletContext().getRequestDispatcher("/error.jsp").forward(req, res);
        }

    }

}
