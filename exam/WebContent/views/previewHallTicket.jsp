<!DOCTYPE html>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/core" prefix = "c" %>
<html lang="en">
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<spring:eval expression="@propertyConfigurer.getProperty('SERVER_PATH')" var="server_path" />
<jsp:include page="common/jscss.jsp">
	<jsp:param value="Preview Hall Ticket" name="title" />
</jsp:include>
<style>
#authentic-photograph{
position:absolute;
margin-top: 93px;
margin-left: -26px;
	/* Safari */
-webkit-transform: rotate(-34deg);

/* Firefox */
-moz-transform: rotate(-34deg);

/* IE */
-ms-transform: rotate(-34deg);

/* Opera */
-o-transform: rotate(-34deg);

color: #5c7090;
/* Internet Explorer */
filter: progid:DXImageTransform.Microsoft.BasicImage(rotation=3);
}
</style>
	<body>
		<%@ include file="common/header.jsp"%>
		<div class="sz-main-content-wrapper">
			<jsp:include page="common/breadcrum.jsp">
				<jsp:param value="Exam;Preview Hall Ticket" name="breadcrumItems" />
			</jsp:include>
			<div class="sz-main-content menu-closed">
				<div class="sz-main-content-inner">
				     <div id="sticky-sidebar">  
						  <jsp:include page="common/left-sidebar.jsp">
							<jsp:param value="Tests" name="activeMenu" />
						</jsp:include> 
					</div>
					<div class="sz-content-wrapper dashBoard demoExam">
						  <%@ include file="common/studentInfoBar.jsp"%>  
						<div class="sz-content">
							<h2 class="red text-capitalize">Preview Hall Ticket </h2>
							<div class="clearfix"></div>
							<div class="panel-content-wrapper">
							<%
							try{
							%>
							<div id="errorMessage" >
							<h4>Note: </h4>
							<c:forEach var="error" items="${serviceLayerResponse.error}" varStatus="status">
							<h4>${error}</h4>
							</c:forEach>
							</div>
								<!-- Code for page goes here start -->
								<div class="preview-hall-ticket-data">
									<div class="row" style="margin-bottom:10px;">
										<div class="col-md-12">
										
										
									
											<c:choose>
										    <c:when test="${serviceLayerResponse.htDownloadStatus}">

												<h6> <span style="color:red">*</span> This Hallticket is <strong style="color:red">ALREADY VALIDATED</strong> and <strong style="color:red">DOWNLOADED</strong>. Please <a href="/studentportal/contactUs" style="font-size: 12px;">contact support</a> if you are facing any issues with the downloaded hallticket.</h6>
												
										
										    </c:when>
										    <c:otherwise>
										       <i style="font-size:10px;"><span style="color:red">*</span> 
											
												Please verify your hall ticket details and submit accordingly
												
												</i>
										    </c:otherwise>
										</c:choose>
										</div>
									</div>
									<div class="row">
										<div class="col-md-12 text-center">
											<h4><b>${serviceLayerResponse.title}</b> </h4>
										</div>
									</div>
									
									<table class="table table-bordered">
										<tbody>
												
												<tr>
													<td colspan="2"><b>Student No : </b>${serviceLayerResponse.student.sapid}</td>
													<td colspan="2"><b>Gender : </b>${serviceLayerResponse.student.gender}</td>
													<td rowspan="4" width="120px">
														<span id="authentic-photograph">AUTHENTIC PHOTOGRAPH</span>
													  <img src="<%=studentPhotoUrl%>" alt="${serviceLayerResponse.student.firstName}-${serviceLayerResponse.student.lastName}-student-photo" height="140" width="110">  
													</td>
												</tr>
												<tr>
													<td colspan="4"><b>Student Name : </b>${serviceLayerResponse.student.firstName} ${serviceLayerResponse.student.lastName}</td>
												</tr>
												<tr>
													<td colspan="2"><b>Program : </b>${serviceLayerResponse.programFullName}</td>
													<td colspan="2"><b>Examination : </b>${serviceLayerResponse.examination}</td>
												</tr>
												<tr>
													<td colspan="4"><b>Exam User ID : </b>${serviceLayerResponse.student.sapid}</td>
												</tr>
												<tr>
													<td colspan="4"><b>Exam User Password : </b>${serviceLayerResponse.password}</td>
												</tr>
												
											
												
										</tbody>
										<tr><td colspan="5"></td></tr>
										
									</table>
									<table class="table table-bordered">
											<thead>
													<tr>
														<th>Sr.No</th>
														<th>Subject</th>
														<th>Sem</th>
														<th>Day</th>
														<th>Date</th>
														<th>Start Time</th>
														<th>End Time</th>
														<th>Location</th>
														<th>Remark</th>
													</tr>
											</thead>
											<tbody>
												
												<c:forEach var="subjects" items="${serviceLayerResponse.examBookedList}"	varStatus="status">
													<tr>
														<td><c:out value="${status.count}" /></td>
														<td><c:out value="${subjects.subject}" /></td>
														<td><c:out value="${subjects.sem}" /></td>
														<td><c:out value="${subjects.day}" /></td>
														<td><c:out value="${subjects.examDate}" /></td>
														<td><c:out value="${subjects.examTime}" /></td>
														<td><c:out value="${subjects.examEndTime}" /></td>
														<td><c:out value="${subjects.address}" /></td>
														<td></td>
													</tr>
												</c:forEach>
												</tbody>
												
									</table>
									<!-- <hr>
									<div class="row">
										<div class="col-md-12">
											<h3 style="display:block; color:black; float:left;">Important Instructions:</h3>
										</div>
									</div>
									<hr>
									<div class="row">
										<div class="col-md-12 text-justify">
											<ol>
												<li>
													<strong>
														It is Mandatory for the student to carry Student ID Card and Hall Ticket at the center. Without both the documents student will strictly not be allowed in the Examination Hall. 
													</strong>
												</li>
												<li>
													It is Mandatory for the student to first register at the Registration Desk for each exam. Without Registration student will not be allowed to appear for the exam. Students are expected to report atleast one hour before actual exam time. 
												</li>
												<li>
													This Hall Ticket is valid for the subject/s mentioned in the Hall Ticket for the above mentioned dates and Exam location displayed. 
												</li>
												<li>
													<strong> 
														Candidates should register themselves and logon to the examination system on or before the scheduled examination time. Exam will start and end as per the scheduled time mentioned in the hall ticket. Students reaching after the start of exam will not be permitted extra exam time. Students will not be allowed entry 30 minutes after the start of the Exam.
													</strong>
												</li>
												<li>
													If the examination does not commence at the scheduled time or is interrupted midway due to any technical difficulty or for any other reason, candidates should follow the instructions of the exam officials. Students may have to wait patiently till the issue is suitably addressed and resolved. In case, the problem is major and cannot be resolved for any reason,their examination may be rescheduled for which the candidates would be duly intimated. 
												</li>
												<li>
													NMIMS will not be liable or accountable for any technology failure prior or during exams. NMIMS will however try to provide suitable resolution as it deems fit. NMIMS resolution in this case will be binding on the student. 
												</li>
												<li>
													<strong>
														Carrying and/or use of any communication devices like any cell phones, PDAs and smartwatches and other electronic, recording, listening, scanning or photographic devices in switched off / on or any other mode carried intentionally or unintentionally is strictly prohibited in the examination hall. Non adherence may result in examination getting Null and Void. Please ensure your communication devices are not in your person during the exam and kept secured in your bag or at designated place inside the lab. 
													</strong>
												</li>
												<li>
													<strong>
														It is mandatory for the students to carry their own calculators (Standard/Scientific)  as there is no on-screen calculator available. Students will not be allowed to borrow calculators  during the examination. Non adherence will result in examination getting Null and Void.
													</strong>
												</li>
												<li>
													Students will not be allowed to use the washroom during the examination. Student will be permitted to use the washroom only after two hours of exam commencement. In case of medical issue, student to seek one week prior approval from NMIMS University by sending medical certificate. 
												</li>
												<li>
													Students would be given Sample Test Questions for practice purpose before they start answering the actual Examination Questions. (Sample Test is available only if the student completes the registration process and reaches 15 minutes early before the start of the actual examination time) 
												</li>
												<li>
													Indiscipline / Unfair Means / Impersonation / Malpractice adoption will be dealt strictly by the University.
												</li>
												<li>
													<strong>
														In all cases the decision of NMIMS University will be final and binding on all students. 
													</strong>
												</li>
												<li>
													<strong>
														For any queries do call us on 1-800-1025-136 (Mon-Sat) 10am - 6pm, during exams the operational hours are from 9am - 6pm. 	
													</strong>
												</li>
											</ol>
											
										</div>
									</div>
									 -->
								</div>
								<hr>
								<div class="row" id="confirmCheck">
								  	<div class="col-md-12 text-center" >
										<span><input type="checkbox" ><strong> The above details are correct proceed to download.</strong></span>
									</div>
								</div>
								<hr>
								<!-- Code for page goes here end -->
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
		
		
		<jsp:include page="common/footer.jsp" />
		<!-- jQuery (necessary for Bootstrap's JavaScript plugins) -->
		<script src="/exam/assets/js/jquery-1.11.3.min.js"></script>
		<!-- //<script src="https://unpkg.com/sweetalert/dist/sweetalert.min.js"></script> -->
					<script src="https://cdnjs.cloudflare.com/ajax/libs/sweetalert/2.1.2/sweetalert.min.js"></script>
		<script>
		$(document).ready(function(){
		$("#errorMessage").hide();
		var errorList = "${serviceLayerResponse.error}";
		if(errorList.length >0){
			$(".preview-hall-ticket-data").hide();
			$("#confirmCheck").hide();
			$("#errorMessage").show();
		}
			
			
			$("input[type=checkbox]").change(function() {
				if(this.checked) {
					
					swal({
						  title: "Are you sure?",
						  text: "No Further Confirmation will be asked",
						  icon: "warning",
						  buttons:["No, Contact Support", "Yes, Proceed to Download"],
						//   buttons: true,
						  dangerMode: true,
						})
						.then((willDelete) => {
						  if (willDelete) {
							window.location.href = "/exam/student/downloadHallTicket";
						    swal("Your Hall Ticket will be downloaded Now", {
						      icon: "success",
						    
						    });
						  } else {
							  window.location.href = "/studentportal/contactUs";
						    
						   
						  }
						});
			    }
			});
		});
		</script>
	</body>
	<%}catch(Exception e){	}
	%>
</html>