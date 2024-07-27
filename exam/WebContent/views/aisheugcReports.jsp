
<!DOCTYPE html>
<html lang="en">

<%@page import="com.nmims.beans.Person"%>
<%@page import="com.nmims.beans.Page"%>
<%@page import="com.nmims.beans.OnlineExamMarksBean"%>
<%@page import="com.nmims.beans.StudentExamBean"%>



<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<jsp:include page="adminCommon/jscss.jsp">
	<jsp:param value="Search Students" name="title" />
</jsp:include>


<body>

	<%@ include file="adminCommon/header.jsp"%>
	<div class="sz-main-content-wrapper">

		<jsp:include page="adminCommon/breadcrum.jsp">
			<jsp:param value="Exam;Search Students" name="breadcrumItems" />
		</jsp:include>


		<div class="sz-main-content menu-closed">
			<div class="sz-main-content-inner">
				<jsp:include page="adminCommon/left-sidebar.jsp">
					<jsp:param value="" name="activeMenu" />
				</jsp:include>


				<div class="sz-content-wrapper examsPage">
					<%@ include file="adminCommon/adminInfoBar.jsp"%>
					<div class="sz-content">

						<h2 class="red text-capitalize">Search Students</h2>
						<div class="clearfix"></div>
						<div class="panel-content-wrapper" style="min-height: 450px;">
							<%@ include file="adminCommon/messages.jsp"%>

							<form:form  action="/exam/admin/aisheugcReportForm" method="post" 
								modelAttribute="student"  enctype="multipart/form-data"  >
								<fieldset>





									<%-- <div class="col-md-4">
										<div class="form-group">
											<form:select id="program" path="program" type="text"
												placeholder="Program" class="form-control"
												itemValue="${student.program}">
												<form:option value="">Select Program</form:option>
												<form:options items="${programList}" />
											</form:select>
										</div>
                                               --%>
          
										<div class="form-group">
											<form:select id="enrollmentYear" path="enrollmentYear"
												type="text" placeholder="Year" class="form-control"
												itemValue="${student.enrollmentYear}">
												<form:option value="">Select Exam  Year</form:option>
												<form:options items="${yearList}" />
											</form:select>
										</div>

										<div class="form-group">
											<form:select id="enrollmentMonth" path="enrollmentMonth"
												type="text" placeholder="Month" class="form-control"
												itemValue="${student.enrollmentMonth}">
												<form:option value="">Select Exam Month</form:option>
												<form:options items="${monthList }" />
											</form:select>
										</div>
										
										
										<div class="form-group">
											<form:select id="sem" path="sem" placeholder="Semester"
												class="form-control" value="${student.sem}">
												<form:option value="">Select Semester</form:option>
												<form:option value="1">1</form:option>
												<form:option value="2">2</form:option>
												<form:option value="3">3</form:option>
												<form:option value="4">4</form:option>
											</form:select>
										</div>


										<div class="col-md-6">

											<div class="form-group">
												<label class="control-label" for="submit"></label>

												<button id="submit" name="submit"
													class="btn btn-large btn-primary"
													formaction="aisheugcReport">Search</button>

												<button id="reset" type="reset" class="btn btn-danger"
													type="reset">Reset</button>


											</div>
										</div>
								</fieldset>
							</form:form>
						</div>

						<h2>
							&nbsp;Student Records <font size="2px"> (${rowCount}
								Records Found)&nbsp; <a href="aisheugcReportdownloads">Download
									to Excel</a>
							</font>
						</h2>
						<div class="clearfix"></div>
						<div class="panel-content-wrapper">
							<div class="table-responsive">
								<table class="table table-striped table-hover"
									style="font-size: 12px" id="aisheugcReports">
									<thead>
										<tr>
											<th>Sr. No.</th>
											<!-- <th>Student ID</th>
											<th>enrollment  Year</th>
											<th> enrollment Month</th>		 -->									
											<th>Program Name</th>
											<th>sem</th>
											
											
											
										</tr>
									</thead>
									<tbody>

										<c:forEach var="student" items="${AllListOfProgram}"
											varStatus="status">
											<tr>
												<td><c:out value="${status.count}" /></td>
												<%-- <td><c:out value="${student.sapid}" /></td>
												<td><c:out value="${student.enrollmentMonth}" /></td>
												<td><c:out value="${student.enrollmentYear}" /></td>	 --%>											
												<td><c:out value="${student.program}" /></td>
												<td><c:out value="${student.sem}" /></td>
												



											</tr>
										</c:forEach>


									</tbody>
								</table>
							</div>
						</div>

					</div>
				</div>
			</div>
</body>
</html>

