<!DOCTYPE html>

<%@page import="java.net.URLEncoder"%>
<%@page import="com.nmims.helpers.AESencrp"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<html lang="en">
    
    <jsp:include page="../common/jscss.jsp">
	<jsp:param value="Overview" name="title"/>
    </jsp:include>
    
    
    
    <body>
    
	<spring:eval expression="@propertyConfigurer.getProperty('SERVER_PATH')" var="server_path" />
	
    	<%@ include file="../common/header.jsp" %>
    	
    	
        
        <div class="sz-main-content-wrapper">
        
        	<jsp:include page="../common/breadcrum.jsp">
			<jsp:param value="Student Zone;Student Support;Overview" name="breadcrumItems"/>
			</jsp:include>
        	
            
            <div class="sz-main-content menu-closed">
                    <div class="sz-main-content-inner">
              				<jsp:include page="../common/left-sidebar.jsp">
								<jsp:param value="Overview" name="activeMenu"/>
							</jsp:include>
              				
              				
              				<div class="sz-content-wrapper ">
              						<%@ include file="../common/studentInfoBar.jsp" %>
              						
              						
              						<div class="sz-content">
										<%@ include file="../common/messages.jsp" %>
										<h2 class="text-capitalize">Overview</h2>
										<div class="clearfix"></div>
		              					<div class="panel-content-wrapper">

											<%
												if (!(lead.getConsumerProgramStructureId().equals("127") || lead.getConsumerProgramStructureId().equals("128"))) {
											%>
											<h2 class="">STUDENT RESOURCE BOOK (SRB)</h2>
											<div class="clearfix"></div>
											<p>
												Please read the Student Resource Book carefully as it contains
												details of the Academic, Evaluation and Administrative Rules and
												Regulations of the University. All students are expected to know
												these rules and policies as mentioned in the SRB. <a
													href="resources_2015/NGA SCE SRB 30 7 18.pdf" target="_blank"><b><i
														class="fa fa-download fa-lg"></i>Download SRB</b></a>
											</p>
											<%
												}
											%>
				
											<h2 class="">HELP AND SUPPORT AT NGASCE IS NOW FASTER</h2>
											<div class="clearfix"></div>
											<ul>
												<li><p>An efficient and responsive help desk reduces the distance between students and the University</p></li>
												<li><p>It is the point of contact for students to get any information / service from the University</p></li>
												<li><p>Timely resolution of students queries is the top priority at NGASCE</p></li>
												<li><p>Our single-window help desk helps you clarify all issues pertaining to any department: Admissions, Academics, Examinations, Books, Icards, Fee Receipts, etc.</p></li>
											</ul>
											<h2 class="">HIGHLIGHTS</h2>
											<div class="clearfix"></div>
											<ul>
												<li><p>Department-wise classification of queries for better response time</p></li>
												<li><p>Time bound resolution of queries within 2 working days</p></li>
												<li><p>Well Defined Three Level Escalation process to manage Complains and Student Grievances</p></li>
												<li><p>Multiple means to raise a query - Phone, Post My Query, Chat and by Visiting our Regional Office</p></li>
												<li><p>Multi skilled Counsellors with extensive knowledge across departments so you can get relevant and specific answers</p></li>
												<li><p>Comprehensive List of Frequently Asked Queries (FAQs) to ensure that we have the answers for most of the questions a student might have</p></li>
											</ul>
																						
											<h2 class="">ESCALATION MATRIX FOR STUDENTS:</h2>
											<div class="clearfix"></div>
											<div class="panel-group panel-overview">
												<div class="panel panel-default">
													<div class="panel-heading">
														<h4 class="panel-title">
															<h5 data-toggle="collapse" href="#collapse4">Level 1: Academic Coordinator at the University Regional Office</h5>
														</h4>
													</div>
													<div id="collapse4" class="panel-collapse collapse">
														<div class="panel-body">
															<p>You can contact the Academic Coordinator along with the SR Number (the unique number you get when you register your service request with NGASCE)</p>
															<form class="support-form" id="supportFormOneOld" method="post" name="supportFormOneOld" role="form">
																<div class="form-group">
																	<select class="form-control" id="supportCityThree" name="supportCityThree" onchange="tableToggle('escalationthree','supportCityThree');">
																		<option value="">Select a City</option>
																		<option value="Mumbai">Mumbai</option>
																		<option value="Delhi">Delhi</option>
																		<option value="Bangalore">Bangalore</option>
																		<option value="Hyderabad">Hyderabad</option>
																		<option value="Pune">Pune</option>
																		<option value="Ahmedabad">Ahmedabad</option>
																		<option value="Kolkata">Kolkata</option>
																		<option value="Chandigarh">Chandigarh</option>
																	</select>
																</div>
															</form>
															<div class="table-responsive">
															<table class="table table-bordered table-hover escalationthree">
																<tbody>
																	<tr style="display: none;">
																		<td>Mumbai</td>
																		<td>Priyanka Pingle</td>
																		<td><a href="mailto:ac_mumbai@nmims.edu">ac_mumbai@nmims.edu</a></td>
																		<%
																			if (lead.getConsumerProgramStructureId().equals("127") || lead.getConsumerProgramStructureId().equals("128")) {
																		%>
																		<td>1800 1025 136 (Toll Free)</td>
																		<%
																			} else {
																		%>
																		<td>+91 22 4235 5775</td>
																		<%
																			}
																		%>
																	</tr>
																	<tr style="display: none;">
																		<td>Delhi</td>
																		<td>Jasmeet Kaur</td>
																		<td><a href="mailto:ac_newdelhi@nmims.edu">ac_newdelhi@nmims.edu</a></td>
																		<%
																			if (lead.getConsumerProgramStructureId().equals("127") || lead.getConsumerProgramStructureId().equals("128")) {
																		%>
																		<td>1800 1025 136 (Toll Free)</td>
																		<%
																			} else {
																		%>
																		<td>+91 11 4505 3868 / +91 22 4235 5922 / +91 22
																			4235 5926</td>
																		<%
																			}
																		%>
																	</tr>
																	<tr style="display: none;">
																		<td>Bangalore</td>
																		<td>Poornima K. P.</td>
																		<td><a href="mailto:ac_bangalore@nmims.edu">ac_bangalore@nmims.edu</a></td>
																		<%
																			if (lead.getConsumerProgramStructureId().equals("127") || lead.getConsumerProgramStructureId().equals("128")) {
																		%>
																		<td>1800 1025 136 (Toll Free)</td>
																		<%
																			} else {
																		%>
																		<td>+91 80 4085 5513</td>
																		<%
																			}
																		%>
																	</tr>
																	<tr style="display: none;">
																		<td>Hyderabad</td>
																		<td>Afifa Ismath</td>
																		<td><a href="mailto:ac_hyderabad@nmims.edu">ac_hyderabad@nmims.edu</a></td>
																		<%
																			if (lead.getConsumerProgramStructureId().equals("127") || lead.getConsumerProgramStructureId().equals("128")) {
																		%>
																		<td>1800 1025 136 (Toll Free)</td>
																		<%
																			} else {
																		%>
																		<td>+91 40 2701 5536</td>
																		<%
																			}
																		%>
																	</tr>
																	<tr style="display: none;">
																		<td>Pune</td>
																		<td>Meghana Patange</td>
																		<td><a href="mailto:ac_pune@nmims.edu">ac_pune@nmims.edu</a></td>
																		<%
																			if (lead.getConsumerProgramStructureId().equals("127") || lead.getConsumerProgramStructureId().equals("128")) {
																		%>
																		<td>1800 1025 136 (Toll Free)</td>
																		<%
																			} else {
																		%>
																		<td>+91 20 2551 1688</td>
																		<%
																			}
																		%>
																	</tr>
																	<tr style="display: none;">
																		<td>Ahmedabad</td>
																		<td>Ketaki Amin</td>
																		<td><a href="mailto:ac_ahmedabad@nmims.edu">ac_ahmedabad@nmims.edu</a></td>
																		<%
																			if (lead.getConsumerProgramStructureId().equals("127") || lead.getConsumerProgramStructureId().equals("128")) {
																		%>
																		<td>1800 1025 136 (Toll Free)</td>
																		<%
																			} else {
																		%>
																		<td>+91 79 4039 3329</td>
																		<%
																			}
																		%>
																	</tr>
																	<tr style="display: none;">
																		<td>Kolkata</td>
																		<td>Sirshendu Sen</td>
																		<td><a href="mailto:ac_kolkata@nmims.edu">ac_kolkata@nmims.edu</a></td>
																		<%
																			if (lead.getConsumerProgramStructureId().equals("127") || lead.getConsumerProgramStructureId().equals("128")) {
																		%>
																		<td>1800 1025 136 (Toll Free)</td>
																		<%
																			} else {
																		%>
																		<td>+91 33 4061 4565</td>
																		<%
																			}
																		%>
																	</tr>
																	<tr style="display: none;">
																		<td>Chandigarh</td>
																		<td>Yapika Mehra</td>
																		<td><a href="mailto:lcchandigarh@nmims.edu">lcchandigarh@nmims.edu</a></td>
																		<%
																			if (lead.getConsumerProgramStructureId().equals("127") || lead.getConsumerProgramStructureId().equals("128")) {
																		%>
																		<td>1800 1025 136 (Toll Free)</td>
																		<%
																			} else {
																		%>
																		<td>+91 84 2402 7204</td>
																		<%
																			}
																		%>
																	</tr>
																	<tr style="display: none;">
																		<td>Chandigarh</td>
																		<td>Kajal Bhatia</td>
																		<td><a href="mailto:acchandigarh@nmims.edu">acchandigarh@nmims.edu</a></td>
																		<%
																			if (lead.getConsumerProgramStructureId().equals("127") || lead.getConsumerProgramStructureId().equals("128")) {
																		%>
																		<td>1800 1025 136 (Toll Free)</td>
																		<%
																			} else {
																		%>
																		<td>+91 99 2005 2566</td>
																		<%
																			}
																		%>
																	</tr>
																</tbody>
															</table>
															</div>
														</div>
													</div>
												</div>
												<div class="panel panel-default">
													<div class="panel-heading">
														<h4 class="panel-title">
															<h5 data-toggle="collapse" href="#collapse5">Level 2: Department Heads at the University Head Office</h5>
														</h4>
													</div>
													<div id="collapse5" class="panel-collapse collapse">
														<div class="panel-body">
															<p>To escalate further, you can contact the Department Head along with the SR Number (the unique number you get when you register your service request with NGASCE)</p>
															<form class="support-form" id="supportFormTwoOld" method="post" name="supportFormTwoOld" role="form">
																<div class="form-group">
																	<select class="form-control" id="supportCityFour" name="supportCityFour" onchange="tableToggle('escalationfour','supportCityFour');">
																		<option value="">Select a Department</option>
																		<option value="Admissions and Validity">Admissions and Validity</option>
																		<option value="Academics">Academics</option>
																		<option value="Examinations">Examinations</option>
																		<option value="Books, Fee Receipts, Icards">Books, Fee Receipts, Icards</option>
																		<option value="Any Other Issue">Any Other Issue</option>
																	</select>
																</div>
															</form>
															<div class="table-responsive">
															<table class="table table-bordered table-hover escalationfour" id="matrixEx">
																<tbody>
																	<tr style="display: none;">
																		<td>Admissions and Validity</td>
																		<td>Manasvi Malve</td>
																		<td><a href="mailto:Manasvi.Malve@nmims.edu">Manasvi.Malve@nmims.edu</a></td>
																		<td>+91 22 423 55792</td>
																	</tr>
																	<tr style="display: none;">
																		<td>Academics</td>
																		<td>Sneha Utekar</td>
																		<td><a href="mailto:Sneha.utekar@nmims.edu">Sneha.utekar@nmims.edu</a></td>
																		<td>+91 22 423 55795</td>
																	</tr>
																	<tr style="display: none;">
																		<td>Examinations</td>
																		<td>Jigna Patel</td>
																		<td><a href="mailto:Jigna.patel@nmims.edu">Jigna.patel@nmims.edu</a></td>
																		<td>+91 22 423 55791</td>
																	</tr>
																	<tr style="display: none;">
																		<td>Books, Fee Receipts, Icards</td>
																		<td>Rashmi Khedkar</td>
																		<td>Rashmi.Khedkar@nmims.edu</td>
																		<td>+91 22 423 55513</td>
																	</tr>
																	<tr style="display: none;">
																		<td>Any Other Issue</td>
																		<td>Sangeeta Shetty</td>
																		<td><a href="mailto:Sangeeta.Shetty@nmims.edu">Sangeeta.Shetty@nmims.edu</a></td>
																		<td>+91 423 55522</td>
																	</tr>
																</tbody>
															</table>
															</div>
														</div>
													</div>
												</div>
												<div class="panel panel-default">
													<div class="panel-heading">
														<h4 class="panel-title">
															<h5 data-toggle="collapse" href="#collapse6">Level 3: Head Student Services</h5>
														</h4>
													</div>
													<div id="collapse6" class="panel-collapse collapse">
														<div class="panel-body">
															<p>If you still want to escalate further, you can contact the Head Student Services along with the SR Number (the unique number you get when you register your service request with NGASCE)</p>
															<div class="table-responsive">
															<table class="table table-bordered table-hover">
																<tbody>
																	<tr>
																		<td>Mumbai</td>
																		<td><a href="mailto:Nelson.Soans@nmims.edu">Nelson.Soans@nmims.edu</a></td>
																		<td>+91 22 423 55529</td>
																	</tr>
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
            
		<div id="csApp"></div>
		<div id="examApp"></div>
        <jsp:include page="/WEB-INF/views/common/footer.jsp"/>
 <%
		String encryptedSapId = URLEncoder.encode(AESencrp.encrypt(userId)); 
		String csAppSSOUrl = (String)pageContext.getAttribute("server_path") + "careerservices/loginforSSO?uid="+encryptedSapId;
		String examAppSSOUrl = (String)pageContext.getAttribute("server_path") + "exam/loginforSSO?uid="+encryptedSapId;
%>


		 <script>
			$( "#csApp" ).load( "<%=csAppSSOUrl%>" ); 
			$( "#examApp" ).load( "<%=examAppSSOUrl%>" );
		</script>
	
    </body>
</html>