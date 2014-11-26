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
public class ViewVisitationServlet extends SecureHTTPServlet {

     @Override
    public void innerFunction(HttpServletRequest request, 
        HttpServletResponse response, 
        ServletOutputStream out)
            throws ServletException, IOException {
        
        try {
            response.setContentType("text/html;charset=UTF-8");
            HttpSession session = request.getSession();
            String username = (String) session.getAttribute("username");
            String role = UserDBAO.getRole(username);
            String query = "";
            QueryResult rs = null;
            int c_count = 0;
            
            out.println("<h3>Patient Visitation Records</h3>");
            
            switch (role.toLowerCase()) {
                case "patient":
                    query = "select PatientUsername, StartTime, ProcedureName,"
                            + "DoctorUsername, EndTime, PrescriptionStart,"
                            + "PrescriptionEnd, Diagnosis, Prescription,"
                            + "ProcedureTime from ece356_22_2014.Visits "
                            + "where PatientUsername = \'"
                            + username
                            + "\'";
                    break;
                case "staff":
                    query = "select 	PatientUsername, StartTime, ProcedureName,"
                            + "DoctorUsername, EndTime, PrescriptionStart,"
                            + "PrescriptionEnd, Diagnosis, Prescription,"
                            + "ProcedureTime from	 	ece356_22_2014.Visits as"
                            + " V where 	V.DoctorUsername     in     ("
                            + " 		select 			"
                            + "D.DoctorUsername         from 		"
                            + "	ece356_22_2014.DoctorStaffAccess as D 		"
                            + "where 			D.StaffUsername = \'" + username
                            + "\' 	);";
                    break;
                case "doctor":
                    query = "select PatientUsername, StartTime, ProcedureName, "
                            + "DoctorUsername, EndTime, CurrentStatus, "
                            + "PrescriptionStart, PrescriptionEnd, Diagnosis, "
                            + "Prescription, Comments, ProcedureTime "
                            + "from ece356_22_2014.Visits where PatientUsername in "
                            + "(select PatientUsername from ece356_22_2014.Patient "
                            + "where DoctorUsername = \'"
                            + username
                            + "\')";
                    break;
            }
            rs = UserDBAO.executeQuery(query);
            ArrayList result = rs.getResultSet();
            int r_count = result.size();
            c_count = 0;
            QueryRow r = null;
            ArrayList header;
            ArrayList r_data;
            
            out.println("<table border=1>");
            out.println("<tr>");
            
            //output headers
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
            
           
            
        } catch (Exception e) {
            
        }
   }
   

}
