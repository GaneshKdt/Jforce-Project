<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.Date"%>
<%@page import="java.util.Calendar"%>
<%@page import="java.util.TreeMap"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.text.Format"%>
<%@page import="com.nmims.beans.*"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<%
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
              				<jsp:include page="common/left-sidebar.jsp">
								<jsp:param value="Exam Registration" name="activeMenu"/>
							</jsp:include>
              				
              				
              				<div class="sz-content-wrapper examsPage">
              						<%@ include file="common/studentInfoBar.jsp" %>
              						
              						
              						<div class="sz-content">
								
										<h2 class="red text-capitalize">Booking Summary</h2>
										<div class="clearfix"></div>
		              					<div class="panel-content-wrapper">
											<%@ include file="common/messages.jsp" %>
											<div class="table-responsive">
												<form:form  action="#" method="post">
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
															<th>Booking Status</th>
															<th>Exam Center Booked</th>
														</tr>
													</thead>
													<tbody>
													
													
													<%
															Map<String, String> examCenterIdNameMap = (HashMap<String, String>)session.getAttribute("examCenterIdNameMap");
																		//String totalFees = (String)request.getSession().getAttribute("totalFees");
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
																<td>
																<%
																String myTime = bean.getExamTime();
																 SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
																 Date d = df.parse(myTime); 
																 Calendar cal = Calendar.getInstance();
																 cal.setTime(d);
																 cal.add(Calendar.MINUTE, 90);
																 String examEndTime = df.format(cal.getTime());
																
																%>
																
																<%= examEndTime%>
																
																</td>
																<td><%= bookingStatus%></td>
																<td><%= examCenterName %></td>
															<%} %>
															
															
															
																							
												        </tr> 
												        <%} 
												        %>  
												        	
															
														
													</tbody>
												</table>
												
												<%-- <%if(totalFees != null) {%>
												<br>Total Exam Fees Paid: INR. <%=totalFees %>/-
												<%} %> --%>
												
												<div class="form-group">
													<label class="control-label" for="submit"></label>
													<div class="controls">
														<button id="cancel" name="cancel" type="button" formnovalidate="formnovalidate" class="btn btn-primary" onClick="window.open('printExecutiveBookingStatus')">Download Exam Booking Summary</button>
														<button id="cancel" name="cancel" class="btn btn-danger" formaction="executiveRegistrationForm" formnovalidate="formnovalidate">Back</button>
														
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