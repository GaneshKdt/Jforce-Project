 <!DOCTYPE html>
<html lang="en">
	
  <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
    <jsp:include page="adminCommon/jscss.jsp">
	<jsp:param value="Report for Executive Exam Bookings" name="title"/>
    </jsp:include>
    
    
    
    <body>
    
    	<%@ include file="adminCommon/header.jsp" %>
        <div class="sz-main-content-wrapper">
        
        	<jsp:include page="adminCommon/breadcrum.jsp">
			<jsp:param value="Exam;Report for Executive Exam Bookings" name="breadcrumItems"/>
			</jsp:include>
        	
            
            <div class="sz-main-content menu-closed">
                    <div class="sz-main-content-inner">
              				<jsp:include page="adminCommon/left-sidebar.jsp">
								<jsp:param value="" name="activeMenu"/>
							</jsp:include>
              				
              				
              				<div class="sz-content-wrapper examsPage">
              						<%@ include file="adminCommon/adminInfoBar.jsp" %>
              					<div class="sz-content">
								
									<h2 class="red text-capitalize">Report for executive Exam Bookings</h2>
									<div class="clearfix"></div>
									<div class="panel-content-wrapper" style="min-height:450px;">
										<%@ include file="adminCommon/messages.jsp" %>
										<form:form  action="passGeneration" method="post" 
										modelAttribute="executiveBean">
											<fieldset>
											<div class="col-md-6 column">

														<div class="form-group">
						<form:select id="examYear" path="examYear" type="text" required="required"	placeholder="Acad Year" class="form-control"   itemValue="${executiveBean.examYear}">
							<form:option value="">Select Acad Year</form:option>
							<form:options items="${yearList}" />
						</form:select>
					</div>
    			
    			
    			<div class="form-group">
						<form:select id="examMonth" path="examMonth" type="text" required="required"	placeholder="Acad Month" class="form-control"   itemValue="${executiveBean.examMonth}">
							<form:option value="">Select Acad Month</form:option>
							<form:options items="${monthList}" />
						</form:select>
					</div>
														
														<div class="form-group">
														<button id="submit" name="submit" class="btn btn-large btn-primary"
															formaction="passGeneration">Generate</button>
															<button id="cancel" name="cancel" class="btn btn-danger" formaction="home" formnovalidate="formnovalidate">Cancel</button>
														</div>
													
											 </div>
										 </fieldset>
										</form:form>
										<c:if test="${rowCount > 0}">
											<legend>&nbsp;Executive Exam Bookings Report<font size="2px"> (${rowCount} Records Found) &nbsp; <a href="downloadReport">Download to Excel</a></font></legend>
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
 
