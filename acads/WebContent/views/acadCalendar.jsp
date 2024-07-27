<%-- <%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
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
</html> --%>

 

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
 <!DOCTYPE html>
<%@page import="java.util.Calendar"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Date"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.nmims.beans.SessionDayTimeAcadsBean"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<jsp:useBean id="now" class="java.util.Date"/>    
<%-- <fmt:formatDate value="${now}" dateStyle="long"/>
<fmt:formatDate value="${now}" pattern="yyyy-dd-mm HH:mm:s"/> --%>


<%
ArrayList<SessionDayTimeAcadsBean> scheduledSessionList = (ArrayList<SessionDayTimeAcadsBean>)request.getAttribute("scheduledSessionList");

Calendar cal = Calendar.getInstance();
String monthYear = new SimpleDateFormat("MMM YYYY").format(cal.getTime());
String sessionId = request.getParameter("sessionId"); 

for(SessionDayTimeAcadsBean sessions :scheduledSessionList){
	if("Orientation Session".equals(sessions.getSessionName()) && "Verizon".equals(sessions.getCorporateName())){ // added temporary
		sessions.setSubject("Orientation");
		sessions.setStartTime("16:30:00");
	}else if("Orientation Session".equals(sessions.getSessionName())){
		sessions.setSubject("Orientation");
		//sessions.setStartTime("17:00:00");
	}
}
%>

<html lang="en">
    <jsp:include page="common/jscss.jsp">
	<jsp:param value="Sessions Calendar" name="title"/>
    </jsp:include>
    

    
    <body>
    
    	<%@ include file="adminCommon/header.jsp" %>
    	
    	
        
        <div class="sz-main-content-wrapper">
        
        <jsp:include page="adminCommon/breadcrum.jsp">
			<jsp:param value="Student Zone;Exam;Academic Calendar" name="breadcrumItems"/>
		</jsp:include>
        	
            
            <div class="sz-main-content menu-closed">
                    <div class="sz-main-content-inner">
              				<jsp:include page="adminCommon/left-sidebar.jsp">
								<jsp:param value="Academic Calendar" name="activeMenu"/>
							</jsp:include>
              				
              				
              				<div class="sz-content-wrapper examsPage">
              						<%@ include file="adminCommon/adminInfoBar.jsp" %>
              						
              						
              						<div class="sz-content">
										<div class="calendar-operations row">
											<div class="col-md-3 col-sm-6">
													<div class="calendar-navigation">
														<h2 id="month"><%=monthYear %></h2>
														<div class="arrows"> <span id="prev" class="glyphicon glyphicon-chevron-left"></span> <span id="next" class="glyphicon glyphicon-chevron-right"></span>
															<div class="clearfix"></div>
														</div>
														<div class="clearfix"></div>
													</div>
											</div>
											<div class="col-md-3  col-sm-6">
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
											<%if(roles.indexOf("Acads Admin") != -1){%>	
											<div style="padding:15px" class="col-lg-2 col-md-4 col-sm-12  col-xs-12">
												<select class="form-control filterDropdown " id="trackValue">
												<option value="" disabled="disabled" selected="selected">Select Track</option>
													<c:forEach var="track" items="${trackList}" varStatus="status">
														<option value="${track }">${ track} </option>
													</c:forEach>
													<option value="noTrack" >No Track Sessions</option>
							    				</select>
											</div>
											
											<div style="padding:15px;margin-right:14px;" class="col-lg-2 col-md-4 col-sm-12  col-xs-12">
												<select id="subjectCodeId"  class="form-control filterDropdown "> 
														<option value="" disabled="disabled" selected="selected">Select  subject</option>
														<c:forEach items="${subjectList}" var="element">
															<option value="${element.subjectCodeId}">${element.subjectcode} ( ${element.subjectName} )</option>
													</c:forEach>
												</select>
											</div>
										
											<div style="padding:15px" class="col-lg-2 col-md-4 col-sm-12  col-xs-12">
												<select class="form-control filterDropdown " id="semValue">
												<option value="" disabled="disabled" selected="selected">Select Sem</option>
													<c:forEach var="sem" items="${semList}" varStatus="status">
														<option value="${sem }">${ sem} </option>
													</c:forEach>
							    				</select>
											</div>
											
											<div style="padding:15px" class="col-lg-2 col-md-4 col-sm-12  col-xs-12">
												<select class="form-control filterDropdown " id="programValue">
													<option value="" disabled="disabled" selected="selected">Select Program</option>
													<c:forEach var="program" items="${programIdMap}" >
														<option value="${program.key }">${ program.value} </option>
													</c:forEach>
							    				</select>
											</div>
											<div style="margin-top:4px;" class="form-group col-lg-2 col-md-4 col-sm-12  col-xs-12">
												<button id="submit" name="submit"  class="btn btn-large btn-primary" onclick="window.location.href='viewTimeTable';">Reset Filter</button>
											</div>
											<%} %>
										</div>
										
										<div class="row">
											<div class="col-lg-6 col-md-6">
												<div id="calendar"></div>
											</div>
											<div class="col-lg-6 col-md-6">
												<iframe id="sessionFrame" src="" width="100%" seamless="seamless" height="550" frameborder="0"></iframe>
											</div>
											
											<div class="clearfix"></div>
										</div>
              								
              						</div>
              				</div>
              		
                            
					</div>
            </div>
        </div>
            

        <jsp:include page="adminCommon/footer.jsp"/>
  

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
		var getUrlParameter = function getUrlParameter(sParam) {
		    var sPageURL = decodeURIComponent(window.location.search.substring(1)),
		        sURLVariables = sPageURL.split('&'),
		        sParameterName,
		        i;

		    for (i = 0; i < sURLVariables.length; i++) {
		        sParameterName = sURLVariables[i].split('=');

		        if (sParameterName[0] === sParam) {
		            return sParameterName[1] === undefined ? true : sParameterName[1];
		        }
		    }
		};
		var id = getUrlParameter('id');
		if(id != null){
			console.log("******************************************");
			console.log("inside id != null condition id : " + id);
			console.log("******************************************");
			document.getElementById("sessionFrame").src='viewScheduledSession?id=' + id;	
		}
		
		$('#calendar').fullCalendar({
			
					defaultDate: '<%=today%>',
					header: false,
					aspectRatio: 1.4,
					eventLimit: true,
					events: [ 
						<% for(int i = 0 ; i < scheduledSessionList.size(); i++){
							 SessionDayTimeAcadsBean bean = scheduledSessionList.get(i);
						%>    
						    
						    
						{
							title: '<%=bean.getSubject().replaceAll("'", "") + " - "+ bean.getSessionName()%>',
							start: '<%=bean.getDate()+"T"+ bean.getStartTime()%>',
							url: 'viewScheduledSession?id=<%=bean.getId()%>',
							className: 'blue-event'
						},
						<% } %>
						
					],
					
					eventClick: function(calEvent, jsEvent, view) {
						$(this).css('border-color', 'red');
						document.getElementById("sessionFrame").src=calEvent.url;
			            return false;
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
	
				var facultyId = '{"facultyId": "<%= (String)request.getSession().getAttribute("userId_acads") %>"}';
			
	           	$.ajax({
	               	type: 'POST',
	               	url: '/careerservices/getFacultySessions',
	               	data: facultyId,
	               	contentType: "application/json;",  
	               	dataType: "json",
			    	success: function(data, textStatus ){
					Object.keys(data).forEach(function (key1){
						var event = {
	                          	"id": data[key1]["id"],
	                         	"title": data[key1]["sessionName"],
	                        	"start": data[key1]["date"] + " " + data[key1]["startTime"],
								"type":'Webinar',
                           		"url": '/careerservices/viewScheduledSession?id=' + data[key1]["id"],
                               	"className": 'red-event'
                        };
						events.push(event);
               		});
					$("#calendar").fullCalendar( 'addEventSource', events );
		  		}
		    });

	        $.ajax({
	               	type: 'POST',
	               	url: '/acads/getInterview',
	               	data: facultyId,
	               	contentType: "application/json;",  
	               	dataType: "json",
			    	success: function(data, textStatus ){
					Object.keys(data).forEach(function (key1){
						var event = {
	                          	"id": data[key1]["id"],
	                         	"title": 'Interview',
	                        	"start": data[key1]["startdate"] + " " + data[key1]["time"],
								"type":'Interview',
                           		"url": '/careerservices/viewScheduledSession?id=' + data[key1]["id"],
                               	"className": 'red-event'
                        };
						events.push(event);
               		});
					$("#calendar").fullCalendar( 'addEventSource', events );
		  		}
		    }); 

	        
	        var filterUrl = 'viewTimeTableFilter';
	        
	        $('.filterDropdown').on('change', function(){
	        	  var option = $(this).find('option:selected');
	        		if(option.val()){
	        			var data = {
					        	track : $('#trackValue').val(),
			        			subjectCodeId : $('#subjectCodeId').val(),
			        			sem : $('#semValue').val(),
			        			programId : $('#programValue').val()
					       }
	        			populateCalendar(filterUrl,data);
	        	  }
	        	})

	    function populateCalendar(url,data){
	    	$('#calendar').fullCalendar('removeEvents');
	    	var events = [];
  				$.ajax({
        	  	 	type: 'POST',
           			url: '/acads/m/'+url,
           			data: JSON.stringify(data),
           			cache: false,
           			contentType : "application/json; charset=utf-8",
	    			success: function(data, textStatus ){
					Object.keys(data).forEach(function (key1){
						var event = {
                    	 	 	"id": data[key1]["id"],
                     			"title": data[key1]["subject"]+ " - "+ data[key1]["sessionName"],
                    			"start": new Date(data[key1]["date"] + " " + data[key1]["startTime"]),
								"type":'Session',
                   				"url": 'viewScheduledSession?id='+ data[key1]["id"]+"'",
                   				"className": 'blue-event'
                			};
					events.push(event);
				
       			});           		
       			$("#calendar").fullCalendar( 'addEventSource', events );
  			}
    		}); 
		}
		
		</script>
    </body>
</html>





