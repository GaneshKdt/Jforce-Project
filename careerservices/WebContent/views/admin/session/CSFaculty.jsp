<!DOCTYPE html>
<%@page import="com.nmims.beans.FacultyCareerservicesBean"%>
<html class="no-js">
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<jsp:include page="/views/adminCommon/jscss.jsp">
	<jsp:param value=" ${ title } " name="title" />
</jsp:include>


<body class="inside">

	<%@ include file="/views/adminCommon/header.jsp"%>
	<section class="content-container login">

		<div class="container-fluid customTheme">
			<div class="row">
				<legend>${ title }</legend>
			</div>
			<%@ include file="/views/common/messages.jsp"%>

			<form style="padding:15px" class="form px-3" onsubmit="return false;" id="addFaculty">
					<div class="form-group">
						<div class="col-12">
						<label for="facultyId">Select Faculty</label>
					
						</div>
					<input type="text" list="facultyId" name="facultyId"/>
					<datalist id="facultyId">
					
						<c:forEach items="${FacultiesNotInCS}" var="facultyNotInCs">
							<option value="${ facultyNotInCs.facultyId }">${ facultyNotInCs.firstName } ${ facultyNotInCs.middleName } ${ facultyNotInCs.lastName } | ${ facultyNotInCs.facultyId }</option>
						</c:forEach>
					
					</datalist>
					</div>
				<div class="form-group">
					<button class="btn btn-primary" type="submit">Submit</button>
				</div>
			</form>
			
			<div class="clearfix"></div>
			<div class="clearfix"></div>
	
			
			<div class="container-fluid customTheme" style="padding-top: 25px">
				<legend>Update Faculty Details for ${ SpeakerDetails.facultyId }</legend>
			<form style="padding:15px" class="form px-3" onsubmit="return false;" id="updateFaculty">
				<input type="hidden" id="id" name="id" value="${ SpeakerDetails.id }">
				<div class="form-group">
					<div class="col-md-4 mb-3">
						<label for="FeatureId">Faculty Linked In URL</label>
						<input type="text" id="speakerLinkedInProfile" name="speakerLinkedInProfile">
					</div>
					<div class="col-md-4 mb-3">
						<label for="FeatureId">Faculty Twitter URL</label>
						<input type="text" id="speakerTwitterProfile" name="speakerTwitterProfile">
					</div>
					<div class="col-md-4 mb-3">
						<label for="FeatureId">Faculty Facebook URL</label>
						<input type="text" id="speakerFacebookProfile" name="speakerFacebookProfile">
					</div>
				</div>
				<div class="clearfix"></div>
				<div class="form-group">
					<div class="col-md-4 mb-3">
						<button class="btn btn-primary" type="submit">Submit</button>
					</div>
				</div>
			</form>
		</div>
	</div>
	<div class="container-fluid customTheme" style="padding-top: 25px">	
		<legend> ${ tableTitle }</legend>
			<table id="dataTable" class="table table-striped table-bordered" style="width:100%">
				<thead>
					<tr>
						<th>Sr.No</th>
						<th>Faculty Name </th>
						<th>LinkedIn Profile Link</th>
						<th>Facebook Profile Link</th>
						<th>Twitter Profile Link</th>
						<th>Description</th>
						<th>Links</th>
					</tr>
				</thead>
				<tbody>
					<%
					int i = 0;
					%>
					<c:forEach items="${FacultiesInCS}" var="Faculty">
						<%
						i++;
						%>
						<tr>
							<td><%=i%></td>
							<td>${ Faculty.firstName } ${ Faculty.middleName } ${ Faculty.lastName }</td>
							<td>${ Faculty.speakerLinkedInProfile }</td>
							<td>${ Faculty.speakerFacebookProfile }</td>
							<td>${ Faculty.speakerTwitterProfile }</td>
							<td>${ Faculty.facultyDescription }</td>
							<td>
								<a href="updateCSFaculty?id=${ Faculty.facultyId }">
									<i class="fa fa-edit"></i> Configure
								</a>
								<a href="deleteCSFaculty?id=${ Faculty.facultyId }">
									<i class="fa fa-trash"></i> Delete
								</a>
							</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</div>
	</section>
	<jsp:include page="/views/adminCommon/footer.jsp" />
	<script>
		$(document).ready(function () {
			$('#dataTable').DataTable();
		});

		$(document).ready(function () {
			$('#dataTable').DataTable();
		});

		$(document).ready(function () {
	
			<%FacultyCareerservicesBean faculty = (FacultyCareerservicesBean) request.getAttribute("SpeakerDetails");
				if(faculty != null){%>
			<% if(faculty.getSpeakerFacebookProfile() != null && !faculty.getSpeakerFacebookProfile().equals("")){ %>	
				$("#speakerFacebookProfile").val("<%= faculty.getSpeakerFacebookProfile() %>");
			<% } %>
			<% if(faculty.getSpeakerLinkedInProfile() != null && !faculty.getSpeakerLinkedInProfile().equals("")){ %>	
				$("#speakerLinkedInProfile").val("<%= faculty.getSpeakerLinkedInProfile() %>");
			<% } %>
			<% if(faculty.getSpeakerTwitterProfile() != null && !faculty.getSpeakerTwitterProfile().equals("")){ %>	
				$("#speakerTwitterProfile").val("<%= faculty.getSpeakerTwitterProfile() %>");
			<% } %>
					<%
				}
			%>
			$('#addFaculty').submit(function (e) {
				e.preventDefault();
				var ddata = formDataToJSON('#addFaculty');


				$.ajax({
					url: '/m/addCSFaculty',
					type: 'post',
					dataType: 'json',
					contentType: 'application/json',
					data: ddata,
					success: function (result) {
						if (result.status == "success") {
							successMessage("Success");
							window.location.reload();
						} else {
							errorMessage(result.message);
						}
						return false;
					},
					error: function (xhr, resp, text) {
						console.log(xhr, resp, text);
						errorMessage("Error. Please check all fields and retry!");
					}
				});
			});
			
			$('#updateFaculty').submit(function (e) {
				e.preventDefault();
				var ddata = formDataToJSON('#updateFaculty');
				console.log(ddata);

				$.ajax({
					url: '/m/updateCSFaculty',
					type: 'post',
					dataType: 'json',
					contentType: 'application/json',
					data: ddata,
					success: function (result) {
						if (result.status == "success") {
							successMessage("Success");
							window.location.reload();
						} else {
							errorMessage(result.message);
						}
						return false;
					},
					error: function (xhr, resp, text) {
						console.log(xhr, resp, text);
						errorMessage("Error. Please check all fields and retry!");
					}
				});
			});
		});
	</script>
</body>

</html>