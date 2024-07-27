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
	<jsp:param value="Add Video Content For Session Module" name="title" />
</jsp:include>

<body class="inside">

	<%@ include file="header.jsp"%>

	<section class="content-container">

		<div class="container-fluid customTheme">
		
		
	<ul class="breadcrumb">
	<li><a href="/">Home</a></li>
		<li><a href="/acads/admin/manageSessionPlan">Manage Session Plans</a></li>
	<li><a href="/acads/admin/editSessionPlan?id=${bean.sessionPlanId}">Edit Session Plan</a></li>
	<li><a href="/acads/admin/manageSessionPlanModuleById?id=${bean.id}&sessionId=${param.sessionId}&batchId=${param.batchId}&action=upload">Manage Session Plan Module</a></li>
	<li class="active" >Add Video Content</li>
	</ul>
		
		<div class="row"><legend>&nbsp;Add Video Content For Session Plan Module </legend></div>
		<div class="panel-body">

		
		<%@ include file="messages.jsp"%>
		
    <%try{ %>
    		<h4> Session Date: ${sessionDetailsForSessionPlanModule.date} </h4>
			<h4> Session Time: ${sessionDetailsForSessionPlanModule.startTime} </h4>
			<h4> Faculty Name: ${sessionDetailsForSessionPlanModule.facultyName} </h4>
			<h4> Batch Name: ${sessionDetailsForSessionPlanModule.batchName} </h4>
    		<div class="sz-content">
				<div class="well">
					<c:if test="${sessionDetails != null}">
					<h2>${sessionDetails}</h2>
					
					<form:form modelAttribute="fileBean" method="post" 	enctype="multipart/form-data" action="uploadVideoContentForSessionPlanModule">
					
					<form:hidden path="fileId" value="${bean.id}" />
					<div class="panel-body">
					<div class="col-md-6 column">
						<div class="form-group">
							<form:label for="fileData" path="fileData">Select file</form:label>
							<form:input path="fileData" type="file" />
						</div>
						
			</div>			
			
			<div class="col-md-12 column">
			<b>Format of Upload: </b><br>
			<a href="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_ACADS_STATIC_RESOURCES')" />resources_2015/templates/MainVidoeStructure.xlsx" target="_blank">Download a Sample Template</a>
			
			</div>
			
			
			</div>
			<br>
			<div class="row">
				<div class="col-md-6 column">
					<button id="submit" name="submit" class="btn btn-large btn-primary"
						formaction="uploadVideoContentForSessionPlanModule">Upload</button>
				</div>

				
			</div>
			</form:form>
			</c:if>
			</div>
				
								
								
											
											
											<div class="container-fluid" style="text-align:center;">
												<c:if test="${not empty videoContentsList}">
												<h2 align="center" class="red text-capitalize" style="padding-top: 20px;"> Video Contents : </h2>
												<table class="table table-striped table-hover tables" style="font-size:12px">
													<thead>
														<tr>
															<th>Sr No.</th>
															<th>Subject</th>
															<th>Video </th>
															<th>Description</th> <!-- 
															<th>Views</th> --> 
															<th>Added On</th> 
															<th >Watch </th>  
															<th >Edit </th> 
															<th >Delete </th>
														</tr>
													</thead>
												<tbody>
												<c:forEach var="VideoContentL" items="${videoContentsList}" varStatus="status">
													<tr>
														<td>${status.count}</td>
														<td>${VideoContentL.subject }</td>
														<td>${VideoContentL.fileName }</td>
														<td>${VideoContentL.description }</td> <%-- 
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
