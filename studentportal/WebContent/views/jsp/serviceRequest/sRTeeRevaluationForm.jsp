
<!DOCTYPE html>
<%@page import="java.util.*"%>
<%@page import="java.text.DateFormat"%>
<html lang="en">


<%@page import="com.nmims.beans.PageStudentPortal"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<jsp:include page="../adminCommon/jscss.jsp">
	<jsp:param value="Search Service Request" name="title" />
</jsp:include>

<style>
	.editable-submit{
		margin-right: 0.5em;
	}
	
	.editable-cancel{
		margin-left: 0.5em;
	}
	
	a.hl-disabled{
		pointer-events: none;
  		cursor: default; 
/* 		opacity: 0.8; */
		color: inherit;
		font-size: inherit;
	}
	
	a.hl-disabled:hover{
  		text-decoration: none; 
	}
	
	h5 {
	font-weight: 600;
	text-transform: none;
	text-align: justify;
	letter-spacing: 0.020em;
	margin-left: 0.5em em;
	margin-right: 0.5em;
	line-height: 1.6em;
	margin-top: auto;
	margin-bottom: auto;
}

.modal-body {
	overflow: hidden;
}

</style>

<body>
	<%@ include file="../adminCommon/header.jsp"%>
	<div class="sz-main-content-wrapper">

		<jsp:include page="../adminCommon/breadcrum.jsp">
			<jsp:param value="Student Portal;Search Service Request"
				name="breadcrumItems" />
		</jsp:include>


		<div class="sz-main-content menu-closed">
			<div class="sz-main-content-inner">
			<div id="sticky-sidebar"> 
				<jsp:include page="../adminCommon/left-sidebar.jsp">
					<jsp:param value="" name="activeMenu" />
				</jsp:include>
				</div>
				<div class="sz-content-wrapper examsPage">
					<%@ include file="../adminCommon/adminInfoBar.jsp"%>
					<div class="sz-content">
						<h2 class="red text-capitalize">Search Service Request</h2>
						<div class="clearfix"></div>
						<div class="panel-content-wrapper" style="min-height: 450px;">
							<%@ include file="../adminCommon/messages.jsp"%>
							<form:form action="teeRevaluationReport" method="post" modelAttribute="sr">
								<fieldset>
									<div class="col-md-4">

										<div class="form-group">
											<form:select id="serviceRequestType"
												path="serviceRequestType" class="form-control">
												<%-- <form:option value="">Select Service Request Type</form:option> --%>
												<%-- <form:option value="Change in Specialisation">Change in Specialisation</form:option>
												<form:option value="Program De-Registration">Program De-Registration</form:option> --%>
												<form:option value="Revaluation of Term End Exam Marks">Revaluation of Term End Exam Marks</form:option>
												<%-- <form:options items="${allRequestTypes}" />  --%>
											</form:select>
										</div>

										<div class="form-group">
											<form:input id="sapId" path="sapId" type="text"
												placeholder="Student ID" class="form-control" />
										</div>

										<div class="form-group">
											<form:select id="tranStatus" path="tranStatus"
												class="form-control">
												<form:option value="">Select Payment Type</form:option>
												<form:option value="Free">Free</form:option>
												<form:option value="Payment Successful">Payment Successful</form:option>
											</form:select>
										</div>
										<div class="form-group">
											<form:input id="id" path="id" type="text"
												placeholder="Request ID" class="form-control" />
										</div>

										<div class="form-group">
											<form:select id="requestStatus" path="requestStatus"
												class="form-control" >
												
												<form:option value="">Select Request Status</form:option>
												<form:option value="Submitted">Submitted (Open)</form:option>
												<form:option value="In Progress">In Progress</form:option>
												<form:option value="Cancelled">Cancelled</form:option>
												<form:option value="Closed">Closed</form:option>
												<form:option value="Payment Pending">Payment Pending</form:option>
												<form:option value="Payment Failed">Payment Failed</form:option>
											</form:select>
										</div>

										<div class="form-group">
											<label class="control-label" for="submit"></label>
											<div class="controls">
												<button id="submit" name="submit"
													class="btn btn-large btn-primary">Search</button>
												<button id="cancel" name="cancel" class="btn btn-danger"
													formaction="${pageContext.request.contextPath}/home" formnovalidate="formnovalidate">Cancel</button>
											</div>
										</div>

									</div>

								</fieldset>
							</form:form>
									</div>
					<c:choose>
					<c:when test="${rowCount > 0}">
	<h2 style="margin-left:50px;">&nbsp;&nbsp;Service Requests<font size="2px"> (${rowCount} Records Found)&nbsp;<a href="downloadSRTeeReport">Download to Excel</a></font></h2>
	<c:set var="defaultReasonOptions" value="${fn:split('Record Purpose,Official Purpose,Scholarship Purpose,Loan Purpose,VISA Purpose',',')}" scope="request"></c:set>
	<div class="clearfix"></div>
	
		<div class="panel-content-wrapper">
		<div class="success-msg-count"></div>
		<div class="error-msg-count"></div>
		<div class="table-responsive">
		<table class="table table-striped table-hover" style="font-size:12px">
						<thead>
							<tr> 
								<th>Sr. No.</th>
								<th>Service Request ID</th>
								<th>Student ID</th>
								<th>Track ID</th>
								<th>Student Name</th>
								<%if(roles.indexOf("Learning Center") != -1 || roles.indexOf("Information Center") != -1){ 	%>
									<%}else{ %>
								<th>Email</th>
								<th>Mobile</th>
								<%} %>
								<th>Service Request Type</th>
								<th>Service Request Status</th>
								<%if(roles.indexOf("Admin") != -1){ %><th>Select</th> <%} %>
								<th>Service Request Cancellation Reason</th>
								<th>Service Request Closed Date</th>
								<th>Expected Closed Date</th>
								<th>Payment Status</th>
								<th>Amount</th>
								<th>Description</th>
								<th>Specified Reason</th>
								<th>Additional Transcript</th>
								<th>Documents</th>
								<th>Mode Of Dispatch</th>
								<th>Collected(Yes/No)</th>
								<th>Preview</th>
								<th>Mail Status</th>
								<th>Created By</th>
								<th>Created Date</th>
								<th>Last Modified By</th>
								<th>Last Modified Date</th>
							</tr>
						</thead>
						<tbody>
						
						<c:forEach var="sr" items="${srList}" varStatus="status">
					        <tr id="serviceRequestTR-<c:out value='${sr.id}'/>">
					            <td><c:out value="${status.count}"/></td>
					            <td id="srID"><c:out value="${sr.id}"/></td>
								<td name='sapidtd' id="studentSapidTD-<c:out value='${sr.id}'/>">
									<c:out value="${sr.sapId}"/>
								</td>
								<td><c:out value="${sr.trackId}"/></td>
								<td><c:out value="${sr.firstName} ${sr.lastName}"/></td>
										<%if(roles.indexOf("Learning Center") != -1 || roles.indexOf("Information Center") != -1){ 	%>
									<%}else{ %>
								<td><c:out value="${sr.emailId}"/></td>
								<td><c:out value="${sr.mobile}"/></td>
									<%} %>
								<td id="srTypeTD-<c:out value='${sr.id}'/>"><c:out value="${sr.serviceRequestType}"/></td>
								
								<td>
									<c:choose>
										<c:when test="${(roles.indexOf('Admin') != -1) && (sr.serviceRequestType !='Change in Contact Details')}">
											<c:set var="allStatus" value="Submitted,In Progress,Closed,Payment Pending,Payment Failed,Cancelled" scope="application" />
											<select  class="form-control selectStatus" statusForId="<c:out value='${sr.id}'/>" style="width: 8rem;" onChange="requestStatusChange(this)" disabled>
											    <c:forEach items="${fn:split(allStatus, ',')}" var="status">
											        <option value="${status}" ${sr.requestStatus == status ? 'selected' : ''}>${status}</option> 
											    </c:forEach> 
											</select> 
											<div class="msg"></div>	 
										</c:when>
										<c:otherwise>
											<c:out value="${sr.requestStatus}"/>
										</c:otherwise>
									</c:choose>
								</td>
								<%if(roles.indexOf("Admin") != -1){ 	%>
								<td>
								<input type="checkbox" class="selectCheckBox" name="checkBox1"  />   
								</td>
								<%} %>
								
								<td id="cancelReasonTD-<c:out value='${sr.id}'/>" width="40">
									<c:choose>
										<c:when test="${roles.indexOf('Admin') != -1 }">
											<a href="#" class="cancelReasonEditable hl-disabled" id="cancelReasonXE-<c:out value='${sr.id}'/>" data-name="cancellationReason" data-type="textarea" 
												data-pk="${sr.id}" data-value="${sr.cancellationReason}" data-title="Enter the Service Request Cancellation Reason"
												>${sr.cancellationReason}
											</a>
										</c:when>
										<c:otherwise>
											<c:out value="${sr.cancellationReason}"/>
										</c:otherwise>
									</c:choose>
								</td>
									
								<td><c:out value="${sr.requestClosedDate}"/></td>
								
							   <td>	
							       
							        <c:set var="CreatedDate" value="${sr.requestClosedDate}"/>
							        <c:set var="serviceRequestType" value="${sr.serviceRequestType}"/>
							        <c:set var="serviceRequestSatus" value="${sr.requestStatus}"/>
							        <%
								        try{
								        	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
									        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
									        Calendar c = Calendar.getInstance();
									        String exceptedDate ="";
									        double diffDays =0;
									        System.out.println("Called-->"+(String)pageContext.getAttribute("CreatedDate")+"---"+(String)pageContext.getAttribute("TatValue")+"------"+(String)pageContext.getAttribute("serviceRequestType"));
									        if((String)pageContext.getAttribute("CreatedDate") !=null  && (String)pageContext.getAttribute("TatValue") !=null)
									        {
									        	c.setTime(dateFormat.parse((String)pageContext.getAttribute("CreatedDate"))); // use SR Created  date.
										        c.add(Calendar.DATE, Integer.parseInt((String)pageContext.getAttribute("TatValue"))); // Adding TAT 
										        exceptedDate = sdf.format(c.getTime());
										        long timediff =Math.abs(dateFormat.parse(exceptedDate).getTime() - new Date().getTime());
									            diffDays = Math.ceil(timediff / (1000 * 3600 * 24)); 
									        }
									        
									        // if service request status is closed,Cancelled,Expired then remove color from excepted date 
									        if("Closed".equalsIgnoreCase((String)pageContext.getAttribute("serviceRequestSatus")) || "Cancelled".equalsIgnoreCase((String)pageContext.getAttribute("serviceRequestSatus")) || "Expired".equalsIgnoreCase((String)pageContext.getAttribute("serviceRequestSatus")))
									        {
									        	diffDays = 0;
									        }
									        pageContext.setAttribute("exceptedDate", exceptedDate);
									        pageContext.setAttribute("diffDays", diffDays);
									        System.out.println((String)pageContext.getAttribute("serviceRequestSatus")); 
								        }catch(Exception e){}
							        	
							        %>
							        <c:if test="${diffDays lt 2 && diffDays ne 0}">
							           <strong style="color: orange;"><c:out value="${exceptedDate}"/></strong>
							        </c:if> 
							        <c:if test="${diffDays gt 2}">
							           <strong style="color: red;"><c:out value="${exceptedDate}"/></strong>
							       </c:if>
							       <c:if test="${diffDays eq 0}">
							           <c:out value="${exceptedDate}"/>
							       </c:if> 
							 </td>	
								
								<td><c:out value="${sr.tranStatus}"/></td>
								<td><c:out value="${sr.respAmount}"/></td>
								<td width="30"><c:out value="${sr.description}"/></td>
								
								<td width="30" name="reasons">
									<c:if test="${fn:containsIgnoreCase(sr.serviceRequestType, 'Issuance of Bonafide')}">
										<c:choose>
											<c:when test="${roles.indexOf('Admin') != -1 }">
												<c:set var="isReasonDefault" value="false"></c:set>
												<c:forEach var="reasonOption" items="${defaultReasonOptions}">
													<c:if test="${reasonOption eq sr.additionalInfo1}">
														<c:set var="isReasonDefault" value="true"></c:set>
														<c:out value="${sr.additionalInfo1}"></c:out>
													</c:if>
												</c:forEach>
												
												<c:if test="${isReasonDefault == false}">
													<a href="#" class="editable" data-name="additionalInfo1" data-type="textarea" 
														data-pk="${sr.id}" data-value="${sr.additionalInfo1}" data-url="updateBonafideIssuanceReason" 
														data-title="Edit the Custom Reason entered by the user">${sr.additionalInfo1}
													</a>
												</c:if>
											</c:when>
											<c:otherwise>
												<c:out value="${sr.additionalInfo1}"></c:out>
											</c:otherwise>
										</c:choose>
									</c:if>
								</td>
								
								<td><c:out value="${sr.noOfCopies}"/></td>
								<td>
									<c:if test="${sr.hasDocuments == 'Y' }">
										<a href="/studentportal/viewSRDocuments?serviceRequestId=${sr.id}" target="_blank">View</a>
									</c:if>
								</td>
								<td><c:out value="${sr.modeOfDispatch}"/></td>
								<%-- <c:if test="${(sr.serviceRequestType eq 'Issuance of Marksheet' || sr.serviceRequestType eq 'Issuance of Final Certificate' || sr.serviceRequestType eq 'Issuance of Bonafide' || sr.serviceRequestType eq 'Issuance Of Transcript') && (sr.issued eq 'N')}"> --%>
								<td>
								<a href="#" class="editable" id="requestStatus" data-type="select" data-pk="${sr.id}" 
								data-source="[{value: 'Y', text: 'Y'},{value: 'N', text: 'N'}]"
								data-url="updateIssuedDocuments" data-title="Select If Document is issued">${sr.issued}</a>
								</td>
								<td>
									<c:if test="${sr.serviceRequestType eq 'Issuance of Bonafide'}">
										<button type="button"
											class="btn btn-small btn-primary viewPdf"
											data-toggle="modal" data-target="#exampleModal">
											View</button> 
											<!--Modal  -->
										<div class="modal fade" id="exampleModal" tabindex="-1"
											role="dialog" aria-labelledby="exampleModalLabel"
											aria-hidden="true">
											<div class="modal-dialog" role="document">
												<div class="modal-content text-center ">
													<div class="modal-body font-weight-normal">
														<h4>TO WHOMSOEVER IT MAY CONCERN</h4>
															<div name="pdfcontent"></div>
													</div>
													<div class="modal-footer">
														<button type="button" class="btn btn-secondary"
															data-dismiss="modal">Close</button>
													</div>
												</div>
											</div>
										</div>
									</c:if>
								</td>
								<td class="text-center">
								<c:set var="tarckingBean" value="${trackingMailStatus[sr.id]}"></c:set>
								<c:out value="${tarckingBean.mailStatus}"/>
								</td>
								<td class="text-center">
								<c:set var="tarckingBean" value="${trackingMailStatus[sr.id]}"></c:set>
								<c:out value="${tarckingBean.createdBy}"/>
								</td>
								<td class="text-center">
								<c:set var="tarckingBean" value="${trackingMailStatus[sr.id]}"></c:set>
								<c:out value="${tarckingBean.createdDate}"/>
								</td>
								<td class="text-center">
								<c:set var="tarckingBean" value="${trackingMailStatus[sr.id]}"></c:set>
								<c:out value="${tarckingBean.lastModifiedBy}"/>
								</td>
								<td class="text-center">
								<c:set var="tarckingBean" value="${trackingMailStatus[sr.id]}"></c:set>
								<c:out value="${tarckingBean.lastModifiedDate}"/>
								</td>
					        </tr>   
					        
					    </c:forEach>
							
						</tbody>
					</table>
				</div>
					<%if(roles.indexOf("Admin") != -1){ 	%>
					<div class="pull-right"> 
						<button name="submit" class="btn btn-large btn-primary submit-btn" class="form-control" disabled>Submit</button>
					</div>
					<%} %>	 
					<br><br>  
				</div>
			<%-- 	<br>
				
				<br> 
				</c:when>
					</c:choose>
									<c:url var="firstUrl" value="searchSRPage?pageNo=1" />
									<c:url var="lastUrl" value="searchSRPage?pageNo=${page.totalPages}" />
									<c:url var="prevUrl" value="searchSRPage?pageNo=${page.currentIndex - 1}" />
									<c:url var="nextUrl" value="searchSRPage?pageNo=${page.currentIndex + 1}" />
				<c:choose>
					<c:when test="${page.totalPages > 1}">
					<div align="center">
						<ul class="pagination">
							<c:choose>
								<c:when test="${page.currentIndex == 1}">
									<li class="disabled"><a href="#">&lt;&lt;</a></li>
									<li class="disabled"><a href="#">&lt;</a></li>
								</c:when>
								<c:otherwise>
									<li><a href="${firstUrl}">&lt;&lt;</a></li>
									<li><a href="${prevUrl}">&lt;</a></li>
								</c:otherwise>
							</c:choose>
							<c:forEach var="i" begin="${page.beginIndex}" end="${page.endIndex}">
								<c:url var="pageUrl" value="searchSRPage?pageNo=${i}" />
								<c:choose>
									<c:when test="${i == page.currentIndex}">
										<li class="active"><a href="${pageUrl}"><c:out value="${i}" /></a></li>
									</c:when>
									<c:otherwise>
										<li><a href="${pageUrl}"><c:out value="${i}" /></a></li>
									</c:otherwise>
								</c:choose>
							</c:forEach>
							<c:choose>
								<c:when test="${page.currentIndex == page.totalPages}">
									<li class="disabled"><a href="#">&gt;</a></li>
									<li class="disabled"><a href="#">&gt;&gt;</a></li>
								</c:when>
								<c:otherwise>
									<li><a href="${nextUrl}">&gt;</a></li>
									<li><a href="${lastUrl}">&gt;&gt;</a></li>
								</c:otherwise>
							</c:choose>
						</ul>
					</div> --%>
				    </c:when>
				</c:choose>
				</div>
			</div>
		</div>
	</div>
	<jsp:include page="../adminCommon/footer.jsp" />

	<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_STUDENTPORTAL_STATIC_RESOURCES')" />resources_2015/js/vendor/bootstrap-editable.js"></script>

	<script>
$(document).ready(function() {
	$('.cancelReasonEditable').editable( {
		mode: 'inline',
		disabled: true,
		validate: function(value) {
			if(value.trim() == '')
				return "Reason for Cancellation cannot be empty!";
		}
	});
});
	
$(function() {
    //toggle `popup` / `inline` mode
    $.fn.editable.defaults.mode = 'inline';     
    
    $('.editable').each(function() {
        $(this).editable({
        	validate: function(value) {
    			if(value.trim() == '')
    				return "Field cannot be empty!";
    		},
        	success: function(response, newValue) {
        		obj = JSON.parse(response);
                if(obj.status == 'error') {
                	return obj.msg; //msg will be shown in editable form
                }
            }
        });
    });

    $('.selectStatus').change(function() {   
		$(this).closest("tr").find(".selectCheckBox").prop("checked","true");     
		
	});

    $(document).on('click', '.submit-btn', function(event) {
    	let selectedCheckboxes = document.querySelectorAll('input[class=selectCheckBox]:checked');
	    let cancelEvent = false;

	    $.each(selectedCheckboxes, function(index, checkbox) {
	    	let statusSelected = $(checkbox).closest("tr").find(".selectStatus option:selected").val();
	    	let reasonText = $(checkbox).closest("tr").find(".cancelReasonEditable").text();
	    	
            if(statusSelected == "Cancelled") {
				if(reasonText.trim() == 'Empty' || reasonText.trim() == '') {
	            	alert("Cancellation Reason cannot be Empty when Status is selected as Cancelled.");
	            	cancelEvent = true;;
				}
            }
		});

		if(cancelEvent === true)
			return false;
        
    	var count = 0 ;
    	$('.selectCheckBox:checkbox:checked').map(function() {
    		count++;
    	});
    	var row = (count>1)?" rows":" row"; 
    	if (!confirm("Are you sure "+count+row+" need to be updated?")) return false;
    		
    	$('.selectCheckBox:checkbox:checked').map(function() {
            var status = $(this).closest("tr").find(".selectStatus option:selected").val();
            var selectBox = $(this).closest("tr").find(".selectStatus");
            $(this).attr('checked', false);    
            var srId = $(this).closest("tr").find(".editable").attr("data-pk");

            let studentSapid = document.getElementById("studentSapidTD-".concat(srId)).innerText;
			let serviceRequestType = document.getElementById("srTypeTD-".concat(srId)).innerText;
            let cancellationReason = document.getElementById("cancelReasonXE-".concat(srId)).textContent.trim();
            
            let reqBody = {	
            		srId: srId,
            		serviceRequestType: serviceRequestType,
            		studentSapid: studentSapid,
                    requestStatus: status,
                    cancellationReason: cancellationReason
                		  }; 
            var element= $(this);
//          console.log("reqBody: ", reqBody);
            
			$.ajax({
				type : 'POST', 

				url : '/studentportal/m/admin/saveRequestStatusAndReason',
				contentType : "application/json",
				data: JSON.stringify(reqBody),
				success : function(response) { 
					$(element).closest("tr").find(".msg").html('<h6 class="alert alert-success alert-dismissible" style="padding: 0px;font-size:13px;"><b>updated <i class="fa-solid fa-circle-check" style="font-size:11px;"></i></b></h6>');      
					$(selectBox).css("border","1px solid green");
					$(selectBox).css("color","green"); 
					
					$(".success-msg-count").html('<h6 class="alert alert-success alert-dismissible" ><b>Success!! '+count+row+' updated.</b></h6>');  
				},   
				error: function(response) {   
					$(element).closest("tr").find(".msg").html('<h6 class="alert alert-danger alert-dismissible" style="padding: 0px;"><b>failed!</b></h6>');  
					console.log("response.responseText"+response.responseText)
					if("Record Not Exist" === response.responseText){
						$(".error-msg-count").html('<h6 class="alert alert-danger alert-dismissible" ><b>Error!! '+count+row+' Failed to closed service request, tracking details not added.</b></h6>');
					}
				} 
			});   
        });
    }); 
});

$('table tbody .viewPdf').click(function(){
	var sapid = jQuery.trim($(this).closest('tr').find("[name = 'sapidtd']")[0].innerHTML);
    var reason = $(this).closest('tr').find("[name='reasons']")[0].textContent;
    var srid = $(this).closest('tr').find("#srID")[0].innerHTML; 
    var status = $(this).closest("tr").find(".selectStatus option:selected").val();
    console.log("status "+status);
    console.log("reason "+ reason);
	console.log(sapid);
	console.log(srid);


	 $.ajax({
			url : "/studentportal/m/showPdf",
			type : "POST",
			dataType : "json",
			data : {	
				"sapid" : sapid,
				"reason" : reason,
				"srid" : srid,
				"status" : status
			},
			success : function(data){
		 	  console.log("success",data.filePath);
		 	  console.log("success",data);
	 	 	  var gender = data.gender =='Male'?'his':'her';
		 	  if(status != 'Closed' && data.programStatus == 'Program Withdrawal' && data.programStatus != null){
		 		  
		 		  if(data.program == 'CBM' || data.program =='DBM'){
		 			 $("#exampleModal [name = 'pdfcontent']").html("<h5> This is to certify that "
			 				 +data.firstName+" "+data.lastName+" was a bonafide student of our "+data.programDuration+" "+data.programDurationUnit+" "
			 				 +data.programName+" program of NMIMS Global Access - School for Continuing Education.He was enrolled for "
			 				 +data.enrollmentMonth+" "+data.enrollmentYear+" batch. Student opted for Exit program after completing the "
			 				 +data.sem+" sem and got "
			 				 +data.programName+ " issued.<br> This letter is issued on "+gender+" "
			 				 +data.additionalInfo1+" request for further studies.</h5>");	
		 		  }
		 		  else{
		 			 $("#exampleModal [name = 'pdfcontent']").html("<h5> This is to certify that "
			 				 +data.firstName+" "+data.lastName+" was a bonafide student of our "+data.programDuration+" "+data.programDurationUnit+" "
			 				 +data.programName+" program of NMIMS Global Access - School for Continuing Education.He was enrolled for "
			 				 +data.enrollmentMonth+" "+data.enrollmentYear+" batch.<br> This letter is issued on "+gender+" "
			 				 +data.additionalInfo1+" request for further studies.</h5>");
		 		  }
		 	  }
		 	   else if(status != 'Closed'){
		 		 $("#exampleModal [name = 'pdfcontent']").html("<h5> This is to certify that "
		 				 +data.firstName+" "+data.lastName+" was a bonafide student of our "+data.programDuration+" "+data.programDurationUnit+" "
		 				 +data.programName+" program of NMIMS Global Access - School for Continuing Education.He was enrolled for "
		 				 +data.enrollmentMonth+" "+data.enrollmentYear+" batch.<br> This letter is issued on "+gender+" "
		 				 +data.additionalInfo1+" request for further studies.</h5>");
		 	  } 
		 	  else{
		 		  console.log("filepath"+data.filePath)
		 		 $("#exampleModal [name = 'pdfcontent']").html("<iframe src="+data.filePath+" width='500' height='400'> </iframe>"); 
		   }
				console.log(data.firstName)
			},
			error : function(data){
				console.log("erroee",data)
			}
			
		   }) 

		  });

	function requestStatusChange(e) {
		let requestStatusSelected = e.options[e.selectedIndex].value;
		let srIdByStatus = e.getAttribute("statusForId");

		let cancellationReasonRowByTRId = document.getElementById("cancelReasonTD-".concat(srIdByStatus));
		let cancellationReasonElement = cancellationReasonRowByTRId.getElementsByTagName("a")[0];

		let cancellationReasonId = cancellationReasonElement.getAttribute("id");
	
		if(requestStatusSelected == "Cancelled") {
			$('#' + cancellationReasonId).editable('option','disabled',false);
			document.getElementById(cancellationReasonId).classList.remove("hl-disabled");
		}
		else {
			$('#' + cancellationReasonId).editable('option','disabled',true);
			document.getElementById(cancellationReasonId).classList.add("hl-disabled");
		}
	}

	
	function reasonValidation() {
	    let selectedCheckboxes = document.querySelectorAll('input[class=selectCheckBox]:checked');

	    selectedCheckboxes.forEach((checkbox) => {
			let statusSelected = $(checkbox).closest("tr").find(".selectStatus option:selected").val(); 
            let reasonElement = $(checkbox).closest("tr").find(".cancelReasonEditable");
            let reasonText = reasonElement.text();
			
			if(statusSelected == "Cancelled"){
				if(reasonText.trim() == 'Empty') {
					alert("Cancellation Reason cannot be Empty when Status is selected as Cancelled.");

					$(".submit-btn").click(function(event) {
						event.clearQueue();
						event.stop();
					});
					return false;
				}
			}
		});
	}

</script>
</body>
</html>