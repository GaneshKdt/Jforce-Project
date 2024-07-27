<!DOCTYPE html>
<html class="no-js">

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<jsp:include page="jscss.jsp">
	<jsp:param value="Syllabus" name="title" />
</jsp:include>

<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.1.0/jquery.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-3-typeahead/4.0.2/bootstrap3-typeahead.min.js"></script>  
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css" />
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/js/bootstrap.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-multiselect/0.9.13/js/bootstrap-multiselect.js"></script>
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-multiselect/0.9.13/css/bootstrap-multiselect.css" />

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
</style>

<body class="inside">

	<%@ include file="header.jsp"%>


	<section class="content-container login">
		<div class="container-fluid customTheme" style="padding: 20px;">

			<%@ include file="messages.jsp"%>
				
			<div class="panel-body">
				
				<div class="snackbar" id="snackbar">Enter snackbar text here</div>	
				
				<div class="row" style="padding: 20px;">
					<p style="color: #c72127; font-size: 2.0em;">Syllabus</p>	
					<div class="form-group" id="subejcts" style="padding: 20px;">
						<table  class="table table-striped" id="subjectDetails">
							<thead>
								<tr>
									<th>Sr No.</th>
									<th>Subject</th>
									<th>Semester</th>
									<th>Edit</th>
									<th>Delete</th>
								</tr>
							</thead>
							<tbody>
								<c:set value="1" var="count"></c:set>
								<c:forEach items="${ syllabus }" var="details">
									<tr>
										<td>${ count }</td>
										<td>${ details.subjectname }</td>
										<td>${ details.sem }</td>
										<td>
											<a href="/acads/syllabusDetails?subjectCodeMappingId=${ details.subjectCodeMappingId }">
											<i style="font-size:20px;" class="fa-solid fa-pen-to-square"  aria-hidden="true"></i>
											</a>
										</td>
										<td>
										<i style="font-size:20px;" class="fa-regular fa-trash-can highlight" aria-hidden="true" subjectCodeMappingId="${ details.subjectCodeMappingId }">
										</i>
										</td>
									</tr>
									<c:set value="${ count+1 }" var="count"></c:set>
								</c:forEach>
							</tbody>
						</table>
					</div>
				</div>
				
			</div>
			
		</div>
	</section>

	<jsp:include page="footer.jsp" />
	
	<script src="https://cdn.datatables.net/1.10.13/js/jquery.dataTables.min.js" ></script>
	<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_ACADS_STATIC_RESOURCES')" />resources_2015/js/vendor/dataTables.bootstrap.js"></script>
	<script src="https://cdn.datatables.net/buttons/1.2.4/js/dataTables.buttons.min.js" ></script>
	<script>

		$(document).ready (function(){

			$('.highlight').on('click', function(){

				let subjectCodeMappingId = $(this).attr('subjectCodeMappingId');
				console.log("clicked: "+subjectCodeMappingId)
				let body = {
						'subjectCodeMappingId':subjectCodeMappingId
						}
				
				$.ajax({

					type : "POST",
					contentType : "application/json",
					url : "/acads/deleteSyllabus",   
					data : JSON.stringify(body),
					success : function(data) {
						
						$('#snackbar').html(data.message);
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
