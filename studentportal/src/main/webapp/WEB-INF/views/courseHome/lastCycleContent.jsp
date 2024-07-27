<%@page import="com.nmims.controllers.BaseController"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.HashMap"%>
<%@page import="com.nmims.beans.ContentStudentPortalBean"%>
<%@page import="com.nmims.beans.ModuleContentStudentPortalBean"%>
<%@page import="com.nmims.beans.StudentStudentPortalBean"%>
<%@page import="com.nmims.beans.VideoContentStudentPortalBean"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<%
	ArrayList<ContentStudentPortalBean> course = (ArrayList<ContentStudentPortalBean>) session.getAttribute("contentLastCycleList");
	BaseController lcCon = new BaseController();
	String link = "";	
	if(lcCon.checkLead(request, response))
		link = "disable";
	int courseSize = course.size();
	String userId=(String)request.getSession().getAttribute("userId");	
	String hideShow = "";
	String isLead = session.getAttribute("isLoginAsLead").toString();
	System.out.println("isLead: "+isLead);
	if(isLead == "true"){
		hideShow = "hideShow('show')";
	}
%>

<style>
	.disable{
		pointer-events: none !important;
        cursor: default;
        color: gray;
	}
</style>

<table class="table table-striped " id="lastCycleContentTable">
	<thead>
		<tr>
			<th>Sr No.</th>
			<th>Name</th>
			<th>Description</th>
			<th>Year-Month</th>
			<th>Type</th>
			<th>Bookmark</th>
			<th>Action</th>
		</tr>
	</thead>

	<tbody>

		<%
			try {
		%>
		<c:forEach var="contentFile" items="${contentLastCycleList}"
			varStatus="status">
			<tr style="display: table-row;">
				<td><c:out value="${status.count}" /></td>
				<td><c:out value="${contentFile.name}" /></td>
				<td><c:out value="${contentFile.description}" /> <c:set
						var="string1" value="${contentFile.description}" /> <c:set
						var="string2" value="${fn:toUpperCase(string1)}" /> <c:set
						var="string3" value="${fn:toUpperCase(string1)}" /> <c:if
						test="${fn:substring(string2,0,4) eq 'TO V'}"> 
										   &nbsp;
										   <a
							href="https://akamaicdn.webex.com/client/WBXclient-32.11.0-388/atrecply.msi">Windows</a> &nbsp;
										   <a
							href="https://akamaicdn.webex.com/client/WBXclient-32.11.0-388/mac/intel/webexplayer_intel.dmg">Mac
							OSX</a>
					</c:if></td>
				<td><c:out value="${contentFile.year}-${contentFile.month}" /></td>
				<td>
					<c:choose>
						<c:when
							test="${fn:endsWith(contentFile.previewPath, '.pdf') || fn:endsWith(contentFile.previewPath, '.PDF') || fn:endsWith(contentFile.previewPath, '.Pdf')}">
							<c:out value="PDF"></c:out>
						</c:when>
						<c:when
							test="${fn:endsWith(contentFile.previewPath, '.doc') || fn:endsWith(contentFile.previewPath, '.docx') || 
							fn:endsWith(contentFile.previewPath, '.DOC') || fn:endsWith(contentFile.previewPath, '.DOCX')}">
							<c:out value="Word"></c:out>
						</c:when>
						<c:when
							test="${fn:endsWith(contentFile.previewPath, '.ppt') || fn:endsWith(contentFile.previewPath, '.pptx') ||
							fn:endsWith(contentFile.previewPath, '.PPT') || fn:endsWith(contentFile.previewPath, '.PPTX')}">
							<c:out value="PPT"></c:out>
						</c:when>
						<c:when
							test="${fn:endsWith(contentFile.previewPath, '.xlsx') || fn:endsWith(contentFile.previewPath, '.xls') ||
									fn:endsWith(contentFile.previewPath, '.XLSX') || fn:endsWith(contentFile.previewPath, '.XLS')}">
							<c:out value="Excel"></c:out>
						</c:when>
					</c:choose>
				</td>
				<c:if test='${contentFile.bookmarked eq "Y"}'>
					<td class="bookmark content" data-selected="yes" id="${contentFile.id}"><i class="fa fa-bookmark" style="color: #fabc3d;"></i></td>
				</c:if>
				<c:if test='${contentFile.bookmarked eq "N" or empty contentFile.bookmarked }'>
					<td class="bookmark content" data-selected="no" id="${contentFile.id}"><i class="fa fa-bookmark-o" onClick = "<%= hideShow %>"></i></td>
				</c:if>
				<td>
				<c:if test="${fn:endsWith(contentFile.previewPath, '.pdf') || fn:endsWith(contentFile.previewPath, '.PDF')
										|| fn:endsWith(contentFile.previewPath, '.Pdf')}">
						
						<c:url value="acads/previewContent" var="previewContentLink">
							<c:param name="previewPath" value="${contentFile.previewPath}" />
							<c:param name="name" value="${contentFile.name}" />
							<c:param name="type" value="PDF" />
						</c:url>
						<%--      <a href="/acads/previewContent?previewPath=${contentFile.previewPath}&name=${contentFile.name}" target="_blank">View</a>
										 --%>
						<a class="<%= link %>" href="/${previewContentLink}" target="_blank">View</a> &nbsp;

					</c:if>
<!-- Commented as ppt can only viewable					 -->
<!-- 
					<c:if test="${!fn:endsWith(contentFile.previewPath, '.pdf') && !fn:endsWith(contentFile.previewPath, '.PDF')
 															&& !fn:endsWith(contentFile.previewPath, '.Pdf')}"> 
							<c:url value="acads/previewContent" var="previewContentLink">
								<c:param name="previewPath" value="${contentFile.previewPath}" />
								<c:param name="name" value="${contentFile.name}" />
							</c:url>								
							<a class="<%= link %>" href="/${previewContentLink}" target="_blank">View</a> &nbsp; / &nbsp;								
					</c:if>	
 -->
						<c:if test="${not empty contentFile.previewPath}">
							<c:if test="${!fn:endsWith(contentFile.previewPath, '.pdf') && !fn:endsWith(contentFile.previewPath, '.PDF')
															&& !fn:endsWith(contentFile.previewPath, '.Pdf')}">
							<a class="<%= link %>" href="#"
								onClick="window.open('<spring:eval expression="@propertyConfigurer.getProperty('CONTENT_PREVIEW_PATH')" />${contentFile.previewPath}')">
								Download</a>
							</c:if>
						</c:if>
						 <c:if test="${not empty contentFile.documentPath}">
						 
							<a class="<%= link %>" href="/${contentFile.documentPath}" target="_blank"
								style="margin: 0px; padding: 0px; width: 21px; bottom: 50px; left: 90%;">
								Download </a>
						</c:if>  
					<c:if test="${not empty contentFile.webFileurl}">
						<c:if test="${contentFile.urlType == 'View' || contentFile.urlType == '' || empty contentFile.urlType	}">
							<a class="<%= link %>" href="${contentFile.webFileurl}" target="_blank">View</a> 
						</c:if>

						<c:if test="${contentFile.urlType == 'Download'}">
							<a class="<%= link %>" href="${contentFile.webFileurl}" target="_blank"> Download</a>
						</c:if>
					</c:if></td>

			</tr>
		</c:forEach>


		<%
			} catch (Exception e) {}
		%>

	</tbody>
</table>
<script>
	$(document).ready( function () {

		$('#lastCycleContentTable').DataTable( {
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