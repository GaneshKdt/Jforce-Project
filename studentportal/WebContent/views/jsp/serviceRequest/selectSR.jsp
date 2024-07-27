<%-- <!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!--> 


<html class="no-js"> <!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<jsp:include page="../jscss.jsp">
<jsp:param value="Select Service Request" name="title" />
</jsp:include>


<body class="inside">

<%@ include file="../header.jsp"%>
	
    <section class="content-container login">
        <div class="container-fluid customTheme">
         <div class="row"><legend>Select Service Request</legend></div>
        
			
       
        <%@ include file="../messages.jsp"%>
		
		<div class="panel-body">
			<div class="col-md-8 column" style="border:1px solid #e5e5e5; padding:10px">
					<h2>Create Service Request</h2>
					<form:form  action="addSRForm" method="post" modelAttribute="sr" >
						<fieldset>
			
	
					<%if("true".equals((String)request.getAttribute("edit"))){ %>
					<form:input type="hidden" path="id" value="${sr.id}"/>
					<%} %>
					
					<div class="form-group">
						<form:label path="serviceRequestType" for="serviceRequestType">Service Request Type</form:label>
						<form:select id="serviceRequestType" path="serviceRequestType"
							class="form-control" required="required">
							<form:option value="">Select Service Request</form:option>
							<form:options items="${requestTypes}" />
						</form:select>
					</div>
						
					<div class="form-group">
						<label class="control-label" for="submit"></label>
						<div class="controls">
							
							<button id="submit" name="submit"
								class="btn btn-large btn-primary" formaction="addSRForm">Proceed</button>
							<button id="cancel" name="cancel" class="btn btn-danger"
								formaction="home" formnovalidate="formnovalidate">Cancel</button>
						</div>
					</div>
					
					</fieldset>
			</form:form>
						
			</div>
			<div class="col-md-2 column">
			</div>
			
			<div class="col-md-8 column" style="border:1px solid #e5e5e5; padding:10px">
			<h2>Raise a Query with Support Team</h2>
			
			<form action="https://www.salesforce.com/servlet/servlet.WebToCase?encoding=UTF-8" method="POST" id="ajaxform">
			<form action="https://cs5.salesforce.com/servlet/servlet.WebToCase" method="POST" id="ajaxform"> Sand box
	
				<!-- <input type=hidden name="orgid" value="00DO0000000War1"> This is Sandbox id -->
				<input type=hidden name="orgid" value="00D90000000s6BL">
				<input type=hidden name="retURL" value="<spring:eval expression="@propertyConfigurer.getProperty('WEB2CASE_SUBMISSION_REDIRECT_URL')" />">
				<input type=hidden name="encoding" value="UTF-8">
				<input type=hidden name="name" value="<%=studentBean.getFirstName() + " " + studentBean.getLastName()%>">
				<input type=hidden name="email" value="<%=studentBean.getEmailId()%>">
				<input type=hidden name="phone" value="<%=studentBean.getMobile()%>">
				<input type=hidden name="origin" value="Student Zone">
				<input type="hidden" id="recordType" name="recordType" value="01290000000A959">
				
				
				<!--  ----------------------------------------------------------------------  -->
				<!--  NOTE: These fields are optional debugging elements. Please uncomment    -->
				<!--  these lines if you wish to test in debug mode.                          -->
				<!--  <input type="hidden" name="debug" value=1>                              -->
				<!--  <input type="hidden" name="debugEmail"                                  -->
				<!--  value="sanketpanaskar@gmail.com">                                       -->
				<!--  ----------------------------------------------------------------------  -->
				
				<div class="form-group">
				  <select  id="00N9000000EPf0L" name="00N9000000EPf0L" title="Category" class="form-control" required="required">
					  <option value="">Select a Category</option>
					  <option value="Re-Registration">Re-Registration</option>
					  <option value="Refund">Refund</option>
					  <option value="Course Delivery & Academics">Course Delivery & Academics</option>
					  <option value="Examination / Results">Examination / Results</option>
					  <option value="Program Validity">Program Validity</option>
					  <option value="General/Other">General/Other</option>
				  </select>													  
			  </div>
				
				<div class="form-group">
				<label for="subject">Subject</label>
				<input  id="subject" maxlength="80" name="subject" size="20" type="text" class="form-control" required="required"/><br>
				</div>
				
				<div class="form-group">
				<label for="description">Description</label>
				<textarea name="description" class="form-control" ></textarea><br>
				</div>
				
				<button id="submit" name="submit" class="btn btn-large btn-primary" >Submit Query</button>
				
				</form>
			
			</div> 
				
		</div>
		<br/>
				
		 	<c:choose>
			<c:when test="${rowCount > 0}">
			<div class="panel-body">
			<legend>&nbsp;My Service Requests<font size="2px">(${rowCount} Records Found)&nbsp; </font></legend>
			<div class="table-responsive">
			<table class="table table-striped table-hover" style="font-size:12px">
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
									</tr>
								</thead>
								<tbody>
								
								<c:forEach var="sr" items="${srList}" varStatus="status">
							        <tr>
							            <td><c:out value="${status.count}"/></td>
							            <td><c:out value="${sr.id}"/></td>
										<td><c:out value="${sr.serviceRequestType}"/></td>
										<td><c:out value="${sr.requestStatus}"/></td>
										<td><c:out value="${sr.tranStatus}"/></td>
										<td><c:out value="${sr.amount}"/></td>
										<td><c:out value="${sr.description}"/></td>
										<td>
											<c:if test="${sr.hasDocuments == 'Y' }">
												<a href="/studentportal/viewSRDocuments?serviceRequestId=${sr.id}" target="_blank">View</a>
											</c:if>
										</td>
							        </tr>   
							    </c:forEach>
									
								</tbody>
							</table>
			</div>
			<br>
		</div>
		</c:when>
		</c:choose>
		</div>
				
					
				
		
	
	
</section>
	
<jsp:include page="../footer.jsp" />

<!-- <script>
	$(document).ready(function(){
	    $("#formButton").submit(function(e)
		{
		    var postData = $(this).serializeArray();
		    var formURL = $(this).attr("action");
		    $.ajax(
		    {
			url : formURL,
			type: "POST",
			data : postData,
			success:function(data, textStatus, jqXHR) 
			{
			    alert('Your Request is Submitted successfully. Please do not resubmit it!');
			},
			error: function(jqXHR, textStatus, errorThrown) 
			{
			    alert('Error in Submission');      
			}
		    });
		    e.preventDefault(); //STOP default action
		    e.unbind(); //unbind. to stop multiple form submit.
		});
		 
		$("#ajaxform").submit(); //Submit  the FORM
	});
</script> -->

</body>
</html>
 --%>


<!DOCTYPE html>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<html lang="en">




<jsp:include page="../common/jscss.jsp">
	<jsp:param value="Select Service Request" name="title" />
</jsp:include>
<body>

	<%@ include file="../common/header.jsp"%>



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

						<h2 class="red text-capitalize">Select Service Request</h2>
						<div class="clearfix"></div>
						<div class="panel-content-wrapper">
							<%@ include file="../common/messages.jsp"%>

							<form:form action="addSRForm" method="post" modelAttribute="sr">
								<fieldset>
									<div class="row">
										<div class="col-md-4">

											<%if("true".equals((String)request.getAttribute("edit"))){ %>
											<form:input type="hidden" path="id" value="${sr.id}" />
											<%} %>

											<div class="form-group">
												<form:label path="serviceRequestType"
													for="serviceRequestType">Service Request Type</form:label>
												<form:select id="serviceRequestType"
													path="serviceRequestType" class="form-control"
													required="required">
													<form:option value="">Select Service Request</form:option>
													<form:options items="${requestTypes}" />
												</form:select>
											</div>

											<div class="form-group">
												<label class="control-label" for="submit"></label>
												<div class="controls">

													<button id="submit" name="submit"
														class="btn btn-large btn-primary" formaction="addSRForm">Proceed</button>
													<button id="cancel" name="cancel" class="btn btn-danger"
														formaction="${pageContext.request.contextPath}/supportOverview"
														formnovalidate="formnovalidate">Cancel</button>
												</div>
											</div>
										</div>
									</div>
								</fieldset>
							</form:form>


						</div>



						<c:if test="${rowCount > 0}">
							<h2 class="red text-capitalize">My Service Requests
								(${rowCount} Records Found)</h2>
							<div class="clearfix"></div>
							<div class="panel-content-wrapper">
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
													<td>
													<c:set var="srPresent" value="false" />
													<c:forEach var="srId" items="${srIdList}">
													  <c:if test="${srId eq sr.id}">
													    <c:set var="srPresent" value="true" />
													  </c:if>
													</c:forEach>
													<c:if test="${srPresent}">
													<form:form id="form1_${ sr.id }" method="POST" action="trackShipment">
													<a href="#" class="js__trackBtn" data-id="${ sr.id }">Track Now</a>
													<input type="hidden" name="srId" value="${sr.id}"/>
													<input type="hidden" name="srType" value="${sr.serviceRequestType}"/>
													</form:form>
													</c:if>
													</td>
													
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


	<jsp:include page="../common/footer.jsp" />

<script>
	$(document).ready(function(){
		$(document).on('click','.js__trackBtn',function(){
				let id = $(this).attr('data-id');
				$('#form1_' + id).submit();
		});
	});
</script>
</body>
</html>