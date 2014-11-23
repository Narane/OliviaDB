/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ece356;

import ece356.QueryResult.QueryRow;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.ArrayList;

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

    public String pageTitle(){
        return "View Users";
    }
    
     @Override
    public void innerFunction(HttpServletRequest request,
            HttpServletResponse response,
            ServletOutputStream out)
            throws ServletException, IOException {
        try {
            response.setContentType("text/html;charset=UTF-8");

            HttpSession session = request.getSession();
            String username = (String) session.getAttribute("username");
            //String username = "kmyin";
            String role = "Doctor";
            Boolean is_query = false;

            String f_name = request.getParameter("f_name");
            String l_name = request.getParameter("l_name");
            String p_num = request.getParameter("p_num");
            String v_date = request.getParameter("v_date");
            String diagnosis = request.getParameter("diagnosis");
            String comment = request.getParameter("comment");
            String prescription = request.getParameter("prescription");
            String surgery = request.getParameter("surgery");

            if (f_name != null || l_name != null || p_num != null ||
                    v_date != null || diagnosis != null || comment != null ||
                    prescription != null || surgery != null) {
                is_query = true;
            }
            
            if(!UserDBAO.securityCheck(request, response)){
                response.sendRedirect("LoginServlet");
                return;
            }
            
            role = UserDBAO.getRole(username);

            QueryResult rs_user_dir = null;
            String col_name = "";

            // generic user information screen

            if (role.toLowerCase().equals("doctor")){
                out.println("<h3>List of Current Patients</h3>");
                rs_user_dir = UserDBAO.executeQuery("select * from ece356_22_2014.Patient "
                        + "where DoctorUsername = \'" +
                        username +
                        "\';");
                
            } else if (role.toLowerCase().equals("patient")) {
                out.println("<h3>Patient User Directory</h3>");
                rs_user_dir = UserDBAO.executeQuery("select FirstName, LastName, "
                        + "Role from ece356_22_2014.User where Username like(\'"
                        + username + "\')");
                
            } else if (role.toLowerCase().equals("staff")) {
                out.println("<h3>Staff User Directory</h3>");
                rs_user_dir = UserDBAO.executeQuery("select FirstName, LastName, "
                        + "Role from ece356_22_2014.User where role in "
                        + "(\'doctor\', \'staff\')");
                
            } else if (role.toLowerCase().equals("superuser")){
                out.println("<h3>Admin User Directory</h3>");
                rs_user_dir = UserDBAO.executeQuery("select * "
                        + "from ece356_22_2014.User");
                
            }

            ArrayList result = rs_user_dir.getResultSet();
            int r_count = result.size();
            int c_count = 0;
            
            QueryRow r = null;
            ArrayList header;
            ArrayList r_data;

            out.println("<table border=1>");
            out.println("<tr>");
            
            r = (QueryRow) result.get(0);
            header = r.getHeader();
            c_count = header.size();
            
            for (int j = 0; j < c_count; j++) {
                out.println("<th>");
                out.println((String) header.get(j));
            }
            out.println("</tr>");
            
            for (int i = 0; i < r_count; i++) {
                r = (QueryRow) result.get(i);
                r_data = r.getRow();
                out.println("<tr>");
                for (int j = 0; j < c_count; j++) {

                    out.println("<td>");
                    out.println((String) r_data.get(j));
                    
                }
                out.println("</tr>");
            }
            
            out.println("</table>");

            if (role.toLowerCase().equals("doctor")) {
                // doctor can query its patients

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
                out.println("Diagnosis: <input type=\"text\" name=\"diagnosis\">" +
                        "<input type=\"checkbox\" name=\"diagnosis_cb\">");
                out.println("Comment: <input type=\"text\" name=\"comment\">" +
                        "<input type=\"checkbox\" name=\"comment_cb\">" + 
                        "<br>");
                out.println("Prescription: <input type=\"text\" name=\"prescription\">" +
                        "<input type=\"checkbox\" name=\"prescription_cb\">");
                out.println("Surgery: <input type=\"text\" name=\"surgery\">" +
                        "<input type=\"checkbox\" name=\"surgery_cb\">" + 
                        "<br>");

                out.println("<br>");
                out.println("<input type=\"submit\" value=\"Submit\">");

                out.println("</form>");
            }

            if (is_query) {

                // returns queried results
                is_query = false;
                ResultData rd = UserDBAO.getPatientInfo(username, f_name, l_name,
                        p_num, v_date, diagnosis, comment, prescription, surgery, username);
                ResultSet rs_patient_info = rd.rs;
                //rs_patient_info = UserDBAO.executeQuery("select * from ece356_22_2014.User;");
                c_count = rd.l.size();

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
                    for (int j = 1; j <= c_count; j++) {
                        out.println("<td>");
                        out.println(rs_patient_info.getString(j));
                    }
                    out.println("</tr>");
                }
                out.println("");
                out.println("");
                out.println("</table>");

            }
        } catch (Exception e) {
            request.setAttribute("exception", e);
            // Set the name of jsp to be displayed if connection fails
            String url = "/error.jsp";
            getServletContext().getRequestDispatcher(url).forward(request, response);
        }
    }
}
