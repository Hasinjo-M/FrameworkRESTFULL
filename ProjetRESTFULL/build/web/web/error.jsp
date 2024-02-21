<%@page contentType="text/html" pageEncoding="UTF-8"%>

<%
    String error = "ERROR";

    if(request.getAttribute("error") != null) {
        error = (String) request.getAttribute("error");
    }
%>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Exception</title>

        <style>
            body {
                font-family: Arial, Helvetica, sans-serif;
            }
            .tab {
                border: none;
            }
            .tab th {
                width: 100px;
            }
            .tab tr {
                text-align: left;
            }
        </style>
    </head>
    <body>
        <h1>Exception Page</h1>
        
        <h3 style="color: rgb(237, 52, 52);"><%=error%></h3>
        <div>
            <br>
            <a href="emp-all"><< Retour</a>
        </div>
    </body>
</html>
