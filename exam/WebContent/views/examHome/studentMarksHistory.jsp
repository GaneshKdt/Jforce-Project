
<!DOCTYPE html>
<%@page import="com.nmims.beans.StudentMarksBean"%>
<%@page import="com.nmims.beans.StudentExamBean"%>
<%@page import="java.util.ArrayList"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>


<html lang="en">

<body>
	<%
ArrayList<StudentMarksBean> studentMarksListForMarksHistory = (ArrayList)request.getAttribute("studentMarksListForMarksHistory");
//int sizeOfStudentMarksListForMarksHistory = studentMarksListForMarksHistory.size();
int sizeOfStudentMarksListForMarksHistory = 0; //Vilpesh on 2021-12-08 
if(null != studentMarksListForMarksHistory) {
	sizeOfStudentMarksListForMarksHistory = studentMarksListForMarksHistory.size();
}
int srNum = 0;
StudentExamBean studentCheck = (StudentExamBean)request.getSession().getAttribute("studentExam");
%>


	<div class="accordion accordion-flush " id="accordionFlushExample">
		<div class="accordion-item row m-0 rounded">
			<h2 class="accordion-header col-12">

				<a class="accordion-button collapsed" type="button"
					data-bs-toggle="collapse" data-bs-target="#flush-collapseOne"
					aria-expanded="false" aria-controls="flush-collapseOne">

					<h4 class="text-capitalize text-danger me-0 text-wrap ">

						<span class="text-uppercase fw-bold">Marks History</span> 
						<c:choose>
							<c:when test="${requestScope.resultSource1 eq 'REDIS' }">
								<c:out value=":"></c:out>
							</c:when>
							<c:when test="${requestScope.resultSource1 eq 'DB' }">
							</c:when>
						</c:choose>&nbsp; &nbsp;
						<span class="text-success"><%=sizeOfStudentMarksListForMarksHistory%>
							Records Available</span>
					</h4>

				</a>

			</h2>

			<div id="flush-collapseOne" class="accordion-collapse collapse"
				data-bs-parent="#accordionFlushExample">
				<div class="accordion-body col-12">

					<%if(sizeOfStudentMarksListForMarksHistory==0){%>
					<div class="no-data-wrapper">
						<p class="no-data text-center fs-5">
							<span class="icon-exams"></span> No Mark History
						</p>
					</div>
					<%}%>
					<%if(sizeOfStudentMarksListForMarksHistory > 0){%>

					<div class="data-content">
						<div class="table-responsive">
							<table class="table table-striped w-100 fs-4"
								id="studentMarksHistory">
								<thead>
									<tr>
										<th class="text-center">Sr. No.</th>
										<th class="text-center">Exam Year</th>
										<th class="text-center">Exam Month</th>
										<th class="text-center">Sem</th>
										<th>Subject</th>
										<th class="text-center">Written</th>

										<% if ( !"EPBM".equalsIgnoreCase(studentCheck.getProgram()) && !"MPDV".equalsIgnoreCase(studentCheck.getProgram())) {%>
										<th class="text-center">Assign.</th>
										<th class="text-center">Grace</th>
										<% } %>
									</tr>
								</thead>
								<tbody>
									<%for(StudentMarksBean studentMarks :studentMarksListForMarksHistory){ 
                                                srNum++;
                                            %>
									<tr>
										<td class="text-center"><%=srNum %></td>
										<td class="text-center"><%=studentMarks.getYear() %></td>
										<td class="text-center"><%=studentMarks.getMonth() %></td>
										<td class="text-center"><%=studentMarks.getSem() %></td>
										<td><%=studentMarks.getSubject() %></td>
										<td class="text-center"><%=studentMarks.getWritenscore()!=null ? studentMarks.getWritenscore():""  %></td>

										<% if ( !"EPBM".equalsIgnoreCase(studentCheck.getProgram()) && !"MPDV".equalsIgnoreCase(studentCheck.getProgram())) {%>
										<td class="text-center"><%=studentMarks.getAssignmentscore()!=null ? studentMarks.getAssignmentscore():"" %></td>
										<td class="text-center"><%=studentMarks.getGracemarks() !=null ? studentMarks.getGracemarks():"" %></td>
										<% } %>
									</tr>
									<%} %>
								</tbody>
							</table>
						</div>
					</div>
					<%} %>


				</div>
				<!--end of accordian body-->



			</div>
			<!--end of accordian item-->

		</div>

	</div>
	<!--end of accordian-->





	<!-- datatable js -->
	<script>$(document).ready(function () {
	    $('#studentMarksHistory').DataTable();
	});</script>

</body>
</html>
