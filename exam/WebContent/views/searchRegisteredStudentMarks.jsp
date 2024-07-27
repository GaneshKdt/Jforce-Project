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
<jsp:param value="Search Registered Student Marks" name="title" />
</jsp:include>

<script type="text/javascript">
	function showExamCenters(){
		mode = document.getElementById('mode').value;
		if(mode == 'Offline'){
			document.getElementById('offlineCenters').style.display = '';
			document.getElementById('onlineCenters').style.display = 'none';
		}else if(mode == 'Online'){
			document.getElementById('onlineCenters').style.display = '';
			document.getElementById('offlineCenters').style.display = 'none';
		}
	}

	function validate(){
		mode = document.getElementById('mode').value;
		if(mode == 'Offline'){
			offlineCenter = document.getElementById('offlineCenters').value;
			if(offlineCenter == ''){
				alert('Please select Exam Center');
				return false;
			}
		}else if(mode == 'Online'){
			offlineCenter = document.getElementById('onlineCenters').value;
			if(offlineCenter == ''){
				alert('Please select Exam Center');
				return false;
			}
		}
		
		return true;
	}
</script>

<body class="inside">

<%@ include file="header.jsp"%>
	
    <section class="content-container login">
        <div class="container-fluid customTheme">
        <div class="row"><legend>Search Registered Student Marks</legend></div>
        <%@ include file="messages.jsp"%>
		<div class="row clearfix">
		<form:form  action="searchRegisteredStudentMarks" method="post" modelAttribute="studentMarks">
			<fieldset>
			<div class="col-md-6 column">
			<!--   -->
		

				<!-- Form Name -->
				

				<!-- Text input-->
				
					<div class="form-group">
						<form:select id="year" path="year" type="text"	placeholder="Year" class="form-control"   itemValue="${studentMarks.year}">
							<form:option value="">Select Year</form:option>
							<form:options items="${yearList}" />
						</form:select>
					</div>
				
					<div class="form-group">
						<form:select id="month" path="month" type="text" placeholder="Month" class="form-control"  itemValue="${studentMarks.month}">
							<form:option value="">Select Month</form:option>
							<form:option value="Apr">Apr</form:option>
							<form:option value="Jun">Jun</form:option>
							<form:option value="Sep">Sep</form:option>
							<form:option value="Dec">Dec</form:option>
						</form:select>
					</div>
					
					<div class="form-group">
							<form:input id="grno" path="grno" type="text" placeholder="GR No." class="form-control" value="${studentMarks.grno}"/>
					</div>
					
					<div class="form-group">
							<form:input id="sapid" path="sapid" type="text" placeholder="SAP ID" class="form-control" value="${studentMarks.sapid}"/>
					</div>
					
					
					<div class="form-group">
						<form:select id="mode" path="examMode" type="text" required="required" class="form-control"  itemValue="${studentMarks.examMode}" 
						onchange="showExamCenters()">
							<form:option value="">Select Exam Mode</form:option>
							<form:option value="Online">Online</form:option>
							<form:option value="Offline">Offline</form:option>
						</form:select>
					</div>
					
					<div class="form-group">
						<form:select id="offlineCenters" path="offlineCenterId" type="text" 	placeholder="Center" class="form-control"   itemValue="${studentMarks.offlineCenterId}">
							<form:option value="">Select Offline Center</form:option>
							<form:options items="${offlineCentersList}" />
						</form:select>
					</div>
					
					<div class="form-group">
						<form:select id="onlineCenters" path="onlineCenterId" type="text" 	placeholder="Center" class="form-control"   itemValue="${studentMarks.onlineCenterId}">
							<form:option value="">Select Online Center</form:option>
							<form:options items="${onlineCentersList}" />
						</form:select>
					</div>
					
					

				
				
			

</div>



<div class="col-md-6 column">

<div class="form-group">
							<form:input id="studentname" path="studentname" type="text" placeholder="Student Name" class="form-control" value="${studentMarks.studentname}"/>
					</div>
					
					<div class="form-group">
						<form:select id="program" path="program" type="text"	placeholder="Program" class="form-control"  itemValue="${studentMarks.program}">
							<form:option value="">Select Program</form:option>
							<form:options items="${programList}" />
						</form:select>
					</div>
					
					<div class="form-group">
						<form:select id="sem" path="sem" placeholder="Semester" class="form-control"  value="${studentMarks.sem}">
							<form:option value="">Select Semester</form:option>
							<form:option value="1">1</form:option>
							<form:option value="2">2</form:option>
							<form:option value="3">3</form:option>
							<form:option value="4">4</form:option>
						</form:select>
					</div>
					
					<div class="form-group" style="overflow:visible;">
							<form:select id="subject" path="subject" class="combobox form-control"   itemValue="${studentMarks.subject}">
								<form:option value="">Select Subject</form:option>
								<form:options items="${subjectList}" />
							</form:select>
					</div>
					
					<div class="form-group">
					<label class="control-label" for="submit"></label>
					<div class="controls">
						<button id="submit" name="submit" class="btn btn-large btn-primary" formaction="searchRegisteredStudentMarks">Search</button>
						<button id="reset" type="reset" class="btn btn-danger" type="reset">Reset</button>
						<button id="cancel" name="cancel" class="btn btn-danger" formaction="getAllStudentMarks" formnovalidate="formnovalidate">Cancel</button>
					</div>
					</div>
					
					
					
					

				</div>
				
				</fieldset>
				</form:form>
		
		</div>
	</div>
	
	<c:choose>
<c:when test="${rowCount > 0}">

	<legend>&nbsp;Student Marks <font size="2px">(${rowCount} Records Found)&nbsp; <a href="downloadRegisteredStudentMarksResults">Download to Excel</a></font></legend>
	<div class="table-responsive">
	<table class="table table-striped table-hover" style="font-size:12px">
						<thead>
							<tr> 
								<th>Sr. No.</th>
								<th>Exam Year</th>
								<th>Exam Month</th>
								<th>Exam Center</th>
								<th>Exam Mode</th>
								<th>SAP ID</th>
								<th>Last Name</th>
								<th>First Name</th>
								<th>Program</th>
								<th>Sem</th>
								<th>Subject</th>
								<th>Written</th>
								<th>Assign.</th>
								<th>Grace</th>
								<!--<th>Total</th>
								<th>Attempt</th>
								  <th>Source</th>
								<th>Location</th>
								<th>Center Code</th>
								<th>Remarks</th> -->
								<th>Actions</th>
							
							</tr>
						</thead>
						<tbody>
						
						<c:forEach var="studentMarks" items="${studentMarksList}" varStatus="status">
					        <tr>
					            <td><c:out value="${status.count}"/></td>
								<td><c:out value="${studentMarks.year}"/></td>
								<td><c:out value="${studentMarks.month}"/></td>
								<td><c:out value="${studentMarks.examCenterName}"/></td>
								<td><c:out value="${studentMarks.examMode}"/></td>
								<td><c:out value="${studentMarks.sapid}"/></td>
								<td nowrap="nowrap"><c:out value="${studentMarks.lastName}"/></td>
								<td nowrap="nowrap"><c:out value="${studentMarks.firstName}"/></td>
								<td><c:out value="${studentMarks.program}"/></td>
								<td><c:out value="${studentMarks.sem}"/></td>
								<td nowrap="nowrap"><c:out value="${studentMarks.subject}"/></td>
								<td><c:out value="${studentMarks.writenscore}"/></td>
								<td><c:out value="${studentMarks.assignmentscore}"/></td>
								<td><c:out value="${studentMarks.gracemarks}"/></td>
								<!--<td><c:out value="${studentMarks.total}"/></td>
								<td><c:out value="${studentMarks.attempt}"/></td>
								 <td><c:out value="${studentMarks.source}"/></td>
								<td><c:out value="${studentMarks.location}"/></td>
								<td><c:out value="${studentMarks.centercode}"/></td>
								<td><c:out value="${studentMarks.remarks}"/></td>  -->
   
					            <td> 
						            <c:url value="editStudentMarks" var="editurl">
									  <c:param name="id" value="${studentMarks.id}" />
									</c:url>
									<c:url value="deleteStudentMarks" var="deleteurl">
									  <c:param name="id" value="${studentMarks.id}" />
									</c:url>
									<c:url value="viewStudentMarksDetails" var="studentMarksDetailsUrl">
									  <c:param name="id" value="${studentMarks.id}" />
									</c:url>
									
									<a href="${studentMarksDetailsUrl}" title="Details"><i class="fa fa-info-circle fa-lg"></i></a>
									<%if(roles.indexOf("Exam Admin") != -1 || roles.indexOf("TEE Admin") != -1){ %>
									&nbsp;
									<a href="${editurl}" title="Edit"><i class="fa fa-pencil-square-o fa-lg"></i></a>
									<%} %>
									&nbsp;									
									<%if(roles.indexOf("Exam Admin") != -1 ){ %>
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

<c:url var="firstUrl" value="searchRegisteredStudentMarksPage?pageNo=1" />
<c:url var="lastUrl" value="searchRegisteredStudentMarksPage?pageNo=${page.totalPages}" />
<c:url var="prevUrl" value="searchRegisteredStudentMarksPage?pageNo=${page.currentIndex - 1}" />
<c:url var="nextUrl" value="searchRegisteredStudentMarksPage?pageNo=${page.currentIndex + 1}" />


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
            <c:url var="pageUrl" value="searchRegisteredStudentMarksPage?pageNo=${i}" />
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

	<script type="text/javascript">
		document.getElementById('offlineCenters').style.display = 'none';
		document.getElementById('onlineCenters').style.display = 'none';
		
		mode = document.getElementById('mode').value;
		if(mode == 'Offline'){
			document.getElementById('offlineCenters').style.display = '';
			document.getElementById('onlineCenters').style.display = 'none';
		}else if(mode == 'Online'){
			document.getElementById('onlineCenters').style.display = '';
			document.getElementById('offlineCenters').style.display = 'none';
		}
		
	</script>

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
	<jsp:param value="Search Registered Student Marks" name="title"/>
    </jsp:include>
    
    
    
    <body>
    
    	<%@ include file="adminCommon/header.jsp" %>
        <div class="sz-main-content-wrapper">
        
        	<jsp:include page="adminCommon/breadcrum.jsp">
			<jsp:param value="Exam;Search Student Marks" name="breadcrumItems"/>
			</jsp:include>
        	
            
            <div class="sz-main-content menu-closed">
                    <div class="sz-main-content-inner">
              				<jsp:include page="adminCommon/left-sidebar.jsp">
								<jsp:param value="" name="activeMenu"/>
							</jsp:include>
              				
              				
              				<div class="sz-content-wrapper examsPage">
              						<%@ include file="adminCommon/adminInfoBar.jsp" %>
              						<div class="sz-content">
								
											<h2 class="red text-capitalize">Search Registered Student Marks</h2>
											<div class="clearfix"></div>
													<div class="panel-content-wrapper" style="min-height:450px;">
											<%@ include file="adminCommon/messages.jsp" %>
																		<form:form  action="searchRegisteredStudentMarks" method="post" modelAttribute="studentMarks">
									<fieldset>
									<div class="col-md-4">
										<div class="form-group">
												<form:select id="year" path="year" type="text"	placeholder="Year" class="form-control"   itemValue="${studentMarks.year}">
													<form:option value="">Select Year</form:option>
													<form:options items="${yearList}" />
												</form:select>
											</div>
										
											<div class="form-group">
												<form:select id="month" path="month" type="text" placeholder="Month" class="form-control"  itemValue="${studentMarks.month}">
													<form:option value="">Select Month</form:option>
													<form:option value="Apr">Apr</form:option>
													<form:option value="May">May</form:option>
													<form:option value="Jun">Jun</form:option>
													<form:option value="Jul">Jul</form:option>
													<form:option value="Aug">Aug</form:option>
													<form:option value="Sep">Sep</form:option>
													<form:option value="Oct">Oct</form:option>
													<form:option value="Dec">Dec</form:option>
												</form:select>
											</div>
											
											<div class="form-group">
													<form:input id="grno" path="grno" type="text" placeholder="GR No." class="form-control" value="${studentMarks.grno}"/>
											</div>
											
											<div class="form-group">
													<form:input id="sapid" path="sapid" type="text" placeholder="SAP ID" class="form-control" value="${studentMarks.sapid}"/>
											</div>
											
											<div class="form-group">
												<form:select id="mode" path="examMode" type="text" required="required" class="form-control"  itemValue="${studentMarks.examMode}" 
												onchange="showExamCenters()">
													<form:option value="">Select Exam Mode</form:option>
													<form:option value="Online">Online</form:option>
													<form:option value="Offline">Offline</form:option>
												</form:select>
											</div>
											
											<div class="form-group">
												<form:select id="offlineCenters" path="offlineCenterId" type="text" 	placeholder="Center" class="form-control"   itemValue="${studentMarks.offlineCenterId}">
													<form:option value="">Select Offline Center</form:option>
													<form:options items="${offlineCentersList}" />
												</form:select>
											</div>
											
											<div class="form-group">
												<form:select id="onlineCenters" path="onlineCenterId" type="text" 	placeholder="Center" class="form-control"   itemValue="${studentMarks.onlineCenterId}">
													<form:option value="">Select Online Center</form:option>
													<form:options items="${onlineCentersList}" />
												</form:select>
											</div>
											</div>
												<div class="col-md-4">

												<div class="form-group">
													<form:input id="studentname" path="studentname" type="text" placeholder="Student Name" class="form-control" value="${studentMarks.studentname}"/>
													</div>
										    <div class="form-group">
												<form:select id="program" path="program" type="text"	placeholder="Program" class="form-control"  itemValue="${studentMarks.program}">
													<form:option value="">Select Program</form:option>
													<form:options items="${programList}" />
												</form:select>
											</div>
											<div class="form-group">
												<form:select id="sem" path="sem" placeholder="Semester" class="form-control"  value="${studentMarks.sem}">
													<form:option value="">Select Semester</form:option>
													<form:option value="1">1</form:option>
													<form:option value="2">2</form:option>
													<form:option value="3">3</form:option>
													<form:option value="4">4</form:option>
												</form:select>
											</div>
											
											<div class="form-group" style="overflow:visible;">
													<form:select id="subject" path="subject" class="combobox form-control"   itemValue="${studentMarks.subject}">
														<form:option value="">Select Subject</form:option>
														<form:options items="${subjectList}" />
													</form:select>
											</div>
											
											<div class="form-group">
											<label class="control-label" for="submit"></label>
											<div class="controls">
												<button id="submit" name="submit" class="btn btn-large btn-primary" formaction="searchRegisteredStudentMarks">Search</button>
												<button id="reset" type="reset" class="btn btn-danger" type="reset">Reset</button>
												<button id="cancel" name="cancel" class="btn btn-danger" formaction="getAllStudentMarks" formnovalidate="formnovalidate">Cancel</button>
											</div>
											</div>
											</div>
										
										</fieldset>
								</form:form>
							</div>
								<c:choose>
								<c:when test="${rowCount > 0}">

									<h2>&nbsp;Student Marks<font size="2px"> (${rowCount} Records Found)&nbsp; <a href="downloadRegisteredStudentMarksResults">Download to Excel</a></font></h2>
									<div class="clearfix"></div>
										<div class="panel-content-wrapper">
										<div class="table-responsive">
									<table class="table table-striped table-hover" style="font-size:12px">
														<thead>
															<tr> 
																<th>Sr. No.</th>
																<th>Exam Year</th>
																<th>Exam Month</th>
																<th>Exam Center</th>
																<th>Exam Mode</th>
																<th>SAP ID</th>
																<th>Last Name</th>
																<th>First Name</th>
																<th>Program</th>
																<th>Sem</th>
																<th>Subject</th>
																<th>Written</th>
																<th>Assign.</th>
																<th>Grace</th>
																<!--<th>Total</th>
																<th>Attempt</th>
																  <th>Source</th>
																<th>Location</th>
																<th>Center Code</th>
																<th>Remarks</th> -->
																<th>Actions</th>
															
															</tr>
														</thead>
														<tbody>
														
														<c:forEach var="studentMarks" items="${studentMarksList}" varStatus="status">
															<tr>
																<td><c:out value="${status.count}"/></td>
																<td><c:out value="${studentMarks.year}"/></td>
																<td><c:out value="${studentMarks.month}"/></td>
																<td><c:out value="${studentMarks.examCenterName}"/></td>
																<td><c:out value="${studentMarks.examMode}"/></td>
																<td><c:out value="${studentMarks.sapid}"/></td>
																<td nowrap="nowrap"><c:out value="${studentMarks.lastName}"/></td>
																<td nowrap="nowrap"><c:out value="${studentMarks.firstName}"/></td>
																<td><c:out value="${studentMarks.program}"/></td>
																<td><c:out value="${studentMarks.sem}"/></td>
																<td nowrap="nowrap"><c:out value="${studentMarks.subject}"/></td>
																<td><c:out value="${studentMarks.writenscore}"/></td>
																<td><c:out value="${studentMarks.assignmentscore}"/></td>
																<td><c:out value="${studentMarks.gracemarks}"/></td>
																<!--<td><c:out value="${studentMarks.total}"/></td>
																<td><c:out value="${studentMarks.attempt}"/></td>
																 <td><c:out value="${studentMarks.source}"/></td>
																<td><c:out value="${studentMarks.location}"/></td>
																<td><c:out value="${studentMarks.centercode}"/></td>
																<td><c:out value="${studentMarks.remarks}"/></td>  -->
								   
																<td> 
																	<c:url value="editStudentMarks" var="editurl">
																	  <c:param name="id" value="${studentMarks.id}" />
																	</c:url>
																	<c:url value="deleteStudentMarks" var="deleteurl">
																	  <c:param name="id" value="${studentMarks.id}" />
																	</c:url>
																	<c:url value="viewStudentMarksDetails" var="studentMarksDetailsUrl">
																	  <c:param name="id" value="${studentMarks.id}" />
																	</c:url>
																	
																	<a href="${studentMarksDetailsUrl}" title="Details"><i class="fa-solid fa-circle-info fa-lg"></i></a>
																	<%if(roles.indexOf("Exam Admin") != -1 || roles.indexOf("TEE Admin") != -1){ %>
																	&nbsp;
																	<a href="${editurl}" title="Edit"><i class="fa-solid fa-pen-to-square fa-lg"></i></a>
																	<%} %>
																	&nbsp;									
																	<%if(roles.indexOf("Exam Admin") != -1 ){ %>
																	 <a href="${deleteurl}" title="Delete" onclick="return confirm('Are you sure you want to delete this record?')"><i class="fa-regular fa-trash-can  fa-lg"></i></a> 
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

						<c:url var="firstUrl" value="searchStudentMarksPage?pageNo=1" />
						<c:url var="lastUrl" value="searchStudentMarksPage?pageNo=${page.totalPages}" />
						<c:url var="prevUrl" value="searchStudentMarksPage?pageNo=${page.currentIndex - 1}" />
						<c:url var="nextUrl" value="searchStudentMarksPage?pageNo=${page.currentIndex + 1}" />


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
											<c:url var="pageUrl" value="searchStudentMarksPage?pageNo=${i}" />
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