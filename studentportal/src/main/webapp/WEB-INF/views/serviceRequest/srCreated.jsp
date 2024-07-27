<%-- <!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!--> 


<html class="no-js"> <!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<jsp:include page="../jscss.jsp">
<jsp:param value="Service Request Summary" name="title" />
</jsp:include>


<body class="inside">

<%@ include file="../header.jsp"%>
	
    <section class="content-container login">
        <div class="container-fluid customTheme">
        <div class="row"><legend>Service Request Summary</legend></div>
	        <form:form  action="addSRForm" method="post" modelAttribute="sr" >
			<fieldset>
	       
	        <%@ include file="../messages.jsp"%>
			<div class="panel-body">
				
				<div class="col-md-12 column">
					
					<div class="form-group">
						<form:label path="serviceRequestType" for="serviceRequestType">Service Request Type:</form:label>
						${sr.serviceRequestType} created successfully. 
					</div>
					
					<div class="form-group">
						<form:label path="serviceRequestType" for="serviceRequestType">Service Request Description:</form:label>
						${sr.description} 
					</div>
					
					<div class="form-group">
						Please quote Service request number <b>${sr.id }</b> for any future communications with Institute.
					</div>
						
					<div class="form-group">
						<label class="control-label" for="submit"></label>
						<div class="controls">
							<button id="submit" name="submit"
								class="btn btn-large btn-primary" formaction="selectSRForm">Create Another Service Request</button>
							<button id="cancel" name="cancel" class="btn btn-danger"
								formaction="home" formnovalidate="formnovalidate">Cancel</button>
						</div>
					</div>
						
				</div>
					
			</div>
			</fieldset>
			</form:form>
		</div>
	
	</section>
	
<jsp:include page="../footer.jsp" />

</body>
</html>
 --%>
<!DOCTYPE html>

<html lang="en">

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>


<jsp:include page="../common/jscss.jsp">
	<jsp:param value="Service Request Summary" name="title" />
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

						<h2 class="red text-capitalize">Service Request Summary</h2>
						<div class="clearfix"></div>
						<div class="panel-content-wrapper">
							<%@ include file="../common/messages.jsp"%>
							<form:form action="addSRForm" method="post" modelAttribute="sr">
								<fieldset>
									<div class="panel-body">
										<div class="col-md-12 column">

											<div class="form-group">
												<form:label path="serviceRequestType"
													for="serviceRequestType">Service Request Type:</form:label>
												<p>${sr.serviceRequestType} created successfully.</p>
											</div>

											<div class="form-group">
												<form:label path="serviceRequestType"
													for="serviceRequestType">Service Request Description:</form:label>
												<c:choose>
													<c:when test="${sr.descriptionList ne null}">
														<p>${sr.descriptionList}</p>
													</c:when>
													<c:otherwise>
														<p>${sr.description}</p>
													</c:otherwise>
												</c:choose>


											</div>

											<div class="form-group">
												<p>
													Please quote Service request number
													<c:choose>
														<c:when test="${sr.srIdList ne null}">
															<b>${sr.srIdList}</b>
														</c:when>
														<c:otherwise>
															<b>${sr.id}</b>
														</c:otherwise>
													</c:choose>
													for any future communications with Institute.
												</p>
											</div>

											<div class="form-group">
												<label class="control-label" for="submit"></label>
												<div class="controls">
													<button id="submit" name="submit"
														class="btn btn-large btn-primary"
														formaction="selectSRForm">OK</button>
													<!-- <button id="cancel" name="cancel" class="btn btn-danger"
													formaction="home" formnovalidate="formnovalidate">Cancel</button> -->
												</div>
											</div>
										</div>
									</div>
								</fieldset>
							</form:form>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	<jsp:include page="../common/footer.jsp" />
</body>
</html>

