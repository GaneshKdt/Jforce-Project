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
<jsp:param value="Search Pass/Fail Records" name="title" />
</jsp:include>

<body class="inside">

<%@ include file="header.jsp"%>
	
    <section class="content-container login">
        <div class="container-fluid customTheme">
        <div class="row"><legend>Search Pass/Fail Records</legend></div>
        <%@ include file="messages.jsp"%>
		<div class="row clearfix">
		<form:form  action="searchPassFail" method="post" modelAttribute="studentMarks">
			<fieldset>
			<div class="col-md-6 column">
				
					<div class="form-group">
						<form:select id="writtenYear" path="writtenYear" type="text"	placeholder="Written Year" class="form-control"   itemValue="${studentMarks.writtenYear}">
							<form:option value="">Select Written Year</form:option>
							<form:options items="${yearList}" />
						</form:select>
					</div>
				
					<div class="form-group">
						<form:select id="writtenMonth" path="writtenMonth" type="text" placeholder="Written Month" class="form-control"  itemValue="${studentMarks.writtenMonth}">
							<form:option value="">Select Written Month</form:option>
							<form:option value="Apr">Apr</form:option>
							<form:option value="Jun">Jun</form:option>
							<form:option value="Sep">Sep</form:option>
							<form:option value="Dec">Dec</form:option>
						</form:select>
					</div>
					
					<div class="form-group">
						<form:select id="assignmentYear" path="assignmentYear" type="text"	placeholder="Assignment Year" class="form-control"   itemValue="${studentMarks.assignmentYear}">
							<form:option value="">Select Assignment Year</form:option>
							<form:options items="${yearList}" />
						</form:select>
					</div>
				
					<div class="form-group">
						<form:select id="assignmentMonth" path="assignmentMonth" type="text" placeholder="Assignment Month" class="form-control"  itemValue="${studentMarks.assignmentMonth}">
							<form:option value="">Select Assignment Month</form:option>
							<form:option value="April">April</form:option>
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
						<label class="control-label" for="centerCode">Information Center</label>
						<form:select id="centerCode" path="centerCode"  class="form-control"  itemValue="${studentMarks.centerCode}" >
							<form:option value="">Select IC</form:option>
							<form:options items="${centerList}" />
						</form:select>
					</div>
				</div>
				<div class="col-md-6 column">

					<div class="form-group">
							<form:input id="name" path="name" type="text" placeholder="Student Name" class="form-control" value="${studentMarks.name}"/>
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
								<form:option value="">Type OR Select Subject</form:option>
								<form:options items="${subjectList}" />
							</form:select>
					</div>
					
					<div class="form-group">
							<form:select id="isPass" path="isPass" placeholder="Pass" class="form-control"   itemValue="${studentMarks.isPass}">
								<form:option value="">Select Pass/Fail</form:option>
								<form:option value="Y">Pass Students</form:option>
							<form:option value="N">Fail Students</form:option>
							</form:select>
					</div>
					
					<div class="form-group">
							<form:select id="graceApplied" path="graceGiven" placeholder="Grace Applied" class="form-control"   itemValue="${studentMarks.graceGiven}">
								<form:option value="">Select Grace Option</form:option>
								<form:option value="Y">Grace Given</form:option>
								<form:option value="N">Grace Not Given</form:option>
							</form:select>
					</div>
					
					<div class="form-group">
						<label class="control-label" for="submit"></label>
						<!-- <div class="controls"> -->
							<button id="submit" name="submit" class="btn btn-large btn-primary" formaction="searchPassFail">Search</button>
							<button id="reset" type="reset" class="btn btn-danger" type="reset">Reset</button>
							<button id="cancel" name="cancel" class="btn btn-danger" formaction="home" formnovalidate="formnovalidate">Cancel</button>
						<!-- </div> -->
					</div>
				</div>
			</fieldset>
		</form:form>
		
		</div>
	
	
	<c:choose>
	<c:when test="${rowCount > 0}">

	<legend>&nbsp;Student Marks <font size="2px">(${rowCount} Records Found) &nbsp; <a href="downloadPassFailResults">Download to Excel</a></font></legend>
	<div class="table-responsive">
	<table class="table table-striped table-hover" style="font-size:12px">
						<thead>
						<tr>
							<th>Sr. No.</th>
							<th>Written Year</th>
							<th>Written Month</th>
							<th>Assignment Year</th>
							<th>Assignment Month</th> 
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
							<th>Pass</th>
							<th>Reason</th>
							<th>Action</th>
						</tr>
					</thead>
						<tbody>
						
						<c:forEach var="studentMarks" items="${studentMarksList}" varStatus="status">
					        <tr>
					            <td><c:out value="${status.count}" /></td>
								<td><c:out value="${studentMarks.writtenYear}" /></td>
								<td><c:out value="${studentMarks.writtenMonth}" /></td>
								<td><c:out value="${studentMarks.assignmentYear}"/></td>
								<td><c:out value="${studentMarks.assignmentMonth}"/></td>
								<td><c:out value="${studentMarks.grno}" /></td>
								<td><c:out value="${studentMarks.sapid}" /></td>
								<td><c:out value="${studentMarks.name}" /></td>
								<td><c:out value="${studentMarks.program}" /></td>
								<td><c:out value="${studentMarks.sem}" /></td>
								<td><c:out value="${studentMarks.subject}" /></td>
								<td><c:out value="${studentMarks.writtenscore}" /></td>
								<td><c:out value="${studentMarks.assignmentscore}" /></td>
								<td><c:out value="${studentMarks.gracemarks}"/></td>
								<td><c:out value="${studentMarks.total}" /></td>
								<td><c:out value="${studentMarks.isPass}" /></td>
								<td><c:out value="${studentMarks.failReason}" /></td>
								
								<c:url value="processPassFailForASubject" var="reprocessUrl">
								  <c:param name="sapid" value="${studentMarks.sapid}" />
								  <c:param name="subject" value="${studentMarks.subject}" />
								</c:url>
								
								<td>
								<%if(roles.indexOf("Exam Admin") != -1 || roles.indexOf("TEE Admin") != -1 || roles.indexOf("Assignment Admin") != -1){ %>
								<a href="${reprocessUrl}" title="Re-process Pass Fail"><i class="fa fa-cog fa-lg fa-spin"></i></a>&nbsp;
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

<c:url var="firstUrl" value="searchPassFailPage?pageNo=1" />
<c:url var="lastUrl" value="searchPassFailPage?pageNo=${page.totalPages}" />
<c:url var="prevUrl" value="searchPassFailPage?pageNo=${page.currentIndex - 1}" />
<c:url var="nextUrl" value="searchPassFailPage?pageNo=${page.currentIndex + 1}" />


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
            <c:url var="pageUrl" value="searchPassFailPage?pageNo=${i}" />
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
	<jsp:param value="Search Pass Fail" name="title"/>
    </jsp:include>
            
    <body>
    
    	<%@ include file="adminCommon/header.jsp" %>
        <div class="sz-main-content-wrapper">
        
        	<jsp:include page="adminCommon/breadcrum.jsp">
			<jsp:param value="Student Zone;Change Password" name="breadcrumItems"/>
			</jsp:include>
        	
            <div class="sz-main-content menu-closed">
                    <div class="sz-main-content-inner">
              				<jsp:include page="adminCommon/left-sidebar.jsp">
								<jsp:param value="" name="activeMenu"/>
							</jsp:include>
              				
              				
              				<div class="sz-content-wrapper examsPage">
              						<%@ include file="adminCommon/adminInfoBar.jsp" %>
              						<div class="sz-content">
											<h2 class="red text-capitalize">Search Pass Fail</h2>
											<div class="clearfix"></div>
											<div class="panel-content-wrapper" style="min-height:450px;">
											<%@ include file="adminCommon/messages.jsp" %>
											<form:form  action="searchPassFail" method="post" modelAttribute="studentMarks">
												<fieldset>
											<div class="col-md-4">
												
												
												<div class="form-group">
													<form:select id="resultProcessedYear" path="resultProcessedYear" type="text"	placeholder="resultProcessedYear" class="form-control writtenYearMonth"   itemValue="${studentMarks.resultProcessedYear}">
														<form:option value="">Select Result Processed Year</form:option>
														<form:options items="${yearList}" />
													</form:select>
												</div>
											
												<div class="form-group">
													<form:select id="resultProcessedMonth" path="resultProcessedMonth" type="text" placeholder="resultProcessedMonth" class="form-control writtenYearMonth"  itemValue="${studentMarks.resultProcessedMonth}">
														<form:option value="">Select Result Processed Month</form:option>
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
												
												<div class="form-group">
													<form:select id="writtenYear" path="writtenYear" type="text"	placeholder="Written Year" class="form-control writtenYearMonth"   itemValue="${studentMarks.writtenYear}">
														<form:option value="">Select Written Year</form:option>
														<form:options items="${yearList}" />
													</form:select>
												</div>
											
												<div class="form-group">
													<form:select id="writtenMonth" path="writtenMonth" type="text" placeholder="Written Month" class="form-control writtenYearMonth"  itemValue="${studentMarks.writtenMonth}">
														<form:option value="">Select Written Month</form:option>
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
												
												<div class="form-group">
													<form:select id="assignmentYear" path="assignmentYear" type="text"	placeholder="Assignment Year" class="form-control assignmentYearMonth"   itemValue="${studentMarks.assignmentYear}">
														<form:option value="">Select Assignment Year</form:option>
														<form:options items="${yearList}" />
													</form:select>
												</div>
											
												<div class="form-group">
													<form:select id="assignmentMonth" path="assignmentMonth" type="text" placeholder="Assignment Month" class="form-control assignmentYearMonth"  itemValue="${studentMarks.assignmentMonth}">
														<form:option value="">Select Assignment Month</form:option>
														<form:option value="Jan">Jan</form:option>
														<form:option value="Apr">Apr</form:option>
														<form:option value="May">May</form:option>
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
												<%if(roles.indexOf("Information Center") == -1 && roles.indexOf("Corporate Center") == -1) {%>
													<label class="control-label" for="centerCode">Information Center</label>
													<form:select id="centerCode" path="centerCode"  class="form-control"  itemValue="${studentMarks.centerCode}" >
														<form:option value="">Select IC</form:option>
														<form:options items="${centerList}" />
													</form:select>
													<%} %>
												</div>
												
											</div>
											<div class="col-md-4">
												<div class="form-group">
														<form:input id="name" path="name" type="text" placeholder="Student Name" class="form-control" value="${studentMarks.name}"/>
												</div>
												
												<div class="form-group">
											<form:select id="consumerType" path="consumerType"
												placeholder="consumerType" class="form-control"
												value="${studentMarks.consumerType}">
												<form:option value="">Select Consumer Type</form:option>
												<form:option value="Retail">Retail</form:option>
												<form:option value="Verizon">Verizon</form:option>
												<form:option value="SAS">SAS</form:option>
												<form:option value="Diageo">Diageo</form:option>
												<form:option value="Bajaj">Bajaj</form:option>
												<form:option value="EMERSON">EMERSON</form:option>
												<form:option value="CIPLA">CIPLA</form:option>
												<form:option value="Disontinued from MLI (No more Employee)">Disontinued from MLI (No more Employee)</form:option>
											</form:select>
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
														<form:option value="">Type OR Select Subject</form:option>
														<form:options items="${subjectList}" />
													</form:select>
											</div>
					
											<div class="form-group">
													<form:select id="isPass" path="isPass" placeholder="Pass" class="form-control"   itemValue="${studentMarks.isPass}">
														<form:option value="">Select Pass/Fail</form:option>
														<form:option value="Y">Pass Students</form:option>
													<form:option value="N">Fail Students</form:option>
													</form:select>
											</div>
					
											<div class="form-group">
													<form:select id="graceApplied" path="graceGiven" placeholder="Grace Applied" class="form-control"   itemValue="${studentMarks.graceGiven}">
														<form:option value="">Select Grace Option</form:option>
														<form:option value="Y">Grace Given</form:option>
														<form:option value="N">Grace Not Given</form:option>
													</form:select>
											</div>
					
											<div class="form-group">
												<label class="control-label" for="submit"></label>
												<!-- <div class="controls"> -->
													<button id="submit" name="submit" class="btn btn-large btn-primary" formaction="searchPassFail">Search</button>
													<button id="reset" type="reset" class="btn btn-danger" type="reset">Reset</button>
													<button id="cancel" name="cancel" class="btn btn-danger" formaction="${pageContext.request.contextPath}/home" formnovalidate="formnovalidate">Cancel</button>
												<!-- </div> -->
											</div>
												
											</div>
												
												</fieldset>
											</form:form>
											</div>
              								<c:choose>
	<c:when test="${rowCount > 0}">

				<h2 style="margin-left:50px;">&nbsp;&nbsp;Student Marks<font size="2px"> (${rowCount} Records Found)&nbsp;<%if(roles.indexOf("Information Center") == -1 && roles.indexOf("Corporate Center") == -1) {%> <a href="downloadPassFailResults">Download to Excel</a><%} %></font></h2>
									<div class="clearfix"></div>
										<div class="panel-content-wrapper">
										<div class="table-responsive">
									<table class="table table-striped table-hover" style="font-size:12px">
						<thead>
						<tr>
							<th>Sr. No.</th>
							<th>Written Year</th>
							<th>Written Month</th>
							<th>Assignment Year</th>
							<th>Assignment Month</th> 
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
							<th>Pass</th>
							<th>Reason</th>
							<th>Assignment Remarks</th>
							<!-- Added By shivam.pandey.EXT - START -->
							<th>Result Declared Date</th>
							<!-- Added By shivam.pandey.EXT - END -->
							<th>Action</th>
						</tr>
					</thead>
						<tbody>
						
						<c:forEach var="studentMarks" items="${studentMarksList}" varStatus="status">
					        <tr>
					            <td><c:out value="${status.count}" /></td>
								<td><c:out value="${studentMarks.writtenYear}" /></td>
								<td><c:out value="${studentMarks.writtenMonth}" /></td>
								<td><c:out value="${studentMarks.assignmentYear}"/></td>
								<td><c:out value="${studentMarks.assignmentMonth}"/></td>
								<td><c:out value="${studentMarks.grno}" /></td>
								<td><c:out value="${studentMarks.sapid}" /></td>
								<td><c:out value="${studentMarks.name}" /></td>
								<td><c:out value="${studentMarks.program}" /></td>
								<td><c:out value="${studentMarks.sem}" /></td>
								<td><c:out value="${studentMarks.subject}" /></td>
								<td><c:out value="${studentMarks.writtenscore}" /></td>
								<td><c:out value="${studentMarks.assignmentscore}" /></td>
								<td><c:out value="${studentMarks.gracemarks}"/></td>
								<td><c:out value="${studentMarks.total}" /></td>
								<td><c:out value="${studentMarks.isPass}" /></td>
								<td><c:out value="${studentMarks.failReason}" /></td>
								<td><c:out value="${studentMarks.assignmentRemarks}" /></td>
								<!-- Added By shivam.pandey.EXT - START -->
								<td><c:out value="${studentMarks.resultDeclaredDate}" /></td>
								<!-- Added By shivam.pandey.EXT - END -->
								<c:url value="processPassFailForASubject" var="reprocessUrl">
								  <c:param name="sapid" value="${studentMarks.sapid}" />
								  <c:param name="subject" value="${studentMarks.subject}" />
								  <c:param name="sem" value="${studentMarks.sem}" />
								</c:url>
								
								<td>
								<%if(roles.indexOf("Exam Admin") != -1 || roles.indexOf("TEE Admin") != -1 || roles.indexOf("Assignment Admin") != -1){ %>
								<a href="${reprocessUrl}" title="Re-process Pass Fail"><i class="fa-solid fa-gear fa-lg fa-spin"></i></a>&nbsp;
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

<c:url var="firstUrl" value="searchPassFailPage?pageNo=1" />
<c:url var="lastUrl" value="searchPassFailPage?pageNo=${page.totalPages}" />
<c:url var="prevUrl" value="searchPassFailPage?pageNo=${page.currentIndex - 1}" />
<c:url var="nextUrl" value="searchPassFailPage?pageNo=${page.currentIndex + 1}" />


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
            <c:url var="pageUrl" value="searchPassFailPage?pageNo=${i}" />
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
        <script>
        $(".writtenYearMonth").change(function(){
        	$(".writtenYearMonth").attr("required", false);
        	$(".assignmentYearMonth").attr("required", false);
        });
        $(".assignmentYearMonth").change(function(){
        	$(".assignmentYearMonth").attr("required", false);
        	$(".writtenYearMonth").attr("required", false);
        });
        
        </script>
		
    </body>
</html>