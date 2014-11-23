/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ece356;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

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
public class UsersServlet extends SecureHTTPServlet {

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
    public void innerFunction(HttpServletRequest request,
            HttpServletResponse response,
            ServletOutputStream out)
            throws ServletException, IOException {
        try {
            response.setContentType("text/html;charset=UTF-8");

            HttpSession session = request.getSession();
            //String username = (String) session.getAttribute("username");
            String username = "kmyin";
            String role = "Doctor";
            Boolean is_query = false;

            String f_name = request.getParameter("f_name");
            String l_name = request.getParameter("l_name");
            String p_num = request.getParameter("p_num");
            String v_date = request.getParameter("v_date");

            if (f_name != null || l_name != null || p_num != null || v_date != null) {
                is_query = true;
            }
            
            /*
            if(!UserDBAO.securityCheck(request, response)){
            //if (false){
                response.sendRedirect("LoginServlet");
                return;
            }
            */
            role = UserDBAO.getRole(username);

            ResultSet rs_user_dir = null;
            //ResultSet rs_patient_info = null;
            ResultSet rs = null;

            rs = UserDBAO.getColumns("User");
            String col_name = "FirstName";

            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Users Servlet</title>");
            out.println("</head>");

            // generic user information screen
            out.println("<div>");
            out.println("<body>");

            out.println("<h3>" + role.substring(0, 1).toUpperCase() + role.substring(1) + " User Directory</h3>");

            if (role.toLowerCase().equals("doctor")){
                rs_user_dir = UserDBAO.executeQuery("select * from ece356_22_2014.Patient where DoctorUsername = \'" +
                        username +
                        "\';");
            } else if (role.toLowerCase().equals("patient")) {
                rs_user_dir = UserDBAO.executeQuery("select * from ece356_22_2014.Patient where DoctorUsername = \'" +
                        username +
                        "\';");
            } else if (role.toLowerCase().equals("staff")) {
                rs_user_dir = UserDBAO.executeQuery("select * from ece356_22_2014.Patient where DoctorUsername = \'" +
                        username +
                        "\';");
            }

            ResultSetMetaData md_user = rs_user_dir.getMetaData();
            int c_count = md_user.getColumnCount();

            out.println("<table border=1>");

            out.println("<tr>");
            for (int j = 1; j < c_count; j++){
                out.println("<th>");
                out.println(md_user.getColumnLabel(j));
            }    
            out.println("</tr>");

            while (rs_user_dir.next()) {
                out.println("<tr>");
                for (int j = 1; j < c_count; j++) {
                    out.println("<td>");
                    out.println(rs_user_dir.getString(j));
                }
                out.println("</tr>");
            }
            out.println("");
            out.println("");
            out.println("</table>");

            out.println("</body>");
            out.println("</div>");

            if (role.toLowerCase().equals("doctor")) {
                // doctor can query its patients

                out.println("<div>");
                out.println("<body>");
                out.println("<h3>Patient Search</h3>");
                out.println("<form>");

                out.println("First Name: <input type=\"text\" name=\"f_name\">" +
                        "<input type=\"checkbox\" name=\"f_name_cb\">");
                out.println("Last Name: <input type=\"text\" name=\"l_name\">" +
                        "<input type=\"checkbox\" name=\"l_name_cb\">" + 
                        "<br>");
                out.println("Patient Number: <input type=\"text\" name=\"p_num\">" +
                        "<input type=\"checkbox\" name=\"p_num_cb\">");
                out.println("Visit Date: <input type=\"text\" name=\"v_date\">" +
                        "<input type=\"checkbox\" name=\"v_date_cb\">" + 
                        "<br>");

                out.println("<br>");
                out.println("<input type=\"submit\" value=\"Submit\">");

                out.println("</form>");
                out.println("</body>");
                out.println("</div>");
            }

            if (is_query) {

                // returns queried results
                is_query = false;
                ResultData rd = UserDBAO.getPatientInfo(username, f_name, l_name, p_num, v_date, username);
                ResultSet rs_patient_info = rd.rs;
                //rs_patient_info = UserDBAO.executeQuery("select * from ece356_22_2014.User;");
                c_count = rd.l.size();
                
                out.println("<div>");
                out.println("<body>");

                out.println("<h3>Search Results</h3>");

                out.println("<table border=1>");
                // first row for column names
                out.println("<tr>");
                for (int j = 0; j < c_count; j++){
                    out.println("<th>");
                    out.println(rd.l.get(j).toString());
                }    
                out.println("</tr>");

                // now for data

                while (rs_patient_info.next()) {
                    out.println("<tr>");
                    for (int j = 1; j < c_count; j++) {
                        out.println("<td>");
                        out.println(rs_patient_info.getString(j));
                    }
                    out.println("</tr>");
                }
                out.println("");
                out.println("");
                out.println("</table>");

                out.println("<div>");
                out.println("<body>");
            }

            out.println("</html>");
        } catch (Exception e) {
            request.setAttribute("exception", e);
            // Set the name of jsp to be displayed if connection fails
            String url = "/error.jsp";
            getServletContext().getRequestDispatcher(url).forward(request, response);
        }
    }
}
