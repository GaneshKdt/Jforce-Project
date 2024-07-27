<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="com.nmims.controllers.BaseController"%>
<%@page import="org.apache.poi.hssf.util.HSSFColor.VIOLET"%>
<%@page import="com.nmims.beans.VideoContentStudentPortalBean"%>
<%@page import="com.nmims.daos.ContentDAO"%>

<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Date"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.nmims.beans.SessionTrackBean"%>



<%
	BaseController sesCon = new BaseController();
	boolean registeredForEvent;
	if(sesCon.checkLead(request, response))
		registeredForEvent = false;
	else
		registeredForEvent = (boolean)session.getAttribute("registeredForEvent");
	ArrayList<VideoContentStudentPortalBean> videoList = (ArrayList<VideoContentStudentPortalBean>)session.getAttribute("videoList");
	int noOfVideos = videoList != null ? videoList.size() : 0;
	//System.out.println("_______________________________________________________ videoList"+videoList);
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
<%if(noOfVideos != 0){ %>
	<div class="calendarWrapper  mt-md-5 mt-lg-0">

	<div class="d-flex align-items-center text-wrap py-1">
		 	<span class="fw-bold me-1"><small class="fs-5">SESSION RECORDINGS</small></span>
			<div class=" d-flex ms-auto">
		 	<a href="#upcoming" aria-controls="upcoming" role="tab" data-toggle="tab" class="active text-dark me-2"><small >RECENT</small></a>	
			<a href="/acads/student/videosHome?pageNo=1&academicCycle=${currentSessionCycle}" class="text-dark me-1"><small class="text-nowrap">SEE ALL</small></a>
		 	<a type="button" data-bs-toggle="collapse" href="#collapseThree" role="button" aria-expanded="true" aria-controls="collapseThree" id="collapseCard" class="text-muted "> 
			<i class="fa-solid fa-square-minus"></i></a>
		</ul>
	</div>
</div>

			

		<%if(noOfVideos == 0){ %>
	
			<div class="card">
				<div class="card-body text-center ">
				<h6><i class="fa-regular fa-calendar-days"></i><small>  No Recent Sessions Recordings Found</small></h6>
			
				</div>
			</div>
	
	<%
		} else {
	%>		
<div class="collapse" id="collapseThree">
			<div class="card">
				<div class="card-body text-center ">
				<h6 type="button" data-bs-toggle="collapse" href="#collapseThree" role="button" aria-expanded="true" aria-controls="collapseThree" id="collapseCard"><i class="fa-regular fa-calendar-days"></i><small>  View Session Recordings</small></h6>
				</div>
			</div>
		</div>		
	<div class="collapse show" id="collapseThree" >
			<div class="row  row-cols-lg-4 ">
			<% 
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                		SimpleDateFormat dateFormatter = new SimpleDateFormat("EEEE, dd-MMM-yyyy");
                		
                		int count = 0;//Show only 4
                        for(VideoContentStudentPortalBean bean : videoList){
                        	count++;
                        	Date formattedDate = formatter.parse(bean.getSessionDate());
                			String formattedDateString = dateFormatter.format(formattedDate);
                		
                			//To Change Calendar event color as per track
							String track = bean.getTrack();
							if ( StringUtils.isBlank(track) ) {
								track = "Common For All Batches";
							}
							String trackColorClass=track.replaceAll("[-_ ]+", "");						
                			String textColor=trackColorClass+"-"+"text";
							
					    %>
						<a href="/acads/student/watchVideos?id=<%= bean.getId() %>&pssId=<%=bean.getProgramSemSubjectId()%>" class="mb-3">
							<div class="card h-100 videoList">
								<div class="col-lg-12" >					  
										<img src=<%=bean.getThumbnailUrl() %> class="card-img-top img-fluid  " >
												<!-- <button type="button">Join Now</button> -->
											<div class="card-body ">
												<h6 class="card-title text-dark mb-1 d-inline-block text-truncate w-100" data-bs-toggle="tooltip" title="<%=bean.getSubject() %> "><%=bean.getSubject() %></h6>
												<div class="text-nowrap text-black mb-1">	
												 <%try {%>							
												<span class="d-block text-truncate w-100">
												<%=bean.getFacultyName() %></span>
												<%}catch(Exception e){ %>
												
												<%} %> 
												<!-- <i class="fa-solid fa-circle-user fa-xl"></i> -->
												</div>
												<p class="d-inline-block text-truncate w-100 " data-bs-toggle="tooltip" title="<%= bean.getDescription() %> "><%= bean.getDescription() %> </p>
												<p class="card-text  text-muted"><%=formattedDateString%></p>
												<p class="card-text text-muted <%=textColor%>"> <%= track %></p>  												
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
			
			
	

		<% } %>


</div>
		<% } %>