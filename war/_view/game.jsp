<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<!DOCTYPE html>
<html>
<head>
    <title>The Green Light District - Game</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/gameStyle.css">
    <link rel="icon" type="image/png" href="${pageContext.request.contextPath}/recs/favicon.png">
	<script src="${pageContext.request.contextPath}/js/gameScript.js"></script>
    
    <link rel="preconnect" href="https://fonts.googleapis.com">
	<link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
	<link href="https://fonts.googleapis.com/css2?family=Comic+Neue:ital,wght@0,300;0,400;0,700;1,300;1,400;1,700&family=Doto:wght@100..900&family=Nova+Mono&family=Poppins:ital,wght@0,100;0,200;0,300;0,400;0,500;0,600;0,700;0,800;0,900;1,100;1,200;1,300;1,400;1,500;1,600;1,700;1,800;1,900&family=Shadows+Into+Light&family=Share+Tech+Mono&family=VT323&family=Yatra+One&display=swap" rel="stylesheet">
</head>
<body>
	<div class="monitor">
		<div id="console">
			<div id="crt_content">
		    	<div class="history-box">
		        	<c:forEach items="${gameHistory}" var="entry">
		    	        <div class="history-item">${entry}</div>
			        </c:forEach>
			    </div>
		
		    	<form action="${pageContext.request.contextPath}/game" method="post" class="input-area" id="submission">
		       		<input type="text" name="userInput" placeholder="Enter your input...">
		       		<button type="submit">Submit</button>
					<input type="hidden" id="crt_field" name="moniterIsOn" value="${moniterIsOn}">
		    	</form>
			</div>
		</div>

		<div class="bottom_row">
			<div class="ipear_logo">
                <img class="ipear_img" src="${pageContext.request.contextPath}/recs/iPearDropShadow.png">
            </div>
			
			<div class="power_button">
				<button id="power_button" onclick="clickPowerButton()"></button>
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
	
	<c:if test="${sudoStage > 0}">
		<form id="delayedSubmit" method="post" action="${pageContext.request.contextPath}/game" style="display: none;">
		</form>
		<script>
			setTimeout(() => {
				document.getElementById("delayedSubmit").submit();
			}, 1000);
		</script>
	</c:if>
	<c:if test="${playHakeSound}">
    <script>
        window.addEventListener('load', function() {
            new Audio('${pageContext.request.contextPath}/recs/hake-scream.mp3').play();
            
        });
    </script>
</c:if>
</body>
</html>