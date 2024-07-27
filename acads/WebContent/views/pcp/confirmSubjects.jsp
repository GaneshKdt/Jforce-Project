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

<jsp:include page="../jscss.jsp">
	<jsp:param value="Select Payment Mode" name="title" />
</jsp:include>


<script type="text/javascript">
function validateForm(mode) {
	var centerList = document.getElementsByName('selectedCenters');
	for(var i = 0; i < centerList.length; ++i)
	{
		var e = centerList[i];
		var selectedVal =  e.options[e.selectedIndex].value;
	    if(e.options[e.selectedIndex].value == ""){
	    	alert("Please select PCP/VC Centers for all subjects.");
	    	return false;
	    }
	}
	
	var e = centerList[0];
	var firstSubjectCenter = e.options[e.selectedIndex].innerHTML;
	var twoCities = false;
	for(var i = 0; i < centerList.length; ++i)
	{
		var e = centerList[i];
		var nextCenter =  e.options[e.selectedIndex].innerHTML;
	    if(nextCenter != firstSubjectCenter){
	    	twoCities = confirm ("You have selected different cities for subjects, are you sure you want to proceed with different cities?");
	    	if(twoCities){
	    		break;
	    	}else{
	    		return false;
	    	}
	    	
	    }
	}
	
	
	return confirm('Please note you will have 5 minutes to complete transaction. Are you sure you want to proceed?');
	
}


</script>


<body class="inside">


	<%@ include file="../header.jsp"%>

	<section class="content-container login">
		<div class="container-fluid customTheme">

			<div class="row"><legend>Select Payment Mode</legend></div>

			<%@ include file="../messages.jsp"%>
			<%
				ArrayList<String> subjects = (ArrayList<String>)request.getAttribute("subjects");
			%>
			
			<form:form id="subjectForm" action="goToGateway" method="post" modelAttribute="pcpBooking" >
			<div class="panel-body">
				<div class="col-md-18 column">
				<h2>Selected Subjects</h2>
				<div>
					<div class="table-responsive">
					<table class="table table-striped" style="font-size:12px">
						<thead>
							<tr> 
								<th>Sr. No.</th>
								<th>Subject</th>
								<th>Select Center</th>
								<th>Fees</th>
							</tr>
						</thead>
						<tbody>
						
						<%
						int count = 1;
						for(int i = 0; i < subjects.size(); i++){
							String subject = (String)subjects.get(i);

						%>
					        <tr>
					            <td><%= count++%></td>
								<td><%= subjects.get(i)%></td>
								<td>
									<select name="selectedCenters" id="selectedCenters<%=(i+1)%>" required="required">
										<option value="">Select Center</option>
										<option value="<c:out value="<%=subject %>"/>|Mumbai">Mumbai</option>
										<option value="<c:out value="<%=subject %>"/>|Delhi">Delhi</option>
										<option value="<c:out value="<%=subject %>"/>|Ahmedabad">Ahmedabad</option>
										<option value="<c:out value="<%=subject %>"/>|Kolkata">Kolkata</option>
										<option value="<c:out value="<%=subject %>"/>|Pune">Pune</option>
										<option value="<c:out value="<%=subject %>"/>|Hyderabad">Hyderabad</option>
										<option value="<c:out value="<%=subject %>"/>|Banglore">Banglore</option>
									</select>
					            </td>
								<td><%=request.getAttribute("feesPerSubject")%>/-</td>
					        </tr> 
					        <%} %>
							 
					       <tr>
					       <td colspan="2" align="right"><b>Total</b></td>
					       <td>${totalFees}/-</td>
					       </tr>
						</tbody>
					</table> 
					 
				</div>
				</div>
			</div>
			
			<div class="col-md-18 column">
					<fieldset>
						<form:hidden path="year"/>
						<form:hidden path="month"/>
							
						<div class="controls">
							<button id="submit" name="submit" class="btn btn-large btn-primary" formaction="goToGateway" onclick="return validateForm();">Proceed To Payment Gateway</button>
							<button id="cancel" name="cancel" class="btn btn-danger" formaction="selectPCPSubjectsForm" formnovalidate="formnovalidate">Back</button>
						</div>
					</fieldset>
				
			</div>
			
		</div>
		</form:form>
		</div>
	</section>

   <jsp:include page="../footer.jsp" />

</body>
</html>
 --%>
 
 <!DOCTYPE html>
<%@page import="java.util.Date"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.text.Format"%>
<%@page import="com.nmims.beans.TimetableAcadsBean"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.nmims.beans.StudentAcadsBean"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<%
	ArrayList<String> subjects = (ArrayList<String>)request.getAttribute("subjects");
%>
			
<html lang="en">
    

	
    
    <jsp:include page="../common/jscss.jsp">
	<jsp:param value="Select Center" name="title"/>
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
								
										<h2 class="red text-capitalize">Select Center</h2>
										<div class="clearfix"></div>
		              					<div class="panel-content-wrapper">
											<%@ include file="../common/messages.jsp" %>
											
											<form:form id="subjectForm" action="goToGateway" method="post" modelAttribute="pcpBooking" >
												<div>
													<div class="table-responsive">
													<table class="table table-striped" style="font-size:12px">
														<thead>
															<tr> 
																<th>Sr. No.</th>
																<th>Subject</th>
																<th>Select Center</th>
																<th>Fees</th>
															</tr>
														</thead>
														<tbody>
														
														<%
														int count = 1;
														for(int i = 0; i < subjects.size(); i++){
															String subject = (String)subjects.get(i);
								
														%>
													        <tr>
													            <td><%= count++%></td>
																<td><%= subjects.get(i)%></td>
																<td>
																	<select name="selectedCenters" id="selectedCenters<%=(i+1)%>" required="required">
																		<option value="">Select Center</option>
																		<option value="<c:out value="<%=subject %>"/>|Mumbai">Mumbai</option>
																		<%-- <option value="<c:out value="<%=subject %>"/>|Delhi">Delhi</option>
																		<option value="<c:out value="<%=subject %>"/>|Ahmedabad">Ahmedabad</option>
																		<option value="<c:out value="<%=subject %>"/>|Kolkata">Kolkata</option> --%>
																		<option value="<c:out value="<%=subject %>"/>|Pune">Pune</option>
																		<%-- <option value="<c:out value="<%=subject %>"/>|Hyderabad">Hyderabad</option>
																		<option value="<c:out value="<%=subject %>"/>|Banglore">Banglore</option> --%>
																	</select>
													            </td>
																<td><%=request.getAttribute("feesPerSubject")%>/-</td>
													        </tr> 
													        <%} %>
													       
														</tbody>
													</table> 
													 
													 
													 <h3 class="total-fee">
														<span>Total Fees:</span>
														Rs. ${totalFees}/-
													</h3>
													
												</div>
												</div>
											
											<div>
													<fieldset>
														<form:hidden path="year"/>
														<form:hidden path="month"/>
														<form:hidden path="amount"/>	
														<div class="controls">
															<button id="submit" name="submit" class="btn btn-large btn-primary" formaction="/acads/student/goToGateway" onclick="return validateForm();">Proceed To Payment Gateway</button>
															<button id="cancel" name="cancel" class="btn btn-danger" formaction="selectPCPSubjectsForm" formnovalidate="formnovalidate">Back</button>
														</div>
													</fieldset>
												
											</div>
											
										</form:form>
											
										</div>
              								
              						</div>
              				</div>
              		
                            
					</div>
            </div>
        </div>
            
  	
        <jsp:include page="../common/footer.jsp"/>
            
		
    </body>
</html>