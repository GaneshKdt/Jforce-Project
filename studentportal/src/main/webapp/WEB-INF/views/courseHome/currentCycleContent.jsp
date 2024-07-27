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
	ArrayList<ModuleContentStudentPortalBean> moduleDocumentList =  (ArrayList<ModuleContentStudentPortalBean>)session.getAttribute("moduleDocumentList");
	int noOfLearningResources = moduleDocumentList != null ? moduleDocumentList.size() : 0;
	StudentStudentPortalBean studentSemCheck = (StudentStudentPortalBean)session.getAttribute("semCheck");
	ArrayList<VideoContentStudentPortalBean> videoContentList =  (ArrayList<VideoContentStudentPortalBean>)session.getAttribute("videoContentList");
	int noOfVideoContents = videoContentList != null ? videoContentList.size() : 0;
	ArrayList<ContentStudentPortalBean> contentList =  (ArrayList<ContentStudentPortalBean>)session.getAttribute("contentList");
	int noOfContents = contentList != null ? contentList.size() : 0;
	System.out.println("IN JSP got contentList size : "+contentList !=null ? contentList.size() : 0);
	int count=1;
	String videoLink = "";
	String userId=(String)request.getSession().getAttribute("userId");
	String hideShow = "";
	String isLead = session.getAttribute("isLoginAsLead").toString();
	System.out.println("isLead: "+isLead);
	if(isLead == "true"){
		hideShow = "hideShow('show')";
	}
%>

<table class="table table-striped" id="courseHomeLearningResources">
	<thead>
		<tr>
			<th>Sr No.</th>
			<th>Name</th>
			<th>Description</th>
			<th>Type</th>
			<th>Bookmark</th>
			<th>Action</th>
		</tr>
	</thead>

	<tbody>
		<!-- Code for Video Content Start -->
		<% try{ 
				if(noOfVideoContents>0){
					for(VideoContentStudentPortalBean video:videoContentList){
				%>
		<tr style="display: table-row;">
			<td><%= count %> </td>
			<td><%=video.getSubject() %> - <%=video.getFileName() %></td>
			<td>
				<%if(!StringUtils.isBlank(video.getFacultyName())) {%> <span>Faculty
					Name : </span> <%=video.getFacultyName() %> <br> <% } %> <span>Date
					: </span> <%=video.getSessionDate() %> <br> <% if(!StringUtils.isBlank(video.getTrack())){ %>
				<span>Track : </span> <%=video.getTrack() %> <% } %>

			</td>
			<td>Video</td>
			<%
				if (video.getBookmarked()!=null && video.getBookmarked().equals("Y")) {
			%><td class="bookmark video"
				data-selected="yes" id="<%=video.getId()%>"><i class="fa fa-bookmark" style="color: #fabc3d;"></i></td>

			<%
				} else {
			%><td class="bookmark video" data-selected="no" id="<%=video.getId()%>"><i class="fa fa-bookmark-o"  onClick = "<%= hideShow %>"></i></td>
			<%} %>

			<% 
				if(isLead == "true"){
					videoLink = "/acads/videosHomeNew?pageNo=1&academicCycle=All";
				}else{
					videoLink = "/acads/watchVideos?id="+video.getId();
				}
			%>
			<td>			
			<a href="<%= videoLink %>" target="_blank">Watch</a> 
			<%if( !"Enterprise Guide".equals(video.getSubject()) && !"Enterprise Miner".equals(video.getSubject()) && !"Visual Analytics".equals(video.getSubject()) ){%>
			<!-- <h4>&nbsp; / &nbsp;</h4> --> 
			<%}%>
			</td>
		</tr>

		<%	count++;
				}
				}
		}
		catch(Exception e){
		}
		%>
		<!-- Code for Video Content End -->

		<c:forEach var="contentFile" items="${contentList}" varStatus="status">
			<tr style="display: table-row;">
				<td><c:out value="<%= count %>" /></td>
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
				<script>alert(${contentFile.bookmarked} +" "+ );</script>
				<c:if test='${contentFile.bookmarked eq "Y"}'>
					<td class="bookmark content" data-selected="yes" id="${contentFile.id}"><i class="fa fa-bookmark" style="color: #fabc3d;"></i></td>
				</c:if>
				<c:if test='${contentFile.bookmarked eq "N" or empty contentFile.bookmarked }'>
					<td class="bookmark content" data-selected="no" id="${contentFile.id}"><i class="fa fa-bookmark-o" onClick = "<%= hideShow %>"></i></td>
				</c:if>
				<td><c:if test="${not empty contentFile.previewPath}">
						<c:if test="${!fn:endsWith(contentFile.previewPath, '.pdf') && !fn:endsWith(contentFile.previewPath, '.PDF')
															&& !fn:endsWith(contentFile.previewPath, '.Pdf')}">
															
							
							<a href="#"
								onClick="window.open('<spring:eval expression="@propertyConfigurer.getProperty('CONTENT_PREVIEW_PATH')" />${contentFile.previewPath}')">
								Download</a>
						</c:if>
					</c:if> <c:if test="${not empty contentFile.documentPath}">
						<c:if
							test="${!fn:endsWith(contentFile.documentPath, '.pdf') && !fn:endsWith(contentFile.documentPath, '.PDF')
															&& !fn:endsWith(contentFile.documentPath, '.Pdf')}">
							<a href="/${contentFile.documentPath}" target="_blank"
								style="margin: 0px; padding: 0px; width: 21px; bottom: 50px; left: 90%;">
								Download </a>
						</c:if>
					</c:if> <c:if
						test="${fn:endsWith(contentFile.previewPath, '.pdf') || fn:endsWith(contentFile.previewPath, '.PDF')
															|| fn:endsWith(contentFile.previewPath, '.Pdf')}">
						<c:url value="acads/previewContent" var="previewContentLink">
							<c:param name="previewPath" value="${contentFile.previewPath}" />
							<c:param name="name" value="${contentFile.name}" />
							<c:param name="type" value="PDF" />
						</c:url>
						<a href="/${previewContentLink}" target="_blank">View</a>
							
					</c:if> <c:if test="${not empty contentFile.webFileurl}">
						<c:if
							test="${contentFile.urlType == 'View' || contentFile.urlType == '' || empty contentFile.urlType	}">
							<a href="${contentFile.webFileurl}" target="_blank">View</a>
						</c:if>

						<c:if test="${contentFile.urlType == 'Download'}">
							<a href="${contentFile.webFileurl}" target="_blank"> Download</a>
						</c:if>
					</c:if></td>

			</tr>
			<% count++; %>
		</c:forEach>

	</tbody>
</table>
<script>
	$(document).ready( function () {

		$('#courseHomeLearningResources').DataTable( {
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