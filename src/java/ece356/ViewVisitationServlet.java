/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ece356;

import ece356.QueryResult.QueryRow;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
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
            
            out.println("<h3>Patient Visitation Records</h3>");
            
            switch (role.toLowerCase()) {
                case "patient":
                    query = "select PatientUsername, StartTime, ProcedureName,"
                            + "DoctorUsername, EndTime, PrescriptionStart,"
                            + "PrescriptionEnd, Diagnosis, Prescription,"
                            + "ProcedureTime from ece356_22_2014.Visits "
                            + "where Active = 1 AND "
                            + "PatientUsername = \'"
                            + username
                            + "\'";
                    break;
                case "staff":
                    query = "select 	PatientUsername, StartTime, ProcedureName,"
                            + "DoctorUsername, EndTime, PrescriptionStart,"
                            + "PrescriptionEnd, Diagnosis, Prescription,"
                            + "ProcedureTime from "
                            + "(SELECT * FROM ece356_22_2014.Visits WHERE Active = 1) as V "
                            + "where 	V.DoctorUsername     in     ("
                            + " 		select 			"
                            + "D.DoctorUsername         from 		"
                            + "	ece356_22_2014.DoctorStaffAccess as D 		"
                            + "where Active = 1 AND "
                            + "D.StaffUsername = \'" + username
                            + "\' 	);";
                    break;
                case "doctor":
                    query = "select PatientUsername, StartTime, ProcedureName, "
                            + "DoctorUsername, EndTime, CurrentStatus, "
                            + "PrescriptionStart, PrescriptionEnd, Diagnosis, "
                            + "Prescription, Comments, ProcedureTime "
                            + "from ece356_22_2014.Visits "
                            + "where Active = 1 AND "
                            + "PatientUsername in "
                            + "(select PatientUsername from ece356_22_2014.Patient "
                            + "where DoctorUsername = \'"
                            + username
                            + "\')";
                    break;
                case "legal":
                    query = "select * from ece356_22_2014.Visits "
                            + "where Active = 1;";
            }
            rs = UserDBAO.executeQuery(query);
            out.println(UserDBAO.generateTable(rs));

        } catch (Exception e) {
            request.setAttribute("exception", e);
            // Set the name of jsp to be displayed if connection fails
            String url = "/error.jsp";
            getServletContext().getRequestDispatcher(url).forward(request, response);
        }
   }
   

}
