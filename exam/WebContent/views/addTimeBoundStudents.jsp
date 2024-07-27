<%-- <<<<<<< HEAD
<html class="no-js"> <!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<jsp:include page="jscss.jsp">
<jsp:param value="Add TimeBound Students" name="title" />
</jsp:include>

<style>
.column .dataTables .btn-group {
	width: auto;
}
</style>

<body class="inside">

<%@ include file="header.jsp"%>
	
    <section class="content-container login">
        <div class="container-fluid customTheme">
       	<div class="row"> <legend>Add TimeBound Students</legend></div>
        <%@ include file="messages.jsp"%>
        <%@ include file="../views/uploadTimeBoundSapidErrorMessage.jsp" %>
        	<form:form modelAttribute="fileBean" method="post" 	enctype="multipart/form-data" action="addTimeBoundStudents">
				<div class="panel-body">
					<form:hidden path="id" value="${TimeBoundSubjectConfigId}" />
					<form:hidden path="prgm_sem_subj_id" value="${prgm_sem_subj_id}"/>
					<div class="col-md-6 column">
						<div class="form-group">
							<form:label for="fileData" path="fileData">Select file</form:label>
							<form:input path="fileData" type="file" />
						</div>
						
						<div class="form-group">
							<form:select id="acadYear" path="acadYear" class="form-control" required="required"  itemValue="${fileBean.acadYear}">
								<form:option value="">Select Academic Year</form:option>
								<form:options items="${yearList}" />
							</form:select>
						</div>
					
						<div class="form-group">
							<form:select id="acadMonth" path="acadMonth"  class="form-control" required="required" itemValue="${fileBean.acadMonth}">
								<form:option value="">Select Academic Month</form:option>
								<form:options items="${monthList}" />
							</form:select>
						</div>
						
						<div class="form-group">
							<button id="submit" name="submit" class="btn btn-large btn-primary" formaction="addTimeBoundStudents">Upload</button>
							<button id="cancel" name="cancel" class="btn btn-danger" formaction="home" formnovalidate="formnovalidate">Cancel</button>
        				</div>
        				
        			</div>
        			
					
        			<div class="col-md-12 column">
						<b>Format of Upload: </b><br>
						Sapid | Student Type<br>
						<a href="${pageContext.request.contextPath}/resources_2015/templates/Upload_TimeBound_Student_Sapid.xlsx" target="_blank">Download a Sample Template</a>
						<br><br>
						
						<h2>&nbsp;Existing Students (${existingStudentList.size()})</h2>
						<c:if test="${existingStudentList.size() > 0 }">
							<div class="panel-body">
								<div class="table-responsive">
									<table class="table table-striped table-hover dataTables" style="font-size:12px">
										<thead>
										<tr>
											<th>Sr.No</th>
											<th>Sapid</th>
											<th>TimeBound Id</th>
											<th>Role</th>
											<th>Action</th>
										</tr>
										</thead>
										<tbody>
											<c:forEach var="student" items="${existingStudentList }" varStatus="status">
												<tr value="${student.id}~${student.userId}">
													<td><c:out value="${status.count}" /></td>
													<td><c:out value="${student.userId}" /></td>
													<td><c:out value="${student.timebound_subject_config_id}" /></td>
													<td><c:out value="${student.role}" /></td>
													<td></td>
												</tr>
											</c:forEach>
										</tbody>
									</table>
								</div>
							</div>
						</c:if>
						
					</div>
			
        		</div>
        		<br>
			</form:form>
			
        </div>	
  	</section>
  	
  	<jsp:include page="footer.jsp" />
  	
	<script src="${pageContext.request.contextPath}/assets/js/jquery-1.11.3.min.js"></script>
	<script src="${pageContext.request.contextPath}/assets/js/bootstrap.js"></script>
	<script src="${pageContext.request.contextPath}/assets/js/jquery.tabledit.js"></script>

	<script src="${pageContext.request.contextPath}/resources_2015/js/vendor/jquery-ui.min.js"></script>
	<script src="https://cdn.datatables.net/1.10.13/js/jquery.dataTables.min.js"></script>
	<script src="${pageContext.request.contextPath}/resources_2015/js/vendor/dataTables.bootstrap.js"></script>
	<script src="https://cdn.datatables.net/buttons/1.2.4/js/dataTables.buttons.min.js"></script>
	
<script>
	$(document).ready( function () {					
		let id = "";
	   
	     $(".dataTables").on('click','tr',function(e){
// 		    e.preventDefault();	
		    var str = $(this).attr('value');
		     id = str.split('~');
		    console.log(id);
		}); 
	    
	    $('.dataTables').Tabledit({
	    	columns: {
				  identifier: [0, 'id'],                 
				  editable: [
					  			[2, 'userId']
				  			]
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
				deleteButton: true,
				// activate save button when click on edit button
				saveButton: true,
				// activate restore button to undo delete action
				restoreButton: true,
				onDraw: function() { 
					$('.dataTables').DataTable(); 
				},
				onAjax: function(action, serialize) {
					
					serialize['id'] = id[0];
					let body = JSON.stringify(serialize);
					
					var url = ''
					if(action === 'delete'){
						url = 'deleteStudentTimeBoundMapping'
					}else if (action === 'edit'){
						url = ''
					}

					$.ajax({
						type : "POST",
						url : url,
						contentType : "application/json",
						data : body,
						dataType : "json",
						success : function(response) {
							
							if (response.Status == "Success") {
								if(action === 'delete'){
									if(!alert('Entry Deleted Successfully')){window.location.reload();}
								}else if (action === 'edit'){
									alert('Entries Saved Successfully')
								}
							} else {
								alert('Entries Failed to update : ' + response.message)
							}

						}
					});
				}
			});
		    $(".tabledit-toolbar").attr("style","text-align:center;margin-left:-80px;");
		    $(".dataTables_filter").attr("style","display:inline-flex;justify-content:space-between;");
		         
		});		
</script>
	
	
	
</body>
======= --%>
<html class="no-js">
<!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<%
	Person person = (Person) request.getSession().getAttribute("user");
%>



<jsp:include page="jscss.jsp">
	<jsp:param value="Add TimeBound Students" name="title" />
</jsp:include>

<style>
.column .dataTables .btn-group {
	width: auto;
	
}

.btn-toolbar {
	margin-left: -194px !important;
	text-align: center;
}

@media only screen and (max-width: 1440px) {
	.btn-toolbar {
		margin-left: -132px !important;
	}
}

@media only screen and (max-width: 1024px) {
	.btn-toolbar {
		margin-left: -92px !important;
	}
}

@media only screen and (max-width: 430px) {
	.btn-toolbar {
		margin-left: -73px !important;
	}
}
</style>



<body class="inside">

	<%@ include file="header.jsp"%>

	<section class="content-container login">
		<div class="container-fluid customTheme">
			<div class="row">
				<legend>Add TimeBound Students</legend>
			</div>
			<%@ include file="messages.jsp"%>
			<%@ include file="../views/uploadTimeBoundSapidErrorMessage.jsp"%>
			<form:form modelAttribute="fileBean" method="post"
				enctype="multipart/form-data" action="addTimeBoundStudents">
				<div class="panel-body">
					<form:hidden path="id" value="${TimeBoundSubjectConfigId}" />
					<form:hidden path="prgm_sem_subj_id" value="${prgm_sem_subj_id}" />
					<div class="col-md-6 column">
						<div class="form-group">
							<form:label for="fileData" path="fileData">Select file</form:label>
							<form:input path="fileData" type="file" required="required" />
						</div>

						<div class="form-group">
							<form:select id="acadYear" path="acadYear" class="form-control"
								required="required" itemValue="${fileBean.acadYear}">
								<form:option value="">Select Academic Year</form:option>
								<form:options items="${yearList}" />
							</form:select>
						</div>

						<div class="form-group">
							<form:select id="acadMonth" path="acadMonth" class="form-control"
								required="required" itemValue="${fileBean.acadMonth}">
								<form:option value="">Select Academic Month</form:option>
								<form:options items="${monthList}" />
							</form:select>
						</div>

						<div class="form-group">
							<button id="submit" name="submit"
								class="btn btn-large btn-primary"
								formaction="addTimeBoundStudents">Upload</button>
							<button id="cancel" name="cancel" class="btn btn-danger"
								formaction="/studentportal/home" formnovalidate="formnovalidate">Cancel</button>
						</div>

					</div>


					<div class="col-md-12 column">
						<b>Format of Upload: </b><br> Sapid | Student Type<br> <a
							href="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/templates/Upload_TimeBound_Student_Sapid.xlsx"
							target="_blank">Download a Sample Template</a> <br>
						<br>




						<h2>
							&nbsp;Existing Students (${existingStudentList.size()})&nbsp;
							<c:if test="${existingStudentList.size() > 0 }">

								<%
									if (person.getRoles().indexOf("Acads Admin") != -1 && person.getRoles().indexOf("MBA-WX Admin") != -1) {
								%>
								<a href="downloadStudentTimeBoundReport" class="h6"><b>Download
										Student Mapped Report</b></a>
								<%
									}
								%>
							</c:if>
						</h2>




						<c:if test="${existingStudentList.size() > 0 }">
							<div class="panel-body">
								<div class="table-responsive">



									<table class="table table-striped table-hover dataTables"
										style="font-size: 12px">
										<thead>
											<tr>
												<th>Sr.No</th>
												<th>Sapid</th>
												<th>TimeBound Id</th>
												<th>Role</th>
												<th>Action</th>
											</tr>
										</thead>
										<tbody>
											<c:forEach var="student" items="${existingStudentList}"
												varStatus="status">
												<tr value="${student.id}~${student.userId}">
													<td><c:out value="${status.count}" /></td>
													<td><c:out value="${student.userId}" /></td>
													<td><c:out value="${student.timebound_subject_config_id}" /></td>
													<td><c:out value="${student.role}" /></td>
													<td style="text-align: center;"></td>
												</tr>
											</c:forEach>
										</tbody>
									</table>
								</div>
							</div>
						</c:if>

					</div>

				</div>
				<br>
			</form:form>

		</div>
	</section>

	<jsp:include page="footer.jsp" />

	<script
		src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />assets/js/jquery-1.11.3.min.js"></script>
	<script
		src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />assets/js/bootstrap.js"></script>
	<script
		src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />assets/js/jquery.tabledit.js"></script>

	<script
		src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/js/vendor/jquery-ui.min.js"></script>
	<script
		src="https://cdn.datatables.net/1.10.13/js/jquery.dataTables.min.js"></script>
	<script
		src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/js/vendor/dataTables.bootstrap.js"></script>
	<script
		src="https://cdn.datatables.net/buttons/1.2.4/js/dataTables.buttons.min.js"></script>

	<script>
		$(document)
				.ready(
						function() {
							let id = "";

							$(".dataTables").on('click', 'tr', function(e) {
								// 		    e.preventDefault();	
								var str = $(this).attr('value');
								id = str.split('~');
								console.log(id);
							});

							$('.dataTables')
									.Tabledit(
											{
												columns : {
													identifier : [ 0, 'id' ],
													editable : [
													//[2, 'userId']
													]
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
												// change the name of attribute in td element for the row identifier
												rowIdentifier : 'id',
												// activate focus on first input of a row when click in save button
												autoFocus : true,
												// hide the column that has the identifier
												hideIdentifier : false,
												// activate edit button instead of spreadsheet style
												editButton : false,
												// activate delete button
												deleteButton : true,
												// activate save button when click on edit button
												saveButton : true,
												// activate restore button to undo delete action
												restoreButton : true,
												onDraw : function() {
													$('.dataTables')
															.DataTable();
												},
												onAjax : function(action,
														serialize) {

													serialize['id'] = id[0];
													let body = JSON
															.stringify(serialize);

													var url = ''
													if (action === 'delete') {
														url = 'deleteStudentTimeBoundMapping'
													} else if (action === 'edit') {
														url = ''
													}

													$
															.ajax({
																type : "POST",
																url : url,
																contentType : "application/json",
																data : body,
																dataType : "json",
																success : function(
																		response) {

																	if (response.Status == "Success") {
																		if (action === 'delete') {
																			if (!alert('Entry Deleted Successfully')) {
																				window.location
																						.reload();
																			}
																		} else if (action === 'edit') {
																			alert('Entries Saved Successfully')
																		}
																	} else {
																		alert('Entries Failed to update : '
																				+ response.message)
																	}

																}
															});
												}
											});
							/*  $(".tabledit-toolbar").attr("style","text-align:center;margin-left:-80px;");  */
							$(".dataTables_filter")
									.attr("style",
											"display:inline-flex;justify-content:space-between;");
							/* 	    $(".dataTables_filter").html("<span style='color: #333333;font-weight: 600;margin-top: 0.6rem;margin-right: 2rem;'>Search:</span><input type='search' class='form-control input-sm' placeholder='' aria-controls='DataTables_Table_0'>");
							 */
						});
	</script>



</body>

</html>