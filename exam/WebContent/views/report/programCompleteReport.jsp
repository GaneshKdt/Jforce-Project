<%-- <!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!-->


<html class="no-js">
<!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<jsp:include page="../jscss.jsp">
	<jsp:param value="Report for Program Completed Students" name="title" />
</jsp:include>

<body class="inside">

	<%@ include file="../header.jsp"%>

	<section class="content-container login">
		<div class="container-fluid customTheme">

			<div class="row"><legend>Report for Program Completed Students</legend></div>
			
				<%@ include file="../messages.jsp"%>

		<div class="panel-body clearfix">
			<form:form  action="/exam/admin/programCompleteReport" method="post" modelAttribute="studentMarks">
			<fieldset>
			<div class="col-md-6 column">

					
					
					<div class="form-group">
						<form:select id="writtenYear" path="year" type="text"	placeholder="Written Year" class="form-control"   itemValue="${studentMarks.year}">
							<form:option value="">Select Written Year</form:option>
							<form:options items="${yearList}" />
						</form:select>
					</div>
					
					<div class="form-group">
						<form:select id="writtenMonth" path="month" type="text" placeholder="Written Month" class="form-control"  itemValue="${studentMarks.month}">
							<form:option value="">Select Written Month</form:option>
							<form:option value="Apr">Apr</form:option>
							<form:option value="Jun">Jun</form:option>
							<form:option value="Sep">Sep</form:option>
							<form:option value="Dec">Dec</form:option>
						</form:select>
					</div>
					
					<div class="form-group">
						<form:select id="examMode" path="examMode" type="text" placeholder="Exam Mode" class="form-control"  itemValue="${studentMarks.examMode}">
							<form:option value="">Select Exam Mode</form:option>
							<form:option value="Online">Online</form:option>
							<form:option value="Offline">Offline</form:option>
						</form:select>
					</div>
					
					<div class="form-group">
					<button id="submit" name="submit" class="btn btn-large btn-primary"
						formaction="/exam/admin/programCompleteReport">Generate</button>
						<button id="cancel" name="cancel" class="btn btn-danger" formaction="home" formnovalidate="formnovalidate">Cancel</button>
					</div>
					
					
					
				
				</div>
				
				</fieldset>
				</form:form>
				
		</div>
		
		<c:choose>
	<c:when test="${rowCount > 0}">

	<h2>&nbsp;Students List 
	<font size="2px">(${rowCount} Records Found) &nbsp; 
		<a href="downloadProgramCompleteReport?year=${studentMarks.year}&month=${studentMarks.month}">Download to Excel</a>&nbsp; &nbsp; &nbsp; 
		<a href="/exam/admin/downloadCertficateCSV?year=${studentMarks.year}&month=${studentMarks.month}">Download Certificate CSV</a>
	</font></h2>
	<div class="table-responsive">
	<table class="panel-body table table-striped" style="font-size:12px">
						<thead>
						<tr>
							<th>Sr. No.</th>
							<th>GR No.</th>
							<th>SAP ID</th>
							<th>Student Name</th>
							<th>Program</th>
						</tr>
					</thead>
						<tbody>
						
						<c:forEach var="studentMarks" items="${studentMarksList}" varStatus="status">
					        <tr>
					            <td><c:out value="${status.count}" /></td>
								<td><c:out value="${studentMarks.grno}" /></td>
								<td><c:out value="${studentMarks.sapid}" /></td>
								<td><c:out value="${studentMarks.name}" /></td>
								<td><c:out value="${studentMarks.program}" /></td>

					        </tr>   
					    </c:forEach>
							
							
						</tbody>
					</table>
	</div>
	<br>

</c:when>
</c:choose>
	</div>
	</section>

	<jsp:include page="../footer.jsp" />


</body>
</html>
 --%>
 <!DOCTYPE html>
<html lang="en">
	
   <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%> 


    <jsp:include page="../adminCommon/jscss.jsp">
	<jsp:param value="Report for Program Completed Students" name="title"/>
    </jsp:include>
    
    
    
    <body>
    
    	<%@ include file="../adminCommon/header.jsp" %>
        <div class="sz-main-content-wrapper">
        
        	<jsp:include page="../adminCommon/breadcrum.jsp">
			<jsp:param value="Exam;Report for Program Completed Students" name="breadcrumItems"/>
			</jsp:include>
        	
            
            <div class="sz-main-content menu-closed">
                    <div class="sz-main-content-inner">
              				<jsp:include page="../adminCommon/left-sidebar.jsp">
								<jsp:param value="" name="activeMenu"/>
							</jsp:include>
              				
              				
              				<div class="sz-content-wrapper examsPage">
              						<%@ include file="../adminCommon/adminInfoBar.jsp" %>
              						<div class="sz-content">
								
											<h2 class="red text-capitalize">Report for Program Completed Students</h2>
											<div class="clearfix"></div>
											<div class="panel-content-wrapper" style="min-height:450px;">
											<%@ include file="../adminCommon/messages.jsp" %>
									<form:form  action="/exam/admin/programCompleteReport" method="post" modelAttribute="studentMarks">
									<fieldset>
									<div class="col-md-4">
										<div class="form-group">
												<form:select id="writtenYear" path="year" type="text"	placeholder="Written Year" class="form-control" itemValue="${studentMarks.year}">
													<form:option value="">Select Written Year</form:option>
													<form:options items="${yearList}" />
												</form:select>
											</div>
											
											<div class="form-group">
												<form:select id="writtenMonth" path="month" type="text" placeholder="Written Month" class="form-control"  itemValue="${studentMarks.month}">
													<form:option value="">Select Written Month</form:option>
													<form:option value="Apr">Apr</form:option>
													<form:option value="Jun">Jun</form:option>
													<form:option value="Sep">Sep</form:option>
													<form:option value="Oct">Oct</form:option>
													<form:option value="Dec">Dec</form:option>
												</form:select>
											</div>
											<%if(!"academic.admin".equalsIgnoreCase((String)session.getAttribute("userId"))){ %>
											<div class="form-group">
												<form:select id="examMode" path="examMode" type="text" placeholder="Exam Mode" class="form-control"  itemValue="${studentMarks.examMode}">
													<form:option value="">Select Exam Mode</form:option>
													<form:option value="Online">Online</form:option>
													<form:option value="Offline">Offline</form:option>
												</form:select>
											</div> <% }%>
											
											<div class="form-group">
											<button id="submit" name="submit" class="btn btn-large btn-primary"
												formaction="/exam/admin/programCompleteReport">Generate</button>
												
												
											<%if(roles.indexOf("Exam Admin") != -1 || roles.indexOf("Assignment Admin") != -1){ %>
											
												<button id="submit" name="submit" class="btn btn-large btn-primary"
												formaction="/exam/admin/makeStudentListAlumni">Mark Program Completion</button>
											
											<% } %>
												
												<!-- <button id="submit" name="submit"
												class="btn btn-large btn-primary"
												formaction="/exam/admin/sendEmailToPCStudents">Send Email
												</button> -->
												<button id="cancel" name="cancel" class="btn btn-danger" formaction="home" formnovalidate="formnovalidate">Cancel</button>
											</div>
											
											
											
										
										</div>
										
										</fieldset>
										</form:form>
											 </div>
								<c:choose>
									<c:when test="${rowCount > 0}">

									<h2>&nbsp;Students List 
									<font size="2px">(${rowCount} Records Found) &nbsp; 
										<a href="/exam/admin/downloadProgramCompleteReport?year=${studentMarks.year}&month=${studentMarks.month}">Download to Excel</a>&nbsp; &nbsp; &nbsp; 
										<a href="/exam/admin/downloadCertficateCSV?year=${studentMarks.year}&month=${studentMarks.month}">Download Certificate CSV</a>
									</font></h2>
									<div class="clearfix"></div>
									<div class="panel-content-wrapper">
									<div class="table-responsive">
									<table class="table table-striped table-hover" style="font-size:12px">
														<thead>
														<tr>
															<th>Sr. No.</th>
															<th>GR No.</th>
															<th>SAP ID</th>
															<th>Student Name</th>
															<th>Program</th>
														</tr>
													</thead>
														<tbody>
														
														<c:forEach var="studentMarks" items="${studentMarksList}" varStatus="status">
															<tr>
																<td><c:out value="${status.count}" /></td>
																<td><c:out value="${studentMarks.grno}" /></td>
																<td><c:out value="${studentMarks.sapid}" /></td>
																<td><c:out value="${studentMarks.name}" /></td>
																<td><c:out value="${studentMarks.program}" /></td>

															</tr>   
														</c:forEach>
														</tbody>
													</table>
												</div>
												</div>
												<br>
										</c:when>
										</c:choose>
              						</div>
              				   </div>
    				       </div>
			           </div>
		           </div>
        <jsp:include page="../adminCommon/footer.jsp"/>
    </body>
</html>