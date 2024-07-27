<%-- <!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!-->

<%@page import="com.nmims.beans.PersonAcads"%>
<%@page import="com.nmims.beans.PageAcads"%>

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

Commented the old one as there were more changes 
 --%>
 
 
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
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/2.1.1/jquery.min.js"></script>
<jsp:include page="jscss.jsp">
	<jsp:param value="Session Polls" name="title" />
</jsp:include>
<%
	String webinarId = (String) request.getSession().getAttribute("webinarId");
	String acadSessionId = (String) request.getSession().getAttribute("acadSessionId");
	request.setAttribute("webinarId",webinarId);	
%>

<body class="inside">

	<style>
.ui-state-hover {
	background: #DED9DA;
}
</style>

	<%@ include file="header.jsp"%>

	<section class="content-container login">
		<div class="container-fluid customTheme"><!-- First div started -->
			<div class="row">
				<legend>Session Polls for ${session.subject }-${session.sessionName }</legend>
			</div>
			<%@ include file="messages.jsp"%>

			<!-- Created Session Polls section : start -->
			<div class="panel-body" id="totalPolls">
				<h2>Total Polls (${totalPollsSize})</h2>
				<c:if test="${not empty totalPolls}">

					<div id="accordion1">

						<c:forEach var="polls" items="${totalPolls}" varStatus="status">
						<c:url value="getWebinarPoll" var="getWebinarPoll">
						  <c:param name="webinarId" value="${webinarId}" />
						  <c:param name="pollId" value="${polls.id}" />
						</c:url>
							<h3 id="${polls.id}" style="font-weight: normal; color: #000">
								<b>${status.count}) Title : ${polls.title}</b>
							</h3>

							<div style="background: #fff">
								<div class="panel-body" style="float: right;">
									<a id="edit" href="${getWebinarPoll}"
										onclick="callUpdate('${polls.id}')"><i class="fa-solid fa-pen-to-square"
										style="font-size: 2.2rem; color: gray; border: 1px solid lightgray; padding: 0.7rem; border-radius: .5rem;"></i></a>
									<c:url var="deleteWebinarPollUrl" value="deleteWebinarPoll">
										<c:param name="webinarId" value="${webinarId}" />
										<c:param name="pollId" value="${polls.id}" />
									</c:url> 
									<a id="delete" href="${deleteWebinarPollUrl}"
										onclick="return confirm('Are you sure you want to delete this poll?');"><i class="fa-solid fa-trash"
										style="font-size: 2.2rem; color: #c72027; border: 1px solid lightgray; padding: 0.7rem 0.9rem; border-radius: .5rem;"></i></a>
								</div>
								<br />
								<br />
								<br />
								<c:forEach items="${polls.questions}" var="question"
									varStatus="questionStatus">
									<div class="panel-body" style="margin-top: 2rem;">
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
									</div>
									<!-- <hr style="border:1px solid whitesmoke;"/> -->
								</c:forEach>
							</div>
						</c:forEach>

					</div>
				</c:if>
			</div>
			<!-- Created Session Polls section : end -->
	
			<!-- Create/Update Session Polls section -->
			
			<div class="panel-body" id="sessionPolls">
				<h2>Create/Update Session Polls</h2>
				<div class="panel-body"
					style="background-color: #f6f6f6; border-radius: .40rem; border: 1px solid lightgray; -webkit-box-shadow: none;">
					<form:form action="createWebinarPolls" method="post"
						modelAttribute="sessionPoll">
						<form:hidden path="id" id="pollId" name="id" />
						<!-- for each main-->	
						<fieldset>
							<div class="col-md-18 column">
							  			
								<div class="form-group">
									<form:input  type="text" style="width: 50%;"
									    class="form-control" placeholder="Add Session Poll Title"
										required="required" path="title"
										pattern="^[A-Z]([-:\s]*[a-zA-Z0-9])*$"
										title="Title should start with Capital letter and can use hyphen(-) and colon(:) along with the title eg. Practice" />
									</div>
								
							
							
										<br /> <br />
										<h4>
											<b>Questions :</b>
										</h4>
										<div class="addQuestions">
											<%
												for (int i = 0; i < 5; i++) {
													request.setAttribute("i",i);
													
													if(i<1){
															
											%>											
											


											<div class="panel-body" style="margin-top: 2rem;">

												<b>${i+1}) <u>Question</u> :</b><br /> <form:input
													id="name${i}" type="text" style="width: 50%;"
													required="required"
													placeholder="Add Session Poll Question" path="questions[${i}].name"
													class="form-control" name="questions[${i}].name"
													pattern="^[A-Z][a-zA-z0-9]+[\s\S]*$"
													title = "Question must start with Capital letter. Eg. Are you Ready?" /> <br /> <br /> <form:select
													id="type${i}" class="form-control" path="questions[${i}].type"
													name="questions[${i}].type" style="width: 50%;">
													<form:option value="">Select Type</form:option>
													<form:option value="single">Single Choice</form:option>
													<form:option value="multiple">Multiple Choice</form:option>

												</form:select><br /> <b><u>Answers</u> :</b><br />
												
												
									<!-- 		<div class="panel-body" style="margin-top: 4rem;">

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
												Commented By Riya as there was similar code-->

											
												<%
													for (int j = 0; j < 10; j++) {
														request.setAttribute("j",j);
																	if (j < 2) {
												%>
												<form:input id="answer" type="text" style="width: 50%;"
												    required="required"
												    onclick="validateType([${i}])"
													placeholder="Add Answer ${j + 1} Compulsory" class="form-control"
													name="questions[${i}].answers[${j}]"
													path="questions[${i}].answers[${j}]"
													pattern="[a-zA-Z0-9]+[\s\S]*$"
													title="Answer should start with either name or number" />
												<%
													} else {
												%>
												<form:input id="answer" type="text" style="width: 50%;"
													onclick="validateType([${i}])"
													placeholder="Add Answer ${j + 1} (optional)" class="form-control"
													name="questions[${i}].answers[${j}]"
													path="questions[${i}].answers[${j}]"
													pattern="[a-zA-Z0-9]+[\s\S]*$"
													title="Answer should start with either name or number" />
												<%
													}
																}
												%>
											</div>
											
											<% } else {
											%>
											<div class="panel-body" style="margin-top: 2rem;">

												<b>${i+1}) <u>Question</u> :</b><br /> <form:input
													id="name${i}" type="text" style="width: 50%;"
													onmouseout="validateAns([${i}])"
													placeholder="Add Session Poll Question.Answers will not be added if it is empty." path="questions[${i}].name"
													class="form-control myQuestions" name="questions[${i}].name"
													pattern="^[A-Z][A-Za-z0-9]+[\s\S]*$" oncut="return false"
													title = "Question must start with Capital letter. Eg. Are you Ready?" /> <br /> <br /> <form:select
													id="type${i}" class="form-control" path="questions[${i}].type"
													name="questions[${i}].type" style="width: 50%;">
													<form:option value="">Select Type</form:option>
													<form:option value="single">Single Choice</form:option>
													<form:option value="multiple">Multiple Choice</form:option>
												
												</form:select><br /> <b><u>Answers</u> :</b><br />
												
												
									<!-- 		<div class="panel-body" style="margin-top: 4rem;">

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
												Commented By Riya as it was a similar code-->

											
												<%
													for (int j = 0; j < 10; j++) {
														request.setAttribute("j",j);
																	if (j < 2) {
												%>
												<form:input id="answers${i}${j}" type="text" style="width: 50%;"
													onclick="validateType([${i}])"
													placeholder="Add Answer ${j + 1} Compulsory" class="form-control"
													name="questions[${i}].answers[${j}]"
													path="questions[${i}].answers[${j}]"
													pattern="[a-zA-Z0-9]+[\s\S]*$"
													title="Answer should start with either name or number" />
												<%
													} else {
												%>
												<form:input id="answer" type="text" style="width: 50%;"
													onclick="validateType([${i}])"
													placeholder="Add Answer ${j + 1} (optional)" class="form-control"
													name="questions[${i}].answers[${j}]"
													path="questions[${i}].answers[${j}]"
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
									

								<div class=" form-group controls">
									<button id="submit" name="submit" onclick="return validation(e)"
										formaction="createWebinarPolls"
										class="btn btn-large btn-primary">Submit Poll</button>
										
										<c:url var="resetForm" value="viewAddSessionPollsForm">
											<c:param name="id" value="<%=acadSessionId%>" />
										</c:url>
									<button id="cancel" name="cancel" class="btn btn-danger"
										formaction="${resetForm}" formnovalidate="formnovalidate">Cancel</button>
								</div>
							</div>
						</fieldset>
						
					</form:form>
				</div>
			</div>

		</div> <!-- First div closed -->
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

	<script>
		var msg='${msg}';
		if(msg.length!=0){						
			console.log("msg",msg)
 			/* $("html, body").animate({scrollTop:$("html, body").offset().top},0);
			$('html, body').animate({scrollTop: $("div#sessionPolls").offset().top}, 1000);	 */
			
		}
		
		var formaction='${formaction}';
		if(formaction.length!=0){
			$("#submit").attr("formaction",formaction);
			
		}
	</script>
	
	<script>
	 function validateType(i,j){
		   
		   var a = "type"+i;
		   var d="name"+i;
		   var b = ""; 
		   
		   var name=document.getElementById(d).value;
		   var type=document.getElementById(a).value;
		   
		   
		   //check whether question is empty or not
		   if(name.length == 0)
		   {
			   alert("Enter the question Name please.");
			   document.getElementById(d).focus;
		   }
		    //check whether answer is empty or not
		    if(type == b){
		    	alert("Please enter the Type");
		    	document.getElementById(a).focus();
		    }
	 }
	 
	 function validateAns(value){
		 
		 var a="name"+value;
		 var name=document.getElementById(a).value;
         if(name.length != 0){
        	 var b="answers"+value+0;
        	 document.getElementById(b).required=true;
        	 var c="answers"+value+1;
        	 document.getElementById(c).required=true;
         }
         
         
     }

	 $(document).on("input",".myQuestions",function(){
		 let val = $(this).val();
		 if(val==""){
		 alert('Please enter the question. Answers will not be added if question is empty.');
		 }
	 });
	      
	</script>
	
	
	
	

</body>
</html>
 