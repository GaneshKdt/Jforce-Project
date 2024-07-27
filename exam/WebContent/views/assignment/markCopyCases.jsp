<!DOCTYPE html>

<html class="no-js">

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix = "fmt" uri = "http://java.sun.com/jsp/jstl/fmt" %>

<jsp:include page="../jscss.jsp">
<jsp:param value="Mark & UnMark Copy Cases" name="title" />
</jsp:include>
<head>
	<link rel="stylesheet" href="https://cdn.datatables.net/1.10.19/css/jquery.dataTables.min.css">
	<style>
		.dataTables_filter > label > input{
			float:right !important;
		}
		.toggleListWell{
		cursor: pointer !important;
			margin-bottom:0px !important;
		}
		.toggleWell{
			background-color:white !important;
		}
		input[type="radio"]{
			width:auto !important;
			height:auto !important;
			
		}
		.optionsWell{
			padding:0px 10px;
		}
	</style>
</head>

<body class="inside">

<%@ include file="../header.jsp"%>
	
    <section class="content-container login">
        <div class="container-fluid customTheme">
        <div class="row"><legend>Mark & UnMark Copy Cases</legend></div>
        <%@ include file="../messages.jsp"%>
		<div class="panel-body clearfix">
			<%-- <form:form id='CC_Mark_Form' action="markCopyCases" method="post" modelAttribute="searchBean"> --%>
			<form:form id='CC_Mark_Form' method="post" modelAttribute="searchBean">
				<fieldset>
					<div class="panel-body">
					<div id="errorMsg" style="display:none"></div>
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
									<form:option value="Apr">Apr</form:option>
									<form:option value="Jun">Jun</form:option>
									<form:option value="Sep">Sep</form:option>
									<form:option value="Dec">Dec</form:option>
								</form:select>
							</div>
							
							<%-- <div class="form-group">
									<form:select id="subject" path="subject" type="text"	placeholder="Subject" class="form-control" itemValue="${searchBean.subject}">
										<form:option value="">Select Subject</form:option>
										<form:options items="${subjectList}" />
									</form:select>
							</div> --%>
							
							<div class="form-group" style="overflow:visible;">
								<form:select id="subject" path="subject"  class="combobox form-control"   itemValue="${searchBean.subject}">
									<form:option value="" selected="selected">Type OR Select Subject</form:option>
									<form:options items="${subjectList}" />
								</form:select>
							</div>
							
						</div>		
							
						<div class="col-md-6 column">
							<div class="form-group">
								<h2>&nbsp;Search Marked Sapid's</font></h2>
									<textarea name="sapIdList" cols="50" rows="7" placeholder="Enter different Student Ids in new lines">${searchBean.sapIdList}</textarea>
							</div>
			
							<div class="form-group">
								<div class="controls">
									<button id="submit" name="submit" class="btn btn-primary btn-sm" formaction="markCopyCases" onclick="return validate();">Mark As Copy</button>
									<button id="submit" name="submit" class="btn btn-sm btn-primary" formaction="searchCopyCases">Search Copy Cases</button>
									<button id="submit" name="submit" class="btn btn-sm btn-primary" formaction="downloadCopyCases">Download Copy Cases</button>
									<button id="cancel" name="cancel" class="btn btn-danger btn-sm" formaction="home" formnovalidate="formnovalidate">Cancel</button>
								</div>
							</div>
						</div>
						<c:if test="${rowCount > 0}">
							<div class="col-md-6 column">
								<h2>&nbsp;Unmark Copy Cases with Common Reason</h2>
									<form:select id="common_reason" path="reason" class="combobox form-control" itemValue="${searchBean.reason}" >
										<form:option value="" selected="selected">Type OR Select Common Reason for all Sapid's</form:option>
											<form:option value="Allocation">Proceed to Allocate</form:option>
											<form:option value="Excellent">Excellent</form:option>
											<form:option value="Very Good">Very Good</form:option>
											<form:option value="Good">Good</form:option>
											<form:option value="Average">Average</form:option>
											<form:option value="Below Average">Below Average</form:option>
											<form:option value="Copy Case-Internet/Course Book">Copy Case (Internet/Course Book)</form:option>
											<form:option value="Copy Case-Other Student">Copy Case (Other student/s)</form:option>
											<form:option value="Wrong Answer"> Wrong Answer/s</form:option>
											<form:option value="Other subject Assignment">Other subject Assignment</form:option>
											<form:option value="Scanned/Handwritten assignment">Scanned/Handwritten assignment</form:option>
											<form:option value="Only Questions written">Only Questions written/Question Paper Uploaded</form:option>
											<form:option value="Blank Assignment">Blank Assignment</form:option>
									</form:select>
								<div>
									<button id="Unmark_CommonReason" name="submit" class="btn btn-sm btn-primary">Unmark</button>
								</div>
							</div>
						</c:if>
					</div>	
				</fieldset>
			</form:form>
			
			<c:if test="${rowCount > 0}">
				<h2>&nbsp;Copy Cases <font size="2px"> (${rowCount} Records Found)&nbsp; </font></h2>
				<div class="panel-body table-responsive">
					<table id="CC_table" class="table table-striped table-hover" class="table table-striped" style="width: 100% !important;">
						<thead>
							<tr> 
								<!-- <th>Sr. No.</th> -->
								<th>Exam Year</th>
								<th>Exam Month</th>
								<th>Subject</th>
								<th><input id="select_All_Checbox" type="checkbox" style="width: 15px; height: 30px;"/>All</th>
								<th>Student ID</th>
								<th>Reason</th>
							</tr>
						</thead>
						<tbody>
							<c:forEach var="bean" items="${assignmentFilesList}">
						        <tr value="${bean.month}~${bean.year}~${bean.subject}~${bean.sapId}">
						            <%-- <td><c:out value="${status.count}"/></td> --%>
									<td><c:out value="${bean.year}"/></td>
									<td><c:out value="${bean.month}"/></td>
									<td nowrap="nowrap"><c:out value="${bean.subject}"/></td>
									<td><input class="checkBox" type="checkbox" name="check" path="id" value="${bean.month}~${bean.year}~${bean.subject}~${bean.sapId}" style="width: 15px; height: 30px;"/></td>
									<td><c:out value="${bean.sapId}"/></td>
									<td>
										<select id="${bean.sapId}" path="reason" class="combobox form-control" style="overflow:visible;" >
											<option value="" selected="selected">Type OR Select Reason</option>
												<option value="Allocation">Proceed to Allocate</option>
												<option value="Excellent">Excellent</option>
												<option value="Very Good">Very Good</option>
												<option value="Good">Good</option>
												<option value="Average">Average</option>
												<option value="Below Average">Below Average</option>
												<option value="Copy Case-Internet/Course Book">Copy Case (Internet/Course Book)</option>
												<option value="Copy Case-Other Student">Copy Case (Other student/s)</option>
												<option value="Wrong Answer"> Wrong Answer/s</option>
												<option value="Other subject File">Other subject File</option>
												<option value="Scanned/Handwritten Project">Scanned/Handwritten Project</option>
												<option value="Only Questions written">Only Questions written/Question Paper Uploaded</option>
												<option value="Blank Project">Blank Project</option>
												<option value="Corrupt file uploaded">Corrupt file uploaded</option>
										</select>
									</td>
						        </tr>   
						    </c:forEach>
						</tbody>
					</table>
				</div>
				<div>
					<button id="Unmark" name="submit" class="btn btn-sm btn-primary" >Unmark</button>
				</div>
				<br>
			</c:if>
		</div>
	</section>

	  <jsp:include page="../footer.jsp" />

<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />assets/js/datatable/jquery.dataTables.min.js"></script>

<script type="text/javascript">

		function scrollToTop() {
			  window.scrollTo(0, 0);
			}
		
        $(function () {
            let table = $("#CC_table").DataTable();

			// Handle click on "Select all" control
			$("#select_All_Checbox").on('click', function(){
			   // Get all rows with search applied
			   var rows = table.rows({ 'search': 'applied' }).nodes();
			   // Check/uncheck checkboxes for all rows in the table
			   $(".checkBox", rows).prop('checked', this.checked);
			});

        });


		// Handle click on checkbox to set state of "Select all" control
	   $('#CC_table tbody').on('change', 'input[type="checkbox"]', function(){
	      // If checkbox is not checked
	      if(!this.checked){
	         var el = $('#select_All_Checbox').get(0);
	         // If "Select all" control is checked and has 'indeterminate' property
	         if(el && el.checked && ('indeterminate' in el)){
	            // Set visual state of "Select all" control
	            // as 'indeterminate'
	            el.indeterminate = true;
	         }
	      }
	   });


	   var sapId=[];
       var month=[];
       var year=[];
       var subject=[];
       
       const button='<button type="button" class="close" data-dismiss="alert"  aria-hidden="true">  &times; </button>';
       
           $('#Unmark_CommonReason').click(function() {
               
        	   	var unMarkCCList = new Array();
        	   	var reason=[];
	           	$("input:checkbox:checked").each(function(i){
	        	   
	            	var str = $(this).val();
	            	var id= str.split('~');
					
	            	if( id[0]!= 'on'){
		            	
	            		month[i]=id[0];
	            		year[i]=id[1];
	   		        	subject[i]=id[2];
	   		        	sapId[i]=id[3];
	   		        	reason[i]=$('#common_reason').val();
	   		        	
	   		        	unMarkCCList.push({
	   		        					"month":month[i],
	         							"year": year[i],
		   		         				"subject":subject[i] ,
		   		         				"sapId": sapId[i],
		   		         				"reason":reason[i]
		   		     	});   
		           }
	           });
		           
	           	if(unMarkCCList == null || unMarkCCList == "" || unMarkCCList.lenght == 0){
					alert("Please select Sapid's");	
           			return; 
	           	}else if (reason == null || reason == "" || reason == ","){
	           		alert("Please select any common reason for Sapid's");	
           			return; 
	           	}else{
					$.ajax({
						type:"POST",
						dataType:"json",
						contentType:"application/json",
						url:"/exam/m/admin/unMarkCopyCases",
						data:JSON.stringify({unMarkCCList:unMarkCCList}),
						success:function(response){
							
							if(response.error!=null){
					    		document.getElementById("errorMsg").style.display="block";
								const div='<div class="alert alert-danger alert-dismissible" id="errorMsgDescription"></div>';
								document.getElementById("errorMsg").innerHTML=div;
								document.getElementById("errorMsgDescription").innerHTML=response.error+button;
								scrollToTop();
							}
	
							if(response.success=='success'){
							   	alert(response.successMessage);
							   	window.location="/exam/admin/searchCopyCases?year=" + response.year + "&month=" + response.month + "&subject="+ response.subject;
							}
							
						},
						error:function(err){
							document.getElementById("errorMsg").style.display="none";
							document.getElementById("errorMsg").innerHTML=div;
							document.getElementById("errorMsgDescription").innerHTML="Error in UnMark CC process : "+err+button;
							scrollToTop();
						}
					});
           		}
           });


           $('#Unmark').click(function() {
        	   var unMarkCCList = new Array();
        	   var reason=[];
	           $("input:checkbox:checked").each(function(i){
	        	   var str = $(this).val();
	            	var id= str.split('~');
	            	if( id[0]!= 'on'){
	            		month[i]=id[0];
	            		year[i]=id[1];
	   		        	subject[i]=id[2];
	   		        	sapId[i]=id[3];
	   		        	reason[i]=$("#"+id[3]).val();
	   		        	
	   		        	unMarkCCList.push({
	   		        						"month":month[i],
		   		         					"year": year[i], 
		   		         					"subject":subject[i], 
		   		         					"sapId": sapId[i],
		   		         					"reason":reason[i]
	         							});   
		           }
	           });
	           
	           if(unMarkCCList == null || unMarkCCList == "" || unMarkCCList.lenght == 0){
					alert("Please select Sapid's");	
          			return; 
	           	}else if (reason == null || reason == "" || reason == ","){
	           		alert("Please select reason for Sapid's");	
          			return; 
	           	}else{
					$.ajax({
							type:"POST",
							dataType:"json",
							contentType:"application/json",
							url:"/exam/m/admin/unMarkCopyCases",
							data:JSON.stringify({unMarkCCList:unMarkCCList}),
							success:function(response){
								
								if(response.error!=null){
						    		document.getElementById("errorMsg").style.display="block";
									const div='<div class="alert alert-danger alert-dismissible" id="errorMsgDescription"></div>';
									document.getElementById("errorMsg").innerHTML=div;
									document.getElementById("errorMsgDescription").innerHTML=response.error+button;
									scrollToTop();
								}
	
								if(response.success=='success'){
								   	alert(response.successMessage);
								   	window.location="/exam/admin/searchCopyCases?year=" + response.year + "&month=" + response.month + "&subject="+ response.subject;
								}
								
							},
							error:function(err){
								document.getElementById("errorMsg").style.display="none";
								document.getElementById("errorMsg").innerHTML=div;
								document.getElementById("errorMsgDescription").innerHTML="Error in UnMark CC process : "+err+button;
								scrollToTop();
							}
					});
           		}
           });
			
</script>

<script type="text/javascript">

$(document).ready(function(){

	  $("#common_reason").change(function(){
		   var data = $("#common_reason").val();
		   if(data!=""){
				document.getElementById("Unmark").disabled=true;
			}else{
				document.getElementById("Unmark").disabled=false;
			}
		   
	  });

	});
	
</script>
</body>
</html>