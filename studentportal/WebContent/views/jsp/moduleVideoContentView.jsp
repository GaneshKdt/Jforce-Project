<%@page import="java.util.ArrayList"%>
<%@page import="com.nmims.beans.*"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%
	String subject = (String)request.getAttribute("subject");
	ArrayList<VideoContentStudentPortalBean> videoContentList = (ArrayList<VideoContentStudentPortalBean>)request.getSession().getAttribute("videoContent");
	
	int noOfVideos = videoContentList != null ? videoContentList.size() : 0;
	
%>
<style>
	.borderView {
		border: 1px solid black;
		border-top-color: #d2232a;
	    border-top-width: 5px;
	/* 	color: #d2232a; */
	}

	.course-sessions-m-wrapper {
		min-height: 40em;
	}
</style>
<div class="course-sessions-m-wrapper">
	<div class="panel-courses-page">
		
		<% if (noOfVideos == 0) { %>
			<div class="no-data-wrapper nodata-wrapper">
				<h4 style="text-align: center">
					<i class="fa-solid fa-circle-exclamation" style="font-size: 19px" aria-hidden="true"></i>
					 Video is not available. Please try again !!!
				</h4>
			</div>

		<% } else { %>
			<div class="row data-content panel-body">
			<h2>Video Content</h2><br><br>
				<div class="col-md-12 " style="padding-bottom: 20px;">
					<div style="font-size: 12px;margin-bottom:1rem;">
						<div class="panel-body">
							<c:forEach items="<%=videoContentList %>" var="bean">
								<a href="/acads/student/watchVideos?id=${bean.id}">
									<div class="col-md-6 col-lg-4 " style="padding-top: 20px;">
										<div class="sz-calnr borderView" style="min-height: 350px;">    
										<div class="sz-time">
											<img src="${bean.thumbnailUrl}" class="img-responsive" style="max-width: 100%;" height="" width="800">
										</div>
										<div class="clearfix"></div>
										<div class="sz-calndr-info">
											<p><br>
												<strong>${bean.subject}</strong><br>
												${bean.description}
											</p>
										</div>
										</div>
									</div>
								</a>
							</c:forEach>
						</div>
					</div>
				</div>
			</div>
		<% } %>
	</div>
</div>