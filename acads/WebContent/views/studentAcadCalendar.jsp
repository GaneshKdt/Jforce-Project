
<%-- 
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html >
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!--> 

<html class="no-js"> <!--<![endif]-->
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Date"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.nmims.beans.SessionDayTimeBean"%>

<%
ArrayList<SessionDayTimeBean> scheduledSessionList = (ArrayList<SessionDayTimeBean>)request.getAttribute("scheduledSessionList");
%>

<jsp:include page="jscss.jsp">
<jsp:param value="Sessions Calendar" name="title" />
</jsp:include>
	   

<style>
	#calendar{
		width: 100%;
		margin: 0 auto;
	}

</style>

<body class="inside">

	<%@ include file="header.jsp"%>
	<section class="content-container login">
        <div class="container-fluid customTheme">
        <div class="row"><legend>Academic Calendar </legend></div>
        <h3>Click on Subject name to view details and attend session</h3>
        <div id="acadLegend"></div>
        <%@ include file="messages.jsp"%>
        
        <div class="row">
        
        <div class="column col-md-10">
        <div id='calendar' align="left"></div><br>
        </div>
        
        <div class="column col-md-8">
        <iframe id="sessionFrame" src="" width="100%" seamless="seamless" height="600" frameborder="0"></iframe>
        </div>
        
        </div>
        
		
	
		<br>
	</div>
	</section>
     
      
      <script>


function detectIE() {
    var ua = window.navigator.userAgent;

    var msie = ua.indexOf('MSIE ');
    if (msie > 0) {
        // IE 10 or older => return version number
        return parseInt(ua.substring(msie + 5, ua.indexOf('.', msie)), 10);
    }

    var trident = ua.indexOf('Trident/');
    if (trident > 0) {
        // IE 11 => return version number
        var rv = ua.indexOf('rv:');
        return parseInt(ua.substring(rv + 3, ua.indexOf('.', rv)), 10);
    }

    var edge = ua.indexOf('Edge/');
    if (edge > 0) {
       // IE 12 => return version number
       return parseInt(ua.substring(edge + 5, ua.indexOf('.', edge)), 10);
    }

    // other browser
    return false;
}


flag = detectIE();

if(flag != false){
	alert('You can not view Calendar using Internet Explorer. Please switch to Chrome/Firefox/Safari');
	document.getElementById("acadLegend").innerHTML = "Academic Calendar not visible on IE. Please switch to Chrome/Firefox/Safari to view Academic Calendar";
	
}



</script>
      
 <jsp:include page="footer.jsp" />
 
 <%
 Date dt = new Date();
 
 SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
 String today = sdf.format(dt);
 %>
 <script>

	$(document).ready(function() {
		
		$('#calendar').fullCalendar({
			header: {
				left: 'prev,next today',
				center: 'title',
				right: 'month,agendaWeek,agendaDay'
			},
			defaultDate: '<%=today%>',
			editable: false,
			eventLimit: true, // allow "more" link when too many events
			events: [
			     <% for(int i = 0 ; i < scheduledSessionList.size(); i++){
			    	 SessionDayTimeBean bean = scheduledSessionList.get(i);
			     %>    
			         
			         
				{
					title: '<%=bean.getSubject().replaceAll("'", "") + " - "+ bean.getSessionName()%>',
					start: '<%=bean.getDate()+"T"+ bean.getStartTime()%>',
					url: 'viewScheduledSession?id=<%=bean.getId()%>',
				},
				
				<%}%>
			],
			eventClick: function(event) {
		        if (event.url) {
		        	document.getElementById("sessionFrame").src=event.url;
		            //window.open(event.url);
		            return false;
		        }
		    }
		});
		
	});

	
	
</script>
   
</body>
</html>
 --%>
 
<%@page import="java.util.Collections"%>
<%@page import="java.util.HashSet"%>
<%@page import="com.nmims.controllers.BaseController"%>
<%@page import="com.sun.prism.impl.BaseContext"%>
<%@page import="org.jsoup.Connection.Base"%>
<%@page import="org.apache.commons.lang3.StringUtils"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
 <!DOCTYPE html>
<%@page import="java.util.Calendar"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Date"%> 
<%@page import="java.text.DateFormat"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.nmims.beans.SessionDayTimeAcadsBean"%>


<%@page import="java.util.List"%> 
<%@page import="java.util.ArrayList"%> 
<%@page import="com.nmims.beans.ExamBookingTransactionAcadsBean"%>
<%@page import="com.nmims.beans.EventBean"%>
<%@page import="com.nmims.beans.SessionTrackBean"%>


<%
	ArrayList<SessionDayTimeAcadsBean> scheduledSessionList = (ArrayList<SessionDayTimeAcadsBean>)request.getAttribute("scheduledSessionList");
	HashSet<String> tracks = new HashSet<String>();
	List<String> trackList = new ArrayList<String>();

	Calendar cal = Calendar.getInstance();
	//boolean registeredForEvent = (boolean)request.getAttribute("registeredForEvent");
	StudentAcadsBean student = (StudentAcadsBean)session.getAttribute("student_acads");
	String monthYear = new SimpleDateFormat("MMM YYYY").format(cal.getTime());
	String sessionId = request.getParameter("sessionId");
	String sapIdToBeBlocked = (String)request.getSession().getAttribute("userId_acads");
	for(SessionDayTimeAcadsBean sessions :scheduledSessionList){
		if("Orientation Session".equals(sessions.getSessionName()) && "Verizon".equals(sessions.getCorporateName())){ // added temporary
			sessions.setSubject("Orientation");
			sessions.setStartTime("16:30:00");
		}else if("Orientation Session".equals(sessions.getSessionName())){
			sessions.setSubject("Orientation");
			//sessions.setStartTime("17:00:00");
		}
		String track = sessions.getTrack();
		if(StringUtils.isBlank(track)){
			 track =" ";
		}
		tracks.add(track);
	}
	if(tracks.size() > 0){
		trackList.addAll(tracks);
		Collections.sort(trackList);
	}
	
	BaseController calCon = new BaseController();
	String disableLink = "";
	if(calCon.checkLead(request, response)){
		disableLink = "disabled";
	}

List<ExamBookingTransactionAcadsBean> bookedExams = (List<ExamBookingTransactionAcadsBean>)request.getAttribute("bookedExams");
List<EventBean> eventsList = (List<EventBean>)request.getAttribute("eventsList");
String trackDropdown=request.getParameter("track");
ArrayList<SessionTrackBean> tracksDetails = (ArrayList<SessionTrackBean>) request.getAttribute("trackDetails");


%>

<html lang="en">
    
	<style>
		a.disabled{
			pointer-events: none;
  			cursor: default;
  			color: gray;
		}

		.interviews{
			background-color: #28B31D;
			border: 1px solid #1B9B11;  
			color: #fff; 
		}
		<% if(tracksDetails!=null && !tracksDetails.isEmpty()){
			for(SessionTrackBean track : tracksDetails){ %>  
				.<%=track.getColorClass()%> {
					background-color: <%=track.getHexCode()%> !important;
					border: 1px solid <%=track.getBorder()%>  !important ;
                    color:<%=track.getFontColor()%>  !important;
				}
				
				.<%=track.getColorClass()%>:hover {
				    color:white !important;
					border: solid 2px #FFFFFF !important;
				}
		<%}}%>
		
	</style>
	
    
    <jsp:include page="common/jscss.jsp">
	<jsp:param value="Sessions Calendar" name="title"/>
    </jsp:include>
    
    
    
    <body>
    
    	<%@ include file="common/header.jsp" %>
    	
    	
        
        <div class="sz-main-content-wrapper">
        
        	<jsp:include page="common/breadcrum.jsp">
		<jsp:param value="Student Zone;Exam;Academic Calendar" name="breadcrumItems"/>
		</jsp:include>
        	
          <%try{ %>  
            <div class="sz-main-content menu-closed">
                    <div class="sz-main-content-inner">
              				<jsp:include page="common/left-sidebar.jsp">
								<jsp:param value="Academic Calendar" name="activeMenu"/>
							</jsp:include>
              				
              				
              				<div class="sz-content-wrapper examsPage">
              						<%@ include file="common/studentInfoBar.jsp" %>
              						
              						
              						<div class="sz-content">
              							<%@include file="common/messages.jsp" %>
              						
										<div class="calendar-operations row">
											<div class="col-lg-3 col-md-4 col-sm-12  col-xs-12">
													<div class="calendar-navigation">
														<h2 id="month"><%=monthYear %></h2>
														<div class="arrows"> <span id="prev" class="glyphicon glyphicon-chevron-left"></span> <span id="next" class="glyphicon glyphicon-chevron-right"></span>
															<div class="clearfix"></div>
														</div>
														<div class="clearfix"></div>
													</div>
											</div>
											<div class="col-lg-2 col-md-4 col-sm-12  col-xs-12">
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
											<div style="padding:15px" class="col-lg-2 col-md-4 col-sm-12  col-xs-12">
												<select class="form-control trackDropdown">
												<option value="" selected>Select Track </option>
													<% for(String track : trackList){ 
														if(StringUtils.isBlank(track)) { %>
<!-- 															<option value=" ">Common </option> -->
									    				<% }else{ %>
															<option 
																<%
																  	if(trackDropdown!=null){
																   		if(trackDropdown.equalsIgnoreCase(track)){out.println("selected"); }
																  	}
																%>
															  	value="<%=track%>"> <%=track%>
															</option>
									    				<% } %>
									    			<% } %>
							    				</select>
											</div>
											<div>
												<a class="btn <%= disableLink %>" style="background-color: #d2232a; color: white; float: right;" 
													href="/acads/student/viewSessionsTimeline"> <i class="fa-regular fa-calendar-check"></i>&nbsp;&nbsp; View Session Timeline 
												</a>
											</div>
																						
											<div class="clearfix"></div>
										</div>
										
										<div class="row">
											<div class="col-lg-7 col-md-7 col-sm-12  col-xs-12">
												<div class="panel panel-default" style="font-size: 12px">
												  	<div class="panel-body">
												  		<% for(String trackForDot : trackList){ %>
												  		<%
												  		    String trackColorClass="";
												  		    if(StringUtils.isBlank(trackForDot)){
												  		    	trackForDot="Common For All Batches";
												  		    }							
											  				trackColorClass=trackForDot.replaceAll("[-_ ]+", "");		
												  		%>												  			
														<span class="dot <%=trackColorClass%>"></span><%=trackForDot %>
												  		<%} %>
												  	</div>
												</div>
												<div id="calendar"></div>
																							
											</div>
											<div class="col-lg-5 col-md-5 col-sm-12  col-xs-12">
												<iframe id="sessionFrame" src="" width="100%" seamless="seamless" height="550" frameborder="0"></iframe>
											</div>
											
											<div class="clearfix"></div>
										</div>
              								
              						</div>
              				</div>
              		
                            
					</div>
            </div>
        </div>
            
  	
        <jsp:include page="common/footer.jsp"/>
            
		<%
		 Date dt = new Date();
		 SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		 String today = sdf.format(dt);
		 %>
		<%--  <script>
		
			$(document).ready(function() {
				
				$('#calendar').fullCalendar({
					header: {
						left: 'prev,next today',
						center: 'title',
						right: 'month,agendaWeek,agendaDay'
					},
					defaultDate: '<%=today%>',
					editable: false,
					eventLimit: true, // allow "more" link when too many events
					events: [
					     <% for(int i = 0 ; i < scheduledSessionList.size(); i++){
					    	 SessionDayTimeBean bean = scheduledSessionList.get(i);
					     %>    
					         
					         
						{
							title: '<%=bean.getSubject().replaceAll("'", "") + " - "+ bean.getSessionName()%>',
							start: '<%=bean.getDate()+"T"+ bean.getStartTime()%>',
							url: 'viewScheduledSession?id=<%=bean.getId()%>',
						},
						
						<%}%>
					],
					eventClick: function(event) {
				        if (event.url) {
				        	document.getElementById("sessionFrame").src=event.url;
				            return false;
				        }
				    }
				});
				
			});
		
			
			
		</script> --%>
		
		<script>
		
		<%-- $(document).ready(function() {
			
			$('#calendar').fullCalendar({
				header: {
					left: 'prev,next today',
					center: 'title',
					right: 'month,agendaWeek,agendaDay'
				},
				defaultDate: '<%=today%>',
				editable: false,
				eventLimit: true, // allow "more" link when too many events
				events: [
				     <% for(int i = 0 ; i < scheduledSessionList.size(); i++){
				    	 SessionDayTimeBean bean = scheduledSessionList.get(i);
				     %>    
				         
				         
					{
						title: '<%=bean.getSubject().replaceAll("'", "") + " - "+ bean.getSessionName()%>',
						start: '<%=bean.getDate()+"T"+ bean.getStartTime()%>',
						url: 'viewScheduledSession?id=<%=bean.getId()%>',
					},
					
					<%}%>
				],
				eventClick: function(event) {
			        if (event.url) {
			        	document.getElementById("sessionFrame").src=event.url;
			            return false;
			        }
			    }
			});
			
		}); --%>
		
		$('.trackDropdown').change(function() {
			  
			  window.location.replace("viewStudentTimeTable?track="+this.value);
			
			});
		
	
		$('#calendar').fullCalendar({
					defaultDate: '<%=today%>',
					header: false,
					aspectRatio: 1.4,
					/* dayClick: function(date, jsEvent, view) {
						console.log('Clicked on: ' + date.format());
					}, */
					eventLimit: true,
					events: [
						<% /* if(!"77777777777".equalsIgnoreCase(sapIdToBeBlocked)){ */
						    for(int i = 0 ; i < scheduledSessionList.size(); i++){
							SessionDayTimeAcadsBean bean = scheduledSessionList.get(i);
							 String track = bean.getTrack();
							 if(StringUtils.isBlank(track)){
								 track ="Common For All Batches";
							 }
							 String trackColorClass=track.replaceAll("[-_ ]+", "");	
				  				
						     String colorClass = trackColorClass;
							 
							 //To Change Calendar event color as per track
											 
							
								if(!StringUtils.isBlank(trackDropdown) || !StringUtils.isEmpty(trackDropdown)){						
									if(track.equalsIgnoreCase(trackDropdown)){
							%>    
										{
											title: '<%=bean.getSubject().replaceAll("'", "") + " - "+ bean.getSessionName()%>',
										<%-- 	start: '<%=bean.getDate()+"T"+ bean.getStartTime()%>', --%>
											start: new Date('<%=bean.getDate()+"T"+ bean.getStartTime()+"+05:30"%>'),
											url: 'viewScheduledSession?id=<%=bean.getId()%>&pssId=<%=bean.getPrgmSemSubId()%>',
											type: 'Session',
											className: '<%=colorClass%>'
										},
																 
									<%}
								 
								}else{%>
										
									{
										title: '<%=bean.getSubject().replaceAll("'", "") + " - "+ bean.getSessionName()%>',
									<%-- 	start: '<%=bean.getDate()+"T"+ bean.getStartTime()%>', --%>
										start: new Date('<%=bean.getDate()+"T"+ bean.getStartTime()+"+05:30"%>'),
										url: 'viewScheduledSession?id=<%=bean.getId()%>&pssId=<%=bean.getPrgmSemSubId()%>',
										type: 'Session',
										className: '<%=colorClass%>'
									},
										
								<%}
							 }%>

						<% 
						if(bookedExams!=null && !bookedExams.isEmpty()){
						for(ExamBookingTransactionAcadsBean bean : bookedExams){    %>       
						{
							title: 'Exam: <%=bean.getSubject().replaceAll("'", "")%>',
							start: '<%=bean.getExamDate()+"T"+ bean.getExamTime()%>',
							end: '<%=bean.getExamDate()+"T"+ bean.getExamEndTime()%>',
							type:'Exam',
							description:'Exam of <%=bean.getSubject().replaceAll("'", "")%> on <%=bean.getExamDate()%>, <%=bean.getExamTime()%>',
							url: '#',
							className: 'green-event'
						},
						
						<%}}%>
						
						<% 
						if(eventsList!=null && !eventsList.isEmpty()){
						for(EventBean event : eventsList){ 
							%>       
						{
							title: '<%=event.getEventName().replaceAll("'", "")%>',
							start: '<%= event.getStartDateTime().replace(" ","T") %>',
							end: '<%= event.getEndDateTime().replace(" ", "T") %>',
							type:'KeyEvent',
							description:'On <%=event.getStartDateTime().split(" ")[0] %> at <%=event.getStartDateTime().split(" ")[1].split(":")[0] %>:<%=event.getStartDateTime().split(" ")[1].split(":")[1] %>  <%=event.getDescription().replaceAll("\\r\\n", "<br>").replaceAll("'", "")%> ',
							url: '#',
							className: 'redvent'
						},
						
						<% 	}} %>
						
					],
					eventClick: function(calEvent, jsEvent, view) {
						
						if(calEvent.type == "Exam"){
							$('#modalTitle').html(calEvent.title);
				            $('#modalBody').html(calEvent.description);
				            $('#eventUrl').attr('href',calEvent.url);
				            $('#calendarModal').modal();
				            return false;
						}else if(calEvent.type == "KeyEvent"){
							$('#modalTitle').html(calEvent.title);
				            $('#modalBody').html(calEvent.description);
				            $('#eventUrl').attr('href',calEvent.url);
				            $('#calendarModal').modal();
				            return false;
						}else if(calEvent.type == "Session"){
							$(this).css('border-color', 'red');
							document.getElementById("sessionFrame").src=calEvent.url;
				            return false;
						}else if(calEvent.type == "Webinar"){
							$(this).css('border-color', 'red');
							document.getElementById("sessionFrame").src=calEvent.url;
				            return false;
						}
					},
				});
				
				function setCurrentTitle() {
					var view = $('#calendar').fullCalendar('getView');
					var start = $('#calendar').fullCalendar('getView').title;
					console.log(view,start);
					$('#month').html(start);
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
				

			 	var events = [];
	
				var sapid = '{"sapid": "<%= (String)request.getSession().getAttribute("userId_acads") %>"}';
			
	           	$.ajax({
	               	type: 'POST',
	               	url: '/careerservices/m/getAllWebinars',
	               	data: sapid,
	               	contentType: "application/json;",  
	               	dataType: "json",
			    	success: function(data, textStatus ){
						Object.keys(data).forEach(function (key1){
							var event = {
		                          	"id": data[key1]["id"],
		                         	"title": data[key1]["sessionName"],
		                        	"start": data[key1]["date"] + " " + data[key1]["startTime"],
									"type":'Webinar',
	                           		"url": '/careerservices/viewScheduledSession?aside=true&id=' + data[key1]["id"],
	                               	"className": 'red-event'
	                        };
							events.push(event);
	               		});
						$("#calendar").fullCalendar( 'addEventSource', events );
		  			}
		    	});
	           
		</script>
		
		<div id="calendarModal" class="modal fade">
		<div class="modal-dialog">
		    <div class="modal-content">
		        <div class="modal-header">
		            <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">×</span> <span class="sr-only">close</span></button>
		            <h4 id="modalTitle" class="modal-title"></h4>
		        </div>
		        <div id="modalBody" class="modal-body"> </div>
		        <div class="modal-footer">
		            <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
		        </div>
		    </div>
		</div>
		</div>

		
		
		<%
		String sessionIdFromDashboard = (String)request.getParameter("id");
		String pssId = (String)request.getParameter("pssId");
		if(sessionIdFromDashboard != null && !"".equals(sessionIdFromDashboard)){
		%>
		
		<script>
		document.getElementById("sessionFrame").src= "viewScheduledSession?id="+<%=sessionIdFromDashboard%>+"&pssId="+<%=pssId%>;
		</script>
		
		<%} } catch(Exception e){;
		  
		}%>
    </body>
</html>