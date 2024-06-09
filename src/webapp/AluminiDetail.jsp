<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Alumni Details</title>
    <!-- Include PDF.js library -->
    <script src="https://cdnjs.cloudflare.com/ajax/libs/pdf.js/2.11.338/pdf.min.js"></script>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 0;
            padding: 0;
            background-color: #f4f4f4;
        }

        .container {
            max-width: 800px;
            margin: 20px auto;
            padding: 20px;
            background-color: #fff;
            border-radius: 5px;
            box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
        }

        h2 {
            margin-top: 0;
            color: #333;
        }

        .profile-image {
            width: 100px;
            height: auto;
            border-radius: 5px;
        }

        .resume-pdf {
            width: 100%;
            height: 500px; /* Set height as per your requirement */
            border: 1px solid #ccc;
            border-radius: 5px;
        }

        .resume-link {
            display: block;
            margin-top: 10px;
            color: #007bff;
            text-decoration: none;
        }

        .resume-link:hover {
            text-decoration: underline;
        }

        .back-button {
            margin-top: 20px;
            background-color: #007bff;
            color: #fff;
            border: none;
            padding: 10px 20px;
            border-radius: 5px;
            cursor: pointer;
        }

        .print-button {
            margin-top: 20px;
            background-color: #28a745;
            color: #fff;
            border: none;
            padding: 10px 20px;
            border-radius: 5px;
            cursor: pointer;
        }
    </style>
</head>
<body>
    <div class="container">
        <h2>Alumni Details</h2>
        <!-- Display Profile Image -->
        <p><strong>Profile Image:</strong></p>
        <img class="profile-image" src="${pageContext.request.contextPath}/${profile_image_path}" alt="Profile Image">
        <!-- Display other details -->
        <p><strong>Name:</strong> ${fname} ${mname} ${lname}</p>
        <p><strong>DOB:</strong> ${dob}</p>
        <p><strong>Email:</strong> ${mailid}</p>
        <p><strong>Contact Number:</strong> ${contactno}</p>
        <p><strong>State:</strong> ${state}</p>
        <p><strong>City:</strong> ${city}</p>
        <p><strong>College:</strong> ${college}</p>
        <p><strong>Department:</strong> ${department}</p>
        <p><strong>Passout Year:</strong> ${passoutyear}</p>
        <p><strong>Current Working Field:</strong> ${currentworkingfield}</p>
        <p><strong>Brief:</strong> ${brief}</p>
        <!-- LinkedIn Profile Link -->
        <p><strong>LinkedIn profile:</strong> <a href="${linkedin_profile}" target="_blank">${linkedin_profile}</a></p>
        <!-- Display Resume 1 PDF -->
        <p><strong>Fresher Resume:</strong></p>
        <canvas class="resume-pdf" id="resume1"></canvas>
        <a class="resume-link" href="${pageContext.request.contextPath}/${resume1_image_path}" target="_blank">PDF of Fresher Resume</a>
        <!-- Provide a link to view the PDF -->
        <!-- Display Resume 2 PDF -->
        <p><strong>Recent Resume:</strong></p>
        <canvas class="resume-pdf" id="resume2"></canvas>
        <a class="resume-link" href="${pageContext.request.contextPath}/${resume2_image_path}" target="_blank">PDF of Recent Resume</a>
        <!-- Provide a link to view the PDF -->
        <!-- Back Button -->
        <button class="back-button" onclick="goBack()">Back</button>
        <!-- Print Button -->
        <button class="print-button" onclick="window.print()">Print</button>
    </div>

    <script>
        // JavaScript function to go back to the previous page
        function goBack() {
            window.history.back();
        }

        // Function to render PDF files using PDF.js
        function renderPDF(url, canvasId) {
            // Fetch PDF document
            pdfjsLib.getDocument(url).promise.then(function(pdf) {
                // Render the first page of PDF
                pdf.getPage(1).then(function(page) {
                    var canvas = document.getElementById(canvasId);
                    var context = canvas.getContext('2d');
                    var viewport = page.getViewport({ scale: 1.5 });
                    canvas.height = viewport.height;
                    canvas.width = viewport.width;

                    var renderContext = {
                        canvasContext: context,
                        viewport: viewport
                    };
                    page.render(renderContext);
                });
            });
        }
        // Call renderPDF function for each PDF file
        renderPDF('${pageContext.request.contextPath}/${resume1_image_path}', 'resume1');
        renderPDF('${pageContext.request.contextPath}/${resume2_image_path}', 'resume2');
    </script>
</body>
</html>
