<%-- <!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!--> 

<%@page import="com.nmims.beans.Person"%>
<%@page import="com.nmims.beans.Page"%>
<%@page import="com.nmims.beans.OnlineExamMarksBean"%>
<html class="no-js"> <!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<jsp:include page="jscss.jsp">
<jsp:param value="Search Online Exam Marks" name="title" />
</jsp:include>

<body class="inside">

<%@ include file="header.jsp"%>
	
    <section class="content-container login">
        <div class="container-fluid customTheme">
        <div class="row"><legend>Search Online Exam Marks</legend></div>
        <%@ include file="messages.jsp"%>
		
		<form:form  action="searchOnlineMarks" method="post" modelAttribute="searchBean">
			<fieldset>
			<div class="row clearfix">
			
			<div class="col-md-6 column">
					<div class="form-group">
						<form:select id="year" path="year" type="text"	placeholder="Year" class="form-control" required="true" itemValue="${searchBean.year}">
							<form:option value="">Select Exam Year</form:option>
							<form:options items="${yearList}" />
						</form:select>
					</div>
				
					<div class="form-group">
						<form:select id="month" path="month" type="text" placeholder="Month" class="form-control" required="true" itemValue="${searchBean.month}">
							<form:option value="">Select Exam Month</form:option>
							<form:option value="Apr">Apr</form:option>
							<form:option value="Jun">Jun</form:option>
							<form:option value="Sep">Sep</form:option>
							<form:option value="Dec">Dec</form:option>
						</form:select>
					</div>
					
					<div class="form-group" style="overflow:visible;">
							<form:select id="subject" path="subject" class="combobox form-control"   itemValue="${searchBean.subject}">
								<form:option value="">Type OR Select Subject</form:option>
								<form:options items="${subjectList}" />
							</form:select>
					</div>
					
					<div class="form-group">
							<form:input id="sapid" path="sapid" type="text" placeholder="Student ID" class="form-control"  value="${searchBean.sapid}"/>
					</div>
					
					<div class="form-group">
							<form:input id="name" path="name" type="text" placeholder="Student Name" class="form-control"  value="${searchBean.name}"/>
					</div>
														
					<div class="form-group">
						<label class="control-label" for="submit"></label>
						<button id="submit" name="submit" class="btn btn-large btn-primary" formaction="searchOnlineMarks">Search</button>
						<button id="reset" type="reset" class="btn btn-danger" type="reset">Reset</button>
						<button id="cancel" name="cancel" class="btn btn-danger" formaction="home" formnovalidate="formnovalidate">Cancel</button>
					</div>
					
			</div>
			
			</div>
			</fieldset>
		</form:form>
		
	<c:choose>
	<c:when test="${rowCount > 0}">
	
		<legend>&nbsp;Student Marks <font size="2px">(${rowCount} Records Found)&nbsp; <a href="downloadOnlineMarksResults">Download to Excel</a></font></legend>
		<div class="table-responsive">
		<table class="table table-striped table-hover" style="font-size:12px">
							<thead>
								<tr> 
									<th>Sr. No.</th>
									<th>Exam Year</th>
									<th>Exam Month</th>
									<th>SAP ID</th>
									<th>Student Name</th>
									<th>Subject</th>
									
									<th>Section 1 Marks</th>
									<th>Section 2 Marks</th>
									<th>Section 3 Marks</th>
									<th>Section 4 Marks</th>
									
									<th>Total</th>
									<th>Rounded Total</th>
									<th>Actions</th>
								
								</tr>
							</thead>
							<tbody>
							
							<c:forEach var="studentMarks" items="${studentMarksList}" varStatus="status">
						        <tr>
						            <td><c:out value="${status.count}"/></td>
									<td><c:out value="${studentMarks.year}"/></td>
									<td><c:out value="${studentMarks.month}"/></td>
									<td><c:out value="${studentMarks.sapid}"/></td>
									<td nowrap="nowrap"><c:out value="${studentMarks.name}"/></td>
									<td nowrap="nowrap"><c:out value="${studentMarks.subject}"/></td>
									
									<td><c:out value="${studentMarks.part1marks}"/></td>
									<td><c:out value="${studentMarks.part2marks}"/></td>
									<td><c:out value="${studentMarks.part3marks}"/></td>
									<td><c:out value="${studentMarks.part4marks}"/></td>
									
									<td><c:out value="${studentMarks.total}"/></td>
									<td><c:out value="${studentMarks.roundedTotal}"/></td>
	   
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
	
	<c:url var="firstUrl" value="searchOnlineMarksPage?pageNo=1" />
	<c:url var="lastUrl" value="searchOnlineMarksPage?pageNo=${page.totalPages}" />
	<c:url var="prevUrl" value="searchOnlineMarksPage?pageNo=${page.currentIndex - 1}" />
	<c:url var="nextUrl" value="searchOnlineMarksPage?pageNo=${page.currentIndex + 1}" />
	
	
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
	            <c:url var="pageUrl" value="searchOnlineMarksPage?pageNo=${i}" />
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
<%@page import="com.nmims.beans.OnlineExamMarksBean"%>


<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
    <jsp:include page="adminCommon/jscss.jsp">
	<jsp:param value="Search Online Exam Marks" name="title"/>
    </jsp:include>
    
    
    
    <body>
    
    	<%@ include file="adminCommon/header.jsp" %>
        <div class="sz-main-content-wrapper">
        
        	<jsp:include page="adminCommon/breadcrum.jsp">
			<jsp:param value="Exam;Search Online Exam Marks" name="breadcrumItems"/>
			</jsp:include>
        	
            
            <div class="sz-main-content menu-closed">
                    <div class="sz-main-content-inner">
              				<jsp:include page="adminCommon/left-sidebar.jsp">
								<jsp:param value="" name="activeMenu"/>
							</jsp:include>
              				
              				
              				<div class="sz-content-wrapper examsPage">
              						<%@ include file="adminCommon/adminInfoBar.jsp" %>
              						<div class="sz-content">
								
											<h2 class="red text-capitalize">Search Online Exam Marks</h2>
											<div class="clearfix"></div>
											<div class="panel-content-wrapper" style="min-height:450px;">
											<%@ include file="adminCommon/messages.jsp" %>
												<form:form  action="searchOnlineMarks" method="post" modelAttribute="searchBean">
														<fieldset>
														<div class="col-md-4">
																<div class="form-group">
																	<form:select id="year" path="year" type="text"	placeholder="Year" class="form-control" required="true" itemValue="${searchBean.year}">
																		<form:option value="">Select Exam Year</form:option>
																		<form:options items="${yearList}" />
																	</form:select>
																</div>
															
																<div class="form-group">
																	<form:select id="month" path="month" type="text" placeholder="Month" class="form-control" required="true" itemValue="${searchBean.month}">
																		<form:option value="">Select Exam Month</form:option>
							<form:option value="Jan">Jan</form:option>
							<form:option value="Feb">Feb</form:option>
							<form:option value="Mar">Mar</form:option>
							<form:option value="Apr">Apr</form:option>
							<form:option value="May">May</form:option>
							<form:option value="Jun">Jun</form:option>
							<form:option value="Jul">Jul</form:option>
							<form:option value="Aug">Aug</form:option>
							<form:option value="Sep">Sep</form:option>
							<form:option value="Oct">Oct</form:option>
							<form:option value="Nov">Nov</form:option>
							<form:option value="Dec">Dec</form:option>
																	</form:select>
																</div>
																
																<div class="form-group" style="overflow:visible;">
																		<form:select id="subject" path="subject" class="combobox form-control"   itemValue="${searchBean.subject}">
																			<form:option value="">Type OR Select Subject</form:option>
																			<form:options items="${subjectList}" />
																		</form:select>
																</div>
																
																<div class="form-group">
																		<form:input id="sapid" path="sapid" type="text" placeholder="Student ID" class="form-control"  value="${searchBean.sapid}"/>
																</div>
																
																<div class="form-group">
																		<form:input id="name" path="name" type="text" placeholder="Student Name" class="form-control"  value="${searchBean.name}"/>
																</div>
																									
																<div class="form-group">
																	<label class="control-label" for="submit"></label>
																	<button id="submit" name="submit" class="btn btn-large btn-primary" formaction="searchOnlineMarks">Search</button>
																	<button id="reset" type="reset" class="btn btn-danger" type="reset">Reset</button>
																	<button id="cancel" name="cancel" class="btn btn-danger" formaction="${pageContext.request.contextPath}/home" formnovalidate="formnovalidate">Cancel</button>
																</div>
																
															</div>
														</fieldset>
												</form:form>
											</div>
														
											</div>
								<c:choose>
								<c:when test="${rowCount > 0}">

									<h2 style="margin-left:50px;">&nbsp;&nbsp;Student Marks<font size="2px"> (${rowCount} Records Found)&nbsp; <a href="downloadOnlineMarksResults">Download to Excel</a></font></h2>
									<div class="clearfix"></div>
										<div class="panel-content-wrapper">
										<div class="table-responsive">
									<table class="table table-striped table-hover" style="font-size:12px">
														<thead>
														<tr> 
															<th>Sr. No.</th>
															<th>Exam Year</th>
															<th>Exam Month</th>
															<th>SAP ID</th>
															<th>Student Name</th>
															<th>Subject</th>
															
															<th>Section 1 Marks</th>
															<th>Section 2 Marks</th>
															<th>Section 3 Marks</th>
															<th>Section 4 Marks</th>
															
															<th>Total</th>
															<th>Rounded Total</th>
															<th>Actions</th>
														
														</tr>
													</thead>
													<tbody>
													
													<c:forEach var="studentMarks" items="${studentMarksList}" varStatus="status">
														<tr>
															<td><c:out value="${status.count}"/></td>
															<td><c:out value="${studentMarks.year}"/></td>
															<td><c:out value="${studentMarks.month}"/></td>
															<td><c:out value="${studentMarks.sapid}"/></td>
															<td nowrap="nowrap"><c:out value="${studentMarks.name}"/></td>
															<td nowrap="nowrap"><c:out value="${studentMarks.subject}"/></td>
															
															<td><c:out value="${studentMarks.part1marks}"/></td>
															<td><c:out value="${studentMarks.part2marks}"/></td>
															<td><c:out value="${studentMarks.part3marks}"/></td>
															<td><c:out value="${studentMarks.part4marks}"/></td>
															
															<td><c:out value="${studentMarks.total}"/></td>
															<td><c:out value="${studentMarks.roundedTotal}"/></td>
							   
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
																<%-- <a href="${studentMarksDetailsUrl}" title="Details"><i class="fa fa-info-circle fa-lg"></i></a>
																--%>
																<%if(roles.indexOf("Exam Admin") != -1 || roles.indexOf("TEE Admin") != -1){ %>
																<%-- <a href="${editurl}" title="Edit"><i class="fa fa-pencil-square-o fa-lg"></i></a> --%>
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
						<c:url var="firstUrl" value="searchOnlineMarksPage?pageNo=1" />
						<c:url var="lastUrl" value="searchOnlineMarksPage?pageNo=${page.totalPages}" />
						<c:url var="prevUrl" value="searchOnlineMarksPage?pageNo=${page.currentIndex - 1}" />
						<c:url var="nextUrl" value="searchOnlineMarksPage?pageNo=${page.currentIndex + 1}" />
	
	
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
												<c:url var="pageUrl" value="searchOnlineMarksPage?pageNo=${i}" />
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
        <jsp:include page="adminCommon/footer.jsp"/>
        
		
    </body>
</html>