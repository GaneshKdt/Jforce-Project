<!DOCTYPE html>


<%@page import="com.nmims.beans.SearchTimeBoundContent"%>
<%@page import="java.util.List"%>
<%@page import="com.nmims.beans.PersonAcads"%>
<%@page import="com.nmims.beans.PageAcads"%>

<html class="no-js"> <!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<jsp:include page="jscss.jsp">
<jsp:param value="Search TimeBound Content" name="title" />
</jsp:include>
<style>
div.dataTables_paginate {text-align: center}

#dataTable tr td {
    height: 10px;
}

</style>

<body class="inside">

<%@ include file="header.jsp"%>
	
    <section class="content-container login">
        <div class="container-fluid customTheme">
        <div class="row"><legend>Search TimeBound Content</legend></div>
        <div class="panel-body">
        <%@ include file="messages.jsp"%>
		
		<form:form  action="searchTimeBoundContent" method="post" modelAttribute="contentList"  id="formData">
			<fieldset>
			
			<div class="col-md-6 column">
					<div class="form-group">
						<form:select id="year" path="year" type="text"	placeholder="Year" class="form-control " required="true" 
							itemValue="${searchBean.year}">
							<form:option value="">Select Academic Year</form:option>
							<form:options items="${yearList}" />
						</form:select>
					</div>
				
					<div class="form-group">
						<form:select id="month" path="month" type="text" placeholder="Month" class="form-control " required="true" itemValue="${searchBean.month}">
							<form:option value="">Select Academic Month</form:option>
							<form:options items="${monthList}" />
						</form:select>
					</div>
				
					  <div class="form-group" style="overflow:visible;">
								<form:select id="programSemSubjectId" path="programSemSubjectId" class="combobox form-control filterBy" itemValue="${searchBean.programSemSubjectId}"> 
									<form:option value="">Select Subject Code</form:option>
										<c:forEach items="${subjectcodes}" var="element">
											<form:option value="${element.programSemSubjectId}">${element.subjectcode} ( ${element.subjectName} )</form:option>
										</c:forEach>
								</form:select>
					</div>
					
					<div class="form-group">
			            <select id="batchId" name="batchId"  class="selectbatch form-control">
			            	<option disabled selected value="">Select Batch</option>
			            </select>
			      	</div>
			      	
			      	<div class="form-group">
			            <select id="facultyId" name="facultyId"  class="selectfaculty form-control">
			            	<option disabled selected value="">Select Faculty Id</option>
			            </select>
			      	</div>
			      	
			      	<div class="form-group">
						<form:input id="date" path="date" type="date" placeholder="Session Date" class="form-control" value="${searchBean.date}" />
					</div>
			
					
				<div class="form-group" >
						<label class="control-label" for="submit"></label>
						<button id="submit" name="submit" class="btn btn-large btn-primary" onclick="validate()" formaction="searchTimeBoundContent">Search</button>
						<button id="cancel" name="cancel" class="btn btn-danger"  formnovalidate="formnovalidate">Cancel</button>
					</div>
				
			</div>
			
			
			</fieldset>
		</form:form>
		</div>
		<c:if  test="${totalRows > 0}">
				<legend>&nbsp;TimeBound Content Report &nbsp;<font size="2px">(${totalRows} records Found) <a href="/acads/admin/downloadTimeBoundContentReport">Download to Excel</a></font></legend>
				<div class="panel-body">
				
									         <div class="column">
												<div class="table-responsive">
													<table class="table table-striped" style="font-size:8px" id="dataTable">
													<thead>
															<tr>
																	<th>Sr. No.</th>
																	<th>Year</th>
																    <th>Month</th>
																	<th>Session Date</th>
																	<th>Session Start Time</th>
																	<th>Day</th>
																	<th>Subject</th>
																	<th>Subject Code</th>
																	<th>Session Name</th>
																	<th>Session ID</th>
																	<th>Faculty ID</th>
																	<th>Track</th>
																	<th>ModuleId</th>
																	<th>Content Name</th>
																	<th>Content Created Date</th>
																	<th>Content LastModified Date</th>
																	<th>No. of Delay Days</th>
																
														
															</tr>
													</thead>
							
													<tbody>
														<c:forEach var="bean" items="${contentData }" varStatus="status">
														<tr>
															<td><c:out value="${status.count}" /></td>
															<td><c:out value="${bean.year }" /></td>
															<td><c:out value="${bean.month}" /></td>
															<td><c:out value="${bean.date}" /></td>
															<td><c:out value="${bean.startTime}" /></td>
															<td><c:out value="${bean.day}" /></td>
															<td><c:out value="${bean.subject}" /></td>
															<td><c:out value="${bean.subjectcode}" /></td>
															<td><c:out value="${bean.sessionName}" /></td>
															<td><c:out value="${bean.sessionId}" /></td>
															<td><c:out value="${bean.facultyId}" /></td>
															<td><c:out value="${bean.track }" /></td>
															<td><c:out value="${bean.moduleid }" /></td>
															<td><c:out value="${bean.contentName }" /></td>
															<td><c:out value="${bean.createdDate }" /></td>
															<td><c:out value="${bean.lastModifiedDate }" /></td>
															
															<c:choose>
															<c:when test="${bean.delayDays > 0}">
																<td>+<c:out value="${bean.delayDays }" /></td>
															</c:when>
															<c:otherwise>
																<td><c:out value="${bean.delayDays }" /></td>
															</c:otherwise>
															</c:choose>
														</tr>
														</c:forEach>
													</tbody>
											</table>
										</div> 
									</div>
						
				</div>
			</c:if>

	
	
	
	
	
	
	
	</section>
	

	<jsp:include page="footer.jsp" />
	  
	
   

    <link href="https://nightly.datatables.net/css/jquery.dataTables.css" rel="stylesheet" type="text/css" />
    <script src="https://nightly.datatables.net/js/jquery.dataTables.js"></script> 
    <script type="text/javascript">
	    $(document).ready (function(){
	    	 $('#dataTable').DataTable(
				{
					  "language": {
				            "search": "_INPUT_",            // Removes the 'Search' field label
				            "searchPlaceholder": "Search"   // Placeholder for the search box
				        },
				        "search": {
				            "addClass": 'form-control input-lg col-xs-12'
				        },
				        "dom": '<"pull-left"f><"pull-right"l>tip',
				        "aoColumns": [
					        null,
				            null,
				            null,
				            { "orderSequence": [ "desc", "asc", "asc" ] },
				            { "orderSequence": [ "desc", "asc", "asc" ] },
				            { "orderSequence": [ "desc", "asc", "asc" ] },
				            null,
				            null,
				            null,
				            { "orderSequence": [ "desc", "asc", "asc" ] },
				            null,
				            null,
				            null,
				            { "orderSequence": [ "desc", "asc", "asc" ] },
				            { "orderSequence": [ "desc", "asc", "asc" ] },
				            { "orderSequence": [ "desc", "asc", "asc" ] },
				            { "orderSequence": [ "desc", "asc", "asc" ]  }
				            
				        ],
				        "createdRow": function( row, data, dataIndex ) {
				             if ( data[16].includes("+")) {        
				            	 $('td', row).css('background-color', '#FFFFE0');
				     
				       	}
				        }
				}

	    	   );
	
	    });

	    
    </script>
   
    <script>
     $(document).ready(function(){

        //Filter Batch Name According to PssIds
  	   $('.filterBy').on('change', function () {
  		
  		 let options = "<option>Loading... </option>";
  		
  
  		 var data ={
  		    year : $('#year').val(),
  		    month : $('#month').val(), 
  		  programSemSubjectId :  $('#programSemSubjectId').val() 
  		 }

  		 	$.ajax({
		   		type : "POST",
		   		contentType : "application/json; charset=utf-8",
		   		url : "/acads/admin/viewBatchNameBypssId", 
		  		data : JSON.stringify(data),
		  		async: false,
		  		cache: false,
		  		success :  function(data) {
				    

				    if(data.length > 0){
				    options = "";
				    let allOption = "";
				    
				    for(let i=0;i < data.length;i++) {
				    	allOption = allOption + ""+ data[i].batchId +",";
			      			options = options + "<option value='" + data[i].batchId + "'> " + data[i].batchName + " </option>";
				    }
				    allOption = allOption.substring(0,allOption.length-1);
				    options = "<option selected value='"+ allOption +"'>All</option>" + options;

				    }else{
				    	 options = "<option selected value='No'>No batch Found</option>" ;
					 }
					 $('.selectbatch').html(options);
				   },
		 		error : function(e) {
			 		
		 			alert("Please Refresh The Page.")
				    
				   
		   		}
		  }).responseText;
 	
  		 getFacultyFilterBypssId();
  	  	 
  	      });

	      ////////////////////////
	//Filter Faculty By PssId
     function getFacultyFilterBypssId()
     {
    	 let options = "<option>Loading... </option>";
    	
    	  
  		 var data ={
  		    year : $('#year').val(),
  		    month : $('#month').val(), 
  		  programSemSubjectId :  $('#programSemSubjectId').val() 
  		 }
  		 console.log("data ",data);
  		 	$.ajax({
		   		type : "POST",
		   		contentType : "application/json; charset=utf-8",
		   		url : "/acads/admin/viewFacultyIdBypssId", 
		  		data : JSON.stringify(data),
		  		async: false,
		  		cache: false,
		  		success :  function(data) {
				   

				    if(data.length > 0){
				    options = "";
				    let allOption = "";
				    
				    for(let i=0;i < data.length;i++) {
				    	allOption = allOption + "'"+ data[i] +"',";
			      			options = options + "<option value='" + data[i] + "'> " + data[i] + " </option>";
				    }
				    allOption = allOption.substring(0,allOption.length-1);
				    options = "<option selected value=\"'"+ allOption +"'\"">All</option>" + options;

				    }else{
				    	 options = "<option selected value='No'>No Faculty Found</option>" ;
					 }
					 $('.selectfaculty').html(options);
				   },
		 		error : function(e) {
			 		
		 			alert("Please Refresh The Page.")
				    
			
		   		}
		  }).responseText;
 	
  		 
      }
     });

    
     //To refresh default value
     $("#cancel").click(function () {
  			$("#formData")[0].reset();
  			return false; // prevent submitting
		});
    </script>

</body>
</html>