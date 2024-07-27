<!DOCTYPE html>

<%@page import="com.nmims.beans.Person"%>
<%@page import="com.nmims.beans.Page"%>
<%@page import="com.nmims.beans.StudentMarksBean"%>
<html class="no-js">

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<jsp:include page="jscss.jsp">
<jsp:param value="Generate Marsheets" name="title" />
</jsp:include>

<body class="inside">

<%@ include file="header.jsp"%>
	
    <section class="content-container login">
        <div class="container-fluid customTheme">
        <div class="row"><legend>Generate Non Graded Marksheet from Service Request</legend></div>
        <%@ include file="messages.jsp"%>
		<div class="panel-body clearfix">
		<form:form  action="searchStudents" method="post" modelAttribute="bean">
			<fieldset>
			
			
			<div class="col-md-6 column">
				<div class="form-group">
				<textarea name="serviceRequestIdList" cols="50" rows="7" placeholder="Enter different Service Request Ids in new lines">${bean.serviceRequestIdList}</textarea>
				</div>
				
				<div class="form-group" id=""> 
					<input type="checkbox" name="logoRequired"  style="width:15px;height:15px"  class="form-control" value="Y"/>Is Logo Required 
				</div>
				
				<div class="form-group">
					<label class="control-label" for="submit"></label>

						<button id="submit" name="submit" class="btn btn-large btn-primary" formaction="generateNonGradedMarksheetFromSR">Generate</button>
						<button id="cancel" name="cancel" class="btn btn-danger" formaction="${pageContext.request.contextPath}/home" formnovalidate="formnovalidate">Cancel</button>

				</div>
					
			</div>

		</fieldset>
		</form:form>
		
		<c:if test="${success == 'true'}">
			<a href="download">Download Marksheet</a>
		</c:if>
		
		
		</div>
	</div>
	
	

	</section>

	  <jsp:include page="footer.jsp" />


</body>
</html>
