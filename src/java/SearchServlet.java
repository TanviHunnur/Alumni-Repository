package abc;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/SearchServlet")
public class SearchServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/mydb";
    private static final String JDBC_USER = "root";
    private static final String JDBC_PASSWORD = "Aparna11/";

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        String listType = request.getParameter("listType");
        if (listType == null || !listType.equals("student")) {
            out.println("Invalid list type");
            return;
        }

        String department1 = request.getParameter("dropdown1");
        String batch1 = request.getParameter("dropdown2");
        String search = request.getParameter("search");

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);

            String query = "SELECT * FROM Student WHERE department = ?";
            if (batch1 != null && !batch1.isEmpty()) {
                query += " AND passoutyear = ?";
            }
            if (search != null && !search.isEmpty()) {
                query += " AND (fname LIKE ? OR mname LIKE ? OR lname LIKE ?)";
            }
            stmt = conn.prepareStatement(query);
            stmt.setString(1, department1);
            int parameterIndex = 2;
            if (batch1 != null && !batch1.isEmpty()) {
                stmt.setString(parameterIndex++, batch1);
                if (search != null && !search.isEmpty()) {
                    stmt.setString(parameterIndex++, "%" + search + "%");
                    stmt.setString(parameterIndex++, "%" + search + "%");
                    stmt.setString(parameterIndex, "%" + search + "%");
                }
            } else if (search != null && !search.isEmpty()) {
                stmt.setString(parameterIndex++, "%" + search + "%");
                stmt.setString(parameterIndex++, "%" + search + "%");
                stmt.setString(parameterIndex, "%" + search + "%");
            }
            rs = stmt.executeQuery();

            // Generate HTML output based on the result set
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Student List</title>");
            out.println("<style>");
            out.println("body { background-color: black; color: white; }");
            out.println("table { border-collapse: collapse; width: 100%; }");
            out.println("th { border: 1px solid white; padding: 8px; color: white; }");
            out.println("td { border: 1px solid white; padding: 8px; color: white; cursor: pointer; }");
            out.println("th { background-color: black; }");
            out.println(".editFormContainer { float: right; width: 30%; padding: 10px; background-color: #333; }");
            out.println("</style>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h2>Student List</h2>");
            out.println("<table>");
            out.println("<tr><th>Name</th><th>Department</th><th>Profile</th><th>Edit</th><th>Delete</th></tr>");
            while (rs.next()) {
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
/*function searchStudents() {
    var formData = new FormData();
    formData.append('listType', 'student');
    formData.append('department', document.getElementById('dropdown1').value);
    formData.append('batch', document.getElementById('dropdown2').value);
    formData.append('searchQuery', document.getElementById('student-search-input').value);

    fetch('AlumniListServlet', {
        method: 'POST',
        body: formData
    })
    .then(response => response.text())
    .then(data => {
        document.getElementById('student-list-placeholder').innerHTML = data;
    })
    .catch(error => console.error('Error fetching Student List:', error));
}
*/

/*function loadStudentList() {
	  fetch('AlumniListServlet?listType=student') // Sending listType parameter
	    .then(response => response.text())
	    .then(data => {
	      document.getElementById('student-list-placeholder').innerHTML = data;
	    })
	    .catch(error => console.error('Error fetching Student List:', error));
}
*/

/*<!-- Student List -->
	<div id="student-list-content" style="display:none;">
  	<h2 style="color:white">Repository for Students</h2>
  	<form id="student-search-form">
    <select id="dropdown1" onchange="showSearch()">
      <option value="" disabled selected>Select Department</option>
      <option value="Computer Science Engineering">Computer Science Engineering</option>
      <option value="Computer Science Engineering(AIML)">Computer Science Engineering(AIML)</option>
      <option value="Computer Science Engineering(AIDS)">Computer Science Engineering(AIDS)</option>
      <option value="Electronics Engineering">Electronics Engineering</option>
      <option value="Mechanical Engineering">Mechanical Engineering</option>
      <option value="Civil Engineering">Civil Engineering</option>
    </select>
    <select id="dropdown2" onchange="showSearch()">
      <option value="" disabled selected>Select Batch</option>
      <option value="2022-2023">2022-2023</option>
      <option value="2023-2024">2023-2024</option>
      <option value="2024-2025">2024-2025</option>
      <option value="2025-2026">2025-2026</option>
      <option value="2026-2027">2026-2027</option>
    </select>
    <input type="text" id="student-search-input" placeholder="Search by Name" class="search-box">
    <button type="button" onclick="searchStudents()" class="button">Search</button>
  	</form>
  
  	<div id="student-list-placeholder"></div>
  	<!-- Placeholder for student list table -->
	</div>
*/