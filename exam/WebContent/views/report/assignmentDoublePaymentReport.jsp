<!DOCTYPE html>
<html lang="en">
	
   <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%> 


    <jsp:include page="../adminCommon/jscss.jsp">
	<jsp:param value="Report for Assignment Double Payments" name="title"/>
    </jsp:include>
    
    
    
    <body>
    
    	<%@ include file="../adminCommon/header.jsp" %>
        <div class="sz-main-content-wrapper">
        
        	<jsp:include page="../adminCommon/breadcrum.jsp">
			<jsp:param value="Exam;Report for Assignment Double Payments" name="breadcrumItems"/>
			</jsp:include>
        	
            
            <div class="sz-main-content menu-closed">
                    <div class="sz-main-content-inner">
              				<jsp:include page="../adminCommon/left-sidebar.jsp">
								<jsp:param value="" name="activeMenu"/>
							</jsp:include>
              				
              				
              				<div class="sz-content-wrapper examsPage">
              						<%@ include file="../adminCommon/adminInfoBar.jsp" %>
              						<div class="sz-content">
								
											<h2 class="red text-capitalize">Report for Assignment Double Payments</h2>
											<div class="clearfix"></div>
											<div class="panel-content-wrapper" style="min-height:450px;">
											<%@ include file="../adminCommon/messages.jsp" %>
										<form:form  action="/exam/admin/assignmentPaymentReport" method="post" modelAttribute="studentMarks">
											<fieldset>
												<div class="col-md-4">
													<div class="form-group">
															<form:select id="writtenYear" path="year" type="text"	placeholder="Written Year" class="form-control"   itemValue="${studentMarks.year}">
																<form:option value="">Select Written Year</form:option>
																<form:options items="${yearList}" />
															
															</form:select>
													</div>
													<div class="form-group">
														<form:select id="writtenMonth" path="month" type="text" required="required" placeholder="Written Month" class="form-control"  itemValue="${studentMarks.month}">
															<form:option value="">Select Exam Month</form:option>
															<form:option value="Apr">Apr</form:option>
															<form:option value="Jun">Jun</form:option>
															<form:option value="Sep">Sep</form:option>
															<form:option value="Dec">Dec</form:option>
														</form:select>
													</div>
													
													<div class="form-group">
													<button id="submit" name="submit" class="btn btn-large btn-primary"
														formaction="assignmentPaymentReportDoublePaymentsRecieved">Generate</button>
														<button id="cancel" name="cancel" class="btn btn-danger" formaction="home" formnovalidate="formnovalidate">Cancel</button>
													</div>
												</div>
											</fieldset>
										</form:form>
										<c:if test="${rowCount > 0}">
											<h2>&nbsp;Assignment Double Payment Report 
											<font size="2px">(${rowCount} Records Found) &nbsp; 
												<a href="downloadAssignmentDoublePaymentReport" style="color:blue;">Download to Excel</a>
											</font></h2>
											<div class="clearfix"></div>
										</c:if>
									</div>
										
              						</div>
              				   </div>
    				       </div>
			           </div>
		           </div>
        <jsp:include page="../adminCommon/footer.jsp"/>
    </body>
</html>