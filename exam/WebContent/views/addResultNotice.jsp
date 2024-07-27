 <!DOCTYPE html>
<html lang="en">
	
<%@page import="com.nmims.beans.Person"%>
<%@page import="com.nmims.beans.Page"%>
<%@page import="com.nmims.beans.StudentMarksBean"%>
        <%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>


<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
    <jsp:include page="adminCommon/jscss.jsp">
	<jsp:param value="Search Student Marks" name="title"/>
    </jsp:include>
    <!-- Froala wysiwyg editor CSS -->
<link href="<c:url value="/resources_2015/css/froala/froala_editor.min.css" />" rel="stylesheet">
<link href="<c:url value="/resources_2015/css/froala/froala_style.min.css" />" rel="stylesheet">
<link href="<c:url value="/resources_2015/css/froala/froala_content.min.css" />" rel="stylesheet">

<link href="<c:url value="/resources_2015/css/froala/themes/dark.min.css" />" rel="stylesheet">	
<link href="<c:url value="/resources_2015/css/froala/themes/grey.min.css" />" rel="stylesheet">	
<link href="<c:url value="/resources_2015/css/froala/themes/red.min.css" />" rel="stylesheet">	
<link href="<c:url value="/resources_2015/css/froala/themes/royal.min.css" />" rel="stylesheet">		
<link href="<c:url value="/resources_2015/css/froala/themes/blue.min.css" />" rel="stylesheet">		
    
    
    <body>
    	<%@ include file="adminCommon/header.jsp" %>
        <div class="sz-main-content-wrapper">
        
        	<jsp:include page="adminCommon/breadcrum.jsp">
			<jsp:param value="Exam;Search Student Marks" name="breadcrumItems"/>
			</jsp:include>
        	
            
            <div class="sz-main-content menu-closed">
                    <div class="sz-main-content-inner">
              				<jsp:include page="adminCommon/left-sidebar.jsp">
								<jsp:param value="" name="activeMenu"/>
							</jsp:include>
              				
              				
              				<div class="sz-content-wrapper examsPage">
              						<%@ include file="adminCommon/adminInfoBar.jsp" %>
              						<div class="sz-content">
								
											<h2 class="red text-capitalize">Result Notice</h2>
											<div class="clearfix"></div>
													<div class="panel-content-wrapper" style="min-height:450px;">
											<%@ include file="adminCommon/messages.jsp" %>
											<form:form  action="addResultNotice" method="post" modelAttribute="resultNotice">
													<fieldset>
													<div class="col-md-18">
													<div class="col-md-4">
																<div class="form-group">
																<label>  Year</label>
																<form:select id="year" path="year" type="text"	placeholder="Year" class="form-control"   itemValue="${resultNotice.year}">
																	<form:option value="">Select Year</form:option>
																	<form:options items="${yearList}" />
																</form:select>
															</div>
														</div>
														<div class="col-md-4">
															<div class="form-group">
															<label>  Month</label>
																<form:select id="month" path="month" type="text" placeholder="Month" class="form-control"  itemValue="${resultNotice.month}">
																	<form:option value="">Select Month</form:option>
																	<form:options items="${monthList}" />
																</form:select>
															</div>
															</div>
															

											<div class="col-md-4">
															 	<div class="form-group">
																<label >Program Structure List</label>
																<form:select id="program" path="program" type="text"	placeholder="Program Structure" class="form-control"  >
																	<form:option value="">Select Program Structure</form:option>
																	<form:options items="${programStructureList}" />
																</form:select>
									</div></div>
									<div class="col-md-4">
														<div class="form-group">
															<form:label path="programList" for="program">Programs</form:label>
															<form:select multiple="multiple" id="programList" path="programList"
																required="required" class="form-control"
																style="overflow: auto;">
																<form:option value="">Select Program</form:option>
																<form:options items="${programList}" />

															</form:select>
														</div></div>
														
														
													
					<div class="col-md-4">	
					<div class="form-group">
					<label> Type </label>
						<form:select id="type" path="type"
							type="text" placeholder="Type" class="form-control"
							required="required" itemValue="${resultNotice.type}">
							<form:option value="">Select Type</form:option>
							<form:option value="Final Result">Final Result</form:option>
							<form:option value="Assignment">Assignment</form:option>
							
						</form:select>
					</div>
					</div>
					
					<div class="col-md-4">	
														<div class="form-group">
															<label>   Title </label>
						
					
						<form:input id="title" path="title" type="text"
							placeholder="Title" class="form-control" required="required" value="${resultNotice.title}"/>
					
					</div>	</div>
					
					<div class="col-md-18" style="padding-top:15%;">	
					<div class="form-group">
					<label>   Description </label>
						<form:textarea path="description" id="editor" required="required" title="${resultNotice.description}"/>
					</div>	
					</div>	
															<div class="form-group">
															<label class="control-label" for="submit"></label>
															<div class="controls">
																<button id="submit" name="submit" class="btn btn-large btn-primary" formaction="addResultNotice">Add</button>
																<button id="reset" type="reset" class="btn btn-danger" type="reset">Reset</button>
																<button id="cancel" name="cancel" class="btn btn-danger" formaction="getAllStudentMarks" formnovalidate="formnovalidate">Cancel</button>
															</div>
														</div>
													</div>
											</fieldset>
									</form:form>
								</div>
								
							</div>
              			</div>
    				</div>
			   </div>
		    </div>
        <jsp:include page="adminCommon/footer.jsp"/>
        
		
    </body>
<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/js/vendor/froala_editor.min.js"></script>
<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/js/vendor/froala-plugins/tables.min.js"></script>
<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/js/vendor/froala-plugins/lists.min.js"></script>
<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/js/vendor/froala-plugins/colors.min.js"></script>
<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/js/vendor/froala-plugins/font_family.min.js"></script>
<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/js/vendor/froala-plugins/font_size.min.js"></script>
<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/js/vendor/froala-plugins/block_styles.min.js"></script>
<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/js/vendor/froala-plugins/media_manager.min.js"></script>
<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/js/vendor/froala-plugins/inline_styles.min.js"></script>
<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/js/vendor/froala-plugins/fullscreen.min.js"></script>
<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/js/vendor/froala-plugins/char_counter.min.js"></script>
<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/js/vendor/froala-plugins/entities.min.js"></script>
<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/js/vendor/froala-plugins/file_upload.min.js"></script>
<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/js/vendor/froala-plugins/urls.min.js"></script>

<script type="text/javascript">

$('#editor').editable({inlineMode: false,
    buttons: ['bold', 'italic', 'underline', 'sep', 
              'strikeThrough', 'subscript', 'superscript', 'sep',
              'fontFamily', 'fontSize', 'color', 'formatBlock', 'blockStyle', 'inlineStyle','sep',
              'align', 'insertOrderedList', 'insertUnorderedList', 'outdent', 'indent', 'selectAll','sep',
              'createLink', 'table','sep',
              'undo', 'redo', 'sep',
              'insertHorizontalRule', 'removeFormat', 'fullscreen'],
    minHeight: 200,
    paragraphy: false,
    placeholder: 'Enter Email Body here',
    theme: 'blue',
    key:'vA-16ddvvzalxvB-13C2uF-10A-8mG-7eC5lnmhuD3mmD-16==',
    toolbarFixed: false
});


/* for (i = 0; i < 23; i++) {
	 //Cannot have more than 23 failed subjects
	 $("#program"+i).depdrop({
       url: '/exam/getAvailableProgramsAsPerProgramStructure',
       depends: ['programList'+i]
   });
}
 */
 $(document)
	.ready(
			function() {
				  $('#program').change(function(){
				      $('.result').show();
				      $('#' + $(this).val()).show();    
				   });

				var program = '${program}';
				$("#program").on('change',function() {
									var program = $('#program').val();
									console.log("called");
									if (program){$.ajax({
													type : 'GET',
													url : '<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />getProgramStructByProgram?'
															+ 'program='
															+ program,
													success : function(data) {
														console.log("sucess");
														console.log(data);
														var json = JSON.parse(data);
														var optionsAsString = "";
														$('#programList').find('option').remove();
														console.log(json);
														for (var i = 0; i < json.length; i++) {
															var idjson = json[i];
															console.log(idjson);
															for ( var key in idjson) {
																console.log(key+ ""+ idjson[key]);
																optionsAsString += "<option value='" +key + "'>"
																		+ idjson[key]
																		+ "</option>";
															}
														}
														console.log("optionsAsString"+ optionsAsString);
														$('#programList').append(optionsAsString);

													}
												});
									} else {
										console.log("no course");
									}
								});
				
			});
</script>
</html>