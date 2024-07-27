<!DOCTYPE html>
<html lang="en">

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
    <jsp:include page="../jscss.jsp">
	<jsp:param value="MBA-X Absolute Grading" name="title"/>
    </jsp:include>
    <%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
    


<body>
	<%@ include file="../header.jsp"%>
	<section class="content-container">
		<div class="container-fluid customTheme">

			<div class="row">
				<legend>MBA-X Absolute Grading </legend>
			</div>

			<%@ include file="../messages.jsp"%>

			<div class="js_result"></div>



			<form:form method="post" modelAttribute="resultBean">
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
								<label>Select Acad Year: <span style="color:red">*</span></label> <select
									required
									class="form-control" name="current_acad_year"
									itemValue="${resultBean.current_acad_year}">
									<option disabled selected value="">-- Select Acad Year --</option>
									<c:forEach var="current_acad_year" items="${acadsYearList}">
										<option value="<c:out value="${current_acad_year}"/>">
											<c:out value="${current_acad_year}" /></option>
									</c:forEach>
								</select>
							</div>
						</div>
						<div class="col-md-3">
							<div class="form-group">
								<label>Select Acad Month: <span style="color:red">*</span></label> <select
									required
									class="form-control" name="current_acad_month"
									itemValue="${resultBean.current_acad_month}">
									<option disabled selected value="">-- Select Acad Month --</option>
									<c:forEach var="current_acad_month" items="${acadsMonthList}">
										<option value="<c:out value="${current_acad_month}"/>">
											<c:out value="${current_acad_month}" /></option>
									</c:forEach>
								</select>
							</div>
						</div>
						

						<!-- div class="col-md-3">
							<div class="form-group">
								<label for="sel1">Select Subject:</label> <select
									required
									name="timebound_id" class="form-control" id="subject"
									itemValue="${resultBean.timebound_id}">
									<option disabled selected value="">-- select subject
										--</option>
								</select>
							</div>
						</div!-->
			
					</div>
	
					<div class="row">
					<div class="col-md-4">
							<!--   -->
							<div class="form-group">
								<button id="submit" name="submit"
									class="btn btn-large btn-primary"
									formaction="/exam/admin/mbaxAbsoluteGrading">Generate Absolute Grading</button>
									
							</div>
							
						</div>
						
					</div>
					
				</div>
			</form:form>
		 	
				<c:if test="${mbaXPassFailDataSize gt 0}">							
		<div class="panel-body">
												<h2>&nbsp;Absolute Grading Report<font size="2px"> (${mbaXPassFailDataSize} Records Found) &nbsp; <a href="downloadMbaXAbsoluteGrading" style="color:blue;">Download to Excel</a></font></h2>
		
								<div class="table-responsive">
									
									<table id="dt" class="table table-striped"
										style="font-size: 12px">
										<thead style="padding: 3px">
											<tr>
												<th>Sr. No.</th>
												<th>Batch Id</th>																			
												<th>SAP Id</th>
												<th>Student Name</th>
												<th>Program</th>
												<th>Subject</th>
												<th>IA Score</th>
												<th>TEE Score</th>
												<th>Grace Marks</th>
												<th>Total Score</th>
												<th>Grade</th>
												<th>Points</th>
												<th>Is Pass</th>
												<th>Fail Reason</th>
												<th>Is Result Live</th> 	
												<th>Status</th>
											</tr>
										</thead>
										<tbody style="padding: 3px">

											<c:forEach items="${mbaXPassFailData }" varStatus="status" var="bean">
												<tr>			
													<td><c:out value="${status.count }"></c:out></td>
													<td><c:out value="${bean.batch_id }"></c:out></td>
													<td><c:out value="${bean.sapid }"></c:out></td>	
													<td><c:out value="${bean.studentName }"></c:out></td>	
													<td><c:out value="${bean.program}"></c:out></td>
													<td><c:out value="${bean.subject}"></c:out></td>
													<td><c:out value="${bean.iaScore}"></c:out></td>	
													<td><c:out value="${bean.teeScore}"></c:out></td>
													<td><c:out value="${bean.graceMarks}"></c:out></td>
													<td><c:out value="${bean.total}"></c:out></td>
													<td><c:out value="${bean.grade}"></c:out></td>
													<td><c:out value="${bean.points}"></c:out></td>
													<td><c:out value="${bean.isPass}"></c:out></td>
													<td><c:out value="${bean.failReason}"></c:out></td>
													<td><c:out value="${bean.isResultLive}"></c:out></td>		
													<td><c:out value="${bean.status}"></c:out></td>		
													
			
													
												</tr>
											</c:forEach>											
										</tbody>
									</table>
								</div>
								</div>								
			</c:if>
			
		</div>
	</section>

	<jsp:include page="../footer.jsp" />
	<script src="https://cdn.datatables.net/1.10.13/js/jquery.dataTables.min.js"></script>
	<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/js/vendor/dataTables.bootstrap.js"></script>
	<script src="https://cdn.datatables.net/buttons/1.2.4/js/dataTables.buttons.min.js"></script>

<script>			$('#dt').DataTable();
</script>




</body>
</html>