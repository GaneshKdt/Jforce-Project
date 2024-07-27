<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!-->

<%@page import="com.nmims.beans.PersonAcads"%>
<%@page import="com.nmims.beans.Page"%>

<html class="no-js">
<!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<jsp:include page="jscss.jsp">
	<jsp:param value="Session Polls" name="title" />
</jsp:include>

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
				<legend>Session Polls for ${session.subject }-${session.sessionName }</legend>
			</div>
			<%@ include file="messages.jsp"%>

			<!-- Created Session Polls section -->
			<div class="panel-body">
				<h2>Total Polls (${totalPollsSize })</h2>
				<c:if test="${not empty totalPolls}">

					<div id="accordion1">

						<c:forEach var="polls" items="${totalPolls}" varStatus="status">
							<h3 style="font-weight: normal; color: #000">
								<b>${status.count}) Title : ${polls.title}</b>
							</h3>
							<div style="background: #fff">
								<c:forEach items="${polls.questions}" var="question"
									varStatus="questionStatus">
									<b><u>Question ${questionStatus.count}</u>.</b>
									<fieldset>
										<div class="col-md-18 column">
											<h4 style="font-weight: normal; color: #000">Name :
												${question.name}</h4>
											<h4 style="font-weight: normal; color: #000">Type :
												${question.type} choice</h4>
											<h4 style="font-weight: normal; color: #000">
												<u>Answers</u> :
											</h4>
											<c:forEach items="${question.answers}" var="answer"
												varStatus="answerStatus">
												<h5 style="font-weight: normal; color: #000">
													<b>${answerStatus.count}) ${answer}</b>
												</h5>
											</c:forEach>
										</div>
									</fieldset>
									<hr />
								</c:forEach>
							</div>
						</c:forEach>

					</div>
				</c:if>
			</div>

			<!-- Create Session Polls section -->
			<div class="panel-body">
				<h2>Create Session Polls</h2>
				<div class="panel-body" style="background-color: #f6f6f6;border-radius:.40rem;border:1px solid lightgray;-webkit-box-shadow: none;">
					<form:form action="createWebinarPolls" method="post"
						modelAttribute="sessionPoll">
						<fieldset>
							<div class="col-md-18 column">
								<div class="form-group">
									<input id="title" type="text" style="width: 50%;"
										placeholder="Add Session Poll Title" class="form-control"
										required="required" name="title"
										pattern="^[a-zA-Z0-9]([-:\s]*[a-zA-Z0-9])*$"
										title="Title should start with either name or number and can use hyphen(-) and colon(:) along with the title" />
									<div>
										<br /> <br />
										<h4>
											<b>Questions :</b>
										</h4>
										<div class="addQuestions">
											<%
												for (int i = 0; i < 5; i++) {
														if (i < 1) {
											%>
											<div>
												<b><%=i + 1%>) <u>Question</u> :</b><br /> <input id="name"
													type="text" style="width: 50%;"
													placeholder="Add Session Poll Question"
													class="form-control" required="required"
													name="questions[<%=i%>].name"
													pattern="[a-zA-Z0-9]+[\s\S]*$"
													title="Question should start with either name or number" /> 
													<br /> <br /> 
													<select id="type" class="form-control" style="width: 50%;"
													required="required" name="questions[<%=i%>].type">
													<option value="">Select Type</option>
													<option value="single">Single Choice</option>
													<option value="multiple">Multiple Choice</option>
												</select><br /> <b><u>Answers</u> :</b><br />
												<%
													for (int j = 0; j < 10; j++) {
																	if (j < 2) {
												%>
												<input id="answer" type="text" style="width: 50%;"
													placeholder="Add Answer <%=j + 1%>" class="form-control"
													required="required"
													name="questions[<%=i%>].answers[<%=j%>]"
													pattern="[a-zA-Z0-9]+[\s\S]*$"
													title="Answer should start with either name or number" />
												<%
													} else {
												%>
												<input id="answer" type="text" style="width: 50%;"
													placeholder="Add Answer <%=j + 1%> (Optional)"
													class="form-control"
													name="questions[<%=i%>].answers[<%=j%>]"
													pattern="[a-zA-Z0-9]+[\s\S]*$"
													title="Answer should start with either name or number" />
												<%
													}
																}
												%>
											</div>
											<%
												} else {
											%>
											<div style="margin-top: 22rem;">
												<b><%=i + 1%>) <u>Question</u> :</b><br /> <input id="name"
													type="text" style="width: 50%;"
													placeholder="Add Session Poll Question"
													class="form-control" name="questions[<%=i%>].name"
													pattern="[a-zA-Z0-9]+[\s\S]*$"
													title="Question should start with either name or number" /> 
													<br /> <br /> <select id="type" class="form-control"
													name="questions[<%=i%>].type" style="width: 50%;">
													<option value="">Select Type</option>
													<option value="single">Single Choice</option>
													<option value="multiple">Multiple Choice</option>
												</select><br /> <b><u>Answers</u> :</b><br />
												<%
													for (int j = 0; j < 10; j++) {
													if (j < 2) {
												%>
												<input id="answer" type="text" style="width: 50%;"
													placeholder="Add Answer <%=j + 1%>" class="form-control"
													name="questions[<%=i%>].answers[<%=j%>]"
													pattern="[a-zA-Z0-9]+[\s\S]*$"
													title="Answer should start with either name or number" />
												<%
													} else {
												%>
												<input id="answer" type="text" style="width: 50%;"
													placeholder="Add Answer <%=j + 1%> (Optional)"
													class="form-control"
													name="questions[<%=i%>].answers[<%=j%>]"
													pattern="[a-zA-Z0-9]+[\s\S]*$"
													title="Answer should start with either name or number" />
												<%
													}
																}
												%>
											</div>
											<%
												}
													}
											%>
										</div>
									</div>
								</div>

								<div class=" form-group controls">
									<button id="submit" name="submit"
										formaction="createWebinarPolls"
										class="btn btn-large btn-primary">Add Poll</button>
									<button id="cancel" name="cancel" class="btn btn-danger"
										formaction="acadsHome" formnovalidate="formnovalidate">Cancel</button>
								</div>
							</div>
						</fieldset>
					</form:form>
				</div>
			</div>

		</div>
	</section>

	<jsp:include page="footer.jsp" />
	<script>
		$(function() {
			$("#accordion1").accordion({
				collapsible : true,
				heightStyle : "content",
				active : false
			});
		});
	</script>


</body>
</html>
