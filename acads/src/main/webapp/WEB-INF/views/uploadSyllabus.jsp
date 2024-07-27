<!DOCTYPE html>
<html class="no-js">

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<jsp:include page="jscss.jsp">
	<jsp:param value="Upload Syllabus" name="title" />
</jsp:include>

<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.1.0/jquery.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-3-typeahead/4.0.2/bootstrap3-typeahead.min.js"></script>  
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css" />
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/js/bootstrap.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-multiselect/0.9.13/js/bootstrap-multiselect.js"></script>
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-multiselect/0.9.13/css/bootstrap-multiselect.css" />


<body class="inside">

	<%@ include file="header.jsp"%>


	<section class="content-container login">
		<div class="container-fluid customTheme" style="padding: 20px;">

			<%@ include file="messages.jsp"%>
				
			<form:form modelAttribute="bean" method="post" 	enctype="multipart/form-data">
			
				<div class="panel-body">
					
					<div class="row" style="padding: 20px;">
						<p style="color: #c72127; font-size: 2.0em;">Upload Syllabus</p>	
						<div class="form-group col-md-8">
							<form:label for="subjectcode" path="subjectcode" >Subject Code</form:label>
							<form:select class="form-control" id="subjectcode" path="subjectcode">
								<form:option value="">Select Subject Code</form:option>
								<c:forEach var="subjectCode" items="${ subjectCodes }">
									<form:option value="${ subjectCode.subjectcode }">
										${ subjectCode.subjectcode } (${ subjectCode.subjectname })</form:option>
								</c:forEach>
							</form:select>
						</div>
<!-- 						<div class="form-group col-md-4"> -->
<%-- 							<form:label for="semester" path="sem" >Semester</form:label> --%>
<%-- 							<form:select class="form-control" id="semester" path="sem"> --%>
<%-- 								<form:option value='0'>Select Semester</form:option> --%>
<%-- 							</form:select> --%>
<!-- 						</div> -->
					</div>
					
					<div class="row" style="padding: 20px;">
					
						<div class="col-md-8">
							<div class="form-group">
								<form:label for="file" path="file" style="font-size:1.2em;">Select file</form:label>
								<form:input id="file" path="file" type="file" />
							</div>
						</div>
						
						<div class="col-md-12 column">
							<b>Format of Upload: </b><br>
							Chapter | Title | Topic | Outcome | Pedagogical Tool  <br>
							<a href="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_ACADS_STATIC_RESOURCES')" />resources_2015/templates/SyllabusUpload.xlsx" target="_blank">Download a Sample Template</a>
						</div>
					</div>
					
					<div class="row" style="padding: 20px;"> 
						<div class="col-md-6 column">
							<button id="submit" name="submit" class="btn btn-primary" style="border-radius: 2px;"
								formaction="uploadSyllabus">Upload</button>
						</div>
					</div>
				</div>

			</form:form>
			
		</div>
	</section>

	<jsp:include page="footer.jsp" />

</body>
</html>
