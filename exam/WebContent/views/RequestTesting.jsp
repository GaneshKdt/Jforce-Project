<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<body>
	<c:choose>
       <c:when test="${error == null}">
              <p> Cache refresh successfully</p>
       </c:when>
       <c:otherwise>
              <h2 style="color:red"> Error while refreshing cache on ports : ${ error } </h2>
       </c:otherwise>
  </c:choose>
</body>
</html>