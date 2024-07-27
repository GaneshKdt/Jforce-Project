<!DOCTYPE html>


<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<html lang="en">




<jsp:include page="common/jscss.jsp">
	<jsp:param value="Share Feedback" name="title" />
</jsp:include>

<style>
.complete-profile-warpper .sz-content-wrapper.withBgImage .student-info-bar .student-image
	{
	border: 2px solid #000;
}

.complete-profile-warpper .sz-content-wrapper.withBgImage .student-info-bar ul.student-info-list li
	{
	color: #333;
}

table {
	border-collapse: separate;
	border-spacing: 80px 0;
}

td {
	padding: 10px 0;
}

input[type=radio]+label {
	display: inline-block;
	margin: -2px;
	padding: 4px 12px;
}
</style>

<body>


	<%@ include file="common/header.jsp"%>



	<div class="sz-main-content-wrapper complete-profile-warpper">

		<jsp:include page="common/breadcrum.jsp">
			<jsp:param value="Student Zone;Feedback" name="breadcrumItems" />
		</jsp:include>


		<div class="sz-main-content menu-closed">
			<div class="container">
				<%-- <%@ include file="common/left-sidebar.jsp" %> --%>


				<div class="sz-content-wrapper dashBoard withBgImage">
					<%@ include file="common/studentInfoBar.jsp"%>


					<h2 class="red text-capitalize"
						style="margin-top: -20px; text-align: center;">
						<p>SVKMS NMIMS UNIVERSITY</p>
						<p>NMIMS Global Access School for Continuing Education</p>
						<p>Student Feedback Form</p>
					</h2>
					<div class="clearfix"></div>
					<div class="panel-content-wrapper">
						<%@ include file="common/messages.jsp"%>



						<div>
							<p>Program : ${facultyCourseFeedBack.program }</p>
						</div>
						<div>
							<p>Semester:${facultyCourseFeedBack.sem}</p>
						</div>

						<div>
							<p>Course : ${facultyCourseFeedBack.program }</p>
						</div>

						<div>
							<p>Faculty :</p>
						</div>

						<div class="clearfix"></div>
						<h2 class="black">Students are requested to share feedback
							ratings</h2>
						<div class="clearfix"></div>

						<form:form action="saveFacultyCourseFeedBack" method="post"
							modelAttribute="facultyCourseFeedBack">
							<form:hidden path="q1CourseResponse" id="q1CourseResponse" />
							<form:hidden path="q2CourseResponse" id="q2CourseResponse" />
							<form:hidden path="q3CourseResponse" id="q3CourseResponse" />
							<form:hidden path="q4CourseResponse" id="q4CourseResponse" />
							<form:hidden path="q5CourseResponse" id="q5CourseResponse" />
							<form:hidden path="q6CourseResponse" id="q6CourseResponse" />
							<form:hidden path="q7CourseResponse" id="q7CourseResponse" />
							<form:hidden path="q8CourseResponse" id="q8CourseResponse" />
							<form:hidden path="q9CourseResponse" id="q9CourseResponse" />
							<fieldset>
								<div class="table-responsive">
									<table border="0" width="100%">
										<tr>
											<td>Q.NO</td>

											<td>Question</td>

											<td>Rating</td>
										</tr>

										<tr>
											<td>1</td>

											<td>
												<p>The subject matter covered in this course helped you
													to understand</p>
												<p>and learn it effectively</p>
											</td>

											<td><jsp:include page="facultyCourseRadioHelper.jsp">
													<jsp:param value="q1CourseResponse" name="nameAttribute" />
												</jsp:include></td>
										</tr>



										<tr>
											<td>2</td>

											<td>
												<p>The course achieved its learning objective (in case
													the learning objective of course was not stated,</p>
												<p>the learning objective understood by you)</p>
											</td>

											<td><jsp:include page="facultyCourseRadioHelper.jsp">
													<jsp:param value="q2CourseResponse" name="nameAttribute" />
												</jsp:include></td>
										</tr>


										<tr>
											<td>3</td>

											<td>
												<p>The course material (e.g. text, cases, readings
													material and reference material assigned etc) were helpful
													towards learning from the course</p>
											</td>

											<td><jsp:include page="facultyCourseRadioHelper.jsp">
													<jsp:param value="q3CourseResponse" name="nameAttribute" />
												</jsp:include></td>
										</tr>

										<tr>
											<td>4</td>

											<td>
												<p>The learning process adopted (e.g. interactive
													discussion in class, case analysis, class participation,
													group interaction and presentation work</p>
												<p>etc.) were helpful towards learning from the course</p>
											</td>

											<td><jsp:include page="facultyCourseRadioHelper.jsp">
													<jsp:param value="q4CourseResponse" name="nameAttribute" />
												</jsp:include></td>
										</tr>


										<tr>
											<td>5</td>

											<td>
												<p>The readings assigned for pre-class preparation
													during the course were well placed and balanced (if
													applicable)</p>
											</td>

											<td><jsp:include page="facultyCourseRadioHelper.jsp">
													<jsp:param value="q5CourseResponse" name="nameAttribute" />
												</jsp:include></td>
										</tr>

										<tr>
											<td>6</td>

											<td>
												<p>The Faculty adhered to the course outline/curriculum
													and teaching/session plan</p>
											</td>

											<td><jsp:include page="facultyCourseRadioHelper.jsp">
													<jsp:param value="q6CourseResponse" name="nameAttribute" />
												</jsp:include></td>
										</tr>


										<tr>
											<td>7</td>

											<td>
												<p>The different components of the course had an
													evaluation weight in relation to the work load assigned</p>
											</td>

											<td><jsp:include page="facultyCourseRadioHelper.jsp">
													<jsp:param value="q7CourseResponse" name="nameAttribute" />
												</jsp:include></td>
										</tr>

										<tr>
											<td>8</td>

											<td>
												<p>The Faculty provided the timely feedback on the
													various components of the course (quizzes, exams,
													assignments, projects, and class participation)</p>
											</td>

											<td><jsp:include page="facultyCourseRadioHelper.jsp">
													<jsp:param value="q8CourseResponse" name="nameAttribute" />
												</jsp:include></td>
										</tr>
										<tr>
											<td>9</td>

											<td>
												<p>All things considered, the course met my expectations
													and was an excellent course</p>
											</td>

											<td><jsp:include page="facultyCourseRadioHelper.jsp">
													<jsp:param value="q9CourseResponse" name="nameAttribute" />
												</jsp:include></td>
										</tr>
									</table>
								</div>

								<div class="col-sm-6">
									<button id="submit" name="submit" class="customBtn red-btn"
										formaction="saveFeedback">Save Feedback</button>
								</div>
							</fieldset>
						</form:form>
					</div>

				</div>


			</div>
		</div>
	</div>


	<jsp:include page="common/footer.jsp" />


</body>
<script>
    $(document).ready(function(){
    	 $('input[type=radio]').change(function() {
    		 var radioName = $(this).attr("name");
    		 var selectorText = null,radioButtonValue = null;
    		 if(radioName == 'q1CourseResponse'){
    			 	selectorText = "input[name="+radioName+"]:checked";
    			 	radioButtonValue = $( selectorText).attr("data-value");
    			 $("#q1CourseResponse").val(radioButtonValue);
    		 }
    		 if(radioName == 'q2CourseResponse'){
    			     selectorText = "input[name="+radioName+"]:checked";
    			     radioButtonValue = $( selectorText).attr("data-value");
    			 $("#q2CourseResponse").val(radioButtonValue);
    		 }
    		 if(radioName == 'q3CourseResponse'){
    			     selectorText = "input[name="+radioName+"]:checked";
    			     radioButtonValue = $( selectorText).attr("data-value");
    			 $("#q3CourseResponse").val(radioButtonValue);
    		 }
    		 if(radioName == 'q4CourseResponse'){
    			     selectorText = "input[name="+radioName+"]:checked";
    			     radioButtonValue = $( selectorText).attr("data-value");
    			 $("#q4CourseResponse").val(radioButtonValue);
    		 }
    		 if(radioName == 'q5CourseResponse'){
			     selectorText = "input[name="+radioName+"]:checked";
			     radioButtonValue = $( selectorText).attr("data-value");
			 	 $("#q5CourseResponse").val(radioButtonValue);
		 	 }
    		 if(radioName == 'q6CourseResponse'){
			     selectorText = "input[name="+radioName+"]:checked";
			     radioButtonValue = $( selectorText).attr("data-value");
			 $("#q6CourseResponse").val(radioButtonValue);
		 	 }
    		 if(radioName == 'q7CourseResponse'){
			     selectorText = "input[name="+radioName+"]:checked";
			     radioButtonValue = $( selectorText).attr("data-value");
			 $("#q7CourseResponse").val(radioButtonValue);
		 	 }
    		 if(radioName == 'q8CourseResponse'){
			     selectorText = "input[name="+radioName+"]:checked";
			     radioButtonValue = $( selectorText).attr("data-value");
			 $("#q8CourseResponse").val(radioButtonValue);
		 	 }
    		 if(radioName == 'q9CourseResponse'){
			     selectorText = "input[name="+radioName+"]:checked";
			     radioButtonValue = $( selectorText).attr("data-value");
			 $("#q9CourseResponse").val(radioButtonValue);
		 	 }
    		 
    		 
    		 
    	 });  
    })
    </script>

</html>