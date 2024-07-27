<!DOCTYPE html>


<html lang="en">
<%@page import="java.util.Map" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<jsp:include page="adminCommon/jscss.jsp">
	<jsp:param value="Report for Session Attendance For Mba Wx " name="title" />
</jsp:include>



<body>
<%
Map<String,String> facultyIdMap = (Map<String,String>)session.getAttribute("facultyIdMap");



%>
	<%@ include file="adminCommon/header.jsp"%>
	<div class="sz-main-content-wrapper">

		<jsp:include page="adminCommon/breadcrum.jsp">
			<jsp:param value="Academics;Report for Attendance & Feedback"
				name="breadcrumItems" />
		</jsp:include>


		<div class="sz-main-content menu-closed">
			<div class="sz-main-content-inner">
				<jsp:include page="adminCommon/left-sidebar.jsp">
					<jsp:param value="" name="activeMenu" />
				</jsp:include>


				<div class="sz-content-wrapper examsPage">
					<%@ include file="adminCommon/adminInfoBar.jsp"%>
					<div class="sz-content">

						<h2 class="red text-capitalize">Report for Session Attendance For Mba Wx </h2>
						<div class="clearfix"></div>
						<div class="panel-content-wrapper" style="min-height: 450px;">
							<%@ include file="adminCommon/messages.jsp"%>
							<form:form action="searchAttendanceFeedbackForMbaWx" method="post"
								modelAttribute="searchBean">
								<fieldset>
									<div class="col-md-6 column">

										<div class="form-group">
											<form:select id="writtenYear" path="year" required="required"
												class="form-control" itemValue="${searchBean.year}">
												<form:option value="">Select Academic Year</form:option>
												<form:options items="${yearList}" />
											</form:select>
										</div>

										<div class="form-group">
											<form:select id="writtenMonth" path="month" required="required"
												class="form-control">
												<form:option value="">Select Academic Month</form:option>
												<form:options items="${acadsMonthList}" />

											</form:select>
										</div>										
										


										<div class="form-group">
											<button id="submit" name="submit"
												class="btn btn-large btn-primary"
												formaction="searchAttendanceFeedbackForMbaWx">Generate</button>
											<button id="cancel" name="cancel" class="btn btn-danger"
												formaction="home" formnovalidate="formnovalidate">Cancel</button>

										</div>



									</div>

									<div class="col-md-4 column">
									
									
									
										<div class="form-group" style="overflow: visible;">
											<form:select id="subject" path="subject"
												class="combobox form-control">
												<form:option value="">Type OR Select Subject</form:option>
												<form:options items="${subjectList}" />
											</form:select>
										</div>
										
											<div class="form-group" style="overflow: visible;">
											<form:select id="facultyFullName" path="facultyFullName"
												class="combobox form-control">
												<form:option value="">Type OR Select Faculty</form:option>
												<c:forEach items="${facultyIdMap}" var="facultyMap">
													<form:option value="${facultyMap.key}">${facultyMap.value}</form:option>
												</c:forEach>
											</form:select>
										</div>



									</div>

								</fieldset>

								<c:if test="${rowCount > 0}">
									<legend>
									<legend>
										&nbsp;Attendance Feedback Report 
										&nbsp;	<font size="2px"> <a
											href="downloadSessionAttendanceReportForMbaWx">Download Session Attendance For MBA Wx in Excel </a></font>
											
									</legend>
										
																				
								<!-- %if(roles.indexOf("Acads Admin") != -1 || roles.indexOf("Student Support") != -1){%>	
									<div class="table-responsive" id="detailSecond">
										<table class="table table-striped" style="font-size: 12px">
											<thead>
												<tr>
													<th>Overall</th>
													<th>Avg Q.1 Response</th>
													<th>Avg Q.2 Response</th>
													<th>Avg Q.3 Response</th>
													<th>Avg Q.4 Response</th>
													<th>Avg Q.5 Response</th>
													<th>Avg Q.6 Response</th>
													<th>Avg Q.7 Response</th>
													<th>Avg Q.8 Response</th>

												</tr>
											</thead>

											<tbody>
												<c:forEach var="bean"
													items="${getSubjectFacultyWiseAverage}" varStatus="status">
													<tr>
														<td><button type="button"
																class="btn btn-large btn-primary"
																onclick="showdetails()" id="overall">Overall</button></td>
														<td><c:out value="${bean.q1Average}"></c:out></td>
														<td><c:out value="${bean.q2Average}"></c:out></td>
														<td><c:out value="${bean.q3Average}"></c:out></td>
														<td><c:out value="${bean.q4Average}"></c:out></td>
														<td><c:out value="${bean.q5Average}"></c:out></td>
														<td><c:out value="${bean.q6Average}"></c:out></td>
														<td><c:out value="${bean.q7Average}"></c:out></td>
														<td><c:out value="${bean.q8Average}"></c:out></td>
													</tr>
												</c:forEach>
											</tbody>

										</table>
									</div!-->
									<!-- !}-->
								</c:if>

							<!-- if(roles.indexOf("Acads Admin") != -1 || roles.indexOf("Student Support") != -1){%>	
								<div class="table-responsive" id="detail" style="display: none;">
									<table class="table table-striped table-hover tables"
										style="font-size: 12px">
										<thead>
											<tr>
												<th>SessionId</th>
												<th>Subject</th>
												<th>SessionName</th>
												<th>Faculty Name</th>
												<th>Avg Q.1 Response</th>
												<th>Avg Q.2 Response</th>
												<th>Avg Q.3 Response</th>
												<th>Avg Q.4 Response</th>
												<th>Avg Q.5 Response</th>
												<th>Avg Q.6 Response</th>
												<th>Avg Q.7 Response</th>
												<th>Avg Q.8 Response</th>

											</tr>
										</thead>

										<tbody>

											<c:forEach var="bean"
												items="${mapOfSubjectFacultySessionWiseAverage}">
												<tr>
													<td><c:out value="${bean.value.sessionId}"></c:out></td>
													<td><c:out value="${bean.value.subject}"></c:out></td>
													<td><c:out value="${bean.value.sessionName}"></c:out></td>
													<td><c:out value="${bean.value.facultyFirstName}"></c:out></td>
													<td><c:out value="${bean.value.q1Average}"></c:out></td>
													<td><c:out value="${bean.value.q2Average}"></c:out></td>
													<td><c:out value="${bean.value.q3Average}"></c:out></td>
													<td><c:out value="${bean.value.q4Average}"></c:out></td>
													<td><c:out value="${bean.value.q5Average}"></c:out></td>
													<td><c:out value="${bean.value.q6Average}"></c:out></td>
													<td><c:out value="${bean.value.q7Average}"></c:out></td>
													<td><c:out value="${bean.value.q8Average}"></c:out></td>
												</tr>
											</c:forEach>
										</tbody>

									</table>

								</div!-->
								<!-- } !-->
							
	</form:form>


						</div>

					</div>
				</div>
			</div>
		</div>
	</div>
	<jsp:include page="adminCommon/footer.jsp" />
<!-- script>
	
		function showdetails() {

			var x = document.getElementById('detail');
			if (x.style.display === "none") {
				x.style.display = "block";
				$('.tables').DataTable( {
			        initComplete: function () {
			            this.api().columns().every( function () {
			                var column = this;
			                var headerText = $(column.header()).text();
			                console.log("header :"+headerText);
			                if(headerText == "Subject")
			                {
			                   var select = $('<select style="width:100%;" class="form-control"><option value="">All</option></select>')
			                    .appendTo( $(column.header()) )
			                    .on( 'change', function () {
			                        var val = $.fn.dataTable.util.escapeRegex(
			                            $(this).val()
			                        );
			 
			                        column
			                            .search( val ? '^'+val+'$' : '', true, false )
			                            .draw();
			                    } );
			 
			                column.data().unique().sort().each( function ( d, j ) {
			                    select.append( '<option value="'+d+'">'+d+'</option>' )
			                } );
			              }
			            } );
			        }
			    } );
			} else {
				x.style.display = "none";
			}
		}
	</script!-->


</body>
</html>