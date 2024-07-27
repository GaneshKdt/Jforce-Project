<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%> 
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
 	<jsp:include page="adminCommon/jscss.jsp">
		<jsp:param value="Elective Report" name="title"/>
    </jsp:include>
</head>
<body>
	<%@ include file="adminCommon/header.jsp" %>
        <div class="sz-main-content-wrapper">
        
        	<jsp:include page="adminCommon/breadcrum.jsp">
				<jsp:param value="Exam;Elective Report" name="breadcrumItems"/>
			</jsp:include>
			
			<div class="sz-main-content menu-closed">
            	<div class="sz-main-content-inner">
              		<jsp:include page="adminCommon/left-sidebar.jsp">
						<jsp:param value="" name="activeMenu"/>
					</jsp:include>
              		
           				<div class="sz-content-wrapper examsPage">
        					<%@ include file="adminCommon/adminInfoBar.jsp" %>
        					<div class="sz-content">
        						<h2 class="red text-capitalize">Elective Completed Report</h2>
								<div class="clearfix"></div>
								
								<div class="panel-content-wrapper">
								<c:if test="${electiveCompleted == 'true' }">
									<%@ include file="adminCommon/messages.jsp"%>
								</c:if>
								<form:form method="post" modelAttribute="specialisation">
								<fieldset>
									<div class="col-md-4">
										<div class="form-group">
											<form:select path="nextYear" type="text" required="required" placeholder="Acad Year" class="form-control"   itemValue="${specialisation.acadYear}">
												<form:option value="">Select Next Acad Year</form:option>
												<form:options items="${yearList}" />
											</form:select>
										</div>
										
										<div class="form-group">
											<form:select path="nextMonth" type="text" required="required" placeholder="Acad Month" class="form-control"  itemValue="${specialisation.acadMonth}">
												<form:option value="">Select Next Acad Month</form:option>
												<form:options items="${monthList}" />
											</form:select>
										</div>
										
										<div class="form-group">
											<form:select path="term" type="text" required="required" placeholder="Select Term" class="form-control"  itemValue="${specialisation.term}">
												<form:option value="">Select Term</form:option>
												<form:option value="3">3/4</form:option>
												<form:option value="5">5</form:option>
											</form:select>
										</div>
										
										<div class="form-group">
											<form:select path="consumerProgramStructureId" type="text"  placeholder="Select Program Structure" class="form-control"  itemValue="${specialisationCompleteReport.consumerProgramStructureId}">
												<form:option value="">Select Program Structure</form:option>
												 <c:forEach items="${specializationTypeMasterkeys}" var="masterkey">
       												 <form:option value="${masterkey.key}">${masterkey.value}</form:option>
    											</c:forEach>
											</form:select>
										</div>
										
										<div class="form-group">
											<button name="submit" class="btn btn-large btn-primary" formaction="electiveReport">Generate Completed</button>
										</div>
										
									</div>
								</fieldset>
								</form:form>
								
								<c:if test="${electiveCompletedRowCount > 0}">
									<div class="panel-body">
										<h2>&nbsp;Elective Completed Report<font size="2px"> (${electiveCompletedRowCount} Records Found) &nbsp; <a href="downloadElectiveReport" style="color:blue;">Download to Excel</a></font></h2>
									</div>
								</c:if>
								
								</div>
        					</div>
        					
        					
        					<div class="sz-content">
        						<h2 class="red text-capitalize">Elective Pending Report</h2>
								<div class="clearfix"></div>
								
								<div class="panel-content-wrapper" style="min-height:450px;">
								<c:if test="${electivePending == 'true'}">
									<%@ include file="adminCommon/messages.jsp"%>
								</c:if>
								<form:form method="post" modelAttribute="specialisation">
								<fieldset>
									<div class="col-md-4">
										<div class="form-group">
											<form:select path="acadYear" type="text" required="required" placeholder="Acad Year" class="form-control"   itemValue="${specialisation.acadYear}">
												<form:option value="">Select Current Acad Year</form:option>
												<form:options items="${yearList}" />
											</form:select>
										</div>
										
										<div class="form-group">
											<form:select path="acadMonth" type="text" required="required" placeholder="Acad Month" class="form-control"  itemValue="${specialisation.acadMonth}">
												<form:option value="">Select Current Acad Month</form:option>
												<form:options items="${monthList}" />
											</form:select>
										</div>
										
										<div class="form-group">
											<form:select path="nextYear" type="text" required="required" placeholder="Acad Year" class="form-control"   itemValue="${specialisation.acadYear}">
												<form:option value="">Select Next Acad Year</form:option>
												<form:options items="${yearList}" />
											</form:select>
										</div>
										
										<div class="form-group">
											<form:select path="nextMonth" type="text" required="required" placeholder="Acad Month" class="form-control"  itemValue="${specialisation.acadMonth}">
												<form:option value="">Select Next Acad Month</form:option>
												<form:options items="${monthList}" />
											</form:select>
										</div>
										
										<div class="form-group">
											<form:select path="term" type="text" required="required" placeholder="Select Term" class="form-control"  itemValue="${specialisation.term}">
												<form:option value="">Select Term</form:option>
												<form:option value="3">3/4</form:option>
												<form:option value="5">5</form:option>
											</form:select>
										</div>
										
										<div class="form-group">
											<form:select path="consumerProgramStructureId" type="text" placeholder="Select Program Structure" class="form-control"  itemValue="${specialisationCompleteReport.consumerProgramStructureId}">
												<form:option value="">Select Program Structure</form:option>
												 <c:forEach items="${specializationTypeMasterkeys}" var="masterkey">
       												 <form:option value="${masterkey.key}">${masterkey.value}</form:option>
    											</c:forEach>
											</form:select>
										</div>
										
										<div class="form-group">
											<button name="submit" class="btn btn-large btn-primary" formaction="electivePendingReport">Generate Pending</button>
										</div>
										
									</div>
								</fieldset>
								</form:form>
								
								<c:if test="${electivePendingRowCount > 0}">
									<div class="panel-body">
										<h2>&nbsp;Elective Pending Report<font size="2px"> (${electivePendingRowCount} Records Found) &nbsp; <a href="downloadElectivePendingReport" style="color:blue;">Download to Excel</a></font></h2>
									</div>
								</c:if>
								
								</div>
        					</div>
        					
        					
        						<div class="sz-content">
        						<h2 class="red text-capitalize">Elective Completed Report (Prod)</h2>
								<div class="clearfix"></div>
								
								<div class="panel-content-wrapper" style="min-height:450px;">
								<c:if test="${electiveCompleteReport == 'true'}">
									<%@ include file="adminCommon/messages.jsp"%>
								</c:if>
								<form:form method="post" modelAttribute="specialisationCompleteReport">
								<fieldset>
									<div class="col-md-4">
										<div class="form-group">
											<form:select path="acadYear" type="text" required="required" placeholder="Acad Year" class="form-control"   itemValue="${specialisationCompleteReport.acadYear}">
												<form:option value="">Select Current Acad Year</form:option>
												<form:options items="${yearList}" />
											</form:select>
										</div>
										
										<div class="form-group">
											<form:select path="acadMonth" type="text" required="required" placeholder="Acad Month" class="form-control"  itemValue="${specialisationCompleteReport.acadMonth}">
												<form:option value="">Select Current Acad Month</form:option>
												<form:options items="${monthList}" />
											</form:select>
										</div>
										
									
										<div class="form-group">
											<form:select path="term" type="text" required="required" placeholder="Select Term" class="form-control"  itemValue="${specialisationCompleteReport.term}">
												<form:option value="">Select Term</form:option>
												<form:option value="3">3</form:option>
												<form:option value="4">4</form:option>
												<form:option value="5">5</form:option>
											</form:select>
										</div>
										
										<div class="form-group">
											<form:select path="consumerProgramStructureId" type="text" required="required" placeholder="Select Program Structure" class="form-control"  itemValue="${specialisationCompleteReport.consumerProgramStructureId}">
												<form:option value="">Select Program Structure</form:option>
												 <c:forEach items="${specializationTypeMasterkeys}" var="masterkey">
       												 <form:option value="${masterkey.key}">${masterkey.value}</form:option>
    											</c:forEach>
											</form:select>
										</div>
										
										<div class="form-group">
											<button name="submit" class="btn btn-large btn-primary" formaction="electiveCompleteProdReport">Generate Pending</button>
										</div>
										
									</div>
								</fieldset>
								</form:form>
								
								<c:if test="${electiveCompleteProdRowCount > 0}">
									<div class="panel-body">
										<h2>&nbsp;Elective Complete Report (Prod)<font size="2px"> (${electiveCompleteProdRowCount} Records Found) &nbsp; <a href="downloadElectiveCompleteProdReport" style="color:blue;">Download to Excel</a></font></h2>
									</div>
								</c:if>
								
								</div>
        					</div>
        					
           				</div>
              		</div>
             </div>
			
		</div>
		
	<jsp:include page="adminCommon/footer.jsp"/>
</body>
</html>