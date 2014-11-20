<%-- 
    Document   : user_data_form
    Created on : Sep 24, 2014, 9:08:32 AM
    Author     : Heesang
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
        <h1>User Data Form</h1>
        
        <form method="post" action="UserDataServlet">
        What is your name? <input type="text" name="names" value="Heesang"><br>
        What is your favourite colour? 
            <select name="colours">
                <option value="red">red</option>
                <option value="blue">blue</option>
                <option value="green">green</option>
            </select>
        <br />
        <input type="submit" value="Submit Query">
        </form>
    </body>
</html>
