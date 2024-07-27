<!DOCTYPE html>

<html class="no-js"> <!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<jsp:include page="jscss.jsp">
<jsp:param value="Executive Add Exam Center" name="title" />
</jsp:include>



<%try{ %>
<body class="inside">

<%@ include file="header.jsp"%>
	
    <section class="content-container login">
        <div class="container-fluid customTheme">
        <div class="row"><legend>Add Executive Exam Center</legend></div>
		
		<form:form  action="addExamCenter" method="post" modelAttribute="examCenter" >
			<fieldset>
			<div class="row clearfix">
			
			
			<div class="col-md-6 column">
				<%if("true".equals((String)request.getAttribute("edit"))){ %>
				<form:input type="hidden" path="centerId" value="${examCenter.centerId}"/>
				<%} %>
				<!-- Form Name -->
						<div class="form-group">
						<form:select id="batchYear" path="batchYear" type="text"	placeholder="Batch Year" class="form-control"  required="required" itemValue="${examCenter.batchYear}">
							<form:option value="">Select Batch Year</form:option>
							<form:options items="${yearList}" />
						</form:select>
					</div>
				
					<div class="form-group">
						<form:select id="batchMonth" path="batchMonth" type="text" placeholder="Batch Month" class="form-control" required="required" itemValue="${examCenter.batchMonth}">
							<form:option value="">Select Batch Month</form:option>
							<form:options items="${monthList}" />
						</form:select>
					</div>
					
					<div class="form-group">
						<form:select id="year" path="year" type="text"	placeholder="Year" class="form-control"  required="required" itemValue="${examCenter.year}">
							<form:option value="">Select Exam Year</form:option>
							<form:options items="${yearList}" />
						</form:select>
					</div>
				
					<div class="form-group">
						<form:select id="month" path="month" type="text" placeholder="Month" class="form-control" required="required" itemValue="${examCenter.month}">
							<form:option value="">Select Exam Month</form:option>
							<form:options items="${monthList}" />
						</form:select>
					</div>
					
					<div class="form-group">
							<form:input id="examCenterName" path="examCenterName" type="text" required="required" placeholder="Exam Center Name" class="form-control" value="${examCenter.examCenterName}"/>
					</div>
					
					
					<div class="form-group">
							<form:input id="capacity" path="capacity" type="text" placeholder="Capacity" class="form-control" value="${examCenter.capacity}"/>
					</div>
					
					<div class="form-group">
							<form:input id="locality" path="locality" type="text" placeholder="Locality" class="form-control" required="required" value="${examCenter.locality}"/>
					</div>
					
				</div>
				
				
				<div class="col-md-6 column">

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
						
						<button id="submit" name="submit" class="btn btn-large btn-primary" formaction="addExecutiveExamCenter">Submit</button>
						<button id="cancel" name="cancel" class="btn btn-danger" formaction="examExecutiveCenterHome" formnovalidate="formnovalidate">Manage Centers</button>
						<button id="cancel" name="cancel" class="btn btn-danger" formaction="home" formnovalidate="formnovalidate">Cancel</button>
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
