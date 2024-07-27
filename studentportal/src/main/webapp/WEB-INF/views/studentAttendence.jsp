<!DOCTYPE html>


<html lang="en">

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<jsp:include page="common/jscss.jsp">
	<jsp:param value="Welcome to Student Zone" name="title" />
</jsp:include>


<body>

	<%@ include file="common/header.jsp"%>



	<div class="sz-main-content-wrapper">

		<jsp:include page="common/breadcrum.jsp">
			<jsp:param value="Student Zone;Attendance" name="breadcrumItems" />
		</jsp:include>


		<div class="sz-main-content menu-closed">
			<div class="sz-main-content-inner">
				<%@ include file="common/left-sidebar.jsp"%>


				<div class="sz-content-wrapper examsPage">
					<%@ include file="common/studentInfoBar.jsp"%>


					<div class="sz-content">

						<h2 class="red text-capitalize">Attendance</h2>
						<br>
						<h3>Session Attended :
							${fn:length(attendedSessionListForStudentDetailDashBoard)}</h3>
						<table class="table table-striped" style="font-size: 12px">
							<thead>
								<tr>
									<th>Subject Name</th>
									<th>Session Name</th>
									<th>Faculty ID</th>
									<th>Date</th>
									<th>Start Time</th>
									<th>End Time</th>
								</tr>
							</thead>
							<tbody>
								<tr>
									<c:if
										test="${fn:length(attendedSessionListForStudentDetailDashBoard) > 0}">
										<c:forEach var="sessionBean"
											items="${attendedSessionListForStudentDetailDashBoard}">
											<tr>
												<td><c:out value="${sessionBean.subject}" /></td>
												<td><c:out value="${sessionBean.sessionName}" /></td>
												<td><c:out value="${sessionBean.facultyId}" /></td>
												<td><c:out value="${sessionBean.date}" /></td>
												<td><c:out value="${sessionBean.startTime}" /></td>
												<td><c:out value="${sessionBean.endTime}" /></td>
											</tr>
										</c:forEach>
									</c:if>
								</tr>

							</tbody>
						</table>

					</div>

				</div>


			</div>

		</div>
	</div>



	<jsp:include page="common/footer.jsp" />


</body>
</html>