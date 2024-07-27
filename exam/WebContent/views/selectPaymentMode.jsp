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
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.nmims.beans.StudentBean"%>

<html class="no-js">
<!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<jsp:include page="jscss.jsp">
	<jsp:param value="Select Payment Mode" name="title" />
</jsp:include>


<script>
  $(function() {
    $( "#accordion" ).accordion({
      collapsible: true,
      heightStyle: "content"
    });
  });
  </script>

<body class="inside">


	<%@ include file="header.jsp"%>

	<section class="content-container login">
		<div class="container-fluid customTheme">

			<div class="row"><legend>Timetable view of Selected Subjects</legend></div>

			<%@ include file="messages.jsp"%>
			<%
				List<TimetableBean> timeTableList = (List<TimetableBean>)request.getSession().getAttribute("timeTableList");
				ArrayList<String> subjects = (ArrayList<String>)request.getSession().getAttribute("subjects");
				StudentBean student = (StudentBean)session.getAttribute("student");
				String programStructure = student.getPrgmStructApplicable();
				String firstName = student.getFirstName();
				String lastName = student.getLastName();
				String sapId = student.getSapid();
			%>
				<!-- <div id="accordion"> -->
				<div class="panel-body">
				<div class="col-md-12 column">
				<h2>Selected Subjects Time table</h2>
				<div>
					<div class="table-responsive">
					<table class="table table-striped" style="font-size:12px">
						<thead>
							<tr> 
								<th>Sr. No.</th>
								<th>Subject</th>
								<th>Sem</th>
								<th>Date</th>
								<th>Start Time</th>
								<th>End Time</th>
								<th>Exam Fees</th>
							</tr>
						</thead>
						<tbody>
						
						<%
						int count = 0;
						for(int i = 0; i < timeTableList.size(); i++){
							TimetableBean bean = (TimetableBean)timeTableList.get(i);
							SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
							SimpleDateFormat dateFormatter = new SimpleDateFormat("EEEE, dd-MMM-yyyy");
							Date formattedDate = formatter.parse(bean.getDate());
							String formattedDateString = dateFormatter.format(formattedDate);
						%>
					        <tr>
					            <%
								if("Jul2014".equals(programStructure)){
									count = (i/2 + 1);
								%>
									<td><%= (i/2 + 1)%></td>
								<%}else{ %>
									<td><%= ++count%></td>
								<%} %>
								<td><%= bean.getSubject()%></td>
								<td><%= bean.getSem()%></td>
								<td><%= formattedDateString%></td>
								<td><%= bean.getStartTime()%></td>
								<td><%= bean.getEndTime()%></td>
								<td><%=request.getAttribute("examFeesPerSubject")%>/-</td>
					        </tr> 
					        <%} 
						
							if(subjects.contains("Project")){
								%>
								<tr>
					            <td><%=++count%></td>
								<td>Project</td>
								<td>4</td>
								<td>NA</td>
								<td>NA</td>
								<td>NA</td>
								<td><%=request.getAttribute("examFeesPerSubject")%>/-</td>
					        </tr> 
							<%}
					        %>  
					       <tr>
					       <td colspan="6" align="right"><b>Total</b></td>
					       <td>${totalFees}/-</td>
					       </tr>
						</tbody>
					</table> 
					 
				</div>
				</div>
			</div>
			
			
		</div>
		
		<%
		if(subjects.contains("Project")){
		%>
		<div>
		<p align="justify">
			<b>Note:</b> 
			In case of non-submission of Project after Exam Registration and Payment of Fees: Fees paid will not be refunded 
			nor will be carry forwarded to next scheduled exam cycle. 
			The student will be marked 'Absent' in the Marksheet issued. 
			</p>
		</div>	
		<%} %>
	
		<div class="panel-body">
		<div class="col-md-12 column">
			<h2>Select Payment Mode</h2>
			<form:form  action="makePaymentForm" method="post" modelAttribute="examCenter">
			
			<fieldset>
				<div class="radioWrapper" align="left">
				
					 <label class="radio-inline flLeft">
                         <form:radiobutton path="paymentMode" value = "Online"  checked="true" class="radio"  name="inlineRadioOptions"/> Online Payment (Debit Card / Credit Card / Netbanking) 
                     </label>
					 <br><br>
                     
                     <label class="radio-inline flLeft">
                          <form:radiobutton path="paymentMode" value = "DD"  class="radio" name="inlineRadioOptions" /> Demand Draft  (DD Payment option is available till <spring:eval expression="@propertyConfigurer.getProperty('EXAM_FEE_DD_LAST_DATE')" />) 
                     </label>
                                    
					
				</div>
				<!-- <div class="form-group">
					<label class="control-label" for="submit"></label> -->
					<div class="controls">
						<button id="submit" name="submit" class="btn btn-large btn-primary" formaction="selectExamCenter">Proceed</button>
						<button id="cancel" name="cancel" class="btn btn-danger" formaction="selectSubjectsForm" formnovalidate="formnovalidate">Back</button>
					</div>
				<!-- </div> -->
				
			</fieldset>
			</form:form>
			</div>
			</div>
		</div>
	</section>

   <jsp:include page="footer.jsp" />

</body>
</html>
 --%>
 
 
 <!DOCTYPE html>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.Date"%>
<%@page import="java.util.TreeMap"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.text.Format"%>
<%@page import="com.nmims.beans.TimetableBean"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.nmims.beans.StudentExamBean"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<html lang="en">
    
    
    <jsp:include page="common/jscss.jsp">
	<jsp:param value="Select Payment Mode" name="title"/>
    </jsp:include>
    
    <script>
	  $(function() {
	    $( "#accordion" ).accordion({
	      collapsible: true,
	      heightStyle: "content"
	    });
	  });
	  </script>
    
    <%
    ArrayList<String> subjects = (ArrayList<String>)request.getSession().getAttribute("subjects");
    
    %>
    <body>
    
    	<%@ include file="common/header.jsp" %>
    	
    	
        
        <div class="sz-main-content-wrapper">
        
        	<%@ include file="common/breadcrum.jsp" %>
        	
            
            <div class="sz-main-content menu-closed">
                    <div class="sz-main-content-inner">
              				<jsp:include page="common/left-sidebar.jsp">
								<jsp:param value="Exam Registration" name="activeMenu"/>
							</jsp:include>
              				
              				
              				<div class="sz-content-wrapper examsPage">
              						<%@ include file="common/studentInfoBar.jsp" %>
              						
              						
              						<div class="sz-content">
								
										<h2 class="red text-capitalize">Select Payment Mode</h2>
										<div class="clearfix"></div>
		              					<div class="panel-content-wrapper">
											<%@ include file="common/messages.jsp" %>
											
											<div class="col-md-12 column">
											<form:form  action="makePaymentForm" method="post" modelAttribute="examCenter">
											
											<fieldset>
												<div class="radio" align="left">
												
													 <label >
								                         <form:radiobutton path="paymentMode" value = "Online"  checked="true" class="radio"  name="inlineRadioOptions"/> Online Payment (Debit Card / Credit Card / Netbanking)
								                     </label>
													 <br><br>
								                     
								                     <%-- <label >
								                          <form:radiobutton path="paymentMode" value = "DD"  class="radio" name="inlineRadioOptions" /> Demand Draft  (Option available till <spring:eval expression="@propertyConfigurer.getProperty('EXAM_FEE_DD_LAST_DATE')" />) 
								                     </label> --%>
								                                    
													
												</div>
												<!-- <div class="form-group">
													<label class="control-label" for="submit"></label> -->
													<div class="controls">
														<button id="submit" name="submit" class="btn btn-large btn-primary" formaction="selectExamCenter">Proceed</button>
														<button id="cancel" name="cancel" class="btn btn-danger" formaction="selectSubjectsForm" formnovalidate="formnovalidate">Back</button>
													</div>
												<!-- </div> -->
												
											</fieldset>
											</form:form>
											</div>
											
											<%if(subjects.contains("Project") || subjects.contains("Module 4 - Project")){%>
												<p class="examNote" align="justify">
													<b>Note:</b> 
													In case of non-submission of Project after Exam Registration and Payment of Fees: Fees paid will not be refunded 
													nor will be carry forwarded to next scheduled exam cycle. 
													The student will be marked 'Absent' in the Marksheet issued. 
													</p>
											<%} %>
										</div>
              								
              						</div>
              				</div>
              		
                            
					</div>
            </div>
        </div>
            
  	
        <jsp:include page="common/footer.jsp"/>
            
		
    </body>
</html>