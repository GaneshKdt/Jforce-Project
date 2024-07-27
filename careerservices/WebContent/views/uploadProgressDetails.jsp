<!DOCTYPE html>
<html class="no-js">


<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<jsp:include page="adminCommon/jscss.jsp">
	<jsp:param value="Upload Progress Details" name="title" />
</jsp:include>

<body class="inside">

	<%@ include file="header.jsp"%>


	<section class="content-container login">
		<div class="container-fluid customTheme">
			
			<c:choose>
				<c:when test="${ success == true }">
					<div class="alert alert-success alert-dismissible">
						<button type="button" class="close" data-dismiss="alert"  aria-hidden="true">  &times;  </button>
						${ successMessage }
					</div>
				</c:when>
				<c:when test="${ error == true }">
					<div class="alert alert-danger alert-dismissible">
						<button type="button" class="close" data-dismiss="alert"  aria-hidden="true">  &times;  </button>
						${ errorMessage }
					</div>
				</c:when>
			</c:choose>
			
			<div class="row">
				<legend>Upload Progress Details</legend>
			</div>
			
			<form:form modelAttribute="progressBean" method="post" enctype="multipart/form-data" action="uploadProgressDetails">
				<div class="panel-body">
					<div class="col-md-6 column">
					
						<div class="row">
							<div class="col-md-4 column form-group">
								<form:label for="packageName" path="packageName">Select Package</form:label>
								<form:select id="packageName" path="packageName" class="form-control" required="required">
									<form:option value="" >Select Package</form:option>
									<form:options items="${packages}" itemLabel="packageName" itemValue="packageName"/>
								</form:select>
							</div>
							
							<div class="col-md-4 column form-group">
								<form:label for="durationMax" path="durationMax">Select Duration</form:label>
								<form:select id="durationMax" path="durationMax" class="form-control" required="required">
									<form:option value="0" >Select Duration</form:option>
								</form:select>
							</div>
		
							<div class="col-md-4 column">
								<div class="form-group">
									<form:label for="featureId" path="featureId">Select Feature</form:label>
									<form:select id="featureId" path="featureId" class="form-control" required="required">
										<form:option value="">Select Feature</form:option>
										<form:options items="${features}" itemLabel="featureName" itemValue="featureId"/>
									</form:select>
								</div>
							</div>
						</div>
						
						<div class="form-group">
							<form:label for="fileData" path="fileData">Select file</form:label>
							<form:input path="fileData" type="file" required="required" />
						</div>
<!-- 				form:select to have value and lable using itemLable name and itemValue, featureId and featureName are beanProperty                         -->
				
						<div class="col-md-12 column">
							<b>Format of Upload: </b><br>
							Sapid | Activation Date (DD-MMM-YYYY hh:mm:ss) <br>
							<a href="resources_2015/templates/ProgressDetails.xlsx" target="_blank">Download a Sample Template</a>
						</div>
						
						<div class="row" > 
							<div class="col-md-6 column">
								<button id="submit" name="submit" class="btn btn-large btn-primary"
									formaction="uploadProgressDetails" style="margin: 10px;">Upload</button>
							</div>
						</div>
					</div>
				</div>
			</form:form>
		</div>
	</section>

	<jsp:include page="adminCommon/footer.jsp" />

<script type="text/javascript">

	$(document).ready (function(){

		$('#packageName').on('change', function(){

			let options = "<option>Loading... </option>";
			$('#durationMax').html(options);
			
			var data = {
					packageName:$('#packageName').val(),
			}
			console.log(this.value)
	
			
			$.ajax({
				type : "POST",
				contentType : "application/json",
				url : "/careerservices/m/getDuration",   
				data : JSON.stringify(data),
				success : function(data) {
					
					console.log("SUCCESS: ", data.duration);
					
					var duration = data.duration;
					
					options = ""; 
					//Data Insert For Subjects List
					//Start
					for(let i=0;i < duration.length;i++){
						
						options = options + "<option value='" + duration[i].durationMax + "'> " + duration[i].durationMax + " </option>";
					}
					
					
					console.log("==========> options\n" + options);
					
					$('#durationMax').html(
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

		$('#durationMax').on('change', function(){
			
			let options = "<option>Loading... </option>";

			$('#featureId').html(options);
			
			var data = {
					packageName:$('#packageName').val(),
					durationMax:$('#durationMax').val(),
			}
			console.log(this.value)
	
			
			$.ajax({
				type : "POST",
				contentType : "application/json",
				url : "/careerservices/m/getFeature",   
				data : JSON.stringify(data),
				success : function(data) {
					
					console.log("SUCCESS: ", data.features);
					
					var features = data.features;
					
					options = ""; 
					//Data Insert For Subjects List
					//Start
					for(let i=0;i < features.length;i++){
						
						options = options + "<option value='" + features[i].featureId + "'> " + features[i].featureName + " </option>";
					}
					
					
					console.log("==========> options\n" + options);
					$('#featureId').html(
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



