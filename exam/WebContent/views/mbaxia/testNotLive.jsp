<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!-->


<html class="no-js">
<!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<jsp:include page="../jscss.jsp">
	<jsp:param value="MAB-X Make Test Live" name="title" />
</jsp:include>




<body class="inside">

	<%@ include file="../header.jsp"%> 

	<section class="content-container login">
		<div class="container-fluid customTheme">
			
			<br></br>
			<div class="clearfix"></div>
			<div class="column" >
				<legend>&nbsp;MAB-X Internal Assessment To Make Live </legend>
				<div class="table-responsive">
				<table class="table table-striped table-hover tables" style="font-size: 12px">
						<thead>
						<tr>
							<th>Sr. No.</th>
							<th>Acads Year</th>
							<th>Acads Month</th>
							<th>Exam Year</th>
							<th>Exam Month</th>
							<th>Test Type</th>
							<th>Applicable Type</th>
							<th>Consumer Type</th>
							<th>Program Structure</th>
							<th>Program</th>
							<th>Batch</th>
							<th>Subject</th>
							<th>Module</th>
							<th>Action</th>        
							<th>Make live</th>   
						</tr>
						</thead>
						<tbody>
						
						 <c:forEach var="config" items="${testsNotLive}"   varStatus="status">
					         <tr>
					            <td ><c:out value="${status.count}" /></td>
					            <td class="acad_year"><c:out value="${config.acadYear}" /></td>
					            <td class="acad_month"><c:out value="${config.acadMonth}" /></td>
					            <td class="exam_year"><c:out value="${config.examYear}" /></td>
					            <td class="exam_month"><c:out value="${config.examMonth}" /></td>
					            <td class="live_type"><c:out value="Regular" /></td>
					            <td class="applicable_type"><c:out value="${config.applicableType}" /></td>
					            <td ><c:out value="${config.consumerType}" /><input type="hidden" class="consumer_type" value="${config.consumerTypeId}"/></td>
					            <td ><c:out value="${config.programStructure}" /><input type="hidden" class="program_structure" value="${config.programStructureId}"/></td>
					            <td ><c:out value="${config.program}"/><input type="hidden" class="program_name" value="${config.programId}"/></td>
	            				
								 <c:choose>
					               <c:when test="${config.applicableType eq 'old'}">
									<td >NA</td>
	            				   </c:when>
					               <c:otherwise>
									 <td ><c:out value="${config.name}"/></td>
					               </c:otherwise>
					              </c:choose>
					            <input type="hidden" class="test_batch" value="${config.batchId}"/>
								 <c:choose>
					               <c:when test="${config.applicableType eq 'old'}">
									<td ><c:out value="${config.subject}"/></td>
	            				   </c:when>
					               <c:otherwise>
									<td ><c:out value="${config.subject}"/></td>
					            	</c:otherwise>
					              </c:choose>
					            <input type="hidden" class="test_subject" value="${config.subject}"/>
					            
								 <c:choose>
					               <c:when test="${config.applicableType eq 'module'}">
									<td ><c:out value="${config.topic}"/></td>
									</c:when>
					               <c:otherwise>
									<td > NA</td>
	            				   </c:otherwise>
					              </c:choose>
					             <input type="hidden" class="module_id" value="${config.sessionModuleNo}"/>
	            				<td>
	            				<div class="row"> 
	            				<a class="col-md-6" href="/exam/mbax/ia/a/viewTestDetails?id=${config.id}">
										<i class="fa-solid fa-circle-info" style="font-size:24px"></i>										
									</a>
									&nbsp;
									<a class="col-md-6"  href="/exam/mbax/ia/a/deleteTest?id=${config.id}"
									onclick="return confirm('Delete Test. Are you sure?')"
									>
										<i class="fa-regular fa-trash-can" style="font-size:24px; color:black !important;""></i>
									</a>
									
									
									</div>
									</td>       
								    <td><a class="btn make_test_live">Make Live</a></td>    
											  					 
					        </tr>   
					    </c:forEach>
							
						</tbody>
					</table>
				</div>
				<br></br>
				
				</div> 

		</div>
	</section>

	<jsp:include page="../footer.jsp" />
	
	
	

<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />assets/js/jquery-1.11.3.min.js"></script>
<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />assets/js/bootstrap.js"></script>
<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />assets/js/jquery.tabledit.js"></script>
<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/js/vendor/jquery-ui.min.js"></script>
<script src="https://cdn.datatables.net/1.10.13/js/jquery.dataTables.min.js"></script>
<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/js/vendor/dataTables.bootstrap.js"></script>
<script src="https://cdn.datatables.net/buttons/1.2.4/js/dataTables.buttons.min.js"></script>

<script type="text/javascript">

$( ".make_test_live" ).click(function() { 
	if(!confirm('Are you sure you want to make this test live?')){
		return false;
	}
	var acad_year = $(this).closest("tr").find(".acad_year").text();
	var acad_month = $(this).closest("tr").find(".acad_month").text();
	var exam_year = $(this).closest("tr").find(".exam_year").text();
	var exam_month = $(this).closest("tr").find(".exam_month").text();
	var live_type = $(this).closest("tr").find(".live_type").text();
	var applicable_type = $(this).closest("tr").find(".applicable_type").text();
	var consumer_type = $(this).closest("tr").find(".consumer_type").val();
	var program_structure = $(this).closest("tr").find(".program_structure").val();
	var program_name = $(this).closest("tr").find(".program_name").val();
	var test_batch = $(this).closest("tr").find(".test_batch").val();
	var test_subject = $(this).closest("tr").find(".test_subject").val();
	var module_id = $(this).closest("tr").find(".module_id").val();
	/* alert(acad_year+"--"+ acad_month+"--"+ exam_year+"--"+ exam_month+"--"+ live_type+"--"+ applicable_type+"--"+ consumer_type+"--"+ program_structure+"--"+ program_name
			+"--"+test_batch+"--"+test_subject+"--"+module_id	); */
	
    var newForm = jQuery('<form>', {
        'action': 'saveTestLiveConfig',
        'method':'POST',
        'target': '_top'
    }).append(jQuery('<input>', {
        'name': 'examYear',
        'value': exam_year,
        'type': 'hidden'
    })).append(jQuery('<input>', {
        'name': 'examMonth',
        'value': exam_month,
        'type': 'hidden'
    })).append(jQuery('<input>', {
        'name': 'acadYear',
        'value': acad_year,
        'type': 'hidden'
    })).append(jQuery('<input>', {
        'name': 'acadMonth',
        'value': acad_month,
        'type': 'hidden'
    })).append(jQuery('<input>', {
        'name': 'consumerTypeId',
        'value': consumer_type,
        'type': 'hidden'
    })).append(jQuery('<input>', {
        'name': 'programStructureId',
        'value': program_structure,
        'type': 'hidden'
    })).append(jQuery('<input>', {
        'name': 'programId',
        'value': program_name,
        'type': 'hidden'
    })).append(jQuery('<input>', {
        'name': 'liveType',
        'value': live_type,
        'type': 'hidden'
    })).append(jQuery('<input>', {
        'name': 'applicableType',
        'value': applicable_type,
        'type': 'hidden'
    })).append(jQuery('<input>', {
        'name': 'moduleBatchId',
        'value': test_batch,
        'type': 'hidden'
    })).append(jQuery('<input>', {
        'name': 'subject',
        'value': test_subject,
        'type': 'hidden'
    })).append(jQuery('<input>', {
        'name': 'referenceId',
        'value': module_id,
        'type': 'hidden'
    }));
    
    newForm.appendTo('body').submit();
    
	});

$(document).ready (function(){
//////////////////////////////////////////////////////////////////

$('.tables').DataTable( {
initComplete: function () {
this.api().columns().every( function () {
var column = this;
var headerText = $(column.header()).text();
console.log("header :"+headerText);
if(headerText == "Acads Year")
{
var select = $('<select style="width:100%;" class="form-control"><option value="">All</option></select>')
.appendTo( $(column.header()) )
.on( 'change', function () {
var val = $.fn.dataTable.util.escapeRegex(
$(this).val()
);

column
.search( val ? '^'+val+'$' : '', true, false )
.draw();
} );

column.data().unique().sort().each( function ( d, j ) {
select.append( '<option value="'+d+'">'+d+'</option>' )
} );
}

if(headerText == "Acads Month")
{
var select = $('<select style="width:100%;" class="form-control"><option value="">All</option></select>')
.appendTo( $(column.header()) )
.on( 'change', function () {
var val = $.fn.dataTable.util.escapeRegex(
$(this).val()
);

column
.search( val ? '^'+val+'$' : '', true, false )
.draw();
} );

column.data().unique().sort().each( function ( d, j ) {
select.append( '<option value="'+d+'">'+d+'</option>' )
} );
}

if(headerText == "Exam Year")
{
var select = $('<select style="width:100%;" class="form-control"><option value="">All</option></select>')
.appendTo( $(column.header()) )
.on( 'change', function () {
var val = $.fn.dataTable.util.escapeRegex(
$(this).val()
);

column
.search( val ? '^'+val+'$' : '', true, false )
.draw();
} );

column.data().unique().sort().each( function ( d, j ) {
select.append( '<option value="'+d+'">'+d+'</option>' )
} );
} 
if(headerText == "Exam Month")
{
var select = $('<select style="width:100%;" class="form-control"><option value="">All</option></select>')
.appendTo( $(column.header()) )
.on( 'change', function () {
var val = $.fn.dataTable.util.escapeRegex(
$(this).val()
);

column
.search( val ? '^'+val+'$' : '', true, false )
.draw();
} );

column.data().unique().sort().each( function ( d, j ) {
select.append( '<option value="'+d+'">'+d+'</option>' )
} );
}               
if(headerText == "Consumer Type")
{
var select = $('<select style="width:100%;" class="form-control"><option value="">All</option></select>')
.appendTo( $(column.header()) )
.on( 'change', function () {
var val = $.fn.dataTable.util.escapeRegex(
$(this).val()
);

column
.search( val ? '^'+val+'$' : '', true, false )
.draw();
} );

column.data().unique().sort().each( function ( d, j ) {
select.append( '<option value="'+d+'">'+d+'</option>' )
} );
}                
if(headerText == "Test Type")
{
var select = $('<select style="width:100%;" class="form-control"><option value="">All</option></select>')
.appendTo( $(column.header()) )
.on( 'change', function () {
var val = $.fn.dataTable.util.escapeRegex(
$(this).val()
);

column
.search( val ? '^'+val+'$' : '', true, false )
.draw();
} );

column.data().unique().sort().each( function ( d, j ) {
select.append( '<option value="'+d+'">'+d+'</option>' )
} );
}             
if(headerText == "Program Structure")
{
var select = $('<select style="width:100%;" class="form-control"><option value="">All</option></select>')
.appendTo( $(column.header()) )
.on( 'change', function () {
var val = $.fn.dataTable.util.escapeRegex(
$(this).val()
);

column
.search( val ? '^'+val+'$' : '', true, false )
.draw();
} );

column.data().unique().sort().each( function ( d, j ) {
select.append( '<option value="'+d+'">'+d+'</option>' )
} );
}             
if(headerText == "Program")
{
var select = $('<select style="width:100%;" class="form-control"><option value="">All</option></select>')
.appendTo( $(column.header()) )
.on( 'change', function () {
var val = $.fn.dataTable.util.escapeRegex(
$(this).val()
);

column
.search( val ? '^'+val+'$' : '', true, false )
.draw();
} );

column.data().unique().sort().each( function ( d, j ) {
select.append( '<option value="'+d+'">'+d+'</option>' )
} );
}


if(headerText == "Applicable Type")
{
var select = $('<select style="width:100%;" class="form-control"><option value="">All</option></select>')
.appendTo( $(column.header()) )
.on( 'change', function () {
var val = $.fn.dataTable.util.escapeRegex(
$(this).val()
);

column
.search( val ? '^'+val+'$' : '', true, false )
.draw();
} );

column.data().unique().sort().each( function ( d, j ) {
select.append( '<option value="'+d+'">'+d+'</option>' )
} );
}

if(headerText == "Batch")
{
var select = $('<select style="width:100%;" class="form-control"><option value="">All</option></select>')
.appendTo( $(column.header()) )
.on( 'change', function () {
var val = $.fn.dataTable.util.escapeRegex(
$(this).val()
);

column
.search( val ? '^'+val+'$' : '', true, false )
.draw();
} );

column.data().unique().sort().each( function ( d, j ) {
select.append( '<option value="'+d+'">'+d+'</option>' )
} );
}

if(headerText == "Subject")
{
var select = $('<select style="width:100%;" class="form-control"><option value="">All</option></select>')
.appendTo( $(column.header()) )
.on( 'change', function () {
var val = $.fn.dataTable.util.escapeRegex(
$(this).val()
);

column
.search( val ? '^'+val+'$' : '', true, false )
.draw();
} );

column.data().unique().sort().each( function ( d, j ) {
select.append( '<option value="'+d+'">'+d+'</option>' )
} );
}

if(headerText == "Module")
{
var select = $('<select style="width:100%;" class="form-control"><option value="">All</option></select>')
.appendTo( $(column.header()) )
.on( 'change', function () {
var val = $.fn.dataTable.util.escapeRegex(
$(this).val()
);

column
.search( val ? '^'+val+'$' : '', true, false )
.draw();
} );

column.data().unique().sort().each( function ( d, j ) {
select.append( '<option value="'+d+'">'+d+'</option>' )
} );
}

} );
}
} );
} );
</script>


</body>

</html>
