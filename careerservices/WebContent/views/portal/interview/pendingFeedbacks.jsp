<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<%@page import="com.nmims.beans.InterviewBean"%>
<%@page import="java.util.ArrayList"%>

<%
	int count=1;
%>

<body>
	<div class="panel-body">
		<div class="card text-center mt-4">
			<div class="card-header card-special mx-auto">
				<div class="row px-5 mx-auto">
					<h2 class="text-center mx-auto material-icon-containter"
						style="color: #d2232a;">
						<!-- <i class="material-icons" style="font-size: 110%"> history</i> -->
						Pending Feedbacks
					</h2>
				</div>
			</div>
			<div class="card-body" id="pendingFeedbacks">
				<c:choose>
					<c:when test="${ empty pendingFeedbacks }">
						<tr>
							<td>No Record Found</td>
						</tr>
					</c:when>
					<c:otherwise>
						<table id="pendingFeedbacksTable" class="table table-hover center">
							<thead class="">
								<tr class="border">
									<th class="border " scope="row" style="text-align: center;">#</th>
									<th class="border " scope="col" style="text-align: center;">Interview
										Date</th>
									<th class="border " scope="col" style="text-align: center;">Time</th>
									<th class="border " scope="col" style="text-align: center;">Actions</th>
								</tr>
							</thead>
							<tbody>
								<c:forEach items="${pendingFeedbacks}" var="pendingFeedback">
									<tr>
										<td style="text-align: center;"><%=count%></td>
										<td style="text-align: center;">${ pendingFeedback.getDate() }</td>
										<td style="text-align: center;">${ pendingFeedback.getStartTime() }</td>
										<td style="text-align: center;"><a
											class="btn btn-primary"
											href="interviewFeedbackForm?facultyId=${ userId }&interviewId=${ pendingFeedback.interviewId }"
											style="border-radius: 4px;">Feedback</a></td>
									</tr>
									<%
												count++;
											%>
								</c:forEach>
							</tbody>
						</table>
					</c:otherwise>
				</c:choose>
			</div>
		</div>
	</div>
</body>
