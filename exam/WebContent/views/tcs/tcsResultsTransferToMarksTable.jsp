<!DOCTYPE html>

<html class="no-js">
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<jsp:include page="../jscss.jsp">
	<jsp:param value="Transfer TCS Results To Marks Table" name="title" />
</jsp:include>

<body class="inside">
	<%@ include file="../header.jsp"%>

	<section class="content-container login">
	
	<!-- added to transfer data to online_marks table -->
	<div class="container-fluid customTheme">
	<div class="row"><legend>Transfer TCS Results To Marks Table</legend></div>
		
			<%@ include file="../messages.jsp"%>
	<div class="panel-body clearfix">
	
	<form:form  action="transferTCSResultsToOnlineMarks" method="post" modelAttribute="studentMarks">
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
				<button id="submit" name="submit" class="btn btn-large btn-primary"
				formaction="transferTCSResultsToOnlineMarks">Transfer TCS Data</button>
				<button id="cancel" name="cancel" class="btn btn-danger" formaction="home" formnovalidate="formnovalidate">Cancel</button>
		</div>
 </div>
 <div class="clearfix"></div>
 </fieldset>
</form:form>
	
	</div>
	</div>

	</section>

	<jsp:include page="../footer.jsp" />


</body>
</html>