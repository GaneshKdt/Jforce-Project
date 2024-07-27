
<!DOCTYPE html>


<html lang="en">

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<jsp:include page="../adminCommon/jscss.jsp">
	<jsp:param value="FAQs" name="title" />
</jsp:include>



<body>

	<%@ include file="../adminCommon/header.jsp"%>
	<div class="sz-main-content-wrapper">

		<jsp:include page="../adminCommon/breadcrum.jsp">
			<jsp:param value="Important Documents;FAQs" name="breadcrumItems" />
		</jsp:include>


		<div class="sz-main-content menu-closed">
			<div class="sz-main-content-inner">
				<jsp:include page="../adminCommon/left-sidebar.jsp">
					<jsp:param value="" name="activeMenu" />
				</jsp:include>


				<div class="sz-content-wrapper examsPage">
					<%@ include file="../adminCommon/adminInfoBar.jsp"%>
					<div class="sz-content">

						<div class="clearfix"></div>


						<div class="common-content supportWrap">
							<div class="col-xs-12">
								<div class="row">
									<div class="col-lg-9 col-sm-6 col-xs-12">
										<h3 class="information-title red">Frequently Asked
											Questions</h3>
									</div>
									<div class="col-lg-3 col-sm-6 col-xs-12"></div>

									<div class="col-xs-12 col-md-12">
										<div class="row" style="padding: 15px;">

											<div class="col-md-12" style="margin: 0em 0; padding: 0px;">

												<input type="text" id="search-criteria" class="form-control"
													style="width: 100%" placeholder="Enter Search Text Here">
											</div>


										</div>
									</div>
								</div>
								<div id="All" class="supportFaq">
									<div class="panel-group" id="accordion" role="tablist"
										aria-multiselectable="true">


										<div class="panel panel-default faq">
											<div class="panel-heading">
												<h4 class="panel-title">
													<a class="collapsed" data-toggle="collapse"
														aria-expanded="false" data-parent="#accordion" href="#192">
														Admissions </a>
												</h4>
											</div>
											<!--/.panel-heading -->


											<div id="192" class="panel-collapse collapse out">
												<div class="panel-body">



													<div class="panel-group" id="nested">
														<!-- BY PS -->
														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#192" href="#007"> What is the
																		eligibility criteria for Data visualization? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="007" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>Bachelor&#39;;s Degree in any discipline from
																		any recognized University or equivalent degree
																		recognized by Association of Indian Universities (AIU)</p>
																	<p>OR</p>
																	<p>H.S.C</p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->


														<!-- BY PS 1.2 -->
														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#192" href="#008"> What is the
																		eligibility criteria for Machine Learning? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="008" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>Bachelor&#39;;s Degree in any discipline from
																		any recognized University or equivalent degree
																		recognized by Association of Indian Universities (AIU)</p>
																	<p>OR</p>
																	<p>H.S.C with Mathematical/Statistical background</p>
																	<p>If for any reason you do not have any
																		Mathematical/Statistical background we let you take a
																		small test for self-assessment and decide if want to
																		proceed with Machine Learning</p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->

														<!-- End of BY PS 1.2 -->
														<!--  BY PS 1.3 -->
														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#192" href="#009"> What are the
																		documents required for enrolling for Data
																		visualization? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="009" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>The documents required as per eligibility
																		criteria</p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->

														<!-- End of BY PS 1.3 -->
														<!--   BY PS 1.4 -->
														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#192" href="#010"> Where do I require
																		to submit my admission documents? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="010" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>It is required to submit the admission document
																		to your Authorized Enrollment Partner to complete your
																		admission procedure</p>

																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->

														<!-- End of BY PS 1.4-->
														<!--  BY PS 1.5-->
														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#192" href="#011"> Does NGASCE-NMIMS
																		provide dual specialization? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="011" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>No, we do not offer dual specialization as a
																		part of our program</p>

																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->

														<!-- End of BY PS 1.5-->
														<!-- BY PS 1.6-->
														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#192" href="#012"> Can I pursue more
																		than one course simultaneously from NGASCE? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="012" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>Yes, student can enroll in Executive Programs
																		while pursuing PG/Diploma or Certificate programs.</p>

																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->


														<!-- End of BY PS 1.6-->
														<!-- BY PS 1.7-->
														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#192" href="#013"> What is the fee
																		structure for the Programs? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="013" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>Program fees for Data visualization is Rs 70,000
																		+ 18&#37; GST
																	<p>Program fees for Machine Learning is Rs 1,10,000
																		+ 18&#37; GST</p>

																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->



														<!-- End of BY PS 1.7-->
														<!--  BY PS 1.8-->
														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#192" href="#014"> What is a Regional
																		office/Learning center of NMIMS? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="014" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>To ensure quality in our Academic delivery,
																		NGASCE has set up its own University Regional Office
																		across 7 major locations of India, [Mumbai, Delhi,
																		Kolkata, Bengaluru, Hyderabad, Pune and Ahmedabad].
																		These Centre&#39;s are NMIMSs&#39; own Centre&#39;s
																		having state of art infrastructure to deliver quality
																		education. These Centre&#39;s also act as local point
																		of contact for students within that area to facilitate
																		student support services. Our University Regional
																		Office are one of our biggest differentiators in
																		Distance Learning Space.</p>

																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->

														<!-- End of BY PS 1.8-->
														<!--  BY PS 1.9-->
														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#192" href="#015"> What is an
																		authorized enrollment partner/ Information Center </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="015" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>Authorized Enrollment Partner/ Information
																		center (IC) means a Centre set up for Student support
																		and their services include administration,
																		coordination and follow up with students for
																		Admissions, Academic deliverables, Exams, queries and
																		concerns. As stipulated by the Institution and based
																		on which it is authorized by the Institution for the
																		purpose of advising, for rendering any assistance or
																		related services, required by the students of the
																		Institution who have been admitted by the Institution
																		in its Distance and Online Education Programs which
																		will not include teaching, examination and assessment.
																	</p>

																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->

														<!-- End of BY PS 1.9-->
														<!--  BY PS 1.10-->
														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#192" href="#016"> Will Authorized
																		Enrollment Partner be involved in any academic
																		process? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="016" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>No, Authorized Enrollment Partner will not be in
																		charge of any academic process. Authorized Enrollment
																		Partner is not authorized to collect any additional
																		fees from the students for NMIMS programs. Any
																		personal dealing with Authorized Enrollment Partner
																		will be at the student&#39;s risk</p>

																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->

														<!-- End of BY PS 1.10-->
														<!--  BY PS 1.11-->
														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#192" href="#017"> Can I change my
																		name after taking admission for the distance learning
																		programs? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="017" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>You can change your name after taking admission
																		for our distance learning programs. You will be
																		required to apply for name change by applying for
																		service request via your student zone portal. You will
																		be required to upload the photo id proof in the First
																		&#38; Last Name order or marriage affidavit as the
																		case may be. Kindly note there are no charges in case
																		you wish to change your name</p>


																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->

														<!-- End of BY PS 1.11-->
														<!--  BY PS 1.12-->
														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#192" href="#0117"> Can I change my
																		date of birth after taking admission for our distance
																		learning programs? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="0117" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>Student can change their date of birth after
																		taking admission for our distance learning programs.
																		Student can change their date of birth via the service
																		request link tab which is updated in your student zone
																		portal. Student will be required to upload your SSC
																		mark sheet or any valid proof of birth.</p>

																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->

														<!-- End of BY PS 1.12-->
														<!--  BY PS 1.13-->
														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#192" href="#018"> Can I
																		change/correct my registered e-mail id? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="018" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>If you have updated your e-mail id incorrectly
																		at the time of your admission or if you want to change
																		your email ID, you need to inform us about the same
																		with the below supporting documents.</p>
																	<p>Billing address proof copy as updated in our
																		system &#38; record</p>
																	<p>Government ID card proof</p>
																	<p>On verification of the above documents, the
																		e-mail id will be updated in our system &#38; you will
																		be able to login to your student zone portal
																		successfully.</p>

																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->

														<!-- End of BY PS 1.13-->
														<!--  BY PS 1.14-->
														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#192" href="#019"> Can a student
																		extend program validity? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="019" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>No, there is not extension available for Data
																		visualization and Machine Learning</p>

																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->

														<!-- End of BY PS 1.14-->
														<!--  BY PS 1.15-->
														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#192" href="#020"> What is the
																		procedure for admission cancellation? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="020" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>Student are requested to kindly fill &#38;
																		submit the original cancellation form with your ID
																		card and original fee receipt to your authorized
																		enrollment partner (mention the center name) for
																		initiating the cancellation process. Kindly note there
																		is a process time line of 28 days for the cancellation
																		refund cheque to be issued to you after deduction of
																		administrative charges &#38; study kit charges in case
																		the same has been issued to you. The process of
																		cancellation has to be initiated by a set date for
																		admission cancelation provided by the university.</p>

																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->

														<!-- End of BY PS 1.15-->
														<!--  BY PS 1.16-->
														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#192" href="#021"> How can I get a
																		Bonafide certificate? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="021" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>You need to raise a service request for
																		Bonafide, follow the path to raise a request:</p>
																	<p>Log in to student portal&rarr;Student
																		support&rarr;service request&rarr; select issuance of
																		Bonafide</p>
																	<p>Enter in the required information for the
																		document and make the appropriate payments towards
																		issuing the Bonafide certificates.</p>

																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->

														<!-- End of BY PS 1.16-->
													</div>
													<!--/.panel-group achere -->
												</div>
												<!--/.panel-body-->
											</div>
											<!--/.panel-collapse-->

										</div>
										<!--/.panel-->

										<div class="panel panel-default faq">
											<div class="panel-heading">
												<h4 class="panel-title">
													<a class="collapsed" data-toggle="collapse"
														aria-expanded="false" data-parent="#accordion" href="#193">
														Academics </a>
												</h4>
											</div>
											<!--/.panel-heading -->

											<div id="193" class="panel-collapse collapse out">
												<div class="panel-body">
													<!-- BY PS academics -->
													<!-- BY PS academics 2.1-->

													<div class="panel panel-default faq">
														<div class="panel-heading">
															<h4 class="panel-title">
																<a data-toggle="collapse" aria-expanded="false"
																	data-parent="#193" href="#2001"> What Academic help
																	What Academic help will I receive after registering for
																	the program? </a>
															</h4>
														</div>
														<!--/.panel-heading -->
														<div id="2001" class="panel-collapse collapse out">
															<div class="panel-body faqAns">
																<p>After registering for any program with NGA-SCE, a
																	student will receive</p>
																<p>Self-learning Material</p>
																<p>Access to our online digital library and Student
																	Portal</p>
																<p>Course presentation</p>
																<p>Mentored sessions with SAS faculty&#39;s to
																	practice data</p>
																<p>Access to recordings of online live sessions</p>
																<p>Session presentations and any additional reading
																	material as shared by faculty</p>
																<p>Doubt clearing sessions with the faculty</p>
																<p>Case Studies</p>
															</div>
															<!--/.panel-body -->
														</div>
														<!--/.panel-collapse -->
													</div>
													<!-- /.panel -->
													<!-- End of BY PS academics 2.1-->
													<!-- BY PS academics 2.2-->

													<div class="panel panel-default faq">
														<div class="panel-heading">
															<h4 class="panel-title">
																<a data-toggle="collapse" aria-expanded="false"
																	data-parent="#193" href="#2002">How are the
																	programs delivered at NGA-SCE? </a>
															</h4>
														</div>
														<!--/.panel-heading -->
														<div id="2002" class="panel-collapse collapse out">
															<div class="panel-body faqAns">
																<p>NGA-SCE follows a blended model of academic
																	delivery. It involves conducting live online sessions,
																	lecture presentations, providing learning resources
																	like books, session plan and recordings of online
																	sessions</p>
															</div>
															<!--/.panel-body -->
														</div>
														<!--/.panel-collapse -->
													</div>
													<!-- /.panel -->
													<!-- End of BY PS academics 2.2-->
													<!-- BY PS academics 2.3-->

													<div class="panel panel-default faq">
														<div class="panel-heading">
															<h4 class="panel-title">
																<a data-toggle="collapse" aria-expanded="false"
																	data-parent="#193" href="#2003"> What is the
																	benefit of attending the online sessions? </a>
															</h4>
														</div>
														<!--/.panel-heading -->
														<div id="2003" class="panel-collapse collapse out">
															<div class="panel-body faqAns">
																<p>Online lectures cover the learning of the entire
																	course content as per session plan.</p>
																<p>Faculty&#39;s interact and collaborate with
																	students and thereby ensure enhanced student
																	engagement.</p>
																<p>Data sets are practiced with the student while
																	explaining the theoretical concept</p>
																<p>Can attend from work/home at your convenience</p>
																<p>Doubt clearing sessions</p>
															</div>
															<!--/.panel-body -->
														</div>
														<!--/.panel-collapse -->
													</div>
													<!-- /.panel -->
													<!-- End of BY PS academics 2.3-->
													<!-- Live lectures -->

													<div class="panel panel-default faq">
														<div class="panel-heading">
															<h4 class="panel-title">
																<a class="collapsed" data-toggle="collapse"
																	aria-expanded="false" data-parent="#193"
																	href="#livelec"> Live lectures </a>
															</h4>
														</div>
														<!--/.panel-heading -->

														<div id="livelec" class="panel-collapse collapse out">
															<div class="panel-body"
																style="border-bottom: solid 2px grey; border-left: solid 2px grey; border-right: solid 2px grey;">
																<div class="panel-group" id="nested">
																	<!-- BY PS academics 2.5.1-->

																	<div class="panel panel-default faq">
																		<div class="panel-heading">
																			<h4 class="panel-title">
																				<a data-toggle="collapse" aria-expanded="false"
																					data-parent="#livelec" href="#20051"> How to
																					attend live online lecture? </a>
																			</h4>
																		</div>
																		<!--/.panel-heading -->
																		<div id="20051" class="panel-collapse collapse out">
																			<div class="panel-body faqAns">
																				<p>In order to attend the live lectures follow
																					the given path in student portal:</p>
																				<p>Login to student portal &rarr; Academic
																					calendar (left hand side of portal) &rarr; select
																					the link from date &rarr; select attend session
																					from the pop up on the right hand side (you can
																					select the particular faculty whose lecture you
																					want to attend).</p>
																				<p>Download the guidelines available for first
																					time users</p>
																			</div>
																			<!--/.panel-body -->
																		</div>
																		<!--/.panel-collapse -->
																	</div>
																	<!-- /.panel -->
																	<!-- End of BY PS academics 2.5.1-->
																	<!-- BY PS academics 2.5.2-->

																	<div class="panel panel-default faq">
																		<div class="panel-heading">
																			<h4 class="panel-title">
																				<a data-toggle="collapse" aria-expanded="false"
																					data-parent="#livelec" href="#20052"> Can I
																					access live lectures on a mobile device\ cellphone?
																					&#42; &#42; Change in the website </a>
																			</h4>
																		</div>
																		<!--/.panel-heading -->
																		<div id="20052" class="panel-collapse collapse out">
																			<div class="panel-body faqAns">
																				<p>Yes, you can attend the online sessions on a
																					mobile device\ cellphone, we recommend that you use
																					Wi-Fi connection and not travel while attending
																					lectures.</p>
																				<p>We have a technical support team for
																					assistance during lectures, the number is
																					0008001001693.</p>
																				<p>&#42;We recommend you to use laptop or
																					desktop, as it will be convenient to practice data
																					sets</p>
																			</div>
																			<!--/.panel-body -->
																		</div>
																		<!--/.panel-collapse -->
																	</div>
																	<!-- /.panel -->
																	<!-- End of BY PS academics 2.5.2-->
																	<!-- BY PS academics 2.5.3-->

																	<div class="panel panel-default faq">
																		<div class="panel-heading">
																			<h4 class="panel-title">
																				<a data-toggle="collapse" aria-expanded="false"
																					data-parent="#livelec" href="#20053"> Can I
																					attend these sessions from my
																					home/workplace/office? </a>
																			</h4>
																		</div>
																		<!--/.panel-heading -->
																		<div id="20053" class="panel-collapse collapse out">
																			<div class="panel-body faqAns">
																				<p>Yes, you can attend these sessions from your
																					home/workplace/office subject to availability of
																					necessary IT infrastructure and firewall settings</p>
																			</div>
																			<!--/.panel-body -->
																		</div>
																		<!--/.panel-collapse -->
																	</div>
																	<!-- /.panel -->
																	<!-- End of BY PS academics 2.5.3-->
																	<!-- BY PS academics 2.5.4-->

																	<div class="panel panel-default faq">
																		<div class="panel-heading">
																			<h4 class="panel-title">
																				<a data-toggle="collapse" aria-expanded="false"
																					data-parent="#livelec" href="#20054"> Is it
																					compulsory to attend the live online lectures? </a>
																			</h4>
																		</div>
																		<!--/.panel-heading -->
																		<div id="20054" class="panel-collapse collapse out">
																			<div class="panel-body faqAns">
																				<p>No, Attendance is not mandatory to attend
																					lectures but highly recommended. In case you have
																					missed the lectures, you will have access to the
																					recordings 48 working hrs post commencement of
																					lectures.</p>
																			</div>
																			<!--/.panel-body -->
																		</div>
																		<!--/.panel-collapse -->
																	</div>
																	<!-- /.panel -->
																	<!-- End of BY PS academics 2.5.4-->
																	<!-- BY PS academics 2.5.5-->

																	<div class="panel panel-default faq">
																		<div class="panel-heading">
																			<h4 class="panel-title">
																				<a data-toggle="collapse" aria-expanded="false"
																					data-parent="#livelec" href="#20055"> Is there
																					any interaction with the faculty during live online
																					lectures? </a>
																			</h4>
																		</div>
																		<!--/.panel-heading -->
																		<div id="20055" class="panel-collapse collapse out">
																			<div class="panel-body faqAns">
																				<p>Yes, you have a chat option to interact with
																					the faculty during live online lectures, after
																					lectures you can still connect with faculty via
																					&#34;Post my query&#34; module, will be made
																					available under your Student Portal access.</p>
																			</div>
																			<!--/.panel-body -->
																		</div>
																		<!--/.panel-collapse -->
																	</div>
																	<!-- /.panel -->
																	<!-- End of BY PS academics 2.5.5-->
																	<!-- BY PS academics 2.5.6-->

																	<div class="panel panel-default faq">
																		<div class="panel-heading">
																			<h4 class="panel-title">
																				<a data-toggle="collapse" aria-expanded="false"
																					data-parent="#livelec" href="#20056"> I&#39;;m
																					getting &#34;Meeting not in progress&#34; error
																					while trying to access the live online lecture. </a>
																			</h4>
																		</div>
																		<!--/.panel-heading -->
																		<div id="20056" class="panel-collapse collapse out">
																			<div class="panel-body faqAns">
																				<p>This is not an error, kindly access the
																					lectures 15 minutes before the lecture starts to
																					avoid this message. You may book your seat for a
																					particular faculty lecture 1 hour prior to the
																					commencement of the lecture.</p>
																			</div>
																			<!--/.panel-body -->
																		</div>
																		<!--/.panel-collapse -->
																	</div>
																	<!-- /.panel -->
																	<!-- End of BY PS academics 2.5.6-->
																	<!-- BY PS academics 2.5.7-->

																	<div class="panel panel-default faq">
																		<div class="panel-heading">
																			<h4 class="panel-title">
																				<a data-toggle="collapse" aria-expanded="false"
																					data-parent="#livelec" href="#20057"> Where do
																					I view my E-books/course presentations of the
																					faculty? </a>
																			</h4>
																		</div>
																		<!--/.panel-heading -->
																		<div id="20057" class="panel-collapse collapse out">
																			<div class="panel-body faqAns">
																				<p>The E-books/course presentations of the
																					faculty will be available for Business Statistics
																					only in the student portal. Follow this path to
																					find the E-books/course presentations:</p>
																				<p>Login to student portal-> my courses (left
																					side of portal) &rarr; Select subject from drop
																					down whose lectures have been conducted &rarr;
																					Scroll down to learning resources.</p>
																				<p>For SAS subjects books will be provide before
																					the Academic cycle goes live.</p>
																			</div>
																			<!--/.panel-body -->
																		</div>
																		<!--/.panel-collapse -->
																	</div>
																	<!-- /.panel -->
																	<!-- End of BY PS academics 2.5.7-->
																	<!-- BY PS academics 2.5.8-->

																	<div class="panel panel-default faq">
																		<div class="panel-heading">
																			<h4 class="panel-title">
																				<a data-toggle="collapse" aria-expanded="false"
																					data-parent="#livelec" href="#20058"> What is
																					the duration and timings of the live sessions? </a>
																			</h4>
																		</div>
																		<!--/.panel-heading -->
																		<div id="20058" class="panel-collapse collapse out">
																			<div class="panel-body faqAns">
																				<p>Lectures are conducted live in a virtual
																					classroom environment. Each subject is taught via
																					live online sessions. Timings for the same is
																					updated in Academic Calendar. The information will
																					be sent Via Email and SMS 24hrs in advance.
																					Academic Calendar is updated on monthly basis.</p>
																			</div>
																			<!--/.panel-body -->
																		</div>
																		<!--/.panel-collapse -->
																	</div>
																	<!-- /.panel -->
																	<!-- End of BY PS academics 2.5.8-->

																</div>
															</div>
														</div>
													</div>
													<!-- End of Live lectures -->
													<!-- Recordings -->

													<div class="panel panel-default faq">
														<div class="panel-heading">
															<h4 class="panel-title">
																<a class="collapsed" data-toggle="collapse"
																	aria-expanded="false" data-parent="#193"
																	href="#recordings"> Recordings </a>
															</h4>
														</div>
														<!--/.panel-heading -->

														<div id="recordings" class="panel-collapse collapse out">
															<div class="panel-body"
																style="border-bottom: solid 2px grey; border-left: solid 2px grey; border-right: solid 2px grey;">
																<div class="panel-group" id="nested">
																	<!-- BY PS academics 2.6.1-->

																	<div class="panel panel-default faq">
																		<div class="panel-heading">
																			<h4 class="panel-title">
																				<a data-toggle="collapse" aria-expanded="false"
																					data-parent="#recordings" href="#20061"> When
																					will the recording of the live lectures be
																					uploaded? </a>
																			</h4>
																		</div>
																		<!--/.panel-heading -->
																		<div id="20061" class="panel-collapse collapse out">
																			<div class="panel-body faqAns">
																				<p>- The recordings are uploaded 48 working
																					hours after the online lecture are conducted</p>
																				<p>- Login to Student portal&rarr; My courses
																					(left side of portal) &rarr; Select subject from
																					drop down whose lectures have been conducted &rarr;
																					Scroll down to learning resource &rarr; View the
																					lecture recordings</p>
																				<p>- Download ARF player as the video files will
																					be supported by the same and can only be played on
																					a laptop or a desktop</p>
																			</div>
																			<!--/.panel-body -->
																		</div>
																		<!--/.panel-collapse -->
																	</div>
																	<!-- /.panel -->
																	<!-- End of BY PS academics 2.6.1-->
																	<!-- BY PS academics 2.6.2-->

																	<div class="panel panel-default faq">
																		<div class="panel-heading">
																			<h4 class="panel-title">
																				<a data-toggle="collapse" aria-expanded="false"
																					data-parent="#recordings" href="#20062"> How to
																					access recordings of lectures? </a>
																			</h4>
																		</div>
																		<!--/.panel-heading -->
																		<div id="20062" class="panel-collapse collapse out">
																			<div class="panel-body faqAns">
																				<p>In order to access recordings of lectures
																					follow the given path in student portal:</p>
																				<p>Login to student portal &rarr;My Courses
																					(left side of portal) &rarr; Select subject from
																					drop down whose lectures have been conducted &rarr;
																					Scroll down to learning resources &rarr; download
																					or stream the lecture</p>
																				<p>You can also view the same under Academic
																					Calendar</p>
																				<p>Download ARF player as the video files will
																					be supported by the same and can only be played on
																					a laptop or a desktop.</p>
																			</div>
																			<!--/.panel-body -->
																		</div>
																		<!--/.panel-collapse -->
																	</div>
																	<!-- /.panel -->
																	<!-- End of BY PS academics 2.6.2-->
																	<!-- BY PS academics 2.6.4-->

																	<div class="panel panel-default faq">
																		<div class="panel-heading">
																			<h4 class="panel-title">
																				<a data-toggle="collapse" aria-expanded="false"
																					data-parent="#recordings" href="#20064"> Can I
																					access Recordings on a mobile device/cellphone? </a>
																			</h4>
																		</div>
																		<!--/.panel-heading -->
																		<div id="20064" class="panel-collapse collapse out">
																			<div class="panel-body faqAns">
																				<p>No, you cannot access and view session
																					recordings on a mobile device\ cellphone. You can
																					only attend online sessions from the mobile
																					application. The session recordings can be viewed
																					and downloaded from the desk top and lap top using
																					an ARF player.</p>
																			</div>
																			<!--/.panel-body -->
																		</div>
																		<!--/.panel-collapse -->
																	</div>
																	<!-- /.panel -->
																	<!-- End of BY PS academics 2.6.4-->
																	<!-- BY PS academics 2.6.5-->

																	<div class="panel panel-default faq">
																		<div class="panel-heading">
																			<h4 class="panel-title">
																				<a data-toggle="collapse" aria-expanded="false"
																					data-parent="#recordings" href="#20065"> How
																					will I get my queries resolved when I&#39;;m
																					watching the recording of the lectures? </a>
																			</h4>
																		</div>
																		<!--/.panel-heading -->
																		<div id="20065" class="panel-collapse collapse out">
																			<div class="panel-body faqAns">
																				<p>Students have an option known as the
																					&#34;Post my query&#34; under Student portal &rarr;
																					Academic Calendar they can ask there queries and
																					the faculty will revert to them within 48 hours.</p>
																				<p>Also we have planned doubt clearing sessions
																					with faculties at regular intervals</p>
																				<p>Follow the path to post your query:</p>
																				<p>Login to student portal &rarr; Academic
																					calendar (left hand side of portal) &rarr; select
																					the link from date of session already done &rarr;
																					there would a pop up which has a post query option.
																				</p>
																			</div>
																			<!--/.panel-body -->
																		</div>
																		<!--/.panel-collapse -->
																	</div>
																	<!-- /.panel -->
																	<!-- End of BY PS academics 2.6.5-->
																	<!-- BY PS academics 2.6.6-->

																	<div class="panel panel-default faq">
																		<div class="panel-heading">
																			<h4 class="panel-title">
																				<a data-toggle="collapse" aria-expanded="false"
																					data-parent="#recordings" href="#20066"> How to
																					ask my queries to the faculty? </a>
																			</h4>
																		</div>
																		<!--/.panel-heading -->
																		<div id="20066" class="panel-collapse collapse out">
																			<div class="panel-body faqAns">
																				<p>You can ask your queries using the chat
																					option during live lecture and using &#34;Post a
																					query&#34; if have additional queries offline.</p>
																				<p>Follow the path to post your query:</p>
																				<p>Login to student portal &rarr; Academic
																					calendar (left hand side of portal) &rarr; select
																					the link from date of session already done &rarr;
																					there would a pop up which has a post query option.
																				</p>
																			</div>
																			<!--/.panel-body -->
																		</div>
																		<!--/.panel-collapse -->
																	</div>
																	<!-- /.panel -->
																	<!-- End of BY PS academics 2.6.6-->

																</div>
															</div>
														</div>
													</div>
													<!-- End of Recordings -->
													<!-- Recordings -->

													<div class="panel panel-default faq">
														<div class="panel-heading">
															<h4 class="panel-title">
																<a class="collapsed" data-toggle="collapse"
																	aria-expanded="false" data-parent="#193"
																	href="#Credentials"> Credentials </a>
															</h4>
														</div>
														<!--/.panel-heading -->

														<div id="Credentials" class="panel-collapse collapse out">
															<div class="panel-body"
																style="border-bottom: solid 2px grey; border-left: solid 2px grey; border-right: solid 2px grey;">
																<div class="panel-group" id="nested">

																	<!-- BY PS academics 2.7.1-->
																	<div class="panel panel-default faq">
																		<div class="panel-heading">
																			<h4 class="panel-title">
																				<a data-toggle="collapse" aria-expanded="false"
																					data-parent="#Credentials" href="#20071"> When
																					will I get my student portal credentials? </a>
																			</h4>
																		</div>
																		<!--/.panel-heading -->
																		<div id="20071" class="panel-collapse collapse out">
																			<div class="panel-body faqAns">
																				<p>Once your admission is confirmed you will
																					receive the credentials within 48 working hours on
																					your registered email ID</p>
																			</div>
																			<!--/.panel-body -->
																		</div>
																		<!--/.panel-collapse -->
																	</div>
																	<!-- /.panel -->
																	<!-- End of BY PS academics 2.7.1-->
																	<!-- BY PS academics 2.7.2-->
																	<div class="panel panel-default faq">
																		<div class="panel-heading">
																			<h4 class="panel-title">
																				<a data-toggle="collapse" aria-expanded="false"
																					data-parent="#Credentials" href="#20072"> How
																					do I login to my student zone portal once I&#39;;ve
																					taken admission for the distance learning program?
																				</a>
																			</h4>
																		</div>
																		<!--/.panel-heading -->
																		<div id="20072" class="panel-collapse collapse out">
																			<div class="panel-body faqAns">
																				<p>You are required to login to your student
																					zone portal with your Sap ID &#38; Password via the
																					link:</p>
																				<p>
																					<a
																						href="https://studentzone-ngasce.nmims.edu/studentportal/"
																						title="Login Page">
																						https://studentzone-ngasce.nmims.edu/studentportal/
																					</a>
																				</p>
																			</div>
																			<!--/.panel-body -->
																		</div>
																		<!--/.panel-collapse -->
																	</div>
																	<!-- /.panel -->
																	<!-- End of BY PS academics 2.7.2-->
																	<!-- BY PS academics 2.7.3-->
																	<div class="panel panel-default faq">
																		<div class="panel-heading">
																			<h4 class="panel-title">
																				<a data-toggle="collapse" aria-expanded="false"
																					data-parent="#Credentials" href="#20073">
																					I&#39;;m getting &#34;invalid credentials&#34;
																					while logging to the student portal. </a>
																			</h4>
																		</div>
																		<!--/.panel-heading -->
																		<div id="20073" class="panel-collapse collapse out">
																			<div class="panel-body faqAns">
																				<p>You are requested to kindly login to your
																					student zone portal with the correct Sap Id &#38;
																					password. In case you are receiving the message of
																					invalid credentials you are required to kindly use
																					forgot password option to login to your student
																					zone portal.</p>
																				<p>Once you use forgot password option the
																					updated password will be emailed on your registered
																					mail Id with which you can login to student zone.</p>
																			</div>
																			<!--/.panel-body -->
																		</div>
																		<!--/.panel-collapse -->
																	</div>
																	<!-- /.panel -->
																	<!-- End of BY PS academics 2.7.3-->
																	<!-- BY PS academics 2.7.4-->
																	<div class="panel panel-default faq">
																		<div class="panel-heading">
																			<h4 class="panel-title">
																				<a data-toggle="collapse" aria-expanded="false"
																					data-parent="#Credentials" href="#20074">
																					I&#39;;m getting &#34;Session expired&#34; while
																					logging to the student portal</a>
																			</h4>
																		</div>
																		<!--/.panel-heading -->
																		<div id="20074" class="panel-collapse collapse out">
																			<div class="panel-body faqAns">
																				<p>The error occurs due to few reason:</p>
																				<p>
																				<ol type="1">
																					<li>Students need to fill in the feedback form
																						and proceed to the portal and not skip it.</li>
																					<li>Please change the password through student
																						zone > Quick links> Change password.</li>
																					<li>If you have done that already please check
																						your connectivity and firewall settings.</li>
																				</ol>
																				</p>
																			</div>
																			<!--/.panel-body -->
																		</div>
																		<!--/.panel-collapse -->
																	</div>
																	<!-- /.panel -->
																	<!-- End of BY PS academics 2.7.4-->
																	<!-- BY PS academics 2.7.5-->
																	<div class="panel panel-default faq">
																		<div class="panel-heading">
																			<h4 class="panel-title">
																				<a data-toggle="collapse" aria-expanded="false"
																					data-parent="#Credentials" href="#20075"> In
																					case my registered e-mail id is incorrect while I
																					have taken admission for the distance learning
																					program what is the process to update my e-mail Id?
																				</a>
																			</h4>
																		</div>
																		<!--/.panel-heading -->
																		<div id="20075" class="panel-collapse collapse out">
																			<div class="panel-body faqAns">
																				<p>If you have updated your e-mail id
																					incorrectly at the time of your admission, you need
																					to provide us with the updated e-mail id with the
																					below supporting documents.</p>
																				<p>Billing address proof copy as updated in our
																					system & record</p>
																				<p>Government ID card proof</p>
																				<p>On verification of the above documents, the
																					e-mail id will be updated in our system & you will
																					be able to login to your student zone portal
																					successfully.</p>
																			</div>
																			<!--/.panel-body -->
																		</div>
																		<!--/.panel-collapse -->
																	</div>
																	<!-- /.panel -->
																	<!-- End of BY PS academics 2.7.5-->
																	<!-- BY PS academics 2.7.6-->
																	<div class="panel panel-default faq">
																		<div class="panel-heading">
																			<h4 class="panel-title">
																				<a data-toggle="collapse" aria-expanded="false"
																					data-parent="#Credentials" href="#20076"> How
																					do I update my e-mail Id /shipping address and
																					mobile number? </a>
																			</h4>
																		</div>
																		<!--/.panel-heading -->
																		<div id="20076" class="panel-collapse collapse out">
																			<div class="panel-body faqAns">
																				<p>You can update your e-mail id
																					/mobile/shipping address from the student zone via
																					the update profile link tab.</p>
																			</div>
																			<!--/.panel-body -->
																		</div>
																		<!--/.panel-collapse -->
																	</div>
																	<!-- /.panel -->
																	<!-- End of BY PS academics 2.7.6-->

																</div>
															</div>
														</div>
													</div>
													<!-- End of Credentials -->
													<!-- Digital library -->

													<div class="panel panel-default faq">
														<div class="panel-heading">
															<h4 class="panel-title">
																<a class="collapsed" data-toggle="collapse"
																	aria-expanded="false" data-parent="#193"
																	href="#Digital"> Digital library </a>
															</h4>
														</div>
														<!--/.panel-heading -->

														<div id="Digital" class="panel-collapse collapse out">
															<div class="panel-body"
																style="border-bottom: solid 2px grey; border-left: solid 2px grey; border-right: solid 2px grey;">
																<div class="panel-group" id="nested">

																	<!-- BY PS academics 2.9.1-->
																	<div class="panel panel-default faq">
																		<div class="panel-heading">
																			<h4 class="panel-title">
																				<a data-toggle="collapse" aria-expanded="false"
																					data-parent="#Digital" href="#20091"> What are
																					the features of online Digital Library? </a>
																			</h4>
																		</div>
																		<!--/.panel-heading -->
																		<div id="20091" class="panel-collapse collapse out">
																			<div class="panel-body faqAns">
																				<p>Digital Library facilities are provided for
																					students who are willing to learn beyond books and
																					their registered subjects. Users can access full
																					text journals online. The contents have been
																					organized in groups for easy access. The search
																					interface allows easy navigation. Students can
																					access our Digital Library round the clock. Digital
																					library provides user-friendly interfaces to its
																					resources; access to journals, databases, eBooks
																					database, research database, company databases etc.
																				</p>
																			</div>
																			<!--/.panel-body -->
																		</div>
																		<!--/.panel-collapse -->
																	</div>
																	<!-- /.panel -->
																	<!-- End of BY PS academics 2.9.1-->

																</div>
															</div>
														</div>
													</div>

													<!-- End of Digital library -->
												</div>
											</div>
										</div>

										<!--    Logistics -->

										<div class="panel panel-default faq">
											<div class="panel-heading">
												<h4 class="panel-title">
													<a class="collapsed" data-toggle="collapse"
														aria-expanded="false" data-parent="#accordion"
														href="#Logistics"> Logistics </a>
												</h4>
											</div>
											<!--/.panel-heading -->

											<div id="Logistics" class="panel-collapse collapse out">
												<div class="panel-body">
													<div class="panel-group" id="nested">

														<!-- BY PS academics 2.10.1-->
														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#Logistics" href="#200101"> When will
																		I get my study kit? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="200101" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>Once your admission is confirmed &#38; your
																		student number is issued to you, you will receive your
																		study kit &#38; your course material applicable for
																		your program within 10 working days&#39; time. The
																		study kit will be dispatched either at your shipping
																		address or your authorized enrolment partner which you
																		chosen while taking your admission.</p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->
														<!-- End of BY PS academics 2.10.1-->
														<!-- BY PS academics 2.10.2-->
														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#Logistics" href="#200102"> How will
																		I receive my ID card and fee receipt? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="200102" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>Welcome Kit will be part of your Study kit,
																		which includes [ID card, Fee receipt, Welcome letter
																		and Student Undertaking].</p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->
														<!-- End of BY PS academics 2.10.2-->
														<!-- BY PS academics 2.10.3-->
														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#Logistics" href="#200103"> Where do
																		I need to submit the student declaration form once I
																		have received the same in my study kit? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="200103" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>You have to submit the original student
																		undertaking form to your authorized enrollment partner
																		that you have opted at the time of your admission</p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->
														<!-- End of BY PS academics 2.10.3-->
														<!-- BY PS academics 2.10.4-->
														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#Logistics" href="#200104"> I have
																		misplaced my study kit of a particular semester what
																		can be done? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="200104" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>If you have misplaced your study kit you will be
																		required to apply for your duplicate study kit for the
																		particular semester by placing a service request via
																		your student zone portal. The study kit charges will
																		be applicable. You can make the online payment via the
																		student zone portal for the same.</p>
																	<p>Follow the path to raise a request for study
																		kit:</p>
																	<p>Log in to student portal &rarr; Student support
																		&rarr; service request &rarr; Re-Dispatch of Study
																		kit.</p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->
														<!-- End of BY PS academics 2.10.4-->
														<!-- BY PS academics 2.10.5-->
														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#Logistics" href="#200105"> How do I
																		opt to receive my study material after I complete my
																		admission procedure? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="200105" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>While you are proceeding with your semester
																		payment via the Apply now link tab which is updated on
																		the website via the Apply now link tab:
																		http://ngasce.force.com/nmLogin_New?type=registration,
																		you can either opt to receive the study material at
																		your shipping address or your authorized enrolment
																		partner. Once you have chosen the respective the study
																		material will be dispatched accordingly.</p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->
														<!-- End of BY PS academics 2.10.5-->
														<!-- BY PS academics 2.10.6-->
														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#Logistics" href="#200106"> In case I
																		want to change my name in my student identity card,
																		what is the process for the same? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="200106" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>In case you have changed your name via the
																		service link which is updated in your student zone
																		portal under the student support link tab. You will be
																		required to apply for duplicate ID card with the
																		revised /updated name. The duplicate id charges with
																		revised name is RS 200/-You can make the online
																		payment towards the same.</p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->
														<!-- End of BY PS academics 2.10.6-->
														<!-- BY PS academics 2.10.7-->
														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#Logistics" href="#200107"> Where do
																		I need to collect my semester fee receipts once I have
																		taken admission for our distance learning programs? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="200107" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>Students are required to kindly get in touch
																		with your authorized enrollment partner whom you are
																		mapped to confirm receipt of your semester fee
																		receipts.</p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->
														<!-- End of BY PS academics 2.10.7-->

													</div>
												</div>
											</div>
										</div>
										<!-- End of Logistics   -->

										<!-- End of BY PS academics -->
										<!--/.panel-->

										<div class="panel panel-default faq">
											<div class="panel-heading">
												<h4 class="panel-title">
													<a class="collapsed" data-toggle="collapse"
														aria-expanded="false" data-parent="#accordion" href="#194">
														Exams </a>
												</h4>
											</div>
											<!--/.panel-heading -->

											<div id="194" class="panel-collapse collapse out">
												<div class="panel-body">
													<div class="panel-group" id="nested">

														<!-- BY PS 3.x -->
														<!-- BY PS 3.1 -->
														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#194" href="#3001"> What is the Exam
																		Pattern for Data visualization and Machine Learning? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="3001" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>There are no Assignments, only Term end exams
																		applicable for both the programs. Pattern will be MCQ
																		and based on the curriculum taught during the Academic
																		Cycle</p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->

														<!--End of BY PS 3.1 -->
														<!-- BY PS 3.2 -->
														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#194" href="#3002"> What is the
																		process of Applying or Registering for Examinations? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="3002" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>The student will have to register online for
																		appearing for the Examination when the window for Exam
																		Registration opens. The exam registration is on first
																		come first serve basis when the online registration
																		window goes live (will be communicated via
																		Email/SMS/Announcement section). Student must not wait
																		till last minute for exam registration as it could
																		lead to unavailability of preferable exam Centre/time
																		slot. To avoid missing out any important
																		information/announcement/date, students must refer
																		regularly to our Website/Student zone as all latest
																		announcements are mentioned under Student Zone</p>
																	<p>Student portal &rarr; Exams &rarr; Exam
																		Registration</p>
																	<p>&#42;NOTE: Exam registration is part of the
																		Program fees</p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->

														<!--End of BY PS 3.2 -->
														<!-- BY PS 3.3 -->
														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#194" href="#3003"> What happens if
																		students misses the examination after registering for
																		the exam? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="3003" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>You will have multiple opportunities to appear
																		for the exam during the validity phase</p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->

														<!--End of BY PS 3.3 -->
														<!-- BY PS 3.4 -->
														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#194" href="#3004"> What is the
																		Passing criteria? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="3004" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>To be eligible for being declared as
																		&#34;Pass&#34; in any course/subject, student is
																		required to obtain 50&#37; marks on the aggregate of
																		marks obtained in the Term End Examination. Please
																		Note: There is no individual cut-off or Individual
																		Passing Criteria. Aggregate marks: 50/100 in each
																		subject, appearance in both components is mandatory</p>
																	<p>Student Portal &rarr; Exams &rarr; Exams results
																		&rarr; Pass/fail status</p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->

														<!--End of BY PS 3.4 -->
														<!-- BY PS 3.5 -->
														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#194" href="#3005"> What if I do not
																		clear either of the subjects or both subjects? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="3005" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>Student gets a Certificate of Participation if
																		they are not able to clear either of the subjects or
																		both the subjects, appearance for both subjects is
																		mandatory</p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->

														<!--End of BY PS 3.5 -->
														<!-- BY PS 3.6 -->
														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#194" href="#3006"> Can student apply
																		for re-valuation? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="3006" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>No, revaluation is not applicable for these
																		programs as they are all multiple choice questions.</p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->

														<!--End of BY PS 3.6 -->
														<!-- BY PS 3.7 -->
														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#194" href="#3007"> When is student
																		given grace marks? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="3007" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>There is not grace marks policy applicable for
																		these Programs</p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->

														<!--End of BY PS 3.7 -->

														<!-- BY PS 3.8 -->
														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#194" href="#3008"> When are the
																		results declared? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="3008" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>The results are generally declared within 2-3
																		working days after the examinations under student zone
																	</p>
																	<p>
																		Link: <a
																			href="https://studentzone-ngasce.nmims.edu/studentzone/Exams">https://studentzone-ngasce.nmims.edu/studentzone/Exams</a>
																	</p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->

														<!--End of BY PS 3.8 -->

														<!-- BY PS 3.9 -->
														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#194" href="#3009"> How can a student
																		view his/her previous marks? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="3009" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>Link:
																		https://studentzone-ngasce.nmims.edu/studentzone
																		&rarr;Exams &rarr; Marks History</p>

																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->

														<!--End of BY PS 3.9 -->


														<!-- Exam registration -->

														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a class="collapsed" data-toggle="collapse"
																		aria-expanded="false" data-parent="#193"
																		href="#registration"> Exam Registration </a>
																</h4>
															</div>
															<!--/.panel-heading -->

															<div id="registration"
																class="panel-collapse collapse out">
																<div class="panel-body"
																	style="border-bottom: solid 2px grey; border-left: solid 2px grey; border-right: solid 2px grey;">
																	<div class="panel-group" id="nested">

																		<!-- BY PS exams 3.9.1-->
																		<div class="panel panel-default faq">
																			<div class="panel-heading">
																				<h4 class="panel-title">
																					<a data-toggle="collapse" aria-expanded="false"
																						data-parent="#registration" href="#30091">
																						Slots not available while registering for the
																						exams even though the window is live? </a>
																				</h4>
																			</div>
																			<!--/.panel-heading -->
																			<div id="30091" class="panel-collapse collapse out">
																				<div class="panel-body faqAns">
																					<p>Exam registration is done on first come
																						first serve basis, so if a student is not able to
																						see any slots available for that particular time
																						or day it&#39;;s because that the slots are full
																						for that day and time.</p>
																				</div>
																				<!--/.panel-body -->
																			</div>
																			<!--/.panel-collapse -->
																		</div>
																		<!-- /.panel -->
																		<!-- End of BY PS exam 3.9.1-->
																		<!-- BY PS exams 3.9.2-->
																		<div class="panel panel-default faq">
																			<div class="panel-heading">
																				<h4 class="panel-title">
																					<a data-toggle="collapse" aria-expanded="false"
																						data-parent="#registration" href="#30092">
																						Fees applicable for Exam registration? </a>
																				</h4>
																			</div>
																			<!--/.panel-heading -->
																			<div id="30092" class="panel-collapse collapse out">
																				<div class="panel-body faqAns">
																					<p>Exam fees and number of attempts is part of
																						the program fees</p>
																				</div>
																				<!--/.panel-body -->
																			</div>
																			<!--/.panel-collapse -->
																		</div>
																		<!-- /.panel -->
																		<!-- End of BY PS exam 3.9.2-->
																		<!-- BY PS exams 3.9.3-->
																		<div class="panel panel-default faq">
																			<div class="panel-heading">
																				<h4 class="panel-title">
																					<a data-toggle="collapse" aria-expanded="false"
																						data-parent="#registration" href="#30093">
																						What is the process of applying or registering for
																						Examinations? </a>
																				</h4>
																			</div>
																			<!--/.panel-heading -->
																			<div id="30093" class="panel-collapse collapse out">
																				<div class="panel-body faqAns">
																					<p>The student will have to register online for
																						appearing for the Term End Examination when the
																						window for Exam Registration opens. The exam
																						registration is on first come first serve basis
																						when the online registration window goes live
																						(will be communicated via Email/SMS/Announcement
																						section). Student must not wait till last minute
																						for exam registration as it could lead to
																						unavailability of preferable exam Centre/time
																						slot. To avoid missing out any important
																						information/announcement/date, students must refer
																						regularly to our Website/Student zone as all
																						latest announcements are mentioned under Student
																						Zone</p>
																				</div>
																				<!--/.panel-body -->
																			</div>
																			<!--/.panel-collapse -->
																		</div>
																		<!-- /.panel -->
																		<!-- End of BY PS exam 3.9.3-->
																		<!-- BY PS exams 3.9.4-->
																		<div class="panel panel-default faq">
																			<div class="panel-heading">
																				<h4 class="panel-title">
																					<a data-toggle="collapse" aria-expanded="false"
																						data-parent="#registration" href="#30094">
																						Students enrolled in which program qualify for the
																						Re-Sit Exam? </a>
																				</h4>
																			</div>
																			<!--/.panel-heading -->
																			<div id="30094" class="panel-collapse collapse out">
																				<div class="panel-body faqAns">
																					<p>For both Programs you qualify for Re-sit
																						exams</p>
																					<p>Business statistics you have three attempts,
																						Data visualization and Machine Learning you have
																						two attempts</p>
																				</div>
																				<!--/.panel-body -->
																			</div>
																			<!--/.panel-collapse -->
																		</div>
																		<!-- /.panel -->
																		<!-- End of BY PS exam 3.9.4-->
																		<!-- BY PS exams 3.9.5-->
																		<div class="panel panel-default faq">
																			<div class="panel-heading">
																				<h4 class="panel-title">
																					<a data-toggle="collapse" aria-expanded="false"
																						data-parent="#registration" href="#30095">
																						Does student need to register for Re-Sit Term End
																						Examination? </a>
																				</h4>
																			</div>
																			<!--/.panel-heading -->
																			<div id="30095" class="panel-collapse collapse out">
																				<div class="panel-body faqAns">
																					<p>Yes, the student needs to register online
																						for the Re-Sit Term End Examination. The exam
																						registration is on first come first serve basis
																						when the online window for Re-Sit Term End Exam
																						opens. Student must not wait till last minute for
																						exam registration as it could lead to
																						unavailability of preferable exam centre/date/time
																						slot as seats are limited</p>
																				</div>
																				<!--/.panel-body -->
																			</div>
																			<!--/.panel-collapse -->
																		</div>
																		<!-- /.panel -->
																		<!-- End of BY PS exam 3.9.5-->
																		<!-- BY PS exams 3.9.6 -->
																		<div class="panel panel-default faq">
																			<div class="panel-heading">
																				<h4 class="panel-title">
																					<a data-toggle="collapse" aria-expanded="false"
																						data-parent="#registration" href="#30096"> Can
																						I give Exams from abroad? </a>
																				</h4>
																			</div>
																			<!--/.panel-heading -->
																			<div id="30096" class="panel-collapse collapse out">
																				<div class="panel-body faqAns">
																					<p>No, we done&#39;t conduct exams abroad, we
																						have our examination Centre&#39;s in India</p>
																				</div>
																				<!--/.panel-body -->
																			</div>
																			<!--/.panel-collapse -->
																		</div>
																		<!-- /.panel -->
																		<!-- End of BY PS exam 3.9.6-->
																	</div>
																</div>
															</div>
														</div>
														<!-- End of Exam registration -->


														<!-- Global Certification -->

														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a class="collapsed" data-toggle="collapse"
																		aria-expanded="false" data-parent="#194"
																		href="#GlobalCertification"> Global Certification:
																	</a>
																</h4>
															</div>
															<!--/.panel-heading -->

															<div id="GlobalCertification"
																class="panel-collapse collapse out">
																<div class="panel-body"
																	style="border-bottom: solid 2px grey; border-left: solid 2px grey; border-right: solid 2px grey;">
																	<div class="panel-group" id="nested">

																		<!-- BY PS Project 3.10.1 -->
																		<div class="panel panel-default faq">
																			<div class="panel-heading">
																				<h4 class="panel-title">
																					<a data-toggle="collapse" aria-expanded="false"
																						data-parent="#GlobalCertification" href="#300101">
																						What is Global Certification? </a>
																				</h4>
																			</div>
																			<!--/.panel-heading -->
																			<div id="300101" class="panel-collapse collapse out">
																				<div class="panel-body faqAns">
																					<p>Global Certification is a recognition which
																						is helpful globally adding a lot of value and
																						weightage to your professional profile. Global
																						Certification gives the student global recognition
																						in the SAS tool and adds your name in the SAS
																						directory. While you clear Global
																						Certification,you will be tagged with an E-badge
																						by SAS on your LinkedIn profile.</p>
																				</div>
																				<!--/.panel-body -->
																			</div>
																			<!--/.panel-collapse -->
																		</div>
																		<!-- /.panel -->
																		<!-- End of BY PS Project 3.10.1-->
																		<!-- BY PS Project 3.10.2 -->
																		<div class="panel panel-default faq">
																			<div class="panel-heading">
																				<h4 class="panel-title">
																					<a data-toggle="collapse" aria-expanded="false"
																						data-parent="#GlobalCertification" href="#300102">
																						How much do I pay for Global Certification? </a>
																				</h4>
																			</div>
																			<!--/.panel-heading -->
																			<div id="300102" class="panel-collapse collapse out">
																				<div class="panel-body faqAns">
																					<p>Global Certification is free of cost if you
																						are doing this course through NMIMS</p>
																				</div>
																				<!--/.panel-body -->
																			</div>
																			<!--/.panel-collapse -->
																		</div>
																		<!-- /.panel -->
																		<!-- End of BY PS Project 3.10.2-->
																		<!-- BY PS Project 3.10.3 -->
																		<div class="panel panel-default faq">
																			<div class="panel-heading">
																				<h4 class="panel-title">
																					<a data-toggle="collapse" aria-expanded="false"
																						data-parent="#GlobalCertification" href="#300103">
																						How many attempts do I get for Global
																						Certification? </a>
																				</h4>
																			</div>
																			<!--/.panel-heading -->
																			<div id="300103" class="panel-collapse collapse out">
																				<div class="panel-body faqAns">
																					<p>Only one attempt for Global Certification</p>
																				</div>
																				<!--/.panel-body -->
																			</div>
																			<!--/.panel-collapse -->
																		</div>
																		<!-- /.panel -->
																		<!-- End of BY PS Project 3.10.3-->
																		<!-- BY PS Project 3.10.4 -->
																		<div class="panel panel-default faq">
																			<div class="panel-heading">
																				<h4 class="panel-title">
																					<a data-toggle="collapse" aria-expanded="false"
																						data-parent="#GlobalCertification" href="#300104">
																						How do I prepare for Global Certification? </a>
																				</h4>
																			</div>
																			<!--/.panel-heading -->
																			<div id="300104" class="panel-collapse collapse out">
																				<div class="panel-body faqAns">
																					<p>We will be conducting a prep session for
																						Global Certification, post which the exams for
																						Global Certification will be conducted at SAS
																						authorized centers</p>
																				</div>
																				<!--/.panel-body -->
																			</div>
																			<!--/.panel-collapse -->
																		</div>
																		<!-- /.panel -->
																		<!-- End of BY PS Project 3.10.4-->
																		<!-- BY PS Project 3.10.5 -->
																		<div class="panel panel-default faq">
																			<div class="panel-heading">
																				<h4 class="panel-title">
																					<a data-toggle="collapse" aria-expanded="false"
																						data-parent="#GlobalCertification" href="#300105">
																						Passing criteria with Global Certification? </a>
																				</h4>
																			</div>
																			<!--/.panel-heading -->
																			<div id="300105" class="panel-collapse collapse out">
																				<div class="panel-body faqAns">
																					<p>You need to score a minimum of 70 marks to
																						clear the Global Certification</p>
																				</div>
																				<!--/.panel-body -->
																			</div>
																			<!--/.panel-collapse -->
																		</div>
																		<!-- /.panel -->
																		<!-- End of BY PS Project 3.10.5-->
																		<!-- BY PS Project 3.10.6 -->
																		<div class="panel panel-default faq">
																			<div class="panel-heading">
																				<h4 class="panel-title">
																					<a data-toggle="collapse" aria-expanded="false"
																						data-parent="#GlobalCertification" href="#300106">
																						How much do I pay for Global Certification? </a>
																				</h4>
																			</div>
																			<!--/.panel-heading -->
																			<div id="300106" class="panel-collapse collapse out">
																				<div class="panel-body faqAns">
																					<p>Yes, you need to score minimum 70 marks in
																						Data Visualisation/Machine Learning in order to
																						qualify and appear for Global Certification</p>
																				</div>
																				<!--/.panel-body -->
																			</div>
																			<!--/.panel-collapse -->
																		</div>
																		<!-- /.panel -->
																		<!-- End of BY PS Project 3.10.6-->

																	</div>
																</div>
															</div>
														</div>
														<!-- End of  Global Certification -->

														<!-- Documents -->

														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a class="collapsed" data-toggle="collapse"
																		aria-expanded="false" data-parent="#194"
																		href="#Documents"> Documents </a>
																</h4>
															</div>
															<!--/.panel-heading -->

															<div id="Documents" class="panel-collapse collapse out">
																<div class="panel-body"
																	style="border-bottom: solid 2px grey; border-left: solid 2px grey; border-right: solid 2px grey;">
																	<div class="panel-group" id="nested">

																		<!-- BY PS Documents 3.11.1 -->
																		<div class="panel panel-default faq">
																			<div class="panel-heading">
																				<h4 class="panel-title">
																					<a data-toggle="collapse" aria-expanded="false"
																						data-parent="#Documents" href="#300111"> Can I
																						have full name of my parents /spouse name on my
																						final certificate? </a>
																				</h4>
																			</div>
																			<!--/.panel-heading -->
																			<div id="300111" class="panel-collapse collapse out">
																				<div class="panel-body faqAns">
																					<p>Only the first name of both the parents will
																						be reflected on the mark sheets and final
																						certificate.</p>
																				</div>
																				<!--/.panel-body -->
																			</div>
																			<!--/.panel-collapse -->
																		</div>
																		<!-- /.panel -->
																		<!-- End of BY PS Documents 3.11.1-->
																		<!-- BY PS Documents 3.11.2 -->
																		<div class="panel panel-default faq">
																			<div class="panel-heading">
																				<h4 class="panel-title">
																					<a data-toggle="collapse" aria-expanded="false"
																						data-parent="#Documents" href="#300112"> How
																						do I get my mark sheets and Final certificate? </a>
																				</h4>
																			</div>
																			<!--/.panel-heading -->
																			<div id="300112" class="panel-collapse collapse out">
																				<div class="panel-body faqAns">
																					<p>Students who want a hard copy of the mark
																						sheet need to raise a Service request through
																						student portal</p>
																					<p>Student portal &rarr; Student support &rarr;
																						Service Request &rarr; Issuance of mark sheet /
																						Final certificate.</p>
																					<p>You can opt to get the same delivered to you
																						shipping address or collect the same from your
																						regional office. You will get a confirmation email
																						once the documents are generated.</p>
																					<p>*Charges applicable wherever required</p>
																				</div>
																				<!--/.panel-body -->
																			</div>
																			<!--/.panel-collapse -->
																		</div>
																		<!-- /.panel -->
																		<!-- End of BY PS Documents 3.11.2 -->
																		<!-- BY PS Documents 3.11.3 -->
																		<div class="panel panel-default faq">
																			<div class="panel-heading">
																				<h4 class="panel-title">
																					<a data-toggle="collapse" aria-expanded="false"
																						data-parent="#Documents" href="#300113"> Will
																						Distance learning be mentioned on the final
																						Certificate/ Mark sheets? </a>
																				</h4>
																			</div>
																			<!--/.panel-heading -->
																			<div id="300113" class="panel-collapse collapse out">
																				<div class="panel-body faqAns">
																					<p>Distance learning will not be mentioned on
																						the final certificate. You will be issued with the
																						final certificate from NMIMS Global Access School
																						for Continuing Education.</p>
																				</div>
																				<!--/.panel-body -->
																			</div>
																			<!--/.panel-collapse -->
																		</div>
																		<!-- /.panel -->
																		<!-- End of BY PS Documents 3.11.3-->
																		<!-- BY PS Documents 3.11.4 -->
																		<div class="panel panel-default faq">
																			<div class="panel-heading">
																				<h4 class="panel-title">
																					<a data-toggle="collapse" aria-expanded="false"
																						data-parent="#Documents" href="#300114"> What
																						is the process of getting a "Transcript"? </a>
																				</h4>
																			</div>
																			<!--/.panel-heading -->
																			<div id="300114" class="panel-collapse collapse out">
																				<div class="panel-body faqAns">
																					<p>A student will have to raise a Service
																						request for Transcript. Three copies of Transcript
																						will be issued. For every additional extra copy
																						there will be charge of Rs. 300/-</p>
																					<p>Link:
																						https://studentzone-ngasce.nmims.edu/studentzone/
																						&rarr; Student Support &rarr; Service request</p>
																				</div>
																				<!--/.panel-body -->
																			</div>
																			<!--/.panel-collapse -->
																		</div>
																		<!-- /.panel -->
																		<!-- End of BY PS Documents 3.11.4-->

																	</div>
																</div>
															</div>
														</div>
														<!-- End of  Documents -->

														<!-- End of BY PS 3.x -->
														<!--/.panel-->
													</div>
												</div>
											</div>
										</div>
									</div>

								</div>
							</div>
						</div>
						<div class="clearfix"></div>
					</div>
					<div class="clearfix"></div>

				</div>
			</div>


		</div>
	</div>

	<jsp:include page="../adminCommon/footer.jsp" />

	<script>
		 $('#search-criteria').on('input',function(e){
			 $('#search-criteria').css("background-color","white");
			$('.panel-body').css("background-color","white");
            $('.panel-collapse').removeClass('in');
			$('.panel-collapse').addClass('out');
			
            var txt = $('#search-criteria').val();
			if(txt == ''){
				$('.panel-collapse').removeClass('in');
				$('.panel-collapse').addClass('out');
				$('#search-criteria').css("background-color","white");
																	
			}else{
				$('.panel-body').each(function(){
				   if($(this).text().toUpperCase().indexOf(txt.toUpperCase()) != -1){
					   $('#search-criteria').css("background-color","#B8F1B8");
					   $(this).css("background-color","lightgrey");
					   $(this).parent('.panel-collapse').removeClass('out');
					   $(this).parent('.panel-collapse').addClass('in');
				   }
				});
			}
            

        });

		  </script>
</body>
</html>