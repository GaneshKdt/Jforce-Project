<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!--> 

<%@page import="com.nmims.beans.Person"%>
<%@page import="com.nmims.beans.Page"%>
<%@page import="com.nmims.beans.AssignmentStatusBean"%>
<html class="no-js"> <!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<jsp:include page="../jscss.jsp">
	<jsp:param value="Block Centers For Students" name="title" />
</jsp:include>

	<style>
	
	 	#loader {
	            border: 16px solid #d2232a;
	            border-radius: 50%;
	            border-top: 16px solid #f3f3f3;
	            width: 100px;
	            height: 100px;
	            animation: spin 0.8s linear infinite;
	        }
	          
        @keyframes spin {
            100% {
                transform: rotate(360deg);
            }
        }
          
        .center {
            position: absolute;
            top: 400px;
            bottom: 0;
            left: 0;
            right: 0;
            margin: auto;
        }
	
		.label {
			position: absolute;
            top: 65px;
            bottom: 0;
            left: 20px;
            right: 0;
            margin: auto;
		}
	
		input[type=radio] {
	     width: 15px !important;
		};
		
	</style> 
	
	<!-- Dynamic Properties for Multi-Select Dropdown -->
	<script type="text/javascript">
		var config={
			search:true,
		    height:'15rem',
		    placeholder:'Select Centers Name',
		    txtSelected:'Selected Centers',
		    txtAll:'All Centers',
		    txtRemove: 'Remove',
		    txtSearch:'Search Center Name'
		  };
	</script>

<body class="inside">
	<%@ include file="../header.jsp"%>
	 <div class="container-fluid customTheme">
        <div class="row"><legend>Block Centers For Students</legend></div>    
        <%@ include file="../messages.jsp"%>  
        <div class="panel-body">    
        	<form:form  action="#" method="post" modelAttribute="searchBean">
				<div class="row">
					<div class="col-md-6 column">
						<div class="form-group">
							<label for="consumerTypeId">Consumer Type</label>
							<form:select path="consumerTypeId" class="form-control" id="consumerTypeId" required="required" itemValue="${searchBean.consumerTypeName}">
								<option value="" selected disabled>Select Consumer Type</option>
								<c:forEach var="consumerType" items="${consumerTypeList}">
									<option value="<c:out value="${consumerType.id}"/>"><c:out value="${consumerType.name}"/></option>
								</c:forEach>
							</form:select>
						</div>
					</div>
					
					<div class="col-md-6 column">
						<div class="form-group">
							<label for="programStructureId">Program Structure</label>
							<form:select path="programStructureId" class="form-control" id="programStructureId" required="required" itemValue="${searchBean.programStructureId}">
								<option value="" selected disabled>Select Program Structure</option>
							</form:select>
						</div>
					</div>
					
					<div class="col-md-6 column">
						<div class="form-group">
							<label for="programId">Program</label>
							<form:select path="programId" class="form-control" id="programId" required="required" itemValue="${searchBean.programId}">
								<option value="" selected disabled>Select Program</option>
							</form:select> 
						</div>
					</div>
					
				</div>
				
				<div class="row">
					<div class="col-md-12 column">
						<div class="form-group mb-5">
							<button id="submit" type="submit" class="btn btn-large btn-primary" formaction="centerNotAllowSearchStudents">
								Search Students
							</button> 
							<button id="cancel" name="cancel" class="btn btn-danger" formaction="/studentportal/home" formnovalidate="formnovalidate">
								Cancel
							</button>
						</div>
					</div>
				</div>
			</form:form>
			
			<h4>OR</h4>
			
			<form:form  action="#" method="post" modelAttribute="searchBean">
				<div class="row">	
					<div class="col-md-6 column">
						<div class="form-group mb-5">
							<button id="submit" type="submit" class="btn btn-large btn-primary" formaction="centerNotAllowSearchUFMStudents">
								Search UFM Students
							</button>
						</div>
					</div>
				</div>
			</form:form>
				
			<h4>OR</h4>
			
			<form:form  action="#" method="post" modelAttribute="searchBean" enctype="multipart/form-data">
				<div class="row">
					<div class="col-md-6 column" id="excelInput">
						<div class="form-group">
							<label for="fileData" id="fileData">Upload list of students to block centers (Optional)</label>
							<input name="file" class="fileData" type="file" />(Format : Sapid)
						</div>		
					</div>
				</div>
				<div class="row">
					<div class="col-md-6 column">
						<button id="submit" type="submit" class="btn btn-large btn-primary" formaction="centerNotAllowSearchExcelUploadStudents">
							Search Students By Excel Upload
						</button>
					</div>
				</div>
			</form:form>
			
		</div>
	</div>
	
	<br>
	
	<%if("true".equalsIgnoreCase((String)request.getAttribute("showStudents"))){ %>
	 <div class="container-fluid">
	 <form:form id="blockCenter" action="#" method="post" modelAttribute="searchBean">
		<div class="panel-body">					
			<div class="row">
				<div class="col-md-6 column">
					<div class="form-group">
						<label for="examYear">Exam Year</label>
						<form:select path="year" class="form-control" id="examYear" required="required" itemValue="${searchBean.year}">
							<option value="" selected>Select Exam Year</option>
							<c:forEach var="year" items="${yearList}">
								<option value="${year}">${year}</option>
							</c:forEach>
						</form:select>
					</div>
				</div>

				<div class="col-md-6 column">
					<div class="form-group">
						<label for="examMonth">Exam Month</label>
						<form:select path="month" class="form-control" id="examMonth" required="required" itemValue="${searchBean.month}">
							<option value="" selected>Select Exam Month</option>
							<option value="Apr">Apr</option>
							<option value="Jun">Jun</option>
							<option value="Sep">Sep</option>
							<option value="Dec">Dec</option>
						</form:select>
					</div>
				</div>
			</div>
			<div class="row">
				<div class="col-md-6 column" id="centerId">
					<div class="form-group">
					<label class="custom-file-label" for="file">Center Name</label>
						<select name="centerIdList" id="centers" class="form-control" required="required" multiple multiselect-search="true" multiselect-max-items="0" multiselect-select-all="true">
						  <c:forEach var="center" items="${examCenterList}">
							 <option value="${center.centerId}">${center.centerName}</option>
						  </c:forEach>
						</select>
					</div>
				</div>
				
				<div class="col-md-6">
					<div class="radio">
					      <input type="radio" name="allowed" value="true" id="allowed" style="padding:0px;height:14px;"><span style="font-size:15px;font-weight:bold;width:0px !important;">Allow Centers</span>
				  	</div>
				  	<div class="radio">
					      <input type="radio" name="allowed" value="false" id="notAllowed" style="padding:0px;height:14px;"><span style="font-size:15px;font-weight:bold;width:0px !important;">Not Allow Centers</span>
				  	</div>		
				</div>
			</div>
			
			<br>
			
			<div class="row">
				<div class="col-md-6 column">
					<button id="submit" type="submit" class="btn btn-large btn-primary" formaction="centerNotAllowBlock"
					onClick="validateAllowStatus()">
						Block Center
					</button>
				</div>
			</div>
		</div>		
	</form:form>
	</div>
	
	<%}%>
	
	<%if("true".equalsIgnoreCase((String)request.getAttribute("showStudents"))){ %>
	
	<section class="content-container login">	
		<div class="container-fluid customTheme">
			<h4>&nbsp;Report For Center Block Students: (<span style="color:red">${centerNotAllowStudentsList.size()}</span> Records Found)
				<font size="2px"> &nbsp; 
					<a href="/exam/admin/downloadBlockStudentsCenterReport" style="color:blue;">Download to Excel</a>
				</font>
			</h4>
			<div class="row"><legend>Students List For Center Block</legend></div>
			<div class="panel-body">
				<div class="clearfix"></div>
				<div class="column">
					<div class="table-responsive">
						<table class="table table-striped table-hover dataTables" style="font-size:12px; width: 100%">
							<thead>
								<tr>
									<th>Sr.No</th>
									<th>Sapid</th>
								</tr>
							</thead>
							
							<tbody>
								<c:forEach var="studentList" items="${centerNotAllowStudentsList}" varStatus="status">
									<tr>
										<td>${status.count}</td>
										<td>${studentList.sapid}</td>
									</tr>
								</c:forEach>
							</tbody>
						</table>
					</div> 
			   	</div>
			 </div>	
	   	</div>	
	</section>	
	<%}%>
	<jsp:include page="../footer.jsp" />
	  
	<!-- Modal For Loader START -->
	<div class="modal fade" id="loaderModal" role="dialog" data-backdrop="static" data-keyboard="false">
   		<div class="modal-dialog modal-md">
   				<div class="modal-body">
   					<div class="container mt-5">
   						<div class="row">
   							<div class="col-10 mt-5">
   								<div id="loader" class="center"></div>
   								<div class="label"><h3 class="center" style="color: black; font-weight: bold;">Loading...</h3></div>
   								
   							</div>
   						</div>
   					</div>	  					
   				</div>
		</div>
   	</div>
	<!-- Modal For Loader END -->
	  
	  
	  
	  
	<!-- Calling Datatable js jQuery -->
	<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/js/vendor/dataTables.bootstrap.js"></script>
	<script src="https://cdn.datatables.net/buttons/1.2.4/js/dataTables.buttons.min.js"></script>
	
	<!-- Calling Dropdown Dependent JS (Select Student Type >> Select Program Structure >> Select Program Name) -->
	<script src="${pageContext.request.contextPath}/assets/js/centerBlock.js"></script>
	
	<!-- Calling Multi-Select Dropdown With Check Box JS library -->
	<script src="${pageContext.request.contextPath}/assets/js/multipleCheckboxDropdown.min.js"></script>
	
	<!-- Custom Script Writing -->
	<script>

		//Start Loading Page
		$(document).ready(function() {
			try
			{
				//To load modal very first when page start loading
				$('#loaderModal').modal('show');
				//Showing datatable
				$('.dataTables').DataTable();
			}
			catch(e)
			{
				console.log("Error in start loading : "+e);
			}
	    });

		//After Page Loaded fully
		document.onreadystatechange = function() {
			try
			{
				if (document.readyState !== "complete") {
		    	   $('#loaderModal').modal('show');
		       	} else {
		    	   $('#loaderModal').modal('hide');
		       	}
			}
	       	catch(e)
	       	{
				console.log("Error in loading assets completely : "+e);
		    }
	   };

	   //Validating the admin are try to allow center or not allow center
	   function validateAllowStatus()
	   {
		   allowed = $('input[name="allowed"]:checked').val();
		   centerCount = $('#centers option:not(:selected)').length;
		   center = document.getElementById('centers');
		   
		   if(center.value) {
				 //remove required attribute if any already 
				 $("#allowed").removeAttr("required");
				 //add new required attribute
				 $('#allowed').attr('required', 'required');
		   }
		   else
		   {
			   $("#allowed").removeAttr("required");
		   }
		   //If admin selected all center and try to allow then prevent them to proceed
		   if(centerCount <= 0 && allowed === "true")
		   {
			   $("#blockCenter").submit(function(e){
			        e.preventDefault();
			    });
			   alert("Centers are not available for blocked. Please deselect at least one and try again!!");
		   }
		   //else let them proceed
		   else
		   {
			   center = document.getElementById('centers');
			   examYear = document.getElementById('examYear');
			   examMonth = document.getElementById('examMonth');
				//Exam Year and Month are Selected or Not
			   if(examYear.value && examMonth.value)
			   {
				   //Center are Selected or not
				   if(center.value ) {

					   if ($("#allowed").prop("checked") || $("#notAllowed").prop("checked")) {
						 //Alert before block the center
							 var sure = confirm('Are you sure. You want to block centers for listed sapids?');
							 
							 if(sure == false)
							 {
								 $("#blockCenter").submit(function(e){
								        e.preventDefault();
								    });
							 }
							 else
							 {
								 $("#blockCenter").unbind('submit');
							 }
						}
				   }
				   //else throw a alert message to select at least one center
				   else
				   {
					   alert("Please select at least one center to proceed!!");
				   }
			   }
		   }
		}
		
	</script>

</body>
</html>
