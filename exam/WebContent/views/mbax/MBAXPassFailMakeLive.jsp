<!DOCTYPE html>
<html lang="en">

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
    <jsp:include page="../jscss.jsp">
	<jsp:param value="MBA-X Results Make Live" name="title"/>
    </jsp:include>
    
    
    
    <body>
    
    	<%@ include file="../header.jsp"%>
	<section class="content-container">
		<div class="container-fluid customTheme">

			<div class="row">
				<legend> MBA-X Results Make Live </legend>
			</div>

			<%@ include file="../messages.jsp"%>
													<div class = "js_result"></div>
																 
											<form:form  method="post" modelAttribute="resultBean">
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
																  <label for="sel1">Select Assessment:</label>
																  <select name="assessments_id" class="form-control" id="assessment"  itemValue="${resultBean.assessments_id}">
																  	<option disabled selected value="">-- select assessment --</option>
																  </select>
																</div>
															</div>
															
															<div class="col-md-3">
																<div class="form-group">
																  <label for="sel1">Select Schedule:</label>
																  <select name="schedule_id" class="form-control" id="schedule"  itemValue="${resultBean.schedule_id}">
																    <option disabled selected value="">-- select schedule --</option>
																  </select>
																</div>
															</div>
														</div>
														
														<div class="row">
															<div class="col-md-3 ">
																<!--   -->
																<div class="form-group">
																	<button id="submit" name="submit" class="btn btn-large btn-primary"
																			formaction="mbaxPassFailMakeLive">Make Results Live</button>
																</div>		
															</div>
														
														</div>
												</div>
									</form:form>
								</div>
						</section>
        <jsp:include page="../footer.jsp"/>
        <script>
		$(document).ready(function(){
			$(document).on('change','#assessment',function(){
				$('#downloadReport').hide();
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
					url:"getMBAXScheduleListByAssessment?id=" + assessment+"&timeid=" + subject,
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
			});
			
			
			$(document).on('change','#batches',function(){
				$('#downloadReport').hide();
				var batch = $(this).val();
				if(batch == ""){
					return false;
				}
				let optionsList = '<option value="" disabled selected>loading</option>';
				$.ajax({
					url:"getMBAXSubjectListByBatchId?id=" + batch,
					method:"GET",
					success:function(response){
						optionsList = '<option disabled selected value="">-- select subject --</option>';
						for(let i=0;i < response.length;i++){
							optionsList = optionsList + '<option value="'+ response[i].id +'"  data-pssid="'+ response[i].prgm_sem_subj_id+'">'+ response[i].subject +'</option>';
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
			
			$(document).on('change','#subject',function(){
				$('#downloadReport').hide();
				var subject = $(this).val();
				let pssId = $(this).find('option:selected').data('pssid');
				if(pssId == 1789 || pssId == 2712){
			    	let option =  '<option value="0" selected>Not Applicable</option>';
			    	$('#assessment').html(option);
			    	$('#schedule').html(option);
			    	return false;
				}
				if(subject == ""){
					return false;
				}
				let optionsList = '<option value="" disabled selected>loading</option>';
				$.ajax({
					url:"getMBAXAssessmentListByTimeBoundId?id=" + subject,
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
			});
			
			
		});
	</script>
    </body>
</html>