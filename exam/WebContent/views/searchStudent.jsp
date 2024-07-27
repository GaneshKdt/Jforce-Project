<%-- <!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!--> 

<%@page import="com.nmims.beans.Person"%>
<%@page import="com.nmims.beans.Page"%>
<%@page import="com.nmims.beans.StudentMarksBean"%>
<html class="no-js"> <!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<jsp:include page="jscss.jsp">
<jsp:param value="Search Students" name="title" />
</jsp:include>

<body class="inside">

<%@ include file="header.jsp"%>
	
    <section class="content-container login">
        <div class="container-fluid customTheme">
        <div class="row"><legend>Search Students</legend></div>
        <%@ include file="messages.jsp"%>
		<div class="panel-body clearfix">
		<form:form  action="searchStudents" method="post" modelAttribute="student">
			<fieldset>
			<div class="col-md-6 column">

					<div class="form-group">
						<form:select id="enrollmentYear" path="enrollmentYear" type="text"	placeholder="Year" class="form-control"   itemValue="${student.enrollmentYear}">
							<form:option value="">Select Enrollment Year</form:option>
							<form:options items="${yearList}" />
						</form:select>
					</div>
				
					<div class="form-group">
						<form:select id="enrollmentMonth" path="enrollmentMonth" type="text" placeholder="Month" class="form-control"  itemValue="${student.enrollmentMonth}">
							<form:option value="">Select Enrollment Month</form:option>
							<form:option value="Jan">Jan</form:option>
							<form:option value="Jul">Jul</form:option>
						</form:select>
					</div>
					
					<div class="form-group">
						<form:select id="validityEndYear" path="validityEndYear" type="text"	placeholder="Year" class="form-control"   itemValue="${student.validityEndYear}">
							<form:option value="">Select Validity End Year</form:option>
							<form:options items="${yearList}" />
						</form:select>
					</div>
				
					<div class="form-group">
						<form:select id="validityEndMonth" path="validityEndMonth" type="text" placeholder="Month" class="form-control"  itemValue="${student.validityEndMonth}">
							<form:option value="">Select Validity End Month</form:option>
							<form:option value="Jun">Jun</form:option>
							<form:option value="Dec">Dec</form:option>
						</form:select>
					</div>
							
					<div class="form-group">
							<form:input id="sapid" path="sapid" type="text" placeholder="SAP ID" class="form-control" value="${student.sapid}"/>
					</div>
					
					<div class="form-group">
							<form:input id="emailId" path="emailId" type="email" placeholder="Email ID" class="form-control" value="${student.emailId}"/>
					</div>
					

			

			</div>



			<div class="col-md-6 column">
				<div class="form-group">
						<form:select id="program" path="program" type="text"	placeholder="Program" class="form-control"  itemValue="${student.program}">
							<form:option value="">Select Program</form:option>
							<form:options items="${programList}" />
						</form:select>
					</div>
					
					<div class="form-group">
						<form:select id="sem" path="sem" placeholder="Semester" class="form-control"  value="${student.sem}">
							<form:option value="">Select Semester</form:option>
							<form:option value="1">1</form:option>
							<form:option value="3">3</form:option>
						</form:select>
					</div>
					
					<div class="form-group">
							<form:input id="firstName" path="firstName" type="text" placeholder="First Name" class="form-control" value="${student.firstName}"/>
					</div>
					
					<div class="form-group">
							<form:input id="lastName" path="lastName" type="text" placeholder="Last Name" class="form-control" value="${student.lastName}"/>
					</div>
					
					<div class="form-group">
						<form:select id="prgmStructApplicable" path="prgmStructApplicable" placeholder="Program Structure" class="form-control"  value="${student.prgmStructApplicable}">
							<form:option value="">Select Program Structure</form:option>
							<form:option value="Jul2009">Jul2009</form:option>
							<form:option value="Jul2013">Jul2013</form:option>
							<form:option value="Jul2014">Jul2014</form:option>
						</form:select>
					</div>

					<div class="form-group">
						<div style="display: inline-block;">
						<form:checkbox path="validStudent"  style="width: 50px;"/> Active Students Only
						</div>
					</div>
	
			
			</div>
			
			<div class="col-md-6 column">
				<div class="form-group">
				<textarea name="sapIdList" cols="50" rows="7" placeholder="Enter different Student Ids in new lines">${student.sapIdList}</textarea>
				</div>
				
				<div class="form-group">
					<label class="control-label" for="submit"></label>

						<button id="submit" name="submit" class="btn btn-large btn-primary" formaction="searchStudents">Search</button>
						<button id="reset" type="reset" class="btn btn-danger" type="reset">Reset</button>
						<button id="cancel" name="cancel" class="btn btn-danger" formaction="home" formnovalidate="formnovalidate">Cancel</button>

				</div>
					
			</div>

</fieldset>
		</form:form>
		
		</div>
	</div>
	
	<c:choose>
<c:when test="${rowCount > 0}">

	<legend>&nbsp;Student Records <font size="2px">(${rowCount} Records Found)&nbsp; <a href="downloadStudents">Download to Excel</a></font></legend>
	<div class="panel-body table-responsive">
	<table class="table table-striped table-hover" style="font-size:12px">
						<thead>
							<tr> 
								<th>Sr. No.</th>
								<th>SAP ID</th>
								<th>First Name</th>
								<th>Last Name</th>
								<th>Program</th>
								<th>Sem</th>
								<th>Email</th>
								<th>Mobile</th>
								<th>Enrollment Year</th>
								<th>Enrollment Month</th>
								<th>Validity End Year</th>
								<th>Validity End Month</th>
								<th>Program Structure</th>
								<th>Actions</th>
							</tr>
						</thead>
						<tbody>
						
						<c:forEach var="student" items="${studentList}" varStatus="status">
					        <tr>
					            <td><c:out value="${status.count}"/></td>
					            <td><c:out value="${student.sapid}"/></td>
					            <td><c:out value="${student.firstName}"/></td>
								<td><c:out value="${student.lastName}"/></td>
								<td><c:out value="${student.program}"/></td>
								<td><c:out value="${student.sem}"/></td>
								<td><c:out value="${student.emailId}"/></td>
								<td><c:out value="${student.mobile}"/></td>
								<td><c:out value="${student.enrollmentYear}"/></td>
								<td><c:out value="${student.enrollmentMonth}"/></td>
								<td><c:out value="${student.validityEndYear}"/></td>
								<td><c:out value="${student.validityEndMonth}"/></td>
								<td><c:out value="${student.prgmStructApplicable}"/></td>
								 <td> 
						            <c:url value="editStudent" var="editurl">
									  <c:param name="sapid" value="${student.sapid}" />
									  <c:param name="sem" value="${student.sem}" />
									</c:url>

									<%if(roles.indexOf("Exam Admin") != -1 || roles.indexOf("Assignment Admin") != -1 || roles.indexOf("Acads Admin") != -1){ %>
									<a href="${editurl}" title="Edit"><i class="fa fa-pencil-square-o fa-lg"></i></a>
									<%} %>

					            </td>
								
					        </tr>   
					    </c:forEach>
							
							
						</tbody>
					</table>
	</div>
	<br>

</c:when>
</c:choose>

<c:url var="firstUrl" value="searchStudentsPage?pageNo=1" />
<c:url var="lastUrl" value="searchStudentsPage?pageNo=${page.totalPages}" />
<c:url var="prevUrl" value="searchStudentsPage?pageNo=${page.currentIndex - 1}" />
<c:url var="nextUrl" value="searchStudentsPage?pageNo=${page.currentIndex + 1}" />


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
        <c:forEach var="i" begin="${page.beginIndex}" end="${page.endIndex}">
            <c:url var="pageUrl" value="searchStudentsPage?pageNo=${i}" />
            <c:choose>
                <c:when test="${i == page.currentIndex}">
                    <li class="active"><a href="${pageUrl}"><c:out value="${i}" /></a></li>
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


	</section>

	  <jsp:include page="footer.jsp" />


</body>
</html>
 --%>
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
							<form:form action="searchStudents" method="post"
								modelAttribute="student">
								<fieldset>
									<div class="col-md-4">

										<div class="form-group">
											<form:select id="enrollmentYear" path="enrollmentYear"
												type="text" placeholder="Year" class="form-control"
												itemValue="${student.enrollmentYear}" Multiple="true"
												Size="4">
												<form:option value="">Select Enrollment Year</form:option>
												<form:options items="${yearList}" />
											</form:select>
										</div>

										<div class="form-group">
											<form:select id="enrollmentMonth" path="enrollmentMonth"
												type="text" placeholder="Month" class="form-control"
												itemValue="${student.enrollmentMonth}">
												<form:option value="">Select Enrollment Month</form:option>
												<form:options items="${monthList }" />
											</form:select>
										</div>

										<div class="form-group">
											<form:select id="validityEndYear" path="validityEndYear"
												type="text" placeholder="Year" class="form-control"
												itemValue="${student.validityEndYear}">
												<form:option value="">Select Validity End Year</form:option>
												<form:options items="${yearList}" />
											</form:select>
										</div>

										<div class="form-group">
											<form:select id="validityEndMonth" path="validityEndMonth"
												type="text" placeholder="Month" class="form-control"
												itemValue="${student.validityEndMonth}">
												<form:option value="">Select Validity End Month</form:option>
												<form:options items="${monthList }" />
											</form:select>
										</div>

										<div class="form-group">
											<form:input id="sapid" path="sapid" type="text"
												placeholder="SAP ID" class="form-control"
												value="${student.sapid}" />
										</div>

										<div class="form-group">
											<form:input id="emailId" path="emailId" type="email"
												placeholder="Email ID" class="form-control"
												value="${student.emailId}" />
										</div>
										<div class="form-group">
											<textarea name="sapIdList" cols="50" rows="7"
												class="form-control"
												placeholder="Enter different Student Ids in new lines">${student.sapIdList}</textarea>
										</div>
									</div>
									<div class="col-md-4">
										<div class="form-group">
											<form:select id="program" path="program" type="text"
												placeholder="Program" class="form-control"
												itemValue="${student.program}">
												<form:option value="">Select Program</form:option>
												<form:options items="${programList}" />
											</form:select>
										</div>

										<div class="form-group">
											<form:select id="sem" path="sem" placeholder="Semester"
												class="form-control" value="${student.sem}">
												<form:option value="">Select Semester</form:option>
												<form:option value="1">1</form:option>
												<form:option value="3">3</form:option>
											</form:select>
										</div>
										
										<div class="form-group">
											<form:select id="prgmStructApplicable"
												path="prgmStructApplicable" placeholder="Program Structure"
												class="form-control" itemValue="${student.prgmStructApplicable}">
												<form:option value="">Select Program Structure</form:option>
												<form:options items="${progStructListFromProgramMaster}" />
											</form:select>
										</div>
										
										<div class="form-group">
											<form:select id="programCleared" path="programCleared"
												placeholder="Program Cleared" class="form-control"
												value="${student.programCleared}">
												<form:option value="">Program Cleared</form:option>
												<form:option value="Y">Yes</form:option>
												<form:option value="N">No</form:option>

											</form:select>
										</div>

										<div class="form-group">
											<form:input id="firstName" path="firstName" type="text"
												placeholder="First Name" class="form-control"
												value="${student.firstName}" />
										</div>

										<div class="form-group">
											<form:input id="lastName" path="lastName" type="text"
												placeholder="Last Name" class="form-control"
												value="${student.lastName}" />
										</div>
										
										<div class="form-group">
											<form:select id="programStatus" path="programStatus" placeholder="programStatus"
												class="form-control" value="${student.programStatus}">
												<form:option value="">Select Program Status</form:option>
												<form:option value="Program Active">Program Active</form:option>
												<form:option value="Program Suspension">Program Suspension</form:option>
												<form:option value="Program Terminated">Program Terminated</form:option>
												<form:option value="Program Withdrawal">Program Withdrawal</form:option>
											</form:select>
										</div>
										
										<div class="form-group">
											<div style="display: inline-block;">
												<form:checkbox path="validStudent" style="width: 50px;" />
												Active Students Only
											</div>
										</div>
									</div>
									<div class="col-md-6">

										<div class="form-group">
											<label class="control-label" for="submit"></label>
											<button id="submit" name="submit"
												class="btn btn-large btn-primary"
												formaction="searchStudents">Search</button>
											<button id="reset" type="reset" class="btn btn-danger"
												type="reset">Reset</button>
											<button id="cancel" name="cancel" class="btn btn-danger"
												formaction="${pageContext.request.contextPath}/home" formnovalidate="formnovalidate">Cancel</button>
										</div>
									</div>
								</fieldset>
							</form:form>
						</div>
						<c:choose>
							<c:when test="${rowCount > 0}">

								<h2>
									&nbsp;Student Records <font size="2px"> (${rowCount}
										Records Found)&nbsp; <a href="downloadStudents">Download
											to Excel</a>
									</font>
								</h2>
								<div class="clearfix"></div>
								<div class="panel-content-wrapper">
									<div class="table-responsive">
										<table class="table table-striped table-hover"
											style="font-size: 12px" id="searchStudent">
											<thead>
												<tr>
													<th>Sr. No.</th>
													<th>SAP ID</th>
													<th>First Name</th>
													<th>Last Name</th>
													<th>Program</th>
													<th>Sem</th>
													<th>Email</th>
													<th>Mobile</th>
													<th>Enrollment Year</th>
													<th>Enrollment Month</th>
													<th>Validity End Year</th>
													<th>Validity End Month</th>
													<th>Program Structure</th>
													<th>Program Status</th>
													<th>Is App Downloaded </th>													
													<th>Actions</th>
												</tr>
											</thead>
											<tbody>

												<c:forEach var="student" items="${studentList}" varStatus="status">
													<tr>
														<td><c:out value="${status.count}" /></td>
														<td><c:out value="${student.sapid}" /></td>
														<td><c:out value="${student.firstName}" /></td>
														<td><c:out value="${student.lastName}" /></td>
														<td><c:out value="${student.program}" /></td>
														<td><c:out value="${student.sem}" /></td>
														<td><c:out value="${student.emailId}" /></td>
														<td><c:out value="${student.mobile}" /></td>
														<td><c:out value="${student.enrollmentYear}" /></td>
														<td><c:out value="${student.enrollmentMonth}" /></td>
														<td><c:out value="${student.validityEndYear}" /></td>
														<td><c:out value="${student.validityEndMonth}" /></td>
														<td><c:out value="${student.prgmStructApplicable}" /></td>	
														
														
														<c:choose>  
															<c:when test="${empty student.programStatus}">  
																<td>Active</td>
															</c:when>   
															<c:otherwise>  
																<td><c:out value="${student.programStatus}" /></td>
															</c:otherwise>  
														</c:choose> 
																							
														<td>		
														<c:choose>												
											            <c:when test="${empty student.onesignalId && empty student.firebaseToken}">
														<c:out value="No"/>
													    </c:when>
											            <c:otherwise>
											            	<c:out value="Yes"/>
														</c:otherwise>
														</c:choose>
														</td>
														
														<td><c:url value="editStudent" var="editurl">
																<c:param name="sapid" value="${student.sapid}" />
																<c:param name="sem" value="${student.sem}" />
															</c:url> 
					                             <%if (roles.indexOf("Exam Admin") != -1
 					                                   || roles.indexOf("Assignment Admin") != -1
 					                                   || roles.indexOf("Acads Admin") != -1) {%> 
 					                                   <a href="${editurl}" title="Edit"><i
																class="fa-solid fa-pen-to-square fa-lg"></i></a>&nbsp; 
												 <%}%> 
												            <c:url value="viewStudent/${student.sapid}/${student.sem}" var="detailsUrl"/>
															<a href="${detailsUrl}" title="Details"><i
																class="fa-solid fa-circle-info fa-lg"></i></a>&nbsp;</td>

													</tr>
												</c:forEach>
											</tbody>
										</table>
									</div>
								</div>
								<br>
							</c:when>
						</c:choose>
						<c:url var="firstUrl" value="searchStudentsPage?pageNo=1" />
						<c:url var="lastUrl"
							value="searchStudentsPage?pageNo=${page.totalPages}" />
						<c:url var="prevUrl"
							value="searchStudentsPage?pageNo=${page.currentIndex - 1}" />
						<c:url var="nextUrl"
							value="searchStudentsPage?pageNo=${page.currentIndex + 1}" />


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
											<c:url var="pageUrl" value="searchStudentsPage?pageNo=${i}" />
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
	<jsp:include page="adminCommon/footer.jsp" />


</body>
</html>

