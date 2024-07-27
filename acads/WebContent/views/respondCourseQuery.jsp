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
        
        	<div class="panel-body">

        		<button id="submit" name="submit" class="btn btn-large btn-primary btn-sm" onclick="openNewWindowForAttanchments()">Upload File & Generate Link</button>
  
        	</div>
        </div>
        
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
				
					<div class="accordion1">
					
					<c:forEach var="queryAnswer" items="${unansweredQueries}" varStatus="status">
						<div class="col-md-12 ">
						<h5 style="font-weight:normal;color:#000">Subject : ${queryAnswer.subject} &nbsp;&nbsp;  Type : ${queryAnswer.queryType} 
						&nbsp;&nbsp;  Raised on : ${queryAnswer.createdDate}&nbsp;&nbsp;&nbsp;
						<c:if test="${queryAnswer.isLiveAccess == 'Y'}">
							<i class="fa-regular fa-eye"></i>
						</c:if>
						<c:if test="${queryAnswer.isLiveAccess == 'N'}">
							<i class="fa-solid fa-video"></i>
						</c:if>
						</h5>
						<h4 style="font-weight:normal;color:#000">Q. ${queryAnswer.query} </h4>
						</div>
						<div class="col-md-12 " style="background:#fff">
							<form:form  action="saveAnswer" method="post" modelAttribute="sessionQuery">
							<fieldset>
							<div class="col-md-8 column">
							
								<form:hidden path="id" value="${queryAnswer.id }"/>
								<form:hidden path="sessionId" value="${queryAnswer.sessionId }"/>
								<form:hidden path="queryType" value="${queryAnswer.queryType }"/>
				
								<div class="form-group">
									<a href="#" onClick="window.open('/acads/admin/queryReplyForm?queryAnswerId=${queryAnswer.id}', 'Post Reply', 'width=800px,height=500px');"><b>Answer the query</b></a>
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
				
					<div class="accordion2">
					<%int sessionCount = 0;%>
					<c:forEach var="queryAnswer" items="${answeredQueries}" varStatus="status">
						<div class="col-md-12 ">
						<h5 style="font-weight:normal;color:#000">Subject : ${queryAnswer.subject} &nbsp;&nbsp; Type :  ${queryAnswer.queryType}
						&nbsp;&nbsp;  Raised on : ${queryAnswer.createdDate} &nbsp;&nbsp;&nbsp;
						<c:if test="${queryAnswer.isLiveAccess == 'Y'}">
							<i class="fa-regular fa-eye"></i>
						</c:if>
						<c:if test="${queryAnswer.isLiveAccess == 'N'}">
							<i class="fa-solid fa-video"></i>
						</c:if>
						</h5>
						<h4 style="font-weight:normal;color:#000">
							Q. ${queryAnswer.query}
							<c:if test="${queryAnswer.isPublic == 'Y' }"><i class="fa-solid fa-earth-asia"></i></c:if>
							<c:if test="${queryAnswer.isPublic == 'N' }"><i class="fa-solid fa-lock"></i></c:if>
						</h4>
						</div>
						<div class="col-md-12 " style="background:#fff">Answer: <br><br>
							<form:form  action="saveAnswer" method="post" modelAttribute="sessionQuery">
							<fieldset>
							<div class="col-md-8 column">
								<form:hidden path="id" value="${queryAnswer.id }"/>
								<form:hidden path="sessionId" value="${queryAnswer.sessionId }"/>
								<form:hidden path="queryType" value="${queryAnswer.queryType }"/>
								<div class="panel-body">
										${queryAnswer.answer}
								</div>
				
								<div class=" form-group controls">
	`							<a href="#" onClick="window.open('/acads/admin/queryReplyForm?queryAnswerId=${queryAnswer.id}', 'Post Reply', 'width=800px,height=500px');"><b>Update Answer</b></a>
								<c:if test="${queryAnswer.isForumThread == 'N' }">
								&nbsp;|&nbsp;
								<a href="#" data-toggle="modal" data-target="#queryForum<%=sessionCount%>"><b>Post Query As Forum</b></a>
								</c:if>
									<!-- <button id="submit" name="submit" class="btn btn-large btn-primary btn-sm" formaction="saveAnswer?isPublic=Y" onClick="return confirm('Are you sure you want to save this as Public answer? All students can view this query and answer.');">Update as Public Answer</button>
									<button id="submit" name="submit" class="btn btn-large btn-primary btn-sm" formaction="saveAnswer?isPublic=N" onClick="return confirm('Are you sure you want to save this as Private answer? Only student who asked can view this query and answer.');">Update as Private Answer</button> -->
								</div>
							</div>
							</fieldset>
							</form:form>
						</div>
						<%sessionCount++;%>
					</c:forEach>	
					</div>
			</c:if>
		</div>
	</div>
<%int sessionCount = 0;%>
<c:forEach var="queryAnswer" items="${answeredQueries}" varStatus="status">
<div id="queryForum<%=sessionCount%>" class="modal fade" role="dialog" data-backdrop="false">
	<div class="modal-dialog"> 
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal">&times;</button>
				<h4 class="modal-title" align="center">Forum Thread</h4>
			</div>
			<div class="modal-body">
					<div class="panel-content-wrapper">

					<form:form action="postQueryAsForum" method="post"
						modelAttribute="sessionQuery">
						<fieldset>
							<h2 style="margin-top: 0px;">Post This Query As Forum</h2>
							<div class="clearfix"></div>

							<div class="form-group">
								<div class="row">
									<div class="col-xs-18">
										<form:input path="title" id="title" type="text" placeholder="Enter Forum Title here..." required="required"/>
									</div>
								</div>
							</div>

							<div class="clearfix"></div>
							<form:hidden path="id" value="${queryAnswer.id}" />
							<form:hidden path="year" value="${queryAnswer.year}" />
							<form:hidden path="month" value="${queryAnswer.month}" />
							<form:hidden path="subject" value="${queryAnswer.subject}" />
							<form:hidden path="assignedToFacultyId" value="${queryAnswer.assignedToFacultyId}" />
							<form:hidden path="query" value="${queryAnswer.query}" />
							<div class="panel-body">
								<%-- <form:textarea path="query" id="query" maxlength="500"
									class="form-control" minlength="5" required="required"
									placeholder=""
									cols="50" rows="3" /> --%>
									${queryAnswer.query}
							</div>

							<div class=" form-group controls">
								<button id="submit" name="submit" class="btn btn-danger"
									onmouseover="this.style.color='red';"
									onmouseout="this.style.color='white';"
									formaction="postQueryAsForum"
									onClick="return confirm('Are you sure you want to post this query as forum topic?');">Create
									Forum</button>
							</div>

						</fieldset>
					</form:form>
				</div>
			</div>
			<!-- div class="modal-footer">
				<button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
			</div> -->
		</div>
	</div>
</div>

<%sessionCount++;%>
</c:forEach>
		
		<div class="wxMBA">
		<!-- Unanswered Queries section -->
		<div class="panel-body">
			<h2>Unanswered Queries (${unansweredWXQueriesSize })</h2>
			<c:if test="${not empty unansweredWXQueries}">
				
					<div class="accordion1">
					
					<c:forEach var="queryAnswer" items="${unansweredWXQueries}" varStatus="status">
						<div class="col-md-12 ">
						<h5 style="font-weight:normal;color:#000">Subject : ${queryAnswer.subject} &nbsp;&nbsp;  Type : ${queryAnswer.queryType} &nbsp;&nbsp;  Raised on : ${queryAnswer.createdDate}</h5>
						<h4 style="font-weight:normal;color:#000">Q. ${queryAnswer.query} </h4>
						</div>
						<div class="col-md-12 " style="background:#fff">
							<form:form  action="saveAnswer" method="post" modelAttribute="sessionQuery">
							<fieldset>
							<div class="col-md-8 column">
							
								<form:hidden path="id" value="${queryAnswer.id }"/>
								<form:hidden path="sessionId" value="${queryAnswer.sessionId }"/>
								<form:hidden path="queryType" value="WX_${queryAnswer.queryType }"/>
								<form:hidden path="query" value="${queryAnswer.query}" />
								<form:hidden path="sapId" value="${queryAnswer.sapId}" />
								<form:hidden path="subject" value="${queryAnswer.subject}"/>
				
								<div class=" form-group">
								<a href="#" onClick="window.open('/acads/admin/queryReplyForm?queryAnswerId=${queryAnswer.id}', 'Post Reply', 'width=800px,height=500px');"><b>Answer to the question</b></a>
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
				
					<div class="accordion2">
					<%int sessionCount1 = 0;%>
					<c:forEach var="queryAnswer" items="${answeredWXQueries}" varStatus="status">
						<div class="col-md-12 ">
						<h5 style="font-weight:normal;color:#000">Subject : ${queryAnswer.subject} &nbsp;&nbsp; Type :  ${queryAnswer.queryType} &nbsp;&nbsp;  Raised on : ${queryAnswer.createdDate}</h5>
						<h4 style="font-weight:normal;color:#000">
							Q. ${queryAnswer.query}
							<c:if test="${queryAnswer.isPublic == 'Y' }"><i class="fa-solid fa-earth-asia"></i></c:if>
							<c:if test="${queryAnswer.isPublic == 'N' }"><i class="fa-solid fa-lock"></i></c:if>
						</h4>
						</div>
						<div class="col-md-12 " style="background:#fff">Answer:<br><br>
							<form:form  action="saveAnswer" method="post" modelAttribute="sessionQuery">
							<fieldset>
							<div class="col-md-8 column">
								<form:hidden path="id" value="${queryAnswer.id }"/>
								<form:hidden path="sessionId" value="${queryAnswer.sessionId }"/>
								<form:hidden path="queryType" value="WX_${queryAnswer.queryType }"/>
								<form:hidden path="query" value="${queryAnswer.query}" />
								<form:hidden path="sapId" value="${queryAnswer.sapId}" />
								<form:hidden path="subject" value="${queryAnswer.subject}"/>
								<div class="panel-body">
										${queryAnswer.answer}
								</div>
								
								
				
								<div class=" form-group">
								<a href="#" onClick="window.open('/acads/admin/queryReplyForm?queryAnswerId=${queryAnswer.id}', 'Post Reply', 'width=800px,height=500px');"><b>Update Answer</b></a>
								<c:if test="${queryAnswer.isForumThread == 'N' }">
								&nbsp;|&nbsp;
								<a href="#" data-toggle="modal" data-target="#queryForumWx<%=sessionCount1%>"><b>Post Query As Forum</b></a>
								</c:if>
									<!-- <button id="submit" name="submit" class="btn btn-large btn-primary btn-sm" formaction="saveAnswer?isPublic=Y" onClick="return confirm('Are you sure you want to save this as Public answer? All students can view this query and answer.');">Update as Public Answer</button>
									<button id="submit" name="submit" class="btn btn-large btn-primary btn-sm" formaction="saveAnswer?isPublic=N" onClick="return confirm('Are you sure you want to save this as Private answer? Only student who asked can view this query and answer.');">Update as Private Answer</button> -->
								</div>
							</div>
							</fieldset>
							</form:form>
							<%sessionCount1++;%>
						</div>
					</c:forEach>
					
					</div>
			</c:if>
		</div>
		
		</div>
		
<%int sessionCount1 = 0;%>
<c:forEach var="queryAnswer" items="${answeredWXQueries}" varStatus="status">
<div id="queryForumWx<%=sessionCount1%>" class="modal fade" role="dialog" data-backdrop="false">
	<div class="modal-dialog"> 
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal">&times;</button>
				<h4 class="modal-title" align="center">Forum Thread</h4>
			</div>
			<div class="modal-body">
					<div class="panel-content-wrapper">

					<form:form action="postQueryAsForum" method="post"
						modelAttribute="sessionQuery">
						<fieldset>
							<h2 style="margin-top: 0px;">Post This Query As Forum</h2>
							<div class="clearfix"></div>

							<div class="form-group">
								<div class="row">
									<div class="col-xs-18">
										<form:input path="title" id="title" type="text" placeholder="Enter Forum Title here..." required="required"/>
									</div>
								</div>
							</div>

							<div class="clearfix"></div>
							<form:hidden path="id" value="${queryAnswer.id}" />
							<form:hidden path="year" value="${queryAnswer.year}" />
							<form:hidden path="month" value="${queryAnswer.month}" />
							<form:hidden path="subject" value="${queryAnswer.subject}" />
							<form:hidden path="assignedToFacultyId" value="${queryAnswer.assignedToFacultyId}" />
							<form:hidden path="query" value="${queryAnswer.query}" />
							<div class="panel-body">
								<%-- <form:textarea path="query" id="query" maxlength="500"
									class="form-control" minlength="5" required="required"
									placeholder=""
									cols="50" rows="3" /> --%>
									${queryAnswer.query}
							</div>

							<div class=" form-group controls">
								<button id="submit" name="submit" class="btn btn-danger"
									onmouseover="this.style.color='red';"
									onmouseout="this.style.color='white';"
									formaction="postQueryAsForum"
									onClick="return confirm('Are you sure you want to post this query as forum topic?');">Create
									Forum</button>
							</div>

						</fieldset>
					</form:form>
				</div>
			</div>
			<!-- div class="modal-footer">
				<button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
			</div> -->
		</div>
	</div>
</div>

<%sessionCount1++;%>
</c:forEach>
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
	    $( ".accordion1" ).accordion({
	      collapsible: true,
	      heightStyle: "content",
	      active:false
	    });
	  });
	  
	  $(function() {
	    $( ".accordion2" ).accordion({
	      collapsible: true,
	      heightStyle: "content",
	      active:false
	    });
	  });

	  function openNewWindowForAttanchments()
	      {
	  	    window.open("adhoc-upload-file-form")
	  	    }
	  </script>
	  

</body>
</html>
