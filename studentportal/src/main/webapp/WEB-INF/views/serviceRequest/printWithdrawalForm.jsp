
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

%>
<jsp:include page="../adminCommon/jscss.jsp">
	<jsp:param value="Search Service Request" name="title" />
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
				<jsp:include page="../adminCommon/left-sidebar.jsp">
					<jsp:param value="" name="activeMenu" />
				</jsp:include>
				<div class="sz-content-wrapper examsPage">
					<%-- <%@ include file="../adminCommon/adminInfoBar.jsp"%> --%>
					<div class="sz-content">
						<h2 class="red text-capitalize">Print Mass withdrawal Form</h2>
						<div class="clearfix"></div>
						<div class="panel-content-wrapper" style="min-height: 450px;">
							<%@ include file="../adminCommon/messages.jsp"%>
								<form:form  action="" method="post" modelAttribute="bean">
									<fieldset>
									
									
									<div class="col-md-6 column">
										<div class="form-group">
										<form:textarea  name="serviceRequestIdList" path="sapId" cols="50" rows="7" placeholder="Enter different Service Request Ids in new lines"></form:textarea>
										</div>
										
										<div class="form-group">
											<label class="control-label" for="submit"></label>
						
												<button id="submit" name="submit" class="btn btn-large btn-primary" formaction="printFormForWithdrawal">Generate</button>
												<button id="cancel" name="cancel" class="btn btn-danger" formaction="home" formnovalidate="formnovalidate">Cancel</button>
						
										</div>
											 
									</div>
						
								</fieldset>
								</form:form>
								<%if("true".equals((String)request.getAttribute("success"))){ %>
									<div class=""> Click <a href="downloadFile?filePath=${filePath}">here to download</a></div>
								<%} %>
						</div>
				</div>
			</div>
		</div>
	</div>
	<jsp:include page="../adminCommon/footer.jsp" />

	<script src="resources_2015/js/vendor/bootstrap-editable.js"></script>

	<script>

</script>
</body>
</html>