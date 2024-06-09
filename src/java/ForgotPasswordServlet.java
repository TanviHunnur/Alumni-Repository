package abc;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/ForgotPasswordServlet")
public class ForgotPasswordServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final String DB_URL = "jdbc:mysql://localhost:3306/mydb";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Aparna11/";

    private static final Map<String, String> rolesTableMap = new HashMap<>();

    static 
    {
        rolesTableMap.put("Student", "Student");
        rolesTableMap.put("Alumni", "Alumni");
        rolesTableMap.put("Faculty", "Faculty");
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String role = request.getParameter("role");
        String email = request.getParameter("email");

        if (role != null && email != null && isValidEmail(email)) {
            String password = getPasswordFromDatabase(role, email);

            if (password != null) 
            {
                sendPasswordByEmail(email, password);

                response.setContentType("text/html");
                PrintWriter out = response.getWriter();
                out.println("<html><body>");
                out.println("Password reset instructions sent to your email.");
                out.println("</body></html>");
            } 
            else 
            {
                response.setContentType("text/html");
                PrintWriter out = response.getWriter();
                out.println("<html><body>");
                out.println("Password not found for the provided email and role.");
                out.println("</body></html>");
            }
        } else {
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();
            out.println("<html><body>");
            out.println("Invalid input. Please provide valid role and email.");
            out.println("</body></html>");
        }
    }

    private boolean isValidEmail(String email) 
    {
        return email != null && email.matches("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}");
    }

    private String getPasswordFromDatabase(String role, String email) 
    {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String password = null;

        try 
        {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            String tableName = rolesTableMap.get(role);
            if (tableName != null) 
            {
                String sql = "SELECT password FROM " + tableName + " WHERE mailid = ?";
                stmt = conn.prepareStatement(sql);
                stmt.setString(1, email);
                rs = stmt.executeQuery();

                if (rs.next()) 
                {
                    password = rs.getString("pwd");
                }
            }
        } 
        catch (SQLException e) 
        {
            e.printStackTrace(); // Handle or log the exception
        } 
        finally 
        {
            try 
            {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } 
            catch (SQLException e) 
            {
                e.printStackTrace();
            }
        }

        return password;
    }

    private void sendPasswordByEmail(String email, String password) 
    {
        final String username = "hijk8445@gmail.com"; // Update with your Gmail email
        final String passwordEmail = "abcd#1919"; // Update with your Gmail password

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, passwordEmail);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
            message.setSubject("Password Reset");
            message.setText("Dear User,\n\nYour password is: " + password);

            // Attempt to send the email
            Transport.send(message);

            System.out.println("Password reset email sent to " + email);
        } catch (MessagingException e) {
            // Print the stack trace or log the exception
            e.printStackTrace();
            throw new RuntimeException(e); // Rethrow the exception or handle as needed
        }
    }
}
