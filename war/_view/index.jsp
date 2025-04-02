<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<!DOCTYPE html>
<html>
<head>
    <title>Game</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    
    <link rel="preconnect" href="https://fonts.googleapis.com">
	<link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
	<link href="https://fonts.googleapis.com/css2?family=Comic+Neue:ital,wght@0,300;0,400;0,700;1,300;1,400;1,700&family=Doto:wght@100..900&family=Nova+Mono&family=Poppins:ital,wght@0,100;0,200;0,300;0,400;0,500;0,600;0,700;0,800;0,900;1,100;1,200;1,300;1,400;1,500;1,600;1,700;1,800;1,900&family=Shadows+Into+Light&family=Share+Tech+Mono&family=VT323&family=Yatra+One&display=swap" rel="stylesheet">
    </head>
<body>
	<div class="monitor">
		<div class="console">
		    <div class="history-box">
		        <c:forEach items="${gameHistory}" var="entry">
		            <div class="history-item">${entry}</div>
		        </c:forEach>
		    </div>
		
		    <form action="${pageContext.request.contextPath}/game" method="post" class="input-area" id="submission">
		        <input type="text" name="userInput" placeholder="Enter your input...">
		        <button type="submit">Submit</button>
		    </form>
		</div>
		<div class="buttons">
			<button class="monitor-button" onclick="passed" style="display: none;">Clear Console</button>
		</div>
	</div>
	<div class=sticky-note-container>
		<div class="sticky-note-3d">
          <div class="sticky-note-content">
            <h1>Commands</h1>
			
			<!-- Move -->
			<c:if test="${fn:contains(foundCommands, 'move')}">
			    <p>move [direction]</p>
			</c:if>
			
			<!-- Describe -->
			<c:if test="${fn:contains(foundCommands, 'describe')}">
				<p>describe [
			</c:if>
			
			<c:if test="${foundCommands.contains('describe_room')}" var="describe_room">
				room 
			</c:if>
			
			<c:if test="${foundCommands.contains('describe_room') and 
				(foundCommands.contains('describe_moves') or
				foundCommands.contains('describe_directions'))}">
				/ 
			</c:if>
			
			<c:if test="${foundCommands.contains('describe_moves')}" var="describe_moves">
				moves 
			</c:if>
			
			<c:if test="${foundCommands.contains('describe_moves') and
				foundCommands.contains('describe_directions')}">
				/ 
			</c:if>
			
			<c:if test="${foundCommands.contains('describe_directions')}" var="describe_directions">
				directions
			</c:if>
			
			<c:if test="${fn:contains(foundCommands, 'describe')}">
				]</p> 
			</c:if>
			
			<!-- Use -->
			<c:if test="${fn:contains(foundCommands, 'use')}">
			    <p>use [item]</p>
			</c:if>
			
			<!-- Pickup -->
			<c:if test="${fn:contains(foundCommands, 'pickup')}">
			    <p>pickup [item]</p>
			</c:if>
			
			<!-- Attack -->
			<c:if test="${fn:contains(foundCommands, 'attack')}">
			    <p>attack [enemy] with [weapon]</p><!-- using [attack] -->
			</c:if>
			
          </div>
        </div>
	</div>
	
	<script src="${pageContext.request.contextPath}/js/script.js"></script>
	<c:if test="${sudoStage > 0}">
		<form id="delayedSubmit" method="post" action="${pageContext.request.contextPath}/game" style="display: none;">
		</form>
		<script>
			setTimeout(() => {
				document.getElementById("delayedSubmit").submit();
			}, 1000);
		</script>
	</c:if>
</body>
</html>