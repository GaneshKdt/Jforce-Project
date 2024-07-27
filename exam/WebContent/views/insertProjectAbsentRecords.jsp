<!DOCTYPE html>
<%@page import="com.nmims.beans.StudentMarksBean"%>
<html class="no-js">

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<jsp:include page="jscss.jsp">
<jsp:param value="Insert Project Not Booked Records" name="title" />
</jsp:include>

<body class="inside">

<%@ include file="header.jsp"%>
	
    <section class="content-container">
        <div class="container-fluid customTheme">
       <div class="row"> <legend>Insert Project Not Booked Records</legend></div>
       <%@ include file="messages.jsp"%>
		
		<form:form  action="insertNotBookedStudent" method="post" modelAttribute="bean" id="notBookedRecord">
			<fieldset>
			<div class="panel-body">
			
			<div class="col-md-6 column">
					<div class="form-group">
						<form:select id="year" path="year"  cssClass="form-control"  itemValue="${bean.year}" >
							<form:option value="">Select Exam Year</form:option>
							<form:options items="${yearList}" />
						</form:select>
					</div>
				
					<div class="form-group">
						<form:select id="month" path="month"  cssClass="form-control" itemValue="${bean.month}">
							<form:option value="">Select Exam Month</form:option>
							<form:option value="Apr">Apr</form:option>
							<form:option value="Jun">Jun</form:option>
							<form:option value="Sep">Sep</form:option>
							<form:option value="Dec">Dec</form:option>
						</form:select>
					</div>
					
					<div class="form-group">
						<label class="control-label" ></label>
						<button id="submit" name="submit" class="btn btn-large btn-primary" onclick=" return validate()" >Submit</button>		
						<button id="cancel" type="submit" class="btn btn-danger" formaction="/exam/admin/uploadProjectNotBookedForm">Cancel</button>
						<c:if test="${count > 0 }">
						<a id ="submit" class="btn btn-danger" type="button" href ="downloadProjectNotBookedStudent">Download Report</a>
					</c:if>
					</div>
			</div>
			
			</div>
			</fieldset>
		</form:form>
		</div>
	</div>
	</section>

	  <jsp:include page="footer.jsp" />

<script>
$("#cancel").click(function(e) {
	document.getElementById('notBookedRecord').reset();
	$('select').prop('selectedIndex', 0);
	return true;
});

function validate(){
	const month = document.getElementById("month").value;
	const year = document.getElementById("year").value;
	
	if (month === "" || year === "") {
			alert("All Fields are required");
			return false;
		} else {
			$('#notBookedRecord').submit();
		}
	}
</script>
</body>
</html>
