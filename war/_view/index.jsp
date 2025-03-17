<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <title>Game</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <h1>Game History</h1>
    <div class="history-box">
        <c:forEach items="${gameHistory}" var="entry">
            <div class="history-item">${entry}</div>
        </c:forEach>
    </div>

    <form action="${pageContext.request.contextPath}/game" method="post">
        <input type="text" name="userInput" placeholder="Enter your input...">
        <button type="submit">Submit</button>
    </form>
</body>
</html>