<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<spring:eval expression="@propertyConfigurer.getProperty('SERVER_PATH')" var="server_path" />
<%@page import="java.net.URLEncoder"%>
<%@page import="com.nmims.helpers.AESencrp"%>
<html>
<jsp:include page="../common/jscss.jsp">
	<jsp:param value="Connect With Us" name="title" />
</jsp:include>
<body>
	<%@ include file="../common/header.jsp"%>
	<div class="sz-main-content-wrapper">
		<jsp:include page="../common/breadcrum.jsp">
			<jsp:param value="Student Zone;Student Support;Connect With Us"
				name="breadcrumItems" />
		</jsp:include>
		<div class="sz-main-content menu-closed">
			<div class="sz-main-content-inner">
			<div id="sticky-sidebar">  
				<jsp:include page="../common/left-sidebar.jsp">
					<jsp:param value="connectWithUs" name="activeMenu" />
				</jsp:include>
			  </div>
				<div class="sz-content-wrapper examsPage">
					<%@ include file="../common/studentInfoBar.jsp"%>
					<div class="sz-content">
						<h2>Connect With Us</h2>
						<%@ include file="../common/messages.jsp"%>
						<div class="clearfix"></div>
						<div class="raw">
							<div class="col-lg-4">
								<div class="clearfix"></div>
								<div class="panel-content-wrapper">
									<h2 style="color: #404041">QUICK CHAT</h2>
									<div class="clearfix"></div>
									<form role="form">
										<label class="form-group">
											<h4>You will be connected to our Student Counsellor to
												help you with the query</h4>
											<h2 style="color: #404041">INSTANT CHAT</h2>
										</label>

										<center>
											<a
												style="background-color: #d2232a; font-size: 20px; color: white; padding: 10px 40px 10px 40px; cursor: pointer"
												id="liveagent_button_online_57390000000H2q5"
												style="display: none; float: right;color:white"
												onclick="liveagent.startChat('57390000000H2q5')">Start
												Chatting</a>
										</center>
									</form>
								</div>
							</div>
							<div class="col-lg-4">
								<div class="clearfix"></div>
								<div class="panel-content-wrapper">
									<h2 style="color: #404041">Raise A Ticket</h2>
									<div class="clearfix"></div>
									<form role="form">
										<label class="form-group">
											<h4>Raise your Query or Concern to our Student
												Counsellor</h4>
											<h2 style="color: #404041"> RESPONSE TIME IS 48 WORKING HOURS</h2>
										</label>
										<center>
											<a
												style="background-color: #d2232a; font-size: 20px; color: white; padding: 10px 40px 10px 40px; cursor: pointer"
												id="liveagent_button_online_57390000000H2q5"
												href="/studentportal/m/connectMyCases?userId=<c:out value='${userId}'></c:out>"
												style="display: none; float: right;color:white"
												target="_blank">My Tickets</a>
										</center>
									</form>
								</div>
							</div>
							<div class="col-lg-4">
								<div class="panel-content-wrapper">
									<h2 style="color: #404041">Call Us</h2>
									<div class="clearfix"></div>
									<form role="form">
										<label class="form-group">
											<h4>Connect with a Student Counsellor ( Monday to
												Saturday - 09:00 am to 07:00 pm IST)</h4>
											<h2 style="color: #404041">CONNECT WITH US</h2>
										</label>

										<center>
											<a style="color: #404041; cursor: pointer; font-size: 20px"
												href="tel:+9118001025136">1800-1025-136(Toll Free) <i
												class="fa fa-phone" aria-hidden="true"></i></a>
										</center>
									</form>
								</div>
							</div>
							<div class="clearfix"></div>
						</div>
					</div>
					<div style="padding: 20px">
						<h2>ESCALATION MATRIX FOR STUDENTS</h2>
						<div class="clearfix"></div>
						<div class="panel-group panel-overview">
							<div class="panel panel-default">
								<div class="panel-heading">
									<h4 class="panel-title">
										<h5 data-toggle="collapse" href="#collapse1">LEVEL 1 -
											STUDENT COUNSELLOR ON THE TOLL-FREE NUMBER OR ASSOCIATE AT
											UNIVERSITY REGIONAL OFFICE & NMAT/NPAT CENTRE</h5>
									</h4>
								</div>
								<div id="collapse1" class="panel-collapse collapse">
									<div class="panel-body">
										<p>You can contact the Student Counsellor on the toll-free
											number or the Associate at University Regional Office &
											NMAT/NPAT Centre along with the SR Number (the unique number
											you get when you register your service request with NGASCE)
											or contact the student services team</p>
										<h5>STUDENT COUNSELLOR ON THE TOLL-FREE NUMBER</h5>
										<div class="table-responsive">
											<table class="table table-bordered table-hover">
												<tbody>
													<tr>
														<td>Mumbai</td>
														<td><a href="mailto:ngasce@nmims.edu">ngasce@nmims.edu</a></td>
														<td>1800 1025 136</td>
													</tr>
												</tbody>
											</table>
										</div>
										<h5>ASSOCIATE AT UNIVERSITY REGIONAL OFFICE & NMAT/NPAT
											CENTRE</h5>
										<form class="support-form" id="supportFormOneNew"
											method="post" name="supportFormOneNew" role="form">
											<div class="form-group">
												<select class="form-control" id="supportCityOne"
													name="supportCity"
													onchange="toggleOnSelect(`#supportCityOne`)">
													<option value="">Select a City</option>
													<option value="Mumbai">Mumbai</option>
													<option value="Delhi">Delhi</option>
													<option value="Bangalore">Bangalore</option>
													<option value="Hyderabad">Hyderabad</option>
													<option value="Pune">Pune</option>
													<option value="Ahmedabad">Ahmedabad</option>
													<option value="Kolkata">Kolkata</option>
													<option value="Chandigarh">Chandigarh</option>
													<option value="Indore">Indore</option>
													<option value="Lucknow">Lucknow</option>
												</select>
											</div>
										</form>
										<div class="table-responsive">
											<table class="table table-bordered table-hover escalationOne"
												id="matrix_1">
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
														<td>+91 11 4505 3868 / +91 22 4235 5922 / +91 22 4235
															5926</td>
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
														<td>Poorvi Nair</td>
														<td><a href="mailto:ac_ahmedabad@nmims.edu">ac_ahmedabad@nmims.edu</a></td>
														<%
															if (lead.getConsumerProgramStructureId().equals("127") || lead.getConsumerProgramStructureId().equals("128")) {
														%>
														<td>1800 1025 136 (Toll Free)</td>
														<%
															} else {
														%>
														<td>+91 79 4039 1068</td>
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
													<tr style="display: none;">
														<td>Indore</td>
														<td>Vidhi Mehta</td>
														<td><a href="mailto:acindore@nmims.edu">acindore@nmims.edu</a></td>
														<%
															if (lead.getConsumerProgramStructureId().equals("127") || lead.getConsumerProgramStructureId().equals("128")) {
														%>
														<td>1800 1025 136 (Toll Free)</td>
														<%
															} else {
														%>
														<td>+91 73 1258 1598</td>
														<%
															}
														%>
													</tr>
													<tr style="display: none;">
														<td>Lucknow</td>
														<td>Arun Mishra</td>
														<td><a href="mailto:Arun.Mishra@nmims.edu">Arun.Mishra@nmims.edu</a></td>
														<%
															if (lead.getConsumerProgramStructureId().equals("127") || lead.getConsumerProgramStructureId().equals("128")) {
														%>
														<td>1800 1025 136 (Toll Free)</td>
														<%
															} else {
														%>
														<td>0522- 4361555</td>
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
										<h5 data-toggle="collapse" href="#collapse2">LEVEL 2 -
											MANAGER - STUDENT SERVICES</h5>
									</h4>
								</div>
								<div id="collapse2" class="panel-collapse collapse">
									<div class="panel-body">
										<p>If your issue is not resolved, you can contact the
											Manager - Student Services along with the SR Number (the
											unique number you get when you register your service request
											with NGASCE)</p>
										<div class="table-responsive">
											<table class="table table-bordered table-hover">
												<tbody>
													<tr>
														<td><a href="mailto:managerservices@nmims.edu">managerservices@nmims.edu</a></td>
														<td>+91 22 423 55522</td>
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
										<h5 data-toggle="collapse" href="#collapse3">LEVEL 3 -
											HEAD - STUDENT SERVICES</h5>
									</h4>
								</div>
								<div id="collapse3" class="panel-collapse collapse">
									<div class="panel-body">
										<p>If you still want to escalate further, you can contact
											the Head - Student Services along with the SR Number (the
											unique number you get when you register your service request
											with NGASCE)</p>
										<div class="table-responsive">
											<table class="table table-bordered table-hover">
												<tbody>
													<tr>
														<!--<td><a href="mailto:studentservices@nmims.edu">studentservices@nmims.edu</a></td>-->
														<td><a href="mailto:headservices@nmims.edu">headservices@nmims.edu</a></td>
														<td>+91 22 423 55529</td>
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
										<h5 data-toggle="collapse" href="#collapse4">LEVEL 4 -
											GRIEVANCE REDRESSAL CELL/COMMITTEE</h5>
									</h4>
								</div>
								<div id="collapse4" class="panel-collapse collapse">
									<div class="panel-body">
										<p>If the students have grievance even after the
											resolution shared at Level 3 which is by Head Services of the
											School, students can put in formal application with all the
											relevant documents to be put forth before the Committee
											within 30 days from the date of the written communication of
											resolutions/recommendations of the Head Services of the
											School. Student must file an application along with necessary
											documents, if any, to the Office of the School, NMIMS
											University, Mumbai.</p>
										<br>
										<p>
											Please click here to download the <a
												href="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_STUDENTPORTAL_STATIC_RESOURCES')" />resources_2015/NGASCE_Student_Grievance_Redressal.pdf"
												target="_blank"><b><u> Student Grievance
														Redressal policy</b></u></a>
										</p>
									</div>
								</div>
							</div>
						</div>
					</div>
					<div class="raw">
						<div class="col-lg-4">
							<h2 class="text-capitalize">Reach Us</h2>
							<div class="clearfix"></div>
							<div class="panel-content-wrapper">
								<form role="form">
									<div class="form-group">
										<label for="question" class="text-uppercase">ALL INDIA
											TOLL FREE</label>
										<h4>
											1800 1025 136 (Toll Free)<br /> <br />Mon-Sat (9am-7pm)
										</h4>
									</div>
									<div class="form-group">
										<label for="email" class="text-uppercase">UNIVERSITY
											REGIONAL OFFICE & NMAT/NPAT CENTRE CONTACT DETAILS</label>
										<form class="support-form" id="supportFormThreeNew"
											method="post" name="supportFormThreeNew" role="form">
											<div class="form-group">
												<div class="select-wrapper">
													<select class="form-control" id="supportCityThree"
														name="supportCityThree"
														onchange="toggleOnSelect(`#supportCityThree`)">
														<option value="">Select a City</option>
														<option value="Mumbai">Mumbai</option>
														<option value="Delhi">Delhi</option>
														<option value="Bangalore">Bangalore</option>
														<option value="Hyderabad">Hyderabad</option>
														<option value="Pune">Pune</option>
														<option value="Ahmedabad">Ahmedabad</option>
														<option value="Kolkata">Kolkata</option>
														<option value="Chandigarh">Chandigarh</option>
														<option value="Indore">Indore</option>
														<option value="Lucknow">Lucknow</option>
													</select>
												</div>
											</div>
										</form>
										<div class="table-responsive">
											<table
												class="table table-bordered table-hover escalationthree"
												id="escalationthree">
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
														<td>+91 11 4505 3868 / +91 22 4235 5922 / +91 22 4235
															5926</td>
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
														<td>Poorvi Nair</td>
														<td><a href="mailto:ac_ahmedabad@nmims.edu">ac_ahmedabad@nmims.edu</a></td>
														<%
															if (lead.getConsumerProgramStructureId().equals("127") || lead.getConsumerProgramStructureId().equals("128")) {
														%>
														<td>1800 1025 136 (Toll Free)</td>
														<%
															} else {
														%>
														<td>+91 79 4039 1068</td>
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
													<tr style="display: none;">
														<td>Indore</td>
														<td>Vidhi Mehta</td>
														<td><a href="mailto:acindore@nmims.edu">acindore@nmims.edu</a></td>
														<%
															if (lead.getConsumerProgramStructureId().equals("127") || lead.getConsumerProgramStructureId().equals("128")) {
														%>
														<td>1800 1025 136 (Toll Free)</td>
														<%
															} else {
														%>
														<td>+91 73 1258 1598</td>
														<%
															}
														%>
													</tr>
													<tr style="display: none;">
														<td>Lucknow</td>
														<td>Arun Mishra</td>
														<td><a href="mailto:Arun.Mishra@nmims.edu">Arun.Mishra@nmims.edu</a></td>
														<%
															if (lead.getConsumerProgramStructureId().equals("127") || lead.getConsumerProgramStructureId().equals("128")) {
														%>
														<td>1800 1025 136 (Toll Free)</td>
														<%
															} else {
														%>
														<td>0522- 4361555</td>
														<%
															}
														%>
													</tr>
												</tbody>
											</table>
										</div>
									</div>
								</form>
							</div>
						</div>
						<div class="col-lg-4">
							<h2 class="text-capitalize">Visit Us</h2>
							<div class="clearfix"></div>
							<div class="panel-content-wrapper">
								<form role="form">
									<div class="form-group">
										<label for="question" class="text-uppercase">MAIN HEAD
											OFFICE</label>
										<p>V.L.Mehta Road, Vile Parle (W) Mumbai, Maharashtra -
											400056</p>
									</div>
									<br> <br>
									<div class="form-group">
										<label for="email" class="text-uppercase">LOCATE
											UNIVERSITY REGIONAL OFFICE & NMAT/NPAT CENTRE</label>
										<form class="support-form" id="supportFormFourNew"
											method="post" name="supportFormFourNew" role="form">
											<div class="form-group">
												<div class="select-wrapper">
													<select class="form-control" id="address-list"
														name="address-list"
														onchange="toggleOnSelect(`#address-list`)">
														<option value="">Select a City</option>
														<option value="Mumbai">Mumbai</option>
														<option value="Delhi">Delhi</option>
														<option value="Bangalore">Bangalore</option>
														<option value="Hyderabad">Hyderabad</option>
														<option value="Pune">Pune</option>
														<option value="Ahmedabad">Ahmedabad</option>
														<option value="Kolkata">Kolkata</option>
														<option value="Chandigarh">Chandigarh</option>
														<option value="Indore">Indore</option>
														<option value="Lucknow">Lucknow</option>
													</select>
												</div>
											</div>
										</form>
										<div class="table-responsive">
											<table
												class="table table-bordered table-hover escalationthree"
												id="escalationthree"></table>
										</div>
										<div class="address-list-container">
											<label for="question" class="text-uppercase">UNIVERSITY
												REGIONAL OFFICE & NMAT/NPAT CENTRE ADDRESS</label>
											<p>NGA SCE, 2nd Floor, NMIMS New Building, Opp Mithibai
												College, V.L.Mehta Road, Vile Parle West, Mumbai - 400056
												Maharashtra</p>
											<p>Upper Ground Floor, KP - 1, Pitampura, Next to Hotel
												City Park, New Delhi - 110034 New Delhi</p>
											<p>11, Kaveri Regent Coronet, 80 Feet Road, 7th Main, 3rd
												Block, Next to Raheja Residency, Koramanagla, Bangalore -
												560034 Karnataka</p>
											<p>12-13-95, Street No. 3, Beside Big Bazar, Taranaka ,
												Hyderabad - 500018 Andhra Pradesh</p>
											<p>365/6, Aaj Ka Anand Building, 2nd Floor, Opposite SSPS
												School, Narveer Tanaji Wadi, Shivajinagar, Pune -411005
												Maharashtra</p>
											<p>B-3, Ground Floor, "Safal Profitaire", Corporate Road,
												Near Prahladnagar Garden, Prahladnagar, Ahmedabad -380 007
												Gujarat</p>
											<p>Unit # 505, Merlin Infinite, DN-51, Salt Lake City,
												Sector V, Kolkata-700091 West Bengal</p>
											<p>Plot No.5, Education City, Opp. Botanical Garden,
												Adjacent to Govt. School, Sarangpur, Chandigarh (U.T.)
												160014</p>
											<p>Ground Floor, Off. Super Corridor, Bada Bangarda, Near
												Gandhi Nagar, Indore, Madhya Pradesh - 453112</p>
											<p>Bhavya Corporate Tower, office no.205-206, 2nd floor,
												Plot No. TC-24V, Vibhuti Khand, Gomti Nagar, Lucknow, Uttar
												Pradesh - 226010</p>
										</div>
									</div>
								</form>
							</div>
						</div>
						<div class="clearfix"></div>
					</div>
				</div>
			</div>
		</div>
	</div>
	</div>
	<jsp:include page="/views/jsp/common/footer.jsp"/>
	<script>
		function toggleOnSelect(idName) {
			var city = $(idName).val();
			var index = $(idName).find('option:selected').index();

			$(idName).each(
					function() {
						console.log("city: " + city);
						$("#supportCityOne").val(city);
						$("#supportCityTwo").val(city);
						$("#supportCityThree").val(city);
						$("#address-list").val(city);

						var tableList = [ "escalationOne", "escalationtwo",
								"escalationthree" ];

						for (var i = 0; i < tableList.length; i++) {
							console.log(tableList[i]);
							$('table.' + tableList[i] + ' tr').each(function() {
								var tdVal = $(this).find('td').html();
								if (tdVal == city) {
									$(this).show();
								} else {
									$(this).hide();
								}
							})
						}

						$('.address-list-container').show();
						$('.address-list-container p').hide();
						$(
								'.address-list-container p:nth-of-type('
										+ index + ')').show();
					});
		}
	</script>
	<jsp:include page="../common/SSOLoader.jsp"></jsp:include>
<%-- 	<div id="csApp"></div>
	<div id="examApp"></div>
	<div id="acadsApp"></div>
	<%
		String encryptedSapId = URLEncoder.encode(AESencrp.encrypt(userId)); 
		String csAppSSOUrl = (String)pageContext.getAttribute("server_path") + "careerservices/loginforSSO?uid="+encryptedSapId;
		String examAppSSOUrl = (String)pageContext.getAttribute("server_path") + "exam/loginforSSO?uid="+encryptedSapId;
		String acadsAppSSOUrl = (String)pageContext.getAttribute("server_path") + "acads/loginforSSO?uid="+encryptedSapId;
%>


		 <script>
			$( "#csApp" ).load( "<%=csAppSSOUrl%>" ); 
			$( "#examApp" ).load( "<%=examAppSSOUrl%>" );
			$( "#acadsApp" ).load( "<%=acadsAppSSOUrl%>" );
		</script> --%>
</body>
</html>