package abc;

import java.io.IOException;
import java.io.PrintWriter;
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
@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet 
{
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException 
    {
        PrintWriter out = response.getWriter();
        String username = request.getParameter("email");
        String password = request.getParameter("password");
        String userType = request.getParameter("role");
        
        
        // Check against Student table
        if (checkLogin("Student", username, password,userType)) 
        {
            // Forward to student page
            RequestDispatcher rd = request.getRequestDispatcher("search.html");
            rd.forward(request, response);
        } 
        // Check against Faculty table
        else if (checkLogin("Faculty", username, password,userType)) 
        {
            // Forward to faculty page
            RequestDispatcher rd = request.getRequestDispatcher("search.html");
            rd.forward(request, response);
        } 
        // Check against Alumni table
        else if (checkLogin("Alumni", username, password,userType)) 
        {
            // Forward to alumni page
            RequestDispatcher rd = request.getRequestDispatcher("search.html");
            rd.forward(request, response);
        }
        else if (checkLogin("Admin", username, password,userType)) 
        {
            // Forward to alumni page
            RequestDispatcher rd = request.getRequestDispatcher("admin.html");
            rd.forward(request, response);
        } 
        else 
        {
        
            // Invalid credentials, forward back to login page with an error message
            //RequestDispatcher rd = request.getRequestDispatcher("loginsam.html");
            out.println("<script>alert('Invalid username or password');</script>");
            RequestDispatcher rd = request.getRequestDispatcher("loginsam.html");
            rd.include(request, response);
        
        }
        
        // Check against Student table
        /*switch(userType)
        {
        case "Student":
        	if(checkLogin("Student", username, password, userType))
            {
                // Forward to student page
                RequestDispatcher rd = request.getRequestDispatcher("search.html");
                rd.forward(request, response);
            } 
        	break;
        case "Alumni":
        	if(checkLogin("Alumni", username, password,userType))
            {
                // Forward to alumni page
                RequestDispatcher rd = request.getRequestDispatcher("search.html");
                rd.forward(request, response);
            }
        	break;
        case "Admin":
        	if(checkLogin("Admin", username, password,userType))
        	{
        		// Forward to faculty page
        		RequestDispatcher rd = request.getRequestDispatcher("admin.html");
            	rd.forward(request, response);
        	}
        	break;
        case "Faculty":
        	if(checkLogin("Faculty", username, password,userType))
        	{
        		// Forward to faculty page
        		RequestDispatcher rd = request.getRequestDispatcher("search.html");
            	rd.forward(request, response);
        	}
        	break;
        default:
        	{
        		// Invalid credentials, forward back to login page with an error message
        		RequestDispatcher rd = request.getRequestDispatcher("loginsam.html");
                out.println("<font color=red>Invalid username or password</font>");
                rd.include(request, response);
        	}
        	break;
        }*/ 
        
    }

    private boolean checkLogin(String tableName, String username, String password,String userType) 
    {
        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/mydb", "root", "Aparna11/")) {
            String query = "SELECT mailid FROM " + tableName + " WHERE mailid=? AND pwd=? AND identity=? AND ctstate=1";
            try (PreparedStatement ps = con.prepareStatement(query)) 
            {
                ps.setString(1, username);
                ps.setString(2, password);
                ps.setString(3, userType);
                try (ResultSet rs = ps.executeQuery()) 
                {
                    return rs.next();
                }
            }
        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
            return false;
        }
    }
}