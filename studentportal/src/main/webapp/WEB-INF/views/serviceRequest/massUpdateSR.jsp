 
<!DOCTYPE html>
<%@page import="java.util.*"%>
<%@page import="java.text.DateFormat"%>
<html lang="en">


<%@page import="com.nmims.beans.PageStudentPortal"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%
HashMap<String,String> mapOfActiveSRTypesAndTAT = (HashMap<String,String>)request.getAttribute("mapOfActiveSRTypesAndTAT");
if("true".equals( (String)request.getAttribute("error"))) {
try{String errormsg = (String)request.getAttribute("errorMessage");
errormsg = errormsg.replaceAll(",", "<br>");
errormsg = errormsg.replaceAll("\\[|\\]", "");
request.setAttribute("errorMessage",errormsg);
} catch(Exception e){}
}
%>
<jsp:include page="../adminCommon/jscss.jsp">
	<jsp:param value="Update SR Status" name="title" />
</jsp:include> 
<body>
	<%@ include file="../adminCommon/header.jsp"%>
	<div class="sz-main-content-wrapper">

		<jsp:include page="../adminCommon/breadcrum.jsp">
			<jsp:param value="Student Portal;Update SR Status"
				name="breadcrumItems" />
		</jsp:include> 
        <c:set var = "statusList" scope = "session" value = "Cancelled,Closed,In Progress,Payment Failed,Payment Pending,Submitted"/>
		<div class="sz-main-content menu-closed">
			<div class="sz-main-content-inner">
				<jsp:include page="../adminCommon/left-sidebar.jsp">
					<jsp:param value="" name="activeMenu" />
				</jsp:include>
				<div class="sz-content-wrapper examsPage">
					<%@ include file="../adminCommon/adminInfoBar.jsp"%>
					<div class="sz-content">
						<h2 class="red text-capitalize">Update SR Status</h2>
						<div class="clearfix"></div>
						<div class="panel-content-wrapper" style="min-height: 450px;">
							<%@ include file="../adminCommon/messages.jsp"%>
							<form:form modelAttribute="sr" enctype="multipart/form-data"  method="post"	action="updateSRStatus">
								<fieldset>
									<div class="col-md-4">
										<label class="control-label" for="status">Service Request Ids</label>
										<div class="form-group">
											<form:textarea id="idList" path="serviceRequestIdList"  required="required"
												cols="50" rows="7" placeholder="Enter different Service Request Ids in new lines" class="form-control" />
										</div>
										<div class="form-group">
										<label class="control-label" for="status">Select SR Status</label>
	 										<form:select path="status" id="status" class="form-control selectStatus"  >
											    <c:forEach items="${statusList}" var="status">
											        <option value="${status}">${status}</option> 
											    </c:forEach> 
											</form:select> 
										</div>
										<div class="form-group">
											
											<div class="controls">
												<button id="submit" name="submit"
													class="btn btn-large btn-primary">Upload</button> 
												<a  href="searchSRForm"
													class="btn btn-large btn-danger">search SR</a> 	 
											</div>
										</div>

									</div>

								</fieldset>
							</form:form>
									</div>
					
							 
				</div>
					   
				</div>
				<br>  
				
				</div>
			</div>
		</div> 
	<jsp:include page="../adminCommon/footer.jsp" />

	<script src="resources_2015/js/vendor/bootstrap-editable.js"></script>

	<script>

</script>
</body>
</html>