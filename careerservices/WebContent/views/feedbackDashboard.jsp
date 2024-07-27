<html class="no-js">
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<%@page import="com.nmims.beans.InterviewBean"%>
<%@page import="java.util.ArrayList"%>

<jsp:include page="/views/adminCommon/jscss.jsp">
	<jsp:param value="Feedback Dashboard" name="title" />
</jsp:include>	

<%
	String feedbackClass="";
	String pendingFeedbackClass="";
	String activeMenu = request.getParameter("activeMenu"); 
	try{
		if(activeMenu.equalsIgnoreCase("feedback")){
			feedbackClass="active";
		}else if(activeMenu.equalsIgnoreCase("pendingFeedback")){
			pendingFeedbackClass="active"; 
		}else{
			feedbackClass="active";
		}
	
	}catch(Exception e){
		feedbackClass="active";
	}

%>

<body class="inside">

	<jsp:include page="/views/adminCommon/header.jsp">
		<jsp:param value="Practice Interview" name="title" />
	</jsp:include>


	<section class="content-container login">
		<div class="container-fluid customTheme">

			<div class="row">
				<legend>${ type }- Feedback Dashboard</legend>
			</div>

			<jsp:include page="/views/messages.jsp" />
 
			<div class="tab-grp1 tabbable-panel" style="margin-top: 20px;">
				<div class="tabbable-line">
					<ul class="nav nav-tabs" style="background: none;">  
						<li class="<%= feedbackClass %>" style=" border-right: 2px solid #B2AAAA;">  
							<a data-toggle="tab" href="#feedback"> 
								<div class="text-center">
									<p>Feedbacks (${ fn:length(feedbacks) })</p>
								</div>
							</a>
						</li>
						<li class="<%= pendingFeedbackClass %>" style="border-right: 2px solid #B2AAAA;">
							<a data-toggle="tab" href="#pendingFeedback">
								<div class="text-center">
									<p>Pending Feedbacks (${ fn:length(pendingFeedbacks) })</p>
								</div>
							</a>
						</li>
					</ul>
				</div>
			</div>

			<div class="tab-content">
				<div id="feedback" class="tab-pane fade in <%= feedbackClass %>">
					<% try { %>
					
					<c:choose>
						<c:when test="${ type == 'Career Counselling'}">
							<%@ include file="/views/portal/counselling/feedbacks.jsp"%>
						</c:when>
						<c:otherwise>
							<%@ include file="/views/portal/interview/feedbacks.jsp"%>
						</c:otherwise>
					</c:choose>
				
					<%
						} catch (Exception e) {
						}
					%>
				</div>
				<div id="pendingFeedback" class="tab-pane fade in <%= pendingFeedbackClass %>">
					<c:choose>
						<c:when test="${ type == 'Career Counselling'}">
							<%@ include file="/views/portal/counselling/pendingFeedbacks.jsp"%>
						</c:when>
						<c:otherwise>
							<%@ include file="/views/portal/interview/pendingFeedbacks.jsp"%>
						</c:otherwise>
					</c:choose>
				</div>
			</div>
		</section>

	<jsp:include page="/views/adminCommon/footer.jsp" />
	
	<script>
		 $(document).ready(function() {
			$('#feedbackTable').DataTable();
		 	$('#pendingFeedbacksTable').DataTable(); 
		 } );
	</script>

</body>
</html>
