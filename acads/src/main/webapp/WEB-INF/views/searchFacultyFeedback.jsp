<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!-->


<html class="no-js">
<!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>


<jsp:include page="jscss.jsp">
	<jsp:param value="Report for Attendance & Feedback" name="title" />
</jsp:include>

<body class="inside">

	<%@ include file="header.jsp"%>

	<section class="content-container login">
		<div class="container-fluid customTheme">

			<div class="row"><legend>Feedback for ${session.subject }-${session.sessionName }</legend></div>
			
			<%@ include file="messages.jsp"%>

			<div class="panel-body">
			<c:choose>
				<c:when test="${rowCount > 0}">
			
				
				<div class="table-responsive">
				<table class="table table-striped" style="font-size:12px">
									<thead>
									<tr>
										<th>Sr. No.</th>
										<th>Q.1 Response( The subject matter covered in this session helped you to understand and learn effectively)</th>
										<th>Q.2 Response(The course material used was helpful towards today's session)</th>
										<th>Q.3 Response(Audio quality was upto the mark.)</th>
										<th>Q.4 Response(Video quality was upto the mark.)</th>
										<th>Q.5 Response(The Faculty was organized and well prepared for the class)</th>
										<th>Q.6 Response(The Faculty was effective in communicating the concept in the class (in terms of clarity and presenting the concepts in understandable manner).)</th>
										<th>Q.7 Response(The Faculty was responsive to student's learning difficulties and dealt with questions appropriately.)</th>
										<th>Q.8 Response(The learning process adopted (e.g. case studies, relevant examples and presentation work etc.) were helpful towards learning from the session.)</th>
										<th>Q.1 Remarks</th>
										<th>Q.2 Remarks</th>
										<th>Q.3 Remarks</th>
										<th>Q.4 Remarks</th>
										<th>Q.5 Remarks</th>
										<th>Q.6 Remarks</th>
										<th>Q.7 Remarks</th>
										<th>Q.8 Remarks</th>
										<th>Remarks</th>
										<th>Student Confirmation For Attendance</th>
										<th>Reason For Not Attending Session</th>
										<th>Other Reason For Not Attending Session</th>
									</tr>
								</thead>
									<tbody>
									
									<c:forEach var="feedback" items="${facultyFeedbackList}" varStatus="status">
								        <tr>
								            <td><c:out value="${status.count}" /></td>
											<td><c:out value="${feedback.q1Response}" /></td>
											<td><c:out value="${feedback.q2Response}" /></td>
											<td><c:out value="${feedback.q3Response}" /></td>
											<td><c:out value="${feedback.q4Response}" /></td>
											<td><c:out value="${feedback.q5Response}" /></td>
											<td><c:out value="${feedback.q6Response}" /></td>
											<td><c:out value="${feedback.q7Response}" /></td>
											<td><c:out value="${feedback.q8Response}" /></td>
											<td><c:out value="${feedback.q1Remark}" /></td>
											<td><c:out value="${feedback.q2Remark}" /></td>
											<td><c:out value="${feedback.q3Remark}" /></td>
											<td><c:out value="${feedback.q4Remark}" /></td>
											<td><c:out value="${feedback.q5Remark}" /></td>
											<td><c:out value="${feedback.q6Remark}" /></td>
											<td><c:out value="${feedback.q7Remark}" /></td>
											<td><c:out value="${feedback.q8Remark}" /></td>
											<td width="60%"><c:out value="${feedback.feedbackRemarks}" /></td>
											<td><c:out value="${feedback.studentConfirmationForAttendance}" /></td>
											<td><c:out value="${feedback.reasonForNotAttending}" /></td>
											<td><c:out value="${feedback.otherReasonForNotAttending}" /></td>
								        </tr>   
								    </c:forEach>
									
										<tr>
								            <td>&nbsp;</td>
											<td><b><fmt:formatNumber type="number"  maxIntegerDigits="3" value="${q1Average}" /></b></td>
											<td><b><fmt:formatNumber type="number"  maxIntegerDigits="3" value="${q2Average}" /></b></td>
											<td><b><fmt:formatNumber type="number"  maxIntegerDigits="3" value="${q3Average}" /></b></td>
											<td><b><fmt:formatNumber type="number"  maxIntegerDigits="3" value="${q4Average}" /></b></td>
											<td><b><fmt:formatNumber type="number"  maxIntegerDigits="3" value="${q5Average}" /></b></td>
											<td><b><fmt:formatNumber type="number"  maxIntegerDigits="3" value="${q6Average}" /></b></td>
											<td><b><fmt:formatNumber type="number"  maxIntegerDigits="3" value="${q7Average}" /></b></td>
											<td><b><fmt:formatNumber type="number"  maxIntegerDigits="3" value="${q8Average}" /></b></td>
											<td>&nbsp;</td>
								        </tr>  
								        
									</tbody>
								</table>
				</div>
				<br>
			
				</c:when>
				</c:choose>
				
		</div>

		</div>
		
	</section>

	<jsp:include page="footer.jsp" />


</body>
</html>
