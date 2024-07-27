<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html lang="en">
	
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
    <jsp:include page="adminCommon/jscss.jsp">
	<jsp:param value="UFM Mark RIA / NV" name="title"/>
    </jsp:include>
    
    <body>
    
    	<%@ include file="adminCommon/header.jsp" %>
        <div class="sz-main-content-wrapper">
        
        	<jsp:include page="adminCommon/breadcrum.jsp">
			<jsp:param value="Exam;Mark RIA / NV" name="breadcrumItems"/>
			</jsp:include>
        	 
            
            <div class="sz-main-content menu-closed">
                    <div class="sz-main-content-inner">
              				<jsp:include page="adminCommon/left-sidebar.jsp">
								<jsp:param value="" name="activeMenu"/>
							</jsp:include>
              				
              				
              				<div class="sz-content-wrapper examsPage">
              						<%@ include file="adminCommon/adminInfoBar.jsp" %>
              						<div class="sz-content">
								
											<h2 class="red text-capitalize">UFM Mark for RIA / NV</h2>
											<div class="clearfix"></div>
											<div class="panel-content-wrapper" style="min-height:100%;">

											<div id="infosMsg"></div>			
											<div id="succussMsg"></div>			
											<div id="errorMsg"></div>			
											
													<div class="row" >
														<div class="col-md-3">
																<select id="year" name="year"   class="form-control" required = "required" >
																	<option value="">Select Exam Year</option>
																	<c:forEach var="year" items="${yearList}">
																		<option value="<c:out value="${year}"/>"><c:out value="${year}"/></option>
																	</c:forEach>
																</select>
													   </div>
														<div class="col-md-3">
																<select id="month" name="month" class="form-control"  required = "required">
																	<option value="">Select Exam Month</option>
																	<c:forEach var="month" items="${monthList}">
																		<option value="<c:out value="${month}"/>"><c:out value="${month}"/></option>
																	</c:forEach>
																</select>
													  </div>
													  <div class="col-md-3">
																<select id="status" name="status" class="form-control"  required = "required">
																	<option value="">Select Status</option>
																	<option value="RIA">RIA</option>
																	<option value="NV">NV</option>
																	<option value="SCORED">Scored</option>
																</select>
													  </div>
												</div>
												<br>
											   <div class="row">
													<div class="col-md-2">
														<button id="search" onclick="searchRecords()" class="btn btn-large btn-primary" >Search</button>
														<div id="downloadReportButtondiv"></div>
														<div id="markButtondiv"></div>
													</div>
													
												</div>
												
								</div>
							</div>
              			</div>
    				</div>
			   </div>
		    </div>
        <jsp:include page="adminCommon/footer.jsp"/>
        
        <script type="text/javascript">
        function hideSearchBtn(){
			console.log('call hide');
			$( "#search" ).replaceWith( "<img id='theImg' src='/exam/resources_2015/gifs/loading-29.gif' style='height:40px' />" );
		}
		
		
		
		function showSearchBtn(){
			$( "#theImg" ).replaceWith( '<button id="search" onclick="searchRecords()" class="btn btn-large btn-primary" >Search</button>' );
		}

		function searchRecords(){
		    
			if ( $("#year").val() == "" || $("#month").val() == "" ){
				alert("Please Select Exam Year and Exam Month");
			    return false;
			}

			if ( $("#status").val() == ""  ){
				alert("Please Select Status");
			    return false;
			}

			let status = $("#status").val();
			let data = {
					"year" : $("#year").val(),
					"month" : $("#month").val(),
					"status" : status
				};
		    hideSearchBtn();

		    $('#downloadbtndiv').html('');
		    $('#infosMsg').html('');
		    $('#succussMsg').html('');
		    $('#errorMsg').html('');
		    $('#markButtondiv').html('');
		    $('#downloadReportButtondiv').html('');
		    
		     $.ajax({
		      url : '/exam/m/getPendingRIARecords',
		      type : 'POST',
		      contentType : "application/json",
			  data : JSON.stringify(data),
			  dataType : "JSON",
		      success : function(data){
					let messageSuccess = '';
					let downloadReportbtn = '';
		        	messageSuccess += '<div class="alert alert-info alert-dismissible">';
		    		messageSuccess += '<button type="button" class="close" data-dismiss="alert"  aria-hidden="true">  &times;  </button>';
					if(status == 'RIA'){
						messageSuccess += 'Total RIA Records is '+data.length;
						downloadReportbtn = '<a  href="downloadUFMMarkRIARecords"  class="btn btn-large btn-primary"  id="btnDownload"   >Download RIA Records Excel </a>';
					}else if(status == 'NV'){
						messageSuccess += 'Total NV Records is '+data.length;
						downloadReportbtn = '<a  href="downloadUFMMarkNVRecords"  class="btn btn-large btn-primary"  id="btnDownload"   >Download NV Records Excel </a>';
					}else if(status == 'SCORED'){
						messageSuccess += 'Total Scored Record is '+data.length;
						downloadReportbtn = '<a  href="downloadUFMMarkScoredRecords"  class="btn btn-large btn-primary"  id="btnDownload"   >Download Scored Records Excel </a>';
					}
		    		
		    		messageSuccess += '</div>';
					$('#infosMsg').html(messageSuccess);
					if(data.length > 0){
						$('#downloadReportButtondiv').html(downloadReportbtn);
					}
		    		showSearchBtn();
		    		if(data.length > 0){
						let markButton= '<button id="updateRIANVRecords" onclick="updateRIANVRecords()" class="btn btn-large btn-primary" >Mark '+status+'</button>';			     	
						$('#markButtondiv').html(markButton);
		    		}

		      },error: function (result, status, err) {
			          alert("There was a Error in processing API");
			          showSearchBtn();
			  }
		    	
		    });
		    
		    
	 }


		function updateRIANVRecords(){
		    
			if ( $("#year").val() == "" || $("#month").val() == "" ){
				alert("Please Select Exam Year and Exam Month");
			    return false;
			}

			if ( $("#status").val() == ""  ){
				alert("Please Select Status");
			    return false;
			}

			let status = $("#status").val();
			let data = {
					"status" : status
				};
		    hideSearchBtn();
		    $('#downloadbtndiv').html('');
		    $('#infosMsg').html('');
		    $('#succussMsg').html('');
		    $('#errorMsg').html('');
		    $('#markButtondiv').html('');
		    $('#downloadReportButtondiv').html('');
		     $.ajax({
		      url : '/exam/m/upateRIANVRecords',
		      type : 'POST',
		      contentType : "application/json",
			  data : JSON.stringify(data),
			  dataType : "JSON",
		      success : function(data){
					let messageSuccess = '';
					
		        	messageSuccess += '<div class="alert alert-success alert-dismissible">';
		    		messageSuccess += '<button type="button" class="close" data-dismiss="alert"  aria-hidden="true">  &times;  </button>';
					if(status == 'RIA' && data.successCount > 0){
						messageSuccess += 'RIA Successfully Marked for '+data.successCount+' records ';
					}else if(status == 'NV' && data.successCount > 0){
						messageSuccess += 'NV Successfully Marked for '+data.successCount+' records ';
					}else if(status == 'SCORED' && data.successCount > 0){
						messageSuccess += 'SCORED Successfully Marked for '+data.successCount+' records ';
					}
		    		
		    		messageSuccess += '</div>';
					$('#succussMsg').html(messageSuccess);
					
					if( data.errorCount > 0){
						let errormessage = '';
						errormessage += '<div class="alert alert-danger alert-dismissible">';
						errormessage += '<button type="button" class="close" data-dismiss="alert"  aria-hidden="true">  &times;  </button>';
						if(status == 'RIA' ){
							errormessage += 'Unable to Mark RIA for '+data.errorCount+' records ';
						}else if(status == 'NV' ){
							errormessage += 'Unable to Mark NV for '+data.errorCount+' records ';
						}else if(status == 'SCORED' ){
							errormessage += 'Unable to Mark SCORED for '+data.errorCount+' records ';
						}
						errormessage += '</div>';
						$('#errorMsg').html(errormessage);
					}

					$("#year").val('');
					$("#month").val('');
					$("#status").val('');
		    		showSearchBtn();
			     	

		      },error: function (result, status, err) {
			          alert("There was a Error in processing API");
			          showSearchBtn();
			  }
		    	
		    });
		    
	 }



		$('#status').on('change', function() {
			$('#markButtondiv').html('');
			$('#downloadReportButtondiv').html('');
		});

		
		   
        </script>
		
    </body>
</html>