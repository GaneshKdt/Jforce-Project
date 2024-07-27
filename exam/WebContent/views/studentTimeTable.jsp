<%-- <!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!-->

<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.Date"%>
<%@page import="java.util.TreeMap"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.text.Format"%>
<%@page import="com.nmims.beans.TimetableBean"%>

<html class="no-js">
<!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<jsp:include page="jscss.jsp">
	<jsp:param value="Exam Time-Table" name="title" />
</jsp:include>


<%
	StudentBean student = (StudentBean)session.getAttribute("student");
	String programStructure = "";
	
	if(student != null){
		programStructure = student.getPrgmStructApplicable();
	}
%>

<body class="inside">


	<%@ include file="header.jsp"%>

	<section class="content-container login">
		<div class="container-fluid customTheme">
	
			<div class="row"> <legend>Time-Table for ${mostRecentTimetablePeriod} Exam</legend> </div>

			<%@ include file="messages.jsp"%>
			<%@ include file="uploadExcelErrorMessages.jsp"%>
			<%if("".equals(programStructure) || "Jul2009".equals(programStructure) || "Jul2013".equals(programStructure)){ %>
			<a href="resources_2015/codeOfConduct.pdf" target="_blank" ><b><i class="fa fa-download fa-lg"></i> 
			Download Student Code of Conduct in the Examination Hall </b></a><br><br>
			<%} %>
			<%
			
			List<TimetableBean> timeTableList = (List<TimetableBean>)request.getAttribute("timeTableList");
			TreeMap<String,  ArrayList<TimetableBean>> programTimetableMap = (TreeMap<String,  ArrayList<TimetableBean>>)request.getAttribute("programTimetableMap");
			%>
			
			<%if(programTimetableMap != null && programTimetableMap.size() > 0){ %>
				<div id="accordion">
			<%} %>
			<%
			for (Map.Entry<String, ArrayList<TimetableBean>> entry : programTimetableMap.entrySet()) {
			    String key = entry.getKey();
			    ArrayList<TimetableBean> list  = entry.getValue();
						
			 %>
			
				<h3><%= key%></h3>
				<div>
					<div class="table-responsive">
					<table class="table table-striped" style="font-size:12px">
						<thead>
							<tr> 
								<th>Sem</th>
								<th style="text-align:left;">Subject</th>
								<th>Date</th>
								<th>Start Time</th>
								<th>End Time</th>

							
							</tr>
						</thead>
						<tbody>
						
						<%
						
						for(int i = 0; i < list.size(); i++){
							TimetableBean bean = (TimetableBean)list.get(i);
							SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
							SimpleDateFormat dateFormatter = new SimpleDateFormat("EEEE, dd-MMM-yyyy");
							Date formattedDate = formatter.parse(bean.getDate());
							String formattedDateString = dateFormatter.format(formattedDate);
						%>
					        <tr>
					            <td><%= bean.getSem()%></td>
								<td style="text-align:left;"><%= bean.getSubject()%></td>
								<td><%= formattedDateString%></td>
								<td><%= bean.getStartTime()%></td>
								<td><%= bean.getEndTime()%></td>
					        </tr> 
					        <%} 
					        %>  
					  		 
						</tbody>
					</table>
					 <button id="submit" name="submit" class="btn btn-large btn-primary" onClick="window.print()">Print</button>
				</div>
				</div>
				
				
				
				
			
		<% } %>
		<%if(programTimetableMap != null && programTimetableMap.size() > 0){ %>
			</div>
		<%} %>
		<br/>
		</div>
	</section>

    <jsp:include page="footer.jsp" />

	<script>
	  $(function() {
	    $( "#accordion" ).accordion({
	      collapsible: true,
	      heightStyle: "content"
	    });
	  });
	  </script>
  
</body>
</html>
 --%>
 
 <!DOCTYPE html>

<%@page import="java.util.Calendar"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.Date"%>
<%@page import="java.util.TreeMap"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.text.Format"%>
<%@page import="com.nmims.beans.TimetableBean"%>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%
StudentExamBean student = (StudentExamBean)session.getAttribute("studentExam");
	
%>

<%
			
List<TimetableBean> timeTableList = (List<TimetableBean>)request.getAttribute("timeTableList");
TreeMap<String,  ArrayList<TimetableBean>> programTimetableMap = (TreeMap<String,  ArrayList<TimetableBean>>)request.getAttribute("programTimetableMap");

String keyForTimetable = student.getProgram() + " - "+ student.getPrgmStructApplicable() + " Program Structure";
ArrayList<TimetableBean> timetableList = programTimetableMap.get(keyForTimetable);

if(timetableList == null){
	timetableList = programTimetableMap.get("NA" + " - "+ student.getPrgmStructApplicable() + " Program Structure");//NA for Resit Exam
	if(timetableList == null){
		timetableList = new ArrayList();
	}
}

Date dt = new Date();

SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
String today = sdf.format(dt);
Calendar cal = Calendar.getInstance();
String monthYear = new SimpleDateFormat("MMM YYYY").format(cal.getTime());

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
                         <div id="sticky-sidebar">  
	              				<jsp:include page="common/left-sidebar.jsp">
									<jsp:param value="Exam Time Table" name="activeMenu"/>
								</jsp:include>
              				</div>
              				
              				<div class="sz-content-wrapper examsPage">
              						<%@ include file="common/studentInfoBar.jsp" %>
              						
              						
              					<div class="sz-content">
              						<%@ include file="common/messages.jsp" %>
              						
              						
									<h2 class="text-capitalize">${mostRecentTimetablePeriod} <span>Exam Time Table (<%=keyForTimetable%>)</span></h2>
									<ul class="pull-right list-inline topRightLinks list-type-switcher">
										<!-- <li class="active"><a id="switch-view-list"><i class="icon-list-list"></i></a></li>
										
										IMP: Commented remporarily: Show students actual bookings here in calendar view
										<li><a id="switch-view-calendar"><i class="icon-academic-calendar"></i></a></li> 
										
										-->
									</ul>
									
									<div class="clearfix"></div>
									<%-- <h3 class="sz-table-heading"><%=keyForTimetable%></h3> --%>
									<div class="clearfix"></div>
									<div class="list-container">
										<div class="panel-content-wrapper">
											
											<div class="table-responsive">
												
												<div class="clearfix"></div>
													<table class="table  table-striped">
														<thead>
															<tr>
																<%if("Offline".equals(student.getExamMode())){ %>
																<th>Subject</th>
																<%} %>
																<th>Date</th>
																<th>Day</th>
																<th>Start Time</th>
																<th>End Time</th>
															</tr>
														</thead>
														<tbody>
								
														<%
														
														for(int i = 0; i < timetableList.size(); i++){
															TimetableBean bean = (TimetableBean)timetableList.get(i);
															SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
															SimpleDateFormat dateFormatter = new SimpleDateFormat("EEEE, dd-MMM-yyyy");
															SimpleDateFormat dayFormatter = new SimpleDateFormat("EEEE");
															Date formattedDate = formatter.parse(bean.getDate());
															String formattedDateString = dateFormatter.format(formattedDate);
															String dayOfExam = dayFormatter.format(formattedDate);
														%>
													        <tr>
													        	<%if("Offline".equals(student.getExamMode())){ %>
													        	<td><%=bean.getSubject() %>
													        	<%} %>
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
									
										
										<div class="calendar-container">
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
														
													for(int i = 0; i < timetableList.size(); i++){
														TimetableBean bean = (TimetableBean)timetableList.get(i);
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
										</div>
	              							
              								
              					</div>
              				</div>
              		
                            
					</div>
            </div>
        </div>
            
  	
        <jsp:include page="common/footer.jsp"/>
        
        
        <script>

			$(document).ready(function() {
				
				$('#calendar').fullCalendar({
					header: false,
					aspectRatio: 1.4,
					defaultDate: '<%=today%>',
					events: [
					     <% for(TimetableBean bean : timetableList){    %>    
					         
					         
						{
							title: '<%=bean.getSubject().replaceAll("'", "")%>',
							start: '<%=bean.getDate()+"T"+ bean.getStartTime()%>',
							end: '<%=bean.getDate()+"T"+ bean.getEndTime()%>',
							className: 'blue-event'
							
						},
						
						<%}%>
					],
					
				});
				
			});
		
			function setCurrentTitle() {
				var view = $('#calendar').fullCalendar('getView');
				var start = $('#calendar').fullCalendar('getView').title;
				console.log(view,start);
				$('#month').html(start);
			}
	
	
			$('#showM').click(function() {
				$('#calendar').fullCalendar( 'changeView', 'month' );
				setCurrentTitle();
			});
			$('#showW').click(function() {
				$('#calendar').fullCalendar( 'changeView', 'agendaWeek' );
				setCurrentTitle();
			});
			$('#showD').click(function() {
				$('#calendar').fullCalendar( 'changeView', 'agendaDay' );
				setCurrentTitle();
			});
			$('#showT').click(function() {
				$('#calendar').fullCalendar( 'changeView', 'agendaDay' );
				setCurrentTitle();
				$('#calendar').fullCalendar('today');
			});
			
			$('#next').click(function() {
				$('#calendar').fullCalendar('next');
				setCurrentTitle();
			});
			$('#prev').click(function() {
				$('#calendar').fullCalendar('prev');
				setCurrentTitle();
			});
			$('.show-calendar-format button').on('click', function(){
				$('.show-calendar-format button').removeClass('active-button');
				$(this).addClass('active-button');
			});
			
		</script>
		
		<%-- <script>
		$('#calendar').fullCalendar({
					header: {
						left: 'prev,next today',
						center: 'title',
						right: 'month,agendaWeek,agendaDay'
					},
					defaultDate: '<%=today%>',
					editable: false,
					eventLimit: true,
					events: [
						<% for(TimetableBean bean : timetableList){    %>       
						{
							title: '<%=bean.getSubject().replaceAll("'", "")%>',
							start: '<%=bean.getDate()+"T"+ bean.getStartTime()%>',
							end: '<%=bean.getDate()+"T"+ bean.getEndTime()%>',
							className: 'blue-event'
						},
						
						<%}%>
						
					],
					
				});
				
		</script> --%>
		
    </body>
</html>