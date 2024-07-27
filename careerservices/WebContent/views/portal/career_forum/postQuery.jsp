  <!DOCTYPE html>


<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>


<html lang="en">
	

	
	
	<jsp:include page="/views/common/jscss.jsp">
		<jsp:param value="Post a Query" name="title"/>
	</jsp:include>
	
	
	
	<body>
	
		<jsp:include page="/views/common/header.jsp"/>
		
		
		
		<div class="sz-main-content-wrapper">
		
			<jsp:include page="/views/common/breadcrum.jsp">
				<jsp:param value="<a href='/careerservices/Home'>Career Services</a>;<a href='career_forum'>Career Forum</a>;<a href='viewScheduledSession?id=${session.id }'>Session Details</a>;Queries" name="breadcrumItems" />
			</jsp:include>
			
			
			<div class="sz-main-content menu-closed">
				<div class="sz-main-content-inner">
					<jsp:include page="/views/common/left-sidebar.jsp">
						<jsp:param value="Academic Calendar" name="activeMenu"/>
					</jsp:include>
						
		  				
	  				<div class="sz-content-wrapper examsPage">
  						<jsp:include page="/views/common/studentInfoBar.jsp"/>
  						<div class="sz-content">
		
							<h2 class="red text-capitalize">
								${ sessionId }
							</h2>
							<div class="clearfix"></div>
							
							<%@ include file="/views/common/messages.jsp" %>
							
							
							<c:if test="${action == 'postQueries' }">
								<div class="panel-content-wrapper row mx-2">
									<form:form  action="postQuery" class="w-100" method="post" modelAttribute="sessionQuery">
										<fieldset>
											<div class="col-12 mb-3">
												<h1>Post a Query</h1>
												<hr>
											</div>
											<form:hidden path="sessionId" value="${session.id }"/>
											<div class="form-group col-12">
												<form:select path="queryType" class="form-control" required="required" id="queryType">
												 	<form:option value="">Select Query Category</form:option>
												 	<form:option value="Academic">Academic - Faculty </form:option>
												 	<form:option value="Technical">Technical - Student Support</form:option>
												</form:select>
											</div>
											
											<div class="form-group col-12">
												<form:textarea path="query" id="query" maxlength="500" class="form-control col-12" placeholder="Please note that only Course Content and/or Session related queries have to be posted in Post Query section which will be replied within 48 working hours. Enter query here..." cols="50"/>
											</div>
											
											<div class=" form-group controls col-12">
												<button id="submit" name="submit" class="btn btn-sm btn-primary" formaction="postQuery" onClick="return confirm('Are you sure you want to submit this query?');">Post Query</button>
											</div>
							
										</fieldset>
									</form:form>
								</div>
							</c:if>
	 							
	 							
	 							<!-- My Queries section -->
							<div class="panel-content-wrapper row mx-2">
							
								<div class="col-12">
									<h1>My Queries</h1>
								</div>
								<div id="accordion1" class="col-12">
									<c:if test="${not empty myQueries}">
										<c:forEach var="queryAnswer" items="${myQueries}" varStatus="status">
											<div class="border-bottom pb-3 my-3">
												<div class="col-12">
													<h2 style="font-size:larger">
													<c:if test="${not empty queryAnswer.queryType }"> [ ${queryAnswer.queryType} ]</c:if>
														${queryAnswer.query}
													</h2>
												</div>
												<div class="col-12 my-1">
													<span class="px-3">
														<c:if test="${queryAnswer.isAnswered == 'Y' }"><i class="fa fa-check" style="color: green;"></i></c:if>
														<c:if test="${queryAnswer.isAnswered == 'N' }"><i class="fa fa-hourglass"></i></c:if>
													</span>
												</div>
												<div class="col-12 my-1" >
													<b>A</b>: ${queryAnswer.answer}
												</div>
												<div class="col-12 my-1">
													<small class="pull-right">
														Posted on ${queryAnswer.createdDate}
														<c:if test="${queryAnswer.isAnswered == 'Y' }">, Answered on ${queryAnswer.lastModifiedDate}</c:if>
													</small>
												</div>
											</div>
										</c:forEach>
									</c:if>
								</div>
							</div>
							
							<!-- Public Queries section -->
							<c:if test="${not empty publicQueries}">
								<div class="panel-content-wrapper row mx-2">
								
									<div class="col-12">
										<h1>Queries by other Students</h1>
										<hr>
									</div>
									<div id="accordion2" class="col-12">
									
									<c:forEach var="queryAnswer" items="${publicQueries}" varStatus="status">
										<div class="border-bottom pb-3 my-3">
											<div class="pb-3 my-3">
												<div class="col-12">
													<h2 style="font-size:larger">
													<c:if test="${not empty queryAnswer.queryType }"> [ ${queryAnswer.queryType} ]</c:if>
														${queryAnswer.query}
													</h2>
												</div>
												<div class="col-12 my-1">
													<span class="px-3">
														<c:if test="${queryAnswer.isAnswered == 'Y' }"><i class="fa fa-check" style="color: green;"></i></c:if>
														<c:if test="${queryAnswer.isAnswered == 'N' }"><i class="fa fa-hourglass"></i></c:if>
													</span>
												</div>
												<div class="col-12 my-1" >
													<b>A</b>: ${queryAnswer.answer}
												</div>
												<div class="col-12 my-1">
													<small class="pull-right">
														Posted on ${queryAnswer.createdDate}
														<c:if test="${queryAnswer.isAnswered == 'Y' }">, Answered on ${queryAnswer.lastModifiedDate}</c:if>
													</small>
												</div>
												<div class="col-12">
													<hr>
												</div>
											</div>
										</div>
									</c:forEach>
									</div>
								</div>
							</c:if>
						</div>
					</div>
				</div>
			</div>
		</div>
			
  	
		<jsp:include page="/views/common/footer.jsp"/>
		
		<script>
		$(document).ready(function(){
			 var select_id = document.getElementById("queryType");

			 var response =  select_id.options[select_id.selectedIndex].value;
			 
			 if(response.trim() == ""){
				 document.getElementById("query").disabled = true;
				 document.getElementById("submit").disabled = true;
			 }
		});
		
		function askToSelectQueryType()
		{
			 var select_id = document.getElementById("queryType");

			 var response =  select_id.options[select_id.selectedIndex].value;
			 
			 if(response.trim() == ""){
				 alert("Please select Query Category");
				 document.getElementById("query").disabled = true;
			 }else{
				 document.getElementById("query").disabled = false;
			 }
		}
		
		$('#queryType').on('change', function () {
			askToSelectQueryType()
		});
		
		$('#query').on('change textInput input', function () {
			askToEnterQuery()
		});
		
		function askToEnterQuery()
		{
			var query =  document.getElementById("query").value;
			 if(query.trim() != ""){
				 document.getElementById("submit").disabled = false;
			 }
		}
		
		  $(function() {
			$( "#accordion1" ).accordion({
			  collapsible: true,
			  heightStyle: "content",
			  active:false
			});
		  });
		  
		  $(function() {
			$( "#accordion2" ).accordion({
			  collapsible: true,
			  heightStyle: "content",
			  active:false
			});
		  });
		  
		  $(function() {
			$( "#accordion3" ).accordion({
			  collapsible: true,
			  heightStyle: "content",
			  active:false
			});
		  });
		  </script>
		
	</body>
</html>