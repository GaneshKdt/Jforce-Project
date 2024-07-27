<!DOCTYPE html>
<html lang="en">


<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
        <%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<jsp:include page="jscss.jsp">
	<jsp:param value="Emba Grace" name="title" />
</jsp:include>



<body>
	<%@ include file="header.jsp"%>
	<section class="content-container">
		<div class="container-fluid customTheme">

			<div class="row">
				<legend> Emba Grace </legend>
			</div>

			<%@ include file="messages.jsp"%>

			<div class="js_result"></div>



			<form:form method="post" modelAttribute="resultBean">
				<div class="panel-body">
					<div class="row">
						<%-- <div class="col-md-3">
							<div class="form-group">
								<label for="sel1">Select Batch:</label>
								<form:select path="batchId" id="batches" >
									<form:option value="" label="-- select batch --"/>
									<form:options items="${batches}" itemLabel="name" itemValue="id"/>
								</form:select>
							</div>
						</div> --%>
						
						<div class="col-md-3">
								<div class="form-group">
									<label for="sel1">Acad Month</label>
									<select name="acadMonth" id="acadMonth" class="form-control" onclick="clearProgram()">
										<option value="">-- Acad Month --</option>
										<c:forEach items="${acadMonth}" var="month">
											<option value="${month}">${month}</option>
										</c:forEach>
									</select>
								</div>
							</div>
					
					<div class="col-md-3 ">
						<div class="form-group">
							<label for="sel1">Acad Year</label>
								<select name="acadYear" id="acadYear" class="form-control" onclick="clearProgram()">
									<option value="">-- Acad Year --</option>
									<c:forEach items="${acadYear}" var="year">
									<option value="${year}">${year}</option>
									</c:forEach>
								</select>
						</div>
					</div>
						
						<div class="col-md-3">
							<div class="form-group">
								<label for="sel1">Select Program Type:</label>
								<select name="programType" id="programType" class="form-control" onChange='programTypeFunc(event)' >
								<option value="">-- select Program Type --</option>
								<option value="M.Sc. (AI & ML Ops)">M.Sc. (AI & ML Ops)</option>
								<option value="M.Sc. (AI)">M.Sc. (AI)</option>
								<option value="MBA - WX">MBA - WX</option>
								<option value="Modular PD-DM">Modular PD-DM</option>
								</select>
							</div>
						</div>
										
						<div class="col-md-3">
							<div class="form-group">
								<label for="sel1">Select Batch:</label>
								<select name="batchId" id="batches" class="form-control" itemValue="${resultBean.batchId}" >
								<option disabled selected value="">-- select Batch --</option>
								</select>
							</div>
						</div>

						<div class="col-md-3">
							<div class="form-group">
								<label for="sel1">Select Subject:</label> <select
									name="timebound_id" class="form-control" id="subject"
									itemValue="${resultBean.timebound_id}">
									<option disabled selected value="">-- select subject
										--</option>
								</select>
							</div>
						</div>
					</div>

					<div class="row">
						<div class="col-md-4 ">
							<!--   -->
							<div class="form-group">
								<button id="submit" name="submit"
									class="btn btn-large btn-primary"
									formaction="embaProjectGraceMarksSearch">Search Students For Grace</button>
							</div>
						</div>

						<c:if test="${finalListforGraceSize gt 0}">
							<div class="col-md-4 ">
								<!--   -->
								<div class="form-group">
									<button id="submit" name="submit"
										class="btn btn-large btn-primary" formaction="embaProjectGraceMarks">Run Grace</button>
								</div>
							</div>
						</c:if>
					</div>
				</div>
			</form:form>
			<c:if test="${finalListforGraceSize gt 0}">
				<div class="panel-body">
					<div class="column">
						<div class="table-responsive">
							<table class="table table-striped table-hover tables"
								style="font-size: 12px; margin-left: 25px;">
								<thead>
									<tr>
										<th>Sapid</th>
										<th>Tee Score</th>
										<th>IA Score</th>
										<th>Applicable Grace</th>
									</tr>
								</thead>
								<tbody>

									<c:forEach var="graceBean" items="${finalListforGrace}">
										<tr>
											<td><c:out value="${graceBean.sapid}" /></td>
											<td><c:out value="${graceBean.teeScore}" /></td>
											<td><c:out value="${graceBean.iaScore}" /></td>
											<td><c:out value="${graceBean.graceMarks}" /></td>
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

	<jsp:include page="footer.jsp" />
	<script src="https://cdn.datatables.net/1.10.13/js/jquery.dataTables.min.js"></script>
	<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/js/vendor/dataTables.bootstrap.js"></script>
	<script src="https://cdn.datatables.net/buttons/1.2.4/js/dataTables.buttons.min.js"></script>
	
	<script>
		$(document).ready(function(){
			
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
		
			
		});

		function programTypeFunc(e)
		{
			var programType=e.target.value;
			var acadMonth=document.getElementById("acadMonth").value;
			var acadYear=document.getElementById("acadYear").value;
			if(programType == "" || acadMonth == "" || acadYear == "")
			{
				return false;
			}

			let optionList='<option value="" disabled selected>Loading...</option>';
			document.getElementById("batches").innerHTML=optionList;
			$.ajax({
				url:'/exam/m/getActiveBatchList',
				method:'POST',
				data:JSON.stringify({ 
					programType : programType,
					acadMonth : acadMonth,
					acadYear : acadYear
				}),
				contentType:'application/json',
				success:function(response)
				{
					optionList = '<option disabled selected value="">-- select Batch --</option>';
					for(let i=0;i < response.length;i++){
						optionList = optionList + '<option value="' + response[i].id +'" >'+ response[i].name + '</option>';
					}
					document.getElementById("batches").innerHTML=optionList;
				},
				error:function(error)
				{
					alert("Unable to fetch Batch List !");
			        optionsList = '<option value="" disabled selected>Unable to fetch Batch List !</option>';
			        document.getElementById("batches").innerHTML=optionList;
				}
			})
		}

		function clearProgram()
		{
			if(document.getElementById("programType").value!=""){
				document.getElementById("programType").value="";
			}
		}
	</script>





</body>
</html>