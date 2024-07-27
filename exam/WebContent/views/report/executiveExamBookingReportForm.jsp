
 <!DOCTYPE html>
<html lang="en">
	
   <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%> 


    <jsp:include page="../adminCommon/jscss.jsp">
	<jsp:param value="Report for Exam Bookings" name="title"/>
    </jsp:include>
    
    
    
    <body>
    
    	<%@ include file="../adminCommon/header.jsp" %>
        <div class="sz-main-content-wrapper">
        
        	<jsp:include page="../adminCommon/breadcrum.jsp">
			<jsp:param value="Exam;Report for Executive Exam Bookings" name="breadcrumItems"/>
			</jsp:include>
        	
            
            <div class="sz-main-content menu-closed">
                    <div class="sz-main-content-inner">
              				<jsp:include page="../adminCommon/left-sidebar.jsp">
								<jsp:param value="" name="activeMenu"/>
							</jsp:include>
              				
              				
              				<div class="sz-content-wrapper examsPage">
              						<%@ include file="../adminCommon/adminInfoBar.jsp" %>
              						<div class="sz-content">
								
											<h2 class="red text-capitalize">Report for Executive Exam Bookings</h2>
											<div class="clearfix"></div>
											<div class="panel-content-wrapper" style="min-height:450px;">
											<%@ include file="../adminCommon/messages.jsp" %>
										<form:form  action="executiveExamBookingReport" method="post" modelAttribute="searchBean">
											<fieldset>
												<div class="col-md-4">
													<div class="form-group">
															<form:select id="enrollmentYear" path="enrollmentYear" type="text"	placeholder="Batch Year" class="form-control"   itemValue="${searchBean.year}">
																<form:option value="">Select Batch Year</form:option>
																
															<form:option value="2018">2018</form:option>
															<form:option value="2019">2019</form:option>
															<form:option value="2020">2020</form:option>
															<form:option value="2021">2021</form:option>
															</form:select>
													</div>
													<div class="form-group">
														<form:select  path="enrollmentMonth" type="text" required="required" placeholder="Batch Month" class="form-control"  itemValue="${searchBean.month}">
															<form:option value="">Select Batch Month</form:option>
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
															<form:select id="writtenYear" path="year" type="text"	placeholder="Year" class="form-control"   itemValue="${searchBean.year}">
																<form:option value="">Select Exam Year</form:option>
																
															<form:option value="2018">2018</form:option>
															<form:option value="2019">2019</form:option>
															<form:option value="2020">2020</form:option>
															<form:option value="2021">2021</form:option>
															</form:select>
													</div>
													<div class="form-group">
														<form:select  path="month" type="text" required="required" placeholder="Month" class="form-control"  itemValue="${searchBean.month}">
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
													<div class="form-group">
													<button id="submit" name="submit" class="btn btn-large btn-primary"
														formaction="executiveExamBookingReport">Generate</button>
														<button id="cancel" name="cancel" class="btn btn-danger" formaction="home" formnovalidate="formnovalidate">Cancel</button>
													</div>
												</div>
											</fieldset>
										</form:form>
										<c:if test="${rowCount > 0}">
											<h2>&nbsp;Exam Bookings Report 
											<font size="2px">(${rowCount} Records Found) &nbsp; 
												<a href="downloadExecutiveExamBookingReport" style="color:blue;">Download to Excel</a>
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