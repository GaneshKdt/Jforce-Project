<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!--> 


<html class="no-js"> <!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
        <%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<jsp:include page="jscss.jsp">
<jsp:param value="Demo Exam" name="title" />
</jsp:include>
<link
	href="https://cdnjs.cloudflare.com/ajax/libs/select2/4.0.6-rc.0/css/select2.min.css"
	rel="stylesheet" />

<body class="inside">

<%@ include file="header.jsp"%>
	
    <section class="content-container login">
        <div class="container-fluid customTheme">
        <div class="row"><legend>Demo exam</legend></div>
        <div class="container-fluid" style="background-color:white !important;padding:20px">
        <%@ include file="adminCommon/messages.jsp"%>
        <c:if test="${ errorFlag == 'true' }">
        <div>
        	<div class="alert alert-danger">Error while inserting somerecords</div>
        	<table class="table alert-danger">
        		<thead>
        			<th>subject</th>
        			<th>link</th>
        		</thead>
        		<tbody>
        			<c:forEach var="demoExamBean" items="${demoExamBeanErrorList}">
        			<tr>
        				<td>${ demoExamBean.subject }</td>
        				<td>${ demoExamBean.link }</td>
        			</tr>
        			</c:forEach>
        		</tbody>
        	</table>
        </div>
        </c:if>
        <form action="/exam/admin/demoExamSubjectLinkMapping" method="POST" enctype="multipart/form-data">
        	<div class="form-group">
  				<label for="subject_link_mapping">Subject link mapping:</label>
  				<input type="file" style="max-width:250px;" name="fileData" class="form-control"/>
        	</div>
        	<button class="btn btn-primary btn-sm">
        		Submit
        	</button><br/><br/>
        </form>
        <button class="btn btn-danger create_new_subject_btn btn-sm">Create new subject demo link</button><br/><br/>
		<table class="table table1">
			<thead>
				<tr>
					<th></th>
					<th>Subject</th>
					<th>Link</th>
					<th>Action</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="demoExam" items="${demoExamList}">
						<tr>
							<td></td>
							<td>${demoExam.subject }</td>
							<td>${demoExam.link }</td>
							<td><a class="update_btn" data-demoExam_id="${ demoExam.id }" data-demoExam_link="${ demoExam.link }" data-demoExam_subject="${ demoExam.subject }" href="javascript:void(0)">Update</a> | <a data-demoExamId="${ demoExam.id }" class="delete_btn" href="javascript:void(0)">Delete</a></td>
						</tr>
					
				</c:forEach>
			</tbody>
		</table>
		</div>
		</div>
		
		<div id="updateModelForm" class="modal fade" role="dialog">
		  <div class="modal-dialog">
		
		    <!-- Modal content-->
		    <div class="modal-content">
		      <div class="modal-header">
		        <button type="button" class="close" data-dismiss="modal">&times;</button>
		        <h4 class="modal-title">Update DemoExam Record</h4>
		      </div>
		      <div class="modal-body">
		        	<input type="hidden" id="id_txt" />
				  <div class="form-group">
				    <label for="subject_txt">subject:</label>
				    <input type="text" class="form-control" id="subject_txt" disabled>
				  </div><br/>
				  <div class="form-group" style="margin-top:8px">
				    <label for="link_txt">link:</label>
				    <input type="text" class="form-control" id="link_txt">
				  </div><br/><br/>
				  <button class="btn btn-primary btn-sm update_demoexam_btn">Submit</button>
				  <button class="btn btn-secondary btn-sm cancel_demoexam_btn">Cancel</button><br/><br/>
				 
		      </div>
		    </div>
		
		  </div>
		</div>
		
		<div id="createModelForm" class="modal fade" role="dialog">
		  <div class="modal-dialog">
		
		    <!-- Modal content-->
		    <div class="modal-content">
		      <div class="modal-header">
		        <button type="button" class="close" data-dismiss="modal">&times;</button>
		        <h4 class="modal-title">Create DemoExam Record</h4>
		      </div>
		      <div class="modal-body">
				  <div class="form-group">
				    <label for="insert_subject_txt">subject:</label>
				    <select class="select2form form-control" id="insert_subject_txt" style="width:100%">
				    	<c:forEach var="subject" items="${subjectList}">
				    		<option value="${ subject }">${ subject }</option>
				    	</c:forEach>
				    </select>
				  </div><br/>
				  <div class="form-group" style="margin-top:8px">
				    <label for="insert_link_txt">link:</label>
				    <input type="text" class="form-control" id="insert_link_txt">
				  </div><br/><br/>
				  <button class="btn btn-primary btn-sm create_demoexam_btn">Submit</button>
				  <button class="btn btn-secondary btn-sm cancel_demoexam_btn">Cancel</button><br/><br/>
				 
		      </div>
		    </div>
		
		  </div>
		</div>
		
		<div id="loading_img" class="modal fade" role="dialog">
		  <div class="modal-dialog">
		
		    <!-- Modal content-->
		    <div class="modal-content">
		      <div class="modal-body">
		        <center>
		        	<img src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/gifs/loading-29.gif" style="width:100px;" />
		        	<h4>Loading...</h4>
		        </center>
		      </div>
		    </div>
		
		  </div>
		</div>
		
	
	</section>

	  <jsp:include page="footer.jsp" />
	  <script type="text/javascript"
		src="https://cdnjs.cloudflare.com/ajax/libs/select2/4.0.6-rc.0/js/select2.min.js"></script>
		<script>
			function showLoading(){
				$('#loading_img').modal({
					backdrop: 'static',
					keyboard: false
				});
			}
			
			function hideLoading(){
				$('#loading_img').modal('hide');
			}
			
			
			function showUpdateFormAndFill(){
				
			}
			
			$(document).ready(function(){
				$('.table1').DataTable();
				
				
				
				$('.create_new_subject_btn').click(function(){
					$('#createModelForm').modal('toggle');
				});
			
				
				$(document).on('click','.cancel_demoexam_btn',function(){
					$('#updateModelForm').modal('hide');
					$('#createModelForm').modal('hide');
				});
				
				$(document).on('click','.update_btn',function(){
					$('#updateModelForm').modal('toggle');
					$('#id_txt').val($(this).attr('data-demoExam_id'));
					$('#subject_txt').val($(this).attr('data-demoExam_subject'));
					$('#link_txt').val($(this).attr('data-demoExam_link'));
				});
				
				$(document).on('click','.create_demoexam_btn',function(){
					$('#createModelForm').modal('hide');
					//if(confirm("Do you want update")){
						showLoading();
						$.ajax({
							url:"/exam/admin/createDemoExamRecord",
							contentType: "application/json",
							method:"POST",
							data : JSON.stringify({
								"subject" :$('#insert_subject_txt').val(),
								"link" : $('#insert_link_txt').val()
							}),
							dataType: "json",
							success:function(response){
								hideLoading();
								if(response.status == "success"){
									alert("Successfully record created");
									window.location.href = '/exam/admin/viewDemoExamList';	
								}else{
									alert("Failed to create record");
								}
								
							},
							error:function(error){
								hideLoading();
								alert("Failed to update record,please try again after sometime");
							}
						});
					//}
				});
				
				$(document).on('click','.update_demoexam_btn',function(){
					$('#updateModelForm').modal('hide');
					if(confirm("Do you want update")){
						showLoading();
						$.ajax({
							url:"/exam/admin/updateDemoExamRecord",
							contentType: "application/json",
							method:"POST",
							data : JSON.stringify({
								"id" : $('#id_txt').val(),
								"subject" :$('#subject_txt').val(),
								"link" : $('#link_txt').val()
							}),
							dataType: "json",
							success:function(response){
								hideLoading();
								if(response.status == "success"){
									alert("Successfully record updated");
									window.location.href = '/exam/admin/viewDemoExamList';
								}else{
									alert("Failed to update record");
								}
								
							},
							error:function(error){
								hideLoading();
								alert("Failed to update record,please try again after sometime");
							}
						});
					}
				});
				
				$(document).on('click','.delete_btn',function(){
					if(confirm("Do you want delete")){
						showLoading();
						$.ajax({
							url:"/exam/admin/deleteDemoExamRecord",
							contentType: "application/json",
							method:"POST",
							data : JSON.stringify({
								"id" : $(this).attr('data-demoExamId')
							}),
							dataType: "json",
							success:function(response){
								hideLoading();
								if(response.status == "success"){
									alert("Successfully record deleted");
									window.location.href = '/exam/admin/viewDemoExamList';	
								}else{
									alert("Failed to delete record");
								}
								
							},
							error:function(error){
								hideLoading();
								alert("Failed to delete record,please try again after sometime");
							}
						});
					}
				});
			});
		</script>
</body>
</html>
