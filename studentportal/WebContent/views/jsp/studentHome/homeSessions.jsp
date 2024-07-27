<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="com.nmims.controllers.BaseController"%>
<%@page import="org.apache.poi.hssf.util.HSSFColor.VIOLET"%>
<%@page import="com.nmims.beans.VideoContentStudentPortalBean"%>
<%@page import="com.nmims.daos.ContentDAO"%>

<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Date"%>
<%@page import="java.text.SimpleDateFormat"%>



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
%>

<script>
function notifyLead(){
	alert("Please enrole for the complete program...");
}
</script>

<style>
	box{
		box-shadow:20px 20px 50px black;  
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
		border: solid 2px #ffffff;
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


<div class=" calendarWrapper " style="padding: 10px;" >
	<div class="panel panel-default bgColorNone">
		<div class="panel-heading" role="tab" id="">
			<h4 class="panel-title">SESSION RECORDINGS</h4>
			<!---TOP TABS-->
			<div class="custom-clearfix clearfix"></div>
			<ul class="topRightLinks list-inline">
				<li><a href="#upcoming" aria-controls="upcoming" role="tab"
					data-toggle="tab" class="active">RECENT</a></li>
				<!-- <li class="borderRight"><a href="#week" aria-controls="week" role="tab" data-toggle="tab">THIS WEEK</a></li> -->
				<li><a href="/acads/student/videosHome?pageNo=1&academicCycle=${currentSessionCycle}">SEE ALL</a></li>
				<li><a class="panel-toggler collapsed" role="button"
					data-toggle="collapse" href="#collapseEight" aria-expanded="true"></a></li>
			</ul>
			<div class="clearfix"></div>
		</div>

		<%if(noOfVideos == 0){ %>
		<div id="collapseEight" class="panel-collapse collapse academic-schedule courses-panel-collapse"
			role="tabpanel">
			<div class="panel-body bgColorNone">
				<div class="no-data-wrapper">
					<p class="no-data">
						<span class="icon-academic-calendar"></span>No Recent Sessions Recordings Found
					</p>
				</div>
			</div>
		</div>
		<%}else{ %>
		<div id="collapseEight" class="panel-collapse collapse academic-schedule courses-panel-collapse" role="tabpanel">
			<div class="row  row-cols-lg-4 ">
			<% 
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                		SimpleDateFormat dateFormatter = new SimpleDateFormat("EEEE, dd-MMM-yyyy");
                		
                		int count = 0;//Show only 4
                        for(VideoContentStudentPortalBean bean : videoList){
                        	count++;
                        	Date formattedDate = formatter.parse(bean.getSessionDate());
                			String formattedDateString = dateFormatter.format(formattedDate);
                			
                			String track = bean.getTrack();
							if ( StringUtils.isBlank(track) ) {
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
					    %>
						<a href="/acads/student/watchVideos?id=<%= bean.getId() %>&pssId=<%=bean.getProgramSemSubjectId()%>">
							<div class="card h-100 ">
								<div class="col-lg-12  mb-3 ">
									<div class="sz-calnr <%=colorClass%>" style="min-height: 350px;">    
										<img src=<%=bean.getThumbnailUrl() %> class="card-img-top" style="max-width: 100%;" height="" width="800">
												<!-- <button type="button">Join Now</button> -->
											<div class="card-body ">
												<h6 class="card-title text-dark mb-3"><%=bean.getSubject() %></h6>
												<p><%=bean.getFacultyName() %> - <%= bean.getDescription() %></p>
												<p class="card-text mb-4 text-muted"><%=formattedDateString%></p>
												<% if(StringUtils.isBlank(bean.getTrack())){ %>
													<p class="card-text text-muted"> Common For All Batches </p>  
												<% }else{ %>
													<p class="card-text  text-muted"><%= bean.getTrack() %></p>  
												<% } %>							
										</div>
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
	</div>
