/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ece356;

import java.util.ArrayList;
import java.sql.*;

/**
 *
 * @author bleskows
 */
public class QueryResult {
    public class QueryRow {
        ArrayList<String> row;
        ArrayList<String> header;
        
        public QueryRow() {
            row = new ArrayList<String>(0);
            header= new ArrayList<String>(0);
        }
        public ArrayList<String> getRow() {
            return row;
        }

        public void setRow(ArrayList<String> row) {
            this.row = row;
        }

        public ArrayList<String> getHeader() {
            return header;
        }

        public void setHeader(ArrayList<String> header) {
            this.header = header;
        }
        
        public void add(String s) {
            row.add(s);
        }
        
        public String getString(String colName) {
            int rowIndex = -1;
            for (int i = 0; i < header.size(); i++) {
                if (header.get(i).equals(colName)) {
                    rowIndex = i;
                    break;
                }
            }
            if (rowIndex == -1) {
                return null;
            }
            return row.get(rowIndex);
        }
    }
    
    ArrayList<QueryRow> resultSet = new ArrayList<QueryRow>(0);
    
    public QueryResult(ResultSet rs) 
        throws SQLException {
        ResultSetMetaData md = rs.getMetaData();
        
        int colCount = md.getColumnCount();
        ArrayList<String> header = new ArrayList<String>(0);
        for (int i = 1; i < colCount; i++) {
            header.add(md.getColumnLabel(i));
        }
        
        while(rs.next()) {
            QueryRow qRow = new QueryRow();
            qRow.setHeader(header);
            for(String label: header) {
                qRow.add(rs.getString(label));
            }
            resultSet.add(qRow);
        }
    } 
    
    public ArrayList<QueryRow> getResultSet() {
        return resultSet;
    }
    
    public QueryRow getRow(int index) {
        return resultSet.get(index);
    }
    
}
