<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<!DOCTYPE html>

<html>
<head>
    <title>The Green Light District - Menu</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/indexStyle.css">
    <link rel="icon" type="image/png" href="${pageContext.request.contextPath}/recs/favicon.png">
    <script src="${pageContext.request.contextPath}/js/indexScript.js"></script>


    <link rel="preconnect" href="https://fonts.googleapis.com">
	<link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
	<link href="https://fonts.googleapis.com/css2?family=Comic+Neue:ital,wght@0,300;0,400;0,700;1,300;1,400;1,700&family=Doto:wght@100..900&family=Nova+Mono&family=Poppins:ital,wght@0,100;0,200;0,300;0,400;0,500;0,600;0,700;0,800;0,900;1,100;1,200;1,300;1,400;1,500;1,600;1,700;1,800;1,900&family=Shadows+Into+Light&family=Share+Tech+Mono&family=VT323&family=Yatra+One&display=swap" rel="stylesheet">
</head>
<body>
	<div class="monitor">
		<div id="console">
			<div id="crt_content">
				<div class="title_card"><p>
				-  The Green Light District  -
				</p></div>

				<div class="main_menu">
					<form action="${pageContext.request.contextPath}/index" method="post">
						<button type="submit" name="endpoint" value="play">Play</button>
						<button type="submit" name="endpoint" value="credits">Credits</button>
						<input type="hidden" id="crt_field" name="moniterIsOn" value="${moniterIsOn}">
					</form>
				</div>
			</div>
		</div>

		<div class="bottom_row">
			<div class="ipear_logo">
                <img class="ipear_img" src="${pageContext.request.contextPath}/recs/iPearDropShadow.png">
            </div>
			
			<div class="power_button_container">
				<button id="power_button" type="button" onclick="clickPowerButton()"></button>
			</div>
		</div>
	</div>
</body>
</html>