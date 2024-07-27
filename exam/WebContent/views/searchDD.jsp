<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!--> 

<%@page import="com.nmims.beans.Person"%>
<%@page import="com.nmims.beans.Page"%>
<%@page import="com.nmims.beans.StudentMarksBean"%>
<html class="no-js"> <!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<jsp:include page="jscss.jsp">
<jsp:param value="Search DD Details" name="title" />
</jsp:include>

<script type="text/javascript">
	function isNumberKey(evt){
	    var charCode = (evt.which) ? evt.which : event.keyCode;
	    if (charCode > 31 && (charCode < 48 || charCode > 57)){
	    	return false;
	    }
	        
	    return true;
	}
	
	function submitForm(id, action, url){
		var c = confirm('Are you sure you want to '+action + ' this DD?');
		if(!c){
			return false;
		}
		var reason = document.getElementById(id).value;
		if(reason == '' && action == 'Reject'){
			alert("Please enter reason for rejection.");
			return false;
		}else{
			window.location.href = url + '&reason=' + reason;
		}
	}

</script>

<body class="inside">

<%@ include file="header.jsp"%>
	
    <section class="content-container login">
        <div class="container-fluid customTheme">
        <div class="row"><legend>Search DD Details</legend></div>
        <%@ include file="messages.jsp"%>
		<%
		final String DD_APPROVAL_PENDING = "DD Approval Pending";
		final String DD_APPROVED = "DD Approved";
		final String DD_REJECTED = "DD Rejected";
		
		%>
		<form:form  action="searchDD" method="post" modelAttribute="transaction" id="ddDetails">
			<fieldset>
			<div class="row clearfix">
			
			<div class="col-md-6 column">
					<div class="form-group">
						<form:select id="year" path="year" type="text"	placeholder="Year" class="form-control"  itemValue="${transaction.year}">
							<form:option value="">Select Exam Year</form:option>
							<form:options items="${yearList}" />
						</form:select>
					</div>
				
					<div class="form-group">
						<form:select id="month" path="month" type="text" placeholder="Month" class="form-control" itemValue="${transaction.month}">
							<form:option value="">Select Exam Month</form:option>
							<form:option value="Jun">Jun</form:option>
							<form:option value="Dec">Dec</form:option>
						</form:select>
					</div>
					
					<div class="form-group">
						<form:input id="sapid" path="sapid" type="text" placeholder="Student ID" class="form-control" maxlength="11"
						value="${transaction.sapid}" onkeypress="return isNumberKey(event)" />
					</div>
					
					
			</div>
			
			<div class="col-md-6 column">
										
					<div class="form-group">
						<form:input id="ddno" path="ddno" type="text" placeholder="Demand Draft Number" class="form-control" maxlength="6"
						value="${transaction.ddno}" onkeypress="return isNumberKey(event)" />
					</div>
					
					<div class="form-group">
						<form:input id="bank" path="bank" type="text" placeholder="Bank Name" class="form-control" value="${transaction.bank}" />
					</div>
					
					<div class="form-group">
						<form:select id="tranStatus" path="tranStatus" type="text" placeholder="Status" class="form-control" itemValue="${transaction.tranStatus}">
							<form:option value="">Select DD Status</form:option>
							<form:option value="<%=DD_APPROVAL_PENDING%>"><%=DD_APPROVAL_PENDING%></form:option>
							<form:option value="<%=DD_APPROVED%>"><%=DD_APPROVED%></form:option>
							<form:option value="<%=DD_REJECTED%>"><%=DD_REJECTED%></form:option>
						</form:select>
					</div>
					
					<div class="form-group">
						<label class="control-label" for="submit"></label>
						<button id="submit" name="submit" class="btn btn-large btn-primary" formaction="searchDD">Search</button>
						<button id="reset" type="reset" class="btn btn-danger" type="reset">Reset</button>
						<button id="cancel" name="cancel" class="btn btn-danger" formaction="home" formnovalidate="formnovalidate">Cancel</button>
					</div>
			</div>
			</div>
			</fieldset>
		</form:form>
		
		
	</div>
	
	<c:choose>
	<c:when test="${rowCount > 0}">

	<legend>&nbsp;Exam Centers<font size="2px"> &nbsp; </font></legend>
	<div class="table-responsive">
	<table class="table table-striped table-hover" style="font-size:12px">
						<thead>
						<tr>
							<th>Sr. No.</th>
							<th>Year</th>
							<th>Month</th>
							<th>SAP ID</th>
							<th>First Name</th>
							<th>Last Name</th>
							<th>Email</th>
							<th>Mobile</th>
							<th>Alt. Phone</th>
							<th>DD No</th>
							<th>Bank Name</th>
							<th>DD Date</th>
							<th>Amount</th>
							<th># of Subjects</th>
							<th>Status</th>
							<th>Reason for rejection</th>
							<th>Actions</th>
						</tr>
					</thead>
						<tbody>
						
						<c:forEach var="transaction" items="${ddsList}" varStatus="status">
					        <tr>
					            <td><c:out value="${status.count}" /></td>
					            <td><c:out value="${transaction.year}" /></td>
					            <td><c:out value="${transaction.month}" /></td>
								<td><c:out value="${transaction.sapid}" /></td>
								<td><c:out value="${transaction.firstName}" /></td>
								<td><c:out value="${transaction.lastName}" /></td>
								<td><c:out value="${transaction.emailId}" /></td>
								<td><c:out value="${transaction.mobile}" /></td>
								<td><c:out value="${transaction.altPhone}" /></td>
								<td><c:out value="${transaction.ddno}" /></td>
								<td><c:out value="${transaction.bank}" /></td>
								<td><c:out value="${transaction.ddDate}" /></td>
								<td><c:out value="${transaction.amount}" /></td>
								<td><c:out value="${transaction.subjectCount}" /></td>
								<td><c:out value="${transaction.tranStatus}"/></td>
								<td>
									<c:choose>
									    <c:when test="${transaction.tranStatus ne 'DD Approved' and transaction.tranStatus ne 'DD Rejected'}">
									        <input type="text" name="reason" id="reason${status.count}" class="form-control">
									    </c:when>
									    <c:otherwise>
									        ${transaction.ddReason}
									    </c:otherwise>
									</c:choose>

								</td>
								<td> 
						            <c:url value="approveDD" var="approveUrl">
									  <c:param name="year" value="${transaction.year}" />
									  <c:param name="month" value="${transaction.month}" />
									  <c:param name="sapid" value="${transaction.sapid}" />
									  <c:param name="ddno" value="${transaction.ddno}" />
									  <c:param name="email" value="${transaction.emailId}" />
									  <c:param name="trackId" value="${transaction.trackId}" />
									</c:url>
									<c:url value="rejectDD" var="rejectUrl">
									  <c:param name="year" value="${transaction.year}" />
									  <c:param name="month" value="${transaction.month}" />
									  <c:param name="sapid" value="${transaction.sapid}" />
									  <c:param name="ddno" value="${transaction.ddno}" />
									  <c:param name="email" value="${transaction.emailId}" />
									  <c:param name="trackId" value="${transaction.trackId}" />
									</c:url>
									
									<%//if(roles.indexOf("Exam Admin") != -1 || roles.indexOf("Assignment Admin") != -1){ %>
									
									<c:if test="${transaction.tranStatus ne 'DD Approved' and transaction.tranStatus ne 'DD Rejected'}"> 
									<a  href="#" title="Approve DD" 
									onclick="submitForm('reason${status.count}', 'Approve', '${approveUrl}')" 
									onDblclick="return confirm('Are you sure you want to approve this DD?')"><i class="fa-regular fa-square-check fa-lg"></i></a>
									&nbsp;
									<a href="#" title="Reject DD" 
									onclick="submitForm('reason${status.count}', 'Reject', '${rejectUrl}')"><i class="fa-solid fa-xmark fa-lg"></i></a>
									</c:if>

					            </td>
					            
					            
					        </tr>   
					    </c:forEach>
							
							
						</tbody>
					</table>
	</div>
	

</c:when>
</c:choose>

<c:url var="firstUrl" value="searchExamCenterPage?pageNo=1" />
<c:url var="lastUrl" value="searchExamCenterPage?pageNo=${page.totalPages}" />
<c:url var="prevUrl" value="searchExamCenterPage?pageNo=${page.currentIndex - 1}" />
<c:url var="nextUrl" value="searchExamCenterPage?pageNo=${page.currentIndex + 1}" />


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
            <c:url var="pageUrl" value="searchExamCenterPage?pageNo=${i}" />
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
</div>
</c:when>
</c:choose>


	</section>

	  <jsp:include page="footer.jsp" />
	

</body>
</html>
