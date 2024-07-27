<!DOCTYPE html>
<%@page import="com.nmims.beans.Person"%>
<%@page import="com.nmims.beans.Page"%>
<%@page import="com.nmims.beans.CaseStudyExamBean"%>
<html class="no-js">

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<jsp:include page="../jscss.jsp">
	<jsp:param value="Search and Evaluate Submitted Case Study" name="title" />
</jsp:include>

<body class="inside">

	<%@ include file="../header.jsp"%>

	<section class="content-container login">
		<div class="container-fluid customTheme">
			<div class="row">
				<legend>Search Evaluated Case Study Status</legend>
			</div>
			<div class = "js_result"></div>
			<%@ include file="../messages.jsp"%>
			<div class="panel-body clearfix">
				<form:form action="" method="post" modelAttribute="csBean">
					<fieldset>
						<div class="col-md-18 column">
							<div class="row">
								<div class="col-md-4 column">
									<div class="form-group">
									<label for="batchYear">Batch Year</label>
										 <form:select id="batchYear" path="batchYear" type="text"
											placeholder="Batch Year" class="form-control" required="required"
											itemValue="${csBean.batchYear}">
											<form:option value="">Select Batch Year</form:option>
											<form:options items="${yearList}" /> 
										</form:select> 
									</div>
								</div>
								<div class="col-md-4 column">
									<div class="form-group">
									<label for="batchMonth">Batch Month</label>
										<form:select id="batchMonth" path="batchMonth" type="text"
											placeholder="Batch Month" class="form-control" required="required"
											itemValue="${csBean.batchMonth}">
											<form:option value="">Select Batch Month</form:option>
										  	 <form:options items="${monthList}" />
										</form:select>
									</div>
								</div>
								<div class="col-md-4 column">
									<div class="form-group">
									<label for="evaluated">Evaluated</label>
										<form:select id="evaluated" path="evaluated" type="text"
											placeholder="Evaluated" class="form-control" 
											itemValue="${csBean.evaluated}">
											<form:option value="">Select Evaluated</form:option>
										  	 <form:options items="${evaluationStatus}" />
										</form:select>
									</div>
								</div>
								<div class="col-md-4 column">
									<div class="form-group">
									<label for="program">Program</label>
										<form:select path="program" type="text" id="program"
											placeholder="Program" class="form-control" required="required" >
											<form:option value="">Select Program</form:option>
											<form:option value="EPBM">EPBM</form:option>
										</form:select>
									</div>
								</div>
								<div class="col-md-4 column">
									<div class="form-group">
									<label for="facultyId">Faculty List</label>
										<form:select id="facultyId" path="facultyId" type="text"
											placeholder="Faculty List" class="form-control" required="required"
											itemValue="${csBean.facultyId}">
											<form:option value="">Select Faculty List</form:option>
										  	 <form:options items="${facultyList}" />
										</form:select>
									</div>
								</div>
								</div>
								<div class="row">
								<div class="col-md-4 column">
									<div class="form-group">
									<br>
										<button id="submit" name="submit"
										class="btn btn-small btn-primary"
										formaction="searchEvaluatedCaseStudy">Search</button>
									</div>
								</div>
							</div>
						</div>
						
					</fieldset>
				</form:form>
			</div>
			
			<c:if test="${cases > 0 }">
				<div class="row">
					<legend>Case Studies Found (${cases}) 
					 <c:if test="${role.indexOf(\"TEE Admin\") != -1 || role.indexOf(\"Exam Admin\") != -1 }"> 
					 <h5><b><a href="/exam/admin/downloadEvalutedCaseStudyDetails"> Click here to Download Excel</a></b></h5> 
					 <!-- <h5><b><a class = "download" href="#"> Click here to get Excel</a></b></h5>  -->
					 </c:if> 
					</legend> 
				</div>
				<div class="panel-body">
				<div class="table-responsive">
					
						<table class="table table-striped table-hover tables" style="font-size: 12px;">
							<thead>
								 <tr>
								 	<td>Sr No</td>
								 	<td>Batch Year</td>
								 	<td>Batch Month</td>
								 	<td>SapId</td>
								 	<td>Topic Submitted</td>
								 	<td>Faculty Id</td>
								 	<td>Faculty Name</td>
								 	<td>Evaluated</td>
								 	<td>Evaluated Date</td>
								 	<td>Evaluated Count</td>
								 	<td>Score</td>
								 	<td>Grade</td>
								 	
									
								 </tr>
							</thead>
							<tbody>
								<c:forEach var="caseBean" items="${casesList}" varStatus="status">
									<tr>
										<td><c:out value="${status.count}" /></td>
										<td nowrap="nowrap"><c:out value="${caseBean.batchYear}" /></td>
										<td><c:out value="${caseBean.batchMonth}" /></td>
										<td><c:out value="${caseBean.sapid}" /></td>
										<td><c:out value="${caseBean.topic}" /></td>
										<td><c:out value="${caseBean.facultyId}" /></td>
										<td><c:out value="${caseBean.firstName} ${caseBean.lastName}" /></td>
										<td><c:out value="${caseBean.evaluated}" /></td>
										<td><c:out value="${caseBean.evaluationDate}" /></td>
										<td nowrap="nowrap">
										<c:if test="${role.indexOf(\"Assignment Admin\") != -1 || role.indexOf(\"Exam Admin\") != -1 }">
											<c:url value="changeCaseStudyEvaluationCount" var="changeEvaluationCountURL">
											  <c:param name="batchYear" value="${caseBean.batchYear}" />
											  <c:param name="batchMonth" value="${caseBean.batchMonth}" />
											  <c:param name="topic" value="${caseBean.topic}" />
											  <c:param name="sapid" value="${caseBean.sapid}" />
											</c:url>
										<a href="#" class="editable" id="evaluationCount" data-type="text" data-pk="${caseBean.batchYear}" data-url="${changeEvaluationCountURL}" data-title="Change Evaluation Count">${caseBean.evaluationCount}</a>
										</c:if>
										</td>
										
										<td><c:out value="${caseBean.score}" /></td>
										<td><c:out value="${caseBean.grade}" /></td>
									
									</tr>
								</c:forEach>
							</tbody>
						</table>
					
				</div>		
			</div>
		</c:if>
	</div>
</section>
<jsp:include page="../footer.jsp" />
<script src="https://cdn.datatables.net/1.10.13/js/jquery.dataTables.min.js"></script>
<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/js/vendor/dataTables.bootstrap.js"></script>
<script src="https://cdn.datatables.net/buttons/1.2.4/js/dataTables.buttons.min.js"></script>

<script type="text/javascript">
	$(document).ready(function(){

		 //toggle `popup` / `inline` mode
	    $.fn.editable.defaults.mode = 'inline';    
	    $('.editable').each(function() {
	        $(this).editable();
	    }); 

		$('.tables').DataTable( {
			 
			   "searching": false,
			   "ordering": false,
	        initComplete: function () {
	        	 this.api().columns().every( function () {
	               var column = this;
	                var headerText = $(column.header()).text();
	                console.log("header :"+headerText);
	         
	             
	             /*    column.data().unique().sort().each( function ( d, j ) {
	                    select.append( '<option value="'+d+'">'+d+'</option>' )
	                } ); */
	             
	            } );
	        } 
	    } );
		
		
	});
	
	/* 
	 $('.download').click(function(){
    	var conf = confirm('The excel will be sent to the registered emailId');
    	if(conf == true){
    	
    		var self = $(this);
             $.ajax({
                type: "POST",
                url: '/exam/downloadEvalutedCaseStudyDetails',
                data: {
                	
                }, 
                success:function(response){
                	if(response == "Success to send mail"){
                		$('.js_result').html('<div class="alert alert-success alert-dismissible"> <a href="#" class="close" data-dismiss="alert" aria-label="close">&times;</a> <strong>Success!</strong> mail sent. </div>');
                	}else{
                		$('.js_result').html('<div class="alert alert-danger alert-dismissible"> <a href="#" class="close" data-dismiss="alert" aria-label="close">&times;</a> <strong>Failed!</strong> mail not sent. </div>');
                	}
               },
               error:function(){
            	   $('.js_result').html('<div class="alert alert-danger alert-dismissible"> <a href="#" class="close" data-dismiss="alert" aria-label="close">&times;</a> <strong>Failed!</strong> Server Error Found. </div>');
               }
            });
    	}
    }); */  
</script>
</body>
</html>
