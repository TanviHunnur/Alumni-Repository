<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Edit Alumni Details</title>
    <style>
        /* Your CSS styles here */
    </style>
</head>
<body>
    <div class="container">
        <h2>Edit Alumni Details</h2>
        <form action="AluminiEditServlet" method="post" enctype="multipart/form-data">
            <input type="text" name="username" placeholder="Username" value="<%= request.getAttribute("username") %>" readonly><br><br>
            <label for="profileImage">Profile Image:</label>
            <input type="file" name="profileImage" id="profileImage"><br><br>
            <label for="resume1">Resume 1:</label>
            <input type="file" name="resume1" id="resume1"><br><br>
            <label for="resume2">Resume 2:</label>
            <input type="file" name="resume2" id="resume2"><br><br>
            <button type="submit">Save</button>
        </form>
    </div>
</body>
</html>
