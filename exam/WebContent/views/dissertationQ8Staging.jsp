<!DOCTYPE html>
<html lang="en">
	
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
    <jsp:include page="jscss.jsp">
        
	<jsp:param value="Pass Fail Staging" name="title"/>
    </jsp:include>
    <head>

    </head>
        <title>MSC(AI & ML Ops Result Processing)</title>
    <body>
    
    	<%@ include file="header.jsp"%>
	<section class="content-container">
		<div class="container-fluid customTheme">

			<div class="row">
				<legend> Transfer to Staging </legend>
			</div>

			<%@ include file="messages.jsp"%>
						
									<form:form  method="post" modelAttribute="resultBean" >
				<div class="panel-body">

					<div class="row">
						<div class="form-group" hidden="true">
							<input type="hidden" name="consumerProgramStructureId"
								class="form-control "
								value="${resultBean.consumerProgramStructureId}" /> <input
								type="hidden" name="prgm_sem_subj_id" class="form-control "
								value="${resultBean.prgm_sem_subj_id}" />
						</div>
						<div class="col-md-3">
							<div class="form-group">
								<label for="sel1">Select Program Type:</label> <select
									name="programType" class="form-control programType"
									itemValue="${resultBean.programType }" disabled>
									<option value="M.Sc. (AI & ML Ops)">M.Sc. (AI & ML
										Ops)</option>
								</select>
							</div>
						</div>

						<div class="col-md-3">
							<div class="form-group">
								<label for="sel1">Select Batch:</label> <select name="batchId"
									class="form-control batches" itemValue="${resultBean.batchId }" required>
									<option value="">Please Select Batch</option>
									<c:forEach items="${batch}" var="batch">
										<option value="${batch.id }">${batch.name }</option>
									</c:forEach>
								</select>
							</div>
						</div>
						<div class="col-md-3">
							<div class="form-group">
								<label for="sel1">Select Subject:</label> <select
									name="timebound_id" class="form-control" id="subject"
									itemValue="${resultBean.timebound_id}" required>
									<option disabled selected value="">-- select subject
										--</option>
								</select>
							</div>
						</div>
					</div>
					<div class="form-group">
						<button class="btn btn-large btn-primary"
							formaction="searchDissertaionQ8Marks" id="submit" name="submit">Search</button>

						<c:if test="${searchList.size() > 0 }">
							<button class="btn btn-large btn-primary"
								formaction="transfermarksToStaging" id="transfer"
								name="submit" formnovalidate>Transfer</button>
						</c:if>

				<a class="btn btn-large btn-primary" type="button" href = "dissertationQ8ExamResultCheckList" >Cancel</a>					
			</div>
					
			
				<c:if test="${upsertListForStaging > 0 }">
						<a href ="/exam/admin/downloadDissertationResultQ8" target ="_blank">Download Excel</a>
				</c:if>
				</div>

		
			</form:form>	
				
				<c:if test="${searchList.size() > 0 }">			
					<div class="panel">
						<div class="column">
							<div class="table-responsive">
								<table class ="dataTable" class="table table-striped table-hover tables">
														<thead>
															<tr>
																<th>SapId</th>															
																<th>Component C Score</th>
																<th>Component C Score Status</th>													
															</tr>
														</thead>
														<tbody>
															<c:forEach var="marks" items="${searchList}">
																<tr>
																	<td><c:out value="${marks.sapid}" /></td>
																	<td><c:out value="${marks.component_c_score}" /></td>										
																	<td><c:out value="${marks.component_c_status}" /></td>				
																</tr>
															</c:forEach>
														</tbody>
													</table>
										</div>
									</div>
								</div>
						</c:if>
					</div>
				</section>
			<jsp:include page="footer.jsp"/>
	</body>		
	<script type="text/javascript">
$(document).ready(function(){
	$(document).on('change','.batches',function(){
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
					console.log( response[i].subject);
					if(response[i].subject == ('${subjectName}')){
					optionsList = optionsList + '<option value="'+ response[i].id +'"  data-hasia="'+ response[i].hasIA+'" data-hastee="'+ response[i].hasTEE+'" >'+ response[i].subject +'</option>';
					}
				}
				$('#subject').html(optionsList);
				
			},
			error:function(error){
				alert("Error while getting schedule data");
			}
		});
		$('#subject').html(optionsList);
	});
});
</script>			
</html>