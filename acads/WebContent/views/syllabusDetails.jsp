<!DOCTYPE html>
<html class="no-js">

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<jsp:include page="jscss.jsp">
	<jsp:param value="Syllabus Details" name="title" />
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
					<p style="color: #c72127; font-size: 2.0em;">Syllabus : ${ subject }</p>	
					<div class="form-group" id="subejcts" style="padding: 20px;">
						<table  class="table table-striped" id="subjectDetails" style="text-align: justify; text-justify: inter-word;">
							<thead>
								<tr>
									<th style="width:05%">Sr No.</th>
									<th style="width:08%">Chapter</th>
									<th style="width:15%">Title</th>
									<th style="width:22%">Topic</th>
									<th style="width:20%">Outcomes</th>
									<th style="width:20%">Pedagogical Tool</th>
									<th style="width:05%;text-align: center;">Edit</th>
									<th style="width:05%;text-align: center;">Delete</th>
								</tr>
							</thead>
							<tbody>
								<c:set value="1" var="count"></c:set>
								<c:forEach items="${ syllabus }" var="details">
									<tr>
										<td>${ count }</td>
										<td>${ details.chapter }</td>
										<td>${ details.title }</td>
										<td>${ details.topic }</td>
										<td>${ details.outcomes }</td>
										<td>${ details.pedagogicalTool }</td>
										<td style="text-align: center;">
											<a href="/acads/editSyllabus?id=${ details.id }">
												<i style="font-size:20px;" class="fa-solid fa-pen-to-square"  aria-hidden="true"></i>
											</a>
										</td>
										<td style="text-align: center;">
											<i style="font-size:20px;" class="fa-regular fa-trash-can highlight" id=${ details.id } aria-hidden="true"></i>
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

			$('#subject').on('change', function(){

				let subject = $('#subject').val();
				let body = {
						'subject':subject
						}
				
				$.ajax({

					type : "POST",
					contentType : "application/json",
					url : "/acads/getSemesterForSubject",   
					data : JSON.stringify(body),
					success : function(data) {
						
						console.log("successInFetchingSemester: ", JSON.stringify(data));
						$('#semester').empty().append("<option value=0>Select Semester</option>");
						for(let i = 0; i<data.length; i++)
							$('#semester').append("<option value="+data[i].sem+">"+data[i].sem+"</option>")
						
					},
					error : function(e) {
						
						alert("Please Refresh The Page.")
						console.log("ERROR: ", e);
						
					}

				})
				
			});
			
			$('.highlight').on('click', function(){

				let id = $(this).attr('id');
				console.log("clicked: "+id)
				let body = {
						'id':id
						}
				
				$.ajax({

					type : "POST",
					contentType : "application/json",
					url : "/acads/deleteSyllabusDetails",   
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
