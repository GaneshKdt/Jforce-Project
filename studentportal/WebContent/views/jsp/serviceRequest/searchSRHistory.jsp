
<!DOCTYPE html>
<%@page import="java.util.*"%>
<%@page import="java.text.DateFormat"%>
<html lang="en">


<%@page import="com.nmims.beans.PageStudentPortal"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<jsp:include page="../adminCommon/jscss.jsp">
	<jsp:param value="Search Service Request History" name="title" />
</jsp:include>
<body>
	<%@ include file="../adminCommon/header.jsp"%>
	<div class="sz-main-content-wrapper">

		<jsp:include page="../adminCommon/breadcrum.jsp">
			<jsp:param value="Student Portal;Search Service Request"
				name="breadcrumItems" />
		</jsp:include>


		<div class="sz-main-content menu-closed">
			<div class="sz-main-content-inner">
			<div id="sticky-sidebar"> 
				<jsp:include page="../adminCommon/left-sidebar.jsp">
					<jsp:param value="" name="activeMenu" />
				</jsp:include>
				</div>
				<div class="sz-content-wrapper examsPage">
					<%@ include file="../adminCommon/adminInfoBar.jsp"%>
					<div class="sz-content">
						<h2 class="red text-capitalize">Search Service Request
							History</h2>
						<div class="clearfix"></div>
						<div class="panel-content-wrapper" style="min-height: 450px;">
							<%@ include file="../adminCommon/messages.jsp"%>
							<form:form action="searchSR" method="post" modelAttribute="sr">
								<fieldset>
									<div class="col-md-4">
										<div class="form-group">
											<form:input id="sapId" path="sapId" type="text"
												placeholder="Student ID" class="form-control" />
										</div>


										<div class="form-group">
											<label class="control-label" for="submit"></label>
											<div class="controls">
												<button id="submit" name="submit"
													class="btn btn-large btn-primary"
													formaction="searchSRHistory">Search</button>
												<button id="cancel" name="cancel" class="btn btn-danger"
													formaction="${pageContext.request.contextPath}/home" formnovalidate="formnovalidate">Cancel</button>
											</div>
										</div>

									</div>

								</fieldset>
							</form:form>

							<%try{ %>
						</div>
						<c:choose>
							<c:when test="${rowCount > 0}">
								<h2 style="margin-left: 50px;">
									&nbsp;&nbsp;Service Requests<font size="2px">
										(${rowCount} Records Found)&nbsp;<!-- <a href="downloadSRReport">Download to Excel</a> -->
									</font>
								</h2>
								<div class="clearfix"></div>
								<div class="panel-content-wrapper">
									<div class="table-responsive">
										<table class="table table-striped table-hover"
											style="font-size: 12px">
											<thead>
												<tr>
													<th>Sr. No.</th>
													<th>Student ID</th>
													<th>Year</th>
													<th>Month</th>
													<th>Semester</th>
													<th>Status Of Service Request</th>
												</tr>
											</thead>
											<tbody>

												<c:forEach var="sr" items="${srList}" varStatus="status">
													<tr>
														<td><c:out value="${status.count}" /></td>
														<td><c:out value="${sr.sapId}" /></td>
														<td><c:out value="${sr.year}" /></td>
														<td><c:out value="${sr.month}" /></td>
														<td><c:out value="${sr.sem}" /></td>

														<td><a href="#" class="editable" id="requestStatus"
															data-type="select" data-pk="${sr.id}"
															data-source="[{value: 'Pending', text: 'Pending'},{value: 'Issued', text: 'Issued'}]"
															data-url="saveHistoryStatus">${sr.status}</a></td>


													</tr>
												</c:forEach>

											</tbody>
										</table>
									</div>
								</div>
								<br>
							</c:when>
						</c:choose>
					</div>
				</div>
			</div>
		</div>
	</div>
	<%}catch(Exception e){}%>

	<jsp:include page="../adminCommon/footer.jsp" />

	<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_STUDENTPORTAL_STATIC_RESOURCES')" />resources_2015/js/vendor/bootstrap-editable.js"></script>

	<script>
$(function() {
    //toggle `popup` / `inline` mode
    $.fn.editable.defaults.mode = 'inline';     
    
    $('.editable').each(function() {
        $(this).editable({
        	success: function(response, newValue) {
        		obj = JSON.parse(response);
                if(obj.status == 'error') {
                	return obj.msg; //msg will be shown in editable form
                }
            }
        });
    });
    
});
</script>
</body>
</html>