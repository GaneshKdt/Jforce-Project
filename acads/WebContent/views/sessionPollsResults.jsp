<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!-->

<%@page import="com.nmims.beans.PersonAcads"%>
<%@page import="com.nmims.beans.PageAcads"%>

<html class="no-js">
<!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/functions" prefix = "fn" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<jsp:include page="jscss.jsp">
	<jsp:param value="Session Polls Results" name="title" />
</jsp:include>
<%
	String webinarId = (String) request.getSession().getAttribute("webinarId");
	String acadSessionId = (String) request.getSession().getAttribute("acadSessionId");
	request.setAttribute("webinarId", webinarId);
%>

<body class="inside">

	<style>
.ui-state-hover {
	background: #DED9DA;
}
</style>

	<%@ include file="header.jsp"%>

	<section class="content-container login">
		<div class="container-fluid customTheme">
			<div class="row">
				<legend>Session Polls Results for ${session.subject }-${session.sessionName }</legend>
			</div>
			<%@ include file="messages.jsp"%>

			<!-- Session Polls Results section :start -->
			<div class="panel-body">
				<h2>Total Submissions (${totalQuesionsSize})</h2>
				<c:if test="${not empty totalQuesions.questions}">
					<input type="hidden" name="title" id="title"
						value="${totalQuesions.title}" />
					<div id="accordion">
						<c:forEach var="question" items="${totalQuesions.questions}"
							varStatus="questionStatus">
							<%-- <h4 id="${question.email}"
								style="font-weight: normal; color: #000">
								<b>${questionStatus.count}) <u>Name</u> : ${question.name}&nbsp;&nbsp; | &nbsp;&nbsp;<u>Email</u>
									: ${question.email}
								</b>
							</h4>
               --%>
               				<h4 id="${question.email}"
								style="font-weight: normal; color: #000">
								<b>${questionStatus.count}. ${question.sapid}
								</b>
							</h4>
							<div style="background: #fff">
							
								<c:forEach items="${question.question_details}" var="qDetails"
									varStatus="qDetailsStatus">
								   
									<div class="panel-body" style="margin-top: 2rem;">
										<fieldset>
												<h4 style="font-weight: normal; color: #000" ><u>Title</u>: ${qDetails.pollName}</h4>
												<h4 style="font-weight: normal; color: #000"><b>${qDetailsStatus.count}) <u>Question</u> :
													${qDetails.question}</b></h4>
												<div><h4 style="font-weight: normal; color: #000"><b><u>Answer</u> :</b></h4>

												<c:set var="qDetailsAnswer" value="${qDetails.answer}" />															
												<c:set var = "answers" value = "${fn:split(qDetailsAnswer, ';')}" />
												<c:forEach items="${answers}" var="answer" varStatus="answerStatus">
													<span><b>${answerStatus.count}) ${answer}</b></span><br/>
												</c:forEach>																																			    											      	
												</div>
											
										</fieldset>
									</div>
								 
								</c:forEach>
								
							</div>
						</c:forEach>

					</div>
				</c:if>
			</div>
			<!-- Session Polls Results section : end -->
		</div>
	</section>

	<jsp:include page="footer.jsp" />
	<script>
		$(function() {
			$("#accordion").accordion({
				collapsible : true,
				heightStyle : "content",
				active : false
			});

		});
	</script>
	
	
	
</body>
</html>
