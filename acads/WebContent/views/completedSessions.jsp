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
.list-group.hoverable .list-group-item:hover{
	border:1px solid #9c9c9c!important;
	color: #dc0000; 
	margin-bottom: 1px;
	cursor:pointer; 
}
.list-group.hoverable .list-group-item:hover h5{ 

    font-weight: 600!important;
}
.list-group.hoverable .list-group-item{
	padding: 2rem;
}
    
</style> 
 
<%@ include file="header.jsp"%>
	
    <section class="content-container login">
        <div class="container-fluid customTheme">
        <div class="row"><legend>Sessions</legend></div>
        <%@ include file="messages.jsp"%>
		 
		
		<div class="top-goback-div">
		<h5><a  href="/studentportal/home"><i class="fa-solid fa-arrow-left"></i> Back to Home</a></h5>
		</div>
		
		<div class="panel-body outer" >
			<ul class="nav nav-tabs large"> 
		        <li class="active" ><a data-toggle="tab" href="#tab1">
		        <h2>Pg Sessions(${pgSessionsSize})</h2></a></li>
		        <li ><a data-toggle="tab" href="#tab2">
		        <h2>Mbawx & M.Sc. (AI & ML Ops) Sessions(${mbawxSessionsSize})</h2></a></li>
        	</ul>
		
			<div class="tab-content" >
		        <div id="tab1" class="tab-pane fade in active">
		        
			        <c:if test="${pgSessionsSize == 0}">
			        	<div class="nodata-toshow-div">
			        		<h4><i class="fa-solid fa-circle-exclamation"></i> no PG Sessions to show</h4>
			        	</div>
			        </c:if>
			        <ul class="list-group hoverable">

						<c:if test="${not empty pgSessions}">
						
							<div id="session-list"> 
								<c:forEach var="sessions" items="${pgSessions}" varStatus="status">
								  <li class="list-group-item"  onclick="qaHref('/acads/admin/gotoChatandQA?sessionId=${sessions.id}');">
									<div class="row" >
										<div class="col-md-10">
											<h5 style="font-weight:normal;color:#000">
												Subject : ${sessions.subject}. &nbsp;&nbsp;&nbsp; Date :  ${sessions.date}. &nbsp;&nbsp;&nbsp;
												<c:if test="${not empty sessions.getMonth() && not empty sessions.getYear()}">  
												Academic Cycle :  ${sessions.month} &nbsp; ${sessions.year}.  
												</c:if> 
												
												<c:if test="${not empty sessions.getTrack()}">  
												Track / Group :  ${sessions.track}} .
												</c:if>
											</h5>
											<h4 style="font-weight:normal;color:#000">${sessions.sessionName}</h4>
											
										</div>
										<div class="col-md-2">
												<h4 style="font-weight:normal;color:#000">Pending Query : ${sessions.count}</h4>
										</div>
									</div>
									
									</li>
								</c:forEach>
							</div>
						</c:if>
					</ul>
				 </div>
				 
				 <div id="tab2" class="tab-pane fade">
		        
			        <c:if test="${mbawxSessionsSize == 0}">
			        	<div class="nodata-toshow-div"><h4><i class="fa fa-exclamation-circle"></i> no mbawx Sessions to show</h4></div>
			        </c:if>
			        
			        <ul class="list-group hoverable">
						<c:if test="${mbawxSessionsSize>0}">
							<div id=""> 
								<c:forEach var="sessions" items="${mbawxSessions}" varStatus="status">
									<li class="list-group-item" onclick="qaHref('/acads/admin/gotoChatandQA?sessionId=${sessions.id}');">
									<div class="row" >
										<div class="col-md-10">
										<h5 style="font-weight:normal;color:#000">Subject : ${sessions.subject}. &nbsp;&nbsp;&nbsp; Date :  ${sessions.date}. &nbsp;&nbsp;&nbsp;
										
										<c:if test="${not empty sessions.getMonth() && not empty sessions.getYear()}">  
										Academic Cycle :  ${sessions.month} &nbsp; ${sessions.year}.  
										</c:if> 
										
										<c:if test="${not empty sessions.getTrack()}">  
										Track / Group :  ${sessions.track}} .
										</c:if>
										</h5>
										<h4 style="font-weight:normal;color:#000">
											 ${sessions.sessionName}
										</h4>
										</div>
										<div class="col-md-2">
												<h4 style="font-weight:normal;color:#000">Pending Query : ${sessions.count}</h4>
										</div>
									</div>
									</li>
									
								</c:forEach>
							</div>
						</c:if>
					</ul>
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
	  });
	  
	  function qaHref(web){
	      window.location.href = web;}
	  </script> 
	  

</body>
</html>
