<!DOCTYPE html>
<html lang="en">

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<jsp:include page="jscss.jsp">

	<jsp:param value="Custom Course Waiver" name="title" />
</jsp:include>
<head>

</head>
<title>Custom Course Waiver</title>
<body>

	<%@ include file="header.jsp"%>
	<section class="content-container">
		<div class="container-fluid customTheme">

			<div class="row">
				<legend>Custom Course Waiver </legend>
			</div>

			<%@ include file="messages.jsp"%>
			<div class="alert" id="message-box" hidden="true">
				<button type="button" id= "closebtn" class="close">&times;</button>
				<div id="message"></div>
			</div>


			<div class="panel-body">
			<div class="row">
			<div class="col-md-6 col-lg-9 column">
				<form:form method="post" modelAttribute="bean" action="getApplicableSubjectForCustomCourse"
					>

					
						
							<div class="form-group">
								<input type="number" placeholder="Please Enter Sapid"
									name="sapid" required/>
							</div>
							<div class="form-group">
								<button type="submit" class="btn btn-large btn-primary"
									>Get
									Applicable Subject</button>
							</div>
						
					
					
				</form:form>
</div>
	<div class="col-md-6 col-lg-9 column">
				<form:form modelAttribute="fileBean" method="post" action="uploadSubject"
					enctype="multipart/form-data">
					<div class="row">
						<div class="col-md-12 column">

							<div class="form-group">
								<form:label for="fileData" path="fileData">Select file</form:label>
								<form:input path="fileData" type="file" required = "required"/>
								<label>Excel template should be - Sapid,Subject,Course Waiver,Sem
							</label>
								<label>where Course Waiver will contain - WaivedIn or WaivedOff</label>
							</div>

							<div class="form-group">
								<button id="submit" name="submit" type = "submit"
									class="btn btn-large btn-primary" >Upload</button>
							</div>
						</div>



					</div>

				</form:form>
				</div>
</div>

				<c:if test="${applicableSubject.size()>0 }">
					<div class="panel-body">
						<div class="row">
							<div class="table-responsive">
								<table id="course" class="table table-striped"
									style="font-size: 10px">
									<thead style="padding: 1px">
										<tr>
											<th>Applicable Subjects</th>
											<th>Current Sem</th>
											<th>Perform Waived</th>
											<th>Sem</th>
											<th>Action</th>
										</tr>
									</thead>
									<tbody style="padding: 3px">
					
								
							
										<c:forEach items="${applicableSubject}" var="subject">
											<tr>
												<td value="${subject.pssId}"><c:out
														value="${subject.subjectName}"></c:out></td>
										
												<td value="${subject.currentSem}"><c:out
														value="${subject.currentSem}"></c:out></td>
														<td hidden="true"><input type="hidden"  id = "registerSem"
									value = "${currentRegistrationSem }" /></td>
												<td><select class="courseWaiver" id= "courseWaiverSelect">
														<c:choose>
															<c:when test="${subject.id == 1 }">
																<option value="WaivedIN">WaivedIn</option>
																<option value="WaivedOff">WaivedOff</option>
																<option value="none">Please Select</option>
															</c:when>

															<c:when test="${subject.id == 2}">
																<option value="WaivedOff">WaivedOff</option>
																<option value="WaivedIN">WaivedIn</option>
																<option value="none">Please Select</option>
															</c:when>
															<c:otherwise>
																<option value="none">Please Select</option>
																<option value="WaivedOff">WaivedOff</option>
																<option value="WaivedIN">WaivedIn</option>
															</c:otherwise>

														</c:choose>
												</select></td>
												<td><c:choose>
														<c:when test="${subject.id == 1 }">
															<select id="waivedSem">
																<option value="${subject.sem }">${subject.sem }</option>
																<c:forEach items="${totalSems}" var="sems">
																	<option value="${sems}">${sems }</option>
																</c:forEach>
															</select>
														</c:when>
														<c:otherwise>
															<select disabled="true" id="waivedSem">
																<c:forEach items="${totalSems}" var="sems">
																	<option value="${sems}">${sems }</option>
																</c:forEach>
															</select>
														</c:otherwise>
													</c:choose></td>

												<td><button type="button" class="btn btn-primary">Submit</button></td>
											</tr>
										</c:forEach>
									</tbody>
								</table>
							</div>
						</div>
					</div>
				</c:if>
			</div>
		</div>

	</section>
	<jsp:include page="footer.jsp" />

</body>
	<script src="https://cdn.datatables.net/1.10.19/js/jquery.dataTables.min.js"></script>
<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>

<script type="text/javascript">
$(document).ready(function() {

	 $('#closebtn').on('click', function() {
		    $('#message-box').hide();
		  });
	
	$('.courseWaiver').on('change',function(){
			var row = $(this).closest('tr');
				
		let waived = row.find('.courseWaiver').val();
			if (waived == 'WaivedIN') {
				row.find('#waivedSem').prop("disabled", false);
			} else {
				row.find('#waivedSem').prop("disabled", true);
			}
		});
		$('button[type="button"]').on('click', function() {
			
			var row = $(this).closest('tr'); // Get the closest <tr> element
			var applicableSubject = row.find('td:first-child').attr('value'); // Get the value from the first <td> element
			var currentSem =  row.find('td:nth-child(2)').attr('value');
			var courseWaiver = row.find('.courseWaiver').val(); // Get the selected value from the dropdown
			var waivedSem = row.find('#waivedSem').val(); // Get the selected value from the disabled select element
			var registerSem = row.find('#registerSem').val();
		

			
		    if (courseWaiver === 'none') {   
		      alert('Please select an option.');
		      return false;
		    }
		    
		   if (courseWaiver==='WaivedIN'){
			if(currentSem === waivedSem){
				alert('Please Select Another Sem');
				return false;
				}}

		   if (courseWaiver==='WaivedOff'){
				if(currentSem === registerSem){
					alert('Please Select Another Subject');
					return false;
			}}
	

			if (courseWaiver == 'WaivedIN') {
				$.ajax({
					url:"saveWaivedInSubject",
					method: 'POST',
					contentType : 'application/json',
					data: JSON.stringify({
						'pssId':applicableSubject,
						'sem' : waivedSem,
						'sapid' : ${bean.sapid}
					}), 
					success:function(data){
		                    $('#message-box').show();
		                    $('#message-box').removeClass('alert-danger');
		                    $('#message-box').addClass('alert-success');
		                    $('#message').html(data+' '+'record has been updated');
		                   
						},
						error:function(error){					
							 $('#message-box').show();
			                    $('#message-box').removeClass('alert-success');
			                    $('#message-box').addClass('alert-danger');
			                    $('#message').html(error.responseText);
							
							}
						
					});
			}
			if (courseWaiver == 'WaivedOff') {
				$.ajax({
					url:"saveWaivedOffSubject",
					method: 'POST',
					contentType : 'application/json',
					data: JSON.stringify({
						'pssId':applicableSubject,	
						'sapid' : ${bean.sapid}
					}), 
					success:function(data){
					
						 $('#message-box').show();
						 $('#message-box').removeClass('alert-danger');
		                    $('#message-box').addClass('alert-success');
		                    $('#message').html(data+' '+'record has been updated');
		                  
						},
						error:function(error){
							 $('#message-box').show();
			                    $('#message-box').removeClass('alert-success');
			                    $('#message-box').addClass('alert-danger');
			                    $('#message').html(error.responseText);
							}
						
					});
			}
			// Perform further operations or send the data to the server
		});
		
	});
</script>

</html>