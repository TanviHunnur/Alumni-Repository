package abc;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/AlumniListServlet")
public class AlumniListServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/mydb";
    private static final String JDBC_USER = "root";
    private static final String JDBC_PASSWORD = "Aparna11/";

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        String listType = request.getParameter("listType");
        if (listType == null) 
        {
            out.println("Invalid list type");
            return;
        }

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try 
        {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);

            String query;
            if (listType.equals("faculty")) 
            {
            	String searchQuery = request.getParameter("searchQuery");
                if (searchQuery != null && !searchQuery.isEmpty()) {
                    // Modify the query to include search functionality
                    query = "SELECT * FROM Faculty WHERE fname LIKE ?";
                    stmt = conn.prepareStatement(query);
                    stmt.setString(1, "%" + searchQuery + "%"); // Set the search parameter
                } else {
                    // Original query to fetch all faculty records
                    query = "SELECT * FROM Faculty";
                    stmt = conn.prepareStatement(query);
                }                rs = stmt.executeQuery();

                out.println("<!DOCTYPE html>");
                out.println("<html>");
                out.println("<head>");
                out.println("<title>" + capitalizeFirstLetter(listType) + " List</title>");
                out.println("<style>");
                out.println("body { background-color: black; color: white; }");
                out.println("table { border-collapse: collapse; width: 100%; }");
                out.println("th { border: 1px solid black; padding: 8px; color: white; }");
                out.println("td { border: 1px solid black; padding: 8px; color: black; cursor: pointer; }");
                out.println("th { background-color: black; }");
                out.println(".editFormContainer { float: right; width: 30%; padding: 10px; background-color: #333; }");
                out.println("</style>");
                out.println("</head>");
                out.println("<body>");
                out.println("<h2>" + capitalizeFirstLetter(listType) + " List</h2>");
                out.println("<table>");
                out.println("<tr><th>Name</th><th>Department</th><th>Profile</th><th>Edit</th><th>Delete</th></tr>");
                while (rs.next()) 
                {
                	String firstName = rs.getString("fname");
                    String middleName = rs.getString("mname");
                    String lastName = rs.getString("lname");
                    String state = rs.getString("state");
                	String city = rs.getString("city");
                	String department = rs.getString("department");
                	String profilePicPath = rs.getString("profile_image_path");
                	String fullName = firstName + " " + middleName + " " + lastName;


                    // Generate edit and delete symbols as links
                    String editLink = "<a href='javascript:void(0);' onclick='displayFacultyEditForm(\"" +
                            rs.getString("mailid") + "\", \"" + firstName + "\", \"" + middleName + "\", \"" +
                            lastName + "\",\"" + state + "\", \"" + city + "\", \"" + department + "\", \"" +
                            profilePicPath + "\",)'>Edit</a>";

                    // Generate profile link to AlumniDetailServlet
                    String profileLink = "<a href='FacultyDetailsServlet?mailid=" + rs.getString("mailid") + "'>Profile</a>";

                    String deleteLink = "<a href='javascript:void(0);' onclick='deleteFaculty(\"" +
                    	    rs.getString("mailid") + "\", \"" + rs.getString("fname") + "\", \"" +
                    	    rs.getString("mname") + "\", \"" + rs.getString("lname") + "\", \"" +
                    	    rs.getString("department") + "\")'>Delete</a>";

                    
                    // Output table row with edit, delete, and profile links
                    out.println("<tr>");
                    out.println("<td>" + fullName + "</td><td>" + department + "</td><td>" + profileLink + "</td><td>"
                            + editLink + "</td><td>" + deleteLink + "</td></tr>");
                }
                out.println("</table>");

                out.println("</body>");
                out.println("</html>");
            } 
            else if (listType.equals("student")) 
            {
                String departmentParam = request.getParameter("department");
                String batchParam = request.getParameter("batch");
                String searchQuery = request.getParameter("searchQuery");
                
                // Initialize the SQL query
                query = "SELECT * FROM Student WHERE 1=1";

                // Add conditions based on parameters
                if (departmentParam != null && !departmentParam.isEmpty()) {
                    query += " AND department = ?";
                    //out.println("depart");

                }
                if (batchParam != null && !batchParam.isEmpty()) {
                    query += " AND passoutyear = ?";
                    //out.println("batch");

                }
                if (searchQuery != null && !searchQuery.isEmpty()) {
                    query += " AND (fname LIKE ? OR mname LIKE ? OR lname LIKE ?)";
                    //out.println("name");

                }

                try 
                {
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

                    // Execute the query
                    rs = stmt.executeQuery();

                    // Generate HTML response
                    out.println("<!DOCTYPE html>");
                    out.println("<html>");
                    out.println("<head>");
                    out.println("<title>" + capitalizeFirstLetter(listType) + " List</title>");
                    out.println("<style>");
                    out.println("body { background-color: black; color: white; }");
                    out.println("table { border-collapse: collapse; width: 100%; }");
                    out.println("th { border: 1px solid black; padding: 8px; color: white; }");
                    out.println("td { border: 1px solid black; padding: 8px; color: black; cursor: pointer; }");
                    out.println("th { background-color: black; }");
                    out.println(".editFormContainer { float: right; width: 30%; padding: 10px; background-color: #333; }");
                    out.println("</style>");
                    out.println("</head>");
                    out.println("<body>");
                    out.println("<h2>" + capitalizeFirstLetter(listType) + " List</h2>");
                    out.println("<table>");
                    out.println("<tr><th>Name</th><th>Department</th><th>Batch</th><th>Profile</th><th>Edit</th><th>Delete</th></tr>");

                    while (rs.next()) 
                    {
                        String firstName = rs.getString("fname");
                        String middleName = rs.getString("mname");
                        String lastName = rs.getString("lname");
                        Date dob = rs.getDate("dob");
                        String contactNo = rs.getString("contactno");
                        String state = rs.getString("state");
                        String city = rs.getString("city");
                        String department = rs.getString("department");
                        String prn = rs.getString("prn");
                        String passoutYear = rs.getString("passoutyear");
                        String linkedinProfile = rs.getString("linkedinprofile");

                        String fullName = firstName + " " + middleName + " " + lastName;

                        // Generate edit and delete symbols as links
                        String editLink = "<a href='javascript:void(0);' onclick='displayStudentEditForm(\"" +
                                rs.getString("mailid") + "\", \"" + firstName + "\", \"" + middleName + "\", \"" +
                                lastName + "\", \"" + dob.toString() + "\",\"" + contactNo + "\", \"" + state + "\", \"" +
                                city + "\", \"" + department + "\", \"" + prn + "\",\"" + passoutYear + "\", \"" + linkedinProfile + "\", \"" +
                                rs.getString("profile_image_path") + "\")'>Edit</a>";

                        String deleteLink = "<a href='javascript:void(0);' onclick='deleteStudent(\"" +
                                rs.getString("mailid") + "\", \"" + rs.getString("fname") + "\", \"" +
                                rs.getString("mname") + "\", \"" + rs.getString("lname") + "\", \"" +
                                rs.getString("department") + "\")'>Delete</a>";

                        // Generate profile link to StudentDetailsServlet
                        String profileLink = "<a href='StudentDetailsServlet?mailid=" + rs.getString("mailid") + "'>Profile</a>";

                        // Output table row with edit, delete, and profile links
                        out.println("<tr>");
                        out.println("<td>" + fullName + "</td><td>" + department +"</td><td>" + passoutYear + "</td><td>" + profileLink + "</td><td>"
                                + editLink + "</td><td>" + deleteLink + "</td></tr>");
                    }

                    out.println("</table>");
                    out.println("</body>");
                    out.println("</html>");

                    rs.close();
                    stmt.close();
                } 
                catch (SQLException ex) 
                {
                    out.println("Error: " + ex.getMessage());
                }
            }
            else if (listType.equals("alumni")) 
            {
            	String departmentParam = request.getParameter("department");
                String batchParam = request.getParameter("batch");
                String searchQuery = request.getParameter("searchQuery");
                
                // Initialize the SQL query
                query = "SELECT * FROM ALumni WHERE 1=1";

                // Add conditions based on parameters
                if (departmentParam != null && !departmentParam.isEmpty()) {
                    query += " AND department = ?";
                    //out.println("depart");

                }
                if (batchParam != null && !batchParam.isEmpty()) {
                    query += " AND passoutyear = ?";
                    //out.println("batch");

                }
                if (searchQuery != null && !searchQuery.isEmpty()) {
                    query += " AND (fname LIKE ? OR mname LIKE ? OR lname LIKE ?)";
                    //out.println("name");

                }

                try 
                {
                    // Prepare the statement with the dynamic query
                    stmt = conn.prepareStatement(query);

                    // Set parameters based on conditions
                    int parameterIndex = 1;
                    if (departmentParam != null && !departmentParam.isEmpty()) 
                    {
                        stmt.setString(parameterIndex++, departmentParam);
                    }
                    if (batchParam != null && !batchParam.isEmpty()) 
                    {
                        stmt.setString(parameterIndex++, batchParam);
                    }
                    if (searchQuery != null && !searchQuery.isEmpty()) 
                    {
                        String searchParam = "%" + searchQuery + "%"; // Add wildcards for partial matching
                        stmt.setString(parameterIndex++, searchParam);
                        stmt.setString(parameterIndex++, searchParam);
                        stmt.setString(parameterIndex, searchParam);
                    }

                    rs = stmt.executeQuery();

                    out.println("<!DOCTYPE html>");
                    out.println("<html>");
                    out.println("<head>");
                    out.println("<title>" + capitalizeFirstLetter(listType) + " List</title>");
                    out.println("<style>");
                    out.println("body { background-color: black; color: white; }");
                    out.println("table { border-collapse: collapse; width: 100%; }");
                    out.println("th { border: 1px solid black; padding: 8px; color: white; }");
                    out.println("td { border: 1px solid black; padding: 8px; color: black; cursor: pointer; }");
                    out.println("th { background-color: black; }");
                    out.println(".editFormContainer { float: right; width: 30%; padding: 10px; background-color: #333; }");
                    out.println("</style>");
                    out.println("</head>");
                    out.println("<body>");
                    out.println("<h2>" + capitalizeFirstLetter(listType) + " List</h2>");
                    out.println("<table>");
                    out.println("<tr><th>Name</th><th>Department</th><th>Batch</th><th>Profile</th><th>Edit</th><th>Delete</th></tr>");
                    while (rs.next()) 
                    {
                    String firstName = rs.getString("fname");
                    String middleName = rs.getString("mname");
                    String lastName = rs.getString("lname");
                    Date dob = rs.getDate("dob");
                    String contactNo = rs.getString("contactno");
                    String state = rs.getString("state");
                	String city = rs.getString("city");
                	String department = rs.getString("department");
                	String passoutYear = rs.getString("passoutyear");
                	String currentWorkingField = rs.getString("currentworkingfield");
                	String brief = rs.getString("breif");
                	String linkedinProfile = rs.getString("linkedinprofile");
                	String profilePicPath = rs.getString("profile_image_path");
                	String resume1Path = rs.getString("resume1_image_path");
                	String resume2Path = rs.getString("resume2_image_path");
                    String fullName = firstName + " " + middleName + " " + lastName;


                    // Generate edit and delete symbols as links
                    String editLink = "<a href='javascript:void(0);' onclick='displayEditForm(\"" +
                            rs.getString("mailid") + "\", \"" + firstName + "\", \"" + middleName + "\", \"" +
                            lastName + "\", \"" + dob.toString() + "\",\"" + contactNo + "\" ,\"" + state + "\", \"" +
                            city + "\", \""+department+"\",\"" + passoutYear + "\", \"" + currentWorkingField + "\", \"" +
                            brief + "\", \"" + linkedinProfile + "\", \"" +
                            profilePicPath + "\", \"" + resume1Path + "\", \"" +
                            resume2Path + "\")'>Edit</a>";
                    
                    String deleteLink = "<a href='javascript:void(0);' onclick='deleteAlumni(\"" +
                    	    rs.getString("mailid") + "\", \"" + rs.getString("fname") + "\", \"" +
                    	    rs.getString("mname") + "\", \"" + rs.getString("lname") + "\", \"" +
                    	    rs.getString("department") + "\")'>Delete</a>";
                    
                    
                    // Generate profile link to AlumniDetailServlet
                    String profileLink = "<a href='AluminiDetailsServlet?mailid=" + rs.getString("mailid") + "'>Profile</a>";

                    // Output table row with edit, delete, and profile links
                    out.println("<tr>");
                    out.println("<td>" + fullName + "</td><td>" + department+ "</td><td>" + passoutYear + "</td><td>" + profileLink + "</td><td>"
                            + editLink + "</td><td>" + deleteLink + "</td></tr>");
                }
                out.println("</table>");

                out.println("</body>");
                out.println("</html>");
                }
                catch (SQLException ex) 
                {
                    out.println("Error: " + ex.getMessage());
                }
            }
            else 
            {
                out.println("Invalid list type");
                return;
            }

        } 
        catch (SQLException | ClassNotFoundException ex) 
        {
            out.println("Error: " + ex.getMessage());
        } 
        finally 
        {
            try 
            {
                if (rs != null)
                    rs.close();
                if (stmt != null)
                    stmt.close();
                if (conn != null)
                    conn.close();
            }
            catch (SQLException ex) 
            {
                out.println("Error closing resources: " + ex.getMessage());
            }
        }
    }

    private String capitalizeFirstLetter(String input) {
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }
}
