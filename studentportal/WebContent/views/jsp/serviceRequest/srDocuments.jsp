<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!-->


<html class="no-js">
<!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<jsp:include page="../jscss.jsp">
	<jsp:param value="Service Request Documents" name="title" />
</jsp:include>


<body class="inside">

	<%@ include file="../header.jsp"%>

	<section class="content-container login">
		<div class="container-fluid customTheme">
			<div class="row">
				<legend>Service Request Documents</legend>
			</div>

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
											<td>
												<c:choose>
													<c:when test = "${document.documentName=='Student medical proof for special need'}">
														<a href="<spring:eval expression="@propertyConfigurer.getProperty('SR_FILES_S3_PATH')" />${document.filePath}"
															target="_blank"><i class="fa-solid fa-download fa-lg"></i></a> 
													</c:when>
													<c:when test="${document.documentName eq 'Document for Change Father/Mother/Spouse Name'}">
														<a href="<spring:eval expression="@propertyConfigurer.getProperty('SR_DOCS_S3_PATH')" />${document.filePath}" 
															target="_blank"><i class="fa-solid fa-download fa-lg"></i></a>
													</c:when>
													<c:when test = "${document.documentName=='Student Scribe Resume for Scribe for Term End Exam SR'}">
																<a href="<spring:eval expression="@propertyConfigurer.getProperty('SR_FILES_S3_PATH')" />${document.filePath}"><i
																		class="fa-solid fa-download fa-lg"></i>
																</a> 
													</c:when>
													<c:otherwise>
														<a href="admin/downloadFile?filePath=${document.filePath}">
															<i class="fa-solid fa-download fa-lg"></i></a>
													</c:otherwise>
												</c:choose>
											</td>
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
