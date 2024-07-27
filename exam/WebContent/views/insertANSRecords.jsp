<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!-->

<%@page import="com.nmims.beans.Person"%>
<%@page import="com.nmims.beans.Page"%>
<%@page import="com.nmims.beans.StudentMarksBean"%>
<html class="no-js">
<!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<jsp:include page="jscss.jsp">
	<jsp:param value="Insert ANS Records in Table" name="title" />
</jsp:include>

<body class="inside">

	<%@ include file="header.jsp"%>

	<section class="content-container">
		<div class="container-fluid customTheme">
			<div class="row">
				<legend>Insert ANS Records in Table</legend>
			</div>
			<%@ include file="messages.jsp"%>

			<form:form action="searchANSRecordsToInsert" method="post"
				modelAttribute="searchBean">
				<fieldset>
					<div class="panel-body">

						<div class="col-md-6 column">
							
							<div class="form-group">
								<form:select id="acadYear" path="acadYear" cssClass="form-control" required="required"
									itemValue="${searchBean.acadYear}">
									<form:option value="">Select Acads Year</form:option>
									<form:options items="${acadsYearList}" />
								</form:select>
							</div>
							
							<div class="form-group">
								<form:select id="acadMonth" path="acadMonth" cssClass="form-control" required="required"
									itemValue="${searchBean.acadMonth}">
									<form:option value="">Select Acads Month</form:option>
									<form:options items="${acadsMonthList}" />
								</form:select>
							</div>
						
							<div class="form-group">
								<form:select id="year" path="year" cssClass="form-control" required="required"
									itemValue="${searchBean.year}">
									<form:option value="">Select Exam Year</form:option>
									<form:options items="${yearList}" />
								</form:select>
							</div>

							<div class="form-group">
								<form:select id="month" path="month" cssClass="form-control" required="required"
									itemValue="${searchBean.month}">
									<form:option value="">Select Exam Month</form:option>
									<form:option value="Jun">Jun</form:option>
									<form:option value="Sep">Sep</form:option>
									<form:option value="Dec">Dec</form:option>
									<form:option value="Apr">Apr</form:option>
								</form:select>
							</div>
							
							
							
							
							

							<div class="form-group">
								<select data-id="consumerTypeDataId" id="consumerTypeId"
									name="consumerTypeId" class="selectConsumerType form-control">
									<option disabled selected value="">Select Consumer
										Type</option>
									<c:forEach var="consumerType" items="${consumerType}">
										<c:choose>
											<c:when
												test="${consumerType.id == searchBean.consumerTypeId}">
												<option selected value="<c:out value="${consumerType.id}"/>">
													<c:out value="${consumerType.name}" />
												</option>
											</c:when>
											<c:otherwise>
												<option value="<c:out value="${consumerType.id}"/>">
													<c:out value="${consumerType.name}" />
												</option>
											</c:otherwise>
										</c:choose>

									</c:forEach>
								</select>
							</div>
							<div class="form-group">
								<select id="programStructureId" name="programStructureId"
									class="selectProgramStructure form-control">
									<option disabled selected value="">Select Program
										Structure</option>
								</select>
							</div>
							<div class="form-group">
								<select id="programId" name="programId"
									class="selectProgram form-control">
									<option disabled selected value="">Select Program</option>
								</select>
							</div>


							<div class="form-group">
								<label class="control-label" for="submit"></label>
								<button id="submit" name="submit"
									class="btn btn-large btn-primary"
									formaction="searchANSRecordsToInsert">Search</button>
								<button id="cancel" name="cancel" class="btn btn-danger"
									formaction="examCenterHome" formnovalidate="formnovalidate">Cancel</button>
							</div>
						</div>

					</div>
				</fieldset>
			</form:form>



			<c:choose>
				<c:when test="${rowCount > 0}">
					<div class="panel-body">
						<legend>
							&nbsp;ANS Records<font size="2px"> (${rowCount} Records
								Found) &nbsp; </font>
						</legend>
						<form:form action="insertANSRecords" method="post"
							modelAttribute="searchBean">
							<fieldset>
								<form:hidden path="acadYear" />
								<form:hidden path="acadMonth" />
								<form:hidden path="year" />
								<form:hidden path="month" />
								<input type="hidden" name="consumerTypeId" value="${ searchBean.consumerTypeId }"/>
								<input type="hidden" name="programStructureId" value="${ searchBean.programStructureId }"/>
								<input type="hidden" name="programId" value="${ searchBean.programId }"/>
								<div class="form-group">
									<label class="control-label" for="submit"></label>
									<button id="submit" name="submit"
										class="btn btn-large btn-primary"
										formaction="insertANSRecords"
										onClick="return confirm('Are you sure?')">Insert ANS
										Records in Exam Marks</button>

									<button id="cancel" name="cancel" class="btn btn-danger"
										formaction="home" formnovalidate="formnovalidate">Cancel</button>
									<span><b>Note:</b>This will insert all ANS records for
										above Year-Month.</span>
								</div>

							</fieldset>
						</form:form>


					</div>
				</c:when>
			</c:choose>
		</div>
	</section>

	<jsp:include page="footer.jsp" />
	<script>
		 var consumerTypeId = '${ searchBean.consumerTypeId }';
		 var programStructureId = '${ searchBean.programStructureId }';
		 var programId = '${ searchBean.programId }';
		</script>
		
		<script>
		function validate(){
			//console.log('validate invoked');
			var examYear = document.getElementById('year').value;
			var examMonth = document.getElementById('month').value;
			//console.log("acadYear" +acadYear);
			//console.log("acadMonth" +acadMonth);
			if(examYear == "" || examMonth == ""){
				alert('Kindly select examYear and examMonth');
				return false;
			}
			
			return true;
		
		}
		</script>
        <%@ include file="../views/common/consumerProgramStructure.jsp" %>

</body>
</html>
