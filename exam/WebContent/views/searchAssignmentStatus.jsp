<%-- <!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!--> 

<%@page import="com.nmims.beans.Person"%>
<%@page import="com.nmims.beans.Page"%>
<%@page import="com.nmims.beans.AssignmentStatusBean"%>
<html class="no-js"> <!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<jsp:include page="jscss.jsp">
<jsp:param value="Search Assignment Status" name="title" />
</jsp:include>

<body class="inside">

<%@ include file="header.jsp"%>
	
    <section class="content-container login">
        <div class="container-fluid customTheme">
        <div class="row"><legend>Search Assignment Status</legend></div>
        <%@ include file="messages.jsp"%>
		<div class="row clearfix">
		<form:form  action="searchAssignmentStatus" method="post" modelAttribute="assignmentStatus">
			<fieldset>
			<div class="col-md-6 column">

				
					<div class="form-group">
						<form:select id="year" path="year" type="text"	placeholder="Year" class="form-control"   itemValue="${assignmentStatus.year}">
							<form:option value="">Select Year</form:option>
							<form:options items="${yearList}" />
						</form:select>
					</div>
				
					<div class="form-group">
						<form:select id="month" path="month" type="text" placeholder="Month" class="form-control"  itemValue="${assignmentStatus.month}">
							<form:option value="">Select Month</form:option>
							<form:option value="Apr">Apr</form:option>
							<form:option value="Jun">Jun</form:option>
							<form:option value="Sep">Sep</form:option>
							<form:option value="Dec">Dec</form:option>
						</form:select>
					</div>
					
				
					<div class="form-group">
							<form:input id="sapId" path="sapId" type="text" placeholder="SAP ID" class="form-control" value="${assignmentStatus.sapId}"/>
					</div>
					
					<div class="form-group" style="overflow:visible;">
							<form:select id="subject" path="subject" class="combobox form-control"   itemValue="${assignmentStatus.subject}">
								<form:option value="">Type OR Select Subject</form:option>
								<form:options items="${subjectList}" />
							</form:select>
					</div>					

				<!-- Button (Double) -->
				<div class="form-group">
					<label class="control-label" for="submit"></label>
					<div class="controls">
						<button id="submit" name="submit" class="btn btn-large btn-primary" formaction="searchAssignmentStatus">Search</button>
						<button id="submit" name="submit" class="btn btn-large btn-primary"  formaction="searchANSForCenters">Download ANS Report</button>
						<button id="cancel" name="cancel" class="btn btn-danger" formaction="home" formnovalidate="formnovalidate">Cancel</button>
					</div>
				</div>

			

</div>



</fieldset>
		</form:form>
		
		</div>
	
	
	<c:choose>
<c:when test="${rowCount > 0}">

	<legend>&nbsp;Assignment Status<font size="2px">(${rowCount} Records Found)&nbsp; <a href="downloadAssignmentStatus">Download to Excel</a></font></legend>
	<div class="table-responsive">
	<table class="table table-striped table-hover" style="font-size:12px">
						<thead>
							<tr> 
								<th>Sr. No.</th>
								<th>Exam Year</th>
								<th>Exam Month</th>
								<th>SAP ID</th>
								<th>Subject</th>
								<th>Submitted</th>
							
							</tr>
						</thead>
						<tbody>
						
						<c:forEach var="assignmentStatus" items="${assignmentStatusList}" varStatus="status">
					        <tr>
					            <td><c:out value="${status.count}"/></td>
								<td><c:out value="${assignmentStatus.examYear}"/></td>
								<td><c:out value="${assignmentStatus.examMonth}"/></td>
								<td><c:out value="${assignmentStatus.sapid}"/></td>
								<td nowrap="nowrap"><c:out value="${assignmentStatus.subject}"/></td>
								<td><c:out value="${assignmentStatus.submitted}"/></td>
								
					        </tr>   
					    </c:forEach>
							
							
						</tbody>
					</table>
	</div>
	<br>

</c:when>
</c:choose>

<c:url var="firstUrl" value="searchAssignmentStatusPage?pageNo=1" />
<c:url var="lastUrl" value="searchAssignmentStatusPage?pageNo=${page.totalPages}" />
<c:url var="prevUrl" value="searchAssignmentStatusPage?pageNo=${page.currentIndex - 1}" />
<c:url var="nextUrl" value="searchAssignmentStatusPage?pageNo=${page.currentIndex + 1}" />


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
            <c:url var="pageUrl" value="searchAssignmentStatusPage?pageNo=${i}" />
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
</html> --%>

<!DOCTYPE html>
<html lang="en">
	
<%@page import="com.nmims.beans.Person"%>
<%@page import="com.nmims.beans.Page"%>
<%@page import="com.nmims.beans.AssignmentStatusBean"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
    <jsp:include page="adminCommon/jscss.jsp">
	<jsp:param value="Search Assignment Status" name="title"/>
    </jsp:include>
    
    
    
    <body>
    
    	<%@ include file="adminCommon/header.jsp" %>
        <div class="sz-main-content-wrapper">
        
        	<jsp:include page="adminCommon/breadcrum.jsp">
			<jsp:param value="Exam;Search Assignment Status" name="breadcrumItems"/>
			</jsp:include>
        	
            
            <div class="sz-main-content menu-closed">
                    <div class="sz-main-content-inner">
              				<jsp:include page="adminCommon/left-sidebar.jsp">
								<jsp:param value="" name="activeMenu"/>
							</jsp:include>
              				
              				
              				<div class="sz-content-wrapper examsPage">
              						<%@ include file="adminCommon/adminInfoBar.jsp" %>
              						<div class="sz-content">
								
											<h2 class="red text-capitalize">Search Assignment Status</h2>
											<div class="clearfix"></div>
							<div class="panel-content-wrapper" style="min-height:450px;">
								<%@ include file="adminCommon/messages.jsp" %>
									<form:form  method="post" modelAttribute="assignmentStatus">
										<fieldset>
												<div class="col-sm-5">
														
														<div class="form-group">
															<form:select id="acadYear" path="acadYear" type="text"	placeholder="Academic Year" class="form-control durationFields">
																<form:option value="">Select Academic Year</form:option>
																<form:options items="${yearList}" />
															</form:select>
														</div>
													
														<div class="form-group">
															<form:select id="acadMonth" path="acadMonth" type="text" placeholder="Academic Month" class="form-control durationFields">
																<form:option value="">Select Academic Month</form:option>
																<form:option value="Jan">Jan</form:option>
																<form:option value="Jul">Jul</form:option>
																
															</form:select>
														</div>
														
														<div class="form-group">
															<form:select id="year" path="year" type="text"	placeholder="Year" class="form-control"  required="true" itemValue="${assignmentStatus.year}">
																<form:option value="">Select Exam Year</form:option>
																<form:options items="${yearList}" />
															</form:select>
														</div>
														<div class="form-group">
															<form:select id="month" path="month" type="text" placeholder="Month" class="form-control" required="true" itemValue="${assignmentStatus.month}">
																<form:option value="">Select Exam Month</form:option>
																<form:option value="Apr">Apr</form:option>
																<form:option value="Jun">Jun</form:option>
																<form:option value="Sep">Sep</form:option>
																<form:option value="Dec">Dec</form:option>
															</form:select>
														</div>
														
														<div class="form-group">
			            <select data-id="consumerTypeDataId" id="consumerTypeId" name="consumerTypeId"  class="selectConsumerType form-control"  >
			             <option disabled selected value="">Select Consumer Type</option>
			             <c:forEach var="consumerType" items="${consumerType}">
			              <c:choose>
			               <c:when test="${consumerType.id == assignmentStatus.consumerTypeId}">
			                <option selected value="<c:out value="${consumerType.id}"/>">
			                              <c:out value="${consumerType.name}"/>
			                            </option>
			               </c:when>
			               <c:otherwise>
			                <option value="<c:out value="${consumerType.id}"/>">
			                              <c:out value="${consumerType.name}"/>
			                            </option>
			               </c:otherwise>
			              </c:choose>
			     
			                      </c:forEach>
			            </select>
			          </div>
			          <div class="form-group">
			            <select id="programStructureId" name="programStructureId"  class="selectProgramStructure form-control"  >
			             <option disabled selected value="">Select Program Structure</option>
			            </select>
			          </div>
			          <div class="form-group">
			            <select id="programId" name="programId"  class="selectProgram form-control" >
			             <option disabled selected value="">Select Program</option>
			            </select>
			          </div> 
          
				       <div class="form-group">
					      <select id="subjectId" name="subject"  class="selectSubject form-control" >
					       <option disabled selected value="">Select Subject</option>
					      </select>
					    </div>   
					
												
														<div class="form-group">
																<form:input id="sapId" path="sapId" type="text" placeholder="SAP ID" class="form-control" value="${assignmentStatus.sapId}"/>
														</div>					
												
													<!-- Button (Double) -->
													<div class="form-group">
														<label class="control-label" for="submit"></label>
															<button id="submit" name="submit" class="btn btn-large btn-primary" formaction="searchAssignmentStatus">Search</button>
															<button  id="submit" name="submit" class="btn btn-large btn-primary"  formaction="searchANSForCenters" onclick="return validate()">Download ANS Report</button>
															<button id="cancel" name="cancel" class="btn btn-danger" formaction="home" formnovalidate="formnovalidate">Cancel</button>
													</div>
												</div>
											</fieldset>
									</form:form>
							</div>
								<c:choose>
		<c:when test="${rowCount > 0}">

	 <h2 style="margin-left:50px;">&nbsp;&nbsp;Assignment Status<font size="2px"> (${rowCount} Records Found)&nbsp; <a href="downloadAssignmentStatus">Download to Excel</a></font></h2>
	<div class="clearfix"></div>
		<div class="panel-content-wrapper">
		<div class="table-responsive">
	<table class="table table-striped table-hover" style="font-size:12px">
						<thead>
							<tr> 
								<th>Sr. No.</th>
								<th>Exam Year</th>
								<th>Exam Month</th>
								<th>SEM</th>
								<th>SAP ID</th>
								<th>Center Name</th>
								<th>Subject</th>
								<th>Submitted</th>
							
							</tr>
						</thead>
						<tbody>
						
						<c:forEach var="assignmentStatus" items="${assignmentStatusList}" varStatus="status">
					        <tr>
					            <td><c:out value="${status.count}"/></td>
								<td><c:out value="${assignmentStatus.examYear}"/></td>
								<td><c:out value="${assignmentStatus.examMonth}"/></td>
								<td><c:out value="${assignmentStatus.sem}"/></td>
								<td><c:out value="${assignmentStatus.sapid}"/></td>
								<td><c:out value="${assignmentStatus.centerName}"/></td>
								<td nowrap="nowrap"><c:out value="${assignmentStatus.subject}"/></td>
								<td><c:out value="${assignmentStatus.submitted}"/></td>
								
					        </tr>   
					    </c:forEach>
						</tbody>
					</table>
				</div>
				</div>
				<br>
							</c:when>
					</c:choose>
							<c:url var="firstUrl" value="searchAssignmentStatusPage?pageNo=1" />
							<c:url var="lastUrl" value="searchAssignmentStatusPage?pageNo=${page.totalPages}" />
							<c:url var="prevUrl" value="searchAssignmentStatusPage?pageNo=${page.currentIndex - 1}" />
							<c:url var="nextUrl" value="searchAssignmentStatusPage?pageNo=${page.currentIndex + 1}" />


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
										<c:url var="pageUrl" value="searchAssignmentStatusPage?pageNo=${i}" />
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
		 var consumerTypeId = '${ assignmentStatus.consumerTypeId }';
		 var programStructureId = '${ assignmentStatus.programStructureId }';
		 var programId = '${ assignmentStatus.programId }';
		 var g_subject = '${ assignmentStatus.subject }';
		</script>
		
		<script>
		function validate(){
			//console.log('validate invoked');
			var acadYear = document.getElementById('acadYear').value;
			var acadMonth = document.getElementById('acadMonth').value;
			//console.log("acadYear" +acadYear);
			//console.log("acadMonth" +acadMonth);
			if(acadYear == "" || acadMonth == ""){
				alert('Kindly select acadYear and acadMonth');
				return false;
			}
			
			return true;
		
		}
		</script>
        <%@ include file="../views/common/consumerProgramStructure.jsp" %>
		
    </body>
</html>