<%@page import="java.util.Collections"%>
<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="java.util.List"%>
<%@page import="java.util.HashSet"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Date"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.nmims.beans.SessionDayTimeStudentPortal"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@page import="com.nmims.beans.SessionTrackBean"%>

<%
	String subject = (String) request.getAttribute("subject");
	HashMap<String, ArrayList<SessionDayTimeStudentPortal>> courseSessionsMap = (HashMap<String, ArrayList<SessionDayTimeStudentPortal>>) session
			.getAttribute("courseSessionsMap");
	ArrayList<SessionDayTimeStudentPortal> scheduledSessionList = courseSessionsMap.get(subject);
	int noOfSessions = scheduledSessionList != null ? scheduledSessionList.size() : 0;
	HashSet<String> tracks = new HashSet<String>();
	List<String> trackList = new ArrayList<String>();
	if(noOfSessions > 0){
		for(SessionDayTimeStudentPortal sessions :scheduledSessionList){
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
	}
	
	boolean registeredForEvent = false;
	try{
		registeredForEvent = (boolean) session.getAttribute("registeredForEvent");
	}catch(Exception e){
	}
	String courseHomeCalendarExpandClass = "";
	String CourseHomeCalendarBorderStyle = "";
	if (noOfSessions > 0) {
		courseHomeCalendarExpandClass = "in"; //this will open Calendar section, otherwise it will be collapsed by Default 
		CourseHomeCalendarBorderStyle = "border:none;";//So that top blue border does not appear. It should show only when there are no sessions
	}
	ArrayList<SessionTrackBean> tracksDetails = (ArrayList<SessionTrackBean>) session.getAttribute("trackDetails");

%>
<head>


<!-- <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.3.0/css/all.min.css" integrity="sha512-SzlrxWUlpfuzQ+pcUCosxcglQRNAq/DZjVsC0lE40xsADsfeQoEypE+enwcOiGjk/bSuGGKHEyjSoQ1zVisanQ==" crossorigin="anonymous" referrerpolicy="no-referrer" /> -->
</head>
 <style>
 
 
.nodata {
	vertical-align: middle;
	color: #a6a8ab;
	font: 1.00em "Open Sans";
	text-align: center;
	margin: 0;
}


.nodata-wrapper {
	padding: 30px;
	background: #fff;
	margin-bottom: 20px;
} 

 .nav-tabs .nav-link {
  color: #6C757D;
  background-color: #F2F2F2;
}
	
.nav-tabs .nav-link.active,
.nav-tabs .nav-item.show .nav-link {
    color: #3c3c3c;
   background-color:  #FFFFFF;
   
   
} 


 <% if(tracksDetails!=null && !tracksDetails.isEmpty()){
		for(SessionTrackBean track : tracksDetails){ %>  
			.<%=track.getColorClass()%> {		
			 color:<%=track.getHexCode()%> !important;
				
			}	
			
			.<%=track.getColorClass()%>-info {
				background-color: <%=track.getHexCode()%>;
				border: 1px solid #000000;
			}
			
			
					
	<% }} %> 
		
</style>

 <div class="container-fluid float-start bg-white rounded mb-4">

<div class="row mt-2 mb-0">
   <span>
		  <h4 class="text-uppercase text-danger">Sessions</h4>
		
		 <!------------------------------------------seesion information ------------------------------------------------------------->	
	
		
	           <ul class=" list-inline mb-1">
				<li class="fw-bold">
					<% if (noOfSessions > 0) {%>
				
						<span>Scheduled:  ${totalSessions} </span> &nbsp; 
						<span>Conducted: ${totalConductedSessions} </span> &nbsp; 
						<span>Attended: ${totalAttendedSessions} </span> &nbsp; 
						<span>Pending: ${totalPendingSessions} </span>
				
					<% } %>
				</li>
			      <li><a class="panel-toggler collapsed" role="button" data-toggle="collapse" href="#collapseOne" aria-expanded="true"></a>
			     </li>
			</ul>
			</span>
		 <div class="clearfix"></div>
		
	</div>
     
     <!------------------------------------------Card Container starts here  ------------------------------------------------------------->	

			 		   
	        <!-------------------------------Shows only when there is no sessions Scheduled--------------------------->
				<%
					if (noOfSessions == 0) {
				%>
				<div class="no-data-wrapper nodata-wrapper">
					<h6 class="no-data nodata fw-bold">
						<span class="fa-regular fa-circle-play "></span> No Upcoming Sessions Scheduled
					</h6>
				</div>

				<%
					} else {
				%>
							
				<div class="row data-content panel-body mt-0">
					<div class="col-md-12  mt-0" >
						<div class="mb-2">
							<div class="panel-body">
								<% for(String track : trackList){ 
								if(StringUtils.isBlank(track)){
									track="Common For All Batches";
								}							
                                String classForColor=track.replaceAll("[-_ ]+", "");	
						     	String colorClass = classForColor+"-info"; %>
						     	     <span class="dot <%=colorClass%>"></span> <%=track%>                                       
						  			
						  		<%}%>
							</div>
						</div>
					 <span>	<i class="fa-regular fa-calendar-days"></i> <%=noOfSessions%> Sessions Scheduled</span>
						

					</div>
				</div>
		
				
					
<div class="row row-cols-lg-2  row-cols-md-1 row-cols-xl-4 mt-3 ">
                         
                         
            <!----------------------------------------- Sessions track list part  ----------------------------->

					<%
						/* int count = 0;//Show only 4 */
							for (SessionDayTimeStudentPortal bean : scheduledSessionList) {
								/* count++; */
								if ("Orientation Session".equals(bean.getSessionName())) {
									bean.setSubject("Orientation");
								}
								String track = bean.getTrack();
								if (track.equals("") || track==null) {
									track = "Common For All Batches";
								}
								
							    String classForColor=track.replaceAll("[-_ ]+", "");	
							    String colorClass = "cal-default"+" "+classForColor;

								//To Change Calendar event color as per track
								
								/* String calendarColorClass="";
								if(count == 2){
									calendarColorClass = "cal-default blue-cal";
								}else if(count == 3){
									calendarColorClass = "cal-default red-cal";
								} */
								if (("Guest Lecture".equalsIgnoreCase(bean.getSessionName()) && !registeredForEvent)) {
									continue;
								}
					%>
					
					
					
						
							
<!-------------------------------------------- Sessions Cards Starts Here ----------------------------------------------->
								
	<a href="/acads/student/viewStudentTimeTable?id=<%=bean.getId()%>&pssId=${programSemSubjectId }">				
	<input type="hidden" class="myField" value="<%=bean.getDate()+"T"+bean.getStartTime()+"+05:30"%>"/>  
	        
     <div class="card" style="margin-bottom:25px;">  
	          <div class="card-body ">	
	         				   				   				   
			                 <div class="sz-calnr">							
				                 <div class="sz-date">	
									<p><%=bean.getDay()%>, 
                                    <span class="myDate card-title" style="float: right;">
                                        <%=bean.getDate()%>
                                    </span>
                                    </p>
								</div>
							
								
								<div class="sz-time"> 
								<h4 style="color:black" class="myTime"><%=bean.getStartTime()%></h4>
								</div>
															
								<div class="sz-calndr-info">
								
								
									<p class="text-truncate w-100"><%=bean.getSessionName()%></p>
																	 
								    <p class="<%=colorClass%>"><%=track %></p> 
									
									
									<p class="cal-name">
										Prof.
										<%=bean.getFirstName() + " " + bean.getLastName()%></p>
										
								           </div>							   
							           </div>
						          </div>
						     </div>
				
					</a>
		

					<%
						}
					%>
					
				</div>
     
         
				<%
					}
				%>
 </div>

				  	    
<!-- If no session then don't show attendance for session -->  
                      
                      
             
				<% if (noOfSessions > 0) { %>
				
		
		<table class="table"  id="attendanceEntries">
						<thead>
							<tr>
								<th>Sr. No1</th>
								<th>Sessions Name</th>
								<th>Prof.</th>
								<th>Track</th>
								<th>Date</th>
								<th>Time</th>
								<th>Attended</th>

							</tr>
						</thead>
						<tbody>

							<c:if test="${fn:length(SessionsAttendanceforSubjectList) eq 0}">
								<tr>
									<td colspan="7">
										<div class="">
											<div class="no-data-wrapper" style="text-align: center">
												<p class="no-data ">
													<span class="fa-regular fa-calendar-days"></span> No Scheduled
													Sessions
												</p>
											</div>
										</div>
									</td>

								</tr>
							</c:if>

							<c:forEach var="attendance"
								items="${SessionsAttendanceforSubjectList}" varStatus="status">
								<tr>
									<input type="hidden" class="myField" value="${attendance.date}T${attendance.startTime}+05:30" />
									<td>${status.count }</td>
									<td><a href="/acads/student/viewStudentTimeTable?id=${attendance.id}&pssId=${programSemSubjectId }">
										<c:out value="${attendance.sessionName}" /></a>
									</td>
<%-- 									<td><c:if test="${not empty attendance.facultyFirstName and not empty attendance.facultyLastName}"><a href="/studentportal/facultyProfile?facultyId=${attendance.facultyId}" target="_blank"><c:out value="${attendance.facultyFirstName}"/> <c:out value="${attendance.facultyLastName}"/></a></c:if></td> --%>
									<td><c:if test="${not empty attendance.facultyFirstName and not empty attendance.facultyLastName}"><c:out value="${attendance.facultyFirstName}"/> <c:out value="${attendance.facultyLastName}"/></c:if></td>
									<td><c:if test="${not empty attendance.track}"><c:out value="${attendance.track}" /></c:if><c:if test="${empty attendance.track}">Common For All Batches</c:if></td>
									<td class="myDate"><c:out value="${attendance.date}" /></td>
									<td class="myTime"><c:out value="${attendance.startTime}" /></td>
									<%-- <td><c:out value="${attendance.attended}"/></td> --%>
									<c:if
										test="${attendance.conducted eq '1' and attendance.attended eq 'Y'}">
										<td><c:out value="Yes" /></td>
									</c:if>
									<c:if
										test="${attendance.conducted eq '1' && attendance.attended eq null}">
										<td><c:out value="No" /></td>
									</c:if>
									<c:if test="${attendance.conducted eq '0'}">
										<td><c:out value="" /></td>
									</c:if>

								</tr>
					</c:forEach>
				</tbody>
			</table>
		 
	<br>
	

				
				<% } %>
	    






<script>


 $(document).ready(function() {
	try{
	
   		$('#attendanceEntries').DataTable();
   		responsive: true
   		
   		
	}catch(e){
		console.error("error");
	}
} );

var elements = document.querySelectorAll(".myField");
for (var i=0; i < elements.length; i++) {
	document.getElementsByClassName("myDate")[i].innerHTML = moment(elements[i].value).format("DD-MMM-YYYY");
	document.getElementsByClassName("myTime")[i].innerHTML = moment(elements[i].value).format("hh:mm:ss A");
}
</script>

