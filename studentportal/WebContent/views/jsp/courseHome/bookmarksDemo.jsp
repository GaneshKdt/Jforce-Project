
<!DOCTYPE html>

<html lang="en">




<jsp:include page="../common/jscssNew.jsp">
	<jsp:param value="Bookmarks" name="title" />
</jsp:include>
<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="java.util.concurrent.TimeUnit"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.Date"%>
<%@page import="com.nmims.beans.ContentStudentPortalBean"%>
<%@page import="com.nmims.beans.VideoContentStudentPortalBean"%>
<%@page import="com.nmims.controllers.BaseController"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.HashMap"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<%
	ArrayList<VideoContentStudentPortalBean> videoContentList = (ArrayList<VideoContentStudentPortalBean>) session
			.getAttribute("bookmarkVideoCotentList");
	ArrayList<ContentStudentPortalBean> contentList = (ArrayList<ContentStudentPortalBean>) session.getAttribute("bookmarkContentList");

	request.setAttribute("contentList", contentList);
%>
<style>
		.videoCard:hover , .contentCard:hover {
			transform: scale(1.009);
			z-index: 1;
			transition:0.7s;			
		}

		
</style>
<body>

	<%@ include file="../common/headerDemo.jsp"%>


	<div class="sz-main-content-wrapper">

		<jsp:include page="../common/breadcrum.jsp">
			<jsp:param value="Student Zone;Bookmarks" name="breadcrumItems" />
		</jsp:include>


		<div class="sz-main-content menu-closed">
			<div class="sz-main-content-inner">
			<div id="sticky-sidebar"> 
					<jsp:include page="../common/left-sidebar.jsp">
						<jsp:param value="Bookmarks" name="activeMenu" />
					</jsp:include>
				</div>


				<div class="sz-content-wrapper examsPage">
					<%@ include file="../common/studentInfoBar.jsp"%>


					<div class="sz-content">

						<h5 class="text text-danger text-uppercase mt-4 mt-md-6 fw-bold ">Bookmarks</h5>
						<div class="clearfix"></div>
<!-- 						<div class="card shadow "> -->
							<%@ include file="../common/messageDemo.jsp"%>		
<!-- 						Check isLoginAsLead then no bookmarks is active -->
							<c:if test="${isLoginAsLead eq false }">

						 <div id="accordion">
				   				 <div class="card rounded border border-1 p-2" id="contentBookmark"> 
				   				  <a data-bs-toggle="collapse" href="#collapseOne">    					 
				      				<div class="card-header bg-white pt-3 pb-3 border border-1">        						 
				         			  <i class="fa-solid fa-chevron-down float-end text text-dark" id="contentChevron"></i>
				         	          <h6 class="mt-0 mb-0 text-black text-uppercase"> Contents </h6>
		        			
				      			    </div>
				      			  </a>
					      			  <div id="collapseOne" class="collapse" data-bs-parent="#accordion">
								          <div class="card-body bg-white" >
								             <c:choose>
													<c:when test='${contentList.size() != 0}'>
																<div class="row ">
																	  	<div class=" data-content panel-body contentResourceCard">
																	  		<c:forEach var="contentFile" items="${contentList}" varStatus="status">
																                       <div class="row mb-3" id="${contentFile.id}" >
																                       		<div class="col-md-2 col-lg-3 "></div>
																                       		<div class="col-12 col-md-8 col-lg-6">
																	                       		<div class="d-flex justify-content-center align-items-center">
																		                       		<div class="card card-body contentCard border border-1 rounded shadow" ">
																		                       			<div class="row">
																											<div class="col-sm-9">
																												<div class="lead d-flex float-start">
																													<c:out value="${contentFile.name}" />
																												</div>
																											</div>
																											<div class="col-sm-3">
																												<div class="d-flex float-end">
																													<jsp:useBean id="now" class="java.util.Date" />
																													<fmt:parseDate
																														value="${contentFile.getCreatedDate()}"
																														var="date1" pattern="yyyy-MM-dd" />
						
																													<fmt:parseNumber
																														value="${(now.time - date1.time) / (1000*60*60*24) }"
																														var="diffDate" integerOnly="true" />
																													<c:if test="${diffDate == 1 }">
																														<c:out value="${diffDate} day ago" />
																													</c:if>
																													<c:if test="${diffDate > 7 }">
																														<c:out
																															value="${fn:substring(contentFile.getCreatedDate(),0,10)}" />
																													</c:if>
																													<c:if test="${diffDate > 1 && diffDate < 8}">
																														<c:out value="${diffDate} days ago" />
																													</c:if>
																												</div>
																											</div>
																										</div>
																										
																										 <div class="row ">
																												<div class="col-sm-12">
																													<p class="fs-6 mt-2">
																														<c:out value="${contentFile.description}" />
																														<c:set var="string1"
																															value="${contentFile.description}" />
																														<c:set var="string2"
																															value="${fn:toUpperCase(string1)}" />
																														<c:set var="string3"
																															value="${fn:toUpperCase(string1)}" />
																														<c:if
																															test="${fn:substring(string2,0,4) eq 'TO V'}"> 
																										  						 &nbsp;
																									  						 <a href="https://akamaicdn.webex.com/client/WBXclient-32.11.0-388/atrecply.msi">Windows</a> &nbsp;
																									   						<a href="https://akamaicdn.webex.com/client/WBXclient-32.11.0-388/mac/intel/webexplayer_intel.dmg">Mac
																																OSX</a>
																														</c:if>
																													</p>
																												</div>
																			 								</div> 
																																					 								
																										<div class="row border border-1 m-3 rounded p-3 mb-2">					
																										<c:if test="${not empty contentFile.previewPath}">
																											<c:if
																												test="${fn:endsWith(contentFile.previewPath, '.pdf') || fn:endsWith(contentFile.previewPath, '.PDF')
																					|| fn:endsWith(contentFile.previewPath, '.Pdf')}">
																												<c:url value="acads/student/previewContent"
																													var="previewContentLink">
																													<c:param name="previewPath"
																														value="${contentFile.previewPath}" />
																													<c:param name="name" value="${contentFile.name}" />
																													<c:param name="type" value="PDF"></c:param>
																												</c:url>
																												<div class="col-sm-2">
																													<img src="https://studentzone-ngasce.nmims.edu/ltidemo/assets/images/preview/pdf.png" />
																												</div>
																												<div class="col-sm-10">
																													<div class="col-sm-12 ms-1">
																														<div>
																															<span>Content Type : </span>
																															<c:out value="${contentFile.contentType}" />
																														</div>
																														<div>
																															<span>File Type : </span>
																															<c:out value="PDF"></c:out>
																														</div>
																														<div class="d-flex float-end">
																															<a href="/${previewContentLink}"
																																target="_blank"><i class="fa fa-regular fa-eye fa-2x border border-1 p-2 rounded"
																																data-bs-toggle="tooltip" data-bs-placement="top" data-bs-title="View Pdf"
																																></i></a>
																															<i class="fa fa-solid fa-bookmark  fa-2x border border-1 ms-2 rounded pt-1 ps-2 pe-2"
																																data-bs-toggle="tooltip" data-bs-placement="top" data-bs-title="Remove BookMark"
																																id="${contentFile.id}"
																																onclick="removeBookmark(this)"></i>
																														</div>
																													</div>
																												</div>
																		
																											</c:if>
																											<c:if
																												test="${fn:endsWith(contentFile.previewPath, '.ppt') || fn:endsWith(contentFile.previewPath, '.PPT') || fn:endsWith(contentFile.previewPath, '.PPTX') || fn:endsWith(contentFile.previewPath, '.pptx')}">
																												<div class="col-sm-2">
																													<img src="https://studentzone-ngasce.nmims.edu/ltidemo/assets/images/preview/powerpoint.png" />
																												</div>
																												<div class="col-sm-10">
																													<div class="col-sm-12 ms-1">
																														<div>
																															<span>Content Type : </span>
																															<c:out value="${contentFile.contentType}" />
																														</div>
																														<div>
																															<span>File Type : </span>
																															<c:out value="PPT"></c:out>
																														</div>
																														<div class="d-flex float-end">
																															
																															<!-- CommentedBy: Siddheshwar Khanse
																																 Date		: Jan 19, 2021 10:30 AM(IST)
																																 Reason		: Preview of PPT content should not visible on portal -->
																															
																															<%-- <c:url value="acads/previewContent"
																																var="previewContentLink">
																																<c:param name="previewPath"
																																	value="${contentFile.previewPath}" />
																																<c:param name="name"
																																	value="${contentFile.name}" />
																															</c:url>
																															<a href="/${previewContentLink}"
																																target="_blank"><i class="fa fa-eye"
																																style="font-size: 20px; color: gray; border: 1px solid lightgray; padding: 0.4rem; border-radius: .25rem;"></i></a> --%>
																															
																															<a href="#"
																																onClick="window.open('<spring:eval expression="@propertyConfigurer.getProperty('CONTENT_BASE_PATH')" />${contentFile.previewPath}')">
																															<i class="fa-solid fa-download fa-2x border border-1 p-2 rounded"
																															data-bs-toggle="tooltip" data-bs-placement="top" data-bs-title="Download"
																															></i>
																															</a> 
																															<i class="fa-solid fa-bookmark  fa-2x border border-1 ms-2 rounded pt-1 ps-2 pe-2"
																																id="${contentFile.id}"
																																data-bs-toggle="tooltip" data-bs-placement="top" data-bs-title="Remove BookMark"
																																onclick="removeBookmark(this)"></i>
																														</div>
																													</div>
																		
																												</div>
																											</c:if>
																											<c:if
																												test="${fn:endsWith(contentFile.previewPath, '.doc') || fn:endsWith(contentFile.previewPath, '.DOC') || fn:endsWith(contentFile.previewPath, '.DOCX') || fn:endsWith(contentFile.previewPath, '.docx')}">
																												<div class="col-sm-2">
																													<img src="https://studentzone-ngasce.nmims.edu/ltidemo/assets/images/preview/document.png" />
																												</div>
																												<div class="col-sm-10">
																													<div class="col-sm-12 ms-1">
																														<div>
																															<span>Content Type : </span>
																															<c:out value="${contentFile.contentType}" />
																														</div>
																														<div>
																															<span>File Type : </span>
																															<c:out value="Word"></c:out>
																														</div>
																														<div class="d-flex float-end">
																															<c:url value="acads/student/previewContent"
																																var="previewContentLink">
																																<c:param name="previewPath"
																																	value="${contentFile.previewPath}" />
																																<c:param name="name"
																																	value="${contentFile.name}" />
																															</c:url>
																															<a href="/${previewContentLink}"
																																target="_blank"><i class="fa-regular fa-eye fa-2x border border-1 p-2 rounded"
																																data-bs-toggle="tooltip" data-bs-placement="top" data-bs-title="View"
																																></i></a>
																															<a href="#"
																																onClick="window.open('<spring:eval expression="@propertyConfigurer.getProperty('CONTENT_BASE_PATH')" />${contentFile.previewPath}')">
																																<i class="fa-solid fa-download fa-2x border border-1 ms-2 rounded pt-1 ps-1 pe-1"
																																data-bs-toggle="tooltip" data-bs-placement="top" data-bs-title="Download"
																																></i>
																															</a> <i class="fa-solid fa-bookmark  fa-2x border border-1 ms-2 rounded pt-1 ps-2 pe-2"
																																id="${contentFile.id}"
																																data-bs-toggle="tooltip" data-bs-placement="top" data-bs-title="Remove BookMark"
																																onclick="removeBookmark(this)"></i>
																														</div>
																													</div>
																												</div>
																											</c:if>
																											<c:if
																												test="${fn:endsWith(contentFile.previewPath, '.xls') || fn:endsWith(contentFile.previewPath, '.XLS') || fn:endsWith(contentFile.previewPath, '.XLSX') || fn:endsWith(contentFile.previewPath, '.xlsx')}">
																												<div class="col-sm-2">
																													<img src="https://studentzone-ngasce.nmims.edu/ltidemo/assets/images/preview/xls.png" />
																												</div>
																												<div class="col-sm-10">
																													<div class="col-sm-12 ms-1">
																														<div>
																															<span>Content Type : </span>
																															<c:out value="${contentFile.contentType}" />
																														</div>
																														<div>
																															<span>File Type : </span>
																															<c:out value="Excel"></c:out>
																														</div>
																														<div class="d-flex float-end">
																															<c:url value="acads/student/previewContent"
																																var="previewContentLink">
																																<c:param name="previewPath"
																																	value="${contentFile.previewPath}" />
																																<c:param name="name"
																																	value="${contentFile.name}" />
																															</c:url>
																															<a href="/${previewContentLink}"
																																target="_blank"><i class="fa-regular fa-eye fa-2x border border-1 p-2 rounded"
																																data-bs-toggle="tooltip" data-bs-placement="top" data-bs-title="View"
																																></i></a>
																															<a href="#"
																																onClick="window.open('<spring:eval expression="@propertyConfigurer.getProperty('CONTENT_BASE_PATH')" />${contentFile.previewPath}')">
																																<i class="fa-solid fa-download fa-2x border border-1 ms-2 rounded pt-1 ps-1 pe-1"
																																data-bs-toggle="tooltip" data-bs-placement="top" data-bs-title="Download"
																																></i>
																															</a> <i class="fa-solid fa-bookmark  fa-2x border border-1 ms-2 rounded pt-1 ps-2 pe-2"
																																id="${contentFile.id}"
																																data-bs-toggle="tooltip" data-bs-placement="top" data-bs-title="Remove BookMark"
																																onclick="removeBookmark(this)"></i>
																														</div>
																													</div>
																												</div>
																											</c:if>
																										</c:if>
																										<c:if test="${not empty contentFile.documentPath}">
																											<c:if
																												test="${fn:endsWith(contentFile.documentPath, '.ppt') || fn:endsWith(contentFile.documentPath, '.PPT') || fn:endsWith(contentFile.documentPath, '.PPTX') || fn:endsWith(contentFile.documentPath, '.pptx')}">
																												<div class="col-sm-2">
																													<img src="https://studentzone-ngasce.nmims.edu/ltidemo/assets/images/preview/powerpoint.png" />
																												</div>
																												<div class="col-sm-10">
																													<div>
																														<span>Content Type : </span>
																														<c:out value="${contentFile.contentType}" />
																													</div>
																													<div>
																														<span>File Type : </span>
																														<c:out value="PPT"></c:out>
																													</div>
																													<div class="d-flex float-end">
																														<a href="/${contentFile.documentPath}"
																															target="_blank"> <i class="fa-solid fa-download fa-2x border border-1 p-2 rounded"></i>
																														</a> <i class="fa-solid fa-bookmark  fa-2x border border-1 ms-2 rounded pt-1 ps-2 pe-2"
																															id="${contentFile.id}"
																															data-bs-toggle="tooltip" data-bs-placement="top" data-bs-title="Remove BookMark"
																															onclick="removeBookmark(this)"></i>
																													</div>
																												</div>
																											</c:if>
																											<c:if
																												test="${fn:endsWith(contentFile.documentPath, '.doc') || fn:endsWith(contentFile.documentPath, '.DOC') || fn:endsWith(contentFile.documentPath, '.DOCX') || fn:endsWith(contentFile.documentPath, '.docx')}">
																												<div class="col-sm-2">
																													<img src="https://studentzone-ngasce.nmims.edu/ltidemo/assets/images/preview/document.png" />
																												</div>
																												<div class="col-sm-10">
																													<div class="col-sm-12 ms-1">
																														<div>
																															<span>Content Type : </span>
																															<c:out value="${contentFile.contentType}" />
																														</div>
																														<div>
																															<span>File Type : </span>
																															<c:out value="Word"></c:out>
																														</div>
																														<div class="d-flex float-end">
																															<a href="/${contentFile.documentPath}"
																																target="_blank"> <i class="fa-solid fa-download fa-2x border border-1 p-2 rounded"></i>
																															</a> <i class="fa-solid fa-bookmark  fa-2x border border-1 ms-2 rounded pt-1 ps-2 pe-2"
																																id="${contentFile.id}"
																																data-bs-toggle="tooltip" data-bs-placement="top" data-bs-title="Remove BookMark"
																																onclick="removeBookmark(this)"></i>
																														</div>
																													</div>
																												</div>
																											</c:if>
																											<c:if
																												test="${fn:endsWith(contentFile.documentPath, '.xls') || fn:endsWith(contentFile.documentPath, '.XLS') || fn:endsWith(contentFile.documentPath, '.XLSX') || fn:endsWith(contentFile.documentPath, '.xlsx')}">
																												<div class="col-sm-2">
																													<img src="https://studentzone-ngasce.nmims.edu/ltidemo/assets/images/preview/xls.png" />
																												</div>
																												<div class="col-sm-10">
																													<div class="col-sm-12 ms-1">
																														<div>
																															<span>Content Type : </span>
																															<c:out value="${contentFile.contentType}" />
																														</div>
																														<div>
																															<span>File Type : </span>
																															<c:out value="Excel"></c:out>
																														</div>
																														<div class="d-flex float-end">
																															<a href="/${contentFile.documentPath}"
																																target="_blank"> 
																																<i class="fa-solid fa-download fa-2x border border-1 p-2 rounded "></i>
																															</a> <i class="fa-solid fa-bookmark  fa-2x border border-1 ms-2 rounded pt-1 ps-2 pe-2"
																																id="${contentFile.id}"
																																data-bs-toggle="tooltip" data-bs-placement="top" data-bs-title="Remove BookMark"
																																onclick="removeBookmark(this)"></i>
																														</div>
																													</div>
																												</div>
																											</c:if>
																										</c:if>
																		
																										<c:if test="${not empty contentFile.webFileurl}">
																											<c:if
																												test="${contentFile.urlType == 'View' || contentFile.urlType == '' || empty contentFile.urlType	}">
																												<a href="${contentFile.webFileurl}"
																													target="_blank"><i class="fa-regular fa-eye fa-2x border border-1 p-2 rounded"></i></a>
																												<i class="fa-solid fa-bookmark  fa-2x border border-1 ms-2 rounded pt-1 ps-2 pe-2"
																													id="${contentFile.id}"
																													data-bs-toggle="tooltip" data-bs-placement="top" data-bs-title="Remove BookMark"
																													onclick="removeBookmark(this)"></i>
																											</c:if>
																		
																											<c:if test="${contentFile.urlType == 'Download'}">
																												<a href="${contentFile.webFileurl}"
																													target="_blank"><i class="fa-solid fa-download fa-2x border border-1 p-2 rounded"></i></a>
																												<i class="fa-solid fa-bookmark fa-2x border border-1 ms-2 rounded pt-1 ps-2 pe-2"
																													id="${contentFile.id}"
																													data-bs-toggle="tooltip" data-bs-placement="top" data-bs-title="Remove BookMark"
																													onclick="removeBookmark(this)"></i>
																											</c:if>
																										</c:if>
																									</div>
																	                       		</div>
																                       			</div>
																                       		</div>
<!-- 																                       </div> -->
																                       
																                     
																                    </div>   
																                      
													                         </c:forEach>
																	  	</div>
																	
																</div>

													</c:when>
													<c:otherwise>
														<div class="col-12 d-flex justify-content-center">
															<h6 class="text text-muted">
																No contents bookmarked
															</h6>
														</div>
													</c:otherwise>
												</c:choose>
										  </div>
									  </div>
								 </div>		
														<!--  Video Content Start  -->		
								<div class="card rounded-bottom border border-1 mt-4 p-2" id="videoBookmark">
						      		<a   data-bs-toggle="collapse"  href="#collapse2" >
						      			<div class="card-header rounded-bottom bg-white pt-3 pb-3 border border-1">			
						         				  <i class="fa-solid fa-chevron-down float-end text text-dark" id="videoChevron"></i>
						         				 <h6 class="mt-0 mb-0 text-black text-uppercase">Video Contents </h6> 
						      			</div>
						             </a>
						           <div id="collapse2" class="collapse" data-bs-parent="#accordion">
						               <div class="card-body bg-white" >
	                                    
								     	<div class="videoContent">
														<%
															try {
																if (videoContentList.size() > 0) {
																	for (VideoContentStudentPortalBean video : videoContentList) {
																		Date date1 = new SimpleDateFormat("yyyy-MM-dd").parse(video.getSessionDate());
																		Date date2 = new Date();
														%>
														<div class="row" id="<%=video.getId() %>">
													
															<div class="col-lg-3 col-md-2"></div>

															<div class="col-lg-6 col-md-8">
																	<div class="card videoCard mb-4 w-100 border border-1 rounded shadow" >
																	  <div class="row g-0">
																	    <div class="col-xl-7">
																	      <div class="ratio ratio-16x9">
																					<iframe src="<%=video.getVideoLink()%>" class="rounded"<%-- src="https://youtu.be/a3ICNMQW7Ok" --%>>
																					</iframe>
																				</div>
																	    </div>
																	    <div class="col-xl-5">
																	      <div class="card-body position-relative">
																	        <h5 class="card-title m-0"><%=video.getSubject()%></h5>
																	          <p class="card-text m-0"><%
																						if ((TimeUnit.DAYS.convert((long) (date2.getTime() - date1.getTime()),
																											TimeUnit.MILLISECONDS)) > 1
																											&& (TimeUnit.DAYS.convert((long) (date2.getTime() - date1.getTime()),
																													TimeUnit.MILLISECONDS)) < 8) {
																					%><%=TimeUnit.DAYS.convert((long) (date2.getTime() - date1.getTime()),
									                                                   TimeUnit.MILLISECONDS) + " days ago"%>
																					<%
																						} else if (TimeUnit.DAYS.convert((long) (date2.getTime() - date1.getTime()),
																											TimeUnit.MILLISECONDS) > 7) {
																					%>
																					<%=video.getCreatedDate().substring(0, 10)%>
																					<%
																						} else {
																					%>
																					<%=TimeUnit.DAYS.convert((long) (date2.getTime() - date1.getTime()),
																						TimeUnit.MILLISECONDS) + " day ago"%>
																					<%
																						}
																					%></p>
																	        <p class="card-text m-0">
																					<%
																						if (!StringUtils.isBlank(video.getDescription())) {
																					%>
																					<span> <%=video.getDescription()%>
																					</span><br />
																					<%
																						}
																									if (!StringUtils.isBlank(video.getFacultyName())) {
																					%>
																					<span>Faculty Name : <%=video.getFacultyName()%></span><br />
																					<%
																						}
																					%>
																					<%
																						if (!StringUtils.isBlank(video.getTrack())) {
																					%>
																					<span>Track : <%=video.getTrack()%></span>
																					<%
																						}
																					%>
																				</p>
																	        <p class="card-text m-0"><small class="text-muted">
																					<%
																						if (!StringUtils.isBlank(video.getFileName())) {
																					%>
																					<span>File Name : <%=video.getFileName()%></span><br />
																					<%
																						}
																					%>
																					<%
																						if (!StringUtils.isBlank(video.getDuration())) {
																					%>
																					<span>Duration : <%=video.getDuration()%></span><br />
																					<%
																						}
																					%>
																				</small></p>
																				
																				
																	      </div>
																	      <i class="fa-solid fa-bookmark video position-absolute rounded p-1 bottom-0 end-0 border border-1 fa-2x me-2 mb-1"
																					id="<%=video.getId()%>"
																					data-bs-toggle="tooltip" data-bs-placement="top" data-bs-title="Remove Bookmark"
																					onclick="removeBookmark(this)"></i>
																	    </div>
																	  </div>
																	</div>
															</div>
															</div>
														
														<%
															}
																} else {
														%>
														<div class="col-12 d-flex justify-content-center">

															<h6 class="text text-muted">
																No video contents bookmarked
															</h6>
														</div>
														<%
															}
															} catch (Exception e) {}
														%>
													</div>

									  
									   </div>
								   </div>
								   <div>
				  				</div>
			      
		
							</div>
	
                        </div>		
							</c:if>
							<c:if test="${isLoginAsLead eq true }">
								<h4 class="d-flex align-items-center">
									<i class="fa-regular fa-circle-exclamation fa-2x p-2 " aria-hidden="true"></i>
									 Bookmarks is not available !!!
								</h4>
							</c:if>
 															<!--  Video Content End -->
					</div>
				</div>


			</div>
		</div>
	</div>
	<script>
		function removeBookmark(obj){
			if(confirm("Are you sure you want to remove this content from bookmark?")){

			          $('[data-bs-toggle="tooltip"]').tooltip("hide");
	       
				let data = {
						'id' : $(obj).attr("id"),
						'bookmarked' : "N"					
					};

						$('#'+$(obj).attr("id")).remove();

						if($(obj).attr("class").split(" ")[2]=="video"){
							if($(".videoContent").html().trim()===""){
								$(".videoContent").html("<div class='col-12 d-flex justify-content-center'><h6 class='text text-muted'>No video contents bookmarked</h6></div>");
							}
						}else{	
							if($(".contentResourceCard").html().trim()===""){
								$(".contentResourceCard").html("<div class='col-12 d-flex justify-content-center'><h6 class='text text-muted'>No contents bookmarked</h6></div>");
							}
						}
										
					
					$.ajax({
						type : "POST",
						contentType : "application/json",
						url : "setBookmark",
						data : JSON.stringify(data),
						success : function(data) {					
						},
						error : function(e) {
						}
					});
				
			}
		}	
	</script>
	<script>
	
		 $('#contentBookmark').click(function() {
			$(this).find('#contentChevron').toggleClass('fa-solid fa-chevron-down  fa-solid  fa-chevron-up');
		});

	 $('#videoBookmark').click(function() {
			$(this).find('#videoChevron').toggleClass('fa-solid fa-chevron-up fa-solid  fa-chevron-down ');
		});  

	 $(document).ready(function() {
		    $("body").tooltip({ selector: '[data-bs-toggle=tooltip]' });
		});
	   
	</script> 
	<jsp:include page="../common/footerDemo.jsp" />
</body>
</html>
