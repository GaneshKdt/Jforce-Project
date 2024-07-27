<!DOCTYPE html>
<html lang="en">

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<jsp:include page="../jscss.jsp">
	<jsp:param value="MBA-X Upload Absent List" name="title" />
</jsp:include>


<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<body>
<%@ include file="../header.jsp"%>
	<section class="content-container">
		<div class="container-fluid customTheme">

			<div class="row">
				<legend> MBA-X Upload Absent List </legend>
			</div>

			<%@ include file="../messages.jsp"%>

							<div class="js_result"></div>
							<form:form method="post" modelAttribute="resultBean" enctype="multipart/form-data">
								<div class="panel-body">
									<div class="row">
										 <div class="col-md-3">
											<div class="form-group">
												<label for="batchId">Select Batch:</label> 
												<form:select path="batchId" id="batches" >
													<form:option value="" label="-- select batch --"/>
													<form:options items="${batches}" itemLabel="name" itemValue="id"/>
												</form:select>
											</div>
										</div> 

										<div class="col-md-3">
											<div class="form-group">
											  <label for="sel1">Select Subject:</label>
											  <select name="timebound_id" class="form-control" id="subject"   itemValue="${resultBean.timebound_id}">
											    <option disabled selected value="">-- select subject --</option>
											  </select>
											</div>
										</div>

										<div class="col-md-3">
											<div class="form-group">
											  <label for="sel1">Select Exam Type:</label>
											  <select name="max_marks" class="form-control" id="subject" itemValue="${resultBean.max_marks}">
											    <option selected value="">-- select exam type --</option>
											    <option selected value="40">Normal</option>
											    <option selected value="100">Re Exam</option>
											  </select>
											</div>
										</div>
										<%-- <div class="col-md-3">
											<div class="form-group">
												<label for="sel1">Select Subject:</label> <form:select
													 class="form-control" id="subject"
													path="prgm_sem_subj_id">
													<form:option value="0">Select Subject</form:option>
													<c:forEach var="filter" items="${subjectList}">
											          <form:option value="${filter.value}">
											            <c:out  value="${filter.key}"></c:out>
											          </form:option>
											        </c:forEach>
												</form:select>
											</div>
										</div> --%>
									
									</div>
										
									<%-- <div class="row">
										<div class="col-md-4 ">
											<!--   -->
											<div class="form-group">
											<form:input path="fileData" type="file" />
											</div>
										</div>

									</div> --%>
									<div class="row">
										<div class="col-md-4 ">
											<!--   -->
											<div class="form-group">
											<!-- <button id="submit" name="submit"
													class="btn btn-large btn-primary"
													formaction="uploadUpgradAbsentList">Upload</button> -->

												<button id="submit" name="submit"
													class="btn btn-large btn-primary"
													formaction="searchABRecordsToInsertMBAX">Search</button>
											</div>
										</div>

									</div>
								</div>
							</form:form>
							<c:choose>
								<c:when test="${rowCount > 0}">
									<div class="panel-body">
										<legend>
											&nbsp;Absent Records<font size="2px"> (${rowCount} Records
												Found) &nbsp; <a href="downloadABReportMBAX">Download AB
													report to verify before insertion</a>
											</font>
										</legend>
										<a class="btn btn-large btn-primary" href="insertABReportMBAX">Insert
											AB Records</a>
									</div>
								</c:when>
							</c:choose>
						</div>
					</section>
	<jsp:include page="../footer.jsp" />
	<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />assets/js/jquery-1.11.3.min.js"></script>
	<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />assets/js/bootstrap.js"></script>
	<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />assets/js/jquery.tabledit.js"></script>

	<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/js/vendor/jquery-ui.min.js"></script>
	<script src="https://cdn.datatables.net/1.10.13/js/jquery.dataTables.min.js"></script>
	<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/js/vendor/dataTables.bootstrap.js"></script>
	<script src="https://cdn.datatables.net/buttons/1.2.4/js/dataTables.buttons.min.js"></script>


	<script>
		$(document).ready(function(){
			/*$(document).on('change','#assessment',function(){
				var assessment = $(this).val();
				if(assessment == ""){
					return false;
				}
				var subject = $('#subject').val();
				if(subject == ""){
					return false;
				}
				let optionsList = '<option value="" disabled selected>loading</option>';
				$.ajax({
					url:"getScheduleListByAssessment?id=" + assessment+"&timeid=" + subject,
					method:"GET",
					success:function(response){
						optionsList = '<option disabled selected value="">-- select schedule --</option>';
						for(let i=0;i < response.length;i++){
							optionsList = optionsList + '<option value="'+ response[i].schedule_id +'">'+ response[i].schedule_name +'</option>';
						}
						$('#schedule').html(optionsList);
						console.log(response[0]);
					},
					error:function(error){
						alert("Error while getting schedule data");
					}
				});
				$('#schedule').html(optionsList);
			});*/
			
			
			$(document).on('change','#batches',function(){
				var batch = $(this).val();
				if(batch == ""){
					return false;
				}
				let optionsList = '<option value="" disabled selected>loading</option>';
				$.ajax({
					url:"getSubjectListByBatchId?id=" + batch,
					method:"GET",
					success:function(response){
						optionsList = '<option disabled selected value="">-- select subject --</option>';
						for(let i=0;i < response.length;i++){
							optionsList = optionsList + '<option value="'+ response[i].id +'">'+ response[i].subject +'</option>';
						}
						$('#subject').html(optionsList);
						console.log(response[0]);
					},
					error:function(error){
						alert("Error while getting schedule data");
					}
				});
				$('#subject').html(optionsList);
			});
			
			/*$(document).on('change','#subject',function(){
				var subject = $(this).val();
				if(subject == ""){
					return false;
				}
				let optionsList = '<option value="" disabled selected>loading</option>';
				$.ajax({
					url:"getAssessmentListByTimeBoundId?id=" + subject,
					method:"GET",
					success:function(response){
						optionsList = '<option disabled selected value="">-- select assessment --</option>';
						for(let i=0;i < response.length;i++){
							optionsList = optionsList + '<option value="'+ response[i].id +'">'+ response[i].customAssessmentName +'</option>';
						}
						$('#assessment').html(optionsList);
						console.log(response[0]);
					},
					error:function(error){
						alert("Error while getting schedule data");
					}
				});
				$('#assessment').html(optionsList);
			}); */
			$('#dt').DataTable();
			$('.dataTables_length').addClass('bs-select');
		});
	</script>
</body>
</html>