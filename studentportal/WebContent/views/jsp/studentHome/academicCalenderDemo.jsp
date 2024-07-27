<%@page import="com.nmims.controllers.BaseController"%>
<%@page import="com.itextpdf.text.BaseColor"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Date"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.nmims.beans.SessionDayTimeStudentPortal"%>
<%@page import="com.nmims.beans.SessionTrackBean"%>


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
	ArrayList<SessionTrackBean> tracksDetails = (ArrayList<SessionTrackBean>) session.getAttribute("trackDetails");

%>
<style>
<!--
<% if(tracksDetails!=null && !tracksDetails.isEmpty()){
		for(SessionTrackBean track : tracksDetails){ %>  
			.<%=track.getColorClass()%>-text{
			   color:<%=track.getHexCode()%> !important;
			}
			
	<% }} %>
-->
</style>
<script>
function notifyLead(){
	alert("Please enrole for the complete program...");
}
</script>
<div class="mt-md-5 mt-lg-0 mb-2">
<%if(noOfSessions != 0){ %>

	
		<div class="d-flex align-items-center text-wrap ">
		 	<span class="fw-bold me-3"><small class="fs-5">ACADEMIC CALENDAR</small></span>
			<div class="ms-auto">
		 	<a href="#upcoming" aria-controls="upcoming" role="tab" data-toggle="tab" class="active text-dark me-1"><small>UPCOMING</small></a>	
			<a href="/acads/student/viewStudentTimeTable" class="text-dark "><small >SEE ALL</small></a>
		 	<a type="button" data-bs-toggle="collapse" href="#collapseTwo" role="button" aria-expanded="true" aria-controls="collapseTwo" id="collapseCard" class="text-muted"> 
			<i class="fa-solid fa-square-minus"></i></a>
		</ul>
	</div>
</div>

		<%if(noOfSessions == 0){ %>
			<div class="collapse" id="collapseTwo">
			<div class="card card-body text-center">
				<h6><i class="fa-regular fa-calendar-days"></i><small> No Sessions Scheduled This week</small></h6>
			</div>
		</div>
		<%}else{ %>
		<div class="collapse " id="collapseTwo" >
			<div class="card card-body text-center">
					<h6><i class="fa-regular fa-calendar-days"></i><small> View Scheduled Sessions</small></h6>
				</div>
			</div>	
				 
		
		<div class="collapse show" id="collapseTwo" >
			<div class="row  row-cols-lg-4  ">
			
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
							if ( StringUtils.isBlank(track) ) {
								track = "Common For All Batches";
							}
							
			  				String trackColorClass=track.replaceAll("[-_ ]+", "");		
			  								
                			String textColor=trackColorClass+"-"+"text";

							//To Change Calendar event color as per track
							
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

							<a href="/acads/student/viewStudentTimeTable?id=<%=bean.getId()%>&pssId=<%=bean.getPrgmSemSubId()%>" class="mb-2">
							<div class="card h-100 p-2">
						
								
									<input type="hidden" class="myField" value="<%=bean.getDate()+"T"+bean.getStartTime()+"+05:30"%>" />
								
									<div class="text-nowrap d-block text-truncate w-100" >
											<small class="text-dark"><span class=" sz-date myDate "><%=formattedDateString%>
											 
											<span class="cal-session">STUDY SESSION</span> </span>
											
											<span class=" sz-time myTime"><%=bean.getStartTime() %></span></small>
											</div>
										
										
										<div class="clearfix"></div>
										<div class="sz-calndr-info">
											<p class="d-block text-truncate w-100 mb-3 fw-bolder" data-bs-toggle="tooltip" title="<%=bean.getSubject() %>">
												<%=bean.getSubject() %>
											</p>
											<p class="d-block text-truncate w-100" data-bs-toggle="tooltip" title="<%=bean.getSessionName() %>"><%=bean.getSessionName() %></p>
											<p class="<%=textColor%>"><%=track %></p>
											
										</div>
										<div class="row text-nowrap">
								          <div class="col-9 text-truncate">
								  			<span class="text-black " data-bs-toggle="tooltip" title="Prof.<%=bean.getFirstName() +  " " + bean.getLastName()%>">								         
												Prof.<%=bean.getFirstName() +  " " + bean.getLastName()%></span>																			           
								            </div>
										
										<div class="col">
											<% 
											String pssId = bean.getPrgmSemSubId() != null ? bean.getPrgmSemSubId() : "";
											if (pssIdsCommaSeparated.contains(pssId) || bean.getSessionName().contains("Doubt Clearing") ||
													bean.getSubject().equalsIgnoreCase("Orientation") || bean.getSubject().equalsIgnoreCase("Assignment")) {%>
												<span title="Live Subject " class="float-end mt-2" >
													<i class="fa-regular fa-eye text-black"></i>
												</span>
											<%}else{%>
												<span title="Recorded Subject " class="float-end mt-2">
													<i class="fa-solid fa-video  text-black"></i>
												</span>
											<% }
										%>
										</div>
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
					<!-- <div role="tabpanel" class="tab-pane" id="week">...</div> -->
			
		<%} %>

<% } %>

</div>
<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_STUDENTPORTAL_STATIC_RESOURCES')" />assets/js/moment.min.js"></script>
<!-- <script>

var elements = document.querySelectorAll(".myField");
for (var i=0; i < elements.length; i++) {
	document.getElementsByClassName("myDate")[i].innerHTML = moment(elements[i].value).format("DD-MMM-YYYY");
	document.getElementsByClassName("myTime")[i].innerHTML = moment(elements[i].value).format("hh:mm:ss A");
}
</script> -->
<script type="text/javascript" src="${pageContext.request.contextPath }/assets/js/academicCalenderDemo.js"></script>
