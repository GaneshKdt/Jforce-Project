<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!-->


<html class="no-js">
<!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<jsp:include page="jscss.jsp">
	<jsp:param value="Upload Faculty Course Mapping" name="title" />
</jsp:include>

<body class="inside">

	<%@ include file="header.jsp"%>

	<section class="content-container login">
		<div class="container-fluid customTheme">

			<div class="row"><legend>Upload Faculty Course Mapping</legend></div>
			
				<%@ include file="messages.jsp"%>
				<%@ include file="uploadFacultyCourseErrorMessages.jsp"%>
				
				<form:form modelAttribute="facultyCourse" method="post" 	enctype="multipart/form-data" action="uploadFacultyCourseMapping">
					<div class="panel-body">
					
					<div class="col-md-6 column">
					
						<div class="form-group">
							<form:label for="fileData" path="fileData">Select file</form:label>
							<form:input path="fileData" type="file" required="required"/>
						</div>
						
						<div class="form-group">
							<form:select id="year" path="year"  cssClass="form-control" required="required"  itemValue="${facultyCourse.year}">
								<form:option value="">Select Academic Year</form:option>
								<form:options items="${yearList}" />
							</form:select>
						</div>
					
						<div class="form-group">
							<form:select id="month" path="month"  cssClass="form-control" required="required" itemValue="${facultyCourse.month}">
								<form:option value="">Select Academic Month</form:option>
								<form:option value="Jan">Jan</form:option>
								<form:option value="Jul">Jul</form:option>
							</form:select>
						</div>
				<div class="col-md-6 column">
					<button id="submit" name="submit" class="btn btn-large btn-primary"	formaction="uploadFacultyCourseMapping">Upload</button>
				</div>
					</div>
			
					<div class="col-md-12 column">
						<b>Format of Upload: </b><br>
						Faculty ID	| Subject | SubjectCode <br>
						<a href="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_ACADS_STATIC_RESOURCES')" />resources_2015/templates/Faculty&Subject_mapping.xlsx" target="_blank">Download a Sample Template</a>
					</div>
				
			</div>
			<br>
				<div class="panel-body">
			<div class="row">
			<c:if test="${row_count > 0}">
									         <div class="column">
												<div class="table-responsive">
													<table class="table table-striped dataTables" style="font-size:8px">
													<thead>
														<tr>
															<th>Sr.No</th>
															<th>Acad Year</th>
															<th>Acad Month</th>
															<th>FacultyId</th>
															<th>Faculty Name</th>
															<th>Subject</th>
															<th>SubjectCode</th>
              											    <th>Delete</th>
														</tr>
													</thead>
							
													<tbody>
														<c:forEach var="bean" items="${faculty_List }" varStatus="status">
														<tr>
															<td><c:out value="${status.count}" /></td>
															<td><c:out value="${bean.year }" /></td>
															<td><c:out value="${bean.month}" /></td>
															<td><c:out value="${bean.facultyId }" /></td>
															<c:choose>
																<c:when test="${bean.fullName  != '' }">
																	<td><c:out value="${bean.fullName }" /></td>
																</c:when>
																<c:otherwise>
																	<td>-</td>
																</c:otherwise>
															</c:choose>
															<td><c:out value="${bean.subject }" /></td>
															<td><c:out value="${bean.subjectcode }" /></td>
															 <td><button type='button' style="font-size:20px; " class="fa fa-trash-o btnDelete" value="${bean.year}~${bean.month}~${bean.facultyId}~${bean.subjectcode}" ></button>
															 </td>
														</tr>
														</c:forEach>
													</tbody>
											</table>
										</div> 
									</div>
						
								</c:if>
				

				
			</div>
			</div>
				
			</form:form>
		</div>
	</section>
<jsp:include page="footer.jsp" />
	  
	 <script>
			$(document).ready(function() {
				let id = "";
				    $('.dataTables').DataTable( {
				        "pagingType": "full_numbers",
				    } );
	
		     $('table').on('click','.btnDelete',function(e){
			    if(confirm(" Are you sure want to delete this entry? ")){
			    
		    	var $tr = $(this).closest('tr');	
			    id = $(this).attr('value').split('~');
			   	var serialize = {}
			    serialize['year'] = id[0];
				serialize['month'] = id[1];
				serialize['facultyId'] = id[2];
				serialize['subjectcode'] = id[3];
				
				let body = JSON.stringify(serialize);
			   $.ajax({
					type : "POST",
					url : "deleteFacultyCourse",
					contentType : "application/json",
					data : body,
					dataType : "json",
					success : function(response) {
						if (response == 1) {
							 alert('Entries Deleted Successfully');
							 $tr.find('td').remove();	
							 window.location = '/acads/admin/uploadFacultyCourseForm';
						}else {
							alert('Entries Failed to update : ' + response.message)
						}
					}
				});
			    }
			});  
		     
			});
	</script> 
</body>
</html>
