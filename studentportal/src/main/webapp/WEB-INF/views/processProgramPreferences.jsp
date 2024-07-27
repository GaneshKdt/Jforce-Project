<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!-->

<%@ page contentType="text/html; charset=UTF-8"%>
<html class="no-js">
<!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<%@page import="com.nmims.beans.*"%>
<%-- <%@page import="java.util.ArrayList"%> --%>
<%@page import="java.util.*"%>

<jsp:include page="jscss.jsp">
	<jsp:param value="Upload ProgramPreference File" name="title" />
</jsp:include>

<body class="inside">

	<section class="content-container login">
		<div class="container-fluid customTheme">
			<%-- 	<%@ include file="messages.jsp"%> --%>
			<jsp:include page="common/messages.jsp" />




			<div class="row">
				<legend>Upload Program Preference Files</legend>
			</div>

			<form:form method="post" enctype="multipart/form-data"
				action="processProgramPreferences" modelAttribute="fileBean">
				<div class="panel-body">
					<div class="col-md-6 column">
						<div class="form-group" align="left">
							<label>Select Program Preference File</label> <input
								id="fileData" type="file" name="fileData">
						</div>

						<div class="form-group">
							<button id="submit" name="submit"
								class="btn btn-large btn-primary"
								formaction="processProgramPreferences">Upload</button>
						</div>
					</div>
				</div>
			</form:form>
			<c:choose>
				<c:when test="${rowCount > 0}">
					<h2 style="margin-left: 50px;">
						&nbsp;&nbsp;Program Preferences<font size="2px">
							(${rowCount} Records Found)&nbsp;<a
							href="downloadProgramPreferenceReport">Download to Excel</a>
						</font>
					</h2>
				</c:when>
			</c:choose>
		</div>
	</section>

	<jsp:include page="common/footer.jsp" />

</body>
</html>
