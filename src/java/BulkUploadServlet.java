package abc;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.Random;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

@WebServlet("/BulkUploadServlet")
@MultipartConfig
public class BulkUploadServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Part filePart = request.getPart("csv-file");

        // Extracting file information
        String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
        InputStream fileContent = filePart.getInputStream();

        // Process the CSV file
        try (BufferedReader br = new BufferedReader(new InputStreamReader(fileContent))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(","); // Assuming CSV format with comma delimiter

                // Extract fields from CSV data
                String fname = data[0];
                String mname = data[1];
                String lname = data[2];
                String mailid = data[3];
                String contactno = data[4];
                String state = data[5];
                String city = data[6];
                String department = data[7];
                String identity = data[8];

                // Set default profile image path and generate random password
                String profileImagePath = "PhotoNotUploaded"; // Default profile image path
                String pwd = generateRandomPassword(8); // Generate an 8-character random password

                // Insert data into the database
                insertIntoDatabase(profileImagePath, fname, mname, lname, mailid, contactno, state, city, department, identity, pwd);
            }

            response.getWriter().println("File uploaded and data inserted into database successfully.");
            //response.sendRedirect("admin.html#bulk-upload-content");
        } catch (Exception e) {
            response.getWriter().println("Error processing CSV file: " + e.getMessage());
        }
    }

    private void insertIntoDatabase(String profileImagePath, String fname, String mname, String lname, String mailid, String contactno, String state, String city, String department, String identity, String pwd) {
        String dbURL = "jdbc:mysql://localhost:3306/mydb";
        String dbUser = "root";
        String dbPassword = "Aparna11/";

        try (Connection conn = DriverManager.getConnection(dbURL, dbUser, dbPassword)) {
            String insertQuery = "INSERT INTO Faculty (profile_image_path, fname, mname, lname, mailid, contactno, state, city, department, identity, pwd, ctstate) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 1)";
            try (PreparedStatement pstmt = conn.prepareStatement(insertQuery)) {
                /*if (profileImagePath.isBlank()) {
                    createProfileUpdateNotification(mailid); // Create notification file
                }*/
                pstmt.setString(1, profileImagePath);
                pstmt.setString(2, fname);
                pstmt.setString(3, mname);
                pstmt.setString(4, lname);
                pstmt.setString(5, mailid);
                pstmt.setString(6, contactno);
                pstmt.setString(7, state);
                pstmt.setString(8, city);
                pstmt.setString(9, department);
                pstmt.setString(10, identity);
                pstmt.setString(11, pwd);

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

    /*private void createProfileUpdateNotification(String email) {
        String notificationDirectory = "path/to/notification/directory";
        try (FileWriter fileWriter = new FileWriter(notificationDirectory + "/" + email + ".txt")) {
            fileWriter.write("Dear User,\n\nPlease update your profile information including the profile picture.\n");
            System.out.println("Notification file created for " + email);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/
}
