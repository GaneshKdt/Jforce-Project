<%-- <!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!-->


<html class="no-js">
<!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<jsp:include page="../jscss.jsp">
	<jsp:param value="Report for Exam Bookings" name="title" />
</jsp:include>

<body class="inside">

	<%@ include file="../header.jsp"%>

	<section class="content-container login">
		<div class="container-fluid customTheme">

			<div class="row"><legend>Report for PCP/VC  Bookings</legend></div>
			
			<%@ include file="../messages.jsp"%>

			<div class="row clearfix">
			<form:form  action="pcpRegistrationReport" method="post" modelAttribute="bean">
			<fieldset>
			<div class="col-md-6 column">

					<div class="form-group">
						<form:select id="writtenYear" path="year" type="text" required="required"	placeholder="Written Year" class="form-control"   itemValue="${bean.year}">
							<form:option value="">Select Acad Year</form:option>
							<form:options items="${yearList}" />
						</form:select>
					</div>
					
					<div class="form-group">
						<form:select id="writtenMonth" path="month" type="text" required="required" placeholder="Written Month" class="form-control"  itemValue="${bean.month}">
							<form:option value="">Select Acad Month</form:option>
							<form:option value="Jan">Jan</form:option>
							<form:option value="Jul">Jul</form:option>
						</form:select>
					</div>
					
					<div class="form-group">
					<button id="submit" name="submit" class="btn btn-large btn-primary"
						formaction="pcpRegistrationReport">Generate</button>
						<button id="cancel" name="cancel" class="btn btn-danger" formaction="home" formnovalidate="formnovalidate">Cancel</button>
					</div>

				</div>
				
				</fieldset>
				</form:form>
				
		</div>

		<c:if test="${rowCount > 0}">
			<legend>&nbsp;PCP/VC Bookings Report<font size="2px"> (${rowCount} Records Found) &nbsp; <a href="downloadPCPBookingReport">Download to Excel</a></font></legend>
		</c:if>
		</div>
	</section>

	<jsp:include page="../footer.jsp" />


</body>
</html>
 --%>
 
 
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
			<jsp:param value="Academics;Report for Exam Bookings" name="breadcrumItems"/>
			</jsp:include>
        	
            
            <div class="sz-main-content menu-closed">
                    <div class="sz-main-content-inner">
              				<jsp:include page="../adminCommon/left-sidebar.jsp">
								<jsp:param value="" name="activeMenu"/>
							</jsp:include>
              				
              				
              				<div class="sz-content-wrapper examsPage">
              						<%@ include file="../adminCommon/adminInfoBar.jsp" %>
              					<div class="sz-content">
								
									<h2 class="red text-capitalize">Report for Exam Bookings</h2>
									<div class="clearfix"></div>
									<div class="panel-content-wrapper" style="min-height:450px;">
										<%@ include file="../adminCommon/messages.jsp" %>
										<form:form  action="pcpRegistrationReport" method="post" modelAttribute="bean">
											<fieldset>
											<div class="col-md-6 column">

													<div class="form-group">
														<form:select id="writtenYear" path="year" type="text" required="required"	placeholder="Written Year" class="form-control"   itemValue="${bean.year}">
															<form:option value="">Select Acad Year</form:option>
															<form:options items="${yearList}" />
														</form:select>
													</div>
														
														<div class="form-group">
															<form:select id="writtenMonth" path="month" type="text" required="required" placeholder="Written Month" class="form-control"  itemValue="${bean.month}">
																<form:option value="">Select Acad Month</form:option>
																<form:option value="Jan">Jan</form:option>
																<form:option value="Jul">Jul</form:option>
															</form:select>
														</div>
														
														<div class="form-group">
														<button id="submit" name="submit" class="btn btn-large btn-primary"
															formaction="pcpRegistrationReport">Generate</button>
															<button id="cancel" name="cancel" class="btn btn-danger" formaction="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />home" formnovalidate="formnovalidate">Cancel</button>
														</div>
													
											 </div>
										 </fieldset>
										</form:form>
										<c:if test="${rowCount > 0}">
											<legend>&nbsp;PCP/VC Bookings Report<font size="2px"> (${rowCount} Records Found) &nbsp; <a href="/acads/admin/downloadPCPBookingReport">Download to Excel</a></font></legend>
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