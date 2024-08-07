<%-- <!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!-->


<html class="no-js">
<!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<jsp:include page="jscss.jsp">
	<jsp:param value="Report for Available Seats" name="title" />
</jsp:include>

<body class="inside">

	<%@ include file="header.jsp"%>

	<section class="content-container login">
		<div class="container-fluid customTheme">

			<div class="row"><legend>Report for Available Seats</legend></div>
			
			<%@ include file="messages.jsp"%>

			<div class="row clearfix">
			<form:form  action="/exam/admin/examCenterCapacityReport" method="post" modelAttribute="studentMarks">
			<fieldset>
			<div class="col-md-6 column">

					<div class="form-group">
						<form:select id="writtenYear" path="year" type="text"	placeholder="Written Year" class="form-control"   itemValue="${studentMarks.year}">
							<form:option value="">Select Exam Year</form:option>
							<form:options items="${yearList}" />
						</form:select>
					</div>
					
					<div class="form-group">
						<form:select id="writtenMonth" path="month" type="text" placeholder="Written Month" class="form-control"  itemValue="${studentMarks.month}">
							<form:option value="">Select Exam Month</form:option>
							<form:option value="Apr">Apr</form:option>
							<form:option value="Jun">Jun</form:option>
							<form:option value="Sep">Sep</form:option>
							<form:option value="Dec">Dec</form:option>
						</form:select>
					</div>
					
					<div class="form-group">
					<button id="submit" name="submit" class="btn btn-large btn-primary"
						formaction="/exam/admin/examCenterCapacityReport">Generate</button>
						<button id="cancel" name="cancel" class="btn btn-danger" formaction="home" formnovalidate="formnovalidate">Cancel</button>
					</div>

				</div>
				
				</fieldset>
				</form:form>
				
		</div>

		<c:if test="${rowCount > 0}">
			<legend>&nbsp;Exam Bookings Report<font size="2px"> (${rowCount} Records Found) &nbsp; <a href="/exam/admin/downloadExamCenterCapacityReport">Download to Excel</a></font></legend>
		</c:if>
		</div>
	</section>

	<jsp:include page="footer.jsp" />


</body>
</html>
 --%>
 <!DOCTYPE html>
<html lang="en">
	
   <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%> 


    <jsp:include page="adminCommon/jscss.jsp">
	<jsp:param value="Report for Available Seats" name="title"/>
    </jsp:include>
    <body>
    
    	<%@ include file="adminCommon/header.jsp" %>
        <div class="sz-main-content-wrapper">
        
        	<jsp:include page="adminCommon/breadcrum.jsp">
			<jsp:param value="Exam;Report for Available Seats" name="breadcrumItems"/>
			</jsp:include>
        	
            
            <div class="sz-main-content menu-closed">
                    <div class="sz-main-content-inner">
              				<jsp:include page="adminCommon/left-sidebar.jsp">
								<jsp:param value="" name="activeMenu"/>
							</jsp:include>
              				
              				
              				<div class="sz-content-wrapper examsPage">
              						<%@ include file="adminCommon/adminInfoBar.jsp" %>
              						<div class="sz-content">
								
											<h2 class="red text-capitalize">Report for Available Seats</h2>
											<div class="clearfix"></div>
											<div class="panel-content-wrapper" style="min-height:450px;">
											<%@ include file="adminCommon/messages.jsp" %>
										<form:form  action="/exam/admin/examCenterCapacityReport" method="post" modelAttribute="studentMarks">
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
														formaction="/exam/admin/examCenterCapacityReport">Generate</button>
														<button id="cancel" name="cancel" class="btn btn-danger" formaction="home" formnovalidate="formnovalidate">Cancel</button>
													</div>
												</div>
											</fieldset>
										</form:form>
										<c:if test="${rowCount > 0}">
											<h2>&nbsp;Exam Bookings Report 
											<font size="2px">(${rowCount} Records Found) &nbsp; 
												<a href="/exam/admin/downloadExamCenterCapacityReport" style="color:blue;">Download to Excel</a>
											</font></h2>
											<div class="clearfix"></div>
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