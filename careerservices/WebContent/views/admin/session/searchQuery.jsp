
<!DOCTYPE html>


<html lang="en">
	
 <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
    <jsp:include page="/views/adminCommon/jscss.jsp">
	<jsp:param value="Report for Attendance & Feedback" name="title"/>
    </jsp:include>
    
    
    
    <body>
    
    	<jsp:include page="/views/adminCommon/header.jsp"/>
        <div class="sz-main-content-wrapper">
        
        	<jsp:include page="/views/adminCommon/breadcrum.jsp">
			<jsp:param value="Academics;Report for Query & Answer" name="breadcrumItems"/>
			</jsp:include>
        	
            
            <div class="sz-main-content menu-closed">
                    <div class="sz-main-content-inner">
              				<jsp:include page="/views/adminCommon/left-sidebar.jsp">
								<jsp:param value="" name="activeMenu"/>
							</jsp:include>
              				
              				
              				<div class="sz-content-wrapper examsPage">
              						<jsp:include page="/views/adminCommon/adminInfoBar.jsp"/>
              					<div class="sz-content">
								
									<h2 class="red text-capitalize">Report for Query & Answer</h2>
									<div class="clearfix"></div>
									<div class="panel-content-wrapper" style="min-height:450px;">
										<jsp:include page="/views/adminCommon/messages.jsp"/>
										<form:form  action="searchQueriesList" method="post" modelAttribute="searchBean">
											<fieldset>
											<div class="col-md-4 column">

													<div class="form-group">
														<form:select id="writtenYear" path="year"  required="required"	class="form-control"   itemValue="${searchBean.year}">
															<form:option value="">Select Academic Year</form:option>
															<form:options items="${yearList}" />
														</form:select>
													</div>
													
													<div class="form-group">
														<form:select id="writtenMonth" path="month"  required="required"  class="form-control"  itemValue="${searchBean.month}">
															<form:option value="">Select Academic Month</form:option>
															<form:option value="Jan">Jan</form:option>
															<form:option value="Jul">Jul</form:option>
														</form:select>
													</div>
													
												</div>
												
												
												
												<div class="col-md-4 column">

													<div class="form-group" style="overflow:visible;">
															<form:select id="subject" path="subject"  class="combobox form-control"  itemValue="${searchBean.subject}" > 
																<form:option value="">Type OR Select Subject</form:option>
																<form:options items="${subjectList}" />
															</form:select>
													</div>
													
													<div class="form-group">
														<form:select id="isAnswered" path="isAnswered"  required="required"  class="form-control"  itemValue="${searchBean.isAnswered}">
															<form:option value="">Select Status</form:option>
															<form:option value="Y">Answered</form:option>
															<form:option value="N">Not Answered</form:option>
														</form:select>
													</div>
													
												<div class="form-group">
													<button id="submit" name="submit" class="btn btn-large btn-primary" formaction="searchQueries">Generate</button>
														<button id="cancel" name="cancel" class="btn btn-danger" formaction="home" formnovalidate="formnovalidate">Cancel</button>
												</div>
													

												</div>
												
												</fieldset>
										</form:form>
										<c:if test="${rowCount > 0}">
											<legend>&nbsp;Query & Answer Report<font size="2px"> (${rowCount} Records Found) &nbsp; <a href="downloadQueries">Download to Excel</a></font></legend>
										</c:if>

									 </div>
              								
              					</div>
              				</div>
    				</div>
			</div>
		</div>
        <jsp:include page="/views/adminCommon/footer.jsp"/>
        
		
    </body>
</html>