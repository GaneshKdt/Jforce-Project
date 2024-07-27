<%@page import="com.nmims.controllers.BaseController"%>
<%@page import="com.itextpdf.text.BaseColor"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Date"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.nmims.beans.SessionDayTimeStudentPortal"%>


<%
	BaseController aCon = new BaseController();
	ArrayList<SessionDayTimeStudentPortal> scheduledSessionList = (ArrayList<SessionDayTimeStudentPortal>)session.getAttribute("scheduledSessionList_studentportal");
	ArrayList<Integer> liveSessionPssIdsList = (ArrayList<Integer>) request.getSession().getAttribute("liveSessionPssIdAccess_studentportal");
	String pssIdsCommaSeparated = "";
	if (liveSessionPssIdsList != null && liveSessionPssIdsList.size() > 0) {
		pssIdsCommaSeparated = org.apache.commons.lang3.StringUtils.join(liveSessionPssIdsList, ",");
	}
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
	border-top-color: #9DB5FB;
	border-top-width: 4px;
}

.academic-schedule .panel-body .blue-cal:hover {
	border: solid 2px #9DB5FB;
	border-top-color: #9DB5FB;
	border-top-width: 4px;
}

.academic-schedule .panel-body .weekDay-event {
	border: solid 2px #ffffff;
	border-top-color: #FF9999;
	border-top-width: 4px;
}

.academic-schedule .panel-body .weekDay-event:hover {
	border: solid 2px #FF9999;
	border-top-color: #FF9999;
	border-top-width: 4px;
}

.academic-schedule .panel-body .weekend-event {
	border: solid 2px #000000;
	border-top-color: #6D9BB0;
	border-top-width: 4px;
}

.academic-schedule .panel-body .weekend-event:hover {
	border: solid 2px #6D9BB0;
	border-top-color: #6D9BB0;
	border-top-width: 4px;
}

.academic-schedule .panel-body .weekendSlow-event {
	border: solid 2px #ffffff;
	border-top-color: #FFC999;
	border-top-width: 4px;
}

.academic-schedule .panel-body .weekendSlow-event:hover {
	border: solid 2px #FFC999;
	border-top-color: #FFC999;
	border-top-width: 4px;
}

.academic-schedule .panel-body .weekendFast-event {
	border: solid 2px #ffffff;
	border-top-color: #73A366;
	border-top-width: 4px;
}

.academic-schedule .panel-body .weekendFast-event:hover {
	border: solid 2px #73A366;
	border-top-color: #73A366;
	border-top-width: 4px;
}

.academic-schedule .panel-body .weekDayFast-event {
	border: solid 2px #ffffff;
	border-top-color: #FFC999;
	border-top-width: 4px;
}

.academic-schedule .panel-body .weekDayFast-event:hover {
	border: solid 2px #FFC999;
	border-top-color: #FFC999;
	border-top-width: 4px;
}

.academic-schedule .panel-body .weekDaySlow-event {
	border: solid 2px #ffffff;
	border-top-color: #FF9999;
	border-top-width: 4px;
}

.academic-schedule .panel-body .weekDaySlow-event:hover {
	border: solid 2px #FF9999;
	border-top-color: #FF9999;
	border-top-width: 4px;
}

.academic-schedule .panel-body .weekendSlowTrack1-event {
	border: solid 2px #ffffff;
	border-top-color: #6D9BB0;
	border-top-width: 4px;
}

.academic-schedule .panel-body .weekendSlowTrack1-event:hover {
	border: solid 2px #6D9BB0;
	border-top-color: #6D9BB0;
	border-top-width: 4px;
}

.academic-schedule .panel-body .weekdaySlowTrack2-event {
	border: solid 2px #ffffff;
	border-top-color: #FFC999;
	border-top-width: 4px;
}

.academic-schedule .panel-body .weekdaySlowTrack2-event:hover {
	border: solid 2px #FFC999;
	border-top-color: #FFC999;
	border-top-width: 4px;
}

.academic-schedule .panel-body .weekendSlowTrack3-event {
	border: solid 2px #ffffff;
	border-top-color: #D58691;
	border-top-width: 4px;
}

.academic-schedule .panel-body .weekendSlowTrack3-event:hover {
	border: solid 2px #D58691;
	border-top-color: #D58691;
	border-top-width: 4px;
}

.academic-schedule .panel-body .weekendFastTrack4-event {
	border: solid 2px #ffffff;
	border-top-color: #73A366;
	border-top-width: 4px;
}

.academic-schedule .panel-body .weekendFastTrack4-event:hover {
	border: solid 2px #73A366;
	border-top-color: #73A366;
	border-top-width: 4px;
}

.academic-schedule .panel-body .weekdayFastTrack5-event {
	border: solid 2px #ffffff;
	border-top-color: #FF9999;
	border-top-width: 4px;
}

.academic-schedule .panel-body .weekdayFastTrack5-event:hover {
	border: solid 2px #FF9999;
	border-top-color: #FF9999;
	border-top-width: 4px;
}

.academic-schedule .panel-body .weekdayBatchTrack1-event {
	border: solid 2px #FFFFFF;
	border-top-color: #FFC999;
	border-top-width: 4px;
}

.academic-schedule .panel-body .weekdayBatchTrack1-event:hover {
	border: solid 2px #FFC999;
	border-top-color: #FFC999;
	border-top-width: 4px;
}

.academic-schedule .panel-body .weekdayBatchTrack2-event {
	border: solid 2px #FFFFFF;
	border-top-color: #FF9999;
	border-top-width: 4px;
}

.academic-schedule .panel-body .weekdayBatchTrack2-event:hover {
	border: solid 2px #FF9999;
	border-top-color: #FF9999;
	border-top-width: 4px;
}

.academic-schedule .panel-body .common-event {
	border: solid 2px #ffffff;
	border-top-color: #9DB5FB;
	border-top-width: 4px;
}

.academic-schedule .panel-body .common-event:hover {
	border: solid 2px #9DB5FB;
	border-top-color: #9DB5FB;
	border-top-width: 4px;
}

.academic-schedule .panel-body .allweekBatchTrack5-event {
	border: solid 2px #ffffff;
	border-top-color: #0A42EB;
	border-top-width: 4px;
}

.academic-schedule .panel-body .allweekBatch-event {
	border: solid 2px #ffffff;
	border-top-color: #0A42EB;
	border-top-width: 4px;
}

.academic-schedule .panel-body .allweekBatchTrack5-event:hover {
	border: solid 2px #0A42EB;
	border-top-color: #0A42EB;
	border-top-width: 4px;
}

.academic-schedule .panel-body .allweekBatch-event:hover {
	border: solid 2px #0A42EB;
	border-top-color: #0A42EB;
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
				<li><a href="/acads/student/viewStudentTimeTable">SEE ALL</a></li>
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
						<span class="fa-regular fa-calendar-days"></span>No Sessions Scheduled
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
							switch (track.toLowerCase()) {

								case "weekday batch":
									colorClass = "cal-default weekDay-event";
									break;
									
								case "weekend batch":
									colorClass = "cal-default weekend-event";
									break;

								case "weekend batch - fast track":
									colorClass = "cal-default weekendFast-event";
									break;

								case "weekend batch - slow track":
									colorClass = "cal-default weekendSlow-event";
									break;
									
								case "weekday batch - slow track":
									colorClass = "cal-default weekDaySlow-event";
									break;
									
								case "weekday batch - fast track":
									colorClass = "cal-default weekDayFast-event";
									break;
									
								case "weekend slow - track 1":
									colorClass = "cal-default weekendSlowTrack1-event";
									break;
				  				
					  			case "weekday slow - track 2":
					  				colorClass = "cal-default weekdaySlowTrack2-event";
									break;
					  				
					  			case "weekend slow - track 3":
					  				colorClass = "cal-default weekendSlowTrack3-event";
									break;
					  				
					  			case "weekend fast - track 4":
					  				colorClass = "cal-default weekendFastTrack4-event";
									break;
					  				
					  			case "weekday fast - track 5":
					  				colorClass = "cal-default weekdayFastTrack5-event";
									break;
									
					  			case "weekday batch - track 1":
					  				colorClass = "cal-default weekdayBatchTrack1-event";
									break;
					  				
					  			case "weekday batch - track 2":
					  				colorClass = "cal-default weekdayBatchTrack2-event";
									break;
									
					  			case "sem i - all week - track 5":
						  			colorClass = "cal-default allweekBatchTrack5-event";
									break;
									
						  		case "sem ii - all week":
							  		colorClass = "cal-default allweekBatch-event";
									break;

								default:
									colorClass = "cal-default common-event";
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

							<a href="/acads/student/viewStudentTimeTable?id=<%=bean.getId()%>&pssId=<%=bean.getPrgmSemSubId()%>"><div
									class="col-md-6 col-lg-3 ">
									<input type="hidden" class="myField" value="<%=bean.getDate()+"T"+bean.getStartTime()+"+05:30"%>" />
									<div class="sz-calnr <%=colorClass%> ">
										<div class="sz-date">
											<p class="myDate"><%=formattedDateString%>
												<span class="cal-session">STUDY SESSION</span>
											</p>
										</div>
										<div class="sz-time">
											<p class="myTime"><%=bean.getStartTime() %></p>
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
										
										<% 
											String pssId = bean.getPrgmSemSubId() != null ? bean.getPrgmSemSubId() : "";
											if (pssIdsCommaSeparated.contains(pssId) || bean.getSessionName().contains("Doubt Clearing") ||
													bean.getSubject().equalsIgnoreCase("Orientation") || bean.getSubject().equalsIgnoreCase("Assignment")) {%>
												<span title="Live Subject" style="font-size: 50px;float: right;margin-top: -75px;color: #404047;">
													<i class="fa-regular fa-eye"></i>
												</span>
											<%}else{%>
												<span title="Recorded Subject" style="font-size: 50px;float: right;margin-top: -75px;color: #404047;">
													<i class="fa-solid fa-video"></i>
												</span>
											<% }
										%>
										
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
<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_STUDENTPORTAL_STATIC_RESOURCES')" />assets/js/moment.min.js"></script>
<script>

var elements = document.querySelectorAll(".myField");
for (var i=0; i < elements.length; i++) {
	document.getElementsByClassName("myDate")[i].innerHTML = moment(elements[i].value).format("DD-MMM-YYYY");
	document.getElementsByClassName("myTime")[i].innerHTML = moment(elements[i].value).format("hh:mm:ss A");
}
</script>