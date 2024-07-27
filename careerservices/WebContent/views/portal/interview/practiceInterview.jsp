<!DOCTYPE html>

<%@page import="com.nmims.beans.InterviewBean"%>
<%@page
	import="com.nmims.beans.CareerForumHomeModelBean"%>
<%@page
	import="com.nmims.beans.AvailablePackagesModelBean"%>
<%@page import="com.nmims.beans.UserViewedWebinar"%>
<%@page import="com.itextpdf.text.log.SysoCounter"%>
<%@page import="java.util.Date"%>
<%@page import="com.nmims.helpers.*"%>
<%@page import="java.net.URLEncoder"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.Calendar"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.text.DateFormat"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>  

<jsp:useBean id="date" class="java.util.Date" />

<html lang="en">

<script type="text/javascript">
		function showAlert() {
			alert('Session not yet started. Please revisit this page 1 hour before start Date/time.');
			return false;
		}
	
		function showLRAlert() {
			alert('Session recordings will be available within 48 hours. You can visit Course Home Page and find recordings under Learning Resources Section.');
			return false;
		}
	</script>

<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>


<jsp:include page="/views/common/jscss.jsp">
	<jsp:param value="Practice Interview" name="title" />
</jsp:include>

<style>
.disabled {
	pointer-events: none;
	cursor: default;
	opacity: 0.6;
	background: gray;
}

.interviews {
	background-color: #28B31D;
	border: 1px solid #1B9B11;
	color: #fff;
}

.dot {
  height: 12px;
  width: 12px;
  border-radius: 50%;
  display: inline-block;
}
</style>

<body>

	<jsp:include page="/views/common/header.jsp" />
	<div class="sz-main-content-wrapper">
		<jsp:include page="/views/common/breadcrum.jsp">
			<jsp:param
				value="<a href='/careerservices/Home'>Career Services</a>;<a href='/careerservices/practiceInterview'>Pactice Interview</a>"
				name="breadcrumItems" />
		</jsp:include>


		<div class="sz-main-content menu-closed">

			<div class="sz-main-content-inner">
				<jsp:include page="/views/common/left-sidebar.jsp">
					<jsp:param value="" name="activeMenu" />
				</jsp:include>

				<div class="sz-content-wrapper">
					<jsp:include page="/views/common/studentInfoBar.jsp" />
					<div class="sz-content" id="page-content">
						<div class="sz-content pt-3">
							<jsp:include page="/views/common/messages.jsp" />

							<h2 class="header pt-3 pl-3">Practice Interview</h2>
							
							<div class="clearfix"></div>
							<div class="row">
								<div class="col-xl-9 col-md-12 col-sm-12 my-3">
									<div class="p-2">
										<p style="font-size: 1rem;" id="pageDescription">Practice
											Interviews will help provide you with comprehensive knowledge
											on building career paths.Students can take a maximum of two
											practice Interview during the course of program.</p>
									</div>

									<div class="card text-center mt-4">
										<div class="card-header card-special mx-auto">
											<h2 class="text-center mx-auto material-icon-containter"
												style="color: #d2232a;">
												<!-- <i class="material-icons" style="font-size: 110%"> list_alt</i> -->
												Activation Status
											</h2>
										</div>

										<div class="card-body">
											<div class="row">
												<div class="col-3 text-center border-right">
													<h4>Total Sessions</h4>
													<p style="font-size: 1.0rem;">
														<span id="totalActivations">${ studentDetails.totalActivations }</span>
													</p>
												</div>
												<div class="col-3 text-center border-right">
													<h4>Remaining</h4>
													<p style="font-size: 1.0rem;">
														<span id="activationsLeft">${ studentDetails.activationsLeft }</span>
													</p>
												</div>
												<div class="col-3 text-center border-right">
													<h4>Available</h4>
													<p style="font-size: 1.0rem;">
														<span id="activationsPossible">${ studentDetails.activationsLeft }</span>
													</p>
												</div>
												<div class="col-3 text-center">
													<h4>More Available On</h4>
													<p style="font-size: 1.0rem;">
														<span id="nextActivationPossible">Not Applicable</span>
													</p>
												</div>
											</div>
										</div>
									</div>
									<div class="card text-center mt-4">
										<div class="card-header card-special mx-auto">
											<div class="row px-5 mx-auto">
												<h2 class="text-center mx-auto material-icon-containter"
													style="color: #d2232a;">
													
													<!-- <i class="material-icons" style="font-size: 110%"> history</i> -->
													
													My Interviews Scheduled
												</h2>
											</div>
										</div>
										<div class="card-body" id="studentInterview">
											<c:choose>
												<c:when test="${ empty studentInterview }">
													<tr>
														<td colspan="4">No Record Found. Please schedule an
															Interview.</td>
													</tr>
												</c:when>
												<c:otherwise>
													<table id="attendedInterview" class="table table-hover">
														<thead class="">
															<tr class="border">
																<th class="border" scope="row">#</th>
																<th class="border" scope="col">Interview Date</th>
																<th class="border" scope="col">Time</th>
																<th class="border" scope="col">Meeting Id</th>
																<th class="border" scope="col">Status</th>
																<th class="border" scope="col">Actions</th>
															</tr>
														</thead>
														<tbody>
														
															<fmt:formatDate value="${date}" var="formatedDate" pattern="yyyy-MM-dd HH:mm:ss" />		
															<c:set var="count" value="0" scope="page" />
															
															<c:forEach items="${studentInterview}" var="interview">

																<c:choose>
																	<c:when test="${ formatedDate > interview.joinTime && formatedDate < interview.joinWindowEndTime }">
																		<c:set var="action" value="updateAttendance(${ interview.interviewId })" />
																		<c:set var="proceed" value=" " />
																		<c:set var="target" value='target="_blank"' />
																		<c:set var="url" value="${ interview.joinUrl }" />
																	</c:when>
																	<c:otherwise>
																		<c:set var="action" value=" " />
																		<c:set var="proceed" value="disabled" />
																		<c:set var="target" value="" />
																		<c:set var="url" value="#" />
																	</c:otherwise>
																</c:choose>
																<c:set var="count" value="${count + 1}" scope="page" />
																<tr>
																	<td>${ count }</td>
																	<td>${ interview.getDate() }</td>
																	<td>${ interview.getStartTime() }</td>
																	<td>${ interview.meetingKey }</td>

																	<c:choose>
																		<c:when test="${ interview.isCancelled == 'Y' }">
																			<td>Cancelled</td>
																			<c:set value="disabled" var="disable"></c:set>
																		</c:when>
																		<c:when test="${ formatedDate > interview.joinWindowEndTime && interview.attended == 'Y' }">
																			<td>Attended</td>
																			<c:set value="disabled" var="disable"></c:set>
																		</c:when>
																		<c:when test="${ formatedDate > interview.joinWindowEndTime && interview.attended == 'N' }">
																			<td>Not Attended</td>
																			<c:set value="disabled" var="disable"></c:set>
																		</c:when>
																		<c:otherwise>
																			<td>Active</td>
																			<c:set value="" var="disable"></c:set>
																		</c:otherwise>
																	</c:choose>
																	<td>
																	
																	<c:choose>
																		<c:when test="${ formatedDate > interview.joinWindowEndTime }">
																			<c:set var="disableAction" value="disabled"></c:set>
																		</c:when>
																		<c:otherwise>
																			<c:set var="disableAction" value=""></c:set>
																		</c:otherwise>
																	</c:choose>
																	
																	
																	
																	<c:choose>
																		<c:when test="${ formatedDate > interview.joinWindowEndTime && interview.attended == 'Y' }">
																			<button id="feedback-link" class="btn btn-primary btn-sm" onclick="checkIfFeedbackProvided(${ interview.interviewId })"
																			style="margin-left: 20px;">View Feedback</button>
																		</c:when>
																		<c:otherwise>
																			<button id="attend${ count }"
																			onclick="hideShow(`attendAction${ count }`,`block`)"
																			class="btn btn-primary btn-sm ${ disable } ${disableAction}"
																			style="margin-left: 20px;">Attend</button>
																					
																			<button id="cancel-button-${ count }"
																			class="btn btn-primary btn-sm ${ disable } ${disableAction}"
																			style="margin-left: 20px;"
																			onclick="hideShow(`cancel-action-${ count }`,`block`)">Cancel</button>
																		</c:otherwise>
																	</c:choose>
																	
																		
																	<!-- _________________________________ Attend _________________________________ -->
																	
																		<div id="attendAction${ count }" class="actionModal">

																			<div class="actionModal-content"
																				style="max-width: 500px;">
																				<i class='fa fa-exclamation-triangle'></i> <b>Confirm
																					Action</b> <br> <br> This is a Practice
																				Interview under your purchased package <b>
																					${studentDetails.packageName}.</b> <br>
																				<br> <b>Session not yet started. Please
																					revisit this page 1 hour before start Date/time.</b>
																				<div style='width: 100%; margin: auto;'>
																					<a class='btn btn-primary ${ proceed }'
																						target="${ target }" style='margin: 10px;'
																						onclick="${ action }" href='${ url }'> Proceed</a>

																					<button type='button' class='btn btn-primary'
																						onclick="hideShow(`attendAction${ count }`,`none`)"
																						id="attendClose" style='margin: 10px;'>Close</button>
																				</div>
																			</div>
																			
																		</div> 
																		
																		<!-- _________________________________ cancel _________________________________ -->

																		<div id="cancel-action-${ count }" class="actionModal">

																			<div class="actionModal-content col-sm-4"
																				id="cancel-conent-${ count }" style="max-width: 500px;">
																				<i class='fa fa-exclamation-triangle'></i> <b>Confirm
																					Action</b> <br> <br> This is a Practice
																				Interview under your purchased package <b>
																					${studentDetails.packageName} </b>.You currently have
																				<b> ${studentDetails.activationsLeft}
																					/${studentDetails.totalActivations} </b> activations
																				left. <b>This interview will be canceled and you
																					will be able to choose another slot.</b> This package
																				started on <b> ${studentDetails.startDate} </b>
																				and will be active till <b>
																					${studentDetails.endDate} </b> <br> <br> <b>
																					Are you sure you want to cancel this practice
																					interview?</b>
																				<div style='width: 100%; margin: auto;'>
																					<button class='btn btn-primary'
																						id="cancel-button-${ count }" style='margin: 10px;'
																						onclick="cancelInterview(${interview.interviewId}, ${ count })">
																						Cancel</button>

																					<button type='button' class='btn btn-primary'
																						onclick="hideShow(`cancel-action-${ count }`,`none`)"
																						id="cancelClose" style='margin: 10px;'>Close</button>
																				</div>
																			</div>
																		</div>

																	</td>
																</tr>
															</c:forEach>
														</tbody>
													</table>
												</c:otherwise>
											</c:choose>
										</div>
									</div>

									<div class="clearfix"></div>

									<div class="card text-center mt-4">
										<div class="card-header card-special">
											<div class="row px-5 mx-auto">
												<h2 class="text-center mx-auto material-icon-containter"
													style="color: #d2232a;">
													<!-- <i class="material-icons" style="font-size: 110%"> history</i> -->
													Interview Session Slots
												</h2>
											</div>
										</div>
										<div style="margin: 20px;">
											<div class="calendar-operations row">
												<div class="col-md-4 col-sm-12  col-xs-12">
													<div class="calendar-navigation">
														<h2 id="month">${ monthYear }</h2>
														<div class="arrows">
															<span id="prev" class="glyphicon glyphicon-chevron-left"></span>
															<span id="next" class="glyphicon glyphicon-chevron-right"></span>
															<div class="clearfix"></div>
														</div>
														<div class="clearfix"></div>
													</div>
												</div>
												<div class="col-md-6 col-sm-12  col-xs-12">
													<ul class="show-calendar-format">
														<li>
															<button class="active-button" id="showM">Month</button>
														</li>
														<li>
															<button id="showW">Week</button>
														</li>
														<li>
															<button id="showD">Day</button>
														</li>
														<!-- <li>
																<button id="showT">Today</button>
															</li> -->
													</ul>
												</div>


												<div class="clearfix"></div>
											</div>
											<div class="row">
												<div class="col-lg-12 col-md-12 col-sm-12  col-xs-12">
													<div id="calendar"></div>
													<div class="panel panel-default" style="font-size: 12px">
														<div class="panel-body"> 
															<span style="padding-right: 20px;"><span class="dot" style="background-color: #FF0000"></span> CS Sessions </span>
															<span class="dot interviews"></span> Interview
														</div>
													</div>
												</div>
											</div>

											<div id="calendarModal" class="modal fade">
												<div class="modal-dialog">
													<div class="modal-content" style="border-radius: 4px;">
														<div class="modal-header">
															<h4 id="modalTitle" class="modal-title"
																style="margin-top: 20px;"></h4>
														</div>
														<div id="modalBody" style="margin: 10px;"></div>
													</div>
												</div>
											</div>
										</div>
									</div>


								</div>
								<div class="col-xl-3 col-md-12 col-sm-12 my-3">
									<jsp:include page="../upcomingAndActiveEvents.jsp" />
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>

		<jsp:include page="/views/common/footer.jsp" />

	</div>
	<c:choose>
		<c:when test="${ studentDetails.activationsLeft <= 0}">
			<c:set var="disabled" scope="application" value="none" />
			<c:set var="link" scope="application" value="#" />
		</c:when>
		<c:otherwise>
			<c:set var="disabled" scope="application" value="" />
			<c:set var="link" scope="application" value="calEvent.url" />
		</c:otherwise>
	</c:choose>
	
	<script>

		$(document).on('ready', function () {

			$('#calendar').fullCalendar({
				
				defaultDate: '${ today }',
				header: false,
				aspectRatio: 1.4,
				eventLimit: true,
				
				events: getCalendarEvents( ${interviewList} ),
					
				eventClick: function(calEvent, jsEvent, view) {
					
					$('#modalTitle').html(calEvent.title);
						
			        $('#modalBody').html(
			            	"<i class='fa fa-exclamation-triangle'></i> <b>Confirm Action</b> <br><br> This is a Practice Interview "+  
					        "under your purchased package <b> ${studentDetails.packageName} </b>. Activating this session will <b>consume</b> an activation. "+
					        "You currently have <b> ${studentDetails.activationsLeft} /${studentDetails.totalActivations} </b> activations left."+
					        "You can activate <b> ${studentDetails.activationsLeft} </b> practice interviews right now. The interview will be scheduled on <b>"+calEvent.interviewDate+
					        "</b> at <b>"+calEvent.interviewTime+"</b>."+
					        "<br><br><div style='width:100%; margin:auto;'>"+
					        "<c:choose> <c:when test='${ studentDetails.activationsLeft <= 0}'>"+
					        "<a class='btn btn-primary' style='margin:10px; display: ${disabled};' href='#'>Proceed</a>"+
					        "</c:when> <c:otherwise> <button class='btn btn-primary' "+
					        "style='margin:10px; display: ${disabled};' onclick='schedulePracticeInterviewSession(${ studentDetails.sapid }, "+ 
					            calEvent.eventId +")'>Proceed</button></c:otherwise></c:choose>"+
					        " <button type='button' class='btn btn-primary' data-dismiss='modal' style='margin:10px;'>Close</button></div>");
			        
			        $('#eventUrl').attr('onclick');
			        $('#calendarModal').modal();
			        return false;
				},
			});

			function setCurrentTitle() {
				var view = $('#calendar').fullCalendar('getView');
				var start = $('#calendar').fullCalendar('getView').title;
				console.log(view,start);
				$('#month').html(start);
			}
	
			function getCalendarEvents( interviews ){
				
				let eventList = []
				
				for( details in interviews ){

					let event = {
						title: "Practice Interview",
						start: interviews[details].date + ' T ' + interviews[details].startTime,
						type: 'Interview',
						eventId: interviews[details].interviewId,
						interviewDate: interviews[details].date,
						interviewTime: interviews[details].startTime,
						className: 'interviews',
					}
					
					eventList.push( event )
				}
				
				return eventList
			}
	
			$('#showM').click(function() {
				$('#calendar').fullCalendar( 'changeView', 'month' );
				setCurrentTitle();
			});
			$('#showW').click(function() {
				$('#calendar').fullCalendar( 'changeView', 'agendaWeek' );
				setCurrentTitle();
			});
			$('#showD').click(function() {
				$('#calendar').fullCalendar( 'changeView', 'agendaDay' );
				setCurrentTitle();
			});
			$('#showT').click(function() {
				$('#calendar').fullCalendar( 'changeView', 'agendaDay' );
				setCurrentTitle();
				$('#calendar').fullCalendar('today');
			});
			
			$('#next').click(function() {
				$('#calendar').fullCalendar('next');
				setCurrentTitle();
			});
			$('#prev').click(function() {
				$('#calendar').fullCalendar('prev');
				setCurrentTitle();
			});
			$('.show-calendar-format button').on('click', function(){
				$('.show-calendar-format button').removeClass('active-button');
				$(this).addClass('active-button');
			});
			
	    });

		function schedulePracticeInterviewSession( sapid, interviewId ){

			var data = {
					'sapid': sapid,
					'interviewId': interviewId
			    }; 
			
			$.ajax({
				type: 'POST',
				url: '/careerservices/m/schedulePracticeInterviewSession',
				data : JSON.stringify(data),
				contentType: "application/json;", 
				dataType: "json",
				success: function(data, textStatus ){
					if(data.status != "success"){
						$('#modalBody').html(
			            		"<i class='fa fa-exclamation-triangle'></i> <b>Confirm Action</b> <br><br>" + data.errorMessage + "<br>"+
			            		"You currently have <b> ${studentDetails.activationsLeft} /${studentDetails.totalActivations} </b> activations left. "+
					            "You can activate <b> ${studentDetails.activationsLeft} </b> practice interviews right now."+
					            "<br><br><div style='width:100%; margin:auto;'>"+
					            "<button type='button' class='btn btn-primary' data-dismiss='modal' style='margin:10px;'>Close</button></div>");
						return;
					}else{
						$('#modalBody').html(
			            		"<i class='fa fa-exclamation-triangle'></i> <b>Confirm Action</b> <br><br>" + data.successMessage + "<br>"+
			            		"You currently have <b> ${studentDetails.activationsLeft - 1} /${studentDetails.totalActivations} </b> activations left. "+
					            "You can activate <b> ${studentDetails.activationsLeft - 1} </b> practice interviews right now."+
					            "<br><br><div style='width:100%; margin:auto;'>"+
					            "<button type='button' class='btn btn-primary' data-dismiss='modal' style='margin:10px;' onclick='window.location.reload();'>Close</button></div>");
						return;
					}
				},
				error: function (error) {
					alert("An error occured while scheduling your interview.");
				}
			});
		}

		function updateAttendance( interviewId ){
			
			var data = {
					'sapid' : ${userId},
					'interviewId': interviewId
			    };
			
			$.ajax({
				type: 'POST',
				url: '/careerservices/m/updateInterviewAttendance',
				data: JSON.stringify(data),
				contentType: "application/json;", 
				dataType: "json",
				success: function( data ){
					if( data.errorMessage ){
						alert("An error occured while updating your attended for the interview, please try again.")
						return;
					}else
						window.location.reload()
				},
				error: function (error) {
					showLoadingError();
				}
			});
		}
	
		function cancelInterview( interviewId, count ){

			var data = {
				'interviewId': interviewId,
				'sapid' : ${ userId }
		    }; 
			
			$.ajax({
				type: 'POST',
				url: '/careerservices/m/cancelInterview',
				data : JSON.stringify(data),
				contentType: "application/json;", 
				dataType: "json",
				success: function(data, textStatus ){

					if(data.status != "success"){
						document.getElementById("cancel-conent-"+count).innerHTML = "<i class='fa fa-exclamation-triangle'></i> <b>"+
						"ConfirmAction</b> <br><br> Your interview for date "+ data.date +" at "+ data.startTime +"  has <b>not been</b> canceled. "+ 
						"There was an error cancelling the interview. Please try again later.<div style='width: 100%; margin: auto;'>"+
						"<button type='button' class='btn btn-primary' id='confirmClose' style='margin: 10px;'  onclick='hideShowRefresh(`cancel-action-"+count+"`,`none`)'>Close</button><div>"
	
						return;
					}else{
						
						document.getElementById("cancel-conent-"+count).innerHTML = "<i class='fa fa-exclamation-triangle'></i> <b>ConfirmAction</b> "+
						"<br><br> <b>Your interview for date "+ data.date +" at "+ data.startTime +"  has been canceled</b>. You will be able to avail another"+
						" practice interview in place of this interview. This package started on "+ 
			            "<b> ${studentDetails.startDate} </b> and will be active till <b> ${studentDetails.endDate} </b> <div style='width: 100%; margin: auto;'>"+
			            "<button type='button' class='btn btn-primary' id='confirmClose' style='margin: 10px;' onclick='hideShowRefresh(`cancel-action-"+count+"`,`none`)'>Close</button><div>"
						return;
					}
				},
				error: function (error) {
					showLoadingError();
				}
			});
		}
	

		function hideShow(modelId, action){
			console.debug('modelId: '+modelId+' action: '+action)
			var model = document.getElementById(modelId);
			model.style.display = action;
		} 
		
		function hideShowRefresh(modelId, action){
			var model = document.getElementById(modelId);
			model.style.display = action;
			window.location.reload();
		} 
		
		function checkIfFeedbackProvided(interviewId){

			var data = {
					'interviewId': interviewId
			    };
		    
			$.ajax({
				type: 'POST',
				url: '/careerservices/m/checkIfInterviewFeedbackProvided',
				data : JSON.stringify(data),
				contentType: "application/json;", 
				dataType: "json",
				success: function(data, textStatus ){
					console.log(data);
					if(data.status != "success"){
						alert(data.errorMessage);
						return;
					}else{
						window.location.href = "/careerservices/student/studentInterviewFeedback?interviewId="+interviewId;
						return;
					}
				}
			});
		}
		
	</script>

</body>
</html>