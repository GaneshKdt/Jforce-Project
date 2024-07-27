
<!DOCTYPE html>
<html lang="en">

<%@page import="com.nmims.beans.Person"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<jsp:include page="../adminCommon/jscss.jsp">
	<jsp:param value="Check Test Questions" name="title" />
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


		<!-- Custom breadcrumbs as requirement is diff. Start -->
		<div class="sz-breadcrumb-wrapper">
			<div class="container-fluid">
				<ul class="sz-breadcrumbs">
					<li><a href="/exam/">Exam</a></li>
					<li><a href="#">Evaluate Test Answers</a></li>

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

						<h2 class="red text-capitalize">Evaluate Test Answers</h2>
						<div class="clearfix"></div>
						<div class="panel-content-wrapper" style="min-height: 450px;">
							<%@ include file="../adminCommon/messages.jsp"%>

							<!-- Code For Page Goes in Here Start -->


							<div class="container">
								
								<div class="panel-group" id="accordion">
									
									<c:forEach var="test" items="${testsDetails}" >
									<div class="panel panel-default">
										<div class="panel-heading">
											<span class="panel-title">
												<a data-toggle="collapse" data-parent="#accordion"
													href="#collapse${test.id}">${test.testName} </a>
											</span>
											<span class="noOfAnswersToCheck">
												${fn:length(testIdAndAnswersToCheckMap[test.id])}
											</span>
										</div>
										<div id="collapse${test.id}" class="panel-collapse collapse">
											<div class="panel-body">
														
											<c:forEach var="question" items="${testIdAndAnswersToCheckMap[test.id]}" >
												<div>
													<h4>${question.question}</h4>
												</div>
											</c:forEach>
											</div>
										</div>
									</div>
									</c:forEach>
								</div>
							</div>


							<!-- Code For Page Goes in Here End -->
						</div>

					</div>
				</div>
			</div>
		</div>
	</div>
	<jsp:include page="../adminCommon/footer.jsp" />

	<script>
		
		</script>

</body>
</html>