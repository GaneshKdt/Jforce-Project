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
	<jsp:param value="Map Module Video Content" name="title" />
</jsp:include>

<body class="inside">

	<%@ include file="header.jsp"%>

	<section class="content-container">

		<div class="container-fluid customTheme">
			<div class="row">
				<legend>&nbsp;Map Module To Video Topic</legend>
			</div>
			<div class="panel-body">
				<%@ include file="messages.jsp"%>

				<%try{ %>
				<div class="sz-content">
					<div class="container-fluid" style="text-align: center;">
						<c:if test="${not empty videoTopicsList}">
							<h2 align="center" class="red text-capitalize"
								style="padding-top: 20px;"> Unmapped videos :</h2>
							<table class="table table-striped table-hover tables"
								style="font-size: 12px">
								<thead>
									<tr>
										<th>Sr No.</th> 
										<th>Subject</th>
										<th>Video Title</th>  
										<th>Map</th><!-- 
										<th>Delete</th> -->
									</tr>
								</thead>
								<tbody>
									<c:forEach var="video"
										items="${unmapped}" varStatus="status">
										<tr>
											<td>${status.count}</td>
											<td>${video.subject }</td>
											<td>
												<!-- code to watch vidoe start -->
												<b>${video.fileName}</b> &nbsp;&nbsp; <a  data-toggle="collapse" data-target="#video${status.count}">Watch Video</a>

												<div id="video${status.count}" class="collapse">
													<div id="embed_container" class="embed-container">
																<iframe height="450vh" width="100%"
																	src="${video.videoLink}" id="video"
																	frameborder='0' webkitAllowFullScreen
																	mozallowfullscreen allowFullScreen></iframe>
															</div>
												</div>


											</td>

											<td><a href="/acads/admin/mapModuleVideo?moduleId=${moduleContent.id}&videoId=${video.id}"
												title="Add Module Video Mapping" class=""> <b>
													Add 
												</b>
											</a></td>
											<%-- <td><a
												href="/acads/deleteModuleVideoMap?moduleId=${moduleContent.id}&videoId=${video.id}"
												title="Delete Module Content" class=""> <b> <i
														style="font-size: 20px; padding-left: 5px"
														class="fa fa-trash-o" aria-hidden="true"></i>
												</b>
											</a></td> --%>
										</tr>
									</c:forEach>
								</tbody>
							</table>
						</c:if>
					</div>
					
					<div class="container-fluid" style="text-align: center;">
						<c:if test="${not empty videoTopicsList}">
							<h2 align="center" class="red text-capitalize"
								style="padding-top: 20px;"> Mapped videos :</h2>
							<table class="table table-striped table-hover tables"
								style="font-size: 12px">
								<thead>
									<tr>
										<th>Sr No.</th> 
										<th>Subject</th>
										<th>Video Title</th> <!--  
										<th>Map</th> -->
										<th>Delete</th>
									</tr>
								</thead>
								<tbody>
									<c:forEach var="videot"
										items="${mapped}" varStatus="status">
										<tr>
											<td>${status.count}</td>
											<td>${videot.subject }</td>
											<td>
												<!-- code to watch vidoe start -->
												<b>${videot.fileName}</b> &nbsp;&nbsp; <a  data-toggle="collapse" data-target="#videot${status.count}">Watch Video</a>

												<div id="videot${status.count}" class="collapse">
													<div id="embed_container" class="embed-container">
																<iframe height="450vh" width="100%"
																	src="${videot.videoLink}" id="video"
																	frameborder='0' webkitAllowFullScreen
																	mozallowfullscreen allowFullScreen></iframe>
															</div>
												</div>


											</td>
											<%-- 
											<td><a href="/acads/mapModuleVideo?moduleId=${moduleContent.id}&videoId=${videot.id}"
												title="Add Module Video Mapping" class=""> <b>
													Add 
												</b>
											</a></td> --%>
											<td><a
												href="/acads/admin/deleteModuleVideoMap?moduleId=${moduleContent.id}&videoId=${videot.id}"
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

		<%}catch(Exception e){
        	  
        } %>
	</section>

	<jsp:include page="footer.jsp" />

	<!-- jQuery (necessary for Bootstrap's JavaScript plugins) -->
	<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_ACADS_STATIC_RESOURCES')" />assets/js/jquery-1.11.3.min.js"></script>
	<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_ACADS_STATIC_RESOURCES')" />assets/js/bootstrap.js"></script>


	<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_ACADS_STATIC_RESOURCES')" />resources_2015/js/vendor/jquery-ui.min.js"></script>
	<script src="https://cdn.datatables.net/1.10.13/js/jquery.dataTables.min.js"></script>
	<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_ACADS_STATIC_RESOURCES')" />resources_2015/js/vendor/dataTables.bootstrap.js"></script>
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
