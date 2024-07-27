<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<jsp:include page="jscss.jsp">
	<jsp:param value="Add Division Students" name="title" />
</jsp:include>
<head>
<meta charset="ISO-8859-1">
<title>Insert title here</title>
</head>
<body>
	<%@ include file="header.jsp"%>
	<section class="content-container login">
		<div class="container-fluid customTheme">
			<div class="row">
				<legend>Add Students to Master Division </legend>
			</div>
			<div class="row">
						<div class="col-12">
							<%@ include file="messages.jsp"%>
						</div>
						
							
				<div class="col-lg-5 col-sm-12 ">
						<form:form modelAttribute="fileBean" method="post" action="uploadStudentToDivision" enctype="multipart/form-data">
							<div class="form-group">
								<form:label for="fileData" path="fileData">Select file</form:label>
								<form:input path="fileData" type="file"/>
							</div>
							<div class="form-group">
								<button id="submit" name="submit"
									class="btn btn-large btn-primary " formaction="uploadStudentToDivision">Upload</button>
								<button id="cancel" name="cancel" class="btn btn-danger "
									formaction="home" formnovalidate="formnovalidate">Cancel</button>
							</div>
						</form:form>
				</div>
	
					
				
				<div class="col-lg-5 col-sm-12 ">
							<b>Format of Upload: </b><br>
							Sapid | Student Type<br>
	<%-- 						<a href="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/templates/Upload_TimeBound_Student_Sapid.xlsx" target="_blank">Download a Sample Template</a> --%>
							<br>
	 						<h2>&nbsp;Existing Students (${listOfExistingStudent.size()})</h2> 
	 						<c:if test="${listOfExistingStudent.size() > 0 }">
								<div class="panel-body">
									<div class="table-responsive">
										<table class="table table-striped table-hover dataTables" style="font-size:12px">
											<thead>
											<tr>
												<th>Sr.No</th>
												<th>Sapid</th>
												<th>Division Id</th>
												<th>Action</th>
											</tr>
											</thead>
											<tbody>
												<c:forEach var="student" items="${listOfExistingStudent}" varStatus="status">
													<tr value="${student.id}~${student.sapId}">
														<td><c:out value="${status.count}" /></td>
														<td><c:out value="${student.sapId}" /></td>
														<td><c:out value="${student.divisionId}" /></td>
	<%-- 													<td><c:out value="${student.role}" /></td> --%>
														<td></td>
													</tr>
												</c:forEach>
											</tbody>
										</table>
									</div>
								</div>
							</c:if> 
							
						</div>
				
				</div>
			</div>
	</section>
	<jsp:include page="footer.jsp" />
</body>
</html>
