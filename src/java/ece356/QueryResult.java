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
        
        public boolean isRowOfNulls() {
            for(String s: row) {
                if (s != null) {
                    return false;
                }
            }
            return true;
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
        
        public boolean setString(String colName, String value) {
            int rowIndex = -1;
            for (int i = 0; i < header.size(); i++) {
                if (header.get(i).equals(colName)) {
                    rowIndex = i;
                    break;
                }
            }
            if (rowIndex == -1) {
                return false;
            }
            row.set(rowIndex, value);
            return true;
        }
    }
    
    ArrayList<QueryRow> resultSet = new ArrayList<QueryRow>(0);
    String message = "";
    
    public QueryResult() {
        resultSet = new ArrayList<QueryRow>(0);
        message = "";
    }
    
    public QueryResult(ResultSet rs) { 
        try {
            ResultSetMetaData md = rs.getMetaData();
            int colCount = md.getColumnCount();
            ArrayList<String> header = new ArrayList<String>(0);
            for (int i = 1; i <= colCount; i++) {
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
        catch (SQLException e) {
            // yield QueryResult with empty resultSet
            resultSet = new ArrayList<QueryRow>(0);
            message = e.getMessage();
        }
    } 
    
    public QueryResult removeNullRows() {
        QueryResult qRes = new QueryResult();
        qRes.setMessage(this.message);
        qRes.setResultSet(this.resultSet);
        
        int i = 0;
        while (i < qRes.getResultSet().size()) {
            if(qRes.getResultSet().get(i).isRowOfNulls()) {
                qRes.getResultSet().remove(i);
                i = i - 1;
            }
            i = i + 1;
        }
        
        return qRes;
        
    }
    
    public ArrayList<QueryRow> getResultSet() {
        return resultSet;
    }
    
    public void setResultSet(ArrayList<QueryRow> resultSet) {
        this.resultSet = resultSet;
    }
    public QueryRow getRow(int index) {
        return resultSet.get(index);
    }
    
    public boolean hasResults() {
        return (resultSet.size() > 0);
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
}
