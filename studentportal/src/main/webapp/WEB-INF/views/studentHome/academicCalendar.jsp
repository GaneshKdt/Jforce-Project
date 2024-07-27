<%@page import="com.nmims.controllers.BaseController"%>
<%@page import="com.itextpdf.text.BaseColor"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Date"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.nmims.beans.SessionDayTimeStudentPortal"%>


<%
	BaseController aCon = new BaseController();
	ArrayList<SessionDayTimeStudentPortal> scheduledSessionList = (ArrayList<SessionDayTimeStudentPortal>)session.getAttribute("scheduledSessionList_studentportal");
	//System.out.println("____________________________________________________________________ scheduledSessionList"+scheduledSessionList);
	int noOfSessions = scheduledSessionList != null ? scheduledSessionList.size() : 0;
	boolean registeredForEvent;
	if(aCon.checkLead(request, response))
		registeredForEvent = false;
	else
		registeredForEvent = (boolean)session.getAttribute("registeredForEvent");
%>

<script>
function notifyLead(){
	alert("Please enrole for the complete program...");
}
</script>

<style>
.academic-schedule .panel-body .blue-cal {
	border: solid 2px #ffffff;
	border-top-color: #26a9e0;
	border-top-width: 4px;
}

.academic-schedule .panel-body .blue-cal:hover {
	border: solid 2px #26a9e0;
	border-top-color: #26a9e0;
	border-top-width: 4px;
}
.academic-schedule .panel-body .weekDay-event {
	border: solid 2px #ffffff;
	border-top-color: #D81B60;
	border-top-width: 4px;
}

.academic-schedule .panel-body .weekDay-event:hover {
	border: solid 2px #D81B60;
	border-top-color: #D81B60;
	border-top-width: 4px;
}

.academic-schedule .panel-body .weekendSlow-event {
	border: solid 2px #ffffff;
	border-top-color: #455A64;
	border-top-width: 4px;
}

.academic-schedule .panel-body .weekendSlow-event:hover {
	border: solid 2px #455A64;
	border-top-color: #455A64;
	border-top-width: 4px;
}

.academic-schedule .panel-body .weekendFast-event {
	border: solid 2px #ffffff;
	border-top-color: #E65100;
	border-top-width: 4px;
}

.academic-schedule .panel-body .weekendFast-event:hover {
	border: solid 2px #E65100;
	border-top-color: #E65100;
	border-top-width: 4px;
}

.academic-schedule .panel-body .weekDaySlow-event {
	border: solid 2px #ffffff;
	border-top-color: #3c763d;
	border-top-width: 4px;
}

.academic-schedule .panel-body .weekDaySlow-event:hover {
	border: solid 2px #3c763d;
	border-top-color: #3c763d;
	border-top-width: 4px;
}

.academic-schedule .panel-body .weekDayFast-event {
	border: solid 2px #ffffff;
	border-top-color: #795548;
	border-top-width: 4px;
}

.academic-schedule .panel-body .weekDayFast-event:hover {
	border: solid 2px #795548;
	border-top-color: #795548;
	border-top-width: 4px;
}

</style>

<%if(noOfSessions != 0){ %>
<div class="calendarWrapper">
	<div class="panel panel-default bgColorNone">
		<div class="panel-heading" role="tab" id="">
			<h4 class="panel-title">ACADEMIC CALENDAR</h4>
			<!---TOP TABS-->
			<div class="custom-clearfix clearfix"></div>
			<ul class="topRightLinks list-inline">
				<li><a href="#upcoming" aria-controls="upcoming" role="tab"
					data-toggle="tab" class="active">UPCOMING</a></li>
				<!-- <li class="borderRight"><a href="#week" aria-controls="week" role="tab" data-toggle="tab">THIS WEEK</a></li> -->
				<li><a href="/acads/viewStudentTimeTable">SEE ALL</a></li>
				<li><a class="panel-toggler collapsed" role="button"
					data-toggle="collapse" href="#collapseOne" aria-expanded="true"></a></li>
			</ul>
			<div class="clearfix"></div>
		</div>

		<%if(noOfSessions == 0){ %>
		<div id="collapseOne"
			class="panel-collapse collapse academic-schedule courses-panel-collapse"
			role="tabpanel">
			<div class="panel-body bgColorNone">
				<div class="no-data-wrapper">
					<p class="no-data">
						<span class="icon-academic-calendar"></span>No Sessions Scheduled
						This week
					</p>
				</div>
			</div>
		</div>
		<%}else{ %>
		<div id="collapseOne"
			class="panel-collapse collapse in academic-schedule courses-panel-collapse"
			role="tabpanel">
			<div class="panel-body bgColorNone" style="border: none">
				<!-- Tab panes -->
				<div class="tab-content">
					<div role="tabpanel" class="tab-pane active" id="upcoming">
						<div class="row data-content">
							<div class="col-md-12 p-closed">
								<div class="no-data-wrapper">
									<p class="no-data">
										<span class="icon-academic-calendar"></span> 4 Calendar Events
									</p>
								</div>
							</div>

							<% 
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                		SimpleDateFormat dateFormatter = new SimpleDateFormat("EEEE, dd-MMM-yyyy");
                		
                     	int count = 0;//Show only 4
                        for(SessionDayTimeStudentPortal bean : scheduledSessionList){
                        	count++;
                        	Date formattedDate = formatter.parse(bean.getDate());
                			String formattedDateString = dateFormatter.format(formattedDate);
                			if("Orientation Session".equals(bean.getSessionName()) && "Verizon".equals(bean.getCorporateName())){ // added temporary
                        		bean.setSubject("Orientation");
                        		bean.setStartTime("16:30:00");
                        	}else if("Orientation Session".equals(bean.getSessionName())){
                        		bean.setSubject("Orientation");
                        		bean.setStartTime("17:00:00");
                        	}

                			String track = bean.getTrack();
							if (track.equals("") || track==null) {
								track = " ";
							}
							String colorClass = "";

							//To Change Calendar event color as per track
							switch (track) {

							case "WeekDay Batch":
								colorClass = "cal-default purple-cal";
								break;

							case "Weekend Batch - Fast Track":
								colorClass = "cal-default orange-cal";
								break;

							case "Weekend Batch - Slow Track":
								colorClass = "cal-default black-cal";
								break;
								
							case "WeekDay Batch - Slow Track":
								colorClass = "cal-default weekDaySlow-event";
								break;
								
							case "WeekDay Batch - Fast Track":
								colorClass = "cal-default weekDayFast-event";
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
							
                        	if( ("Guest Lecture".equalsIgnoreCase(bean.getSessionName()) && !registeredForEvent))
							{
                        		continue;
							}
					    %>

							<a href="/acads/viewStudentTimeTable?id=<%=bean.getId()%>"><div
									class="col-md-6 col-lg-3 ">
									<div class="sz-calnr <%=colorClass%> ">
										<div class="sz-date">
											<p><%=formattedDateString%>
												<span class="cal-session">STUDY SESSION</span>
											</p>
										</div>
										<div class="sz-time">
											<p><%=bean.getStartTime() %></p>
											<!-- <button type="button">Join Now</button> -->
										</div>
										<div class="clearfix"></div>
										<div class="sz-calndr-info">
											<p>
												<strong><%=bean.getSubject() %></strong>
											</p>
											<p><%=bean.getSessionName() %></p>
											<p>
												<%if(bean.getTrack()==null || bean.getTrack().equals("")) { %>
													<%="Common For All Batches"%>
												<% } else{ %>
													<%=bean.getTrack()%>
												<% } %>
												</p>
											<p class="cal-name">
												Prof.
												<%=bean.getFirstName() +  " " + bean.getLastName()%></p>
										</div>
										<div class="clearfix"></div>
									</div>
								</div>
								</a>

							<%
						    if(count == 4){
						    	break; //Show only 4 sessions
						    }
                        
                        } %>


						</div>
					</div>

					<!--TAB PANEL 2-->
					<div role="tabpanel" class="tab-pane" id="week">...</div>
				</div>
			</div>
		</div>
		<%} %>

	</div>
</div>
<% } %>