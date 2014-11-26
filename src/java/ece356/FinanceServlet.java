package ece356;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class FinanceServlet extends SecureHTTPServlet {

    public String pageTitle(){
        return "Finance Hub";
    }
    
    @Override
    public void innerFunction(
            HttpServletRequest request, 
            HttpServletResponse ression, 
            ServletOutputStream out)
        throws ServletException,  IOException {
            try {
                String d_name = request.getParameter("d_name");
                String p_name = request.getParameter("p_name");
                String s_date = request.getParameter("MyDate1");
                String e_date = request.getParameter("MyDate2");
                String procedure = request.getParameter("procedure");
                String g_bill = request.getParameter("g_bill");
                
                String query = "";

                /*
                String comment = request.getParameter("comment");
                String prescription = request.getParameter("prescription");
                String surgery = request.getParameter("surgery");
                */

                Boolean is_query = false;
                Boolean display_count = false;

                if (d_name != null
                        || p_name != null
                        || s_date != null
                        || e_date != null
                        || procedure != null
                        || g_bill != null) {
                        //|| comment != null || prescription != null || surgery != null
                    is_query = true;
                }

                out.println("<h3>Finance Search</h3>");
                out.println("<form>");
                out.println("Doctor Name: <input type=\"text\" name=\"d_name\">");
                out.println("Patient Name: <input type=\"text\" name=\"p_name\"><br>");
                out.println("Start Date: <input type=\"text\" name=\"MyDate1\" class=\"datepicker\">");
                out.println("End Date: <input type=\"text\" name=\"MyDate2\" class=\"datepicker\"><br>");
                out.println("Procedure Name: <input type=\"text\" name=\"procedure\">");
                out.println("Generate Bill: <input type=\"checkbox\" name=\"g_bill\"><br>");
                //out.println("Start Date: <input type=\"text\" name=\"comment\"><br>");
                
                out.println("<input type=\"submit\" value=\"Submit\">");
                out.println("</form>");
                
                if (is_query) {
                    is_query = false;
                    
                    out.println("<h3>Search Result</h3>");
                    
                    if (s_date.equals("")
                            || e_date.equals("")) {
                        // search for all dates
                        s_date = " like \'%%\' ";
                        e_date = " like \'%%\' ";
                    } else {
                        s_date = " > \'" + s_date + "\'";
                        e_date = " < \'" + e_date + "\'";
                    }
                    
                    if (!d_name.equals("")) {
                        // information of doctor
                        p_name = "";

                        // pull how many patients doctor has seen
                        query = "select V.DoctorUsername, count(*) as PatientSaw,     sum(C.Cost) + count(*) * (select Cost from ece356_22_2014.Costs where ProcedureName like '%Visit%') "
                                + "as TotalRevenue from 	ece356_22_2014.Doctor "
                                + "as D inner join 	ece356_22_2014.Visits as V inner "
                                + "join 	ece356_22_2014.Costs as C on 	"
                                + "D.DoctorUsername = V.DoctorUsername "
                                + "and 	V.ProcedureName = C.ProcedureName where "
                                + "DATE_FORMAT(V.StartTime, \'%Y-%m-%d\') "
                                + s_date
                                + " and 	DATE_FORMAT(V.EndTime, \'%Y-%m-%d\') "
                                + e_date
                                + " and 	V.PatientUsername like \'%"
                                + p_name
                                + "%\' and 	V.DoctorUsername like \'%"
                                + d_name
                                + "%\' group by DoctorUsername	";
                    }
                    else if (!p_name.equals("")) {
                        // information on patient
                        display_count = true;
                        query = "select 	V.DoctorUsername,     "
                                + "V.StartTime, V.EndTime,    V.Diagnosis,  "
                                + "V.Prescription, V.PrescriptionStart, "
                                + "V.PrescriptionEnd from ece356_22_2014.Doctor "
                                + "as D inner join 	ece356_22_2014.Visits "
                                + "as V inner join 	ece356_22_2014.Costs as "
                                + "C on 	D.DoctorUsername = "
                                + "V.DoctorUsername and 	"
                                + "V.ProcedureName = C.ProcedureName where 	"
                                + "DATE_FORMAT(V.StartTime, '%Y-%m-%d') "
                                + s_date
                                + " and 	DATE_FORMAT(V.EndTime, '%Y-%m-%d') "
                                + e_date
                                + " and 	V.PatientUsername "
                                + "like '"
                                + p_name
                                + "' and 	V.DoctorUsername like '%%'";

                    }
                    else if (!procedure.equals("")) {
                        query = "select 	V.ProcedureName, 	"
                                + "sum(C.Cost) + count(*) * (select Cost from ece356_22_2014.Costs where ProcedureName like '%Visit%') as Revenue from 	ece356_22_2014.Doctor "
                                + "as D inner join 	ece356_22_2014.Visits as V "
                                + "inner join 	ece356_22_2014.Costs as C on 	"
                                + "D.DoctorUsername = V.DoctorUsername and 	"
                                + "V.ProcedureName = C.ProcedureName where 	"
                                + "DATE_FORMAT(V.StartTime, '%Y-%m-%d') " + s_date 
                                + " and 	DATE_FORMAT(V.EndTime, '%Y-%m-%d')  " + e_date
                                + " and 	V.PatientUsername like '%" + p_name
                                + "%' and 	"
                                + "V.DoctorUsername like '%" + d_name
                                + "%' and 	"
                                + "V.ProcedureName like '%" + procedure
                                + "%' group by 	"
                                + "ProcedureName";
                    }
                    else if (g_bill.equals("on")) {
                        query = "select sum(C.Cost) + count(*) * (select Cost from ece356_22_2014.Costs where ProcedureName like '%Visit%') as BillAmount from 	ece356_22_2014.Doctor "
                                + "as D inner join 	ece356_22_2014.Visits as "
                                + "V inner join 	ece356_22_2014.Costs as "
                                + "C on 	D.DoctorUsername = V.DoctorUsername "
                                + "and 	V.ProcedureName = C.ProcedureName where 	"
                                + "DATE_FORMAT(V.StartTime, '%Y-%m-%d') " + s_date
                                + " and 	"
                                + "DATE_FORMAT(V.EndTime, '%Y-%m-%d') " + e_date
                                + " and 	"
                                + "V.PatientUsername like '%" + p_name
                                + "%' and 	"
                                + "V.DoctorUsername like '%" + d_name
                                + "%' and 	"
                                + "V.ProcedureName like '%" + procedure
                                + "%'";
                    }
                    else {
                        query = "select V.DoctorUsername,V.PatientUsername,V.StartTime,V.EndTime,V.CurrentStatus,"
                                + "V.PrescriptionStart,V.PrescriptionEnd,V.Diagnosis,"
                                + "V.Prescription,V.Comments,V.ProcedureTime,"
                                + "C.ProcedureName,C.Cost from 	ece356_22_2014.Doctor "
                                + "as D inner join 	ece356_22_2014.Visits as "
                                + "V inner join 	ece356_22_2014.Costs as "
                                + "C on 	D.DoctorUsername = V.DoctorUsername "
                                + "and 	V.ProcedureName = C.ProcedureName where 	"
                                + "DATE_FORMAT(V.StartTime, '%Y-%m-%d') " + s_date
                                + " and 	"
                                + "DATE_FORMAT(V.EndTime, '%Y-%m-%d') " + e_date
                                + " and 	"
                                + "V.PatientUsername like '%" + p_name
                                + "%' and 	"
                                + "V.DoctorUsername like '%" + d_name
                                + "%' and 	"
                                + "V.ProcedureName like '%" + procedure
                                + "%'";
                    }
                    
                    // print results
                    QueryResult qr = UserDBAO.executeQuery(query);
                    
                    if (display_count) {
                        display_count = false;
                        int visit_count = qr.getResultSet().size();
                        if (visit_count <= 1) {
                            out.print("The patient had " + visit_count + " visit");
                        } else {
                            out.print("The patient had " + visit_count + " visits");
                        }
                    }
                    
                    out.println(UserDBAO.generateTable(qr));
                }
            } catch (Exception e) {
                
            }
    }
}
