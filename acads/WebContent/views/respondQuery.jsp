<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!--> 

<%@page import="com.nmims.beans.PersonAcads"%>
<%@page import="com.nmims.beans.PageAcads"%>

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

</style>

<%@ include file="header.jsp"%>
	
    <section class="content-container login">
        <div class="container-fluid customTheme">
        <div class="row"><legend>Queries for ${session.subject }-${session.sessionName }</legend></div>
        <%@ include file="messages.jsp"%>
		
		<!-- Unanswered Queries section -->
		<div class="panel-body">
			<h2>Unanswered Queries (${unansweredQueriesSize })</h2>
			<c:if test="${not empty unansweredQueries}">
				
					<div id="accordion1">
					
					<c:forEach var="queryAnswer" items="${unansweredQueries}" varStatus="status">
						<h3 style="font-weight:normal;color:#000">Q. ${queryAnswer.query}</h3>
						<div style="background:#fff">Answer:
							<form:form  action="saveAnswer" method="post" modelAttribute="sessionQuery">
							<fieldset>
							<div class="col-md-18 column">
								<form:hidden path="id" value="${queryAnswer.id }"/>
								<form:hidden path="sessionId" value="${queryAnswer.sessionId }"/>
								<div class="form-group">
										<form:textarea path="answer"  maxlength="7500" class="form-control" placeholder="Enter Answer here" cols="50" rows="3"/>
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
						<h3 style="font-weight:normal;color:#000">
							Q. ${queryAnswer.query}
							<c:if test="${queryAnswer.isPublic == 'Y' }"><i class="fa-solid fa-earth-asia"></i></c:if>
							<c:if test="${queryAnswer.isPublic == 'N' }"><i class="fa-solid fa-lock"></i></c:if>
						</h3>
						<div style="background:#fff">Answer: 
							<form:form  action="saveAnswer" method="post" modelAttribute="sessionQuery">
							<fieldset>
							<div class="col-md-18 column">
								<form:hidden path="id" value="${queryAnswer.id }"/>
								<form:hidden path="sessionId" value="${queryAnswer.sessionId }"/>
								<div class="form-group">
										<textarea name="answer" maxlength="1500" class="form-control" placeholder="Enter Answer here" cols="50" rows="3" >${queryAnswer.answer}</textarea>
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
	
	</section>

	<jsp:include page="footer.jsp" />
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
