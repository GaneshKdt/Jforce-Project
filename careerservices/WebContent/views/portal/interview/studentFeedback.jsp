<!DOCTYPE html>

<%@page import="com.nmims.beans.InterviewBean"%>
<%@page
	import="com.nmims.beans.CareerForumHomeModelBean"%>
<%@page
	import="com.nmims.beans.AvailablePackagesModelBean"%>
<%@page import="com.nmims.beans.UserViewedWebinar"%>
<%@page import="com.itextpdf.text.log.SysoCounter"%>
<%@page import="java.util.Date"%>
<%@page import="com.nmims.helpers.*"%>
<%@page import="java.net.URLEncoder"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.Calendar"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.text.DateFormat"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>  

<jsp:useBean id="date" class="java.util.Date" />

<%
	Calendar cal = Calendar.getInstance();
	Date dt = new Date();
	String monthYear = new SimpleDateFormat("MMM YYYY").format(cal.getTime());
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	String today = sdf.format(dt);
	ArrayList<InterviewBean> interview = (ArrayList<InterviewBean>) session.getAttribute("interviewList");
	session.setAttribute("featureName", "Practice Interviews");
	String disabled = "";
%>

<html lang="en">

<script src='https://cdn.plot.ly/plotly-latest.min.js'></script>

<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>


<jsp:include page="/views/common/jscss.jsp">
	<jsp:param value="Interview Feedback" name="title" />
</jsp:include>

<style>
.disabled {
	pointer-events: none;
	cursor: default;
	opacity: 0.6;
	background: gray;
}

.interviews {
	background-color: #28B31D;
	border: 1px solid #1B9B11;
	color: #fff;
}

.dot {
  height: 12px;
  width: 12px;
  border-radius: 50%;
  display: inline-block;
}
</style>

<body>

	<jsp:include page="/views/common/header.jsp" />
	<div class="sz-main-content-wrapper">
		<jsp:include page="/views/common/breadcrum.jsp">
			<jsp:param
				value="<a href='/careerservices/Home'>Career Services</a>;<a href='/careerservices/studentFeedback'>Interview Feedback</a>"
				name="breadcrumItems" />
		</jsp:include>


		<div class="sz-main-content menu-closed">

			<div class="sz-main-content-inner">
				<jsp:include page="/views/common/left-sidebar.jsp">
					<jsp:param value="" name="activeMenu" />
				</jsp:include>

				<div class="sz-content-wrapper">
					<jsp:include page="/views/common/studentInfoBar.jsp" />
					<div class="sz-content" id="page-content">
						<div class="sz-content pt-3">
							<jsp:include page="/views/common/messages.jsp" />

							<h2 class="header pt-3 pl-3">Interview Feedback</h2>
							<div class="clearfix"></div>
							<div class="row">
								<div class="col-xl-9 col-md-12 col-sm-12 my-3">
									<div class="p-2">
										<p style="font-size: 1rem;" id="pageDescription">Feedback for the interview held on ${ interview.date } is provided below.</p>
									</div>

									<div class="card text-center mt-4">
										<div class="card-header card-special mx-auto">
											<h2 class="text-center mx-auto material-icon-containter"
												style="color: #d2232a;">
												<!-- <i class="material-icons" style="font-size: 110%"> list_alt</i> -->
												Activation Status
											</h2>
										</div>

										<div class="card-body">
											<div class="row">
												<div class="col-3 text-center border-right">
													<h4>Total Sessions</h4>
													<p style="font-size: 1.0rem;">
														<span id="totalActivations">${ studentDetails.totalActivations }</span>
													</p>
												</div>
												<div class="col-3 text-center border-right">
													<h4>Remaining</h4>
													<p style="font-size: 1.0rem;">
														<span id="activationsLeft">${ studentDetails.activationsLeft }</span>
													</p>
												</div>
												<div class="col-3 text-center border-right">
													<h4>Available</h4>
													<p style="font-size: 1.0rem;">
														<span id="activationsPossible">${ studentDetails.activationsLeft }</span>
													</p>
												</div>
												<div class="col-3 text-center">
													<h4>More Available On</h4>
													<p style="font-size: 1.0rem;">
														<span id="nextActivationPossible">Not Applicable</span>
													</p>
												</div>
											</div>
										</div>
									</div>
									<div class="card text-center mt-4">
										<div class="card-body" id="studentInterview">
											<div class="row" style="margin: 10px; text-align: left;">
												<div class="col-md-6" style="border-right: 2px solid gray;">
													<p style="font-weight: 500; font-size: 1.2em; margin-top: 25px;">Evaluation of your performance has been divided into a few categories:-</p>
													<div id="description" style="margin-top: 20px;">
														<p>
															<strong>Preparedness:</strong> ${ feedback.preparedness }
														</p>
														<p>
															<strong>Communication & Confidence:</strong> ${ feedback.communication }
														</p>
														<p>
															<strong>Listening Skills:</strong> ${ feedback.listeningSkills }
														</p>
														<p>
															<strong>Body Language:</strong> ${ feedback.bodyLanguage }
														</p>
														<p>
															<strong>Clarity of Thought:</strong> ${ feedback.clarityOfThought }
														</p>
														<p>
															<strong>Connect/Engage:</strong> ${ feedback.connect }
														</p>
														<p>
															<strong>Examples:</strong> ${ feedback.examples }
														</p>
														<p>
															<strong>Area of Strength:</strong> ${ feedback.strength }
														</p>
														<p>
															<strong>Areas of Improvement:</strong> ${ feedback.improvements }
														</p>
														<p>
															<strong>Feedback on CV tweaking as per the role
																requirements and interviewing techniques:</strong> ${ feedback.cvtweaking }
														</p>
														<p>
															<strong>Alternate career choice:</strong> ${ feedback.careerchoice }
														</p>
													</div>
												</div>
												
												<div class="col-md-6" id="graph">
													<div style="pointer-events: none;" id="feedbackBlock"></div>
													
													<div class="row" id="parameter" style="display: block">
														<div class="col-md-6">
															<p style="white-space: nowrap;">
															<b>PN </b>: 'Preparedness',&nbsp;<b>C&C</b>: 'Communication &
															Confidence',&nbsp;<b>LS</b>: 'Listening Skills'
															</p>
														</div>
														<div class="col-md-6">
														<p style="white-space: nowrap;">
															<b>BL </b>: 'Body Language',&nbsp;<b>COT</b>: 'Clarity of Thought',&nbsp;
															<b>CE </b>: 'Connect/Engage',&nbsp;<b>E </b>: 'Examples'
														</p></div>
													</div>
												</div>
											</div>
										</div>
									</div>
								</div>
								<div class="col-xl-3 col-md-12 col-sm-12 my-3">
									<jsp:include page="../upcomingAndActiveEvents.jsp" />
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>

	<jsp:include page="/views/common/footer.jsp" />

	<script>
		let parameters = {
			type: 'bar',
			x : ['PN', 'C&C', 'LS', 'BL', 'COT', 'CE', 'E' ],
			    
			y : [ ${feedback.preparedness}, ${feedback.communication}, ${feedback.listeningSkills}, ${feedback.bodyLanguage},
					${feedback.clarityOfThought}, ${feedback.connect}, ${feedback.examples} ],

			text: [ 'Preparedness', 
					'Communication & Confidence',
					'Listening Skills',
					'Body Language ',
					'Clarity of Thought',
					'Connect/Engage',
					'Examples' ],
			mode: 'text',
			textposition: 'bottom',
			marker : {
					color : '#C8A2C8',
					line : {
						width : 1
					}
				}
		};

		var data = [ parameters ];

		var layout = {
			title : 'Interview Feedback',
			font : {
				size : 14
			},
			showlegend: false,
			hoverinfo: 'none'
		};

		var config = {
			responsive : true
		}

		Plotly.newPlot('feedbackBlock', data, layout, {displayModeBar: false});	
	
	</script>

</body>
</html>