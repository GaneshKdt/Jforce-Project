<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!--> 

<%@page import="com.nmims.beans.PersonAcads"%>
<%@page import="com.nmims.beans.Page"%>

<html class="no-js"> <!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<jsp:include page="jscss.jsp">
<jsp:param value="Queries" name="title" />
</jsp:include>

<body class="inside">

<style>

.ui-state-hover{
	background:#DED9DA;
}
.btn-default{
	background-color:#f6f6f6;
}
.tab-active,.tab-active:hover{
	background-color:#c72127 !important;
	color:white !important;
}
</style>

<%@ include file="header.jsp"%>
	
    <section class="content-container login">
        <div class="container-fluid customTheme">
        <div class="row"><legend>Queries</legend></div>
        <%@ include file="messages.jsp"%>
		<div class="tab-view">
			<button class="btn btn-default tab-active tab-btn" data-type="regular">Regular</button>
			<button class="btn btn-default tab-btn" data-type="wx">MBA (WX)</button>
		</div>
		<div style="background-color:red;padding:1px;"></div>
		<div class="regularMBA">
		<!-- Unanswered Queries section -->
		<div class="panel-body">
			<h2>Unanswered Queries (${unansweredQueriesSize })</h2>
			<c:if test="${not empty unansweredQueries}">
				
					<div id="accordion1">
					
					<c:forEach var="queryAnswer" items="${unansweredQueries}" varStatus="status">
						<div class="col-md-12 ">
						<h5 style="font-weight:normal;color:#000">Subject : ${queryAnswer.subject}. &nbsp;&nbsp;  Type : ${queryAnswer.queryType}. </h5>
						<h4 style="font-weight:normal;color:#000">Q. ${queryAnswer.query} </h4>
						</div>
						<div class="col-md-12 " style="background:#fff">Answer:
							<form:form  action="saveAnswer" method="post" modelAttribute="sessionQuery">
							<fieldset>
							<div class="col-md-8 column">
							
								<form:hidden path="id" value="${queryAnswer.id }"/>
								<form:hidden path="sessionId" value="${queryAnswer.sessionId }"/>
								<form:hidden path="queryType" value="${queryAnswer.queryType }"/>
								<div class="form-group">
										<form:textarea path="answer"  maxlength="7500" class="form-control" placeholder="Enter Answer here" cols="50" rows="3" required ="true" />
								</div>
				
								<div class=" form-group controls">
									<button id="submit" name="submit" class="btn btn-large btn-primary btn-sm" formaction="saveAnswer?isPublic=Y" onClick="return confirm('Are you sure you want to save this as Public answer? All students can view this query and answer.');">Save as Public Answer</button>
									<button id="submit" name="submit" class="btn btn-large btn-primary btn-sm" formaction="saveAnswer?isPublic=N" onClick="return confirm('Are you sure you want to save this as Private answer? Only student who asked can view this query and answer.');">Save as Private Answer</button>
								</div>
							</div>
							</fieldset>
							</form:form>
						</div>
					</c:forEach>
					
					</div>
			</c:if>
		</div>
		
		<!-- Answered Queries section -->
		<div class="panel-body">
			<h2>Answered Queries (${answeredQueriesSize })</h2>
			<c:if test="${not empty answeredQueries}">
				
					<div id="accordion2">
					
					<c:forEach var="queryAnswer" items="${answeredQueries}" varStatus="status">
						<div class="col-md-12 ">
						<h5 style="font-weight:normal;color:#000">Subject : ${queryAnswer.subject}. &nbsp;&nbsp; Type :  ${queryAnswer.queryType}. </h5>
						<h4 style="font-weight:normal;color:#000">
							Q. ${queryAnswer.query}
							<c:if test="${queryAnswer.isPublic == 'Y' }"><i class="fa fa-globe"></i></c:if>
							<c:if test="${queryAnswer.isPublic == 'N' }"><i class="fa fa-lock"></i></c:if>
						</h4>
						</div>
						<div class="col-md-12 " style="background:#fff">Answer: 
							<form:form  action="saveAnswer" method="post" modelAttribute="sessionQuery">
							<fieldset>
							<div class="col-md-8 column">
								<form:hidden path="id" value="${queryAnswer.id }"/>
								<form:hidden path="sessionId" value="${queryAnswer.sessionId }"/>
								<form:hidden path="queryType" value="${queryAnswer.queryType }"/>
								<div class="form-group">
										<textarea name="answer" maxlength="1500" class="form-control" placeholder="Enter Answer here" cols="50" rows="3" required ="true">${queryAnswer.answer}</textarea>
								</div>
				
								<div class=" form-group controls">
									<button id="submit" name="submit" class="btn btn-large btn-primary btn-sm" formaction="saveAnswer?isPublic=Y" onClick="return confirm('Are you sure you want to save this as Public answer? All students can view this query and answer.');">Update as Public Answer</button>
									<button id="submit" name="submit" class="btn btn-large btn-primary btn-sm" formaction="saveAnswer?isPublic=N" onClick="return confirm('Are you sure you want to save this as Private answer? Only student who asked can view this query and answer.');">Update as Private Answer</button>
								</div>
							</div>
							</fieldset>
							</form:form>
						</div>
					</c:forEach>
					
					</div>
			</c:if>
		</div>
		
		</div>
		
		<div class="wxMBA">
		<!-- Unanswered Queries section -->
		<div class="panel-body">
			<h2>Unanswered Queries (${unansweredWXQueriesSize })</h2>
			<c:if test="${not empty unansweredWXQueries}">
				
					<div id="accordion1">
					
					<c:forEach var="queryAnswer" items="${unansweredWXQueries}" varStatus="status">
						<div class="col-md-12 ">
						<h5 style="font-weight:normal;color:#000">Subject : ${queryAnswer.subject}. &nbsp;&nbsp;  Type : ${queryAnswer.queryType}. </h5>
						<h4 style="font-weight:normal;color:#000">Q. ${queryAnswer.query} </h4>
						</div>
						<div class="col-md-12 " style="background:#fff">Answer:
							<form:form  action="saveAnswer" method="post" modelAttribute="sessionQuery">
							<fieldset>
							<div class="col-md-8 column">
							
								<form:hidden path="id" value="${queryAnswer.id }"/>
								<form:hidden path="sessionId" value="${queryAnswer.sessionId }"/>
								<form:hidden path="queryType" value="WX_${queryAnswer.queryType }"/>
								<form:hidden path="query" value="${queryAnswer.query}" />
								<form:hidden path="sapId" value="${queryAnswer.sapId}" />
								<form:hidden path="subject" value="${queryAnswer.subject}"/>
								<div class="form-group">
										<form:textarea path="answer"  maxlength="7500" class="form-control" placeholder="Enter Answer here" cols="50" rows="3" required ="true"/>
								</div>
				
								<div class=" form-group controls">
									<button id="submit" name="submit" class="btn btn-large btn-primary btn-sm" formaction="saveAnswer?isPublic=Y" onClick="return confirm('Are you sure you want to save this as Public answer? All students can view this query and answer.');">Save as Public Answer</button>
									<button id="submit" name="submit" class="btn btn-large btn-primary btn-sm" formaction="saveAnswer?isPublic=N" onClick="return confirm('Are you sure you want to save this as Private answer? Only student who asked can view this query and answer.');">Save as Private Answer</button>
								</div>
							</div>
							</fieldset>
							</form:form>
						</div>
					</c:forEach>
					
					</div>
			</c:if>
		</div>
		
		<!-- Answered Queries section -->
		<div class="panel-body">
			<h2>Answered Queries (${answeredWXQueriesSize })</h2>
			<c:if test="${not empty answeredWXQueries}">
				
					<div id="accordion2">
					
					<c:forEach var="queryAnswer" items="${answeredWXQueries}" varStatus="status">
						<div class="col-md-12 ">
						<h5 style="font-weight:normal;color:#000">Subject : ${queryAnswer.subject}. &nbsp;&nbsp; Type :  ${queryAnswer.queryType}. </h5>
						<h4 style="font-weight:normal;color:#000">
							Q. ${queryAnswer.query}
							<c:if test="${queryAnswer.isPublic == 'Y' }"><i class="fa fa-globe"></i></c:if>
							<c:if test="${queryAnswer.isPublic == 'N' }"><i class="fa fa-lock"></i></c:if>
						</h4>
						</div>
						<div class="col-md-12 " style="background:#fff">Answer: 
							<form:form  action="saveAnswer" method="post" modelAttribute="sessionQuery">
							<fieldset>
							<div class="col-md-8 column">
								<form:hidden path="id" value="${queryAnswer.id }"/>
								<form:hidden path="sessionId" value="${queryAnswer.sessionId }"/>
								<form:hidden path="queryType" value="WX_${queryAnswer.queryType }"/>
								<form:hidden path="query" value="${queryAnswer.query}" />
								<form:hidden path="sapId" value="${queryAnswer.sapId}" />
								<form:hidden path="subject" value="${queryAnswer.subject}"/>
								<div class="form-group">
										<textarea name="answer" maxlength="1500" class="form-control" placeholder="Enter Answer here" cols="50" rows="3" required ="true">${queryAnswer.answer}</textarea>
								</div>
				
								<div class=" form-group controls">
									<button id="submit" name="submit" class="btn btn-large btn-primary btn-sm" formaction="saveAnswer?isPublic=Y" onClick="return confirm('Are you sure you want to save this as Public answer? All students can view this query and answer.');">Update as Public Answer</button>
									<button id="submit" name="submit" class="btn btn-large btn-primary btn-sm" formaction="saveAnswer?isPublic=N" onClick="return confirm('Are you sure you want to save this as Private answer? Only student who asked can view this query and answer.');">Update as Private Answer</button>
								</div>
							</div>
							</fieldset>
							</form:form>
						</div>
					</c:forEach>
					
					</div>
			</c:if>
		</div>
		
		</div>
	</div>
	
	</section>

	<jsp:include page="footer.jsp" />
	<script>
		$(document).ready(function(){
			$('.regularMBA').show();
			$('.wxMBA').hide();
			$('.tab-btn').click(function(){
				if($(this).attr('data-type') == 'regular'){
					$('.regularMBA').show();
					$('.wxMBA').hide();
				}else if($(this).attr('data-type') == 'wx'){
					$('.regularMBA').hide();
					$('.wxMBA').show();
				}
				$('.tab-active').removeClass('tab-active');
				$(this).addClass('tab-active');
			});
			
		});
	</script>
	<script>
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
	  </script>
	  

</body>
</html>
