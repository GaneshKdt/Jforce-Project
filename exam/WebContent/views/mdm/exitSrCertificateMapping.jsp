<!DOCTYPE html>
<html lang="en">
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>


<style>
.panel-title .glyphicon {
	font-size: 14px;
}

.select2-results__option{
   color:black;
}

.column {
	margin-bottom: 20px;
}
</style>





<%@page import="com.nmims.beans.Page"%>


<jsp:include page="../adminCommon/jscss.jsp">
	<jsp:param value="Exit SR Certificate Mapping" name="title" />
</jsp:include>
<body>
	<%@ include file="../adminCommon/header.jsp"%>
	<div class="sz-main-content-wrapper">

		<jsp:include page="../adminCommon/breadcrum.jsp">
			<jsp:param value="Exam;exit Sr Certificate Mapping" name="breadcrumItems" />
		</jsp:include>


		<div class="sz-main-content menu-closed">
			<div class="sz-main-content-inner">
				<jsp:include page="../adminCommon/left-sidebar.jsp">
					<jsp:param value="" name="activeMenu" />
				</jsp:include>
				<div class="sz-content-wrapper examsPage">
					<%@ include file="../adminCommon/adminInfoBar.jsp"%>
					<div class="sz-content">
						<h2 class="red text-capitalize">Add Certificate For Exit Programs </h2>
						<div class="clearfix"></div>
						<div class="panel-content-wrapper" style="min-height: 450px;">
							<%@ include file="../adminCommon/messages.jsp"%>

						
							
							<div class="clearfix"></div>
							<div class="column">
							
								<form:form method="POST" modelAttribute="ProgramsBean">
								<fieldset>
									<div class="panel-body">
										<div class="column">
											<div class="col-sm-3 column">
												<label for="consumerTypeList">Consumer Type</label>
												<form:select path="consumerType" name="consumerType"
													id="consumer" class="form-control" required="required">
													<option disabled selected value="">Select Consumer
														Type</option>
													<c:forEach var="consumerTypeListData"
														items="${consumerTypeListData}">
														<option value="<c:out value="${consumerTypeListData.id}"/>">
															<c:out value="${consumerTypeListData.name}" />
														</option>
													</c:forEach>
											</form:select>
											</div>

											<div class="col-sm-3 column">
												<label for="programStructureList">Program Structure</label>
													<form:select  path="programStructure" name="programStructure"
													id="programStructure" class="form-control" required="required">
													<option disabled selected value="">Select Program Structure</option>
													   
													</form:select>
											</div>

											<div class="col-sm-3 column">
												<label for="programList">Program </label>
					                               <form:select  path="program" name="pName"
													 id="PrgrmName" class="form-control" required="required">
													<option disabled selected value="">Select Program</option>
													
											      </form:select>
											</div>
											
											<div class="clearfix"></div>

                                           <div class="col-sm-3 column">
												<label for="SemList">Sem </label>
					                               <form:select path="sem" name="sem"
													id="Sem" class="form-control" required="required">
													<option disabled selected value="">Select Sem
													</option>
													
											      </form:select>
											</div>

                                            <div class="col-sm-3 column">
												<label for="newMasterkeyList">Program to be Mapped </label>
					                               <form:select path="newConsumerProgramStructureId" name="newMasterkey"
													id="newMasterkey" class="form-control" required="required">
													<option disabled selected value="">Select Program
													</option>
													
											      </form:select>
											</div>

                                          <div class="clearfix"></div>

											<div class="col-md-6 column">

												<button id="submit" name="submit"
													class="btn btn-large btn-primary"
													formaction="exitSrCertificateMapping">Add
													Entry</button>

												<button id="cancel" name="cancel" class="btn btn-danger"
													formaction="${pageContext.request.contextPath}/home" formnovalidate="formnovalidate">Cancel</button>
											</div>

										</div>

									</div>

								</fieldset>
							</form:form>
								
									<div class="clearfix"></div>
									
					 <c:choose>
							<c:when test="${rowCount > 0}">
								<div class="column">
								<legend>
								<span style="margin-left:20px;"><font size="3px"> (${rowCount} Records Found)&nbsp;<a href="downloadSRReport"><font size="4px">Download to Excel</font></a></font></span>
								</legend>
								
								<div class="clearfix"></div>
								<div class="table-responsive">
									<table class="table table-striped table-hover tables"
									style="font-size: 12px; margin-left: 25px;">
										<thead>
											<tr>
												<th>Sr. No.</th>
												<th>Consumer Type</th>
												<th>Program Structure</th>
												<th>Program</th>
												<th>Sem</th>
												<th>New Program Mapped </th>
											
												


											</tr>
										</thead>
										<tbody>
										
									
                                         <c:forEach var="getMappedCertificateData"
												items="${getMappedCertificateData}"
												varStatus="status">
												<tr value="${getMappedCertificateData.id}~${getMappedCertificateData.sem}~${getMappedCertificateData.programname}~${getMappedCertificateData.newConsumerProgramStructureId}">
													<td><c:out value="${status.count}" /></td>
													<td><c:out
															value="${getMappedCertificateData.consumerType}" /></td>
													<td><c:out
															value="${getMappedCertificateData.programStructure}" /></td>
													<td><c:out
															value="${getMappedCertificateData.programname}" /></td>
													
													<td><c:out
															value="${getMappedCertificateData.sem}" /></td>
													
													
													
													
													<td id="newPrgm_structure_map_${getMappedCertificateData.id}">
													<select >
													<option disabled selected value="">${getMappedCertificateData.newPrgm_structure_map}
													</option>
													
													</select>
													</td>
														

												</tr>
											</c:forEach>
											
											
										</tbody>
									</table>
								   </div>
                    			</div>
								 </c:when>
											<c:otherwise>
												
												
												
											</c:otherwise>
										</c:choose>
							
							</div>
						</div>
			         </div>
				</div>
			</div>
		</div>
	</div>
	<jsp:include page="../adminCommon/footer.jsp" />







</body>
<!-- jQuery (necessary for Bootstrap's JavaScript plugins) -->
<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />assets/js/jquery-1.11.3.min.js"></script>
<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />assets/js/bootstrap.js"></script>


<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/js/vendor/jquery-ui.min.js"></script>
<script src="https://cdn.datatables.net/1.10.13/js/jquery.dataTables.min.js"></script>
<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />assets/js/jquery.tabledit.js"></script>

<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/js/vendor/dataTables.bootstrap.js"></script>
<script src="https://cdn.datatables.net/buttons/1.2.4/js/dataTables.buttons.min.js"></script>
<link href="https://cdn.jsdelivr.net/npm/select2@4.0.13/dist/css/select2.min.css" rel="stylesheet" />
<script src="https://cdn.jsdelivr.net/npm/select2@4.0.13/dist/js/select2.min.js"></script>
<script src="${pageContext.request.contextPath}/assets/js/exitSrMdm.js"></script>

<script type="text/javascript">

var id = "";
var str="";
var newMappedProgram;
var userId="<%= (String)request.getSession().getAttribute("userId") %>";

$(".tables").on('click', 'tr', function(e) {
	e.preventDefault();
	 str = $(this).attr('value');
	id = str.split('~');
});

$(document).on('click', '.tabledit-edit-button', function(e){
	id = str.split('~');
		let options = "<option>Loading... </option>";

		var data = {
				sem : id[1],
				
			}
		console.log("id[1]"+id[1])
		
		       $.ajax({
						type : "POST",
						contentType : "application/json",
						url : "getProgramBySem",  
						data : JSON.stringify(data),
						success : function(data) {
							options = "";
	                  			for(var i=0;i<data.length;i++){
				                 	 options = options
									+ "<option  value='"
									+ data[i].id
									+ "'> "
									+ data[i].consumerType +' - '+
      	               				 data[i].programStructure +' - '+
    	               				 data[i].program 
									+ " </option>";
                    				
	                  			}
	                  			$("#newPrgm_structure_map_"+id[0]).children(".tabledit-input").html( options);
              			
						}
					})
                   
	
	});





    $(".tables")
		.Tabledit(
				{
					columns : {
						identifier : [ 0, 'id' ],
						editable : [

								
								 [
										5,
										'newConsumerProgramStructureId',
										'{}' ,]  ]
					},

					// link to server script
					// e.g. 'ajax.php'
					url : "",
					// class for form inputs
					inputClass : 'form-control input-sm',
					// // class for toolbar
					toolbarClass : 'btn-toolbar',
					// class for buttons group
					groupClass : 'btn-group btn-group-sm',
					// class for row when ajax request fails
					dangerClass : 'warning',
					// class for row when save changes
					warningClass : 'warning',
					// class for row when is removed
					mutedClass : 'text-muted',
					// trigger to change for edit mode.
					// e.g. 'dblclick'
					eventType : 'click',
					// change the name of attribute in td
					// element for the row identifier
					rowIdentifier : 'id',
					// activate focus on first input of a
					// row when click in save button
					autoFocus : true,
					// hide the column that has the
					// identifier
					hideIdentifier : false,
					// activate edit button instead of
					// spreadsheet style
					editButton : true,
					// activate delete button
					deleteButton : true,
					// activate save button when click on
					// edit button
					saveButton : true,
					// activate restore button to undo
					// delete action
					restoreButton : false,
					// custom action buttons
					// executed after draw the structure
					onDraw : function() {
						$('.tables').DataTable();
					},

					// onAjax(action, serialize)
					onAjax : function(action, serialize) {
						if(action === 'edit'){
							return false;
						}
						serialize['id'] = id[0];
						serialize['sem'] = id[1];
						let body = JSON
								.stringify(serialize);

						var url = ''
						if (action === 'delete') {
							url = 'deleteSemCertificateExitprogram'
						} 

						$.ajax({
							type : "POST",
							url : url,
							contentType : "application/json",
							data : body,
							dataType : "json",
							success : function(response) 
							{
								
								if(response.status == "Success") {
									alert('Entry Deleted Successfully!')
									window.location.assign('exitSrCertificateMappingForm')
								}
							},
							error : function(e) {
								alert('Entries Failed to delete. please try again!');
							}

						});
					}
				});
               

    $(document).on('click', '.tabledit-save-button', function(e){
    	var value= $(this).parent().parent().parent().attr("value");
    	value = value.split("~");
		       $.ajax({
						type : "POST",
						contentType : "application/json",
						url : "updateSemCertificateExitprogram",  
						data : JSON.stringify({
							id : value[0],
							newConsumerProgramStructureId : $("#newPrgm_structure_map_"+value[0]).children(".tabledit-input").val(),
							sem:id[1],
							"lastModifiedBy":userId,
						}),
						success : function(data) {
							alert('Entry Updated Successfully!')
							window.location
							.assign('exitSrCertificateMappingForm');
	      					
						},
				       error:function(e){
                          alert("Failed To Update .Please try Again!")
					   }
					}) 
                   
	
	});
	 
 

</script>



</html>

