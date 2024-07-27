<%-- original page
<!DOCTYPE html>
<html lang="en">
	
   <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%> 
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

    <jsp:include page="../adminCommon/jscss.jsp">
	<jsp:param value="Revenue Report" name="title"/>
    </jsp:include>
    
    
    
    <body>
    
    	<%@ include file="../adminCommon/header.jsp" %>
        <div class="sz-main-content-wrapper">
        
        	<jsp:include page="../adminCommon/breadcrum.jsp">
			<jsp:param value="Student Zone;Revenue Report" name="breadcrumItems"/>
			</jsp:include>
        	
            
            <div class="sz-main-content menu-closed">
                    <div class="sz-main-content-inner">
              				<jsp:include page="../adminCommon/left-sidebar.jsp">
								<jsp:param value="" name="activeMenu"/>
							</jsp:include>
              				
              				
              				<div class="sz-content-wrapper examsPage" style="min-height:900px;">
              						<%@ include file="../adminCommon/adminInfoBar.jsp" %>
              						<div class="sz-content">
								
											<h2 class="red text-capitalize">Revenue Report</h2>
											<div class="clearfix"></div>
											<div class="panel-content-wrapper" >
											<%@ include file="../adminCommon/messages.jsp" %>
									<form:form  action="/exam/admin/operationsRevenue" method="post" modelAttribute="operationsRevenue">
									<fieldset>
									<div class="col-md-4">
										<div class="form-group">
											<form:input id="startDate" path="startDate" type="date" placeholder="Start Date" class="form-control"  />
										</div>
										
										<div class="form-group">
											<form:input id="endDate" path="endDate" type="date" placeholder="End Date" class="form-control"  />
										</div>
											<div class="form-group">
											<form:select id="lc" path="lc" type="text" placeholder="Select LC" class="form-control">
											<form:option value="All">Select LC</form:option>
											<form:option value="Other"> Other </form:option>
											<form:options items="${lclist}"></form:options>
											</form:select>
										</div>
											<div class="form-group">
											<button id="submit" name="submit" class="btn btn-large btn-primary"
												formaction="/exam/admin/operationsRevenue">Generate</button>
												
												<button id="cancel" name="cancel" class="btn btn-danger" formaction="home" formnovalidate="formnovalidate">Cancel</button>
											</div>
											
											
											
										
										</div>
										
										</fieldset>
										</form:form>
											 </div>
								<c:choose>
									<c:when test="${rowCount > 0}">

									<h2>&nbsp;Revenue Details 
									<font size="2px">(${rowCount} Records Found)&nbsp; <a href="/exam/admin/downloadOperationsRevenueReport">Download to Excel</a>
									</font></h2>
									<div class="clearfix"></div>
									<div class="panel-content-wrapper">
									<div class="table-responsive">
									<table class="table table-striped table-hover" style="font-size:12px">
														<thead>
														<tr>
															<th>Sr. No.</th>
															<th>Type</th>
															<th>Amount</th>
														</tr>
													</thead>
														<tbody>
														
														<c:forEach var="revenue" items="${revenueList}" varStatus="status">
															<tr>
																<td><c:out value="${status.count}" /></td>
																<td><c:out value="${revenue.revenueSource}" /></td>
																<td><fmt:formatNumber type="number"  value="${revenue.amount}" /></td>

															</tr>   
														</c:forEach>
															<tr>
																<td><c:out value="6" /></td>
																<td><b><c:out value="Total" /></b></td>
																<td><b><fmt:formatNumber type="number"  value="${total}" /></b></td>

															</tr> 
														</tbody>
													</table>
												</div>
												</div>
												<br>
										</c:when>
										</c:choose>
              						</div>
              				   </div>
    				       </div>
			           </div>
		           </div>
        <jsp:include page="../adminCommon/footer.jsp"/>
    </body>
</html> --%>

<!-- first update -->
<!DOCTYPE html>
<html lang="en">

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<jsp:include page="../adminCommon/jscss.jsp">
	<jsp:param value="Revenue Report" name="title" />
</jsp:include>

<link
	href="https://cdnjs.cloudflare.com/ajax/libs/select2/4.0.6-rc.0/css/select2.min.css"
	rel="stylesheet" />

<body>

	<%@ include file="../adminCommon/header.jsp"%>
	<div class="sz-main-content-wrapper">
		<jsp:include page="../adminCommon/breadcrum.jsp">
			<jsp:param value="Student Zone;Revenue Report" name="breadcrumItems" />
		</jsp:include>
		<div class="sz-main-content menu-closed">
			<div class="sz-main-content-inner">
				<jsp:include page="../adminCommon/left-sidebar.jsp">
					<jsp:param value="" name="activeMenu" />
				</jsp:include>
				<div class="sz-content-wrapper examsPage" style="min-height: 900px;">
					<%@ include file="../adminCommon/adminInfoBar.jsp"%>
					<div class="sz-content">
						<h2 class="red text-capitalize">Revenue Report</h2>
						<div class="clearfix"></div>
						<div class="panel-content-wrapper">
							<%@ include file="../adminCommon/messages.jsp"%>
							<form:form action="/exam/admin/operationsRevenue" method="post"
								modelAttribute="operationsRevenue">
								<fieldset>
									<div class="col-md-4 column">
										<div class="form-group">
											<label for=startDate>StartDate</label>
											<form:input id="startDate" path="startDate" type="date"
												data-placeholder="Start Date" class="form-control" />
										</div>

										<div class="form-group">
											<label for=endDate>EndDate</label>
											<form:input id="endDate" path="endDate" type="date"
												data-placeholder="End Date" class="form-control" />
										</div>

										<div class="form-group">
											<label for=lc_list>Select LC</label> <br>
											<form:select id="lc" path="lc_list" type="text"
												class="js-lc-basic-multiple form-control"
												multiple="multiple" placeholder="Select LC"
												style="width: 100%">
												<%-- <form:option value="">Select LC</form:option> --%>
												<%-- 	<form:option value="Other"> Other </form:option> --%>
												<form:options items="${lclist}"></form:options>
											</form:select>
										</div>
										<div class="form-group">
											<label for=ic_list>Select IC</label> <br>
											<form:select id="ic" path="ic_list" type="text"
												class="js-ic-basic-multiple form-control"
												multiple="multiple" data-placeholder="Select IC"
												style="width: 100%">
												<%-- <form:option value="">Select IC</form:option> --%>
												<%-- <form:option value="Other"> Other </form:option> --%>
												<form:options items="${iclist}"></form:options>
											</form:select>
										</div>
										<div class="form-group">
											<label for=programType>Select Program Type</label> <br>
											<form:select id="programType" path="programType" type="text"
												class="form-control" style="width: 100%">
												<form:option value="">Program Type</form:option>
												<form:option value="PG">PG (includes Cert,Diploma)</form:option>
												<form:option value="MBA - WX">MBAwx</form:option>
												<form:option value="MBA - X">MBAx</form:option>
											</form:select>
										</div>
										<div class="form-group">
											<label for=paymentOption>Select Gateway</label> <br>
											<form:select id="paymentOption" path="paymentOption"
												type="text" class="form-control">
												<form:option value="">Select Payment Gateway</form:option>
												<form:option value="">All</form:option>
												<form:options items="${paymentOptions}"></form:options>
											</form:select>
										</div>
										<div class="form-group">
											<button id="submit" name="submit"
												class="btn btn-large btn-primary"
												formaction="/exam/admin/operationsRevenue">Generate</button>

											<button id="cancel" name="cancel" class="btn btn-danger"
												formaction="home" formnovalidate="formnovalidate">Cancel</button>
										</div>
									</div>

								</fieldset>
							</form:form>
						</div>
						<c:choose>
							<c:when test="${rowCount > 0}">

								<h2>
									&nbsp;Revenue Details <font size="2px">&nbsp; <a
										href="/exam/admin/downloadOperationsRevenueReport">Download to Excel</a>
									</font>
								</h2>
								<div class="clearfix"></div>
								<div class="panel-content-wrapper">
									<div class="table-responsive">
										<table class="table table-striped table-hover"
											style="font-size: 12px">
											<thead>
												<tr>
													<th>Sr.No.</th>
													<th>Type</th>
													<th>Amount</th>
												</tr>
											</thead>
											<tbody>

												<c:forEach var="revenue" items="${revenueTypeBasedList}"
													varStatus="status">
													<tr>
														<td><c:out value="${status.count}" /></td>
														<c:choose>
															<c:when
																test="${revenue.revenueSource eq 'Service Request'}">
																<td><a data-toggle="modal" data-target="#myModal"><c:out
																			value="${revenue.revenueSource}" /></a></td>
															</c:when>
															<c:otherwise>
																<td><c:out value="${revenue.revenueSource}" /></td>
															</c:otherwise>
														</c:choose>

														<td><fmt:formatNumber type="number"
																value="${revenue.amount}" /></td>

													</tr>
												</c:forEach>
											</tbody>
										</table>
									</div>
								</div>
								<br>
							</c:when>
						</c:choose>

						<!-- Modal -->
						<div class="modal fade" id="myModal" role="dialog">
							<div class="modal-dialog">

								<!-- Modal content-->
								<div class="modal-content">
									<div class="modal-header">
										<button type="button" class="close" data-dismiss="modal">&times;</button>
										<h4 class="modal-title">SR type based distribution</h4>
									</div>
									<div class="modal-body">
										<div class="panel-content-wrapper">
											<div class="table-responsive">
												<table class="table table-striped table-hover"
													style="font-size: 12px">
													<thead>
														<tr>
															<th>Sr. No.</th>
															<th>Type</th>
															<th>Amount</th>
														</tr>
													</thead>
													<tbody>

														<c:forEach var="revenue" items="${srRevenue}"
															varStatus="status">
															<tr>
																<td><c:out value="${status.count}" /></td>
																<td><c:out value="${revenue.serviceRequestType}" /></td>
																<td><fmt:formatNumber type="number"
																		value="${revenue.amount}" /></td>

															</tr>
														</c:forEach>
													</tbody>
												</table>
											</div>
										</div>
									</div>
									<div class="modal-footer">
										<button type="button" class="btn btn-default"
											data-dismiss="modal">Close</button>
									</div>
								</div>

							</div>
						</div>

					</div>
				</div>
			</div>
		</div>
	</div>
	<jsp:include page="../adminCommon/footer.jsp" />

	<script type="text/javascript"
		src="https://cdnjs.cloudflare.com/ajax/libs/select2/4.0.6-rc.0/js/select2.min.js"></script>
	<script>
	$(document).ready(function() {
		�//�� $('.js-example-basic-multiple').select2();
		
		� $('.js-lc-basic-multiple').select2({
		    placeholder: "Click to select LC"
		});
		
		 $('.js-ic-basic-multiple').select2({
			    placeholder: "Click to select IC"
			});
		
		
		});
</script>
</body>
</html>