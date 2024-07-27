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
 
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/chosen/1.5.1/chosen.min.css">
<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.9.1/jquery.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/chosen/1.5.1/chosen.jquery.min.js"></script>

<link rel="stylesheet" href="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />assets/test/css/style.css">
<!-- <link rel="stylesheet" href="assets/test/css/chosen.min.css" > -->
<!-- <script src="assets/test/js/chosen.jquery.min.js"></script> -->

<link
	href="https://gitcdn.github.io/bootstrap-toggle/2.2.2/css/bootstrap-toggle.min.css"
	rel="stylesheet">
<script src="https://code.jquery.com/jquery-3.3.1.js"
	integrity="sha256-2Kok7MbOyxpgUVvAk/HJ2jigOSYS2auK4Pfzbm7uH60="
	crossorigin="anonymous"></script>
<script
	src="https://gitcdn.github.io/bootstrap-toggle/2.2.2/js/bootstrap-toggle.min.js"></script>


<link rel="stylesheet" href="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />assets/js/ckeditor/ckeditor_responsive_table/plugin.css">

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

						<h2 class="red text-capitalize">Add / Edit Section</h2>
						<br> 
						<div class="row">
							<div class="col-md-3 col-sm-3 col-xs-4 column">
								<div class="form-group">
									<a class="section_plusbtn" style="margin: 0px"> <i
										class="fa fa-plus-circle"
										style="font-size: 27px; color: #28a745"></i>
									</a>
								</div>
							</div> 
						</div>  
						<table class="table table-bordered">
							<thead>
								<tr>
									<th>Section Name</th> 
									<th>Instructions</th>
									<th>Randomize Questions</th> 
									<th>Section Question Type - Count</th>
									<th>Action</th>
								<tr />
							</thead>
							<tbody class="section_created_tbody">
								
								<c:forEach var="section" items="${sectionList}" varStatus="status">
				        			<tr><td>${section.sectionName}</td> 
									<td>${section.instructions}</td> 
									<td>${section.sectionRandQns}</td>  
									<td>
										<c:forEach var="qnConfig" items="${section.sectionQnTypeConfigbean}" varStatus="status">
											${qnConfig.sectionQnType} - ${qnConfig.sectionQnCount} (${qnConfig.questionMarks} Marks)  
											<br>    
										</c:forEach> 
									</td>       
									<td><i class="fa-solid fa-circle-minus" onClick="deleteSection('${section.id}');"   style="font-size: 27px;color: #dc3545;"></i></td>  </tr>   
				        		 </c:forEach>   
								  
							</tbody>
						</table>
					</div>

					</div>
				</div>
			</div>
		</div> 
		<div id="myModal" class="modal fade" role="dialog">
			<div class="modal-dialog modal-lg ">
			 
				<!-- Modal content-->
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal">&times;</button>
						<h4 class="modal-title">Add Section</h4>
					</div>
					<form action="saveSectionsForTemplate" modelAttribute="section" method="post" class="submitForm">
					<div class="modal-body modalBody">
					
						<!-- modal start -->
						<div class="sectionDiv" > 
							<div class="row">
								<div class="col-md-3 col-sm-3 col-xs-4 column">
									<label path="sectionName" for="sectionName">Section Name</label>
								</div>  
								<div class="col-md-6 col-sm-8 col-xs-12 column">
									<div class="form-group">
									<input id="sectionName"  name="sectionName" type="text"
											placeholder="Enter Section Name" class="form-control" required="required"
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
									<textarea  id="instructions"  placeholder="Enter Instructions" class="form-control"  required="required"
											 value="" name="instructions" > </textarea> 
										   	
									<input type="hidden" value="${templateId}" name="templateId"/>    		        
									</div>
								</div>
							</div>
							<div class="row">
								 
								<div class="col-md-3 col-sm-3 col-xs-6 column">
									<label path="sectionRandQns" for="showCalculator">Randomize Questions</label>
								</div>  
								<div class="col-md-3 col-sm-3 col-xs-6 column"> 
									<div class="form-group">
										<input type="radio" name="sectionRandQns" id="YesW" value="Y" required="required"/>
										Yes &nbsp&nbsp
										<input type="radio" name="sectionRandQns" id="NoW" value="N"/>
										No         
									</div> 
								</div>
							</div>   
							<div class="parent"> 
								<div class="row" id="add_section_qnType_row">
									<div class="col-md-3 col-sm-3 col-xs-4 column">
										<label for="showCalculator">Question Type</label>
									</div>  
									<div class="col-md-3 col-sm-3 col-xs-6 column">
									<div class="form-group">  
										<select id="sectionQnType" name="sectionQnTypeConfigbean[0].sectionQnType"  class="form-control sectionQnType "  required="required">
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
												value="" name="sectionQnTypeConfigbean[0].sectionQnCount"  required="required" />  
										</div>
									</div>
									<div class="col-md-2 col-sm-3 col-xs-6 column">
										<div class="form-group">
											<input id="sectionTypeQnMarks" type="number"
												placeholder="Marks" class="form-control sectionTypeQnMarks"
												value="" name="sectionQnTypeConfigbean[0].questionMarks"  required="required"/>
										</div>
									</div> 
									<div class="col-md-1 col-sm-2 column">
										<div class="form-group">
											<a class="add_qnType_btn" style="margin: 0px"> <i
												class="fa fa-plus-circle"
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
							formaction="saveSectionsForTemplate">Save</button>        
					</div> 
					</form>
				</div> 
			</div>
		</div>
	<div id="snackbar">Some text some message..</div>
	<jsp:include page="../adminCommon/footer.jsp" />


</body>
<!-- <script src="../../test/js/default.js"></script> -->
<script>  
function deleteSection(id){
	var data = {id:id} 
	$.ajax({
		type : "POST",
		contentType : "application/json",
		url : "deleteSectionbyId",   
		data : JSON.stringify(data),
		success : function(data) {
			console.log(data); 
			location.reload();  
		},
		error : function(e) { 
			alert("Please Refresh The Page.") 
			console.log("ERROR: ", e);
			display(e);
		}
	});
}
$(document).on('click', '.section_plusbtn', function(){  
	$('#myModal').find('select').val('');    
	$('#myModal').find('textarea').val(''); 
	$('.select2-selection__choice').remove(); 
	$("#sectionQnCount").val('');   
	$('input[name=allQnsMandatory]').attr('checked',false);
	$('input[name=sectionRandQns]').attr('checked',false);     
	$('#myModal').modal('show');      
}); 
$(document).on('change', '#sectionDay', function(){    
	calculateDuration($(this)); 
});
$(document).on('change', '#sectionHours', function(){  
	calculateDuration($(this)); 
});
$(document).on('change', '#sectionMinutes', function(){  
	calculateDuration($(this)); 
});
function calculateDuration(thisElement){
	var d = $(thisElement).closest(".submitForm").find("#sectionDay").val();  
	var hr =$(thisElement).closest(".submitForm").find("#sectionHours").val();   
	var min =$(thisElement).closest(".submitForm").find("#sectionMinutes").val(); 
	       
	var durInHr =  (d*24)+parseInt(hr); 
	    
	var durInMin = parseInt(durInHr* 60) + parseInt(min);    
	$(thisElement).closest(".submitForm").find(".sectionDuration").val(durInMin);      
}
$(".add_qnType_btn").on('click', function() { 
	var parent =$(this).closest(".parent");
	var clon=$(this).closest(".parent").find("#add_section_qnType_row").first().clone(); 
	clearAllValuesInCloneForSection(clon);           
	clon.find(".add_qnType_btn").first().html(       
		'<i class="fa fa-minus-circle" style="font-size: 27px;color: #dc3545;"></i>'
      );
	clon.find(".add_qnType_btn").first().attr({
		'class':'remove_qnType_btn'  
	});   
    
	$(this).closest(".parent").append(clon);     
	changeSectionQnTypeName(parent);     
});
function changeSectionQnTypeName(parent){
	var i=0;     
	$(parent).find(".sectionQnType").each(function(){
		$(this).attr({'name':'sectionQnTypeConfigbean['+i+'].sectionQnType'})
		i++;   
	});  
	var i=0;      
	$(parent).find(".sectionTypeQnCount").each(function(){
		$(this).attr({'name':'sectionQnTypeConfigbean['+i+'].sectionQnCount'})
		i++;   
	}); 
	var i=0;    
	$(parent).find(".sectionTypeQnMarks").each(function(){
		$(this).attr({'name':'sectionQnTypeConfigbean['+i+'].questionMarks'})
		i++;   
	});  
}
function clearAllValuesInCloneForSection(clon){  
	clon.find(".sectionQnType").first().val(''); 
	clon.find(".sectionTypeQnCount").first().val('');  
	clon.find(".sectionTypeQnMarks").first().val(''); 
} 
$(document).on('click', '.remove_qnType_btn', function(){
	 var parent =$(this).closest(".parent");
	$(this).closest("#add_section_qnType_row").remove();      
	     
	changeSectionQnTypeName($(parent));   
}); 
</script>