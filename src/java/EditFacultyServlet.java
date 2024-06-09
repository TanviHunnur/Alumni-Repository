package abc;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet("/EditFacultyServlet")
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 2, // 2MB
                 maxFileSize = 1024 * 1024 * 10,      // 10MB
                 maxRequestSize = 1024 * 1024 * 50)   // 50MB
public class EditFacultyServlet extends HttpServlet 
{
    private static final long serialVersionUID = 1L;

    // Define your database connection parameters
    private static final String dbURL = "jdbc:mysql://localhost:3306/mydb";
    private static final String dbUser = "root";
    private static final String dbPassword = "Aparna11/";

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
    {
        Connection conn = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(dbURL, dbUser, dbPassword);

            // Retrieve form data
            String mailId = request.getParameter("mailId3");
            String firstName = request.getParameter("firstName3");
            String middleName = request.getParameter("middleName3");
            String lastName = request.getParameter("lastName3");
            String department = request.getParameter("department3");
            String state = request.getParameter("State3");
            String city = request.getParameter("City3");

            // Process uploaded profile picture
            Part profilePicPart = request.getPart("existingProfilePath3");
            String profilePicFileName = null;
            if (profilePicPart != null && profilePicPart.getSize() > 0) 
            {
                profilePicFileName = profilePicPart.getSubmittedFileName();
                String profilePicSavePath = "C:\\Users\\bhagy\\myproject\\src\\main\\webapp\\FacultyImages" + File.separator + profilePicFileName;                
                try (InputStream fileContent = profilePicPart.getInputStream()) 
                {
                    Files.copy(fileContent, new File(profilePicSavePath).toPath(), StandardCopyOption.REPLACE_EXISTING);
                }
            }

            // Fetch existing profile picture path from database if no new file uploaded
            String existingProfilePicPath = null;
            if (profilePicFileName == null) {
                existingProfilePicPath = getExistingProfilePicPath(conn, mailId);
            }

            // Prepare the update query
            String updateQuery = "UPDATE Faculty SET profile_image_path=?, fname=?, mname=?, lname=?, department=?, state=?, city=? WHERE mailid=?";
            try (PreparedStatement pstmt = conn.prepareStatement(updateQuery)) {
                pstmt.setString(1, profilePicFileName != null ? "FacultyImages/" + profilePicFileName : existingProfilePicPath); // Use existing path if no new file uploaded
                pstmt.setString(2, firstName);
                pstmt.setString(3, middleName);
                pstmt.setString(4, lastName);
                pstmt.setString(5, department);
                pstmt.setString(6, state);
                pstmt.setString(7, city);
                pstmt.setString(8, mailId);

                pstmt.executeUpdate();
            }
            response.sendRedirect("admin.html#faculty-list-content"); // Redirect to success page
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            response.sendRedirect("admin.html"); // Redirect to error page
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // Method to fetch existing profile picture path from the database
    private String getExistingProfilePicPath(Connection conn, String mailId) throws SQLException {
        String query = "SELECT profile_image_path FROM Faculty WHERE mailid=?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, mailId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("profile_image_path");
            }
        }
        return null;
    }
}
