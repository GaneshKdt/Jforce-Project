<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!-->

<%@page import="com.nmims.beans.PersonAcads"%>
<%@page import="com.nmims.beans.Page"%>
<html class="no-js">
<!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<jsp:include page="jscss.jsp">
	<jsp:param value="Add Module Content" name="title" />
</jsp:include>

<body class="inside">

	<%@ include file="header.jsp"%>

	<section class="content-container">

		<div class="container-fluid customTheme">
		<div class="row"><legend>&nbsp;Add Module Content</legend></div>
		<div class="panel-body">
		<%@ include file="messages.jsp"%>
		
    <%try{ %>
    		<div class="sz-content">
				<div class="well"> 
					
					<form:form modelAttribute="fileBean" method="post" 	enctype="multipart/form-data" action="uploadLearningResourcesFiles">
					
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
						formaction="uploadLearningResourcesFiles">Upload</button>
				</div>

				
			</div>
			</form:form>
			</div>

											
											
											<div class="container-fluid" style="text-align:center;">
												<c:if test="${not empty moduleContentsList}">
												<h2 align="center" class="red text-capitalize" style="padding-top: 20px;"> Module Contents : </h2>
												<table class="table table-striped table-hover tables" style="font-size:12px">
													<thead>
														<tr>
															<th>Sr No.</th>
															<th>Subject</th>
															<th>Module </th>
															<th>Description</th>
															<th>Due Date</th> 
															<th >Edit </th> 
															<th >Delete </th>
														</tr>
													</thead>
												<tbody>
												<c:forEach var="moduleContentTemp" items="${moduleContentsList}" varStatus="status">
													<tr>
														<td>${status.count}</td>
														<td>${moduleContentTemp.subject }</td>
														<td>${moduleContentTemp.moduleName }</td>
														<td>${moduleContentTemp.description }</td>
														<td>${moduleContentTemp.dueDate }</td> 
														
														<td>
															<a href="/acads/admin/editModuleContents?id=${moduleContentTemp.id }" title="Edit Module Content" class="">
																<b>
																	<i style="font-size:20px; padding-left:5px" class="fa fa-pencil-square-o" aria-hidden="true"></i>
																</b>
															</a>
														</td>
														<td>
															<a href="/acads/admin/deleteModuleContents?id=${moduleContentTemp.id }" title="Delete Module Content" class="">
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
        	e.printStackTrace();
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
