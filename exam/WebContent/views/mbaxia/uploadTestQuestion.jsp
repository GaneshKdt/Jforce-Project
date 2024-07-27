
<!DOCTYPE html>
<html lang="en">

<%@page import="com.nmims.beans.Person"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<jsp:include page="../adminCommon/jscss.jsp">
	<jsp:param value="Internal Assessments" name="title" />
</jsp:include>

<link
	href="https://gitcdn.github.io/bootstrap-toggle/2.2.2/css/bootstrap-toggle.min.css"
	rel="stylesheet">
<script src="https://code.jquery.com/jquery-3.3.1.js"
	integrity="sha256-2Kok7MbOyxpgUVvAk/HJ2jigOSYS2auK4Pfzbm7uH60="
	crossorigin="anonymous"></script>
<script
	src="https://gitcdn.github.io/bootstrap-toggle/2.2.2/js/bootstrap-toggle.min.js"></script>



<body>

	<%@ include file="../adminCommon/header.jsp"%>
	<div class="sz-main-content-wrapper">
		<div class="sz-breadcrumb-wrapper">

			<div class="container-fluid">
				<ul class="sz-breadcrumbs">
					<li><a href="/exam/">Exam</a></li>
					<li><a href="/exam/mbax/ia/a/viewAllTests">Internal Assessment</a></li>
					<li><a href="/exam/mbax/ia/a/viewTestDetails?id=${test.id}">Internal Assessment
							Details</a></li>
					<li><a href="#">Upload Internal Assessment Questions</a></li>

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

		<div class="sz-main-content menu-closed">
			<div class="sz-main-content-inner">
				<jsp:include page="../adminCommon/left-sidebar.jsp">
					<jsp:param value="" name="activeMenu" />
				</jsp:include>


				<div class="sz-content-wrapper examsPage">
					<%@ include file="../adminCommon/adminInfoBar.jsp"%>
					<div class="sz-content">

						<h2 class="red text-capitalize">Upload ChapterWise Test
							Questions</h2>
						<div class="clearfix"></div>
						<div class="panel-content-wrapper" style="min-height: 450px;">
							<%@ include file="../adminCommon/messages.jsp"%>

							<!-- Code For Page Goes in Here Start -->

							<div class="well">
								<c:if test="${test.id != null}">
									<h4>${test.testName}
										&nbsp;&nbsp; <a href="/exam/mbax/ia/a/viewTestDetails?id=${test.id}">
											<i class="fa-solid fa-circle-info" style="font-size: 24px"></i>
										</a>
									</h4>

									<form:form modelAttribute="fileBean" method="post"
										enctype="multipart/form-data" action="uploadTestQuestion">

										<form:hidden path="fileId" value="${test.id}" />
										<div class="panel-body">
											<div class="row">	
											<div class="col-md-6 column">
												<div class="form-group">
													<form:label for="fileData" path="fileData">Select Section</form:label>
													<form:select path="sectionId" id="sectionId" required="required">
													<form:option value="">Select Section</form:option>
														<c:forEach var="sectionbean" items="${sectionList}" varStatus="status">
															<form:option value="${sectionbean.id}" >${sectionbean.sectionName}</form:option>
														</c:forEach>
													</form:select>
												</div>

											</div>
											</div>
											<div class="row">
											<div class="col-md-6 column">
												<div class="form-group">
													<form:label for="fileData" path="fileData">Select file</form:label>
													<form:input path="fileData" type="file" />
												</div>

											</div>
											</div>

											<div class="col-md-12 column">
												<b>Format of Upload: </b><br> Chapter | description |
												marks | type | option1 | option2 | option3 | option4 |
												option5 | option6 | option7 | option8 | correctOption <br>
												<a
													href="/exam/resources_2015/templates/onlineTest/TestQuestionUploadTemplate.xlsx"
													target="_blank">Download a Sample Template</a>

											</div>


										</div>
										<br>
										<div class="row">
											<div class="col-md-6 column">
												<button id="submit" name="submit"
													class="btn btn-large btn-primary"
													formaction="uploadTestQuestion">Upload</button>
											</div>


										</div>
									</form:form>
								</c:if>
							</div>

						</div>

					</div>
				</div>
			</div>
		</div>
	</div>
	<jsp:include page="../adminCommon/footer.jsp" />


</body>
</html>