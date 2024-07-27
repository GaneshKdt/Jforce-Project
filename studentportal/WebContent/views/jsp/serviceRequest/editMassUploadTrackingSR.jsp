
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<!DOCTYPE html>
<html lang="en">
<jsp:include page="../adminCommon/jscss.jsp">
	<jsp:param value="Upload SR Records" name="title" />
</jsp:include>
<style>
.table input{
border: 0.5px solid #3b3b3b;
color:#000000;
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
						<!-- start Dashboard -->			
					<h2 style="margin-left:10px;">&nbsp;&nbsp;Service Requests Track</h2>
					<div class="clearfix"></div>
				<div class="panel-content-wrapper">
				<div hidden="true" id="error-mssg">
				</div>
			<%@ include file="../adminCommon/messages.jsp"%>
			<div class="success-msg-count"></div>
			<div class="table-responsive">
			<form:form action="updateMassUploadTrackingSR" method="POST" onsubmit = "return (validate());">
			<table class="table table-striped table-hover" style="font-size:12px">
					<thead>
					<tr> 
					    <th>Sr. No.</th>
						<th>Service Request ID</th>
						<th>Track ID</th>
						<th>Courier Name</th>
						<th>URL</th>
					</tr>
					</thead>
					<tbody>
						<tr>
						 <td><c:out value="${rowCount}"/></td>
						 <td><input name="serviceRequestId" class="form-control" type="text" value="${sr.serviceRequestId}" readonly></td>
						 <td ><input name="trackId" class="form-control" type="text" value="${sr.trackId}" required></td>
						 <td>
						 <select name="courierName" class="form-control">
						 <option value="${sr.courierName}">Select Courier Name</option>
						 <c:forEach var="courier" items="${courierName}">
						 <option value="${courier}">${courier}</option>
						 </c:forEach>
						 </select>
						 </td>
						 <td><input name="url" class="form-control" type="text" value="${sr.url}" required></td>
						 </tr>
						</tbody>
					</table>
					<div class="pull-right"> 
						<a href="${pageContext.request.contextPath}/admin/searchTrackingSRForm" class="btn btn-danger button">Cancel</a> 
						<button type="submit" name="submit" class="btn btn-large btn-primary" class="form-control">Submit</button>
						<input type="hidden" name="id" value="${sr.serviceRequestId}"/>
				    </div>
					</form:form>
					</div>
					</div>
					<%-- </c:when>
					</c:choose> --%>
						</div>
						</div>
					<!-- end Dashboard -->
				</div>
				</div>
			</div>
	<jsp:include page="../adminCommon/footer.jsp" />

<script>

function validate(){
	//validate url using regex
	var pattern = /^(https?:\/\/)*[w|W]{3}(\.[a-zA-z0-9-]+)\.([a-zA-z0-9]+){1,7}(\.)*([a-zA-z0-9]+)$/;
	var checkUrl = $("input[name='url']").val();

	  if(!pattern.test(checkUrl)){
		$("#error-mssg").html('<h5 class="alert alert-danger alert-dismissible" ><strong>Incorrect url..</strong><a href="#" class="close" data-dismiss="alert">x</a></h5>');
		$("#error-mssg").prop("hidden",false); 
		return false;
	}   
	  
  //convert url to lower case and remove http or https from prefix
  var lowerCaseUrl = $("input[name='url']").val().toLowerCase();
  var prefix1 = "https://";
  var prefix2 = "http://";
  
  //concatenate 'https://' at the begining of url if not present
  var validUrl = !lowerCaseUrl.startsWith(prefix1) && !lowerCaseUrl.startsWith(prefix2) ? prefix1 + lowerCaseUrl : lowerCaseUrl;
  
  $("input[name='url']").val(validUrl);
  
  	return true;
}

</script>
</body>
</html>