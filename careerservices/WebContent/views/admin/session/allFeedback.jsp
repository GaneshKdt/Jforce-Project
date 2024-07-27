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
		<legend> ${ title }</legend>
		<div style="width:100%; max-width:100%; overflow:auto">
		<table id="dataTable" class="table table-striped table-bordered" >
			<thead>
				<tr>
					<th style="text-align: center;">Session Name </th>
					<th style="text-align: center;">Date </th>
					<th style="text-align: center;">Faculty Id </th>
					<th style="text-align: center;">Student Name </th>
					<th style="text-align: center;">sapid </th>
					<th style="text-align: center;">Student Package Id </th>
					<th style="text-align: center;">Sales Force UID </th>
					<th style="text-align: center;">Start Date </th>
					<th style="text-align: center;">Attended </th>
					<th style="text-align: center;">Attend Time </th>
					<th style="text-align: center;">Device </th>
					<th style="text-align: center;">Feedback Submitted </th>
					<th style="text-align: center;">Successfully Attended</th>
					<th style="text-align: center;">Not Attended Reason  </th>
					<th style="text-align: center;">Question Type </th>
					<th style="text-align: center;">Question </th>
					<th style="text-align: center;">Answer </th>
					<th style="text-align: center;">Additional Comment Required </th>
					<th style="text-align: center;">Additional Comment </th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${AllFeedback}" var="Feedback">
					<tr>
						<td>${ Feedback.sessionName }</td>
						<td>${ Feedback.date }</td>
						<td>${ Feedback.facultyId }</td>
						<td>${ Feedback.studentName }</td>
						<td>${ Feedback.sapid }</td>
						<td>${ Feedback.studentPackageId }</td>
						<td>${ Feedback.salesForceUID }</td>
						<td>${ Feedback.startDate }</td>
						<td>${ Feedback.attended }</td>
						<td>${ Feedback.attendTime }</td>
						<td>${ Feedback.device }</td>
						<td>${ Feedback.feedbackSubmitted }</td>
						<td>${ Feedback.successfullyAttended }</td>
						<td>${ Feedback.notAttendedReason  }</td>
						<td>${ Feedback.questionType }</td>
						<td>${ Feedback.question }</td>
						<td>${ Feedback.answer }</td>
						<td>${ Feedback.additionalCommentRequired }</td>
						<td>${ Feedback.additionalComment }</td>
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