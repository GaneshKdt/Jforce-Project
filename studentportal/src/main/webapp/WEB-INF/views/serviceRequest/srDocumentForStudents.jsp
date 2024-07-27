<%-- <!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!--> 


<html class="no-js"> <!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<jsp:include page="../jscss.jsp">
<jsp:param value="Service Request Documents"  name="title" />
</jsp:include>


<body class="inside">

<%@ include file="../header.jsp"%>
	
    <section class="content-container login">
        <div class="container-fluid customTheme">
         <div class="row"><legend>Service Request Documents</legend></div>
       
        <%@ include file="../messages.jsp"%>
		<div class="panel-body">
		
			<c:if test="${not empty documents }">
		
			<div class="col-md-18 column">
				
				<div class="table-responsive">
				<table class="table table-striped" style="font-size: 12px">
					<thead>
						<tr>
							<th>Sr. No.</th>
							<th>Document Name</th>
							<th>Download</th>
						</tr>
					</thead>
					<tbody>

						<c:forEach var="document" items="${documents}"
							varStatus="status">
							<tr>
								<td><c:out value="${status.count}" /></td>
								
								<td><c:out value="${document.documentName}" /></td>
								<td><a href="downloadFile?filePath=${document.filePath}"><i class="fa fa-download fa-lg"></i></a></td>
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
	
<jsp:include page="../footer.jsp" />

</body>
</html>
 --%>


<!DOCTYPE html>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>


<html lang="en">




<jsp:include page="../common/jscss.jsp">
	<jsp:param value="Service Request Documents" name="title" />
</jsp:include>



<body>

	<%@ include file="../common/header.jsp"%>



	<div class="sz-main-content-wrapper">

		<%@ include file="../common/breadcrum.jsp"%>


		<div class="sz-main-content menu-closed">
			<div class="sz-main-content-inner">
				<jsp:include page="../common/left-sidebar.jsp">
					<jsp:param value="Service Request" name="activeMenu" />
				</jsp:include>



				<div class="sz-content-wrapper examsPage">
					<%@ include file="../common/studentInfoBar.jsp"%>


					<div class="sz-content">

						<h2 class="red text-capitalize">Service Request Documents</h2>
						<div class="clearfix"></div>
						<div class="panel-content-wrapper">
							<%@ include file="../common/messages.jsp"%>

							<c:if test="${not empty documents }">

								<div class="col-md-18 column">

									<div class="table-responsive">
										<table class="table table-striped" style="font-size: 12px">
											<thead>
												<tr>
													<th>Sr. No.</th>
													<th>Document Name</th>
													<th>Download</th>
												</tr>
											</thead>
											<tbody>

												<c:forEach var="document" items="${documents}"
													varStatus="status">
													<tr>
														<td><c:out value="${status.count}" /></td>

														<td><c:out value="${document.documentName}" /></td>
														<td><a
															href="downloadFile?filePath=${document.filePath}"><i
																class="fa fa-download fa-lg"></i></a></td>
													</tr>
												</c:forEach>

											</tbody>
										</table>
									</div>

								</div>

							</c:if>

						</div>

					</div>
				</div>


			</div>
		</div>
	</div>


	<jsp:include page="../common/footer.jsp" />


</body>
</html>