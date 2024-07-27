<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!--> 

<%@page import="com.nmims.beans.Person"%>
<%@page import="com.nmims.beans.Page"%>
<%@page import="com.nmims.beans.AssignmentStatusBean"%>
<html class="no-js"> <!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>


<script type="text/javascript">

function validate(){
	var subject = document.getElementById("subject").value;
	if(subject == ""){
		alert("Please enter a subject");
		return false;
	}
}

</script>

<jsp:include page="../jscss.jsp">
<jsp:param value="Mark Copy Cases" name="title" />
</jsp:include>

<body class="inside">

<%@ include file="../header.jsp"%>
	
    <section class="content-container login">
        <div class="container-fluid customTheme">
        <div class="row"><legend>Mark Copy Cases</legend></div>
        <%@ include file="../messages.jsp"%>
		<div class="panel-body clearfix">
		<form:form  action="markCopyCases" method="post" modelAttribute="searchBean">
			<fieldset>
			<div class="col-md-6 column">
				
					<div class="form-group">
						<form:select id="year" path="year" type="text"	placeholder="Year" class="form-control" itemValue="${searchBean.year}">
							<form:option value="">Select Year</form:option>
							<form:options items="${yearList}" />
						</form:select>
					</div>
				
					<div class="form-group">
						<form:select id="month" path="month" type="text" placeholder="Month" class="form-control" itemValue="${searchBean.month}">
							<form:option value="">Select Month</form:option>
							<form:option value="Apr">Apr</form:option>
							<form:option value="Jun">Jun</form:option>
							<form:option value="Sep">Sep</form:option>
							<form:option value="Dec">Dec</form:option>
						</form:select>
					</div>
					
					<div class="form-group">
							<form:select id="subject" path="subject" type="text"	placeholder="Subject" class="form-control" itemValue="${searchBean.subject}">
								<form:option value="">Select Subject</form:option>
								<form:option item="Project" value="Project" />
								<form:option value="Module 4 - Project">Module 4 - Project</form:option>
							</form:select>
					</div>
					
					
					
					</div>		
					
					
					<div class="col-md-6 column">
						<div class="form-group">
							<textarea name="sapIdList" cols="50" rows="7" placeholder="Enter different Student Ids in new lines">${searchBean.sapIdList}</textarea>
						</div>
	
						<div class="form-group">
							<div class="controls">
								<button id="submit" name="submit" class="btn btn-primary btn-sm" formaction="markProjectCopyCases" onclick="return validate();">Mark As Copy</button>
								<button id="submit" name="submit" class="btn btn-sm btn-primary" formaction="searchProjectCopyCases">Search Copy Cases</button>
								<button id="submit" name="submit" class="btn btn-sm btn-primary" formaction="downloadProjectCopyCases">Download Copy Cases</button>
								<button id="cancel" name="cancel" class="btn btn-danger btn-sm" formaction="home" formnovalidate="formnovalidate">Cancel</button>
							</div>
						</div>
					</div>	
			</fieldset>
			</form:form>
			</div>
			
			<c:if test="${rowCount > 0}">
		
			<h2>&nbsp;Copy Cases <font size="2px"> (${rowCount} Records Found)&nbsp; </font></h2>
			<div class="panel-body table-responsive">
				<table class="table table-striped table-hover" style="font-size:12px">
					<thead>
						<tr> 
							<th>Sr. No.</th>
							<th>Exam Year</th>
							<th>Exam Month</th>
							<th>Subject</th>
							<th>Student ID</th>
							
						</tr>
					</thead>
					<tbody>
					
					<c:forEach var="assignmentFile" items="${assignmentFilesList}" varStatus="status">
				        <tr>
				            <td><c:out value="${status.count}"/></td>
							<td><c:out value="${assignmentFile.year}"/></td>
							<td><c:out value="${assignmentFile.month}"/></td>
							<td nowrap="nowrap"><c:out value="${assignmentFile.subject}"/></td>
							<td><c:out value="${assignmentFile.sapId}"/></td>
				        </tr>   
				    </c:forEach>
		
					</tbody>
				</table>
			</div>
			<br>
		
		</c:if>
		
		</div>
	
		

	</section>

	  <jsp:include page="../footer.jsp" />


</body>




</html>
