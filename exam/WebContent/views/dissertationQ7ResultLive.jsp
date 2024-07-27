<!DOCTYPE html>
<html lang="en">
	
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
    <jsp:include page="jscss.jsp">
        
	<jsp:param value="Make Live" name="title"/>
    </jsp:include>
    <head>

    </head>
        <title>MSC(AI & ML Ops Result Processing)</title>
    <body>
    
    	<%@ include file="header.jsp"%>
	<section class="content-container">
		<div class="container-fluid customTheme">

			<div class="row">
				<legend> Make Live</legend>
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
							formaction="dissertationMakeResultLive" id="submit"
							name="submit">Make Live</button>
						<a class="btn btn-large btn-primary"  href= "dissertationQ7ExamResultCheckList" type="button">Cancel</a>
					</div>
				</div>

			</form:form>	
				
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