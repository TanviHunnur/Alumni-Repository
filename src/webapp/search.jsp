<%@ page import="java.sql.*" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Department Selection</title>
    <style>
        body 
        {
            font-family: Arial, sans-serif;
            margin: 0;
            padding: 0;
            background-color: #f4f4f4;
        }
        .container 
        {
            max-width: 600px;
            margin: 20px auto;
            padding: 20px;
            background-color: #fff;
            border-radius: 8px;
            box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
        }
        h1 
        {
            margin-top: 0;
            text-align: center;
        }
        label 
        {
            display: block;
            margin-bottom: 8px;
        }
        select, input 
        {
            width: 100%;
            padding: 10px;
            margin-bottom: 15px;
            border: 1px solid #ccc;
            border-radius: 4px;
            box-sizing: border-box;
        }
        button 
        {
            background-color: #4caf50;
            color: #fff;
            padding: 10px 20px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
        }
        button:hover 
        {
            background-color: #45a049;
        }
    </style>
</head>
<body>
    <div class="container">
        <h2>Search for Alumini</h2>
        <form action="SearchServlet" method="get">
             <label for="searchInput">Search:</label>
            <input type="text" id="searchInput" name="searchInput" placeholder="Search...">
            <br>
            <button type="submit">Search</button>
            <h2>Search Result</h2>
    	<% 
        List<String[]> resultList = (List<String[]>) request.getAttribute("resultList");
        if (resultList != null && !resultList.isEmpty()) 
        {
            for (String[] aluminiData : resultList) 
            {
                out.println("<p>Name: " + aluminiData[0] + " " + aluminiData[1] + " " + aluminiData[2] + "</p>");
                out.println("<p>Department: " + aluminiData[3] + "</p>");
                out.println("<p>Mail ID: " + aluminiData[4] + "</p>");
                out.println("<li><a href=\"AluminiDetailsServlet?mailid=" + aluminiData[4] + "\">More Details</a></li>");
                out.println("<hr>");
            }
        } 
        else 
        {
        		out.println("<p>No alumni found.</p>");
        }
    %>           
        </form>
    </div>
       
</body>
</html>
