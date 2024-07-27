<!DOCTYPE html>
<!-- Deprecated. This JSP file was used by the /admin/viewSingleStudentDetail API, which is now replaced by /viewStudent API.  -->
<html lang="en">

<%@page import="com.nmims.beans.Person"%>
<%@page import="com.nmims.beans.Page"%>
<%@page import="com.nmims.beans.OnlineExamMarksBean"%>


<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<jsp:include page="adminCommon/jscss.jsp">
	<jsp:param value="Student Details" name="title" />
</jsp:include>


<%
	try {
%>
<body>

	<%@ include file="adminCommon/header.jsp"%>
	<div class="sz-main-content-wrapper">

		<jsp:include page="adminCommon/breadcrum.jsp">
			<jsp:param value="Exam;Student Details" name="breadcrumItems" />
		</jsp:include>


		<div class="sz-main-content menu-closed">
			<div class="sz-main-content-inner">
				<jsp:include page="adminCommon/left-sidebar.jsp">
					<jsp:param value="" name="activeMenu" />
				</jsp:include>


				<div class="sz-content-wrapper examsPage">
					<%@ include file="adminCommon/adminInfoBar.jsp"%>
					<div class="sz-content">

						<h2 class="red text-capitalize">Student Details</h2>
						<div class="clearfix"></div>
						<div class="panel-content-wrapper" style="min-height: 450px;">
							<%@ include file="adminCommon/messages.jsp"%>
							<form:form action="studentDetails" method="post"
								modelAttribute="studentDetail">
								<fieldset>
									<h4><label style="font-size:medium">First Name:</label> ${studentDetail.firstName}</h4>
									<h4><label style="font-size:medium">Last Name: </label>${studentDetail.lastName}</h4>
									<h4><label style="font-size:medium">Middle Name:</label> ${studentDetail.middleName}</h4>
									<h4><label style="font-size:medium">Father Name: </label>${studentDetail.fatherName}</h4>
									<h4><label style="font-size:medium">Mother Name:</label> ${studentDetail.motherName}</h4>
									<h4><label style="font-size:medium">Spouse Name: </label>${studentDetail.husbandName}</h4>
									<h4><label style="font-size:medium">DOB: </label>${studentDetail.dob}</h4>
									<h4><label style="font-size:medium">Gender:</label> ${studentDetail.gender}</h4>
									<h4><label style="font-size:medium">Email Id:</label> ${studentDetail.emailId}</h4>
									<h4><label style="font-size:medium">Mobile: </label>${studentDetail.mobile}</h4>
									<h4><label style="font-size:medium">Address: </label>${studentDetail.address}</h4>
									<h4><label style="font-size:medium">Enrollment Year:</label> ${studentDetail.enrollmentYear}</h4>
									<h4><label style="font-size:medium">Enrollment Month: </label>${studentDetail.enrollmentMonth}</h4>
									<h4><label style="font-size:medium">SapId:</label> ${studentDetail.sapid}</h4>
									<h4><label style="font-size:medium">Program:</label> ${studentDetail.program}</h4>
									<h4><label style="font-size:medium">Semester:</label> ${studentDetail.sem}</h4>
									<h4><label style="font-size:medium">Program Status: </label>${studentDetail.programStatus}</h4>
									<h4><label style="font-size:medium">Program Structure: </label>${studentDetail.prgmStructApplicable}</h4>
									<h4><label style="font-size:medium">Program Remarks: </label>${studentDetail.programRemarks}</h4>
					                             
                                    <div class="control-group">
										<div class="controls">
		                                    <c:url value="editStudent" var="editurl">
												<c:param name="sapid" value="${studentDetail.sapid}" />
												<c:param name="sem" value="${studentDetail.sem}" />
											</c:url> 
											
											<%if (roles.indexOf("Exam Admin") != -1
					 					          || roles.indexOf("Assignment Admin") != -1
					 					              || roles.indexOf("Acads Admin") != -1) {%> 
					 					    <button id="edit" name="edit" class="btn btn-primary" formaction="${editurl}">Edit</button>
					 					     <%}%> 
											<button id="cancel" name="cancel" class="btn btn-danger" formaction="home" formnovalidate="formnovalidate">Back to Home</button>
					
										
										</div>
									</div>
									<div class="clearfix"></div>
									<c:if test="${validityExtensionSize >0 }">
										<div class="table-responsive">
											<div class="col-md-12 col-sm-12">
												<table class="table table-striped table-hover tables"
													style="font-size: 12px">
													<thead>
														<tr>
															<th>Old Validity</th>
															<th>New Validity</th>
															<th>Extended By</th>
															<th>Extended On</th>
	
														</tr>
													</thead>
													<tbody>
														<c:forEach var="student" items="${validityExtensionList}">
															<tr>
																<td>${student.oldValidityEndMonth}-${student.oldValidityEndYear}</td>
																<td>${student.newValidityEndMonth}-${student.newValidityEndYear}</td>
																<td>${student.lastModifiedBy}</td>
																<td>${student.lastModifiedDate}</td>
															</tr>
	
														</c:forEach>
													</tbody>
	
												</table> 
											</div>
										</div>
									</c:if>
									
								</fieldset>
							</form:form>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>

	<%
		} catch(Exception e){}
	%>
	<jsp:include page="adminCommon/footer.jsp" />


	

</body>
</html>
