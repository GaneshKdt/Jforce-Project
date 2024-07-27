<!DOCTYPE html>
<html class="no-js">
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<jsp:include page="/views/adminCommon/jscss.jsp">
	<jsp:param value=" ${ title } " name="title" />
</jsp:include>


<body class="inside">

	<%@ include file="/views/adminCommon/header.jsp"%>
	<section class="content-container login">
	<div class="container-fluid customTheme">	
		<%@ include file="/views/common/messages.jsp"%>
		<legend>All Queries</legend>
		<div style="width:100%; max-width:100%; overflow:auto">
		<table id="dataTable" class="table table-striped table-bordered" >
			<thead>
				<tr>
					<th style="text-align: center;">Session Name</th>
					<th style="text-align: center;">Question By</th>
					<th style="text-align: center;">Query Type</th>
					<th style="text-align: center;">Answered</th>
					<th style="text-align: center;">Speaker/Host</th>
					<th style="text-align: center;">Asked On</th>
					<th style="text-align: center;">Last Edited On</th>
					<th>Link</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${AllQueries}" var="Query">
					<tr>
						<td>${ Query.sessionName }</td>
						<td>${ Query.studentName } | ${ Query.sapId }</td>
						<td>${ Query.queryType }</td>
						<td>${ Query.isAnswered }</td>
						<td>${ Query.facultyName } | ${ Query.facultyId }</td>
						<td>${ Query.createdDate }</td>
						<td>${ Query.lastModifiedDate }</td>
						<td class="details-control">AB</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
		</div>
	</div>

	</section>
	<jsp:include page="/views/adminCommon/footer.jsp" />
	
	<script>
		var groupInfos = Array();
		
		var groupNo = 0;
		$('#dataTable thead tr').clone(true).appendTo( '#dataTable thead' );
		$('#dataTable thead tr:eq(1) th').each( function (i) {
			var title = $(this).text();
			console.log(title);
			$(this).html( '<input class="form-control" type="text" placeholder="Search '+title+'" />' );
	 
			$( 'input', this ).on( 'keyup change', function () {
				if ( table.column(i).search() !== this.value ) {
					table
						.column(i)
						.search( this.value )
						.draw();
				}
			} );
		} );

		var table = $('#dataTable').DataTable({
			orderCellsTop: true,
			fixedHeader: true,
			columnDefs: [
				{ visible: false, targets: 0 }
			],
			rowGroup: {		  
				startRender: function ( rows, group ) {
					var state = Array();
					
					var types = rows
					.data()
					.pluck(2);
					
					var answeredStatus = rows
					.data()
					.pluck(3);

					var numAcademic = 0;
					var numTechnical = 0;

					var numAcademicAnswered = 0;
					var numTechnicalAnswered = 0;
					var numAcademicUnAnswered = 0;
					var numTechnicalUnAnswered = 0;
					
					var numAnswered = 0;
					var numUnAnswered = 0;
					$.each(types,function(index){
						if(types[index] == "Academic"){
							numAcademic ++;
							if(answeredStatus[index] == "Y"){
								numAcademicAnswered++;
							}else{
								numAcademicUnAnswered++;
							}
							
						}else if(types[index] == "Technical"){
							numTechnical ++;
							if(answeredStatus[index] == "Y"){
								numTechnicalAnswered++;
							}else{
								numTechnicalUnAnswered++;
							}
						}

						if(answeredStatus[index] == "Y"){
							numAnswered++;
						}else{
							numUnAnswered++;
						}
					});
					groupNo++;
					groupInfos = `
						<div class="col-6 px-0">
							<button style="float: right;" class="btn btn-primary collapsed" data-toggle="collapse" data-target="#groupInfo` + groupNo + `">Show Info</button>
						</div>
						<div id="groupInfo` + groupNo + `" class="collapse" style="overflow: hidden;height: 0px;">
							<table class="table table-bordered">
								<thead>
									<tr>
										<th style="text-align: center;">Total Answered</th>
										<th style="text-align: center;">Total Unanswered</th>
										<th style="text-align: center;">Total Academic</th>
										<th style="text-align: center;">Total Technical</th>
										<th style="text-align: center;">Total Academic Answered</th>
										<th style="text-align: center;">Total Academic Unanswered</th>
										<th style="text-align: center;">Total Technical Answered</th>
										<th style="text-align: center;">Total Technical Unanswered</th>
									</tr>
								</thead>
								<tbody>
									<tr>
										<td style="background-color: #ffffff;text-align: center;">` + numAnswered + `</td>
										<td style="background-color: #ffffff;text-align: center;">` + numUnAnswered + `</td>
										<td style="background-color: #ffffff;text-align: center;">` + numAcademic + `</td>
										<td style="background-color: #ffffff;text-align: center;">` + numTechnical + `</td>
										<td style="background-color: #ffffff;text-align: center;">` + numAcademicAnswered + `</td>
										<td style="background-color: #ffffff;text-align: center;">` + numAcademicUnAnswered + `</td>
										<td style="background-color: #ffffff;text-align: center;">` + numTechnicalAnswered + `</td>
										<td style="background-color: #ffffff;text-align: center;">` + numTechnicalUnAnswered + `</td>
									</tr>
								</thead>
							</table>
						</div>
						`;
					return $('<tr/>')
						.append( 
								`<td colspan="7">
									<div class="row mx-5">
										<div class="col-6 px-0">
											<h3>` + group + ` </h3>
										</div>
											` + groupInfos + `
									</div>
								</td>
				  			` );
					},
				dataSrc: 0  
			},
			dom: `
				<'row'<'col-12 mb-3'B>>
				<'row'<'col-sm-12 col-md-6'l><'col-sm-12 col-md-6'f>>
				<'row'<'col-sm-12'tr>>
				<'row'<'col-sm-12 col-md-5'i><'col-sm-12 col-md-7'p>>`,
			buttons: [
				 	{
						extend: 'colvis',
						text: 'Show/hide columns',
						className:'m-1 btn-rounded'
					},
					{
						extend: 'copy',
						title: '${ tableTitle }',
						footer: true,
						exportOptions: {
							 columns: ':visible'
					 	},
						className:'m-1 btn-rounded'
					},
					{
						extend: 'csv',
						title: '${ tableTitle }',
						footer: true,
						exportOptions: {
							 columns: ':visible'
					 	},
						className:'m-1 btn-rounded'
					},
					{
						extend: 'excelHtml5',
						title: '${ tableTitle }',
						footer: true,
						exportOptions: {
							 columns: ':visible'
					 	},
						className:'m-1 btn-rounded'
					},
					{
						extend: 'csv',
						title: '${ tableTitle }',
						footer: true,
						exportOptions: {
							 columns: ':visible'
					 	},
						className:'m-1 btn-rounded'
					},
					{
						extend: 'pdf',
						title: '${ tableTitle }',
						footer: true,
						exportOptions: {
							 columns: ':visible'
					 	},
						className:'m-1 btn-rounded'
					},
					{
						extend: 'print',
						title: '${ tableTitle }',
						exportOptions: {
							 columns: ':visible'
					 	},
						className:'m-1 btn-rounded'
					}
			]
		});
	</script>
</body>

</html>