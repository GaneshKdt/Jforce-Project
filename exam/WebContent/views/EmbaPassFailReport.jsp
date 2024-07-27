<!DOCTYPE html>
<html lang="en">
<%@page import="com.nmims.beans.Person"%>
<%@page import="com.nmims.beans.Page"%>
<%@page import="com.nmims.beans.StudentMarksBean"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<jsp:include page="adminCommon/jscss.jsp">
	<jsp:param value="Emba PassFail Report" name="title"/>
</jsp:include>
<style>

.fixed-position {
   width: 300px; 
  height: 34px; 
}


}

</style>
<body>
	<%@ include file="adminCommon/header.jsp" %>
    <div class="sz-main-content-wrapper">
        <jsp:include page="adminCommon/breadcrum.jsp">
			<jsp:param value="Exam;Emba PassFail Report" name="breadcrumItems"/>
		</jsp:include>
        <div class="sz-main-content menu-closed">
            <div class="sz-main-content-inner">
              	<jsp:include page="adminCommon/left-sidebar.jsp">
					<jsp:param value="" name="activeMenu"/>
				</jsp:include>
              	<div class="sz-content-wrapper examsPage">
              		<%@ include file="adminCommon/adminInfoBar.jsp" %>
              		
              		<div class="sz-content">
						<h2 class="red text-capitalize">Emba PassFail Report</h2>
						<div class="clearfix"></div>
						<div class="panel-content-wrapper" style="min-height:100%;">
							<div class = "js_result"></div>
							<%@ include file="adminCommon/messages.jsp" %>
							<form:form  method="post" modelAttribute="resultBean">
								<div class="panel-body">
									<div class="row col-12">
										<h6>Note : To retrive all batches combined passfail data do not select any filters</h6>
										</div>
										<div class="row d-flex justify-content-center align-items-center">
											<div class="col-lg-3 col-md-6 col-12 column">
												<div class="form-group">
													<div class="row">
														<div class="col-12">
															<label>Exam Month:</label>
														</div>
													</div>
													<div class="row">
														<div class="col-12">
															<select id="examMonth" name="examMonth" path="examMonth" class="form-control">
																<option value="">-- select exam month --</option>
																<c:forEach items="${currentMonthList}" var="examMonth">
																	<option value="${examMonth}">${examMonth}</option>
																</c:forEach>
															</select>
														</div>
													</div>
												</div>
											</div>
											<div class="col-lg-3 col-md-6 col-12 column">
												<div class="form-group">
													<div class="row">
														<div class="col-12">
															<label>Exam Year:</label>
														</div>
													</div>
													<div class="row">
														<div class="col-12">
															<select id="examYear" class="form-control examYear" name="examYear" path="examYear">
																<option value="">-- select exam year --</option>
																<c:forEach items="${currentYearList}" var="examyr">
																	<option value="${examyr}">${examyr}</option>
																</c:forEach>
															</select>
														</div>
													</div>
												</div>
											</div>
											<div class="col-lg-3 col-md-6 col-12 column">
												<div class="form-group">
													<div class="row">
														<div class="col-12">
															<label for="consumerType">Consumer Type:</label>
														</div>
													</div>
													<div class="row">
														<div class="col-12">
															<select data-id="consumerTypeDataId" id="consumerTypeId" path="consumerTypeId" name="consumerTypeId" class="form-control selectConsumerType">
																<option disabled selected value="">-- select consumer type --</option>
																<c:forEach var="consumerType" items="${consumerType}">
																	<option value="<c:out value="${consumerType.id}"/>">
																		<c:out value="${consumerType.name}" />
																	</option>
																</c:forEach>
															</select>
														</div>
													</div>
												</div>
											</div>
											<div class="col-lg-3 col-md-6 col-12 column">
												<div class="form-group">
													<div class="row">
														<div class="col-12">
															<label for="programStructure">Program Structure:</label>
														</div>
														<div class="col-12">
															<select data-id="programStructureDataId" id="programStructureId" path="programStructureId" name="programStructureId" class="form-control selectProgramStructure">
																<option disabled selected value="">-- select program structure --</option>
															</select>
														</div>
													</div>
												</div>
											</div>
										</div>
										<div class="row">
											<div class="col-lg-3 col-md-6 col-12">
												<div class="form-group">
													<div class="row">
														<div class="col-12">
															<label for="Program">Program:</label>
														</div>
													</div>
													<div class="row">
														<div class="col-12">
															<select data-id="programDataId" id="programId" path="programId" name="programId" class="form-control selectProgram">
																<option disabled selected value="">-- select program --</option>
															</select>
														</div>
													</div>
												</div>
											</div>
											<%-- <div class="row">
												 	<div class="col-md-3">
														<div class="form-group">
														  	<label for="sel1">Select Batch:</label>
															<form:select path="batchId" id="batches" >
																<form:option value="" label="-- select batch --"/>
																<form:options items="${batches}" itemLabel="name" itemValue="id"/>
															</form:select>
														</div>
													</div>  --%>
											<div class="col-lg-3 col-md-6 col-12 column">
												<div class="form-group">
													<div class="row">
														<div class="col-12">
															<label for="batch">Batch:</label>
														</div>
													</div>
													<div class="row">
														<div class="col-12">
															<select data-id="batchDataId" path="batchId" id="batches" name="batchId" class="form-control batches">
																<option disabled selected value="">-- select batch --</option>
															</select>
														</div>
													</div>
												</div>
											</div>
											<div class="col-lg-3 col-md-6 col-12">
												<div class="form-group">
													<div class="row">
														<div class="col-12">
															<label for="sel1">Select Subject:</label>
														</div>
													</div>
													<div class="row">
														<div class="col-12">
															<select name="timebound_id" class="form-control" id="subject" itemValue="${resultBean.timebound_id}">
																<option disabled selected value="">-- select subject --</option>
															</select>
														</div>
													</div>
												</div>
											</div>
											<div class="col-lg-3 col-md-6 col-12">
												<div class="form-group">
													<div class="row">
														<div class="col-12">
															<label for="sel1">Select Assessment:</label>
														</div>
													</div>
													<div class="row">
														<div class="col-12">
															<select name="assessments_id" class="form-control" id="assessment" itemValue="${resultBean.assessments_id}">
																<option disabled selected value="">-- select assessment --</option>
															</select>
														</div>
													</div>
												</div>
											</div>
										</div>
										<div class="row">
											<div class="col-lg-3 col-md-6 col-12">
												<div class="form-group">
													<div class="row">
														<div class="col-12">
															<label for="sel1">Select Schedule:</label>
														</div>
													</div>
													<div class="row">
														<div class="col-12">
															<select name="schedule_id" class="form-control" id="schedule" itemValue="${resultBean.schedule_id}">
																<option disabled selected value="">-- select schedule --</option>
															</select>
														</div>
													</div>
												</div>
											</div>
										
											<div class="col-lg-3 col-md-6 col-12">
												<div class="form-group">
													<div class="row">
														<div class="col-12">
															<label for="sel1">Enter Sapid:</label>
														</div>
													</div>
													
													<div class="row">
														<div class="col-12">
															<form:input path="sapid" type="text" placeholder="-- enter sapid --" class="form-control fixed-position" />
														</div>
													</div>
												</div>
											</div>
										</div>
										<div class="row">
											<div class="col-lg-3 col-md-6 col-12">
												<div class="form-group">
													<button id="submit" name="submit" class="btn btn-large btn-primary" formaction="embaPassFailReport">Search Pass Fail Records</button>
												</div>		
											</div>
											<c:if test="${passFailResultsListSize > 0 || Q7ResultList > 0 || Q8ResultList > 0}">
												<div class="col-lg-3 col-md-6 col-12">
													<div class="form-group">
														<button id="downloadReport" name="submit" class="btn btn-large btn-primary" formaction="embaPassFailReportDownload">Download Complete Pass Fail Records</button>
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
 	<jsp:include page="adminCommon/footer.jsp"/>
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
					url:"getScheduleListByAssessment?id=" + assessment+"&timeid=" + subject,
					method:"GET",
					success:function(response){
						optionsList = '<option disabled selected value="">-- select schedule --</option>';
						for(let i=0;i < response.length;i++){
							optionsList = optionsList + '<option value="'+ response[i].schedule_id +'">'+ response[i].schedule_name +'</option>';
						}
						$('#schedule').html(optionsList);
						//console.log(response[0]);
					},
					error:function(error){
						alert("Error while getting schedule data");
						optionsList = '<option disabled selected value="">-- select schedule --</option>';
						$('#schedule').html(optionsList);
						let assessmentOption='<option disabled selected value="">-- select assessment --</option>';
						$('#assessment').html(assessmentOption);
						let subjectOption='<option disabled selected value="">-- select subject --</option>';
						$('#subject').html(subjectOption);
						
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
							optionsList = optionsList + '<option value="'+ response[i].id +'">'+ response[i].subject +'</option>';
						}
						$('#subject').html(optionsList);
						//console.log(response[0]);
					},
					error:function(error){
						alert("Error while getting schedule data");
						optionsList = '<option disabled selected value="">-- select schedule --</option>';
						$('#schedule').html(optionsList);
						let assessmentOption='<option disabled selected value="">-- select assessment --</option>';
						$('#assessment').html(assessmentOption);
						let subjectOption='<option disabled selected value="">-- select subject --</option>';
						$('#subject').html(subjectOption);
					}
				});
				$('#subject').html(optionsList);
			});
			
			$(document).on('change','#subject',function(){
				$('#downloadReport').hide();
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
						//console.log(response[0]);
					},
					error:function(error){
						alert("Error while getting schedule data");
						optionsList = '<option disabled selected value="">-- select schedule --</option>';
						$('#schedule').html(optionsList);
						let assessmentOption='<option disabled selected value="">-- select assessment --</option>';
						$('#assessment').html(assessmentOption);
						let subjectOption='<option disabled selected value="">-- select subject --</option>';
						$('#subject').html(subjectOption);
					}
				});
				$('#assessment').html(optionsList);
			});
			
			$(document).on('click','#downloadReport',function(){
				$(this).css('background-color','green');
			})
		});
	</script> 
	
	
	<script type="text/javascript">
		$(document).ready (function(){
			
			 $('.tables').DataTable({
				 "searching": false
			 });

			 var programElementArray = [];
		 		
			$('.selectConsumerType').on('change', function(){

				$('#downloadReport').hide();
				let id = $(this).attr('data-id');
				
				let options = "<option>Loading... </option>";
				let batchOptions="<option value=''> -- select batch -- </option>";
				$('#batches').html(batchOptions);

				let schedualOptionsList = '<option disabled selected value="">-- select schedule --</option>';
				$('#schedule').html(schedualOptionsList);
				let assessmentOption='<option disabled selected value="">-- select assessment --</option>';
				$('#assessment').html(assessmentOption);
				let subjectOption='<option disabled selected value="">-- select subject --</option>';
				$('#subject').html(subjectOption);
// 				$('#programStructureId').html(options);
// 				$('#programId').html(options);

				var consumerTypeId = document.getElementById("consumerTypeId").value;
 				var consumerProgramStructure = document.getElementById("programStructureId").value;
 				var programId = document.getElementById("programId").value;

 				var consumerProgramStructureArray = consumerProgramStructure.split(",");
 				
 				
				var data = {
					id:this.value
				}
				
				$.ajax({
					type : "POST",
					contentType : "application/json",
					url : "getDataByConsumerType",   
					data : JSON.stringify(data),
					success : function(data) {
						
						var programData = data.programData;
						var programStructureData = data.programStructureData;
						
						options = "";
						let allOption = "";
						
						for(let i=0;i < programData.length;i++){
							for (let j = 0; j < programElementArray.length; j++) {
								if(programData[i].id === programElementArray[j]){
							allOption = allOption + ""+ programData[i].id +",";
							options = options + "<option value='" + programData[i].id + "'> " + programData[i].name + " </option>";
								} 
							}
						}
						
						allOption = allOption.substring(0,allOption.length-1);
						
						$('#programId').html("<option value='"+ allOption +"'>All</option>" + options);
						
						options = ""; 
						allOption = "";
						
						for(let i=0;i < programStructureData.length;i++){
							for (let j = 0; j < consumerProgramStructureArray.length; j++) {
							if(programStructureData[i].id === consumerProgramStructureArray[j]){
							allOption = allOption + ""+ programStructureData[i].id +",";
							options = options + "<option value='" + programStructureData[i].id + "'> " + programStructureData[i].name + " </option>";
								}
							}
						}
						
						allOption = allOption.substring(0,allOption.length-1);
						
						$('#programStructureId').html("<option value='"+ allOption +"'>All</option>" + options);
					},
					error : function(e) {
						alert("Please Refresh The Page.");
						display(e);
					}
				});
			
			});
	
			$('.selectProgramStructure').on('change', function(){

				$('#downloadReport').hide();
				let id = $(this).attr('data-id');
				let options = "<option>Loading... </option>";
				let batchOptions="<option value=''> -- select batch -- </option>";
				$('#batches').html(batchOptions);
				$('#programId').html(options);

				let schedualOptionsList = '<option disabled selected value="">-- select schedule --</option>';
				$('#schedule').html(schedualOptionsList);
				let assessmentOption='<option disabled selected value="">-- select assessment --</option>';
				$('#assessment').html(assessmentOption);
				let subjectOption='<option disabled selected value="">-- select subject --</option>';
				$('#subject').html(subjectOption);
				
				var data = {
						programStructureId:this.value,
						consumerTypeId:$('#consumerTypeId').val()
				}
		
				$.ajax({
					type : "POST",
					contentType : "application/json",
					url : "getDataByProgramStructure",   
					data : JSON.stringify(data),
					success : function(data) {
						
						var programData = data.programData;
						
						options = "";
						let allOption = "";
						
						for(let i=0;i < programData.length;i++){
							for (let j = 0; j < programElementArray.length; j++) {
								if(programData[i].id === programElementArray[j]){
							allOption = allOption + ""+ programData[i].id +",";
							options = options + "<option value='" + programData[i].id + "'> " + programData[i].name + " </option>";
								}
							}
						}
						
						allOption = allOption.substring(0,allOption.length-1);
						
						$('#programId').html("<option value='"+ allOption +"'>All</option>" + options);
					},
					error : function(e) {
						alert("Please Refresh The Page.");
						display(e);
					}
				});
				
				
			});

			$('#programId').on('change', function(){

				$('#downloadReport').hide();
				let id = $(this).attr('data-id');
				let options = "<option>Loading... </option>";
				$('#batches').html(options);

				let schedualOptionsList = '<option disabled selected value="">-- select schedule --</option>';
				$('#schedule').html(schedualOptionsList);
				let assessmentOption='<option disabled selected value="">-- select assessment --</option>';
				$('#assessment').html(assessmentOption);
				let subjectOption='<option disabled selected value="">-- select subject --</option>';
				$('#subject').html(subjectOption);
				
				var consumerTypeId = document.getElementById("consumerTypeId").value;
				var consumerProgramStructure = document.getElementById("programStructureId").value;
				var consumerProgram = document.getElementById("programId").value;
				var examYear = document.getElementById("examYear").value;
				var examMonth = document.getElementById("examMonth").value;
			
				var data = {
					examYear: examYear,
					examMonth: examMonth,
					programId: consumerProgram,
					programStructureId: consumerProgramStructure,
					consumerTypeId: consumerTypeId
				};
				
				$.ajax({
					type : "POST",
					contentType : "application/json",
					url : "getBatchByMsterKey",
					data : JSON.stringify(data),
					success : function(data) {
						
					    var programData = data.subjectsData;
			
						options = "";
			 		    let allOption = "";
			 		    
			 		    for(let i=0; i < programData.length; i++){
			 		    	allOption = allOption + ""+ programData[i].id +",";
			 		        options = options + "<option value='" + programData[i].id + "'> " + programData[i].name + " </option>";
			 		    }
			 		    
					    allOption = allOption.substring(0, allOption.length-1);
					    
					    $('#batches').html("<option value='"+ allOption +"'>All</option>" + options);
					},
					error : function(e) {
			 			alert("Please Refresh The Page.");
						display(e);
					}
				}); 
			});

				$('#examYear,#examMonth').on('change', function(){
				$('#downloadReport').hide();
				let id = $(this).attr('data-id');
				let options = "<option>Loading... </option>";
// 				$('#batches').html(options);
				$('#consumerTypeId').html(options);
				$('#programStructureId').html(options);
				$('#programId').html(options);

				let schedualOptionsList = '<option disabled selected value="">-- select schedule --</option>';
				$('#schedule').html(schedualOptionsList);
				let assessmentOption='<option disabled selected value="">-- select assessment --</option>';
				$('#assessment').html(assessmentOption);
				let subjectOption='<option disabled selected value="">-- select subject --</option>';
				$('#subject').html(subjectOption);
				let batchOption='<option disabled selected value="">-- select batch --</option>';
				$('#batches').html(batchOption);
							
				var examYear = document.getElementById("examYear").value;
				var examMonth = document.getElementById("examMonth").value;
			
				var data = {
					examYear: examYear,
					examMonth: examMonth		
				};

				
				$.ajax({
					type : "POST",
					contentType : "application/json",
					url : "getConsumerTypeByExamYearMonth",
					data : JSON.stringify(data),
					success : function(data) {

						//console.log("status :: "+data.status);
					    var consumerData = data.subjectsData;
					    //console.log("programData :: "+consumerData);
			
						options = "";
			 		    let allOption = "";
			 		    
			 		    for(let i=0; i < consumerData.length; i++){
			 		    	allOption = allOption + ""+ consumerData[i].id +",";
			 		        options = options + "<option value='" + consumerData[i].id + "'> " + consumerData[i].name + " </option>";
			 		    }
			 		    
					    allOption = allOption.substring(0, allOption.length-1);
					    
					    $('#consumerTypeId').html("<option value='"+ allOption +"'>All</option>" + options);

					    var programData = data.programData;
						var programStructureData = data.programStructureData;
						
						options = "";
						let programDataAllOption = "";
						programElementArray=[];
						
						for(let i=0;i < programData.length;i++){
							programElementArray[i]=programData[i].id;
							programDataAllOption = programDataAllOption + ""+ programData[i].id +",";
							options = options + "<option value='" + programData[i].id + "'> " + programData[i].code + " </option>";
						}
						
						programDataAllOption = programDataAllOption.substring(0,programDataAllOption.length-1);
						
						$('#programId').html("<option value='"+ programDataAllOption +"'>All</option>" + options);
						
						options = ""; 
						programStructureDataAllOption = "";
						
						for(let i=0;i < programStructureData.length;i++){
							programStructureDataAllOption = programStructureDataAllOption + ""+ programStructureData[i].id +",";
							options = options + "<option value='" + programStructureData[i].id + "'> " + programStructureData[i].program_structure + " </option>";
						}
						
						programStructureDataAllOption = programStructureDataAllOption.substring(0,programStructureDataAllOption.length-1);
						
						$('#programStructureId').html("<option value='"+ programStructureDataAllOption +"'>All</option>" + options);
					},
					error : function(e) {
						if(e.status === 404){
							alert(" Select a valid year and month.");
							}else{
			 				alert("Please Refresh The Page.");
							display(e);
							}
					}
				}); 
 				
			});

// 				$('#examMonth').on('change', function(){
// 					$('#downloadReport').hide();
// 				});
 		}); 
	</script>
    </body>
</html>