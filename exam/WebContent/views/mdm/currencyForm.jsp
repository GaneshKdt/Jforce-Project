<!DOCTYPE html>
<%@page import="java.util.*"%>
<%@page import="java.text.DateFormat"%>
<html lang="en">
<%@page import="java.util.ArrayList"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@page import="com.nmims.beans.ProgramExamBean"%>

<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<style>
.panel-title .glyphicon {
	font-size: 14px;
}

.column {
	margin-bottom: 20px;
}
</style>
<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />assets/js/jquery-1.11.3.min.js"></script>
<%@page import="com.nmims.beans.Page"%>



<jsp:include page="../adminCommon/jscss.jsp">
	<jsp:param value="Currency Details Entry" name="title" />
</jsp:include>
<body>
	<%@ include file="../adminCommon/header.jsp"%>
	<div class="sz-main-content-wrapper">

		<jsp:include page="../adminCommon/breadcrum.jsp">
			<jsp:param value="Exam;Currency Details Entries" name="breadcrumItems" />
		</jsp:include>


		<div class="sz-main-content menu-closed">
			<div class="sz-main-content-inner">
				<jsp:include page="../adminCommon/left-sidebar.jsp">
					<jsp:param value="" name="activeMenu" />
				</jsp:include>
				<div class="sz-content-wrapper examsPage">
					<%@ include file="../adminCommon/adminInfoBar.jsp"%>
					<div class="sz-content">
						<h2 class="red text-capitalize">Add Currency Details</h2>
						<div class="clearfix"></div>
						<div class="panel-content-wrapper" style="min-height: 450px;">
						<div id="messageBox">
							<%@ include file="../adminCommon/messages.jsp"%></div>

							<form:form method="post" action="saveCurrencyDetails" id="currencyForm"
								modelAttribute="currency">
								<fieldset>


									<div class="panel-body">
										<div class="row">

											<div class="col-sm-3 column">
												<label>Consumer Type </label>
												<form:select data-id="consumerTypeDataId" required="required" value="${currency.consumerType }" id="consumerTypeId" name="consumerTypeId" path="consumerType" class="selectConsumerType form-control">
													<option disabled selected value="">Select Consumer Type</option>
													<c:forEach var="consumerType" items="${consumerType}">
														<c:choose>
															<c:when
																test="${consumerType.id == programsForm.consumerType}">
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
												</form:select>
											</div>
											<div class="col-sm-3 column">
												<label>Program Structure </label>
												<form:select id="programStructureId" required="required" name="programStructureId" path="programStructure" class="selectProgramStructure form-control">
													<option disabled selected value="">Select Program Structure</option>
												</form:select>
											</div>
											<div class="col-sm-3 column">
												<label>Program</label>
												<form:select id="programId" required="required" name="programId" path="program" class="selectProgram form-control">
													<option disabled selected value="">Select Program</option>
												</form:select>
											</div>
											<div class="col-sm-3 column">
												<label>Select Product</label>
												<form:select id="feeType" required="required" name="feeTypeId" path="feeId" class="selectFeeType form-control">
													<option disabled selected value="">Select Product</option>
													<c:forEach var="currency" items="${feeTypeList}">
														<option value="<c:out value="${currency.key}"/>"><c:out value="${currency.value}" /></option>
													</c:forEach>
												</form:select>

											</div>
											<div class="col-sm-3 column">
												<label>Select Currency</label>
												<form:select id="currency" required="required" name="currencyId" path="currencyId" class="selectCurrency form-control">													
													<option disabled selected value="">Select Currency</option>
													<c:forEach var="currency" items="${currencyList}">
														<option value="<c:out value="${currency.key}"/>"><c:out value="${currency.value}" /></option>
													</c:forEach>
												</form:select>

											</div>
											<div class="col-sm-3 column">
												<label>Price</label>
												<form:input id="price" path="price" type="text" placeholder="Enter your price" class="form-control" required="required"
													value="${currency.price}" />
											</div>
											<div class="clearfix"></div>
										</div>

										<button id="submitEntry" name="submit" class="btn btn-large btn-primary" onclick="return formValidation()" type="submit">Add Currency Details</button>
										<button id="cancel" name="cancel" class="btn btn-danger" formaction="${pageContext.request.contextPath}/home" formnovalidate="formnovalidate" >Cancel</button>
									</div>
								</fieldset>
							</form:form>
							<c:if test="${rowCount > 0}">
							<h4>(${rowCount} Records Found) &nbsp;<a href="downloadCurrencyReport">Download In Excel</a></h4></c:if>
							<div class="clearfix"></div>
						
							<div class="column">
								<div class="table-responsive">
									<table class="table table-striped table-hover tables"
										style="font-size: 12px">
										<thead>
											<tr>
												<th>Sr. No.</th>
												<th>Product</th>
												<th>Consumer Type</th>
												<th>Program</th>
												<th>Program Structure</th>
												<th>Currency</th>
												<th>Price</th>
												<th>Action</th>
												

											</tr>
										</thead>
										<tbody>

											<c:forEach var="currencyDetailsList" items="${currencyDetailsList}"
												varStatus="status">
												<tr 
												value="${currencyDetailsList.id}~${currencyDetailsList.feeName}~${currencyDetailsList.consumerType}~${currencyDetailsList.program}~${currencyDetailsList.programStructure}~${currencyDetailsList.currencyName}~${currencyDetailsList.price}">
							

													<td><c:out value="${currencyDetailsList.id}" /></td>
													<td><c:out value="${currencyDetailsList.feeName}" /></td>
													<td><c:out value="${currencyDetailsList.consumerType}" /></td>
													<td><c:out value="${currencyDetailsList.program}" /></td>
													<td><c:out value="${currencyDetailsList.programStructure}" /></td>												
													<td><c:out value="${currencyDetailsList.currencyName}" /></td>
													<td><c:out value="${currencyDetailsList.price}"/></td>							
													<td></td>
												</tr>
											</c:forEach>

										</tbody>
									</table>
								</div>


							</div>
						</div>
				</div>
				</div>
			</div>
		</div>
	</div>
	<jsp:include page="../adminCommon/footer.jsp" />


</body>

<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />assets/js/bootstrap.js"></script>
<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />assets/js/jquery.tabledit.js"></script>

<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/js/vendor/jquery-ui.min.js"></script>
<script src="https://cdn.datatables.net/1.10.13/js/jquery.dataTables.min.js"></script>
<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/js/vendor/dataTables.bootstrap.js"></script>
<script src="https://cdn.datatables.net/buttons/1.2.4/js/dataTables.buttons.min.js"></script>
<script src="${pageContext.request.contextPath}/assets/js/currencyProgram.js"></script>
<script src="${pageContext.request.contextPath}/assets/js/consumerProgramStructureCommonDropDown.js"></script>

</html>