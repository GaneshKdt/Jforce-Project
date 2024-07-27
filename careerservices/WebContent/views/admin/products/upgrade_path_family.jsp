<!DOCTYPE html>
<html class="no-js">
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<jsp:include page="/views/adminCommon/jscss.jsp">
	<jsp:param value="${ title }" name="title" />
</jsp:include>


<body class="inside">

<%@ include file="/views/adminCommon/header.jsp"%>
    <section class="content-container login">
        
        <div class="container-fluid customTheme">
       		<div class="row"> <legend>${ title }</legend></div>
        	<%@ include file="/views/common/messages.jsp"%>
	       	<form style="padding:15px" class="form px-3" onsubmit="return false;">
				<input type="text" readonly hidden="hidden"  name="pathId" value="${ pathId }">
				<div class="clearfix"></div>
				<div class="col-12">		
					<% if(!(boolean) request.getAttribute("Update")){%>
						<div class="form-group col-12">
							<label for="levelValue">Select Family</label>
							<select  class="form-control pl-2" id="packageFamilyId" name="packageFamilyId">
							 	<c:forEach items="${FamiliesNotInPath}" var="thisFamily">	
							 		<option value="${ thisFamily.familyId }">${ thisFamily.familyName }</option>
							 	</c:forEach>
							</select>
						</div>
					<% } else { %>
						<input type="text" readonly hidden="hidden"  name="packageFamilyId" value="${ UpgradePathFamily.packageFamilyId }">
					<% } %>
					<div class="form-group col-12">
						<label for="levelValue">Level Value</label>
						<input type="number"  class="form-control pl-2" id="levelValue" name="levelValue">
					</div>
					<div class="form-group col-12">
						<label for="minLevelToPurchase">Min Level To Purchase</label>
						<input type="number"  class="form-control pl-2" id="minLevelToPurchase" name="minLevelToPurchase" required>
					</div>
					<div class="form-group col-12">
						<label for="maxLevelToPurchase">Max Level To Purchase</label>
						<input type="number"  class="form-control pl-2" id="maxLevelToPurchase" name="maxLevelToPurchase" required>
					</div>
					<div class="form-group col-12">
						<label for="validityAfterEndDate">Validity After End Date</label>
						<input type="number"  class="form-control pl-2" id="validityAfterEndDate" name="validityAfterEndDate" required>
					</div>
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
	                <th>Path Name</th>
	                <th>Family Name</th>
	                <th>Level Value</th>
	                <th>Min Level To Purchase</th>
	                <th>Max Level To Purchase</th>
	                <th>Valid For Months After End Date</th>
	                <th>Links</th>
	            </tr>
	        </thead>
	        <tbody>
	        	<% int i = 0; %>
			 	<c:forEach items="${AllFamiliesInUpgradePaths}" var="thisPathFamily">	
				 	<tr>
				 		<% i++; %>
				 		<td> <%= i %></td>
		            	<td>${ thisPathFamily.pathName }</td>
		            	<td><a href="updatePackageFamily?familyId=${ thisPathFamily.packageFamilyId }">${ thisPathFamily.packageFamilyName }</a></td>
		            	<td>${ thisPathFamily.levelValue}</td>
		            	<td>${ thisPathFamily.minLevelToPurchase}</td>
		            	<td>${ thisPathFamily.maxLevelToPurchase}</td>
		            	<td>${ thisPathFamily.validityAfterEndDate}</td>
        			    <td>
        			    	<a href="updateFamilyInUpgradePath?packageFamilyId=${ thisPathFamily.packageFamilyId }&pathId=${ thisPathFamily.pathId }">
        			    		<i class="fa fa-edit"></i> Edit
       			    		</a>
       			    		<br>
       			    		<a onclick="confirmDelete(`Package Family Upgrade Path Relation`, `deleteFamilyInUpgradePath?packageFamilyId=${ thisPathFamily.packageFamilyId }&pathId=${ thisPathFamily.pathId } `);">
       			    			<i class="fa fa-trash"></i> Delete
     			    		</a>
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
		$("#familyName").val("${ Family.familyName }");
		$("#validityAfterEndDate").val("${ UpgradePathFamily.validityAfterEndDate }");
		$("#maxLevelToPurchase").val("${ UpgradePathFamily.maxLevelToPurchase }");
		$("#minLevelToPurchase").val("${ UpgradePathFamily.minLevelToPurchase }");
		$("#levelValue").val("${ UpgradePathFamily.levelValue }");
		
		$( 'form' ).submit(function ( e ) {
		    var ddata = formDataToJSON();
		    e.preventDefault();
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