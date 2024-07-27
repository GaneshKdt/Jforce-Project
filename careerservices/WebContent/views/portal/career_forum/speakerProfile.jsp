<!DOCTYPE html>


<%@page import="com.nmims.beans.FacultyCareerservicesBean"%>
<%@page import="com.google.gson.Gson"%>
<%@page import="com.nmims.beans.CSHomeModelBean"%>
<%@page import="com.nmims.beans.UserViewedWebinar"%>
<%@page import="com.itextpdf.text.log.SysoCounter"%>
<%@page import="java.util.Date"%>
<%@page import="com.nmims.beans.SessionDayTimeBean"%>
<%@page import="com.nmims.helpers.*"%>
<%@page import="java.net.URLEncoder"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.Calendar" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>


<html lang="en">
	
	<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
	
	
	<jsp:include page="/views/common/jscss.jsp">
		<jsp:param value="Speaker Profile" name="title"/>
	</jsp:include>
	<style>
		.social-links,.social-links i{
			font-size: 1.2rem;
		}
		
		h2{
    		font-size: 1.6rem;
    		text-transform: none;
		}
		
		.fa-facebook-f, facebook {
		    color:#3b5998;
		    
		} 
		.fa-linkedin-in, linkedin {
		    color:#007bb6;
		}
		.fa-twitter, twitter {
		    color:#00aced
		}
 	</style>
	<body>
	
		<jsp:include page="/views/common/header.jsp" />
		<div class="sz-main-content-wrapper">
			<jsp:include page="/views/common/breadcrum.jsp">
				<jsp:param value="<a href='/careerservices/Home'>Career Services</a>;Speaker Profile" name="breadcrumItems" />
			</jsp:include>
			<%
			CSHomeModelBean pageData = (CSHomeModelBean) request.getAttribute("PageData");
			%>
		
			<div class="sz-main-content menu-closed text-manager">
				<div class="sz-main-content-inner">
					<jsp:include page="/views/common/left-sidebar.jsp">
						<jsp:param value="" name="activeMenu"/>
					</jsp:include>	
		
					<div class="sz-content-wrapper">
						<jsp:include page="/views/common/studentInfoBar.jsp" />
						<div class="sz-content">
							<div class="sz-content large-padding-top">
							<jsp:include page="/views/common/messages.jsp" />
								<div class="card" style="overflow:hidden">
								
									<div class="clearfix"></div>
									<div class="row mx-2">
										<div class="col-xl-9 col-md-6 col-sm-12 my-3">
											<div class="row m-2">
											
												<%
																							FacultyCareerservicesBean faculty = (FacultyCareerservicesBean) request.getAttribute("Faculty");
																							%>
															
											 	<div class="col-12">
											 		<div class="col-12">
											 			<h1>${ Faculty.firstName } ${ Faculty.middleName } ${ Faculty.lastName }</h1>
											 		</div>
											 	</div>
											 	<div class="col-12 mt-3">
		                                        	<table class="table col-6">
														<% if(faculty.getTeachingExp() != null && !faculty.getTeachingExp().equals("")){ %>	
				                                       		<tr>
				                                       			<td class="pl-3 ml-2">
				                                       				<b>Teaching Experience</b>
				                                       			</td>
				                                       			<td>
				                                       				${ Faculty.teachingExp } Years
				                                       			</td>
				                                       		</tr>
				                                       	<% } %>
														<% if(faculty.getNgasceExp() != null && !faculty.getNgasceExp().equals("")){ %>	
				                                       		<tr>
				                                       			<td class="pl-3 ml-2">
				                                       				<b>Experience With NGASCE</b>
				                                       			</td>
				                                       			<td>
				                                       				${ Faculty.ngasceExp } Years
				                                       			</td>
				                                       		</tr>
				                                       	<% } %>
														<% if(faculty.getCorporateExp() != null && !faculty.getCorporateExp().equals("")){ %>	
				                                       		<tr>
				                                       			<td class="pl-3 ml-2">
				                                       				<b>Corporate Experience</b>
				                                       			</td>
				                                       			<td>
				                                       				${ Faculty.corporateExp } Years
				                                       			</td>
				                                       		</tr>
				                                       	<% } %>
														<% if(faculty.getCurrentOrganization() != null && !faculty.getCurrentOrganization().equals("")){ %>	
				                                       		<tr>
				                                       			<td class="pl-3 ml-2">
				                                       				<b>Organization</b>
				                                       			</td>
				                                       			<td>
				                                       				${ Faculty.currentOrganization }
				                                       			</td>
				                                       		</tr>
				                                       	<% } %>
														<% if(faculty.getDesignation() != null && !faculty.getDesignation().equals("")){ %>	
				                                       		<tr>
				                                       			<td class="pl-3 ml-2">
				                                       				<b>Designation</b>
				                                       			</td>
				                                       			<td>
				                                       				${ Faculty.designation }
				                                       			</td>
				                                       		</tr>
				                                       	<% } %>
			                                       	</table>
                                       				<div class="col-12 row">
                                       					<div class="my-2 col-12">
		                                       				<h2>
		                                       					About
		                                       				</h2>
                                       					</div>
                                       					
                                       					<div class="col-12">
                                       						${ Faculty.facultyDescription }
                                       					</div>
                                       				</div>
												</div>
											</div>
                                        </div>
                                        <div class="col-xl-3 col-md-6 col-sm-12 my-3">
                                       
                                        	<img class="mt-3 shadow-sm bg-white rounded border rounded" onError="this.onerror=null;this.src='assets/placeholder.png';" src="${ Faculty.imgUrl }" alt="No image available" style="width:100%">
                                        	
                                        	<div class="social-links mx-auto text-center">
													
												<% if(faculty.getSpeakerFacebookProfile() != null && !faculty.getSpeakerFacebookProfile().equals("")){ %>	
											    	<a href="<%= faculty.getSpeakerFacebookProfile() %>" class="p-1 facebook"><i class="fab fa-facebook-f"></i></a>
												<% } %>
												<% if(faculty.getSpeakerLinkedInProfile() != null && !faculty.getSpeakerLinkedInProfile().equals("")){ %>	
											    	<a href="<%= faculty.getSpeakerLinkedInProfile() %>" class="p-1 linkedin" ><i class="fab fa-linkedin-in"></i></a>
												<% } %>
												<% if(faculty.getSpeakerTwitterProfile() != null && !faculty.getSpeakerTwitterProfile().equals("")){ %>	
											    	<a href="<%= faculty.getSpeakerTwitterProfile() %>" class="p-1 twitter"><i class="fab fa-twitter"></i></a>
												<% } %>
											
									 		</div>
                                        </div>
										<div class="col-12 mb-4">
									 		<div class="col-12 mx-4">
									 			<h2>Sessions By This Speaker</h2>
									 		</div>
									 		<div class="clearfix"></div>
									 		<div class="m-2 w-80 mx-auto px-5">
									 			<div class="col-xl-9 col-lg-9 col-md-12 col-sm-12">
													<table id="sessions" class="table">
														<thead>
															<tr>
																<th style="font-weight: bold">
																	Subject
																</th>
																<th style="font-weight: bold">
																	Date
																</th>
															</tr>
														</thead>
														<tbody>
															<% 
																
																List<SessionDayTimeBean> sessions = (List<SessionDayTimeBean>) request.getAttribute("Sessions");
																
																for(SessionDayTimeBean thisSession: sessions){
															%>
																<tr>
																	<td>
																		<h2><a href = "viewScheduledSession?id=<%= thisSession.getId() %>"><%= thisSession.getSessionName() %></a></h2>
															 		</td>
																	<td>
																		<%= thisSession.getDate() %>
															 		</td>
																</tr>
															<%
																}
															%>
													</tbody>
												</table>
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
		<jsp:include page="/views/common/footer.jsp" />
	</body>
</html>