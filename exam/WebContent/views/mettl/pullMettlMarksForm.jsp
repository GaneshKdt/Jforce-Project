<!DOCTYPE html>
<!--[if lt IE 7]>	<html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>		<html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>		<html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!--> 

<%@page import="com.nmims.beans.MettlPGResponseBean"%>
<%@page import="java.util.List"%>
<%@page import="com.nmims.beans.Person"%>
<%@page import="com.nmims.beans.Page"%>
<%@page import="com.nmims.beans.StudentMarksBean"%>
<%@page import="com.nmims.beans.MBAExamBookingRequest"%>
<%@page import="java.util.ArrayList"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%-- <%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%> --%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<html class="no-js"> <!--<![endif]-->


<jsp:include page="../jscss.jsp">
<jsp:param value="Pull Results from Mettl" name="title" />
</jsp:include>

<body class="inside">

<%@ include file="../header.jsp"%>
		
	<section class="content-container login">
		<div class="container-fluid customTheme">
		<div class="row"><legend>Pull Results from Mettl</legend></div>
		<%@ include file="../messages.jsp"%>
		
		<div class="clearfix"></div>
		<div id="infoMsg">
			<c:if test = "${resultbean.bookingcount gt 0 && resultbean.pullProcessStart}">
			<div class="alert alert-info alert-dismissible">
			<button type="button" class="close" data-dismiss="alert"  aria-hidden="true">  &times;  </button>
			Fetching Mettl Result Data For ${resultbean.bookingcount} Exam Booking records of ${resultbean.examMonth} ${resultbean.examYear} Exam cycle. You Can Navigate Away From Screen.
			</div>
			</c:if>
		</div>
		<div id="successMsg">
			<c:if test = "${resultbean.successcount gt 0 }">
			<div class="alert alert-success alert-dismissible">
			<button type="button" class="close" data-dismiss="alert"  aria-hidden="true">  &times;  </button>
			<c:if test = "${resultbean.pullTaskCompleted}">
			<p>Successfully fetched ${resultbean.successcount} out of ${resultbean.bookingcount} records for ${resultbean.examMonth} ${resultbean.examYear} Exam cycle.</p>
			<c:if test="${resultbean.transferredCount > 0 }"> <p> Records Transferred to Marks : ${resultbean.transferredCount}. </p> </c:if>
			<c:if test="${resultbean.bodAppliedCount > 0 }"> <p> BOD successfully Applied to ${resultbean.bodAppliedCount} Records. </p> </c:if>
			</c:if>
			<c:if test = "${resultbean.pullProcessStart}">
			Successfully fetched ${resultbean.successcount}  records.
			</c:if>
			</div>
			</c:if>
		</div>
		<div id="errorMsg">
			<c:if test = "${resultbean.failureResponse.size() gt 0 }">    
			<div class="alert alert-danger alert-dismissible">
			<button type="button" class="close" data-dismiss="alert"  aria-hidden="true">  &times;  </button>
			Error in  ${resultbean.failureResponse.size()} records.
			</div>
			</c:if>
		</div>
		
			<div class="row">
				<form id="form">
					<div class="panel-body">
						<div class="row">
							<div class="col-md-6 column">
								<div class="form-group">
									<label id="fromDate" for="fromDate">From Date</label>						
									<div class='input-group date' id=''>
										<input name="fromDate" id="fromDate" type="date" />
									</div>
								</div>
							</div>
							<div class="col-md-6 column">
								<div class="form-group">
									<label id="toDate" for="toDate">To Date</label>						
									<div class='input-group date' id=''>
										<input name="toDate" id="toDate" type="date" />
									</div>
								</div>
							</div>
						</div>

						<div class="row">
							<div class="col-md-6 column">
								<div class="form-group">
									<label for="consumerTypeId">Consumer Type</label>
									<select name="consumerTypeId" id="consumerTypeId">
										<option value="All">All</option>
										<c:forEach var="consumerType" items="${consumerTypeList}">
										<option value="<c:out value="${consumerType.id}"/>"><c:out value="${consumerType.name}"/></option>
										</c:forEach>
									</select>
								</div>
							</div>
	
							<div class="col-md-6 column">
								<div class="form-group">
									<label for="programStructureId">Program Structure</label>
									<select name="programStructureId" id="programStructureId">
										<option value="All">All</option>
										<c:forEach var="programStructure" items="${programStructureList}">
										<option value="<c:out value="${programStructure.id}"/>"><c:out value="${programStructure.program_structure}"/></option>
										</c:forEach>
									</select>
								</div>
							</div>
	
							<div class="col-md-6 column">
								<div class="form-group">
									<label for="programId">Program</label>
									<select name="programId" id="programId">
										<option value="All">All</option>
										<c:forEach var="program" items="${programList}">
										<option value="<c:out value="${program.id}"/>"><c:out value="${program.code}"/></option>
										</c:forEach>
									</select>
								</div>
							</div>
	
							<div class="col-md-6 column">
								<div class="form-group">
									<label for="sifySubjectCode">Subject</label>
									<select name="sifySubjectCode" id="sifySubjectCode">
										<option value="All">All</option>
									</select>
								</div>
							</div>
							
							<div class="col-md-6 column">
								<div class="form-group">
									<label for="examYear">Exam Year</label>
									<select name="examYear" id="examYear" required="required">
										<option value=""></option>
										<c:forEach items="${yearList}" var="year">
											<option value="${year}">${year}</option>
										</c:forEach>
									</select>
								</div>
							</div>
	
							<div class="col-md-6 column">
								<div class="form-group">
									<label for="examMonth">Exam Month</label>
									<select name="examMonth" id="examMonth" required="required">
										<option value=""></option>
										<option value="Jan">Jan</option>
										<option value="Feb">Feb</option>
										<option value="Mar">May</option>
										<option value="Apr">Apr</option>
										<option value="May">May</option>
										<option value="Jun">Jun</option>
										<option value="Jul">Jul</option>
										<option value="Aug">Aug</option>
										<option value="Sep">Sep</option>
										<option value="Oct">Oct</option>
										<option value="Nov">Nov</option>
										<option value="Dec">Dec</option>
									</select>
								</div>
							</div>
	
							<div class="col-md-12 column">
								<div class="form-group">
									<label for="fileData" id="fileData">Upload list of students to pull marks for (Optional)</label>
									<input name="fileData" class="fileData" type="file" />(FORMAT. Sapid, Subject)
								</div>		
							</div>
						</div>
						<br>
						<div class="row">
							<div class="col-md-6 column">
							 <input type="hidden" id="createdBy" name="createdBy" value="${userId}">
							 <input type="hidden" id="lastModifiedBy" name="lastModifiedBy" value="${userId}">
								<button id="submit" name="submit" class="btn btn-large btn-primary" onclick="pullMettlMarks()">
									Pull Results
								</button>
							</div>
							<c:if test = "${resultbean.failureResponse.size() gt 0 && resultbean.pullTaskCompleted}">  
							<div class="col-md-3 column" id = "downloadbtndiv">
								<a  href="downloadPgTeePullProcessFailureResponse"  class="btn btn-large btn-primary"  id="btnDownload"   >Download Error Records Excel </a>
							</div>
							</c:if>
						</div>
					</div>
				</form>
			</div>
		</div>
	</section>
	
	
	<jsp:include page="../footer.jsp" />
	
	<script>
		let pullProcessStart = ${resultbean.pullProcessStart}

		if(pullProcessStart){
			hideSearchBtn();
			sendRequest();
			disabledFormField();
		}
		
		$(document).ready(function() {

			$('.fileData').on('change', function() {
				
				var validExts = new Array(".xlsx", ".xls");
			    var fileExt = this.value;
			    fileExt = fileExt.substring(fileExt.lastIndexOf('.'));
			    if (validExts.indexOf(fileExt) < 0) {
			    	
			      alert("Invalid file selected, valid files are of " +
			               validExts.toString() + " types.");
			      $(this).val('');
			      return false;
			    }
			});
			
			$('#programId, #programStructureId, #consumerTypeId').change(function() {
				var input = {}
				if($('#programId').val() != 'All') {
					input.programId = $('#programId').val()
				}

				if($('#consumerTypeId').val() != 'All') {
					input.consumerTypeId = $('#consumerTypeId').val()
				}

				if($('#programStructureId').val() != 'All') {
					input.programStructureId = $('#programStructureId').val()
				}
				
				console.log(input)
				$.ajax({
					url: "getListOfSubjectsForMettl",
					data : JSON.stringify(input),
					type: "POST",
					contentType: "application/json",
					dataType: 'json',
					
					success: function(response){
						$('#sifySubjectCode').empty()
						
					    $('#sifySubjectCode').append('<option value="All">All</option>')
						response.forEach(function(data) {
						    $('#sifySubjectCode').append('<option value="' + data.sifySubjectCode + '">' + data.subject + '</option>')
						})
					}
				});
			})
		})
		
		function pullMettlMarks(){
			    
				if ( $("#examYear").val() == "" || $("#examMonth").val() == "" ){
					alert("Please Select Exam Year and Exam Month");
				    return false;
				}
			    
			    hideSearchBtn();
			    $('#downloadbtndiv').html('');
			    $('#infoMsg').html('');
			    $('#successMsg').html('');
			    $('#errorMsg').html('');
			    const form = document.getElementById('form');
			    const formData = new FormData(form);
			    // Ajax call with file upload
			     $.ajax({
			      url : '/exam/admin/m/pullMettlMarksForTeeExams',
			      type : 'POST',
			      enctype: 'multipart/form-data',
			      data : formData,
			      cache : false,
			      contentType : false,
			      processData : false,
			      success : function(data){
			    		if(data.bookingcount > 0){
							let messageSuccess = '';
				        	messageSuccess += '<div class="alert alert-info alert-dismissible">';
				    		messageSuccess += '<button type="button" class="close" data-dismiss="alert"  aria-hidden="true">  &times;  </button>';
				    		messageSuccess += 'Fetching Mettl Result Data For '+data.bookingcount+' Exam Booking records of '+data.examMonth+' '+data.examYear+ ' Exam cycle, You Can Navigate Away From Screen.';
				    		messageSuccess += '</div>';
							$('#infoMsg').html(messageSuccess);
							sendRequest();
			    			disabledFormField();
				         }else{
				        		let messageInfo = '';
								messageInfo += '<div class="alert alert-info alert-dismissible">';
								messageInfo += '<button type="button" class="close" data-dismiss="alert"  aria-hidden="true">  &times;  </button>';
								messageInfo += data.bookingcount+' Exam Booking records found for '+data.examMonth+' '+data.examYear+ ' Exam cycle ';
								messageInfo += '</div>';
								$('#infoMsg').html(messageInfo);
								 showSearchBtn();
						 }

			    		if(data.successcount > 0){
							let messageSuccess = '';
				        	messageSuccess += '<div class="alert alert-success alert-dismissible">';
				    		messageSuccess += '<button type="button" class="close" data-dismiss="alert"  aria-hidden="true">  &times;  </button>';
				    		messageSuccess += '<p> Successfully fetched '+data.successcount+' records. </p>';
							if(data.transferredCount > 0) { 				    		
				    			messageSuccess += '<p> Records Transferred to Marks : ' + data.transferredCount + '. </p>';
							}
							if(data.bodAppliedCount > 0) {
				    			messageSuccess += '<p> Bod Applied to ' + data.bodAppliedCount + ' records. </p>';	
					    	}
				    		messageSuccess += '</div>';
							$('#successMsg').html(messageSuccess);
				         }
				     	
				     	if(data.failureResponse.length > 0){
				         	
				         	let messageError = '';
				     		messageError += '<div class="alert alert-danger alert-dismissible">';
				    		messageError += '<button type="button" class="close" data-dismiss="alert"  aria-hidden="true">  &times;  </button>';
				    		messageError += "Error in " + data.failureResponse.length + " records." ;
				    		messageError += '</div>';
							$('#errorMsg').html(messageError);
				        }
				     	

			      },error: function (result, status, err) {
				          alert("There was a Error in processing API, Please contact to IT department!");
				          showSearchBtn();
				          enabledFormField()
				  }
			    	
			    });
			    
			    
		 }


		function hideSearchBtn(){
			console.log('call hide');
			$( "#submit" ).replaceWith( "<img id='theImg' src='/exam/resources_2015/gifs/loading-29.gif' style='height:40px' />" );
		}
		
		
		
		function showSearchBtn(){
			$( "#theImg" ).replaceWith( '<button id="submit" name="submit" class="btn btn-large btn-primary" onclick="pullMettlMarks()" > Pull Results </button>' );
		}

		function sendRequest(){
			let pullTaskCompleted = false;
		      $.ajax({
		    	type : "GET",
		        url: "/exam/admin/m/getPullProcessStatus",
		        success: function(data){
		        	pullTaskCompleted = data.pullTaskCompleted;

		        	if(pullTaskCompleted){
		        		location.reload();
			        }
		        	
		    		if(data.bookingcount > 0){
						let messageInfo = '';
						messageInfo += '<div class="alert alert-info alert-dismissible">';
						messageInfo += '<button type="button" class="close" data-dismiss="alert"  aria-hidden="true">  &times;  </button>';
						messageInfo += 'Fetching Mettl Result Data For '+data.bookingcount+' Exam Booking records of '+data.examMonth+' '+data.examYear+ ' Exam cycle, You Can Navigate Away From Screen.';
						messageInfo += '</div>';
						$('#infoMsg').html(messageInfo);
			         }

		    		if(data.successcount > 0){
						let messageSuccess = '';
			        	messageSuccess += '<div class="alert alert-success alert-dismissible">';
			    		messageSuccess += '<button type="button" class="close" data-dismiss="alert"  aria-hidden="true">  &times;  </button>';
			    		messageSuccess += '<p> Successfully fetched '+data.successcount+' records. </p>';
			    		
			    		if(data.transferredCount > 0) { 				    		
			    			messageSuccess += '<p> Records Transferred to Marks : ' + data.transferredCount + '. </p>';
						}
						if(data.bodAppliedCount > 0) {
			    			messageSuccess += '<p> Bod Applied to ' + data.bodAppliedCount + ' records. </p>';	
				    	}
			    		
			    		messageSuccess += '</div>';
						$('#successMsg').html(messageSuccess);
			         }

					
			     	
			     	if(data.failureResponse.length > 0){
			         	
			         	let messageError = '';
			     		messageError += '<div class="alert alert-danger alert-dismissible">';
			    		messageError += '<button type="button" class="close" data-dismiss="alert"  aria-hidden="true">  &times;  </button>';
			    		messageError += "Error in " + data.failureResponse.length + " records." ;
			    		messageError += '</div>';
						$('#errorMsg').html(messageError);
			        }
		        },
		        complete: function() {
		        	// Schedule the next request when the current one's complete
		        	if(!pullTaskCompleted){
					    setTimeout(sendRequest, 5000);  
				    }
		     }
		    });
		}

		function disabledFormField(){
			var form = document.getElementById("form");
			var elements = form.elements;
			for (var i = 0, len = elements.length; i < len; ++i) {
			    elements[i].disabled = 'disabled';
			}
		}

		function enabledFormField(){
			var form = document.getElementById("form");
			var elements = form.elements;
			for (var i = 0, len = elements.length; i < len; ++i) {
			    elements[i].disabled = '';
			}
		}
		
	</script>
</body>
</html>
