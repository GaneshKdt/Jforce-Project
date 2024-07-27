<!DOCTYPE html>

<html class="no-js">

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<style>
.error {
	transition: 0.28s;
	overflow: hidden;
	color: red;
	font-style: italic;
	background-color: #ff00007a;
	color: white;
	padding: 2px 10px 1px 10px;
}
</style>
<script type="text/javascript">
</script>

<jsp:include page="../jscss.jsp">
<jsp:param value="Originality Analysis" name="title" />
</jsp:include>

<body class="inside">

<%@ include file="../header.jsp"%>
	
    <section class="content-container login">
        <div class="container-fluid customTheme">
        <div class="row"><legend>Originality Analysis</legend></div>
        <%@ include file="../messages.jsp"%>
		<div class="panel-body clearfix">
		<form:form id='getAssignmentCopyCaseReportForm' action="downloadAssignmentCopyCaseReport" method="post" modelAttribute="searchBean">
			<fieldset>
				<div class="col-md-6 column">
					<div class="form-group">
						<form:select id="year" path="year" type="text"	placeholder="Year" class="form-control" itemValue="${searchBean.year}" required="required">
							<form:option value="">Select Year</form:option>
							<form:options items="${yearList}" />
						</form:select>
					</div>
				
					<div class="form-group">
						<form:select id="month" path="month" type="text" placeholder="Month" class="form-control" itemValue="${searchBean.month}" required="required">
							<form:option value="">Select Month</form:option>
							<form:options items="${examMonthList}" />
						</form:select>
					</div>
					
					<%-- <div class="form-group">
							<form:select id="subject" path="subject" type="text"	placeholder="Subject" class="form-control" itemValue="${searchBean.subject}">
								<form:option value="">Select Subject</form:option>
								<form:options items="${subjectList}" />
							</form:select>
					</div> --%>
					<div class="form-group" style="overflow:visible;">
							<form:select id="subject" path="subject"  class="combobox form-control"   itemValue="${searchBean.subject}" >
								<form:option value="" selected="selected">Type OR Select Subject</form:option>
								<form:options items="${subjectList}" />
							</form:select>
					</div>
					
					<%-- <div class="form-group">
						<input id="minMatchPercent" name="minMatchPercent" path="minMatchPercent" type="text" placeholder="Matching %" class="form-control" value="${searchBean.minMatchPercent}"/>
							<span>Threshold 1 Matching %</span> 
					</div>
				
					<div class="form-group">
						<input id="threshold2" name="threshold2" path="threshold2" type="text" placeholder="Matching %" class="form-control" value="${searchBean.threshold2}"/>
					    <span>Threshold 2 Matching %</span> 
					    <span class="error" style="display:none;"></span> 
					</div> --%>
				</div>		
					
				<div class="col-md-6 column">
					
					<div class="form-group">
						<div class="controls">
							<button id="submit" name="submit" class="btn btn-sm btn-primary" formaction="downloadAssignmentWebCopyCaseReport">Download Copy Case Report</button>
							<button id="cancel" name="cancel" class="btn btn-danger btn-sm" formaction="<spring:eval expression="@propertyConfigurer.getProperty('SERVER_PATH')" />studentportal/home" formnovalidate="formnovalidate">Cancel</button>
						</div>
					</div>
				</div>	
			</fieldset>
			</form:form>
			</div>
			
		</div>
	
		

	</section>

	  <jsp:include page="../footer.jsp" />


</body>

<script>

function validate(){
	var subject = document.getElementById("subject").value;
	if(subject == ""){
		alert("Please enter a subject");
		return false;
	}
}


/* $("#threshold2").keyup(function() { 
	 var  threshold1 = parseInt($( "#minMatchPercent" ).val());
	   var threshold2 = parseInt($( "#threshold2" ).val());
	  if(threshold1 > threshold2){ 
		  $( ".error" ).html("threshold2 should be greater than threshold1");
		  $( ".error" ).css("display","");  
	  }else{
		  $( ".error" ).css("display","none");
	  }
}); 
$( "#getAssignmentCopyCaseReportForm" ).submit(function( event ) {
  
  var  threshold1 = parseInt($( "#minMatchPercent" ).val());
   var threshold2 = parseInt($( "#threshold2" ).val());
  if(threshold1 > threshold2){     
	  return false;   
  }
}); */
</script>


</html>

</html>