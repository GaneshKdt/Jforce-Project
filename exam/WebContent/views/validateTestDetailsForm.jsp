<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!--> 

<%@page import="com.nmims.beans.UpgradAssessmentExamBean"%>
<%@page import="com.nmims.beans.ProgramSubjectMappingExamBean"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Date"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.text.Format"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>


<html class="no-js"> <!--<![endif]-->
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

	<link rel="stylesheet" type="text/css" href="https://cdn.datatables.net/1.10.20/css/jquery.dataTables.css">
	<jsp:include page="jscss.jsp">
		<jsp:param value="Validate Test Details for UPGRAD" name="title" />
	</jsp:include>
	<script>

	 function getTestDetails() {
		    
	     $.getJSON('searchBySubjectForTest_ajax', {
	    	 examYear : $('#examYearList').val(), examMonth: $('#examMonthList').val(), acadYear: $('#acadYearList').val(), 
				acadMonth:  $('#acadMonthList').val(), subjectId: $('#subjectName_list').val(),
	      ajax : 'true'
	     }, function(data) {
		  
	      var html = '<option disabled selected value="">Select Test</option>';
	      var len = data.length;
	      console.log(len);
	      for ( var i = 0; i < len; i++) {
	       html += '<option value="' + data[i].testId + '">'
	         + data[i].testName + '</option>';
	      }
	      html += '</option>';
	   
	      $('#testName_list').html(html);
	     });
	     
	    }

		
		function getSubjectDetails() {
			
			 $('#testName_list').load(" #testName_list > *");
			var data = { batchId :  $('#batch_list').val() };
	
	     $.ajax({
				type : "POST",
				url : 'getSubjectDetails',
				contentType : "application/json",
				data : JSON.stringify(data),
				dataType : "json",

				success : function(data) {
 
						var htmlSubjectList = '<option disabled selected value="">Select Subject</option>';
					      var len = data.length;
					      for ( var i = 0; i < len; i++) {
					    	  htmlSubjectList += '<option value="' + data[i].subjectId + '">'
					         + data[i].subject+ '</option>';
					      }
					      htmlSubjectList += '</option>';
					   
					      $('#subjectName_list').html(htmlSubjectList);
					
	
				}
			});	
			
	    }

//following function added for Display Dropdown Batch 
		function getBatchDetails() {

			// following fields added for reload the Dropdown Menu
			 $('#testName_list').load(" #testName_list > *");
			 $('#subjectName_list').load(" #subjectName_list > *");

				
			var data = { examYear : $('#examYearList').val(), examMonth: $('#examMonthList').val(), acadYear: $('#acadYearList').val(), 
					acadMonth:  $('#acadMonthList').val() };
	
	     $.ajax({
				type : "POST",
				url : 'getBatchDetails',
				contentType : "application/json",
				data : JSON.stringify(data),
				dataType : "json",
				success : function(data) {

					      var htmlBatchList = '<option disabled selected value="">Select Batch</option>';
					      var len = data.length;
					      for ( var i = 0; i < len; i++) {
					    	  htmlBatchList += '<option value="' + data[i].batchId + '">'
					         + data[i].batchName+ '</option>';
					      }
					      htmlBatchList += '</option>';
					   
					      $('#batch_list').html(htmlBatchList);					
	
				}
			});	
			
	    }

		
		
	    
	</script>
	<body class="inside">
	
   <%@ include file="header.jsp"%>
    
    <section class="content-container login">
        <div class="container-fluid customTheme">
          <div class="row">
             <legend>Verify Test Data For UPGRAD</legend>
          </div> <!-- /row -->
           
          <%@ include file="messages.jsp" %>
          
         
          	<div class="row">
					<div class="col-md-16 column">
					
						<div class="row">
							<div class="col-md-4 column">
								<div class="form-group">
									<label >Exam Year</label>
									<select  id="examYearList" name="examYearList" onchange="getBatchDetails()"  >
									<option disabled selected value="">Select Exam Year</option>
										<c:forEach var="examYear" items="${examYearList}">
						                <option value="<c:out value="${examYear}"/>">
						                  <c:out value="${examYear}"/>
						                </option>
				            			</c:forEach>
									</select>
								</div>
							</div>
							
							<div class="col-md-4 column">
								<div class="form-group">
									<label >Exam Month</label>
									<select  id="examMonthList" name="examMonthList" onchange="getBatchDetails()"  >
									<option disabled selected value="">Select Exam Month</option>
										<c:forEach var="examMonth" items="${examMonthList}">
						                <option value="<c:out value="${examMonth}"/>">
						                  <c:out value="${examMonth}"/>
						                </option>
				            			</c:forEach>
									</select>
								</div>
							</div>
							
							<div class="col-md-4 column">
								<div class="form-group">
									<label >Acads Year</label>
									<select  id="acadYearList" name="acadYearList" onchange="getBatchDetails()"  >
									<option disabled selected value="">Select Acads Year</option>
										<c:forEach var="acadYear" items="${acadsYearList}">
						                <option value="<c:out value="${acadYear}"/>">
						                  <c:out value="${acadYear}"/>
						                </option>
				            			</c:forEach>
									</select>
								</div>
							</div>
							
							<div class="col-md-4 column">
								<div class="form-group">
									<label >Acads Month</label>
									<select  id="acadMonthList" name="acadMonthList"  onchange="getBatchDetails()" >
									<option disabled selected value="">Select Acads Month</option>
										<c:forEach var="acadMonth" items="${acadsMonthList}">
						                <option value="<c:out value="${acadMonth}"/>">
						                  <c:out value="${acadMonth}"/>
						                </option>
				            			</c:forEach>
									</select>
								</div>
							</div>
					
						</div>
					
					<div class="row">
					
					<!-- ///////////////////////////////////////////////////////////////// -->
						
						<div class="col-md-4 column">
							<div class="form-group">
								<label for="batch">Batch</label>
								<select  id="batch_list" name="batch_list" onchange="getSubjectDetails()"  required="required">
									<option disabled selected value="">Select Batch</option>	
								</select>
							</div>
						</div>
						
						
						<div class="col-md-4 column">
							<div class="form-group">
								<label for="semSubject">Subject</label>
								<select  id="subjectName_list" name="subjectName_list" onchange="getTestDetails()"  required="required">
									<option disabled selected value="">Select Subject</option>	
								</select>
							</div>
						</div>
						
						<div class="col-md-4 column">
							<div class="form-group">
							<label for="testName">Test</label>
							<select id="testName_list" >
              					<option   disabled  selected value="">Select Test</option>
              				</select>
							</div>
						</div> 
						
						<div class="col-md-4 column">
							<br>
							<input type="button" class="btn btn-md btn-primary" value="Search" id ="search" onclick="getStudentTestDetails()">
						</div> 
					</div>
					<br>

			</div>
		  
		  <div id="searchBody" style="display:none;">          
            <div class="col-xs-18 panel-body" >
	          <div class="bullets">
                <h4>Verify Test Data For UPGRAD</h4>
                
              </div>
              
             <br>
            
             <div class = "row">
				<div class="col-sm-offset-1 col-sm-6 panel-body">
				
				<h2>&nbsp;Records Count Distribution </h2>
				
				<div class="table-responsive">
					<table class="table table-striped" style="font-size: 12px" border="1px">
					<thead>
					  <tr>
						<th>Category</th>
						<th>Count</th>
					  </tr>
					</thead>
					<tbody>
						
						<tr>
							<td>Expected SapId</td>
							<td><span id="expSapId"></td>
						</tr>
						<tr>
							<td>Received SapId</td>
							<td> <span id="totalSapId"></span></td>
						</tr>
						<tr>
							<td>Show Result</td>
							<td>Y : <span id="totalShowResultY"></span> &nbsp;&nbsp; &#38; &nbsp; &nbsp; N : <span id="totalShowResultN"></span>  </td>
						</tr>
						<tr>
							<td>Attempt</td>
							<td>Y : <span id="totalAttemptY"></span> &nbsp;&nbsp; &#38; &nbsp; &nbsp; N : <span id="totalAttemptN"></span></td>
						</tr>
						<tr>
							<td>Copy Case</td>
							<td><span id="copyCaseCount"></span></td>
						</tr>
					
					</tbody>
					
					</table>
				</div>
				</div>
								
		</div>
            <div class="row">
            	<div class="col-sm-offset-14 col-sm-4">
					<a href="/exam/admin/downloadVerifyTestDataForUPGRAD" id="downloadExcel" class="btn btn-large btn-primary" title="Download Verify Test Data For UPGRAD">Download Excel</a>
				</div>
            </div>
              	
       		<br>  	
              <div class="panel-body">
              	
                <form:form  role="form" id="validateTestDetails" action="validateTestDetailsStatusForm" method="post"   modelAttribute="upgradAssessmentBean" >
           
                	
                	
             			<div class="table-responsive">
							<table class="table table-striped table-hover dataTables"  style="font-size:12px">
								<thead>
									<tr> 
										<th>Sr. No.</th>
										<th>SapId</th>
										<th>Name</th>
										<th>Email</th>
										<th>Contact No.</th>
										<th>Batch</th>
										<th>Test </th>
										<th>Attempt</th>
										<th>Score</th>
										<th>Total Questions</th>
										<th>Max Peer Penalty</th>
     									<th>Max Online Penalty</th>
										
										<th>TestStartedOn</th>
										<th>TestEndedOn</th>
										<th>Remaining Time</th>
										<th>ShowResult</th>
										<th></th>
										
									</tr>
								</thead>
								<tbody id="testTable">
							
								 
								</tbody>
							</table>
							
						</div>
			
               		<br>
               		
               		<div class="row">
               		<div class=" col-md-4">
               			<form:input path="testId" id="testId" type="hidden" />
                    	<label id="checkLabel" ><input type="checkbox" id="checkme"  disabled="disabled" /> &nbsp;&nbsp;&nbsp;&nbsp; Student Test Verify</label>
                    	<button type="submit" class="customBtn red-btn " disabled="disabled" id="sendToUpgrad">Student Test Score Have Been Verified </button>
                 	</div>
                 	</div>
                </form:form >
                
              </div> <!--/module-box-->
            </div> <!-- /col-xs-6 -->
          </div> <!-- /row -->
          
        </div> <!-- /container -->
    </section>
    

     	<div class="modal fade" id="myModal" role="dialog" >
     		<div class="modal-dialog modal-lg">
     			<div class="modal-content">
     				<div class="modal-header">
     				<button type="button" class="close" data-dismiss="modal">&times;</button>
     				<h4 class="modal-title">Test Questions Details of SapID : <span id="modalSapId"></span></h4>
     				</div>
     				<div class="modal-body">
     				<div class="panel-body">
     				<div class="row">
     					<div class="table-responsive col-sm-18">
     						<table class="table table-striped table-hover dataTables2" >
     							<thead>
     								<tr>
     								<th>Sr No</th>
     								
     								<th>Question</th>
     								<th>StudentAnswer</th>
     								<th>MarksObtained</th>
     								<th>Before Normalize</th>
     								<th>Peer Penalty</th>
     								<th>Online Penalty</th>
     								<th>Action</th>
     								</tr>
     							</thead>
     							<tbody id="questionDeatils">
     							
     							</tbody>
     						</table>
     					</div>
     				</div>
     				</div>
     				</div>
     				<div class="modal-footer">
     					<button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
     				</div>
 				</div>
     		</div>
     	</div>

    <jsp:include page="footer.jsp" />
   <!-- jQuery (necessary for Bootstrap's JavaScript plugins) -->
	
	<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />assets/js/jquery.tabledit.js"></script>


	<script src="https://cdn.datatables.net/1.10.13/js/jquery.dataTables.min.js"></script>
	<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/js/vendor/dataTables.bootstrap.js"></script>
	<script src="https://cdn.datatables.net/buttons/1.2.4/js/dataTables.buttons.min.js"></script>
	
   
    
	<script>

	var checker = document.getElementById('checkme');
	 var sendbtn = document.getElementById('sendToUpgrad');
	 sendbtn.style.cursor = "not-allowed";
	 // when unchecked or checked, run the function
	 checker.onchange = function(){
	if(this.checked){
		sendbtn.style.cursor = "pointer";
	    sendbtn.disabled = false;
	    
	} else {
		sendbtn.style.cursor = "not-allowed";
	    sendbtn.disabled = true;
	}

	}

	 function is_url(str) {
			var regexp =
				/^(?:(?:https?|ftp):\/\/)?(?:(?!(?:10|127)(?:\.\d{1,3}){3})(?!(?:169\.254|192\.168)(?:\.\d{1,3}){2})(?!172\.(?:1[6-9]|2\d|3[0-1])(?:\.\d{1,3}){2})(?:[1-9]\d?|1\d\d|2[01]\d|22[0-3])(?:\.(?:1?\d{1,2}|2[0-4]\d|25[0-5])){2}(?:\.(?:[1-9]\d?|1\d\d|2[0-4]\d|25[0-4]))|(?:(?:[a-z\u00a1-\uffff0-9]-*)*[a-z\u00a1-\uffff0-9]+)(?:\.(?:[a-z\u00a1-\uffff0-9]-*)*[a-z\u00a1-\uffff0-9]+)*(?:\.(?:[a-z\u00a1-\uffff]{2,})))(?::\d{2,5})?(?:\/\S*)?$/;
			if (regexp.test(str)) {
				return true;
			} else {
				return false;
			}
		}
		
	function getQuestions(sapId, testId){
		var sapid = sapId;
		var testid = testId;
	

		 $().ready(	function() { 
			 
			 $.getJSON('getStudentAssessmentDetails', {
		    		testid : testid,
			    	 sapid : sapid,
			      ajax : 'true'
			     }, function(data) {
			    	 var modal = '';
			    	 var len = data.length;
				      var srNo = 0;
				      for(var i = 0 ; i< len; i++){
					      srNo++;
					      let attemptStatus = '';
					      	if(data[i].remark == 'Marked For Copy Case'){
					      		attemptStatus = 'CopyCase';
							}
							
							modal += '<tr data-copycase-id="'+attemptStatus+'" data-questionType-id="'+data[i].questionTypeId+'" value="'+data[i].sapid+'~'+data[i].testId+'~'+data[i].questionNo+'">';
							modal += '<td >'+srNo+'</td>';
							
							modal += '<td>'+data[i].question+'</td>';
							if(is_url(data[i].studentAnswer)){
								modal += '<td> <a href= "'+data[i].studentAnswer+'" target="_blank" > Student Answer Link </a></td>';
							}else{
								modal += '<td>'+data[i].studentAnswer+'</td>';
							}
							
							modal += '<td >'+data[i].marksObtained+'</td>';
							modal += '<td>'+( data[i].beforeNormalizeScore ? data[i].beforeNormalizeScore : '') +'</td>';
							
							modal += '<td >'+data[i].peerPenalty+'</td>';
							modal += '<td >'+data[i].onlinePenalty+'</td>';	
							modal += '</tr>';
							
					      }
				      $('#questionDeatils').html(modal);
				      $('#modalSapId').html(sapid);

				      let id = "";
					   
					     $(".dataTables2").on('click','tr',function(e){
//				 		    e.preventDefault();	
						    var str = $(this).attr('value');
						     id = str.split('~');
						    
						}); 
					    
					    $('.dataTables2').Tabledit({
					    	columns: {
								  identifier: [0, 'id'],                 
								  editable: [
									  			[3, 'marksObtained']
									  			
								  			]
								},
								// link to server script
								// e.g. 'ajax.php'
								url: "",
								// class for form inputs
								inputClass: 'form-control input-sm',
								// // class for toolbar
								toolbarClass: 'btn-toolbar',
								// class for buttons group
								groupClass: 'btn-group btn-group-sm',
								// class for row when ajax request fails
								 dangerClass: 'warning',
								// class for row when save changes
								warningClass: 'warning',
								// class for row when is removed
								mutedClass: 'text-muted',
								// trigger to change for edit mode.
								// e.g. 'dblclick'
								eventType: 'click',
								// change the name of attribute in td element for the row identifier
								rowIdentifier: 'id',
								// activate focus on first input of a row when click in save button
								autoFocus: true,
								// hide the column that has the identifier
								hideIdentifier: false,
								// activate edit button instead of spreadsheet style
								editButton: true,
								// activate delete button
								deleteButton: false,
								// activate save button when click on edit button
								saveButton: true,
								// activate restore button to undo delete action
								restoreButton: true,
								onDraw: function() { 
									/* $('.dataTables').DataTable();  */
								},
								onAjax: function(action, serialize) {
									
									
									serialize['sapid'] = id[0];
									serialize['testId'] = id[1];
									serialize['questionNo'] = id[2];
									let body = JSON.stringify(serialize);
							

									$.ajax({
										type : "POST",
										url : 'updateMarksObtained',
										contentType : "application/json",
										data : body,
										dataType : "json",
										success : function(response) {
											
											if (response.Status == "Success") {
												 if (action === 'edit'){
													alert('Entries Saved Successfully')
													$("#search").triggerHandler("click")							
													getQuestions(id[0], id[1])
												}
											} else {
												alert('Entries Failed to update : ' + response.Message)
											}

										},
									      error: function (result, status, err) {
									          alert("Sorry, there was a Error in processing API!");
									          
									        }
									});
								}
							});
					    $(".tabledit-toolbar.btn-toolbar").removeAttr("style");	
					    $('tr[data-questionType-id="1"]').each(function() {
					         $(this).find("button.tabledit-edit-button").css("display","none");
					            });
					    $('tr[data-questionType-id="2"]').each(function() {
					         $(this).find("button.tabledit-edit-button").css("display","none");
					            });
					    $('tr[data-questionType-id="5"]').each(function() {
					         $(this).find("button.tabledit-edit-button").css("display","none");
					            });
					    $('tr[data-copycase-id="CopyCase"]').each(function() {
					         $(this).find("button.tabledit-edit-button").css("display","none");
					            });
					    		
				      $('#myModal').modal('show');
				      
				      
			     });	
		});
		
	}

	function hideSearchBtn(){
		console.log('call hide');
		$( "#search" ).replaceWith( '<img id="theImg" src="/exam/resources_2015/gifs/loading-29.gif" style="height:40px" />' );
	}
	
	
	
	function showSearchBtn(){
		$( "#theImg" ).replaceWith( '<input type="button" class="btn btn-md btn-primary" value="Search" id ="search"  onclick="getStudentTestDetails()" />' );
	}
		
			

	function getStudentTestDetails() {
				hideSearchBtn();
				var testId =	$('#testName_list').val();
				$('#testId').val(testId)
				
				$.getJSON('searchByTestId', {
			    	 
			    	 testId : testId,
			      ajax : 'true'
			     }, function(data) {
			      var html = '';
			      var sapIdCount = 0, showResultCountY = 0, attemptCountY = 0, showResultCountN, attemptCountN, copyCaseCount = 0;
			      var len = data.length;
			      var srNo = 0;
			      if(data!=null)
			      	$('#expSapId').html(data[0].expectedSapIdCount); 	
			      
			      for ( var i = 0; i < len; i++) {
				      srNo++;
				      sapIdCount = data[i].sapIdCount;
				      showResultCountY = data[i].showResultCountY;
				      attemptCountY = data[i].attemptCount;
				      copyCaseCount = data[i].copyCaseCount;
				       html += '<tr >';
				       html += '<td >'+ srNo+ '</td>';    
				       html += '<td >'+ data[i].sapid + '</td>';
				       html += '<td >'+ data[i].name + '</td>';
				       html += '<td >'+ data[i].emailId + '</td>';
				       html += '<td >'+ data[i].mobile + '</td>';
				       html += '<td >'+ data[i].batchName + '</td>';
				       html += '<td style="width: 226px;">'+ data[i].testName + '</td>';

				       if(data[i].attemptStatus == 'CopyCase'){
					       html += '<td >Copy Case</td>';
					   }else if(data[i].attempt == 1){
				       html += '<td >Y</td>';
				       }else if(data[i].attempt == 0){
				    	   html += '<td >N</td>';
						}else{
							html += '<td ></td>';
						}
				       html += '<td >'+ data[i].score +' / ' +data[i].maxScore +'</td>';
				       html += '<td  onclick="getQuestions('+data[i].sapid+','+data[i].testId+');">'+ data[i].noOfQuestionsAttempted  + ' / '+ data[i].questionNoCount +'</td>';
				       html += '<td >'+( data[i].peerPenalty ? data[i].peerPenalty : '') + '</td>';	
				       html += '<td >'+( data[i].onlinePenalty ? data[i].onlinePenalty : '') + '</td>';	
				       html += '<td >'+( data[i].testStartedOn ? data[i].testStartedOn : '') + '</td>';
				       html += '<td >'+( data[i].testEndedOn ? data[i].testEndedOn : '') + '</td>';
						
				       html += '<td >'+ data[i].remainingTime + '</td>';		
				       html += '<td >'+ data[i].showResult + '</td>';
				      
				       	html += '<td  onclick="getQuestions('+data[i].sapid+','+data[i].testId+');"><span class="glyphicon glyphicon-info-sign"></span></td>'
				       
				       html += '</tr>';
			      }
				
			      showResultCountN = sapIdCount - showResultCountY;
			      attemptCountN = ( sapIdCount - attemptCountY ) - copyCaseCount;
				$('#searchBody').css("display", "block");
			      if( !$('#testName_list').val() || len === 0 ){
				      console.log('true');
				      $('#downloadExcel').attr('disabled','disabled');
			    	  var table = $('.dataTables').DataTable(); 
				       table.destroy();
				       $('#totalSapId').html(sapIdCount);  
				      $('#totalShowResultY').html(showResultCountY);  
				      $('#totalShowResultN').html(showResultCountN);  
				      $('#totalAttemptY').html(attemptCountY);   
				      $('#totalAttemptN').html(attemptCountN);   
				      $('#copyCaseCount').html(copyCaseCount);   
				      $('#testTable').html(html);
				      $('.dataTables').DataTable(); 
			    	  checker.disabled = true;
			    	  $('.dataTables').DataTable(); 
				      }else{  
				    	  $('#downloadExcel').removeAttr('disabled');        
				      var table = $('.dataTables').DataTable(); 
				       table.destroy();
				       checker.disabled = false;
				       document.getElementById('checkme').checked = false;
				       sendbtn.style.cursor = "not-allowed";
					    sendbtn.disabled = true;
				      $('#totalSapId').html(sapIdCount);  
				      $('#totalShowResultY').html(showResultCountY);  
				      $('#totalShowResultN').html(showResultCountN);  
				      $('#totalAttemptY').html(attemptCountY);   
				      $('#totalAttemptN').html(attemptCountN);
				      $('#copyCaseCount').html(copyCaseCount);   
				      $('#testTable').html(html);			     
				      $('.dataTables').DataTable(); 
				      
				  }
			      showSearchBtn();
					
			     });
			}

	</script>



  </body>
</html>