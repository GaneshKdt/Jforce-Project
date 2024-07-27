<!DOCTYPE html>
<html lang="en">
	
<%@page import="com.nmims.beans.Person"%>
<%@page import="com.nmims.beans.Page"%>
<%@page import="com.nmims.beans.StudentMarksBean"%>


<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
    <jsp:include page="../adminCommon/jscss.jsp">
	<jsp:param value="MBA-X PassFail Report" name="title"/>
    </jsp:include>
    
    
    
    <body>
    
    	<%@ include file="../adminCommon/header.jsp" %>
        <div class="sz-main-content-wrapper">
        
        	<jsp:include page="../adminCommon/breadcrum.jsp">
			<jsp:param value="Exam;MBA-X PassFail Report" name="breadcrumItems"/>
			</jsp:include>
        	
            
            <div class="sz-main-content menu-closed">
                    <div class="sz-main-content-inner">
              				<jsp:include page="../adminCommon/left-sidebar.jsp">
								<jsp:param value="" name="activeMenu"/>
							</jsp:include>
              				
              				
              				<div class="sz-content-wrapper examsPage">
              						<%@ include file="../adminCommon/adminInfoBar.jsp" %>
              						<div class="sz-content">
								
											<h2 class="red text-capitalize">MBA-X PassFail Report</h2>
											<div class="clearfix"></div>
													<div class="panel-content-wrapper" style="min-height:100%;">
													<div class = "js_result">
													
													</div>
																 
													
											<%@ include file="../adminCommon/messages.jsp" %>
											<form:form  method="post" modelAttribute="resultBean">
													<div class="panel-body">
													<h6>Note : To retrive all batches combined passfail data do not select any filters</h6>
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
																			formaction="mbaxPassFailReport">Search Pass Fail Records</button>
																</div>		
															</div>
														
															<c:if test="${passFailResultsListSize gt 0}">
															<div class="col-md-3 ">
																<!--   -->
																<div class="form-group">
																	<button id="downloadReport" name="submit" class="btn btn-large btn-primary"
																					formaction="mbaxPassFailReportDownload">Download Complete Pass Fail Records</button>
																</div>	
															</div>
															</c:if>
															
														</div>
												</div>
									</form:form>
								</div>
							</div>
              			</div>
    				</div>
			   </div>
		    </div>
        <jsp:include page="../adminCommon/footer.jsp"/>
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
					url:"getSubjectListByBatchId?id=" + batch,
					method:"GET",
					success:function(response){
						optionsList = '<option disabled selected value="">-- select subject --</option>';
						for(let i=0;i < response.length;i++){
							optionsList = optionsList + '<option value="'+ response[i].id +'" data-pssid="'+ response[i].prgm_sem_subj_id+'" >'+ response[i].subject +'</option>';
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
				if(pssId == 1789){
			    	let option =  '<option value="" selected>Not Applicable</option>';
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
			
			$(document).on('click','#downloadReport',function(){
				$(this).css('background-color','green');
			})
		});
	</script>
    </body>
</html>