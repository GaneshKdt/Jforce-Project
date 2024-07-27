<%-- <!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!--> 

<%@page import="com.nmims.beans.FacultyBean"%>
<%@page import="com.nmims.beans.Page"%>
<%@page import="java.util.*"%>

<html class="no-js"> <!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<jsp:include page="jscss.jsp">
<jsp:param value="Search Faculty Allocation" name="title" />
</jsp:include>

<body class="inside">

<%@ include file="header.jsp"%>
	
    <section class="content-container login">
        <div class="container-fluid customTheme">
        <div class="row"><legend>Search Faculty Allocation</legend></div>
        <%@ include file="messages.jsp"%>
		
		<form:form  action="searchFacultyAllocation" method="post" modelAttribute="faculty">
			<fieldset>
			<div class="panel-body">
			
			<div class="col-md-6 column">
					<div class="form-group">
						<form:select id="year" path="acadYear" type="text"	placeholder="Year" class="form-control" >
							<form:option value="">Select Academic Year</form:option>
							<form:options items="${yearList}" />
						</form:select>
					</div>
				
					<div class="form-group">
						<form:select id="month" path="acadMonth" type="text" placeholder="Month" class="form-control" >
							<form:option value="">Select Academic Month</form:option>
							<form:options items="${acadMonthList}" />
						</form:select>
					</div>
					
					<div class="form-group">
						<form:select id="year" path="examYear" type="text"	placeholder="Year" class="form-control" >
							<form:option value="">Select Exam Year</form:option>
							<form:options items="${yearList}" />
						</form:select>
					</div>
				
					<div class="form-group">
						<form:select id="month" path="examMonth" type="text" placeholder="Month" class="form-control" >
							<form:option value="">Select Exam Month</form:option>
							<form:options items="${examMonthList}" />
						</form:select>
					</div>
					
					<div class="form-group">
						<form:select id="year" path="roleForAllocation" type="text"	placeholder="Year" class="form-control" >
							<form:option value="">Select Allocated Role</form:option>
							<form:options items="${rolesForAllocationList}" />
						</form:select>
					</div>
					
					<div class="form-group">
					     <form:input path="facultyAllocated" id="facultyAllocated" type="text" placeholder="Faculty ID" class="from-control"/>
					</div>
					
					<div class="form-group">
						<label class="control-label" for="submit"></label>
						<button id="submit" name="submit" class="btn btn-large btn-primary" formaction="searchFacultyAllocation">Search</button>
						<button id="cancel" name="cancel" class="btn btn-danger" formaction="home" formnovalidate="home">Cancel</button>
					</div>
					
			</div>
			
			</div>
			</fieldset>
		</form:form>
		
		 <c:choose>
	<c:when test="${rowCount > 0}">

	<legend>&nbsp;Faculty Allocation <font size="2px"> (${rowCount} Records Found) &nbsp;<a href="downloadFacultyAllocationReport">Download Excel</a> </font></legend>
	<div class="table-responsive">
	<table class="table table-striped" style="font-size:12px">
						<thead>
						<tr>
							<th>Sr. No.</th>
							<th>Acad Year</th>
							<th>Acad Month</th>
							<th>Exam Year</th>
							<th>Exam Month</th>
							<th>Faculty ID</th>
							<th>Role Allocated</th>
							<th>Rating</th>
						</tr>
					</thead>
						<tbody>
						
						<c:forEach var="bean" items="${facultyAllocationList}" varStatus="status">
					        <tr>
					            <td><c:out value="${status.count}" /></td>
					            <td><c:out value="${bean.acadYear}" /></td>
					            <td><c:out value="${bean.acadMonth}" /></td>
					            <td><c:out value="${bean.examYear}" /></td>
					            <td><c:out value="${bean.examMonth}" /></td>
								<td><c:out value="${bean.facultyAllocated}" /></td>
								<td><c:out value="${bean.roleForAllocation}" /></td>
								<td id="counter"><c:out value="${bean.rating}"/><span class="glyphicon glyphicon-plus" id="update"></span></td>
					        </tr>   
					    </c:forEach>
							
							
						</tbody>
					</table>
	</div>
	<br>

	</c:when>
	</c:choose>
	
	<c:url var="firstUrl" value="searchFacultyAllocationPage?pageNo=1" />
	<c:url var="lastUrl" value="searchFacultyAllocationPage?pageNo=${page.totalPages}" />
	<c:url var="prevUrl" value="searchFacultyAllocationPage?pageNo=${page.currentIndex - 1}" />
	<c:url var="nextUrl" value="searchFacultyAllocationPage?pageNo=${page.currentIndex + 1}" />
	
	
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
	            <c:url var="pageUrl" value="searchFacultyAvailabilityPage?pageNo=${i}" />
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
	</div>
	

	</section>

	  <jsp:include page="footer.jsp" />


</body>
<script>

$('#counter').data('count', 0);
$('#update').click(function(){
    $('#counter').html(function(){
        var $this = $(this),
            count = $this.data('count') + 1;

        $this.data('count', count);
        return count;
    });
});

</script>
</html>
 --%>

<!DOCTYPE html>
<html lang="en">


<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<jsp:include page="adminCommon/jscss.jsp">
	<jsp:param value="Welcome to Student Zone" name="title" />
</jsp:include>
<style>
.jumbotron {
	background-color: #fff;
	padding: 0.5px;
}

label {
	font-size: 15px;
}
</style>


<body>

	<%@ include file="adminCommon/header.jsp"%>






	<div class="sz-main-content-wrapper">

		<jsp:include page="adminCommon/breadcrum.jsp">
			<jsp:param value="Academics;Search Faculty Allocation" name="breadcrumItems" />
		</jsp:include>


		<div class="sz-main-content menu-closed">
			<div class="sz-main-content-inner">
				<jsp:include page="adminCommon/left-sidebar.jsp">
					<jsp:param value="" name="activeMenu" />
				</jsp:include>


				<div class="sz-content-wrapper examsPage">
					<%@ include file="adminCommon/adminInfoBar.jsp"%>


					<div class="sz-content">
						<h2 class="red text-capitalize">Search Faculty Allocation</h2>
						<div class="clearfix"></div>
						<div class="panel-content-wrapper" style="min-height: 450px;">
							<%@ include file="adminCommon/messages.jsp"%>
							<form:form action="searchFacultyAllocation" method="post"
								modelAttribute="faculty">
								<fieldset>
									<div class="panel-body">

										<div class="col-md-6 column">
											<div class="form-group">
												<form:select id="year" path="acadYear" type="text"
													placeholder="Year" class="form-control">
													<form:option value="">Select Academic Year</form:option>
													<form:options items="${yearList}" />
												</form:select>
											</div>

											<div class="form-group">
												<form:select id="month" path="acadMonth" type="text"
													placeholder="Month" class="form-control">
													<form:option value="">Select Academic Month</form:option>
													<form:options items="${acadMonthList}" />
												</form:select>
											</div>

											<div class="form-group">
												<form:select id="year" path="examYear" type="text"
													placeholder="Year" class="form-control">
													<form:option value="">Select Exam Year</form:option>
													<form:options items="${yearList}" />
												</form:select>
											</div>

											<div class="form-group">
												<form:select id="month" path="examMonth" type="text"
													placeholder="Month" class="form-control">
													<form:option value="">Select Exam Month</form:option>
													<form:options items="${examMonthList}" />
												</form:select>
											</div>

											<div class="form-group">
												<form:select id="year" path="roleForAllocation" type="text"
													placeholder="Year" class="form-control">
													<form:option value="">Select Allocated Role</form:option>
													<form:options items="${rolesForAllocationList}" />
												</form:select>
											</div>
											
												<div class="form-group">
														<form:input id="facultyAllocated" path="facultyAllocated" type="text" placeholder="Enter Faculty Id" style="width:50%;" class="form-control"/>
												</div>
											

											<div class="form-group">
												<label class="control-label" for="submit"></label>
												<button id="submit" name="submit"
													class="btn btn-large btn-primary"
													formaction="searchFacultyAllocation">Search</button>
												<button id="cancel" name="cancel" class="btn btn-danger"
													formaction="home" formnovalidate="home">Cancel</button>
											</div>

										</div>

									</div>
								</fieldset>
							</form:form>
							<c:choose>
								<c:when test="${rowCount > 0}">

									<legend>
										&nbsp;Faculty Allocation <font size="2px"> (${rowCount}
											Records Found) &nbsp;<a
											href="/acads/admin/downloadFacultyAllocationReport">Download Excel</a>
										</font>
									</legend>
									<div class="table-responsive">
										<table class="table table-striped" style="font-size: 12px">
											<thead>
												<tr>
													<th>Sr. No.</th>
													<th>Faculty ID</th>
													<th>Faculty First Name</th>
													<th>Faculty Last Name</th>
													<th>Role Allocated</th>
													<th>Rating</th>
												</tr>
											</thead>
											<tbody>

												<c:forEach var="facultyBean" items="${facultyAllocationList}"
													varStatus="status">
													<tr>
														<td><c:out value="${status.count}" /></td>
														<td><c:out value="${facultyBean.facultyAllocated}" /></td>
														<td><c:out value="${facultyBean.firstName}" /></td>
														<td><c:out value="${facultyBean.lastName}" /></td>
														<td><c:out value="${facultyBean.roleForAllocation}" /></td>
														<td>
														<a href="#" class="editable" id="saveFacultyRating" data-type="select" data-pk="${facultyBean.id}" 
														data-source="[{value: '1', text: '1'},{value: '2', text: '2'},{value: '3', text: '3'},{value: '4', text: '4'},{value: '5', text: '5'}]"
														data-url="saveFacultyRating" data-title="Rating Value">${facultyBean.rating}</a>
													</td>
													</tr>
												</c:forEach>


											</tbody>
										</table>
									</div>
									<br>

								</c:when>
							</c:choose>

							<c:url var="firstUrl"
								value="/acads/admin/searchFacultyAllocationPage?pageNo=1" />
							<c:url var="lastUrl"
								value="/acads/admin/searchFacultyAllocationPage?pageNo=${page.totalPages}" />
							<c:url var="prevUrl"
								value="/acads/admin/searchFacultyAllocationPage?pageNo=${page.currentIndex - 1}" />
							<c:url var="nextUrl"
								value="/acads/admin/searchFacultyAllocationPage?pageNo=${page.currentIndex + 1}" />


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
											<c:forEach var="i" begin="${page.beginIndex}"
												end="${page.endIndex}">
												<c:url var="pageUrl"
													value="/acads/admin/searchFacultyAvailabilityPage?pageNo=${i}" />
												<c:choose>
													<c:when test="${i == page.currentIndex}">
														<li class="active"><a href="${pageUrl}"><c:out
																	value="${i}" /></a></li>
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

						</div>
					</div>

				</div>

			</div>
		</div>


	</div>
	<jsp:include page="adminCommon/footer.jsp" />
	<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_ACADS_STATIC_RESOURCES')" />resources_2015/js/vendor/bootstrap-editable.js"></script>
<script>
$(function() {
    //toggle `popup` / `inline` mode
    $.fn.editable.defaults.mode = 'inline';     
    
    $('.editable').each(function() {
        $(this).editable({
        	success: function(response, newValue) {
        		obj = JSON.parse(response);
                if(obj.status == 'error') {
                	return obj.msg; //msg will be shown in editable form
                }
            }
        });
    });
    
});
</script>
</body>

</html>