<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!-->


<html class="no-js">
<!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<jsp:include page="jscss.jsp">
	<jsp:param value="Upload Content Files For Session Plan Module" name="title" />
</jsp:include>

<body class="inside">

	<%@ include file="header.jsp"%>

	<section class="content-container login">
		<div class="container-fluid customTheme">
			
			
	<ul class="breadcrumb">
	<li><a href="/">Home</a></li>
	<li><a href="/acads/admin/manageSessionPlan">Manage Session Plans</a></li>
	<li><a href="/acads/admin/editSessionPlan?id=${bean.sessionPlanId}">Edit Session Plan</a></li>
	<li><a href="/acads/admin/manageSessionPlanModuleById?id=${bean.id}&batchId=${param.batchId}&sessionId=${param.sessionId}&action=upload">Manage Session Plan Module</a></li>
	
	<li class="active" >Upload LR Files</li>
	</ul>
			<%@ include file="messages.jsp"%>
			<div class="row"><legend>Upload Content Files For Session Plan Module of Subject : ${sessionPlan.subject} </legend></div>
				<h4> Session Date: ${sessionDetailsForSessionPlanModule.date} </h4>
				<h4> Session Time: ${sessionDetailsForSessionPlanModule.startTime} </h4>
				<h4> Faculty Name: ${sessionDetailsForSessionPlanModule.facultyName} </h4>
				<h4> Batch Name: ${sessionDetailsForSessionPlanModule.batchName} </h4>
				
				<form:form modelAttribute="filesSet" method="post"	enctype="multipart/form-data" action="uploadContentFiles"  onsubmit = "return checkDate()">
					<form:hidden path="id" value="${bean.id }"/> 
					<form:hidden path="subject" value="${sessionPlan.subject }"/> 
					
					
					<div class="panel-body">
					<div class="col-md-18 column">
					

					
					<div class="row">
					
									<div class="col-md-4 col-sm-6 col-xs-12 column">
										<div class="form-group">
											<!--  Here
											<form:label path="subject" for="subject">Subject</form:label>
											<form:select id="subject" path="subject" type="text"	placeholder="Subject" class="form-control selectSubject" required="required" 
													 itemValue="${filesSet.subject}"  readonly="readonly"> 
													<form:option value="">Select Subject</form:option>
													<form:options items="${subjectList}" />
											</form:select>
											 -->
										
										</div>
									</div>
					
							<%-- 		<div class="col-md-4 col-sm-6 col-xs-12 column">
					<div class="form-group">
						<form:label path="year" for="year">Acads Year</form:label>
						<form:select id="year" path="year"  class="form-control" required="true" itemValue="${filesSet.year}">
							<form:option value="">Select Academic Year</form:option>
							<form:options items="${yearList}" />
						</form:select>
					</div>
					</div> --%>
					
									<%-- <div class="col-md-4 col-sm-6 col-xs-12 column">
					<div class="form-group">
						<form:label path="month" for="month">Acads Month</form:label>
						<form:select id="month" path="month"  required="true" itemValue="${filesSet.month}">
							<form:option value="">Select Academic Month</form:option>
							<form:option value="Jan">Jan</form:option>
							<form:option value="Jul">Jul</form:option>
						</form:select>
					</div>
					</div> --%>
					
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
					<div class="col-sm-1 column">
							<div class="form-group">
											<h4>Active Date</h4>
							</div>
					</div>
					
					</div>
					
					
					<% for(int i = 0 ; i < 10 ; i++) {%>
					
					<div class="row inputValidation">
					
					
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
					
					<%-- <div class="col-md-2 column">
							<div class="form-group">
								<select name="contentFiles[<%=i%>].programStructure" >
									<option value="All">All</option>
									<option value="Jul2009">Jul2009</option>
									<option value="Jul2013">Jul2013</option>
									<option value="Jul2014">Jul2014</option>
									<option value="Jan2018">Jan2018</option>
									<option value="Jan2019">Jan2019</option>
								</select>
							</div>
					</div> --%>
					
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
					
					<div class="col-md-2 column">
											<div class="form-group" align="left">
												<input type="datetime-local"  name="contentFiles[<%=i%>].activeDate" id="activeDate<%=i%>" />
											</div>
										</div>
					
					</div>
				
					
					<%} %>
					
					
					<div class="form-group">
						<button id="submit" name="submit" class="btn btn-large btn-primary" 
						formaction="uploadContentFilesForSessionModule">Upload</button>
					</div>
					</div>

			</div>

			</form:form>
		</div>
									<div class="container-fluid">
												<c:if test="${not empty contentList}">
												<h2 align="center" class="red text-capitalize" style="padding-top: 20px;"> Video Contents : </h2>
												<table class="table table-striped table-hover tables" style="font-size:12px">
													<thead>
														<tr>
															<th>Sr No.</th>
															<th>Subject</th>
															<th>Name </th>
															<th>Description</th>
															<th>View/Download</th>
															<th >Edit </th> 
															<th >Delete </th>
														</tr>
													</thead>
												<tbody>
												<c:forEach var="content" items="${contentList}" varStatus="status">
													<tr>
														<td>${status.count}</td>
														<td>${content.subject }</td>
														<td>${content.name }</td>
														<td>${content.description }</td> 
														<td>
														<c:if test="${fn:endsWith(content.previewPath, '.pdf') || fn:endsWith(content.previewPath, '.PDF') || fn:endsWith(content.previewPath, '.Pdf')}">
										   			  			 <a href="previewContentForAdmin?previewPath=${content.previewPath}&name=${content.name}&type=PDF" target="_blank">View</a>
														</c:if>
														<c:if test="${not empty content.previewPath}">
															/ <a href="#" onClick="window.open('<spring:eval expression="@propertyConfigurer.getProperty('CONTENT_BASE_PATH')" />${content.previewPath}')" />Download</a>
														</c:if>
														</td>
														<td>
															<a href="/acads/admin/editContent?id=${content.id }" title="Edit  Content" class="">
																<b>
																	<i style="font-size:20px; padding-left:5px" class="fa-solid fa-pen-to-square" aria-hidden="true"></i>
																</b>
															</a>
														</td>
														<td>
															<a href="/acads/admin/deleteContent?id=${content.id }" title="Delete Content" class="">
																<b>
																	<i style="font-size:20px; padding-left:5px" class="fa-regular fa-trash-can" aria-hidden="true"></i>
																</b>
															</a>
														</td>
													</tr>
												</c:forEach>
												</tbody>
												</table>
												</c:if>
											</div>
	</section>

	<jsp:include page="footer.jsp" />
	
	
<script type="text/javascript">

$(document).ready (function(){

///////////////////////////////////////////////////////////////////
	
	
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
		url : "/exam/admin/getDataByConsumerType",   
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
	
	///////////////////////////////////////////////////////
	
	
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
		url : "/exam//admin/getDataByProgramStructure",   
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


/////////////////////////////////////////////////////////////

	
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

//////////////////////////////////////////////








	
	
	
});

function checkDate(){
	var ids = document.getElementsByClassName("inputValidation");
	for (let i = 0; i < ids.length; i++) {
		var idName = "activeDate"+i;
		var activeDate = document.getElementById(idName).value;
		if(activeDate.length != 0){
        	var givenDate = new Date(activeDate);
        	var currentDate = new Date();
        	 if (givenDate <= currentDate) {
        	    alert("Active date cannot be less than Current date. Either clear the date or Select any future Date.");
        	    document.getElementById(idName).focus();
        	    return false;
        	 }
        }
	}
}

</script>
 

</body>
</html>

 
 
<%--  <!DOCTYPE html>
<html lang="en">
	
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
    <jsp:include page="adminCommon/jscss.jsp">
	<jsp:param value="Upload Content Files" name="title"/>
    </jsp:include>
    
    
    
    <body>
    
    	<%@ include file="adminCommon/header.jsp" %>
        <div class="sz-main-content-wrapper">
        
        	<jsp:include page="adminCommon/breadcrum.jsp">
			<jsp:param value="Student Zone;Acads;Upload Content Files" name="breadcrumItems"/>
			</jsp:include>
        	
            
            <div class="sz-main-content menu-closed">
                    <div class="sz-main-content-inner">
              				<jsp:include page="adminCommon/left-sidebar.jsp">
								<jsp:param value="" name="activeMenu"/>
							</jsp:include>
               				<div class="sz-content-wrapper examsPage">
              						<%@ include file="adminCommon/adminInfoBar.jsp" %>
              						<div class="sz-content">
								
								<h2 class="red text-capitalize">Select Subject</h2>
								<div class="clearfix"></div>
		              			<div class="panel-content-wrapper" style="min-height:450px;">
								<%@ include file="adminCommon/messages.jsp" %>
								<form:form modelAttribute="filesSet" method="post"	enctype="multipart/form-data" action="uploadContentFiles">
					<div class="panel-body">
					<div class="col-md-18 column">
					
					<div class="row">
					<div class="col-md-4 column">
						<div class="form-group" style="overflow:visible;">
								<form:select id="subject" path="subject"  class="combobox form-control" required="true"  itemValue="${filesSet.subject}">
									<form:option value="">Type OR Select Subject</form:option>
									<form:options items="${subjectList}" />
								</form:select>
						</div>
					</div>
					
					<div class="col-md-4 column">
					<div class="form-group">
						<form:select id="year" path="year"  class="form-control" required="true" itemValue="${filesSet.year}">
							<form:option value="">Select Academic Year</form:option>
							<form:options items="${yearList}" />
						</form:select>
					</div>
					</div>
					
					<div class="col-md-4 column">
					<div class="form-group">
						<form:select id="month" path="month" class="form-control" required="true" itemValue="${filesSet.month}">
							<form:option value="">Select Academic Month</form:option>
							<form:option value="Jan">Jan</form:option>
							<form:option value="Jul">Jul</form:option>
						</form:select>
					</div>
					</div>
					
					</div>
					<br>
					<h2>Select Files to Upload</h2>
					<br></br>
					<div class="row">
					
					
					
					
					
					<div class="col-sm-2 column">
							<div class="form-group">
							<h4>Document Name</h4>
							</div>
					</div>
					
					
					<div class="col-sm-2 column">	
							<div class="form-group" align="left" >	
							<h4>Description</h4>
							</div>
					</div>
					
					<div class="col-sm-2 column">
							<div class="form-group">
							<h4>Program Structure</h4>
							</div>
					</div>
					
					<div class="col-sm-2 column">
							<div class="form-group" align="left">
							<h4>Attachment</h4>
							</div>
					</div>
					
					<div class="col-sm-2 column">
							<div class="form-group" align="left">
							<h4>URL (If not Attachment)</h4>
							</div>
					</div>
					
					<div class="col-sm-2 column">
							<div class="form-group">
							<h4>URL Type</h4>
							</div>
					</div>
					
					<div class="col-sm-2 column">
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
					
					<div class="col-md-2 column">
							<div class="form-group">
								<select name="contentFiles[<%=i%>].programStructure" >
									<option value="All">All</option>
									<option value="Jul2009">Jul2009</option>
									<option value="Jul2013">Jul2013</option>
									<option value="Jul2014">Jul2014</option>
									<option value="Jan2019">Jul2019</option>
								</select>
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
						<button id="submit" name="submit" class="btn btn-large btn-primary"
						formaction="uploadContentFiles">Upload</button>
					</div>
					</div>

			</div>

			</form:form>
								    </div>
               					    </div>
              					</div>
    					  </div>
					  </div>
			    </div>
        <jsp:include page="adminCommon/footer.jsp"/>
        
		
    </body>
</html> --%>