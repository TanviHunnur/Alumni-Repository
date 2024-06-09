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
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet("/EditStudentServlet")
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 2, // 2MB
        maxFileSize = 1024 * 1024 * 10,      // 10MB
        maxRequestSize = 1024 * 1024 * 50)   // 50MB
public class EditStudentServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static final String dbURL = "jdbc:mysql://localhost:3306/mydb";
    private static final String dbUser = "root";
    private static final String dbPassword = "Aparna11/";

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Connection conn = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(dbURL, dbUser, dbPassword);
            PrintWriter out = response.getWriter();

            // Retrieve form data
            String mailId = request.getParameter("mailId2");
            String firstName = request.getParameter("firstName2");
            String middleName = request.getParameter("middleName2");
            String lastName = request.getParameter("lastName2");
            String dob = request.getParameter("dob2");
            String contactNo = request.getParameter("contactNo2");
            String state = request.getParameter("State2");
            String city = request.getParameter("City2");
            String department = request.getParameter("department2");
            String prn = request.getParameter("prn2");
            String passoutYear = request.getParameter("passout-year2");
            String linkedinProfile = request.getParameter("linkedin-profile2");
            
            out.println("Department:"+department);

            
            Part profilePicPart = request.getPart("existingProfilePath2");
            String profilePicFileName = null;
            if (profilePicPart != null && profilePicPart.getSize() > 0) {
                profilePicFileName = profilePicPart.getSubmittedFileName();
                String profilePicSavePath = "C:\\Users\\bhagy\\myproject\\src\\main\\webapp\\StudentImages" + File.separator + profilePicFileName;
                try (InputStream fileContent = profilePicPart.getInputStream()) {
                    Files.copy(fileContent, new File(profilePicSavePath).toPath(), StandardCopyOption.REPLACE_EXISTING);
                }
            }

            // Fetch existing profile picture path from database if no new file uploaded
            String existingProfilePicPath = null;
            if (profilePicFileName == null) {
                existingProfilePicPath = getExistingProfilePicPath(conn, mailId);
            }

            // Insert data into the database
            String insertQuery = "UPDATE Student SET profile_image_path=?, fname=?, mname=?, lname=?, dob=?, contactno=?, state=?, city=?, department=?, prn=?, passoutyear=?, linkedinprofile=? WHERE mailid=?";
            try (PreparedStatement pstmt = conn.prepareStatement(insertQuery)) {
                pstmt.setString(1, profilePicFileName != null ? "StudentImages/" + profilePicFileName : existingProfilePicPath);
                pstmt.setString(2, firstName);
                pstmt.setString(3, middleName);
                pstmt.setString(4, lastName);
                pstmt.setString(5, dob);
                pstmt.setString(6, contactNo);
                pstmt.setString(7, state);
                pstmt.setString(8, city);
                pstmt.setString(9, department);
                pstmt.setString(10, prn);
                pstmt.setString(11, passoutYear);
                pstmt.setString(12, linkedinProfile);
                pstmt.setString(13, mailId);
                
                pstmt.executeUpdate();
            }

            response.sendRedirect("admin.html#student-list-content"); // Redirect to success page
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            response.sendRedirect("admin.html"); // Redirect to error page
        } finally {
            if (conn != null) {
                try 
                {
                    conn.close();
                } 
                catch (SQLException e) 
                {
                    e.printStackTrace();
                }
            }
        }
    }

    private String getExistingProfilePicPath(Connection conn, String mailId) throws SQLException {
        String query = "SELECT profile_image_path FROM Student WHERE mailid=?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) 
        {
            pstmt.setString(1, mailId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("profile_image_path");
            }
        }
        return null;
    }
}
