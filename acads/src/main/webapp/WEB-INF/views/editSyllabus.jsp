<!DOCTYPE html>
<html class="no-js">

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<jsp:include page="jscss.jsp">
	<jsp:param value="Edit Syllabus Details" name="title" />
</jsp:include>

<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.1.0/jquery.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-3-typeahead/4.0.2/bootstrap3-typeahead.min.js"></script>  
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css" />
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/js/bootstrap.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-multiselect/0.9.13/js/bootstrap-multiselect.js"></script>
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-multiselect/0.9.13/css/bootstrap-multiselect.css" />
<%@page pageEncoding="UTF-8" %>

<style>
	.highlight{
		color:#337ab7;
		
	}
	.highlight:hover {
		color:-webkit-link;
		cursor: pointer;
	}
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
	    color: white;
	    display: none;
	}
	.actionModal {
	  display: none; 
	  position: fixed; 
	  z-index: 3; 
	  padding-top: 150px; 
	  left: 0;
	  top: 0;
	  width: 100%; 
	  height: 100%; 
	  overflow: auto; 
	  background-color: rgb(0,0,0); 
	  background-color: rgba(0,0,0,0.4); 
	}
	
	.actionModal-content {
		font-weight: 400;
		background-color: #fefefe;
		margin: auto;
		padding: 20px;
		border: 1px solid #888;
		max-height: calc(100vh - 250px);
		overflow: auto; 
		border-radius: 4px;
		font-size: 1.2em;
	}
	
	.actionModal-content::-webkit-scrollbar {
	  width: 10px;
	}
	
	/* Track */
	.actionModal-content::-webkit-scrollbar-track {
	  background: #f1f1f1; 
	  border-radius: 4px;
	}
	 
	/* Handle */
	.actionModal-content::-webkit-scrollbar-thumb {
	  background: #888; 
	  border-radius: 4px;
	}
	
	/* Handle on hover */
	.actionModal-content::-webkit-scrollbar-thumb:hover {
	  background: #555; 
	}
	
	
	.actionModal-content b{
		font-weight: 700;
	}
	
	hr{
		border: 2px solid #c72127;
	}
</style>

<body class="inside">

	<%@ include file="header.jsp"%>


	<section class="content-container login">
		<div class="container-fluid customTheme" style="padding: 20px;">

			<%@ include file="messages.jsp"%>
				
			<div class="panel-body">
				
				<div class="snackbar" id="snackbar">Enter snackbar text here</div>	
					
				<div class="row" style="padding: 20px;">
					<p style="color: #c72127; font-size: 2.0em;">Syllabus : ${ subject }</p>	
					<div class="form-group" id="subejcts" style="padding: 20px;">
						<label for="chapter">Chapter</label>
						<input id="chapter" type="text" value="${ syllabus.chapter }">
						<label for="title">Title</label>
						<input id="title" type="text" value="${ syllabus.title }">
						<label for="topic">Topic</label>
						<textarea id="topic">${ syllabus.topic }</textarea>
						<label for="outcomes">Outcomes</label>
						<textarea id="outcomes">${ syllabus.outcomes }</textarea>
						<label for="pedagogicalTool">Pedagogical Tool</label>
						<textarea id="pedagogicalTool">${ syllabus.pedagogicalTool }</textarea>
						<input type="hidden" value='${ syllabus.id }' id='id'>
						<button id="updateWarning" class="btn btn-primary btn-lg" style="margin-top: 20px; background: #c72127; border: none;">Update</button>
					</div>
				</div>
					
			</div>
			
			<div id="warning" class="actionModal">
				<div class="actionModal-content" id="actionModel" style="max-width: 500px; text-align: center;">
					<div id="content" style="text-align: justify;"></div>
					<button type='button' class='btn btn-primary' id="closeWarning" 
					style="margin-top: 20px; margin-right: 15px; background: #c72127; border: none;">Close</button>
					<button type='button' class='btn btn-primary' id="updateConfirm" 
					style="margin-top: 20px; background: #c72127; border: none;">Update</button>
				</div>
			</div>
	
					
		</div>
	</section>

	<jsp:include page="footer.jsp" />
	
	<script src="https://cdn.datatables.net/1.10.13/js/jquery.dataTables.min.js" ></script>
	<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_ACADS_STATIC_RESOURCES')" />resources_2015/js/vendor/dataTables.bootstrap.js"></script>
	<script src="https://cdn.datatables.net/buttons/1.2.4/js/dataTables.buttons.min.js" ></script>
	<script type="text/javascript" src="//cdn.ckeditor.com/4.8.0/standard-all/ckeditor.js"></script>
	<script>
	CKEDITOR.replace('topic');
	CKEDITOR.replace('outcomes');
	CKEDITOR.replace('pedagogicalTool');
	</script>
	<script>

		let message;

		$(document).ready (function(){

			$('#updateWarning').on('click', function(){
				
				$('#content').html("<div style='text-align: center;'><i class='fa fa-exclamation-triangle' style='color:  #c72127;'></i>"+
					"<b>Confirm Action</b></div> <hr> The changes in syllabus will be reflected from the <b>next time "+
					"it is cloned for a session plan</b>. If you want to modify a session plan please edit it from the "+
					"<b>manage session plan</b> page.<div style='width: 100%; margin: auto;'></div>");
				$('#warning').show();
				
			});

			$('#updateConfirm').on('click', function(){
	
				let chapter = $('#chapter').val();
				let title = $('#title').val();
				let topic = CKEDITOR.instances['topic'].getData();
				let outcomes = CKEDITOR.instances['outcomes'].getData();
				let pedagogicalTool = CKEDITOR.instances['pedagogicalTool'].getData();
				let id = $('#id').val();
				
				let body = {
					'id':id,
					'chapter':chapter,
					'title':title,
					'topic':topic,
					'outcomes':outcomes,
					'pedagogicalTool':pedagogicalTool
				}
				
				console.log('body: '+JSON.stringify(body))
				
				$.ajax({
	
					type : "POST",
					contentType : "application/json",
					url : "/acads/updateSyllabus",   
					data : JSON.stringify(body),
					success : function(data) {
	
						message = data.message;
						$('#warning').hide();
						$('#snackbar').html(message);
						$('#snackbar').fadeIn(10);
						setTimeout(function(){
							$('#snackbar').fadeOut(1000);
							location = location;
						},500);
						
					},
					error : function(e) {
						
						alert("Please Refresh The Page.")
						console.log("ERROR: ", e);
						
					}
	
				})
				
			});
				
		});
	
		$('#closeWarning').on('click', function(){
	
			$('#warning').hide();
				
		});
		
		$("#subjectDetails").DataTable({
			"dom": `
				<'row'<'col-sm-3'l>
			   	<'row'<'col-sm-4 offset-sm-6'f>>
				<'row'<'col-sm-12'tr>> 
				<'row'<'col-sm-5 col-md-5'i><'col-sm-5 col-md-7'p>>`
		});
	</script>

</body>
</html>
