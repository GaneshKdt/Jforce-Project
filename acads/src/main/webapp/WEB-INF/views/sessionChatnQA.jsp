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
</style>

<%@ include file="header.jsp"%>
	
    <section class="content-container login">
        <div class="container-fluid customTheme">
        <div class="row"><legend>Queries</legend></div>
        <%@ include file="messages.jsp"%>
		
		<div class="top-goback-div">
		<h5><a  href="gotoFacultySessionList"><i class="fa fa-arrow-left"></i> Back to Session List</a></h5>
		</div>
		
		<div class="panel-body outer" >
			    <ul class="nav nav-tabs large"> 
	        <li class="active" ><a data-toggle="tab" href="#tab1">
	        <h2>Unanswered Queries (${unansweredCount})</h2></a></li>
	        <li ><a data-toggle="tab" href="#tab2">
	        <h2>Answered Queries(${answeredCount})</h2></a></li>
        </ul>
		
		<div class="tab-content" >
	        <div id="tab1" class="tab-pane fade in active">
	        <div class="pull-right search-input-placeholder">
		        <input class="input-sm search-input" type="text" id="myInput" onkeyup="filter()" placeholder="Search for questions..">
		   <span class="fa fa-search form-control-feedback"></span>
	        </div>
	        
		        <c:if test="${unansweredCount == 0}">
		        	<div class="nodata-toshow-div">
		        		<h4><i class="fa fa-exclamation-circle"></i> no Unanswered Q&A to show</h4>
		        	</div>
		        </c:if>
		        
	          	<!-- Unanswered Queries section -->
					<c:if test="${unansweredCount > 0}">
						
						<div id="accordion1">
						
							<c:forEach var="questionAnswer" items="${sessionQA}" varStatus="status">
							
							<c:if test="${questionAnswer.getStatus() == 'Open'}">
								<div class="col-md-12 qna-block">
								<h5 style="font-weight:normal;color:#000">Student : ${questionAnswer.firstName} &nbsp;&nbsp;${questionAnswer.lastName}. &nbsp;&nbsp;  SapId : ${questionAnswer.sapId}. </h5>
								<h4 class="question" style="font-weight:normal;color:#000">Q. ${questionAnswer.query} </h4>
								</div>
								<div class="col-md-12 " style="background:#fff">Answer:
									<fieldset>
<!-- 									<div class="col-md-8 column"> -->
									
<!-- 										<div class="form-group"> -->
<%-- 												<p name="answer">${questionAnswer.answer}</p> --%>
<!-- 										</div> -->
						
<!-- 									</div>  -->
									</fieldset>
									<form:form  action="saveQAAnswer" method="post" modelAttribute="sessionQn">
									<fieldset>
									<div class="col-md-8 column">
										<form:hidden path="id" value="${questionAnswer.id }"/>
										<form:hidden path="sessionId" value="${questionAnswer.sessionId }"/>
										<div class="form-group">
												<textarea name="answer" maxlength="1500" class="form-control" placeholder="Enter Answer here" cols="50" rows="3" >${queryAnswer.query}</textarea>
										</div>
						
										<div class=" form-group controls">
											<button id="submit" name="submit" class="btn btn-large btn-primary btn-sm" formaction="saveQAAnswer?isPublic=Y" onClick="return confirm('Are you sure you want to save this as Public answer? All students can view this query and answer.');">Update as Public Answer</button>
											<button id="submit" name="submit" class="btn btn-large btn-primary btn-sm" formaction="saveQAAnswer?isPublic=N" onClick="return confirm('Are you sure you want to save this as Private answer? Only student who asked can view this query and answer.');">Update as Private Answer</button>
										</div>
									</div>
									</fieldset>
									</form:form>
								</div>
								</c:if>
							</c:forEach>
						
						</div>
					</c:if>
	        </div>
	        <div id="tab2" class="tab-pane fade">
	        
		        <c:if test="${answeredCount == 0}">
		        <div class="nodata-toshow-div"><h4><i class="fa fa-exclamation-circle"></i> no answered Q&A to show</h4></div>
		        </c:if>
				<c:if test="${answeredCount > 0}">
						<div id="accordion2">
						
						<c:forEach var="questionAnswer" items="${sessionQA}" varStatus="status">
						<c:if test="${questionAnswer.getStatus() == 'Answered'}">
							<div class="col-md-12 ">
							<h5 style="font-weight:normal;color:#000">Student : ${questionAnswer.firstName} &nbsp;&nbsp;${questionAnswer.lastName}. &nbsp;&nbsp;  SapId : ${questionAnswer.sapId}. </h5>
							<h4 style="font-weight:normal;color:#000">Q. ${questionAnswer.query} </h4>
							</div>
							<div class="col-md-12 " style="background:#fff">Answer:
								<fieldset>
								<div class="col-md-8 column">
								
									<div class="form-group">
											<p name="answer">${questionAnswer.answer}</p>
									</div>
					
								</div>  
								</fieldset>  
								<form:form  action="saveQAAnswer" method="post" modelAttribute="sessionQn">
								<fieldset>
								<div class="col-md-8 column">
									<form:hidden path="id" value="${questionAnswer.id }"/>
									<form:hidden path="sessionId" value="${questionAnswer.sessionId }"/>
									<div class="form-group">
											<textarea name="answer" maxlength="1500" class="form-control" placeholder="Enter Answer here" cols="50" rows="3" >${queryAnswer.query}</textarea>
									</div>
					
									<div class=" form-group controls">
										<button id="submit" name="submit" class="btn btn-large btn-primary btn-sm" formaction="saveQAAnswer?isPublic=Y" onClick="return confirm('Are you sure you want to save this as Public answer? All students can view this query and answer.');">Update as Public Answer</button>
										<button id="submit" name="submit" class="btn btn-large btn-primary btn-sm" formaction="saveQAAnswer?isPublic=N" onClick="return confirm('Are you sure you want to save this as Private answer? Only student who asked can view this query and answer.');">Update as Private Answer</button>
									</div>
								</div>
								</fieldset>
								</form:form>
							</div>
							</c:if>
						</c:forEach>
						
						</div>
				</c:if>
	        </div>
      	</div>
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
	    $( "#accordion2" ).accordion({
		      collapsible: true,
		      heightStyle: "content",
		      active:false
		    });
	  });
	
	  function filter() {
	    // Declare variables
	    var input, filter, ul, li, a, i, txtValue;
	    input = document.getElementById('myInput');
	    
	    filter = input.value.toUpperCase();
	    
	    ul = document.getElementById("accordion1");
	    
	    li = ul.getElementsByClassName('qna-block');
	   
	    // Loop through all list items, and hide those who don't match the search query
	    for (i = 0; i < li.length; i++) {
	      a = li[i].getElementsByClassName("question")[0];
	     
	      txtValue = a.textContent || a.innerText;
	      
	      if (txtValue.toUpperCase().indexOf(filter) > -1) {
		      
	        li[i].style.display = "";
	      } else {
	        li[i].style.display = "none";
	       
	      }
	    }
	  }
	
	  </script> 
</body>
</html>
