<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!--> 


<html class="no-js"> <!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<jsp:include page="jscss.jsp">
<jsp:param value="Add Exam Center" name="title" />
</jsp:include>


<script language="JavaScript">
	function validateForm() {
		var mode = document.getElementById('mode').value;
		var capacity = document.getElementById('capacity').value;
		
		if(mode == 'Online'){
			if(capacity == ''){
				alert('Capacity can not be blank if Exam center is set up for Online mode');
				return false;
			}
		}
		return true;
	}
</script>
<%try{ %>
<body class="inside">

<%@ include file="header.jsp"%>
	
    <section class="content-container login">
        <div class="container-fluid customTheme">
        <div class="row"><legend>Add Exam Center</legend></div>
		
		<form:form  action="addExamCenter" method="post" modelAttribute="examCenter" onsubmit="return validateForm();">
			<fieldset>
			<div class="row clearfix">
			
			
			<div class="col-md-6 column">
				<%if("true".equals((String)request.getAttribute("edit"))){ %>
				<form:input type="hidden" path="centerId" value="${examCenter.centerId}"/>
				<%} %>
				<!-- Form Name -->
				
					<div class="form-group">
						<form:select id="year" path="year" type="text"	placeholder="Year" class="form-control"  required="required" itemValue="${examCenter.year}">
							<form:option value="">Select Exam Year</form:option>
							<form:options items="${yearList}" />
						</form:select>
					</div>
				
					<div class="form-group">
						<form:select id="month" path="month" type="text" placeholder="Month" class="form-control" required="required" itemValue="${examCenter.month}">
							<form:option value="">Select Exam Month</form:option>
							<form:option value="Apr">Apr</form:option>
							<form:option value="Jun">Jun</form:option>
							<form:option value="Sep">Sep</form:option>
							<form:option value="Dec">Dec</form:option>
						</form:select>
					</div>
					
					
						
						<%if("true".equals((String)request.getAttribute("edit"))){ 
						%>
						
						<div class="form-group">
						<form:input type="hidden" path="mode" value="${examCenter.mode}"/>
						Exam Mode: ${examCenter.mode}
						</div>
						<%}else{ 
						%>
						<div class="form-group">
						<form:select id="mode" path="mode" type="text" placeholder="Exam Mode" class="form-control"  required="required" itemValue="${examCenter.mode}" >
						<form:option value="">Select Exam Mode at this center</form:option>
							<form:option value="Online">Online</form:option>
							<form:option value="Offline">Offline</form:option>
						</form:select>
						</div>
						<%} %>
						
							
					
					<div class="form-group">
							<form:input id="examCenterName" path="examCenterName" type="text" required="required" placeholder="Exam Center Name" class="form-control" value="${examCenter.examCenterName}"/>
					</div>
					
					<div class="form-group">
						<form:select id="ic" path="ic" type="text" placeholder="Exam Mode" class="form-control" required="required" >
						<form:option value="">Select Corporate Center</form:option>
							<c:forEach var="icValue" items="${corporateCenterList}">
							<form:option value="${icValue}">${icValue}</form:option>
							</c:forEach>
						</form:select>
					</div>
					
					
					<div class="form-group">
							<form:input id="capacity" path="capacity" type="text" placeholder="Capacity" class="form-control" value="${examCenter.capacity}"/>
					</div>
					
				</div>
				
				
				<div class="col-md-6 column">

					<div class="form-group">
							<form:input id="locality" path="locality" type="text" placeholder="Locality" class="form-control" required="required" value="${examCenter.locality}"/>
					</div>
					
					<div class="form-group">
							<form:textarea id="address" path="address" placeholder="Address" rows="4" cols="48" required="required"></form:textarea>
					</div>
					
					<div class="form-group">
							<form:input id="city" path="city" type="text" placeholder="City" class="form-control" required="required" value="${examCenter.city}"/>
					</div>
					
					<div class="form-group">
						<form:select id="state" path="state" placeholder="State" class="form-control" required="required"  itemValue="${examCenter.state}">
							<form:option value="">Select State</form:option>
							<form:options items="${stateList}" />
						</form:select>
					</div>
					
					<div class="form-group">
							<form:textarea id="googleMapUrl" path="googleMapUrl" placeholder="Google Map URL" rows="2" cols="48"></form:textarea>
					</div>
					
				<div class="form-group">
					<label class="control-label" for="submit"></label>
						<%if("true".equals((String)request.getAttribute("edit"))){ %>
							<button id="submit" name="submit" class="btn btn-large btn-primary" formaction="updateExamCenter">Update</button>
						<%}else	{%>
							<button id="submit" name="submit" class="btn btn-large btn-primary" formaction="addExamCenter">Submit</button>
						<%} %>
						<button id="cancel" name="cancel" class="btn btn-danger" formaction="examCenterHome" formnovalidate="formnovalidate">Cancel</button>
				</div>
				</div>
				
				</div>
				
				
			</fieldset>
		</form:form>

		</div>
		
	
	</section>

	  <jsp:include page="footer.jsp" />
<%}catch(Exception e){ %>
					
					<%} %>

</body>
</html>
