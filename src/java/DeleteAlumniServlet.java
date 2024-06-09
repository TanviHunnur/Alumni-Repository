package abc;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/DeleteAlumniServlet")
public class DeleteAlumniServlet extends HttpServlet 
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// JDBC URL, username, and password of MySQL server
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/mydb";
    private static final String JDBC_USER = "root";
    private static final String JDBC_PASSWORD = "Aparna11/";

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Get the mailid parameter from the request
        String mailId = request.getParameter("mailId");
        
        // Database connection and deletion logic
        Connection conn = null;
        PreparedStatement stmt = null;
        PrintWriter out = response.getWriter();


        try 
        {
            // Register JDBC driver
            Class.forName("com.mysql.jdbc.Driver");

            // Open a connection
            conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);

            // SQL query to delete a student by mailid
            String query = "DELETE FROM Alumni WHERE mailid = ? AND ctstate=1";
            stmt = conn.prepareStatement(query);
            stmt.setString(1, mailId);
            //out.println("mail id:"+mailId);

            // Execute the update statement
            int rowsDeleted = stmt.executeUpdate();

            // Send a response back to the client
            response.setContentType("text/html");
            out.println("<html><body>");
            if (rowsDeleted > 0) 
            {
                response.sendRedirect("admin.html#alumni-list-content");
            }
            else 
            {
            	response.sendRedirect("admin.html");
            }
            out.println("</body></html>");
        }
        catch (ClassNotFoundException | SQLException e) 
        {
            e.printStackTrace();
            out.println("<h1>Error Deleting Alumni</h1>");
            out.println("<p>An error occurred while deleting the Alumni.</p>");
        } 
        finally 
        {
            // Close resources
            try 
            {
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } 
            catch (SQLException e) 
            {
                e.printStackTrace();
            }
        }
    }
}