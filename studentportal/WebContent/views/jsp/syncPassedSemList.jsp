<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!--> 


<html class="no-js"> <!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<jsp:include page="jscss.jsp">
<jsp:param value="Announcement" name="title" />
</jsp:include>

<body class="inside">

<%@ include file="header.jsp"%>
    <section class="content-container login">
        <div class="container-fluid customTheme">
        <div class="row"><legend>Sync Passed Sem info with salesforce</legend></div>
         <%@ include file="messages.jsp"%> 
		<div class="panel-body">
		
			<form:form  method="post" modelAttribute="bean" enctype="multipart/form-data">
				<div class="row">
					<div class="col-md-6 column">														
					<div class="form-group">
						<form:select id="acadYear" path="acadYear" type="text"	placeholder="Academic Year" class="form-control " required="required">
							<form:option value="">Select Academic Year</form:option>
							<form:options items="${yearList}" />
						</form:select>
					</div>
				
					<div class="form-group">
						<form:select id="acadMonth" path="acadMonth" type="text" placeholder="Academic Month" class="form-control " required="required">
							<form:option value="">Select Academic Month</form:option>
							<form:option value="Jan">Jan</form:option> 
							<form:option value="Mar">Mar</form:option> 
							<form:option value="Apr">Apr</form:option> 
							<form:option value="Jun">Jun</form:option>
							<form:option value="Jul">Jul</form:option> 
							<form:option value="Sep">Sep</form:option> 
							<form:option value="Oct">Oct</form:option>
							<form:option value="Dec">Dec</form:option> 
						</form:select>
					</div> 
					<div class="form-group">
						<form:select id="syncType" path="syncType" type="text"	placeholder="Sync Type" class="form-control " required="required">
							<form:option value="">Select Sync Type </form:option>
							<form:option value="pass">Pass Only </form:option>
							<form:option value="passfail">Pass And Fail </form:option>
						</form:select>
					</div>
					</div>
				</div> 
				<div class="row">
					<div class="col-md-8 column form-group">  
						<button type="submit" formaction="getPassedSemInfoForPg"  class="btn btn-primary " id="add">Trigger For PG Students</button>    
						<button type="submit" formaction="getPassedSemInfoForMbax"  class="btn btn-primary" id="add">Trigger For MBA( X ) Students</button>  
					</div>
				</div>  
				<div class="row">
					<div class="col-md-8 column form-group">   
						<button type="submit" formaction="getPassedSemInfoForMsc"  class="btn btn-primary" id="add">Trigger For MSC Students</button>   
						<button type="submit" formaction="getPassedSemInfoForMbawx"  class="btn btn-primary" id="add">Trigger For MBA( WX ) Students</button>     
					</div> 
				</div>  
				<div class="row">
					<div class="col-md-8 column form-group">   
						<button type="submit" formaction="getPassedSemInfoForPDDM"  class="btn btn-primary" id="addPDDM">Trigger For PDDM Students</button>   
					</div> 
				</div>  
			</form:form> 
			
			
		</div> 
	</div>
</section>


<jsp:include page="footer.jsp" />
<script>
$("#addPDDM").click(function() {
	document.getElementById("acadYear").required = false;
	document.getElementById("acadMonth").required = false;
});
</script>
</body>
</html>