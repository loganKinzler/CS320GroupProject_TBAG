<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ page import="java.io.*" %>

<!DOCTYPE html>

<html>
<head>
    <title>The Green Light District - Menu</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/creditsStyle.css">
    <link rel="icon" type="image/png" href="${pageContext.request.contextPath}/recs/favicon.png">
    <script src="${pageContext.request.contextPath}/js/creditsScript.js"></script>


    <link rel="preconnect" href="https://fonts.googleapis.com">
	<link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
	<link href="https://fonts.googleapis.com/css2?family=Comic+Neue:ital,wght@0,300;0,400;0,700;1,300;1,400;1,700&family=Doto:wght@100..900&family=Nova+Mono&family=Poppins:ital,wght@0,100;0,200;0,300;0,400;0,500;0,600;0,700;0,800;0,900;1,100;1,200;1,300;1,400;1,500;1,600;1,700;1,800;1,900&family=Shadows+Into+Light&family=Share+Tech+Mono&family=VT323&family=Yatra+One&display=swap" rel="stylesheet">
</head>
<body>
	<div class="monitor">
		<div id="console">
			<div id="crt_content" class="">
				<div class="title_card"><p>
				-  Created by  -
				</p></div>
				
				

				<div class="main_menu">
					<%
					    String filePath = application.getRealPath("/recs/evan.txt");
					    BufferedReader reader = new BufferedReader(new FileReader(filePath));
					    StringBuilder content = new StringBuilder();
					    String line;
					    while ((line = reader.readLine()) != null) {
					        content.append(line).append("\n");
					    }
					    reader.close();
					%>
					<div>
					<pre id = "face"><%= content.toString() %></pre>
					<p id="name">Evan Natale</p>
					</div>
					
					<%
						filePath = application.getRealPath("/recs/logan.txt");
						reader = new BufferedReader(new FileReader(filePath));
						content = new StringBuilder();
						while ((line = reader.readLine()) != null) {
							content.append(line).append("\n");
						}
						reader.close();
					%>
					<div>
						<pre id = "face"><%= content.toString() %></pre>
						<p id="name">Logan Kinzler</p>
					</div>
					
					<%
						filePath = application.getRealPath("/recs/nadia.txt");
						reader = new BufferedReader(new FileReader(filePath));
						content = new StringBuilder();
						while ((line = reader.readLine()) != null) {
							content.append(line).append("\n");
						}
						reader.close();
					%>
					<div>
						<pre id = "face"><%= content.toString() %></pre>
						<p id="name">Nadia Hanna</p>
					</div>
					
					<%
						filePath = application.getRealPath("/recs/andrew.txt");
						reader = new BufferedReader(new FileReader(filePath));
						content = new StringBuilder();
						while ((line = reader.readLine()) != null) {
							content.append(line).append("\n");
						}
						reader.close();
					%>
					<div>
						<pre id = "face"><%= content.toString() %></pre>
						<p id="name">Andrew Hellmann</p>
					</div>
				</div>
				<div class="special"><p style="font-size:3rem;">
								-  Special Thanks to  -
								</p></div>
				<div class = "special_thanks">
					<%
						filePath = application.getRealPath("/recs/hake.txt");
						reader = new BufferedReader(new FileReader(filePath));
						content = new StringBuilder();
						while ((line = reader.readLine()) != null) {
							content.append(line).append("\n");
						}
						reader.close();
					%>
					<div style="margin-top:-1rem;">
						<pre id = "face"><%= content.toString() %></pre>
						<p id="name">Professor Hake</p>
					</div>
					
				
					<%
						filePath = application.getRealPath("/recs/babcock.txt");
						reader = new BufferedReader(new FileReader(filePath));
						content = new StringBuilder();
						while ((line = reader.readLine()) != null) {
							content.append(line).append("\n");
						}
						reader.close();
					%>
					<div style="margin-top:-1rem;">
						<pre id = "babcock"><%= content.toString() %></pre>
						<p id="bab">Dr. Babcock</p>
					</div>
					
					<%
						filePath = application.getRealPath("/recs/arrow.txt");
						reader = new BufferedReader(new FileReader(filePath));
						content = new StringBuilder();
						while ((line = reader.readLine()) != null) {
							content.append(line).append("\n");
						}
						reader.close();
					%>
					<div>
						<form action="${pageContext.request.contextPath}/credits" method="post">
						  <button id="back_button" type="submit" name="endpoint" value="back">Go Back</button>
						</form>
	
					</div>
																
				</div>
			</div>
		</div>

		<div class="bottom_row">
			<div class="ipear_logo">
                <img class="ipear_img" src="${pageContext.request.contextPath}/recs/iPearDropShadow.png">
            </div>
			
			<div class="power_button_container">
				<button id="power_button" type="button"></button>
			</div>
		</div>
	</div>
	
	
</body>
</html>