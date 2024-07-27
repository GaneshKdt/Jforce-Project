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


<html lang="en">
	
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.Date"%>
<%@page import="java.util.TreeMap"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.text.Format"%>
<%@page import="com.nmims.beans.TimetableBean"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
        <%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

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
										<%if("".equals(programStructure) || "Jul2009".equals(programStructure) || "Jul2013".equals(programStructure)){ %>
										<a href="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/codeOfConduct.pdf" target="_blank" ><b><i class="fa fa-download fa-lg"></i> 
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
			
									<h5><%= key%></h5>
										
									 
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
											<%}%> 											
												 
											 
											</tbody>
										</table>
										<button id="submit" name="submit" class="btn btn-large btn-primary" onClick="window.print()">Print</button>
									</div>
                					<% } %>
							<%if(programTimetableMap != null && programTimetableMap.size() > 0){ %>
								</div>
							<%} %>
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
	    $( "#accordion" ).accordion({
	      collapsible: true,
	      heightStyle: "content"
	    });
	  });
	  </script>
        
		
    </body>
</html>