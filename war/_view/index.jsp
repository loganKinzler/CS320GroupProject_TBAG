<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
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
		
		    <form action="${pageContext.request.contextPath}/game" method="post" class="input-area">
		        <input type="text" name="userInput" placeholder="Enter your input...">
		        <button type="submit">Submit</button>
		    </form>
		</div>
		<div class="buttons">
			<button class="monitor-button" onclick="passed">Clear Console</button>
		</div>
	</div>
	<div class=sticky-note-container>
		<div class="sticky-note-3d">
          <div class="sticky-note-content">
            <h1>Commands</h1>
            <p>describe</p>
            <p>move</p>
          </div>
        </div>
	</div>
	
	<script src="${pageContext.request.contextPath}/js/script.js"></script>
</body>
</html>