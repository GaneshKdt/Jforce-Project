<%-- <%@page import="java.util.ArrayList"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Date"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.nmims.beans.SessionDayTimeBean"%>


<%
	String subject = (String)request.getAttribute("subject");
	HashMap<String, ArrayList<SessionDayTimeBean>> courseSessionsMap = (HashMap<String, ArrayList<SessionDayTimeBean>>)session.getAttribute("courseSessionsMap");
	ArrayList<SessionDayTimeBean> scheduledSessionList = courseSessionsMap.get(subject);
	int noOfSessions = scheduledSessionList != null ? scheduledSessionList.size() : 0; 
	boolean registeredForEvent = (boolean)session.getAttribute("registeredForEvent");
  
%>



<div class="course-sessions-m-wrapper">
	<div class="panel panel-default panel-courses-page">
		<div class="panel-heading" role="tab" id="">
			<h2>Sessions</h2>
			<!---TOP TABS-->
			<div class="custom-clearfix clearfix"></div>
			<ul class="topRightLinks list-inline">
				<li>
					<h3><span>Total: </span><%=noOfSessions %> Sessions</h3>
			
				</li>
				<li><a class="panel-toggler collapsed"  role="button" data-toggle="collapse" href="#collapseOne" aria-expanded="true"></a></li>
			</ul>
			<div class="clearfix"></div>
		</div>
		<div class="clearfix"></div>
		
			<%if(noOfSessions == 0){ %>
			<div id="collapseOne" class="panel-collapse collapse academic-schedule courses-panel-collapse" role="tabpanel">
			<div class="panel-body"> 
				<div class="no-data-wrapper">
					<p class="no-data"><span class="icon-academic-calendar"></span>No Upcoming Sessions Scheduled</p>
				</div>
			</div>
			</div>
			<%}else{ %>
			<div id="collapseOne" class="panel-collapse collapse in academic-schedule courses-panel-collapse" role="tabpanel" style="border:none">
			<div class="panel-body" > 
				<div class="row data-content">
				<div class="col-md-12 p-closed"> <i class="icon-academic-calendar"></i>
					<h4><span><%=noOfSessions %></span> Sessions Scheduled <span class="expand">Expand to view all Sessions</span></h4>
				</div>
				
				<% 
                       int count = 0;//Show only 4
                       for(SessionDayTimeBean bean : scheduledSessionList){
                       	count++;
                        if("Orientation Session".equals(bean.getSessionName())){
                    		bean.setSubject("Orientation");
                    	}
                       	String calendarColorClass="";
                       	if(count == 2){
                       		calendarColorClass = "cal-default blue-cal";
                       	}else if(count == 3){
                       		calendarColorClass = "cal-default red-cal";
                       	}
                       	if( ("Guest Lecture".equalsIgnoreCase(bean.getSessionName()) && !registeredForEvent))
						{
                    		continue;
						}
				%> 
						<a href="/acads/viewStudentTimeTable?id=<%=bean.getId()%>">
							<div class="col-md-6 col-lg-3 ">
	                            <div class="sz-calnr <%=calendarColorClass%>">
	                              <div class="sz-date">
	                                <p><%=bean.getDay() %>, <%=bean.getDate() %> </p>
	                              </div>
	                              <div class="sz-time">
	                                <p><%=bean.getStartTime() %></p>
	                                <!-- <button type="button">Join Now</button> -->
	                              </div>
	                              <div class="clearfix"></div>
	                              <div class="sz-calndr-info">
	                                <p><%=bean.getSessionName() %></p>
	                                <p class="cal-name">Prof. <%=bean.getFirstName() +  " " + bean.getLastName()%></p>
	                              </div>
	                              <div class="clearfix"></div>
	                            </div>
	                          </div>
                          </a>
				
				
				
				 <%}%>
				 
				 </div>
			 </div>
			 </div>
			<%}%>  
					
					
					
					<!-- <div class="col-md-6 col-lg-3">
						<div class="sz-calnr cal-default load-more-sessions">
							<a data-toggle="modal" data-target="#viewAllSessions" data-dismiss="modal">+4 More Sessions</a>
						</div>
					</div> -->
					
					
				
			
		
	</div>
</div> --%>

<%-- <%@page import="org.apache.commons.lang3.StringUtils"%> --%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Date"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.nmims.beans.SessionDayTimeStudentPortal"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>


<%
	String subject = (String) request.getAttribute("subject");
	HashMap<String, ArrayList<SessionDayTimeStudentPortal>> courseSessionsMap = (HashMap<String, ArrayList<SessionDayTimeStudentPortal>>) session
			.getAttribute("courseSessionsMap");
	ArrayList<SessionDayTimeStudentPortal> scheduledSessionList = courseSessionsMap.get(subject);
	int noOfSessions = scheduledSessionList != null ? scheduledSessionList.size() : 0;
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
%>


<style>
.nodata {
	vertical-align: middle;
	color: #a6a8ab;
	font: 1.00em "Open Sans";
	text-align: center;
	margin: 0;
}

.nodata-wrapper {
	padding: 20px;
	background: #fff;
	margin-bottom: 20px;
}
</style>
<div class="course-sessions-m-wrapper">
	<div class="panel-courses-page">
		<div class="panel-heading" role="tab" id="">
			<h2>Sessions</h2>
			<!---TOP TABS-->
			<div class="custom-clearfix clearfix"></div>
			<ul class="topRightLinks list-inline">
				<li>
					<% if (noOfSessions > 0) {%>
					<h3>
						<span>Scheduled:  ${totalSessions} </span> &nbsp; 
						<span>Conducted: ${totalConductedSessions} </span> &nbsp; 
						<span>Attended: ${totalAttendedSessions} </span> &nbsp; 
						<span>Pending: ${totalPendingSessions} </span>
					</h3>
					<% } %>
				</li>
				<li><a class="panel-toggler collapsed" role="button"
					data-toggle="collapse" href="#collapseOne" aria-expanded="true"></a></li>
			</ul>
			<div class="clearfix"></div>
		</div>
		
		<div class="clearfix"></div>
		<div id="collapseFour"
			class=" collapse in academic-schedule courses-panel-collapse panel-content-wrapper accordion-has-content"
			role="tabpanel">
			<div class="panel-body" style="padding: 20px;">




				<%
					if (noOfSessions == 0) {
				%>
				<div class="no-data-wrapper nodata-wrapper">

					<h6 class="no-data nodata">
						<span class="icon-academic-calendar"></span> No Upcoming Sessions Scheduled
					</h6>
				</div>

				<%
					} else {
				%>
				<div class="row data-content panel-body">
					<div class="col-md-12 " style="padding-bottom: 20px;">
						<div style="font-size: 12px;margin-bottom:1rem;">
							<div class="panel-body">
								<span class="dot blue-event"></span> Common For All Batches 
								<span class="dot weekDay-event"></span> WeekDay Batch 
								<span class="dot weekendSlow-event"></span> Weekend Batch - Slow Track 
								<span class="dot weekendFast-event"></span> Weekend Batch - Fast Track
								<span class="dot weekDaySlow-event"></span> WeekDay Batch - Slow Track
								<span class="dot weekDayFast-event"></span> WeekDay Batch - Fast Track
							</div>
						</div>
						<i class="icon-academic-calendar"></i> <span><%=noOfSessions%></span>
						Sessions Scheduled

					</div>

					<%
						/* int count = 0;//Show only 4 */
							for (SessionDayTimeStudentPortal bean : scheduledSessionList) {
								/* count++; */
								if ("Orientation Session".equals(bean.getSessionName())) {
									bean.setSubject("Orientation");
								}
								String track = bean.getTrack();
								if (track.equals("") || track==null) {
									track = " ";
								}
								String colorClass = "";

								//To Change Calendar event color as per track
								switch (track.toLowerCase()) {

								case "weekday batch":
									colorClass = "cal-default purple-cal";
									break;

								case "weekend batch - fast track":
									colorClass = "cal-default orange-cal";
									break;

								case "weekend batch - slow track":
									colorClass = "cal-default black-cal";
									break;
									
								case "weekday batch - slow track":
									colorClass = "cal-default green-cal";
									break;
									
								case "weekday batch - fast track":
									colorClass = "cal-default stone-cal";
									break;

								default:
									colorClass = "cal-default blue-cal";
									break;
								}
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
					<a href="/acads/viewStudentTimeTable?id=<%=bean.getId()%>">
						<div class="col-md-6 col-lg-3 ">
							<div class="sz-calnr <%=colorClass%>" >
								<div class="sz-date">
									<p><%=bean.getDay()%>,
										<%=bean.getDate()%>
									</p>
								</div>
								<div class="sz-time"> 
									<p><%=bean.getStartTime()%></p>
									<!-- <button type="button">Join Now</button> -->
								</div>
								<div class="clearfix"></div>
								<div class="sz-calndr-info">
									<p><%=bean.getSessionName()%></p>
									<p>
									<%if(bean.getTrack()==null || bean.getTrack().equals("")) { %>
										<%="Common For All Batches"%>
									<% } else{ %>
										<%=bean.getTrack()%>
									<% } %>
									</p>
									<p class="cal-name">
										Prof.
										<%=bean.getFirstName() + " " + bean.getLastName()%></p>
								</div>
								<div class="clearfix"></div>
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
				
<!-- 			If no session then don't show attendance for session -->
				<% if (noOfSessions > 0) {%>
				
				<div class="">
					<table class="table table-striped showAllEntries" id="attendanceEntries">
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
									<td colspan="6">
										<div class="">
											<div class="no-data-wrapper" style="text-align: center">
												<p class="no-data ">
													<span class="icon-academic-calendar"></span> No Scheduled
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
									<td>${status.count }</td>
									<td><a href="/acads/viewStudentTimeTable?id=${attendance.id}">
										<c:out value="${attendance.sessionName}" /></a>
									</td>
<%-- 									<td><c:if test="${not empty attendance.facultyFirstName and not empty attendance.facultyLastName}"><a href="/studentportal/facultyProfile?facultyId=${attendance.facultyId}" target="_blank"><c:out value="${attendance.facultyFirstName}"/> <c:out value="${attendance.facultyLastName}"/></a></c:if></td> --%>
									<td><c:if test="${not empty attendance.facultyFirstName and not empty attendance.facultyLastName}"><c:out value="${attendance.facultyFirstName}"/> <c:out value="${attendance.facultyLastName}"/></c:if></td>
									<td><c:if test="${not empty attendance.track}"><c:out value="${attendance.track}" /></c:if><c:if test="${empty attendance.track}">Common For All Batches</c:if></td>
									<td><c:out value="${attendance.date}" /></td>
									<td><c:out value="${attendance.startTime}" /></td>
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
				</div>
				
				<% } %>


			</div>
		</div>
	</div>
</div>
<script>
$(document).ready(function() {
	try{
   		$('#attendanceEntries').DataTable();
	}catch(e){
		console.error("error");
	}
} );
</script>