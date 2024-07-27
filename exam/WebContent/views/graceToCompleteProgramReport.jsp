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
	<jsp:param value="Report for Program Completed Students" name="title" />
</jsp:include>

<body class="inside">

	<%@ include file="header.jsp"%>

	<section class="content-container login">
		<div class="container-fluid customTheme">

			<div class="row"><legend>Report for Students needing < 10 marks to complete program</legend></div>
			
				<%@ include file="messages.jsp"%>

<div class="row clearfix">
				<form:form  action="/exam/admin/graceToCompleteProgramReport" method="post" modelAttribute="studentMarks">
			<fieldset>
			<div class="col-md-6 column">

					
					
					<div class="form-group">
						<form:select id="writtenYear" path="year" type="text"	placeholder="Written Year" class="form-control"   itemValue="${studentMarks.year}">
							<form:option value="">Select Written Year / Validity End Year</form:option>
							<form:options items="${yearList}" />
						</form:select>
					</div>
					
					<div class="form-group">
						<form:select id="writtenMonth" path="month" type="text" placeholder="Written Month" class="form-control"  itemValue="${studentMarks.month}">
							<form:option value="">Select Written Month / Validity End Month</form:option>
							<form:option value="Jun">Jun</form:option>
							<form:option value="Dec">Dec</form:option>
						</form:select>
					</div>
					
					<div class="form-group">
					<button id="submit" name="submit" class="btn btn-large btn-primary"
						formaction="/exam/admin/graceToCompleteProgramReport">Generate</button>
						<button id="cancel" name="cancel" class="btn btn-danger" formaction="home" formnovalidate="formnovalidate">Cancel</button>
					</div>
					
					
					
				
				</div>
				
				</fieldset>
				</form:form>
				
		</div>
		
		<c:choose>
<c:when test="${rowCount > 0}">

	<legend>&nbsp;Student Marks <font size="2px">(${rowCount} Records Found) &nbsp; <a href="/exam/admin/downloadGraceToCompleteProgramReport">Download to Excel</a></font></legend>
	<div class="table-responsive">
	<table class="table table-striped" style="font-size:12px">
						<thead>
						<tr>
							<th>Sr. No.</th>
							<th>SAP ID</th>
							<th>Student Name</th>
							<th>Program</th>
							<th>Total Grace Needed</th>
						</tr>
					</thead>
						<tbody>
						
						<c:forEach var="studentMarks" items="${studentMarksList}" varStatus="status">
					        <tr>
					            <td><c:out value="${status.count}" /></td>
								<td><c:out value="${studentMarks.sapid}" /></td>
								<td><c:out value="${studentMarks.name}" /></td>
								<td><c:out value="${studentMarks.program}" /></td>
								<td><c:out value="${studentMarks.gracemarks}" /></td>
					        </tr>   
					    </c:forEach>
							
							
						</tbody>
					</table>
	</div>
	<br>

</c:when>
</c:choose>

	</div>	
	</section>

	<jsp:include page="footer.jsp" />


</body>
</html>
 --%>
 

 <%-- <!DOCTYPE html>
<html lang="en">
	
   <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%> 


    <jsp:include page="adminCommon/jscss.jsp">
	<jsp:param value="Report for Program Completed Students" name="title"/>
    </jsp:include>
    
    
    
    <body>
    
    	<%@ include file="adminCommon/header.jsp" %>
        <div class="sz-main-content-wrapper">
        
        	<jsp:include page="adminCommon/breadcrum.jsp">
			<jsp:param value="Exam;Report for Program Completed Students" name="breadcrumItems"/>
			</jsp:include>
        	
            
            <div class="sz-main-content menu-closed">
                    <div class="sz-main-content-inner">
              				<jsp:include page="adminCommon/left-sidebar.jsp">
								<jsp:param value="" name="activeMenu"/>
							</jsp:include>
              				
              				
              				<div class="sz-content-wrapper examsPage">
              						<%@ include file="adminCommon/adminInfoBar.jsp" %>
              						<div class="sz-content">
								
											<h2 class="red text-capitalize">Report for Students needing < 10 marks to complete program</h2>
											<div class="clearfix"></div>
											<div class="panel-content-wrapper" style="min-height:450px;">
											<%@ include file="adminCommon/messages.jsp" %>
									<form:form  action="/exam/admin/graceToCompleteProgramReport" method="post" modelAttribute="studentMarks">
									<fieldset>
									<div class="col-md-8">
										<div class="form-group">
												<form:select id="writtenYear" path="year" type="text"	placeholder="Written Year" class="form-control"   itemValue="${studentMarks.year}">
													<form:option value="">Select Written Year</form:option>
													<form:options items="${yearList}" />
												</form:select>
											</div>
											
											<div class="form-group">
												<form:select id="writtenMonth" path="month" type="text" placeholder="Written Month" class="form-control"  itemValue="${studentMarks.month}">
													<form:option value="">Select Written Month / Validity End Month</form:option>
													<form:option value="Apr">Apr</form:option>
													<form:option value="Jun">Jun</form:option>
													<form:option value="Sep">Sep</form:option>
													<form:option value="Dec">Dec</form:option>
													
												</form:select>
											</div>
											
											
											<div class="form-group">
											<button id="submit" name="submit" class="btn btn-large btn-primary"
												formaction="/exam/admin/graceToCompleteProgramReport">Generate</button>
												<button id="cancel" name="cancel" class="btn btn-danger" formaction="home" formnovalidate="formnovalidate">Cancel</button>
											</div>
										</div>
										</fieldset>
										</form:form>
											 </div>
								<c:choose>
									<c:when test="${rowCount > 0}">
									<h2>&nbsp;Student Marks 
									<font size="2px">(${rowCount} Records Found) &nbsp; 
										<a href="/exam/admin/downloadGraceToCompleteProgramReport">Download to Excel</a>
									</font></h2>
									
									<div class="clearfix"></div>
									<div class="panel-content-wrapper">
									<div class="table-responsive">
									<table class="panel-body table table-striped" style="font-size:12px">
														<thead>
														<tr>
															<th>Sr. No.</th>
															<th>SAP ID</th>
															<th>Student Name</th>
															<th>Program</th>
															<th>Total Grace Needed</th>
														</tr>
													</thead>
														<tbody>
														
														<c:forEach var="studentMarks" items="${studentMarksList}" varStatus="status">
															<tr>
																<td><c:out value="${status.count}" /></td>
																<td><c:out value="${studentMarks.sapid}" /></td>
																<td><c:out value="${studentMarks.name}" /></td>
																<td><c:out value="${studentMarks.program}" /></td>
																<td><c:out value="${studentMarks.gracemarks}" /></td>
															</tr>   
														</c:forEach>
														</tbody>
													</table>
												</div>
												<br>
										</c:when>
										</c:choose>
              						</div>
              				   </div>
    				       </div>
			           </div>
		           </div>
        <jsp:include page="adminCommon/footer.jsp"/>
    </body>
</html>   --%>

<%-- Latest working code below --%>
<%-- Added by stef--%>
<%-- <!DOCTYPE html>
<html lang="en">
	
   <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%> 


    <jsp:include page="adminCommon/jscss.jsp">
	<jsp:param value="Report for Program Completed Students" name="title"/>
    </jsp:include>
    
    
    
    <body>
    
    	<%@ include file="adminCommon/header.jsp" %>
        <div class="sz-main-content-wrapper">
        
        	<jsp:include page="adminCommon/breadcrum.jsp">
			<jsp:param value="Exam;Report for Program Completed Students" name="breadcrumItems"/>
			</jsp:include>
        	
            
            <div class="sz-main-content menu-closed">
                    <div class="sz-main-content-inner">
              				<jsp:include page="adminCommon/left-sidebar.jsp">
								<jsp:param value="" name="activeMenu"/>
							</jsp:include>
              				
              				
              				<div class="sz-content-wrapper examsPage">
              						<%@ include file="adminCommon/adminInfoBar.jsp" %>
              						<div class="sz-content">
								
											<h2 class="red text-capitalize">Report for Students needing <= 10 or 12 marks to complete program</h2>
											<div class="clearfix"></div>
											<div class="panel-content-wrapper" style="min-height:100%;">
											<div class = "js_result">
													
													</div>
											<%@ include file="adminCommon/messages.jsp" %>
									<form:form  action="/exam/admin/graceToCompleteProgramReport" method="post" modelAttribute="studentMarks">
									<fieldset>
									<div class="col-md-8">
										<div class="form-group">
												<form:select id="writtenYear" path="year" type="text" required="required"	placeholder="Written Year" class="form-control"   itemValue="${studentMarks.year}">
													<form:option value="">Select Written Year</form:option>
													<form:options items="${yearList}" />
												</form:select>
											</div>
											
											<div class="form-group">
												<form:select id="writtenMonth" path="month" type="text" required="required" placeholder="Written Month" class="form-control"  itemValue="${studentMarks.month}">
													<form:option value="">Select Written Month / Validity End Month</form:option>
													<form:option value="Apr">Apr</form:option>
													<form:option value="Jun">Jun</form:option>
													<form:option value="Sep">Sep</form:option>
													<form:option value="Dec">Dec</form:option>
													
												</form:select>
											</div>
										
											 <div class="form-group">
												<form:select id="program" path="program" type="text"	placeholder="Select Program" class="form-control"   itemValue="${studentMarks.program}">
													<form:option value="">Select Program</form:option>
													<form:options items="${programList}" />
												</form:select>
											</div>
											<div class="form-group">
												<form:select id="programStructApplicable" path="programStructApplicable" type="text" required="required"	placeholder="Program Structure" class="form-control"   itemValue="${studentMarks.programStructApplicable}">
													<form:option value="">Select Program Structure</form:option>
													<form:options items="${programStructureList}" />
												</form:select>
											</div> 
											
											<div class="form-group">
												<form:select id="gracemarks" path="gracemarks" type="text" required="required"	placeholder="Max Grace Applicable" class="form-control"   itemValue="${studentMarks.gracemarks}">
													<form:option value="">Select Max Grace Applicable</form:option>
													<form:option value="4">4</form:option>
													<form:option value="10">10</form:option> 
													<form:option value="12">12</form:option> 
												</form:select>
											</div>
											
											<div class="form-group">
											<button id="submit" name="submit" class="btn btn-large btn-primary"
												formaction="/exam/admin/graceToCompleteProgramReport">Generate</button>
												<button id="cancel" name="cancel" class="btn btn-danger" formaction="home" formnovalidate="formnovalidate">Cancel</button>
											</div>
										</div>
										</fieldset>
										</form:form>
											 </div>
								<c:choose>
									<c:when test="${rowCount > 0}">
									<h2>&nbsp;Student Marks 
									<font size="2px">(${rowCount} Records Found) &nbsp; 
										<a href="/exam/admin/downloadGraceToCompleteProgramReport">Download to Excel</a>
									</font></h2>
									
									<div class="clearfix"></div>
									<div class="panel-content-wrapper">
									<div class="table-responsive">
									<table class="panel-body table table-striped" style="font-size:12px">
													<thead>
														<tr>
															<th>Sr. No.</th>
															<th>SAP ID</th>
															<th>Student Name</th>
															<th>Program</th>
															<th>Total Grace Needed</th>
															<th>Action</th>
															
														</tr>
													</thead>
													<tbody>
															<c:forEach var="studentMarks" items="${studentMarksList}" varStatus="status">
																<tr>
																	<td class="count"><c:out value="${status.count}" /></td>
																	<td><c:out value="${studentMarks.sapid}" /></td>
																	<td><c:out value="${studentMarks.name}" /></td>
																	<td><c:out value="${studentMarks.program}" /></td>
																	<td class = "score" data-count = ${status.count}><c:out value="${studentMarks.gracemarks}" /></td>
																	<td>
																	<div class="row">
													 					<button data-program="${studentMarks.program}" data-sapid="${studentMarks.sapid}" data-totalGracemarks="${studentMarks.gracemarks}" 
																        data-sem="${studentMarks.sem}" data-studentType="${studentMarks.studentType }"
																        data-resultProcessedYear = "${examProcessingYear}" data-resultProcessedMonth = "${examProcessingMonth}"
																        id="grace${status.count}" type="button" style = "background-color: #c62828"  
																        class ="result_grace"><i class="material-icons right"></i>Apply Grace</button>
																        <button 
																        id="grace_download${status.count}" type="button" style = "background-color: #c62828 ; display : none"  
																        class ="result_grace_download" 
																        onclick = "location.href='/exam/admin/downloadGraceAppliedDetails?program=${studentMarks.program}&sapid=${studentMarks.sapid}&totalGrace=${studentMarks.gracemarks}'">Download</button>
													 				</div>
													 				</td>
													 				
																</tr>   
															</c:forEach>
														</tbody>
													</table>
												</div>
												<br>
										</c:when>
										</c:choose>
              						</div>
              				   </div>
    				       </div>
			           </div>
		           </div>
        <jsp:include page="adminCommon/footer.jsp"/>
        <script type="text/javascript">
			 $('.result_grace').click(function(){

			        	var conf = confirm('Are you sure you want to apply grace? This is not reversible');
			        	if(conf == true){
			        		$('.js_result').html('<h3>loading...</h3>');
			        	var self = $(this);
		                 $.ajax({
		                    type: "POST",
		                    url: '/exam/admin/applyGraceforValidityEnd',
		                    data: {
		                    	'program' : $(this).attr('data-program'),
		                    	'totalGracemarks': $(this).attr('data-totalGracemarks'),
		                    	'sapid' : $(this).attr('data-sapid'),
		                    	'studentType' : $(this).attr('data-studentType'),
		                    	'resultProcessedYear' : $(this).attr('data-resultProcessedYear'),
		                    	'resultProcessedMonth' : $(this).attr('data-resultProcessedMonth'),
		                    }, 
		                    success:function(response){
		                    	if(response.Status == "Success"){
		                    		var count = self.parents('tr').children('.count').html();
		                    		console.log("The row count is "+count);
		                			
		                    			$('.js_result').html('<div class="alert alert-success alert-dismissible"> <a href="#" class="close" data-dismiss="alert" aria-label="close">&times;</a> <strong>Success!</strong> grace applied. </div>');
		                    			 self.parents('tr').children('.score').html('0');
		                    			 $('#grace'+count).css('background-color', "#c5e1a5");
		                    			 $('#grace_download'+count).css('display', 'block');
		                    			 
		                    	}else{
		                    		$('.js_result').html('<div class="alert alert-danger alert-dismissible"> <a href="#" class="close" data-dismiss="alert" aria-label="close">&times;</a> <strong>Failed!</strong> grace not applied. </div>');
		                    	}
		                   },
		                   error:function(){
		                	   $('.js_result').html('<div class="alert alert-danger alert-dismissible"> <a href="#" class="close" data-dismiss="alert" aria-label="close">&times;</a> <strong>Failed!</strong> Server Error Found. </div>');
		                   }
		                });
		        	}else{
		        		$(this).parent().children('.score').html();
		        		console.log($(this).parent().children('.score').html());
		        		this.checked = false;
		        		
		        	}
		        }); 
			 

		  </script>
    </body>
</html>   --%>


<!DOCTYPE html>
<html lang="en">
	
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%> 


    <jsp:include page="adminCommon/jscss.jsp">
	<jsp:param value="Report for Program Completed Students" name="title"/>
    </jsp:include>
    
    
    
    <body>
    
    	<%@ include file="adminCommon/header.jsp" %>
        <div class="sz-main-content-wrapper">
        
        	<jsp:include page="adminCommon/breadcrum.jsp">
			<jsp:param value="Exam;Report for Program Completed Students" name="breadcrumItems"/>
			</jsp:include>
        	
            
            <div class="sz-main-content menu-closed">
                    <div class="sz-main-content-inner">
              				<jsp:include page="adminCommon/left-sidebar.jsp">
								<jsp:param value="" name="activeMenu"/>
							</jsp:include>
              				
              				
              				<div class="sz-content-wrapper examsPage">
              						<%@ include file="adminCommon/adminInfoBar.jsp" %>
              						<div class="sz-content">
								
											<h2 class="red text-capitalize">Report for Students needing <= 10 or 12 marks to complete program</h2>
											<div class="clearfix"></div>
											<div class="panel-content-wrapper" style="min-height:100%;">
											<div class = "js_result">
													
													</div>
											<%@ include file="adminCommon/messages.jsp" %>
									<form:form  action="/exam/admin/graceToCompleteProgramReport" method="post" modelAttribute="studentMarks">
									<fieldset>
									<div class="col-md-8">
										<div class="form-group">
												<form:select id="writtenYear" path="year" type="text" required="required"	placeholder="Written Year" class="form-control"   itemValue="${studentMarks.year}">
													<form:option value="">Select Written Year</form:option>
													<form:options items="${yearList}" />
												</form:select>
											</div>
											
											<div class="form-group">
												<form:select id="writtenMonth" path="month" type="text" required="required" placeholder="Written Month" class="form-control"  itemValue="${studentMarks.month}">
													<form:option value="">Select Written Month / Validity End Month</form:option>
													<form:option value="Apr">Apr</form:option>
													<form:option value="Jun">Jun</form:option>
													<form:option value="Sep">Sep</form:option>
													<form:option value="Dec">Dec</form:option>
													
												</form:select>
											</div>
										
											 <%-- <div class="form-group">
												<form:select id="program" path="program" type="text"	placeholder="Select Program" class="form-control"   itemValue="${studentMarks.program}">
													<form:option value="">Select Program</form:option>
													<form:options items="${programList}" />
												</form:select>
											</div>
											<div class="form-group">
												<form:select id="programStructApplicable" path="programStructApplicable" type="text" required="required"	placeholder="Program Structure" class="form-control"   itemValue="${studentMarks.programStructApplicable}">
													<form:option value="">Select Program Structure</form:option>
													<form:options items="${programStructureList}" />
												</form:select>
											</div>  --%>
											<div class="form-group">
										
												<form:select data-id="consumerTypeDataId" required="required" value="${ studentMarks.consumerType }" id="consumerTypeId" name="consumerTypeId"   path = "consumerType" class="selectConsumerType form-control" >
													<option disabled selected value="">Select Consumer Type</option>
													<c:forEach var="consumerType" items="${consumerType}">
										                
										                <c:choose>
															<c:when test="${consumerType.id == studentMarks.consumerType}">
																<option selected value="<c:out value="${consumerType.id}"/>">
												                  <c:out value="${consumerType.name}"/>
												                </option>
															</c:when>
															<c:otherwise>
																<option value="<c:out value="${consumerType.id}"/>">
												                  <c:out value="${consumerType.name}"/>
												                </option>
															</c:otherwise>
														</c:choose>
														
										            </c:forEach>
												</form:select>
										</div>
										<div class="form-group">
										
												<form:select id="programStructureId" required="required" name="programStructureId"  path = "programStructApplicable" class="selectProgramStructure form-control" >
													<option disabled selected value="">Select Program Structure</option>
												</form:select>
										</div>
										<div class="form-group">
											
												<form:select id="programId" required="required" name="programId" path = "program" class="selectProgram form-control" >
													<option disabled selected value="">Select Program</option>
												</form:select>
										</div>
											<div class="form-group">
												<form:select id="gracemarks" path="gracemarks" type="text" required="required"	placeholder="Max Grace Applicable" class="form-control"   itemValue="${studentMarks.gracemarks}">
													<form:option value="">Select Max Grace Applicable</form:option>
													<form:option value="4">4</form:option>
													<form:option value="10">10</form:option> 
													<form:option value="12">12</form:option> 
												</form:select>
											</div>
											
											<div class="form-group">
											<button id="submit" name="submit" class="btn btn-large btn-primary"
												formaction="/exam/admin/graceToCompleteProgramReport">Generate</button>
												<button id="cancel" name="cancel" class="btn btn-danger" formaction="home" formnovalidate="formnovalidate">Cancel</button>
											</div>
										</div>
										</fieldset>
										</form:form>
											 </div>
								<c:choose>
									<c:when test="${rowCount > 0}">
									<h2>&nbsp;Student Marks 
									<font size="2px">(${rowCount} Records Found) &nbsp; 
										<a href="/exam/admin/downloadGraceToCompleteProgramReport">Download to SubjectWise Excel</a>
									</font></h2>
									
									<div class="clearfix"></div>
									<div class="panel-content-wrapper">
									<div class="table-responsive">
									<table class="panel-body table table-striped" style="font-size:12px">
													<thead>
														<tr>
															<th>Sr. No.</th>
															<th>SAP ID</th>
															<th>Student Name</th>
															<th>Program</th>
															<th>Total Grace Needed</th>
															<th>Action</th>
															
														</tr>
													</thead>
													<tbody>
															<c:forEach var="studentMarks" items="${studentMarksList}" varStatus="status">
																<tr>
																	<td class="count"><c:out value="${status.count}" /></td>
																	<td><c:out value="${studentMarks.sapid}" /></td>
																	<td><c:out value="${studentMarks.name}" /></td>
																	<td><c:out value="${studentMarks.program}" /></td>
																	<td class = "score" data-count = ${status.count}><c:out value="${studentMarks.gracemarks}" /></td>
																	<td>
																	<div class="row">
																		<img id="img1${status.count}" src="/exam/resources_2015/gifs/loading-29.gif" height="30px"  width="30px"  style = " display : none"/>
													 					<button data-program="${studentMarks.program}" data-sapid="${studentMarks.sapid}" data-totalGracemarks="${studentMarks.gracemarks}" 
																        data-sem="${studentMarks.sem}" data-studentType="${studentMarks.studentType }"
																        data-resultProcessedYear = "${examProcessingYear}" data-resultProcessedMonth = "${examProcessingMonth}"
																        id="grace${status.count}" type="button" style = "background-color: #c62828"  
																        class ="result_grace"><i class="material-icons right"></i>Apply Grace</button>
																        <button 
																        id="grace_download${status.count}" type="button" style = "background-color: #c5e1a5 ; display : none"  
																        class ="result_grace_download" 
																        onclick = "location.href='/exam/admin/downloadGraceAppliedDetails?program=${studentMarks.program}&sapid=${studentMarks.sapid}&totalGrace=${studentMarks.gracemarks}'"><i class="fa-solid fa-download" aria-hidden="true"></i></button>
													 				</div>
													 				</td>
													 				
																</tr>   
															</c:forEach>
														</tbody>
													</table>
												</div>
												<br>
										</c:when>
										</c:choose>
              						</div>
              				   </div>
    				       </div>
			           </div>
		           </div>
        <jsp:include page="adminCommon/footer.jsp"/>
        <script type="text/javascript">
			 $('.result_grace').click(function(){

			        	var conf = confirm('Are you sure you want to apply grace? This is not reversible');
			        	if(conf == true){
			        		//console.log('count of row is  ')
			        		var self = $(this);
			        		var count = self.parents('tr').children('.count').html();
			        		console.log('count of row is :: '+count)
			        		$('#img1'+count).show();
			        		$('#grace'+count).css('display', 'none');
		                 $.ajax({
		                    type: "POST",
		                    url: '/exam/admin/applyGraceforValidityEnd',
		                    data: {
		                    	'program' : $(this).attr('data-program'),
		                    	'totalGracemarks': $(this).attr('data-totalGracemarks'),
		                    	'sapid' : $(this).attr('data-sapid'),
		                    	'studentType' : $(this).attr('data-studentType'),
		                    	'resultProcessedYear' : $(this).attr('data-resultProcessedYear'),
		                    	'resultProcessedMonth' : $(this).attr('data-resultProcessedMonth'),
		                    }, 
		                    success:function(response){
		                    	if(response.Status == "Success"){
		                    		//var count = self.parents('tr').children('.count').html();
		                    		console.log("The row count is "+count);
		                			
		                    			$('.js_result').html('<div class="alert alert-success alert-dismissible"> <a href="#" class="close" data-dismiss="alert" aria-label="close">&times;</a> <strong>Success!</strong> grace applied. </div>');
		                    			 self.parents('tr').children('.score').html('0');
		                    			// $('#grace'+count).css('background-color', "#c5e1a5");
		                    			 $('#grace'+count).css('display', 'none');

		                    			 $('#grace_download'+count).css('display', 'block');
		                    			 $('#img1'+count).hide();
		                    	}else{
		                    		$('.js_result').html('<div class="alert alert-danger alert-dismissible"> <a href="#" class="close" data-dismiss="alert" aria-label="close">&times;</a> <strong>Failed!</strong> grace not applied. '+response.Status+' </div>');
		                    		$('#img1'+count).hide();
		                    		$('#grace'+count).css('display', 'block');
		                    	}
		                   },
		                   error:function(){
		                	   $('.js_result').html('<div class="alert alert-danger alert-dismissible"> <a href="#" class="close" data-dismiss="alert" aria-label="close">&times;</a> <strong>Failed!</strong> Server Error Found. </div>');
		                	   $('#img1'+count).hide();
		                	   $('#grace'+count).css('display', 'block');
		                   }
		                });
		        	}else{
		        		$(this).parent().children('.score').html();
		        		console.log($(this).parent().children('.score').html());
		        		this.checked = false;
		        		$('#img1'+count).hide();
		        		$('#grace'+count).css('display', 'block');
		        	}
		        }); 
			 

		  </script>
<script>
/////////////////////////////////////////////////////
/// data loading based on selection
/* 
 $('#consumerType').on('change', function(){


let id = $(this).attr('data-id');


let options = "<option>Loading... </option>";
$('#prgmStructApplicable').html(options);
$('#program').html(options);



var data = {
	id:this.value
}
console.log(this.value)

console.log("===================> data id : " + id);
$.ajax({
type : "POST",
contentType : "application/json",
url : "getValueByConsumerType",   
data : JSON.stringify(data),
success : function(data) {
	console.log("SUCCESS Program Structure: ", data.programStructureData);
	console.log("SUCCESS Program: ", data.programData);
	
	var programData = data.programData;
	var programStructureData = data.programStructureData;
	
	
	options = "";
	
	
	//Data Insert For Program List
	//Start
	for(let i=0;i < programData.length;i++){
		
		options = options + "<option value='" + programData[i].name + "'> " + programData[i].name + " </option>";
	}
	
	
	console.log("==========> options\n" + options);
	$('#program').html(
			" <option disabled selected value=''> Select Program Name</option> " + options
	);
	//End
	options = ""; 
	
	//Data Insert For Program Structure List
	//Start
	for(let i=0;i < programStructureData.length;i++){
		
		options = options + "<option value='" + programStructureData[i].name + "'> " + programStructureData[i].name + " </option>";
	}
	
	
	console.log("==========> options\n" + options);
	$('#prgmStructApplicable').html(
			" <option disabled selected value=''> Select Program Structure </option> " + options
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


$('#prgmStructApplicable').on('change', function(){


let id = $(this).attr('data-id');


let options = "<option>Loading... </option>";
$('#program').html(options);



var data = {
	programStructureId:this.value,
	consumerTypeId:$('#consumerType').val()
}
console.log(this.value)

console.log("===================> data id : " + $('#consumerType').val());
$.ajax({
type : "POST",
contentType : "application/json",
url : "getValueByProgramStructure",   
data : JSON.stringify(data),
success : function(data) {
	
	console.log("SUCCESS: ", data.programData);
	var programData = data.programData;
	
	
	options = "";
	
	
	//Data Insert For Program List
	//Start
	for(let i=0;i < programData.length;i++){
	
		options = options + "<option value='" + programData[i].name + "'> " + programData[i].name + " </option>";
	}
	
	
	console.log("==========> options\n" + options);
	$('#program').html(
			" <option disabled selected value=''> Select Program Structure </option> " + options
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

 */
/////////////////////////////////////////////////////////////
</script>
    
  <script>
		 var consumerTypeId = '${ studentMarks.consumerType }';
		 var programStructureId = '${ studentMarks.programStructApplicable }';
		 var programId = '${ studentMarks.program }';	
  </script>
	
	<%@ include file="../../views/common/consumerProgramStructure.jsp" %>
 
    
    </body>
</html>  