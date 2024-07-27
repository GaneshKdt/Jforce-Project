<!DOCTYPE html>
<%@page import="java.util.Arrays"%>
<html lang="en">

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.ArrayList"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<jsp:include page="../adminCommon/jscss.jsp">
	<jsp:param value="Report for Re-Registration" name="title" />
</jsp:include>

<%
	try {
%>

<%
		HashMap<String, String> mapOfStudentNumberSemAndCountOfFailedSubjects = (HashMap<String, String>) request.getAttribute("mapOfStudentNumberSemAndCountOfFailedSubjects");
		HashMap<String, String> mapOfStudentNumberAndSemAndCountOfANSSubjects = (HashMap<String, String>) request.getAttribute("mapOfStudentNumberAndSemAndCountOfANSSubjects");
		HashMap<String, String> mapOfStudentNumberAndSemAndCountOfABSubjects = (HashMap<String, String>) request.getAttribute("mapOfStudentNumberAndSemAndCountOfABSubjects");
		HashMap<String, String> mapOfStudentNumberAndSemAndGAPInReReg = (HashMap<String, String>) request.getAttribute("mapOfStudentNumberAndSemAndGAPInReReg");
		HashMap<String, String> mapOfStudentNumberAndSemAndPendingNumberOfExamBookings = (HashMap<String, String>) request.getAttribute("mapOfStudentNumberAndSemAndPendingNumberOfExamBookings");
		HashMap<String, String> mapOfStudentNumberAndSemAndCountOfSessionsAttended = (HashMap<String, String>) request.getAttribute("mapOfStudentNumberAndSemAndCountOfSessionsAttended");
		HashMap<String, String> mapOfStudentNumberAndSemAndDriveMonthYear = (HashMap<String, String>) request.getAttribute("mapOfStudentNumberAndSemAndDriveMonthYear");	
		ArrayList<String> listOfSapIdOfActiveStudents = (ArrayList<String>) request.getAttribute("listOfSapIdOfActiveStudents");
		HashMap<String,StudentExamBean> mapOfSAPIDAndStudentBean = (HashMap<String, StudentExamBean>)request.getAttribute("studentsMap");
%>



<style>
table, th, td {
	border: 1px solid black;
	border-collapse: collapse;
}

th, td {
	padding: 5px;
	text-align: center;
}

#subjectNames {
	font-weight: bold;
}
</style>

<body>

	<%@ include file="../adminCommon/header.jsp"%>
	<div class="sz-main-content-wrapper">

		<jsp:include page="../adminCommon/breadcrum.jsp">
			<jsp:param value="Exam;Re-Registration Report" name="breadcrumItems" />
		</jsp:include>


		<div class="sz-main-content menu-closed">
			<div class="sz-main-content-inner">
				<jsp:include page="../adminCommon/left-sidebar.jsp">
					<jsp:param value="" name="activeMenu" />
				</jsp:include>


				<div class="sz-main-content menu-closed">
					<div class="sz-main-content-inner">
						<jsp:include page="../adminCommon/left-sidebar.jsp">
							<jsp:param value="" name="activeMenu" />
						</jsp:include>


						<div class="sz-content-wrapper examsPage">
							<%@ include file="../adminCommon/adminInfoBar.jsp"%>
							<div class="sz-content">
								<h2 class="red text-capitalize">Re-RegistrationReport</h2>
								<div class="clearfix"></div>
								<div class="panel-content-wrapper" style="min-height: 450px;">
									<%@ include file="../adminCommon/messages.jsp"%>
									<form:form action="/exam/admin/reRegistrationReport" method="post"
										modelAttribute="studentBean">
										<fieldset>
										
											<div class="col-md-4">
												<div class="form-group">
													<form:select id="enrollmentYear" path="enrollmentYear"
														 class="form-control"
														itemValue="${studentBean.enrollmentYear}">
														<form:option value="">Select Enrollment Year</form:option>
														<form:options items="${yearList}" />
													</form:select>
												</div>


											</div>
											<div class="col-md-4">
												<div class="form-group">
													<form:select id="enrollmentMonth" path="enrollmentMonth"
														 class="form-control"
														itemValue="${studentBean.enrollmentMonth}">
														<form:option value="">Select Enrollment Month</form:option>
														<form:option value="Jan">Jan</form:option>
														<form:option value="Jul">Jul</form:option>
													</form:select>
												</div>
											</div>
											
											<div class="col-md-4">
												<div class="form-group">
													<form:input id="sapid" path="sapid" type="text"
														placeholder="SAP ID" class="form-control" />
												</div>
											</div>
											
											<div class="col-md-4">
												<div class="form-group">
													<form:select id="program" path="program" type="text"
														placeholder="Program" class="form-control">
														<form:option value="">Select Program</form:option>
														<form:options items="${programList}" />
													</form:select>
												</div>
											</div>
											<div class="col-md-4">
												<div class="form-group">
													<%
													if(!"academic.admin".equalsIgnoreCase((String)session.getAttribute("userId"))){
														if(roles.indexOf("Information Center") == -1 && roles.indexOf("Corporate Center") == -1) {
													%>

													<form:select id="centerCode" path="centerCode"
														class="form-control"
														itemValue="${studentBean.centerCode}">
														<form:option value="">Select IC</form:option>
														
														<form:options items="${centerList}" />
													</form:select>
													<%		}
														}
													%>
												</div>
											</div>
											<div class="col-md-6">

												<div class="form-group">
													<label class="control-label" for="submit"></label>
													<button id="submit" name="submit"
														class="btn btn-large btn-primary"
														formaction="/exam/admin/reRegistrationReport">Search</button>

													<button id="cancel" name="cancel" class="btn btn-danger"
														formaction="home" formnovalidate="formnovalidate">Cancel</button>
												</div>
											</div>
										</fieldset>
									</form:form>

									<div class="col-md-4">
											#FS - Failed Subject <br /> #GAP - Gap in Days since last
											Re-Registration <br /> #AB - TEE Absent Count <br /> #ANS -
											Assignment Not Submitted
									</div>
									<div class="col-md-4">
											 #PB - Pending Bookings <br />
											#SA - Sessions Attended <br /> Re-Reg - Re-Registration Cycle
									</div>

								</div>
								<c:choose>
									<c:when test="${rowCount > 0}">



										<h2>
											&nbsp;&nbsp;Re-Registration Report<font size="2px">
												(${rowCount} Records Found)&nbsp; <a
												href="/exam/admin/downloadToReRegistrationReportExcel"  style="margin-left: 10px;">Download
													to Excel</a>
											</font>
										</h2>
										<div class="clearfix"></div>
										<div class="panel-content-wrapper">


											<div class="form-group">
												<select id="semesterFilter">
													<option value="">Select Semester</option>
													<option value="All">Show All</option>
													<option value="1">Semester 1</option>
													<option value="2">Semester 2</option>
													<option value="3">Semester 3</option>
													<option value="4">Semester 4</option>
												</select>
											</div>


											<div class="form-group"></div>

											<div class="table-responsive">
												<table id="reregTable" class="table table-striped"
													style="font-size: 12px">
													<thead>
														<tr>
															<th>SAP ID</th>
															<th colspan="5">Student Details</th>
															<th class="semester1" colspan="6">Semester 1</th>
															<th class="semester2" colspan="6">Semester 2</th>
															<th class="semester3" colspan="6">Semester 3</th>
															<th class="semester4" colspan="6">Semester 4</th>
														</tr>
													</thead>
													<tbody>


														<tr>
															<td>SAPID</td>
															<td>Name</td>
															<td>Mobile</td>
															<td>EmailId</td>
															<%if(!"academic.admin".equalsIgnoreCase((String)session.getAttribute("userId"))){ %>
															<td>IC</td>
															<% } %>
															<td>ValidityEnd</td>
															<td class="semester1">#FS</td>
															<!-- <td class="semester1">#GAP</td> -->
															<td class="semester1">#AB</td>
															<td class="semester1">#ANS</td>
															<td class="semester1">#PB</td>
															<td class="semester1">#SA</td>
															<td class="semester1">Re-Reg</td>

															<td class="semester2">#FS</td>
													<!-- 		<td class="semester2">#GAP</td> -->
															<td class="semester2">#AB</td>
															<td class="semester2">#ANS</td>
															<td class="semester2">#PB</td>
															<td class="semester2">#SA</td>
															<td class="semester2">Re-Reg</td>

															<td class="semester3">#FS</td>
															<!-- <td class="semester3">#GAP</td> -->
															<td class="semester3">#AB</td>
															<td class="semester3">#ANS</td>
															<td class="semester3">#PB</td>
															<td class="semester3">#SA</td>
															<td class="semester3">Re-Reg</td>

															<td class="semester4">#FS</td>
														<!-- 	<td class="semester4">#GAP</td> -->
															<td class="semester4">#AB</td>
															<td class="semester4">#ANS</td>
															<td class="semester4">#PB</td>
															<td class="semester4">#SA</td>
															<td class="semester4">Re-Reg</td>


														</tr>

														<%
															for (String sapid : listOfSapIdOfActiveStudents) {
																StudentExamBean student = mapOfSAPIDAndStudentBean.get(sapid);
																String fullName = student.getFirstName() + " "+ student.getLastName();
														%>
														<tr>
															<td><a target="_blank" 
																href="/studentportal/viewStudentDetailsDashBoard?sapId=<%=sapid%>"><%=sapid%></a></td>
															<td><%=fullName%></td>
															<td><%=student.getMobile()%></td>
															<td><%=student.getEmailId()%></td>
															<%if(!"academic.admin".equalsIgnoreCase((String)session.getAttribute("userId"))){ %>
															<td><%=student.getCenterName()%></td>
															<% } %>
															<td><%=student.getValidityEndMonth()+ " - " +student.getValidityEndYear()%></td>
															<td class="semester1"><a href="#"
																onclick="getSubjectNames('FS',<%=sapid%>,'1');"><%=mapOfStudentNumberSemAndCountOfFailedSubjects.get(sapid + "-1") == null ? "0": mapOfStudentNumberSemAndCountOfFailedSubjects.get(sapid + "-1")%></a></td>
															<%-- <td class="semester1"><%=mapOfStudentNumberAndSemAndGAPInReReg.get(sapid + "-1") == null ? "0": mapOfStudentNumberAndSemAndGAPInReReg.get(sapid + "-1")%></td> --%>
															<td class="semester1"><a href="#"
																onclick="getSubjectNames('AB',<%=sapid%>,'1');"><%=mapOfStudentNumberAndSemAndCountOfABSubjects.get(sapid + "-1") == null ? "0": mapOfStudentNumberAndSemAndCountOfABSubjects.get(sapid + "-1")%></a></td>
															<td class="semester1"><a href="#"
																onclick="getSubjectNames('ANS',<%=sapid%>,'1');"><%=mapOfStudentNumberAndSemAndCountOfANSSubjects.get(sapid + "-1") == null ? "0": mapOfStudentNumberAndSemAndCountOfANSSubjects.get(sapid + "-1")%></a></td>
															<td class="semester1"><a href="#"
																onclick="getSubjectNames('PB',<%=sapid%>,'1');"><%=mapOfStudentNumberAndSemAndPendingNumberOfExamBookings.get(sapid + "-1") == null ? "0": mapOfStudentNumberAndSemAndPendingNumberOfExamBookings.get(sapid + "-1")%></a></td>
															<td class="semester1"><%=mapOfStudentNumberAndSemAndCountOfSessionsAttended.get(sapid + "-1") == null ? "0": mapOfStudentNumberAndSemAndCountOfSessionsAttended.get(sapid + "-1")%></td>
															<td class="semester1"><%=mapOfStudentNumberAndSemAndDriveMonthYear.get(sapid + "-1") == null ? "--": mapOfStudentNumberAndSemAndDriveMonthYear.get(sapid + "-1")%></td>


															<td class="semester2"><a href="#"
																onclick="getSubjectNames('FS',<%=sapid%>,'2');"><%=mapOfStudentNumberSemAndCountOfFailedSubjects.get(sapid + "-2") == null ? "0": mapOfStudentNumberSemAndCountOfFailedSubjects.get(sapid + "-2")%></a></td>
															<%-- <td class="semester2"><%=mapOfStudentNumberAndSemAndGAPInReReg.get(sapid + "-2") == null ? "0": mapOfStudentNumberAndSemAndGAPInReReg.get(sapid + "-2")%></td> --%>
															<td class="semester2"><a href="#"
																onclick="getSubjectNames('AB',<%=sapid%>,'2');"><%=mapOfStudentNumberAndSemAndCountOfABSubjects.get(sapid + "-2") == null ? "0": mapOfStudentNumberAndSemAndCountOfABSubjects.get(sapid + "-2")%></a></td>
															<td class="semester2"><a href="#"
																onclick="getSubjectNames('ANS',<%=sapid%>,'2');"><%=mapOfStudentNumberAndSemAndCountOfANSSubjects.get(sapid + "-2") == null ? "0": mapOfStudentNumberAndSemAndCountOfANSSubjects.get(sapid + "-2")%></a></td>
															<td class="semester2"><a href="#"
																onclick="getSubjectNames('PB',<%=sapid%>,'2');"><%=mapOfStudentNumberAndSemAndPendingNumberOfExamBookings.get(sapid + "-2") == null ? "0": mapOfStudentNumberAndSemAndPendingNumberOfExamBookings.get(sapid + "-2")%></a></td>
															<td class="semester2"><%=mapOfStudentNumberAndSemAndCountOfSessionsAttended.get(sapid + "-2") == null ? "0": mapOfStudentNumberAndSemAndCountOfSessionsAttended.get(sapid + "-2")%></td>
															<td class="semester2"><%=mapOfStudentNumberAndSemAndDriveMonthYear.get(sapid + "-2") == null ? "--": mapOfStudentNumberAndSemAndDriveMonthYear.get(sapid + "-2")%></td>

															<td class="semester3"><a href="#"
																onclick="getSubjectNames('FS',<%=sapid%>,'3');"><%=mapOfStudentNumberSemAndCountOfFailedSubjects.get(sapid + "-3") == null ? "0": mapOfStudentNumberSemAndCountOfFailedSubjects.get(sapid + "-3")%></a></td>
															<%-- <td class="semester3"><%=mapOfStudentNumberAndSemAndGAPInReReg.get(sapid + "-3") == null ? "0": mapOfStudentNumberAndSemAndGAPInReReg.get(sapid + "-3")%></td> --%>
															<td class="semester3"><a href="#"
																onclick="getSubjectNames('AB',<%=sapid%>,'3');"><%=mapOfStudentNumberAndSemAndCountOfABSubjects.get(sapid + "-3") == null ? "0": mapOfStudentNumberAndSemAndCountOfABSubjects.get(sapid + "-3")%></a></td>
															<td class="semester3"><a href="#"
																onclick="getSubjectNames('ANS',<%=sapid%>,'3');"><%=mapOfStudentNumberAndSemAndCountOfANSSubjects.get(sapid + "-3") == null ? "0": mapOfStudentNumberAndSemAndCountOfANSSubjects.get(sapid + "-3")%></a></td>
															<td class="semester3"><a href="#"
																onclick="getSubjectNames('PB',<%=sapid%>,'3');"><%=mapOfStudentNumberAndSemAndPendingNumberOfExamBookings.get(sapid + "-3") == null ? "0": mapOfStudentNumberAndSemAndPendingNumberOfExamBookings.get(sapid + "-3")%></a></td>
															<td class="semester3"><%=mapOfStudentNumberAndSemAndCountOfSessionsAttended.get(sapid + "-3") == null ? "0": mapOfStudentNumberAndSemAndCountOfSessionsAttended.get(sapid + "-3")%></td>
															<td class="semester3"><%=mapOfStudentNumberAndSemAndDriveMonthYear.get(sapid + "-3") == null ? "--": mapOfStudentNumberAndSemAndDriveMonthYear.get(sapid + "-3")%></td>

															<td class="semester4"><a href="#"
																onclick="getSubjectNames('FS',<%=sapid%>,'4');"><%=mapOfStudentNumberSemAndCountOfFailedSubjects.get(sapid + "-4") == null ? "0": mapOfStudentNumberSemAndCountOfFailedSubjects.get(sapid + "-4")%></a></td>
															<%-- <td class="semester4"><%=mapOfStudentNumberAndSemAndGAPInReReg.get(sapid + "-4") == null ? "0": mapOfStudentNumberAndSemAndGAPInReReg.get(sapid + "-4")%></td> --%>
															<td class="semester4"><a href="#"
																onclick="getSubjectNames('AB',<%=sapid%>,'4');"><%=mapOfStudentNumberAndSemAndCountOfABSubjects.get(sapid + "-4") == null ? "0": mapOfStudentNumberAndSemAndCountOfABSubjects.get(sapid + "-4")%></a></td>
															<td class="semester4"><a href="#"
																onclick="getSubjectNames('ANS',<%=sapid%>,'4');"><%=mapOfStudentNumberAndSemAndCountOfANSSubjects.get(sapid + "-4") == null ? "0": mapOfStudentNumberAndSemAndCountOfANSSubjects.get(sapid + "-4")%></a></td>
															<td class="semester4"><a href="#"
																onclick="getSubjectNames('PB',<%=sapid%>,'4');"><%=mapOfStudentNumberAndSemAndPendingNumberOfExamBookings.get(sapid + "-4") == null ? "0": mapOfStudentNumberAndSemAndPendingNumberOfExamBookings.get(sapid + "-4")%></a></td>
															<td class="semester4"><%=mapOfStudentNumberAndSemAndCountOfSessionsAttended.get(sapid + "-4") == null ? "0": mapOfStudentNumberAndSemAndCountOfSessionsAttended.get(sapid + "-4")%></td>
															<td class="semester4"><%=mapOfStudentNumberAndSemAndDriveMonthYear.get(sapid + "-4") == null ? "--": mapOfStudentNumberAndSemAndDriveMonthYear.get(sapid + "-4")%></td>
														</tr>
														<%
															}
														%>

													</tbody>
												</table>
											</div>
										</div>
										<br>

									</c:when>
								</c:choose>

								<c:url var="firstUrl" value="/admin/reRegistrationReportPage?pageNo=1" />
								<c:url var="lastUrl"
									value="/admin/reRegistrationReportPage?pageNo=${page.totalPages}" />
								<c:url var="prevUrl"
									value="/admin/reRegistrationReportPage?pageNo=${page.currentIndex - 1}" />
								<c:url var="nextUrl"
									value="/admin/reRegistrationReportPage?pageNo=${page.currentIndex + 1}" />



								<c:choose>
									<c:when test="${page.totalPages > 1}">
										<div align="center">
											<ul class="pagination">
												<c:choose>
													<c:when test="${page.currentIndex == 1}">
														<li class="disabled"><a href="#">&lt;&lt;</a></li>
														<li class="disabled"><a href="#">&lt;</a></li>
													</c:when>
													<c:otherwise>
														<li><a href="${firstUrl}">&lt;&lt;</a></li>
														<li><a href="${prevUrl}">&lt;</a></li>
													</c:otherwise>
												</c:choose>
												<c:forEach var="i" begin="${page.beginIndex}"
													end="${page.endIndex}">
													<c:url var="pageUrl"
														value="/admin/reRegistrationReportPage?pageNo=${i}" />
													<c:choose>
														<c:when test="${i == page.currentIndex}">
															<li class="active"><a href="${pageUrl}"><c:out
																		value="${i}" /></a></li>
														</c:when>
														<c:otherwise>
															<li><a href="${pageUrl}"><c:out value="${i}" /></a></li>
														</c:otherwise>
													</c:choose>
												</c:forEach>
												<c:choose>
													<c:when test="${page.currentIndex == page.totalPages}">
														<li class="disabled"><a href="#">&gt;</a></li>
														<li class="disabled"><a href="#">&gt;&gt;</a></li>
													</c:when>
													<c:otherwise>
														<li><a href="${nextUrl}">&gt;</a></li>
														<li><a href="${lastUrl}">&gt;&gt;</a></li>
													</c:otherwise>
												</c:choose>
											</ul>
										</div>
									</c:when>
								</c:choose>
							</div>
						</div>
					</div>
				</div>
			</div>
			<jsp:include page="../adminCommon/footer.jsp" />
			<%
				} catch (Exception e) {
						
					}
			%>
			<div id="viewSubjectModal" class="modal fade" role="dialog">
				<div class="modal-dialog">

					<!-- Modal content-->
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal">&times;</button>
							<h4 id="modal-title" class="modal-title"></h4>
						</div>
						<div class="modal-body">
							<p id="subjectNames"></p>
						</div>
						<div class="modal-footer">
							<button type="button" class="btn btn-default"
								data-dismiss="modal">Close</button>
						</div>
					</div>

				</div>
			</div>
</body>
<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />assets/js/jquery.table2excel.js"></script>
<script>
$('#semesterFilter').on('change', function() {
	  var semesterValue = this.value;
	  if(semesterValue == "1"){
		  $(".semester2").hide();
		  $(".semester3").hide();
		  $(".semester4").hide();
		  $(".semester1").show();
	  }
	  if(semesterValue == "2"){
		  $(".semester1").hide();
		  $(".semester2").show();
		  $(".semester3").hide();
		  $(".semester4").hide();
	  }
	  if(semesterValue == "3"){
		  $(".semester1").hide();
		  $(".semester2").hide();
		  $(".semester3").show();
		  $(".semester4").hide();
	  }
	  if(semesterValue == "4"){
		  $(".semester1").hide();
		  $(".semester2").hide();
		  $(".semester3").hide();
		  $(".semester4").show();
	  }
	  if(semesterValue == "All"){
		  $(".semester1").show();
		  $(".semester2").show();
		  $(".semester3").show();
		  $(".semester4").show();
	  }
	})


function downloadToExcel(){
	$("#reregTable").table2excel({
		exclude: ".noExl",
		name: "Excel Document Name"
	});
	
	
	
}

function getSubjectNames(columnType,sapid,semester){
	
	var xhttp = new XMLHttpRequest();
	var subjectList = new Array();
	$("#subjectNames").val('');
	$("#modal-title").text('');
	xhttp.onreadystatechange = function() {
	    if (this.readyState == 4 && this.status == 200) {
	    	var subjectList;
	    	var subjectNamesForDisplay;
	    	response = this.responseText;
	    	subjectList = response.split(',');
	    	
	    		$("#viewSubjectModal").modal('show');
	      	
	      		$("#subjectNames").text(subjectList);
	      		
		 	 	if(columnType == 'PS'){
		 	 		
		 	 		$("#modal-title").append('Pass Subject List');
		 	 	}
		 	 	if(columnType=='FS'){
		 	 		$("#modal-title").append('Failed Subject List');
		 	 	}
		 	 	if(columnType=='ANS'){
		 	 		$("#modal-title").append('ANS Subject List');
		 	 	}
	    	
	    	
	    }
	  };
	  xhttp.open("GET", "/exam/admin/getSubjectNames?type="+columnType+"&sapid="+sapid+"&sem="+semester, true);
	  xhttp.send();
}

</script>
</html>