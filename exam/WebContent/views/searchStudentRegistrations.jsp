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
<jsp:param value="Search Students Registrations" name="title" />
</jsp:include>

<body class="inside">

<%@ include file="header.jsp"%>
	
    <section class="content-container login">
        <div class="container-fluid customTheme">
        <div class="row"><legend>Search Students Registrations</legend></div>
        <%@ include file="messages.jsp"%>
		<div class="row clearfix">
		<form:form  action="searchStudentRegistraions" method="post" modelAttribute="student">
			<fieldset>
			<div class="col-md-6 column">

					<div class="form-group">
						<form:select id="year" path="year" type="text"	placeholder="Year" class="form-control"   itemValue="${student.year}">
							<form:option value="">Select Session Year</form:option>
							<form:options items="${yearList}" />
						</form:select>
					</div>
				
					<div class="form-group">
						<form:select id="month" path="month" type="text" placeholder="Month" class="form-control"  itemValue="${student.month}">
							<form:option value="">Select Session Month</form:option>
							<form:option value="Jan">Jan</form:option>
							<form:option value="Jul">Jul</form:option>
						</form:select>
					</div>
					
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
							<form:option value="2">2</form:option>
							<form:option value="3">3</form:option>
							<form:option value="4">4</form:option>
						</form:select>
					</div>
							
					<div class="form-group">
							<form:input id="sapid" path="sapid" type="text" placeholder="SAP ID" class="form-control" value="${student.sapid}"/>
					</div>
					
					<div class="form-group">
						<label class="control-label" for="submit"></label>
						<button id="submit" name="submit" class="btn btn-large btn-primary" formaction="searchStudentRegistraions">Search</button>
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

	<legend>&nbsp;Student Records <font size="2px">(${rowCount} Records Found)&nbsp; <a href="downloadStudentRegistrations">Download to Excel</a></font></legend>
	<div class="table-responsive">
	<table class="table table-striped table-hover" style="font-size:12px">
						<thead>
							<tr> 
								<th>Sr. No.</th>
								<th>Session Year</th>
								<th>Session Month</th>
								<th>Student ID</th>
								<th>Program</th>
								<th>Sem</th>
								<th>Actions</th>
							</tr>
						</thead>
						<tbody>
						
						<c:forEach var="student" items="${studentList}" varStatus="status">
					        <tr>
					            <td><c:out value="${status.count}"/></td>
					            <td><c:out value="${student.year}"/></td>
								<td><c:out value="${student.month}"/></td>
					            <td><c:out value="${student.sapid}"/></td>
								<td><c:out value="${student.program}"/></td>
								<td><c:out value="${student.sem}"/></td>
								<td> 
						            <c:url value="editStudentRegistration" var="editurl">
									  <c:param name="sapid" value="${student.sapid}" />
									  <c:param name="sem" value="${student.sem}" />
									</c:url>
									
									<c:url value="deleteStudentRegistration" var="deleteurl">
									  <c:param name="sapid" value="${student.sapid}" />
									  <c:param name="sem" value="${student.sem}" />
									  <c:param name="pageNo" value="1" />
									</c:url>

									<%if(roles.indexOf("Exam Admin") != -1 || roles.indexOf("Assignment Admin") != -1 || roles.indexOf("Acads Admin") != -1){ %>
									<a href="${editurl}" title="Edit"><i class="fa fa-pencil-square-o fa-lg"></i></a>
									<a href="${deleteurl}" title="Delete" onclick="return confirm('Are you sure you want to delete this record?')"><i class="fa fa-trash-o fa-lg"></i></a>
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

<c:url var="firstUrl" value="searchStudentRegistraionsPage?pageNo=1" />
<c:url var="lastUrl" value="searchStudentRegistraionsPage?pageNo=${page.totalPages}" />
<c:url var="prevUrl" value="searchStudentRegistraionsPage?pageNo=${page.currentIndex - 1}" />
<c:url var="nextUrl" value="searchStudentRegistraionsPage?pageNo=${page.currentIndex + 1}" />


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
            <c:url var="pageUrl" value="searchStudentRegistraionsPage?pageNo=${i}" />
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
<%@page import="com.nmims.beans.StudentMarksBean"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
    <jsp:include page="adminCommon/jscss.jsp">
	<jsp:param value="Search Students Registrations" name="title"/>
    </jsp:include>
    
    
    
    <body>
    
    	<%@ include file="adminCommon/header.jsp" %>
        <div class="sz-main-content-wrapper">
        
        	<jsp:include page="adminCommon/breadcrum.jsp">
			<jsp:param value="Exam;Search Students Registrations" name="breadcrumItems"/>
			</jsp:include>
        	
            
            <div class="sz-main-content menu-closed">
                    <div class="sz-main-content-inner">
              				<jsp:include page="adminCommon/left-sidebar.jsp">
								<jsp:param value="" name="activeMenu"/>
							</jsp:include>
              				
              				
              				<div class="sz-content-wrapper examsPage">
              						<%@ include file="adminCommon/adminInfoBar.jsp" %>
              						<div class="sz-content">
								
											<h2 class="red text-capitalize">Search Students Registrations</h2>
											<div class="clearfix"></div>
							<div class="panel-content-wrapper" style="min-height:450px;">
								<%@ include file="adminCommon/messages.jsp" %>
												<form:form  action="searchStudentRegistraions" method="post" modelAttribute="student">
															<fieldset>
															<div class="col-md-4">

																	<div class="form-group">
																		<form:select id="year" path="year" type="text"	placeholder="Year" class="form-control"   itemValue="${student.year}">
																			<form:option value="">Select Session Year</form:option>
																			<form:options items="${yearList}" />
																			<%--Commented by Steffi <form:option value="2017">2017</form:option> --%>
																		</form:select>
																	</div>
																
																	<div class="form-group">
																		<form:select id="month" path="month" type="text" placeholder="Month" class="form-control"  itemValue="${student.month}">
																			<form:option value="">Select Session Month</form:option>
																			<form:option value="Jan">Jan</form:option>
																			<form:option value="Jul">Jul</form:option>
																		</form:select>
																	</div>
																	
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
																			<form:option value="2">2</form:option>
																			<form:option value="3">3</form:option>
																			<form:option value="4">4</form:option>
																		</form:select>
																	</div>
																			
																	<div class="form-group">
																			<form:input id="sapid" path="sapid" type="text" placeholder="SAP ID" class="form-control" value="${student.sapid}"/>
																	</div>
																	
																	<div class="form-group">
																		<label class="control-label" for="submit"></label>
																		<button id="submit" name="submit" class="btn btn-large btn-primary" formaction="searchStudentRegistraions">Search</button>
																		<button id="reset" type="reset" class="btn btn-danger">Reset</button>
																		<button id="cancel" name="cancel" class="btn btn-danger" formaction="${pageContext.request.contextPath}/home" formnovalidate="formnovalidate">Cancel</button>
																	</div>
															</div>
															</fieldset>
													</form:form>
							</div>
								<c:choose>
		<c:when test="${rowCount > 0}">

	<h2 style="margin-left:50px;">&nbsp;&nbsp;Student Records<font size="2px"> (${rowCount} Records Found)&nbsp; <a href="downloadStudentRegistrations">Download to Excel</a></font></h2>
	<div class="clearfix"></div>
		<div class="panel-content-wrapper">
		<div class="table-responsive">
	<table class="table table-striped table-hover" style="font-size:12px">
						<thead>
							<tr> 
								<th>Sr. No.</th>
								<th>Session Year</th>
								<th>Session Month</th>
								<th>Student ID</th>
								<th>Program</th>
								<th>Sem</th>
								<th>Actions</th>
							</tr>
						</thead>
						<tbody>
						
						<c:forEach var="student" items="${studentList}" varStatus="status">
					        <tr>
					            <td><c:out value="${status.count}"/></td>
					            <td><c:out value="${student.year}"/></td>
								<td><c:out value="${student.month}"/></td>
					            <td><c:out value="${student.sapid}"/></td>
								<td><c:out value="${student.program}"/></td>
								<td><c:out value="${student.sem}"/></td>
								<td> 
						            <c:url value="editStudentRegistration" var="editurl">
									  <c:param name="sapid" value="${student.sapid}" />
									  <c:param name="sem" value="${student.sem}" />
									</c:url>
									
									<c:url value="deleteStudentRegistration" var="deleteurl">
									  <c:param name="sapid" value="${student.sapid}" />
									  <c:param name="sem" value="${student.sem}" />
									  <c:param name="pageNo" value="1" />
									</c:url>

									<%if(roles.indexOf("Exam Admin") != -1 || roles.indexOf("Assignment Admin") != -1 || roles.indexOf("Acads Admin") != -1){ %>
									<a href="${editurl}" title="Edit"><i class="fa-solid fa-pen-to-square fa-lg"></i></a>
									<a href="${deleteurl}" title="Delete" onclick="return confirm('Are you sure you want to delete this record?')"><i class="fa-regular fa-trash-can fa-lg"></i></a>
									<%} %>

					            </td>
								
					        </tr>   
					    </c:forEach>
						</tbody>
					</table>
				</div>
				</div>
				<br>

							</c:when>
					</c:choose>
							<c:url var="firstUrl" value="searchStudentRegistraionsPage?pageNo=1" />
							<c:url var="lastUrl" value="searchStudentRegistraionsPage?pageNo=${page.totalPages}" />
							<c:url var="prevUrl" value="searchStudentRegistraionsPage?pageNo=${page.currentIndex - 1}" />
							<c:url var="nextUrl" value="searchStudentRegistraionsPage?pageNo=${page.currentIndex + 1}" />


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
										<c:url var="pageUrl" value="searchStudentRegistraionsPage?pageNo=${i}" />
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

							</div>
              			</div>
    				</div>
			   </div>
		    </div>
        <jsp:include page="adminCommon/footer.jsp"/>
        
		
    </body>
</html>