<!DOCTYPE html>
<html lang="en">

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<jsp:include page="../jscss.jsp">
	<jsp:param value="Mark RIA/NV/Score" name="title" />
</jsp:include>

<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>


<body>

	<%@ include file="../header.jsp"%>
	<section class="content-container">
		<div class="container-fluid customTheme">

			<div class="row">
				<legend> MBA-X Mark RIA/NV/Score </legend>
			</div>

			<%@ include file="../messages.jsp"%>

			<div class="js_result"></div>



			<form:form method="post" modelAttribute="resultBean">
				<fieldset>

					<div class="panel-body">

						<div class="row">
							<div class="col-md-3">
								<div class="form-group">

									<form:input id="sapid" path="sapid" type="text"
										placeholder="SAP ID" class="form-control"
										value="${resultBean.sapid}" required="required" />

								</div>
							</div>
						</div>
						<div class="row">
							<div class="col-md-3 ">
								<!--   -->
								<div class="form-group">
									<button id="submit" name="submit"
										class="btn btn-large btn-primary"
										formaction="mbaxSearchScoresForRIANV">Search Student
										Scores</button>
								</div>
							</div>

						</div>
					</div>
				</fieldset>
			</form:form>
			<c:choose>
				<c:when test="${rowCount > 0}">

					<div class="panel-body">
						<div class="row">
							<div class="table-responsive">
								<table id="dt" class="table table-striped"
									style="font-size: 12px">
									<thead style="padding: 3px">
										<tr>
											<th>Sr. No.</th>
											<th>SAP ID</th>
											<th>Student Name</th>
											<th>Subject</th>
											<th>TEE Score</th>
											<th>Schedule Name</th>
											<th>Batch</th>
											<th>Actions</th>

										</tr>
									</thead>
									<tbody style="padding: 3px">
										<% try{ %>
										<c:forEach var="studentMarks" items="${teeScores}"
											varStatus="status">
											<tr>
												<td><c:out value="${status.count}" /></td>

												<td><c:out value="${studentMarks.sapid}" /></td>
												<td nowrap="nowrap"><c:out
														value="${studentMarks.student_name}" /></td>

												<td nowrap="nowrap"><c:out
														value="${studentMarks.subject}" /></td>
												<td class="score" data-count=${status.count }><c:out
														value="${studentMarks.score}" /></td>
												<td nowrap="nowrap"><c:out
														value="${studentMarks.schedule_name}" /></td>
												<td nowrap="nowrap"><c:out
														value="${studentMarks.batchId}" /></td>
												<td><c:choose>
														<c:when test="${studentMarks.status eq 'RIA'}">
															      RIA <input checked
																data-timebound_id="${studentMarks.timebound_id}"
																data-sapid="${studentMarks.sapid}"
																data-subject="${studentMarks.subject}"
																data-schedule_id="${studentMarks.schedule_id}"
																data-pss_id="${studentMarks.prgm_sem_subj_id}"
																type="radio" value="RIA"
																name="writtenScore${status.count}"
																class="markMBAXSingleSubjectRIANV" style="width:10px; height:10px" />
															<br>
														</c:when>
														<c:otherwise>
															       RIA <input
																data-timebound_id="${studentMarks.timebound_id}"
																data-sapid="${studentMarks.sapid}"
																data-subject="${studentMarks.subject}"
																data-schedule_id="${studentMarks.schedule_id}"
																data-pss_id="${studentMarks.prgm_sem_subj_id}"
																type="radio" value="RIA"
																name="writtenScore${status.count}"
																class="markMBAXSingleSubjectRIANV" style="width:10px; height:10px"/>
															<br>
														</c:otherwise>
													</c:choose> <c:choose>
														<c:when test="${studentMarks.status eq 'NV'}">
															       NV <input checked
																data-timebound_id="${studentMarks.timebound_id}"
																data-sapid="${studentMarks.sapid}"
																data-subject="${studentMarks.subject}"
																data-schedule_id="${studentMarks.schedule_id}"
																data-pss_id="${studentMarks.prgm_sem_subj_id}"
																type="radio" value="NV"
																name="writtenScore${status.count}"
																class="markMBAXSingleSubjectRIANV" style="width:10px; height:10px"/>
															<br>

														</c:when>
														<c:otherwise>
															        NV <input
																data-timebound_id="${studentMarks.timebound_id}"
																data-sapid="${studentMarks.sapid}"
																data-subject="${studentMarks.subject}"
																data-schedule_id="${studentMarks.schedule_id}"
																data-pss_id="${studentMarks.prgm_sem_subj_id}"
																type="radio" value="NV"
																name="writtenScore${status.count}"
																class="markMBAXSingleSubjectRIANV" style="width:10px; height:10px"/>
															<br>
														</c:otherwise>
													</c:choose> <c:set var="numberAsString">${studentMarks.score}</c:set>

													<c:if test="${numberAsString.matches('[0-9]+')}">
															Score <input checked
															data-timebound_id="${studentMarks.timebound_id}"
															data-sapid="${studentMarks.sapid}"
															data-subject="${studentMarks.subject}"
															data-schedule_id="${studentMarks.schedule_id}"
															data-pss_id="${studentMarks.prgm_sem_subj_id}"
															type="radio" value="Score"
															name="writtenScore${status.count}"
															class="markMBAXSingleSubjectRIANV" style="width:10px; height:10px"/>

													</c:if> <c:if test="${!numberAsString.matches('[0-9]+')}">
															Score <input
															data-timebound_id="${studentMarks.timebound_id}"
															data-sapid="${studentMarks.sapid}"
															data-subject="${studentMarks.subject}"
															data-schedule_id="${studentMarks.schedule_id}"
															data-pss_id="${studentMarks.prgm_sem_subj_id}"
															type="radio" value="Score"
															name="writtenScore${status.count}"
															class="markMBAXSingleSubjectRIANV" style="width:10px; height:10px"/>

													</c:if></td>
											</tr>
										</c:forEach>
										<%}catch(Exception e){
															}	%>
									</tbody>
								</table>
							</div>
						</div>
					</div>
					<br>

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
	
	<script type="text/javascript">
   
        $('.markMBAXSingleSubjectRIANV').click(function(){
        	
        	var conf = confirm('Are you sure you want to edit this?');
        	if(conf == true){
        		$('.js_result').html('<h3>loading...</h3>');
        		if(this.checked){
        			var self = $(this);
                $.ajax({
                    type: "POST",
                    url: '/exam/admin/updateMBAXSubjectAsRIANV',
                    data: {
                    	'status' : $(this).attr('value'),
                    	'subject' : $(this).attr('data-subject'),
                    	'timebound_id' : $(this).attr('data-timebound_id'),
                    	'schedule_id' : $(this).attr('data-schedule_id'),
                    	'sapid' : $(this).attr('data-sapid'),
                    	'prgm_sem_subj_id' : $(this).attr('data-pss_id'),
                    	
                    },
                    success:function(response){
                    	
                    	if(response.Status == "Success"){
                    		if(response.score == "RIA"){
                    			$('.js_result').html('<div class="alert alert-success alert-dismissible"> <a href="#" class="close" data-dismiss="alert" aria-label="close">&times;</a> <strong>Success!</strong> updated result status to RIA. </div>');
                    		 self.parents('tr').children('.score').html('RIA');
                    		}
							if(response.score == "NV"){
								$('.js_result').html('<div class="alert alert-success alert-dismissible"> <a href="#" class="close" data-dismiss="alert" aria-label="close">&times;</a> <strong>Success!</strong> updated result status to NV. </div>');
								 self.parents('tr').children('.score').html('NV');
                    		}
							if(response.score != "NV" && response.score != "RIA"){
								$('.js_result').html('<div class="alert alert-success alert-dismissible"> <a href="#" class="close" data-dismiss="alert" aria-label="close">&times;</a> <strong>Success!</strong> updated  result status to marks. </div>');
								 self.parents('tr').children('.score').html(response.score);
                    		}
                    		
                    	}else{
                    		$('.js_result').html('<div class="alert alert-danger alert-dismissible"> <a href="#" class="close" data-dismiss="alert" aria-label="close">&times;</a> <strong>Failed!</strong> update of result status. </div>');
                    	}
                   },
                   error:function(){
                	   $('.js_result').html('<div class="alert alert-danger alert-dismissible"> <a href="#" class="close" data-dismiss="alert" aria-label="close">&times;</a> <strong>Failed!</strong> Server Error Found. </div>');
                   }
                });

        		}
        		}else{
        			this.checked = false;
        			var return_score = $(this).parent().parent().find('.score');
        			var status_count = return_score.attr('data-count');
        			$('input[type="radio"][name = "writtenScore"+status_count][value = "'+ return_score.html() +'"]').prop('checked', true);
            		console.log($(this).parent().parent().find('.score').html());
            		
        		}
        });

        
        /* $('.markAllRIANV').click(function(){
        	var conf = confirm('Are you sure you want to edit these?');
        	if(conf == true){
        	$('.js_result').html('<h3>loading...</h3>');
        		var self = $(this);
                 $.ajax({
                    type: "POST",
                    url: '/exam/updateSubjectAsRIANV',
                    data: {
                    	'status' : $(this).attr('data-status'),
                    	'subject' : '',
                    	'sem' : $(this).attr('data-sem'),
                    	'program' : $(this).attr('data-program'),
                    	'year' : $(this).attr('data-year'),
                    	'month' : $(this).attr('data-month'),
                    	'sapid' : $(this).attr('data-sapid'),
                    	'studentType' : $(this).attr('data-studentType'),
                    	'prgm_sem_subj_id' : $(this).attr('data-pss_id'),
                    }, 
                    success:function(response){
                    	if(response.Status == "Success"){
                    		if(response.writtenScore == "RIA"){
                    			$('.js_result').html('<div class="alert alert-success alert-dismissible"> <a href="#" class="close" data-dismiss="alert" aria-label="close">&times;</a> <strong>Success!</strong> updated all result status to RIA. </div>');
                    		$('.score').html('RIA');
                    		$("input[type='radio'][value = 'NV']").removeAttr('checked');
                    		$("input[type='radio'][value = 'RIA']").prop('checked',true);
                    		}
							if(response.writtenScore == "NV"){
								$('.js_result').html('<div class="alert alert-success alert-dismissible"> <a href="#" class="close" data-dismiss="alert" aria-label="close">&times;</a> <strong>Success!</strong> updated all result status to NV. </div>');
								$('.score').html('NV');
								$("input[type='radio'][value = 'RIA']").removeAttr('checked');
								$("input[type='radio'][value = 'NV']").prop('checked',true);
                    		}
                    	}else{
                    		$('.js_result').html('<div class="alert alert-danger alert-dismissible"> <a href="#" class="close" data-dismiss="alert" aria-label="close">&times;</a> <strong>Failed!</strong> update of all result status. </div>');
                    	}
                   },
                   error:function(){
                	   $('.js_result').html('<div class="alert alert-danger alert-dismissible"> <a href="#" class="close" data-dismiss="alert" aria-label="close">&times;</a> <strong>Failed!</strong> Server Error Found. </div>');
                   }
                });
        	}else{
        		$(this).parent().children('.score').html();
        		console.log($(this).parent().children('.score').html());
        		this.checked = false;
        		
        	}
        }); */ 
        $('#dt').DataTable();
		$('.dataTables_length').addClass('bs-select');
        </script>

</body>
</html>