<%@page import="java.util.ArrayList"%>
<%@page import="com.nmims.beans.*"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%
	ArrayList<ContentStudentPortalBean> resourceContentList = (ArrayList<ContentStudentPortalBean>)request.getSession().getAttribute("resourceContent");
	int noOfResources = resourceContentList != null ? resourceContentList.size() : 0;
	
%>
<style>
	.border {
	border: 1px solid black;
	color: #d2232a;
}
</style>
<div class="course-sessions-m-wrapper">
	<div class="panel-courses-page">
		<% if (noOfResources == 0) { %>
			<div class="no-data-wrapper nodata-wrapper">
				<h4 style="text-align: center">
					<i class="fa fa-exclamation-circle" style="font-size: 19px" aria-hidden="true"></i>
					 Content is not available. Please try again !!!
				</h4>
			</div>
		<% } else { %>
		<div class="row data-content panel-body">
			<h2>Content Resources</h2><br><br>
			<div class="col-md-12 " style="padding-bottom: 20px;">
				<div style="font-size: 12px;margin-bottom:1rem;">
					<div class="panel-body">
						<table class="table table-striped" id="moduleContentResources">
							<thead>
								<tr>
									<th>Sr No.</th>
									<th>Name</th>
									<th>Description</th>
									<th>Type</th>
									<th>Action</th>
								</tr>
							</thead>
							<tbody>
								<c:forEach items="<%=resourceContentList %>" var="bean" varStatus="status">
									<tr style="display: table-row;">
										<td><c:out value="${status.count }"></c:out></td>
										<td><c:out value="${bean.name }"></c:out></td>
										<td><c:out value="${bean.description }"></c:out></td>
										<td>
											<c:choose>
												<c:when
													test="${fn:endsWith(bean.previewPath, '.pdf') || fn:endsWith(bean.previewPath, '.PDF') || fn:endsWith(bean.previewPath, '.Pdf')}">
													<c:out value="PDF"></c:out>
												</c:when>
												<c:when
													test="${fn:endsWith(bean.previewPath, '.doc') || fn:endsWith(bean.previewPath, '.docx')}">
													<c:out value="Word"></c:out>
												</c:when>
												<c:when
													test="${fn:endsWith(bean.previewPath, '.ppt') || fn:endsWith(bean.previewPath, '.pptx')}">
													<c:out value="PPT"></c:out>
												</c:when>
											</c:choose>
										</td>
										
										<td>
											<c:if test="${not empty bean.previewPath}">
												<c:if test="${!fn:endsWith(bean.previewPath, '.pdf') && !fn:endsWith(bean.previewPath, '.PDF')
														&& !fn:endsWith(bean.previewPath, '.Pdf')}">
														
													<a href="#" onClick="window.open('<spring:eval expression="@propertyConfigurer.getProperty('CONTENT_PREVIEW_PATH')" />${bean.previewPath}')">
													 Download
													</a>
												</c:if>
											</c:if>
											
											<c:if test="${not empty bean.documentPath}">
												<c:if test="${!fn:endsWith(bean.documentPath, '.pdf') && !fn:endsWith(bean.documentPath, '.PDF') && !fn:endsWith(bean.documentPath, '.Pdf')}">
													<a href="/${bean.documentPath}" target="_blank" style="margin: 0px; padding: 0px; width: 21px; bottom: 50px; left: 90%;">
													 Download 
													</a>
												</c:if>
											</c:if>
											
											<c:if test="${fn:endsWith(bean.previewPath, '.pdf') || fn:endsWith(bean.previewPath, '.PDF') || fn:endsWith(bean.previewPath, '.Pdf')}">
												<c:url value="acads/previewContent" var="previewContentLink">
													<c:param name="previewPath" value="${bean.previewPath}" />
													<c:param name="name" value="${bean.name}" />
													<c:param name="type" value="PDF" />
												</c:url>
												<a href="/${previewContentLink}" target="_blank">View</a>
											</c:if>
											
											<c:if test="${not empty bean.webFileurl}">
												<c:if
													test="${bean.urlType == 'View' || bean.urlType == '' || empty bean.urlType	}">
													<a href="${bean.webFileurl}" target="_blank">View</a>
												</c:if>
						
												<c:if test="${bean.urlType == 'Download'}">
													<a href="${bean.webFileurl}" target="_blank"> Download</a>
												</c:if>
											</c:if>
											
										</td>
									</tr>
								</c:forEach>
							</tbody>
						</table>
					</div>
				</div>
			</div>
		</div>
		
		<% } %>
	
	</div>
</div>

<script>
	$(document).ready( function () {

		$('#moduleContentResources').DataTable( {
		destroy: true,
		initComplete: function () {
            this.api().columns().every( function () {
                var column = this;
                var headerText = $(column.header()).text();
                console.log("header :"+headerText);
                if(headerText == "Type")
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
	         });
			}
		} );
	});
	
</script>