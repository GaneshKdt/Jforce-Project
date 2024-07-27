<%-- <!DOCTYPE html>

<html class="no-js">
<!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>


<jsp:include page="jscss.jsp">
	<jsp:param value="Log In as" name="title" />
</jsp:include>


<body class="inside">

	<%@ include file="header.jsp"%>

	<section class="content-container login">
		<div class="container-fluid customTheme">
			<div class="row">
			<h2 class="red text-capitalize" style="margin-top:-20px;">Online Event Registration</h2>
			
			</div>
			<%@ include file="messages.jsp"%>
			<div class="row clearfix">
				<form:form action="onlineEventRegistration" method="post" modelAttribute="feedback">

					<div class="table-responsive">


						<table class="table table-striped" style="font-size: 12px">
							<thead>
								<tr>
									<th>Event Name</th>
									<th>Event Date</th>
									<th>Register</th>

								</tr>
							</thead>

							<tbody>

								<tr>
									<td>Seminar on fraud and forensic</td>
									<td>Nov 14 2017</td>
									<td><button id="submit" name="submit" type="button"
											class="btn btn-large btn-primary"
											onclick="showhide('detail')">Register</button></td>
								</tr>
							</tbody>

						</table>


					</div>



					<!-- <div class="panel-body" id="detail" style="display: none;"> -->
					
	                <div class="table-responsive" id="detail" style="display: none;" >
										<table class="table table-striped" style="font-size: 12px">
								         <thead>	
										<tr>
										<th>Program category:</th> </tr>
										<tr><th>Lecture on:</th></tr>
										<tr><th>Eminent Speaker: </th></tr>
										<tr><th>Date:</th></tr>
										<tr><th>Time:</th></tr>
										<tr><th>Fees:</th></tr>
										<tr><th>Description:</th></tr>
										</thead>
										
										<tbody>
										<c:forEach var="bean" items="${getSubjectWiseAverage}" varStatus="status">
										<tr>
									
										</tr>
										</c:forEach>
										</tbody>
										
										</table>	
										</div>    				
					<!-- 	<p>Program category: Guest lecture from Industry expert for
							students pursuing financial management. Lecture on "Relevance of
							Financial Accounting And Analysis" Eminent Speaker: CA CMA CS
							Rammohan Bhave LIMCA Record IFRS - Consulting,
							Training,valuations- 22 countries Global Faculty Date: Time:
							Fees: Description:( -Co-ordinator)</p> -->
				<!-- 	</div> -->

<div class="panel-body">
<div><p>Program category: Guest lecture from Industry expert for students pursuing financial management.</p></div>
<div><p>Lecture on "Relevance of Financial Accounting And Analysis"</p></div>
<div><p>Eminent Speaker: CA CMA CS Rammohan Bhave LIMCA Record IFRS - Consulting, Training, valuations- 22 countries Global Faculty
</p></div>
<div><p>Date:27-10-2017</p></div>
<div><p>Time:7pm</p></div>

<div class="row">
<div class="col-sm-2">
<button id="submit" name="submit" class="btn btn-small btn-primary"  formaction="saveEventRegistration?response=Yes"  >Yes</button></div>
<div class="col-sm-2">
<button id="submit" name="submit" class="btn btn-small btn-primary"  formaction="saveEventRegistration?response=No"  >No</button></div>
<div class="col-sm-2">
<button id="submit" name="submit" class="btn btn-small btn-primary"  formaction="home">Maybe Later</button>

</div>
</div>

				</form:form>

			</div>
		</div>
	</section>
	<jsp:include page="footer.jsp"/>
	
	
<!-- 
	<script>

function showhide(id) {
   	var e = document.getElementById(id);
   	e.style.display = (e.style.display == 'block') ? 'none' : 'block'; 
}
   	
</script> -->


</body>
</html> --%>


<!DOCTYPE html>


<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<html lang="en">




<jsp:include page="common/jscss.jsp">
	<jsp:param value="Online Event Registration" name="title" />
</jsp:include>

<style>
.complete-profile-warpper .sz-content-wrapper.withBgImage .student-info-bar .student-image
	{
	border: 2px solid #000;
}

.complete-profile-warpper .sz-content-wrapper.withBgImage .student-info-bar ul.student-info-list li
	{
	color: #333;
}
</style>



<body>

	<%-- <%@ include file="common/header.jsp" %> --%>



	<div class="sz-main-content-wrapper complete-profile-warpper">

		<jsp:include page="common/breadcrum.jsp">
			<jsp:param value="Student Zone;Online Event Registration"
				name="breadcrumItems" />
		</jsp:include>


		<div class="sz-main-content menu-closed">
			<div class="container">
				<%-- <%@ include file="common/left-sidebar.jsp" %> --%>


				<div class="sz-content-wrapper dashBoard withBgImage">
					<%@ include file="common/studentInfoBar.jsp"%>


					<h2 class="red text-capitalize" style="margin-top: -20px;">Online
						Registration for Guest Lecture</h2>
					<div class="clearfix"></div>
					<div class="panel-content-wrapper">
						<%@ include file="common/messages.jsp"%>
						<div class="row clearfix">
							<form:form action="onlineEventRegistration" method="post"
								modelAttribute="onlineEvent">


								<!-- <div class="panel-body">
					<div><p>Audience: Students pursuing Financial Management </p></div>
					<div><p>Topic: "Relevance of Financial Accounting And Analysis"</p></div>
					<div><p>Eminent Speaker: Mr. Rammohan Bhave, LIMCA Record holder on IFRS, FCA, FCMA, ACS, LL.B. (G.), Diploma in IFRS (ACCA, UK), Certified IFRS (ICAI, India), Certified XBRL, SIX Sigma - Green Belt </p></div>
					<div><p>Date:27-10-2017</p></div>
					<div><p>Time:7pm</p></div> -->

								<!-- <div class="panel-body">
					<div><p>Dear Students, </p></div>
					<div><p>We have a session scheduled on Business Statistics to cover the following concepts to help you prepare well for your TERM END exams.</p></div>
					<div><p>Kindly help us understand your availability for the session, details of the lecture will be updated under your Academic calendar.</p></div>
					<div><p>Topics:Probability,Probability Distribution - SND,Least Square Regression,Numerical of all topics,Correlation and Regression</p></div>
					<div><p>Date:17-11-2017</p></div>
					<div><p>Time:7pm</p></div> -->

								<div class="panel-body">
									<div>
										<p>Dear Students,</p>
										<br>
									</div>
									<div>
										<p>I am pleased to introduce CA Bimal Jain &#8209; Member
											of Institute of Chartered Accountants of India and Member of
											Institute of Company Secretaries of India who will be sharing
											his insights on GST. He will also be covering the below
											topics:</p>
										<br>
									</div>
									<div>
										<p>&nbsp;&#8209;Shortcomings of the pre-GST indirect tax
											system.</p>
									</div>
									<div>
										<p>&nbsp;&#8209;Concept and Need of GST.</p>
									</div>
									<div>
										<p>&nbsp;&#8209;Role of GST in Indian Economy.</p>
									</div>
									<div>
										<p>&nbsp;&#8209;Concurrent Dual GST Model for India and
											its modus operandi.</p>
									</div>
									<div>
										<p>&nbsp;&#8209;The Concept of input tax credit and
											seamless flow of credits in GST.</p>
										<br>
									</div>
									<div>
										<p>Considering the importance of GST, we highly recommend
											you to attend this extremely informative webinar.</p>
									</div>
									<div>
										<p>Post attending the webinar, you will be better placed
											to determine the importance of GST in India along with the
											scope of work in this field.</p>
									</div>
									<div>
										<p>Date:22-04-2018</p>
									</div>
									<div>
										<p>Time:3pm to 5pm</p>
									</div>

									<div class="row">
										<div class="col-sm-2">
											<button id="submit" name="submit"
												class="btn btn-small btn-primary"
												formaction="saveEventRegistration?response=Yes&online_EventId=${onlineEventID}&eventName=${onlineEventName}">Yes</button>
										</div>
										<div class="col-sm-2">
											<button id="submit" name="submit"
												class="btn btn-small btn-primary"
												formaction="saveEventRegistration?response=No&online_EventId=${onlineEventID}&eventName=${onlineEventName}">No</button>
										</div>
										<div class="col-sm-2">
											<!-- <button id="submit" name="submit" class="btn btn-small btn-primary"  formaction="home">Maybe Later</button> -->
										</div>
									</div>
								</div>
							</form:form>

						</div>
					</div>


				</div>
			</div>
		</div>
	</div>


	<jsp:include page="footer.jsp" />
</body>
</html>


