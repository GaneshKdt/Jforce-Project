<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!--> 


<html class="no-js"> <!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<jsp:include page="../jscss.jsp">
<jsp:param value="Student Exam Data" name="title" />
</jsp:include>

<%
String sapId = request.getParameter("sapId");
%>

<body class="inside">

	
    <section class="content-container login">
        <div class="container-fluid customTheme">
       
        <%@ include file="../messages.jsp"%>
		
		<div class="panel-body">
		<!-- Marks Section Start -->
		<div id="accordionMarks">
			<h3>&nbsp;Student Marks Data <font size="2px">(${marksRowCount} Records Found) &nbsp;</font></h3>
			<c:if test="${marksRowCount > 0 }">
			<div class="panel-body table-responsive ">
			<table class="table table-striped table-hover" style="font-size:12px">
						<thead>
							<tr> 
								<th>Sr. No.</th>
								<th>Exam Year</th>
								<th>Exam Month</th>
								<th>Syllabus Year</th>
								<th>GR No.</th>
								<th>SAP ID</th>
								<th>Student Name</th>
								<th>Program</th>
								<th>Sem</th>
								<th>Subject</th>
								<th>Written</th>
								<th>Assign.</th>
								<th>Grace</th>
							
							</tr>
						</thead>
						<tbody>
						
						<c:forEach var="studentMarks" items="${marksResults}" varStatus="status">
					        <tr>
					            <td><c:out value="${status.count}"/></td>
								<td><c:out value="${studentMarks.year}"/></td>
								<td><c:out value="${studentMarks.month}"/></td>
								<td><c:out value="${studentMarks.syllabusYear}"/></td>
								<td><c:out value="${studentMarks.grno}"/></td>
								<td><c:out value="${studentMarks.sapid}"/></td>
								<td nowrap="nowrap"><c:out value="${studentMarks.studentname}"/></td>
								<td><c:out value="${studentMarks.program}"/></td>
								<td><c:out value="${studentMarks.sem}"/></td>
								<td nowrap="nowrap"><c:out value="${studentMarks.subject}"/></td>
								<td><c:out value="${studentMarks.writenscore}"/></td>
								<td><c:out value="${studentMarks.assignmentscore}"/></td>
								<td><c:out value="${studentMarks.gracemarks}"/></td>
					          
					        </tr>   
					    </c:forEach>
							
						</tbody>
					</table>
				</div>
				</c:if>
		</div>
		<!--  Marks Section End -->
		<br/>
		
		
		<!-- Pass Fail Section Start -->
		<div id="accordionPassFail">
			<h3>&nbsp;Student Pass Fail Data <font size="2px">(${passFailRowCount} Records Found) &nbsp;</font></h3>
			
			<c:if test="${passFailRowCount > 0 }">
			<div class="panel-body table-responsive">
			<table class="table table-striped table-hover" style="font-size:12px">
				<thead>
				<tr>
					<th>Sr. No.</th>
					<th>Written Year</th>
					<th>Written Month</th>
					<th>Assignment Year</th>
					<th>Assignment Month</th> 
					<th>SAP ID</th>
					<th>Student Name</th>
					<th>Program</th>
					<th>Sem</th>
					<th>Subject</th>
					<th>Written</th>
					<th>Assign.</th>
					<th>Grace</th>
					<th>Total</th>
					<th>Pass</th>
					<th>Reason</th>
				</tr>
				</thead>
				<tbody>
				
				<c:forEach var="studentMarks" items="${passFailResults}" varStatus="status">
			        <tr>
			            <td><c:out value="${status.count}" /></td>
						<td><c:out value="${studentMarks.writtenYear}" /></td>
						<td><c:out value="${studentMarks.writtenMonth}" /></td>
						<td><c:out value="${studentMarks.assignmentYear}"/></td>
						<td><c:out value="${studentMarks.assignmentMonth}"/></td>
						<td><c:out value="${studentMarks.sapid}" /></td>
						<td><c:out value="${studentMarks.name}" /></td>
						<td><c:out value="${studentMarks.program}" /></td>
						<td><c:out value="${studentMarks.sem}" /></td>
						<td><c:out value="${studentMarks.subject}" /></td>
						<td><c:out value="${studentMarks.writtenscore}" /></td>
						<td><c:out value="${studentMarks.assignmentscore}" /></td>
						<td><c:out value="${studentMarks.gracemarks}"/></td>
						<td><c:out value="${studentMarks.total}" /></td>
						<td><c:out value="${studentMarks.isPass}" /></td>
						<td><c:out value="${studentMarks.failReason}" /></td>
	
			        </tr>   
			    </c:forEach>
					
				</tbody>
			</table>
			</div>
			</c:if>
		</div>
		<!--  Pass Fail section End -->
		<br/>
		
		<!-- Exam Booking Section Start -->
		<div id="accordionExamBookings">
			<h3>&nbsp;Exam Booking Data <font size="2px">(${examBookingsRowCount} Records Found) &nbsp;</font></h3>
			
			<c:if test="${examBookingsRowCount > 0 }">
			<div class="panel-body table-responsive">
			<table class="table table-striped table-hover" style="font-size:12px">
				<thead>
				<tr>
					<th>Sr. No.</th>
					<th>Exam Year</th>
					<th>Exam Month</th>
					<th>Program</th>
					<th>Sem</th>
					<th>Subject</th>
					<th>Exam Date</th>
					<th>Exam Time</th>
					<th>Payment Mode</th>
					<th>Amount</th>
					<th>Unique Track ID per payment</th>
					<th>Hall Ticket Downloaded</th>
				</tr>
				</thead>
				<tbody>
				
				<c:forEach var="booking" items="${examBookingResults}" varStatus="status">
			        <tr>
			            <td><c:out value="${status.count}" /></td>
						<td><c:out value="${booking.year}" /></td>
						<td><c:out value="${booking.month}" /></td>
						<td><c:out value="${booking.program}" /></td>
						<td><c:out value="${booking.sem}" /></td>
						<td><c:out value="${booking.subject}" /></td>
						<td><c:out value="${booking.examDate}" /></td>
						<td><c:out value="${booking.examTime}" /></td>
						<td><c:out value="${booking.paymentMode}"/></td>
						<td><c:out value="${booking.amount}" /></td>
						<td><c:out value="${booking.trackId}" /></td>
						<td><c:out value="${booking.htDownloaded}" /></td>
	
			        </tr>   
			    </c:forEach>
					
				</tbody>
			</table>
			</div>
			</c:if>
		</div>
		<!--  Exam Booking section End -->
		<br/>
		
		<div id="accordionLinks">
			<h3>&nbsp;Other Information&nbsp;</h3>
			<div>
				<a href="/exam/student/downloadHallTicket?userId=<%=sapId %>" target="_blank">Download Hall Ticket</a><br/>
			</div>
		</div>
		
	</div>
	
	</div>
</section>
<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/js/vendor/jquery-1.11.2.min.js"></script>
<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/js/vendor/bootstrap.min.js"></script>
<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/js/vendor/jquery-ui.min.js"></script>
<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/js/vendor/jquery.validate.min.js"></script>
<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/js/vendor/additional-methods.min.js"></script>
<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/js/vendor/fileinput.min.js"></script>
<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/js/vendor/bootstrap-datepicker.min.js"></script>
<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/js/vendor/scripts.js"></script>
<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/js/main.js?id=1"></script>
<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/js/TimeCircles.js"></script>
<!-- jQuery Editable element Plugin -->
<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/js/vendor/bootstrap-editable.js" ></script>

	<script>
	  $(function() {
	    $( "#accordionPassFail" ).accordion({
	      collapsible: true,
	      heightStyle: "content",
	      active:false
	    });
	  });
	  
	  
	 $(function() {
	    $( "#accordionMarks" ).accordion({
	      collapsible: true,
	      heightStyle: "content",
	      active:false
	    });
	  });
	 
	 
	 $(function() {
	    $( "#accordionExamBookings" ).accordion({
	      collapsible: true,
	      heightStyle: "content",
	      active:false
	    });
	  });
	 
	 $(function() {
	    $( "#accordionLinks" ).accordion({
	      collapsible: true,
	      heightStyle: "content",
	      active:false
	    });
	  });
	  
	  </script>

</body>
</html>
