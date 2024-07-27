<!DOCTYPE html>
<html lang="en">
	
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
    <jsp:include page="jscss.jsp">
		<jsp:param value="Upload Content Files" name="title"/>
    </jsp:include>
    
    
    
    <body class="inside">
    
    	<jsp:include page="header.jsp"/>
    	<%
		            String userId = (String)session.getAttribute("userId");
    	%>
    		<section class="content-container login">
				<div class="container-fluid customTheme">
					<%@ include file="messages.jsp"%>
					<div class="row">
						<legend>Upload Content Files</legend>
					</div>
					
					<form:form modelAttribute="filesSet" method="post"	enctype="multipart/form-data" action="uploadContentFiles" onsubmit = "return validate()">
						<div class="panel-body">
							<div class="col-md-18 column">
								<div class="row">
									
									
									
									
									
	
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
							</div>
									<input type= "hidden" name = "sessionPlanModuleId" value = 0/>
									<input type= "hidden" name = "productType" value = "PG"/>
									
									
							<div class="col-md-18 column">
								<div class="row">	
									<div class="col-md-8 column">
										<div class="form-group" style="overflow:visible;">
											<form:select id="subjectCodeId" path="subjectCodeId" class="combobox form-control" itemValue="${filesSet.subjectCodeId}"> 
												<form:option value="">Select Subject Code</form:option>
												<c:forEach items="${subjectcodes}" var="element">
													<form:option value="${element.subjectCodeId}">${element.subjectcode} ( ${element.subjectName} )</form:option>
												</c:forEach>
											</form:select>
										</div>
										
									<!-- 	<div class="form-group" style="overflow:visible;">
											<form:select id="subjectCodeId" path="subjectCodeId" class="combobox form-control" itemValue="${filesSet.subjectCodeId}"> 
											<form:option value="">Select Subject Code</form:option>
												<c:forEach items="${subjectcodes}" var="element">
												<form:option value="${element.subjectCodeId}">${element.subjectcode} ( ${element.subjectName} )</form:option>
												</c:forEach>
											</form:select>
										</div> -->
										
										
										
									</div>
								</div>
								<input type ="hidden" value="" id ="subject"  name="subject" path="subject"/>
								<label>You can upload content by Subject Code or Programs. Only Select one option</label>
								<div class="row">
									<div class ="col-md-8 column">
										
										<div class="form-group" id="programId">
												<jsp:include page="masterKeySelectWidget.jsp"/>
										</div>
										
									</div>
								</div>
							</div>
									<%-- <div class="col-md-4 column">
									<div class="form-group" style="overflow:visible;">
											<form:select id="subject" path="subject"  class="combobox form-control" required="true"  itemValue="${filesSet.subject}">
												<form:option value="">Type OR Select Subject</form:option>
												<form:options items="${subjectList}" />
											</form:select>
									</div>
									</div> --%>
								
								<br>
								
						<!-- File Upload Start	-->
												
								<h2>Select Files to Upload</h2><br>
								<div class="row">
									<div class="col-md-4 column">
										<div class="form-group">
											<h4>Document Name</h4>
										</div>
									</div>
									
									<div class="col-md-4 column">	
										<div class="form-group" align="left" >	
											<h4>Description</h4>
										</div>
									</div>
									
									<div class="col-sm-3 column">
										<div class="form-group" align="left">
											<h4>Attachment</h4>
										</div>
									</div>
											
									<div class="col-sm-3 column">
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
								<input type= "hidden" name = "createdBy" value = "userId">
								<input type= "hidden" name = "lastModifiedBy" value = "userId">
								<input type= "hidden" name = "sessionPlanModuleId" value = 0>
									<div class="row">
										<div class="col-md-4 column">
											<div class="form-group">
												<input type="text"	size="27px"	 name="contentFiles[<%=i%>].name" placeholder="Name of the Document">
											</div>
										</div>
									
										<div class="col-md-3 column">	
											<div class="form-group" align="left" >	
												<textarea id="description" rows="2" cols="23" name="contentFiles[<%=i%>].description"></textarea>
											</div>
										</div>
										
										<div class="col-md-4 column">
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
									<button id="submit" name="submit" onClick=" { $('#selected_program option').prop('selected', true);}" class="btn btn-large btn-primary" formaction="uploadContentFilesNew">Upload</button>
								</div>
							</div>
						</div>
					</form:form>
						
        		</div>
        	</section>
        <jsp:include page="footer.jsp"/>
         
		<jsp:include page="ConsumerProgramStructureCommonDropdown.jsp" />
    </body>
    <script>
    function validate()
    {
    	
    	
    	var subjectcode = document.getElementById("subjectCodeId").value;
    	
    	var options = document.getElementById('selected_program').selectedOptions;
    	var data = Array.from(options).map(el=>el.value);
    	
    	if(data.length != 0 && subjectcode.length == 0)
    	{
    		var result = findSubjectMappedPssIds(data);
    		
    		if(result == "")
    		{
    			$('#selected_program option').appendTo('#masterKey');
    			
    			alert("More than one subject selected, Please select again.");
    			
    			$('#masterKey').focus();
    			return false;
    		}
    		else{
    			document.getElementById("subject").value = result;
    		}
    	}
    	
    	if(data.length != 0 && subjectcode.length != 0 )
    	{
   
    		alert("Subject Code and Program both are selected. Please select any one of them");
    		return false;
    		
    	}
    	if(data.length == 0 && subjectcode.length == 0 )
    	{
    		
    		alert("Subject Code and Program is blank. Please select any one of them ");
    		return false;
    	}	
    }
 	
    function findSubjectMappedPssIds(data)
    {
    	return  $.ajax({
		   		type : "POST",
		   		contentType : "application/json; charset=utf-8",
		   		url : "/acads/admin/getSubjectNameByPssId", 
		  		data : JSON.stringify(data),
		  		async: false,
		  		cache: false,
		  		success : function(data) {
			    		console.log("SUCCESS "+data); 
			   		},
		 		error : function(e) {
		 			console.log("ERROR: "+ JSON.stringify(e));
		   		}
		  }).responseText;
    	

    }
    
    
    
    </script>
</html> 