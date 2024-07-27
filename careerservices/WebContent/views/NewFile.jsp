<!DOCTYPE html>
<html class="no-js">
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<jsp:include page="adminCommon/jscss.jsp">
<jsp:param value="Add Families " name="title" />
</jsp:include>


<body class="inside">

<%@ include file="header.jsp"%>
	<%
	boolean isEdit = "true".equals((String)request.getAttribute("edit"));
	%>
    <section class="content-container login">
        
        <div class="container-fluid customTheme">
       		<div class="row"> <legend>Add Families </legend></div>
        	<%@ include file="common/messages.jsp"%>
        </div>
        
        <div class="col-md-4 column">
        
          <div class="form-group">
			<input type="hidden" class="form-control" id="entitlementId" name="entitlementId" value="">
		  </div>
		  
		  <div class="form-group">
		  <label for="activated">Activated</label>
			  <select class="form-control pl-2" id="activated" name="activated">
				<option value="false">No</option>
				<option value="true">Yes</option>
			  </select>
		  </div>
		  
		 <div class="form-group">
		  <label for="activatedByStudent">Activated By Student</label>
			  <select class="form-control pl-2" id="activatedByStudent" name="activatedByStudent">
				<option value="false">No</option>
				<option value="true">Yes</option>
			  </select>
		  </div>
		  
		  <div class="form-group">
		  	  <label for="activationsLeft">Activations Left</label>
			  <input type="number" class="form-control" id="activationsLeft" name="activationsLeft" value="">
		  </div>
		  <div class="clearfix"></div>
		  <br>
		   	  <button class="btn btn-primary" type="submit">Submit form</button>
		  
        </div>
        <div class="clearfix"></div>
        <legend>&nbsp; Entitlement </legend>
        <div class="col-md-18 column">
        
        <table id="dataTable" class="table table-striped table-bordered" style="width:100%">
	        <thead>
	            <tr>
	                <th>Sr.No</th>
	                <th>Entitlement Id</th>
	                <th>Entitlement Name</th>
	                <th>Activated</th>
	                <th>Activated By Student</th>
	                <th>Activations Left</th>
	            </tr>
	        </thead>
	        <tbody>
	            <tr>
	            	<td></td>
	            	<td></td>
	            	<td></td>
	            	<td></td>
	            	<td></td>
	            	<td></td>
	            </tr>
	      	</tbody>
      	</table>
        
        </div>
        <br><br><br><br>
  	</section>
  	
  	<jsp:include page="adminCommon/footer.jsp" />
  	
</body>
<script>
$(document).ready(function() {
	$("#entitlementId").val("${ Entitlement.entitlementId}");
	$("#activated").val("${ Entitlement.activated}");
	$("#activatedByStudent").val("${ Entitlement.activatedByStudent}");
	$("#activationsLeft").val("${ Entitlement.activationsLeft}");

    $('#dataTable').DataTable();
} );
</script>
</html>