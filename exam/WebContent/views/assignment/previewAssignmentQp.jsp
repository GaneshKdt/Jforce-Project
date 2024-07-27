<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix = "fmt" uri = "http://java.sun.com/jsp/jstl/fmt" %> 
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>   
<jsp:include page="../jscss.jsp">
	<jsp:param value="Upload Assignment Files" name="title" />
</jsp:include>   
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Insert title here</title>
</head>
<body>

<section class="content-container login">
		<div class="container-fluid customTheme"">
			<c:choose>
			  <c:when test="${role eq 'creator'}">
			  <c:set var = "gobackUrl" value = "/exam/facultyAssignmentUpload"/>
			  </c:when>
			  <c:otherwise>     
			   <c:set var = "gobackUrl"  value = "/exam/facultyAssignmentQpReview"/>
		      </c:otherwise> 
		   </c:choose>
			<div class="row"><legend>Upload Assignment Question</legend></div>
			<%@ include file="../common/messages.jsp"%>
			<div class="pull-left">
			<a href="${gobackUrl}" ><u><i class="fa-solid fa-arrow-left" aria-hidden="true"></i> back</u></a> 
			</div>   </br></br>    
				  
				<div class="container" style="width:70%">      
				<div class="panel-body clearfix " style="padding-left: 6rem;" >  
					<c:forEach var="bean" items="${questionsBean}" varStatus="i">
					
					
			    	<div class="row" >
			    	<div class="col-md-1" style="padding: 0px!important;">       
					<p>${bean.qnNo}</p> 
					</div> 
					<div class="col-md-15">    
					${bean.question} 
					</div>  
					</div>   
					 <div class="row" >
						 <div class="col-md-16">         
						 	<p class="pull-right" style="font-size:12px"><b>${bean.mark} Marks</b></p>
						 </div>  
					 </div>
					</c:forEach> 
					</div>
				</div>
			</div>
			
		</div>
	</section>
</body>
</html>