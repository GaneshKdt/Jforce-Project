<!DOCTYPE html>

<%@page import="java.util.Calendar"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.TreeSet"%>
<%@page import="java.util.TreeMap"%>
<%@page import="java.util.HashSet"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.Date"%>
<%@page import="java.util.TreeMap"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.text.Format"%>
<%@page import="com.nmims.beans.ExecutiveTimetableBean"%>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%
StudentExamBean student = (StudentExamBean)session.getAttribute("studentExam");
List<ExecutiveTimetableBean> timeTableList = (List<ExecutiveTimetableBean>)request.getAttribute("timtableList"); 
String keyForTimetable = student.getProgram() + " - "+ student.getPrgmStructApplicable() + " Program Structure";
Date dt = new Date();
SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
String today = sdf.format(dt);
Calendar cal = Calendar.getInstance();
String monthYear = new SimpleDateFormat("MMM YYYY").format(cal.getTime());

boolean isTimetableLive = (boolean)request.getSession().getAttribute("isTimetableLive");


%>

<html lang="en">
    
    <jsp:include page="common/jscss.jsp">
	<jsp:param value="" name="title"/>
    </jsp:include>
    
    <body>
    
    	<%@ include file="common/header.jsp" %>
    	
        <div class="sz-main-content-wrapper">
        
        	<jsp:include page="common/breadcrum.jsp">
			<jsp:param value="Student Zone;Exams;Exam Calendar" name="breadcrumItems"/>
			</jsp:include>
        	
            
            <div class="sz-main-content menu-closed">
                    <div class="sz-main-content-inner">
              				<jsp:include page="common/left-sidebar.jsp">
								<jsp:param value="Executive Exam Time Table" name="activeMenu"/>
							</jsp:include>
              				
              				
              				<div class="sz-content-wrapper examsPage">
              						<%@ include file="common/studentInfoBar.jsp" %>
              						
              						
              					<div class="sz-content">
              						<%@ include file="common/messages.jsp" %>
              						
              					<%if(isTimetableLive){ %>
									<h2 class="text-capitalize"><span>${mostRecentTimetablePeriod} Exam Time Table (<%=keyForTimetable%>)</span></h2>
									<ul class="pull-right list-inline topRightLinks list-type-switcher">
									
									</ul>
									
									
									<div class="clearfix"></div>
									
									<div class="well">
										<p>
											<b>Note:</b> Below are Lists of slots and subjects available for exams. Ignore the subjects if already cleared.
										</p>
									</div>
								
								
									<div class="list-container">
										<div class="panel-content-wrapper">
										 <div class="panel-group" id="accordion">
    <div class="panel panel-default">
      <div class="panel-heading">
        <h4 class="panel-title">
          <a data-toggle="collapse" data-parent="#accordion" href="#collapse1">Exam Slots List</a>
          <span style="float:right;" ><i class="fa-solid fa-circle-chevron-down"></i></span>
        </h4>
      </div>
      <div id="collapse1" class="panel-collapse collapse ">
        <div class="panel-body">
        											
											<div class="table-responsive">
												
												<div class="clearfix"></div>
													<table class="table  table-striped">
														<thead>
															<tr>
																<th>Date</th>
																<th>Day</th>
																<th>Start Time</th>
																<th>End Time</th>
															</tr>
														</thead>
														<tbody>
								
														<%
														TreeSet<String> studentsSubjects = new TreeSet<>();
														TreeMap<String,ExecutiveTimetableBean> tempMap = new TreeMap<>();
														HashSet<ExecutiveTimetableBean> tempList = new HashSet<>();
														
														for(int i = 0; i < timeTableList.size(); i++){
															ExecutiveTimetableBean bean = (ExecutiveTimetableBean)timeTableList.get(i);
															studentsSubjects.add(bean.getSubject());
															
															ExecutiveTimetableBean tempBean = new ExecutiveTimetableBean();
															tempBean.setDate(bean.getDate());
												       		tempBean.setStartTime(bean.getStartTime());
												       		tempBean.setEndTime(bean.getEndTime());
												       		tempList.add(tempBean);
														
														}
														for(ExecutiveTimetableBean b : tempList){
															tempMap.put(b.getDate()+b.getStartTime(), b);
														}
														for(Map.Entry<String,ExecutiveTimetableBean> e : tempMap.entrySet()) {
															ExecutiveTimetableBean bean = e.getValue();
															
															SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
															SimpleDateFormat dateFormatter = new SimpleDateFormat("EEEE, dd-MMM-yyyy");
															SimpleDateFormat dayFormatter = new SimpleDateFormat("EEEE");
															Date formattedDate = formatter.parse(bean.getDate());
															String formattedDateString = dateFormatter.format(formattedDate);
															String dayOfExam = dayFormatter.format(formattedDate);
												       	
														%>
													        <tr>
													        	
													            <td><%= formattedDateString%></td>
																<td style="text-align:left;"><%=dayOfExam%></td>
																<td><%= bean.getStartTime()%></td>
																<td><%= bean.getEndTime()%></td>
													        </tr> 
													     <%} %>  
													  		 
														</tbody>
													</table>
												</div>
        
        </div>
      </div>
    </div>
    <div class="panel panel-default">
      <div class="panel-heading">
        <h4 class="panel-title">
          <a data-toggle="collapse" data-parent="#accordion" href="#collapse2">Available Subjects List</a>
          <span style="float:right;" ><i class="fa-solid fa-circle-chevron-down"></i></span>
        </h4>
      </div>
      <div id="collapse2" class="panel-collapse collapse">
        <div class="panel-body">
       	<%
       	
       	%>
       		
       		<div class="table-responsive">
												
												<div class="clearfix"></div>
													<table class="table  table-striped">
														<thead>
															<tr>
																<th>Subject</th>
															</tr>
														</thead>
														<tbody>
								
														<%
														
														for(String sub: studentsSubjects){
														%>
													        <tr>
													        	
													            <td><%= sub%></td>
													        </tr> 
													     <%} %>  
													  		 
														</tbody>
													</table>
												</div>
       		
       	<%
       	
       	%>
        </div>
      </div>
    </div>
  </div> 
										

											</div>
										</div>
									
										<%} %>
										<%-- <div class="calendar-container">
											<div class="calendar-operations row">
												<div class="col-md-4">
														<div class="calendar-navigation">
															<h2 id="month"><%=monthYear %></h2>
															<div class="arrows"> <span id="prev" class="glyphicon glyphicon-chevron-left"></span> <span id="next" class="glyphicon glyphicon-chevron-right"></span>
																<div class="clearfix"></div>
															</div>
															<div class="clearfix"></div>
														</div>
												</div>
												<div class="col-md-4">
													<ul class="show-calendar-format">
														<li>
															<button class="active-button"  id="showM">Month</button>
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
											</div>
										
											<div class="row">
											
											<div class="col-lg-8 col-md-8">
												<div id="calendar"></div>
											</div>
											
											<div class="col-lg-4 col-md-4">												
												<div class="sz-time-table-list">
													<ul class="time-table-list-item">
													<%
														
													for(int i = 0; i < timeTableList.size(); i++){
														ExecutiveTimetableBean bean = (ExecutiveTimetableBean)timeTableList.get(i);
														SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
														SimpleDateFormat dateFormatter = new SimpleDateFormat("EEEE, dd-MMM-yyyy");
														Date formattedDate = formatter.parse(bean.getDate());
														String formattedDateString = dateFormatter.format(formattedDate);
													%>
													
														<li>
															<h4><%=formattedDateString%></h4>
															<p><%=bean.getStartTime()%>-<%= bean.getEndTime()%></p>
															<p><%=bean.getSubject()%></p>
														</li>
													<%} %> 
													</ul>
												</div>
											</div>
											
										</div>
										</div> --%>
	              							
              								
              					</div>
              				</div>
              		
                            
					</div>
            </div>
        </div>
            
  	
        <jsp:include page="common/footer.jsp"/>
        
        
       

    </body>
</html>