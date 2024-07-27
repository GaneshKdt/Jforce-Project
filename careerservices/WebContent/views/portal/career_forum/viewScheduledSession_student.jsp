<!DOCTYPE html>
<!--[if lt IE 7]>	<html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>		<html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>		<html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!-->


<%@page import="com.nmims.beans.VideoContentCareerservicesBean"%>
<%@page import="com.nmims.beans.SessionDayTimeBean"%>
<%@page import="java.util.HashMap"%>
<%@page import="com.nmims.beans.UserAuthorizationBean"%>
<html class="no-js">
<!--<![endif]-->
<%@page import="com.nmims.beans.PersonCareerservicesBean"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<jsp:useBean id="now" class="java.util.Date" />
<jsp:include page="/views/common/jscss.jsp">
    <jsp:param value="Session Details" name="title" />
</jsp:include>


<style>
    ul {
        padding-left: 20px;
    }

    .sz-content li {
        list-style: disc;
        list-style-position: inside;
    }



    .table td {
        text-align: center;
    }

    .table thead th {
        text-align: center;
        font-weight: 600;
    }

    .table {
        margin-bottom: 25px;
    }

    .padding-top {
        padding-top: 25px;
    }
</style>

<%
String userId = (String)session.getAttribute("userId");
	PersonCareerservicesBean user = (PersonCareerservicesBean)session.getAttribute("user_careerservices");
	String roles = "";
	String sessionId = "";
	boolean canActivate = (boolean) request.getAttribute("canActivate");
	boolean sessionViewed = (boolean) request.getAttribute("sessionViewed");
	int activationsLeft = (int) request.getAttribute("activationsLeft");
	
	boolean canView = false;
	if(canActivate || sessionViewed ){
		canView = true;
	}else if(activationsLeft > 0){
		canView = true;
	} 
	
	SessionDayTimeBean sessionBean = (SessionDayTimeBean) request.getAttribute("session");
	boolean sessionCancelled = false;
	sessionId = sessionBean.getId();
	if(sessionBean != null){
		if(sessionBean.getIsCancelled() != null){
	if(sessionBean.getIsCancelled().equals("Y")){
		sessionCancelled = true;
	}
		}
	}
	
	boolean enableAttendButton = false;
	if(request.getAttribute("enableAttendButton") != null){
		if(((String) request.getAttribute("enableAttendButton")).equals("true")){
	enableAttendButton = true;
		}
	}

	boolean sessionOver = true;
	if(request.getAttribute("sessionOver") != null){
		if(((String) request.getAttribute("sessionOver")).equals("false")){
	sessionOver = false;
		}
	}
	
	boolean videoContentAvailable = false;
	VideoContentCareerservicesBean videoContentBean = new VideoContentCareerservicesBean();
	if(request.getAttribute("videoContentAvailable") != null){
		if((boolean) request.getAttribute("videoContentAvailable")){
	videoContentAvailable = true;
	videoContentBean = ( VideoContentCareerservicesBean ) request.getAttribute("videoContentBean");
		}
	}
	
	boolean shownAside = false;
	if(request.getParameter("aside") != null){
		if(((String) request.getParameter("aside")).equals("true")){
	shownAside = true;
		}
	}

	String startTime = sessionBean.getStartTime();
	String endTime = sessionBean.getEndTime();

	startTime = startTime.substring(0, 5);
	endTime = endTime.substring(0, 5);
%>

<body>
	<% if(!shownAside){ %>
    	<jsp:include page="/views/common/header.jsp" />
    <% } %>
    
   		<% if(!shownAside){ %>
    		<div class="sz-main-content-wrapper">
		        <jsp:include page="/views/common/breadcrum.jsp">
		            <jsp:param value="<a href='/careerservices/Home'>Career Services</a>;<a href='career_forum'>Career Forum</a>;Session Details" name="breadcrumItems" />
		        </jsp:include>
		<% }else{ %>
			<div>
		<% } %>

        <div class="sz-main-content menu-closed">
            <div class="sz-main-content-inner">
           
           		<% if(!shownAside){ %>
	                <jsp:include page="/views/common/left-sidebar.jsp">
	                    <jsp:param value="" name="activeMenu" />
	                </jsp:include>
                <% } %>
            	<% if(!shownAside){ %>
                	<div class="sz-content-wrapper">
	                   	<jsp:include page="/views/common/studentInfoBar.jsp" />
						<jsp:include page="/views/portal/loader.jsp" />
						<div class="sz-content" style="display:none;" id="page-content">
	                        <div class="p-3">
                <% }else{ %>
                	<div class="sz-content-wrapper p-0 m-0">
						<jsp:include page="/views/portal/loader.jsp" />
						<div class="sz-content p-0 m-0" style="display:none;" id="page-content">
                    		<div>
               	<% } %>
                            <jsp:include page="/views/common/messages.jsp" />
                            <div class="">
                                <div class="card card-primary">

                                    <div class="card-body row mx-3">
                                        <div class="col-12">
                                            <h2 class="header" style="font-size: 1.6rem;">${session.sessionName}</h2>
                                        </div>
                                        <div class="col-xs-12 d-none d-sm-block d-md-none my-3">
                                        	<% if(videoContentAvailable){ %>
                                        	<img src="<%= videoContentBean.getThumbnailUrl() %>" onError="this.onerror=null;this.src='assets/placeholder.png';"  alt="No screenshot available" style="width:100%">
                                        	<% } %>
                                        </div>
                                        <div class="col-md-6 col-xs-12 my-3">
                                        	<table class="table">
                                        		<tr>
                                        			<td class="text-left pl-3"><b>Speaker</b></td>
                                        			<td class="text-left">
	                                        			<span>
															<a href="/careerservices/speakerProfile?id=${ session.facultyId }">
																${ session.facultyName }
															</a>
														</span>
													</td>
                                        		</tr>
                                        		
                                        		<tr>
                                        			<td class="text-left pl-3"><b>Date</b></td>
                                        			<td class="text-left"><span>${ session.date }</span></td>
                                        		</tr>
                                        		<% if(!videoContentAvailable && !sessionOver){ %>
	                                        		<tr>
	                                        			<td class="text-left pl-3"><b>Time</b></td>
	                                        			<td class="text-left"><%= startTime %> - <%= endTime %></td>
	                                        		</tr>
                                        		<% }else{ %>
	                                        		<tr>
	                                        			<td class="text-left pl-3" width="30%"><b>Duration</b></td>
	                                        			<td class="text-left" width="70%">
		                                        			<% if(videoContentBean.getDuration() != null){ %>
		                                        				<%= videoContentBean.getDuration() %>
		                                        			<% }else{ %>
		                                        				Not Available
		                                        			<% } %>
                                        				</td>
	                                        			
	                                        		</tr>
                                        		<% } %>
                                        	</table>
                                        	
                                            <c:url value="attendScheduledSession" var="joinUrl">
                                                <c:param name="id" value="${session.id}" />
                                                <c:param name="joinFor" value="HOST" />
                                            </c:url>

                                            <c:url value="attendScheduledSession" var="attendUrl">
                                                <c:param name="id" value="${session.id}" />
                                                <c:param name="joinFor" value="ANY" />
                                            </c:url>

                                            <c:url value="loginIntoWebEx" var="startMeetingUrl">
                                                <c:param name="id" value="${session.id}" />
                                            </c:url>

                                            <c:url value="loginIntoZoom" var="startZoomMeetingUrl">
                                                <c:param name="id" value="${session.id}" />
                                            </c:url>
                                            <c:url value="postQueryForm" var="postQueryUrl">
                                                <c:param name="id" value="${session.id}" />
                                                <c:param name="action" value="postQueries" />
                                            </c:url>

                                            <c:url value="postQueryForm" var="myQueriesUrl">
                                                <c:param name="id" value="${session.id}" />
                                                <c:param name="action" value="viewQueries" />
                                            </c:url>

                                            <c:url value="refreshSession" var="refreshurl">
                                                <c:param name="id" value="${session.id}" />
                                            </c:url>

                                            <c:url value="createParallelSession" var="createParallelSessionUrl">
                                                <c:param name="id" value="${session.id}" />
                                            </c:url>

                                            <c:url value="viewFacultyFeedback" var="facultyFeedbackUrl">
                                                <c:param name="id" value="${session.id}" />
                                            </c:url>

                                            <c:url value="viewQueryForm" var="viewQueryUrl">
                                                <c:param name="id" value="${session.id}" />
                                            </c:url>
                                        	<div class="row mt-3">
                                        		<div class="col-12">
	                                        		<h2 class="" style="float:left;">Questions / Issues</h2>
                                        		</div>
	                                            <div class="col-12 mr-auto">
	                                                <a class="btn btn-primary btn-sm" href="${postQueryUrl}"
	                                                    target="_blank">
	                                                    <i class="fa fa-question-circle fa-lg"></i>
	                                                    Post A Query
	                                                </a> &nbsp;
	                                                <a class="btn btn-primary btn-sm" href="${myQueriesUrl}"
	                                                    target="_blank">
	                                                    <i class="fa fa-question-circle fa-lg"></i>
	                                                    My Queries
	                                                </a> &nbsp;
	                                            </div>
                                        		
                                        	</div>
										</div>
                                        <div class="col-md-6 col-xs-12 d-sm-none d-md-block my-3">
                                        	<% if(videoContentAvailable){ %>
                                        		<img src="<%= videoContentBean.getThumbnailUrl() %>" alt="No screenshot available" style="width:100%">
                                        	<% } %>
                                        </div>
										<div class="col-12">
											<div class="row my-4">

                                                <div class="col-12 mt-2">
                                                    <h2 class="py-2" style="font-size: larger;">Description</h2>
                                                </div>
                                                <div class="col-12">
                                                    <p style="font-size: medium;">${session.description}</p>
                                                </div>
                                            </div>
                                            <div>
                                            <% if(!sessionViewed && 
                                            		(
                                            				(!sessionOver && !sessionCancelled && enableAttendButton) ||
                                            				(sessionOver && !sessionCancelled && videoContentAvailable)
                                         			)
                                            	){ %>
                                                <h2 class="my-2 ">Your current package status</h2>
                                                <table class="table  text-centered mx-auto">
                                                    <thead>
                                                        <tr>
                                                            <th><h5 class="py-0 my-0">Total Activations</h5></th>
                                                            <th><h5 class="py-0 my-0">Activations Remaining</h5></th>
                                                            <th><h5 class="py-0 my-0">Activations Currently Possible</h5></th>
                                                            <th><h5 class="py-0 my-0">Next Activation Possible Date</h5></th>
                                                        </tr>
                                                    </thead>
                                                    <tbody>
                                                        <tr>
                                                            <td>
                                                                <span id="totalActivations"></span>
                                                            </td>
                                                            <td>
                                                                <span id="activationsLeft"></span>
                                                            </td>
                                                            <td>
                                                                <span id="activationsPossible"></span>
                                                            </td>
                                                            <td>
                                                                <span id="nextActivationPossible"></span>
                                                            </td>
                                                        </tr>
                                                    </tbody>
                                                </table>
                                                <% } %>
                                            </div>

                                      		<% if(!videoContentAvailable && !sessionOver){ %>
	                                            <div class="bullets card bg-light col-md-6 col-sm-12 text-dark">
	                                            	<div class="card-header hover-icon" onclick="accodionClicked();" data-toggle="collapse" href="#instructions" aria-expanded="false" aria-controls="instructions">
		                                                <span class="text-left">
	                                                    	Instructions to attend session:
	                                                    </span>
	                                                    <span class="text-right ml-auto float-right">
	                                                    	<i id="accodionIcon" class="fa fa-angle-left"></i>
	                                                    </span>
                                                    </div>
	                                                <div id="instructions" class="collapse card-body">
	                                                    <ul>
	                                                        <li>Attend Session Button will be enabled 1 hour before session.
	                                                        </li>
	                                                        <li>You can join meeting 15 minutes before start time.</li>
	                                                        <li>Please keep your Headset ready to attend the session over
	                                                            Zoom.</li>
	                                                        <li>You can post queries related to session after session is
	                                                            over.</li>
	                                                        <li>Please use Google Chrome OR Mozilla Firefox OR Safari
	                                                            browser preferably.</li>
	                                                        <li>Please contact Technical Support Desk +1-888-799-9666 for
	                                                            any Technical Assistance in
	                                                            joining Zoom Webinar </li>
	                                                        <li style="list-style: none;"><a href="resources_2015/LiveSessionGuide.pdf" target="_blank">Download
	                                                            User Guide to Attend Session</a>
                                                            </li>
	                                                    </ul>
	                                                </div>
	                                            </div>
											<% } %>
                                            <!-- 		Added for zoom -->

                                                
                                            <div class="controls pt-5 row" style="padding-bottom: 5px; padding-left: 15px;">
                                                <% if(!sessionCancelled && sessionOver && sessionViewed){%>
													<div class="col-12">
													</div>
														<button class="btn btn-primary btn-sm mx-3" onClick="window.location.href='submitSessionFeedback?sessionId=<%= sessionId %>'">
		                                                    <i class="fa fa-play-circle-o fa-lg"></i> Feedback for this session
		                                                </button>
	                                                <div class="col-12">
													</div>
                                                <% } %>
                                                <% if(!sessionCancelled && enableAttendButton){ %>
	                                                <!-- Student can join Webinar from here -->
	                                                <button class="btn btn-primary btn-sm " onclick="confirmConsumption()">
	                                                    <i class="fa fa-headphones fa-lg"></i>
	                                                    Attend Session by
	                                                    ${mapOfFacultyIdAndFacultyRecord[session.facultyId].fullName}
	                                                    (Remaining Seats : ${facultyIdAndRemSeatsMap[session.facultyId]})
	                                                </button> &nbsp;
                                                <% } %>
                                                
												<c:if test="${enableAttendButton != 'true' && sessionOver != 'true'}">
													<div class="meetingButtons">
														<a  class="btn btn-primary btn-sm" href="#" onClick="return showAlert();"><i class="fa fa-headphones fa-lg"></i> Attend Session</a> &nbsp;
													</div>
													<div class="meetingButtons">
														<a  class="btn btn-primary btn-sm" href="#" onClick="return showLRAlert();" target="_blank"><i class="fa fa-play-circle-o fa-lg"></i> View Session Recordings</a> &nbsp;
													</div>
												</c:if>

                                                <% if(sessionCancelled){%>
	                                                <div class="form-group" style="color:#d2232a ;font-weight:bold;">
	                                                    Session Cancelled
	                                                </div>
	                                                <div class="form-group" style="color:#d2232a;">
	                                                    Remarks : ${session.reasonForCancellation}
	                                                </div>
                                                <% } %>

                                                <% if(videoContentAvailable && sessionOver){%>
                                                    <% if(sessionViewed){ %>
                                                   		<h2 class="my-2 py-2 " style="font-size: larger;">Session Video</h2>
														<% if(!shownAside){ %>
															<div class="embed_container" id="embed_container" style="position: relative;height: 0;width: 100%;min-height: 610px !important;" class='embed-container col-12' >
														      <iframe class="col-12 h-100 video" src='${ videoContentBean.videoLink }' id="video" frameborder='0' webkitAllowFullScreen mozallowfullscreen allowFullScreen></iframe>
														   	</div>
														<% }else{ %>
														<div class="col-12"></div>
	                                                    	<button class="btn btn-primary btn-sm mx-3 "
	                                                        onClick="window.location.href='viewScheduledSession?id=${ session.id }'">
		                                                        <i class="fa fa-play-circle-o fa-lg"></i> View Session
		                                                        Recordings
	                                                    	</button>
														<% } %>
														
												   	</div>
                                                    <% }else{ %>
                                                    	<button class="btn btn-primary btn-sm mx-3"
                                                        onClick="confirmConsumption()">
	                                                        <i class="fa fa-play-circle-o fa-lg"></i> View Session
	                                                        Recordings
                                                    	</button>
                                                	<% }%>
                                                <% }else if(sessionOver && !sessionCancelled){ %>
	                                                <div class="form-group" style="color:#d2232a ;">
	                                                    Session recording not available yet.
	                                                    Please check again later.
	                                                </div>
                                                <% } %>
                                                
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    
    <% if(!shownAside){ %>
   		<jsp:include page="/views/common/footer.jsp" />
    <% }else{ %>
		<div class="d-none">
	   		<jsp:include page="/views/common/footer.jsp" />
	   	</div>
    <% } %>
    
    
  	<jsp:include page="/views/common/iFrameVideoResizer.jsp" />
    
    <script>

    	function accodionClicked(){
    		console.log('click');
    		$('#accodionIcon').removeClass();
    		var accodionclass = $("#instructions").attr("class");

    		console.log(accodionclass);
    		console.log($("#accodionIcon").attr("class"));
    		if(accodionclass.lastIndexOf("show") == -1){
    			$('#accodionIcon').addClass("fa fa-angle-down");
    		}else{
    			$('#accodionIcon').addClass("fa fa-angle-left");
    		}
    		console.log($("#accodionIcon").attr("class"));
    	}

    	function showAlert(){
    		alert('Session not yet started. Please revisit this page 1 hour before start Date/time.');
    		return false;
    	}

    	function showLRAlert(){
    		alert('Session recordings will be available within 48 hours after the session ends.');
    		return false;
    	}

        function showMessage() {
            $('#guestLecture').show();
        }

        var activationInfo;
        $(document).ready(function () {
            getCareerForumActivationInfo();
           
        });

        function getCareerForumActivationInfo() {

            var sapid = '{"sapid": "<%= (String)request.getSession().getAttribute("userId") %>"}';
            $.ajax({
                type: 'POST',
                url: '/careerservices/m/getCareerForumActivationInfo',
                data: sapid,
                contentType: "application/json;",
                dataType: "json",
                success: function (data, textStatus) {
                	if(data.status != "success"){
						showLoadingError();
						return;
					}
                    activationInfo = data.response;
                    
                    var nextActivationDateStr = activationInfo["nextActivationAvailableDate"];
                    var nextActivationDate = new Date(nextActivationDateStr);
                    nextActivationDate = nextActivationDate.toDateString();
                    if(nextActivationDate == "Invalid Date"){
                    	nextActivationDate = "Not Applicable";
                    }
                    activationInfo["nextActivationPossible"] = nextActivationDate;
                    
                    
                    $("#totalActivations").html(activationInfo.totalActivations);
                    $("#activationsPossible").html(activationInfo.activationsPossible);
                    $("#nextActivationPossible").html(activationInfo.nextActivationPossible);
                    $("#activationsLeft").html(activationInfo.activationsLeft);
			    	stopLoading();
                }
            });
        }

        function confirmConsumption() {
            var consumed = <%= sessionViewed %>;
            if (!consumed) {
            	
                    var nextActivationDateStr = activationInfo["nextActivationAvailableDate"];
                    var nextActivationDate = new Date(nextActivationDateStr);
                    activationInfo["nextActivationPossible"] = nextActivationDate.toDateString();
                    
                    var nextActivation = "You will get another activation on " + activationInfo.nextActivationPossible;
                    if(activationInfo.nextActivationPossible == "Invalid Date"){
                    	nextActivation = "You will <b>Not</b> get any more activations for this package";
                    }
                    $.confirm({
            		    icon: 'fa fa-exclamation-triangle',
            		    title: 'Confirm Action',
            		    theme: 'material',
            		    type: 'orange',
            		    backgroundDismiss: true,
            		    columnClass: 'col-lg-6 col-lg-offset-3 col-md-8 col-md-offset-2 col-sm-12',
            		    content: `
            				This is a session of <b>` + activationInfo.featureName + `</b> under your purchased package <b>` + activationInfo.packageName + `</b>.
            				<br>Activating this session will <b>consume</b> an activation. 
            				<br>You currently have <b>` + activationInfo.activationsLeft + `/` + activationInfo.totalActivations + `</b> activations left. 
            				<br>You can activate <b>` + activationInfo.activationsPossible + `</b> sessions right now.
            				<br>` + nextActivation + `.
            				<br>
            				<br>This package started on <b>` + activationInfo.packageStartDate + `</b> and will be active till <b>` + activationInfo.packageEndDate + `</b>.
            			`,
            		    buttons: {
            		        Yes: {
            		            text: 'Proceed',
            		            btnClass: 'btn btn-primary',
            		            action: function(){
            		            	
            		            	<% if(enableAttendButton){ %>
	            	                    var url = "attendScheduledSession?id=" + <%= sessionId %>;
	            	                    window.open(url);
            		            	<% }else{ %>
	            	                    var url = "attendScheduledSession?id=" + <%= sessionId %>;
	            	                    window.open(url);
            		            	<% } %>
            		            }
            		        },
            		        No: {
            		            text: 'Back',
            		            btnClass: 'btn btn-secondary',
            		            action: function(){
            		            	
            		            }
            		        }
            		    }
            		});
                
            } else {
                <% 
					String url = "";
					if(!sessionCancelled){
						if(videoContentAvailable){
							url = videoContentBean.getVideoLink();
						}else if(enableAttendButton){
							if(request.getAttribute("joinUrl") != null){
								url = (String) request.getAttribute("joinUrl");
							}
						}
				%>
                var url = "<%= url %>";
               	window.open(url);
                <%
					}
				
				%>
            }
        }
        
    </script>

</body>

</html>