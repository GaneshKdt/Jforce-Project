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

<jsp:include page="../jscss.jsp">
	<jsp:param value="PCP/VC Booking Status" name="title" />
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


	<%@ include file="../header.jsp"%>

	<section class="content-container login">
		<div class="container-fluid customTheme">

			<div class="row"><legend>PCP/VC Registration Status</legend></div>

			<%@ include file="../messages.jsp"%>
			<%
				String ddPaid = (String)session.getAttribute("ddPaid");
				List<PCPBookingTransactionBean> pcpBookings = (List<PCPBookingTransactionBean>)session.getAttribute("pcpBookings");
			%>
				
					<div class="table-responsive panel-body">
					<form:form  action="#" method="post" modelAttribute="examCenter">
					<fieldset>
					
					<table class="table table-striped" style="font-size:12px">
						<thead>
							<tr> 
								<th>Sr. No.</th>
								<th>Subject</th>
								<th>Sem</th>
								<th>Transaction Status</th>
								<th>Booking Status</th>
								<th>Center Booked</th>
							</tr>
						</thead>
						<tbody>
						
						<%
								Map<String, String> examCenterIdNameMap = (HashMap<String, String>)session.getAttribute("examCenterIdNameMap");
								String totalFees = (String)request.getSession().getAttribute("totalFees");
								int count = 1;
								for(int i = 0; i < pcpBookings.size(); i++){
									PCPBookingTransactionBean bean = (PCPBookingTransactionBean)pcpBookings.get(i);
									
									String booked = bean.getBooked();
									String bookingStatus = null;
									if("Y".equals(booked)){
										bookingStatus = "Booked";
									}else{
										bookingStatus = "Not Booked";
									}
									
									String subject = bean.getSubject();
							%>
					        <tr>
					            <td><%= count++%></td>
								<td><%= subject%></td>
								<td><%= bean.getSem()%></td>
								<td><%= bean.getTranStatus()%></td>
								<td><%= bookingStatus%></td>
								<td><%= bean.getCenter()%></td>
					        </tr> 
					        <%} 
					        %>  
							
						</tbody>
					</table>
					
					<%if(totalFees != null) {%>
					<br>Total PCP/VC Fees Paid: INR. <%=totalFees %>/-
					<%} %>
					
					<div class="form-group">
						<label class="control-label" for="submit"></label>
						<div class="controls">
							<button id="download" name="download" class="btn btn-large btn-primary" formaction="downloadEntryPass">Download Entry Pass</button>
							<button id="cancel" name="cancel" class="btn btn-danger" formaction="home" formnovalidate="formnovalidate">Back to Home</button>
							
						</div>
					</div>
					</fieldset>
					</form:form>
				</div>
				
	
		
		</div>
	</section>

    <jsp:include page="../footer.jsp" />

</body>
</html>
 --%>
 
 <!DOCTYPE html>
<%@page import="java.util.List"%>
<%@page import="com.nmims.beans.PCPBookingTransactionBean"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<%
	String ddPaid = (String)session.getAttribute("ddPaid");
	List<PCPBookingTransactionBean> pcpBookings = (List<PCPBookingTransactionBean>)session.getAttribute("pcpBookings");
%>
			
<html lang="en">
    

	
    
    <jsp:include page="../common/jscss.jsp">
	<jsp:param value="PCP/VC Booking Status" name="title"/>
    </jsp:include>
    
    
    
    <body>
    
    	<%@ include file="../common/header.jsp" %>
    	
    	
        
        <div class="sz-main-content-wrapper">
        
        	<%@ include file="../common/breadcrum.jsp" %>
        	
            
            <div class="sz-main-content menu-closed">
                    <div class="sz-main-content-inner">
              				<jsp:include page="../common/left-sidebar.jsp">
								<jsp:param value="PCP/VC Registration" name="activeMenu"/>
							</jsp:include>
              				
              				
              				<div class="sz-content-wrapper examsPage">
              						<%@ include file="../common/studentInfoBar.jsp" %>
              						
              						
              						<div class="sz-content">
								
										<h2 class="red text-capitalize">PCP Booking Status</h2>
										<div class="clearfix"></div>
		              					<div class="panel-content-wrapper">
											<%@ include file="../common/messages.jsp" %>
											
											<div class="table-responsive ">
											<form:form  action="downloadEntryPass" method="post" modelAttribute="examCenter">
											<fieldset>
											
											<table class="table table-striped" style="font-size:12px">
												<thead>
													<tr> 
														<th>Sr. No.</th>
														<th>Subject</th>
														<th>Sem</th>
														<th>Transaction Status</th>
														<th>Booking Status</th>
														<th>Center Booked</th>
													</tr>
												</thead>
												<tbody>
												
												<%
														Map<String, String> examCenterIdNameMap = (HashMap<String, String>)session.getAttribute("examCenterIdNameMap");
														String totalFees = (String)request.getSession().getAttribute("totalFees");
														int count = 1;
														for(int i = 0; i < pcpBookings.size(); i++){
															PCPBookingTransactionBean bean = (PCPBookingTransactionBean)pcpBookings.get(i);
															
															String booked = bean.getBooked();
															String bookingStatus = null;
															if("Y".equals(booked)){
																bookingStatus = "Booked";
															}else{
																bookingStatus = "Not Booked";
															}
															
															String subject = bean.getSubject();
													%>
											        <tr>
											            <td><%= count++%></td>
														<td><%= subject%></td>
														<td><%= bean.getSem()%></td>
														<td><%= bean.getTranStatus()%></td>
														<td><%= bookingStatus%></td>
														<td><%= bean.getCenter()%></td>
											        </tr> 
											        <%} 
											        %>  
													
												</tbody>
											</table>
											
											<%if(totalFees != null) {%>
												<h3 class="total-fee">
													<span>Total PCP/VC Fees Paid:</span>
													Rs. <%=totalFees %>/-
												</h3>
											<%} %>
											
											<div class="form-group">
												<label class="control-label" for="submit"></label>
												<div class="controls">
													<button id="download" name="download" class="btn btn-large btn-primary" formaction="downloadEntryPass">Download Entry Pass</button>
													<button id="cancel" name="cancel" class="btn btn-danger" formaction="home" formnovalidate="formnovalidate">Back to Home</button>
													
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
            
  	
        <jsp:include page="../common/footer.jsp"/>
            
		
    </body>
</html>