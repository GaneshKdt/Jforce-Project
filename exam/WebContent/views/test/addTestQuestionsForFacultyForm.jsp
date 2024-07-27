<!DOCTYPE html>
<html lang="en">

<%@page import="com.nmims.beans.Person"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
 <%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<jsp:include page="../adminCommon/jscss.jsp">
	<jsp:param value="Create Online Test" name="title" />
</jsp:include>

<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/chosen/1.5.1/chosen.min.css">

<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.9.1/jquery.js"></script>

<script src="https://cdnjs.cloudflare.com/ajax/libs/chosen/1.5.1/chosen.jquery.min.js"></script>

<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/test/css/style.css">
<!-- <link rel="stylesheet" href="assets/test/css/chosen.min.css" > -->
<!-- <script src="assets/test/js/chosen.jquery.min.js"></script> -->

<link href="https://gitcdn.github.io/bootstrap-toggle/2.2.2/css/bootstrap-toggle.min.css" rel="stylesheet">
<script src="https://code.jquery.com/jquery-3.3.1.js" integrity="sha256-2Kok7MbOyxpgUVvAk/HJ2jigOSYS2auK4Pfzbm7uH60="
 crossorigin="anonymous"></script>
<script src="https://gitcdn.github.io/bootstrap-toggle/2.2.2/js/bootstrap-toggle.min.js"></script>


<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/js/ckeditor/ckeditor_responsive_table/plugin.css">
 
<!-- 

 Froala wysiwyg editor CSS 
<link
	href="<c:url value="/resources_2015/css/froala/froala_editor.min.css" />"
	rel="stylesheet">
<link
	href="<c:url value="/resources_2015/css/froala/froala_style.min.css" />"
	rel="stylesheet">
<link
	href="<c:url value="/resources_2015/css/froala/froala_content.min.css" />"
	rel="stylesheet">

<link
	href="<c:url value="/resources_2015/css/froala/themes/dark.min.css" />"
	rel="stylesheet">
<link
	href="<c:url value="/resources_2015/css/froala/themes/grey.min.css" />"
	rel="stylesheet">
<link
	href="<c:url value="/resources_2015/css/froala/themes/red.min.css" />"
	rel="stylesheet">
<link
	href="<c:url value="/resources_2015/css/froala/themes/royal.min.css" />"
	rel="stylesheet">
<link
	href="<c:url value="/resources_2015/css/froala/themes/blue.min.css" />"
	rel="stylesheet">
 -->

<style>
.disabledLink {
    pointer-events: none;
    color: grey;
}
</style>

<body>

	<%@ include file="../adminCommon/header.jsp" %>
	<div class="sz-main-content-wrapper">
		 <c:choose>
               <c:when test="${fn:indexOf(user.roles, 'Faculty') > -1}">
               			<!-- breadcrums as per add/edit test start -->

			<div class="sz-breadcrumb-wrapper">
				<div class="container-fluid">
					<ul class="sz-breadcrumbs">
						<li><a href="/exam/">Exam</a></li>
						<li><a href="/exam/viewTestsForFaculty">Tests</a></li>
			        	<li><a href="/exam/addTestQuestionsForFacultyForm?id=${test.id}">Manage Questions</a></li>
			        	
					</ul>
					<ul class="sz-social-icons">
						<li><a href="https://www.facebook.com/NMIMSSCE" class="icon-facebook" target="_blank"></a></li>
						<li><a href="https://twitter.com/NMIMS_SCE" class="icon-twitter" target="_blank"></a></li>
						<!-- <li><a href="https://plus.google.com/u/0/116325782206816676798/posts" class="icon-google-plus" target="_blank"></a></li> -->

					</ul>
				</div>
			</div>
		<!-- breadcrums as per add/edit test end -->
				</c:when>
               <c:otherwise>
               		
        <!-- Custom breadcrumbs as requirement is diff. Start -->
			<div class="sz-breadcrumb-wrapper">
			    <div class="container-fluid">
			        <ul class="sz-breadcrumbs">
			        		<li><a href="/exam/">Exam</a></li>
			        		<li><a href="/exam/viewAllTests">Tests</a></li>
			        		<li><a href="/exam/viewTestDetails?id=${test.id}">Test Details</a></li>
			        		<li><a href="#">Manage Questions</a></li>
			        	
			        </ul>
			        <ul class="sz-social-icons">
			            <li><a href="https://www.facebook.com/NMIMSSCE" class="icon-facebook" target="_blank"></a></li>
			            <li><a href="https://twitter.com/NMIMS_SCE" class="icon-twitter" target="_blank"></a></li>
			            <!-- <li><a href="https://plus.google.com/u/0/116325782206816676798/posts" class="icon-google-plus" target="_blank"></a></li> -->
						
			        </ul>
			    </div>
			</div>
			<!-- Custom breadcrumbs as requirement is diff. End -->
               </c:otherwise>
              </c:choose>
		


		<div class="sz-main-content menu-closed">
			<div class="sz-main-content-inner">
				<jsp:include page="../adminCommon/left-sidebar.jsp">
					<jsp:param value="" name="activeMenu" />
				</jsp:include>


				<div class="sz-content-wrapper examsPage">
					<%@ include file="../adminCommon/adminInfoBar.jsp" %>
					<div class="sz-content">

							<h2 class="red text-capitalize">Add / Edit Test Questions </h2>
						


						<div class="clearfix"></div>
						<div class="panel-content-wrapper" style="min-height:450px;">
							<%@ include file="../adminCommon/messages.jsp" %>
		
							<!-- Code For Page Goes in Here Start -->


							<!-- question-form START -->
							<div class="add-question-form" hidden="hidden">
								<form class="form-horizontal" action="addQuestionController" method="POST" enctype="multipart/form-data">
									
									<!-- threshold start -->
									
									
									<div class="form-group">
										<label for="copyCaseThreshold" class="col-xs-3 col-md-1 col-sm-1 control-label">CopyCase Threshold</label>
										<div class="col-sm-10 col-xs-12">
											<select name="copyCaseThreshold" id="copyCaseThreshold" onchange="refresh();">
												<c:forEach var="j" begin="0" end="100">  
												   
												   <c:if test="${j != 40 }">
												   	<option value="<c:out value="${j}"/>">
												   		<c:out value="${j}"/>
												   	</option>
												   </c:if>
												   <c:if test="${j == 40 }">
												   	<option 
												   	  value="<c:out value="${j}"/>"
												   	  selected="selected">
												   		<c:out value="${j}"/>
												   	</option>
												   </c:if>
												   
												   
												</c:forEach>  
												
												
											</select>
										</div>
										
									</div>
									
									<!-- threshold end -->
										
									<div class="form-group">
										<label for="question-type" class="col-xs-3 col-md-1 col-sm-1 control-label">Question Type</label>
										<div class="col-sm-10 col-xs-12">
											<select name="question-type" id="question-type" onchange="refresh();">
												<option value="SINGLESELECT" questiontypeval="1" selected="selected">Single-select</option>
												<option value="MULTISELECT"	questiontypeval="2">Multi-select</option>
												<option value="CASESTUDY"	questiontypeval="3" >Case-study</option>
												<option value="DISCRIPTIVE"	questiontypeval="4">Descriptive</option>
												<option value="TRUEFALSE"	questiontypeval="5" >True / False</option>
												<option value="IMAGEUPLOAD"	questiontypeval="6" >Image-Upload</option>
												<option value="VIDEO"	questiontypeval="7">Video</option>
												<option value="LINK"	questiontypeval="8">Link</option>
											</select>
										</div>
										
									</div>

									<div class="form-group">
										<label for="section" class="col-xs-3 col-md-1 col-sm-1 control-label">Section</label>
										<div class="col-sm-10 col-xs-12">
											<select name="section" id="sectionId" >
											<option value="">Select Section</option>
												<c:forEach var="sectionbean" items="${sectionList}" varStatus="status">
														<option value="${sectionbean.id}" >${sectionbean.sectionName}</option>
												</c:forEach>
											</select>
										</div>
										
									</div>

									<div class="form-group">
										<label for="marks" class="col-xs-3 col-md-1 col-sm-1 control-label">Marks</label>
										<div class="col-sm-3 col-xs-5">
											<select name="marks" class="marks" >
												<option value="0.5">0.5</option>
												<option value="1">1</option>
												<c:forEach var="m" begin="2" end="${test.maxScore}">  
													<option value="${m-0.5}">${m-0.5}</option> 
													<option value="${m}">${m}</option>
												</c:forEach>
												<!-- 
												<option value="2">2</option>
												<option value="3">3</option>
												<option value="4">4</option>
												<option value="5">5</option>
												<option value="6">6</option>
												<option value="7">7</option>
												<option value="8">8</option>
												<option value="9">9</option>
												<option value="10">10</option>
												 -->		
											</select>
										</div>
										
									</div>

									<!-- SINGLESELECT Start-->
									<div id="SINGLESELECT" class="subform">
										<div class="form-group">
											<label for="question-description" class="col-xs-3 col-md-1 col-sm-1 control-label">Question</label>
											<div class="col-md-5 col-sm-10 col-xs-12">
												<textarea class="form-control SINGLESELECT-desc" id="description-singlemulti" rows="2" name="question-description"
												 placeholder="Enter question"></textarea>
											</div>
										</div>
										<div id="added-answers"></div>
										<div id="add-answer" class="form-group">
											<label for="answer" class="col-xs-3 col-md-1 col-sm-1 control-label">Option</label>
											<div class="col-md-5 col-sm-10 col-xs-12">
												<div class="input-group">
													<input class="form-control answer-description" type="text" id="answer-single" placeholder="Enter option"
													 maxlength="1000" />
													<div class="input-group-addon"><span title="Add Answer" id="add-answer-btn" onclick="addSingleMultiAnswer('single');">ADD</span></div>
												</div>
											</div>
										</div>

									</div>
									<!-- .SINGLESELECT End-->

									<!-- MULTISELECT Start-->
									<div id="MULTISELECT" class="subform">
										<div class="form-group">
											<label for="question-description" class="col-xs-3 col-md-1 col-sm-1 control-label">Question</label>
											<div class="col-md-5 col-sm-10 col-xs-12">
												<textarea class="form-control MULTISELECT-desc" id="description-multi" rows="2" name="question-description"
												 placeholder="Enter question"></textarea>
											</div>
										</div>
										<div id="added-answers-multi">
										</div>
										<div id="add-answer" class="form-group">
											<label for="answer" class="col-xs-3 col-md-1 col-sm-1 control-label">Option</label>
											<div class="col-md-5 col-sm-10 col-xs-12">
												<div class="input-group">
													<input class="form-control answer-description" type="text" id="answer-multi" placeholder="Enter option"
													 maxlength="1000" />
													<div class="input-group-addon"><span title="Add Answer" id="add-multi-answer-btn" onclick="addSingleMultiAnswer('multi');">ADD</span></div>
												</div>
											</div>
										</div>

									</div>
									<!-- .MULTISELECT End-->

									<!-- .CASESTUDY START -->
									<div id="CASESTUDY" class="subform">
										<div class="form-group">
											<label for="passage-description" class="col-xs-3 col-md-1 col-sm-1 control-label">Header Question</label>
											<div class="col-md-5 col-sm-10 col-xs-12">
												<input class="form-control" name="passage-header-question" id="passage-header-question" placeholder="Enter question " />
											</div>
										</div>
										<div class="form-group">
											<label for="passage-description" class="col-xs-3 col-md-1 col-sm-1 control-label">Summary</label>
											<div class="col-md-5 col-sm-10 col-xs-12">
												<textarea class="form-control CASESTUDY-desc" rows="4" name="passage-description" id="description-casestudy"
												 placeholder="Enter passage information"></textarea>
											</div>
										</div>
										<div id="qa-list"></div>

										<div id="add-passage-question" class="form-group">
											<label for="passage-question" class="col-xs-3 col-md-1 col-sm-1 control-label">Sub Question</label>
											<div class="col-md-5 col-sm-10 col-xs-12">
												<div class="input-group">
													<input class="form-control" type="text" id="passage-question" placeholder="Enter Sub Question"
													 maxlength="1000" />
													<div class="input-group-addon"><span title="Add Question" id="add-passage-question-btn">ADD</span></div>
												</div>
											</div>
										</div>

									</div>
									<!-- CASESTUDY END -->

									<!-- DISCRIPTIVE START -->
									<div id="DISCRIPTIVE" class="subform">
										<div class="form-group">
											<label for="disctiptive-question-desc" class="col-xs-3 col-md-1 col-sm-1 control-label">Question</label>
											<div class="col-md-5 col-sm-10 col-xs-12">
												<textarea class="form-control DISCRIPTIVE-desc" id="disctiptive-question-desc" rows="2" name="discriptive-passage"
												 placeholder="Enter question"></textarea>
											</div>
										</div>
									</div>
									<!-- DISCRIPTIVE END -->

									<!-- TRUEFALSE Start-->
									<div id="TRUEFALSE" class="subform">
										<div class="form-group">
											<label for="true-false-question" class="col-xs-3 col-md-1 col-sm-1 control-label">Question</label>
											<div class="col-md-5 col-sm-10 col-xs-12">
												<input type="text" class="form-control TRUEFALSE-desc" id="true-false-question" name="question-description"
												 placeholder="Enter question">
											</div>
										</div>

										<div id="true-false-answer">
											<div class="form-group">
												<div class="col-sm-offset-1 col-sm-6">
													<input type="radio" name="trueorfalse" optiondata="true" class="true-false-answer"  id="true">
													<label for="true">True</label>
												</div>
											</div>
											<div class="form-group">
												<div class="col-sm-offset-1 col-sm-6">
													<input type="radio" class="true-false-answer"  name="trueorfalse" optiondata="false" id="false">
													<label for="false">False</label>
												</div>
											</div>
										</div>
									</div>
									<!-- TRUEFALSE END-->

									<!-- IMAGEUPLOAD Start-->
									<div id="IMAGEUPLOAD" class="subform">
										<div class="form-group">
											<label for="header-question-image-upload" class="col-xs-3 col-md-1 col-sm-1 control-label"> Question </label>
											<div class="col-md-5 col-sm-10 col-xs-12">
												<input type="text" name="image-upload-header-question" class="IMAGEUPLOAD-desc form-control" placeholder="Enter question">
											</div>
										</div>
										<hr>
										<div class="form-group">
											<label for="link-location-text-imageupload" class="col-xs-3 col-md-1 col-sm-1 control-label"> Image URL
											</label>
											<div class="col-md-5 col-sm-10 col-xs-12">
												<input type="text" name="link-location-text-imageupload" id="link-location-text-imageupload" class="form-control"
												 placeholder="Enter Image Location URL">
											</div>
										</div>
										<div class="form-group">
											<div class="col-sm-offset-1 col-sm-1">
												<span>OR</span>
											</div>
										</div>
										
										<div class="form-group">
											<label for="link-location-file" class="col-xs-3 col-md-1 col-sm-1 control-label"> Image File </label>
											<div class="col-md-5 col-sm-10 col-xs-12">
												<input type="text" hidden="hidden" class="file-name">
												<span class="btn btn-success btn-file">
													Upload<span class="filename"></span>
													<input type="file" accept="image/*" class="input-file-btn" id="file-imageupload"/>
												</span>
											</div>
										</div>
										<hr>
										<div id="added-answers-imageupload"></div>
										<div id="add-answer" class="form-group">
											<label for="answer" class="col-xs-3 col-md-1 col-sm-1 control-label">Option</label>
											<div class="col-md-5 col-sm-10 col-xs-12">
												<div class="input-group">
													<input class="form-control answer-description" type="text" id="answer-imageupload" placeholder="Enter option"
													 maxlength="1000" />
													<div class="input-group-addon"><span title="Add Answer" id="add-answer-btn" onclick="addSingleMultiAnswer('imageupload');">ADD</span></div>
												</div>
											</div>
										</div>
									</div>
									<!-- IMAGEUPLOAD END-->

									<!-- VIDEO Start-->
									<div id="VIDEO" class="subform">
										<div class="form-group">
											<label for="header-question-video-upload" class="col-xs-3 col-md-1 col-sm-1 control-label"> Question </label>
											<div class="col-md-5 col-sm-10 col-xs-12">
												<input type="text" name="video-upload-header-question" class="VIDEO-desc form-control" placeholder="Enter question">
											</div>
										</div>
										<hr>
										<div class="form-group">
											<label for="link-location-text-video" class="col-xs-3 col-md-1 col-sm-1 control-label"> Video URL </label>
											<div class="col-md-5 col-sm-10 col-xs-12">
												<input type="text" name="link-location-text" id="link-location-text-video" class="form-control" placeholder="Enter Video Location URL">
											</div>
										</div>
										<div class="form-group">
											<div class="col-sm-offset-1 col-sm-1">
												<span>OR</span>
											</div>
										</div>
										<div class="form-group">
											<label for="link-location-file" class="col-xs-3 col-md-1 col-sm-1 control-label"> Video File </label>
											<div class="col-md-5 col-sm-10 col-xs-12">
												<input type="text" hidden="hidden" class="file-name">
												<span class="btn btn-success btn-file">
													Upload<span class="filename"></span>
													<input type="file" accept="video/*" class="input-file-btn" id="file-video"/>
												</span>
											</div>
										</div>
										<hr>
										<div id="added-answers-video"></div>
										<div id="add-answer" class="form-group">
											<label for="answer" class="col-xs-3 col-md-1 col-sm-1 control-label">Option</label>
											<div class="col-md-5 col-sm-10 col-xs-12">
												<div class="input-group">
													<input class="form-control answer-description" type="text" id="answer-video" placeholder="Enter option"
													 maxlength="1000" />
													<div class="input-group-addon"><span title="Add Answer" id="add-answer-btn" onclick="addSingleMultiAnswer('video');">ADD</span></div>
												</div>
											</div>
										</div>
									</div>
									<!-- VIDEO END-->


									<!-- LINKASSIGNMENTUPLOAD Start-->
									<div id="LINK" class="subform">
										<div class="form-group">
											<label for="header-question-link-upload" class="col-xs-3 col-md-1 col-sm-1 control-label"> Question </label>
											<div class="col-md-5 col-sm-10 col-xs-12">
												<input type="text" name="link-upload-header-question" class="LINK-desc form-control" id="link-question" placeholder="Enter question">
											</div>
										</div>
										<div class="form-group">
											<label for="uploadType" class="col-xs-3 col-md-1 col-sm-1 control-label"> Link Upload Type </label>
											<div class="col-sm-3 col-xs-5">
												<select name="uploadType" class="uploadType" id="link-uploadType">
												<option value="">Select Answer File Type</option>
												<option value="pdf">pdf</option>
												<option value="mp4">mp4</option>
												<option value="zip">zip</option>
											</select>
											</div>
										</div>
										<hr>
										<div class="form-group">
											 <label for="link-location-text-link" class="col-xs-3 col-md-1 col-sm-1 control-label">File URL
											</label> 
											<div class="col-md-5 col-sm-10 col-xs-12">
												<input type="text" name="link-location-text" id="link-location-text-link" class="form-control"
												 placeholder="Enter File Location URL" readonly>
											</div>
										</div>
										<div class="form-group">
											<label for="link-location-file" class="col-xs-3 col-md-1 col-sm-1 control-label"> File </label>
											<div class="col-md-5 col-sm-10 col-xs-12">
												<input type="text" hidden="hidden" class="file-name">
												<span class="btn btn-success btn-file">
													Upload<span class="filename"></span>
													<input type="file"  class="input-file-btn" id="file-link"/>
												</span>
											</div>
										</div>
										<hr>
									</div>
									<!-- LINKASSIGNMENTUPLOAD END-->
									<div class="form-group">
										<div class="col-sm-6 text-center">
											<button type="submit" class="btn btn-default">Save</button>
											<button class="btn btn-back">Close</button>
										</div>
									</div>
								</form>
							</div>
							<!-- question-form END -->

							<!-- question-list START  -->
							<div id="question-list">
								<div id="alert" class="alert alert-success" hidden="hidden">
									<strong>Question Added Successfully!</strong>
									<a href="javascript:closeAlert();" style="float:right;">x</a>
								</div>
								
								<c:choose>
									<c:when test="${canCurrentUserUpdateQuestions == true }">
									
										<button id="add-question-btn">Add Question</button>
									
									</c:when>
									<c:otherwise>
										<div class="alert alert-danger">
											<button id="add-question-btn" disabled >Add Question</button>
											<strong> Add,Edit and Delete Questions is disabled as IA is live. </strong>
										</div>
									</c:otherwise>
								</c:choose>
								<div id="added-question-list" class="table-responsive">
									<table class="table table-bordered table-striped table-hover tables" >
										<thead>
											<tr>
												<th>Sr No</th>
												<th>Question</th>
												<th>Section Name</th>
												<th>CopyCaseThreshold</th>
												<th>Marks</th>
												<th>Type</th>
												<th>Action</th>
												<!-- <th><input type="checkbox" name="select-all" id="select-all" onchange="selectAll();"></th> -->
											</tr>
										</thead>
										<tbody>
											<c:forEach var="question" items="${testQuestions}" varStatus="status">
												<tr id="question-row-id-value-${question.id}">
													<td><c:out value="${status.count}" /></td>
													<td><c:out value="${question.question}" /></td>
													<td><c:out value="${question.sectionName}" /></td>
													<td><c:out value="${question.copyCaseThreshold}" /></td>
													<td><c:out value="${question.marks}" /></td>
													<td><c:out value="${question.typeInString}" /></td>
													<td>
													<a href="/exam/editTestQuestion?id=${question.id}"
																		
														<c:if test="${canCurrentUserUpdateQuestions == false }">
															class ="disabledLink"
														</c:if>
								
													>
														<i class="fa-solid fa-pen-to-square" title="edit"></i>
													</a>
													|
													<%-- <i class="fa fa-eye" 
													 onclick="viewQuestions('${status.count}')"
													 title="view"></i>| --%>
													<i class="fa-solid fa-trash" 
													   title="delete" 
													   		
														<c:if test="${canCurrentUserUpdateQuestions == false }">
															class ="disabledLink"
														</c:if>
														<c:if test="${canCurrentUserUpdateQuestions == true }">
													   		onclick="deleteQuestions('${question.id}');"
														</c:if>
													   ></i>
													</td>
													<!-- <td><input type="checkbox" name="select-question-1" id="select-question-">
													</td> -->

												</tr>
											</c:forEach>
										</tbody>
										<!-- <tfoot>
											<tr>
												<td colspan="5">
													<button class="btn btn-warning btn-custom" onclick="deleteMultiple();">Delete Selected</button>
												</td>
											</tr>
										</tfoot> -->
									</table>
								</div>
							</div>
							<!-- question-list END  -->



							<!-- Code For Page Goes in Here End -->
						</div>

					</div>
				</div>
			</div>
		</div>
	</div>

	<div id="snackbar">Some text some message..</div>
	<jsp:include page="../adminCommon/footer.jsp" />


</body>
<!-- <script src="../../test/js/default.js"></script> -->

<script src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-datetimepicker/4.17.37/js/bootstrap-datetimepicker.min.js"></script>

 <script type="text/javascript"
		src="//cdn.ckeditor.com/4.14.0/standard-all/ckeditor.js"></script> 
<script type="text/javascript" src="//cdnjs.cloudflare.com/ajax/libs/mathjax/2.7.0/MathJax.js?config=TeX-AMS_HTML"></script>
 
 
	<script src="${pageContext.request.contextPath}/assets/js/ckeditor/ckeditor_responsive_table/plugin.js"></script>
 
 <!-- 
<script type="text/javascript">
	 let editor = CKEDITOR.replace(
						'description-singlemulti',
						{
							extraPlugins : 'mathjax',
							mathJaxLib : 'https://cdnjs.cloudflare.com/ajax/libs/mathjax/2.7.0/MathJax.js?config=TeX-AMS_HTML'
						}); 
	 
	 editor.on( 'change', function( evt ) {
		    // getData() returns CKEditor's HTML content.
		    console.log( 'Total bytes: ' + evt.editor.getData() );
		    $('#description-singlemulti').html(evt.editor.getData());
		});
</script>
 -->
<!-- 
	<script src="resources_2015/js/vendor/froala_editor.min.js"></script>
	<script src="resources_2015/js/vendor/froala-plugins/tables.min.js"></script>
	<script src="resources_2015/js/vendor/froala-plugins/lists.min.js"></script>
	<script src="resources_2015/js/vendor/froala-plugins/colors.min.js"></script>
	<script
		src="resources_2015/js/vendor/froala-plugins/font_family.min.js"></script>
	<script src="resources_2015/js/vendor/froala-plugins/font_size.min.js"></script>
	<script
		src="resources_2015/js/vendor/froala-plugins/block_styles.min.js"></script>
	<script
		src="resources_2015/js/vendor/froala-plugins/media_manager.min.js"></script>
	<script
		src="resources_2015/js/vendor/froala-plugins/inline_styles.min.js"></script>
	<script src="resources_2015/js/vendor/froala-plugins/fullscreen.min.js"></script>
	<script
		src="resources_2015/js/vendor/froala-plugins/char_counter.min.js"></script>
	<script src="resources_2015/js/vendor/froala-plugins/entities.min.js"></script>
	<script
		src="resources_2015/js/vendor/froala-plugins/file_upload.min.js"></script>
	<script src="resources_2015/js/vendor/froala-plugins/urls.min.js"></script>
 -->

<!-- 
	<script type="text/javascript">
$('#description-singlemulti').editable({inlineMode: false,
    buttons: ['bold', 'italic', 'underline', 'sep', 
              'strikeThrough', 'subscript', 'superscript', 'sep',
              'fontFamily', 'fontSize', 'color', 'formatBlock', 'blockStyle', 'inlineStyle','sep',
              'align', 'insertOrderedList', 'insertUnorderedList', 'outdent', 'indent', 'selectAll','sep',
              'createLink', 'table','sep',
              'undo', 'redo', 'sep',
              'insertHorizontalRule', 'removeFormat', 'fullscreen'],
    minHeight: 200,
    paragraphy: false,
    placeholder: 'Enter Question here',
    theme: 'blue',
    toolbarFixed: false
});

</script>
-->
<script>
	$(function () {

		$('#Yes').on('click', function () {
			//alert('slidYes');
			$('#maxQuestions').show();

			$("#maxQuestnToShow").prop('required', true);
			$("#testQuestionWeightageReq").prop('required', true);
			$('#testQuestionWeightageReq').show();

		});

		$('#No').on('click', function () {
			//alert('slidNo');
			$('#maxQuestions').hide();

			$("#maxQuestnToShow").prop('required', false);
			$('#testQuestionWeightageReq').hide();
			$('#testQuestionWeightageReq').prop('required', false);

		});

	});
</script>


<script>
	$(document)
		.ready(
			function () {

			});
	$("#datetimepicker1").on("dp.change", function (e) {

		validDateTimepicks();
	}).datetimepicker({
		//minDate:new Date()
		useCurrent: false,
		format: 'YYYY-MM-DD HH:mm:ss'
	});

	$("#datetimepicker2").on("dp.change", function (e) {

		validDateTimepicks();
	}).datetimepicker({
		//minDate:new Date()
		useCurrent: false,
		format: 'YYYY-MM-DD HH:mm:ss'
	});

	function validDateTimepicks() {
		if (($('#startDate').val() != '' && $('#startDate').val() != null) &&
			($('#endDate').val() != '' && $('#endDate').val() != null)) {
			var fromDate = $('#startDate').val();
			var toDate = $('#endDate').val();
			var eDate = new Date(fromDate);
			var sDate = new Date(toDate);
			if (sDate < eDate) {
				alert("endate cannot be smaller than startDate");
				$('#startDate').val("");
				$('#endDate').val("");
			}
		}
	}
</script>
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/chosen/1.5.1/chosen.min.css">
<!-- <link rel="stylesheet" href="assets/test/css/chosen.min.css"> -->
<script src="https://cdnjs.cloudflare.com/ajax/libs/chosen/1.5.1/chosen.jquery.min.js"></script>
<!-- <script src="assets/test/js/chosen.jquery.min.js"></script> -->

<!-- Test CSS -->
<style>
	.input-group-addon:hover {
		font-weight: bold;
		cursor: pointer;
	}

	.btn-file {
		position: relative;
		overflow: hidden;
	}

	.btn-file input[type=file] {
		position: absolute;
		top: 0;
		right: 0;
		min-width: 100%;
		min-height: 100%;
		font-size: 100px;
		text-align: right;
		filter: alpha(opacity=0);
		opacity: 0;
		outline: none;
		background: blue;
		cursor: inherit;
		display: block;
	}

	/* The snackbar - position it at the bottom and in the middle of the screen */
	#snackbar {
		visibility: hidden;
		/* Hidden by default. Visible on click */
		min-width: 250px;
		/* Set a default minimum width */
		margin-left: -125px;
		/* Divide value of min-width by 2 */
		background-color: #333;
		/* Black background color */
		color: #fff;
		/* White text color */
		text-align: center;
		/* Centered text */
		border-radius: 2px;
		/* Rounded borders */
		padding: 16px;
		/* Padding */
		position: fixed;
		/* Sit on top of the screen */
		z-index: 1;
		/* Add a z-index if needed */
		left: 50%;
		/* Center the snackbar */
		bottom: 30px;
		/* 30px from the bottom */
	}

	/* Show the snackbar when clicking on a button (class added with JavaScript) */
	#snackbar.show {
		visibility: visible;
		/* Show the snackbar */
		/* Add animation: Take 0.5 seconds to fade in and out the snackbar. 
However, delay the fade out process for 2.5 seconds */
		-webkit-animation: fadein 0.5s, fadeout 0.5s 2.5s;
		animation: fadein 0.5s, fadeout 0.5s 2.5s;
	}

	/* Animations to fade the snackbar in and out */
	

	/* Styling for snackbar end */

	/* loader START */
	.lds-dual-ring {
		display: inline-block;
		width: 64px;
		height: 64px;
	}

	.lds-dual-ring:after {
		content: " ";
		display: block;
		width: 46px;
		height: 46px;
		margin: 1px;
		border-radius: 50%;
		border: 5px solid #fff;
		border-color: #fff transparent #fff transparent;
		animation: lds-dual-ring 1.2s linear infinite;
	}

	@keyframes lds-dual-ring {
		0% {
			transform: rotate(0deg);
		}
		100% {
			transform: rotate(360deg);
		}
	}
	/* loader END */

</style>
<!-- Test JS-->
<script>

	//Globals Arguments
	
	var sapid = '${userId}';
	var testId = ${test.id};
	var tableRowDisplayCount = 	<c:out value="${empty fn:length(testQuestions) ? '0' : fn:length(testQuestions)}" />;
	
	var questionDetails;
	var answerArray = [];
	var questionsList = [];
	var passageQuestionAnswerCount = [];
	
	var optionsCount = 0; 
	var tableRowCount = 1;
	var questiontypeval = 1;
	var passageAnswerCount = 1;
	var passageQuestionCount = 1;
	
	var filename = '';
	var answerBlock = "";
	var passageAnswerBlock = "";

	var testQuestions = new Array();
	
	var questionsTextEditorObj = {};
	
	//Constants
	
	const minOptionLimit = 2;
	const maxOptionsLimit = 8;
	const acceptedFileSize = 5000000;
	const acceptedVideoFileSize = 39000000;

	$(document).ready(function () {
		checkQuestionList();
		
		//responsive table plugin start
		/* not is use
CKEDITOR.editorConfig = function (config) {

  // Es totalmente obligatorio que la carpeta se llame como el plugin internamente:
  // CKEDITOR.plugins.add('ckeditor_responsive_table')
  // Y tener en cuenta que en "extraPlugins" lo que estamos poniendo es el nombre del directorio que asume que tienes en "/plugins". Si aadis un nombre de plugin inexistente o uno externo muy posiblemente te lance este error "Uncaught TypeError: Cannot read property 'icons' of null"
  // En el caso de querer aadir un plugin que est fuera del directorio de plugins estandar, vas a tener que usar  CKEDITOR.plugins.addExternal() --> ver ejemplo comentado mas abajo


  // Cargamos los plugin que hay fuera del directorio de plugins.
  // El primer param tiene que ser exactamente el nombre del plugin si no quieres revibir un "Uncaught TypeError: Cannot read property 'icons' of null"
  CKEDITOR.plugins.addExternal('ckeditor_responsive_table', '/exam/assets/js/ckeditor/ckeditor_responsive_table/plugin.js');

  // Hora de aadir tanto los plugin de ckeditor como los externos
  config.extraPlugins = ['ckeditor_responsive_table'];


  // No importa si el boton tiene establecido algo como "toolbar: 'tools'"
  // config.toolbar = [
  //   {name: 'basicstyles', items: ['Timestampo', 'ResponsiveTable', 'Italic']}
  // ];

  config.toolbar = [
    {name: 'basicstyles', items: ['ResponsiveTable']}
  ];

  // Allow all html tags for demo purpose.
  config.allowedContent = true;

};
		*/
		//responsive table plugin end
		
	});

	$(document).on("click", ".fa-remove", function () {
		$(this).parent().parent().remove();
		optionsCount--;
		passageAnswerCount--;
	});

	$("#question-type").chosen({
		width: '49%'
	});

	$("#sectionId").chosen({
		width: '49%'
	});

	$(".marks").chosen({
		width:'40%'
	});
	
	$(".subquestion-marks").chosen({
		width:'40%'
	});
	

	$("#copyCaseThreshold").chosen({
		width:'40%'
	});
	


	$("#add-question-btn").on("click", function () {
		refresh();
		$(".add-question-form").slideToggle();
		$("#add-question-btn").hide('slow');
	});


	var flag = false;
	
	// Image, Video size and type validation
	$('.input-file-btn').change(function (e) {
		var filename = e.target.files[0].name;
		$('#file-name').val(filename);
		var extension = filename.split('.')[1];
		var file = e.target.files[0];
		var fileType = file["type"];

		var ValidTypes = [];
		var filesize = this.files[0].size;
		var acceptedSize;
		flag = false;
		
		if ($('#question-type').val() == 'IMAGEUPLOAD') {

			ValidTypes = ["image/gif", "image/jpeg", "image/png", "image/jpg"];
			acceptedSize = acceptedFileSize;

		} else if ($('#question-type').val() == 'VIDEO'){
			acceptedSize = acceptedVideoFileSize;
			ValidTypes = ["video/mp4", "video/mov", "video/avi"];
			
		}else{
			console.log("link upload else entered");
			acceptedSize = acceptedVideoFileSize;
			ValidTypes = ["application/pdf", "application/docx", "application/doc", "application/csv","application/xlsx","application/xls","application/vnd.ms-excel","application/vnd.openxmlformats-officedocument.spreadsheetml.sheet","application/vnd.openxmlformats-officedocument.wordprocessingml.document"];
		}

		if ($.inArray(fileType, ValidTypes) < 0) {
			// invalid file type code goes here
			alert('Valid types are :' + ValidTypes + ' given file type :' + fileType);
			return false;
		}else{
			flag = true;
		}
		if (filesize > acceptedSize) {
			alert('file size exceeded the ' + acceptedSize / 1000000 + 'MB limit');
			return false;
		}else{
			flag = true;
		}
		$('.filename').html("ed : " + filename);

		if(flag){
			if($('#question-type').val() == 'LINK'){
				saveFile(file);
			}else{
				saveMedia(file);
			}
				
		}
		

	});
	
	function showSnackBar(message) {
		// Get the snackbar DIV
		var x = document.getElementById("snackbar");

		x.innerHTML = message;

		// Add the "show" class to DIV
		x.className = "show";

		setTimeout(location.reload(), 100);
		// After 3 seconds, remove the show class from DIV
		setTimeout(function () {
			x.className = x.className.replace("show", "");
		}, 3000);
		
	}

    function saveMedia(myFile) {
		try{
			var myFileInput = $('#file-'+$('#question-type').val().toLowerCase()).prop('files')[0];
			
			console.log("myFileInput : ");
			console.log(myFileInput);
			
			var formData = new FormData();
			formData.append('image', myFileInput); 
			
			 $.ajax({
				url : '/exam/m/uploadTestQuestionImage?testId=',
				data : formData,
				processData : false,
				contentType : false,
				type : 'POST',
				success : function(data) {
					console.log("IN uploadTestQuestionImage got data : ");
					console.log(data);
					$('#link-location-text-'+$('#question-type').val().toLowerCase()).val(data["imageUrl"]);
					$('#link-location-text-'+$('#question-type').val().toLowerCase()).attr('readonly', true);
				},
				error : function(err) {
					
					alert('error : '+JSON.stringify(error));
				}
			}); 
		}catch(error){
			alert('Following Error Occurred:'+error);
		}
    }
    //Added for link upload start
    function saveFile(myFile) {
		try{
			var myFileInput = $('#file-'+$('#question-type').val().toLowerCase()).prop('files')[0];
			
			console.log("myFileInput : ");
			console.log(myFileInput);
			
			var formData = new FormData();
			formData.append('image', myFileInput); 
			
			 $.ajax({
				url : '/exam/m/uploadTestAssignmentQuestionFile?testId=${test.id}',
				data : formData,
				processData : false,
				contentType : false,
				type : 'POST',
				success : function(data) {
					console.log("IN uploadTestAssignmentQuestion got data : ");
					console.log(data);
					$('#link-location-text-'+$('#question-type').val().toLowerCase()).val(data["imageUrl"]);
					$('#link-location-text-'+$('#question-type').val().toLowerCase()).attr('readonly', true);
				},
				error : function(err) {
					
					alert('error : '+JSON.stringify(error));
				}
			}); 
		}catch(error){
			alert('Following Error Occurred:'+error);
		}
    }
  //Added for link upload end
	$(".btn-back").on("click", function (event) {
		refresh();
		$('#alert').hide();
		goBack(event);
		$("#add-question-btn").slideToggle();
	});

	// save btn submit form
	$(".btn-default").on("click", function (event) {

		event.preventDefault();

		let sectionId = $('#sectionId').val();

		if (sectionId == '') {
			alert("Please Select Section");
			return false;
		}
		
		let copyCaseThreshold = $('#copyCaseThreshold').val();

		if (copyCaseThreshold == '') {
			alert("Please Select copyCaseThreshold");
			return false;
		}
		
		
		
		var questionType = $('#question-type').val();

		var textareaID = "." + questionType + '-desc';
		
		let textareaIdSelector = $(textareaID).attr('id');
		
		var questionText = $(textareaID).val().trim();
		
		console.log(CKEDITOR.instances);
		
	    let questionsTextEditorsText = CKEDITOR.instances[textareaIdSelector].getData();

		console.log("IN submit form text from editor :  ",questionsTextEditorsText);
			
	   console.log("IN submit form text from editor :  ",questionsTextEditorsText);
		
	   questionText = questionsTextEditorsText;
	   
		if(questionType.toLowerCase() == 'link' && $('#link-uploadType').val() == ''){
			if (confirm("Please select link-uploadType !")) {
				return false;
			}
		}
		if (!(questionText.trim().length > 0)) {
			if (confirm("Please enter " + questionType.toLowerCase() + " Question!")) {
				return false;
			}
		} else {

			if (!checkForCorrectAnswer()) {
				return false;
			}

			var headerQuestion = '';
			var Question = '';
			var questionType = $('#question-type').val();
			var options = '';

			var tr = "";

			
			tableRowDisplayCount++;
			
			tr += '<tr id="question-row-' + tableRowCount + '">';
			tr += '<td>' + tableRowDisplayCount + '</td>';
			tr += '<td>' + questionText + '</td>';
			tr += '<td>' + questiontypeval + '</td>';
			tr += '<td id="edit-icon-append-'+ tableRowDisplayCount+'">' +
				'|<i class="fa-solid fa-trash" title="delete" onclick="deleteQuestions(' + tableRowCount +
				');"></i></td></tr>';

			$('tbody').append(tr);
			tr = "";

			checkQuestionList();
			goBack(event);

			$("#alert").show();

	
			$("#alert").fadeTo(2000, 500).slideUp(500, function () {
				$("#alert").slideUp(500);
			});
			tableRowCount++;
			
			var optionsListForQuestion =  questiontypeval == 5 ? makeAnswerArray('.true-false-answer') :  makeAnswerArray('.correct-answer');
			
			var questionDetails = {
				'question': questionText,
				'type': questiontypeval,
				'optionsList' : optionsListForQuestion,
				'isSubQuestion' : 0,
				'mainQuestionId' : -1,
				'marks' : $('.marks').val(),
				'description' : '',
				'uploadType' : '',
				'sectionId' : sectionId,
				'copyCaseThreshold' : copyCaseThreshold,
				
			}
			
			// Saving data depending upon the question type
			
			let qSaved = false;
			
			switch(questiontypeval){
				
				case "1": //Single Select
				case "2": //Multi Select
				case "4": //Descriptive
				case "5": //True False
					qSaved = saveSingleSelectQuestion(questionDetails);
					break;
				case "3": //Case Study
					
					questionDetails.question = $('#passage-header-question').val();
					questionDetails.description = $('#description-casestudy').val();
					var mainQuestionId = 0;
					qSaved = saveCaseStudyQuestion(questionDetails);
					break;

				case "6": //ImageUpload
					questionDetails.url = $('#link-location-text-imageupload').val();
					
					qSaved = saveSingleSelectQuestion(questionDetails);
					break;

				case "7": //Video
					questionDetails.url = $('#link-location-text-video').val();
					qSaved = saveSingleSelectQuestion(questionDetails);
					break;
				case "8": //AssignemntFileUpload
					questionDetails.url = $('#link-location-text-link').val();
					questionDetails.uploadType = $('#link-uploadType').val();
					qSaved = saveSingleSelectQuestion(questionDetails);
					break;
				default:
					alert('Invalid option selected');

					break;
			}
			
			if (qSaved) {

				$("#add-question-btn").slideToggle();
				return true;
			} else {
				
				$("#add-question-btn").slideToggle();
				return false;
			}


		}
	});

	function addSingleMultiAnswer(answerType = 'single') {

		var answer = $("#answer-" + answerType).val();

		if (answer == undefined || answer.trim() == '') {
			alert('Please enter option description ' + answer);
			return false;
		}

		var questionType, answerListDiv;

		switch (answerType) {
			case 'multi':
				questionType = 'checkbox';
				answerListDiv = $("#added-answers-multi");
				break;
			case 'imageupload':
				questionType = 'checkbox';
				answerListDiv = $("#added-answers-imageupload");
				break;
			case 'video':
				questionType = 'checkbox';
				answerListDiv = $("#added-answers-video");
				break;

			default:
				questionType = 'radio';
				answerListDiv = $("#added-answers");
				break;
		}

		if(!(optionsCount > maxOptionsLimit)){
		
		// [option:"+optionData+",isCorrect:"+isCorrect+"]
		answerBlock += '<div class="form-group">';
		answerBlock += '<div class="col-md-offset-1 col-md-4 col-sm-10 col-xs-10">';
		answerBlock += '<input type="' + questionType +
			'" name="correct-answer" class="correct-answer" title="Correct Answer" optionData="'+answer+'">';
		answerBlock += '<p style="display:inline-block;margin-left:2%;" >' + answer + '</p>';
		answerBlock += '</div>';
		answerBlock += '<div class="col-sm-1">';
		answerBlock += '<i class="fa-solid fa-xmark" title="Delete Answer" ></i>';
		answerBlock += '</div>';
		answerBlock += '</div>';

		answerListDiv.append(answerBlock);
		}else{
			alert('The max option limit of ' + maxOptionsLimit + ' options has exceeded');
			return false;
		}
		optionsCount++;

		$("#answer-" + answerType).val('');
		answerBlock = "";

		return true;

	}

	/* 
	addPassageAnswers :
		@performs: 
			1. append option for the subquestion for case study 
			2. check for limit for added options
		@accepts: rowNumber which is the passageQuestionCount 
		
	*/

	function addPassageAnswers(rowNumber) {

		if (passageAnswerCount > maxOptionsLimit) {
			alert('The max option limit of ' + maxOptionsLimit + ' options has exceeded');
			return false;
		}

		var passageAnswer = $("#passage-answer-" + rowNumber).val();

		if (passageAnswer == undefined || passageAnswer.trim() == '') {
			alert('Please enter passage answer ' + passageAnswer);
			$('#passage-answer-' + rowNumber).addClass('has-error');
			return false;
		}
	 	
		var passageAnswerBlock = "";

		passageAnswerBlock += '<div class="form-group" >';
		passageAnswerBlock += '<input type="checkbox" class="is-correct-answer-'+rowNumber+'" optiondata="'+ passageAnswer +'" id="correct-answer-' + rowNumber + '-' + passageAnswerCount + '" data-answer-number="' + passageAnswerCount + '" class="correct-answer passage-answer-' + passageQuestionCount +
			'" title="Correct Answer">';
		passageAnswerBlock += '<p style="display:inline-block;">&nbsp;' + passageAnswer;
		passageAnswerBlock += '</p>';
		passageAnswerBlock += '<span>';
		passageAnswerBlock += '<i class="fa-solid fa-xmark" style="float: right;margin-right: 6%;" title="Delete Answer">';
		passageAnswerBlock += '</i>';
		passageAnswerBlock += '</span>';
		passageAnswerBlock += '</div>';

		passageQuestionAnswerCount[rowNumber] = passageAnswerCount;

		$("#passage-answer-list-" + rowNumber).append(passageAnswerBlock);

		passageAnswerBlock = "";
		
		$("#passage-answer-" + rowNumber).val('');
		
		passageAnswerCount++;
		
		return true;
	}

	$("#add-passage-question-btn").on("click", function () {

		if ($("#passage-question").val().trim() == '') {
			alert("Please enter Sub Question");
			return false;
		}
		if (passageQuestionCount > 1) {
			passageAnswerCount = 1;
		}
		passageQuestionAnswerCount[passageQuestionCount] = passageAnswerCount;

		var passageQuestion = $("#passage-question").val();

		var passageAnswerBlock = "";

		passageAnswerBlock += '<div class="form-group">';
		passageAnswerBlock += '<div id="passage-question-' + passageQuestionCount +
			'" class="col-sm-offset-1 col-md-5 col-sm-10 col-xs-12"  >';
		passageAnswerBlock += '<div class="panel panel-default" >';
		passageAnswerBlock += '<div class="panel-heading" >';
		passageAnswerBlock += '<input type="hidden" id="subquestion_'+passageQuestionCount+'" value="'+passageQuestion+'">';
		passageAnswerBlock += '<strong>Question ' + passageQuestionCount + '. ' + passageQuestion +'</strong>';
		passageAnswerBlock += '<h4 style="display:inline; ">';
		passageAnswerBlock += '</h4>';
		passageAnswerBlock += '<i class="fa-solid fa-trash" style="float:right;" onclick="deletePassageQuestion(' +
		passageQuestionCount + ');" title="Delete Question"></i>';
		passageAnswerBlock += '<br><br> <strong>Marks :  </strong> ';
		passageAnswerBlock += '<select class="marks" id="subquestion-marks-'+passageQuestionCount+'" >';
		passageAnswerBlock += '<option value="1">1</option><option value="2">2</option><option value="3">3</option><option value="4">4</option><option value="5">5</option><option value="6">6</option><option value="7">7</option><option value="8">8</option><option value="9">9</option><option value="10">10</option></select>';
		passageAnswerBlock += '</div>';
		passageAnswerBlock += '<div class="panel-body">';
		passageAnswerBlock += '<div id="passage-answer-list-' + passageQuestionCount + '" style="margin-left:6%;">';
		passageAnswerBlock += '</div>';
		passageAnswerBlock += '<div id="add-passage-answer" class="form-group">';
		passageAnswerBlock += '<div class="input-group" style="margin-left: 2%;margin-right: 2%;">';
		passageAnswerBlock += '<input class="form-control" type="text" id="passage-answer-' + passageQuestionCount +	'" placeholder="Enter option" maxlength="1000">';
		passageAnswerBlock += '<div class="input-group-addon" >';
		passageAnswerBlock += '<span title="Add Answer" onclick="addPassageAnswers(' + passageQuestionCount + ');" >ADD</span>';
		passageAnswerBlock += '</div>';
		passageAnswerBlock += '</div>';
		passageAnswerBlock += '</div>';
		passageAnswerBlock += '</div>';
		passageAnswerBlock += '</div>';
		passageAnswerBlock += '</div>';
		passageAnswerBlock += '</div>';

		$("#qa-list").append(passageAnswerBlock);

		passageAnswerBlock = "";
		
		$("#passage-question").val('');

		passageQuestionCount++;

	});

	function checkForCorrectAnswer() {

		var alertText = "Please select atleast one correct answer";
	
		switch ($('#question-type').val()) {
			case 'SINGLESELECT':
			case 'MULTISELECT':
				
				if($(".correct-answer").length < 2){
					alert('Please add atleast two options');
					return false;
				}
				if (!$(".correct-answer").is(":checked")) {
					alert(alertText);
					return false;
				} else {
				 
					makeAnswerArray('.correct-answer');
					return true;
				}
			case 'TRUEFALSE':
				if (!$(".true-false-answer").is(":checked")) {
					alert(alertText);
					return false;
				} else {
					return true;
				}
				
				break;

			case 'CASESTUDY':

				var checkArray = [];
				
				for (let pq = 1; pq < passageQuestionAnswerCount.length; pq++) {
					var questionid = 'question' + pq;
					checkArray['q_' + pq] = [];
					$('#passage-answer-list-' + pq).find(':checkbox').each(function () {
						checkArray['q_' + pq].push($('#' + $(this).attr('id')).is(':checked'));
					});
				}
			
				var questionAtleastone = [];
				for (let q = 1; q < passageQuestionAnswerCount.length; q++) {
					const answerStatusArray = checkArray['q_' + q];
					questionAtleastone[q] = ($.inArray(true, answerStatusArray) >= 0 ? 1 : 0);
				}

				var flag = "invalid";
				if (questionAtleastone.length > 0) {
					if ($.inArray(0, questionAtleastone) >= 0) {
						alert(alertText);
						return false;
					} else {
						flag = "valid";
					}
				
				} else {
					alert('Please add questions');
					return false;
				}

				if (flag == "valid") {
					return true;
				}

				break;
			case 'IMAGEUPLOAD':

				if ($('#link-location-text-imageupload').val() === '' && $('.filename').html() === '') {
					alert('Please enter either image URL or upload the file locally');
					return false;
				}
				
				//if (! $('#link-location-text-imageupload').is('[readonly]') ) { 
					if ($('#link-location-text-imageupload').val() === '' && $('.filename').html() === '') {
						alert('Atleast image text or image file should be present');
						return false;

					}
				//}
				
				if ($('.filename').html() == '' && !is_url($('#link-location-text-imageupload').val())) {
					alert('URL link is not valid');
					return false;
				}

				if ($('#added-answers-imageupload').is(':empty')) {
					alert('Please add atleast one option');
					return false;
				}

				if (!$(".correct-answer").is(":checked")) {
					alert(alertText);
					return false;
				} else {
					return true;
				}


			case 'VIDEO':

				if ($('#link-location-text-video').val() === '' && $('.filename').html() === '') {
					alert('Please enter either image URL or upload the file locally');
					return false;
				}

				//if (! $('#link-location-text-video').is('[readonly]') ) { 
					if (!($('#link-location-text-video').val() === '' || $('.filename').html() === '')) {
						alert('Atleast image text or image file should be present');
						return false;
					}
					if (!is_url($('#link-location-text-video').val())) {
						alert('URL link is not valid');
						return false;
					}
				//}

				if ($('#added-answers-video').is(':empty')) {
					alert('Please add atleast one option');
					return false;
				}

				if (!$(".correct-answer").is(":checked")) {
					alert(alertText);
					return false;
				} else {
					return true;
				}

				break;
			case "DISCRIPTIVE":
				
				return true;
				
				break;
			case 'LINK':

				if (($('#link-location-text-link').val() === '' || $('.filename').html() === '') && $('#link-uploadType').val() === 'pdf') {
					alert('Please Upload the file locally');
					return false;
				}else{
					return true;
				}
				
					
					/* if (!is_url($('#link-location-text-link').val())) {
						alert('URL link is not valid');
						return false;
					} */

				break;
			default:
				alert('Inside Default statement');
				return true;
				break;
		}


	}

	/* 
		makeAnswerArray:
		 
		 @Accepts: class given to input elements with type checkbox and containing optiondata attribute
		 @Returns: Array of JSON objects
		 			sample : [
								0 => { 
									'optionData': 'Option data',
							 		'isCorrect':'Y'
									  },
								1 => {
									 'optionData': 'Option data',
									  'isCorrect':'N' 
									}
					 		]

	 */

	function makeAnswerArray(answerClass){
		answerArray = [];
		$(answerClass).each(function(index, inputElement){
			answerArray[index] = {};

			if(!$(inputElement).attr('optionData')){
				return;
			}else{
			
				answerArray[index].optionData = $(inputElement).attr('optionData');
			}

			answerArray[index].isCorrect = ($(inputElement).is(':checked')?'Y':'N');
		});
		
		
		return answerArray;
	}

	function checkQuestionList() {
		if ($('tbody').children().length == 0 || tableRowCount == 0) {
			var tr =
				"<tr class=\"warning\" id=\"emptylist\"><td colspan=\"4\">The question list is empty. Please add question using the 'ADD QUESTION' button.</td></tr>";
			$('tbody').append(tr);
		} else {
			$("#emptylist").remove();
		}
	}

	function is_url(str) {
		var regexp =
			/^(?:(?:https?|ftp):\/\/)?(?:(?!(?:10|127)(?:\.\d{1,3}){3})(?!(?:169\.254|192\.168)(?:\.\d{1,3}){2})(?!172\.(?:1[6-9]|2\d|3[0-1])(?:\.\d{1,3}){2})(?:[1-9]\d?|1\d\d|2[01]\d|22[0-3])(?:\.(?:1?\d{1,2}|2[0-4]\d|25[0-5])){2}(?:\.(?:[1-9]\d?|1\d\d|2[0-4]\d|25[0-4]))|(?:(?:[a-z\u00a1-\uffff0-9]-*)*[a-z\u00a1-\uffff0-9]+)(?:\.(?:[a-z\u00a1-\uffff0-9]-*)*[a-z\u00a1-\uffff0-9]+)*(?:\.(?:[a-z\u00a1-\uffff]{2,})))(?::\d{2,5})?(?:\/\S*)?$/;
		if (regexp.test(str)) {
			return true;
		} else {
			return false;
		}
	}

	function closeAlert() {

		$("#alert").fadeTo(2000, 500).slideUp(500, function () {
			$("#alert").slideUp(500);
		});
	}

	function refresh() {
	
		$("#question-type option").each(function () {
			if ($(this).is(':selected')) {
				$("#" + $(this).val()).show();
				questiontypeval = $(this).attr("questiontypeval");
				
				//on change of question type update questionsTextEditorObj start
				let questionType = $('#question-type').val();

				let textareaClass = "." + questionType + '-desc';
				
				let textareaId = $(textareaClass).attr('id');
				
				console.log("IN refresh() textareaId : ",textareaId);
				
				questionsTextEditorObj =  CKEDITOR.replace(
						textareaId,
						{
							extraPlugins : 'uploadimage,image2,mathjax,autogrow,colorbutton,font,justify,print,tableresize,uploadfile,pastefromword,liststyle,pagebreak',
							//      extraPlugins: 'colorbutton,font,justify,print,tableresize,uploadimage,uploadfile,pastefromword,liststyle,pagebreak',

							
							mathJaxLib : 'https://cdnjs.cloudflare.com/ajax/libs/mathjax/2.7.0/MathJax.js?config=TeX-AMS_HTML',
							uploadUrl : '/exam/ckeditorFileUpload',

						      // Configure your file manager integration. This example uses CKFinder 3 for PHP.
						      filebrowserBrowseUrl: '/exam/ckeditorFileUpload',
						      filebrowserImageBrowseUrl: '/exam/ckeditorFileUpload',
						      filebrowserUploadUrl: '/exam/ckeditorFileUpload',
						      filebrowserImageUploadUrl: '/exam/ckeditorFileUpload',
							

						      // Load the default contents.css file plus customizations for this sample.
						      contentsCss: [
						        'https://cdn.ckeditor.com/4.14.0/full-all/contents.css',
						        'https://ckeditor.com/docs/vendors/4.14.0/ckeditor/assets/css/pastefromgdocs.css'
						        
						      ],

						      // Configure the Enhanced Image plugin to use classes instead of styles and to disable the
						      // resizer (because image size is controlled by widget styles or the image takes maximum
						      // 100% of the editor width).
						      image2_alignClasses: ['image-align-left', 'image-align-center', 'image-align-right'],
						      image2_disableResizer: true,
						      
						      autoGrow_minHeight: 200,
						      autoGrow_maxHeight: 600,
						      autoGrow_bottomSpace: 50,

						}
						
				);
				//on change of question type update questionsTextEditorObj end
				
			} else {

				$("#" + $(this).val()).hide();
			}
		});
	
		$("#added-answers").empty();
		$("#added-answers-multi").empty();
		$("#added-answers-image").empty();
		$("#added-answers-video").empty();
		$("#qa-list").empty();
		
		$('#discriptive-question-desc').val('');
		$('.' + $('#question-type').val() + '-desc').val('');
		$(".answer-description").val('');
		$(".common-question").val('');
		$(".correct-answer").prop('checked', false);
		$('.filename').html('');
		$('.file-name').val('');
		$('.form-control').val('');
		$(".view-form").hide();
		$('#sectionId').val('').trigger('chosen:updated');;
		
		passageAnswerCount = 1;
		passageQuestionCount = 1;
		optionsCount = 0;
		answerBlock = "";
		passageAnswerBlock = "";
		passageQuestionAnswerCount = [];
		answerArray = [];
		filename = '';
	}

	function goBack(event) {
		event.preventDefault();
		$(".add-question-form").hide();
		$("#question-list").show();
	}

	function selectAll() {

		if ($("#select-all").prop("checked") == true) {
			$(".question-select").prop("checked", true);
		} else {
			$(".question-select").prop("checked", false);
		}
	}

	function deleteMultiple() {

		var selectedQuestions = [];
		
		$.each($(".question-select:checked"), function () {
			deleteQuestions($(this).val());
			selectedQuestions.push($(this).val());
		});

		if (selectedQuestions.length == 0) {
			alert("selected questions : " + selectedQuestions);
			return false;
		}

		$("#select-all").prop("checked", false);
		
	}

	function deleteQuestions(rowNumber) {

		$("#question-row-id-value-"+rowNumber).remove();
		
		tableRowCount--;
		
		checkQuestionList();
		
		deleteTestQuestionsAjax(rowNumber);
	}

	function deletePassageQuestion(questionNumber) {
		
		$("#passage-question-" + questionNumber).remove();
		
		passageQuestionCount--;
		
		passageAnswerCount = 1;
	}

	function viewQuestions(questionType) {

		var view = "#view-" + questionType;

		$('.view-form').hide();
		$('.add-question-form').hide();
		$('#question-list').hide();

		$(view).show();
	}

  
	function saveCaseStudyQuestion(questionDetails){
		
		return new Promise(resolve => {

				var saved = saveQuestionAjax(questionDetails)
					.then(success, failure)
			
				function success(data) {
					
					//We are perform multiple save for case study of saving 
					//the subquestion and main questions. 
					//This is generating multiple edit icons at the frontend.
					//Therefore as a temporary fix the page reload is added.
					
					location.reload();
					
					// Header Question and Summary Saved
					// Next Save Subquestions using the mainQuestionId
			
					var catcherror = '';
					var successData;
					var temparray = [];
					var errorMessageAfterSavingSubQuestion ='';
					
					try {
						if(!mainQuestionId){
							throw new Error("mainQuestionId is undefined");
						}else{
							// All good then fill the questionDetails object with subquestion
							if(passageQuestionCount > 1){
								for (let index = 1; index < passageQuestionCount; index++) {
									
									let subQuetion={};
									
									subQuetion.mainQuestionId = mainQuestionId;
									subQuetion.type = 2;
									subQuetion.marks = $('#subquestion-marks-'+index).val();
									subQuetion.description = '';
									subQuetion.isSubQuestion = 1;
									subQuetion.question = $('#subquestion_'+index).val();
									subQuetion.optionsList = makeAnswerArray('.is-correct-answer-'+index);
								
									temparray.push(subQuetion);
								}
								
								for (let i = 0; i < temparray.length; i++) {
																		
									var saved = saveQuestionAjax(temparray[i])
									.then(success, failure)
			
									function success(data) {
										successData +=data; 
									}
									function failure(data) {
										errorMessageAfterSavingSubQuestion=errorMessageAfterSavingSubQuestion+' Error in saving subquestion '+i+'.';
										
									}
									
								}
								
								console.debug("1518:saveCaseStudyQuestion promise success data = "+data);
								console.debug(errorMessageAfterSavingSubQuestion);
	
							}
						}
					} catch (error) {
						catcherror = error;
						console.debug("1525::saveCaseStudyQuestion error :"+error);
						console.debug("1526::saveCaseStudyQuestion errorMessageAfterSavingSubQuestion :"+errorMessageAfterSavingSubQuestion);
												
					} finally{
						// check for any uncaught exceptions, implementation pending
						if(catcherror.message != "mainQuestionId is undefined"){
							console.error(catcherror.message);
						}
					}
					
					refresh();
					resolve(false);
				}

				function failure(data) {
					refresh();
					resolve(false);
				}
			});
		
	}
	
	function saveSingleSelectQuestion(questionDetails) {
		return new Promise(resolve => {

			var saved = saveQuestionAjax(questionDetails)
				.then(success, failure)

			function success(data) {
			
				refresh();
				resolve(false);
			}

			function failure(data) {

				refresh();
				resolve(false);
			}
		});
	}
	
	function saveQuestionAjax(questionDetails) {
		
		var promiseObj = new Promise(function (resolve, reject) {
		console.group("saveQuestionAjax");
			var methodRetruns = false;
			// Making the json object which is to be sent to the testController
			var body = {
				'question': questionDetails.question,	
				'sapid': sapid,
				'url': questionDetails.url,
				'testId': testId,
				'type': questionDetails.type,
				'optionsList' : questionDetails.optionsList,
				'isSubQuestion' : questionDetails.isSubQuestion,
				'marks' : questionDetails.marks,
				'description': questionDetails.description,
				'active' : 'Y',
				'mainQuestionId' : questionDetails.mainQuestionId, // will only be applicable for casestudy question rest all will have value of -1 and not be saved in DB
				'createdBy' : sapid,
				'lastModifiedBy' : sapid,
				'uploadType' : questionDetails.uploadType, // will only be applicable for link question 
				'sectionId' : questionDetails.sectionId,
				'copyCaseThreshold' : questionDetails.copyCaseThreshold
			};
			
			//Check if the body is getting generated properly
			console.info(body);
			
			//Call the function saveSingleTestQuestion with the JSON object
			$.ajax({
				type: 'POST',
				url: '/exam/m/saveSingleTestQuestion',
				data: JSON.stringify(body),
				contentType: "application/json",
				dataType: "json",

			}).done(function (data) {
		
				mainQuestionId = data.mainQuestionId;
				
				console.info(questiontypeval);
			
				var editIcon = "<a href='/exam/editTestQuestion?id="+mainQuestionId+"'><i class='fa-solid fa-pen-to-square' title='edit'></i></a>";
				
				$("#edit-icon-append-"+tableRowDisplayCount).prepend(editIcon);
			
				showSnackBar("Success in saving question.");
				
				methodRetruns = true;
				
				resolve(methodRetruns);
				
			}).fail(function (xhr) {
				
				$("#alert").hide();
				$("#edit-icon-append-"+tableRowDisplayCount).parent().remove();
				
				showSnackBar("Error in saving question. Try Again.");
				
				methodRetruns = false;

				reject(methodRetruns);
			});
		});
		console.groupEnd();
		
		return promiseObj;

	}

	function deleteTestQuestionsAjax(id){
		var promiseObj = new Promise(function (resolve, reject) {
			console.group("deleteTestQuestionsAjax");
			var methodRetruns = false;
	
			//Mapping the ID of the question to be deleted
			var body = {
				'id': id
			};
		
			//Call the testController with the JSON object
			$.ajax({
				type: 'POST',
				url: '/exam/m/deleteTestQusetion',
				data: JSON.stringify(body),
				contentType: "application/json",
				dataType: "json",

			}).done(function (data) {

				console.debug("1654: deleteTestQuestionsAjax data ="+data);
				showSnackBar("Success in deleting question.");
				methodRetruns = true;
				resolve(methodRetruns);
				
			}).fail(function (xhr) {
				 
				showSnackBar("Error in deleting question. Try Again.");
				methodRetruns = false; 
				reject(methodRetruns);
			});
			
		});
		console.groupEnd();
		return promiseObj;
	}
	
	
</script>


</html>