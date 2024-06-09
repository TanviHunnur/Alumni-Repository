package abc;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/alumniSearch")
public class alumniSearch extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/mydb";
    private static final String JDBC_USER = "root";
    private static final String JDBC_PASSWORD = "Aparna11/";

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        String listType = request.getParameter("listType");
        if (listType == null) {
            out.println("Invalid list type");
            return;
        }

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);

            String query;
            if (listType.equals("alumni")) 
            {
                String departmentParam = request.getParameter("department");
                String batchParam = request.getParameter("batch");
                String searchQuery = request.getParameter("searchQuery");

                // Initialize the SQL query
                query = "SELECT * FROM Alumni WHERE 1=1";

                // Add conditions based on parameters
                if (departmentParam != null && !departmentParam.isEmpty()) {
                    query += " AND department = ?";
                }
                if (batchParam != null && !batchParam.isEmpty()) {
                    query += " AND passoutyear = ?";
                }
                if (searchQuery != null && !searchQuery.isEmpty()) {
                    query += " AND (fname LIKE ? OR mname LIKE ? OR lname LIKE ?)";
                }

                try {
                    // Prepare the statement with the dynamic query
                    stmt = conn.prepareStatement(query);

                    // Set parameters based on conditions
                    int parameterIndex = 1;
                    if (departmentParam != null && !departmentParam.isEmpty()) {
                        stmt.setString(parameterIndex++, departmentParam);
                    }
                    if (batchParam != null && !batchParam.isEmpty()) {
                        stmt.setString(parameterIndex++, batchParam);
                    }
                    if (searchQuery != null && !searchQuery.isEmpty()) {
                        String searchParam = "%" + searchQuery + "%"; // Add wildcards for partial matching
                        stmt.setString(parameterIndex++, searchParam);
                        stmt.setString(parameterIndex++, searchParam);
                        stmt.setString(parameterIndex, searchParam);
                    }

                    rs = stmt.executeQuery();

                    out.println("<!DOCTYPE html>");
                    out.println("<html>");
                    out.println("<head>");
                    out.println("<title style='color:black;'>Alumni List</title>");
                    out.println("<style>");
                    out.println("body { background-color: black; color: white; }");
                    out.println("table { border-collapse: collapse; width: 100%; }");
                    out.println("th { border: 1px solid white; padding: 8px; color: white; }");
                    out.println("td { border: 1px solid black; padding: 8px; color: black; cursor: pointer; }");
                    out.println("th { background-color: black; }");
                    out.println(".editFormContainer { float: right; width: 30%; padding: 10px; background-color: #333; }");
                    out.println("</style>");
                    out.println("</head>");
                    out.println("<body>");
                    out.println("<h2 style='color:black;'>Alumni List</h2>");
                    out.println("<table>");
                    out.println("<tr><th>Name</th><th>Department</th><th>Batch</th><th>Profile</th></tr>");
                    while (rs.next()) 
                    {
                        String fullName = rs.getString("fname") + " " + rs.getString("mname") + " " + rs.getString("lname");
                        String department = rs.getString("department");
                        String passoutYear = rs.getString("passoutyear");
                        String profileLink = "<a href='AluminiDetailsServlet?mailid=" + rs.getString("mailid") + "'>Profile</a>";
                        out.println("<tr><td>" + fullName + "</td><td>" + department +  "</td><td>" + passoutYear + "</td><td>" + profileLink + "</td></tr>");
                    }
                    out.println("</table>");
                    out.println("</body>");
                    out.println("</html>");
                } catch (SQLException ex) {
                    out.println("Error: " + ex.getMessage());
                }
            } 
            else if (listType.equals("student")) 
            {
                String mailId = request.getParameter("mailid");
                // Initialize the SQL query
                query = "SELECT * FROM Student where mailid=?";
                stmt = conn.prepareStatement(query);
                stmt.setString(1, mailId);

                // Execute query
                rs = stmt.executeQuery();
                if (rs.next()) {
                    String fullName = rs.getString("fname") + " " + rs.getString("mname") + " " + rs.getString("lname");
                    String dob = new SimpleDateFormat("yyyy-MM-dd").format(rs.getDate("dob"));
                    String state = rs.getString("state");
                    String city = rs.getString("city");
                    String department = rs.getString("department");
                    String passoutYear = rs.getString("passoutyear");
                    String linkedinProfile = rs.getString("linkedinprofile");

                    out.println("profile");

                    out.println("<!DOCTYPE html>");
                    out.println("<html lang=\"en\">");
                    out.println("<head>");
                    out.println("<meta charset=\"UTF-8\">");
                    out.println("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">");
                    out.println("<title>User Profile</title>");
                    out.println("</head>");
                    out.println("<body>");
                    out.println("<h1>User Profile</h1>");
                    out.println("<div><label>Full Name:</label><span>" + fullName + "</span></div>");
                    out.println("<div><label>Date of Birth:</label><span>" + dob + "</span></div>");
                    out.println("<div><label>State:</label><span>" + state + "</span></div>");
                    out.println("<div><label>City:</label><span>" + city + "</span></div>");
                    out.println("<div><label>Department:</label><span>" + department + "</span></div>");
                    out.println("<div><label>Passout Year:</label><span>" + passoutYear + "</span></div>");
                    out.println("<div><label>LinkedIn Profile:</label><span>" + linkedinProfile + "</span></div>");
                    out.println("</body>");
                    out.println("</html>");
                } else {
                    out.println("<h1>No student found with the provided email ID.</h1>");
                }
            }
        } catch (SQLException | ClassNotFoundException ex) {
            out.println("Error: " + ex.getMessage());
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (stmt != null)
                    stmt.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                out.println("Error closing resources: " + ex.getMessage());
            }
        }
    }
}
