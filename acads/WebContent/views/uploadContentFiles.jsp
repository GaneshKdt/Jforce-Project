<%-- <!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!-->


<html class="no-js">
<!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<jsp:include page="jscss.jsp">
	<jsp:param value="Upload Content Files" name="title" />
</jsp:include>

<body class="inside">

	<%@ include file="header.jsp"%>

	<section class="content-container login">
		<div class="container-fluid customTheme">
			<%@ include file="messages.jsp"%>
			<div class="row"><legend>Select Subject</legend></div>
			
				
				<form:form modelAttribute="filesSet" method="post"	enctype="multipart/form-data" action="uploadContentFiles">
					<div class="panel-body">
					<div class="col-md-18 column">
					
					<div class="row">
					<div class="col-md-6 column">
						<div class="form-group" style="overflow:visible;">
								<form:select id="subject" path="subject"  class="combobox form-control" required="true"  itemValue="${filesSet.subject}">
									<form:option value="">Type OR Select Subject</form:option>
									<form:options items="${subjectList}" />
								</form:select>
						</div>
					</div>
					
					<div class="col-md-6 column">
					<div class="form-group">
						<form:select id="year" path="year"  class="form-control" required="true" itemValue="${filesSet.year}">
							<form:option value="">Select Academic Year</form:option>
							<form:options items="${yearList}" />
						</form:select>
					</div>
					</div>
					
					<div class="col-md-6 column">
					<div class="form-group">
						<form:select id="month" path="month"  required="true" itemValue="${filesSet.month}">
							<form:option value="">Select Academic Month</form:option>
							<form:option value="Jan">Jan</form:option>
							<form:option value="Jul">Jul</form:option>
							<form:option value="Apr">Apr</form:option>
							<form:option value="Oct">Oct</form:option>
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
					
					<div class="col-md-2 column">
							<div class="form-group">
							<h4>Program Structure</h4>
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
					
					<div class="col-md-2 column">
							<div class="form-group">
								<select name="contentFiles[<%=i%>].programStructure" >
									<option value="All">All</option>
									<option value="Jul2009">Jul2009</option>
									<option value="Jul2013">Jul2013</option>
									<option value="Jul2014">Jul2014</option>
									<option value="Jan2018">Jan2018</option>
									<option value="Jan2019">Jan2019</option>
									<option value="Jul2019">Jul2019</option>
									<option value="Jul2020">Jul2020</option>
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
	</section>

	<jsp:include page="footer.jsp" />


</body>
</html>

--%> 
 
<!DOCTYPE html>
<html lang="en">
	
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
    <jsp:include page="jscss.jsp">
		<jsp:param value="Upload Content Files" name="title"/>
    </jsp:include>
    
    
    
    <body class="inside">
    
    	<jsp:include page="header.jsp"/>
    	<%
		            String userId = (String)session.getAttribute("userId_acads");
    	%>
    		<section class="content-container login">
				<div class="container-fluid customTheme">
					<%@ include file="messages.jsp"%>
					<div class="row">
						<legend>Upload Content Files</legend>
					</div>
					
					<form:form modelAttribute="filesSet" method="post"	enctype="multipart/form-data" action="uploadContentFiles">
						<div class="panel-body">
							<div class="col-md-18 column">
								<div class="row">
									<div class="col-md-4 column">
										<div class="form-group">
											<label for="consumerType">Consumer Type</label>
											<select data-id="consumerTypeDataId" id="consumerTypeId" name="consumerTypeId" 
												class="selectConsumerType form-control" required="required">
												<option disabled selected value="">Select Consumer Type</option>
												<c:forEach var="consumerType" items="${consumerType}">
									                <option value="<c:out value="${consumerType.id}"/>">
									                  <c:out value="${consumerType.name}"/>
									                </option>
									            </c:forEach>
											</select>
										</div>
									</div>
									
									<div class="col-md-4 column">
										<div class="form-group">
											<label for="programStructure">Program Structure</label>
											<select data-id="programStructureDataId" id="programStructureId" name="programStructureId" 
												class="selectProgramStructure form-control" required="required">
												<option disabled selected value="">Select Program Structure</option>
											</select>
										</div>
									</div>
									
									<div class="col-md-4 column">
										<div class="form-group">
											<label for="Program">Program</label>
											<select data-id="programDataId" id="programId" name="programId" class="selectProgram form-control">
												<option disabled selected value="">Select Program</option>
											</select>
										</div>
									</div>
									
									<div class="col-md-8 column">
										<div class="form-group">
										<select  data-id="subjectId" name="subject" class="selectSubject form-control">
											<option disabled selected value="">Select Subject</option>
										</select>
										</div>
									</div>
									
	
									<div class="col-md-3 column">
									<div class="form-group">
										<form:select id="year" path="year"  class="form-control" required="true" itemValue="${filesSet.year}">
											<form:option value="">Select Academic Year</form:option>
											<form:options items="${yearList}" />
										</form:select>
									</div>
									</div>
									
									<div class="col-md-3 column">
									<div class="form-group">
										<form:select id="month" path="month" class="form-control" required="true" itemValue="${filesSet.month}">
											<form:option value="">Select Academic Month</form:option>
											<form:option value="Jan">Jan</form:option>
											<form:option value="Jul">Jul</form:option>
										</form:select>
									</div>
									</div>
									<input type= "hidden" name = "sessionPlanModuleId" value = 0/>
									<input type= "hidden" name = "productType" value = "PG"/>
									
									<%-- <div class="col-md-4 column">
									<div class="form-group" style="overflow:visible;">
											<form:select id="subject" path="subject"  class="combobox form-control" required="true"  itemValue="${filesSet.subject}">
												<form:option value="">Type OR Select Subject</form:option>
												<form:options items="${subjectList}" />
											</form:select>
									</div>
									</div> --%>
								</div>
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
									<button id="submit" name="submit" class="btn btn-large btn-primary" formaction="uploadContentFilesNew">Upload</button>
								</div>
							</div>
						</div>
					</form:form>
					
        		</div>
        	</section>
        <jsp:include page="footer.jsp"/>
        
		<jsp:include page="ConsumerProgramStructureCommonDropdown.jsp" />
    </body>
</html> 

