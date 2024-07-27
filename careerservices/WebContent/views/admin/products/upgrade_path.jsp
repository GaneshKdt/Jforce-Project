<!DOCTYPE html>
<html class="no-js">
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<jsp:include page="/views/adminCommon/jscss.jsp">
	<jsp:param value="Add Families " name="title" />
</jsp:include>


<body class="inside">

<%@ include file="/views/adminCommon/header.jsp"%>
	<%
	boolean isEdit = "true".equals((String)request.getAttribute("edit"));
	%>
    <section class="content-container login">
        
        <div class="container-fluid customTheme">
       		<div class="row"> <legend> ${ title } </legend></div>
        	<%@ include file="/views/common/messages.jsp"%>
	       	<form style="padding:15px" class="form px-3" onsubmit="return false;">
				<input type="text" readonly hidden="hidden"  name="pathId" value="${ UpgradePath.pathId }">
				<div class="clearfix"></div>
				<div class="col-12">				
					<div class="form-group col-12">
						<label for="familyName">Path Name</label>
						<input type="text"  class="form-control pl-2" id="pathName" name="pathName">
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
	                <th>Path Id</th>
	                <th>Path Name</th>
	                <th>Total Families in this Path</th>
	                <th>Total Families in this Path</th>
	            </tr>
	        </thead>
	        <tbody>
	        <% int i = 0; %>
			 	<c:forEach items="${AllUpgradePaths}" var="Path">	
				 	<tr>
				 		<% i++; %>
				 		<td> <%= i %></td>
		            	<td>${ Path.pathId }</td>
		            	<td><a href="updatePackageFamily?pathId=${ Path.pathId }">${ Path.pathName }</a></td>
		            	<td><a href="addToUpgradePath?pathId=${ Path.pathId }">${ Path.numberOfFamilies}</a></td>
        			    <td>
        			    	<a href="addToUpgradePath?pathId=${ Path.pathId }">
        			    		<i class="fa fa-plus"></i> 
        			    		Add Families
        			    		
       			    		</a> <br>
        			    	<a href="updateUpgradePath?pathId=${ Path.pathId }">
        			    		<i class="fa fa-edit"></i> 
        			    		Edit
       			    		</a> <br>
       			    		<a onclick="confirmDelete(`path`, `deleteUpgradePath?pathId=${ Path.pathId } `);">
       			    			<i class="fa fa-trash"></i> 
       			    			Delete
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
		$("#pathName").val("${ UpgradePath.pathName }");
		
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