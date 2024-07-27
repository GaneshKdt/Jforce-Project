<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html class="no-js"> <!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

	<jsp:include page="jscss.jsp">
		<jsp:param value="Sessions Video Recording" name="title" />
	</jsp:include>

<body class="inside">
	<%@ include file="header.jsp"%>
	
	<section class="content-container login">
        <div class="container-fluid customTheme">
	        <div class="row"><legend>Sessions Video Recording</legend></div>
	        <%@ include file="messages.jsp"%>
	        
	        <form:form modelAttribute="videoContentBean" method="post">
	        	<fieldset>
					<div class="panel-body">
						<div class="col-md-6 column">
							<div class="form-group">
								<form:select path="year" type="text" placeholder="Acad Year" class="form-control"   itemValue="${specialisation.year}">
									<form:option value="">Select Acad Year</form:option>
									<form:options items="${yearList}" />
								</form:select>
							</div>
									
							<div class="form-group">		
								<form:select path="month" type="text" placeholder="Acad Month" class="form-control"  itemValue="${specialisation.month}">
									<form:option value="">Select Acad Month</form:option>
									<form:options items="${monthList}" />
								</form:select>
							</div>
							
							<div class="form-group">
								<form:input path="sessionDate" type="date" placeholder="Session Date" class="form-control" value="${videoContentBean.sessionDate}" />
							</div>
							
							<div class="form-group" >
							<form:select id="programType" path="programType"  class="form-control"> 
								<form:option value="">Select Program Type</form:option>
								<form:option value="PG">PG Sessions</form:option>
								<form:option value="MBA - WX">MBA-WX Sessions</form:option>
							</form:select>
							</div> 
												
							<div class="form-group">
								<label class="control-label" for="submit"></label>
								<button id="submit" name="submit" class="btn btn-large btn-primary" formaction="searchSessionRecording">Generate</button>
								<button id="cancel" name="cancel" class="btn btn-danger" formaction="home" formnovalidate="formnovalidate">Cancel</button>
							</div>
							
						</div>
						
					
		        	</div>
	        	</fieldset>
	        </form:form>
	        
	        <c:if test="${recordingListSize > 0}">
	        	<div class="panel-body">
					<h2>&nbsp;Session Video Report<font size="2px"> (${recordingListSize} Records Found) &nbsp; <a href="downloadSessionVideoReport" style="color:blue;">Download to Excel</a></font></h2>
				</div>
	        </c:if>
        
        </div>
 	</section>
 	
 	<jsp:include page="footer.jsp" />

</body>
</html>