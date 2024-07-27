<html lang="en">
	
   <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%> 


    <jsp:include page="../adminCommon/jscss.jsp">
	<jsp:param value="Report for Project Submission Status" name="title"/>
    </jsp:include>
    
    
    
    <body>
    
    	<%@ include file="../adminCommon/header.jsp" %>
        <div class="sz-main-content-wrapper">
        
        	<jsp:include page="../adminCommon/breadcrum.jsp">
			<jsp:param value="Exam;Report for Project Submission Status" name="breadcrumItems"/>
			</jsp:include>
        	
            
            <div class="sz-main-content menu-closed">
                    <div class="sz-main-content-inner">
              				<jsp:include page="../adminCommon/left-sidebar.jsp">
								<jsp:param value="" name="activeMenu"/>
							</jsp:include>
              				
              				
              				<div class="sz-content-wrapper examsPage">
              						<%@ include file="../adminCommon/adminInfoBar.jsp" %>
              						<div class="sz-content">
								
											<h2 class="red text-capitalize">Report for Project Submission Status</h2>
											<div class="clearfix"></div>
											<div class="panel-content-wrapper" style="min-height:450px;">
											<%@ include file="../adminCommon/messages.jsp" %>
									<form:form  action="/exam/admin/projectStatusReport" method="post" modelAttribute="marksBean">
									<fieldset>
									<div class="col-md-4">
										<div class="form-group">
												<form:select id="writtenYear" path="year" type="text"	placeholder="Year" class="form-control" itemValue="${marksBean.year}">
													<form:option value="">Select Exam Year</form:option>
													<form:options items="${year}" />
												</form:select>
											</div>
											
											<div class="form-group">
												<form:select id="writtenMonth" path="month" type="text" placeholder="Month" class="form-control"  itemValue="${marksBean.month}">
													<form:option value="">Select Exam Month</form:option>
													<form:options items="${month}" />
												</form:select>
											</div>
											
											
											<div class="form-group">
											<button id="submit" name="submit" class="btn btn-large btn-primary"
												formaction="/exam/admin/projectStatusReport">Generate</button>
												
											<button id="cancel" name="cancel" class="btn btn-danger" 
												formaction="home" formnovalidate="formnovalidate">Cancel</button>
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