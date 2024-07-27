<!DOCTYPE html>


<%@page import="java.util.List"%>
<%@page import="java.util.Date"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.nmims.beans.MailStudentPortalBean"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<html lang="en">



<%-- 
<jsp:include page="common/jscss.jsp">
	<jsp:param value="Welcome to Student Zone" name="title" />
</jsp:include> --%>



<body>
	<%-- 
	<%@ include file="common/header.jsp"%> --%>



	<div class="sz-main-content-wrapper">
		<%-- 
		<jsp:include page="common/breadcrum.jsp">
			<jsp:param value="Student Zone;Single Email" name="breadcrumItems" />
		</jsp:include>
 --%>

		<div class="sz-main-content menu-closed">
			<div class="sz-main-content-inner">
				<%-- 		<%@ include file="common/left-sidebar.jsp"%> --%>


				<div class="sz-content-wrapper examsPage">
					<%-- 	<%@ include file="common/studentInfoBar.jsp"%> --%>


					<div class="sz-content">

						<div class="row">
							<div class="col-md-9 col-sm-12">
								<h2 class="red text-capitalize">${mail.subject}</h2>
							</div>
							<!-- <div class="col-md-3 col-sm-12" style="margin: 1.5rem 0 1rem 0"><a href="/studentportal/myEmailCommunicationsForm" class="pull-right"><i class="fa fa-envelope" ></i> Back to Inbox</a></div> -->
						</div>

						<div class="clearfix"></div>
						<div class="panel-content-wrapper">
							<%@ include file="common/messages.jsp"%>
							<p>${mail.body}</p>
						</div>

					</div>

				</div>


			</div>

		</div>
	</div>
	<%-- <jsp:include page="common/footer.jsp" /> --%>


</body>
</html>