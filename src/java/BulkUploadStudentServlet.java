package abc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
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

@WebServlet("/BulkUploadStudentServlet")
@MultipartConfig
public class BulkUploadStudentServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Part filePart = request.getPart("csv-file1");

        // Extracting file information
        String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
        InputStream fileContent = filePart.getInputStream();

        // Process the CSV file
        try (BufferedReader br = new BufferedReader(new InputStreamReader(fileContent))) {
            String line;
            while ((line = br.readLine()) != null) 
            {
                String[] data = line.split(","); // Assuming CSV format with comma delimiter

                // Extract fields from CSV data
                String fname = data[0];
                String mname = data[1];
                String lname = data[2];
                String dobString = data[3]; // Assuming dob is in the fourth column
                String mailid = data[4];
                String contactno = data[5];
                String state = data[6];
                String city = data[7];
                String department = data[8];
                String prn = data[9];
                String passoutyear = data[10];
                String linkedinprofile = data[11];

                // Set default profile image path and generate random password
                String identity ="Student";
                String college = "D.K.T.E";
                String profileImagePath = "PhotoNotUploaded"; // Default profile image path
                String pwd = generateRandomPassword(8); // Generate an 8-character random password

                // Convert dobString to LocalDate
                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                LocalDate dob = LocalDate.parse(dobString, dateFormatter);
                // Insert data into the database
                insertIntoDatabase(profileImagePath, fname, mname, lname, dob, mailid, contactno, state, city, college, department, prn, passoutyear, identity, pwd, linkedinprofile);
            }

            response.getWriter().println("File uploaded and data inserted into database successfully.");
            //response.sendRedirect("admin.html#bulk-upload-content");
        } catch (Exception e) {
            response.getWriter().println("Error processing CSV file: " + e.getMessage());
        }
    }

    private void insertIntoDatabase(String profileImagePath, String fname, String mname, String lname, LocalDate dob, String mailid, String contactno, String state, String city, String college, String department, String prn, String passoutyear, String identity, String pwd, String linkedinprofile) 
    {
        String dbURL = "jdbc:mysql://localhost:3306/mydb";
        String dbUser = "root";
        String dbPassword = "Aparna11/";

        try (Connection conn = DriverManager.getConnection(dbURL, dbUser, dbPassword)) {
            String insertQuery = "INSERT INTO Student (profile_image_path, fname, mname, lname, dob, mailid, contactno, state, city, college, department, prn, passoutyear, identity, pwd, linkedinprofile, ctstate) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 1)";
            try (PreparedStatement pstmt = conn.prepareStatement(insertQuery)) 
            {
                pstmt.setString(1, profileImagePath);
                pstmt.setString(2, fname);
                pstmt.setString(3, mname);
                pstmt.setString(4, lname);
                pstmt.setDate(5, java.sql.Date.valueOf(dob)); // Convert LocalDate to java.sql.Date
                pstmt.setString(6, mailid);
                pstmt.setString(7, contactno);
                pstmt.setString(8, state);
                pstmt.setString(9, city);
                pstmt.setString(10, college);
                pstmt.setString(11, department);
                pstmt.setString(12, prn);
                pstmt.setString(13, passoutyear);
                pstmt.setString(14, identity);
                pstmt.setString(15, pwd);
                pstmt.setString(16, linkedinprofile);

                pstmt.executeUpdate(); // Execute the INSERT statement
            }
        } catch (Exception e) {
            e.printStackTrace();
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
