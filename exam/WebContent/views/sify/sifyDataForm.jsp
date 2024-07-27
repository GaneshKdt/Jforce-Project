<!DOCTYPE html>

<html class="no-js">
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<jsp:include page="../jscss.jsp">
	<jsp:param value="Sify Data" name="title" />
</jsp:include>

<body class="inside">
	<%@ include file="../header.jsp"%>

	<section class="content-container login">
		<div class="container-fluid customTheme">

			<div class="row"><legend>Pull Sify Results</legend></div>
			
			<%@ include file="../messages.jsp"%>

			<div class="panel-body clearfix">
			<form:form  action="readSifyMarksFromAPI" method="post" modelAttribute="sifyMarksBean">
			<fieldset>
				<div class="col-md-6 column">
				<div class="form-group">
 					<form:select id="examCode" path="examCode" type="text" required="required"	placeholder="Exam Year" class="form-control"   itemValue="${sifyMarksBean.examCode}">
						<form:option value="">(*) Select Exam Year</form:option>
						<form:options items="${yearList}"/>
					</form:select>
				</div>
				<div class="form-group">
					<form:select id="month" path="month" type="text" required="required" placeholder="Exam Month" class="form-control"  itemValue="${sifyMarksBean.month}">
	 					<form:option value="">(*) Select Exam Month</form:option>
						<form:options items="${monthList}"/>
					</form:select>
				</div>
				<div class="form-group">
					<form:select id="studentType" path="studentType" type="text" required="required" placeholder="Students Type" class="form-control"  itemValue="${sifyMarksBean.studentType}">
						<form:option value="">(*) Select Students Type</form:option>
						<form:options items="${studentTypeList}"/>
					</form:select>
				</div>
				<div class="form-group">
				<button id="submit" name="submit" class="btn btn-large btn-primary"
				formaction="readSifyMarksFromAPI">Generate Sify Data</button>
				<c:if test="${rowCount > 0}">		
					<button id="submit" name="submit" class="btn btn-large btn-primary"
					formaction="summaryReport">Generate Summary Report</button>
				</c:if>	
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
