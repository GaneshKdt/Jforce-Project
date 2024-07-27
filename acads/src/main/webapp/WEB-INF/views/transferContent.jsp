<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!--> 

<%@page import="com.nmims.beans.PersonAcads"%>
<%@page import="com.nmims.beans.Page"%>

<html class="no-js"> <!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<script type="text/javascript">

function checkAll(){
	$( 'input:checkbox[name=contentToTransfer]' ).prop('checked', true);
	return false;
}

function uncheckAll(){
	$( 'input:checkbox[name=contentToTransfer]' ).prop('checked', false);
	return false;
}
</script>
<jsp:include page="jscss.jsp">
<jsp:param value="Transfer Content" name="title" />
</jsp:include>

<body class="inside">

<%@ include file="header.jsp"%>
	
    <section class="content-container login">
        <div class="container-fluid customTheme">
        <div class="row"><legend>Transfer Content</legend></div>
        <%@ include file="messages.jsp"%>
		
		<form:form  action="searchContentToTransfer" method="post" modelAttribute="searchBean">
			<fieldset>
			<div class="panel-body">
			
			<div class="col-md-6 column">
					<div class="form-group">
						<form:label path="year">Transfer From Academic Year</form:label>
						<form:select id="year" path="year" type="text"	placeholder="Year" class="form-control" required="true" itemValue="${searchBean.year}">
							<form:option value="">Select Academic Year</form:option>
							<form:options items="${yearList}" />
						</form:select>
					</div>
				
					<div class="form-group">
						<form:label path="month">Transfer From Academic Month</form:label>
						<form:select id="month" path="month" type="text" placeholder="Month" class="form-control" required="true" itemValue="${searchBean.month}">
							<form:option value="">Select Academic Month</form:option>
							<form:option value="Jan">Jan</form:option>
							<form:option value="Jul">Jul</form:option>
						</form:select>
					</div>
					
					<!--  <div class="form-group" style="overflow:visible;">
							<form:label path="subject">Subject</form:label>
							<form:select id="subject" path="subject"  class="combobox form-control"  itemValue="${searchBean.subject}">
								<form:option value="">Type OR Select Subject</form:option>
								<form:options items="${subjectList}" />
							</form:select>
					</div>-->
					
					<div class="form-group" style="overflow:visible;margin-top:40px;">
					<form:select id="subjectCodeId" path="subjectCodeId" class="combobox form-control" itemValue="${subjectcodes}"> 
							<form:option value="">Select Subject Code</form:option>
									<c:forEach items="${subjectcodes}" var="element">
									<form:option value="${element.subjectCodeId}">${element.subjectcode} ( ${element.subjectName} )</form:option>
							</c:forEach>
					</form:select>
				</div>
					
			</div>
			
			
			<div class="col-md-6 column">
			
					<div class="form-group">
						<form:label path="toYear">Transfer To Academic Year</form:label>
						<form:select id="toYear" path="toYear" class="form-control" required="true" itemValue="${searchBean.toYear}">
							<form:option value="">Select Academic Year</form:option>
							<form:options items="${yearList}" />
						</form:select>
					</div>
				
					<div class="form-group">
						<form:label path="toMonth">Transfer To Academic Month</form:label>
						<form:select id="toMonth" path="toMonth" class="form-control" required="true" itemValue="${searchBean.toMonth}">
							<form:option value="">Select Academic Month</form:option>
							<form:option value="Jan">Jan</form:option>
							<form:option value="Jul">Jul</form:option>
						</form:select>
					</div>
					
										
					<div class="form-group">
						<label class="control-label" for="submit"></label>
						<button id="submit" name="submit" class="btn btn-large btn-primary" formaction="searchContentToTransfer">Search</button>
						<button id="cancel" name="cancel" class="btn btn-danger" formaction="acadsHome" formnovalidate="formnovalidate">Cancel</button>
					</div>
					
			</div>
			
			</div>
			
		
	<c:if test="${rowCount > 0}">

		<h2>&nbsp;Content List<font size="2px"> (${rowCount} Records Found) &nbsp; </font></h2>
		<div class="panel-body table-responsive" id="results">
		<table class="table table-striped" style="font-size:12px">
				<thead>
				<tr>
					<th>Sr.No.</th>
					<th>(<a onclick="checkAll()">All</a> | <a onclick="uncheckAll()" >None</a>)</th>
					<th>Year</th>
					<th>Month</th>
					<th>Subject</th>
					<th>Name</th>
					<th>Description</th>
					<th>Action</th>
				</tr>
				</thead>
				<tbody>
				
				<c:forEach var="contentFile" items="${contentList}" varStatus="status">
			        <tr>
			            <td><c:out value="${status.count}" /></td>
			            <td><form:checkbox path="contentToTransfer" value="${contentFile.id}" style="width:50px;" /></td>
			            <td><c:out value="${contentFile.year}" /></td>
			            <td><c:out value="${contentFile.month}" /></td>
			            <td><c:out value="${contentFile.subject}" /></td>
			            <td><c:out value="${contentFile.name}" /></td>
			            <td><c:out value="${contentFile.description}" /></td>
			            <td width="10%">
							<c:if test="${not empty contentFile.previewPath}">
							   <a href="#" onClick="window.open('<spring:eval expression="@propertyConfigurer.getProperty('CONTENT_PREVIEW_PATH')" />${contentFile.previewPath}')" />Download</a>
							</c:if>
							<c:if test="${fn:endsWith(contentFile.previewPath, '.pdf') || fn:endsWith(contentFile.previewPath, '.PDF')
							|| fn:endsWith(contentFile.previewPath, '.Pdf')}">
							    / <a href="previewContent?previewPath=${contentFile.previewPath}&name=${contentFile.name}" target="_blank">View</a>
							</c:if>
							
							<c:if test="${not empty contentFile.webFileurl}">
								<c:if test="${contentFile.urlType == 'View' || contentFile.urlType == '' || empty contentFile.urlType	}">
							   		<a href="${contentFile.webFileurl}" target="_blank">View</a>
							   </c:if>
							   <c:if test="${contentFile.urlType == 'Download'}">
							   		<a href="${contentFile.webFileurl}" target="_blank">Download</a>
							   </c:if>
							</c:if>
						</td>
			        </tr>   
			    </c:forEach>
					
				</tbody>
			</table>
		</div>
		
		<div class="form-group">
			<label class="control-label" for="submit"></label>
			<button id="submit" name="submit" class="btn btn-large btn-primary" formaction="transferContent">Transfer</button>
			<button id="cancel" name="cancel" class="btn btn-danger" formaction="acadsHome" formnovalidate="formnovalidate">Cancel</button>
		</div>
					

	</c:if>

	
	</fieldset>
	</form:form>
		
	</div>
	</section>

	  <jsp:include page="footer.jsp" />


</body>
</html>
