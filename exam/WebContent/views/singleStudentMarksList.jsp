<%-- <!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!-->

<%@page import="com.nmims.beans.Person"%>
<%@page import="com.nmims.beans.Page"%>
<%@page import="com.nmims.beans.StudentMarksBean"%>
<html class="no-js">
<!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<jsp:include page="jscss.jsp">
	<jsp:param value="View Marks History" name="title" />
</jsp:include>

<body class="inside">

	<%@ include file="header.jsp"%>

	<section class="content-container">
		<div class="container-fluid customTheme">

			<div class="row clearfix ">
			<legend>Marks History <font size="2px">(${size} Records Found)</font></legend>
			</div>
			<div class="panel-body small-padding">
				<div class="col-sm-6">
					<h2>Pass Fail Status</h2>
					<p>View Pass/Fail Status of all subjects.</p>
					<a class="btn btn-primary " href="getMostRecentPassFailResults">Pass Fail Status</a>
				</div>
				
				<div class="col-sm-6">
				
					<c:choose>
						<c:when test="${size > 0}">

							<div class="row">

							<div class="titleContainer titleContainerResultIns">
								<p>Student Name</p>
								<h3>${studentMarksList[0].studentname}</h3>
							</div>

							<div class="titleContainer titleContainerResultIns">
								<p>Student Number</p>
								<h3>${studentMarksList[0].sapid}</h3>
							</div>

							<div class="titleContainer titleContainerResultIns">
								<p>Program</p>
								<h3>${studentMarksList[0].program}</h3>
							</div>

							</div>

						</c:when>
					</c:choose>
				</div>
				<div class="col-sm-12">
				<h2>Search Marks</h2>
				<form:form action="searchSingleStudentMarks" method="post" modelAttribute="studentMarks">
					<fieldset>
						<div class="col-sm-6">
							<div class="form-group">
								<form:select id="year" path="year" type="text"
									placeholder="Year" class="form-control"
									itemValue="${studentMarks.year}">
									<form:option value="">Select Year</form:option>
									<form:options items="${yearList}" />
								</form:select>
							</div>

							<div class="form-group">
								<form:select id="month" path="month" type="text"
									placeholder="Month" class="form-control"
									itemValue="${studentMarks.month}">
									<form:option value="">Select Month</form:option>
									<form:option value="Apr">Apr</form:option>
									<form:option value="Jun">Jun</form:option>
									<form:option value="Sep">Sep</form:option>
									<form:option value="Dec">Dec</form:option>
								</form:select>
							</div>
						</div>
						<div class="col-sm-6">

							<div class="form-group">
								<form:select id="subject" path="subject" type="text" placeholder="Subject" class="form-control" itemValue="${studentMarks.subject}">
									<form:option value="">Select Subject</form:option>
									<form:options items="${subjectList}" />
								</form:select>
							</div>

							<div class="form-group">
								<form:select id="sem" path="sem" placeholder="Semester" class="form-control" value="${studentMarks.sem}">
									<form:option value="">Select Semester</form:option>
									<form:option value="1">1</form:option>
									<form:option value="2">2</form:option>
									<form:option value="3">3</form:option>
									<form:option value="4">4</form:option>
								</form:select>
							</div>
						</div>
						<div class="col-sm-6">
							<button id="submit" name="submit" class="btn btn-large btn-primary"	formaction="searchSingleStudentMarks">Search</button>
							<button id="cancel" name="cancel" class="btn btn-danger" formaction="home" formnovalidate="formnovalidate">Cancel</button>
						</div>
					</fieldset>
				</form:form>
				</div>
				
			</div>

			<c:choose>
			<c:when test="${size > 0}">
			<div class="table-responsive panel-body">
			<table class="table table-striped table-hover" style="font-size: 12px">
				<thead>
					<tr>
						<th>Sr. No.</th>
						<th>Exam Year</th>
						<th>Exam Month</th>
						<th>Sem</th>
						<th style="text-align:left;">Subject</th>
						<th>Written</th>
						<th>Assign.</th>
						<th>Grace</th>
					</tr>
				</thead>
				<tbody>

					<c:forEach var="studentMarks" items="${studentMarksList}"
						varStatus="status">
						<tr>
							<td><c:out value="${status.count}" /></td>
							<td><c:out value="${studentMarks.year}" /></td>
							<td><c:out value="${studentMarks.month}" /></td>
							<td><c:out value="${studentMarks.sem}" /></td>
							<td nowrap="nowrap" style="text-align:left;"><c:out value="${studentMarks.subject}" /></td>
							<td><c:out value="${studentMarks.writenscore}" /></td>
							<td><c:out value="${studentMarks.assignmentscore}" /></td>
							<td><c:out value="${studentMarks.gracemarks}" /></td>
						</tr>
					</c:forEach>


				</tbody>
			</table>
			</div>
			</c:when>
			</c:choose>
			
		</div>
	</section>




	<jsp:include page="footer.jsp" />

</body>
</html>
 --%>
 
 
 <!DOCTYPE html>
<%@page import="com.nmims.beans.StudentMarksBean"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<html lang="en">
    
    
    <jsp:include page="common/jscss.jsp">
	<jsp:param value="Marks History" name="title"/>
    </jsp:include>
    
    
    
    <body>
    
    	<%@ include file="common/header.jsp" %>
    	
    	
        
        <div class="sz-main-content-wrapper">
        
        	<jsp:include page="common/breadcrum.jsp">
			<jsp:param value="Student Zone;Exams;Mark History" name="breadcrumItems"/>
			</jsp:include>
        	
            
            <div class="sz-main-content menu-closed">
                    <div class="sz-main-content-inner">
              				<jsp:include page="common/left-sidebar.jsp">
								<jsp:param value="Marks History" name="activeMenu"/>
							</jsp:include>
              				
              				
              				<div class="sz-content-wrapper examsPage">
              						<%@ include file="common/studentInfoBar.jsp" %>
              						
              						
              						<div class="sz-content">
              								<h2 class="red text-capitalize">Marks History (${size} Records Found)</h2>
											<div class="clearfix"></div>
											
											<div class="panel-content-wrapper">
													<div class="row">
															<div class="col-md-8">
																	<form:form action="searchSingleStudentMarks" method="post" modelAttribute="studentMarks">
																	<fieldset>
																		<div class="col-md-6">
																			<div class="form-group">
																				<form:select id="year" path="year" type="text"
																					placeholder="Year" class="form-control"
																					itemValue="${studentMarks.year}">
																					<form:option value="">Select Year</form:option>
																					<form:options items="${yearList}" />
																				</form:select>
																			</div>
												
																			<div class="form-group">
																				<form:select id="month" path="month" type="text"
																					placeholder="Month" class="form-control"
																					itemValue="${studentMarks.month}">
																					<form:option value="">Select Month</form:option>
																					<form:option value="Apr">Apr</form:option>
																					<form:option value="Jun">Jun</form:option>
																					<form:option value="Sep">Sep</form:option>
																					<form:option value="Dec">Dec</form:option>
																				</form:select>
																			</div>
																		</div>
																		<div class="col-md-6">
												
																			<div class="form-group">
																				<form:select id="subject" path="subject" type="text" placeholder="Subject" class="form-control" itemValue="${studentMarks.subject}">
																					<form:option value="">Select Subject</form:option>
																					<form:options items="${subjectList}" />
																				</form:select>
																			</div>
												
																			<div class="form-group">
																				<form:select id="sem" path="sem" placeholder="Semester" class="form-control" value="${studentMarks.sem}">
																					<form:option value="">Select Semester</form:option>
																					<form:option value="1">1</form:option>
																					<form:option value="2">2</form:option>
																					<form:option value="3">3</form:option>
																					<form:option value="4">4</form:option>
																				</form:select>
																			</div>
																		</div>
																		<div class="col-md-6">
																			<button id="submit" name="submit" class="btn btn-large btn-primary"	formaction="searchSingleStudentMarks">Search</button>
																			<button id="cancel" name="cancel" class="btn btn-danger" formaction="/studentportal/home" formnovalidate="formnovalidate">Cancel</button>
																		</div>
																	</fieldset>
																</form:form>
															</div>
															
															<div class="col-md-4">
																	<!-- <a class="btn btn-primary " href="getMostRecentPassFailResults">View Pass Fail Status</a> -->
															</div>
													</div>
											</div>
											
											
											<c:if test="${size > 0}">
											<h2 class="red text-capitalize">${size} <span>Records in Marks History</h2>
											<div class="clearfix"></div>
											
											<div class="panel-content-wrapper">
													<div class="table-responsive">
														<table class="table table-striped table-hover">
															<thead>
																<tr>
																	<th>Sr. No.</th>
																	<th>Exam Year</th>
																	<th>Exam Month</th>
																	<th>Sem</th>
																	<th style="text-align:left;">Subject</th>
																	<th>Written</th>
																	<th>Assign.</th>
																	<th>Grace</th>
																</tr>
															</thead>
															<tbody>
											
																<c:forEach var="studentMarks" items="${studentMarksList}"
																	varStatus="status">
																	<tr>
																		<td><c:out value="${status.count}" /></td>
																		<td><c:out value="${studentMarks.year}" /></td>
																		<td><c:out value="${studentMarks.month}" /></td>
																		<td><c:out value="${studentMarks.sem}" /></td>
																		<td nowrap="nowrap" style="text-align:left;"><c:out value="${studentMarks.subject}" /></td>
																		<td><c:out value="${studentMarks.writenscore}" /></td>
																		<td><c:out value="${studentMarks.assignmentscore}" /></td>
																		<td><c:out value="${studentMarks.gracemarks}" /></td>
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
            </div>
        </div>
            
  	
        <jsp:include page="common/footer.jsp"/>
            
		
    </body>
</html>