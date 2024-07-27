<!DOCTYPE html>

<%@page import="com.nmims.beans.CareerForumHomeModelBean"%>
<%@page import="com.nmims.beans.AvailablePackagesModelBean"%>
<%@page import="com.nmims.beans.UserViewedWebinar"%>
<%@page import="com.itextpdf.text.log.SysoCounter"%>
<%@page import="java.util.Date"%>
<%@page import="com.nmims.beans.SessionDayTimeBean"%>
<%@page import="com.nmims.helpers.*"%>
<%@page import="java.net.URLEncoder"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.Calendar" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>


<html lang="en">
	
	<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
	
	
	<jsp:include page="/views/common/jscss.jsp">
		<jsp:param value="Career Forum" name="title"/>
	</jsp:include>
	<body>
	
		<jsp:include page="/views/common/header.jsp" />
		<div class="sz-main-content-wrapper">
			<jsp:include page="/views/common/breadcrum.jsp">
				<jsp:param value="<a href='/careerservices/Home'>Career Services</a>;<a href='career_forum'>Career Forum</a>" name="breadcrumItems" />
			</jsp:include>
		
		
			<div class="sz-main-content menu-closed">
				
				<div class="sz-main-content-inner">
					<jsp:include page="/views/common/left-sidebar.jsp">
						<jsp:param value="" name="activeMenu"/>
					</jsp:include>	
		
					<div class="sz-content-wrapper">
						<jsp:include page="/views/common/studentInfoBar.jsp" />
						<jsp:include page="/views/portal/loader.jsp" />
						<div class="sz-content" style="display:none;" id="page-content">
							<div class="sz-content pt-3">
								<jsp:include page="/views/common/messages.jsp" />
								
								<h2 class="header pt-3 pl-3"> Career Forum</h2>
								<div class="clearfix"></div>
								<div class="row">
									<div class="col-xl-9 col-md-12 col-sm-12 my-3">
										<div class="p-2">
											<p style="font-size: 1rem;" id="pageDescription">
											</p>
										</div>
										
										<div class="card text-center mt-4">
											<div class="card-header card-special mx-auto">
												<h2 class="text-center mx-auto material-icon-containter" style="color: #d2232a;">
													<!-- <i class="material-icons" style="font-size: 110%"> list_alt</i> -->
													Activation Status 
												</h2>
											</div>
											<div class="card-body">
												<div class="row">
													<div class="col-3 text-center border-right">
														<h4>Total Sessions</h4>
														<p style="font-size: 1.0rem;"><span id="totalActivations"></span></p>	
													</div>
													<div class="col-3 text-center border-right">
														<h4>Remaining</h4>
														<p style="font-size: 1.0rem;"><span id="activationsLeft"></span></p>	
													</div>
													<div class="col-3 text-center border-right">
														<h4>Available</h4>
														<p style="font-size: 1.0rem;"><span id="activationsPossible"></span> </p>	
													</div>
													<div class="col-3 text-center">
														<h4>More Available On</h4>
														<p style="font-size: 1.0rem;"><span id="nextActivationPossible"></span> </p>	
													</div>
												</div>
											</div>
										</div>
										<div class="card text-center mt-4">
											<div class="card-header card-special mx-auto">
												<div class="row px-5 mx-auto">
													<h2 class="text-center mx-auto material-icon-containter" style="color: #d2232a;">
														<!-- <i class="material-icons" style="font-size: 110%"> history</i> -->
														Session Activation History 
													</h2>
												</div>
											</div>
											<div class="card-body" id="studentWebinarData">
												<table id="viewedSessions" class="table table-hover">
													<thead class="">
														<tr class="border">
															<th class="border" scope="row">#</th>
															<th class="border" scope="col">Session Name</th>
															<th class="border" scope="col">Host</th>
															<th class="border" scope="col">Session Date</th>
															<th class="border" scope="col">Activation Date</th>
															<th class="border" scope="col">Actions</th>
														</tr>
													</thead>
													<tbody>
													</tbody>
												</table>
												
											</div>
										</div>
										
										<div class="card text-center mt-4">
											<div class="card-header card-special container">
												<div class="px-5">
													<div>
														<h2 class="text-center mx-auto material-icon-containter" style="color: #d2232a;">
															<!-- <i class="material-icons" style="font-size: 110%"> history</i> -->
															Available Sessions
														</h2>
													</div>
													<div class="clearfix"></div>
												</div>	
											</div>
											<div class="card-body" id="studentWebinarData">
												<table id="availableSessions" class="table table-hover">
													<thead class="">
														<tr class="border">
															<th class="border" scope="row">#</th>
															<th class="border" scope="col">Session Name</th>
															<th class="border" scope="col">Host</th>
															<th class="border" scope="col">Session Date</th>
															<th class="border" scope="col">Duration</th>
															<th class="border" scope="col">Actions</th>
														</tr>
													</thead>
													<tbody>
													</tbody>
												</table>
											</div>
											
											<div class="card-footer text-right">
												<p>Activating these sessions will consume an activation</p>
											</div>
										</div>
									</div>
									<div class="col-xl-3 col-md-12 col-sm-12 my-3">
										
										<!-- 
										<div class="mb-1">
											<div class="mx-auto text-center">
												<button class="btn btn-primary" href="#all-upcoming">
													All Upcoming Career Forum Sessions
												</button>
											</div>
										</div>
										-->
										
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
	
		<script>

		
		$(window).on('resize', function(){
			resizeImages();
		});
		
		var viewedSessions = $('#viewedSessions').DataTable({
			"oLanguage": {
	       		"sEmptyTable": "You have viewed no sessions so far!"
	    	},
	        "columnDefs": [
            	{ "type": "date", "targets": 3 }
         	],
	    	"dom": `
	    	<'row'<'col-sm-12 col-md-6 offset-md-6'f>>
			<'row'<'col-sm-12'tr>>
			<'row'<'col-sm-12 col-md-5'i><'col-sm-12 col-md-7'p>>`,
	    	 "pageLength": 5
		});
		var availableSessions = $('#availableSessions').DataTable({
			"oLanguage": {
	       		"sEmptyTable": "No sessions available!"
	    	},
	        "order": [[ 5, 'asc' ], [ 3, 'asc' ]],
	        "columnDefs": [
	            { "type": "date", "targets": 3 }
	          ],
	    	"dom": `
		    	<'row'<'col-sm-12 col-md-6 offset-md-6'f>>
				<'row'<'col-sm-12'tr>>
				<'row'<'col-sm-12 col-md-5'i><'col-sm-12 col-md-7'p>>`,
	    	 "pageLength": 5
		});

		$(document).on('ready', function () {
	
				$('.load-more-table a').on('click', function () {
					$("#courseHomeLearningResources").addClass('showAllEntries');
					$(this).hide();
				});
				
				$(document).ready( function () {
					populateEvents();
				} );
			});
			
			var sapid = '{"sapid": "<%= (String)request.getSession().getAttribute("userId") %>"}';
			var viewedEvents;
			
			var totalViewedSessions = 0;
			
			function addViewedSessions(data){
				data.forEach(function(data1){
					allViewedEvents.push(data1);
					addViewedSessionData(data1);
				});
				
				viewedSessions.draw();
			}
			function addViewedSessionData(data){
				totalViewedSessions ++ ;
				var eventId = data["id"];
				var sessionUrl = "viewScheduledSession?id=" + eventId;
				var sessionName = data["sessionName"];
				var facultyName = `<a href="speakerProfile?id=` + data["facultyId"] + `">` + data["facultyName"] + `</a>`;
				
				var sessionDate = data["date"];
				var sessionConsumptionDate = data["attendTime"];
				
				var sessionPageURL = `<a href="` + sessionUrl + `">` + sessionName + `</a>`;
				
				var rowData = Array();
				
				rowData.push(totalViewedSessions);
				rowData.push(sessionPageURL);
				rowData.push(facultyName);
				rowData.push(formatDate(sessionDate));
				rowData.push(formatDate(sessionConsumptionDate));

				var sessionJoinActivateButton = `<h5 style="color: #d2232a;"> Recording not yet available </h5>`;
				
				var active = false;
				allActiveEvents.forEach(function(data){
					if(data.id == eventId){
						active = true;
					}
				});
				if(active){
					sessionJoinActivateButton = `
						<button class="btn btn-primary" onclick = 'activateEvent(` + data["id"] + `)'>Join</button>
					`;
				}else if(data.videoContent != null){
					sessionJoinActivateButton = `
						<button class="btn btn-primary" onclick = 'activateEvent(` + data["id"] + `)'>View</button>
					`;
				}
				rowData.push(sessionJoinActivateButton);
				
				
				viewedSessions.row.add( rowData );
			}
			

			function availableEvents(data){
				data.forEach(function(data1){
					addAvailableSessionData(data1);
				});
				
				availableSessions.draw();
			}
			var dataAll;
			var totalAvailableSessions = 0;
			function addAvailableSessionData(data){
				totalAvailableSessions ++ ;
				var sessionUrl = "viewScheduledSession?id=" + data["id"];
				var sessionName = data["sessionName"];
				var facultyName = `<a href="speakerProfile?id=` + data["facultyId"] + `">` + data["facultyName"] + `</a>`;
				var duration = "";
				var sessionPageURL = sessionName;
				var sessionActivationButton = `<h5 style="color: #d2232a;"> Recording not yet available </h5>`;
			
				console.log(data.activationInfos);
		        var activationInfo = data.activationInfo;
		        var nextActivationDateStr = activationInfo.nextActivationAvailableDate;
		        var nextActivationDate = new Date(nextActivationDateStr);
		        activationInfo["nextActivationPossible"] = nextActivationDate.toDateString();
				activationInfos[data["id"]] = activationInfo;
				if(data.videoContent != null){
					if(data.videoContent.duration != null){
						duration = data.videoContent.duration;
					}else{
						duration = "-";
					}
					sessionPageURL = `<a href="` + sessionUrl + `">` + sessionName + `</a>`;
					sessionActivationButton = `
						<button class="btn btn-primary" onclick = 'activateEvent(` + data["id"] + `)'>Activate</button>
					`;
				}else{
					duration = "-";
				}
				var sessionStartTime = data["startTime"];
				
				var sessionDate = data["date"];
				var sessionConsumptionDate = data["attendTime"];
				
				
				var rowData = Array();
				
				rowData.push(totalAvailableSessions);
				rowData.push(sessionPageURL);
				rowData.push(facultyName);
				rowData.push(formatDate(sessionDate));
				rowData.push(duration);
				
				
				rowData.push(sessionActivationButton);
				
				availableSessions.row.add( rowData );
			}
			
			function formatDate(dateString) {
				var date = new Date(dateString);
			  	var monthNames = [
			    	"January", "February", "March",
			    	"April", "May", "June", "July",
			    	"August", "September", "October",
			    	"November", "December"
			  	];

			  	var day = date.getDate();
			  	var monthIndex = date.getMonth();
			  	var year = date.getFullYear();

			  	return day + ' ' + monthNames[monthIndex] + ' ' + year;
			}
			
			var careerForumActivationsPossible = 0;
			var careerForumTotalActivations = 0;
			var careerForumNextActivationPossible = "Not Available";
			var careerForumActivationsLeft = 0;
			function populateEvents(){

				$.ajax({
				    type: 'POST',
				    url: '/careerservices/m/getCareerForumInfo',
				    data: sapid,
				    contentType: "application/json;",  
				    dataType: "json",
				    success: function(data, textStatus ){
						if(data.status != "success"){
							showLoadingError();
							return;
						}
				    	$("#totalActivations").html(data.response.activationInfo.totalActivations);
				    	$("#activationsPossible").html(data.response.activationInfo.activationsPossible);
						
				    	var nextActivationDateStr = data.response.activationInfo.nextActivationAvailableDate;
				    	var nextActivationDate = new Date(nextActivationDateStr);
				    	if(nextActivationDate.toDateString() == "Invalid Date"){
				    		nextActivationDate = "Not Applicable";
				    	}else{
				    		nextActivationDate = nextActivationDate.toDateString();
				    	}
				    	
				    	$("#nextActivationPossible").html(nextActivationDate);
				    	$("#activationsLeft").html(data.response.activationInfo.activationsLeft);
				    	$("#pageDescription").html(data.response.description);
				    	
				    	upcomingEvents(data.response.events.schedule.upcomingEvents);
				    	//active always before viewed. this is so we can get the generated active event info
				    	activeEvents(data.response.events.schedule.activeEvents);
				    	addViewedSessions(data.response.events.status.viewedEvents);
				    	availableEvents(data.response.events.status.notViewedEvents);

				    	stopLoading();
				    },
					error: function (error) {
						showLoadingError();
					}
				});
			}
		</script>
	</body>
</html>