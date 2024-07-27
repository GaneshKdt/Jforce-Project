
<!DOCTYPE html>
<html lang="en">

<%@page import="com.nmims.beans.Person"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<jsp:include page="../adminCommon/jscss.jsp">
	<jsp:param value="Question Details" name="title" />
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
					<li><a href="/exam/viewAllTests">Tests</a></li>
					<li><a href="/exam/viewTestDetails?id=${test.id}">Test
							Details</a></li>
					<li><a href="#">Upload Test Questions
							</a></li>
					<li><a href="#">Question Details</a></li>

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

						<h2 class="red text-capitalize">Question Details</h2>
						<div class="clearfix"></div>
						<div class="panel-content-wrapper" style="min-height: 450px;">
							<%@ include file="../adminCommon/messages.jsp"%>

							<!-- Code For Page Goes in Here Start -->


							<div class="row">
								<div class="col-xs-12">
									<div class="form-group">
										<b>Question :</b> ${question.question}
									</div>
								</div>
								<div class="col-xs-12 ">
									<div class="form-group">
										<b>Description : </b> ${question.description}
									</div>
								</div>
								<div class="col-md-3 col-sm-6 col-xs-12 ">
									<div class="form-group">
										<b>Type : </b> ${question.typeInString}
									</div>
								</div>
								<div class="col-md-3 col-sm-6 col-xs-12 ">
									<div class="form-group">
										<b>Marks : </b> ${question.marks}
									</div>
								</div>
								<div class="col-md-3 col-sm-6 col-xs-12 ">
									<div class="form-group">
										<b>Chapter : </b> ${question.chapter}
									</div>
								</div>
							</div>
								<br>

							
									<h4>Options</h4>
										<div class="row">
									<c:forEach var="option" items="${question.optionsList}"
										varStatus="status">
											<div class="col-md-3 col-sm-6 col-xs-12 ">
												<div class="panel-body">
													<b>Option ${status.count} : </b> ${option.optionData} <br>
													IsCorrect : ${option.isCorrect}
												</div>
											</div>
									</c:forEach>
										</div>

								
								<c:if test="${question.type == 3}">
								<div class="well">
									<h4>Upload Case Study Sub Questions</h4>

									<div class="container">
												<form:form modelAttribute="fileBean" method="post"
												enctype="multipart/form-data" action="uploadTestSubQuestion">
												
												<form:hidden path="fileId" value="${question.testId}" />
												<form:hidden path="id" value="${question.id}" /> 
												<div class="panel-body">
													<div class="col-md-6 column">
														<div class="form-group">
															<form:label for="fileData" path="fileData">Select file</form:label>
															<form:input path="fileData" type="file" />
														</div>

													</div>


													<div class="col-md-12 column">
														<b>Format of Upload: </b><br> Chapter | description |
														marks | type | option1 | option2 | option3 | option4 |
														option5 | option6 | option7 | option8 | correctOption <br>
														<a
															href="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/templates/onlineTest/Questions.xlsx"
															target="_blank">Download a Sample Template</a>

													</div>


												</div>
												<br>
												<div class="row">
													<div class="col-md-6 column">
														<button id="submit" name="submit"
															class="btn btn-large btn-primary"
															formaction="uploadTestSubQuestion">Upload</button>
													</div>


												</div>
											</form:form>
									</div>

								</div>
								
								<div class="well">
								<h4>Sub Questions List</h4>
																	
																	
									<div class="container">
									  <div class="panel-group" id="accordion">
									    
									    <c:forEach var="subQuestion" items="${question.subQuestionsList }" varStatus="status">
									    <div class="panel panel-default">
									      <div class="panel-heading">
									        <h4 class="panel-title">
									          <a data-toggle="collapse" data-parent="#accordion" href="#collapse${status.count}">${subQuestion.question}</a>
									        </h4>
									      </div>
									      <div id="collapse${status.count}" class="panel-collapse collapse">
									        <div class="panel-body">
									        
									        <div class="row">
												<div class="col-xs-12">
													<div class="form-group">
														<b>Question :</b> ${subQuestion.question}
													</div>
												</div>
												<div class="col-xs-12 ">
													<div class="form-group">
														<b>Description : </b> ${subQuestion.description}
													</div>
												</div>
												<div class="col-md-3 col-sm-6 col-xs-12 ">
													<div class="form-group">
														<b>Type : </b> ${subQuestion.type}
													</div>
												</div>
												<div class="col-md-3 col-sm-6 col-xs-12 ">
													<div class="form-group">
														<b>Marks : </b> ${subQuestion.marks}
													</div>
												</div>
												<div class="col-md-3 col-sm-6 col-xs-12 ">
													<div class="form-group">
														<b>Chapter : </b> ${subQuestion.chapter}
													</div>
												</div>
											</div>
												<br>
				
											
													<h4>Options</h4>
														<div class="row">
													<c:forEach var="option" items="${subQuestion.optionsList}"
														varStatus="status">
															<div class="col-md-3 col-sm-6 col-xs-12 ">
																<div class="panel-body">
																	<b>Option ${status.count} : </b> ${option.optionData} <br>
																	IsCorrect : ${option.isCorrect}
																</div>
															</div>
													</c:forEach>
														</div>
									        
									        </div>
									      </div>
									    </div>
									    </c:forEach>
									    
									  </div> 
									</div>
									    
									
								</div>
								</c:if>
							<!-- Code For Page Goes in Here End -->
						</div>
					</div>
				</div>

			</div>
		</div>
	</div>


	<jsp:include page="../adminCommon/footer.jsp" />


</body>
</html>