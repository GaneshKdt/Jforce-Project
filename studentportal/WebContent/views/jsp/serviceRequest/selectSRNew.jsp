<!DOCTYPE html>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<html lang="en">

<jsp:include page="../common/jscssNew.jsp">
	<jsp:param value="Select Service Request" name="title" />
</jsp:include>
<body>

	<%@ include file="../common/headerDemo.jsp"%>



	<div class="sz-main-content-wrapper">

		<jsp:include page="../common/breadcrum.jsp">
			<jsp:param value="Student Zone;Student Support;Service Request"
				name="breadcrumItems" />
		</jsp:include>


		<div class="sz-main-content menu-closed">
			<div class="sz-main-content-inner">
				<div id="sticky-sidebar">
					<jsp:include page="../common/left-sidebar.jsp">
						<jsp:param value="Service Request" name="activeMenu" />
					</jsp:include>
				</div>
				<div class="sz-content-wrapper examsPage">
					<%@ include file="../common/studentInfoBar.jsp"%>


					<div class="sz-content">

						<h5 style="color: #d2232a; font-weight: bold;">Select Service
							Request</h5>
						<div class="clearfix"></div>
						<div class="card card-body">
							<%@ include file="../common/messageDemo.jsp"%>

							<form:form action="addSRForm" method="post" modelAttribute="sr">
								<fieldset>
									<div class="row">
										<div class="col-md-4">

											<%
												if ("true".equals((String) request.getAttribute("edit"))) {
											%>
											<form:input type="hidden" path="id" value="${sr.id}" />
											<%
												}
											%>

											<div class="form-group">
												<form:label path="serviceRequestType"
													for="serviceRequestType">
													<span style="font-weight: bold;">Service Request
														Type</span>
												</form:label>
												<form:select id="serviceRequestType"
													path="serviceRequestType" class="form-select"
													required="required">
													<form:option value="">Select Service Request</form:option>
													<form:options items="${requestTypes}" />
												</form:select>
											</div>

											<div class="form-group">
												<label class="control-label" for="submit"></label>
												<div class="controls">

													<button id="submit" name="submit"
														class="btn btn-danger me-2" formaction="addSRForm">Proceed</button>
													<button id="cancel" name="cancel" class="btn btn-secondary"
														formaction="${pageContext.request.contextPath}/supportOverview"
														formnovalidate="formnovalidate">Cancel</button>
												</div>
											</div>
										</div>
									</div>
								</fieldset>
							</form:form>


						</div>

						</br> </br>

						<c:if test="${rowCount > 0}">
							<h2 style="font-size: 19.2px; font-weight: bold;">My Service
								Requests (${rowCount} Records Found)</h2>
							<div class="clearfix"></div>
							<div class="card card-body">
								<div class="table-responsive">
									<table class="table table-striped table-hover"
										style="font-size: 12px">
										<thead>
											<tr>
												<th>Sr. No.</th>
												<th>Service Request ID</th>
												<th>Service Request Type</th>
												<th>Service Request Status</th>
												<th>Payment Status</th>
												<th>Amount</th>
												<th>Description</th>
												<th>Documents</th>
												<th>Track Shipment</th>
												<!-- <th>Process Service Request</th>-->
											</tr>
										</thead>
										<tbody>

											<c:forEach var="sr" items="${srList}" varStatus="status">
												<!-- <c:url value="proceedToPayForSR" var="processSR">
																      <c:param name="srId" value="${sr.id}" />
																    </c:url> -->
												<tr>
													<td><c:out value="${status.count}" /></td>
													<td><c:out value="${sr.id}" /></td>
													<td><c:out value="${sr.serviceRequestType}" /></td>
													<td><c:out value="${sr.requestStatus}" /></td>
													<td><c:out value="${sr.tranStatus}" /></td>
													<td><c:out value="${sr.respAmount}" /></td>
													<td><c:out value="${sr.description}" /></td>
													<td><c:if test="${sr.hasDocuments == 'Y' }">
															<a
																href="/studentportal/student/viewSRDocumentsForStudents?serviceRequestId=${sr.id}"
																target="_blank">View</a>
														</c:if></td>
													<td><c:set var="srPresent" value="false" /> <c:forEach
															var="srId" items="${srIdList}">
															<c:if test="${srId eq sr.id}">
																<c:set var="srPresent" value="true" />
															</c:if>
														</c:forEach> <c:if test="${srPresent}">
															<form:form id="form1_${ sr.id }" method="POST"
																action="trackShipment">
																<a href="#" class="js__trackBtn" data-id="${ sr.id }">Track
																	Now</a>
																<input type="hidden" name="srId" value="${sr.id}" />
																<input type="hidden" name="srType"
																	value="${sr.serviceRequestType}" />
															</form:form>
														</c:if></td>

													<!-- <td>
																			<c:if test="${sr.requestStatus =='Re-Opened'}">
																			<a href="${processSR}" title="Proceed To Pay For SR"><i class="fa fa-cog fa-lg fa-spin"></i></a>
																			</c:if>
																		</td>-->
												</tr>
											</c:forEach>

										</tbody>
									</table>
								</div>
								<br>
							</div>
						</c:if>

					</div>
				</div>


			</div>
		</div>
	</div>


	<jsp:include page="../common/footerDemo.jsp" />

	<script>
		$(document).ready(function() {
			$(document).on('click', '.js__trackBtn', function() {
				let id = $(this).attr('data-id');
				$('#form1_' + id).submit();
			});
		});
	</script>
</body>
</html>