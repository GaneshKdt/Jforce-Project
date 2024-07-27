<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!--> 


<html class="no-js"> <!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/functions" prefix = "fn" %>

<jsp:include page="jscss.jsp">
<jsp:param value="Add/Edit Content" name="title" />
</jsp:include>

<body class="inside">

<% boolean isEdit = "true".equals((String)request.getAttribute("edit"));  %>

<%@ include file="header.jsp"%>
	
    <section class="content-container login">
        <div class="container-fluid customTheme">
        <legend>Add/Edit Content Faculty</legend>
        <%@ include file="messages.jsp"%>
		<div class="panel-body">
		
			
			<div class="col-md-6 column">
			
			<form:form  action="addContent" method="post" modelAttribute="content">
			<fieldset>

				<%if(isEdit){ %>
				<form:input type="hidden" path="id" value="${content.id}"/>
				<form:input type="hidden" path="consumerProgramStructureId" value="${content.consumerProgramStructureId}"/>
				<form:input type="hidden" path="subject" value="${content.subject}"/>
				<form:input type="hidden" path="allowedToUpdate" value="${content.allowedToUpdate}"/>
				<form:input type="hidden" path="year" value="${content.year}"/>
				<form:input type="hidden" path="month" value="${content.month}"/>
				<form:input type="hidden" path="consumerTypeId" value="${content.consumerTypeId}"/>
				<form:input type="hidden" path="programStructureId" value="${content.programStructureId}"/>
				<form:input type="hidden" path="programId" value="${content.programId}"/>
				<form:input type="hidden" path="filePath" value="${content.filePath}"/>
				<form:input type="hidden" path="previewPath" value="${content.previewPath}"/>
				<form:input type="hidden" path="programSemSubjectId" value="${content.programSemSubjectId}"/>
				<input type = "hidden" name="subjectCodeId" value = "${content.subjectCodeId}"/>
				<form:input type="hidden" path="activeDate" value="${content.activeDate}"/>
				<%} %>
				
				<c:choose>
					
		            <c:when  test="${content.allowedToUpdate eq 'true'}">
				 <!-- code for configuration start -->
					<div class="row">
						
						<div class="form-group">
							<c:choose>
								<c:when test="${content.editSingleContentFromCommonSetup eq true}">
									<form:label path="consumerTypeId" for="consumerTypeId">Consumer Type</form:label>
									<form:select id="consumerTypeId" path="consumerTypeId" type="text"	placeholder="consumerType" 
												 class="form-control selectConsumerType" required="required" readonly="readonly"> 
		
						            	<c:forEach var="consumerType" items="${consumerType}">
							                <c:if test='${content.consumerTypeId eq consumerType.id}'>
												 <form:option value="${consumerType.id}" selected="true">
							                		<c:out value="${consumerType.name}"/>
							                	 </form:option>
											</c:if>
						            	</c:forEach>
									</form:select>
								</c:when>
								
								<c:otherwise>
									<form:select id="consumerTypeId" path="consumerTypeId" type="text" placeholder="consumerType" 
									 class="form-control selectConsumerType" required="required" readonly="readonly"> 
									
										<form:option value="">Select Consumer Type</form:option>
										
										<c:forEach var="consumerType" items="${consumerType}">
							                <form:option value="${consumerType.id}">
							                	<c:out value="${consumerType.name}"/>
							                </form:option>
					                		
					                		<c:if test='${content.consumerTypeId eq consumerType.id}'>
												<form:option value="">Select Consumer Type</form:option>
											</c:if>
					            		</c:forEach>
					            	
					            		<c:forEach var="consumerType" items="${consumerType}">
					                		<c:if test='${content.consumerTypeId eq consumerType.id}'>
										 		<form:option value="${consumerType.id}" selected="true">
					                				<c:out value="${consumerType.name}"/>
					                	 		</form:option>
					               			</c:if>
					            		</c:forEach>
									</form:select>
								</c:otherwise>
								
							</c:choose>
						</div>	
						
						<div class="form-group">
							<c:choose>
								<c:when test="${content.editSingleContentFromCommonSetup eq true}">
									
									<form:label path="programStructureId" for="programStructureId">Program Structure </form:label>
										<form:select id="programStructureId" path="programStructureId" type="text" placeholder="Program Structure" 
													 	class="form-control selectProgramStructure" required="required" itemValue="${content.programStructureId}"  
													 		readonly="readonly"> 
											<form:option value="">Select Program Structure</form:option>
													
												<c:choose>
										            <c:when test="${fn:length(fn:split(content.programStructureId, ',')) == 1}">
												    	<c:forEach var="entry" items="${programStructureIdNameMap}">
											                <c:if test='${content.programStructureId eq entry.key}'>
																 <form:option value="${entry.key}" selected="true">
											                		<c:out value="${entry.value}"/>
											                	 </form:option>
															</c:if>
										            	</c:forEach>	
										            </c:when>
										            
										            <c:when test="${fn:length(fn:split(content.programStructureId, ',')) > 1}">
												       	<form:option value="${content.programStructureId}" selected="true">
										                	<c:out value="All"/>
										         		</form:option>
													</c:when>
										            
										            <c:otherwise>
										            	<form:option value="">Re-Select Consumer Type</form:option>
													</c:otherwise>
													
										        </c:choose>
											</form:select>
											
										</c:when>
									<c:otherwise>
											
										<form:label path="programStructureId" for="programStructureId">Program Structure </form:label>
										<form:select id="programStructureId" path="programStructureId" type="text" placeholder="Program Structure" class="form-control selectProgramStructure" 
														required="required" itemValue="${content.programStructureId}" readonly="readonly"> 
											<form:option value="">Select Program Structure</form:option>
											
											<c:choose>
											
											    <c:when test="${fn:length(fn:split(content.programStructureId, ',')) == 1}">
													<c:forEach var="entry" items="${programStructureIdNameMap}">
									                <c:if test='${content.programStructureId eq entry.key}'>
														 <form:option value="${entry.key}" selected="true">
									                		<c:out value="${entry.value}"/>
									                	 </form:option>
													</c:if>
									            	</c:forEach>
											  	</c:when>
											            
									            <c:when test="${fn:length(fn:split(content.programStructureId, ',')) > 1}">
											         <form:option value="${content.programStructureId}" selected="true">
								                		<c:out value="All"/>
								                	 </form:option>
									            </c:when>
									            <c:otherwise>
									            	<form:option value="">Re-Select Consumer Type</form:option>
												</c:otherwise>
												
									        </c:choose>
													
										</form:select>
											
									</c:otherwise>
								</c:choose>		
							</div>
							
							<div class="form-group">
								<c:choose>
								 <c:when test="${content.editSingleContentFromCommonSetup eq true}">
								
									<form:label path="programId" for="programId">Program </form:label>
									<form:select id="programId" path="programId" type="text" placeholder="Program " class="form-control selectProgram" 
													required="required" itemValue="${content.programId}"  readonly="readonly"> 
											
										<c:choose>
										
								            <c:when test="${fn:length(fn:split(content.programId, ',')) == 1}"><c:forEach var="entry" items="${programIdNameMap}">
								                <c:if test='${content.programId eq entry.key}'>
													 <form:option value="${entry.key}" selected="true">
								                		<c:out value="${entry.value}"/>
								                	 </form:option>
												</c:if>
								            	</c:forEach>
								            </c:when>
								            
								            <c:when test="${fn:length(fn:split(content.programId, ',')) > 1}">
										            	
								            		 <form:option value="${content.programId}" selected="true">
								                		All
								                	 </form:option>
												
								            </c:when>
								            
								            <c:otherwise>
								            	<form:option value="">Re-Select Consumer Type</form:option>
											</c:otherwise>
											
								        </c:choose>
											
									</form:select>
								
								</c:when>
								
								<c:otherwise>
									
									<form:label path="programId" for="programId">Program </form:label>
									<form:select id="programId" path="programId" type="text"
											     placeholder="Program " class="form-control selectProgram" required="required" 
											 itemValue="${content.programId}"  readonly="readonly"> 
											<form:option value="">Select Program</form:option>
											
											<c:choose>
									            <c:when test="${fn:length(fn:split(content.programId, ',')) == 1}">
											            	
									            	<c:forEach var="entry" items="${programIdNameMap}">
									                <c:if test='${content.programId eq entry.key}'>
														 <form:option value="${entry.key}" selected="true">
									                		<c:out value="${entry.value}"/>
									                	 </form:option>
													</c:if>
									            	</c:forEach>
									            	
									            </c:when>
									            
									            <c:when test="${fn:length(fn:split(content.programId, ',')) > 1}">
											            	
									            		 <form:option value="${content.programId}" selected="true">
									                		All
									                	 </form:option>
													
									            </c:when>
									            <c:otherwise>
									            	<form:option value="">Re-Select Consumer Type</form:option>
												</c:otherwise>
									        </c:choose>
											
									</form:select>
								
								</c:otherwise>
							</c:choose>		
						</div>
					
					</div>
										
										<!-- code for configuration end -->
										
					</c:when>
		            <c:otherwise>
		            	<form:input type="hidden" path="consumerTypeId" value="${content.consumerTypeId}"/>
		            	
		            	<div class="form-group">
							<form:label path="programStructure" for="programStructure">Applicable Program Structure</form:label>
							<form:select id="programStructure" path="programStructure" class="form-control" required="true" itemValue="${content.programStructure}">
								<form:option value="All">All</form:option>
								<form:options items="${programStrutureList }"/>
							</form:select>
						</div>
		            	
		            </c:otherwise>
		        </c:choose>
				
				
				<div class="form-group">
					<form:label path="name" for="name">Name of the Document</form:label>
					<form:input id="name" path="name" type="text" placeholder="Name of the Document" class="form-control" required="required" value="${content.name}"/>
				</div>
				
				<div class="form-group">
					<form:label path="description" for="description">Description</form:label>
					<form:textarea path="description" id ="description" placeholder="Description" value="${content.name}" cols="47"/>
				</div>
				
				<div class="form-group">
					<form:label path="webFileurl" for="webFileurl">Web URL</form:label>
					<form:input id="webFileurl" path="webFileurl" type="text" placeholder="Web URL" class="form-control"  value="${content.webFileurl}"/>
				</div>
				
				<div class="form-group">
					<form:label path="urlType" for="urlType">Web URL Type</form:label>
					<form:select id="urlType" path="urlType"  class="form-control" required="true" itemValue="${content.urlType}">
						<form:option value="View">View</form:option>
						<form:option value="Download">Download</form:option>
					</form:select>
				</div>
				
				<div class="form-group">
					<form:label path="contentType" for="contentType">Content Type</form:label>
					<form:select id="contentType" path="contentType"  class="form-control" required="true" itemValue="${content.contentType}">
						<form:option value="">Select</form:option>
						<form:option value="Course Material">Course Material</form:option>
						<form:option value="Course Presentation">Course Presentation</form:option>
						<form:option value="Session Recording">Session Recording</form:option>
						<form:option value="Session Plan">Session Plan</form:option>
						<form:option value="Session Presentation">Session Presentation</form:option>
						<form:option value="Reading Material">Reading Material</form:option>
					</form:select>
				</div>
				
	
			     <c:if test="${content.editSingleContentFromCommonSetup eq true}">
			    		
				<div class="form-group">
					<label class="control-label" for="submit"></label>
					<div class="controls">
						<%if("true".equals((String)request.getAttribute("editMapping"))){ %>
							<button id="submit" name="submit" class="btn btn-large btn-primary" formaction="updateSingleContentMapping"
									onClick="return confirm('Are you sure? Saving these changes will delete old mappings.')" >Update</button>
						<%}else	{%>
							
							<button id="submit" name="submit" class="btn btn-large btn-primary" formaction="updateSingleContentFromCommonSetup"
									onClick="return confirm('Are you sure? Saving these changes will delete old mappings.')" >Update</button>
						<%} %>		
									
							<button id="cancel" name="cancel" class="btn btn-danger" formaction="acadsHome" formnovalidate="formnovalidate">Cancel</button>
					</div>
				</div>
				 
			     </c:if>
			     
			      <c:if test="${content.editSingleContentFromCommonSetup ne true}">
			    		
					<div class="form-group">
						<label class="control-label" for="submit"></label>
						<div class="controls">
							<%if("true".equals((String)request.getAttribute("edit"))){ %>
								<%if("true".equals((String)request.getAttribute("editMapping"))){ %>
									<input type = "hidden" name="masterKeys" value = "${consumerProgramStructureId }"/>
									<button id="submit" name="submit" class="btn btn-large btn-primary" formaction="updateContents">Update</button>
								<%}else	{%>
									<button id="submit" name="submit" class="btn btn-large btn-primary" formaction="updateContent">Update</button>
								<%} %>
							<%}else	{%>
								<button id="submit" name="submit" class="btn btn-large btn-primary" formaction="addFaculty">Submit</button>
							<%} %>
							<button id="cancel" name="cancel" class="btn btn-danger" formaction="acadsHome" formnovalidate="formnovalidate">Cancel</button>
						</div>
					</div>
				 
			     </c:if>
				
			</fieldset>
		</form:form>

		</div>
		</div>
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
		url : "/exam/admin/getDataByProgramStructure",   
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



</script>

</body>
</html>
