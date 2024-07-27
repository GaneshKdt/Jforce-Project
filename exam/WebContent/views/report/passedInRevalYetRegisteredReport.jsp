<!DOCTYPE html>
<html lang="en">
	
   <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%> 
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

    <jsp:include page="../adminCommon/jscss.jsp">
	<jsp:param value="Passed In Reval Yet Registered Report" name="title"/>
    </jsp:include>
    
    
    
    <body>
    
    	<%@ include file="../adminCommon/header.jsp" %>
        <div class="sz-main-content-wrapper">
        
        	<jsp:include page="../adminCommon/breadcrum.jsp">
			<jsp:param value="Student Zone;Passed In Reval Yet Registered Report" name="breadcrumItems"/>
			</jsp:include>
        	
            
       	<div class="sz-main-content menu-closed">
          	<div class="sz-main-content-inner">
           		<jsp:include page="../adminCommon/left-sidebar.jsp">
					<jsp:param value="" name="activeMenu"/>
				</jsp:include>
              		
				<div class="sz-content-wrapper examsPage" style="min-height:900px;">
        			<%@ include file="../adminCommon/adminInfoBar.jsp" %>
        				<div class="sz-content">
		
							<h2 class="red text-capitalize">Students Passed In Reval Yet Registered Report</h2>
							<div class="clearfix"></div>
							<div class="panel-content-wrapper" >
								<%@ include file="../adminCommon/messages.jsp" %>
								<form:form  action="/exam/admin/passedInRevalYetRegisteredReport" method="post" modelAttribute="searchBean">
									<fieldset>
										<div class="col-md-4">
											<div class="form-group">
												<form:select id="writtenYear" path="year" type="text"	placeholder="Year" class="form-control" itemValue="${marksBean.year}">
													<form:option value="">Select Passed Exam Year</form:option>
													<form:options items="${year}" />
												</form:select>
											</div>
											
											<div class="form-group">
												<form:select id="writtenMonth" path="month" type="text" placeholder="Month" class="form-control"  itemValue="${marksBean.month}">
													<form:option value="">Select Passed Exam Month</form:option>
													<form:options items="${month}" />
												</form:select>
											</div>
											
											<div class="form-group">
												<form:select id="writtenYear" path="bookedYear" type="text"	placeholder="Year" class="form-control" itemValue="${marksBean.bookedYear}">
													<form:option value="">Select Booked Exam Year</form:option>
													<form:options items="${year}" />
												</form:select>
											</div>
											
											<div class="form-group">
												<form:select id="writtenMonth" path="bookedMonth" type="text" placeholder="Month" class="form-control"  itemValue="${marksBean.bookedMonth}">
													<form:option value="">Select Booked Exam Month</form:option>
													<form:options items="${month}" />
												</form:select>
											</div>
											
											
											<div class="form-group">
											<button id="submit" name="submit" class="btn btn-large btn-primary"
												formaction="/exam/admin/passedInRevalYetRegisteredReport">Generate</button>
												
												<button id="cancel" name="cancel" class="btn btn-danger" formaction="home" formnovalidate="formnovalidate">Cancel</button>
											</div>
											
										</div>
										
									</fieldset>
								</form:form>
							</div>
								
							
								<c:if test="${rowCount > 0}">

									<h2>&nbsp;Student Details  
									<font size="2px">(${rowCount} Records Found)&nbsp; <a href="/exam/admin/downloadPassedInRevalYetRegisteredReport">Download to Excel</a></font></h2>
									<div class="clearfix"></div>
									
								</c:if>
							
              						</div>
              				   </div>
    				       </div>
			           </div>
		           </div>
        <jsp:include page="../adminCommon/footer.jsp"/>
    </body>
</html>