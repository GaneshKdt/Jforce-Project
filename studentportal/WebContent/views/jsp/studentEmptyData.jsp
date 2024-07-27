
<!DOCTYPE html>
<html lang="en">
<%@page import="com.nmims.helpers.PersonStudentPortalBean"%>
<%@page import="java.net.URLEncoder"%>
<%@page import="com.nmims.helpers.AESencrp"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<jsp:include page="adminCommon/jscss.jsp">
	<jsp:param value="Welcome to Student Zone" name="title" />
</jsp:include>
<style>
.jumbotron {
	background-color: #fff;
	padding: 0.5px;
}
</style>
<spring:eval expression="@propertyConfigurer.getProperty('SERVER_PATH')"
	var="server_path" />

<body>

	<%@ include file="adminCommon/header.jsp"%>


	<%

         String encryptedSapId = URLEncoder.encode(AESencrp.encrypt((String)session.getAttribute("userId"))); 
    String examAppSSOUrl = (String)pageContext.getAttribute("server_path") + "exam/loginforSSO?uid="+encryptedSapId;
    String acadsAppSSOUrl = (String)pageContext.getAttribute("server_path") + "acads/loginforSSO?uid="+encryptedSapId;
    
    %>



	<div class="sz-main-content-wrapper">

		<jsp:include page="adminCommon/breadcrum.jsp">
			<jsp:param value="Student Zone;Home" name="breadcrumItems" />
		</jsp:include>


		<div class="sz-main-content menu-closed">
			<div class="sz-main-content-inner">
				<jsp:include page="adminCommon/left-sidebar.jsp">
					<jsp:param value="" name="activeMenu" />
				</jsp:include>


				<div class="sz-content-wrapper examsPage">
					<%@ include file="adminCommon/adminInfoBar.jsp"%>


					<div class="sz-content">


						<table class="table table-bordered">
							<thead>
								<tr>
									<th>SAP ID</th>
									<th>Column name</th>
								</tr>
							</thead>
							<tbody>
								<c:forEach items="${IncorrectStudentDataList}" var="student">
									<tr>
										<td>${ student.sapid }</td>
										<td><c:if
												test="${ student.firstName == null || student.firstName == ''}">
									firstName , 
								</c:if> <c:if
												test="${ student.lastName == null || student.lastName == ''}">
									lastName , 
								</c:if> <c:if
												test="${ student.emailId == null || student.emailId == ''}">
									email , 
								</c:if> <c:if test="${ student.city == null || student.city == ''}">
									city , 
								</c:if> <c:if test="${ student.mobile == null || student.mobile == ''}">
									mobile , 
								</c:if> <c:if
												test="${ student.validityEndMonth == null || student.validityEndMonth == ''}">
									validityEndMonth , 
								</c:if> <c:if
												test="${ student.validityEndYear == null || student.validityEndYear == ''}">
									validityEndYear , 
								</c:if> <c:if
												test="${ student.prgmStructApplicable == null || student.prgmStructApplicable == ''}">
									program Structure Applicable ,
								</c:if> <c:if
												test="${ student.enrollmentMonth == null || student.enrollmentMonth == ''}">
									enrollmentMonth , 
								</c:if> <c:if
												test="${ student.enrollmentYear == null || student.enrollmentYear == ''}">
									enrollmentYear , 
								</c:if> <c:if
												test="${ student.program == null || student.program == ''}">
									program 
								</c:if></td>
									</tr>
								</c:forEach>
							</tbody>
						</table>


					</div>
				</div>


			</div>
		</div>

		<div id="examApp"></div>
		<div id="acadsApp"></div>

		<jsp:include page="adminCommon/footer.jsp" />
		<script>
		$( "#examApp" ).load( "<%=examAppSSOUrl%>" );
		$( "#acadsApp" ).load( "<%=acadsAppSSOUrl%>" );
	</script>
</body>
</html>