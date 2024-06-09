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

@WebServlet("/EditAlumniServlet")
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 2, // 2MB
        maxFileSize = 1024 * 1024 * 10,      // 10MB
        maxRequestSize = 1024 * 1024 * 50)   // 50MB
public class EditAlumniServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static final String dbURL = "jdbc:mysql://localhost:3306/mydb";
    private static final String dbUser = "root";
    private static final String dbPassword = "Aparna11/";

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Connection conn = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(dbURL, dbUser, dbPassword);

            // Retrieve form data
            String mailId = request.getParameter("mailId1");
            String firstName = request.getParameter("firstName1");
            String middleName = request.getParameter("middleName1");
            String lastName = request.getParameter("lastName1");
            String dob = request.getParameter("dob1");
            String contactNo = request.getParameter("contactNo1");
            String state = request.getParameter("State1");
            String city = request.getParameter("City1");
            String department = request.getParameter("department1");
            String passoutYear = request.getParameter("passout-year1");
            String currentWorkingField = request.getParameter("current-working-field1");
            String brief = request.getParameter("brief1");
            String linkedinProfile = request.getParameter("linkedin-profile1");

            Part profilePicPart = request.getPart("existingProfilePath1");
            String profilePicFileName = null;
            if (profilePicPart != null && profilePicPart.getSize() > 0) {
                profilePicFileName = profilePicPart.getSubmittedFileName();
                String profilePicSavePath = "C:\\Users\\bhagy\\myproject\\src\\main\\webapp\\AlumniImages" + File.separator + profilePicFileName;
                try (InputStream fileContent = profilePicPart.getInputStream()) {
                    Files.copy(fileContent, new File(profilePicSavePath).toPath(), StandardCopyOption.REPLACE_EXISTING);
                }
            }

            // Fetch existing profile picture path from database if no new file uploaded
            String existingProfilePicPath = null;
            if (profilePicFileName == null) {
                existingProfilePicPath = getExistingProfilePicPath(conn, mailId);
            }

            // Process uploaded resumes
            String resume1FileName = handleFileUpload(request.getPart("existingResume1Path1"), "Resume1pdf");
            String resume2FileName = handleFileUpload(request.getPart("existingResume2Path1"), "Resume2pdf");

            // Fetch existing resume paths from database if no new file uploaded
            if (resume1FileName == null) {
                resume1FileName = getExistingResume1Path1(conn, mailId);
            }
            if (resume2FileName == null) {
                resume2FileName = getExistingResume2Path1(conn, mailId);
            }

            // Insert data into the database
            String insertQuery = "UPDATE Alumni SET profile_image_path=?, fname=?, mname=?, lname=?, dob=?, contactno=?, state=?, city=?, department=?, passoutyear=?, currentworkingfield=?, breif=?, linkedinprofile=?, resume1_image_path=?, resume2_image_path=? WHERE mailid=?";
            try (PreparedStatement pstmt = conn.prepareStatement(insertQuery)) {
                pstmt.setString(1, profilePicFileName != null ? "AlumniImages/" + profilePicFileName : existingProfilePicPath);
                pstmt.setString(2, firstName);
                pstmt.setString(3, middleName);
                pstmt.setString(4, lastName);
                pstmt.setString(5, dob);
                pstmt.setString(6, contactNo);
                pstmt.setString(7, state);
                pstmt.setString(8, city);
                pstmt.setString(9, department);
                pstmt.setString(10, passoutYear);
                pstmt.setString(11, currentWorkingField);
                pstmt.setString(12, brief);
                pstmt.setString(13, linkedinProfile);
                pstmt.setString(14, resume1FileName);
                pstmt.setString(15, resume2FileName);
                pstmt.setString(16, mailId);
                pstmt.executeUpdate();
            }

            response.sendRedirect("admin.html#alumni-list-content"); // Redirect to success page
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

    private String getExistingProfilePicPath(Connection conn, String mailId) throws SQLException {
        String query = "SELECT profile_image_path FROM Alumni WHERE mailid=?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, mailId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("profile_image_path");
            }
        }
        return null;
    }

    private String handleFileUpload(Part part, String saveDir) throws IOException {
        if (part != null && part.getSize() > 0) {
            String fileName = part.getSubmittedFileName();
            String savePath = "C:\\Users\\bhagy\\myproject\\src\\main\\webapp\\" + saveDir + File.separator + fileName;
            try (InputStream fileContent = part.getInputStream()) {
                Files.copy(fileContent, new File(savePath).toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
            return saveDir + "/" + fileName;
        }
        return null; // Return null if no file uploaded
    }

    private String getExistingResume1Path1(Connection conn, String mailId) throws SQLException {
        String query = "SELECT resume1_image_path FROM Alumni WHERE mailid=?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, mailId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("resume1_image_path");
            }
        }
        return null;
    }

    private String getExistingResume2Path1(Connection conn, String mailId) throws SQLException {
        String query = "SELECT resume2_image_path FROM Alumni WHERE mailid=?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, mailId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("resume2_image_path");
            }
        }
        return null;
    }
}
