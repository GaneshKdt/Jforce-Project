<%-- 
Commented and kept as will require late by PS 28May
<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!-->

<%@page import="com.nmims.beans.Person"%>
<%@page import="com.nmims.beans.Page"%>
<html class="no-js">
<!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<jsp:include page="jscss.jsp">
	<jsp:param value="Add Key Video Content" name="title" />
</jsp:include>

<style>
#loading {
    background: url('resources_2015/images/spinningwheel.gif') no-repeat center center;
    position: fixed;
    top: 0;
    left: 0;
    height: 100vh;
    width: 100%;
    z-index: 9999999;
}
</style>

<body class="inside">
<div id="loading"></div>

	<%@ include file="header.jsp"%>

	<section class="content-container">

		<div class="container-fluid customTheme">
		<div class="row"><legend>&nbsp;Add Video Content</legend></div>
		<div class="panel-body">
		<%@ include file="messages.jsp"%>
		
    <%try{ %>
    		<div class="sz-content">
				<div class="well">
					<c:if test="${sessionDetails != null}">
					<h2>${sessionDetails}</h2>
					<%@ include file="uploadVideoContentFilesErrorMessages.jsp"%>
				
					<form:form modelAttribute="fileBean" method="post" 	enctype="multipart/form-data" action="uploadVideoContentFiles">
					
					<form:hidden path="fileId" value="${sessionId}" />
					<h1>got sessionid ${sessionId}</h1>
					<div class="panel-body">
					<div class="col-md-6 column">
						<div class="form-group">
							<form:label for="fileData" path="fileData">Select file</form:label>
							<form:input path="fileData" type="file" />
						</div>
						
			</div>
			
			
			<div class="col-md-12 column">
			<b>Format of Upload: </b><br>
			Year | Month | Subject | FileName | KeyWords | Description | Default Video  <br>
			<a href="resources_2015/templates/MainVidoeStructure.xlsx" target="_blank">Download a Sample Template</a>
			
			</div>
			
			
			</div>
			<br>
			<div class="row">
				<div class="col-md-6 column">
					<button id="submit" name="submit" class="btn btn-large btn-primary"
						formaction="uploadVideoContentFiles">Upload</button>
				</div>

				
			</div>
			</form:form>
			</c:if>
			</div>
				
			<!-- Bulk upload Start -->
					<div class="jumbotron"> 
					<h2>Video Details Batch Upload</h2> 
				
					<form:form modelAttribute="fileBean" method="post" 	enctype="multipart/form-data" action="uploadVideoContentFilesBatch">
					 
					<div class="panel-body">
					<div class="col-md-6 column">
						<div class="form-group">
							<form:label for="fileData" path="fileData">Select file</form:label>
							<form:input path="fileData" type="file" />
				   </div>
						
			</div>
			
			
			<div class="col-md-12 column">
			<b>Format of Upload: </b><br>
			Year | Month | Subject | FileName | KeyWords | Description | Default Video  <br>
			<a href="resources_2015/templates/MainVidoeStructureBatchUpload.xlsx" target="_blank">Download a Sample Template</a>
			
			</div>
			
			
			</div>
			<br>
			<div class="row">
				<div class="col-md-6 column">
					<button id="submit" name="submit" class="btn btn-large btn-primary"
						formaction="uploadVideoContentFilesBatch">Upload</button>
				</div>

				
			</div>
			</form:form> 
			</div>
			<!-- Bulk upload End -->
			
								
								
								
										Old Code By PS
 										<h2 class="red text-capitalize">
											<c:if test="${action == 'Add Video Content' }">
									        	Create a new Video Content
											</c:if>
									        
									        <c:if test="${action == 'Edit VideoContent' }">
									        	Edit Video Content
									        </c:if>
										</h2>
										<c:if test="${action == 'Edit VideoContent' }">
									       
										<div class="clearfix"></div>
											
											<div class="container-fluid row" style="color:black; padding-top:20px;">
											<div class=" col-md-5">
											<form:form  action="postVideoContents" method="post" modelAttribute="videoContent">
												<fieldset> 
													<form:hidden path="id" value="${videoContent.id }"/> 
													<div class="clearfix"></div>
													<div class="form-group">
														<form:input path="fileName" value="${videoContent.fileName}" required="required"  placeholder="File Name" style="color:black; padding:6px 12px; width:100%;"/>
													</div>
													<div class="form-group">
														<form:input path="keywords" value="${videoContent.keywords}" required="required"  placeholder="Add Keywords" style="color:black; padding:6px 12px; width:100%;"/>
													</div>
													<div class="form-group">
															<form:textarea path="description" id="description" maxlength="500" class="form-control" value='${videoContent.description}' placeholder="Add description for the Video Content..." cols="50"/>
													</div>
													<div class="form-group">
														<form:input path="subject" value="${videoContent.subject}" required="required"  placeholder="Add Subject" style="color:black; padding:6px 12px; width:100%;"/>
													</div>
													<div class="form-group">
														<form:input path="videoLink" value="${videoContent.videoLink}" required="required"  placeholder="Add Video Link" style="color:black; padding:6px 12px; width:100%;"/>
													</div>
													<div class="form-group">
														<form:select id="year" path="year" type="text"	placeholder="Year" class="form-control"   itemValue="${videoContent.year}">
															<form:option value="">Select Year</form:option>
															<form:option value="2015">2015</form:option>
															<form:option value="2016">2016</form:option>
															<form:option value="2017">2017</form:option>
															<form:option value="2018">2018</form:option>
															<form:option value="2019">2019</form:option>
															<form:option value="2020">2020</form:option> 
														</form:select>
													</div>
												
													<div class="form-group">
														<form:select id="month" path="month" type="text" placeholder="Month" class="form-control"  itemValue="${videoContent.month}">
															<form:option value="">Select Month</form:option>
															<form:option value="Apr">Apr</form:option>
															<form:option value="Jun">Jun</form:option>
															<form:option value="Sep">Sep</form:option>
															<form:option value="Dec">Dec</form:option>
														</form:select>
													</div>
													
													<div class="form-group"> 
														<label for="duration"> Video Duration </label>
														<form:input type="time" required="required" path="duration" value="${videoContent.duration}" id="endDateTime" style="color:black;"/>
													</div>
													<div class=" form-group controls">
														<button id="submit" name="submit" class="btn btn-sm btn-primary" formaction="postVideoContents" >${action}</button>
													</div>
									
												</fieldset>
											</form:form>
											</div>
											</div> 
											</c:if>
											
											
											<div class="container-fluid" style="text-align:center;">
												<c:if test="${not empty VideoContentsList}">
												<h2 align="center" class="red text-capitalize" style="padding-top: 20px;"> Video Contents : </h2>
												<table class="table table-striped table-hover tables" style="font-size:12px">
													<thead>
														<tr>
															<th>Sr No.</th>
															<th>Year</th>
															<th>Month</th>
															<th>Subject</th>
															<th>Video Title</th>
															<th>Applicable TO</th>
															<th>Added On</th> 
															<th >Watch </th>  
															<th >Edit </th> 
															<th >Delete </th>
														</tr>
													</thead>
												<tbody>
												<c:forEach var="VideoContentL" items="${VideoContentsList}" varStatus="status">
													<tr>
														<td>${status.count}</td>
														<td>${VideoContentL.year }</td>
														<td>${VideoContentL.month }</td>
														<td>${VideoContentL.subject }</td>
														<td>${VideoContentL.fileName }</td>
														<td>
												         	<a href="javascript:void(0)"
												         	   data-contentId="${ VideoContentL.id }" 
												         	   class="commonLinkbtn">
												         	   Common file for 
												         	   <c:out value="${ VideoContentL.countOfProgramsApplicableTo }" /> 
												         	   programs 
												         	</a>
												         </td> 
														<td> ${VideoContentL.addedOn } </td> 
														<td>
															<a href="/acads/admin/watchVideos?id=${VideoContentL.id }" title="Edit Video Content" class="">
																<b>
																	<i style="font-size:20px; padding-left:5px" class="fa fa-play-circle-o" aria-hidden="true"></i>
																</b>
															</a>
														</td>
														<td>
															<a href="/acads/admin/editVideoContents?id=${VideoContentL.id }" title="Edit Video Content" class="">
																<b>
																	<i style="font-size:20px; padding-left:5px" class="fa fa-pencil-square-o" aria-hidden="true"></i>
																</b>
															</a>
														</td>
														<td>
															<a href="/acads/admin/deleteVideoContents?id=${VideoContentL.id }" title="Delete Video Content" class="">
																<b>
																	<i style="font-size:20px; padding-left:5px" class="fa fa-trash-o" aria-hidden="true"></i>
																</b>
															</a>
														</td>
													</tr>
												</c:forEach>
												</tbody>
												</table>
												</c:if>
											</div>
									</div>
              				</div>
              		</div>
         
        <%}catch(Exception e){
        	  
        } %>
	</section>
		         <!-- Modal  -->
     <div id="myModal" class="modal fade" role="dialog">
  <div class="modal-dialog modal-lg ">

    <!-- Modal content-->
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal">&times;</button>
        <h4 class="modal-title">Common Program List</h4>
      </div>
      <div class="modal-body modalBody">
      	
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
      </div>
    </div>

  </div>
</div>
<!-- End of modal -->

	<jsp:include page="footer.jsp" />
	
        <!-- jQuery (necessary for Bootstrap's JavaScript plugins) --> 
<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />assets/js/jquery-1.11.3.min.js"></script> 
<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />assets/js/bootstrap.js"></script> 


<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/js/vendor/jquery-ui.min.js"></script>  
<script src="https://cdn.datatables.net/1.10.13/js/jquery.dataTables.min.js" ></script>
<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/js/vendor/dataTables.bootstrap.js"></script>
<script src="https://cdn.datatables.net/buttons/1.2.4/js/dataTables.buttons.min.js" ></script>
	<script>
		
	$('.tables').DataTable(
			
			{

				  language: {
				     processing: "https://thomas.vanhoutte.be/miniblog/wp-content/uploads/spinningwheel.gif"
				  },
				  processing: true,
        initComplete: function () {
            this.api().columns().every( function () {
                var column = this;
                var headerText = $(column.header()).text();
                console.log("header :"+headerText);
                if(headerText == "Subject")
                {
                   var select = $('<select style="width:100%;" class="form-control"><option value="">All</option></select>')
                    .appendTo( $(column.header()) )
                    .on( 'change', function () {
                        var val = $.fn.dataTable.util.escapeRegex(
                            $(this).val()
                        );
 
                        column
                            .search( val ? '^'+val+'$' : '', true, false )
                            .draw();
                    } );
 
                column.data().unique().sort().each( function ( d, j ) {
                    select.append( '<option value="'+d+'">'+d+'</option>' )
                } );
              }
                
                if(headerText == "Year")
                {
                   var select = $('<select style="width:100%;" class="form-control"><option value="">All</option></select>')
                    .appendTo( $(column.header()) )
                    .on( 'change', function () {
                        var val = $.fn.dataTable.util.escapeRegex(
                            $(this).val()
                        );
 
                        column
                            .search( val ? '^'+val+'$' : '', true, false )
                            .draw();
                    } );
 
                column.data().unique().sort().each( function ( d, j ) {
                    select.append( '<option value="'+d+'">'+d+'</option>' )
                } );
              }
                
                if(headerText == "Month")
                {
                   var select = $('<select style="width:100%;" class="form-control"><option value="">All</option></select>')
                    .appendTo( $(column.header()) )
                    .on( 'change', function () {
                        var val = $.fn.dataTable.util.escapeRegex(
                            $(this).val()
                        );
 
                        column
                            .search( val ? '^'+val+'$' : '', true, false )
                            .draw();
                    } );
 
                column.data().unique().sort().each( function ( d, j ) {
                    select.append( '<option value="'+d+'">'+d+'</option>' )
                } );
              }
                
            } );
        }
    } );

	 $('#loading').hide();
	
	</script>

<script type="text/javascript">

$(document).ready (function(){
	
	$('.commonLinkbtn').on('click',function(){
		let contentId = $(this).attr('data-contentId');
		let modalBody = "<center><h4>Loading...</h4></center>";
		let data = {
			'id':contentId
		};
		$.ajax({
			   type : "POST",
			   contentType : "application/json",
			   url : "/acads/admin/getProgramsListForCommonVideoContent",   
			   data : JSON.stringify(data),
			   success : function(data) {
				   modalBody = '<div class="table-responsive"> <table class="table"> <thead><td>Exam year</td><td>Exam month</td> <td>Consumer Type</td> <td>Program Structure</td> <td>Program</td> <td>Subject</td> <td>Action</td> </thead><tbody>';
				   for(let i=0;i < data.length;i++){
					   modalBody = modalBody + '<tr><td>'
					   							+ data[i].year 
					   							+'</td><td>'
					   							+ data[i].month 
					   							+'</td><td>'
					   							+ data[i].consumerType 
					   							+'</td><td>'
					   							+ data[i].programStructure 
					   							+'</td><td>'
					   							+ data[i].program 
					   							+'</td><td>'
					   							+ data[i].subject 
					   							+'</td><td> '
					   							+' <a href="admin/editSingleVideoContentFromCommonSetup?contentId='+ data[i].id 
					   									+'&consumerTypeId='+ data[i].consumerTypeId
					   									+'&programStructureId='+data[i].programStructureId
					   									+'&programId='+data[i].programId
					   									+'&consumerProgramStructureId='+data[i].consumerProgramStructureId
					   									+'"  title="Edit"><i class="fa fa-pencil-square-o fa-lg"></i></a> | '
					   							+' <a href="/admin/deleteSingleVideoContentFromCommonSetup?id='+ data[i].id
					   									+'&consumerProgramStructureId='+data[i].consumerProgramStructureId
					   									+'" onclick="return confirm(\'Are you sure you want to delete this record?\')"  title="Delete"><i class="fa fa-trash-o fa-lg"></i></a>  '
					   							
					   							+'</td></tr>';
				   }
				   
				   modalBody = modalBody + '<tbody></table></div>';
				   $('.modalBody').html(modalBody);
			   },
			   error : function(e) {
				   alert("Please Refresh The Page.")
			   }
		});
		$('.modalBody').html(modalBody);
		//modal-body
		$('#myModal').modal('show');
	});
	
});
	
</script>

</body>
</html>
 --%>
 
 <!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!-->

<%@page import="com.nmims.beans.PersonAcads"%>
<%@page import="com.nmims.beans.PageAcads"%>
<html class="no-js">
<!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<jsp:include page="jscss.jsp">
	<jsp:param value="Add Key Video Content" name="title" />
</jsp:include>

<body class="inside">

	<%@ include file="header.jsp"%>

	<section class="content-container">

		<div class="container-fluid customTheme">
		<div class="row"><legend>&nbsp;Add Video Content</legend></div>
		<div class="panel-body">
		<%@ include file="messages.jsp"%>
		
    <%try{ %>
    		<div class="sz-content">
				<div class="well">
					<c:if test="${sessionDetails != null}">
					<h2>${sessionDetails}</h2>
					<%@ include file="uploadVideoContentFilesErrorMessages.jsp"%>
				
					<form:form modelAttribute="fileBean" method="post" 	enctype="multipart/form-data" action="uploadVideoContentFiles">
					
					<form:hidden path="fileId" value="${sessionId}" />
					<%-- <h1>got sessionid ${sessionId}</h1> --%>
					<div class="panel-body">
					<div class="col-md-6 column">
						<div class="form-group">
							<form:label for="fileData" path="fileData">Select file</form:label>
							<form:input path="fileData" type="file" />
						</div>
						
			</div>
			
			
			<div class="col-md-12 column">
			<b>Format of Upload: </b><br>
			Year | Month | Subject | FileName | KeyWords | Description | Default Video  <br>
			<a href="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_ACADS_STATIC_RESOURCES')" />resources_2015/templates/MainVidoeStructure.xlsx" target="_blank">Download a Sample Template</a>
			
			</div>
			
			
			</div>
			<br>
			<div class="row">
				<div class="col-md-6 column">
					<button id="submit" name="submit" class="btn btn-large btn-primary"
						formaction="uploadVideoContentFiles">Upload</button>
				</div>

				
			</div>
			</form:form>
			</c:if>
			</div>
				
			<!-- Bulk upload Start -->
					<div class="jumbotron"> 
					<h2>Video Details Batch Upload</h2> 
				
					<form:form modelAttribute="fileBean" method="post" 	enctype="multipart/form-data" action="uploadVideoContentFilesBatch">
					 
					<div class="panel-body">
					<div class="col-md-6 column">
						<div class="form-group">
							<form:label for="fileData" path="fileData">Select file</form:label>
							<form:input path="fileData" type="file" />
				   </div>
						
			</div>
			
			
			<div class="col-md-12 column">
			<b>Format of Upload: </b><br>
			Year | Month | Subject | FileName | KeyWords | Description | Default Video  <br>
			<a href="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_ACADS_STATIC_RESOURCES')" />resources_2015/templates/MainVidoeStructureBatchUpload.xlsx" target="_blank">Download a Sample Template</a>
			
			</div>
			
			
			</div>
			<br>
			<div class="row">
				<div class="col-md-6 column">
					<button id="submit" name="submit" class="btn btn-large btn-primary"
						formaction="uploadVideoContentFilesBatch">Upload</button>
				</div>

				
			</div>
			</form:form> 
			</div>
			<!-- Bulk upload End -->
			
								
								
								
										<%-- Old Code By PS --%>
 										<h2 class="red text-capitalize">
											<c:if test="${action == 'Add Video Content' }">
									        	Create a new Video Content
											</c:if>
									        
									        <c:if test="${action == 'Edit VideoContent' }">
									        	Edit Video Content
									        </c:if>
										</h2>
										<c:if test="${action == 'Edit VideoContent' }">
									       
										<div class="clearfix"></div>
											
											<div class="container-fluid row" style="color:black; padding-top:20px;">
											<div class=" col-md-5">
											<form:form  action="postVideoContents" method="post" modelAttribute="videoContent">
												<fieldset> 
													<form:hidden path="id" value="${videoContent.id }"/> 
													<div class="clearfix"></div>
													<div class="form-group">
														<form:input path="fileName" value="${videoContent.fileName}" required="required"  placeholder="File Name" style="color:black; padding:6px 12px; width:100%;"/>
													</div>
													<div class="form-group">
														<form:input path="keywords" value="${videoContent.keywords}" required="required"  placeholder="Add Keywords" style="color:black; padding:6px 12px; width:100%;"/>
													</div>
													<div class="form-group">
															<form:textarea path="description" id="description" maxlength="500" class="form-control" value='${videoContent.description}' placeholder="Add description for the Video Content..." cols="50"/>
													</div>
													<div class="form-group">
														<form:input path="subject" value="${videoContent.subject}" required="required"  placeholder="Add Subject" style="color:black; padding:6px 12px; width:100%;"/>
													</div>
													<div class="form-group">
														<form:input path="videoLink" value="${videoContent.videoLink}" required="required"  placeholder="Add Video Link" style="color:black; padding:6px 12px; width:100%;"/>
													</div>
													<div class="form-group">
														<form:select id="year" path="year" type="text"	placeholder="Year" class="form-control"   itemValue="${videoContent.year}">
															<form:option value="">Select Year</form:option>
															<form:option value="2015">2015</form:option>
															<form:option value="2016">2016</form:option>
															<form:option value="2017">2017</form:option>
															<form:option value="2018">2018</form:option>
															<form:option value="2019">2019</form:option>
															<form:option value="2020">2020</form:option> 
															<form:option value="2021">2021</form:option>
															<form:option value="2022">2022</form:option> 
														</form:select>
													</div>
												
													<div class="form-group">
														<form:select id="month" path="month" type="text" placeholder="Month" class="form-control"  itemValue="${videoContent.month}">
															<form:option value="">Select Month</form:option>
															<form:option value="Apr">Apr</form:option>
															<form:option value="Jun">Jun</form:option>
															<form:option value="Sep">Sep</form:option>
															<form:option value="Dec">Dec</form:option>
														</form:select>
													</div>
													
													<div class="form-group"> 
														<label for="duration"> Video Duration </label>
														<form:input type="time" required="required" path="duration" value="${videoContent.duration}" id="endDateTime" style="color:black;"/>
													</div>
													<div class=" form-group controls">
														<button id="submit" name="submit" class="btn btn-sm btn-primary" formaction="postVideoContents" >${action}</button>
													</div>
									
												</fieldset>
											</form:form>
											</div>
											</div> 
											</c:if>
											
											
											<div class="container-fluid" style="text-align:center;">
												<c:if test="${not empty VideoContentsList}">
												<h2 align="center" class="red text-capitalize" style="padding-top: 20px;"> Video Contents : </h2>
												<table class="table table-striped table-hover tables" style="font-size:12px">
													<thead>
														<tr>
															<th>Sr No.</th>
															<th>Subject</th>
															<th>Video </th>
															<th>Description</th>
															<th>Keywords</th> <!-- 
															<th>Views</th> --> 
															<th>Added On</th> 
															<th >Watch </th>  
															<th >Edit </th> 
															<th >Delete </th>
														</tr>
													</thead>
												<tbody>
												<c:forEach var="VideoContentL" items="${VideoContentsList}" varStatus="status">
													<tr>
														<td>${status.count}</td>
														<td>${VideoContentL.subject }</td>
														<td>${VideoContentL.fileName }</td>
														<td>${VideoContentL.description }</td>
														<td>${VideoContentL.keywords }</td> <%-- 
														<td> ${VideoContentL.viewCount } </td> --%> 
														<td> ${VideoContentL.addedOn } </td> 
														<td>
															<a href="/acads/admin/watchVideos?id=${VideoContentL.id }" title="Edit Video Content" class="">
																<b>
																	<i style="font-size:20px; padding-left:5px" class="fa-regular fa-play-circle" aria-hidden="true"></i>
																</b>
															</a>
														</td>
														<td>
															<a href="/acads/admin/editVideoContents?id=${VideoContentL.id }" title="Edit Video Content" class="">
																<b>
																	<i style="font-size:20px; padding-left:5px" class="fa-solid fa-pen-to-square" aria-hidden="true"></i>
																</b>
															</a>
														</td>
														<td>
															<a href="/acads/admin/deleteVideoContents?id=${VideoContentL.id }" title="Delete Video Content" class="">
																<b>
																	<i style="font-size:20px; padding-left:5px" class="fa-regular fa-trash-can" aria-hidden="true"></i>
																</b>
															</a>
														</td>
													</tr>
												</c:forEach>
												</tbody>
												</table>
												</c:if>
											</div>
									</div>
              				</div>
              		</div>
         
        <%}catch(Exception e){
        	  
        } %>
	</section>

	<jsp:include page="footer.jsp" />
	
        <!-- jQuery (necessary for Bootstrap's JavaScript plugins) --> 
<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_ACADS_STATIC_RESOURCES')" />assets/js/jquery-1.11.3.min.js"></script> 
<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_ACADS_STATIC_RESOURCES')" />assets/js/bootstrap.js"></script> 


<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_ACADS_STATIC_RESOURCES')" />resources_2015/js/vendor/jquery-ui.min.js"></script>  
<script src="https://cdn.datatables.net/1.10.13/js/jquery.dataTables.min.js" ></script>
<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_ACADS_STATIC_RESOURCES')" />resources_2015/js/vendor/dataTables.bootstrap.js"></script>
<script src="https://cdn.datatables.net/buttons/1.2.4/js/dataTables.buttons.min.js" ></script>
	<script>
		
	$('.tables').DataTable( {
        initComplete: function () {
            this.api().columns().every( function () {
                var column = this;
                var headerText = $(column.header()).text();
                console.log("header :"+headerText);
                if(headerText == "Subject")
                {
                   var select = $('<select style="width:100%;" class="form-control"><option value="">All</option></select>')
                    .appendTo( $(column.header()) )
                    .on( 'change', function () {
                        var val = $.fn.dataTable.util.escapeRegex(
                            $(this).val()
                        );
 
                        column
                            .search( val ? '^'+val+'$' : '', true, false )
                            .draw();
                    } );
 
                column.data().unique().sort().each( function ( d, j ) {
                    select.append( '<option value="'+d+'">'+d+'</option>' )
                } );
              }
            } );
        }
    } );
	
	</script>

</body>
</html>
 