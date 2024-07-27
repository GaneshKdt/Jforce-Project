<!DOCTYPE html>
<html>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<jsp:include page="jscss.jsp">
	<jsp:param value="Upload Communication Modules" name="title" />
</jsp:include>

<body class="inside">

	<%@ include file="header.jsp"%>

	<section class="content-container login">
		<div class="container-fluid customTheme">
			<%@ include file="messages.jsp"%>
			<div class="panel-body" style="margin-top: 180px;">
				<form:form modelAttribute="filesSet" method="post"	enctype="multipart/form-data" action="uploadCommunicationModule">
					<h2>Upload Communication Modules</h2>
					<div class="form-group col-md-6" style="margin: 20px;">
						<input id="fileData" type="file" style="margin-bottom: 30px;;">
						<button id="submit" name="submit" class="btn btn-primary" style="border-radius: 4px;"
						formaction="uploadCommunicationModule">Upload</button>
					</div>
				</form:form>
			</div>
		</div>
	</section>

	<jsp:include page="footer.jsp" />


</body>
</html>
