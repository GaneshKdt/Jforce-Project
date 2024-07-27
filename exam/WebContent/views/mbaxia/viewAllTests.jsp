
<!DOCTYPE html>
<html lang="en">

<%@page import="com.nmims.beans.Person"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<jsp:include page="../adminCommon/jscss.jsp">
	<jsp:param value="Internal Assessments" name="title" />
</jsp:include>


<link
	href="https://gitcdn.github.io/bootstrap-toggle/2.2.2/css/bootstrap-toggle.min.css"
	rel="stylesheet">
<script src="https://code.jquery.com/jquery-3.3.1.js"
	integrity="sha256-2Kok7MbOyxpgUVvAk/HJ2jigOSYS2auK4Pfzbm7uH60="
	crossorigin="anonymous"></script>
<script
	src="https://gitcdn.github.io/bootstrap-toggle/2.2.2/js/bootstrap-toggle.min.js"></script>



<body>

	<%@ include file="../adminCommon/header.jsp"%>
	<div class="sz-main-content-wrapper">


		<!-- Custom breadcrumbs as requirement is diff. Start -->
		<div class="sz-breadcrumb-wrapper">
			<div class="container-fluid">
				<ul class="sz-breadcrumbs">
					<li><a href="/exam/">Exam</a></li>
					<li><a href="/exam/mbax/ia/a/viewAllTests">Internal Assessments</a></li>

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
		<!-- Custom breadcrumbs as requirement is diff. End -->



		<div class="sz-main-content menu-closed">
			<div class="sz-main-content-inner">

				<%try{ %>

				<jsp:include page="../adminCommon/left-sidebar.jsp">
					<jsp:param value="" name="activeMenu" />
				</jsp:include>

				<%}catch(Exception e){} %>

				<div class="sz-content-wrapper examsPage">
					<%@ include file="../adminCommon/adminInfoBar.jsp"%>
					<div class="sz-content">

						<h2 class="red text-capitalize">Search MBAX Internal Assessments</h2>
						<div class="clearfix"></div>
						<div class="panel-content-wrapper" style="min-height: 450px;">

							<%@ include file="../adminCommon/messages.jsp"%>
							<!-- Code For Page Goes in Here Start -->


							<%try{ %>
							<div class="table-responsive">
								<table class="table table-striped table-hover tables"
									style="font-size: 12px">
									<thead>
										<tr>
											<th>Sr. No.</th>
											<th>Name</th>
											<th>Exam Year</th>
											<th>Exam Month</th>
											<th>Acad Year</th>
											<th>Acad Month</th>

											<th>Consumer Type</th>
											<th>Program Structure</th>
											<th>Program</th>
											<th>Applicable Type</th>
											<th>Batch Name</th>
											<th>Subject</th>
											<th>Module Name</th>
											<th>Start Time</th>
											<th>End Time</th>
											<th>Results</th>
											<th>Actions</th>
										</tr>
									</thead>
									<tbody>

										<c:forEach var="test" items="${allTests}" varStatus="status">
											<tr>
												<td><c:out value="${status.count}" /></td>
												<td><c:out value="${test.testName}" /></td>
												<td><c:out value="${test.year}" /></td>
												<td><c:out value="${test.month}" /></td>
												<td><c:out value="${test.acadYear}" /></td>
												<td><c:out value="${test.acadMonth}" /></td>
												<c:choose>
													<c:when test="${test.countOfProgramsApplicableTo > 1}">
														<td><a href="javascript:void(0)"
															data-testId="${ test.id }" class="commonLinkbtn">
																Common file for <c:out
																	value="${ test.countOfProgramsApplicableTo }" />
																programs
														</a></td>
														<td></td>
														<td></td>
													</c:when>
													<c:otherwise>
														<td><c:out value="${test.consumerType}" /></td>
														<td><c:out value="${test.programStructure}" /></td>
														<td><c:out value="${test.program}" /></td>
													</c:otherwise>
												</c:choose>

												<td><c:out value="${test.applicableType}" /></td>
												<c:choose>
													<c:when test="${test.applicableType eq 'module'}">
														<td><c:out value="${test.name}" /></td>
														<td><c:out value="${test.subject}" /></td>
														<td><c:out value="${test.referenceBatchOrModuleName}" /></td>
													</c:when>
													<c:when test="${test.applicableType eq 'batch'}">
														<td><c:out value="${test.referenceBatchOrModuleName}" /></td>
														<td><c:out value="${test.subject}" /></td>
														<td>NA</td>
													</c:when>
													<c:when test="${test.applicableType eq 'old'}">
														<td>NA</td>
														<td><c:out value="${test.subject}" /></td>
														<td>NA</td>
													</c:when>
													<c:otherwise>
														<td>Error in the test contact dev team.</td>
														<td>Error</td>
														<td>Error</td>
													</c:otherwise>
												</c:choose>

												<td><c:out
														value="${fn:replace(test.startDate,'T', ' ')}"></c:out></td>
												<td><c:out
														value="${fn:replace(test.endDate, 'T', ' ')}"></c:out></td>
												<td style=""><c:if
														test="${test.showResultsToStudents == 'N'}">
														<a href="/exam/mbax/ia/a/showResultsForTest?id=${test.id}"
															onclick="return confirm('Show Result For ${test.testName}. Are you sure?')">
															<span style="color: #d2232a !important;">Show
																Results</span>
														</a>
													</c:if> <c:if test="${test.showResultsToStudents == 'Y'}">
														<a href="/exam/mbax/ia/a/hideResultsForTest?id=${test.id}"
															onclick="return confirm('Hide Result For ${test.testName}. Are you sure?')">
															<span style="color: blue !important;">Hide Results</span>
														</a>
													</c:if></td>

												<td><a href="/exam/mbax/ia/a/viewTestDetails?id=${test.id}">
														<i class="fa-solid fa-circle-info" style="font-size: 24px"></i>
												</a> &nbsp; <a href="/exam/mbax/ia/a/deleteTest?id=${test.id}"
													onclick="return confirm('Delete Test. Are you sure?')">
														<i class="fa-regular fa-trash-can"
														style="font-size: 24px; color: black !important;"></i>
												</a></td>
											</tr>
										</c:forEach>


									</tbody>
								</table>
							</div>



							<%}catch(Exception e){} %>
							<!-- Code For Page Goes in Here End -->
						</div>

					</div>
				</div>
			</div>
		</div>
	</div>
	<!-- Modal  -->
	<div id="myModal" class="modal fade" role="dialog">
		<div class="modal-dialog modal-lg ">

			<!-- Modal content-->
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal">&times;</button>
					<h4 class="modal-title">Common Program List</h4>
				</div>
				<div class="modal-body modalBody"></div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
				</div>
			</div>

		</div>
	</div>
	<!-- End of modal -->
	<jsp:include page="../adminCommon/footer.jsp" />

	<script>
		
	$('.tables').DataTable( {
        initComplete: function () {
            this.api().columns().every( function () {
                var column = this;
                var headerText = $(column.header()).text();
                console.log("header :"+headerText);
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
                
                if(headerText == "Acad Year")
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
                if(headerText == "Acad Month")
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
                
                if(headerText == "Batch Name")
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
                
                if(headerText == "Module Name")
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
	
	</script>

	<script type="text/javascript">

	$(document).ready (function(){
		
		$('.commonLinkbtn').on('click',function(){
			let testId = $(this).attr('data-testId');
			let modalBody = "<center><h4>Loading...</h4></center>";
			let data = {
				'id':testId
			};
			$.ajax({
				   type : "POST",
				   contentType : "application/json",
				   url : "/exam/mbax/ia/a/getProgramsListForCommonTest",   
				   data : JSON.stringify(data),
				   success : function(data) {
					   modalBody = '<div class="table-responsive"> <table class="table"> <thead><td>Exam year</td><td>Exam month</td> <td>Consumer Type</td> <td>Program Structure</td> <td>Program</td> <td>Subject</td> <td>Action</td> </thead><tbody>';
					   for(let i=0;i < data.length;i++){
						   modalBody = modalBody + '<tr><td>'
						   							+ data[i].year 
						   							+'</td><td>'
						   							+ data[i].month 
						   							+'</td><td>'
						   							+ data[i].consumerType 
						   							+'</td><td>'
						   							+ data[i].programStructure 
						   							+'</td><td>'
						   							+ data[i].program 
						   							+'</td><td>'
						   							+ data[i].subject 
						   							+'</td><td> '
						   							+' <a href="editTestFromSearchTestsForm?testId='+ data[i].id 
						   									+'&consumerProgramStructureId='+ data[i].consumerProgramStructureId
						   									+'&testConfigIds='+ data[i].consumerTypeId
						   										+'~'+data[i].programStructureId
						   										+'~'+data[i].programId
						   										+'~'+data[i].subject.replace(/\&/g, 'and')
						   									+'"  title="Edit"><i class="fa fa-pencil-square-o fa-lg"></i></a></td></tr>';
					   }
					   
					   modalBody = modalBody + '<tbody></table></div>';
					   $('.modalBody').html(modalBody);
				   },
				   error : function(e) {
					   alert("Please Refresh The Page.")
				   }
			});
			$('.modalBody').html(modalBody);
			//modal-body
			$('#myModal').modal('show');
		});
	
	
		
		$('.selectConsumerType').on('change', function(){
		
		
		let id = $(this).attr('data-id');
		
		
		let options = "<option>Loading... </option>";
		$('#programStructureId').html(options);
		$('#programId').html(options);
		$('.selectSubject').html(options);
		
		 
		var data = {
				id:this.value
		}
	console.log(this.value)
		
		console.log("===================> data id : " + id);
		$.ajax({
			type : "POST",
			contentType : "application/json",
			url : "getDataByConsumerType",   
			data : JSON.stringify(data),
			success : function(data) {
				console.log("SUCCESS Program Structure: ", data.programStructureData);
				console.log("SUCCESS Program: ", data.programData);
				console.log("SUCCESS Subject: ", data.subjectsData);
				var programData = data.programData;
				var programStructureData = data.programStructureData;
				var subjectsData = data.subjectsData;
				
				options = "";
				let allOption = "";
				
				//Data Insert For Program List
				//Start
				for(let i=0;i < programData.length;i++){
					allOption = allOption + ""+ programData[i].id +",";
					options = options + "<option value='" + programData[i].id + "'> " + programData[i].name + " </option>";
				}
				allOption = allOption.substring(0,allOption.length-1);
				
				console.log("==========> options\n" + options);
				$('#programId').html(
						 "<option value='"+ allOption +"'>All</option>" + options
				);
				//End
				options = ""; 
				allOption = "";
				//Data Insert For Program Structure List
				//Start
				for(let i=0;i < programStructureData.length;i++){
					allOption = allOption + ""+ programStructureData[i].id +",";
					options = options + "<option value='" + programStructureData[i].id + "'> " + programStructureData[i].name + " </option>";
				}
				allOption = allOption.substring(0,allOption.length-1);
				
				console.log("==========> options\n" + options);
				$('#programStructureId').html(
						 "<option value='"+ allOption +"'>All</option>" + options
				);
				//End
				
				options = ""; 
				allOption = "";
				//Data Insert For Subjects List
				//Start
				for(let i=0;i < subjectsData.length;i++){
					
					options = options + "<option value='" + subjectsData[i].name + "'> " + subjectsData[i].name + " </option>";
				}
				
				
				console.log("==========> options\n" + options);
				$('.selectSubject').html(
						" <option disabled selected value=''> Select Subject </option> " + options
				);
				//End
				
				
				
			},
			error : function(e) {
				
				alert("Please Refresh The Page.")
				
				console.log("ERROR: ", e);
				display(e);
			}
		});
		
		
	});
		
		$('.selectProgramStructure').on('change', function(){
		
		
		let id = $(this).attr('data-id');
		
		
		let options = "<option>Loading... </option>";
		$('#programId').html(options);
		$('.selectSubject').html(options);
		
		 
		var data = {
				programStructureId:this.value,
				consumerTypeId:$('#consumerTypeId').val()
		}
		console.log(this.value)
		
		console.log("===================> data id : " + $('#consumerTypeId').val());
		$.ajax({
			type : "POST",
			contentType : "application/json",
			url : "getDataByProgramStructure",   
			data : JSON.stringify(data),
			success : function(data) {
				
				console.log("SUCCESS: ", data.programData);
				var programData = data.programData;
				var subjectsData = data.subjectsData;
				
				options = "";
				let allOption = "";
				
				//Data Insert For Program List
				//Start
				for(let i=0;i < programData.length;i++){
					allOption = allOption + ""+ programData[i].id +",";
					options = options + "<option value='" + programData[i].id + "'> " + programData[i].name + " </option>";
				}
				allOption = allOption.substring(0,allOption.length-1);
				
				console.log("==========> options\n" + options);
				$('#programId').html(
						 "<option value='"+ allOption +"'>All</option>" + options
				);
				//End
				
				options = ""; 
				allOption = "";
				//Data Insert For Subjects List
				//Start
				for(let i=0;i < subjectsData.length;i++){
					
					options = options + "<option value='" + subjectsData[i].name + "'> " + subjectsData[i].name + " </option>";
				}
				
				
				console.log("==========> options\n" + options);
				$('.selectSubject').html(
						" <option disabled selected value=''> Select Subject </option> " + options
				);
				//End
				
				
				
				
			},
			error : function(e) {
				
				alert("Please Refresh The Page.")
				
				console.log("ERROR: ", e);
				display(e);
			}
		});
		
		
	});
	
		$('.selectProgram').on('change', function(){
		
		let id = $(this).attr('data-id');
		
		
		let options = "<option>Loading... </option>";
		$('.selectSubject').html(options);
		
		 
		var data = {
				programId:this.value,
				consumerTypeId:$('#consumerTypeId').val(),
				programStructureId:$('#programStructureId').val()
		}
		console.log(this.value)
		
		
		$.ajax({
			type : "POST",
			contentType : "application/json",
			url : "getDataByProgram",   
			data : JSON.stringify(data),
			success : function(data) {
				
				console.log("SUCCESS: ", data.subjectsData);
				
				var subjectsData = data.subjectsData;
				
				options = ""; 
				//Data Insert For Subjects List
				//Start
				for(let i=0;i < subjectsData.length;i++){
					
					options = options + "<option value='" + subjectsData[i].name + "'> " + subjectsData[i].name + " </option>";
				}
				
				
				console.log("==========> options\n" + options);
				$('.selectSubject').html(
						" <option disabled selected value=''> Select Subject </option> " + options
				);
				//End
				
				
				
				
			},
			error : function(e) {
				
				alert("Please Refresh The Page.")
				
				console.log("ERROR: ", e);
				display(e);
			}
		});
		
		
	});
	
	
	$('#applicableType').on('change', function(){
	
	let applicableType = this.value;
	console.log('IN applicableType on change event : applicableType = ');
	console.log(applicableType);
	
	var data = {
	programId:$('.selectProgram').val(),
	consumerTypeId:$('#consumerTypeId').val(),
	programStructureId:$('#programStructureId').val(),
	subject:$('.selectSubject').val(),
	acadYear:$('#acadYear').val(),
	acadMonth:$('#acadMonth').val()
	};
	console.log("data : ");
	console.log(data);
	
	if( data.programId === '' || data.consumerTypeId ==='' || data.programStructureId === '' || data.subject === ''
	|| data.acadYear === ''  || data.acadMonth === ''){
	alert('Please Select All Consumer, Program, ProgramStructureId, subject, acadMonth and acadYear');
	return;
	}
	
	if( data.programId.split(',').length > 1 ||
	data.consumerTypeId.split(',').length > 1 || 
	data.programStructureId.split(',').length > 1 ){
	
	document.getElementById("applicableType").options[3].selected = 'selected';
	
	return;
	}
	
	if(applicableType === 'batch'){
	//make api call to get batch details 
	
	$.ajax({
	type : "POST",
	contentType : "application/json",
	url : "/exam/mbax/ia/a/api/getBatchDataByMasterKeyConfig",   
	data : JSON.stringify(data),
	success : function(data) {
	
	console.log("SUCCESS: dataForReferenceId :");
	console.log(data.dataForReferenceId);
	
	
	var dataForReferenceId = data.dataForReferenceId;
	
	if( !dataForReferenceId && dataForReferenceId.length === 0){
	alert("Unable to get batches for selected program config, Kindly update selections and try again.");
	return;
	}
	
	var labelForReferenceId = "<label for='refrenceId'>Applicable Batch</label>";
	var selectElementTopPart = "<select  id='referenceId'  name='referenceId' readonly='readonly' placeholder='referenceId' type='text' class='form-control' required='required'>" 
	var selectElementBottomPart = " </select>";
	
	options = ""; 
	//Start
	for(let i=0;i < dataForReferenceId.length;i++){
	
	options = options + "<option value='" + dataForReferenceId[i].id + "'> "
				+ dataForReferenceId[i].name + " </option>";
	}
	
	
	console.log("==========> options\n" + options);
	
	var htmlToAppend = labelForReferenceId 
			+ selectElementTopPart 
			+ " <option disabled selected value=''> Select Option </option> " + options 
			+ selectElementBottomPart
	
	console.log("htmlToAppend");
	console.log(htmlToAppend);
	$('#referenceIdDiv').html( htmlToAppend );
	//End
	
	
	
	
	},
	error : function(e) {
	
	alert("Please Refresh The Page.")
	
	console.log("ERROR: ", e);
	console.log(e);
	}
	});
	//make api call to get batch details end
	
	}else if(applicableType === 'module'){
	//make api call to get module details
	
	$.ajax({
	type : "POST",
	contentType : "application/json",
	url : "/exam/mbax/ia/a/api/getModuleDataByMasterKeyConfig",   
	data : JSON.stringify(data),
	success : function(data) {
	
	console.log("SUCCESS: dataForReferenceId :");
	console.log(data.dataForReferenceId);
	
	
	var dataForReferenceId = data.dataForReferenceId;
	
	if( !dataForReferenceId && dataForReferenceId.length === 0){
	alert("Unable to get Modules for selected program config, Kindly update selections and try again.");
	return;
	}
	
	var labelForReferenceId = "<label for='refrenceId'>Applicable Modules</label>";
	var selectElementTopPart = "<select  id='referenceId'  name='referenceId' readonly='readonly' placeholder='referenceId' type='text' class='form-control' required='required'>" 
	var selectElementBottomPart = " </select>";
	
	options = ""; 
	//Start
	for(let i=0;i < dataForReferenceId.length;i++){
	
	options = options + "<option value='" + dataForReferenceId[i].id + "'> " 
				+ dataForReferenceId[i].topic + " </option>";
	}
	
	
	console.log("==========> options\n" + options);
	
	var htmlToAppend = labelForReferenceId 
			+ selectElementTopPart 
			+ " <option disabled selected value=''> Select Option </option> " + options 
			+ selectElementBottomPart
	
	console.log("htmlToAppend");
	console.log(htmlToAppend);
	$('#referenceIdDiv').html( htmlToAppend );
	//End
	
	
	
	
	},
	error : function(e) {
	
	alert("Please Refresh The Page.")
	
	console.log("ERROR: ", e);
	console.log(e);
	}
	});
	
	
	//make api call to get module details end
	
	}else if(applicableType === 'old'){ 
		return; 
	} else{
		$('#referenceIdDiv').html( "<span></span>" );
		alert('Please select an option.');
	}
	});
	
	});


</script>
</body>
</html>