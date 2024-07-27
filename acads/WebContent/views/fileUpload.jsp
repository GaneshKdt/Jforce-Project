<!DOCTYPE html>
<html lang="en">

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<jsp:include page="jscss.jsp">
	<jsp:param value="Upload Content Files" name="title" />
</jsp:include>

<head>

<script
	src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.1/jquery.min.js"></script>
<link rel="stylesheet"
	href="https://cdn.datatables.net/1.10.22/css/jquery.dataTables.min.css" />

<script type="text/javascript"
	src="https://code.jquery.com/jquery-3.5.1.js">
	
</script>
<script type="text/javascript"
	src="https://cdn.datatables.net/1.10.22/js/jquery.dataTables.min.js">
	
</script>

</head>
<body class="inside">

	<jsp:include page="header.jsp" />
	<%
		String userId = (String) session.getAttribute("userId_acads");
	%>
	<section class="content-container login">
		<div class="container-fluid customTheme">

			<form:form method="post" enctype="multipart/form-data"
				action="adhoc-upload-file">
				<div class="mx-auto">
					<legend>Select a file to upload on server</legend>
				</div>
				<div class="panel-body">
				<div class="row">
				<%@ include file="messages.jsp"%>	
				<div class="col-md-4 column">
						<div class="form-group" align="left">
						<label>File title :</label>
							<input id="title" type="text" size="27px" name="title"
								placeholder="Name of the Document" required="required">
						</div>
					</div>
					</div>

					<div class="row">
						<div class="col-md-4 column">
							<div class="form-group" align="left">
							<label>Select a file :</label>
								<input id="file" type="file" name="file" required="required">
							</div>
						</div>
					</div>
					<div class="form-group">
						<button id="submit" name="submit"
							class="btn btn-large btn-primary" formaction="adhoc-upload-file">Upload
							& Generate Link</button>
					</div>
				</div>
			</form:form>

			<c:if test="${file_path != null}">
				<legend>Link For Content Files</legend>

				<div class="panel-body">
					<div class="row" style="margin-left: 10px;">
						<div class="form-group" align="left">
							<p id="path" style="color: green; font-size: 30px">${file_path }</p>
							<br>
							<button onclick="copyToClipboard('#path')"
								class="btn btn-large btn-primary">Copy Link</button>
						</div>
					</div>
				</div>
			</c:if>

			<c:if test="${urlList != null}">
				<legend>&nbsp;Previous Uploads</legend>
				<div class="panel-body">
					<div class="col-md-18 column">
						<div class="table-responsive">
							<table id="paginatedTable" class="table table-striped"
								style="font-size: 12px">
								<thead>
									<tr>
										<th>Sr. No.</th>
										<th>Title</th>
										<th>Web URL</th>
										<th>Uploaded By</th>
										<th>Uploaded Date</th>
										<th>Delete</th>
									</tr>
								</thead>
								<tbody>

									<c:forEach var="bean" items="${urlList}" varStatus="status">
										<tr>
											<td><c:out value="${status.count}" /></td>
											<td><c:out value="${bean.title}" /></td>
											<td><c:out value="${bean.webFileurl}" /></td>
											<td><c:out value="${bean.createdBy}" /></td>
											<td><c:out value="${bean.createdDate}" /></td>
											<td><a href="/acads/admin/adhocdeleteFile?id=${ bean.id }" onclick="return confirm('Are you sure you want to delete this document?')">Delete</a>
										</tr>
									</c:forEach>

								</tbody>
							</table>
						</div>
					</div>
				</div>
			</c:if>
		</div>
	</section>
	<jsp:include page="footer.jsp" />
</body>

<script
	src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.1/jquery.min.js"></script>
<link rel="stylesheet"
	href="https://cdn.datatables.net/1.10.22/css/jquery.dataTables.min.css" />

<script type="text/javascript"
	src="https://code.jquery.com/jquery-3.5.1.js">
	
</script>
<script type="text/javascript"
	src="https://cdn.datatables.net/1.10.22/js/jquery.dataTables.min.js">
	
</script>
<script>
	function copyToClipboard(element) {
		var $temp = $("<input>");
		$("body").append($temp);
		$temp.val($(element).text()).select();
		document.execCommand("copy");
		$temp.remove();
	}

	$(document).ready(function() {
		$('#paginatedTable').DataTable({});
	});
</script>
</html>
