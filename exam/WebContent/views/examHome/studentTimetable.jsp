<%@page import="com.nmims.beans.StudentExamBean"%>
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
<%try{ %>

<%
StudentExamBean student = (StudentExamBean)session.getAttribute("studentExam");
	String programStructure = "";
	
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
	
	int timeTablesize = timetableList.size();
	
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	String today = sdf.format(dt);
	Calendar cal = Calendar.getInstance();
	String monthYear = new SimpleDateFormat("MMM YYYY").format(cal.getTime());

%>


<div class="course-learning-resources-m-wrapper">
	<div class="panel panel-default panel-courses-page">
		<div class="panel-heading" role="tab" id="">
			<h2>Exam Calendar</h2>
			<!---TOP TABS-->
			<div class="custom-clearfix clearfix"></div>
			<ul class="topRightLinks list-inline">
				<li>
					<h3 class=" green"><span>${mostRecentTimetablePeriod}</span> Exam Time Table</h3>
				</li>
				<li><a class="panel-toggler collapsed"  role="button" data-toggle="collapse" href="#collapseTwo" aria-expanded="true"></a></li>
				<div class="clearfix"></div>
			</ul>
			<div class="clearfix"></div>
		</div>
		<div class="clearfix"></div>
		<div id="collapseTwo" class="panel-collapse collapse in academic-schedule courses-panel-collapse panel-content-wrapper" role="tabpanel">
			<div class="panel-body" > 
				<%if(timeTablesize == 0) {%>
					<div class="no-data-wrapper">
						<p class="no-data"><span class="icon-icon-pdf"></span>No Exam Calendar Set up yet</p>
					</div>
				<%} %>
				
				
				<%if(timeTablesize > 0) {%>
				
				<div class="data-content">
					<div class="col-md-12 p-closed"> 
						<i class="fa-regular fa-calendar-days"></i>
						<h4><span><%=timeTablesize %></span> exam slots Available<span class="expand">Expand to view all timetable entries</span></h4>
					</div>
						<div class="table-responsive">
												
							<div class="clearfix"></div>
								<table class="table  table-striped" id="examHomePageTimetable">
									<thead>
										<tr>
											<%if("Offline".equals(student.getExamMode())){ //Subject shown to only offline students %>
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
								        	<%if("Offline".equals(student.getExamMode())){  //Subject shown to only offline students %>
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
							<%if(timeTablesize > 5) {%>
							<div class="load-more-table">
								<a>+<%=(timeTablesize - 5) %> More Records <span class="icon-accordion-closed"></span></a>
							</div>
							<%} %>
						
				</div>
				<%} %>
				
			</div>
		</div>
	</div>
</div>
<%}catch(Exception e){ 
}%>
