<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!-->


<html class="no-js">
<!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<jsp:include page="jscss.jsp">
	<jsp:param value="Upload Content Files For Leads " name="title" />
</jsp:include>

<body class="inside">

	<%@ include file="header.jsp"%>

	<section class="content-container login">
		<div class="container-fluid customTheme">
			<%@ include file="messages.jsp"%>
			<div class="row"><legend>Select Subject For Leads</legend></div>
			
				
				<form:form modelAttribute="filesSet" method="post"	enctype="multipart/form-data" action="uploadContentFilesForLeads">
					<div class="panel-body">
					<div class="col-md-18 column">
													 <!-- code for configuration start -->
											
					<div class="row">
<%-- 	 					${consumerLead }  --%>
<%-- 						${programStructure } --%>
<%-- 	 					${ programsForLeads }  --%>

						<div class="col-md-4 column form-group">
							<label for="consumerTypeIdField">Consumer Type</label>
							<input type="text" value="${consumerLead.name}" id="consumerTypeIdField" style="background-color: #EAE8E8;" class="form-control selectSubject" readonly="readonly"/>
							<form:hidden path="consumerTypeId" value="${consumerLead.id}"/>
						</div>
												<div class="col-md-4 column programStructure">
							<div class="form-group">
								<label for="programStructureIdField">Program Structure</label>
								<form:input path="programStructureIdFormValue" value="${programStructure.name}" style="background-color: #EAE8E8;" class="form-control selectSubject" readonly="readonly"/>
     							<form:hidden path="programStructureId" value="${programStructure.id}"/>
<!--      							user form hidden to parse values to the beam directly  -->
							</div>
						</div> 
							
						<div class="col-md-4 column">
							<div class="form-group">
								<form:label path="programId" for="programId">Program </form:label>
								<form:select id="programId" path="programId" type="text" placeholder="Program " class="form-control selectProgram" required="required"> 
									<form:option value="" >Select Program</form:option>
									<c:forEach var="programsForLeads" items="${programsForLeads}">
									<form:option value="${programsForLeads.programId}" >
										<c:out value="${programsForLeads.code}"/>
									</form:option>
									</c:forEach>
								</form:select>
							</div>
						</div>
					</div>
					
					<div class="row">
						<div class="col-md-4 col-sm-6 col-xs-12 column">
							<div class="form-group">
								<!-- Add FacultyList Here -->
								<form:label path="subject" for="subject">Subject</form:label>
								<form:select id="subject" path="subject" type="text" placeholder="Subject" class="form-control selectSubject" required="required" 
								itemValue="${filesSet.subject}"> 
									<form:option value="">Select Program</form:option>
									<form:options items="${subjectList}" />
								</form:select>
							</div>
						</div>
					
						<div class="col-md-4 col-sm-6 col-xs-12 column">
							<div class="form-group" style="display: none;">
								<form:label path="year" for="year">Acads Year</form:label>
								<form:select id="year" path="year"  class="form-control" itemValue="${filesSet.year}">
									<form:option value="">Select Academic Year</form:option>
									<form:options items="${yearList}" />
								</form:select>
							</div>
						</div>
					
						<div class="col-md-4 col-sm-6 col-xs-12 column">
							<div class="form-group" style="display: none;">
								<form:label path="month" for="month">Acads Month</form:label>
								<form:select id="month" path="month" itemValue="${filesSet.month}">
									<form:option value="">Select Academic Month</form:option>
									<form:option value="Jan">Jan</form:option>
									<form:option value="Jul">Jul</form:option>
								</form:select>
							</div>
						</div>
					
					</div>
					<br>
					
					<h2>Select Files to Upload</h2>
					
					<div class="row">
					
					
					<div class="col-md-3 column">
							<div class="form-group">
							<h4>Document Name</h4>
							</div>
					</div>
					
					
					<div class="col-md-3 column">	
							<div class="form-group" align="left" >	
							<h4>Description</h4>
							</div>
					</div>
					
					
					<div class="col-md-3 column">
							<div class="form-group" align="left">
							<h4>Attachment</h4>
							</div>
					</div>
					
					<div class="col-md-3 column">
							<div class="form-group" align="left">
							<h4>URL (If not Attachment)</h4>
							</div>
					</div>
					
					<div class="col-md-2 column">
							<div class="form-group">
							<h4>URL Type</h4>
							</div>
					</div>
					
					<div class="col-md-2 column">
							<div class="form-group">
							<h4>Content Type</h4>
							</div>
					</div>
					
					</div>
					
					
					<% for(int i = 0 ; i < 10 ; i++) {%>
					
					<div class="row">
					
					
					<div class="col-md-3 column">
							<div class="form-group">
							<input type="text"	size="27px"	 name="contentFiles[<%=i%>].name" placeholder="Name of the Document">
							</div>
					</div>
					
					
					<div class="col-md-3 column">	
							<div class="form-group" align="left" >	
							<textarea id="description" rows="2"   name="contentFiles[<%=i%>].description"></textarea>
							</div>
					</div>
										
					<div class="col-md-3 column">
							<div class="form-group" align="left">
								<input id="fileData" type="file" name="contentFiles[<%=i%>].fileData" > 
							</div>
					</div>
					
					<div class="col-md-3 column">
							<div class="form-group" align="left">
								<input type="text" size="40" name="contentFiles[<%=i%>].webFileurl" placeholder="URL of file if it is on Web">
							</div>
					</div>
					
					<div class="col-md-2 column">
							<div class="form-group">
								<select name="contentFiles[<%=i%>].urlType" >
									<option value="View">View</option>
									<option value="Download">Download</option>
								</select>
							</div>
					</div>
					
					<div class="col-md-2 column">
							<div class="form-group">
								<select name="contentFiles[<%=i%>].contentType" >
									<option value="">Select</option>
									<option value="Course Material">Course Material</option>
									<option value="Course Presentation">Course Presentation</option>
									<option value="Session Recording">Session Recording</option>
									<option value="Session Plan">Session Plan</option>
									<option value="Session Presentation">Session Presentation</option>
									<option value="Session Pre-Read">Session Pre-Read</option>
									<option value="Reading Material">Reading Material</option>
								</select>
							</div>
					</div>
					
					</div>
				
					
					<%} %>
					
					
					<div class="form-group">
						<button id="submit" name="submit" class="btn btn-large btn-primary"formaction="uploadContentFilesForLeads">Upload</button>
					</div>
					</div>

			</div>

			</form:form>
		</div>
	</section>

	<jsp:include page="footer.jsp" />
	
	
<script type="text/javascript">

$(document).ready (function(){

	$('.selectProgram').on('change', function(){

		alert('subjectChanged');
		let id = $(this).attr('data-id');
		let options = "<option>Loading... </option>";
		$('#subject').html(options);
		
		var data = {
				'programId':this.value,
				'consumerTypeId':$('#consumerTypeId').val(),
				'programStructureId':$('#programStructureId').val()
		}
		
		alert(JSON.stringify(data))
		
		$.ajax({
			type : "POST",
			contentType : "application/json",
			url : "/exam/getDataByProgram",   
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
				$('#subject').html(
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
});

</script>
 

</body>
</html>

 
 
