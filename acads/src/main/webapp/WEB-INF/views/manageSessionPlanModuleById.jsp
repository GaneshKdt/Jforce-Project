<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!-->

<html class="no-js">
<!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/functions" prefix = "fn" %>
<%@page pageEncoding="UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<jsp:include page="jscss.jsp">
	<jsp:param value="Edit Session Plan" name="title" />
</jsp:include>


<!-- Include Editor style. -->
<link href="https://cdnjs.cloudflare.com/ajax/libs/froala-editor/2.5.1/css/froala_editor.pkgd.min.css" rel="stylesheet" type="text/css" />
<link href="https://cdnjs.cloudflare.com/ajax/libs/froala-editor/2.5.1/css/froala_style.min.css" rel="stylesheet" type="text/css" />
<link data-require="sweet-alert@*" data-semver="0.4.2" rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/sweetalert/1.1.3/sweetalert.min.css" />
 
    <!-- for medium editor start -->
 <script src="//cdn.jsdelivr.net/npm/medium-editor@latest/dist/js/medium-editor.min.js"></script>
<link rel="stylesheet" href="//cdn.jsdelivr.net/npm/medium-editor@latest/dist/css/medium-editor.min.css" type="text/css" media="screen" charset="utf-8">
 <!-- for medium editor end -->
   	
   	
<!-- css for accordian start -->
<style>
.panel-title > a:before {
    float: right !important;
    font-family: FontAwesome;
    content:"\f068";
    padding-right: 5px;
}
.panel-title > a.collapsed:before {
    float: right !important;
    content:"\f067";
}
.panel-title > a:hover, 
.panel-title > a:active, 
.panel-title > a:focus  {
    text-decoration:none;
}
 
 .editable{
 	border : 1px solid grey;
 }
 
 	.multiselect{
		border-radius: 2px; 
		display: inline-block;
		background-color: white;
	    border: 1px solid #bababa;
	}
	
	.option{
		float: none;
    	width: 20px;
    	display: inline;
   		vertical-align: middle;
	}

	.chapterinput{
	    outline: none;
	    border: none;
	    width: 100%;
	    margin-right: 5px;
	}
</style>
<!-- css for accordian end  -->


<body class="inside">

	<%@ include file="header.jsp"%>
	
	
	<!-- code for page start -->
	<div class="well well-lg">
	
	
	<ul class="breadcrumb">
	<li><a href="/">Home</a></li>
		<li><a href="/acads/admin/manageSessionPlan">Manage Session Plans</a></li>
	<li><a href="/acads/admin/editSessionPlan?id=${bean.sessionPlanId}">Edit Session Plan</a></li>
	<li class="active" >Manage Session Plan Module</li>
	</ul>
	
	<legend> Add LRs for ${bean.topic}.  </legend>
	
	<%@ include file="messages.jsp"%>
		
	<div class="panel-group">

	<c:if test="${action eq 'edit' }">
  <!-- code for add module start -->
  	<div class="panel panel-default">
    <div class="panel-heading">
      <h4 class="panel-title">
        <a class="btn btn-info btn-block" data-toggle="collapse" href="#collapse2">Edit Session Plan Module </a>
      </h4>
    </div>
    <div id="collapse2" class="panel-collapse collapse border border-primary">
      <div class="panel-body">
      	<form:form modelAttribute="module" method="post">
			<form:hidden path="id" value="${bean.id}"/> 
			<form:hidden path="cloneSyllabus" value="${ false }"/>
			<form:hidden path="sessionPlanId" value="${bean.sessionPlanId}"/> 
			<div class="row">
				<div class="container-fluid">
					
					
					<div class="row">
					<div class="">
						<div class="col-md-6">
							<div class="form-group">
								<label >Module Topic</label>
								<form:input path="topic" value="${bean.topic}" required="required"  placeholder="Module Topic"
									style="color:black; padding:6px 12px; width:100%;"/>
							</div>
						</div>
						
					</div>
					</div>
					
					
					<div class="row">
						
						<!-- ____________________________________ start of multiselect chapter ____________________________________ -->

						<div class='col-md-6'>
							<label for="selected" style="float: left">Chapter</label>
							<div class='multiselect' style="min-width: 100%; cursor: pointer;">
								<div id='edit'>
									<input id='chapterdata' class='chapterinput' type="text" onchange='storeData(chapterdata)' placeholder="Select Chapter">
									<span id='selected' style='color: gray; font-size: 1em'></span>
									<span class="fa fa-caret-down" style="float: right"></span>
								</div>
								<div id="options_edit" style="display: none; float:left; padding: 10px;">
								
									<c:set var="count" value="${ 1 }"></c:set>
									<c:choose>
										<c:when test="${ empty syllabus }">
											<label style="display: inline;">No chapters found.</label>
										</c:when>
										<c:otherwise>
											<c:forEach var="item" items="${ syllabus }">
												<span  style="cursor: pointer;">
													<input class="option" type="checkbox" id="item_${ count }" value="${ item.id }" chapterName="${ item.chapter }" onclick='storeData(`item_${ count }`)'>
													<label for="item_${ count }" style="display: inline; padding: 5px">${ item.chapter } - ${ item.title }</label>
												</span><br>
												<c:set var="count" value="${ count+1 }"></c:set>
											</c:forEach>
										</c:otherwise>
									</c:choose>
									
							 	</div>
							</div>
							<form:hidden id="chapter" path="chapter" value="" />
							<form:hidden id="chapters" path="chapters" value="" />
							
						</div>	
							<!-- ____________________________________ end of multiselect chapter ____________________________________ -->
					</div>
					
					
					<div class="row">
					<div class="well">
						
						<div class="col-md-6">
							<div class="form-group">
								<label >Session Number :  </label>
								<form:input type="number" path="sessionModuleNo" value="${bean.sessionModuleNo}"
									required="required"  placeholder="0" 
									style="color:black; padding:6px 12px; width:100%;"/>
							</div>
						</div>
						
						
						
					</div>
					</div>
					<br>
					
					<div class="row">
					<div class="">
						<div class="col-md-12">
							<div class="form-group">
								<label >Edit data of Module Outcones : </label>
								<textarea class="editable"  id="outcomes" name="outcomes"  path="outcomes" value="${bean.outcomes}"  rows="5" cols="150" >${bean.outcomes} </textarea>
							</div>
						</div>
						
					</div>
					</div>
					
					
					<div class="row">
					<div class="">
						<div class="col-md-12">
							<div class="form-group">
								<label >Edit data of Module Pedagogical Tool : </label>
								<textarea class="editable" id="pedo" name="pedagogicalTool" path="pedagogicalTool" value="${bean.pedagogicalTool}"  rows="5" cols="150" >${bean.pedagogicalTool}</textarea>
							</div>
						</div>
						
					</div>
					</div>
					
					<br>
					
					<div class="form-group">
						<button id="submit" name="submit" class="btn btn-large btn-primary"
						formaction="saveSessionPlanModule">Save Module</button>
					</div>
					
				</div>
			</div>
		</form:form>
      </div><!-- panelbody end -->
      <div class="panel-footer"></div>
    </div>
  </div>
  <!-- code for add module end -->
	</c:if>
	
	<c:if test="${action eq 'upload' }">	
  <div class="panel panel-default">
    <div class="panel-heading">
      <h4 class="panel-title">
        <a class="btn btn-info btn-block" 
         	href="/acads/admin/uploadVideoContentForSessionPlanModuleForm?sessionPlanModuleId=${bean.id}">
         	Add Session Recording for Session Plan Module
        </a>
      </h4>
    </div>
</div>
      
  <div class="panel panel-default">
    <div class="panel-heading">
      <h4 class="panel-title">
        <a class="btn btn-info btn-block" 
         	href="/acads/admin/uploadLRContentForSessionPlanModule?id=${bean.id}">
         	Upload Learning Resources For Session Plan Module
        </a>
      </h4>
    </div>
      
  </div>
  </c:if>  
  
	
	</div>	
	</div>
	<!-- code for page end -->
	
	

	
	<jsp:include page="footer.jsp" />
	
        <!-- jQuery (necessary for Bootstrap's JavaScript plugins) --> 
<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_ACADS_STATIC_RESOURCES')" />assets/js/bootstrap.js"></script> 


<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_ACADS_STATIC_RESOURCES')" />resources_2015/js/vendor/jquery-ui.min.js"></script>  
<script src="https://cdn.datatables.net/1.10.13/js/jquery.dataTables.min.js" ></script>
<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_ACADS_STATIC_RESOURCES')" />resources_2015/js/vendor/dataTables.bootstrap.js"></script>
<script src="https://cdn.datatables.net/buttons/1.2.4/js/dataTables.buttons.min.js" ></script>

<!-- For Forola TextBox Start -->


<!-- Include Editor JS files. -->
<script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/froala-editor/2.5.1//js/froala_editor.pkgd.min.js"></script>




<script>
	
	$('#outcomes').text('${bean.outcomes}');
	$('#pedo').text('${bean.pedagogicalTool}');
	
</script>

<script>var editor = new MediumEditor('.editable');</script>

<script>

let chapters = [];
let displayString = ""; 

$(document).ready (function(){

	$('#edit').on('click', function(event) {
		event.preventDefault();
		$('#options_edit').slideToggle()
	});
	
	$('#add').on('click', function(event) {
		event.preventDefault();
		$('#options_add').slideToggle()
	});

	$('#chapterdata').on('change', function(event) {
		let inputValue = $('#chapterdata').val();

		if(inputValue != null){
			displayString = inputValue;
			chapters.push(inputValue);
			$('#chapters').val(chapters); 
			$('#chapter').val(chapters); 
		}else{
			const index = chapters.indexOf(inputValue);
			if (index > -1) {
				chapters.splice(index, 1);
			}
		}

	});
		
});

	function storeData(instance){

		let selected = $("#"+instance).val();
		let chapterName = $('#'+instance).attr('chapterName')
		let inputValue = $('#chapterdata').val();
		
		if(displayString == 'Select Chapter'){
			displayString = ""
		}
		
		if($('#' + instance).is(":checked")){
			if(displayString.trim() == "")
				displayString = chapterName+" ";
			else
				displayString = displayString+", "+chapterName+",";
	
			chapters.push(selected);
			displayString = displayString.substring(0, (displayString.length-1));  
			if(displayString.trim() == "")
				displayString = 'Select Chapter';
			$('#selected').empty().append(displayString);
			$('#chapter').val(displayString); 
			$('#chapters').val(chapters); 
		}else{
			const index = chapters.indexOf(selected);
			if (index > -1) {
				chapters.splice(index, 1);
			}
			displayString  = displayString.replace(chapterName,'');
			displayString  = displayString.replace(",",'');
			if(displayString.trim() == "")
				displayString = 'Select Chapter';
			$('#selected').empty().append(displayString);
			$('#chapter').val(displayString); 
			$('#chapters').val(chapters); 
		}

	}
	
	$('#syllabus').DataTable({
		"lengthMenu": [5, 10, 15, 20],
	    "pageLength": 5
		});
</script>
</body>
</html>
