
<!DOCTYPE html>
<html lang="en">

<%@page import="com.nmims.beans.Person"%>
<%@page import="com.nmims.beans.Page"%>
<%@page import="com.nmims.beans.OnlineExamMarksBean"%>
        <%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>


<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<jsp:include page="adminCommon/jscss.jsp">
	<jsp:param value="Search Students" name="title" />
</jsp:include>


<%
	try {
%>
<style>
.navheading{
	color: #d2232a!important;
    font-size: 1.2rem;
    font-family: "Aller";
    font-weight: bold;  
    }
</style>
<body>

	<%@ include file="adminCommon/header.jsp"%>
	<div class="sz-main-content-wrapper">

		<jsp:include page="adminCommon/breadcrum.jsp">
			<jsp:param value="Exam;Assignment Submission Attempts"
				name="breadcrumItems" />
		</jsp:include>


		<div class="sz-main-content menu-closed">
			<div class="sz-main-content-inner">
				<jsp:include page="adminCommon/left-sidebar.jsp">
					<jsp:param value="" name="activeMenu" />
				</jsp:include>


				<div class="sz-content-wrapper examsPage">
					<%@ include file="adminCommon/adminInfoBar.jsp"%>
					<div class="sz-content">
					<br> 
					<c:if test="${activeTab =='2'}" >
					<c:set var = "extTab" scope = "session" value = ""/>
					<c:set var = "urlTab" scope = "session" value = "active"/>
					</c:if>
					<c:if test="${activeTab !='2'}" >
					<c:set var = "extTab" scope = "session" value = "active"/>
					<c:set var = "urlTab" scope = "session" value = ""/>
					</c:if> 
					<%@ include file="adminCommon/messages.jsp"%>
					<ul class="nav nav-tabs nav-justified">  
					   <li class="${extTab}"><a data-toggle="tab" href="#home" class=" navheading">
					    Extended Assignment/Project Submission</a></li>    
					  <li class="${urlTab}"><a data-toggle="tab" href="#menu1" class="navheading">Last Cycle Assg Submission  Link</a></li> 
					</ul>   
					
					<div class="tab-content">
  						<div id="home" class="tab-pane fade in ${extTab}"> 
	    					<div class="clearfix"></div>
							<div class="panel-content-wrapper" style="min-height: 450px;"> 
							
								
	
								<form:form  method="post"
									modelAttribute="bean">
									<fieldset>
									  <br>
										<div class="col-md-4"> 
											<div class="form-group">
												<form:select id="subject" path="subject" type="text"
													placeholder="subject" class="form-control" required="true">
													<form:option value="">Select Subject</form:option>
													<form:options items="${subjectList}" />
												</form:select>
											</div>
	
											<div class="form-group">
												<form:input id="sapId" path="sapId" type="text"
													placeholder="SAP ID" class="form-control" required="true" />
											</div>
											
											<div class="form-group">
												<label class="control-label" for="button"></label>
												<button id="button" name="button"
													class="btn btn-large btn-primary"
													formaction="updateAssignmentSubmissionTime">Save</button>
														<!-- <button id="button" name="button"
													class="btn btn-large btn-primary"
													formaction="deleteExtendedAssignmentSubmission">Delete Entry</button> -->
												<button id="cancel" name="cancel" class="btn btn-danger"
													formaction="extendedAssignmentSubmission" formnovalidate="formnovalidate">Cancel</button>
											</div>
										</div>
									</fieldset>
									
									<c:if test="${rowCount > 0}">
										<h2>
											&nbsp;Extended Submission Students
										</h2>
										<div class="clearfix"></div>
										
								<%-- 	<%if(roles.indexOf("Acads Admin") != -1 || roles.indexOf("Student Support") != -1){%> --%>	
										<div class="table-responsive">
											<table id="myTable" class="table table-striped table-hover tables" style="font-size: 12px ; margin-left: 25px;">
												<thead>
													<tr>
														<!-- <th>Sr No.</th> -->
														<th>SapId</th>
														<th>Subject</th>
														<th>Created By</th>
														<th>Created Date</th>
														<th style="width: 80px;"> <input class="checkAll" type="checkbox" id="selectAll" style="width: 15px; height: 30px;"/>&nbsp;All</th>
													</tr>
												</thead>
	
												<tbody>
													<c:forEach var="bean"
														items="${timeExtendedStudentIdSubjectList}" varStatus="status">
														<tr value="${bean.id}~${bean.sapId}~${bean.subject}">
															<%-- <td><c:out value="${bean.id}"></c:out></td> --%>
															<td><c:out value="${bean.sapId}"></c:out></td>
															<td><c:out value="${bean.subject}"></c:out></td>
															<td><c:out value="${bean.createdBy}"></c:out></td>
															<td><c:out value="${bean.createdDate}"></c:out></td>
														     <td><input class="checkBox" type="checkbox" onclick="setCheckAll()" path="id" value="${bean.id}~${bean.sapId}~${bean.subject}" style="width: 15px; height: 30px;"/></td>
														</tr>
													</c:forEach>
												</tbody>
	
											</table>
											
                                         
										</div>
								<%-- 		<%} %> --%>
									</c:if>
								</form:form> 
								
							</div>
							
							<div class="menu pmd-floating-action" role="navigation"
	style="z-index: 9;">
	<button  id="delete2" class="btn btn-large btn-primary">Delete </button> 
</div>
							
							
  						</div>
  						
  						
  						
	  					<div id="menu1" class="tab-pane fade in ${urlTab} "> 
	    					<div class="panel-content-wrapper" style="min-height: 450px;"> 
	    						<form:form  method="post"
									modelAttribute="bean">
									<fieldset>
									  <br>
										<div class="col-md-4">  
	
											<div class="form-group">
												<form:input id="sapId" path="sapId" type="text"
													placeholder="SAP ID" class="form-control" required="true"/>
											</div>
											
											<div class="form-group">
												<form:select id="year" path="year" type="text"	placeholder="Year" class="form-control"   required="true" >
													<form:option value="">Select Year</form:option>
													<form:options items="${yearList}" />
												</form:select>
											</div>
											<div class="form-group">
												<form:select id="month" path="month" type="text" placeholder="Month" class="form-control" required="true" >
													<form:option value="">Select Month</form:option>
													<form:option value="Jan">Jan</form:option>
													<form:option value="Apr">Apr</form:option>
													<form:option value="Jun">Jun</form:option>
													<form:option value="Sep">Sep</form:option>
													<form:option value="Dec">Dec</form:option>
												</form:select>
											</div>
											<div class="form-group">
												<form:select id="subject" path="subject" type="text"
													placeholder="subject" class="form-control" required="true">
													<form:option value="">Select Subject</form:option>
													<form:options items="${subjectList}" />
												</form:select>
											</div>
											<div class="form-group">
												<label class="control-label" for="button"></label>
												<button id="button" name="button"
													class="btn btn-large btn-primary"
													formaction="generateLastCycleAssgSubmissionLink">Generate</button>
											</div>
										</div> 
									</fieldset>
									 <div class="col-md-9 "> 
										<a>${generatedUrl}</a>
									 </div>
								</form:form>
	    					</div>
	  					</div>	 
						
					</div>
				</div>
			</div>
		</div>
	</div>
	
	<jsp:include page="adminCommon/footer.jsp" />
	


<!-- Code for Ask Faculty Fab Button  :end-->

<!-- Code for Ask Faculty Fab Button Model :start-->






<div id="deleteConfirm" class="modal fade" role="dialog">
	<div class="modal-dialog">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal">&times;</button>
				<h3 class="modal-title selected" style="color:red"></h3>
			</div>
			<div class="modal-body">
				<div class="panel-content-wrapper">
                            	<div class="table-responsive">
											<table id="myTable1" class="table table-striped table-hover myTable1" style="font-size: 12px ; margin-left: 25px;">
												<thead>
													<tr>
														<th>SapId</th>
														<th>Subject</th>
													</tr>
												</thead>
												<tbody class="">													
														<tr>
															<%-- <td><c:out value="${bean.id}"></c:out></td> --%>
															<td class="sapid"></td>
															<td class="subject"></td>
														</tr>												
												</tbody>
	                                		</table>
									    </div>
							<div class="clearfix"></div>
			    
						<div class ="text-center" ><button id="DeleteButton12"  type="button" class="btn  btn-primary" >Delete</button></div>
				</div>
			</div>
			
		</div>
	</div>

	
</div>
</div>


<!--  Code for Ask Faculty FAb Button Model :end-->

	<%
		} catch (Exception e) {
		}
	%>
	
	<!-- jQuery (necessary for Bootstrap's JavaScript plugins) -->
	<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />assets/js/jquery-1.11.3.min.js"></script>
	<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />assets/js/bootstrap.js"></script>
	<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />assets/js/jquery.tabledit.js"></script>

	<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/js/vendor/jquery-ui.min.js"></script>
	<script src="https://cdn.datatables.net/1.10.13/js/jquery.dataTables.min.js"></script>
	<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/js/vendor/dataTables.bootstrap.js"></script>
	<script src="https://cdn.datatables.net/buttons/1.2.4/js/dataTables.buttons.min.js"></script>
	<script src="https://datatables.net/plug-ins/api/fnReloadAjax"></script>
	
	<script>
	$(document).ready( function () {
		let id = "";
	   
	     $(".tables").on('click','tr',function(e){
		    //e.preventDefault();	
		    var str = $(this).attr('value');
		     id = str.split('~');
		    console.log(id);
		}); 
	    
	    $('.tables').Tabledit({
	    	columns: {
				  identifier: [0, 'id'],                 
				  editable:[]
				},
				// link to server script
				// e.g. 'ajax.php'
				url: "",
				// class for form inputs
				inputClass: 'form-control input-sm',
				// // class for toolbar
				toolbarClass: 'btn-toolbar',
				// class for buttons group
				groupClass: 'btn-group btn-group-sm',
				// class for row when ajax request fails
				 dangerClass: 'warning',
				// class for row when save changes
				warningClass: 'warning',
				// class for row when is removed
				mutedClass: 'text-muted',
				// trigger to change for edit mode.
				// e.g. 'dblclick'
				eventType: 'click',
				// change the name of attribute in td element for the row identifier
				rowIdentifier: 'id',
				// activate focus on first input of a row when click in save button
				autoFocus: true,
				// hide the column that has the identifier
				hideIdentifier: false,
				// activate edit button instead of spreadsheet style
				editButton: false,
				// activate delete button
				deleteButton: false,
				// activate save button when click on edit button
				saveButton: true,
				// activate restore button to undo delete action
				restoreButton: true,
				onDraw: function() { 
					$('.tables').DataTable(); 
				},
				onAjax: function(action, serialize) {
				
						serialize['id'] = id[0];
						serialize['sapId'] = id[1];
						serialize['subject'] = id[2];
						let body = JSON.stringify(serialize);
						$.ajax({
							type : "POST",
							url : 'deleteExtendedAssignmentSubmissionNew',
							contentType : "application/json",
							data : body,
							dataType : "json",
							success : function(response) {
								console.log(response)

								if (response.status == "Success") {
									//alert('Entries Deleted Successfully')
									//location.reload();
									window.location="/exam/admin/extendedAssignmentSubmission";
								} else {
									console.log('Entries Failed to update : ' + response.message)
								}

							}
						});
				}
				
			});
	    
		});
	</script>
	
	<script type="text/javascript">
		// for select all	
		$('#selectAll').click(function (e) {
		    $(this).closest('table').find('td input:checkbox').prop('checked', this.checked);
		});
	
		$('.checkBox').click(function (e) {
			console.log("checkBox clicked");
			var ischecked= $(this).is(':checked');
			console.log(ischecked);
			if(!ischecked){
				$("#selectAll").prop("checked", false);
			}
		   
		});
	
		function setCheckAll() {
		  document.querySelector('input.checkAll').checked =
		     document.querySelectorAll('.checkBox').length ==
		     document.querySelectorAll('.checkBox:checked').length;
		}


		 //first delete button for view modal confirmation started here 

	 $('#delete2').click(function() {
		 let options = "<option>Loading... </option>";
		 var ids=[];
         var sapid=[];
         var subject=[];
         $("input:checkbox:checked").each(function(i){

        	 var str = $(this).val();
        	var id= str.split('~');
             
		        if( id[0]!= 'on'){
		        	ids[i]=id[0];
		        	sapid[i]=id[1];
		        	subject[i]=id[2];
			        }
	              });

         
	    
	    		 var checkboxes = $('input:checkbox:checked').length;
	    		 var l=1;
	    		  if($("#selectAll").prop("checked", false)){
            		 l=0;
	    	    	}  	
	    		  var checkboxes = $('input:checkbox:checked').length-l;
        

         var selected=checkboxes;

         $('.selected').html(options);
         options = ""; 

         if(selected>0){
         options = options + "<h3 style='color:#F31B28  !important;' value='" +selected + "'>Are you sure really want to delete selected <b>(  " + selected+ " )</b> entries?  </h3>";
         }
         else{
        	 options = options + "<h3 style='color:#F31B28  !important;'>You Did Not Selected Any Entries Please Select To Delete! </h3>";
             }
         $('.selected').html(
					" <h3 disabled selected value=''> </h3> " + options
			);
      
   
         $('.sapid').html(options);
         options = ""; 
			//Data Insert For Subjects List
			//Start
			for(let i=0;i < sapid.length;i++){
				if(sapid[i]!=null){
				options = options + "<option value='" + sapid[i] + "'>  " + sapid[i]+ " </option>";}
			}
			//console.log("==========> options\n" + options);
			$('.sapid').html(
					" <option disabled selected value=''> </option> " + options
			);

		 $('.subject').html(options);
	         options = ""; 
				//Data Insert For Subjects List
				//Start
				for(let i=0;i < sapid.length;i++){
					if(sapid[i]!=null){
					options = options + "<option value='" + subject[i] + "'> " + subject[i]+ " </option>";}
				}
				//console.log("==========> options\n" + options);
				$('.subject').html(
						" <option disabled selected value=''> </option> " + options
				);

		 
       $('#deleteConfirm').modal('show');
		   	
		   	              });
	    //first delete button for view modal confirmation ended here
	
	
	
	 //Second delete button for confirm Delete Started here
	$('#DeleteButton12').click(function() {
 var l=0
             var ids=[];
             var sapid=[];
             var subject=[];
      $("input:checkbox:checked").each(function(i){

            	 var str = $(this).val();
            	var id= str.split('~');
	             
   		        if( id[0]!= 'on'){
   		        	ids[i]=id[0];
   		        	sapid[i]=id[1];
   		        	subject[i]=id[2];
   			        }
   	              });    
         
             $.ajax({
   	    	   type:"POST",
   	    	   dataType:"json",
   	    	   contentType:"application/json",
   	    	   url:"deleteExtendedAssignmentSubmissionNewly",
   	    	   data:JSON.stringify({ids:ids}),
   	    	  success:function(response){
	   	    	  console.log(response.status)
	   	    	  if(response.status=='Success'){
                   	 
   	    		   window.location="/exam/admin/extendedAssignmentSubmission" ;
   	    		    /*  alert(
   	    	    		     'Deleted Successfully...'+response.code+'  Entries');*/
   		    	   } 
   	    	  else{
             		console.log('Entries Failed to delete : ' + response.message)
	   	    	  }
	   	    	  }
   		       });

		
	});

	 //Second delete button for confirm Delete Ended here
	</script>
	
</body>
</html>