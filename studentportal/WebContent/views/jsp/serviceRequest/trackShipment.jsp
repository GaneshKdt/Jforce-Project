
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<html lang="en">

<jsp:include page="../common/jscss.jsp">
	<jsp:param value="Select Service Request" name="title" />
</jsp:include>

<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.2.1/dist/css/bootstrap.min.css" rel="stylesheet">
 <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.2.1/dist/js/bootstrap.bundle.min.js"></script>
<link rel="stylesheet" type="text/css" href="https://use.fontawesome.com/releases/v5.7.2/css/all.css">
<style>

.card-header {
	padding: 0.75rem 1.25rem;
	margin-bottom: 0;
	background-color: #fff;
	border-bottom: 1px solid rgba(0, 0, 0, 0.1)
}

</style> 

<body>

	<%@ include file="../common/header.jsp"%>



	<div class="sz-main-content-wrapper">

		<jsp:include page="../common/breadcrum.jsp">
			<jsp:param value="Student Zone;Student Support;Service Request"
				name="breadcrumItems" />
		</jsp:include>


		<div class="sz-main-content menu-closed">
			<div class="sz-main-content-inner">
			<div id="sticky-sidebar"> 
				<jsp:include page="../common/left-sidebar.jsp">
					<jsp:param value="Service Request" name="activeMenu" />
				</jsp:include>
				</div>

				<div class="sz-content-wrapper examsPage">
					<%@ include file="../common/studentInfoBar.jsp"%>


					<div class="sz-content">

						<h2 class=" text-capitalize py-2">Track Shipment</h2>
						<div class="clearfix"></div>
						<div class="panel-content-wrapper">
						<%@ include file="../common/messages.jsp"%>
						<section class="tracking-card px-2 py-2">
						    <article class="card">
						        <header class="card-header"> My Orders </header> 
						        <div class="card-body">
				              	 	<c:set var="trackUrl" value="${fn:startsWith(massUploadTrackingSRBean.url,'https://') || fn:startsWith(massUploadTrackingSRBean.url,'http://') ? massUploadTrackingSRBean.url : 'https://'+= massUploadTrackingSRBean.url}"></c:set>
						            <article class="card d-flex">
						                <div class="card-body row text-center">
						                    <div class="col-md-4 col-12 my-2 fs-5"> <strong>Tracking Id: </strong><br> ${massUploadTrackingSRBean.trackId} </div>
						                    <div class="col-md-4 col-12 my-2 text-capitalize fs-5"> <strong>Shipping By:</strong> <br> ${massUploadTrackingSRBean.courierName}</div>
						                    <div class="col-md-4 col-12 my-2 fs-5 text-wrap"> <strong>Tracking url:</strong> <br><a class="text-danger fs-5" target="_blank" 
						                    href="${trackUrl}">${massUploadTrackingSRBean.url }</a></div>
						                </div>
						            </article>
						            <div class="mt-5 d-inline-flex">
						                <h5 class="text-capitalize pt-1">Service RequestType : </h5><small class="ps-2 fs-5">${srType}</small>
					                </div>
				             	<button value="${massUploadTrackingSRBean.trackId}" class="btn btn-small btn-outline-danger form-control mt-5" id="trackId__js">copy trackid</button>
					            <a href="${pageContext.request.contextPath	}/student/selectSRForm" class="btn btn-small btn-outline-danger me-2" > <i class="fa fa-chevron-left"></i> Back to serviceRequest</a>
					            <a target="_blank" href="${trackUrl}" class="btn btn-small btn-outline-danger"> Track order <i class="fa fa-chevron-right"></i></a>
						        </div>
					   	 	</article>
						</section>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>


	<jsp:include page="../common/footer.jsp" />
<script>

$(document).ready(function() { 
	
   $('#trackId__js').click(function() { 
       var trackId = $(this).attr('value');
       
      // copying trackId to clipboard 
       navigator.clipboard.writeText(trackId);
   }); 
}); 
	
$(document).on("click","#trackId__js",function() {
    $('#trackId__js').text('copied!');
});
</script>

</body>
</html>