<html class="no-js"> <!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<jsp:include page="../jscss.jsp">
<jsp:param value="SOP/Synopsis Submitted and Transaction or Synopsis Score Report Form" name="title" />
</jsp:include>
<link rel="stylesheet" href="//cdn.datatables.net/1.10.20/css/jquery.dataTables.min.css"/>

<body class="inside">

<%@ include file="../header.jsp"%>	
    <section class="content-container login">
        <div class="container-fluid customTheme">
        <div class="row"><legend>SOP/Synopsis Submitted and Transaction or Synopsis Score Report Form</legend></div>
        <%@ include file="../messages.jsp"%>
			<div class="clearfix" style="background-color:white;padding:10px;">
			<form:form  method="POST" modelAttribute="inputBean" enctype="multipart/form-data">
				<div class="panel-body">
					<div class="col-md-6">
						<div class="form-group">
							<label>Month</label>
							<form:select id="month" name="month" path="month" class="form-control" required="required" itemValue="${inputBean.month}">
								<form:option value="">Select Month</form:option>
								<form:options items="${monthList}" />
							</form:select>
						</div>
					</div>
					<div class="col-md-6">
						<div class="form-group">
							<label>Year</label>
							<form:select id="year" name="year" path="year" type="text" placeholder="Acad Year" class="form-control" required="required" itemValue="${inputBean.year}">
								<form:option value="">Select Year</form:option>
								<form:options items="${yearList}" />
							</form:select>
						</div>
					</div>	
				</div>
				<div>
				<button id="SOP" name="submit" class="btn btn-large btn-primary" formaction="searchSOPSubmittedandTransactionList">Generate SOP Submitted and Transaction List</button>
				<button id="Synopsis" name="submit" class="btn btn-large btn-primary" formaction="searchSynopsisSubmittedandTransactionList">Generate Synopsis and Transaction Submitted List</button>
				<button id="SynopsisScoreReport" name="submit" class="btn btn-large btn-primary" formaction="searchSynopsisEvaluatedScoreReport">Generate Synopsis Evaluated Score Report</button>
				</div>
				</form:form>
			<br/>
				<div>
				<c:choose>
					<c:when test="${SOPCount > 0}">
						<h2 style="margin-left:30px;">&nbsp;&nbsp;SOP Submitted List : (${SOPCount} Records Found)      
						<c:if test="${SOPTransactionCount > 0}"> &nbsp;&nbsp;SOP Transaction List : (${SOPTransactionCount} Records Found)</c:if> &nbsp;
						<font size="4px">
						<a href="downloadSOPSubmittedandTransactionExcelReport" style="color:blue">Download Excel Report</a></font>
						</h2>
					</c:when>
				</c:choose>
				</div>
				<div>
				<c:choose>
					<c:when test="${SynopsisCount > 0}">
						<h2 style="margin-left:30px;">&nbsp;&nbsp;Synopsis Submitted List : (${SynopsisCount} Records Found)&nbsp;
						<c:if test="${SynopsisTransactionCount > 0}"> &nbsp;&nbsp;Synopsis Transaction List : (${SynopsisTransactionCount} Records Found)</c:if> &nbsp;
						<font size="4px"><a href="downloadSynopsisSubmittedandTransactionExcelReport" style="color:blue;">Download Excel Report</a></font>
						</h2>
					</c:when>
				</c:choose>
				</div>
				<div>
				<c:choose>
					<c:when test="${SynopsisScoreReportCount > 0}">
						<h2 style="margin-left:30px;">&nbsp;&nbsp;Synopsis Evaluated Score List : (${SynopsisScoreReportCount} Records Found)&nbsp;
						<font size="4px">
						<a href="downloadSynopsisEvaluatedScoreExcelReport" style="color:blue">Download Excel Report</a></font>
						</h2>
					</c:when>
				</c:choose>
				</div>	
			</div>
		</div><br/>
		
		
	</section>
	<jsp:include page="../footer.jsp" />
	<script src="//cdn.datatables.net/1.10.20/js/jquery.dataTables.min.js">
	</script>
</body>
</html>