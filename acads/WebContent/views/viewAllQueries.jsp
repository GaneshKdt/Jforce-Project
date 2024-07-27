<!DOCTYPE html>
<html lang="en">

<%@page import="com.nmims.beans.PersonAcads"%>
<%@page import="com.nmims.beans.PageAcads"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<jsp:include page="adminCommon/jscss.jsp">
	<jsp:param value="Search Unanswered Queries" name="title" />
</jsp:include>
<body>
	<%@ include file="adminCommon/header.jsp"%>
	<div class="sz-main-content-wrapper">

		<jsp:include page="adminCommon/breadcrum.jsp">
			<jsp:param value="Exam;Search Exam Centers" name="breadcrumItems" />
		</jsp:include>


		<div class="sz-main-content menu-closed">
			<div class="sz-main-content-inner">
				<jsp:include page="adminCommon/left-sidebar.jsp">
					<jsp:param value="" name="activeMenu" />
				</jsp:include>
				<div class="sz-content-wrapper examsPage">
					<%@ include file="adminCommon/adminInfoBar.jsp"%>
					<div class="sz-content">
						<h2 class="red text-capitalize">Search Queries</h2>
						<div class="clearfix"></div>
						<div class="panel-content-wrapper" style="min-height: 450px;">
							<%@ include file="adminCommon/messages.jsp"%>
							<form:form action="viewAllUnAnsweredQueriesForm" method="post"
								modelAttribute="queryAnswer">
								<fieldset>
									<div class="col-md-4">
										<div class="form-group">
											<form:select id="year" path="year" type="text"
												required="required" placeholder="Year" class="form-control">
												<form:option value="">Select Acad Year</form:option>
												<form:options items="${yearList}" />
											</form:select>
										</div>

										<div class="form-group">
											<form:select id="month" path="month" type="text"
												required="required" placeholder="Month" class="form-control">
												<form:option value="">Select Acad Month</form:option>
												<form:option value="Jan">Jan</form:option>
												<form:option value="Jul">Jul</form:option>

											</form:select>
										</div>

										<div class="form-group">
											<form:select id="facultyId" path="facultyId" type="text"
												placeholder="Faculty" class="form-control">
												<form:option value="">Select Faculty</form:option>
												<c:forEach var="faculty" items="${listOfFaculty}">
													<form:option value="${faculty.facultyId}">${faculty.firstName} ${faculty.lastName}</form:option>
												</c:forEach>
											</form:select>
										</div>




										<div class="form-group">
											<label class="control-label" for="submit"></label>
											<button id="submit" name="submit"
												class="btn btn-large btn-primary"
												formaction="viewAllUnAnsweredQueries">Search</button>
											<button id="reset" type="reset" class="btn btn-danger"
												type="reset">Reset</button>
											<button id="cancel" name="cancel" class="btn btn-danger"
												formaction="acadHome" formnovalidate="formnovalidate">Cancel</button>
										</div>
									</div>

								</fieldset>
							</form:form>
						</div>
						<c:choose>
							<c:when test="${rowCount > 0}">
								<h2 style="margin-left: 50px;">
									&nbsp;Un-Answered Queries<font size="2px"> (${rowCount}
										Records Found)&nbsp;</font>
								</h2>

								<div class="clearfix"></div>
								<form:form action="allocateQueryToFaculty" method="post"
									modelAttribute="allocateAnswer">
									<form:hidden path="id"/>
									<form:hidden path="facultyId"/>
									<div class="panel-content-wrapper">
										<div class="table-responsive">
											<table class="table table-striped table-hover"
												style="font-size: 12px">
												<thead>
													<tr>
														<th>Sr. No.</th>
														<th>SAP ID</th>
														<th>Session Name</th>
														<th>Faculty For Session</th>
														<th>Query</th>
														<th>Subject</th>
														<th>Allocate</th>

													</tr>
												</thead>
												<tbody>
													<%
														try {
													%>

													<c:forEach var="queryAns"
														items="${listOfSessionQueryAnswerForMonthAndYear}"
														varStatus="status">
														<tr>
															<td><c:out value="${status.count}" /></td>
															<td><c:out value="${queryAns.sapId}" /></td>
															<td><c:out value="${queryAns.sessionName}" /></td>
															<td><c:out
																	value="${queryAns.firstName} ${queryAns.lastName}" /></td>
															<td><c:out value="${queryAns.query}" /></td>
															<td><c:out value="${queryAns.subject}" /></td>
															<td><form:checkbox value="${queryAns.id}"
																	path="listOfRecordIdToBeAssigned" /></td>
														</tr>
													</c:forEach>

													<%
														} catch (Exception e) {
																		  
																	}
													%>

												</tbody>
											</table>
										</div>
										<div class="form-group">
											<button id="submit" name="submit"
												class="btn btn-large btn-primary"
												formaction="allocateFacultyToQueries">Allocate</button>
										</div>
									</div>
								</form:form>

								<br>
							</c:when>
						</c:choose>


					</div>
				</div>
			</div>
		</div>
	</div>
	<jsp:include page="adminCommon/footer.jsp" />


</body>
</html>