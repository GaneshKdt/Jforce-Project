<!DOCTYPE html>
<html lang="en">

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<jsp:include page="common/jscssNew.jsp">
	<jsp:param value="Update Settings" name="title" />
</jsp:include>
<body>

	<%@ include file="common/headerDemo.jsp"%>

		<div class="sz-main-content-wrapper">
			<%@ include file="common/breadcrum.jsp"%>
			<div class="sz-main-content menu-closed">
				<div class="sz-main-content-inner">
					<%@ include file="common/left-sidebar.jsp"%>
					<div class="sz-content-wrapper examsPage">
						<%@ include file="common/studentInfoBar.jsp"%>
						<div class="sz-content">
						  <div class="container-fluid p-4">
						  		<div class="alert  alert-dismissible fade show shadow" role="alert" id="alertMessageOnUpdate" style="display:none">
								  <div id="message"></div>
								  <button type="button" class="btn-close"  id="successCloseButton"></button>
								</div>
							<div class="card shadow mt-5 mt-lg-0">
							  <h4 class="card-header  fw-bold">Settings</h4>
							  <div class="card-body">
							    	<div class="card">
									  <div class="card-body">
									   		<div class="row">
									   			<div class="col-9">
									   				<p class="card-text fs-6">Text To Speech</p>
									   			</div>
									   			<div class="col-3">
									   				<div class="form-check form-switch d-flex float-end">
									   				<c:choose>
									   					<c:when test="${studentBean.textToSpeech > 0 }">
									   						<input class="form-check-input fs-4" type="checkbox" role="switch" id="textToSpeech" onchange = "updateSetting('textToSpeech',event)" checked>
									   					</c:when>
									   					<c:otherwise>
									   						<input class="form-check-input fs-4" type="checkbox" role="switch" id="textToSpeech" onchange = "updateSetting('textToSpeech',event)">
									   					</c:otherwise>
									   				</c:choose>
													</div>
									   			</div>
									   		</div>
									  </div>
									</div>
									
									<div class="card mt-2 d-none">
									  <div class="card-body">
									  	 <div class="row">
									   			<div class="col-9">
									   				<p class="card-text fs-6">High Contrast</p>
									   			</div>
									   			<div class="col-3">
									   				<div class="form-check form-switch d-flex float-end" id="formCheck">
													<c:choose>
									   					<c:when test="${studentBean.highContrast > 0 }">
									   						<input class="form-check-input fs-4" type="checkbox" role="switch" id="highContrast" onchange = "updateSetting('highContrast',event)" checked disabled="disabled">
									   					</c:when>
									   					<c:otherwise>
									   						<input class="form-check-input fs-4" type="checkbox" role="switch" id="highContrast" onchange = "updateSetting('highContrast',event)" disabled="disabled" >
									   					</c:otherwise>
									   				</c:choose>												
									   				</div>
									   			</div>
									   		</div>
									  </div>
									</div>
						      </div>
							</div>
							</div>	
						</div>
					</div>
				</div>
			</div>
		</div>
	<jsp:include page="common/footer.jsp" />
<input type="hidden" id="userId" value="${studentBean.sapid }"/>
<script type="text/javascript" src="${pageContext.request.contextPath }/assets/js/studentSettings.js"></script>
</body>
</html>
