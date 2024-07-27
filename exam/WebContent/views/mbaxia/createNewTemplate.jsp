<!DOCTYPE html>
<html lang="en">

<%@page import="com.nmims.beans.Person"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<jsp:include page="../adminCommon/jscss.jsp">
	<jsp:param value="Create Template" name="title" />
</jsp:include>
 <%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
 
<link rel="stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/chosen/1.5.1/chosen.min.css">

<script
	src="https://ajax.googleapis.com/ajax/libs/jquery/1.9.1/jquery.js"></script>

<script
	src="https://cdnjs.cloudflare.com/ajax/libs/chosen/1.5.1/chosen.jquery.min.js"></script>

<link rel="stylesheet" href="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />assets/test/css/style.css">
<!-- <link rel="stylesheet" href="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />assets/test/css/chosen.min.css" > -->
<!-- <script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />assets/test/js/chosen.jquery.min.js"></script> -->

<link
	href="https://gitcdn.github.io/bootstrap-toggle/2.2.2/css/bootstrap-toggle.min.css"
	rel="stylesheet">
<script src="https://code.jquery.com/jquery-3.3.1.js"
	integrity="sha256-2Kok7MbOyxpgUVvAk/HJ2jigOSYS2auK4Pfzbm7uH60="
	crossorigin="anonymous"></script>
<script
	src="https://gitcdn.github.io/bootstrap-toggle/2.2.2/js/bootstrap-toggle.min.js"></script>


<link rel="stylesheet"
	href="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />assets/js/ckeditor/ckeditor_responsive_table/plugin.css">

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
<body>

	<%@ include file="../adminCommon/header.jsp"%>
	<div class="sz-main-content-wrapper">


		<!-- Custom breadcrumbs as requirement is diff. Start -->
		<div class="sz-breadcrumb-wrapper">
			<div class="container-fluid">
				<ul class="sz-breadcrumbs">
					<li><a href="/exam/">Exam</a></li>
					<li><a href="/exam/viewAllTests">Tests</a></li>
					<li><a href="#">Create Template</a></li>

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

						<h2 class="red text-capitalize">Add / Edit Templates</h2>



						<div class="clearfix"></div>
						<div class="panel-content-wrapper" style="min-height: 450px;">
							<%@ include file="../adminCommon/messages.jsp"%>


							<div id="question-list">

								<button class="btn btn-primary" data-toggle="collapse"
									data-target="#collapseExample" aria-expanded="false"
									aria-controls="collapseExample">Create New</button>

								<div class="collapse" id="collapseExample">
									<div class="card card-body">
										<form modelAttribute="formBean" method="post"
											class="submitForm">
											<div class="col-lg-7 col-md-7 col=lg-5"
												style="margin-bottom: 2rem;">
												<div class="panel-body col-md-9 ">
													<div class="row ">
														<div class="col-md-12 form-group">
															<label>Template Name:</label> <input required="required"
																type="text" name="name" class="form-control" value="" />
														</div>
													</div>

													<div class="row ">
														<div class="col-lg-5 col-md-4 form-group">
															<label>Test Type:</label> <select name="testType"
																required="required" class="form-control">
																<option value="">Select Test Type</option>
																<option value="Test">Test</option>
																<option value="Assignment">Assignment</option>
																<option value="Project">Project</option>
															</select>
														</div>
													</div>
													<div class="row ">
														<div class="col-md-6">
															<label>Test Duration:</label>
														</div>
													</div>
													<div class="row ">
														<div class="col-md-12 col-xs-12 col-lg-12">
															<div class="form-group">
																<div class="row">
																	<div class="col-lg-3  col-md-3 col-sm-3">
																		<select class="form-control" id="day"
																			required="required">
																			<option>0</option>
																			<option>1</option>
																			<option>2</option>
																			<option>3</option>
																			<option>4</option>
																			<option>5</option>
																			<option>6</option>
																			<option>7</option>
																			<option>8</option>
																			<option>9</option>
																			<option>10</option>
																		</select>
																	</div>
																	<div class="col-lg-1 col-md-1 col-sm-1">
																		<label>Day</label>
																	</div>
																	<div class="col-lg-3 col-md-3 col-sm-3">
																		<select class="form-control" id="hours"
																			required="required">
																			<c:forEach var="i" begin="0" end="23">
																				<option><c:out value="${i}" /></option>
																			</c:forEach>
																		</select>
																	</div>
																	<div class="col-lg-1 col-md-1 col-sm-1">
																		<label>Hr</label>
																	</div>
																	<div class="col-lg-3  col-md-3 col-sm-3">
																		<select class="form-control" id="minutes"
																			required="required">
																			<option>0</option>
																			<option>10</option>
																			<option>15</option>
																			<option>20</option>
																			<option>25</option>
																			<option>30</option>
																			<option>35</option>
																			<option>40</option>
																			<option>45</option>
																			<option>50</option>
																		</select>
																	</div>
																	<div class="col-lg-1 col-md-1 col-sm-1">
																		<label>Min</label>
																	</div>
																</div>
															</div>
														</div>
													</div>
												</div>
												<input type="hidden" name="duration" class="duration" />
												<div class="panel-body col-md-12 parent"
													style="padding-top: 0px;">
													<%-- <div class="row ">
														<div class="col-md-4 ">
															<label>Question Type</label>
														</div>
														<div class="col-md-3 ">
															<label>No of Questions</label>
														</div>
														<div class="col-md-3 ">
															<label>Marks Per Question</label>
														</div>
													</div>

													<div class="row " id="add_type_row">
														<div class="col-md-4 col-sm-4 form-group">
															<input type="hidden" class="status" /> <select
																name="testQuestionConfigBean[0].type"
																required="required" class="form-control selType">
																<option value="">Select Question Type</option>
																<c:forEach var="qtype" items="${AllQuestionTypes}">
																	<option value="${qtype.id}">${qtype.typeName}</option>
																</c:forEach>
															</select>
														</div>
														<div class="col-md-3 col-sm-3 form-group">
															<select name="testQuestionConfigBean[0].minNoOfQuestions"
																required="required" class="form-control qnNo">
																<option value="">Select Number of Questions</option>
																<c:forEach var = "i" begin = "1" end = "10"> 
																<option value="${i}">${i}</option> 
																</c:forEach>
															</select>
														</div>
														<div class="col-md-3 col-sm-3 form-group">
															<select name="testQuestionConfigBean[0].questionMarks"
																required="required" class="form-control qnMarks">
																<option value="">Select Question Marks</option>
																<option value=1>1</option>
																<option value=2>2</option>
																<option value=3>3</option>
																<option value=4>4</option>
																<option value=5>5</option>
																<option value=6>6</option>
																<option value=7>7</option>
																<option value=8>8</option>
																<option value=9>9</option>
																<option value=10>10</option>
															</select>
														</div>
														<div class="col-md-2 col-sm-2 column">
															<div class="form-group">
																<a class="add_btn" style="margin: 0px"> <i
																	class="fa fa-plus-circle"
																	style="font-size: 27px; color: #28a745"></i>
																</a>
															</div>
														</div>
													</div> --%>

												</div>
												
												<div class="row">
										<!-- <div class="col-md-3 col-sm-3 col-xs-4 column">
											<label  for="showCalculator">Is Section</label>
										</div>  
										<div class="col-md-4 col-sm-6 col-xs-12 column"> 
											<div class="form-group ">
												<input type="radio" name="sectionApplicable"  value="Y"/> 
												Yes &nbsp&nbsp 
												<input type="radio" name="sectionApplicable" value="N"/>
												No         
											</div>
										</div> --> 
									</div>
									
									<div class="row add_section" >    
										<div class="col-md-3 col-sm-3 col-xs-4 column">
											<label style="vertical-align:middle;"  for="showCalculator">Add Section</label>
										</div>   
										<div class="col-md-3 col-sm-3 col-xs-4 column">
											<a > <i class="fa-solid fa-circle-plus section_plusbtn" style="font-size: 27px; color: #28a745"></i></a>
										</div> 
										
										<div class="sectionBean"></div>
										<div class="section_created"></div> 
									</div> <br>
									
												<div id="myModal" class="modal fade" role="dialog">
													<div class="modal-dialog modal-lg ">
													 
														<!-- Modal content-->
														<div class="modal-content">
														<form > 
															<div class="modal-header">
																<button type="button" class="close" data-dismiss="modal">&times;</button>
																<h4 class="modal-title">Add Section</h4>
															</div>
															<div class="modal-body modalBody">
															
																<!-- modal start -->
																<div class="sectionDiv" > 
																	<div class="row">
																		<div class="col-md-3 col-sm-3 col-xs-4 column">
																			<label path="sectionName" for="sectionName">Section Name</label>
																		</div>  
																		<div class="col-md-6 col-sm-8 col-xs-12 column">
																			<div class="form-group">
																			<input id="sectionName" path="sectionName" type="text" required="required" 
																					placeholder="Enter Section Name" class="form-control" 
																					 value="" />        
																			</div> 
																		</div>
																	</div> 
																	 
																	<div class="row">
																		<div class="col-md-3 col-sm-3 col-xs-4 column">
																			<label path="instructions" for="instructions">Instructions</label>
																		</div>  
																		<div class="col-md-6 col-sm-8 col-xs-12 column">
																			<div class="form-group">
																			<textarea  id="instructions"  placeholder="Enter Instructions" class="form-control"
																					 value="" > </textarea>         
																			</div>
																		</div>
																	</div>
																	<div class="row">
																		<div class="col-md-3 col-sm-3 col-xs-6 column">
																			<label path="allQnsMandatory" for="showCalculator">Randomize Questions</label>
																		</div>  
																		<div class="col-md-3 col-sm-3 col-xs-6 column">
																			<div class="form-group">
																				<input type="radio" name="sectionRandQns" id="YesW" value="Y" />
																				Yes &nbsp&nbsp
																				<input type="radio" name="sectionRandQns" id="NoW" value="N" />
																				No         
																			</div>  
																		</div>     
																			
																		<!-- <div class="col-md-3 col-sm-3 col-xs-6 column">
																			<label path="sectionRandQns" for="showCalculator">Randomize Questions</label>
																		</div>  
																		<div class="col-md-3 col-sm-3 col-xs-6 column"> 
																			<div class="form-group">
																				<input type="radio" name="sectionRandQns" id="YesW" value="Y"/>
																				Yes &nbsp&nbsp
																				<input type="radio" name="sectionRandQns" id="NoW" value="N"/>
																				No         
																			</div> 
																		</div> -->
																	</div>  
																	<div class="parent">  
																		<div class="row" id="add_section_qnType_row">
																			<div class="col-md-3 col-sm-3 col-xs-4 column">
																				<label for="showCalculator">Question Type</label>
																			</div>  
																			<div class="col-md-3 col-sm-3 col-xs-6 column">
																			<div class="form-group">  
																				<select id="sectionQnType" name="sectionQnTypeConfigbean[0].sectionQnType"  class="form-control sectionQnType " required="required">
																	        		<c:forEach var="type" items="${sectionQnTypes}" varStatus="status">
																	        		<option value="${type.id }">${type.typeName }</option>     
																	        		 </c:forEach>      
																	        	</select>   
																				</div>
																			</div>
																			<div class="col-md-2 col-sm-3 col-xs-6 column">
																			<div class="form-group">  
																				<input id="sectionTypeQnCount"  type="number"
																						placeholder="Count" class="form-control sectionTypeQnCount"
																						value="" name="sectionTypeQnCount" />   
																				</div>
						 													</div>
																			<div class="col-md-2 col-sm-3 col-xs-6 column">
																			<div class="form-group">  
																				<input id="sectionTypeQnMarks"  type="number"
																						placeholder="Marks" class="form-control sectionTypeQnMarks"
																						value="" name="sectionTypeQnMarks" />   
																				</div>
																			</div>   
																			<div class="col-md-1 col-sm-2 column">
																				<div class="form-group">
																					<a class="add_qnType_btn" style="margin: 0px"> <i
																						class="fa-solid fa-circle-plus"
																						style="font-size: 27px; color: #28a745"></i>
																					</a>
																				</div>
																			</div> 
																		</div> 
																	</div>
																</div> 
																<!-- modal end -->
																 
															</div>
															<div class="modal-footer">
															<button type="submit" class="btn btn-default saveSection"
																	>Save</button>    
															</div>
															</form>
														</div> 
														
													</div>
												</div>
												
												<button formaction="createNewTemplate"
													class="btn  btn-primary pull-left createTemplateBtn" style="margin: 0px;">Save</button>


											</div>
										</form>
									</div>
								</div>
								<div id="added-question-list">

									<table class="table table-bordered">
										<thead>
											<tr>
												<th>Sl No.</th>
												<th>Template Name</th>
												<th>Template Details</th>
												<th>Action</th>
											</tr>
										</thead>
										<tbody>
											<c:forEach var="type" items="${tTypes}" varStatus="status">

												<tr>
													<td><c:out value="${status.index+1}" /></br></td>
													<td><c:out value="${type.name}" /></br></td>
													<td style="max-width: 25rem;">
														<div class="template_info">
															<c:forEach var="bean" items="${templateMap}"
																varStatus="status">

																<c:if test="${bean.templateId eq type.templateId}">
																	<c:out value="${bean.typeName}" />-<c:out
																		value="${bean.minNoOfQuestions}" />
																	</br>
																</c:if>
															</c:forEach>
														</div>
														<form class="edit_form submitForm" style="display: none;"
															modelAttribute="formBean" method="post"
															action="editTestQnTemplate">
															<div class="col-lg-12 col-md-12 col=lg-12"
																style="margin-bottom: 2rem;">
																<div class="panel-body col-md-9 parent">
																	<c:set var="index" value="0" />
																	<div class="row ">
																		<div class="col-md-12 form-group">
																			<label>Template Name:</label> <input type="text"
																				name="name" class="form-control" required="required"
																				value="${type.name }" />
																		</div>
																	</div>
																	<div class="row ">
																		<div class="col-lg-12 col-md-12 col-sm-12 form-group">
																			<label>Template Type:</label> <select name="testType"
																				required="required" class="form-control">
																				<option disabled value="">Select IA Type</option>
																				<option selected value="${type.testType }">${type.testType }</option>
																				<option value="Assignment">Assignment</option>
																				<option value="Project">Project</option>
																			</select>
																		</div>
																	</div>
																	<div class="row ">
																		<div class="col-md-6">
																			<label>Test Duration:</label>
																		</div>
																	</div>
																	<div class="row ">
																		<div class="col-md-12 col-xs-12 col-lg-12">
																			<div class="form-group">
																				<div class="row">
																					<div class="col-lg-3  col-md-3 col-sm-3">
																						<select class="form-control" id="day"
																							required="required">
																							<option>0</option>
																							<option>1</option>
																							<option>2</option>
																							<option>3</option>
																							<option>4</option>
																							<option>5</option>
																							<option>6</option>
																							<option>7</option>
																							<option>8</option>
																							<option>9</option>
																							<option>10</option>
																						</select>
																					</div>
																					<div class="col-lg-1 col-md-1 col-sm-1">
																						<label>Day</label>
																					</div>
																					<div class="col-lg-3 col-md-3 col-sm-3">
																						<select class="form-control" id="hours"
																							required="required">
																							<option>0</option>
																							<option>1</option>
																							<option>2</option>
																							<option>3</option>
																							<option>4</option>
																						</select>
																					</div>
																					<div class="col-lg-1 col-md-1 col-sm-1">
																						<label>Hr</label>
																					</div>
																					<div class="col-lg-3  col-md-3 col-sm-3">
																						<select class="form-control" id="minutes"
																							required="required">
																							<option>0</option>
																							<option>10</option>
																							<option>15</option>
																							<option>20</option>
																							<option>25</option>
																							<option>30</option>
																							<option>35</option>
																							<option>40</option>
																							<option>45</option>
																							<option>50</option>
																						</select>
																					</div>
																					<div class="col-lg-1 col-md-1 col-sm-1">
																						<label>Min</label>
																					</div>
																				</div>
																			</div>
																		</div>
																	</div>
																	<input type="hidden" value="${type.templateId }"
																		name="templateId" /> <input type="hidden"
																		name="duration" value="${type.duration }"
																		class="duration" />
																</div> 
																  
																<div class="col-lg-12 col-md-12 col-lg-12"
																style="margin-bottom: 2rem;"> 
																	<a href="editSectionForm?id=${type.templateId}">Edit Section</a> 
																</div> 
															</div>
															<button formaction="editTestQnTemplate"
																class="btn btn-sm btn-primary ">save</button>
															<button class="btn btn-sm  cancel_btn">Cancel</button>
														</form>
													</td>
													<td>
														<button class="btn btn-sm btn-primary edit_template">edit</button>
														<a href="deleteTestQnTemplate?id=${type.templateId}"><button
																class="btn btn-sm">delete</button></a>
													</td>
												</tr>
											</c:forEach>

										</tbody>
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

<script
	src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-datetimepicker/4.17.37/js/bootstrap-datetimepicker.min.js"></script>

<script type="text/javascript"
	src="//cdn.ckeditor.com/4.14.0/standard-all/ckeditor.js"></script>
<script type="text/javascript"
	src="//cdnjs.cloudflare.com/ajax/libs/mathjax/2.7.0/MathJax.js?config=TeX-AMS_HTML"></script>
 	

<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />assets/js/ckeditor/ckeditor_responsive_table/plugin.js"></script>



</html>
<script>
$(".duration").each(function(){//convert duration to day hour min format
	var duration = $(this).val();     
	var day= Math.floor((duration/60)/24);   
	var remainingMins=duration - (day*24*60); 
	var hours = Math.floor(remainingMins/60); 
	var minutes = remainingMins % 60;   
	 
	$(this).closest(".submitForm").find("#day").val(day);       
	$(this).closest(".submitForm").find("#hours").val(hours); 
	$(this).closest(".submitForm").find("#minutes").val(minutes);   
	   
});     

$(document).on('change', '#day', function(){    
	calculateDuration($(this)); 
});
$(document).on('change', '#hours', function(){  
	calculateDuration($(this)); 
});
$(document).on('change', '#minutes', function(){  
	calculateDuration($(this));
});
function calculateDuration(thisElement){
	var d = $(thisElement).closest(".submitForm").find("#day").val();  
	var hr =$(thisElement).closest(".submitForm").find("#hours").val();   
	var min =$(thisElement).closest(".submitForm").find("#minutes").val(); 
	       
	var durInHr =  (d*24)+parseInt(hr); 
	    
	var durInMin = parseInt(durInHr* 60) + parseInt(min);    
	$(thisElement).closest(".submitForm").find(".duration").val(durInMin);      
}
$(".add_btn").on('click', function() {   
	var parent =$(this).closest(".parent");
	var clon=$(this).closest(".parent").find("#add_type_row").first().clone(); 
	clearAllValuesInClone(clon);           
	clon.find(".add_btn").first().html(      
		'<i class="fa-solid fa-circle-minus" style="font-size: 27px;color: #dc3545;"></i>'
      );
	clon.find(".add_btn").first().attr({
		'class':'remove_btn'  
	});     
	$(this).closest(".parent").append(clon);     
	changeName(parent);
}); 
$(".add_qnType_btn").on('click', function() {   
	var parent =$(this).closest(".parent");
	var clon=$(this).closest(".parent").find("#add_section_qnType_row").first().clone(); 
	clearAllValuesInCloneForSection(clon);           
	clon.find(".add_qnType_btn").first().html(       
		'<i class="fa-solid fa-circle-minus" style="font-size: 27px;color: #dc3545;"></i>'
      );
	clon.find(".add_qnType_btn").first().attr({
		'class':'remove_qnType_btn'  
	});   
    
	$(this).closest(".parent").append(clon);     
	changeName(parent);  
}); 
$(document).on('click', '.remove_btn', function(){
	 var parent =$(this).closest(".parent");
	$(this).closest("#add_type_row").remove();    
	     
	changeName($(parent));   
}); 
$(document).on('click', '.remove_qnType_btn', function(){
	 var parent =$(this).closest(".parent");
	$(this).closest("#add_section_qnType_row").remove();      
	     
	changeSectionQnTypeName($(parent));   
});
function clearAllValuesInClone(clon){  
	clon.find(".selType").first().val(''); 
	clon.find(".qnNo").first().val('');   
}
function clearAllValuesInCloneForSection(clon){  
	clon.find(".sectionQnType").first().val(''); 
	clon.find(".sectionTypeQnCount").first().val('');  
	clon.find(".sectionTypeQnMarks").first().val('');
}
$(document).on('change', '.selType', function(){       
	var mark=1;  
	if($(this).val()==4){
		mark=3;
	}                 
	$(this).closest(".row").find(".qnMarks").val(mark); 
});  
function changeName(parent){
	var i=0;     
	$(parent).find(".selType").each(function(){
		$(this).attr({'name':'testQuestionConfigBean['+i+'].type'})
		i++;   
	});  
	var i=0;      
	$(parent).find(".qnNo").each(function(){
		$(this).attr({'name':'testQuestionConfigBean['+i+'].minNoOfQuestions'})
		i++;   
	});
	var i=0;      
	$(parent).find(".qnMarks").each(function(){  
		$(this).attr({'name':'testQuestionConfigBean['+i+'].questionMarks'})
		i++;   
	}); 
	
}
function changeSectionQnTypeName(parent){
	var i=0;     
	$(parent).find(".sectionQnType").each(function(){
		$(this).attr({'name':'sectionQnTypeConfigbean['+i+'].type'})
		i++;   
	});  
	var i=0;      
	$(parent).find(".sectionTypeQnCount").each(function(){
		$(this).attr({'name':'sectionQnTypeConfigbean['+i+'].minNoOfQuestions'})
		i++;   
	});
	$(parent).find(".sectionTypeQnMarks").each(function(){
		$(this).attr({'name':'sectionQnTypeConfigbean['+i+'].marks'})
		i++;   
	}); 
}
$(document).on('click', '.edit_template', function(){       
	$(this).closest('tr').find(".edit_form").show();     
	$(this).closest('tr').find(".template_info").hide();   
}); 

$(document).on('click', '.cancel_btn', function(e){
	e.preventDefault();  
	$(this).closest('tr').find(".edit_form").hide();       
	$(this).closest('tr').find(".template_info").show();   
}); 
$('input[name=sectionApplicable]').change(function() { 
	if($(this).val()=="Y"){
		$(".add_section").show(); 
		$(".section_plusbtn").click();
	}else{
		$(".add_section").hide();  
	} 
});  
$(document).on('click', '.section_plusbtn', function(){
	$('#myModal').find('input:text').val('');      
	$('#myModal').find('select').val('');    
	$('#myModal').find('textarea').val(''); 
	$('.select2-selection__choice').remove(); 
	$("#sectionQnCount").val('');   
	$('input[name=allQnsMandatory]').attr('checked',false);
	$('input[name=sectionRandQns]').attr('checked',false);     
	$('#myModal').modal('show');      
}); 
 
var jsonObj = [];
$(document).on('click', '.saveSection', function(e){ 
	e.preventDefault();    
	var section = $(this).closest("#myModal"); 
	var sectionName = section.find("#sectionName").val();       
	var sectionDay = section.find("#sectionDay").val();
	var sectionHours = section.find("#sectionHours").val();
	var sectionMinutes = section.find("#sectionMinutes").val();
	var instructions = section.find("#instructions").val();
	var allQnsMandatory = section.find('input[name=allQnsMandatory]:checked').val();  
	var sectionRandQns = section.find('input[name=sectionRandQns]:checked').val();   
	var sectionQnCount = section.find("#sectionQnCount").val();
	var sectionQnType = section.find("#sectionQnType").val();  
	var sectionDurInHr =  (sectionDay*24)+parseInt(sectionHours); 
	var sectionDurInMin = parseInt(sectionDurInHr* 60) + parseInt(sectionMinutes); 
	
	//section form validations
	var validation=true;
	if( (sectionName==null) ||(sectionDurInMin==null) || (instructions==null)||(sectionRandQns==null) ){
		validation= false;
	}
	section.find(".sectionTypeQnCount").each(function() {    
		if($(this).val().trim()=="") { validation= false;}  });    
	section.find(".sectionTypeQnMarks").each(function() { 
		if($(this).val().trim()=="") {validation= false;}  }); 
	section.find(".sectionQnType").each(function() { 
		if($(this).val().trim()=="") {validation= false;}  });    
	 if(validation==false){return false;} 
	//section form validations ends
	
	item = {};
	item ["sectionName"] = sectionName;
	item ["sectionDur"] = sectionDurInMin; 
	item ["instructions"] = instructions; 
	item ["allQnsMandatory"] = allQnsMandatory;  
	item ["sectionRandQns"] = sectionRandQns;  
	item ["sectionQnCount"] = sectionQnCount;  
	item ["sectionQnType"] = sectionQnType; 
	sectionQnTypeConfigbean=[]; 
	   
	 
	var sectionQnCount =[];
	section.find(".sectionTypeQnCount").each(
		    function() { sectionQnCount.push($(this).val());  }); 
	var sectionQnMarks=[]; 
	section.find(".sectionTypeQnMarks").each(
		    function() { 
		    	sectionQnMarks.push($(this).val());  }); 
	var i =0;
	section.find(".sectionQnType").each(function() {    
		    	sectionQnTypeConfigbean.push({
		    		sectionQnName:$(this).find("option:selected").text(), 
		    		sectionQnType: $(this).val(),
		    		sectionQnCount:sectionQnCount[i] , 
		    		questionMarks:sectionQnMarks[i]
		    		});    
		    	i++;    
	});    
	 
	item["sectionQnTypeConfigbean"]=sectionQnTypeConfigbean;
	jsonObj.push(item);  
	console.log(jsonObj);    
    $(".section_created").html('<table  class="table table-bordered"><thead><tr><th>Section Name</th>'+
    '<th>Instructions</th><th>Section Questions Mandatory</th><th>Section Question Type-Section Question Count(Marks)</th><th>Action</th><tr/></thead><tbody class="section_created_tbody"></tbody></table>');
    fillSectionTable();
    $('#myModal').modal('toggle');  
});  
function deleteSection(i){ 
	jsonObj.splice(i, 1);    
	fillSectionTable();
}

function fillSectionTable(){   
	if(jsonObj.length==0){
		$(".section_created").html("");	
	}  
	$(".section_created_tbody").html("");
	$(".sectionBean").html("");
	$.each( jsonObj, function( i,val ) {
		
		var qnTypeList=""; 
		var qnType_Count="";
		$.each( val.sectionQnTypeConfigbean, function( j,data ){   
			qnTypeList=qnTypeList+'<input type="hidden" name="sectionBean['+i+'].sectionQnTypeConfigbean['+j+'].sectionQnType" value="'+data.sectionQnType+'"/>'+
			'<input type="hidden" name="sectionBean['+i+'].sectionQnTypeConfigbean['+j+'].sectionQnCount" value="'+data.sectionQnCount+'"/>'
			+'<input type="hidden" name="sectionBean['+i+'].sectionQnTypeConfigbean['+j+'].questionMarks" value="'+data.questionMarks+'"/>';
			qnType_Count=qnType_Count+data.sectionQnName+"-"+data.sectionQnCount+"("+data.questionMarks+"Marks)<br>"; 
			    
		});        
	$(".sectionBean").append('<input type="hidden" name="sectionBean['+i+'].sectionName" value="'+val.sectionName+'"/> '+
			'<input type="hidden" name="sectionBean['+i+'].sectionDur" value="'+val.sectionDur+'"/>' +
			'<input type="hidden" name="sectionBean['+i+'].instructions" value="'+val.instructions+'"/>' +
			'<input type="hidden" name="sectionBean['+i+'].sectionRandQns" value="'+val.sectionRandQns+'"/>' + 
			'<input type="hidden" name="sectionBean['+i+'].sectionQnCount" value="'+val.sectionQnCount+'"/>'+qnTypeList);   
	$(".sectionBean").append("<br><br>");        
		    
	$(".section_created_tbody").append('<tr><td>'+val.sectionName+'</td> '+
			   
			'<td>'+val.instructions+'</td> '+  
			'<td>'+val.sectionRandQns+'</td> '+  
			'<td>'+qnType_Count+'</td><td><i class="fa-solid fa-circle-minus" onClick="deleteSection('+i+')" style="font-size: 27px;color: #dc3545;"></i></td></tr> '); 
	});
}
$(document).on('click', '.createTemplateBtn', function(e){  
	if(jsonObj.length==0){     
		alert("section not added");
		return false;  
	}
}); 

</script>