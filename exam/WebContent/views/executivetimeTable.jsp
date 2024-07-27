<!DOCTYPE html>


<html lang="en">
	
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.Date"%>
<%@page import="java.util.TreeMap"%>
<%@page import="java.util.TreeSet"%>
<%@page import="java.util.HashSet"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.text.Format"%>
<%@page import="com.nmims.beans.TimetableBean"%>
<%@page import="com.nmims.beans.ExecutiveTimetableBean"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
    <jsp:include page="adminCommon/jscss.jsp">
	<jsp:param value="Exam Time-Table" name="title"/>
    </jsp:include>
    
    <%
    StudentExamBean student = (StudentExamBean)session.getAttribute("studentExam");
	String programStructure = "";
	
	if(student != null){
		programStructure = student.getPrgmStructApplicable();
	}
%>
    
    <body>
    
    	<%@ include file="adminCommon/header.jsp" %>
        <div class="sz-main-content-wrapper">
        
        	<jsp:include page="adminCommon/breadcrum.jsp">
			<jsp:param value="Exam;Exam Time-Table" name="breadcrumItems"/>
			</jsp:include>
        	
            
            <div class="sz-main-content menu-closed">
                    <div class="sz-main-content-inner">
              				<jsp:include page="adminCommon/left-sidebar.jsp">
								<jsp:param value="" name="activeMenu"/>
							</jsp:include>
              				
              				
              				<div class="sz-content-wrapper examsPage">
              						<%@ include file="adminCommon/adminInfoBar.jsp" %>
              					<div class="sz-content">
								
									<h2 class="red text-capitalize">Exam Time-Table</h2>
									<div class="clearfix"></div>
									<div class="panel-content-wrapper" style="min-height:450px;">
										<%@ include file="adminCommon/messages.jsp" %>
										<%@ include file="uploadExcelErrorMessages.jsp"%>
									
										<%
										
										List<TimetableBean> timeTableList = (List<TimetableBean>)request.getAttribute("timeTableList");
										TreeMap<String,  ArrayList<TimetableBean>> programTimetableMap = (TreeMap<String,  ArrayList<TimetableBean>>)request.getAttribute("programTimetableMap");
										%>
										
										<%if(programTimetableMap != null && programTimetableMap.size() > 0){ 
										int count = 0;
										int count2 = 1;
										
										for (Map.Entry<String, ArrayList<TimetableBean>> entry : programTimetableMap.entrySet()) {
											String key = entry.getKey();
											ArrayList<TimetableBean> list  = entry.getValue();
											count++;
											count2++;
											String acc= "accordion"+count;
										 %>
										 
									
				<div id="<%= acc%>" class="accordian">
									<h5><%= key%></h5>
										
							
									
									
									
									<div class="list-container">
										<div class="panel-content-wrapper">
										 <div class="panel-group" id="#accordion<%= count+2%>">
    <div class="panel panel-default">
      <div class="panel-heading">
        <h4 class="panel-title">
          <a data-toggle="collapse" data-parent="#accordion<%= count+1%>" href="#collapse<%= count2%>">Exam Slots List</a>
          <span style="float:right;" ><i class="fa-solid fa-circle-chevron-down"></i></span>
        </h4>
      </div>
      <div id="collapse<%= count2%>" class="panel-collapse collapse ">
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
														TreeMap<String,TimetableBean> tempMap = new TreeMap<>();
														HashSet<TimetableBean> tempList = new HashSet<>();
														for(int i = 0; i < list.size(); i++){
															TimetableBean bean = (TimetableBean)list.get(i);
															
															studentsSubjects.add(bean.getSubject());
												       		
												       		TimetableBean tempBean = new TimetableBean();
												       		tempBean.setDate(bean.getDate());
												       		tempBean.setStartTime(bean.getStartTime());
												       		tempBean.setEndTime(bean.getEndTime());
												       		tempList.add(tempBean);
														}

														for(TimetableBean b : tempList){
															tempMap.put(b.getDate()+b.getStartTime(), b);
														}
														for(Map.Entry<String,TimetableBean> e : tempMap.entrySet()) {
															TimetableBean bean = e.getValue();
															
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
          <a data-toggle="collapse" data-parent="#accordion<%= count+2%>" href="#collapse<%= count2+2%>">Available Subjects List</a>
          <span style="float:right;" ><i class="fa-solid fa-circle-chevron-down"></i></span>
        </h4>
      </div>
      <div id="collapse<%= count2+2%>" class="panel-collapse collapse">
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
       		
       
        </div>
      </div>
    </div>
    										<button id="submit" name="submit" class="btn btn-large btn-primary" onClick="window.print()">Print</button>
    
  </div> 
										

											</div>
										</div>
										
										</div>
                					<% } } %>
							</div>
							<br/>
							</div>
              				</div>
    				</div>
			</div>
		</div>
        <jsp:include page="adminCommon/footer.jsp"/>
        	<script>
	  $(function() {
	    $('.accordian' ).accordion({
	      collapsible: true,
	      heightStyle: "content"
	    });
	  });
	  </script>
        
		
    </body>
</html>