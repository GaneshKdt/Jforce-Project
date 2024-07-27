<%@page import="java.util.ArrayList"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Date"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.nmims.beans.ExamBookingTransactionStudentPortalBean"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%


ArrayList<ExamBookingTransactionStudentPortalBean> upcomingExams = (ArrayList<ExamBookingTransactionStudentPortalBean>)session.getAttribute("upcomingExams");
int noOfUpcomingExams =upcomingExams != null ? upcomingExams.size() : 0; 
ExamBookingTransactionStudentPortalBean examBookingTemp = null;
String upcomingExamExpandClass = "";	
String upcomingExamBorderStyle = "";
if(noOfUpcomingExams > 0){
	examBookingTemp = upcomingExams.get(0);
	upcomingExamExpandClass = "in"; //this will open Calendar section, otherwise it will be collapsed by Default 
	upcomingExamBorderStyle = "border:0px solid black !important;";//So that top blue border does not appear. It should show only when there are no sessions
%>
<div class="calendarWrapper">
	<div class="panel panel-default bgColorNone">
		<div class="panel-heading" role="tab" id="">
			<h4 class="panel-title">UPCOMING EXAMS</h4>
			<!---TOP TABS-->
			<div class="custom-clearfix clearfix"></div>
			<ul class="topRightLinks list-inline">
				<!-- 
                    <li><a href="#upcoming" aria-controls="upcoming" role="tab" data-toggle="tab" class="active">UPCOMING</a></li>
                    -->
				<!-- <li class="borderRight"><a href="#week" aria-controls="week" role="tab" data-toggle="tab">THIS WEEK</a></li> -->
				<li><a href="/acads/viewStudentTimeTable"><b>SEE ALL</b></a></li>
				<li><a class="panel-toggler collapsed" role="button"
					data-toggle="collapse" href="#collapseOne" aria-expanded="true"></a></li>
			</ul>
			<div class="clearfix"></div>
		</div>


		<%if(noOfUpcomingExams == 0){ %>
		<div id="collapseOne"
			class="panel-collapse collapse academic-schedule courses-panel-collapse"
			role="tabpanel">
			<div class="panel-body bgColorNone">
				<div class="no-data-wrapper">
					<p class="no-data">
						<span class="icon-academic-calendar"></span>No Upcoming Exams
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
										<span class="icon-academic-calendar"></span> Upcoming Exams
									</p>
								</div>
							</div>

							<% 
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                		SimpleDateFormat dateFormatter = new SimpleDateFormat("EEEE, dd-MMM-yyyy, HH:mm");
                		
                        int count = 0;//Show only 4
                        for(ExamBookingTransactionStudentPortalBean bean : upcomingExams){
                        	count++;
                        	Date formattedDate = formatter.parse(bean.getExamDate()+" "+bean.getExamTime());
                			String formattedDateString = dateFormatter.format(formattedDate);
                			
                        	String calendarColorClass="";
                        	if(count == 2){
                        		calendarColorClass = "cal-default blue-cal";
                        	}else if(count == 3){
                        		calendarColorClass = "cal-default red-cal";
                        	}
                        	
					    %>
							<a href="<%= bean.getGoogleMapUrl() %>" target="_blank"
								title="Google Map of Exam Center">
								<div class="col-md-6 col-lg-3 ">
									<div class="sz-calnr <%=calendarColorClass%>">
										<div class="sz-date">
											<p style="font-size: 15px;">
												<strong><%=bean.getSubject() %></strong>
											</p>
										</div>
										<div class="sz-time">
											<p style="font-size: 18px !important;"><%=formattedDateString%>
											</p>
											<!-- <button type="button">Join Now</button> -->
										</div>
										<div class="clearfix"></div>
										<div class="sz-calndr-info">
											<p style="color: #a2a2a7 !important;"><%=bean.getExamCenterName() %></p>
											<p style="color: #a2a2a7 !important;"><%=bean.getAddress() %></p>

											<p class="cal-name"
												style="float: right; padding-right: 10px; font-size: 15px;">
												<i class="fa fa-map-marker" aria-hidden="true"></i> Google
												Map
											</p>
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
<%
}
%>

