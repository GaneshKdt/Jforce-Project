<html class="no-js"> <!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<jsp:include page="jscss.jsp">
	<jsp:param value="Move TimeBound Mapping" name="title" />
</jsp:include>
<body class="inside">

<%@ include file="header.jsp"%>
	
    <section class="content-container login">
        <div class="container-fluid customTheme">
	       	<div class="row"> 
	       		<legend>Move TimeBound Mapping from Staging</legend>
	       	</div>
	        <%@ include file="messages.jsp"%>
	        
	        <form:form modelAttribute="mappingBean" method="post" action="addTimeBoundStudentMapping">
   		
	   			<fieldset>
					<div class="panel-body">
						<div class="col-md-6 column">
							<div class="form-group">
								<label for="acadYear">Select Acad Year</label>
								<form:select id="acadYear" path="acadYear" class="form-control batchChange" required="required" itemValue="${fileBean.acadYear}">
									<form:option value="">Select Academic Year</form:option>
									<form:options items="${yearList}" />
								</form:select>
							</div>
							
							<div class="form-group">
								<label for="acadMonth">Select Acad Month</label>
								<form:select id="acadMonth" path="acadMonth"  class="form-control batchChange" required="required" itemValue="${fileBean.acadMonth}">
									<form:option value="">Select Academic Month</form:option>
									<form:options items="${monthList }" />
								</form:select>
							</div>
							
							<div class="form-group">
								<label for="semester">Select Sem</label>
								<form:select path="sem" type="text" required="required" placeholder="Select Term" class="form-control" itemValue="${fileBean.sem}">
									<form:option value="">Select Term</form:option>
									<form:option value="3">3/4</form:option>
									<form:option value="5">5</form:option>
								</form:select>
							</div>
							
							<div class="form-group">
								<label for="semester">Select Program Structure</label>
								<form:select id="structure" path="consumerType" type="text" required="required" placeholder="Select Program Structure" 
									class="form-control">
									<form:option value="">Select Structure</form:option>
									<form:option value="160">New Structure</form:option>
									<form:option value="111">Old Structure</form:option>
								</form:select>
							</div>
							
							<div class="clearfix"></div><br>
						
							<div class="form-group">
								<button id="submit" name="submit" type="submit" class="btn btn-large btn-primary">Submit & Move</button>
								<button id="cancel" name="cancel" class="btn btn-danger" formaction="home" formnovalidate="formnovalidate">Cancel</button>
							</div>
							
						</div>
					</div>
				</fieldset>
			
			</form:form>
	  	</div>
	</section>
    
    <jsp:include page="footer.jsp" />
    
    <script type="text/javascript">
    
	    $( document ).ready(function() {
    		let structure = $('#structure').val()
    		if(structure == 160)
    			$("#submit").attr("formaction", "moveStagingToTimeBoundTable_V2");
    		else
    			$("#submit").attr("formaction", "moveStagingToTimeBoundTable");
    		
	    });
    
    	$('#structure').on('change', function(){
    		let structure = $(this).val()
    		if(structure == 160)
    			$("#submit").attr("formaction", "moveStagingToTimeBoundTable_V2");
    		else if(structure == 111 || structure == 151)
    			$("#submit").attr("formaction", "moveStagingToTimeBoundTable");
    	})
    
    </script>

</body>
</html>