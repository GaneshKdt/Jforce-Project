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
	<jsp:param value="Find Blocked Center Students" name="title" />
</jsp:include>

	<!-- Style For Loader START-->
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
		
	</style>
	<!-- Style For Loader START-->
	
	
<body class="inside">
<%@ include file="../header.jsp"%>
	<section class="content-container login">
	 	<div class="container-fluid customTheme">
        <div class="row"><legend>Find Blocked Center Students</legend></div>
        <%@ include file="../messages.jsp"%>
        
	        <form:form  action="#" method="post" modelAttribute="searchBean">
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
							</div>
							
							<div class="row">
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
								<div class="col-md-6 column">
									<div class="form-group">
										<label for="sapid">Sapid(Optional)</label>
										<form:input type="text" class="form-control" id="sapid" placeholder="Enter Sapid" path="sapid"/>
									</div>
								</div>
							</div>
							
							<br> 
							
							<div class="row">
								<div class="col-md-6 column">
									<div class="form-group mb-5">
										<button id="submit" type="submit" class="btn btn-large btn-primary" formaction="searchBlockStudentsCenter">
											Find Students
										</button> 
										<button id="cancel" name="cancel" class="btn btn-danger" formaction="/studentportal/home" formnovalidate="formnovalidate">
											Cancel
										</button>
									</div>
								</div>
							</div>
						<br>
					</div>		
			</form:form>
			<%if("true".equalsIgnoreCase((String)request.getAttribute("showStudents"))){ %>
				<h4>&nbsp;Blocked Students Centers : (<span style="color:red">${centerNotAllowStudentsList.size()}</span> Records Found)</h4>
					<div class="row"><legend>Center Blocked Students List</legend></div>
					<div class="clearfix"></div>
						<div class="panel-body">
							<div class="column">
								<div class="table-responsive">
									<table class="table table-striped table-hover dataTables" style="font-size:12px; width: 100%">
										<thead>
										<tr>
											<th>Sr.No</th>
											<th>Sapid</th>
											<th>Year</th>
											<th>Month</th>
											<th>Blocked Center</th>
											<th>Action</th>
										</tr>
										</thead>
										
										<tbody>
											<form:form  action="#" method="post">
											<c:forEach var="studentList" items="${centerNotAllowStudentsList}" varStatus="status">
												<tr>
													<td>${status.count}</td>
													<td>${studentList.sapid}</td>
													<td>${studentList.year}</td>
													<td>${studentList.month}</td>
													<td>${studentList.centerName}</td>
													<td>
														<a href="/exam/admin/unblockStudentsCenter?year=${studentList.year}&month=${studentList.month}&sapid=${studentList.sapid}&centerId=${studentList.centerId}&centerName=${studentList.centerName}"
														class="btn btn-small btn-primary" onclick="return confirm('Are you sure. You want to unblock ${studentList.centerName} center for ${studentList.sapid}')">Unblock</a>
													</td>
												</tr>
											</c:forEach>
											</form:form>
										</tbody>
									</table> 
				   				</div>
				   			</div>
				   		</div>
						<%}%>
				</div>	
		</section>	
	
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
	  
	  
	  
	  
	  
	<!-- JavaScript START -->
	
	<!-- Calling Datatable js jQuery -->
	<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/js/vendor/dataTables.bootstrap.js"></script>
	<script src="https://cdn.datatables.net/buttons/1.2.4/js/dataTables.buttons.min.js"></script>

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
		   
	</script>
	
	<!-- JavaScript END -->
</body>
</html>
 