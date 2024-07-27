	<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<jsp:include page="../jscss.jsp">
	<jsp:param value="Add specialization mapping" name="title" />
</jsp:include>
<style>

	.content{
	    margin: 1em;
    	display: flow-root;
    	text-align:center;
	}
	
	.items{
		background: aliceblue;
	    padding: 0.5em;
	    border-radius: 5px;
    	margin: 0.2em;
    	text-align: center;
    	min-height: 6em;
    	display: flex;
    	justify-content: center;
    	flex-direction: column;
    	min-width: 14vw;    
    	max-width: 14vw;
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
		top: 80%;
	    display: none;
	}

	.icon{
		font-size: large;
	}
	
	.icon:hover{
		cursor: pointer;
	}
	
	.icon:hover + .hoverText{
		display: block;
	}
	
	.hoverText{
	    z-index: 1;
	    background: lightgray;
	    padding: 0.2em;
	    border-radius: 0.2em;
	    position: absolute;
	    display: none;
    }
	
</style>
<body>
	<%@ include file="../header.jsp"%>
	<div class="container-fluid customTheme">
		<div class="row"> 
	       	<legend>Add specialization mapping</legend>
	    </div>
	    <%@ include file="../messages.jsp"%>
	    <div class="panel-body">
	    	<div class='content' >
	    		<div class="col-lg-4" >
	    			<label for='acadMonth' style="text-align: left;">Academic Month</label>
			    	<select id='acadMonth' class="form-select" onchange="handleYearMonthSemChange()">
			    		<option selected="selected" value='0'>Select Academic Month</option>
						<c:forEach var="month" items="${ acad_month_list }">
							<option value="${month}">${month}</option>
						</c:forEach>
			    	</select>
	    		</div>
	    		<div class="col-lg-4" >
	    			<label for='acadYear' style="text-align: left;">Academic Year</label>
			    	<select id='acadYear' class="form-select" onchange="handleYearMonthSemChange()">
			    		<option selected="selected" value='0'>Select Academic Year</option>
						<c:forEach var="year" items="${ acad_year_list }">
							<option value="${year}">${year}</option>
						</c:forEach>
			    	</select>
	    		</div>
	    		<div class="col-lg-4" >
	    			<label for='batch' style="text-align: left;">Semester</label>
			    	<select id='semester' class="form-select" onchange="handleYearMonthSemChange()">
			    		<option selected="selected" value='0'>Select Semester</option>
						<option value='3'>Semester 3</option>
						<option value='4'>Semester 4</option>
						<option value='5'>Semester 5</option>
			    	</select>
	    		</div>
	    		<div class="col-lg-4" >
	    			<label for='batch' style="text-align: left;">Batch</label>
			    	<select id='batch' class="form-select" onchange="handleBatchChange()">
			    		<option selected="selected" value='0'>Select Batch</option>
			    	</select>
	    		</div>
	    	</div>
	    	<div id='subjectMapping' class='content' style="display: none;">
	    	</div>
	    	<div id='selectionUIPreview' class='content' style="display: none;">
	    	</div>
	    	<div id='snackbar' class='snackbar'>
				<p id='snackbarDetails' style="color: white;">Hello! This is the snackbar.</p>
			</div>
	    	<div id='submiteSelection' class='content' style="display: none;">
	    		<button class="btn btn-primary" onclick='buildAndSaveSpecialization()'>Confirm Selection</button>
	    	</div>
	    </div>
	</div>
	<jsp:include page="../footer.jsp" />
	<script>

		let totalBlockCount = 5
		let totalInstancesInBlock = 3
		let totalSubjectInInstances = 5
		let totalSubjectInBlock = 15

		function handleYearMonthSemChange(){

			$('#subjectMapping').hide();
			$('#selectionUIPreview').hide();
			$('#submiteSelection').hide();
			
			let acadYear = $('#acadYear').val();
			let acadMonth = $('#acadMonth').val();
			let semester = $('#semester').val();
			
			if(acadYear == 0 || acadMonth == 0 || semester == 0)
				return;
			
			let body = {
					'acadYear':acadYear,
					'acadMonth':acadMonth,
					'sem':semester
				}
			
			$('#batch').find('option')
		    		.remove().end()
		    $('#batch').append($("<option></option>")
		    		.attr("value", 0) 
            		.text("Select Batch")); 

			fetchAndPopulateBatch(body);
		}
		
		function fetchAndPopulateBatch(body){
			
			$.ajax({
				type : "POST",
				contentType : "application/json",
				url : "/exam/m/getBatchsForYearMonth",   
				data : JSON.stringify(body),
				success : function(data) {
					for(let i=0; i < data.length;i++){
						 $('#batch').append($("<option></option>")
				                    .attr("value", data[i].batchId) 
				                    .attr("specialization", data[i].specialization) 
				                    .text(data[i].batchName)); 
					}
				},
				error : function(e) {
					console.debug("error: ", e);
					alert("Please Refresh The Page.")
				}
			});
			
		}
		
		function handleBatchChange(){

			let acadYear = $('#acadYear').val()
			let acadMonth = $('#acadMonth').val()
			let batch = $('#batch').val()
			let semester = $('#semester').val()
			
			if(acadYear == 0 || acadMonth == 0 || batch == 0 || semester == 0){
				$('#subjectMapping').hide();
				$('#selectionUIPreview').hide();
				$('#submiteSelection').hide();
				return;
			}

			let body = {
					'acadYear':acadYear,
					'acadMonth':acadMonth,
					'batchId':batch,
					'sem':semester
			}
			
			fetchAndPopulateSubjectsForBatch(body);

			$('#subjectMapping').show();
			$('#selectionUIPreview').show();
			$('#submiteSelection').show();
		}
		
		function fetchAndPopulateSubjectsForBatch(data){

			populateSelectionUI()
			populateSubjectDetails(data)
			
			$.ajax({
				type : "POST",
				contentType : "application/json",
				url : "/exam/m/checkIfSpecializationDetailsExists",   
				data : JSON.stringify(data),
				success : function(success) {
					if(success){
						updateSelectionUI(data)
					}
				},
				error : function(e) {
					console.debug("error: ", e);
					alert("Please Refresh The Page.")
				}
			});
			
		}
		
		function populateSubjectDetails(body){
			
			let subjectMapping = '<div class="content"><h4>Please Select the Block and Sequence for the Subjects</h4></div><div class="form-group">'
			
			$.ajax({
				type : "POST",
				contentType : "application/json",
				url : "/exam/m/getSubjectForBatch",   
				data : JSON.stringify(body),
				success : function(data) {
					
					subjectMapping += '<div><table class="table table-borderless"><thead><tr><th>Subjects</th><th>Specialization</th><th>Block</th>'
					subjectMapping += '<th>Sequence</th><th>Core Subject</th><th>Has Prerequisite</th><th>Prerequisite</th>'
					subjectMapping += '<th>Action</th></tr></thead><tbody>'
					
					for(let i=0; i < data.length;i++){
						subjectMapping += '<tr>'
						subjectMapping += '<td>'+data[i].subject+'</td>'
						subjectMapping += '<td>'+data[i].specializationTypeName+'</td>'
						subjectMapping += '<td><select id=block-'+i+' class=form-control '
							+'onchange="handleBlockAndSequenceAllotment(`'+data[i].subject+'`, '+data[i].timeBoundId
							+', '+data[i].prgm_sem_subj_id+', '+data[i].specializationType +', '+i+
							')"> <option value=na>Select Block</option>'
						for(let i = 0; i<totalBlockCount; i++){
							subjectMapping +='<option value='+i+'>'+(i+1)+'</option>'	
						}
						subjectMapping += '</td>'
						subjectMapping += '<td><select id=sqeuence-'+i+' class=form-control '
							+'onchange="handleBlockAndSequenceAllotment(`'+data[i].subject+'`, '+data[i].timeBoundId
							+', '+data[i].prgm_sem_subj_id+', '+data[i].specializationType +', '+i+
							')"> <option value=na>Select Sequence</option>'	
						for(let i = 0; i<totalSubjectInBlock; i++){
							subjectMapping +='<option value='+i+'>'+(i+1)+'</option>'
						}
						subjectMapping += '</td>'
						subjectMapping += '<td><input type="checkbox" style="box-shadow: none;" onchange=handleCoreSubjectCheckBox('+i+') '
						subjectMapping += 'id="coresubject-'+i+'"></td>'
						subjectMapping += '<td><input type="checkbox" style="box-shadow: none;" onchange=handlePrerequisiteCheckBox('+i+') '
						subjectMapping += 'id="hasPrerequisite-'+i+'"></td>'
						subjectMapping += '<td><select id=prerequisite-'+i+' class=form-control '
							+'onchange="handlePrerequisiteAllotment('+i+')" disabled>'
							+'<option value=na>Select Prerequisite Subject</option>'	
						for(let j = 0; j<data.length; j++){
							subjectMapping +='<option value='+data[j].prgm_sem_subj_id+'>'+data[j].subject+'</option>'
						}
						subjectMapping += '</td>'
						subjectMapping += '<td><i class="fa-solid fa-arrows-rotate icon" style="margin-right: 0.8em"'
						subjectMapping += 'onclick="handleRest(`'+data[i].subject+'`, '+data[i].timeBoundId+', '+data[i].specializationType
						subjectMapping += ', '+i+')" aria-hidden="true"></i><div class="hoverText">Reset</div>'
						subjectMapping += '<i class="fa-solid fa-delete-left icon" '
						subjectMapping += 'onclick="handleDelete(`'+data[i].subject+'`, '+data[i].timeBoundId+', '+data[i].specializationType
						subjectMapping += ', '+i+')"aria-hidden="true"></i><div class="hoverText" style="right: 1em;">Delete</div></td></tr>'
					}
				
					subjectMapping += '</table></div>'
					$('#subjectMapping').html(subjectMapping)
					
				},
				error : function(e) {
			
					console.debug ("error: ", e);
					alert("Please Refresh The Page.")
					
				}
			});
			
		}

		function populateSelectionUI(){

			let sem = $('#semester').val()
			
			let selectionUI = '<div class="content"><h4>Selection UI Preview</h4></div><div class="content">'
			selectionUI += '<div style="display: inline-block;"><div style="float:left;">'
			selectionUI += '<div class="items" style="background: inherit; font-weight: 800;">Term '+sem+'</div></div>'
			selectionUI += '<div style="float:right; display: inline-flex;">'
			selectionUI += '<div class="items" style="background: inherit; font-weight: 800;">Marketing</div>'
			selectionUI += '<div class="items" style="background: inherit; font-weight: 800;">Leadership and Strategy</div>'
			selectionUI += '<div class="items" style="background: inherit; font-weight: 800;">Operations and Supply Chain</div>'
			selectionUI += '<div class="items" style="background: inherit; font-weight: 800;">Applied Finance</div>'
			selectionUI += '<div class="items" style="background: inherit; font-weight: 800;">Digital Marketing</div>'
			selectionUI += '</div>'
			
			for(let i = 0; i<totalBlockCount; i++){
				let index = 0;
				selectionUI += '<div id=block_'+i+' style="display: inline-block;"><div style="float:left;">'
				selectionUI += '<div class="items" style="min-height: 19em;">Block '+(i+1)+'</div></div>'
				for(let j = 0; j<totalInstancesInBlock; j++){
					selectionUI += '<div style="float:right; display: inline-flex;">'
					for(let k = 0; k<totalSubjectInInstances; k++){
						selectionUI += '<div id=sequence_instance_'+i+'-'+index+' class="items">Not Selected</div>'
						index++
					}
					selectionUI += '</div>'
				}
				selectionUI += '</div>'
			}
			selectionUI += '</div>'
			
			$('#selectionUIPreview').html(selectionUI)
			
		}
		
		function updateSelectionUI(data){

			let index
			
			$.ajax({
				type : "POST",
				contentType : "application/json",
				url : "/exam/m/getSpecializationDetails",   
				data : JSON.stringify(data),
				success : function(data) {
					for(let i = 0; i<data.length; i++){
						index = '#sequence_instance_'+data[i].block+'-'+data[i].sequence
						$(index).css('background', 'cornflowerblue');
						$(index).text(data[i].subject);
						$(index).attr('block', data[i].block);
						$(index).attr('sequence', data[i].sequence);
						$(index).attr('timeBoundId', data[i].timeBoundId);
						$(index).attr('specialization', data[i].specialization);
						$(index).attr('programSemSubjectId', data[i].program_sem_subject_id);
					}
				},
				error : function(e) {
					console.debug("error: ", e);
					alert("Please Refresh The Page.")
				}
			});
			
		}
		
		function handleBlockAndSequenceAllotment(subject, timeBoundId, programSemSubjectId, specialization, index){

			let blockIndex = $('#block-'+index).val();
			let blockIndexText = $('#block-'+index+' option:selected').text();
			let sequenceIndex = $('#sqeuence-'+index).val();
			let sequenceIndexText = $('#sqeuence-'+index+' option:selected').text();
			let divToPopulate = '#sequence_instance_'+blockIndex+'-'+sequenceIndex
			
			if(blockIndex && blockIndex != 'na' && sequenceIndex && sequenceIndex != 'na'){
				$(divToPopulate).css('background', 'cornflowerblue');
				$(divToPopulate).text(subject);
				$(divToPopulate).attr('block', blockIndex);
				$(divToPopulate).attr('sequence', sequenceIndex);
				$(divToPopulate).attr('timeBoundId', timeBoundId);
				$(divToPopulate).attr('specialization', specialization);
				$(divToPopulate).attr('programSemSubjectId', programSemSubjectId);
				$('#coresubject-'+index).prop('checked', false);
				$('#hasPrerequisite-'+index).prop('checked', false);
				$('#prerequisite-'+index).prop('disabled', true);
				$('#prerequisite-'+index+' option:eq(0)').prop('selected', true)
			}
		}
		
		function handlePrerequisiteCheckBox(index){

			let blockIndex = $('#block-'+index).val()
			let sequenceIndex = $('#sqeuence-'+index).val()
			
			if ($("#hasPrerequisite-"+index).is(":checked")) {
				$('#prerequisite-'+index).prop('disabled', false);
				$('#sequence_instance_'+blockIndex+'-'+sequenceIndex).attr('hasPrerequisite', true);
			}else {
				$('#prerequisite-'+index).prop('disabled', 'disabled');
				$('#sequence_instance_'+blockIndex+'-'+sequenceIndex).attr('hasPrerequisite', false);
				$('#sequence_instance_'+blockIndex+'-'+sequenceIndex).removeAttr('prerequisiteSubject');
				$('#prerequisite-'+index+' option:eq(0)').prop('selected', true)
			}
		}

		function handleCoreSubjectCheckBox(index){

			let blockIndex = $('#block-'+index).val()
			let sequenceIndex = $('#sqeuence-'+index).val()
			
			if ($("#coresubject-"+index).is(":checked")) {
				$('#sequence_instance_'+blockIndex+'-'+sequenceIndex).attr('isCoreSubject', true);
			}else {
				$('#sequence_instance_'+blockIndex+'-'+sequenceIndex).attr('isCoreSubject', false);
			}
		}
		
		function handlePrerequisiteAllotment(index){

			let blockIndex = $('#block-'+index).val()
			let sequenceIndex = $('#sqeuence-'+index).val()
			
			let prerequisite = $('#prerequisite-'+index).val()
			$('#sequence_instance_'+blockIndex+'-'+sequenceIndex).attr('prerequisite', prerequisite);
			
		}
		
		function handleRest(subject, timeBoundId, specialization, index){

			let divToRest
			let blockText
			
			$('#block-'+index+' option:eq(0)').prop('selected', true)
			$('#sqeuence-'+index+' option:eq(0)').prop('selected', true)
			$('#coresubject-'+index).prop('checked', false);
			$('#hasPrerequisite-'+index).prop('checked', false);
			$('#prerequisite-'+index).prop('disabled', true);
			$('#prerequisite-'+index+' option:eq(0)').prop('selected', true)
			
			for(let i = 0; i<totalBlockCount; i++){
				let index = 0;
				for(let j = 0; j<totalInstancesInBlock; j++){
					for(let k = 0; k<totalSubjectInInstances; k++){
						blockText = $('#sequence_instance_'+i+'-'+index).text()
						if(blockText == subject){
							divToRest = '#sequence_instance_'+i+'-'+index
							$(divToRest).text("Not Selected");
							$(divToRest).css('background', 'aliceblue');
							$(divToRest).removeAttr('block');
							$(divToRest).removeAttr('sequence');
							$(divToRest).removeAttr('timeBoundId');
							$(divToRest).removeAttr('specialization');
						}
						index++
					}
				}
			}

		}

		function handleDelete(subject, timeBoundId, specialization, index){

			let divToRest
			let blockText
			let instances
			
			$('#block-'+index+' option:eq(0)').prop('selected', true)
			$('#sqeuence-'+index+' option:eq(0)').prop('selected', true)
			$('#coresubject-'+index).prop('checked', false);
			$('#hasPrerequisite-'+index).prop('checked', false);
			$('#prerequisite-'+index).prop('disabled', true);
			$('#prerequisite-'+index+' option:eq(0)').prop('selected', true)
			
			for(let i = 0; i<totalBlockCount; i++){
				let index = 0;
				for(let j = 0; j<totalInstancesInBlock; j++){
					for(let k = 0; k<totalSubjectInInstances; k++){
						instances = '#sequence_instance_'+i+'-'+index
						blockText = $(instances).text()
						instanceSpecialization = $(instances).attr('specialization')
						
						if(blockText == subject && instanceSpecialization == specialization){
							bean = {
									'timeBoundId':$(instances).attr('timeboundId'),
									'specialization':$(instances).attr('specialization'),
									'acadYear':$('#acadYear').val(),
									'acadMonth':$('#acadMonth').val(),
									'sem':$('#semester').val(),
									'userId':'${userId}'
									
								}
							
							deleteInsance(bean)
							
							divToRest = '#sequence_instance_'+i+'-'+index
							$(divToRest).css('background', 'aliceblue');
							$(divToRest).text("Not Selected");
							$(divToRest).removeAttr('block');
							$(divToRest).removeAttr('sequence');
							$(divToRest).removeAttr('timeBoundId');
							$(divToRest).removeAttr('specialization');
						}
						index++
					}
				}
			}
		}
		
		function deleteInsance(data){

			$.ajax({
				type : "POST",
				contentType : "application/json",
				url : "/exam/m/deleteSpecializationInstance",   
				data : JSON.stringify(data),
				success : function(data) {
					$('#snackbarDetails').text('Successfully deleted the entry!')
					$('#snackbar').fadeIn(10).delay(1000).fadeOut(1000)
				},
				error : function(e) {
					console.debug("error: ", e);
					$('#snackbarDetails').text('An error occured while attempting to delete the entry, please try again later.')
					$('#snackbar').fadeIn(10).delay(1000).fadeOut(1000)
				}
			});
			
		}
		
		function buildAndSaveSpecialization(){
		
			let instances
			let isCoreSubject
			let hasPrerequisite
			let bean
			let data = []
			let specialization
			let batchSpecialization = $('#batch option:selected').attr('specialization')
			
			for(let i = 0; i<totalBlockCount; i++){
				let index = 0;
				for(let j = 0; j<totalInstancesInBlock; j++){
					for(let k = 0; k<totalSubjectInInstances; k++){
						instances = '#sequence_instance_'+i+'-'+index
						$(instances).attr('specialization')
						specialization = $(instances).attr('specialization')
						if($(instances).text() != 'Not Selected' && batchSpecialization == specialization){
							isCoreSubject = $(instances).attr('isCoreSubject')
							hasPrerequisite = $(instances).attr('hasPrerequisite')
							if(typeof hasPrerequisite != 'undefined' && hasPrerequisite == 'true'){
								let prerequisite = $(instances).attr('prerequisite')
								if(typeof prerequisite == 'undefined' || prerequisite == 'na'){
									alert("Please select a prerequisite for "+$(instances).text());
									return;
								}
							}
							bean = {
								'subject':$(instances).text(),
								'program_sem_subject_id' :$(instances).attr('programSemSubjectId'),
								'timeBoundId':$(instances).attr('timeboundId'),
								'specialization':specialization,
								'block':$(instances).attr('block'),
								'sequence':$(instances).attr('sequence'),
								'acadYear':$('#acadYear').val(),
								'acadMonth':$('#acadMonth').val(),
								'sem':$('#semester').val(),
								'isCoreSubject':typeof isCoreSubject == 'undefined' ? false : isCoreSubject,
								'hasPrerequisite':typeof hasPrerequisite == 'undefined' ? false : hasPrerequisite,
								'prerequisite':$(instances).attr('prerequisite'),
								'userId':'${userId}'
								
							}
							data.push(bean)
						}
						index++
					}
				}
			}
			
			$.ajax({
				type : "POST",
				contentType : "application/json",
				url : "/exam/m/saveSpecializationDetails",   
				data : JSON.stringify(data),
				success : function(data) {
					$('#snackbarDetails').text('Successfully updated the details.')
					$('#snackbar').fadeIn(10).delay(1000).fadeOut(1000)
				},
				error : function(e) {
					console.debug("error: ", e);
					$('#snackbarDetails').text('There was an error in updating the details.')
					$('#snackbar').fadeIn(10).delay(1000).fadeOut(1000)
				}
			});
			
		}
		
	</script>
</body>
</html>