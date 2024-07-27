
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<html>
<head>
<jsp:include page="jscss.jsp">
<jsp:param value="Division Details" name="title" />
</jsp:include>
</head>
<body class="inside">
<%@ include file="header.jsp"%>
 <section class="content-container login mt-2">
<div class="container-fluid customTheme">
			<div class="row">
				<legend>Master Division</legend>
			</div>
			<form:form modelAttribute="bean" method="post" action="divisionFormSubmit">
				<div class="row text-center">
					<div class="col-lg-3 col-md-12 mt-2 column">
						<div class="form-group">
								<label for="acadYear">Year</label>
								<form:select id="acadYear" path="year" type="text"	placeholder="Year" class="form-control" required="required" itemValue="${bean.year}"  >
									<form:option value="">Select Year</form:option>
									<form:options items="${yearList}"/>
								</form:select>
						</div>
					</div>
					<div class="col-lg-3 col-md-12 mt-2 column">
						<div class="form-group">
							<label for="acadMonth">Month</label>
							<form:select id="acadMonth" path="month" type="text" placeholder="Month" class="form-control" required="required" itemValue="${bean.month}" >
								<form:option value="">Select Month</form:option>
								<form:options items="${monthList}" />
							</form:select>
						</div>
					</div>
					<div class="col-lg-3 col-md-12 mt-2 column">
						<div class="form-group">
						<label for="batch">Enter Division Name</label>
							<input name="divisionName" type="text" placeholder="Enter Batch Name">
						</div>
					</div>
				</div>
				<div class="form-group">
					<button id="submit" name="submit" class="btn btn-large btn-primary"
					formaction="divisionFormSubmit">Submit</button>
				</div>
     </form:form>
          <c:if test="${rowCount>0}">
    	<legend>Existing Division Entries</legend>
			
		<div class="clearfix"></div>
			<div class="column">
			<div class="table-responsive">
				<table class="table table-striped table-hover dataTables" style="font-size:12px; width: 100%">
					<thead>
					<tr>
						<th>Sr.No</th>
						<th>Batch Name</th>
						<th>Acad Year</th>
						<th>Acad Month</th>
						<th>Add Students</th>
						<th>Action</th>
					</tr>
					</thead>
					<tbody>
						<c:forEach var="bean" items="${existingDivisionList}" varStatus="status">
							<tr value="${bean.id}~${bean.divisionName}">
								<td><c:out value="${status.count}" /></td>
								<td><c:out value="${bean.divisionName}"></c:out></td>
								<td><c:out value="${bean.year}"></c:out></td>
								<td><c:out value="${bean.month}"></c:out></td>
								<td><a href="addStudentsTodivision?id=${bean.id}&year=${bean.year}"><i class="fa-solid fa-users"></i> Add Students</a></a></td>
								<td></td>
							</tr>
						</c:forEach> 
					</tbody>
				</table>
			</div> 
    		</div>
	    </c:if>
        </div>
     </div>
</section>
<jsp:include page="footer.jsp" />
</body>
</html>