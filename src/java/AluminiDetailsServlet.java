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

@WebServlet("/AluminiDetailsServlet")
public class AluminiDetailsServlet extends HttpServlet {
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
            String query = "SELECT * FROM Alumni WHERE mailid = ?";
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
                String dob = rs.getString("dob");
                String mailid = rs.getString("mailid");
                String contactno = rs.getString("contactno");
                String state = rs.getString("state");
                String city = rs.getString("city");
                String college = rs.getString("college");
                String department = rs.getString("department");
                String passoutyear = rs.getString("passoutyear");
                String currentworkingfield = rs.getString("currentworkingfield");
                String brief = rs.getString("breif"); // corrected attribute name
                String linkedin_profile = rs.getString("linkedinprofile"); // corrected attribute name
                String resume1_image_path = rs.getString("resume1_image_path");
                String resume2_image_path = rs.getString("resume2_image_path");
                boolean ctstate = rs.getBoolean("ctstate");

                request.setAttribute("profile_image_path", profile_image_path);
                request.setAttribute("fname", fname);
                request.setAttribute("mname", mname);
                request.setAttribute("lname", lname);
                request.setAttribute("dob", dob);
                request.setAttribute("mailid", mailid);
                request.setAttribute("contactno", contactno);
                request.setAttribute("state", state);
                request.setAttribute("city", city);
                request.setAttribute("college", college);
                request.setAttribute("department", department);
                request.setAttribute("passoutyear", passoutyear);
                request.setAttribute("currentworkingfield", currentworkingfield);
                request.setAttribute("brief", brief);
                request.setAttribute("linkedin_profile", linkedin_profile);
                request.setAttribute("resume1_image_path", resume1_image_path);
                request.setAttribute("resume2_image_path", resume2_image_path);
                request.setAttribute("ctstate", ctstate);

                // Forward the request to the JSP page
                RequestDispatcher dispatcher = request.getRequestDispatcher("AluminiDetail.jsp");
                dispatcher.forward(request, response);
            } else {
                response.getWriter().println("Alumni not found");
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            // Handle error
            response.getWriter().println("An error occurred while processing your request.");
        } finally {
            // Close resources
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
