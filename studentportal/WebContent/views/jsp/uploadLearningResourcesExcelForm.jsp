<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!-->


<%@page import="com.nmims.beans.PageStudentPortal"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.nmims.beans.ModuleContentStudentPortalBean"%>
<%@page import="com.nmims.beans.FileStudentPortalBean"%>
<html class="no-js">
<!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<jsp:include page="jscss.jsp">
	<jsp:param value="Add Module Content" name="title" />
</jsp:include>

<body class="inside">

	<%@ include file="header.jsp"%>

	<section class="content-container">

		<div class="container-fluid customTheme">
			<div class="row">
				<legend>&nbsp;Add Module Content</legend>
			</div>
			<div class="panel-body">
				<%@ include file="messages.jsp"%>

				<%try{ %>
				<div class="sz-content">
					<div class="well">

						<form:form modelAttribute="fileBean" method="post"
							enctype="multipart/form-data"
							action="uploadLearningResourcesFiles">

							<%-- <h1>got sessionid ${sessionId}</h1> --%>
							<div class="panel-body">
								<div class="col-md-4 column">
									<div class="form-group">
										<form:label for="fileData" path="fileData">Select file</form:label>
										<form:input path="fileData" type="file" />
									</div>

								</div>


								<div class="col-md-4 column">
									<b>Format of Upload: </b><br>
									SUBJECT | MODULE_NAME | DESCRIPTION <br>
									 <a href="${pageContext.request.contextPath}/resources_2015/templates/Add_Module_Content.xlsx"
										target="_blank">Download a Sample Template</a>

								</div>



							</div>
							<br>
							<div class="row">
								<div class="col-md-6 column">
									<button id="submit" name="submit"
										class="btn btn-large btn-primary"
										formaction="uploadLearningResourcesFiles">Upload</button>
								</div>


							</div>
						</form:form>

						<div class="well">
							<div class="row">
								<div class="panel-group" id="accordion">
									<div class="panel panel-default">
										<div class="panel-heading">
											<h4 class="panel-title">
												<a data-toggle="collapse" data-parent="#accordion"
													href="#collapse1"> Upload Files For Download Center
													Section </a>
											</h4>
										</div>
										<div id="collapse1" class="panel-collapse collapse">
											<div class="panel-body">
												<div class="container">

													<form:form modelAttribute="fileBean" method="post"
														enctype="multipart/form-data"
														action="uploadDownloadModulesPdf">

														<div class="panel-body">

															<div class="col-sm-4">
																<div class="form-group" style="overflow: visible;">
																	<form:label for="subject" path="subject" id="subject">Select subject </form:label>
																	<form:select id="subject" path="subject"
																		class="combobox form-control" itemValue=""
																		required="required">
																		<form:option value="">Type OR Select Subject</form:option>
																		<form:options items="${subjectList}" />
																	</form:select>
																</div>



																<div class="form-group">
																	<form:label for="fileData" path="fileData"
																		id="fileData">Select file</form:label>
																	<form:input path="fileData" type="file"
																		required="required" />
																</div>
															</div>

															<div class="col-sm-4">

																<div class="form-group">
																	<form:label for="year" path="year" id="year">Select Year</form:label>
																	<form:select id="year" path="year" type="text"
																		placeholder="year" class="form-control"
																		required="required">

																		<form:option value="">Select Year</form:option>
																		<form:option value="2014">2014</form:option>
																		<form:option value="2015">2015</form:option>
																		<form:option value="2016">2016</form:option>
																		<form:option value="2017">2017</form:option>
																		<form:option value="2018">2018</form:option>
																		<form:option value="2019">2019</form:option>
																		<form:option value="2020">2020</form:option>
																		<form:option value="2021">2021</form:option>
																	</form:select>

																</div>
																<div class="form-group">
																	<form:label for="month" path="month" id="month">Select Month</form:label>
																	<form:select id="month" path="month" type="text"
																		placeholder="month" class="form-control"
																		required="required">
																		<form:option value="">Select Month</form:option>
																		<form:option value="Jan">Jan</form:option>
																		<form:option value="Jul">Jul</form:option>
																	</form:select>
																</div>
															</div>
															<div class="col-sm-4">
																<div class="form-group">
																	<form:label for="description" path="description"
																		id="description">Description</form:label>
																	<form:input path="description" type="text"
																		required="required" />
																</div>
															</div>
														</div>
														<br>

														<div class="row">
															<div class="col-md-6 column">
																<button id="submit" name="submit"
																	class="btn btn-large btn-primary"
																	formaction="uploadDownloadModulesPdf">Upload</button>
															</div>

														</div>
													</form:form>

												</div>
											</div>
										</div>
									</div>

									<div class="panel panel-default">
										<div class="panel-heading">
											<h4 class="panel-title">
												<a data-toggle="collapse" data-parent="#accordion"
													href="#collapse2"> Edit Download Center Contents </a>
											</h4>
										</div>
										<div id="collapse2" class="panel-collapse collapse">
											<div class="panel-body">
												<div class="container">

													<c:if test="${empty downloadCenterContentsList}">
														<h5>No Content</h5>
													</c:if>
													<c:if test="${not empty downloadCenterContentsList}">
														<table class="table table-striped table-hover tables"
															style="font-size: 12px">
															<thead>
																<tr>
																	<th>Sr No.</th>
																	<th>Subject</th>
																	<th>File</th>
																	<th>Description</th>
																	<!-- 
															<th >Edit </th>  -->
																	<th>Delete</th>
																</tr>
															</thead>
															<tbody>
																<c:forEach var="dc"
																	items="${downloadCenterContentsList}"
																	varStatus="status">
																	<tr>
																		<td>${status.count}</td>
																		<td>${dc.subject }</td>
																		<td>${dc.fileName }</td>
																		<td>${dc.description }</td>

																		<%-- <td>
															<a href="/studentportal/editModuleContents?id=${moduleContentTemp.id }" title="Edit Module Content" class="">
																<b>
																	<i style="font-size:20px; padding-left:5px" class="fa fa-pencil-square-o" aria-hidden="true"></i>
																</b>
															</a>
														</td> --%>
																		<td><a
																			href="/studentportal/deleteDownloadCenterContents?id=${dc.id }"
																			title="Delete Module Content" class=""> <b> <i
																					style="font-size: 20px; padding-left: 5px"
																					class="fa-regular fa-trash-can" aria-hidden="true"></i>
																			</b>
																		</a></td>
																	</tr>
																</c:forEach>
															</tbody>
														</table>
													</c:if>

												</div>
											</div>
										</div>
									</div>

									<div class="panel panel-default">
										<div class="panel-heading">
											<h4 class="panel-title">
												<a data-toggle="collapse" data-parent="#accordion"
													href="#collapse3"> Edit Module Contents </a>
											</h4>
										</div>
										<div id="collapse3" class="panel-collapse collapse">
											<div class="panel-body">

												<div class="container-fluid" style="text-align: center;">
													<c:if test="${not empty moduleContentsList}">
														<h4>No Content</h4>
													</c:if>
													<c:if test="${not empty moduleContentsList}">
														<table class="table table-striped table-hover tables"
															style="font-size: 12px">
															<thead>
																<tr>
																	<th>Sr No.</th>
																	<th>Subject</th>
																	<th>Module</th>
																	<th>Description</th>
																	<th>Due Date</th>
																	<th>Edit</th>
																	<th>Delete</th>
																</tr>
															</thead>
															<tbody>
																<c:forEach var="moduleContentTemp"
																	items="${moduleContentsList}" varStatus="status">
																	<tr>
																		<td>${status.count}</td>
																		<td>${moduleContentTemp.subject }</td>
																		<td>${moduleContentTemp.moduleName }</td>
																		<td>${moduleContentTemp.description }</td>
																		<td>${moduleContentTemp.dueDate }</td>

																		<td><a
																			href="/studentportal/editModuleContents?id=${moduleContentTemp.id }"
																			title="Edit Module Content" class=""> <b> <i
																					style="font-size: 20px; padding-left: 5px"
																					class="fa-solid fa-pen-to-square" aria-hidden="true"></i>
																			</b>
																		</a></td>
																		<td><a
																			href="/studentportal/deleteModuleContents?id=${moduleContentTemp.id }"
																			title="Delete Module Content" class=""> <b> <i
																					style="font-size: 20px; padding-left: 5px"
																					class="fa-regular fa-trash-can" aria-hidden="true"></i>
																			</b>
																		</a></td>
																	</tr>
																</c:forEach>
															</tbody>
														</table>
													</c:if>
												</div>

											</div>
										</div>
									</div>

									<div class="panel panel-default">
										<div class="panel-heading">
											<h4 class="panel-title">
												<a data-toggle="collapse" data-parent="#accordion"
													href="#collapse4"> Upload Excel For Module And Video
													Topic Mapping </a>
											</h4>
										</div>
										<div id="collapse4" class="panel-collapse collapse">
											<div class="panel-body">
												<div class="container">

													<form:form modelAttribute="fileBean" method="post"
														enctype="multipart/form-data"
														action="uploadModuleVideoMapXl">

														<div class="panel-body">

															<div class="col-sm-6">
																<div class="form-group" style="overflow: visible;">
																	<form:label for="subject" path="subject" id="subject">Select Subject</form:label>
																	<form:select id="subject" path="subject"
																		class="combobox form-control" itemValue="">
																		<form:option value="">Type OR Select Subject</form:option>
																		<form:options items="${subjectList}" />
																	</form:select>
																</div>

																<div class="form-group">
																	<form:label for="fileData" path="fileData"
																		id="fileData">Select file</form:label>
																	<form:input path="fileData" type="file" />
																</div>



															</div>
														</div>
														<br>

														<div class="row">
															<div class="col-md-6 column">
																<button id="submit" name="submit"
																	class="btn btn-large btn-primary"
																	formaction="uploadModuleVideoMapXl">Upload</button>
															</div>

														</div>
													</form:form>

												</div>
											</div>
										</div>
									</div>




								</div>

							</div>


						</div>
					</div>







				</div>
			</div>
		</div>

		<%}catch(Exception e){} %>
	</section>

	<jsp:include page="footer.jsp" />

	<!-- jQuery (necessary for Bootstrap's JavaScript plugins) -->
	<script src="${pageContext.request.contextPath}/assets/js/jquery-1.11.3.min.js"></script>
	<script src="${pageContext.request.contextPath}/assets/js/bootstrap.js"></script>

	<script src="${pageContext.request.contextPath}/resources_2015/js/vendor/jquery-ui.min.js"></script>
	<script src="https://cdn.datatables.net/1.10.13/js/jquery.dataTables.min.js"></script>
	<script src="${pageContext.request.contextPath}/resources_2015/js/vendor/dataTables.bootstrap.js"></script>
	<script src="https://cdn.datatables.net/buttons/1.2.4/js/dataTables.buttons.min.js"></script>
	
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