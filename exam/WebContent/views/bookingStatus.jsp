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
<%@page import="com.nmims.beans.*"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>

<html class="no-js">
<!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<jsp:include page="jscss.jsp">
	<jsp:param value="Exam Booking Status" name="title" />
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

			<div class="row"><legend>Exam Booking Status</legend></div>

			<%@ include file="messages.jsp"%>
			<%
				String ddPaid = (String)session.getAttribute("ddPaid");
					List<ExamBookingTransactionBean> examBookings = (List<ExamBookingTransactionBean>)session.getAttribute("examBookings");
					StudentBean student = (StudentBean)session.getAttribute("student");
			%>
				
			<div class="panel-body">
			
			<div class="col-sm-6">
				<div class="row">
						<b>Student Name : </b><%=student.getFirstName()%> <%=student.getLastName()%>
				</div>
				<div class="row">
						<b>Student ID : </b><%=student.getSapid()%>
				</div>
				<div class="row">
						<b>Program : </b><%=student.getProgram()%>
				</div>
			</div>
	
			</div>

					
					<div class="table-responsive panel-body">
					<form:form  action="#" method="post" modelAttribute="examCenter">
					<fieldset>
					
					<table class="table table-striped" style="font-size:12px">
						<thead>
							<tr> 
								<th>Sr. No.</th>
								<th>Subject</th>
								<th>Sem</th>
								<th>Date</th>
								<th>Start Time</th>
								<th>End Time</th>
								<th>Transaction Status</th>
								<th>Booking Status</th>
								<th>Exam Center Booked</th>
							</tr>
						</thead>
						<tbody>
						
						
						<%
								Map<String, String> examCenterIdNameMap = (HashMap<String, String>)session.getAttribute("examCenterIdNameMap");
											String totalFees = (String)request.getSession().getAttribute("totalFees");
											int count = 1;
											for(int i = 0; i < examBookings.size(); i++){
												ExamBookingTransactionBean bean = (ExamBookingTransactionBean)examBookings.get(i);
												SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
												SimpleDateFormat dateFormatter = new SimpleDateFormat("EEEE, dd-MMM-yyyy");
												Date formattedDate = formatter.parse(bean.getExamDate());
												String examDate = dateFormatter.format(formattedDate);
										
												
												String booked = bean.getBooked();
												String bookingStatus = null;
												if("Y".equals(booked)){
													bookingStatus = "Booked";
												}else{
													bookingStatus = "Not Booked";
												}
												String examCenterName = examCenterIdNameMap.get(bean.getCenterId());
												
												String subject = bean.getSubject();
							%>
					        <tr>
					            <td><%= count++%></td>
								<td><%= subject%></td>
								<td><%= bean.getSem()%></td>
								
								<%if("Project".equals(subject)){ %>
									<td>NA</td>
									<td>NA</td>
									<td>NA</td>
									<td><%= bean.getTranStatus()%></td>
									<td><%= bookingStatus%></td>
									<td>NA</td>
								<%}else{ %>
									<td><%= examDate%></td>
									<td><%= bean.getExamTime()%></td>
									<td><%= bean.getExamEndTime()%></td>
									<td><%= bean.getTranStatus()%></td>
									<td><%= bookingStatus%></td>
									<td><%= examCenterName %></td>
								<%} %>
								
								
								
																
					        </tr> 
					        <%} 
					        %>  
					        	
								
							
						</tbody>
					</table>
					
					<%if(totalFees != null) {%>
					<br>Total Exam Fees Paid: INR. <%=totalFees %>/-
					<%} %>
					
					<div class="form-group">
						<label class="control-label" for="submit"></label>
						<div class="controls">
							<button id="cancel" name="cancel" type="button" formnovalidate="formnovalidate" class="btn btn-primary" onClick="window.open('printBookingStatus')">Download Exam Booking Receipt</button>
							<button id="cancel" name="cancel" class="btn btn-danger" formaction="home" formnovalidate="formnovalidate">Back to Home</button>
							
						</div>
					</div>
					</fieldset>
					</form:form>
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
<%@page import="com.nmims.beans.*"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<%
	String ddPaid = (String)session.getAttribute("ddPaid");
	List<ExamBookingTransactionBean> examBookings = (List<ExamBookingTransactionBean>)session.getAttribute("examBookings");
	StudentExamBean student = (StudentExamBean)session.getAttribute("studentExam");
%>
			
<html lang="en">
    

	
    
    <jsp:include page="common/jscss.jsp">
	<jsp:param value="Booking Summary" name="title"/>
    </jsp:include>
    
    
    
    <body>
    
    	<%@ include file="common/header.jsp" %>
    	
    	
        
        <div class="sz-main-content-wrapper">
        
        	<%@ include file="common/breadcrum.jsp" %>
        	
            
            <div class="sz-main-content menu-closed">
                    <div class="sz-main-content-inner">
                    <div id="sticky-sidebar"> 
             			<jsp:include page="common/left-sidebar.jsp">
							<jsp:param value="Exam Registration" name="activeMenu"/>
						</jsp:include>
					</div>          				
              				<div class="sz-content-wrapper examsPage">
              						<%@ include file="common/studentInfoBar.jsp" %>
              						
              						
              						<div class="sz-content">
								
										<h2 class="red text-capitalize">Booking Summary</h2>
										<div class="clearfix"></div>
		              					<div class="panel-content-wrapper">
											<%@ include file="common/messages.jsp" %>
											<div class="table-responsive">
												<form:form  action="#" method="post" modelAttribute="examCenter">
												<fieldset>
												
												<table class="table table-striped" style="font-size:12px">
													<thead>
														<tr> 
															<th>Sr. No.</th>
															<th>Subject</th>
															<th>Sem</th>
															<th>Date</th>
															<th>Start Time</th>
															<th>End Time</th>
															<th>Transaction Status</th>
															<th>Booking Status</th>
															<th>Exam Center Booked</th>
														</tr>
													</thead>
													<tbody>
													
													
													<%
															Map<String, String> examCenterIdNameMap = (HashMap<String, String>)session.getAttribute("examCenterIdNameMap");
																		String totalFees = (String)request.getSession().getAttribute("totalFees");
																		int count = 1;
																		for(int i = 0; i < examBookings.size(); i++){
																			ExamBookingTransactionBean bean = (ExamBookingTransactionBean)examBookings.get(i);
																			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
																			SimpleDateFormat dateFormatter = new SimpleDateFormat("EEEE, dd-MMM-yyyy");
																			Date formattedDate = formatter.parse(bean.getExamDate());
																			String examDate = dateFormatter.format(formattedDate);
																	
																			
																			String booked = bean.getBooked();
																			String bookingStatus = null;
																			if("Y".equals(booked)){
																				bookingStatus = "Booked";
																			}else{
																				bookingStatus = "Not Booked";
																			}
																			String examCenterName = examCenterIdNameMap.get(bean.getCenterId());
																			
																			String subject = bean.getSubject();
														%>
												        <tr>
												            <td><%= count++%></td>
															<td><%= subject%></td>
															<td><%= bean.getSem()%></td>
															
															<%if("Project".equals(subject) || "Module 4 - Project".equals(subject)){ %>
																<td>NA</td>
																<td>NA</td>
																<td>NA</td>
																<td><%= bean.getTranStatus()%></td>
																<td><%= bookingStatus%></td>
																<td>NA</td>
															<%}else{ %>
																<td><%= examDate%></td>
																<td><%= bean.getExamTime()%></td>
																<td><%= bean.getExamEndTime()%></td>
																<td><%= bean.getTranStatus()%></td>
																<td><%= bookingStatus%></td>
																<td><%= examCenterName %></td>
															<%} %>
															
															
															
																							
												        </tr> 
												        <%} 
												        %>  
												        	
															
														
													</tbody>
												</table>
												
												<%if(totalFees != null) {%>
												<br>Total Exam Fees Paid: INR. <%=totalFees %>/-
												<%} %>
												
												<div class="form-group">
													<label class="control-label" for="submit"></label>
													<div class="controls">
														<button id="cancel" name="cancel" type="button" formnovalidate="formnovalidate" class="btn btn-primary" onClick="window.open('student/printBookingStatus')">Download Exam Booking Receipt</button>
														<button id="cancel" name="cancel" class="btn btn-danger" formaction="selectSubjectsForm" formnovalidate="formnovalidate">Back</button>
														
													</div>
												</div>
												</fieldset>
												</form:form>
											</div>
											
										</div>
              								
              						</div>
              				</div>
              		
                            
					</div>
            </div>
        </div>
            
  	
        <jsp:include page="common/footer.jsp"/>
            
		
    </body>
</html>