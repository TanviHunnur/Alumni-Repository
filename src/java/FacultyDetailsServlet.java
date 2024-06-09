package abc;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/FacultyDetailsServlet")
public class FacultyDetailsServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Get the alumnus ID from the request parameter
        String mailId = request.getParameter("mailid");

        // Perform database query to fetch alumnus details based on ID
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            // Establish database connection
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/mydb", "root", "Aparna11/");
            
            // Prepare SQL statement
            String query = "SELECT * FROM Faculty WHERE mailid = ?";
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, mailId);

            // Execute query
            rs = pstmt.executeQuery();

            // Set alumnus details as request attributes
            if (rs.next()) {
                String profile_image_path = rs.getString("profile_image_path");
                String fname = rs.getString("fname");
                String mname = rs.getString("mname");
                String lname = rs.getString("lname");
                String mailid = rs.getString("mailid");
                String contactno = rs.getString("contactno");
                String state = rs.getString("state");
                String city = rs.getString("city");
                String department = rs.getString("department");
                boolean ctstate = rs.getBoolean("ctstate");

                request.setAttribute("profile_image_path", profile_image_path);
                request.setAttribute("fname", fname);
                request.setAttribute("mname", mname);
                request.setAttribute("lname", lname);
                request.setAttribute("mailid", mailid);
                request.setAttribute("contactno", contactno);
                request.setAttribute("state", state);
                request.setAttribute("city", city);
                request.setAttribute("department", department);
                request.setAttribute("ctstate", ctstate);

                // Forward the request to the JSP page
                RequestDispatcher dispatcher = request.getRequestDispatcher("FacultyDetail.jsp");
                dispatcher.forward(request, response);
            } 
            else
            {
                response.getWriter().println("Alumni not found");
            }
        } 
        catch (ClassNotFoundException | SQLException e) 
        {
            e.printStackTrace();
            // Handle error
            response.getWriter().println("An error occurred while processing your request.");
        } 
        finally 
        {
            // Close resources
            try 
            {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            }
            catch (SQLException e) 
            {
                e.printStackTrace();
            }
        }
    }
}
