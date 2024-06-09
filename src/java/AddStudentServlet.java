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
import java.sql.SQLException;

/**
 * Servlet implementation class AddAlumniServlet
 */
@WebServlet("/AddStudentServlet")
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 2, // 2MB
                 maxFileSize = 1024 * 1024 * 10,      // 10MB
                 maxRequestSize = 1024 * 1024 * 50)   // 50MB
public class AddStudentServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Define your database connection parameters
    private static final String dbURL = "jdbc:mysql://localhost:3306/mydb";
    private static final String dbUser = "root";
    private static final String dbPassword = "Aparna11/";

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Connection conn = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(dbURL, dbUser, dbPassword);

            // Retrieve form data
            String firstName = request.getParameter("first-name");
            String middleName = request.getParameter("middle-name");
            String lastName = request.getParameter("last-name");
            String email = request.getParameter("email");
            String contactNo = request.getParameter("contact-no");
            String dob = request.getParameter("dob");
            String state = request.getParameter("state");
            String city = request.getParameter("city");
            String passoutYear = request.getParameter("passout-year");
            String department = request.getParameter("department");
            String prn = request.getParameter("prn");
            String linkedinProfile = request.getParameter("linkedin-profile");

            // Process uploaded profile picture
            Part profilePicPart = request.getPart("profile-pic");
            String profilePicFileName = profilePicPart.getSubmittedFileName();
            String profilePicSavePath = "C:\\Users\\bhagy\\myproject\\src\\main\\webapp\\StudentImages" + File.separator + profilePicFileName;
            try (InputStream fileContent = profilePicPart.getInputStream()) {
                Files.copy(fileContent, new File(profilePicSavePath).toPath(), StandardCopyOption.REPLACE_EXISTING);
            }

            // Insert data into the database
            String insertQuery = "INSERT INTO Student (profile_image_path, fname, mname, lname, dob, mailid, contactno, state, city, college, department,prn, passoutyear,identity, pwd, linkedinprofile,ctstate) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,1)";
            try (PreparedStatement pstmt = conn.prepareStatement(insertQuery)) 
            {
            	pstmt.setString(1, "StudentImages/" + profilePicFileName);
                pstmt.setString(2, firstName);
                pstmt.setString(3, middleName);
                pstmt.setString(4, lastName);
                pstmt.setString(5, dob);
                pstmt.setString(6, email);
                pstmt.setString(7, contactNo);
                pstmt.setString(8, state);
                pstmt.setString(9, city);
                pstmt.setString(10, "D.K.T.E");
                pstmt.setString(11, department);
                pstmt.setString(12, prn);
                pstmt.setString(13, passoutYear);
                pstmt.setString(14, "Student");
                pstmt.setString(15, "abcd");
                pstmt.setString(16, linkedinProfile);
                pstmt.executeUpdate();
            }

            response.sendRedirect("admin.html#add-student-content");
        } 
        catch (ClassNotFoundException | SQLException e) 
        {
            e.printStackTrace();
            response.sendRedirect("admin.html"); // Redirect to error page
        } 
        finally 
        {
            if (conn != null) 
            {
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
}