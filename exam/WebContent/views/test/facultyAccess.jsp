
<!DOCTYPE html>
<html lang="en">

<%@page import="com.nmims.beans.Person"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<jsp:include page="../adminCommon/jscss.jsp">
	<jsp:param value="Grant Faculty Edit Rights" name="title" />
</jsp:include>


<link
	href="https://gitcdn.github.io/bootstrap-toggle/2.2.2/css/bootstrap-toggle.min.css"
	rel="stylesheet">
<script src="https://code.jquery.com/jquery-3.3.1.js"
	integrity="sha256-2Kok7MbOyxpgUVvAk/HJ2jigOSYS2auK4Pfzbm7uH60="
	crossorigin="anonymous"></script>
<script
	src="https://gitcdn.github.io/bootstrap-toggle/2.2.2/js/bootstrap-toggle.min.js"></script>

<style>

	.snackbar{
	    background: #5b5a5a;
	    width: 30%;
	    padding: 20px;
	    margin: 25px;
	    text-align: center;
	    border-radius: 4px;
   	 	position: fixed;
    	z-index: 2;
	    left: 50%;
	    transform: translate(-50%, 0);
	    display: none;
	}

</style>

<body>

	<%@ include file="../adminCommon/header.jsp"%>
	<div class="sz-main-content-wrapper">

		<!-- Custom breadcrumbs as requirement is diff. Start -->
		<div class="sz-breadcrumb-wrapper">
			<div class="container-fluid">
				<ul class="sz-breadcrumbs">
					<li><a href="/exam/">Exam</a></li>
					<li><a href="/exam/viewAllTests">Tests</a></li>
					<li><a href="#">Test Details</a></li>

				</ul>
				<ul class="sz-social-icons">
					<li><a href="https://www.facebook.com/NMIMSSCE"
						class="icon-facebook" target="_blank"></a></li>
					<li><a href="https://twitter.com/NMIMS_SCE"
						class="icon-twitter" target="_blank"></a></li>
					<!-- <li><a href="https://plus.google.com/u/0/116325782206816676798/posts" class="icon-google-plus" target="_blank"></a></li> -->

				</ul>
			</div>
		</div>
		<!-- Custom breadcrumbs as requirement is diff. End -->



		<div class="sz-main-content menu-closed">
			<div class="sz-main-content-inner">
				<jsp:include page="../adminCommon/left-sidebar.jsp">
					<jsp:param value="" name="activeMenu" />
				</jsp:include>


				<div class="sz-content-wrapper examsPage">
					<%@ include file="../adminCommon/adminInfoBar.jsp"%>
					<div class="sz-content">

						<div class="panel-content-wrapper" style="min-height: 450px; margin-top: 50px">

							<%@ include file="../adminCommon/messages.jsp"%>

							<div class="well col-sm-12">
								<h2 class="red text-capitalize" style="float: none;">Grant Faculty Edit Rights</h2>
								<h2 style="float: none; text-transform: inherit;">${ test.testName }</h2>
								<br>
								<div>
									<table id="fTable" class="table table-hover table-striped"
										style="font-size: 12px">
										<thead>
											<tr>
												<th>FacultyId</th>
												<th>Faculty Name</th>
												<th>Allocated Answers</th>
												<th style='text-align: center;'>Action</th>
											</tr>
										</thead>
										<tbody>
										
											<c:forEach var="faculty" items="${facultyAndAllocatedAnswer}" >
												<tr id="row-${typeId}">
													<td>${faculty.facultyId}</td>
													<c:set var="currentfacultyId" value="${faculty.facultyId}" />
													<c:set var="teachingfacultyId" value="${test.facultyId}" />
													<c:choose>
														<c:when test="${currentfacultyId == teachingfacultyId }">
															<td>${faculty.facutlyName}(Teaching Faculty)</td>
														</c:when>
														<c:otherwise>
															<td>${faculty.facutlyName}</td>
														</c:otherwise>
													</c:choose>
													<td id="id-${faculty.facultyId}">${faculty.allocatedAnswers}</td>
													<td style='text-align: center;'>
														<button onclick="grantFacultyEditRights(`${test.id}`, `${faculty.facultyId}`)"> Grant</button>	
														<button onclick="revokeFacultyEditRights(`${test.id}`, `${faculty.facultyId}`)"> Revoke</button>		
													</td>
												</tr>
											</c:forEach>
											
										</tbody>
									</table>
								</div>
							</div>

							<div id='snackbar' class='snackbar'>
								<p id='snackbarDetails' style="color: white;"></p>
							</div>	
							
						</div>
					</div>
				</div>

			</div>
		</div>
	</div>


	<jsp:include page="../adminCommon/footer.jsp" />
	<script type="text/javascript">
	
		function showSnackBar(message) {
			// Get the snackbar DIV
			var x = document.getElementById("snackbar");
			console.log("In showSnackBar() got message " + message);
			x.innerHTML = message;

			// Add the "show" class to DIV
			x.className = "show";

			// After 3 seconds, remove the show class from DIV
			setTimeout(function() {
				x.className = x.className.replace("show", "");
			}, 3000);
			console.log("Exiting showSnackBar()");
		}
		
		function grantFacultyEditRights(testId, facultyId){
			
			let canFacultyEditIA = 'Y'
			
			let body = {
				'id' : testId,
				'facultyId' : facultyId,
				'canFacultyEditIA' : canFacultyEditIA,
				'userId' : '${userId}'
			}
			
			triggerAjax(body)
			
		}

		function revokeFacultyEditRights(testId, facultyId){
			
			let canFacultyEditIA = 'N'
			
			let body = {
				'id' : testId,
				'facultyId' : facultyId,
				'canFacultyEditIA' : canFacultyEditIA,
				'userId' : '${userId}'
			}
			
			triggerAjax(body)
			
		}
		
		function triggerAjax(body){

			$.ajax({
				type : "POST",
				url : "/exam/m/saveFacultyEditRights",   
				contentType : "application/json",
				data : JSON.stringify(body),
				success : function(data) {

					if(data.status == 'success'){
					    $('#snackbarDetails').html('Faculty edit rights has been successfully updated.')
			    		$('#snackbar').fadeIn(10)
			    		$('#snackbar').fadeOut(3000)
					}else{
					    $('#snackbarDetails').html('Something went wrong. Please try again.')
			    		$('#snackbar').fadeIn(10)
			    		$('#snackbar').fadeOut(3000)
					}
						
				},
				error : function(e) {
				    $('#snackbarDetails').html('Something went wrong with error code : '+e.status+'. Please try again.')
		    		$('#snackbar').fadeIn(10)
		    		$('#snackbar').fadeOut(3000)
				}
			});
			
		}
		
	</script>
</body>
</html>