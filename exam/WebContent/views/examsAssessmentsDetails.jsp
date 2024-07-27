<html class="no-js">
	<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
	<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
	        <%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
	
	<jsp:include page="jscss.jsp">
	<jsp:param value="Extend Exam Assessment End Time" name="title" />
	</jsp:include>
	<head>
		<link rel="stylesheet" href="https://cdn.datatables.net/1.10.19/css/jquery.dataTables.min.css">
		<style>
			
			 .dataTables .btn-group {
				width: auto;
			} 
			
			.customTheme .btn-group {
    			width: auto;
			}
			
			 .dataTables_filter > label > input{
				float:right !important;
			}
			.toggleListWell{
			cursor: pointer !important;
				margin-bottom:0px !important;
			}
			.toggleWell{
			display:none;
				background-color:white !important;
			}
			input[type="radio"]{
				width:auto !important;
				height:auto !important;
				
			}
			.optionsWell{
				padding:0px 10px;
			} 
		</style>
	</head>

	<body class="inside">
	
	<%@ include file="header.jsp"%>
		<section class="content-container login">
			<div class="container-fluid customTheme">
			<div class="row"><legend>Exam Assessment Panel</legend></div>
					<%@ include file="messages.jsp"%>
				<div class="column">
				<h4 style="text-align: right;"><a href="https://docs.google.com/document/d/1nlrXQAcB-07ylFEeaOm2YKcww0xn8FSVVHxxNyN2MIc/edit" target="_blank">View steps to extent exam end time.</a></h4>
					<div class="table-responsive">
					<table id="dataTable" class="table table-striped table-hover dataTables" style="width: 100%">
						<thead>
							<td><b>Assessments_id</b></td>
							<td><b>Name</b></td>
							<td><b>CustomAssessmentName</b></td>
							<td><b>Subject</b></td>
							<td><b>Schedule_id</b></td>
							<td><b>Schedule Name</b></td>
							<td><b>Exam Start Date</b></td>
							<td><b>Exam End Date</b></td>
							<td><b>Extent End Time</b></td>
							<td><b>Batch Name</b></td>
							<td><b>Action</b></td>
						</thead>
						<tbody>
							<c:forEach var="exam_assessment" items="${exam_assessments}">
								<tr
								value="${exam_assessment.schedule_id }~${exam_assessment.exam_end_date_time}">
									<td><c:out value="${exam_assessment.assessments_id}"/></td>
									<td><c:out value="${exam_assessment.name}"/></td>
									<td><c:out value="${exam_assessment.customAssessmentName}"/></td>
									<td><c:out value="${exam_assessment.subject}"/></td>
									<td><c:out value="${exam_assessment.schedule_id}"/></td>
									<td><c:out value="${exam_assessment.schedule_name}"/></td>
									<td><c:out value="${exam_assessment.exam_start_date_time}"/></td>
									<td><c:out value="${exam_assessment.exam_end_date_time}"/></td>
									<td></td>
									<td><c:out value="${exam_assessment.batchName}"/></td>
									<td></td>
								</tr>
							</c:forEach>
						</tbody>
					</table>
					<br>
					</div>
			</div>
			</div>
			
		
		</section>
		<br>
		<jsp:include page="footer.jsp" />
		
		<!-- jQuery (necessary for Bootstrap's JavaScript plugins) -->
	<!-- 	<script src="assets/js/jquery-1.11.3.min.js"></script> -->
	<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />assets/js/bootstrap.js"></script>
	<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />assets/js/jquery.tabledit.js"></script>

	<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/js/vendor/jquery-ui.min.js"></script>
	<script src="https://cdn.datatables.net/1.10.13/js/jquery.dataTables.min.js"></script>
	<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/js/vendor/dataTables.bootstrap.js"></script>
	<script src="https://cdn.datatables.net/buttons/1.2.4/js/dataTables.buttons.min.js"></script>

<script>
	$(document).ready( function () {					
		let id = "";
	     $(".dataTables").on('click','tr',function(e){	
		    var str = $(this).attr('value');
		     id = str.split('~');
		    console.log(id);
		}); 
	    
	    $('.dataTables').Tabledit({
	    	columns: {
				  identifier: [4, 'id'],                 
				  editable: [
					  			[8, 'extendExamEndTime','{"":"Select Time","00:15:00":"15 Min","00:30:00":"30 Min", "00:45:00":"45 Min", "01:00:00":"1 Hour", "01:30:00":" 1 Hour 30 Min", "02:00:00":"2 Hours"}']
					  			
					  		]
				},
				// link to server script
				url: "",
				// class for form inputs
				inputClass: 'form-control input-sm',
				// // class for toolbar
				toolbarClass: 'btn-toolbar',
				// class for buttons group
				groupClass: 'btn-group btn-group-sm',
				// class for row when ajax request fails
				 dangerClass: 'warning',
				// class for row when save changes
				warningClass: 'warning',
				// class for row when is removed
				mutedClass: 'text-muted',
				// trigger to change for edit mode.
				// e.g. 'dblclick'
				eventType: 'click',
				// change the name of attribute in td element for the row identifier
				rowIdentifier: 'id',
				// activate focus on first input of a row when click in save button
				autoFocus: true,
				// hide the column that has the identifier
				hideIdentifier: false,
				// activate edit button instead of spreadsheet style
				editButton: true,
				// activate delete button
				deleteButton: false,
				// activate save button when click on edit button
				saveButton: true,
				// activate restore button to undo delete action
				restoreButton: true,
				onDraw: function() {
					$('.dataTables').DataTable({
						//Added for column filter
						initComplete: function () {
				        	this.api().columns().every( function () {
				                var column = this;
				                var headerText = $(column.header()).text();
				                console.log("header :"+headerText);
				                
				                if(headerText == "Name"){
				                   	var select = $('<select style="width:100%;" class="form-control"><option value="">All</option></select>')
				                    .appendTo( $(column.header()) )
				                    .on('change', function () {
				                        var val = $.fn.dataTable.util.escapeRegex(
				                            $(this).val()
				                        );
				 
				                        column.search( val ? '^'+val+'$' : '', true, false ).draw();
				                    });
				                	column.data().unique().sort().each( function ( d, j ) {
				                    	select.append( '<option value="'+d+'">'+d+'</option>' )
				                	});
				              	}
				                
				                if(headerText == "Subject"){
				                   	var select = $('<select style="width:100%;" class="form-control"><option value="">All</option></select>')
				                    .appendTo( $(column.header()) )
				                    .on('change', function () {
				                        var val = $.fn.dataTable.util.escapeRegex(
				                            $(this).val()
				                        );
				 
				                        column.search( val ? '^'+val+'$' : '', true, false ).draw();
				                    });
				                	column.data().unique().sort().each( function ( d, j ) {
				                    	select.append( '<option value="'+d+'">'+d+'</option>' )
				                	});
				              	}
				                
				                if(headerText == "Schedule Name"){
				                   	var select = $('<select style="width:100%;" class="form-control"><option value="">All</option></select>')
				                    .appendTo( $(column.header()) )
				                    .on('change', function () {
				                        var val = $.fn.dataTable.util.escapeRegex(
				                            $(this).val()
				                        );
				 
				                        column.search( val ? '^'+val+'$' : '', true, false ).draw();
				                    });
				                	column.data().unique().sort().each( function ( d, j ) {
				                    	select.append( '<option value="'+d+'">'+d+'</option>' )
				                	});
				              	}
				                
				                if(headerText == "Exam Start Date"){
				                   	var select = $('<select style="width:100%;" class="form-control"><option value="">All</option></select>')
				                    .appendTo( $(column.header()) )
				                    .on('change', function () {
				                        var val = $.fn.dataTable.util.escapeRegex(
				                            $(this).val()
				                        );
				 
				                        column.search( val ? '^'+val+'$' : '', true, false ).draw();
				                    });
				                	column.data().unique().sort().each( function ( d, j ) {
				                    	select.append( '<option value="'+d+'">'+d+'</option>' )
				                	});
				              	}
				                
				                if(headerText == "Exam End Date"){
				                   	var select = $('<select style="width:100%;" class="form-control"><option value="">All</option></select>')
				                    .appendTo( $(column.header()) )
				                    .on('change', function () {
				                        var val = $.fn.dataTable.util.escapeRegex(
				                            $(this).val()
				                        );
				 
				                        column.search( val ? '^'+val+'$' : '', true, false ).draw();
				                    });
				                	column.data().unique().sort().each( function ( d, j ) {
				                    	select.append( '<option value="'+d+'">'+d+'</option>' )
				                	});
				              	}
				                
				                if(headerText == "Batch Name"){
				                   	var select = $('<select style="width:100%;" class="form-control"><option value="">All</option></select>')
				                    .appendTo( $(column.header()) )
				                    .on('change', function () {
				                        var val = $.fn.dataTable.util.escapeRegex(
				                            $(this).val()
				                        );
				 
				                        column.search( val ? '^'+val+'$' : '', true, false ).draw();
				                    });
				                	column.data().unique().sort().each( function ( d, j ) {
				                    	select.append( '<option value="'+d+'">'+d+'</option>' )
				                	});
				              	}
				        	})
						}
					});
				}, 	
				onAjax: function(action, serialize) {
					serialize['id'] = id[0];
					serialize['exam_end_date_time'] = id[1];
					let body = JSON.stringify(serialize);

					$.ajax({
						type : "POST",
						url  : "extendExamAssessmentDateTime",
						contentType : "application/json",
						data : body,
						dataType : "json",
						success : function(response) {
							
							if (response.Status == "Success") {
									window.location.reload();
									alert('Entries Saved Successfully');
							}else {
								alert('Entries Failed to update : ' + response.message);
							}
						}
					});
				}
			});
	   	});		
</script>
	<script src="https://cdn.datatables.net/1.10.19/js/jquery.dataTables.min.js"></script>
	<script src="https://cdnjs.cloudflare.com/ajax/libs/moment.js/2.24.0/moment.min.js"></script>

	<script>
		$(document).ready( function () {
				$('#dataTable').DataTable();
				
				$(document).on('click','.toggleListWell',function(){
					$(this).parent().children(".toggleWell").slideToggle();
				});
			});
	</script>	
	
	</body>
</html>