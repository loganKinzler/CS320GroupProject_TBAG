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
					<pre id = "evan"><%= content.toString() %></pre>
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
						<pre id = "evan"><%= content.toString() %></pre>
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
						<pre id = "evan"><%= content.toString() %></pre>
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
						<pre id = "evan"><%= content.toString() %></pre>
						<p id="name">Andrew Hellmann</p>
					</div>
				</div>
				<div class="special"><p>
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
										<div>
											<pre id = "evan"><%= content.toString() %></pre>
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
															<div>
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
	
	<div class=sticky-note-container>
			<div class="sticky-note-3d">
	          <div class="sticky-note-content">
	            <h1>Commands</h1>
				
				
				<!-- Move -->
				<c:if test="${foundCommands.contains('move')}">
				    <p>move [ direction ]</p>
				</c:if>
				
				
				<!-- Describe Group: Room -->
				<c:if test="${foundCommands.contains('describeGroup_room')}"><p>describe [</c:if>
				<c:if test="${foundCommands.contains('describe_room')}">room</c:if>
				<c:if test="${foundCommands.contains('describe_room') &&
							(foundCommands.contains('describe_moves') || 
							foundCommands.contains('describe_direction'))}"> / </c:if>
				<c:if test="${foundCommands.contains('describe_moves')}">moves</c:if>
				<c:if test="${(foundCommands.contains('describe_room') ||
							foundCommands.contains('describe_moves')) && 
							foundCommands.contains('describe_directions')}"> / </c:if>
				<c:if test="${foundCommands.contains('describe_directions')}">directions</c:if>
				<c:if test="${foundCommands.contains('describeGroup_room')}">]</p></c:if>
				
				<!-- Describe Group Attack -->
				<c:if test="${foundCommands.contains('describeGroup_attack')}"><p>describe [</c:if>
				<c:if test="${foundCommands.contains('describe_enemies')}">enemies</c:if>
				<c:if test="${foundCommands.contains('describe_enemies') &&
							foundCommands.contains('describe_stats')}"> / </c:if>
				<c:if test="${foundCommands.contains('describe_stats')}">stats</c:if>
				<c:if test="${foundCommands.contains('describeGroup_attack')}">]</p></c:if>
				
				<!-- Describe Group: Items -->
				<c:if test="${foundCommands.contains('describeGroup_items')}"><p>describe [</c:if>
				<c:if test="${foundCommands.contains('describe_items')}">items</c:if>
				<c:if test="${foundCommands.contains('describe_items') &&
							foundCommands.contains('describe_inventory')}"> / </c:if>
				<c:if test="${foundCommands.contains('describe_inventory')}">inventory</c:if>
				<c:if test="${foundCommands.contains('describeGroup_items')}">]</p></c:if>
				
				<!-- Pickup & Drop -->
				<c:if test="${foundCommands.contains('pickup') ||
							foundCommands.contains('drop')}"><p></c:if>
				<c:if test="${foundCommands.contains('pickup')}">pickup</c:if>
				<c:if test="${foundCommands.contains('pickup') &&
							foundCommands.contains('drop')}"> / </c:if>
				<c:if test="${foundCommands.contains('drop')}">drop</c:if>
				<c:if test="${foundCommands.contains('pickup') ||
							foundCommands.contains('drop')}"> [ quantity ] [ item ]</p></c:if>
				
				<!-- Equip & Unequip] -->
				<c:if test="${foundCommands.contains('equip')}">
				    <p>equip [ weapon ] into [ slot ]</p>
				</c:if>
				
				<c:if test="${foundCommands.contains('unequip')}"><p>unequip [</c:if>
				<c:if test="${foundCommands.contains('unequip_weapon')}">weapon</c:if>
				<c:if test="${foundCommands.contains('unequip_weapon') &&
							foundCommands.contains('unequip_slot')}"> / </c:if>
				<c:if test="${foundCommands.contains('unequip_slot')}">slot</c:if>
				<c:if test="${foundCommands.contains('unequip')}">]</p></c:if>
				
				
				<!-- Attack -->
				<c:if test="${foundCommands.contains('attack')}">
				    <p>attack [ enemy ] with [ weapon ]</p><!-- using [attack] -->
				</c:if>
				
				<!-- Use -->
				<c:if test="${foundCommands.contains('use')}">
				    <p>use [ item ]</p>
				</c:if>
	          </div>
	        </div>
		</div>
</body>
</html>