
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<!DOCTYPE html>
<html lang="en">
<jsp:include page="../adminCommon/jscss.jsp">
	<jsp:param value="Upload SR Records" name="title" />
</jsp:include>

<style>

div.entry.input-group.upload-input-group {
	display:flex;
	align-items:center;
}

div#fields {
	padding-right:2rem;
	margin-top:1rem;
}

div.col-lg-6.col-md-6.col-12 {
	padding-left:2rem;
}

.btn-upload {  
    padding: 10px 20px;  
    margin-left: 10px;  
}  

a.card-link {
	color:#c72127;
}

.upload-input-group {  
    margin-bottom: 10px;  
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
						<h2 class="red text-capitalize">Upload SR Records</h2>
						<div class="clearfix"></div>
						<!-- start -->
						<div class="panel-content-wrapper" style="min-height: 220px;">
							<%@ include file="../adminCommon/messages.jsp"%>
							
							<form method="POST" action="massUploadTrackingSR" enctype="multipart/form-data" id="js__myform">
							  <div class="mb-3">
								  <div class="row">
									  <div class="col-lg-6 col-md-6 col-12 my-1 form-group">
									  <div class="card-header">  
                    					<div style="padding-top : 0.6rem;"> <strong> Upload in Multi Selection </strong> </div>  
           							 </div> 
                                        <div class="" id="fields">  
		                                    <label class="control-label" for="field1">  
		                                        Upload File  
		                                    </label>  
		                                    <div class="controls">  
                                       	 <div class="entry input-group upload-input-group">  
                                            <input class="form-control" name="file" type="file" required>  
                                       	</div>  
	                                    	</div>  
	                                         <button id="startUpload" class="btn btn-primary py-5">Upload</button>	
	                                	</div> 
                               		</div>
									<div class="col-lg-6 col-md-6 col-12 my-2">
										<!-- second column for  -->
										<div class="card" style="width: 18rem;">
										  <div class="card-body">
										    <h5 class="card-title">Format of Upload:</h5>
										    <h5 class="card-subtitle mb-2 text-muted font-weight-normal">Supports: XLSX</h5>
										    <p class="card-text"></p>
										    <table class="table table-striped">
								  			 <thead>
											    <tr>
											      <th scope="col">serviceRequestId</th>
											      <th scope="col">trackId</th>
											      <th scope="col">courierName</th>
											      <th scope="col">url</th> 
											     </tr>
										      </thead>
										      </table>
										    <a href="https://d3q78eohsdsot3.cloudfront.net/resources_2015/MassuploadTemplate.xlsx" class="card-link link-danger">Download a Sample Template</a>
										  </div>
										</div>
									  </div>
								  </div>
								</div>
							</form>
							</div>
						<!-- end -->
					</div>
						<!-- start Dashboard -->			
					<%-- 	<c:choose>
					 <c:when test="${rowCount > 0}">  --%>
					 <div class="sz-content">
					<h2>&nbsp;&nbsp;Service Requests Track (${rowCount} Records Found)&nbsp;<!-- <a href="downloadSRReport">Download to Excel</a></> --></h2>
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
						<c:forEach var="sr" items="${srList}" varStatus="status">
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
							<a href="javascript:;" class="js__deleteRecord" data-id="${ sr.serviceRequestId }"><i class="fa-solid fa-trash"></i></a>
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
					<%-- </c:when>
					</c:choose> --%>
						</div>
						</div>
					<!-- end Dashboard -->
				</div>
				</div>
	<jsp:include page="../adminCommon/footer.jsp" />

	
	<%-- <script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_STUDENTPORTAL_STATIC_RESOURCES')" />resources_2015/js/vendor/bootstrap-editable.js"></script> --%>
	
<script>
		
</script>
</body>
</html>