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
	if( session.getAttribute("isLoginAsLead").equals("true") )
		registeredForEvent = false;
	else
		registeredForEvent = (boolean)session.getAttribute("registeredForEvent");
	ArrayList<VideoContentStudentPortalBean> videoList = (ArrayList<VideoContentStudentPortalBean>)session.getAttribute("videoList");
	int noOfVideos = videoList != null ? videoList.size() : 0;
	System.out.println("_______________________________________________________ videoList"+videoList);
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
		border-top-color:  #D81B60;
		border-top-width: 4px;
	}

	.academic-schedule .panel-body .weekDay-event:hover {
		border: solid 2px #D81B60;
		border-top-color: #D81B60;
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
	
</style>

<%if(noOfVideos != 0){ %>
<div class="calendarWrapper" style="padding: 20px;" >
	<div class="panel panel-default bgColorNone">
		<div class="panel-heading" role="tab" id="">
			<h4 class="panel-title">SESSION RECORDINGS</h4>
			<!---TOP TABS-->
			<div class="custom-clearfix clearfix"></div>
			<ul class="topRightLinks list-inline">
				<li><a href="#upcoming" aria-controls="upcoming" role="tab"
					data-toggle="tab" class="active">RECENT</a></li>
				<!-- <li class="borderRight"><a href="#week" aria-controls="week" role="tab" data-toggle="tab">THIS WEEK</a></li> -->
				<li><a href="/acads/videosHomeNew?pageNo=1&academicCycle=${currentSessionCycle}">SEE ALL</a></li>
				<li><a class="panel-toggler collapsed" role="button"
					data-toggle="collapse" href="#collapseOne" aria-expanded="true"></a></li>
			</ul>
			<div class="clearfix"></div>
		</div>

		<%if(noOfVideos == 0){ %>
		<div id="collapseOne"
			class="panel-collapse collapse academic-schedule courses-panel-collapse"
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
                        for(VideoContentStudentPortalBean bean : videoList){
                        	count++;
                        	Date formattedDate = formatter.parse(bean.getSessionDate());
                			String formattedDateString = dateFormatter.format(formattedDate);
                			
                        	String calendarColorClass="";
                        	if(bean.getTrack().equals("WeekDay Batch")){
                        		calendarColorClass = "cal-default weekDay-event";
                        	}else if(bean.getTrack().equals("Weekend Batch - Slow Track")){
                        		calendarColorClass = "cal-default weekendSlow-event";
                        	}else if(bean.getTrack().equals("Weekend Batch - Fast Track")){
                        		calendarColorClass = "cal-default weekendFast-event";
                        	} else{
                        		calendarColorClass = "cal-default blue-cal";
                        	}
					    %>
							<a href="/acads/watchVideos?id=<%= bean.getId() %>">

								<div class="col-md-6 col-lg-3 " style="padding-top: 20px;">
									<div class="sz-calnr <%=calendarColorClass%>" style="min-height: 350px;">    
										<div class="sz-time">
											<img src=<%=bean.getThumbnailUrl() %> class="img-responsive" style="max-width: 100%;" height="" width="800">
												<!-- <button type="button">Join Now</button> -->
										</div>
										<div class="clearfix"></div>
											<div class="sz-calndr-info">  
												<p>
													<strong><%=bean.getSubject() %></strong>
												</p>
												<p><%=bean.getFacultyName() %> - <%= bean.getDescription() %></p>
												<p class="cal-name">
													<%=formattedDateString%></p>

												<% if(StringUtils.isBlank(bean.getTrack())){ %>
													<p class="cal-name"> Common For All Batches </p>  
												<% }else{ %>
												<p class="cal-name">
													<%= bean.getTrack() %></p>  
													<% } %>
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
		<% } %>

	</div>
	</div>
<% } %>