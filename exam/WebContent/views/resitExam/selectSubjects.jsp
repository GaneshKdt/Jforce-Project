<%-- <!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!-->

<%@page import="com.nmims.beans.Person"%>
<%@page import="com.nmims.beans.Page"%>
<%@page import="com.nmims.beans.StudentMarksBean"%>
<%@page import="com.nmims.beans.ProgramSubjectMappingBean"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.nmims.beans.StudentBean"%>

<html class="no-js">
<!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<jsp:include page="../jscss.jsp">
	<jsp:param value="Select subjets for Exam" name="title" />
</jsp:include>

<script language="JavaScript">
	function validateForm() {
		
		var assignmentSubmittedList = document.getElementsByName('applicableSubjects');
		var atleastOneSelected = false;
		for(var i = 0; i < assignmentSubmittedList.length; ++i)
		{
		    if(assignmentSubmittedList[i].checked){
		    	atleastOneSelected = true;
		    	break;
		    }
		}
		if(!atleastOneSelected){
			alert("Please select at least one subject to proceed.");
			return false;
		}
		
		for(var i = 0; i < assignmentSubmittedList.length; ++i)
		{
		    if(!assignmentSubmittedList[i].checked){
		    	return confirm('You have not selected some of the subjects for exam. Are you sure you want to proceed to next step?');
		    }
		}
		
		return true;
	}
	
	
	
	function validateFreeSubjectsForm() {
		
		var assignmentSubmittedList = document.getElementsByName('freeApplicableSubjects');
		var atleastOneSelected = false;
		for(var i = 0; i < assignmentSubmittedList.length; ++i)
		{
		    if(assignmentSubmittedList[i].checked){
		    	atleastOneSelected = true;
		    	break;
		    }
		}
		if(!atleastOneSelected){
			alert("Please select at least one subject to proceed.")
			return false;
		}
		document.getElementById("subjectForm").action = 'selectResitExamCenterForFree';
		document.getElementById("subjectForm").novalidate = 'novalidate';
		for(var i = 0; i < assignmentSubmittedList.length; ++i)
		{
		    if(!assignmentSubmittedList[i].checked){
		    	return submit =  confirm('You have not selected some of the subjects for exam. Are you sure you want to proceed to next step?');
		    }
		}
		
		
		document.getElementById("subjectForm").submit();
		//document.forms["subjectForm"].submit();
		//window.location.href='selectExamCenterForFree';
		//return true;
	}
</script>

<body class="inside">

	<%@ include file="../header.jsp"%>
	<%
	ArrayList<ProgramSubjectMappingBean> applicableSubjectsList = (ArrayList<ProgramSubjectMappingBean>)session.getAttribute("applicableSubjectsList");
	
	StudentBean student = (StudentBean)session.getAttribute("student");
	String firstName = student.getFirstName();
	String lastName = student.getLastName();
	String sapId = student.getSapid();
	%>
	<section class="content-container login">
		<div class="container-fluid customTheme">
		

			<div class="row clearfix">
					<legend>Select subjects for ${mostRecentTimetablePeriod} Exam : Applicable Subjects  (${applicableSubjectsListCount}) </legend>
			</div>
			
			<%@ include file="../messages.jsp"%>
			<div class="panel-body">
			
			<div class="col-md-18 column">
				
				<c:choose>
				<c:when test="${applicableSubjectsListCount > 0}">
				<div>
					<div class="table-responsive">
					<form:form id="subjectForm" action="selectResitExamCenter" method="post" modelAttribute="examBooking" >
						<fieldset>
						<input type="hidden"  name = "month" value = "${examBooking.month }"/>
						<input type="hidden"  name = "year" value = "${examBooking.year }"/>
					
						<table class="table table-striped" style="font-size:12px">
						<thead>
							<tr> 
								<th>Sr. No.</th>
								<th>Program</th>
								<th>Sem</th>
								<th>Subject</th>
								<th>Assignment Submitted</th>
								<th>Booking Status</th>
								<th style="text-align:center">Select</th>
								<th>Actions Permitted</th>
								<th>Exam Center Booked</th>
					
							</tr>
						</thead>
						<tbody>
						
						<% int count = 0; 
						final String DD_APPROVED = "DD Approved";
						final String NOT_BOOKED = "Not Booked";
						final String ONLINE_PAYMENT_MANUALLY_APPROVED = "Online Payment Manually Approved"; 
						final String FEE_IN_ADMISSION = "Exam Fees part of Registration Fees/Exam Fees Exempted";
						final String SEAT_RELEASED = "Seat Released";
						final String BOOKED = "Booked";
						
						for(int i = 0; i < applicableSubjectsList.size() ; i++){
							ProgramSubjectMappingBean bean = applicableSubjectsList.get(i);
							count++;
							String studentProgram = bean.getProgram();
							String sem = bean.getSem();
							String subject = bean.getSubject();
							String assignmentSubmitted = bean.getAssignmentSubmitted();
							String bookingStatus = bean.getBookingStatus();
							String canBook = bean.getCanBook();
							String canFreeBook = bean.getCanFreeBook();
							String bookingStatusForDisplay = bookingStatus;
							String centerName = bean.getCenterName();
							if(ONLINE_PAYMENT_MANUALLY_APPROVED.equals(bookingStatus)){
								bookingStatusForDisplay = "Approved";
							}
						 %>
						
						 	<tr>
					            <td><c:out value="<%=count %>"/></td>
					            <td><c:out value="<%=studentProgram %>"/></td>
					            <td><c:out value="<%=sem %>"/></td>
								<td nowrap="nowrap"><c:out value="<%=subject %>"/></td>
								<td><c:out value="<%=assignmentSubmitted %>"/></td>
								<td><c:out value="<%=bookingStatusForDisplay %>"/></td>
								<td>
								<%if("Yes".equals(canBook)){ %>
									<form:checkbox path="applicableSubjects" value="<%=subject %>"  />
								<%}else if("Yes".equals(canFreeBook)){ %>
									<form:checkbox path="freeApplicableSubjects" value="<%=subject %>"  />
								<%} %>
					            </td>
					            
					            <td>
								<%if(DD_APPROVED.equals(bookingStatus)){ %>
									Select Exam Center
								<%}else if(ONLINE_PAYMENT_MANUALLY_APPROVED.equals(bookingStatus)){ %>
									Select Exam Center 
								<%}else if(NOT_BOOKED.equals(bookingStatus)){ %>
								Select Exam Center & Proceed to Pay
								<%}else if(FEE_IN_ADMISSION.equals(bookingStatus)){ %>
								Select Exam Center - No Charges
								<%}else if(SEAT_RELEASED.equals(bookingStatus)){ %>
								Change Exam Center
								<%} %>
					            </td>
					            <td>
					            <%if(BOOKED.equals(bookingStatus)){ %>
					            	<c:out value="<%=centerName %>"/>
					            <%} %>&nbsp;
					            </td>
					        </tr>   
						        
						<%} %>
							
						</tbody>
					</table>
					
					<c:url value="selectResitExamCenterForOnline" var="selectResitExamCenterForOnlineUrl">
					  <c:param name="year" value="${examBooking.year }" />
					  <c:param name="month" value="${examBooking.month }" />
					</c:url>
					
					<c:url value="selectResitExamCenterForRelesedSubjects" var="selectResitExamCenterForRelesedSubjectsUrl">
					  <c:param name="year" value="${examBooking.year }" />
					  <c:param name="month" value="${examBooking.month }" />
					</c:url>
					
					<c:url value="selectResitExamCenterForRelesedNoChargeSubjects" var="selectResitExamCenterForRelesedNoChargeSubjectsUrl">
					  <c:param name="year" value="${examBooking.year }" />
					  <c:param name="month" value="${examBooking.month }" />
					</c:url>
					
					<div class="form-group">
						<label class="control-label" for="submit"></label>
						<div class="controls">
						
						<c:if test="${isExamRegistraionLive == 'true' }">
						
								<c:choose><c:when test="${subjectsToPay > 0}">
									<button id="submit" name="submit" class="btn btn-large btn-primary" onclick="return validateForm();">Select Exam Center & Proceed to Pay</button>
								</c:when></c:choose>
								
								<%if("true".equals((String)request.getAttribute("hasApprovedOnlineTransactions"))){ %>
									<button id="cancel" name="cancel" class="btn btn-primary" onclick="window.location.href='${selectResitExamCenterForOnlineUrl}'" type="button" formnovalidate="formnovalidate">Select Exam Center for Online Payment Already Made</button>
								<%} %>
								<%if("true".equals((String)request.getAttribute("hasFreeSubjects"))){ %>
									<button id="cancel" name="cancel" class="btn btn-primary" onclick="return validateFreeSubjectsForm();"  formnovalidate="formnovalidate">Select Exam Center - No Charges</button>
								<%} %>
								<%if("true".equals((String)request.getAttribute("hasReleasedSubjects"))){ %>
									<button id="cancel" name="cancel" class="btn btn-primary" type="button" onclick="window.location.href='${selectResitExamCenterForRelesedSubjectsUrl}'" formnovalidate="formnovalidate">Change Exam Center</button>
								<%} %>
								<%if("true".equals((String)request.getAttribute("hasReleasedNoChargeSubjects"))){ %>
									<button id="cancel" name="cancel" class="btn btn-primary" type="button" onclick="window.location.href='${selectResitExamCenterForRelesedNoChargeSubjectsUrl}'" formnovalidate="formnovalidate">Change Exam Center For No Fees</button>
								<%} %>
								
						</c:if>
						<button id="cancel" name="cancel" type="button" class="btn btn-danger" onclick="window.location.href='/studentportal/home'" formnovalidate="formnovalidate">Back</button>
						</div>
					</div>
				</fieldset>
				</form:form>
				</div>
				</div>
			</c:when>
				<c:otherwise>
					<button id="cancel" name="cancel" type="button" class="btn btn-danger" onclick="window.location.href='/studentportal/home'" formnovalidate="formnovalidate">Back</button>
				</c:otherwise>
			</c:choose>
			</div>
			
						
			</div>
			
			<div>
			<p align="justify">
				<b>Note:</b> 
				You are allowed to book for an exam only if you have submitted the internal assignment in current or previous attempts.
				Please verify your assignment submitted status in table above. 
				</p>
			</div>		
			
			
			
		
		
		
		
		</div>
	</section>




	<jsp:include page="../footer.jsp" />
</body>
</html>
 --%>
 
 
 
 <!DOCTYPE html>
<%@page import="com.nmims.beans.StudentMarksBean"%>
<%@page import="com.nmims.beans.ProgramSubjectMappingExamBean"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.nmims.beans.StudentExamBean"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<html lang="en">
    
	<%
	ArrayList<ProgramSubjectMappingExamBean> applicableSubjectsList = (ArrayList<ProgramSubjectMappingExamBean>)session.getAttribute("applicableSubjectsList");
	
	StudentExamBean student = (StudentExamBean)session.getAttribute("studentExam");
	String firstName = student.getFirstName();
	String lastName = student.getLastName();
	String sapId = student.getSapid();
	%>
	
    
    <jsp:include page="../common/jscss.jsp">
	<jsp:param value="Select subjects for Exam" name="title"/>
    </jsp:include>
    
    
	<script language="JavaScript">
		function validateForm() {
			
			var assignmentSubmittedList = document.getElementsByName('applicableSubjects');
			var atleastOneSelected = false;
			for(var i = 0; i < assignmentSubmittedList.length; ++i)
			{
			    if(assignmentSubmittedList[i].checked){
			    	atleastOneSelected = true;
			    	break;
			    }
			}
			if(!atleastOneSelected){
				alert("Please select at least one subject to proceed.");
				return false;
			}
			
			for(var i = 0; i < assignmentSubmittedList.length; ++i)
			{
			    if(!assignmentSubmittedList[i].checked){
			    	return confirm('You have not selected some of the subjects for exam. Are you sure you want to proceed to next step?');
			    }
			}
			
			return true;
		}
		
		
		
		function validateFreeSubjectsForm() {
			
			var assignmentSubmittedList = document.getElementsByName('freeApplicableSubjects');
			var atleastOneSelected = false;
			for(var i = 0; i < assignmentSubmittedList.length; ++i)
			{
			    if(assignmentSubmittedList[i].checked){
			    	atleastOneSelected = true;
			    	break;
			    }
			}
			if(!atleastOneSelected){
				alert("Please select at least one subject to proceed.")
				return false;
			}
			document.getElementById("subjectForm").action = 'selectResitExamCenterForFree';
			document.getElementById("subjectForm").novalidate = 'novalidate';
			for(var i = 0; i < assignmentSubmittedList.length; ++i)
			{
			    if(!assignmentSubmittedList[i].checked){
			    	return submit =  confirm('You have not selected some of the subjects for exam. Are you sure you want to proceed to next step?');
			    }
			}
			
			
			document.getElementById("subjectForm").submit();
			//document.forms["subjectForm"].submit();
			//window.location.href='selectExamCenterForFree';
			//return true;
		}
	</script>
	    
    
    <body>
    
    	<%@ include file="../common/header.jsp" %>
    	
    	
        
        <div class="sz-main-content-wrapper">
        
        	<jsp:include page="../common/breadcrum.jsp">
			<jsp:param value="Student Zone;Exams;Resit-Exam Registration" name="breadcrumItems"/>
			</jsp:include>
        	
            
            <div class="sz-main-content menu-closed">
                    <div class="sz-main-content-inner">
              				<jsp:include page="../common/left-sidebar.jsp">
								<jsp:param value="Resit-Exam Registration" name="activeMenu"/>
							</jsp:include>
              				
              				
              				<div class="sz-content-wrapper examsPage">
              						<%@ include file="../common/studentInfoBar.jsp" %>
              						
              						
              						<div class="sz-content">
								
										<h2 class="red text-capitalize">Select subjects for ${mostRecentTimetablePeriod} Exam : Applicable Subjects  (${applicableSubjectsListCount})</h2>
										<div class="clearfix"></div>
		              					<div class="panel-content-wrapper">
											<%@ include file="../common/messages.jsp" %>
											
				
												<c:choose>
												<c:when test="${applicableSubjectsListCount > 0}">
													<div class="table-responsive">
													<form:form id="subjectForm" action="selectResitExamCenter" method="post" modelAttribute="examBooking" >
														<fieldset>
														<input type="hidden"  name = "month" value = "${examBooking.month }"/>
														<input type="hidden"  name = "year" value = "${examBooking.year }"/>
													
														<table class="table table-striped" style="font-size:12px">
														<thead>
															<tr> 
																<th>Sr. No.</th>
																<th>Program</th>
																<th>Sem</th>
																<th>Subject</th>
																<th>Assignment Submitted</th>
																<th>Exam Fees</th>
																<th>Booking Status</th>
																<th style="text-align:center">Select</th>
																<th>Actions Permitted</th>
																<th>Exam Center Booked</th>
													
															</tr>
														</thead>
														<tbody>
														
														<% int count = 0; 
														final String DD_APPROVED = "DD Approved";
														final String NOT_BOOKED = "Not Booked";
														final String ONLINE_PAYMENT_MANUALLY_APPROVED = "Online Payment Manually Approved"; 
														final String FEE_IN_ADMISSION = "Exam Fees part of Registration Fees/Exam Fees Exempted";
														final String SEAT_RELEASED = "Seat Released";
														final String BOOKED = "Booked";
														
														for(int i = 0; i < applicableSubjectsList.size() ; i++){
															ProgramSubjectMappingExamBean bean = applicableSubjectsList.get(i);
															count++;
															String studentProgram = bean.getProgram();
															String sem = bean.getSem();
															String subject = bean.getSubject();
															String assignmentSubmitted = bean.getAssignmentSubmitted();
															String examFees = bean.getExamFees();
															if(examFees == null){
																examFees = "--";
															}
															String bookingStatus = bean.getBookingStatus();
															String canBook = bean.getCanBook();
															String canFreeBook = bean.getCanFreeBook();
															String bookingStatusForDisplay = bookingStatus;
															String centerName = bean.getCenterName();
															if(ONLINE_PAYMENT_MANUALLY_APPROVED.equals(bookingStatus)){
																bookingStatusForDisplay = "Approved";
															}
														 %>
														
														 	<tr>
													            <td><c:out value="<%=count %>"/></td>
													            <td><c:out value="<%=studentProgram %>"/></td>
													            <td><c:out value="<%=sem %>"/></td>
																<td nowrap="nowrap"><c:out value="<%=subject %>"/></td>
																<td><c:out value="<%=assignmentSubmitted %>"/></td>
																<td><c:out value="<%=examFees %>"/></td>
																<td><c:out value="<%=bookingStatusForDisplay %>"/></td>
																<td>
																<%if("Yes".equals(canBook)){ %>
																	<form:checkbox path="applicableSubjects" value="<%=subject %>"  />
																<%}else if("Yes".equals(canFreeBook)){ %>
																	<form:checkbox path="freeApplicableSubjects" value="<%=subject %>"  />
																<%} %>
													            </td>
													            
													            <td>
																<%if(DD_APPROVED.equals(bookingStatus)){ %>
																	Select Exam Center
																<%}else if(ONLINE_PAYMENT_MANUALLY_APPROVED.equals(bookingStatus)){ %>
																	Select Exam Center 
																<%}else if(NOT_BOOKED.equals(bookingStatus)){ %>
																Select Exam Center & Proceed to Pay
																<%}else if(FEE_IN_ADMISSION.equals(bookingStatus)){ %>
																Select Exam Center - No Charges
																<%}else if(SEAT_RELEASED.equals(bookingStatus)){ %>
																Change Exam Center
																<%} %>
													            </td>
													            <td>
													            <%if(BOOKED.equals(bookingStatus)){ %>
													            	<c:out value="<%=centerName %>"/>
													            <%} %>&nbsp;
													            </td>
													        </tr>   
														        
														<%} %>
															
														</tbody>
													</table>
													
													<c:url value="selectResitExamCenterForOnline" var="selectResitExamCenterForOnlineUrl">
													  <c:param name="year" value="${examBooking.year }" />
													  <c:param name="month" value="${examBooking.month }" />
													</c:url>
													
													<c:url value="selectResitExamCenterForRelesedSubjects" var="selectResitExamCenterForRelesedSubjectsUrl">
													  <c:param name="year" value="${examBooking.year }" />
													  <c:param name="month" value="${examBooking.month }" />
													</c:url>
													
													<c:url value="selectResitExamCenterForRelesedNoChargeSubjects" var="selectResitExamCenterForRelesedNoChargeSubjectsUrl">
													  <c:param name="year" value="${examBooking.year }" />
													  <c:param name="month" value="${examBooking.month }" />
													</c:url>
													
													<div class="form-group">
														<label class="control-label" for="submit"></label>
														<div class="controls">
														
														<c:if test="${isExamRegistraionLive == 'true' }">
														
																<c:choose><c:when test="${subjectsToPay > 0}">
																	<button id="submit" name="submit" class="btn btn-large btn-primary" onclick="return validateForm();">Select Exam Center & Proceed to Pay</button>
																</c:when></c:choose>
																
																<%if("true".equals((String)request.getAttribute("hasApprovedOnlineTransactions"))){ %>
																	<button id="cancel" name="cancel" class="btn btn-primary" onclick="window.location.href='${selectResitExamCenterForOnlineUrl}'" type="button" formnovalidate="formnovalidate">Select Exam Center for Online Payment Already Made</button>
																<%} %>
																<%if("true".equals((String)request.getAttribute("hasFreeSubjects"))){ %>
																	<button id="cancel" name="cancel" class="btn btn-primary" onclick="return validateFreeSubjectsForm();"  formnovalidate="formnovalidate">Select Exam Center - No Charges</button>
																<%} %>
																<%if("true".equals((String)request.getAttribute("hasReleasedSubjects"))){ %>
																	<button id="cancel" name="cancel" class="btn btn-primary" type="button" onclick="window.location.href='${selectResitExamCenterForRelesedSubjectsUrl}'" formnovalidate="formnovalidate">Change Exam Center</button>
																<%} %>
																<%if("true".equals((String)request.getAttribute("hasReleasedNoChargeSubjects"))){ %>
																	<button id="cancel" name="cancel" class="btn btn-primary" type="button" onclick="window.location.href='${selectResitExamCenterForRelesedNoChargeSubjectsUrl}'" formnovalidate="formnovalidate">Change Exam Center For No Fees</button>
																<%} %>
																
														</c:if>
														<button id="cancel" name="cancel" type="button" class="btn btn-danger" onclick="window.location.href='/studentportal/home'" formnovalidate="formnovalidate">Back</button>
														</div>
													</div>
												</fieldset>
												</form:form>
												</div>
											</c:when>
												<c:otherwise>
													<button id="cancel" name="cancel" type="button" class="btn btn-danger" onclick="window.location.href='/studentportal/home'" formnovalidate="formnovalidate">Back</button>
												</c:otherwise>
											</c:choose>
											
											<p align="justify" class="examNote">
											<b>Note:</b> 
											You are allowed to book for an exam only if you have submitted the internal assignment in current or previous attempts.
											Please verify your assignment submitted status in table above. 
											</p>
										</div>
              								
              						</div>
              				</div>
              		
                            
					</div>
            </div>
        </div>
            
  	
        <jsp:include page="../common/footer.jsp"/>
            
		
    </body>
</html>