<!DOCTYPE html>

<html class="no-js">
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<jsp:include page="../jscss.jsp">
	<jsp:param value="Transfer Mettl Results To Marks Table" name="title" />
</jsp:include>

<body class="inside">
	<%@ include file="../header.jsp"%>

	<section class="content-container login">
	
	<!-- added to transfer data to online_marks table -->
	<div class="container-fluid customTheme">
	<div class="row"><legend>Transfer Mettl Results To Marks Table</legend></div>
		
	<%@ include file="../messages.jsp"%>
	<div class="panel-body clearfix">
	
	<form:form  action="transferMettlResultsToOnlineMarks" method="post" modelAttribute="studentMarks">
			<fieldset>
				<div class="col-md-6 column">
				<div class="form-group">
 					<form:select id="examCode" path="year" type="text" required="required"	placeholder="Exam Year" class="form-control"   itemValue="${studentMarks.year}">
						<form:option value="">(*) Select Exam Year</form:option>
						<form:options items="${yearList}"/>
					</form:select>
				</div>
				<div class="form-group">
 					<form:select id="month" path="month" type="text" required="required"	placeholder="Exam Month" class="form-control"   itemValue="${studentMarks.month}">
						<form:option value="">(*) Select Exam Month</form:option>
						<form:options items="${monthList}"/>
					</form:select>
				</div>
				<%-- <div class="form-group">
					<form:select id="studentType" path="studentType" type="text" required="required" placeholder="Students Type" class="form-control"  itemValue="${sifyMarksBean.studentType}">
						<form:option value="">(*) Select Students Type</form:option>
						<form:options items="${studentTypeList}"/>
					</form:select>
				</div> --%>
				
				<div class="form-group">
				<button id="submit" name="submit" class="btn btn-large btn-primary" formaction="transferMettlResultsToOnlineMarks">Transfer Mettl Data</button>
		</div>
 </div>
 <div class="clearfix"></div>
 </fieldset>
</form:form>
	
	</div>
	</div>

	</section>

	<c:if test="${fn:length(failureResponse) gt 0}">
		<section class="content-container login">
			<div class="container-fluid customTheme">
			<div class="row"><legend>Error Records</legend></div>
			<div class="clearfix"></div>
				<div class="row">
					<table id="error-list-table" class="table table-striped " style="width: 100% !important;">
			 			<thead>
			 				<tr>
								<th>Sapid</th>
								<th>Sify Subject Code</th>
								<th>Program</th>
								<th>Sem</th>
								<th>Subject</th>
								<th>Exam Year</th>
								<th>Exam Month</th>
								<th>Access Key</th>
								<th>Error</th>
			 				</tr>
			 			</thead>
			 			<tbody>
			 				<c:forEach var="bean" items="${failureResponse}">
			 					<tr>
									<td><c:out value="${bean.sapid}"/></td>
									<td><c:out value="${bean.subject}"/></td>
									<td><c:out value="${bean.sem}"/></td>
									<td><c:out value="${bean.subject}"/></td>
									<td><c:out value="${bean.year}"/></td>
									<td><c:out value="${bean.month}"/></td>
									<td><c:out value="${bean.errorMessage}"/></td>
			 					</tr>
			 				</c:forEach>
			 			</tbody>
			 		</table>
				</div>
			</div>
		</section>
	</c:if>
	<jsp:include page="../footer.jsp" />


</body>
</html>
