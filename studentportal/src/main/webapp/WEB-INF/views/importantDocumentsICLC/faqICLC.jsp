
<!DOCTYPE html>


<html lang="en">

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<jsp:include page="../adminCommon/jscss.jsp">
	<jsp:param value="FAQ's" name="title" />
</jsp:include>



<body>

	<%@ include file="../adminCommon/header.jsp"%>
	<div class="sz-main-content-wrapper">

		<jsp:include page="../adminCommon/breadcrum.jsp">
			<jsp:param value="Important Documents;FAQ's" name="breadcrumItems" />
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
								<div id="All" class="supportFaq" style="display: none;">
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
																		data-parent="#192" href="#007"> How to apply for
																		the program? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="007" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>Enrolment with distance learning program can be
																		done using the following link:</p>
																	<p>
																		<a
																			href="http://ngasce.force.com/nmLogin_New?type=registration"
																			title="Apply for Program Here">
																			http://ngasce.force.com/nmLogin_New?type=registration
																		</a>
																	</p>
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
																		eligibility criteria for PG diploma programs? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="008" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>A Bachelor's Degree in any discipline from any
																		recognized University or equivalent degree recognized
																		by Association of Indian Universities (AIU) with
																		minimum 50% marks at Graduation level OR
																		Bachelor&#39;s Degree in any discipline from any
																		recognized University or equivalent degree recognized
																		by Association of Indian Universities (AIU) with less
																		than 50% marks at Graduation level and minimum 2 Years
																		work experience.</p>
																	<p>Check the following link to check various PG
																		diploma programs.</p>
																	<p>
																		<a
																			href="http://distance.nmims.edu/program/post-graduate-diploma-programs"
																			title="PG diploma programs">
																			http://distance.nmims.edu/program/post-graduate-diploma-programs
																		</a>
																	</p>
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
																		data-parent="#192" href="#009"> What is the
																		eligibility criteria for diploma programs? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="009" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>Bachelor's Degree in any discipline from any
																		recognized University or equivalent degree recognized
																		by Association of Indian Universities (AIU)</p>
																	<p>OR</p>
																	<p>H.S.C plus 2 years of work experience OR S.S.C
																		plus 3 years of Diploma recognized by AICTE and 2
																		years of work experience.</p>
																	<p>
																		<a
																			href="http://distance.nmims.edu/program/diploma-programs"
																			title="diploma-programs">
																			http://distance.nmims.edu/program/diploma-programs </a>
																	</p>
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
																		data-parent="#192" href="#010"> What is the
																		eligibility criteria for Advance
																		Certificate/Certificate programs? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="010" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>H.S.C OR S.S.C plus 2 years of work experience,
																		click on the below link for detailed information</p>
																	<p>
																		<a
																			href="http://distance.nmims.edu/program/certificate-programs"
																			title="certificate-programs">
																			http://distance.nmims.edu/program/certificate-programs
																		</a>
																	</p>

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
																		data-parent="#192" href="#011"> What are the
																		documents required for enrolling into a PG diploma
																		program with NGASCE-NMIMS? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="011" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>Below link provides you with detailed
																		information on document requirements</p>
																	<p>
																		<a
																			href="http://distance.nmims.edu/admission-process.html#Eligibility-Criteria"
																			title="Eligibility-Criteria">
																			http://distance.nmims.edu/admission-process.html#Eligibility-Criteria
																		</a>
																	</p>

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
																		data-parent="#192" href="#012"> What are the
																		documents required for enrolling into a Diploma
																		program with NGASCE-NMIMS? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="012" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>Below link provides you with detailed
																		information on document requirements</p>
																	<p>
																		<a
																			href="http://distance.nmims.edu/admission-process.html#Eligibility-Criteria"
																			title="Eligibility-Criteria">
																			http://distance.nmims.edu/admission-process.html#Eligibility-Criteria
																		</a>
																	</p>

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
																		data-parent="#192" href="#012"> What are the
																		documents required for enrolling into Advance
																		certificate / Certificate programs with NGASCE-NMIMS </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="012" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>The documents required as per eligibility
																		criteria</p>
																	<p>
																		<a
																			href="http://distance.nmims.edu/admission-process.html#Eligibility-Criteria"
																			title="Eligibility-Criteria">
																			http://distance.nmims.edu/admission-process.html#Eligibility-Criteria
																		</a>
																	</p>

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
																		data-parent="#192" href="#013"> Where do I require
																		to submit my admission documents? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="013" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>It is required to submit the attached admission
																		document to your authorized enrolment partner to
																		complete your admission procedure</p>

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
																		data-parent="#192" href="#014"> Does NGASCE- NMIMS
																		provide dual specialization? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="014" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>No, we do not offer dual specialization as a
																		part of our program.</p>

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
																		data-parent="#192" href="#015"> Can I pursue more
																		than one course simultaneously from NGA-SCE? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="015" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>No, student cannot pursue more than one course
																		from a University simultaneously.. However post
																		completion of any program you can enroll for any other
																		new Program</p>

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
																		data-parent="#192" href="#016"> I have completed
																		my Diploma /PG diploma program am I eligible to take
																		another Diploma /PG Diploma program? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="016" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>Yes, Lateral entry will be possible, below link
																		provides more insight on lateral entry process:</p>
																	<p>
																		<a
																			href="http://distance.nmims.edu/lateral-admissions-or-upgradation.html#Overview"
																			title="">
																			http://distance.nmims.edu/lateral-admissions-or-upgradation.html#Overview
																		</a>
																	</p>

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
																		data-parent="#192" href="#017"> How to change the
																		program? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="017" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>Students requesting for Program Change will have
																		to click the checkbox &#34;Program Change&#34; at the
																		time of Online Re-registration process, provide the
																		details asked and pay the applicable fees and for
																		Offline in the form of Demand Draft at the Authorized
																		Enrollment Partner. Once the program is changed the
																		student will be issued new Identity Card with the
																		updated Program name and applicable study material.</p>

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
																		data-parent="#192" href="#018"> What is the fee
																		structure for the Programs? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="018" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>For the fee structure for the Programs check the
																		below link:</p>
																	<p>
																		<a
																			href="http://distance.nmims.edu/admission-process.html#Fee-Structure"
																			title="Fee Structure">
																			http://distance.nmims.edu/admission-process.html#Fee-Structure
																		</a>

																	</p>

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
																		data-parent="#192" href="#019"> What is a Regional
																		office/Learning center of NMIMS? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="019" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>To ensure quality in our Academic delivery,
																		NGA&minus;SCE has set up its own University Regional
																		Office across 7 major locations of India, [Mumbai,
																		Delhi, Kolkata, Bengaluru, Hyderabad, Pune and
																		Ahmedabad]. These Centre&#39;s are NMIMS&#39;s own
																		Centre&#39;s having state of art infrastructure to
																		deliver quality education. These Centre&#39;s also act
																		as local point of contact for students within that
																		area to facilitate student support services. Our
																		University Regional Office are one of our biggest
																		differentiators in Distance Learning Space.</p>
																	<p>Check the Link to find the information on
																		regional offices of NMIMS:</p>
																	<p>
																		<a
																			href="http://distance.nmims.edu/centers.html#Learning-Center"
																			title="Learning-Center">
																			http://distance.nmims.edu/centers.html#Learning-Center
																		</a>

																	</p>

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
																		data-parent="#192" href="#020"> What is an
																		authorized enrollment partner/ Information Center </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="020" class="panel-collapse collapse out">
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
																	<p>Check the Link to find the Authorized enrollment
																		partner near you:</p>
																	<p>
																		<a href="http://distance.nmims.edu/centers.html"
																			title="Centers">
																			http://distance.nmims.edu/centers.html </a>

																	</p>

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
																		data-parent="#192" href="#021"> Will Authorized
																		Enrollment Partner be involved in any academic
																		process? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="021" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>No, Authorized Enrollment Partner will not be in
																		charge of any academic process. Authorized Enrollment
																		Partner is not authorized to collect any additional
																		fees from the students for NMIMS programs. Any
																		personal dealing with Authorized Enrollment Partner
																		will be at the student's risk</p>

																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->

														<!-- End of BY PS 1.16-->
														<!--  BY PS 1.17-->
														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#192" href="#022"> Can I change the
																		authorized enrollment partner? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="022" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>Student can request for a change of Authorized
																		Enrollment Partner only at the time of
																		Re-registration. Students are not allowed to change
																		the Authorized Enrollment Partner in between the
																		Semester. Students will have to pay the Authorized
																		Enrollment Partner Change Fee as prescribed by the
																		University from time to time along with the No
																		Objection letter from Authorized Enrollment Partner
																		he/she is attached with. University discourages the
																		Change of Authorized Enrollment Partner within the
																		city under any circumstances.</p>

																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->

														<!-- End of BY PS 1.17-->
														<!--  BY PS 1.18-->
														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#192" href="#023"> How to change the
																		authorized enrollment partner? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="023" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>Student requesting for Inter-City Authorized
																		Enrollment Partner change will have to click the
																		checkbox &#34;Change Authorized Enrollment
																		Partner/IC&#34; at the time of Online Re-registration
																		and provide the details asked and pay the applicable
																		fees as prescribed by the University from time to time
																		either online on the School&#39;s website or Offline
																		in the form of Demand Draft at the Authorized
																		Enrollment Partner along with Re-registration fee.
																		Once the Authorized Enrollment Partner is changed the
																		student will be issued with the New Identity Card with
																		the name of the New Authorized Enrollment Partner.</p>

																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->

														<!-- End of BY PS 1.18-->
														<!--  BY PS 1.19-->
														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#192" href="#024"> Do I need to pay
																		any additional fee to change my Authorized Enrollment
																		Partner? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="024" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>Yes, you will have to pay a partner location
																		change fee of Rs.2500/- which will be added to your
																		Semester fee while filling up the re-registration
																		form.</p>

																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->

														<!-- End of BY PS 1.19-->
														<!--  BY PS 1.20-->
														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#192" href="#025"> Can I change my
																		name after taking admission for the distance learning
																		programs? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="025" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>You can change your name after taking admission
																		for our distance learning programs. You will be
																		required to apply for name change by applying for
																		service request via your student zone portal. You will
																		be required to upload the photo id proof in the First
																		& Last Name order or marriage affidavit as the case
																		may be. Kindly note there are no charges in case you
																		wish to change your name</p>

																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->

														<!-- End of BY PS 1.20-->
														<!--  BY PS 1.21-->
														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#192" href="#026"> Can I change my
																		date of birth after taking admission for our distance
																		learning programs? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="026" class="panel-collapse collapse out">
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

														<!-- End of BY PS 1.21-->
														<!--  BY PS 1.22-->
														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#192" href="#027"> Can I
																		change/correct my registered e-mail id? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="027" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>If you have updated your e-mail id incorrectly
																		at the time of your admission or if you want to change
																		your email ID, you need to inform us about the same
																		with the below supporting documents.</p>
																	<p>Billing address proof copy as updated in our
																		system & record</p>
																	<p>Government ID card proof</p>
																	<p>On verification of the above documents, the
																		e-mail id will be updated in our system & you will be
																		able to login to your student zone portal
																		successfully.</p>

																	</p>

																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->

														<!-- End of BY PS 1.22-->
														<!--  BY PS 1.23-->
														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#192" href="#028"> What is
																		Re-registration? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="028" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>Re-registration is a process where a student
																		pursuing a course with NGASCE- NMIMS registers for the
																		next semester. Students who have paid annual or full
																		fees will also have to re-register to the subsequent
																		semester in order to activate the semester access</p>

																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->

														<!-- End of BY PS 1.23-->
														<!--  BY PS 1.24-->
														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-patren="#192" href="#029"> How to
																		re-register? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="029" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>Re-registration to particular semesters can be
																		done from the following link:</p>
																	<p>
																		<a
																			href="http://ngasce.force.com/nmLogin_New?type=reregistration"
																			title="Re-Registration">
																			http://ngasce.force.com/nmLogin_New?type=reregistration.
																		</a>
																	</p>
																	<p>Follow the steps in the form to complete the
																		re-registration.</p>

																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->

														<!-- End of BY PS 1.24-->
														<!--  BY PS 1.25-->
														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-patren="#192" href="#030"> Can I still
																		re-register in case I haven't cleared a particular
																		semester? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="030" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>Yes, student can re-register for the prospective
																		semesters irrespective of the number of Course passed
																		in the previous semester/s as per the dates announced
																		by the University from time to time.</p>

																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->

														<!-- End of BY PS 1.25-->
														<!--  BY PS 1.26-->
														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-patren="#192" href="#031"> Do I need to
																		submit and fill the re-registration form to the
																		Authorized Centre since I have made payment for
																		re-registration? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="031" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>It is mandatory for student to fill and submit
																		the re-registration form to their Authorized Centre so
																		that the course material applicable for your semester
																		can be issued to you</p>

																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->

														<!-- End of BY PS 1.26-->
														<!--  BY PS 1.27-->
														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-patren="#192" href="#032"> Can I change the
																		Specialization selected by me? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="032" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>Students requesting for Program Change will have
																		to click the checkbox &#34;Program Change&#34; at the
																		time of Online Re-registration and provide the details
																		asked and pay the applicable fees as prescribed by the
																		University from time to time either Online on the
																		School&#39;s website or Offline in the form of Demand
																		Draft at the Authorized Enrollment Partner. Once the
																		program is changed the student will be issued new
																		Identity Card with the updated Program name.</p>

																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->

														<!-- End of BY PS 1.27-->
														<!--  BY PS 1.28-->
														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-patren="#192" href="#033"> What is the exit
																		policy? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="033" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>Students who have taken admission to Post
																		Graduate Diploma Program but want to discontinue after
																		completion of one year have to follow the following
																		policies and procedures:</p>
																	<p>
																		<b>Policies: </b>
																	</p>
																	<p>
																	<ol type="1">
																		<li>Post Graduate Diploma Students can
																			discontinue the Program on successful completion of
																			all the courses of semester I and Semester II, after
																			the approval is received from the University.</li>
																		<li>Such students will then be awarded with a
																			Diploma in General Management.</li>
																		<li>Students who have already taken admission in
																			the Semester III and/or Semester IV will not be
																			refunded any fees if they apply for discontinuation
																			of the Program.</li>
																	</ol>
																	</p>
																	<p>
																		<b>Procedures: </b>
																	</p>
																	<p>
																	<ol type="1">
																		<li>The student who wants to discontinue the
																			program has to submit an application at the
																			Authorized Enrollment Partner.</li>
																		<li>After the approval is received from the
																			University the student will be awarded with Diploma
																			in General Management.</li>
																	</ol>
																	</p>

																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->

														<!-- End of BY PS 1.28-->
														<!--  BY PS 1.29-->
														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-patren="#192" href="#034"> Can a student
																		extend program validity? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="034" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>Students can extend the validity of their
																		registration only in continuation with the existing
																		validity of the program for which they are registered:
																	</p>
																	<p>Diploma Program &minus; 6 months</p>
																	<p>Post Graduate Diploma Program &minus; 1 year (at
																		a time only by 6 months)</p>
																	<p>Post Graduate Diploma Program (Lateral
																		Admission) &minus; 6 months</p>
																	<p>Students who have registered for all the
																		Semesters are only eligible for extension of validity.

																	</p>
																	<p>Fees for extension of validity of the program
																		will be then existing 50% of the tuition fees
																		applicable for the program</p>
																	<p>Please apply for extension of validity within 2
																		months from the expiry of the validity period.</p>
																	<p>University does not offer any refund policy;
																		Fees once paid towards extension of validity will not
																		be refunded under any circumstances</p>

																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->

														<!-- End of BY PS 1.29-->
														<!--  BY PS 1.30-->
														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-patren="#192" href="#035"> How to get
																		validity extension? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="035" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>To extend the validity use the following link:</p>
																	<p>
																		http://ngasce.force.com/ApplyForValidityExtension</p>

																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->

														<!-- End of BY PS 1.30-->
														<!--  BY PS 1.31-->
														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-patren="#192" href="#036"> What is the
																		procedure for admission cancellation? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="036" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>Student are requested to kindly fill & submit
																		the original cancellation form with your ID card and
																		original fee receipt to your authorized enrollment
																		partner (mention the center name) for initiating the
																		cancellation process. Kindly note there is a process
																		time line of 28 daysfor the cancellation refund cheque
																		to be issued to you after deduction of administrative
																		charges & study kit charges in case the same has been
																		issued to you. The process of cancellation has to be
																		initiated by a set date for admission cancelation
																		provided by the university.</p>

																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->

														<!-- End of BY PS 1.31-->
														<!--  BY PS 1.32-->
														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-patren="#192" href="#037"> How can I get a
																		Bonafide certificate? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="037" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>You need to raise a service request for
																		Bonafide, follow the path to raise a request:</p>
																	<p>Log in to student portal-> Student support->
																		service request-> select issuance of Bonafide</p>
																	<p>Enter in the required information for the
																		document and make the appropriate payments towards
																		issuing the Bonafide certificates.</p>

																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->

														<!-- End of BY PS 1.32-->

														<!-- End of BY PS -->

														a
														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#192" href="#333"> Can I apply for
																		lateral admission once I complete the diploma
																		program/PG program successfully, for the Post graduate
																		diploma program? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="333" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>We have started with the facility of Course
																		Waiver for our students who have registered for or
																		after July 2014 batch in 1 year Diploma Program or 2
																		year Post Graduate Diploma</p>
																	<p>Program and have completed the program and are
																		willing to take Admission in Post Graduate Diploma
																		Program.</p>
																	<ul>
																		<p>
																			<b>Policies:</b>
																		</p>
																		<li>
																			<p>1. Students should have completed the earlier
																				registered program. Courses successfully
																				passed/cleared will only be considered for course
																				waiver.</p>
																			<p>2. Students should apply waiver only within 2
																				years of successful completion of the earlier
																				program.</p>
																			<p>3. All Diploma students except DGM will be
																				admitted to Semester II and validity will be of 2.5
																				years. DGM and Post Graduate Diploma student will be
																				registered directly to Semester III and validity
																				will be of 2 years.</p>
																			<p>4. No fee and time waiver will be given.</p>
																			<p>5. The then existing fee structure will be
																				applicable.</p>
																			<p>6. Students will have to submit all the
																				required documents for admission as per the
																				eligibility criteria opted.</p>
																			<p>7. Course waiver is not applicable for Project
																				(Semester IV).</p>
																			<p>8. Student's admission will be treated as
																				fresh admission and all the existing policies will
																				be applicable.</p>
																		</li>
																	</ul>
																	<ul>
																		<p>
																			<b>Process:</b>
																		</p>
																		<li>
																			<p>
																				1. Students will have to register as a fresh student
																				via the Apply now form available on our website <a>distance.nmims.edu.</a>
																			</p>
																			<p>2. They will select "Yes" in the option Any
																				Course Done from NGA-SCE on the Step 3 Program
																				Details Information page and fill the required
																				details and will also select "Yes" for the option Do
																				you want to opt for Lateral Admission.</p>
																			<p>3.Student will be directly admitted:</p>
																			<ul>
																				<li>
																					<p>a. Diploma students other than DGM will be
																						directly registered in Semester II.</p>
																					<p>b. Post Graduate Diploma and DGM program
																						students will be directly registered in Semester
																						III.</p>
																				</li>
																			</ul>
																		</li>
																	</ul>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->

														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#192" href="#331"> How do I update my
																		e-mail Id /shipping address and mobile number? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="331" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>You can update your e-mail id /mobile/shipping
																		address from the student zone via the update profile
																		link tab.</p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->

														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#192" href="#336"> What is
																		Re-registration? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="336" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>Re-registration is a process where a student
																		pursuing a course through Education registers himself
																		for the next semester in order to be eligible for SLM,
																		access to learning resources on LMS, attend online
																		live lectures and enrolling for Personal Contact
																		Program.</p>
																	<p>Students who have paid annual or full fees will
																		also have to re-register to the subsequent semester in
																		order to activate the semester access</p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->

														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#192" href="#337"> Can I still
																		re-register in case I haven't cleared a particular
																		semester? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="337" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>A student can re-register for the prospective
																		semesters irrespective of the number of Course passed
																		in the previous semester/s as per the dates announced
																		by the University from time to time.</p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->

														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#192" href="#339"> What is the
																		process of re-registering? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="339" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>
																		Click here to know the process of re-registering:
																		link: <a
																			href="http://distance.nmims.edu/re-register.html"
																			target="_blank">http://distance.nmims.edu/re-register.html</a>
																	</p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->

														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#192" href="#340"> Do I need to
																		submit and fill the re-registration form to the
																		authorized center since I have made payment for
																		re-registration? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="340" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>It is mandatory for student to fill and submit
																		the re-registration form to their authorized center so
																		that the course material applicable for your semester
																		can be issued to you</p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->

														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#192" href="#341"> What is the cost
																		and process for Bona fide Certificate? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="341" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>The five copies of the bona fide certificate is
																		issued free of cost, If the student requires 20 copies
																		it would be Rs. 1500/- (Rs. 100/- per Bona fide).
																		Student is required to forward a written application
																		for issuance 20 copies of Bona fide certificate along
																		with Demand draft of Rs. 1500/- in favour of "SVKM's
																		NMIMS" payable at Mumbai.</p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->



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
																	will I receive after registering for the program? </a>
															</h4>
														</div>
														<!--/.panel-heading -->
														<div id="2001" class="panel-collapse collapse out">
															<div class="panel-body faqAns">
																<p>After registering for any program with NGA-SCE, a
																	student will receive Self Learning Material (semester
																	wise), access to our online digital library, course
																	presentation, access to recordings of online live
																	sessions, session presentations and any additional
																	reading material as shared by faculty if any.</p>
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
																	data-parent="#193" href="#2002"> How are the
																	programs delivered at NGA-SCE? </a>
															</h4>
														</div>
														<!--/.panel-heading -->
														<div id="2002" class="panel-collapse collapse out">
															<div class="panel-body faqAns">
																<p>NGA-SCE follows a blended model of academic
																	delivery. It involves conducting live, online sessions,
																	Personal Contact Program, lecture presentations,
																	providing learning resources like books, session plan
																	and recordings of online sessions</p>
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
																	data-parent="#193" href="#2003"> What learning
																	resources will I receive after registering for the
																	program? </a>
															</h4>
														</div>
														<!--/.panel-heading -->
														<div id="2003" class="panel-collapse collapse out">
															<div class="panel-body faqAns">
																<p>After registering for any program with NGA-SCE, a
																	student will receive Self Learning Material (semester
																	wise), access to our online digital library, course
																	presentation, access to recordings of online live
																	sessions, session presentations and any additional
																	reading material as shared by faculty.</p>
															</div>
															<!--/.panel-body -->
														</div>
														<!--/.panel-collapse -->
													</div>
													<!-- /.panel -->
													<!-- End of BY PS academics 2.3-->
													<!-- BY PS academics 2.4-->

													<div class="panel panel-default faq">
														<div class="panel-heading">
															<h4 class="panel-title">
																<a data-toggle="collapse" aria-expanded="false"
																	data-parent="#193" href="#2004"> What is the
																	benefit of attending the online sessions? </a>
															</h4>
														</div>
														<!--/.panel-heading -->
														<div id="2004" class="panel-collapse collapse out">
															<div class="panel-body faqAns">
																<p>Online lectures cover the learning of the entire
																	course content as per session plan. Faculty's interact
																	and collaborate with students and thereby ensure
																	enhanced student engagement</p>
															</div>
															<!--/.panel-body -->
														</div>
														<!--/.panel-collapse -->
													</div>
													<!-- /.panel -->
													<!-- End of BY PS academics 2.4-->
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
																				</a>
																			</h4>
																		</div>
																		<!--/.panel-heading -->
																		<div id="20052" class="panel-collapse collapse out">
																			<div class="panel-body faqAns">
																				<p>Yes, you can attend the online sessions on a
																					mobile device\ cellphone, we recommend that you use
																					Wi-Fi connection and not travel while attending
																					lectures. We have a technical support team for
																					assistance during lectures, the number is
																					0008001001693.</p>
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
																					the faculty during live online lectures</p>
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
																					data-parent="#livelec" href="#20056"> I&#39;m
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
																					faculty will be available in the student portal.
																					Follow this path to find the E-books/course
																					presentations:</p>
																				<p>Login to student portal-> my courses (left
																					side of portal) &rarr; Select subject from drop
																					down whose lectures have been conducted &rarr;
																					Scroll down to learning resources.</p>
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
																					live online sessions of 8 hours per subject (2hrs
																					*4 sessions) Timings for the same is updated in
																					Academic Calendar. The information will be sent Via
																					Email and SMS 3-4 days in advance. Academic
																					Calendar is updated on monthly basis.</p>
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
																					data-parent="#recordings" href="#20061"> How to
																					attend live online lecture? </a>
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
																					Scroll down to learning resource &rarr; download or
																					stream the lecture</p>
																				<p>- Download ARF player as the video files will
																					be supported by the same and can only be played on
																					a laptop or a desktop</p>
																				<p>- You can also visit your Academic Calendar</p>
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
																	<!-- BY PS academics 2.6.3-->

																	<div class="panel panel-default faq">
																		<div class="panel-heading">
																			<h4 class="panel-title">
																				<a data-toggle="collapse" aria-expanded="false"
																					data-parent="#recordings" href="#20063"> There
																					are no recording of my previous semester pending
																					subjects </a>
																			</h4>
																		</div>
																		<!--/.panel-heading -->
																		<div id="20063" class="panel-collapse collapse out">
																			<div class="panel-body faqAns">
																				<p>In order to access recordings of lectures of
																					previous semester pending subjects follow the given
																					path in student portal:</p>
																				<p>Login to student portal &rarr; My courses
																					(left side of portal) -> select subject from drop
																					down whose lectures have been conducted-> scroll
																					down to learning resources-> select last cycle
																					recordings(right side)->download or stream the
																					lecture.</p>
																				<p>Download ARF player as the video files will
																					be supported by the same and can only be played on
																					a laptop or a desktop.</p>
																			</div>
																			<!--/.panel-body -->
																		</div>
																		<!--/.panel-collapse -->
																	</div>
																	<!-- /.panel -->
																	<!-- End of BY PS academics 2.6.3-->
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
																					will I get my queries resolved when I&#39;m
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
																					do I login to my student zone portal once I&#39;ve
																					taken admission for the distance learning program?
																				</a>
																			</h4>
																		</div>
																		<!--/.panel-heading -->
																		<div id="20072" class="panel-collapse collapse out">
																			<div class="panel-body faqAns">
																				<p>You are required to login to your student
																					zone portal with your Sap ID & Password via the
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
																					I&#39;m getting &#34;invalid credentials&#34; while
																					logging to the student portal. </a>
																			</h4>
																		</div>
																		<!--/.panel-heading -->
																		<div id="20073" class="panel-collapse collapse out">
																			<div class="panel-body faqAns">
																				<p>You are requested to kindly login to your
																					student zone portal with the correct Sap Id &
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
																					I&#39;m getting &#34;Session expired&#34; while
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
													<!-- PCP -->

													<div class="panel panel-default faq">
														<div class="panel-heading">
															<h4 class="panel-title">
																<a class="collapsed" data-toggle="collapse"
																	aria-expanded="false" data-parent="#193" href="#PCP">
																	PCP ( Personal Contact Program ) </a>
															</h4>
														</div>
														<!--/.panel-heading -->

														<div id="PCP" class="panel-collapse collapse out">
															<div class="panel-body"
																style="border-bottom: solid 2px grey; border-left: solid 2px grey; border-right: solid 2px grey;">
																<div class="panel-group" id="nested">

																	<!-- BY PS academics 2.8.1-->
																	<div class="panel panel-default faq">
																		<div class="panel-heading">
																			<h4 class="panel-title">
																				<a data-toggle="collapse" aria-expanded="false"
																					data-parent="#Credentials" href="#20081"> What
																					is a "Personal Contact Program" and where can I
																					attend this? </a>
																			</h4>
																		</div>
																		<!--/.panel-heading -->
																		<div id="20081" class="panel-collapse collapse out">
																			<div class="panel-body faqAns">
																				<p>Personal Contact Programs are Face to Face
																					sessions conducted by empanelled faculty of NGA-SCE
																					only at University Regional Offices. Personal
																					Contact Program aims to include all or any of the
																					following as per the session plan of the course:</p>
																				<p>
																				<ol type="1">
																					<li>Student Doubt clearing</li>
																					<li>Group activity/discussion</li>
																					<li>Case study discussion</li>
																				</ol>
																				</p>
																				<p>Personal Contact Programs are conducted for 3
																					hours per course. It will be held at University
																					Regional Offices at Mumbai and Pune.</p>
																			</div>
																			<!--/.panel-body -->
																		</div>
																		<!--/.panel-collapse -->
																	</div>
																	<!-- /.panel -->
																	<!-- End of BY PS academics 2.8.1-->
																	<!-- BY PS academics 2.8.2-->
																	<div class="panel panel-default faq">
																		<div class="panel-heading">
																			<h4 class="panel-title">
																				<a data-toggle="collapse" aria-expanded="false"
																					data-parent="#Credentials" href="#20082"> How
																					can I register for Personal Contact Program? </a>
																			</h4>
																		</div>
																		<!--/.panel-heading -->
																		<div id="20082" class="panel-collapse collapse out">
																			<div class="panel-body faqAns">
																				<p>The student has to visit our website:
																					http://distance.nmims.edu/, login to Student Zone -
																					Academics - PCP Registration and register for
																					Personal Contact Program by paying the then
																					applicable PCP registration fee.</p>
																			</div>
																			<!--/.panel-body -->
																		</div>
																		<!--/.panel-collapse -->
																	</div>
																	<!-- /.panel -->
																	<!-- End of BY PS academics 2.8.2-->
																	<!-- BY PS academics 2.8.3-->
																	<div class="panel panel-default faq">
																		<div class="panel-heading">
																			<h4 class="panel-title">
																				<a data-toggle="collapse" aria-expanded="false"
																					data-parent="#Credentials" href="#20083"> What
																					is the duration for Personal Contact Program? </a>
																			</h4>
																		</div>
																		<!--/.panel-heading -->
																		<div id="20083" class="panel-collapse collapse out">
																			<div class="panel-body faqAns">
																				<p>NGA-SCE offers 3 hours Personal Contact
																					Program per course to its students subject to the
																					payment of prescribed fees and subject to minimum
																					number of student registration.</p>
																			</div>
																			<!--/.panel-body -->
																		</div>
																		<!--/.panel-collapse -->
																	</div>
																	<!-- /.panel -->
																	<!-- End of BY PS academics 2.8.3-->
																	<!-- BY PS academics 2.8.4-->
																	<div class="panel panel-default faq">
																		<div class="panel-heading">
																			<h4 class="panel-title">
																				<a data-toggle="collapse" aria-expanded="false"
																					data-parent="#Credentials" href="#20084"> What
																					are the advantages of attending the Personal
																					Contact Programs? </a>
																			</h4>
																		</div>
																		<!--/.panel-heading -->
																		<div id="20084" class="panel-collapse collapse out">
																			<div class="panel-body faqAns">
																				<p>Personal Contact Programs are designed for
																					activities like doubt clearing, case study
																					discussion and other group activities related to
																					the course for enhanced student learning.</p>
																			</div>
																			<!--/.panel-body -->
																		</div>
																		<!--/.panel-collapse -->
																	</div>
																	<!-- /.panel -->
																	<!-- End of BY PS academics 2.8.4-->
																	<!-- BY PS academics 2.8.5-->
																	<div class="panel panel-default faq">
																		<div class="panel-heading">
																			<h4 class="panel-title">
																				<a data-toggle="collapse" aria-expanded="false"
																					data-parent="#Credentials" href="#20085"> Is it
																					mandatory to attend Personal Contact Programs? </a>
																			</h4>
																		</div>
																		<!--/.panel-heading -->
																		<div id="20085" class="panel-collapse collapse out">
																			<div class="panel-body faqAns">
																				<p>Though attending the live online session or
																					Personal Contact Program is not mandatory, we
																					strongly recommend our students to attend these
																					sessions in order to get the best learning
																					experience.</p>
																			</div>
																			<!--/.panel-body -->
																		</div>
																		<!--/.panel-collapse -->
																	</div>
																	<!-- /.panel -->
																	<!-- End of BY PS academics 2.8.5-->

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

													<!--    Logistics -->

													<div class="panel panel-default faq">
														<div class="panel-heading">
															<h4 class="panel-title">
																<a class="collapsed" data-toggle="collapse"
																	aria-expanded="false" data-parent="#193"
																	href="#Logistics"> Logistics </a>
															</h4>
														</div>
														<!--/.panel-heading -->

														<div id="Logistics" class="panel-collapse collapse out">
															<div class="panel-body"
																style="border-bottom: solid 2px grey; border-left: solid 2px grey; border-right: solid 2px grey;">
																<div class="panel-group" id="nested">

																	<!-- BY PS academics 2.10.1-->
																	<div class="panel panel-default faq">
																		<div class="panel-heading">
																			<h4 class="panel-title">
																				<a data-toggle="collapse" aria-expanded="false"
																					data-parent="#Logistics" href="#200101"> When
																					will I get my study kit? </a>
																			</h4>
																		</div>
																		<!--/.panel-heading -->
																		<div id="200101" class="panel-collapse collapse out">
																			<div class="panel-body faqAns">
																				<p>Once your admission is confirmed & your
																					student number is issued to you, you will receive
																					your study kit & your course material applicable
																					for your program within 10 working days&#39; time.
																					The study kit will be dispatched either at your
																					shipping address or your authorized enrolment
																					partner which you chosen while taking your
																					admission.</p>
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
																					data-parent="#Logistics" href="#200102"> How
																					will I receive my ID card and fee receipt? </a>
																			</h4>
																		</div>
																		<!--/.panel-heading -->
																		<div id="200102" class="panel-collapse collapse out">
																			<div class="panel-body faqAns">
																				<p>Welcome Kit will be part of your Study kit,
																					which includes [ID card, Fee receipt, Welcome
																					letter and Student Undertaking].</p>
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
																					data-parent="#Logistics" href="#200103"> Where
																					do I need to submit the student declaration form
																					once I have received the same in my study kit? </a>
																			</h4>
																		</div>
																		<!--/.panel-heading -->
																		<div id="200103" class="panel-collapse collapse out">
																			<div class="panel-body faqAns">
																				<p>You have to submit the original student
																					undertaking form to your authorized enrolment
																					partner that you have opted at the time of your
																					admission</p>
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
																					misplaced my study kit of a particular semester
																					what can be done? </a>
																			</h4>
																		</div>
																		<!--/.panel-heading -->
																		<div id="200104" class="panel-collapse collapse out">
																			<div class="panel-body faqAns">
																				<p>If you have misplaced your study kit you will
																					be required to apply for your duplicate study kit
																					for the particular semester by placing a service
																					request via your student zone portal. The study kit
																					charges will be applicable. You can make the online
																					payment via the student zone portal for the same.</p>
																				<p>Follow the path to raise a request for study
																					kit:</p>
																				<p>Log in to student portal &rarr; Student
																					support &rarr; service request &rarr; Re-Dispatch
																					of Study kit.</p>
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
																					data-parent="#Logistics" href="#200105"> How do
																					I opt to receive my study material after I complete
																					my admission procedure? </a>
																			</h4>
																		</div>
																		<!--/.panel-heading -->
																		<div id="200105" class="panel-collapse collapse out">
																			<div class="panel-body faqAns">
																				<p>While you are proceeding with your semester
																					payment via the Apply now link tab which is updated
																					on the website via the Apply now link tab:
																					http://ngasce.force.com/nmLogin_New?type=registration,
																					you can either opt to receive the study material at
																					your shipping address or your authorized enrolment
																					partner. Once you have chosen the respective the
																					study material will be dispatched accordingly.</p>
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
																					data-parent="#Logistics" href="#200106"> In
																					case I want to change my name in my student
																					identity card, what is the process for the same? </a>
																			</h4>
																		</div>
																		<!--/.panel-heading -->
																		<div id="200106" class="panel-collapse collapse out">
																			<div class="panel-body faqAns">
																				<p>In case you have changed your name via the
																					service link which is updated in your student zone
																					portal under the student support link tab. You will
																					be required to apply for duplicate ID card with the
																					revised /updated name. The duplicate id charges
																					with revised name is RS 200/-You can make the
																					online payment towards the same.</p>
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
																					data-parent="#Logistics" href="#200107"> Where
																					do I need to collect my semester fee receipts once
																					I have taken admission for our distance learning
																					programs? </a>
																			</h4>
																		</div>
																		<!--/.panel-heading -->
																		<div id="200107" class="panel-collapse collapse out">
																			<div class="panel-body faqAns">
																				<p>Students are required to kindly get in touch
																					with your authorized enrolment partner whom you are
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
													<div class="panel-group" id="nested">


														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#193" href="#242"> What is the
																		duration and timings of the live online sessions? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="242" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>Lectures are conducted live in a virtual
																		classroom environment. Each subject is taught via live
																		online sessions of 8 hours per subject (2hrs *4
																		sessions) Timings for the same is updated in Academic
																		Calendar .The information will be sent Via Email and
																		SMS 3-4 days in advance. Academic Calendar is updated
																		on monthly basis.</p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->

														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#193" href="#244"> How to attend the
																		online session? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="244" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>
																		You are requested to kindly login for the session from
																		the student zone zone via the link tab: <a>https://studentzone-ngasce.nmims.edu/studentzone/</a>
																		-> the academic calendar tab <a>https://studentzone-
																			ngasce.nmims.edu/acads/viewStudentTimeTable </a>
																	</p>
																	<p>You are requested to kindly click on the session
																		which is scheduled today and then click on attend
																		session button to attend the live session</p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->

														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#193" href="#245"> Can I attend the
																		online session from Mobile Application? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="245" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>
																		Yes, you can attend the online sessions from the
																		mobile application, we recommend that you use Wi-Fi
																		connection and not travel while attending lectures.
																		You can take assistance from the technical support
																		team in case required the number is 0008001001693</a>
																	</p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->

														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#193" href="#2451"> Can I access and
																		view recordings from my mobile application? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="2451" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>
																		No. You cannot access and view session recordings on
																		the mobile application. You can only attend online
																		sessions from the mobile application. The session
																		recordings can be viewed and downloaded from the desk
																		top and lap top.</a>
																	</p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->


														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#193" href="#243"> Can I attend these
																		sessions from my home/workplace/office? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="243" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>Yes, you can attend these sessions from your
																		home/workplace/office subject to availability of
																		necessary IT infrastructure.</p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->

														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#193" href="#2441"> How can I
																		register for Personal Contact Program? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="2441" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>
																		The student has to visit our website: <a
																			title="http://distance.nmims.edu/"
																			href="http://distance.nmims.edu/">http://distance.nmims.edu/</a>
																		, login to <b>Student Zone - Academics - PCP
																			Registration</b> and register for Personal Contact
																		Program by paying the then applicable PCP registration
																		fee.
																	</p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->

														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#193" href="#24512"> What are the
																		fees for attending live online sessions? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="24512" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>NGA-SCE charges no separate fee for attending
																		live online session.</p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->

														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#193" href="#246"> What is the
																		benefit of attending the online sessions? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="246" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>Online lectures cover the learning of the entire
																		course content as per session plan. Faculty's interact
																		and collaborate with students and thereby ensure
																		enhanced student engagement.</p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->

														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#193" href="#247"> What is "Personal
																		Contact Program" and where can I attend this? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="247" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>Personal Contact Programs are Face to Face
																		sessions conducted by empanelled faculty of NGA-SCE
																		only at University Regional Offices. Personal Contact
																		Program aims to include all or any of the following as
																		per the session plan of the course:</p>
																	<p>1. Student Doubt clearing</p>
																	<p>2. Group activity/discussion</p>
																	<p>3. Case study discussion</p>
																	<p>Personal Contact Programs are conducted for 3
																		hours per course. Students can attend these sessions
																		at the 7 University Regional Offices at Mumbai, Delhi,
																		Kolkata, Hyderabad, Bangalore, Ahmedabad and Pune.
																		Click here to view the details:</p>
																	<p>
																		<a href="http://distance.nmims.edu/centers.html"
																			target="_blank">http://distance.nmims.edu/centers.html</a>
																	</p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->

														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#193" href="#248"> What is the
																		duration of Personal Contact Program? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="248" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>NGA-SCE offers 3 hours Personal Contact Program
																		per course to its students subject to the payment of
																		prescribed fees and subject to minimum number of
																		student registration.</p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->

														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#193" href="#250"> What are the
																		advantages of attending the Personal Contact Programs?
																	</a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="250" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>Personal Contact Programs are designed for
																		activities like doubt clearing, case study discussion
																		and other group activities related to the course for
																		enhanced student learning.</p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->

														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#193" href="#251"> What learning
																		resources will I receive after registering for the
																		program? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="251" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>After registering for any program with NGA-SCE,
																		a student will receive Self Learning Material
																		(semester wise), access to our online digital library,
																		course presentation, access to recordings of online
																		live sessions, session presentations and any
																		additional reading material as shared by faculty if
																		any.</p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->

														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#193" href="#252"> What are the
																		features of online Digital Library? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="252" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>Digital Library facilities are provided for
																		students who are willing to learn beyond books and
																		their registered subjects. Users can access full text
																		journals online. The contents have been organized in
																		groups for easy access. The search interface allows
																		easy navigation. Students can access our Digital
																		Library round the clock. Digital library provides
																		user-friendly interfaces to its resources; access to
																		journals, databases, eBooks database, research
																		database, company databases etc.</p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->

														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#193" href="#253"> Is it mandatory to
																		attend live lecture and/or Personal Contact Programs?
																	</a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="253" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>Though attending the live online session or
																		Personal Contact Program is not mandatory, we strongly
																		recommend our students to attend these sessions in
																		order to get the best learning experience.</p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->

														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#193" href="#254"> In case I have any
																		doubt/query relating to a course, what support will
																		NGA-SCE provide? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="254" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>
																		The student has to visit our website: <a
																			href="http://distance.nmims.edu/" target="_blank">http://distance.nmims.edu/</a>
																		, login to <b>Student Zone</b> - <b>Sessions
																			Calendar</b> - <b>Session details page</b> and click on <b>POST
																			QUERY</b> button to post any course/content related
																		query.
																	</p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->

													</div>
													<!--/.panel-group-->
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
																		data-parent="#194" href="#3001"> When are the
																		Exams conducted? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="3001" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>There are 4 exam cycles in a year April, June,
																		September and December. Post completion of 6 months in
																		any Academic cycle student is eligible to appear for
																		the exams, April and September are meant for re-sit
																		(not eligible for students appearing first time in any
																		Semester)</p>
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
																		weightage given to Internal Assignment and Term End
																		Examinations? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="3002" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>The Internal Assignment carry 30% weightage and
																		Term End Examination (Multiple Choice Questions +
																		Descriptive type questions) carry 70% weightage. (30 +
																		70 = 100 Total Marks).Aggregate passing-50%</p>
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
																		data-parent="#194" href="#3003"> When are the
																		examinations held for Students enrolled from July 2014
																		batch onwards? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="3003" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>All exams are conducted at NGA-SCE regional
																		centres or at designated Exam centres nationwide.
																		Students enrolled in Post Graduate Diploma /Diploma
																		program from July, 2014 batch onwards have proctored
																		computer based examination. The pattern of the
																		question paper is composite, wherein 50 marks out of
																		the 70 are reserved for Multiple Choice Questions
																		(MCQ) and the remaining 20 Marks are reserved for two
																		Descriptive questions of 10 Marks each.</p>
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
																		data-parent="#194" href="#3004"> When are the
																		examinations held for Students enrolled before July
																		2014 batch? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="3004" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>Examinations for Post Graduate Diploma program
																		/Diploma program students, enrolled prior to Jul 2014
																		batch in old program is conducted in the month of June
																		and December</p>
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
																		data-parent="#194" href="#3005"> What is the
																		process of Applying or Registering for Examinations? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="3005" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>The student will have to register online for
																		appearing for the Term End Examination when the window
																		for Exam Registration opens. The exam registration is
																		on first come first serve basis when the online
																		registration window goes live (will be communicated
																		via Email/SMS/Announcement section). Student must not
																		wait till last minute for exam registration as it
																		could lead to unavailability of preferable exam
																		Centre/time slot. To avoid missing out any important
																		information/announcement/date, students must refer
																		regularly to our Website/Student zone as all latest
																		announcements are mentioned under Student Zone</p>
																	<p>Student portal &rarr; Exams &rarr; Exam
																		Registration</p>
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
																		data-parent="#194" href="#3006"> What happens if
																		students misses the examination after registering for
																		the exam? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="3006" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>In case you have registered for the examinations
																		& not able to appear or give your examinations on the
																		scheduled date the student will marked
																		&#34;Absent&#34; for that Exam.</p>
																	<p>Kindly note the exam registration fee cannot be
																		refunded or adjusted with the next exam cycle.</p>
																	<p>For e.g.: Student of June 2017 batch fails to
																		appear for examination is 2017 December, he/she can
																		appear for the examination in any of the upcoming
																		examination cycle within the student&#39;s validity
																		period.</p>
																	<p>The same is applicable for assignments too.</p>
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
																		data-parent="#194" href="#3007"> What is the
																		Passing criteria? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="3007" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>To be eligible for being declared as
																		&#34;Pass&#34; in any course/subject, student is
																		required to obtain 50% marks on the aggregate of marks
																		obtained in the Internal Assignment and Term End
																		Examination taken together. Please Note: There is no
																		individual cut-off or Individual Passing Criteria.
																		Aggregate marks: 50/100 in each subject. Appearance in
																		both components is mandatory</p>
																	<p>Student Portal &rarr; Exams &rarr; Exams results
																		&rarr; Pass/fail status</p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->

														<!--End of BY PS 3.7 -->
														<!--  Assignments   -->

														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a class="collapsed" data-toggle="collapse"
																		aria-expanded="false" data-parent="#194"
																		href="#Assignments"> Assignments </a>
																</h4>
															</div>
															<!--/.panel-heading -->

															<div id="Assignments" class="panel-collapse collapse out">
																<div class="panel-body"
																	style="border-bottom: solid 2px grey; border-left: solid 2px grey; border-right: solid 2px grey;">
																	<div class="panel-group" id="nested">

																		<!-- BY PS Exams 3.8.1-->
																		<div class="panel panel-default faq">
																			<div class="panel-heading">
																				<h4 class="panel-title">
																					<a data-toggle="collapse" aria-expanded="false"
																						data-parent="#Assignments" href="#30081"> How
																						Assignment evaluation is done? </a>
																				</h4>
																			</div>
																			<!--/.panel-heading -->
																			<div id="30081" class="panel-collapse collapse out">
																				<div class="panel-body faqAns">
																					<p>After the closure of the assignment
																						submission due date, the assignment submitted by
																						the students will be sent to the faculties for
																						evaluation.</p>
																					<p>Pls. Note: Since the assignment evaluation
																						is done online by the faculties there is no
																						concept of sharing the checked photocopy of
																						assignment. However, the overall faculty remarks
																						given after evaluation will be shared with the
																						students when assignment result is declared.</p>
																				</div>
																				<!--/.panel-body -->
																			</div>
																			<!--/.panel-collapse -->
																		</div>
																		<!-- /.panel -->
																		<!-- End of BY PS Exams 3.8.1-->
																		<!-- BY PS Exams 3.8.2-->
																		<div class="panel panel-default faq">
																			<div class="panel-heading">
																				<h4 class="panel-title">
																					<a data-toggle="collapse" aria-expanded="false"
																						data-parent="#Assignments" href="#30082">
																						Where will I get the Assignment questions? </a>
																				</h4>
																			</div>
																			<!--/.panel-heading -->
																			<div id="30082" class="panel-collapse collapse out">
																				<div class="panel-body faqAns">
																					<p>Assignment questions are uploaded within the
																						Student Zone under Assignment Module. Pls. Note:
																						For every exam cycle, a fresh set of Assignment
																						Questions would be uploaded on the student zone.
																						Student is expected to download the applicable
																						exam subject assignment question paper and submit
																						the assignment/s through the Assignment Module on
																						or before the last date announced by NGA-SCE for
																						that respective exam cycle. Assignment submitted
																						via email or hard copy sent to NGA-SCE will not be
																						accepted.</p>
																					<p>
																						Link: <a
																							href="https://studentzone-ngasce.nmims.edu/studentzone/"
																							title="Home Page">https://studentzone-ngasce.nmims.edu/studentzone/
																						</a> &rarr; Exams &rarr; Assignment (For complete
																						guidelines on assignment)
																					</p>
																				</div>
																				<!--/.panel-body -->
																			</div>
																			<!--/.panel-collapse -->
																		</div>
																		<!-- /.panel -->
																		<!-- End of BY PS Exams 3.8.2-->
																		<!-- BY PS Exams 3.8.3-->
																		<div class="panel panel-default faq">
																			<div class="panel-heading">
																				<h4 class="panel-title">
																					<a data-toggle="collapse" aria-expanded="false"
																						data-parent="#Assignments" href="#30083"> Is
																						there any assignment fee applicable? </a>
																				</h4>
																			</div>
																			<!--/.panel-heading -->
																			<div id="30083" class="panel-collapse collapse out">
																				<div class="panel-body faqAns">
																					<p>In each subject, no assignment submission
																						fees will be charged for the first two assignment
																						submission exam attempts. However, from the third
																						assignment submission exam attempt (applicable
																						fees) will be charged per subject per attempt.</p>
																				</div>
																				<!--/.panel-body -->
																			</div>
																			<!--/.panel-collapse -->
																		</div>
																		<!-- /.panel -->
																		<!-- End of BY PS Exams 3.8.3-->
																		<!-- BY PS Exams 3.8.4-->
																		<div class="panel panel-default faq">
																			<div class="panel-heading">
																				<h4 class="panel-title">
																					<a data-toggle="collapse" aria-expanded="false"
																						data-parent="#Assignments" href="#30084"> How
																						Assignments are accessed? </a>
																				</h4>
																			</div>
																			<!--/.panel-heading -->
																			<div id="30084" class="panel-collapse collapse out">
																				<div class="panel-body faqAns">
																					<p>- Students are free to refer to any
																						books/reference material/website/internet for
																						attempting their assignments but are not allowed
																						to copy the matter ad-verbatim from the source or
																						reference. Such assignments will be under copy
																						case.</p>
																					<p>- Copying of Assignments from other
																						students/group is strictly not allowed and will be
																						under copy case.</p>
																					<p>- Assignment which falls under copy case:
																						such assignments of the respective subject graded
																						as &#34;zero&#34;. However these students will be
																						allowed to register and appear for the scheduled
																						Term End Examination.</p>
																				</div>
																				<!--/.panel-body -->
																			</div>
																			<!--/.panel-collapse -->
																		</div>
																		<!-- /.panel -->
																		<!-- End of BY PS Exams 3.8.4-->
																		<!-- BY PS Exams 3.8.5-->
																		<div class="panel panel-default faq">
																			<div class="panel-heading">
																				<h4 class="panel-title">
																					<a data-toggle="collapse" aria-expanded="false"
																						data-parent="#Assignments" href="#30085">
																						Assignment status still showing &#34;Not
																						submitted&#34;&#34; even though students have made
																						the submission in the last examination cycle? </a>
																				</h4>
																			</div>
																			<!--/.panel-heading -->
																			<div id="30085" class="panel-collapse collapse out">
																				<div class="panel-body faqAns">
																					<p>Two aspect why you will see this message, if
																						the assignment is not submitted for that
																						particular subject or if the assignment is
																						submitted and last cycle assignment results are
																						not declared and new examination cycle assignments
																						are released. Once results are declared and if the
																						students clears the subject the assignments will
																						automatically disappear from the student&#39;s
																						dash board. Please ensure you check &#34;Previous
																						submissions&#34; to verify when have you submitted
																						the assignments and if you would like to re-submit
																						the same</p>
																					<p>Note : Best of assignment marks and Latest
																						of term end marks is taken in to consideration.</p>
																				</div>
																				<!--/.panel-body -->
																			</div>
																			<!--/.panel-collapse -->
																		</div>
																		<!-- /.panel -->
																		<!-- End of BY PS Exams 3.8.5-->
																		<!-- BY PS Exams 3.8.6-->
																		<div class="panel panel-default faq">
																			<div class="panel-heading">
																				<h4 class="panel-title">
																					<a data-toggle="collapse" aria-expanded="false"
																						data-parent="#Assignments" href="#30086"> Will
																						I still be eligible to appear for exams in case I
																						have not submitted the assignments? </a>
																				</h4>
																			</div>
																			<!--/.panel-heading -->
																			<div id="30086" class="panel-collapse collapse out">
																				<div class="panel-body faqAns">
																					<p>Yes, assignment submission is not a
																						pre-requisite to register and appear for the term
																						end examination. Student can register for the Term
																						End Examination without submitting the assignment.
																					</p>
																					<p>Link:
																						https://studentzone-ngasce.nmims.edu/studentzone/
																						&rarr; Exams &rarr; Assignment (For complete
																						guidelines on assignment)</p>
																				</div>
																				<!--/.panel-body -->
																			</div>
																			<!--/.panel-collapse -->
																		</div>
																		<!-- /.panel -->
																		<!-- End of BY PS Exams 3.8.6-->
																		<!-- BY PS Exams 3.8.7-->
																		<div class="panel panel-default faq">
																			<div class="panel-heading">
																				<h4 class="panel-title">
																					<a data-toggle="collapse" aria-expanded="false"
																						data-parent="#Assignments" href="#30087"> Is
																						Assignment Submission a prerequisite to register &
																						appear for my term end examinations? </a>
																				</h4>
																			</div>
																			<!--/.panel-heading -->
																			<div id="30087" class="panel-collapse collapse out">
																				<div class="panel-body faqAns">
																					<p>No, assignment submission is no longer a
																						pre-requisite to register and appear for the Term
																						End Examination. This policy is effective from
																						December, 2016 Exam Cycle onwards which will be
																						applicable to all students. However, Assignment
																						submission is a mandatory component along with
																						Term end.</p>
																				</div>
																				<!--/.panel-body -->
																			</div>
																			<!--/.panel-collapse -->
																		</div>
																		<!-- /.panel -->
																		<!-- End of BY PS Exams 3.8.7-->
																		<!-- BY PS Exams 3.8.8-->
																		<div class="panel panel-default faq">
																			<div class="panel-heading">
																				<h4 class="panel-title">
																					<a data-toggle="collapse" aria-expanded="false"
																						data-parent="#Assignments" href="#30088"> Can
																						a student appear for exam first and submit the
																						assignment later? </a>
																				</h4>
																			</div>
																			<!--/.panel-heading -->
																			<div id="30088" class="panel-collapse collapse out">
																				<div class="panel-body faqAns">
																					<p>Yes, Student can appear for Exams and then
																						submit the Assignment, however both components are
																						equally important for result declaration and
																						results would be on hold till the time assignment
																						is not submitted</p>
																				</div>
																				<!--/.panel-body -->
																			</div>
																			<!--/.panel-collapse -->
																		</div>
																		<!-- /.panel -->
																		<!-- End of BY PS Exams 3.8.8-->
																		<!-- BY PS Exams 3.8.9-->
																		<div class="panel panel-default faq">
																			<div class="panel-heading">
																				<h4 class="panel-title">
																					<a data-toggle="collapse" aria-expanded="false"
																						data-parent="#Assignments" href="#30089"> In
																						case I have submitted my assignments for a
																						particular exam cycle & do not wish to re-submit
																						my assignments then would my previous exam
																						cycle&#39;s assignments marks be carried forward?
																					</a>
																				</h4>
																			</div>
																			<!--/.panel-heading -->
																			<div id="30089" class="panel-collapse collapse out">
																				<div class="panel-body faqAns">
																					<p>Yes, assignment marks are carried forward in
																						case you have submitted your Assignment for a
																						particular exam cycle and do not wish to re-submit
																						your Assignment once again.</p>
																					<p>In case the student re-submits the
																						assignments then best of assignment marks will be
																						considered.</p>
																					<p>It is solely at the discretion of the
																						student whether they wish to submit their
																						assignments again for the next exam cycle.</p>
																				</div>
																				<!--/.panel-body -->
																			</div>
																			<!--/.panel-collapse -->
																		</div>
																		<!-- /.panel -->
																		<!-- End of BY PS Exams 3.8.9-->
																		<!-- BY PS Exams 3.8.10-->
																		<div class="panel panel-default faq">
																			<div class="panel-heading">
																				<h4 class="panel-title">
																					<a data-toggle="collapse" aria-expanded="false"
																						data-parent="#Assignments" href="#300810"> In
																						case I have submitted my Assignment and wish to
																						re-submit my Assignments to improvise my marks can
																						I do so? </a>
																				</h4>
																			</div>
																			<!--/.panel-heading -->
																			<div id="300810" class="panel-collapse collapse out">
																				<div class="panel-body faqAns">
																					<p>If you clear a particular subject you cannot
																						re-submit the assignment neither you can appear
																						for Term end exam. However if you have not cleared
																						the subject you can re-submit your assignments.
																						Best of assignment marks would be considered at
																						the time of result declaration.</p>
																				</div>
																				<!--/.panel-body -->
																			</div>
																			<!--/.panel-collapse -->
																		</div>
																		<!-- /.panel -->
																		<!-- End of BY PS Exams 3.8.10-->
																		<!-- BY PS Exams 3.8.11-->
																		<div class="panel panel-default faq">
																			<div class="panel-heading">
																				<h4 class="panel-title">
																					<a data-toggle="collapse" aria-expanded="false"
																						data-parent="#Assignments" href="#300811"> Can
																						student apply for assignment re-valuation? </a>
																				</h4>
																			</div>
																			<!--/.panel-heading -->
																			<div id="300811" class="panel-collapse collapse out">
																				<div class="panel-body faqAns">
																					<p>Yes a Student can apply for revaluation.
																						Pls. Note: Applying for revaluation does not
																						indicate that the marks would increase than the
																						original score. It could remain same, increase or
																						even decrease than the original score. Revaluation
																						Fees is Rs.1, 000/- per subject. Student can apply
																						and pay the prescribed revaluation fees through
																						student zone</p>
																					<p>Student Portal &rarr; Student Support &rarr;
																						Service request &rarr; Assignment re-valuation</p>
																				</div>
																				<!--/.panel-body -->
																			</div>
																			<!--/.panel-collapse -->
																		</div>
																		<!-- /.panel -->
																		<!-- End of BY PS Exams 3.8.11-->
																		<!-- BY PS Exams 3.8.12-->
																		<div class="panel panel-default faq">
																			<div class="panel-heading">
																				<h4 class="panel-title">
																					<a data-toggle="collapse" aria-expanded="false"
																						data-parent="#Assignments" href="#300812"> Do
																						we have Model assignments available for reference?
																					</a>
																				</h4>
																			</div>
																			<!--/.panel-heading -->
																			<div id="300812" class="panel-collapse collapse out">
																				<div class="panel-body faqAns">
																					<p>Yes, Model assignments are available for
																						semester I students under Student Portal &rarr;
																						Exams &rarr; Assignment section.Exam result</p>
																				</div>
																				<!--/.panel-body -->
																			</div>
																			<!--/.panel-collapse -->
																		</div>
																		<!-- /.panel -->
																		<!-- End of BY PS Exams 3.8.12-->
																		<!-- BY PS Exams 3.8.13-->
																		<div class="panel panel-default faq">
																			<div class="panel-heading">
																				<h4 class="panel-title">
																					<a data-toggle="collapse" aria-expanded="false"
																						data-parent="#Assignments" href="#300813">
																						When is student given grace marks? </a>
																				</h4>
																			</div>
																			<!--/.panel-heading -->
																			<div id="300813" class="panel-collapse collapse out">
																				<div class="panel-body faqAns">
																					<p>A candidate failing in one or more subject/s
																						in a semester is given up to 2 percent of the
																						marks on the aggregate marks of that subject, in
																						which he/she has appeared in the said examination
																						to enable him/her to pass the subject. (2% of 100
																						= 2 marks only in each subject & not more than 2).
																					</p>
																				</div>
																				<!--/.panel-body -->
																			</div>
																			<!--/.panel-collapse -->
																		</div>
																		<!-- /.panel -->
																		<!-- End of BY PS Exams 3.8.13-->
																		<!-- BY PS Exams 3.8.14-->
																		<div class="panel panel-default faq">
																			<div class="panel-heading">
																				<h4 class="panel-title">
																					<a data-toggle="collapse" aria-expanded="false"
																						data-parent="#Assignments" href="#300814">
																						When are the results declared? </a>
																				</h4>
																			</div>
																			<!--/.panel-heading -->
																			<div id="300814" class="panel-collapse collapse out">
																				<div class="panel-body faqAns">
																					<p>The results are generally declared within
																						four to six weeks after the last date of term end
																						examinations under student zone</p>
																					<p>Link:
																						https://studentzone-ngasce.nmims.edu/studentzone/
																						&rarr; Exams</p>
																				</div>
																				<!--/.panel-body -->
																			</div>
																			<!--/.panel-collapse -->
																		</div>
																		<!-- /.panel -->
																		<!-- End of BY PS Exams 3.8.14-->
																		<!-- BY PS Exams 3.8.15 -->
																		<div class="panel panel-default faq">
																			<div class="panel-heading">
																				<h4 class="panel-title">
																					<a data-toggle="collapse" aria-expanded="false"
																						data-parent="#Assignments" href="#300815"> How
																						can a student view his/her previous assignment and
																						term end exam marks? </a>
																				</h4>
																			</div>
																			<!--/.panel-heading -->
																			<div id="300815" class="panel-collapse collapse out">
																				<div class="panel-body faqAns">
																					<p>Link:
																						https://studentzone-ngasce.nmims.edu/studentzone/
																						&rarr; Exams &rarr; Marks History</p>
																				</div>
																				<!--/.panel-body -->
																			</div>
																			<!--/.panel-collapse -->
																		</div>
																		<!-- /.panel -->
																		<!-- End of BY PS Exams 3.8.15-->
																		<!-- BY PS Exams 3.8.16 -->
																		<div class="panel panel-default faq">
																			<div class="panel-heading">
																				<h4 class="panel-title">
																					<a data-toggle="collapse" aria-expanded="false"
																						data-parent="#Assignments" href="#300816"> Can
																						student apply for Assignment Revaluation? </a>
																				</h4>
																			</div>
																			<!--/.panel-heading -->
																			<div id="300816" class="panel-collapse collapse out">
																				<div class="panel-body faqAns">
																					<p>Yes a Student can apply for revaluation.
																						Pls. Note: Applying for revaluation does not
																						indicate that the marks would increase than the
																						original score. It could remain same, increase or
																						even decrease than the original score. Revaluation
																						Fees is Rs.1,000/- per subject. Student can apply
																						and pay the prescribed revaluation fees through
																						student zone</p>
																					<p>Link:
																						https://studentzone-ngasce.nmims.edu/studentzone/
																						&rarr; Student Support &rarr; Service request</p>
																					<p>Note: There is no revaluation done for copy
																						case.</p>
																				</div>
																				<!--/.panel-body -->
																			</div>
																			<!--/.panel-collapse -->
																		</div>
																		<!-- /.panel -->
																		<!-- End of BY PS Exams 3.8.16-->

																	</div>
																</div>
															</div>
														</div>
														<!-- End of  Assignments -->

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
																						or day it&#39;s because that the slots are full
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
																					<p>Exam fees are charged at Rs 600 per subject
																						per attempt.</p>
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
																						Which are the Exam Centre's for Re-Sit Term End
																						Examination? </a>
																				</h4>
																			</div>
																			<!--/.panel-heading -->
																			<div id="30094" class="panel-collapse collapse out">
																				<div class="panel-body faqAns">
																					<p>Re-Sit Exams in April/Sept. will be
																						conducted only at seven cities where NMIMS
																						University has its own Campus / NMIMS Regional
																						Offices i.e. Mumbai, Bangalore, Hyderabad, Delhi,
																						Pune, Ahmedabad and Kolkata. For Re-Sit exams
																						there will be no additional exam centre provided
																						apart from the mentioned cities/location.</p>
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
																						Students enrolled in which program qualify for the
																						Re-Sit Exam? </a>
																				</h4>
																			</div>
																			<!--/.panel-heading -->
																			<div id="30095" class="panel-collapse collapse out">
																				<div class="panel-body faqAns">
																					<p>Students enrolled only in Post Graduate (PG)
																						program (New Course) and Diploma program (New
																						Course) from July 2014 onwards and students
																						enrolled in all Certificate program would qualify
																						for the Re- Sit Term End Examination.</p>
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
																						data-parent="#registration" href="#30096">
																						Students enrolled in which program would not
																						qualify for the Re-Sit Exam? </a>
																				</h4>
																			</div>
																			<!--/.panel-heading -->
																			<div id="30096" class="panel-collapse collapse out">
																				<div class="panel-body faqAns">
																					<p>Students enrolled in Post Graduate (PG)
																						program (Old Course), Diploma program (Old Course)
																						prior to July 2014 and who have offline mode of
																						examination would not qualify for the Re-Sit Term
																						End Examination. Re-Sit Exam is not for offline
																						exam mode students.</p>
																				</div>
																				<!--/.panel-body -->
																			</div>
																			<!--/.panel-collapse -->
																		</div>
																		<!-- /.panel -->
																		<!-- End of BY PS exam 3.9.6-->
																		<!-- BY PS exams 3.9.7 -->
																		<div class="panel panel-default faq">
																			<div class="panel-heading">
																				<h4 class="panel-title">
																					<a data-toggle="collapse" aria-expanded="false"
																						data-parent="#registration" href="#30097"> Can
																						student directly appear for Re-Sit Term End
																						Examination? </a>
																				</h4>
																			</div>
																			<!--/.panel-heading -->
																			<div id="30097" class="panel-collapse collapse out">
																				<div class="panel-body faqAns">
																					<p>Student can directly appear for Re-Sit
																						Exams, but needs to complete minimum six months in
																						each semester after enrolment in a semester. For
																						e.g. student enrolling in (January Batch) will be
																						eligible to appear for the first term end
																						examination only in June and not for April, Re-Sit
																						examination and student enrolling in (July Batch)
																						will be eligible to appear for the first term end
																						examination only in December and not for
																						September, Re-Sit examination.</p>
																				</div>
																				<!--/.panel-body -->
																			</div>
																			<!--/.panel-collapse -->
																		</div>
																		<!-- /.panel -->
																		<!-- End of BY PS exam 3.9.7-->
																		<!-- BY PS exams 3.9.8 -->
																		<div class="panel panel-default faq">
																			<div class="panel-heading">
																				<h4 class="panel-title">
																					<a data-toggle="collapse" aria-expanded="false"
																						data-parent="#registration" href="#30098">
																						What is the eligibility criteria for Re-Sit Term
																						End Examination? </a>
																				</h4>
																			</div>
																			<!--/.panel-heading -->
																			<div id="30098" class="panel-collapse collapse out">
																				<div class="panel-body faqAns">
																					<p>To be eligible for the Re-Sit Term End
																						Examination every student is expected to complete
																						minimum six months in a semester after enrolment
																						in a semester.</p>
																				</div>
																				<!--/.panel-body -->
																			</div>
																			<!--/.panel-collapse -->
																		</div>
																		<!-- /.panel -->
																		<!-- End of BY PS exam 3.9.8-->
																		<!-- BY PS exams 3.9.9 -->
																		<div class="panel panel-default faq">
																			<div class="panel-heading">
																				<h4 class="panel-title">
																					<a data-toggle="collapse" aria-expanded="false"
																						data-parent="#registration" href="#30099">
																						Does student need to register for Re-Sit Term End
																						Examination? </a>
																				</h4>
																			</div>
																			<!--/.panel-heading -->
																			<div id="30099" class="panel-collapse collapse out">
																				<div class="panel-body faqAns">
																					<p>Yes. The student needs to register online
																						for the Re-Sit Term End Examination. The exam
																						registration is on first come first serve basis
																						when the online window for Re-Sit Term End Exam
																						opens. Student must not wait till last minute for
																						exam registration as it could lead to
																						unavailability of preferable exam centre/date/time
																						slot as seats are limited.</p>
																				</div>
																				<!--/.panel-body -->
																			</div>
																			<!--/.panel-collapse -->
																		</div>
																		<!-- /.panel -->
																		<!-- End of BY PS exam 3.9.9-->
																		<!-- BY PS exams 3.9.10 -->
																		<div class="panel panel-default faq">
																			<div class="panel-heading">
																				<h4 class="panel-title">
																					<a data-toggle="collapse" aria-expanded="false"
																						data-parent="#registration" href="#300910"> Is
																						the Exam schedule are Flexi schedule? </a>
																				</h4>
																			</div>
																			<!--/.panel-heading -->
																			<div id="300910" class="panel-collapse collapse out">
																				<div class="panel-body faqAns">
																					<p>Exams has flexi schedule and is schedule
																						over three weekends, three days (Fri/Sat/Sun) and
																						would be conducted in three exam time slots i.e. 3
																						weekends x 3 days x 3 exam time slot. The student
																						is free to choose the Exam Centre, Exam
																						Day/Date/Time over three weekends based on his/her
																						preference and appear for the examination. There
																						is no fixed date and subject examination timetable
																						for exams.</p>
																				</div>
																				<!--/.panel-body -->
																			</div>
																			<!--/.panel-collapse -->
																		</div>
																		<!-- /.panel -->
																		<!-- End of BY PS exam 3.9.10-->
																		<!-- BY PS exams 3.9.11 -->
																		<div class="panel panel-default faq">
																			<div class="panel-heading">
																				<h4 class="panel-title">
																					<a data-toggle="collapse" aria-expanded="false"
																						data-parent="#registration" href="#300911">
																						How many subject exams can a student appear in a
																						day? </a>
																				</h4>
																			</div>
																			<!--/.panel-heading -->
																			<div id="300911" class="panel-collapse collapse out">
																				<div class="panel-body faqAns">
																					<p>Student has choice to appear for one/two or
																						maximum three subject exams in a day, subject to
																						exam date/exam timeslot/exam centre availability.
																					</p>
																				</div>
																				<!--/.panel-body -->
																			</div>
																			<!--/.panel-collapse -->
																		</div>
																		<!-- /.panel -->
																		<!-- End of BY PS exam 3.9.11-->
																		<!-- BY PS exams 3.9.12 -->
																		<div class="panel panel-default faq">
																			<div class="panel-heading">
																				<h4 class="panel-title">
																					<a data-toggle="collapse" aria-expanded="false"
																						data-parent="#registration" href="#300912">
																						Can I give Exams from abroad? </a>
																				</h4>
																			</div>
																			<!--/.panel-heading -->
																			<div id="300912" class="panel-collapse collapse out">
																				<div class="panel-body faqAns">
																					<p>No, we don&#39;t conduct exams abroad, we
																						have our examination Centre&#39;s in India</p>
																				</div>
																				<!--/.panel-body -->
																			</div>
																			<!--/.panel-collapse -->
																		</div>
																		<!-- /.panel -->
																		<!-- End of BY PS exam 3.9.12-->

																	</div>
																</div>
															</div>
														</div>
														<!-- End of Exam registration -->

														<!-- Project -->

														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a class="collapsed" data-toggle="collapse"
																		aria-expanded="false" data-parent="#194"
																		href="#Project"> Project </a>
																</h4>
															</div>
															<!--/.panel-heading -->

															<div id="Project" class="panel-collapse collapse out">
																<div class="panel-body"
																	style="border-bottom: solid 2px grey; border-left: solid 2px grey; border-right: solid 2px grey;">
																	<div class="panel-group" id="nested">

																		<!-- BY PS Project 3.10.1 -->
																		<div class="panel panel-default faq">
																			<div class="panel-heading">
																				<h4 class="panel-title">
																					<a data-toggle="collapse" aria-expanded="false"
																						data-parent="#Project" href="#300101"> Where
																						can semester 4 student find project details? </a>
																				</h4>
																			</div>
																			<!--/.panel-heading -->
																			<div id="300101" class="panel-collapse collapse out">
																				<div class="panel-body faqAns">
																					<p>Project is one of the mandatory subject of
																						Semester &minus; IV (Marks out of 100) Student
																						needs to make their own project as per the
																						guidelines given, the topic needs to be chosen by
																						the students which should be related to their
																						specialization.</p>
																					<p>It is mandatory for the student to refer to
																						the Website/Student Zone for the latest Project
																						Preparation Guidelines and refer to last date of
																						Project submission as announced by NGA &minus; SCE
																						for the respective Exam Cycle. Non submission of
																						Project/failure in Project will lead to
																						non-completion of program.</p>
																					<p>Student Portal &rarr; Exams &rarr; Exam
																						registration</p>
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
																						data-parent="#Project" href="#300102"> How
																						does a student go ahead with project submission? </a>
																				</h4>
																			</div>
																			<!--/.panel-heading -->
																			<div id="300102" class="panel-collapse collapse out">
																				<div class="panel-body faqAns">
																					<p>Semester &minus; IV PG students have to
																						register for the Project. Exam Registration for
																						the Project will be Online. Student will have to
																						register for the Project along with Term End
																						Examination registration for scheduled Exam Cycle
																						when the Exam Registration Window opens. No
																						project sent in hardcopy or by email will be
																						accepted.</p>
																					<p>Student Portal &rarr; Exams &rarr; Project
																						submission.</p>
																				</div>
																				<!--/.panel-body -->
																			</div>
																			<!--/.panel-collapse -->
																		</div>
																		<!-- /.panel -->
																		<!-- End of BY PS Project 3.10.2-->

																	</div>
																</div>
															</div>
														</div>
														<!-- End of  Project -->

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

														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#194" href="#289"> What is the
																		evaluation component at NGA-SCE? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="289" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>NGA-SCE evaluation mechanism mainly has two
																		components: Internal Assignment (30marks) and Term End
																		Examination (70 marks). To be declared as "Pass" (50
																		marks) in each subject, appearance in both the above
																		components is mandatory.</p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->

														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#194" href="#290"> What is the
																		weightage given to Internal Assignment and Term End
																		Examinations? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="290" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>The Internal Assignment carry 30% weightage and
																		Term End Examination (Multiple Choice Questions +
																		Descriptive type questions) carry 70% weightage. (30 +
																		70 = 100 Total Marks).Aggregate passing-50%.</p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->

														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#194" href="#291"> What is the
																		minimum passing criterion for a particular subject? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="291" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>
																		Aggregate Passing is the criteria i.e. the student
																		must obtain 50 marks or more out of 100 marks in each
																		subject which includes the internal assignment marks
																		plus Term End Examination marks for passing a
																		particular subject. <b>Pls. Note:</b> In the above two
																		components, there is neither individual passing
																		criteria nor there is individual component cut-off
																		marks.
																	</p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->

														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#194" href="#292"> When are the
																		examinations held for Students enrolled from July 2014
																		batch onwards? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="292" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>
																		Examinations for Post Graduate Diploma program
																		/Diploma program students, enrolled from Jul. 2014 is
																		conducted in the month of June/ September/ December/
																		April. (For complete details refer to <a>http://distance.nmims.edu/</a>
																		Key dates and Student Resource Book)
																	</p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->

														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#194" href="#293"> When are the
																		examinations held for Students enrolled before July
																		2014 batch? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="293" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>Examinations for Post Graduate Diploma program
																		/Diploma program students, enrolled prior to Jul 2014
																		batch in old program is conducted in the month of June
																		and December.</p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->

														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#194" href="#294"> Where will I get
																		the Assignment questions? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="294" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>
																		Assignment questions are uploaded within the Student
																		Zone under Assignment Module. <b>Pls. Note:</b> For
																		every exam cycle, a fresh set of Assignment Questions
																		would be uploaded on the student zone. Student is
																		expected to download the applicable exam subject
																		assignment question paper and submit the assignment/s
																		through the Assignment Module on or before the last
																		date announced by NGA-SCE for that respective exam
																		cycle. Assignment submitted via email or hard copy
																		sent to NGA-SCE will not be accepted.Link: <a>https://studentzone-ngasce.nmims.edu/studentzone/</a>
																		-> Exams -> Assignment (For complete guidelines on
																		assignment)
																	</p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->

														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#194" href="#295"> Will I still be
																		eligible to appear for exams in case I have not
																		submitted the assignments? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="295" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>
																		Assignment submission is not a pre-requisite to
																		register and appear for the term end examination.
																		Student can register for the Term End Examination
																		without submitting the assignment. Link: <a>https://studentzone-ngasce.nmims.edu/studentzone/</a>
																		Exams -> Assignment (For complete guidelines on
																		assignment)
																	</p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->

														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#194" href="#297"> Can a student
																		appear for exam first and submit the assignment later?
																	</a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="297" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>Yes, Student can appear for Exams and then
																		submit the Assignment, however both components are
																		equally important for result declaration and results
																		would be on hold till the time assignment is not
																		submitted</p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->

														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#194" href="#298"> How do I submit my
																		Project? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="298" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>Project is one of the mandatory subjects in
																		Semester-IV for Post Graduate Diploma program. The
																		latest exam cycle applicable project guidelines need
																		to be followed by the student. Student needs to submit
																		the project online through student zone via the
																		project submission module on or before the last date
																		announced. No project sent in hardcopy or by email
																		will be accepted.</p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->

														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#194" href="#299"> Where can a
																		student get guidance for Project work preparation? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="299" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>
																		Project Preparation Guidelines are updated on the
																		website under the Examination Tab.Link: <a>https://studentzone-ngasce.nmims.edu/studentzone/</a>
																		Exams -> Projects (For complete guidelines on Project
																		Submission)
																	</p>


																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->

														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#194" href="#300"> Is there any
																		examination fee that I have to pay? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="300" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>Exam fee is Rs.500/- per subject per attempt.
																		When the Exam Registration window opens, student can
																		register and pay exam fees using their Debit Card,
																		Credit Card or Net Banking within the dates announced
																		by NGA-SCE. Exam fee payment via cash is not
																		acceptable. Exam fees once paid is neither refunded
																		nor carry forwarded to next exam cycle in case the
																		student cannot appear for the examination for reasons
																		whatsoever. 2nd attempt for any subject would be
																		charged at Rs 600/-</p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->

														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#194" href="#301"> What is the
																		process of applying or registering for Examinations? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="301" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>The student will have to register online for
																		appearing for the Term End Examination when the window
																		for Exam Registration opens. The exam registration is
																		on first come first serve basis when the online
																		registration window goes live (will be communicated
																		via Email/SMS/Announcement section). Student must not
																		wait till last minute for exam registration as it
																		could lead to unavailability of preferable exam
																		Centre/time slot. To avoid missing out any important
																		information/announcement/date, students must refer
																		regularly to our Website/Student zone as all latest
																		announcements are mentioned under Student Zone</p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->

														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#194" href="#306"> When are the
																		results declared? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="306" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>
																		The results are generally declared within four to six
																		weeks after the last date of term end examinations
																		under student zone.Link: <a>https://studentzone-ngasce.nmims.edu/studentzone/</a>
																		-> Exams
																	</p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->

														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#194" href="#308"> Can student apply
																		for Assignment Revaluation? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="308" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>
																		From June, 2015 exam onwards, after the declaration of
																		results, in case a student is not satisfied with the
																		assignment marks awarded to him/her and wish to apply
																		for assignment revaluation can do so. <b>Pls.
																			Note:</b> Applying for revaluation does not indicate that
																		the marks would increase than the original score. It
																		could remain same or increase or even decrease than
																		the original score. Revaluation Fees is Rs.1,000/- per
																		subject. There is no compulsion on any student to
																		apply for assignment revaluation from NMIMS
																		University. Student can apply and pay the prescribed
																		revaluation fees through student zone via student
																		service request on or before the last date announced
																		by NGA-SCE.
																	</p>
																	<p>
																		Link: <a>https://studentzone-ngasce.nmims.edu/studentzone/
																		</a> -> Student Support -> Service request
																	</p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->

														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#194" href="#310"> How can a student
																		view his/her previous assignment and term end exam
																		marks? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="310" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>
																		Link: <a>https://studentzone-ngasce.nmims.edu/studentzone/</a>
																		Exams -> Marks History
																	</p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->

														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#194" href="#311"> How can a student
																		get his/her final Certificate? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="311" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>
																		After successfully clearing all semesters, student is
																		eligible for issuance of final certificate. Student
																		needs to apply to NGA-SCE for issuance of his/her
																		Final Certificate by filling an application form which
																		is uploaded in Student Support System and/or mail it
																		to <a href="mailto: ngasce.exams@nmims.edu">ngasce.exams@nmims.edu</a>
																		<b>Pls. Note:</b> Student has to clear the program
																		(all subjects, all semesters) before the completion of
																		program validity from the date of admission.
																	</p>
																	<p>
																		Link:<a>https://studentzone-ngasce.nmims.edu/studentzone/
																		</a> -> Student Support -> Service request
																	</p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->

														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#194" href="#312"> What is the
																		process of getting a "Transcript""? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="312" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>A student will have to send an application with
																		scanned/photocopy of mark sheet &amp; make payment by
																		Demand Draft of Rs.1000 favoring "SVKM's NMIMS"
																		payable at Mumbai and send it to NGA-SCE. Three copies
																		of Transcript will be issued. For every additional
																		extra copy there will be charge of Rs. 300/-.</p>
																	<p>
																		Link: <a>https://studentzone-ngasce.nmims.edu/studentzone/
																		</a> -> Student Support -> Service request
																	</p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->

														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#194" href="#313"> When is the Re-Sit
																		Term End Examination scheduled? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="313" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>Re-Sit Term End Examination is scheduled in
																		April and September after the June/December Term End
																		Examination result declaration specifically for Post
																		Graduate Diploma and Diploma program students
																		appearing in online mode of examination based on the
																		eligibility criteria. (For complete details refer to
																		Student Resource Book)</p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->

														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#194" href="#314"> What is the
																		eligibility criterion for Re-Sit Term End Examination?
																	</a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="314" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>To be eligible for the Re-Sit Term End
																		Examination every student is expected to complete
																		minimum six months in a semester after enrolment in a
																		semester.</p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->

														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#194" href="#317"> Students enrolled
																		in which program qualify for the Re-Sit Exam? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="317" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>Students enrolled only in Post Graduate (PG)
																		program (New Course) &amp; Diploma program (New
																		Course) from July 2014 onwards and have online mode of
																		examination would qualify for the Re- Sit Term End
																		Examination.</p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->

														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#194" href="#318"> Students enrolled
																		in which program would not qualify for the Re-Sit
																		Exam? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="318" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>Students enrolled in Post Graduate (PG) program
																		(Old Course), Diploma program (Old Course) prior to
																		July 2014 &amp; all Certificate Course students, who
																		have offline mode of examination would not qualify for
																		the Re-Sit Term End Examination. Re-Sit Exam is not
																		for offline exam mode students.</p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->

														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#194" href="#319"> Who can register
																		for the Re-Sit Term End Examination? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="319" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>Online exam mode PG &amp; Diploma students, who
																		had submitted assignments for Jun/Dec exam cycle,
																		appeared for Jun/Dec. exams and failed, or students
																		who had submitted assignments for Jun/Dec exam cycle,
																		registered for Jun/Dec exams and remained absent, or
																		students who had submitted the assignment/s for
																		Jun/Dec exams and could not register for the Jun/Dec.
																		term end examination of subject/s or students who have
																		completed six months in a semester and had not
																		submitted assignment of few subject/s of the earlier
																		semester are now eligible for submission of assignment
																		and also eligible to register &amp; appear for the
																		Re-Sit term end examination.</p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->

														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#194" href="#321"> Can student
																		directly appear for Re-Sit Term End Examination? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="321" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>Student can directly appear for Re-Sit Exams,
																		but needs to complete minimum six months in each
																		semester after enrolment in a semester. For e.g.
																		student enrolling in (January Batch) will be eligible
																		to appear for the first term end examination only in
																		June and not for April, Re-Sit examination and student
																		enrolling in (July Batch) will be eligible to appear
																		for the first term end examination only in December
																		and not for September, Re-Sit examination.</p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->

														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#194" href="#322"> Does student need
																		to register for Re-Sit Term End Examination? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="322" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>Yes. The student needs to register online for
																		the Re-Sit Term End Examination. The exam registration
																		is on first come first serve basis when the online
																		window for Re-Sit Term End Exam opens. Student must
																		not wait till last minute for exam registration as it
																		could lead to unavailability of preferable exam
																		centre/date/time slot as seats are limited.</p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->

														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#194" href="#323"> Is Exam Fee
																		charged separately? What is the exam fee charge
																		towards Re-Sit Term End Examination? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="323" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>Exam fee is not a part of program fee and is
																		charged separately. Exam Fees is Rs.500/- per subject
																		per attempt. Exam fees once paid is neither refunded
																		nor carry forwarded to next exam cycle in case the
																		student cannot appear for the examination for reasons
																		whatsoever.</p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->

														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#194" href="#324"> Is the Exam
																		schedule for Re-Sit Exams Flexi schedule? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="324" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>Exams has flexi schedule and is schedule over
																		three weekends, three days (Fri/Sat/Sun) and would be
																		conducted in three exam time slots i.e. 3 weekends x 3
																		days x 3 exam time slot. The student is free to choose
																		the Exam Centre, Exam Day/Date/Time over three
																		weekends based on his/her preference and appear for
																		the examination. There is no fixed date and subject
																		examination timetable for exams.</p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->

														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#194" href="#325"> How many subject
																		exams can a student appear in a day in a Re-Sit
																		schedule? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="325" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>Student has choice to appear for one/two or
																		maximum three subject exams in a day, subject to exam
																		date/exam timeslot/exam centre availability.</p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->

														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#194" href="#326"> Which are the Exam
																		Centre's for Re-Sit Term End Examination? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="326" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>Re-Sit Exams in April/Sept. will be conducted
																		only at seven cities where NMIMS University has its
																		own Campus / NMIMS Regional Offices i.e. Mumbai,
																		Bangalore, Hyderabad, Delhi, Pune, Ahmedabad and
																		Kolkata. For Re-Sit exams there will be no additional
																		exam centre provided apart from the mentioned
																		cities/location.</p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->

														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#194" href="#326123"> What is Four
																		Examination Cycle Policy? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="326123" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>As per the revised examination policy w.e.f.
																		April, 2016 examination, to pass (clear) the failed
																		subject/s, the student now has the following options
																		to choose from:</p>
																	<p>(a) Submit only the failed subject assignment
																		and not appear for the term end exam</p>
																	<p>Or</p>
																	<p>(b) Register and appear for the failed subject/s
																		Re-Sit or Term End Examination (the previous exam
																		cycle assignment marks will be carry forwarded in case
																		there is no resubmission of assignment found in the
																		respective exam cycle)</p>
																	<p>Or</p>
																	<p>(c) Both re-submit the assignment of the failed
																		subject/s and also register and appear for the Term
																		End Examination of the failed subject/s</p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->

														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#194" href="#32612"> What is the
																		criteria for Grace Marks? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="32612" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>A candidate failing in one or more subject/s in
																		a semester is given up to 2 percent of the marks on
																		the aggregate marks of that subject, in which he/she
																		has appeared in the said examination to enable him/her
																		to pass the subject. (2% of 100 = 2 marks only in each
																		subject & not more than 2)</p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->

														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#194" href="#326ms"> What is the
																		process to get Marksheet/Certificate? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="326ms" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>Student has to raise a request for the issuance
																		of mark sheet/certificate by log into Student Zone ->
																		Service Request tab and the same will be available at
																		Student's Regional Office within 10 to 15 days. In
																		case, student wish to receive the mark
																		sheet/certificate at his/her residential address via
																		courier then student has to pay the courier charges of
																		Rs.100/- online in Service Request. Courier charges is
																		Rs.100/- per mark sheet/certificate.</p>
																	<p>
																		Link: <a>https://studentzone-ngasce.nmims.edu/studentzone/
																		</a> -> Student Support -> Service request
																	</p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->

														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#194" href="#326ca"> Can I have full
																		name of my parents /spouse name on my final
																		certificate? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="326ca" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>Only the first name of both the parents will be
																		reflected on the mark sheets and final certificate.</p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->

														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#194" href="#326w"> Will Distance
																		learning be mentioned on the final Certificate/ Mark
																		sheets? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="326w" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>Distance learning will not be mentioned on the
																		final certificate. You will be issued with the final
																		certificate from NMIMS Global Access School For
																		Continuing Education.</p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->

														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#194" href="#326ab"> Can I give Exams
																		from abroad? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="326ab" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>No, we don't conduct exams abroad, we have our
																		examination Centre's in India</p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->

														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#194" href="#331we"> Where will the
																		student get latest announcement relating to
																		assignment/examination? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="331we" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>Students need to regularly check the
																		website/student portal for all latest announcements to
																		avoid missing out any important date and/or
																		information.</p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->

													</div>
													<!--/.panel-group-->
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
														aria-expanded="false" data-parent="#accordion" href="#195">
														General </a>
												</h4>
											</div>
											<!--/.panel-heading -->

											<div id="195" class="panel-collapse collapse out">
												<div class="panel-body">
													<div class="panel-group" id="nested">
														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#195" href="#161"> What is NGA-SCE? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="161" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>NMIMS Global Access - School for Continuing
																		Education, (NGA-SCE) is one of the 9 schools of NMIMS
																		University offering Post Graduate Diploma Programs,
																		Diploma Programs and Certificate Programs in distance
																		learning mode.</p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->

														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#195" href="#163"> What are the
																		duration of various programs offered by NGA-SCE? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="163" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>
																		NGA-SCE offers 6 months Certificate programs, 1 year
																		Diploma Programs and 2 years Post Graduate Diploma
																		Programs in various specializations. To know more
																		about the specialization offered, kindly visit the
																		link : <a
																			href="http://distance.nmims.edu/programs.html"
																			target="_blank">
																			http://distance.nmims.edu/programs.html</a>
																	</p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->

														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#195" href="#164"> What is the
																		recognition of programs offered by NGA-SCE? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="164" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>The programs offered by NGA-SCE were approved by
																		the erstwhile Joint Committee of UGC-AICTE- DEC.
																		Subsequently, our PG as well as Diploma programs have
																		been approved by UGC - DEB. As per UGC Letter
																		F1No-52/2000(CPP-II) Dated May 05, 2004 it is
																		mentioned that degree/diploma/certificate awarded by
																		Open Universities in conformity with UGC notification
																		of degrees be treated as equivalent to corresponding
																		awards of the traditional universities in the country.</p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->

														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#195" href="#165"> Is NMIMS
																		University a member of Association of Indian
																		Universities (AIU)? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="165" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>
																		NMIMS University, our parent body, is a member of AIU.<br>
																		Link :<a
																			href="http://www.aiuweb.org/members/memberss.asp"
																			target="_blank">
																			http://www.aiuweb.org/members/memberss.asp</a>
																	</p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->

														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#195" href="#166"> What is the
																		ranking/awards for programs under NGA SCE? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="166" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>NGA-SCE has been ranked as one of the top 10
																		Schools providing Distance Education programs in
																		several surveys conducted.</p>
																	<p>
																		Zee Business ranks NMIMS School of Distance Learning
																		second in its ranking of the Top 10 B-Schools offering
																		Management Programs in Distance Learning Mode
																		consecutively for 2 years.<br> The DNA-Indus
																		Learning 2012 Survey identifies NMIMS School of
																		Distance Learning 5th in the top distance learning
																		institutes in India that have leveraged methodology,
																		technology, faculty and infrastructure to provide best
																		experience.<br> Competition Success Review
																		honored NMIMS Global Access-School for Continuing
																		Education as "Top Distance Learning Institute of
																		India" award consecutively for 2 years at the CSR
																		Awards for Excellence 2013 and 2014.
																	</p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->

														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#195" href="#255"> What is the role
																		of Admission Enrollment Partner (AEP) and Regional
																		Offices (RO)? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="255" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>Role of AUTHORIZED ENROLLMENT PARTNERS:</p>
																	<p>The prime responsibility of an Authorized
																		Enrollment Partner is to facilitate admission and
																		re-registration process and provide student support.
																		Authorized Enrollment Partner will also be responsible
																		for issuing study material along with student kit to
																		registered students.</p>
																	<p>Role of REGIONAL OFFICES:</p>
																	<p>Regional Offices are NMIMS owned centres having
																		state-of-the-art infrastructure to deliver quality
																		education. Students can access various digital
																		resources like Learning Management System, online
																		lectures, e-books, digital library, etc. The Regional
																		Offices are equipped with classrooms and have the best
																		in class faculty available to conduct Personal Contact
																		Programs offered by NGA-SCE. These offices also act as
																		local touch points for students within that area to
																		facilitate student support services.</p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->

														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#195" href="#256"> In which cities
																		are the Authorized Enrollment Partners and Regional
																		Offices of NGA-SCE present? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="256" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>
																		Kindly click on the link: <a title=""
																			href="http://distance.nmims.edu/centers.html">http://distance.nmims.edu/centers.html</a>
																		to know about the locations of our Regional Offices
																		&amp; Authorized Enrollment Partners
																	</p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->

														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#195" href="#257"> Am I allowed to
																		change my Authorized Enrollment Partner? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="257" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>Once registered, students are not permitted to
																		change their Authorized</p>
																	<p>Enrollment Partner within a city. However, you
																		can switch to an Authorized Enrollment Partner in
																		another city</p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->

														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#195" href="#258"> What will be the
																		procedure to change the Authorized Enrollment Partner?
																	</a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="258" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>The student will have to select the desired
																		Authorized Enrollment</p>
																	<p>Partner while applying for the re-registration
																		process itself. No request for change of Authorized
																		Enrollment Partner will be accepted post
																		re-registration process.</p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->

														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#195" href="#259"> Do I need to pay
																		any additional fee to change my Authorized Enrollment
																		Partner? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="259" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>Yes, you will have to pay a partner location
																		change fee of Rs.2500/-</p>
																	<p>which will be added to your Semester fee while
																		filling up the re-registration form.</p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->

														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#195" href="#260"> Will Authorized
																		Enrollment Partner be involved in any academic
																		process? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="260" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>No, Authorized Enrollment Partner will not be in
																		charge of any academic</p>
																	<p>process. Authorized Enrollment Partner is not
																		authorized to collect any additional fees from the
																		students for NMIMS programs. Any personal dealing with
																		Authorized Enrollment Partner will be at the student's
																		risk</p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->

														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#195" href="#261"> Will Regional
																		Office be involved in any academic process? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="261" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>Yes, the Regional Office will be responsible to
																		conduct Personal Contact</p>
																	<p>Programs, examinations and will also provide
																		digital resources like Learning Management System,
																		online lectures, e-books, digital library, etc.</p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->

														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#195" href="#332"> Is dual
																		specialization offered in the programs offered by
																		NGA-SCE? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="332" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>We do not offer dual specialization as a part of
																		our program.</p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->

														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#195" href="#334"> Can I change the
																		Specialization selected by me? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="334" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>A student can change their specialization only
																		once during the validity of his/her Registration,
																		subject to University's approval by paying the
																		applicable fee.</p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->

														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#195" href="#335"> When can I give my
																		request for change in specialization initially
																		selected by me? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="335" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>A student can request for change in
																		specialization only at the time of Re-Registration:</p>
																	<p>a) A Diploma student can request for change of
																		specialization only at the time of Re- registration to
																		semester II</p>
																	<p>b) A Post Graduate Diploma student can request
																		for a change of specialization only at the time of
																		Re-registration to semester II and semester III</p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->

														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#195" href="#340pu"> Can I pursue
																		more than one course simultaneously from NGA-SCE? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="340pu" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>As per UGC guidelines a student cannot pursue
																		more than one course from a University simultaneously,
																		hence we do not extend such facility for students.</p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->

														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#195" href="#334eg"> Am I eligible
																		for Government jobs after completion of PG Diploma
																		programs from NGASCE? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="334eg" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>You need to check the eligibility criteria
																		before enrolling for distance learning</p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->

														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#195" href="#334dc"> Is there any
																		discount for People with disabilities? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="334dc" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>There is No discount for People with
																		disabilities</p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->

														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#195" href="#334df"> What is the
																		difference between PG and MBA? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="334df" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>There is almost no difference between PGDM and
																		MBA but if we look at the basics, PGDM is</p>
																	<p>a diploma awarded by autonomous institutions,
																		whereas MBA is a degree program which is offered by a
																		University or a college.</p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->

													</div>
													<!--/.panel-group-->
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
														aria-expanded="false" data-parent="#accordion" href="#196">
														Student Support </a>
												</h4>
											</div>
											<!--/.panel-heading -->

											<div id="196" class="panel-collapse collapse out">
												<div class="panel-body">
													<div class="panel-group" id="nested">
														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#196" href="#400"> What is the
																		Escalation matrix? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="400" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>To ensure that all queries and concerns are
																		addressed on time we encourage students to follow the
																		below escalation matrix:</p>
																	<p>
																		<b>Toll Free number</b> is Operational Mon-Sat
																		(10am-6pm)
																	</p>
																	<p>
																		<b>Service Request module-</b> Helps you to raise an
																		online request and the same is monitored to ensure the
																		query is closed on time
																	</p>
																	<p>
																		Link: <a
																			href="https://studentzone-ngasce.nmims.edu/studentportal/"
																			target="_blank">
																			https://studentzone-ngasce.nmims.edu/studentportal/ </a>
																		-> Student Support -> Service request
																	</p>
																	<p>
																		<b>Cases -</b> Will help you to raise a query/concern
																		online and same is directed to the respective
																		team/function
																	</p>
																	<p>
																		Link: <a>https://studentzone-ngasce.nmims.edu/studentportal/
																		</a> -> Student Support -> Contact Us
																	</p>
																	<p>
																		Link: <a>http://distance.nmims.edu/help-and-support.html#writeUs</a>
																		(Doesn't have Student credentials)
																	</p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->

														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#196" href="#401"> What if I feel my
																		query is not responded as per my expectation and want
																		to escalate things further? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="401" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>
																		You can write in your concern with complete synopsis
																		to Nelson Soans- Head Student Services at <a>nelson.soans@nmims.edu</a>
																	</p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->

														<!-- BY PS Student Support 5.1 -->
														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#196" href="#5001"> What is the
																		student support working hours? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="5001" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>Student support operational Mon-Sat (10am-6pm)</p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->

														<!-- End of BY PS Student Support 5.1 -->
														<!-- BY PS Student Support 5.2 -->
														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#196" href="#5002"> Once a query is
																		raised how soon will there be a response? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="5002" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>A query will be responded within minimum 24 hrs.
																	</p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->

														<!-- End of BY PS Student Support 5.2 -->
													</div>
													<!--/.panel-group-->
												</div>
												<!--/.panel-body-->
											</div>
											<!--/.panel-collapse-->
										</div>
										<!--/.panel-->
									</div>
								</div>
							</div>
							<div class="clearfix"></div>



						</div>
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