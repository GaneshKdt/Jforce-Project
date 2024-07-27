<!DOCTYPE html>
<html class="no-js">
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<jsp:include page="/views/adminCommon/jscss.jsp">
	<jsp:param value=" ${ title } " name="title" />
</jsp:include>


<body class="inside">

<%@ include file="/views/adminCommon/header.jsp"%>
	
    <section class="content-container login">
        
        <div class="container-fluid customTheme">
       		<div class="row"> 
       			<legend>  ${ title }  </legend>
       		</div>
        	<%@ include file="/views/common/messages.jsp"%>
   	        <form style="padding:15px" class="form px-3" onsubmit="return false;">
				<input type="text" readonly hidden="hidden" id="packageId" name="packageId">
						
				<div class="form-group">
					<label for="packageName">Package Name</label>
					<input type="text"  class="form-control pl-2" id="packageName" name="packageName" required>
				</div>
				<div class="form-group">
					<label for="durationMax">Max Duration</label>
					<input type="number" min="0" class="form-control pl-2" id="durationMax" name="durationMax" >
				</div>
				<div class="form-group">
					<label for="packageFamily">Package Family</label>
					<select class="form-control pl-2" id="packageFamily" name="packageFamily">
					 	<c:forEach items="${Families}" var="family">	
							<option value="${ family.familyId }">${ family.familyName }</option>
						</c:forEach>
					</select>
				</div>
				<div class="form-group">
					<label for="durationType">Duration Type</label>
					<select class="form-control pl-2" id="durationType" name="durationType">
						<option value="Fast">Fast</option>
						<option value="Normal">Normal</option>
						<option value="Slow">Slow</option>
					</select>
				</div>
				
				<div class="form-group">
					<label for="durationType">Open For Sale</label>
					<select class="form-control pl-2" id="openForSale" name="openForSale">
						<option value="false">No</option>
						<option value="true">Yes</option>
					</select>
				</div>
		
			<button type="submit" class="btn btn-primary">Submit</button>
		</form>
       
        </div>
        
 	<div class="container-fluid customTheme"> 	
 		<legend> ${ tableTitle }</legend>
 	
        <table id="table" class="table table-striped table-bordered" style="width:100%">
	        <thead>
	            <tr>
	                <th>Sr.No</th>
	                <th>Sales Force UID</th>
	                <th>Family</th>
	                <th>Package Name</th>
	                <th>Open For Sales</th>
	                <th>Maximum Duration</th>
	                <th>Duration Type</th>
	                <th>Number Of Features</th>
	                <th>Package Purchasability</th>
	                <th>Links</th>
	            </tr>
	        </thead>
	        <tbody>
	        <% int i = 0; %>
			 	<c:forEach items="${AllPackagesData}" var="thisPackage">	
				 	<tr>
				 		<% i++; %>
				 		<td> <%= i %> </td>
		            	<td>${ thisPackage.salesForceUID}</td>
		            	<td><a href="updatePackageFamily?familyId=${ thisPackage.packageFamily }">${ thisPackage.familyName}</a></td>
		            	<td><a href="updatePackage?packageId=${ thisPackage.packageId }">${ thisPackage.packageName}</a></td>
		            	<td>${ thisPackage.openForSale }</td>
		            	<td>${ thisPackage.durationMax} Months</td>
		            	<td>${ thisPackage.durationType} Duration</td>
		            	<td><a href="addFeatureToPackage?packageId=${ thisPackage.packageId }">${ thisPackage.numberOfFeatures}</a></td>
		            	<td>
	            			<a href="addPackageRequirements?packageId=${ thisPackage.packageId }">
	            				<i class="fa fa-cog"></i> 
	            				Configure Constraints
            				</a>
           				</td>
           				<td>
		            		<a href="updatePackage?packageId=${ thisPackage.packageId }">
		            			<i class="fa fa-edit"></i> Edit
	            			</a>
            				<br>
	            			<a onclick="confirmDelete(`package`, `deletePackage?packageId=${ thisPackage.packageId }`, `${ thisPackage.packageId }`);">
	            				<i class="fa fa-trash"></i> Delete
            				</a> 
            				<br>
           				</td>
		            </tr>
					
				</c:forEach>
	            
	      	</tbody>
      	</table>
        </div>
    </section>
  	<jsp:include page="/views/adminCommon/footer.jsp" />
	  	
  	<script>
		$( document ).ready(function() {
			$("#durationType").val("${ Package.durationType }");
			$("#packageFamily").val("${ Package.packageFamily }");
			$("#packageId").val("${ Package.packageId }");
			$("#packageName").val("${ Package.packageName }" );
			$("#durationMax").val("${ Package.durationMax }");
			$("#openForSale").val("${ Package.openForSale }");

			
			$( 'form' ).submit(function ( e ) {
			    e.preventDefault();
			    var ddata = formDataToJSON();
			    $.ajax({
		            url: '${url}',
					type: 'post',
					dataType : 'json',
					contentType: 'application/json',
					data: ddata, 
					success : function(result) {
			                        if (result.status == "success") {
			                            successMessage("Success");
			                            window.location.reload();
			                        } else {
			                            errorMessage(result.message);
			                        }
									return false;
								},
					error: function(xhr, resp, text) {
						console.log(xhr, resp, text);
                        errorMessage("Error. Please check all fields and retry!");
					}
				});
			});
		});
		
	</script>
</body>

<script>
	$(document).ready(function() {
		<jsp:include page="/views/adminCommon/datatables.jsp" />
	} );
	
</script>

</html>