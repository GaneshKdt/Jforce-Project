<%-- <!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!--> 

<%@page import="com.nmims.beans.Person"%>
<%@page import="com.nmims.beans.Page"%>
<%@page import="com.nmims.beans.ExamBookingTransactionBean"%>
<%@page import="java.util.*"%>
<html class="no-js"> <!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<jsp:include page="jscss.jsp">
<jsp:param value="Release Bookings" name="title" />
</jsp:include>

<script type="text/javascript">
	function isNumberKey(evt){
	    var charCode = (evt.which) ? evt.which : event.keyCode;
	    if (charCode > 31 && (charCode < 48 || charCode > 57)){
	    	return false;
	    }
	        
	    return true;
	}
	
	function submitForm(id, action, url){
		var c = confirm('Are you sure you want to '+action + ' this DD?');
		if(!c){
			return false;
		}
		var reason = document.getElementById(id).value;
		if(reason == '' && action == 'Reject'){
			alert("Please enter reason for rejection.");
			return false;
		}else{
			window.location.href = url + '&reason=' + reason;
		}
	}
	
function validateForm() {
		
		var assignmentSubmittedList = document.getElementsByName('releaseSubjects');
		var atleastOneSelected = false;
		for(var i = 0; i < assignmentSubmittedList.length; ++i)
		{
		    if(assignmentSubmittedList[i].checked){
		    	atleastOneSelected = true;
		    	break;
		    }
		}
		if(!atleastOneSelected){
			alert("Please select at least one subject to proceed.")
			return false;
		}
		
		return confirm('Are you sure you want to release seats for those subjects?');
		    
	}

</script>

<body class="inside">

<%@ include file="header.jsp"%>
	
    <section class="content-container login">
        <div class="container-fluid customTheme">
       <div class="row"> <legend>Release Bookings</legend></div>
        <%@ include file="messages.jsp"%>
		<%
			final String DD_APPROVAL_PENDING = "DD Approval Pending";
				final String DD_APPROVED = "DD Approved";
				final String DD_REJECTED = "DD Rejected";
				final String SEAT_RELEASED = "Seat Released";
				boolean hasSeatToRelease = false;
				ArrayList<ExamBookingTransactionBean> confirmedBookings = (ArrayList<ExamBookingTransactionBean>)request.getAttribute("confirmedBookings");
		%>
		<form:form  action="searchBookingsToRelease" method="post" modelAttribute="booking" id="ddDetails">
			<fieldset>
			<div class="row clearfix">
			
			<div class="col-md-6 column">
					
					<div class="form-group">
						<form:select id="year" path="year" type="text"	placeholder="Year" class="form-control"  itemValue="${booking.year}">
							<form:option value="">Select Exam Year</form:option>
							<form:options items="${yearList}" />
						</form:select>
					</div>
				
					<div class="form-group">
						<form:select id="month" path="month" type="text" placeholder="Month" class="form-control" itemValue="${booking.month}">
							<form:option value="">Select Exam Month</form:option>
							<form:option value="Apr">Apr</form:option>
							<form:option value="Jun">Jun</form:option>
							<form:option value="Sep">Sep</form:option>
							<form:option value="Dec">Dec</form:option>
						</form:select>
					</div>
					
					<div class="form-group">
						<form:input id="sapid" path="sapid" type="text" required="required" placeholder="Student ID" class="form-control" maxlength="11"
						value="${booking.sapid}" onkeypress="return isNumberKey(event)" />
					</div>
					
					<div class="form-group">
						<label class="control-label" for="submit"></label>
						<button id="submit" name="submit" class="btn btn-large btn-primary" formaction="searchBookingsToRelease">Search</button>
						<button id="cancel" name="cancel" class="btn btn-danger" formaction="home" formnovalidate="formnovalidate">Cancel</button>
					</div>
					
			</div>
			

			</div>
			</fieldset>
		</form:form>
		
		
	
	
	<c:choose>
	<c:when test="${rowCount > 0}">

	<legend>&nbsp;Booked Subjects<font size="2px"> (${rowCount} Records Found) &nbsp; </font></legend>
	<div class="table-responsive">
	<form:form  action="searchBookingsToRelease" method="post" modelAttribute="booking" id="ddDetails">
			<fieldset>
	<table class="table table-striped" style="font-size:12px">
						<thead>
						<tr>
							<th>Sr. No.</th>
							<th>Select</th>
							<th>Year</th>
							<th>Month</th>
							<th>SAP ID</th>
							<th>Subject</th>
							<th>Status</th>
							<th>First Name</th>
							<th>Last Name</th>
							<th>Email</th>
							<th>Mobile</th>
							<th>Alt. Phone</th>
							<th>Center Name</th>

						</tr>
					</thead>
						<tbody>
						
						<%
													HashMap<String, String> examCenterIdNameMap = (HashMap<String, String>)session.getAttribute("examCenterIdNameMap");
																for(int i = 0; i < confirmedBookings.size(); i++){
																	
																	ExamBookingTransactionBean bean = confirmedBookings.get(i);
																	String examCenterName = examCenterIdNameMap.get(bean.getCenterId());
												%>
							
							<tr>
					            <td><%= (i+1)%></td>
					            <%if("Y".equalsIgnoreCase(bean.getBooked())){ 
					            	hasSeatToRelease = true;
					            %>
					            <td><form:checkbox path="releaseSubjects" value="<%=bean.getSubject() %>"  /></td>
					            <%}else{ %>
					            <td></td>
					            <%} %>
					            <td><%=bean.getYear()%></td>
					            <td><%=bean.getMonth()%></td>
					            <td><%=bean.getSapid()%></td>
					            <td><%=bean.getSubject()%></td>
					            <td><%=bean.getTranStatus()%></td>
					            <td><%=bean.getFirstName()%></td>
					            <td><%=bean.getLastName()%></td>
					            <td><%=bean.getEmailId()%></td>
					            <td><%=bean.getMobile()%></td>
					            <td><%=bean.getAltPhone()%></td>
					            <td><%=examCenterName%></td>
          
							</tr>
						<% }%>
						</tbody>
					</table>
					<form:input path="sapid" type="hidden" value="${booking.sapid}"/>
					<form:input path="year" type="hidden" value="${booking.year}"/>
					<form:input path="month" type="hidden" value="${booking.month}"/>
					
					<div class="form-group">
						<label class="control-label" for="submit"></label>
						<%if(hasSeatToRelease){ %>
						<button id="submit" name="submit" class="btn btn-large btn-primary" onclick="return validateForm();" formaction="releaseBookings?noCharges=false">Release</button>
						<button id="submit" name="submit" class="btn btn-large btn-primary" onclick="return validateForm();" formaction="releaseBookings?noCharges=true">Release with No Charges</button>
						<%} %>
						<button id="cancel" name="cancel" class="btn btn-danger" formaction="home" formnovalidate="formnovalidate">Cancel</button>
					</div>
					</fieldset>
		</form:form>
	</div>
	<br>

</c:when>
</c:choose>


</div>

	</section>

	  <jsp:include page="footer.jsp" />
	

</body>
</html> Old Code--%>
<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!--> 

<%@page import="com.nmims.beans.Person"%>
<%@page import="com.nmims.beans.Page"%>
<%@page import="com.nmims.dto.ExamBookingTransactionDTO"%>
<%@page import="java.util.*"%>
<html class="no-js"> <!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<jsp:include page="jscss.jsp">
<jsp:param value="Release Bookings" name="title" />
</jsp:include>

<script type="text/javascript">
	function isNumberKey(evt){
	    var charCode = (evt.which) ? evt.which : event.keyCode;
	    if (charCode > 31 && (charCode < 48 || charCode > 57)){
	    	return false;
	    }
	        
	    return true;
	}
	
	function submitForm(id, action, url){
		var c = confirm('Are you sure you want to '+action + ' this DD?');
		if(!c){
			return false;
		}
		var reason = document.getElementById(id).value;
		if(reason == '' && action == 'Reject'){
			alert("Please enter reason for rejection.");
			return false;
		}else{
			window.location.href = url + '&reason=' + reason;
		}
	}
	
function validateForm(element) {
		
		var assignmentSubmittedList = document.getElementsByName('releaseSubjects');
		var releaseReasonList = document.getElementsByName("releaseReasonsList");
		var dataArg=element.getAttribute("data-arg");
	
		var atleastOneSelected = false;
		var reasonCount = 0;
		var checkedCount = 0;
		
		for(var i = 0; i < assignmentSubmittedList.length; ++i)
		{
		    if(assignmentSubmittedList[i].checked){
		    	atleastOneSelected = true;
				++checkedCount;
		    	
		    	if(document.getElementsByName("releaseReasonsList")[i].value.length < 15){
			    	alert("Please provide at least 15 characters valid reason for selected seat record.")
					return false;
				}
		    	++reasonCount;
		    }else{
			    if(releaseReasonList[i].value.length >= 15){
			    	++reasonCount;
				}
			}
		}
		
		if(!atleastOneSelected){
			alert("Please select at least one subject to proceed.")
			return false;
		}

		if(checkedCount != reasonCount){
			alert("Please provide the reason for the selected subjects only to proceed.")
			return false;
		}

		if(dataArg=='RL')
			return confirm('Are you sure you want to release seats for those subjects?');
		else if(dataArg=='CL')
			return confirm('Are you sure you want to cancel seats for those subjects?');
		    
	}

</script>

<body class="inside">

<%@ include file="header.jsp"%>
	
    <section class="content-container login">
        <div class="container-fluid customTheme">
       <div class="row"> <legend>Release Bookings</legend></div>
        <%@ include file="messages.jsp"%>
		<%
		final String DD_APPROVAL_PENDING = "DD Approval Pending";
		final String DD_APPROVED = "DD Approved";
		final String DD_REJECTED = "DD Rejected";
		final String SEAT_RELEASED = "Seat Released";
		boolean hasSeatToRelease = false;
		List<ExamBookingTransactionDTO> confirmedBookings = (List<ExamBookingTransactionDTO>)request.getAttribute("confirmedBookings");
		%>
		<form:form  action="searchBookingsToRelease" method="post" modelAttribute="booking" id="ddDetails">
			<fieldset>
			<div class="row clearfix">
			
			<div class="col-md-6 column">
					
					<div class="form-group">
						<form:select id="year" path="year" type="text"	placeholder="Year" class="form-control"  itemValue="${booking.year}">
							<form:option value="">Select Exam Year</form:option>
							<form:options items="${yearList}" />
						</form:select>
					</div>
				
					<div class="form-group">
						<form:select id="month" path="month" type="text" placeholder="Month" class="form-control" itemValue="${booking.month}">
							<form:option value="">Select Exam Month</form:option>
							<form:option value="Apr">Apr</form:option>
							<form:option value="Jun">Jun</form:option>
							<form:option value="Sep">Sep</form:option>
							<form:option value="Dec">Dec</form:option>
						</form:select>
					</div>
					
					<div class="form-group">
						<form:input id="sapid" path="sapid" type="text" required="required" placeholder="Student ID" class="form-control" maxlength="11"
						value="${booking.sapid}" onkeypress="return isNumberKey(event)" />
					</div>
					
					<form:input path="productType" type="hidden" value="${booking.productType}"/>
					
					<div class="form-group">
						<label class="control-label" for="submit"></label>
						<button id="submit" name="submit" class="btn btn-large btn-primary" formaction="searchBookingsToRelease">Search</button>
						<button id="cancel" name="cancel" class="btn btn-danger" formaction="home" formnovalidate="formnovalidate">Cancel</button>
					</div>
					
			</div>
			

			</div>
			</fieldset>
		</form:form>
		
		
	
	
	<c:choose>
	<c:when test="${rowCount > 0}">

	<legend>&nbsp;Booked Subjects<font size="2px"> (${rowCount} Records Found) &nbsp; </font></legend>
	<div class="table-responsive">
	<form:form  action="searchBookingsToRelease" method="post" modelAttribute="booking" id="ddDetails">
			<fieldset>
	<table class="table table-striped" style="font-size:12px">
						<thead>
						<tr>
							<th>Sr. No.</th>
							<th>Select</th>
							<th>Release Reason<span style="color:red">*</span></th>
							<th>Year</th>
							<th>Month</th>
							<th>SAP ID</th>
							<th>Subject</th>
							<th>Status</th>
							<th>First Name</th>
							<th>Last Name</th>
							<th>Email</th>
							<th>Mobile</th>
							<th>Alt. Phone</th>
							<th>Center Name</th>
							<th>Exam Date</th>
							<th>Exam Start Time</th>
							<th>TrackId</th>

						</tr>
					</thead>
						<tbody>
						
						<%
							Map<String, String> examCenterIdNameMap = (Map<String, String>)session.getAttribute("examCenterIdNameMap");
							for(int i = 0; i < confirmedBookings.size(); i++){
								ExamBookingTransactionDTO bean = confirmedBookings.get(i);
								String examCenterName = examCenterIdNameMap.get(bean.getCenterId());
							%>
							
							<tr>
					            <td><%= (i+1)%></td>
					            <%if("Y".equalsIgnoreCase(bean.getBooked())){ 
					            	hasSeatToRelease = true;
					            	String val = bean.getSubject().replace(",", "")+"|"+bean.getTrackId();
					            %>
					            <td><form:checkbox path="releaseSubjects" value="<%=val %>" /></td>
					            <td><form:input id="reason"  path="releaseReasonsList" /></td>
					            <%}else{ %>
					            <td></td>
					            <td></td>
					            <%} %>
					            <td><%=bean.getYear()%></td>
					            <td><%=bean.getMonth()%></td>
					            <td><%=bean.getSapid()%></td>
					            <td><%=bean.getSubject()%></td>
					            <td><%=bean.getTranStatus()%></td>
					            <td><%=bean.getFirstName()%></td>
					            <td><%=bean.getLastName()%></td>
					            <td><%=bean.getEmailId()%></td>
					            <td><%=bean.getMobile()%></td>
					            <td><%=bean.getAltPhone()%></td>
					            <td><%=examCenterName%></td>
					            <td><%=bean.getExamDate()%></td>
					            <td><%=bean.getExamTime()%></td>
					            <td><%=bean.getTrackId() %></td>
          
							</tr>
						<% }%>
						</tbody>
					</table>
					<form:input path="sapid" type="hidden" value="${booking.sapid}"/>
					<form:input path="year" type="hidden" value="${booking.year}"/>
					<form:input path="month" type="hidden" value="${booking.month}"/>
					<form:input path="productType" type="hidden" value="${booking.productType}"/>
					
					<div class="form-group">
						<label class="control-label" for="submit"></label>
						<%if(hasSeatToRelease){ %>
						<button id="submit" name="submit" class="btn btn-large btn-primary" data-arg="RL" onclick="return validateForm(this);" formaction="releaseBookings?noCharges=false">Release</button>
						<button id="submit" name="submit" class="btn btn-large btn-primary" data-arg="RL" onclick="return validateForm(this);" formaction="releaseBookings?noCharges=true">Release with No Charges</button>
						<button id="submit" name="submit" class="btn btn-large btn-primary" data-arg="CL" onclick="return validateForm(this);" formaction="cancelExamBookings?refund=true">Cancellation with refund</button>
						<button id="submit" name="submit" class="btn btn-large btn-primary" data-arg="CL" onclick="return validateForm(this);" formaction="cancelExamBookings?refund=false">Cancellation without refund</button>
						<%} %>
						<button id="cancel" name="cancel" class="btn btn-danger" formaction="home" formnovalidate="formnovalidate">Cancel</button>
					</div>
					</fieldset>
		</form:form>
	</div>
	<br>

</c:when>
</c:choose>


</div>

	</section>

	  <jsp:include page="footer.jsp" />
	

</body>
</html>
