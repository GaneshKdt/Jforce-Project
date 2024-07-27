<!DOCTYPE html>
<html lang="en">
	
	
	<%@page import="com.nmims.beans.ExamCenterSlotMappingBean"%>
   <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%> 


    <jsp:include page="adminCommon/jscss.jsp">
	<jsp:param value="Report for Pending Exam Bookings" name="title"/>
    </jsp:include>
    
    
    
    <body>
    
    	<%@ include file="adminCommon/header.jsp" %>
        <div class="sz-main-content-wrapper">
        
        	<jsp:include page="adminCommon/breadcrum.jsp">
			<jsp:param value="Exam;Report for Pending Exam Bookings" name="breadcrumItems"/>
			</jsp:include>
        	
            
            <div class="sz-main-content menu-closed">
                    <div class="sz-main-content-inner">
              				<jsp:include page="adminCommon/left-sidebar.jsp">
								<jsp:param value="" name="activeMenu"/>
							</jsp:include>
              				
              				
              				<div class="sz-content-wrapper examsPage">
              						<%@ include file="adminCommon/adminInfoBar.jsp" %>
              						<div class="sz-content">
											<h2 class="red text-capitalize">REPORT FOR PENDING EXAM BOOKINGS</h2>
											<div class="clearfix"></div>
								<div class="panel-content-wrapper" style="min-height:450px;">
									<form:form  action="/exam/admin/examBookingPendingReportExecutive" method="post" modelAttribute="studentMarks">
												<fieldset>
												<div class="col-md-4">
												
														<div class="form-group">
															<form:select id="enrollmentYear" path="enrollmentYear" type="text"	placeholder="Batch Year" class="form-control"   itemValue="${studentMarks.year}">
																<form:option value="">Select Batch Year</form:option>
																<form:options items="${yearList}" />
																
															</form:select>
														</div>
														
														<div class="form-group">
															<form:select id="enrollmentMonth" path="enrollmentMonth" type="text" placeholder="Batch Month" class="form-control"  itemValue="${studentMarks.month}">
																<form:option value="">Select Batch Month</form:option>
																<form:options items="${monthList}" />
															</form:select>
														</div>

														<div class="form-group">
															<form:select id="writtenYear" path="year" type="text" required="required"	placeholder="Written Year" class="form-control"   itemValue="${studentMarks.year}">
																<form:option value="">Select Exam Year</form:option>
																<form:options items="${yearList}" />
																
															</form:select>
														</div>
														
														<div class="form-group">
															<form:select id="writtenMonth" path="month" type="text" required="required" placeholder="Written Month" class="form-control"  itemValue="${studentMarks.month}">
																<form:option value="">Select Exam Month</form:option>
																<form:options items="${monthList}" />
															</form:select>
														</div>
														
														
														<div class="form-group">
														<button id="submit" name="submit" class="btn btn-large btn-primary"
															formaction="/exam/admin/examBookingPendingReportExecutive">Generate</button>
															<button id="cancel" name="cancel" class="btn btn-danger" formaction="home" formnovalidate="formnovalidate">Cancel</button>
														</div>
													</div>
												</fieldset>
									</form:form>
									<c:if test="${rowCount > 0}">
										<h2>&nbsp;Pending Exam Bookings Report<font size="2px"> (${rowCount} Records Found) &nbsp; <a href="/exam/admin/downloadExamBookingPendingReport" style="color:blue;">Download to Excel</a></font></h2>
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