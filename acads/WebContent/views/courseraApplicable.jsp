<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<!DOCTYPE html>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<html lang="en">
<jsp:include page="/views/common/jscss.jsp">
	<jsp:param value="Coursera - Available Products" name="title" />
</jsp:include>
<head>
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@4.0.0/dist/css/bootstrap.min.css" integrity="sha384-Gn5384xqQ1aoWXA+058RXPxPg6fy4IWvTNh0E263XmFcJlSAwiGgFAW/dAiS6JXm" crossorigin="anonymous">
<link rel="stylesheet" href="path/to/font-awesome/css/font-awesome.min.css">
</head>
	<style>
.box-shadow{
	box-shadow: 0px 2px 10px rgba(0,0,0,0.5)
}
</style>
<body>
	<jsp:include page="/views/common/header.jsp" />
	<div class="sz-main-content-wrapper">
		<jsp:include page="/views/common/breadcrum.jsp">
			<jsp:param value="Home;Skillsets" name="breadcrumItems" />
		</jsp:include>
		<div class="sz-main-content menu-closed">
			<div class="sz-main-content-inner">
				<jsp:include page="/views/common/left-sidebar.jsp">
					<jsp:param value="" name="activeMenu" />
				</jsp:include>
				<div class="sz-content-wrapper dashBoard">
					<jsp:include page="/views/common/studentInfoBar.jsp" />
					<div class="sz-content padding-top">
						<jsp:include page="/views/common/messages.jsp" />
					</div>
				</div>
			</div>
		</div>
	</div>

	<jsp:include page="/views/common/footer.jsp" />
</body>
</html>