<!DOCTYPE html>
<html lang="en">
	
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%> 
<jsp:include page="../adminCommon/jscss.jsp">
<jsp:param value="UG Student Consent Form" name="title"/>
</jsp:include>
<body>
    
<%@ include file="../adminCommon/header.jsp" %>
<div class="sz-main-content-wrapper">
<jsp:include page="../adminCommon/breadcrum.jsp">
<jsp:param value="Exam;UG Student Consent Form" name="breadcrumItems"/>
</jsp:include>
     
     <div class="sz-main-content menu-closed">
          <div class="sz-main-content-inner">
              <jsp:include page="../adminCommon/left-sidebar.jsp">
					<jsp:param value="" name="activeMenu"/>
				</jsp:include>
              		
              	<div class="sz-content-wrapper examsPage">
              		<%@ include file="../adminCommon/adminInfoBar.jsp" %>
              	<div class="sz-content">
								
				<h2 class="red text-capitalize">Ug Student Consent Form Report</h2>
				<div class="clearfix"></div>
				<div class="panel-content-wrapper" style="min-height:450px;">
				<%@ include file="../adminCommon/messages.jsp" %>
				<form:form  action="/exam/admin/ugConsentReport" method="post" modelAttribute="ugStudent">
				<fieldset>
					<div class="col-md-4">
						<%-- <div class="form-group">
						<form:select id="consent_option" path="consent_option" type="text"	placeholder="option" class="form-control " 
							itemValue="${ugStudent.consent_option}">
							<form:option value="">Select Option</form:option>
							<form:option value="1">Opt for the 6-month Certificate Program. At the end of the 6-month cycle, opt for lateral admission/program upgrade to Semester 2 of (var-Bcom/BBA) in January 2023 academic cycle. This option will ensure continuity and overall duration of the program remains as is.</form:option>
							<form:option value="2">Opt out of the current academic cycle completely & request for transfer of admission to the next admission intake (January 2023) for (var-Bcom/BBA) program. This means your program start date will be January 2023.</form:option>
							<form:option value="3">Opt for 6-month Certificate Program only until its completion and then have the freedom of decision for your next steps</form:option>
							<form:option value="4">Admission Cancellation You may cancel your admission and get a full refund of the fee paid. </form:option>
						</form:select>
					</div> --%>
				<div class="form-group">
						<form:select id="program" path="program" type="text"	placeholder="program" class="form-control " 
							itemValue="${searchBean.program}">
							<form:option value="'BBA','B.com','BBA-BA'">Select Program</form:option>
							<form:option value="'BBA'">BBA</form:option>
							<form:option value="'B.Com'">B.Com</form:option>
							<form:option value="'BBA-BA'">BBA-BA</form:option>
						</form:select>
					</div>
													
					<div class="form-group">
						<form:select id="type" path="type" type="text"	placeholder="type" class="form-control " required="true" 
							itemValue="${searchBean.type}">
							<form:option value="">Select Student Option Status</form:option>
							<form:option value="Submitted">Option Submitted Student</form:option>
							<form:option value="Pending">Option Pending Student</form:option>
						</form:select>
					</div>
													
					<div class="form-group">
						<button id="submit" name="submit" class="btn btn-large btn-primary" formaction="/exam/admin/ugConsentReport">Generate</button>
						<button id="cancel" name="cancel" class="btn btn-danger" formaction="/exam/home" formnovalidate="formnovalidate">Cancel</button>
					</div>
			</div>
	</fieldset>
</form:form>
<c:if test="${rowCount > 0}">
	<h2>&nbsp;Ug Student ${type} option report
	<font size="2px">(${rowCount} Records Found) &nbsp; 
	<a href="/exam/admin/downloadUGConsentReport" style="color:blue;">Download to Excel</a>
	</font></h2>
											
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