<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<jsp:include page="jscss.jsp">
	<jsp:param value="Report for StudentZone Logins" name="title" />
</jsp:include>

<body class="inside">

	<%@ include file="header.jsp"%>

	<section class="content-container login">
		<div class="container-fluid customTheme">

			<div class="row"><legend>Report For Student Logins</legend></div>
			
			<%@ include file="messages.jsp"%>

			<div class="panel-body clearfix">
			<form:form  action="searchStudentZoneLogins" method="post" modelAttribute="searchBean">
			<fieldset>
			<div class="col-md-6 column">

					<div class="form-group">
						<form:input id="sapID" path="sapid" type="text" placeholder="Enter SAP-ID" class="form-control" required="required"/>
					</div>
					
				
					<div class="form-group">
					<button id="submit" name="submit" class="btn btn-large btn-primary"
						formaction="searchStudentZoneLogins">Generate</button>
						<button id="cancel" name="cancel" class="btn btn-danger" formaction="home" formnovalidate="formnovalidate">Cancel</button>
					</div>

				</div>
				
				</fieldset>
				</form:form>
				
		</div>

		<c:choose>
	<c:when test="${rowCount > 0}">

	<legend>&nbsp;Student Zone Logins<font size="2px">(${rowCount} Records Found)&nbsp;</font></legend>
	<div class="table-responsive">
	<table class="table table-striped table-hover" style="font-size:12px">
						<thead>
							<tr> 
								<th>Sr. No.</th>
								<th>Login Time</th>
								<th>Enrollment Year</th>
								<th>Enrollment Month</th>
								
							</tr>
						</thead>
						<tbody>
						
						<c:forEach var="login" items="${loginList}" varStatus="status">
					        <tr>
					            <td><c:out value="${status.count}"/></td>
					            <td><c:out value="${login.logintime}"/></td>
								<td><c:out value="${login.enrollmentYear}"/></td>
								<td><c:out value="${login.enrollmentMonth}"/></td>
								
								
								
					        </tr>   
					    </c:forEach>
							
						</tbody>
					</table>
	</div>
	<br>

</c:when>
</c:choose>

<c:url var="firstUrl" value="searchLoginPage?pageNo=1" />
<c:url var="lastUrl" value="searchLoginPage?pageNo=${page.totalPages}" />
<c:url var="prevUrl" value="searchLoginPage?pageNo=${page.currentIndex - 1}" />
<c:url var="nextUrl" value="searchLoginPage?pageNo=${page.currentIndex + 1}" />


<c:choose>
<c:when test="${page.totalPages > 1}">
<div align="center">
    <ul class="pagination">
        <c:choose>
            <c:when test="${page.currentIndex == 1}">
                <li class="disabled"><a href="#">&lt;&lt;</a></li>
                <li class="disabled"><a href="#">&lt;</a></li>
            </c:when>
            <c:otherwise>
                <li><a href="${firstUrl}">&lt;&lt;</a></li>
                <li><a href="${prevUrl}">&lt;</a></li>
            </c:otherwise>
        </c:choose>
        <c:forEach var="i" begin="${page.beginIndex}" end="${page.endIndex}">
            <c:url var="pageUrl" value="searchSRPage?pageNo=${i}" />
            <c:choose>
                <c:when test="${i == page.currentIndex}">
                    <li class="active"><a href="${pageUrl}"><c:out value="${i}" /></a></li>
                </c:when>
                <c:otherwise>
                    <li><a href="${pageUrl}"><c:out value="${i}" /></a></li>
                </c:otherwise>
            </c:choose>
        </c:forEach>
        <c:choose>
            <c:when test="${page.currentIndex == page.totalPages}">
                <li class="disabled"><a href="#">&gt;</a></li>
                <li class="disabled"><a href="#">&gt;&gt;</a></li>
            </c:when>
            <c:otherwise>
                <li><a href="${nextUrl}">&gt;</a></li>
                <li><a href="${lastUrl}">&gt;&gt;</a></li>
            </c:otherwise>
        </c:choose>
    </ul>
</div>
</c:when>
</c:choose>
		
		</div>
		
	</section>

	<jsp:include page="footer.jsp" />


</body>
</html>