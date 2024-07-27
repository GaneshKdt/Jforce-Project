
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<!DOCTYPE html>
<html lang="en">
<jsp:include page="../adminCommon/jscss.jsp">
	<jsp:param value="Upload SR Records" name="title" />
</jsp:include>
<link rel="stylesheet" type="text/css" href="https://cdn.datatables.net/1.12.1/css/jquery.dataTables.css">
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.2.0/css/all.min.css" integrity="sha512-xh6O/CkQoPOWDdYTDqeRdPCVd1SpvCA9XXcUnZS2FmJNp1coAFzvtCN9BmamE+4aHK8yyUHUSCcJHgXloTyT2A==" crossorigin="anonymous" referrerpolicy="no-referrer" />
<style>

#actionbtn{
	width:60px;
	justify-content:space-around;
	justify-item:center;
	text-align-center;
	display: flex;
}

#actionbtn a .fa-trash{
	color: #d2232a;
}

#actionbtn a .fa-trash:hover{
	bgcolor:#bd422a;
}

#table_id_filter input{
color:#404041;
} 

.sz-content-wrapper {
    padding: 0px 15px 0px 15px;
}

</style>
<body>
	<%@ include file="../adminCommon/header.jsp"%>
	<div class="sz-main-content-wrapper">

		<jsp:include page="../adminCommon/breadcrum.jsp">
			<jsp:param value="Student Portal;Upload SR Records"
				name="breadcrumItems" />
		</jsp:include>

		<div class="sz-main-content menu-closed">
			<div class="sz-main-content-inner">
			<div id="sticky-sidebar"> 
				<jsp:include page="../adminCommon/left-sidebar.jsp">
					<jsp:param value="" name="activeMenu" />
				</jsp:include>
				</div>
				
				<div class="sz-content-wrapper">
					<%@ include file="../adminCommon/adminInfoBar.jsp"%>
					<div class="sz-content">
						<h2 class="red text-capitalize">Search SR Tracking Records</h2>
						<div class="clearfix"></div>
						<!-- start -->
						<div class="panel-content-wrapper" style="min-height: 450px;">
							<%@ include file="../adminCommon/messages.jsp"%>
							
			<form:form  method="post" modelAttribute="searchBean" action="searchTrackingSR" onsubmit = "return (validate());">
				<fieldset>
					<div class="panel-body">
						<div class="col-md-6 column">
						
							<div class="form-group">
								<form:label path="fromDate">From</form:label>
									<form:input id="date" path="fromDate" type="date" placeholder="From Date" 
										class="form-control"  value="${searchBean.fromDate}"/>
										
										<form:label path="toDate">To</form:label>
									<form:input id="date" path="toDate" type="date" placeholder="TO Date" 
										class="form-control"  value="${searchBean.toDate}"/>
							</div>
							
							<div class="form-group">
								<form:input id="srId" path="serviceRequestId" type="number" onkeydown="return event.keyCode !== 69" 
								placeholder="SR ID" class="form-control" value="${searchBean.serviceRequestId}"/>
							</div>
							
							<div class="form-group">
								<form:select id="srType" path="serviceRequestType" type="text" placeholder="SR Name" class="form-control" 
								itemValue="${searchBean.serviceRequestType}">
									<form:option value="">Select SR Name</form:option>
									<form:options items="${srTypeList}" />
								</form:select>
							</div>
							
							<div class="form-group">
								<form:select id="courierName" path="courierName" type="text" placeholder="Courier Name" class="form-control" 
								itemValue="${searchBean.courierName}">
									<form:option value="">Select Courier Name</form:option>
									<form:options items="${courierList}" />
								</form:select>
							</div>
							
							<div class="form-group">
								<label class="control-label" for="submit"></label>
								<button type="submit" id="search" name="submit" class="btn btn-large btn-primary">Search</button>
								<button id="cancel" name="cancel" class="btn btn-danger" formaction="${pageContext.request.contextPath}/home" 
								formnovalidate="formnovalidate">Cancel</button>
							</div>
							
						</div>
					</div>
				</fieldset>
		   </form:form>
			</div>
			<!-- end -->
			</div>
			<!-- start Dashboard -->			
			<c:choose>
		 	<c:when test="${rowCount > 0}"> 
		 	<div class="sz-content" style="margin-top : -80px">
			<h2>&nbsp;&nbsp;Service Requests Track (${rowCount} Records Found)&nbsp;<a href="downloadTrackingReport">Download to Excel</a></h2>
			<div class="clearfix"></div>
			<div class="panel-content-wrapper">
			<div class="success-msg-count"></div>
			<div class="table-responsive">
			<table id="table_id" class="table table-striped table-hover display" style="-size:12px">
			<thead>
			<tr> 
			    <th>Sr. No.</th>
				<th>Service Request ID</th>
				<th>Track ID</th>
				<th>Courier Name</th>
				<th>URL</th>
				<th>Action</th>
			</tr>
			</thead>
			<tbody>
				<c:forEach var="sr" items="${searchTrackingList}" varStatus="status">
				<tr>
				 <td><c:out value="${status.count}"/></td>
				 <td class = 'track_id_mass'><c:out value="${sr.serviceRequestId}"/></td>
				 <td ><c:out value="${sr.trackId}"/></td>
				 <td><c:out value="${sr.courierName}"/></td>
				 <td><c:out value="${sr.url}"/></td>
				 <td id="actionbtn"> 
				 	<form:form id="form1_${ sr.serviceRequestId }" method="POST" action="editMassUploadTrackingSR">
					<a  href="javascript:;"  class="js__editRecord" data-id="${ sr.serviceRequestId }"><i class="fa fa-pencil-square-o fa-lg"></i></a>
					 <input type="hidden" name="srId" value="${sr.serviceRequestId}"/>
					</form:form>
					<form:form id="form2_${ sr.serviceRequestId }" method="POST" action="deleteMassUploadTrackingSR">
					<a href="javascript:;" class="js__deleteRecord" data-id="${ sr.serviceRequestId }"><i class="fa fa-trash fa-lg"></i></a>
					 <input type="hidden" name="srId" value="${sr.serviceRequestId}"/>
					</form:form>
				 </td>
				 </tr>
				</c:forEach>
				</tbody>
			</table>
			</div>
			</div>
			</div>
			</c:when>
			</c:choose>
				</div>
				</div>
			<!-- end Dashboard -->
		</div>
		</div>
				
	<jsp:include page="../adminCommon/footer.jsp" />

	<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_STUDENTPORTAL_STATIC_RESOURCES')" />resources_2015/js/vendor/bootstrap-editable.js"></script>
	<script type=”text/javascript” src=”https://cdn.datatables.net/1.10.22/js/jquery.dataTables.min.js”></script>
	<script type="text/javascript" charset="utf8" src="https://cdn.datatables.net/1.12.1/js/jquery.dataTables.js"></script>
	
<script>
	
function validate(){
	var srIdLength = $('#srId').val().length;
	if(srIdLength > 9 ){
		alert("SR ID should be less than 10 digit");
		return false;
	}
	return true;
}

$(document).ready(function(){
	
	$(document).on('click','.js__editRecord',function(){
		if(confirm("Do you want to edit")){
			let srId = $(this).attr('data-id');
			$('#form1_' + srId).submit();
		}
	});
	
	$(document).on('click','.js__deleteRecord',function(){
		if(confirm("Do you want to delete")){
			let srId = $(this).attr('data-id');
			$('#form2_' + srId).submit();
		}
	});
	
});

$(document).ready( function () {
    $('#table_id').DataTable();
} );

</script>
</body>
</html>