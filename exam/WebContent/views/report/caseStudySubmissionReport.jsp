<html lang="en">
	
   <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%> 


    <jsp:include page="../adminCommon/jscss.jsp">
	<jsp:param value="Report for Case Study Submission Status" name="title"/>
    </jsp:include>
    
    
    
    <body>
    
    	<%@ include file="../adminCommon/header.jsp" %>
        <div class="sz-main-content-wrapper">
        
        	<jsp:include page="../adminCommon/breadcrum.jsp">
			<jsp:param value="Exam;Report for Case Study Submission Status" name="breadcrumItems"/>
			</jsp:include>
        	
            
            <div class="sz-main-content menu-closed">
                    <div class="sz-main-content-inner">
              				<jsp:include page="../adminCommon/left-sidebar.jsp">
								<jsp:param value="" name="activeMenu"/>
							</jsp:include>
              				
              				
              				<div class="sz-content-wrapper examsPage">
              						<%@ include file="../adminCommon/adminInfoBar.jsp" %>
              						<div class="sz-content">
								
											<h2 class="red text-capitalize">Report for Case Study Submission Status</h2>
											<div class="clearfix"></div>
											<div class="panel-content-wrapper" style="min-height:150px;">
											<%@ include file="../adminCommon/messages.jsp" %>
									<form:form  action="/exam/admin/caseStudySubmissionReport" method="post" modelAttribute="caseStudyBean">
									<fieldset>
									<div class="col-md-4">
										<div class="form-group">
												<form:select id="batchYear" path="batchYear" type="text"	placeholder="Year" class="form-control" itemValue="${caseStudyBean.batchYear}">
													<form:option value="">Select Batch Year</form:option>
													<form:options items="${yearList}" />
												</form:select>
											</div>
											
											<div class="form-group">
												<form:select id="batchMonth" path="batchMonth" type="text" placeholder="Month" class="form-control"  itemValue="${caseStudyBean.batchMonth}">
													<form:option value="">Select Batch Month</form:option>
													<form:options items="${monthList}" />
												</form:select>
											</div>
											
											
											<div class="form-group">
											<button id="submit" name="submit" class="btn btn-large btn-primary"
												formaction="/exam/admin/caseStudySubmissionReport">Generate</button>
												
											<button id="cancel" name="cancel" class="btn btn-danger" 
												formaction="home" formnovalidate="formnovalidate">Cancel</button>
											</div>
											
										</div>
										<div class ="col-md-8">
										<c:choose>
									<c:when test="${rowCount > 0}">
									<h2>&nbsp;Submissions List 
									<font size="2px">(${rowCount} Records Found) &nbsp; 
										<a href="/exam/admin/downloadCaseStudySubmissionsReport?year=${caseStudyBean.batchYear}&month=${caseStudyBean.batchMonth}">Download to Excel</a>&nbsp; &nbsp; &nbsp; 
										
									</font></h2>
									<div class="clearfix"></div>
												<br>
										</c:when>
										
										</c:choose>
										</div>
										</fieldset>
										</form:form>
											 </div>
		
              						</div>
              				   </div>
    				       </div>
			           </div>
		           </div>
        <jsp:include page="../adminCommon/footer.jsp"/>
    </body>
</html>