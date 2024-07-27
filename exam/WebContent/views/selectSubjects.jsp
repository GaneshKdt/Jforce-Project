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

<jsp:include page="jscss.jsp">
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
		document.getElementById("subjectForm").action = 'selectExamCenterForFree';
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

	<%@ include file="header.jsp"%>
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
			
			<%@ include file="messages.jsp"%>
			<div class="panel-body">
			
			<div class="col-md-18 column">
				
				<c:choose>
				<c:when test="${applicableSubjectsListCount > 0}">
				<div>
					<div class="table-responsive">
					<form:form id="subjectForm" action="selectSubjects" method="post" modelAttribute="examBooking" >
						<fieldset>
				
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
								Select Payment Mode
								<%}else if(FEE_IN_ADMISSION.equals(bookingStatus)){ %>
								Select Exam Center
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
					
					<div class="form-group">
						<label class="control-label" for="submit"></label>
						<div class="controls">
						
						<c:if test="${isExamRegistraionLive == 'true' }">
							<c:choose><c:when test="${subjectsToPay > 0}">
								<button id="submit" name="submit" class="btn btn-large btn-primary" onclick="return validateForm();">Select Payment Mode</button>
							</c:when></c:choose>
							<%if("true".equals((String)request.getAttribute("hasApprovedDD"))){ %>
								<button id="cancel" name="cancel" class="btn btn-primary"  onclick="window.location.href='selectExamCenterForDD'" type="button" formnovalidate="formnovalidate">Select Exam Center for DD Approved Subjects</button>
							<%} %>
							
							<%if("true".equals((String)request.getAttribute("hasApprovedOnlineTransactions"))){ %>
								<button id="cancel" name="cancel" class="btn btn-primary" onclick="window.location.href='selectExamCenterForOnline'" type="button" formnovalidate="formnovalidate">Select Exam Center for Online Payment Already Made</button>
							<%} %>
							<%if("true".equals((String)request.getAttribute("hasFreeSubjects"))){ %>
								<button id="cancel" name="cancel" class="btn btn-primary" onclick="return validateFreeSubjectsForm();"  formnovalidate="formnovalidate">Select Exam Center</button>
							<%} %>
							<%if("true".equals((String)request.getAttribute("hasReleasedSubjects"))){ %>
								<button id="cancel" name="cancel" class="btn btn-primary" type="button" onclick="window.location.href='selectExamCenterForRelesedSubjects'" formnovalidate="formnovalidate">Change Exam Center</button>
							<%} %>
							<%if("true".equals((String)request.getAttribute("hasReleasedNoChargeSubjects"))){ %>
								<button id="cancel" name="cancel" class="btn btn-primary" type="button" onclick="window.location.href='selectExamCenterForRelesedNoChargeSubjects'" formnovalidate="formnovalidate">Change Exam Center For No Fees</button>
							<%} %>
						</c:if>
						
						<button id="cancel" name="cancel" type="button" class="btn btn-danger" onclick="window.location.href='verifyInformation'" formnovalidate="formnovalidate">Back</button>
						</div>
					</div>
				</fieldset>
				</form:form>
				</div>
				</div>
			</c:when>
				<c:otherwise>
					<button id="cancel" name="cancel" type="button" class="btn btn-danger" onclick="window.location.href='verifyInformation'" formnovalidate="formnovalidate">Back</button>
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




	<jsp:include page="footer.jsp" />
</body>
</html>
 --%>
 
 
 <!DOCTYPE html>
<%@page import="com.nmims.beans.StudentMarksBean"%>
<%@page import="com.nmims.beans.ProgramSubjectMappingExamBean"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.nmims.beans.StudentExamBean, com.nmims.controllers.HomeController"%> 

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>


<%
	ArrayList<ProgramSubjectMappingExamBean> applicableSubjectsList = (ArrayList<ProgramSubjectMappingExamBean>)session.getAttribute("applicableSubjectsList");
	String earlyAccess = (String)session.getAttribute("earlyAccess");
	StudentExamBean student = (StudentExamBean)session.getAttribute("studentExam");
    String hasFreeSubjects = (String)request.getAttribute("hasFreeSubjects");
	String firstName = student.getFirstName();
	String lastName = student.getLastName();
%>
	
<html lang="en">
	
    
    <jsp:include page="common/jscss.jsp">
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
			document.getElementById("subjectForm").action = 'selectExamCenterForFree';
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
    
    	<%@ include file="common/header.jsp" %>
    	
    	
        
        <div class="sz-main-content-wrapper">
        
        	<jsp:include page="common/breadcrum.jsp">
			<jsp:param value="Student Zone;Exams;Exam Registration" name="breadcrumItems"/>
			</jsp:include>
        	
            
            <div class="sz-main-content menu-closed">
                    <div class="sz-main-content-inner">
                           <div id="sticky-sidebar">  
	              				<jsp:include page="common/left-sidebar.jsp">
									<jsp:param value="Exam Registration" name="activeMenu"/>
								</jsp:include>
              				</div>
              				
              				<div class="sz-content-wrapper examsPage">
              						<%@ include file="common/studentInfoBar.jsp" %>
              						
              						
              						<div class="sz-content">
              						<%-- <c:if test="${student.centerName eq 'Verizon'}">
              						   <font color="red" size="5"><b>Exam Registration is not Live currently</b></font>
              						</c:if> --%>
              						<%@ include file="common/messages.jsp" %>
              						<br>
									<c:if test="${isExamRegistraionLive}">
									<c:choose>
									<c:when test="${sessionScope.isProvisionalAdmission == HomeController.PROVISIONAL_ADMISSION_EXAMBOOKING_NOT_ALLOWED}">
										<div class="alert alert-info alert-dismissible">
											<button type="button" class="close" data-dismiss="alert"  aria-hidden="true">  &times;  </button>
		              						Please submit your documents before proceeding ahead.
										</div>
									</c:when>
									<c:otherwise>	
										<div class="alert alert-info alert-dismissible">
											<button type="button" class="close" data-dismiss="alert"  aria-hidden="true">  &times;  </button>
		              						If you are not able to see your exam booking for which you have been already charged, we advise you wait for 30 mins and even then, if your booking details do not reflect please email.
										</div>
										<h2 class="red text-capitalize" style="width:100%">Select subjects for ${mostRecentTimetablePeriod} Exam : Applicable Subjects  (${applicableSubjectsListCount})
										<span class="pull-right animate-flicker"> 
											<a target="_blank" href="/exam/student/viewModelQuestionForm" style="font-size:16px;"><i class="fa-solid fa-pen-to-square" aria-hidden="true"></i> Take Demo Exam</a> 
											<span class="newFlag">New!</span>
										</span>
									</c:otherwise>	
									</c:choose>
										</h2>
										<div class="clearfix"></div>
	             						<div class="panel-content-wrapper">
										
										
										<c:choose>
											
											<c:when test="${applicableSubjectsListCount > 0}">
												<div>
													<div class="table-responsive">
													<form:form id="subjectForm" action="selectSubjects" method="post" modelAttribute="examBooking" >
														<fieldset>
												
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
															String bookingStatus = bean.getBookingStatus();
															String canBook = bean.getCanBook();
															String canFreeBook = bean.getCanFreeBook();
															String bookingStatusForDisplay = bookingStatus;
															String centerName = bean.getCenterName();
															if(ONLINE_PAYMENT_MANUALLY_APPROVED.equals(bookingStatus)){
																bookingStatusForDisplay = "Approved";
															}
															
															String examFees = bean.getExamFees();
															if(examFees == null){
																examFees = "--";
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
																Select Exam Center
																<%}else if(FEE_IN_ADMISSION.equals(bookingStatus)){ %>
																Select Exam Center
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
													<div class="form-group">
														<label class="control-label" for="submit"></label>
														<div class="controls">
														
														<c:if test="${isExamRegistraionLive == 'true' }">
															
															<c:choose><c:when test="${subjectsToPay > 0 && canBookSubjects}">
																<button id="submit" name="submit" class="btn btn-large btn-primary" onclick="return validateForm();">Select Exam Center</button>
															</c:when></c:choose>
															<%if("true".equals((String)request.getAttribute("hasApprovedDD"))){ %>
																<button id="cancel" name="cancel" class="btn btn-primary"  onclick="window.location.href='selectExamCenterForDD'" type="button" formnovalidate="formnovalidate">Select Exam Center for DD Approved Subjects</button>
															<%} %>
															
															<%if("true".equals((String)request.getAttribute("hasApprovedOnlineTransactions"))){ %>
																<button id="cancel" name="cancel" class="btn btn-primary" onclick="window.location.href='selectExamCenterForOnline'" type="button" formnovalidate="formnovalidate">Select Exam Center for Online Payment Already Made</button>
															<%} %>
															<%if("true".equals((String)request.getAttribute("hasFreeSubjects")) && "true".equals((String)request.getAttribute("canBookFreeSubjects"))){ %>
																<button id="cancel" name="cancel" class="btn btn-primary" onclick="return validateFreeSubjectsForm();"  formnovalidate="formnovalidate">Select Exam Center (No charges)</button>
															<%} %>
															<%if("true".equals((String)request.getAttribute("hasConfirmedBookings"))){ %>
				
																<button id="cancel" name="cancel" class="btn btn-primary" onclick="return changeSlotConfirm();" formaction="searchBookingsToReleaseStudent" formnovalidate="formnovalidate">Change Exam Center/Date/Slot</button>
															
															<%} %>
															
															<c:if test="${canBookReleasedSubjects == 'true' }">
															
															<%if("true".equals((String)request.getAttribute("hasReleasedSubjects"))){ %>
																<button id="cancel" name="cancel" class="btn btn-primary" type="button" onclick="window.location.href='selectExamCenterForRelesedSubjects'" formnovalidate="formnovalidate">Select Exam Center For Released Bookings</button>
															<%} %>
															<%if("true".equals((String)request.getAttribute("hasReleasedNoChargeSubjects"))){ %>
																<button id="cancel" name="cancel" class="btn btn-primary" type="button" onclick="window.location.href='selectExamCenterForRelesedNoChargeSubjects'" formnovalidate="formnovalidate">Change Exam Center For No Fees</button>
															<%} %>
															
															</c:if>
														</c:if>
														
														<!-- <button id="cancel" name="cancel" type="button" class="btn btn-danger" onclick="window.location.href='/studentportal/home'" formnovalidate="formnovalidate">Back</button> -->
														</div>
													</div>
												</fieldset>
												</form:form>
												</div>
												</div>
											</c:when>
											
											<c:otherwise>
													<!-- <button id="cancel" name="cancel" type="button" class="btn btn-danger" onclick="window.location.href='/studentportal/home'" formnovalidate="formnovalidate">Back</button> -->
												</c:otherwise>
											</c:choose>
											
										</div>
              								</c:if>
              							<div>
											<b>Note:</b>
											<ol>
											<c:if test="${student.centerName ne 'Diageo'}">
											<li>A student enrolling in each semester, has to necessarily complete a study period of at least six months in the enrolled semester, to be eligible for the Term End Examination of the subjects of that semester. Students can register and appear for the examination without submitting the internal assignment.</li>
											</c:if>
											<c:if test="${student.centerName eq 'Diageo'}">
											<li>A student enrolling in each semester, has to necessarily complete a study period of at least three months in the enrolled semester, to be eligible for the Term End Examination of the subjects of that semester. Students can register and appear for the examination without submitting the internal assignment.</li>
											</c:if>
											<li>For Result Declaration: Aggregate passing is the criteria i.e. Internal Assignment plus Term End Examination marks together must be <c:if test="${student.prgmStructApplicable eq 'Jul2017'}">40</c:if><c:if test="${student.prgmStructApplicable ne 'Jul2017'}">50</c:if> marks or more out of 100. For being declared as 'Pass' in each subject, appearance in both the components (Internal Assignment and Term End Examination) is mandatory. Without submitting the assignment and only appearing for term end examination cannot be declared as pass. In such cases, the result will be kept on hold.</li>
											<li>Internal Assignment/s submitted on or before the last date of assignment submission only will reflect in the respective exam cycle result declaration. No assignment submission request will be considered for reason whatsoever after the closure of assignment submission window for that respective exam cycle. Please verify your assignment submitted status in the exam registration table.</li>
											<c:if test="${student.centerName ne 'Verizon' && student.centerName ne 'Diageo'}">
											<li>Exam fee is not a part of program fee and is charged separately. Exam fees once paid is neither refunded nor carry forwarded to next exam cycle in case the student cannot appear for the examination for reasons whatsoever.</li>
											</c:if>
											</ol>
										</div>
										<div>
										<b>Note (for students with Special Needs only):</b>
										<ol>
										<li>If you are a Special Need student, kindly submit a service request via the Student Portal with your medical certificate. SR raised till 10 days before the start any exam cycle will only be taken into consideration..</li>
										<li>If you also require Scribe assistance as a special need student, kindly raise a new service request for scribe with required documents.</li>
										<li>These documents are subject to verification and the approval of service request(s) is solely at the discretion of the University.</li>
										<li>In case of other medical issues, you may inform the University via mail on ngasce@nmims.edu or call us at 1800 1025 136 (Mon-Sat)  9am - 7pm.</li>
										</ol>
										</div>		
										
              						</div>
              				</div>
              		
                            
					</div>
            </div>
        </div>
            
  	
        <jsp:include page="common/footer.jsp"/>
        <script>
        	$(document).ready(function(){
        		$('.requestFormBtn').click(function(){
        			var attr = $(this).attr('data-action');
        			$('#requestFormAction').val(attr);
        			$('#requestForm').submit();
        		});
        	});

        	function changeSlotConfirm(){
				var slotConfirmFees = "${releaseFees}";
				var dialogueVar = "Are you sure you want to change exam centers? You will be charged INR. "+slotConfirmFees+"/- to change Exam Centers/Date/Time.";
				return confirm(dialogueVar);
            	}
        </script>
		
    </body>
</html>