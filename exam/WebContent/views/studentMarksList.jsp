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
<jsp:param value="Exam Data Management" name="title" />
</jsp:include>

<body class="inside">

<%@ include file="header.jsp"%>
	
    <section class="content-container">

		<div class="container-fluid customTheme">
					<div class="row"><legend>&nbsp;Student Marks <font size="2px">(${page.rowCount} Records Found)</font></legend></div>
					
					<div class="table-responsive">
					<table class="table table-striped table-hover" style="font-size:12px">
						<thead>
							<tr> 
								<th>Sr. No.</th>
								<th>Exam Year</th>
								<th>Exam Month</th>
								<th>Syllabus Year</th>
								<th>GR No.</th>
								<th>SAP ID</th>
								<th>Student Name</th>
								<th>Program</th>
								<th>Sem</th>
								<th>Subject</th>
								<th>Written</th>
								<th>Assign.</th>
								<th>Grace</th>
								<th>Total</th>
								<th>Attempt</th>
								<!--  <th>Source</th>
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
								<td><c:out value="${studentMarks.syllabusYear}"/></td>
								<td><c:out value="${studentMarks.grno}"/></td>
								<td><c:out value="${studentMarks.sapid}"/></td>
								<td nowrap="nowrap"><c:out value="${studentMarks.studentname}"/></td>
								<td><c:out value="${studentMarks.program}"/></td>
								<td><c:out value="${studentMarks.sem}"/></td>
								<td nowrap="nowrap"><c:out value="${studentMarks.subject}"/></td>
								<td><c:out value="${studentMarks.writenscore}"/></td>
								<td><c:out value="${studentMarks.assignmentscore}"/></td>
								<td><c:out value="${studentMarks.gracemarks}"/></td>
								<td><c:out value="${studentMarks.total}"/></td>
								<td><c:out value="${studentMarks.attempt}"/></td>
								<!-- <td><c:out value="${studentMarks.source}"/></td>
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
									<a href="${editurl}" title="Edit"><i class="fa fa-pencil-square-o fa-lg"></i></a>
									<a class="glyphicon glyphicon-trash" href="${deleteurl}" title="Delete" onclick="return confirm('Are you sure you want to delete this record?')"></a>
					            </td>
					        </tr>   
					    </c:forEach>
							
							
						</tbody>
					</table>
					</div>

<c:url var="firstUrl" value="getAllStudentMarksPage?pageNo=1" />
<c:url var="lastUrl" value="getAllStudentMarksPage?pageNo=${page.totalPages}" />
<c:url var="prevUrl" value="getAllStudentMarksPage?pageNo=${page.currentIndex - 1}" />
<c:url var="nextUrl" value="getAllStudentMarksPage?pageNo=${page.currentIndex + 1}" />


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
            <c:url var="pageUrl" value="getAllStudentMarksPage?pageNo=${i}" />
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
	<jsp:param value="Search Student Marks" name="title"/>
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
											<h2 class="red text-capitalize">Search Student Marks</h2>
											<div class="clearfix"></div>
													<div class="panel-content-wrapper" style="min-height:450px;">
											<%@ include file="adminCommon/messages.jsp" %>
								<h2>&nbsp;Search Results<font size="2px"> (${rowCount} Records Found)&nbsp; <a href="downloadStudentMarksResults">Download to Excel</a></font></h2>
								<div class="clearfix"></div>
									<div class="table-responsive">
								<table class="table table-striped table-hover" style="font-size:12px">
													<thead>
														<tr> 
															<th>Sr. No.</th>
															<th>Exam Year</th>
															<th>Exam Month</th>
															<th>Syllabus Year</th>
															<th>GR No.</th>
															<th>SAP ID</th>
															<th>Student Name</th>
															<th>Program</th>
															<th>Sem</th>
															<th>Subject</th>
															<th>Written</th>
															<th>Assign.</th>
															<th>Grace</th>
															<th>Total</th>
															<th>Attempt</th>
															<!--  <th>Source</th>
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
																	<td><c:out value="${studentMarks.syllabusYear}"/></td>
																	<td><c:out value="${studentMarks.grno}"/></td>
																	<td><c:out value="${studentMarks.sapid}"/></td>
																	<td nowrap="nowrap"><c:out value="${studentMarks.studentname}"/></td>
																	<td><c:out value="${studentMarks.program}"/></td>
																	<td><c:out value="${studentMarks.sem}"/></td>
																	<td nowrap="nowrap"><c:out value="${studentMarks.subject}"/></td>
																	<td><c:out value="${studentMarks.writenscore}"/></td>
																	<td><c:out value="${studentMarks.assignmentscore}"/></td>
																	<td><c:out value="${studentMarks.gracemarks}"/></td>
																	<td><c:out value="${studentMarks.total}"/></td>
																	<td><c:out value="${studentMarks.attempt}"/></td>
																	<!-- <td><c:out value="${studentMarks.source}"/></td>
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
																		<a href="${editurl}" title="Edit"><i class="fa-solid fa-pen-to-square fa-lg"></i></a>
																		<%-- <a class="glyphicon glyphicon-trash" href="${deleteurl}" title="Delete" onclick="return confirm('Are you sure you want to delete this record?')"></a> --%>
																	</td>
																</tr>   
															</c:forEach>
													</tbody>
												</table>
								</div>
								</div>
								<br>
						

									<c:url var="firstUrl" value="getAllStudentMarksPage?pageNo=1" />
									<c:url var="lastUrl" value="getAllStudentMarksPage?pageNo=${page.totalPages}" />
									<c:url var="prevUrl" value="getAllStudentMarksPage?pageNo=${page.currentIndex - 1}" />
									<c:url var="nextUrl" value="getAllStudentMarksPage?pageNo=${page.currentIndex + 1}" />


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
											<c:url var="pageUrl" value="getAllStudentMarksPage?pageNo=${i}" />
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