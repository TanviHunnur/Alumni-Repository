package abc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Random;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

@WebServlet("/BulkUploadAlumniServlet")
@MultipartConfig
public class BulkUploadAlumniServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Part filePart = request.getPart("csv-file2");

        // Extracting file information
        String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
        InputStream fileContent = filePart.getInputStream();

        // Process the CSV file
        try (InputStreamReader isr = new InputStreamReader(fileContent, StandardCharsets.UTF_8);
             BufferedReader br = new BufferedReader(isr)) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(","); // Assuming CSV format with comma delimiter

                // Validate data length
                if (data.length < 13) {
                    // Handle invalid CSV format
                    response.getWriter().println("Error: Invalid data format in CSV.");
                    continue; // Skip this line
                }

                // Extract fields from CSV data
                String fname = data[0];
                String mname = data[1];
                String lname = data[2];
                String dobString = data[3]; // Correct the index for dobString
                String mailid = data[4];
                String contactno = data[5];
                String state = data[6];
                String city = data[7];
                String department = data[8];
                String passoutyear = data[9]; // Correct the index for passoutyear
                String CurrentWorkingField = data[10]; // Correct the index for CurrentWorkingField
                String breif = data[11]; // Correct the index for breif
                String linkedinprofile = data[12]; // Correct the index for linkedinprofile

                // Convert dobString to LocalDate
                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                LocalDate dob = LocalDate.parse(dobString, dateFormatter);

                // Set default profile image path and generate random password
                String identity = "Alumni";
                String college = "D.K.T.E";
                String profileImagePath = "PhotoNotUploaded"; // Default profile image path
                String Resume1ImagePath = "Resume1NotUploaded"; // Default resume image path
                String Resume2ImagePath = "Resume2NotUploaded"; // Default resume image path
                String pwd = generateRandomPassword(8); // Generate an 8-character random password

                // Insert data into the database
                insertIntoDatabase(profileImagePath, fname, mname, lname, dob, mailid, contactno, state, city, college, department, passoutyear, CurrentWorkingField, breif, linkedinprofile, Resume1ImagePath, Resume2ImagePath, identity, pwd, response);
            }

            response.getWriter().println("File uploaded and data inserted into database successfully.");
            //response.sendRedirect("admin.html#bulk-upload-content");
        } catch (IOException | SQLException e) {
            e.printStackTrace();
            response.getWriter().println("Error processing CSV file: " + e.getMessage());
        }
    }

    private void insertIntoDatabase(String profileImagePath, String fname, String mname, String lname, LocalDate dob, String mailid, String contactno, String state, String city, String college, String department, String passoutyear, String CurrentWorkingField, String breif, String linkedinprofile, String Resume1ImagePath, String Resume2ImagePath, String identity, String pwd, HttpServletResponse response) throws SQLException, IOException {
        String dbURL = "jdbc:mysql://localhost:3306/mydb";
        String dbUser = "root";
        String dbPassword = "Aparna11/";

        try (Connection conn = DriverManager.getConnection(dbURL, dbUser, dbPassword)) {
            String insertQuery = "INSERT INTO Alumni (profile_image_path, fname, mname, lname, dob, mailid, contactno, state, city, college, department, passoutyear, currentworkingfield, breif, linkedinprofile, resume1_image_path, resume2_image_path, identity, pwd, ctstate) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 1)";
            try (PreparedStatement pstmt = conn.prepareStatement(insertQuery)) {
                pstmt.setString(1, profileImagePath);
                pstmt.setString(2, fname);
                pstmt.setString(3, mname);
                pstmt.setString(4, lname);
                pstmt.setDate(5, java.sql.Date.valueOf(dob)); // Convert LocalDate to java.sql.Date
                pstmt.setString(6, mailid);
                pstmt.setString(7, contactno);
                pstmt.setString(8, state);
                pstmt.setString(9, city);
                pstmt.setString(10, college); // Add college parameter
                pstmt.setString(11, department);
                pstmt.setString(12, passoutyear);
                pstmt.setString(13, CurrentWorkingField);
                pstmt.setString(14, breif);
                pstmt.setString(15, linkedinprofile);
                pstmt.setString(16, Resume1ImagePath); // Add Resume1ImagePath parameter
                pstmt.setString(17, Resume2ImagePath); // Add Resume2ImagePath parameter
                pstmt.setString(18, identity);
                pstmt.setString(19, pwd);

                pstmt.executeUpdate(); // Execute the INSERT statement
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.getWriter().println("Error inserting data into the database.");
            throw e; // Rethrow the exception for handling in the calling method
        }
    }

    private String generateRandomPassword(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder randomPassword = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            randomPassword.append(characters.charAt(random.nextInt(characters.length())));
        }
        return randomPassword.toString();
    }
}
