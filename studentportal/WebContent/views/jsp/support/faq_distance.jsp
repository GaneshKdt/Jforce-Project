<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>

<html lang="en">




<jsp:include page="../common/jscss.jsp">
	<jsp:param value="FAQ" name="title" />
</jsp:include>



<body>

	<%@ include file="../common/header.jsp"%>



	<div class="sz-main-content-wrapper">

		<jsp:include page="../common/breadcrum.jsp">
			<jsp:param value="Student Zone;Student Support;FAQs"
				name="breadcrumItems" />
		</jsp:include>


		<div class="sz-main-content menu-closed">
			<div class="sz-main-content-inner">
				<jsp:include page="../common/left-sidebar.jsp">
					<jsp:param value="FAQs" name="activeMenu" />
				</jsp:include>


				<div class="sz-content-wrapper examsPage">
					<%@ include file="../common/studentInfoBar.jsp"%>


					<div class="sz-content">

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
								<div class="supportFaq">
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
																		data-parent="#192" href="#007"> How to register for Distance Learning Programs ? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="007" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>You can enroll for our Distance Learning Programs using the below link and paying the applicable charges<br>
																	
																		<a
																			href="https://ngasce.secure.force.com/nmLogin_New?type=registration"
																			title="Enroll for Distance Learning Programs Here">
																			https://ngasce.secure.force.com/nmLogin_New?type=registration
																		</a>
																	<br>
																	Please note : The registration charges are non-refundable.</p>
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
																		data-parent="#192" href="#008"> What is a Regional Office / Learning Center of NMIMS Global Access? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="008" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>To ensure effective support  NMIMS Global Access School has set up its own University Regional Offices 
																	across different locations in India - Mumbai, Navi Mumbai,  New Delhi, Kolkata, Bengaluru, Hyderabad, Pune, 
																	Indore, Ahmedabad, Chandigarh. These centres act as the local point of contact for students within that area 
																	to facilitate student support services. Our University Regional Offices are one of our biggest differentiators 
																	in online & distance learning space.</p>
																	<p>Follow the below link to get more information on our Regional Offices-<br>
																	
																		<a
																			href="https://distance.edu/contact-us/"
																			title="Regional Offices information">
																			https://distance.edu/contact-us/
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
																		data-parent="#192" href="#009"> What is a Authorized Enrollment Partner ( AEP )? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="009" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p> Authorized Enrollment Partner helps students with admission and documentation process. 
																	They also update students on important key dates and guide them with re-registration process.</p>
													
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
																		data-parent="#192" href="#010"> Will Authorized Enrollment Partner be involved in any academic process? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="010" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p> No, Authorized Enrollment Partner will not be in charge of any academic process. 
																	Authorized Enrollment Partner is not authorized to collect any additional fees from the students 
																	for NMIMS programs. Any personal dealing with Authorized Enrollment Partner will be at the student's risk.</p>
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
																		data-parent="#192" href="#07777"> Can I change my Authorized Enrolment Partner? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="07777" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>No, once selected you do not have the option to change the Authorized Enrolment Partner </p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
															
														<!-- End of BY PS 1.4-->
														<!--  BY PS 1.5-->
														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#192" href="#011"> Can we pursue dual specialization? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="011" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>No, we do not offer dual specialization. </p>
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
																		data-parent="#192" href="#012"> When can I enroll for a program? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="012" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>The University has two admission drives January and July </p>
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
																		data-parent="#192" href="#012a"> What is the Eligibility Criteria for the MBA (Distance) Programs? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="012a" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>Bachelor’s Degree (10+2+3) in any discipline from any recognised University or an equivalent degree recognised by Association of Indian Universities (AIU) with minimum 50% marks at Graduation Level.<br>
																	   OR<br>
																	   Bachelor’s Degree (10+2+3) in any discipline from any recognised University or equivalent degree recognised by Association of Indian Universities (AIU) with less than 50% marks at Graduation level and a minimum of 2 years of work experience.</p>
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
																		data-parent="#192" href="#013"> What is the Eligibility Criteria for the Diploma Programs? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="013" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>Bachelor's Degree (10+2+3) in any discipline from a recognized University or an equivalent degree recognized by AIU<br>
																	OR<br>
																	H.S.C plus 2 years of work experience<br>
																	OR<br>
																	S.S.C plus 3 years of Diploma recognized by AICTE and 2 years of work experience</p>

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
																		data-parent="#192" href="#014"> What is the Eligibility Criteria for the Certificate Programs? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="014" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>Bachelor's Degree (10+2+3) in any discipline from a recognised University or an equivalent degree recognised by AIU
																	   OR<br>
																	   H.S.C<br>
																	   OR<br>
																	   S.S.C plus 2 years of work experience</p>

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
																		data-parent="#192" href="#015"> What is the admission process? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="015" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>Follow the four-step process to complete the admission process for PG Diploma, Diploma & Certificate programs:<br>

																	   Step 1– Registration (fees are non refundable)<br>
																	
																	   Step 2– Submission of documents<br>
																	
																	   Step 3–Fee Payment<br>
																	
																	   Step 4– Confirmation</p>

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
																		data-parent="#192" href="#016"> Where do I submit my Admission documents? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="016" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>All your self-attested documents need to be submitted to your chosen Authorised Enrolment Partner.</p>
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
																		data-parent="#192" href="#017"> What is Provisional Admission? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="017" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>Provisional Admission is granted only if the candidate is eligible for the program but has not
																	 submitted following documents:<br>
																	   Final /Convocation Degree Marksheet/Certificate.</p>

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
																		data-parent="#192" href="#018"> What are the payment modes available? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="018" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>Modes of Fee payment:<br> 
																	   - Online on the School's website - distance.nmims.edu<br>
																		
																	   - Offline (Student submits the Demand Draft at the Authorized Enrolment Partner)<br>
																		
																	   - If the candidate wants to pay via Demand Draft he / she will have to visit the Authorized Enrolment 
																	   Partner (AEP) for submission of the Demand Draft.</p>
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
																		data-parent="#192" href="#07778"> Is there a change in the Program fees ? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="07778" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>The Program Fees has changed . The amount to be paid by the student will depend on the selection of Lecture pattern - Recorded Sessions only,
																	 Combination of Live + Recorded Session or all live sessions.</p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#192" href="#07779"> How will the Total Fees be calculated? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="07779" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>Total fees will be calculated as follows:
																	 If student has opted for the Live Lectures - 
																	 Total fees = Semester Fees + Fees paid towards the Live subjects
																	 
																	 If student has not opted for Live Lectures - 
																	 Total Fees = Semester Fees</p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#192" href="#07780"> How will the down payment be calculated at the time student applies for Loan ? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="07780" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>The down payment will be calculated on the Total Fees paid by the student.
 																	Eg:- If the student has chosen to pay for the full Program (Rs.120,000) + Live for all subjects in Semester 1 (Rs.6,000) - Down Payment amount will be calculated on Rs. 126,000</p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#192" href="#07781"> Can a student apply for Loan to select Live subject once the payment towards Semester fees is done? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="07781" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>Once the Semester fees are paid student will have to pay online for any additions, no loan facility will be available.</p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!--  BY PS 1.15-->
														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#192" href="#020"> Can I pursue more than 1 course simultaneously from NMIMS Global Access School? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="020" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>If you have enrolled for MBA (Distance) / Diploma / Certificate  programs, 
																	you can only take up another Certificate program alongside.</p>
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
																		data-parent="#192" href="#021"> What is Re-registration? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="021" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>Every student has to compulsorily re-register themselves for the next semester. This process is applicable for 
																	every semester, even for students who have paid Annual or Full fees, in order to activate the next semester access.
																	Only post re-registration will you be able to access the course material and lectures of the prospective semester.
																	This will also enable you to register and appear for the examination of the prospective semester.</p>
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
																		data-parent="#192" href="#022"> How do I re-register? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="022" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>You can re-register from your Student portal > Re-registration tab<br>
																	   It is also available under Student Portal > Quick Links > Re-Registration.</p>

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
																		data-parent="#192" href="#023"> Can I re-register even if I have not cleared a particular Semester? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="023" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>Yes, you can re-register into the next semester even if you haven't passed the subjects of the current semester.</p>
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
																		data-parent="#192" href="#024"> How can I change my choice of Program? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="024" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>You have the option to change your program within the same program group at the time of 
																	Re-registration (charges applicable).</p>
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
																		data-parent="#192" href="#025"> Will I be issued a new ID card post a Program Change? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="025" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>Once your program is changed, you will be issued a new identity card with the updated Program name along with the applicable study material. The Student number remains the same.</p>
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
																		data-parent="#192" href="#026"> When can I apply for a Program Change for a Diploma program? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="026" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>Program Change can be applied at the time of Re-Registration to Sem 2 </p>

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
																		data-parent="#192" href="#027"> When can I apply for a Program Change for MBA (Distance) program? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="027" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>Program Change can be applied at the time of Re-Registration to Sem 2 and/or Sem 3 (charges applicable)</p>
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
																		data-parent="#192" href="#028"> When can I apply for a Specialization Change for MBA (Distance) program? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="028" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>Specialization Change can be applied at the time of Re-Registration to Sem 2 and/or Sem 3 (charges applicable)</p>
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
																		data-patren="#192" href="#029"> I have completed my Diploma / Post Graduate Diploma (before July 2021) / Certificate (CBM) 
																		from NMIMS Global Access. 
																		Am I eligible to enrol for MBA (Distance) Program? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="029" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>Yes, you are eligible to do so via lateral entry. Conditions apply.</p>
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
																		data-patren="#192" href="#030"> Will I be issued a new ID card if I apply for a lateral admission? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="030" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>Yes, In case of lateral admission you will be issued a new ID card with a new Student Number.</p>
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
																		data-patren="#192" href="#031"> Will a student be eligible for a Fee Waiver in case of lateral admission? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="031" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>No fee waiver is applicable. Total Fees applicable will be as per the Semester in which the student is directly registered.</p>
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
																		data-patren="#192" href="#032">  Course Waiver is applicable to which students? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="032" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>Student who has completed Diploma and wants to enroll for MBA (Distance) within the  same specialization or  different specialization

																	Student who has completed Certificate in Business Management and wants to enrol for Diploma / MBA (Distance) program.

																	<b>Note :-</b> Application for course waiver should be received within 2 years of successful completion of an earlier program. Course waiver is not applicable for Project (Semester IV) for MBA (Distance) students</p>

																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->

														<!-- End of BY PS 1.27-->
														<!--  BY PS 1.29-->
														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-patren="#192" href="#034"> Can a student enrolled in Sem 1 of Post Graduate Diploma (before July 2021) / Diploma program apply for a lateral admission in MBA (Distance) program? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="034" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>Students currently in Sem 1 of Post Graduate Diploma / Diploma programs can choose to complete Semester 1 and apply for an Exit. 
																	 Such students can get a lateral entry into Sem 2 of MBA (Distance).
																	 <b>Note</b> - Student enrolled July 2019 onwards and applying for an exit will be awarded Certificate in Business Management. Program fees of MBA (Distance) will be applicable. 
																	</p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->

														<!-- End of BY PS 1.29-->
														
														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-patren="#192" href="#07782"> Student has completed Diploma program and wants to apply for a lateral admission in MBA (Distance) program? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="07782" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>Students currently in Sem 2 of Diploma programs, can choose to complete Semester 2.
																	 Such students can get a lateral entry into Sem 2 of MBA (Distance). 
																	 Students currently in Sem 2 of of Diploma in Business Management program can get a lateral entry into Sem 3 of MBA (Distance).
																	 <b>Note</b> - Program fees of MBA (Distance) will be applicable.
																	</p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														
														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-patren="#192" href="#07783"> Can a student enrolled in Certificate program apply for a lateral admission in MBA (Distance) program? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="07783" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>Students enrolled in Certificate (Only Certificate in Business Management) programs, can choose to complete the Semester.
																	 Such students can get a lateral entry into Sem 2 of MBA (Distance).
																	 <b>Note -</b> Program fees of MBA (Distance) will be applicable. 
																	</p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														
														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-patren="#192" href="#07784"> Can a student enrolled in Sem 2 of Post Graduate Diploma program (before July 2021) apply for a lateral admission in MBA (Distance) program? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="07784" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>Students currently in Sem 2 of Post Graduate Diploma programs can choose to complete Semester 2 and apply for an Exit. 
																	 Such students can get a lateral entry into Sem 3 of MBA (Distance).
																	 Note - Student enrolled July 2019 onwards and applying for an exit will be awarded Diploma in Business Management. 
																	 Program fees of MBA (Distance) will be applicable.Program fees of MBA (Distance) will be applicable.
 																	</p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														
														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-patren="#192" href="#07785"> Can a student enrolled in Sem 3 or Sem 4 of Post Graduate Diploma program  (before July 2021) apply for a lateral admission in MBA (Distance) program? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="07785" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>Student in Sem 3
																	Complete the Semester, apply for an Exit with Diploma in Business Management, take lateral admission into Sem 3 of MBA (Distance)	 
																	Student in Sem 4
																	Complete the Program and take lateral admission into Sem 3 of MBA (Distance)
 																	</p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														
														<!--  BY PS 1.30-->
														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-patren="#192" href="#035"> What happens if the Program Validity ends but there are subjects yet to clear? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="035" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>Sometimes students are not able to complete their Program within the validity period provided to them. 
																	In such scenarios student can apply for a Program Validity extension to clear any pending subjects. </p>
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
																		data-patren="#192" href="#036"> Can I extend my Program Validity and How ? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="036" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>In case you are unable to complete your program within the validity provided by the University, you can request to extend your validity.<br>

																	   Validity can be extended for the following period of the respective programs:<br>
																	
																	   Certificate Programs – 6 months<br>
																	
																	   Diploma Program –&nbsp;6 months<br>
																	
																	   MBA (Distance) Program – 1 year (only by 6 months at a time)<br>

																	   MBA (Distance)  Program  (Lateral Admission) – 6 months</p>
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
																		data-patren="#192" href="#037"> How to apply for a Validity Extension? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="037" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>You can follow the below link to get a validity extension-<br>
																	<a href="https://ngasce.secure.force.com/ApplyForValidityExtension"
																	>https://ngasce.secure.force.com/ApplyForValidityExtension</a><br>
																	 Note:<br>
																	  – Fees for extension of validity of the program will be 50% of the Semester fees applicable for the program ( as per the fee structure applicable at the time of admission)
																	  – Students need to apply for extension of validity within 12 months of the expiry of the validity period. MBA (Distance) students applying for further extension after expiry of the extended 6 months, need to apply within 6 months of the expiry of the first extended validity period.
																	  – University does not offer any refund policy; fees once paid towards extension of validity will not be refunded under any circumstances.
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->

														<!-- End of BY PS 1.32-->

														<!-- End of BY PS -->
														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#192" href="#333"> What is the procedure to cancel the admission? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="333" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>Admission Cancellation is subject to the dates as announced by the University.<br>
																	 Please refer to the website:<br>  
																	<a href="https://distance.nmims.edu/admission-process/">https://distance.nmims.edu/admission-process/</a>
																	Select Program > Cancellation/Refund Policy<br>	
																	To cancel your admission, you need to inform your Authorised Enrolment Partner or Regional Centre to 
																	initiate your admission cancellation process with the University. University will send a Refund form via 
																	mail which you will have to fill and submit along with the specimen of your signature. Kindly note there is a
																	process time line of 15 - 20 working days for payment Refund from the date the Refund form is submitted by you. 
																	The process of cancellation has to be initiated by a set date provided by the University, the same will be
																	 communicated at the time of admission.<br>

																	Note: The Admission Processing Fee/Registration fee is non-refundable. No canellation requests will be accepted after the 
																	last date of admission cancellation</p>
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
																		data-parent="#192" href="#331"> What is a Bonafide Certificate ? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="331" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>A Bonafide Certificate is a document issued as proof that you belong to a particular educational institute or an organisation.</p>
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
																		data-parent="#192" href="#336"> What is the process to get a Bonafide Certificate? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="336" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>Log in to Student Portal-> Student support > Service request > Issuance of Bonafide.</p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->
														
														<!-- 1.36 -->
														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#192" href="#337"> What is the process to apply for program Exit ? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="337" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>The student who wants to discontinue the program has to submit an application on ngasce@nmims.edu 
																	and raise the service request from your Student Portal > Student Support > Service Request > Exit the Program. </p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->
														
														<!-- 1.37 -->
														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#192" href="#339"> What is the exit policy for MBA (Distance) Student? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="339" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>Students who have taken admission to  MBA (Distance) Program but want to discontinue and exit :<br> 

																	   1. MBA (Distance) Students can discontinue the Program on successful completion of all the 
																	   courses of semester I and Semester II, after the approval is received from the University.<br> 

																	   2. Such students will then be awarded with a Diploma in General Management.</p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->

														<!-- 1.38 -->	
														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#192" href="#340"> Will the fees be refunded if a student chooses to Exit the program ? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="340" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>MBA (Distance) Students who have already taken admission/paid fees for the Semester III 
																	and/or Semester IV and Diploma students who have taken admission/paid fees for Semester II will not 
																	be refunded any fees if they apply for dis-continuation of the Program.</p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->
														
														<!-- 1.39 -->
														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#192" href="#341"> What is the Exit Policy for MBA (Distance) students enrolled before July 2019? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="341" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>Students who have taken admission to  MBA (Distance) Program but want to discontinue and exit :<br> 

																	Diploma Student: enrolled post July 2014 but before July 2019<br>
																	1.No Certificate will be awarded on completion of SEMESTER 1<br>
												
																	2.On successful completion of SEMESTER 2, they will be awarded Diploma in General Management</p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->
														<!-- 1.40 -->
														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#192" href="#342">What is the Exit Policy for Diploma Student enrolled in July 2019 onwards? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="342" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>Students who have taken admission to Diploma Program but want to discontinue and exit :<br> 
																	Diploma Student: enrolled post July 2019 :<br>
																	1.Diploma Students can discontinue the Program on successful completion of all the courses of semester I after the approval is received from the University.<br> 																	
																	2. Such students will then be awarded with a Certificate in Business Management.</p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->
														
														<!-- 1.41 -->
														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#192" href="#343"> What is Program Withdrawal process ? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="343" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>Student who wish to discontinue the program and not eligible for Exit needs to apply for Program withdrawal. 
																	Students needs to raise the request for same by writing to ngasce@nmims.edu<br>
																	<b>Please Note:</b> No refund is applicable for program withdrawal. </p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->
														
														<!-- 1.42 -->
														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#192" href="#344"> When will I get my Study Kit? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="344" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>After your admission is confirmed, you will receive your Course Material applicable to your program within 8-10  working days.
																	 The study kit will be dispatched either to your Shipping address or your Authorised Enrolment Partner which you would have chosen while taking your admission.<br>
																	   Any special area, which is out of dispatched location for our courier patners, will take longer time to dispatched. Please note: All the study kit is dispached from Mumbai main campus. </p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->
														
														<!-- 1.43 -->
														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#192" href="#345"> How do I opt to receive my study material after I complete my admission procedure? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="345" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>During the registration, you can either opt to receive the study material at your Shipping Address or your Authorised Enrolment Partner. Once you have chosen the respective the study material will be dispatched accordingly.</p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->
														
														<!-- 1.44 -->
														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#192" href="#346"> What will Study kit contain? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="346" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>Study kit will have your semester books and Welcome kit (ID card, Welcome letter and Student Undertaking form). </p>
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
																		data-parent="#192" href="#07786"> Is there a change in Credit Points? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="07786" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>There is no change in the structure of Credit Points. 
 																	<b>Note -</b> Only Nomenclature has changed from Post Graduate Diploma to MBA (Distance) </p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														
														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#192" href="#07787"> Have any of the subjects changed in the July 2021 structure? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="07787" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>There is no change in the subjects offered Semester wise
 																	Note - Only Nomenclature has changed from Post Graduate Diploma to MBA (Distance) </p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														
														<!-- 1.45 -->
														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#192" href="#347">Are books for all semetser given together ? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="347" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>Books are dispatched only for the semester student has registered for.</p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->
														
														<!-- 1.46 -->
														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#192" href="#348">What is to be done if I receive wrong study kit ?</a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="348" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>Incase of receipt of wrong study kit or incorrect books student needs to write an email to the University at ngasce@nmims.edu along with the screenshot of the book /s received.</p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->
														
														<!-- 1.47 -->
														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#192" href="#349">What happens if the Study Kit is returned to the University?</a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="349" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>The student will have to raise a request for Re-Dispatch (charges applicable)</p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->
														
														<!-- 1.48 -->
														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#192" href="#350">What can be done, if I have misplaced my study kit for a particular semester? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="350" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>If you have misplaced your study kit, you will be required to apply for a duplicate study kit for the particular semester by placing a service request via your Student Zone. The study kit charges will be applicable.<br>
																	   Student Portal > Student Support >  Service Request >   Re-Dispatch of Study kit.</p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->
														
														<!-- 1.49 -->
														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#192" href="#351">How will I receive my ID card ? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="351" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>The Welcome Kit will include the Books, Student Id card and Student Undertaking form (to be submitted to the AEP)</p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->
														
														<!-- 1.50 -->
														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#192" href="#352"> How will I receive the fee receipt? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="352" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>An E-Fee Receipt is sent to the registered Email Id.  Student can write an email to ngasce@nmims.edu for the Hard Copy of the Fee Receipt</p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->
														
														<!-- 1.51 -->
														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#192" href="#353">What if the Welcome Kit does not include my Student Id-Card ? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="353" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>The student needs to report this to the University within 7 working days of receipt of the Welcome Kit,. The student is expected to write an email to ngasce@nmims.edu</p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->
														
														<!-- 1.52 -->
														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#192" href="#354"> What if I misplace my ID card  ? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="354" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>Students can apply for a duplicate ID card by paying the appropriate charges from your Student portal > Student Support > Service request > Duplicate ID card. </p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->
														
														<!-- 1.53 -->
														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#192" href="#355"> In case I want to change my name on my student identity card, what is the process for the same? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="355" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>You will need to first raise Service Request for Name Change, once the changes are done you can then raise Service Request for Duplicate I-card (charges applicable)</p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->
														
														<!-- 1.54 -->
														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#192" href="#356"> Where do I need to submit the student declaration form once I have received the same in my study kit? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="356" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>You have to submit the original (hard-copy) student undertaking form to your Authorised Enrolment Partner that you have opted for at the time of your admission.</p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->
														
														<!-- 1.55 -->
														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#192" href="#357">Where do I need to collect my semester fee receipts once I have taken admission for the online & distance learning programs? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="357" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>Students are required to kindly get in touch with the Authorised Enrolment Partner, to get the Semester Fee Receipts.<br> 
																	   The E-Fee Receipt will be sent to the registered email id on admission confirmation. Student can also write to ngasce@nmims.edu for the hard copy of the fee receipt</p>
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
																	data-parent="#193" href="#7701"> Which programs is the July 2021 session structure applicable to? </a>
															</h4>
														</div>
														<!--/.panel-heading -->
														<div id="7701" class="panel-collapse collapse out">
															<div class="panel-body faqAns">
																<p> The new Session Structure is applicable to the enrollments - MBA (Distance) , Diploma , Certificate Programs from July 2021 onwards.</p>
															</div>
															<!--/.panel-body -->
														</div>
														<!--/.panel-collapse -->
													</div>
													
													<div class="panel panel-default faq">
														<div class="panel-heading">
															<h4 class="panel-title">
																<a data-toggle="collapse" aria-expanded="false"
																	data-parent="#193" href="#2001"> What Academic help will I receive after registering for the program? </a>
															</h4>
														</div>
														<!--/.panel-heading -->
														<div id="2001" class="panel-collapse collapse out">
															<div class="panel-body faqAns">
																<p> After registering for any program a student will receive semester-wise self learning material, 
																access to our online digital library , Online sessions (Live and Recorded), course presentations, 
																session presentations and additional reading material as shared by faculty (if any).</p>
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
																	data-parent="#193" href="#2002"> What is the benefit of attending the online sessions? </a>
															</h4>
														</div>
														<!--/.panel-heading -->
														<div id="2002" class="panel-collapse collapse out">
															<div class="panel-body faqAns">
																<p>Online lectures cover the learning of the entire course content on the basis of a session plan. Faculty interact via chat with students and thereby ensure enhanced student engagement. Real-time doubt clarification is also possible.</p>
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
																	data-parent="#193" href="#7702"> Which are the 3 options available for Sessions to student enrolled in MBA (Distance) / Diploma / Certificate program from July 2021 onwards? </a>
															</h4>
														</div>
														<!--/.panel-heading -->
														<div id="7702" class="panel-collapse collapse out">
															<div class="panel-body faqAns">
																<p>The students can choose to either 
 																Subscribe to Live Lectures in addition to the recordings (The pricing for complete Semester Subjects or Per Subjects for Live Recording is different)</p>
					 											<p>OR</p>
 																<p>Subscribe to Recorded sessions only OR Combination of Live v/s Recorded</p>
															</div>
															<!--/.panel-body -->
														</div>
														<!--/.panel-collapse -->
													</div>
													
													<div class="panel panel-default faq">
														<div class="panel-heading">
															<h4 class="panel-title">
																<a data-toggle="collapse" aria-expanded="false"
																	data-parent="#193" href="#7703"> What is the difference between Prime and Pro? </a>
															</h4>
														</div>
														<!--/.panel-heading -->
														<div id="7703" class="panel-collapse collapse out">
															<div class="panel-body faqAns">
																<p><b>Prime -</b><br> Access to Recorded sessions only 
 																<b>Pro -</b><br> 
 																Access to Live Lectures in addition to the Recordings ( The pricing for complete Semester Subjects or Per Subjects for Live Recording is different )</p>
															</div>
															<!--/.panel-body -->
														</div>
														<!--/.panel-collapse -->
													</div>
													
													<div class="panel panel-default faq">
														<div class="panel-heading">
															<h4 class="panel-title">
																<a data-toggle="collapse" aria-expanded="false"
																	data-parent="#193" href="#7704"> Will the student be able to choose subjects of which he wants to attend Live and which to refer to Recordings only? </a>
															</h4>
														</div>
														<!--/.panel-heading -->
														<div id="7704" class="panel-collapse collapse out">
															<div class="panel-body faqAns">
																<p>Students will have an option to un-select or change the choice of Program at the time of Registration ( subject to the Semester wise Fee payment )</p>
															</div>
															<!--/.panel-body -->
														</div>
														<!--/.panel-collapse -->
													</div>
													
													<div class="panel panel-default faq">
														<div class="panel-heading">
															<h4 class="panel-title">
																<a data-toggle="collapse" aria-expanded="false"
																	data-parent="#193" href="#7705"> What are the charges if the student wishes to attend live lectures for less than 6 subjects? </a>
															</h4>
														</div>
														<!--/.panel-heading -->
														<div id="7705" class="panel-collapse collapse out">
															<div class="panel-body faqAns">
																<p>If the student wishes to opt for live lectures for 1 to 4 subjects individually, they will be charged Rs. 1200 per subject (1200 * Number of subjects)</p>
															</div>
															<!--/.panel-body -->
														</div>
														<!--/.panel-collapse -->
													</div>
													
													<div class="panel panel-default faq">
														<div class="panel-heading">
															<h4 class="panel-title">
																<a data-toggle="collapse" aria-expanded="false"
																	data-parent="#193" href="#7706"> Student has paid in full for (Program Fees + Live Lectures) for all Semesters. Can he change or opt out of some subjects? </a>
															</h4>
														</div>
														<!--/.panel-heading -->
														<div id="7706" class="panel-collapse collapse out">
															<div class="panel-body faqAns">
																<p>The student cannot change or opt out of subjects once payment is made. No refund is applicable in such Cases</p>
															</div>
															<!--/.panel-body -->
														</div>
														<!--/.panel-collapse -->
													</div>
													
													<div class="panel panel-default faq">
														<div class="panel-heading">
															<h4 class="panel-title">
																<a data-toggle="collapse" aria-expanded="false"
																	data-parent="#193" href="#7707"> Student has selected Live Lectures for 4 subjects but wants to subscribe to Live Lectures for an additional subject. Is there a provision? </a>
															</h4>
														</div>
														<!--/.panel-heading -->
														<div id="7707" class="panel-collapse collapse out">
															<div class="panel-body faqAns">
																<p>Yes, The student can choose to add subjects for Live Lectures by raising an AEP Request and paying the applicable charges (Rs. 1200* Number of subject/s)
 																<br><b>Note -</b> For Sem 1 and Sem 2 , Student will need to check Start and End date of all Tracks (provided in the PDF) to understand how many tracks are yet to begin</p>
															</div>
															<!--/.panel-body -->
														</div>
														<!--/.panel-collapse -->
													</div>
													
													<div class="panel panel-default faq">
														<div class="panel-heading">
															<h4 class="panel-title">
																<a data-toggle="collapse" aria-expanded="false"
																	data-parent="#193" href="#7708"> Will a student be able to change the selection of Live subjects once Registration and Payment is done? </a>
															</h4>
														</div>
														<!--/.panel-heading -->
														<div id="7708" class="panel-collapse collapse out">
															<div class="panel-body faqAns">
																<p>Students will be permitted to change the selection of the live subjects after registration but before 7 days before the start of the lecture delivery of the first track / session
 																Students cannot change the selection of live subjects after lecture delivery start of the first track / session</p>
															</div>
															<!--/.panel-body -->
														</div>
														<!--/.panel-collapse -->
													</div>
													
													<div class="panel panel-default faq">
														<div class="panel-heading">
															<h4 class="panel-title">
																<a data-toggle="collapse" aria-expanded="false"
																	data-parent="#193" href="#7709"> Can a student reduce the number of Live subjects once selection is complete and payment is done? </a>
															</h4>
														</div>
														<!--/.panel-heading -->
														<div id="7709" class="panel-collapse collapse out">
															<div class="panel-body faqAns">
																<p>No, Once the payment is made a student cannot reduce the number of subjects selected for Live + Recording mode
 																<br><b>Note -</b> Fees paid will not be refunded.</p>
															</div>
															<!--/.panel-body -->
														</div>
														<!--/.panel-collapse -->
													</div>
													
													<div class="panel panel-default faq">
														<div class="panel-heading">
															<h4 class="panel-title">
																<a data-toggle="collapse" aria-expanded="false"
																	data-parent="#193" href="#7710"> Can a student add subjects (Live + Recorded mode) once selection is complete and payment is done? </a>
															</h4>
														</div>
														<!--/.panel-heading -->
														<div id="7710" class="panel-collapse collapse out">
															<div class="panel-body faqAns">
																<p>Yes, student will be able to pay for more live subjects during the Semester at the price of Rs. 1,200 per subject irrespective of the number of live sessions which have already been conducted for that subject.</p>
															</div>
															<!--/.panel-body -->
														</div>
														<!--/.panel-collapse -->
													</div>
													
													<div class="panel panel-default faq">
														<div class="panel-heading">
															<h4 class="panel-title">
																<a data-toggle="collapse" aria-expanded="false"
																	data-parent="#193" href="#7711"> If a student has selected 1/2/3/4 live subjects in a Semester and made the payment for it, can he/she change the selected subjects? </a>
															</h4>
														</div>
														<!--/.panel-heading -->
														<div id="7711" class="panel-collapse collapse out">
															<div class="panel-body faqAns">
																<p>If a student has selected 1/2/3/4 live subjects in a Semester and made the payment for it he /she will be permitted to change the selection of the live subjects after registration. This change can be done upto 7 days before the start of the lecture delivery of the first track
 																<br>Students cannot change the selection of live subjects after lecture delivery starts for the subject (first track)</p>
															</div>
															<!--/.panel-body -->
														</div>
														<!--/.panel-collapse -->
													</div>
													
													<div class="panel panel-default faq">
														<div class="panel-heading">
															<h4 class="panel-title">
																<a data-toggle="collapse" aria-expanded="false"
																	data-parent="#193" href="#2003"> How are lectures conducted? </a>
															</h4>
														</div>
														<!--/.panel-heading -->
														<div id="2003" class="panel-collapse collapse out">
															<div class="panel-body faqAns">
																<p>Lectures are conducted using the ZOOM platform.  Each subject is taught via live online sessions, timings for the same are updated in the Academic Calendar. 
																</p>
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
																	data-parent="#193" href="#2004"> How to attend live online lecture? </a>
															</h4>
														</div>
														<!--/.panel-heading -->
														<div id="2004" class="panel-collapse collapse out">
															<div class="panel-body faqAns">
																<p>Student Portal > Academic Calendar > Click on the scheduled Session. </p>
															</div>
															<!--/.panel-body -->
														</div>
														<!--/.panel-collapse -->
													</div>
													<!-- /.panel -->
													<!-- End of BY PS academics 2.4-->
													<div class="panel-group" id="nested">


														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#193" href="#242"> Can I access live lectures on a mobile device/cellphone? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="242" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>Yes, you can attend the online sessions on the desktop, as well as through our mobile app.</p>
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
																		data-parent="#193" href="#244"> Can I attend these sessions from my home/workplace/office? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="244" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>
																		Yes, you can attend these sessions from your home/workplace/office subject to availability of necessary IT infrastructure and firewall settings. </a>
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
																		data-parent="#193" href="#245"> Is it compulsory to attend the live online lectures? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="245" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>
																		Attendance is not mandatory for lectures but highly recommended. In case you have missed the lectures, recordings will be available within 48 - 72 hours on Student Portal > Session Videos</a>
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
																		data-parent="#193" href="#2451"> Is there any interaction with the faculty during live online lectures? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="2451" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>
																		Yes, you have a chat option to interact with the faculty during live online lectures.</a>
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
																		data-parent="#193" href="#243"> I’m getting “Meeting not in progress“ while trying to access the live online lecture. What should I do? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="243" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>This is not an error, kindly access the lectures 15 minutes before the lecture starts to avoid this message. You may book your seat for a particular faculty lecture 1 hour prior to the commencement of the lecture.</p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														
														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#193" href="#7712"> Student has opted for Recorded Session, will he have access to Orientation Sessions? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="7712" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>Yes, all students regardless of the Mode of Sessions delivery (Prime or Pro) chosen will be able to watch and refer to Orientation Sessions.</p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														
														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#193" href="#7713"> Student has opted for Recorded Session, will he have access to Doubt Clearing Sessions? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="7713" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>Yes, all students regardless of the Mode of Sessions delivery (Prime or Pro) chosen will be able to watch and refer to Doubt Clearing Sessions.</p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														
														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#193" href="#7714"> Does a student get to choose from Live or Recorded subjects? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="7714" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>Yes, student gets an option to choose from Live or Recorded subjects at the time Admission Registration and/ or Semester Re-Registration
 																	<b>The live subjects will be selected by default for the Semester at the time of Fee payment option, students will have to unselect subjects depending on their choice of Sessions</p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>

														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#193" href="#2441"> Where do I view my E-books/Course presentations ? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="2441" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>
																		The E-books/Course presentations will be available in the Student Portal. Follow the path below<br>
																		Student Portal > My Courses (left side of the portal)  > Select Subject > Resources<br> 
																		<b>Please Note :</b> The E-books are referred to Course Material on Student Portal
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
																		data-parent="#193" href="#24512"> Are the soft copies of study material downloadable? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="24512" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>The soft copies of Study material can be downloaded on the Mobile App only. It cannot be downloaded on the Computer / Laptop due to data protection restriction.</p>
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
																		data-parent="#193" href="#247"> Will I get Lecture notifications? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="247" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>The information will be sent via Email and SMS 24 hours in advance. The Academic Calendar is also updated with the Session schedule.
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
																		data-parent="#193" href="#248"> How can I select the lecture / Session batch to be attended? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="248" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>Every lecture is marked with a track making it easy for you to refer and download the session.<br> 
																	   There are colors to indicate different tracks making it easy for student to identify different tracks.<br> 
																	   We recommend you to follow one track.</p>
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
																		data-parent="#193" href="#250"> How are Sessions conducted for Semester 1  MBA (Distance)  Program students?
																	</a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="250" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>For Sem 1<br> 
																	   Lectures are conducted in 3 different tracks, Weekend Fast track, Weekend Slow track and Weekday batch,<br> 
																	   Weekend Batch Slow Track (2 sessions on every Saturday and Sunday in different slots)<br> 
																	   Weekday Batch -  1 session every day - Monday to Friday ( Saturday if required) in the evening from 7.00 pm to 9.00 pm<br>
																	   Weekend Batch-Fast Track - 3 sessions on every Saturday and Sunday in different slot  ( Friday if required)</p>
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
																		data-parent="#193" href="#251"> How are Sessions conducted for Semester 2  MBA (Distance)  Program students? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="251" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>For Sem 2<br> 
																	   Lectures are conducted in 2 different tracks, Weekend and Weekday batch:<br>
																	   Weekday Batch -  session every day - Monday to Friday ( Saturday if required) in the evening from 7.00 pm to 9.00 pm.<br>
																	   Weekend Batch - sessions on every Saturday and Sunday in different slot  ( Friday if required)</p>
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
																		data-parent="#193" href="#252">How are Sessions conducted for Semester 3 and 4 for   MBA (Distance)  Program students? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="252" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>For Sem 3 & 4<br> 
																	Lectures are conducted on Weekend only - sessions on every Saturday and Sunday in different slot  ( Friday if required)</p>
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
																		data-parent="#193" href="#253"> How are Sessions conducted for Semester 1 Diploma Program students?
																	</a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="253" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>For Sem 1<br> 
																	   Lectures are conducted in 3 different tracks, Weekend Fast track, Weekend Slow track and Weekday batch,<br> 
																	   Weekend Batch  Slow Track (2 sessions on every Saturday and Sunday in different slots)<br> 
																	   Weekday Batch -  1 session every day - Monday to Friday ( Saturday if required) in the evening from 7.00 pm to 9.00 pm<br>
																	   Weekend Batch&nbsp;Fast Track  - 3 sessions on every Saturday and Sunday in different slot  ( Friday if required)</p>
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
																		data-parent="#193" href="#254"> How are Sessions conducted for Semester 2  (Diploma Program) students? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="254" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>For Sem 2 <br>
																	 Lectures are conducted on Weekend:<br>
																	 Weekend Batch - sessions on every Saturday and Sunday in different slot  ( Friday if required)</p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->
														
														<!-- 2.20 -->
														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#193" href="#255"> How are Sessions conducted for Certificate Program students? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="255" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>Lectures are conducted on Weekend- Saturday and Sunday in different slot  ( Friday if required).<br>
																	 *Only for Certificate in Business Managnment (CBM) program lectures will be conducted in multiple slots over Weekend and Weekdays.</p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->
														
														<!-- 2.21 -->
														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#193" href="#256"> When will the recording of the live lectures be uploaded? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="256" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>The recordings are uploaded 48-72 working hours after the online lecture is conducted</p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->
														
														<!-- 2.22 -->
														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#193" href="#257">How to access recordings of lectures? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="257" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>In order to access recordings of lectures follow the given path in the student portal: </p>
																	<p>Student portal > My Courses (left side of the portal) >Subject > Resources</p>
																	<p>You can also view the recordings under session videos in the student portal.</p>
																	<p>Please Note : The Session videos can be downloaded on the Mobile App only. It cannot be downloaded on the Computer / Laptop due to data protection restriction.</p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->
														
														<!-- 2.23 -->
														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#193" href="#258">Where can I view recordings of the previous semester pending subjects?</a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="258" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	You can view the available session recordings in the Student Portal > Session videos, filter according to the subject and academic cycle to view the same.</p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->
														
														<!-- 2.24 -->
														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#193" href="#259">Can I access recordings on a mobile device?</a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="259" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>Yes, you can access the session recordings on mobile by downloading the mobile application.</p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->
														
														<!-- 2.25 -->
														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#193" href="#260">How will I get my queries resolved from the Faculty when I’m watching the recording of the lectures?</a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="260" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>Students have an option known as the "Post my query" under Student portal >  Academic Calendar Or Student Portal > My Courses > Q&A under each subject.<br>
																	   Here they can ask their queries and the faculty will revert to them within 48 hours.</p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->
														
														<!-- 2.26 -->
														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#193" href="#261">How do I log into the Student Portal once I take admission?</a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="261" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>You are required to login to your Student Zone with your SAP Id & Password provided by the University in the Welcome Email.<br>
																	   Please use the below link:<br>
																		<a href="https://studentzone-ngasce.edu/studentportal/">https://studentzone-ngasce.edu/studentportal/</a>
																	</p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->
														
														<!-- 2.27 -->
														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#193" href="#262">I’m getting “invalid credentials” while logging in to the Student Portal. What do I do?</a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="262" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>In case you are receiving the message of Invalid Credentials, you are entering either Student Number or Password or both incorrect. You are required to use the Forgot Password option to login to your Student Portal.<br>
																	Once you use Forgot Password option the password will be sent to your registered email id with which you can login to Student Portal.<br>
																	Please use Chrome to log in to the Student Portal<br>
																	Make sure you download the NMIMS Distance Learning App from the Playstore/App Store</p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->
														
														<!-- 2.28 -->
														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#193" href="#263">I’m getting “Session expired” while logging in to the Student Zone. What should I do?</a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="263" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>The error occurs due to few reasons:<br><br>
																	If the Feedback Form is not filled, students need to fill the feedback form and proceed.<br>
																	Check the connectivity and firewall settings.<br>
																	Update Student Portal  > My Profile > Personal Information > Parent's first name only<br>
																	Update Student Portal  > My Profile > Contact Information > Shipping Address<br>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->
														
														<!-- 2.29 -->
														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#193" href="#264">What are the features of online Digital Library? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="264" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>Digital Library facilities are provided for students who are willing to learn beyond books and their registered subjects. Users can access full text journals online.<br>
																	The contents have been organised in groups for easy access. The search interface allows for easy navigation. Students can access our Digital Library 24/7.<br>
																	The Digital library provides an user-friendly interface to access its resources, such as journals, databases, eBooks database, research database, company databases etc.<br>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->
														
														<!-- 2.30 -->
														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#193" href="#265">How does University recognize my academic achievement ?</a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="265" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>Student will be awarded Ranking and Badging for their performance throughout the academic journey with the University. </p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->
														
														<!-- 2.31 -->
														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#193" href="#266">What is Badging?</a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="266" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>Students will be awarded badges for various accomplishments that they achieve during their program eg: - Lecture Attendance, Assignment submission, Ask a Query<br>
																	These batches can be shared on the Linkedin Profile</p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->
														
														<!-- 2.32 -->
														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#193" href="#267">How can I claim the Badges? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="267" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>When the student performs the activity linked to the accomplishment the badge will be Unlocked which can be claimed by clicking on it. The Badges can be shared on the Linkedin Profile.</p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->
														
														<!-- 2.33 -->
														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#193" href="#268">Can I share Badges on other Social profiles</a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="268" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>Yes, Badges can be shared on the LinkedIn profile via the share button available on the Student Portal.</p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->
														
														<!-- 2.34 -->
														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#193" href="#269">What is Ranking ?</a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="269" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>Students are awarded Semester wise ranking and subject wish ranking on the student portal.<br> 
																	There is one leaderboard for every semester of your program. The leaderboard will display the names and scores of the top 5 ranked students across all subjects in that semester. The leaderboard will also display where do you stand (your rank) among your fellow students in the same semester of your program.<br>
																	Note: You will see your rank only if you have cleared all of your subjects in the very first attempt as per your semester registration month and year.<br>
																	The ranking will not be displayed for the students that pass a subject in backlog.</p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->
														
														<!-- 2.35 -->
														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#193" href="#270">I am unable to see my Rank on the student portal. </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="270" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>You will see your rank only if you have cleared all of your subjects in the very first attempt as per your semester registration month and year. The ranking will not be displayed for the students that pass a subject in backlog.<br>
																	Eg. If student has enrolled for January batch and have cleared all the subject in the default exam attemp (June exam cycle), rank will be awarded.</p>
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
										<div style="margin: 5px"></div>
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
																		data-parent="#194" href="#3001"> When are the Exams conducted? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="3001" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>There are 4 exam cycles in a year - June, September, December and April.<br>
																	The Main Examination for enrolled semester is held in June and December. eg:- For a student enrolled in Jan batch the first applicable exam will be in June. Similarly,  for a student enrolled in July batch the first applicable exam will be in December. 
																	April and September cycles are Re-sit  exams(to clear any pending subjects from the previous Semesters)</p>
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
																		data-parent="#194" href="#3002">What is a Resit Exam ?</a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="3002" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>Re-sit  exams are conducted to allow students to clear any pending subjects from the previous Semesters. April and September exam cycles are referred to as Re-Sit Exams.<br>
																	   eg: A student enrolled in January batch the default exam will be June. However if the student misses the June Exam he has an option to appear for the subjects in the September exam cycle</p>
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
																		data-parent="#194" href="#3003">What is the criteria for attending the Exams?</a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="3003" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>To be eligible for the Term End Examination, student is expected to complete the academic cycle of the Semester enrolled for.  There are 4 exam cycles in a year June, September, December and April.<br> 
																	   April and September exam cycle are not meant for students appearing first time in any Semester.</p>
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
																		data-parent="#194" href="#3004">What is the weightage given to Internal Assignment ?</a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="3004" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>The Internal Assignment carry 30% weightage - 30 marks</p>
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
																		data-parent="#194" href="#3005">What is the weightage given to Term End Examinations?</a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="3005" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>Term End Examination carry 70% weightage  - 70 marks.</p>
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
																		data-parent="#194" href="#3006">What is the Passing percentage for the enrolled program?</a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="3006" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>Diploma, MBA (Distance) - Aggregate passing - 50%  ( TEE + Assignment ) - 50 marks out of 100 marks.<br>

																	Certificate Programs - Aggregate passing-40%   ( TEE + Assignment ) - 40 marks out of 100 marks</p>
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
																		data-parent="#194" href="#3007">What is an Internal Assignment ? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="3007" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>The purpose of the Internal assessment is to evaluate the student’s understanding of concepts.
																	Assignments are set to evaluate the student’s thought process, conceptual understanding and application</p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->

														<!--End of BY PS 3.7 -->
														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#194" href="#289">Where are Assignments conducted? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="289" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>Students will be given a period of tentatively 70 - 75 days to prepare Assignments and submit online on the Portal.<br>
																	Students need to refer the latest applicable assignment question paper and guidelines applicable for the respective exam cycle before submitting the assignment.</p>
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
																		data-parent="#194" href="#290">How many Assignments is a student expected to submit?</a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="290" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>Number of Assignments is equal to the number of subjects applicable in a particular Semester.<br>
																	eg: - 6 Subjects / Semester = 6 Assignments</p>
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
																		data-parent="#194" href="#291">Where can a student access Assignment questions?</a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="291" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>Student Portal > Exams > Assignment<br>

																	<b>Pls. Note:</b> For every exam cycle, a fresh set of Assignment Questions would be uploaded on the student zone.
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
																		data-parent="#194" href="#292">Are there any model Assignments available for reference?</a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="292" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>Yes, Model assignments are available for students under:<br>

																	Student Zone > Exams > Assignment section<br>
																		
																	Also there are  videos for Assignment Preparation and Project Preparation under Session Videos
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
																		data-parent="#194" href="#293">How are Assignments /Projects evaluated?</a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="293" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>After the closure of the Assignment / Project submission due date, the Assignment / Project submitted by the students will be sent to the faculties for evaluation.<br>

																	<b>Pls. Note:</b> Since the evaluation is done online by the faculties there is no concept of sharing the checked photocopy . However, the overall faculty remarks given after evaluation will be shared with the students when the  result is declared.</p>
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
																		data-parent="#194" href="#294">Is there any Assignment fee applicable?</a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="294" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>
																		In each subject, no assignment submission fees will be charged for the first two assignment submission exam attempts. However, from the third assignment submission exam attempt (applicable fees) will be charged per subject per attempt.
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
																		data-parent="#194" href="#295">What happens if Assignment is marked under Copy Case?</a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="295" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>
																		Assignment falling under copy case will be graded as “zero”.&nbsp;The students scoring a zero in Assignments can opt to submit the Assignment in the upcoming Exam cycle by using the new Assignment question file ( Only if the student does not Pass in the subject - aggregate of TEE + Assignments)
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
																		data-parent="#194" href="#297">Is completing Internal Assignments a prerequisite to register & appear for Term End Examinations?
																	</a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="297" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>No, Assignment submission is no longer a pre-requisite to register and appear for the Term End Examination. However, Assignment submission is a mandatory component along with Term End Exam to be declared Pass in each subject.</p>
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
																		data-parent="#194" href="#298">Where can a student access Project details?</a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="298" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>Student Zone > Exams > Project<br>
																	Students need to pay the applicable fee and then proceed with the submission on or before the deadline date.</p>
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
																		data-parent="#194" href="#299">Is Project submission mandatory?</a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="299" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>
																		Project is one of the mandatory subject of Semester-IV (100 marks) for students enrolled in the MBA (Distance) program. 
																		Student needs to make their own Project as per the guidelines given, the topic needs to be chosen by the students which should be related to their specialization.
																		Non submission of Project/failure in Project will lead to non-completion of program.
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
																		data-parent="#194" href="#300">How many marks are allotted to Project?</a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="300" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>Project is scored out of 100 marks.</p>
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
																		data-parent="#194" href="#300a">What is the passing criteria for Project?</a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="300a" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>Student has to score a minimum of 50 marks or more out of 100 marks to be declared 'PASS' in Project.</p>
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
																		data-parent="#194" href="#301">Is there a Project fee applicable?</a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="301" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>Every Project submission attempt has an applicable fee.</p>
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
																		data-parent="#194" href="#306">Is there any model Project available for reference?</a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="306" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>No, University does not provide sample/model project. University provides Project preparation guidelines. 
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
																		data-parent="#194" href="#308">Is there any reference material that can be used for completing Assignments or Projects?</a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="308" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>Students are free to refer to any books/reference material/website/internet  but are not allowed to copy the matter verbatim from the source or reference.<br>
																	Assignments / Project that are copied ad-verbatim from any common source or reference and submitted will be scored ‘zero’&nbsp;and marked as Copy Case
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
																		data-parent="#194" href="#310">What happens if the Project is marked under Copy Case?</a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="310" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>If matter is copied ad-verbatim from the reference source the Project will be marked under  ‘Copy Case’. <br>
																	Projects falling under copy case will be graded as “zero”. The students scoring a zero in Project will have to submit a new Project in the upcoming Exam cycle
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
																		data-parent="#194" href="#311">What happens if a student clears all the 23 subjects but fails to submit Project?</a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="311" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>Project is one of the mandatory subject of Semester&nbsp;IV (Marks out of 100) for students enrolled in the Post Graduate Diploma program.<br> 
																	Non submission of Project / Failure in Project at the end of program Validity will lead to non-completion of the enrolled Program.
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
																		data-parent="#194" href="#312">What is the process of applying or registering for Term End examinations?</a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="312" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>The student will have to register from Student Zone > Exams > Exam Registration to appear for the Term End Examination when the window for Exam Registration opens. The exam registration is on first come first serve basis.<br>
																	Student will be communicated via Email/SMS/Announcement section once the Exam registration goes live.<br> 
																	<b>Note:-</b> Student must not wait till last minute for exam registration as it could lead to unavailability of preferable exam Centre/time slot.
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
																		data-parent="#194" href="#313">The slot for registering for exams isn’t available, even though the window is live. What should I do?</a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="313" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>Exam registration is done on first come first serve basis, so if a student is not able to see any slots available for that particular time or day it’s because that the slots are full for that day and time. <br>Students will have to select from the available slots.</p>
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
																		data-parent="#194" href="#314">What is the fee applicable for Exam Registration?
																	</a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="314" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>Exam fees are charged at Rs 600/- per subject per attempt.</p>
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
																		data-parent="#194" href="#317">Which are the Exam centres for Term End Examination? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="317" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>Exams will be conducted only at the NMIMS University / NMIMS Regional Offices i.e. Mumbai, Navi Mumbai, Bangalore, Chandigarh, Hyderabad, Delhi, Pune, Indore, Ahmedabad and Kolkata.<br> 
																	<b>Note - </b> Any additional centres will be declared at the time of Exam registration</p>
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
																		data-parent="#194" href="#318">Is the exam schedule flexible?</a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="318" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>Exams have flexible schedule and are scheduled on weekends (Fri/Sat/Sun) and would be conducted in three exam time slots. <br>The student is free to choose the Exam Centre, Exam Day/Date/Time based on his/her preference and appear for the examination. <br>There is no fixed date and subject examination timetable for exams.</p>
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
																		data-parent="#194" href="#319">How many subject exams can a student appear in a day?</a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="319" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>A student has a choice to appear for one/two or maximum three subject exams in a day, subject to exam date/exam timeslot/exam centre availability.</p>
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
																		data-parent="#194" href="#321">Can I give Exams from abroad?</a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="321" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>No, we do not conduct exams abroad, all our examination centres are in India only.</p>
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
																		data-parent="#194" href="#322">What happens if a student has registered but fails to appear for the Term end examinations?</a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="322" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>In case the student has registered for the examinations &  is not able to appear or give the examinations on the scheduled date the student will be marked ‘Absent’ for that Exam.<br>
																		Kindly note the exam registration fee cannot be refunded or adjusted with the next exam cycle.<br>
																		For e.g.: Student of June 2020 batch fails to appear for examination in December 2020 , he/she can appear for the examination in any of the upcoming examination cycle within the Program validity period.<br>
																		The same is applicable for Internal Assignments too.</p>
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
																		data-parent="#194" href="#323">Can a student directly appear for Re-Sit term end examination?</a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="323" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>Once student completes the academic cylce of the enroled semester, they can appear in any exam cycle within their program validity period. <br>There are two main examinations i.e June and December and two re-sit examination cycle i.e April and September. <br>For e.g. student enrolling in (January Batch) will be eligible to appear for the first term end examination only in June and not for April Re-Sit examination and student enrolling in (July Batch) will be eligible to appear for the first term end examination only in December and not for September, Re-Sit examination.</p>
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
																		data-parent="#194" href="#324">Does student need to register for Re-Sit term end examination?</a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="324" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>Yes. The student needs to register online for the Re-Sit term end examination. </p>
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
																		data-parent="#194" href="#325">Can a student appear for Term End Exams first and submit the Assignment later?</a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="325" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>Yes, Student can appear for Exams and then submit the Assignment, however both components are equally important for result declaration and results would be on hold till the time assignment is not submitted.</p>
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
																		data-parent="#194" href="#326">What should a student do in case their assignment/project status shows “Not submitted” on the Student Zone, even though he/she had made the submission in the last examination cycle? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="326" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>This happens in the below situations  :<br>
																	If the assignment is not submitted for that particular subject <br>
																	OR <br> 
																	If the Assignment is submitted, Last cycle assignment results are not declared and new examination cycle assignments are released 
																	OR<br>
																	Term End Exam for that subject is not cleared. </p>
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
																		data-parent="#194" href="#326123">When are the results declared?</a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="326123" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>The results are generally declared within four to six weeks after the last date of Term End Examinations under student zone.</p>
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
																		data-parent="#194" href="#32612">Can a student apply for Assignment/Project Re-valuation?</a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="32612" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>Yes a Student can apply for revaluation from Student Portal.<br>

																	<b>Note:</b> There is no revaluation done for Copy Case.</p>
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
																		data-parent="#194" href="#326ms">Are Assignment Copy case proofs shared with the student?</a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="326ms" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>The Assignment Copy Case is shared only on student's request using the Anydesk application
																	The Academic Co-ordinator will share the details with the student.<br>
																	<b>Note:</b> The proof's cannot be shared on Whatsapp or Email</p>
																
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
																		data-parent="#194" href="#326ca">Is Project Copy Case proof shared with the student?</a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="326ca" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>The Project Copy Case is not shared with the student however if the student demands the University can provide the Feedback.</p>
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
																		data-parent="#194" href="#326w">If a student re-submits a subject’s Assignment, can the marks from the previous exam cycle’s Term End Exam be carried forward?</a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="326w" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>Yes, If students are content with the marks they have in the term end exam it is not mandatory for them to re-submit the assignments again. <br>The marks for the previous Term End Exam will be carried forward.The University considers Best of Assignment and Best of Term End marks.</p>
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
																		data-parent="#194" href="#326ab">If a student is re-taking a subject’s Term End Exam, can the marks from the previous exam  cycle’s Assignment be carried forward?</a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="326ab" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>Yes, If students are content with the marks they have in assignments it is not mandatory for them to submit the assignments again. <br>The marks for the previous assignments submission will be carried forward.The University considers Best of Assignment and Best of Term End marks. </p>
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
																		data-parent="#194" href="#331we">What action is taken against the student if found copying during the Term End Exam?</a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="331we" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>Any student found copying or adopting any malpractices at the time of the exam will be marked under Unfair Means (UFM).
																		Such students will be issued a Show Cause Notice </p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->
														
														<!-- 3.44 -->
														
														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#194" href="#331wf">How is the Show Cause notice sent to the students?</a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="331wf" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>When a student is marked for UFM, a show cause notice is uploaded on the Student Portal, an alert will be displayed on the Student portal dashboard guiding them to go to the UFM notice page along with a notification email. <br>The student needs to click on the link to navigate to UFM dashboard. <br>The student can submit the UFM show cause response/explanation in the open text given for response. </p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->
														
														<!-- 3.45 -->
														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#194" href="#331wg">Post Result declaration my status is showing "RIA" </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="331wg" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>RIA stands for Result Kept in Abeyance, the same will be displayed when students has been issued UFM notice and result for UFM are yet to be declared. </p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->
														
														<!-- 3.46 -->
														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#194" href="#331wh">My subject scores is reflecting as NV ?</a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="331wh" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>NV stands for Null and Void. The UFM committee will go through the explanations and declare the decision. Based on the UFM <br>
																	committe decision if student clears the subject marks will be updated else if found gulity the status will change from RIA to NV.<br> 
																	<b>Please Note:</b> Subject marks as NV, students will have to re-appear for the particular subject by paying the examintion fees. </p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->
														
														<!-- 3.47 -->
														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#194" href="#331wi">What is the Grace Policy?</a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="331wi" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>A candidate failing in one or more subject/s in a semester is given up to 2 percent of the marks on the aggregate marks of that subject, in which he/she has appeared in the said examination to enable him/her to pass the subject. (2% of 100 = 2 marks only in each subject & not more than 2) </p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->
														
														<!-- 3.48 -->
														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#194" href="#331wj">Will a student be eligible for Grace marks (more than 2 marks / subject) if the maximum period of studies of a candidate for a program comes to an end and he/she is left out with one or more subjects to clear the program ?</a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="331wj" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>A candidate may be allotted not more than 12 marks (July 2014 Batch Onwards: New Course: Six subjects in each semester) 
																		If the maximum period of studies of a candidate for a program comes to an end and he/she is left out with one or more subjects to clear the program and it is mandatory for the student to appear in the last exam attempt of the program validity. </p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->
														
														<!-- 3.50 -->
														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#194" href="#331wl">How do I get my Mark sheets ?</a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="331wl" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>Students who want a hard copy of the Mark sheet  need to raise a service request through Student Zone:<br>

																	Student Zone > Student Support > Service Request > Issuance of Mark sheet<br>

																	You can opt to get the same delivered to your shipping address (charges applicable) or collect the same from your Regional Office. You will get a confirmation email once the documents are generated. </p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->
														
														<!-- 3.51 -->
														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#194" href="#331wm">How do I get my Final Certificate?</a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="331wm" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>Students who want a hard copy of the  Final Certificate need to raise a service request through Student Zone:<br>

																	Student Zone > Student Support > Service Request > Issuance of Final certificate.<br>

																	You can opt to get the same delivered to your shipping address (charges applicable) or collect the same from your Regional Office. You will get a confirmation email once the documents are generated. </p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->
														
														<!-- 3.52 -->
														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#194" href="#331wn">Can I share the Final Certificate on my other Social Profiles ?</a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="331wn" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>Yes, The Final Certificate can be shared from the Student Portal on your Linkedin Profile</p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->
														
														<!-- 3.53 -->
														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#194" href="#331wo">Is the photograph displayed on the Student Portal printed on the Final Certificate?</a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="331wo" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>Yes, The photograph displayed on the Student Portal is printed on the Final Certificate. </p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->
														
														<!-- 3.54 -->
														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#194" href="#331wp">How many days does it take for the Marksheet / Final Certificate to be ready?</a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="331wp" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>It takes approximately 10- 15 working days for the Marksheet / Final Certificate to be printed and ready. 
																	Student can check the status of the Service Request raised on the Student Portal Dashboard  - Scroll down to the bottom<br>
																	OR<br>
																	Student Portal > Student Support > Service Request </p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->
														
														<!-- 3.55 -->
														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#194" href="#331wq">Will Distance Learning be mentioned on the Final Certificate/Mark sheets?</a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="331wq" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>Distance learning will not be mentioned on the Final Certificate / Marksheet. TheFinal Certificate / Marksheet is issued from NMIMS Global Access School for Continuing Education, which is the distance learning school of NMIMS-Deemed to be University.</p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->
														
														<!-- 3.56 -->
														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#194" href="#331wr">Can I download the Marksheet instead of applying for the hard copy?</a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="331wr" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>Yes, you can download the Marksheet from Student Portal > Exams > Marksheet.<br>
																	<b>Note:</b> The soft copy of Marksheet downloaded from the Portal does not have the Authority's signature and University stamp. It is for reference purpose only </p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->
														
														<!-- 3.57 -->
														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#194" href="#331ws">Can I have full name of my Parents's / Spouse name on my Final Certificate?</a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="331ws" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>As per the University Policy and Format, only the first name of Parents's / Spouse will reflect on the Mark sheets and Final Certificate.</p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->
														
														<!-- 3.58 -->
														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#194" href="#331wt">What is a Transcript ?</a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="331wt" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>The Transcript is a consolidated Marksheet. </p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->
														
														<!-- 3.59 -->
														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#194" href="#331wu">Who can apply for a Transcript ?</a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="331wu" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>Student who has appeared for atleast 1 Exam can raise a request for a Transcript</p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->
														
														<!-- 3.60 -->
														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#194" href="#331wv">Do we mention Ranks / Grades / Percentage on the Final Certificate?</a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="331wv" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>There is no Rank / Grade / Percentage mentioned on the Final Certificate</p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->
														
														<!-- 3.61 -->
														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#194" href="#331ww">Do we mention Ranks / Grades / Percentage on the Transcript?</a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="331ww" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>Only total Percentage is mentioned on the Transcript</p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->
														
														<!-- 3.62 -->
														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#194" href="#331wx">What is WES?</a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="331wx" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>World Education Services (WES) is a nonprofit organization that provides credential evaluations for international students and immigrants planning to study or work in the U.S. and Canada</p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->
														
														<!-- 3.63 -->
														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#194" href="#331wy">What is the process of getting a “Transcript”?</a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="331wy" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>A student will have to raise a service request for a transcript by following the below pathway<br><br>
																	Student Portal > Student Support > Service Request > Issuance of Transcript<br><br>

																	3 copies of the Transcript will be issued for Rs. 1000/-. For every additional extra copy, there will be a charge of Rs. 300/-<br><br>

																	You can opt to get the same delivered to your shipping address (charges applicable) or collect the same from your Regional Office. It takes approximately 10- 15 working days for the Transcript to be printed and ready. You will get a confirmation email once the Transcripts are generated.</p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->
														
														<!-- 3.64 -->
														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#194" href="#331wz">Does the university send the Transcripts for credential evaluations?</a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="331wz" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>Yes, we do send the transcripts for credential evaluations (WES , IQAS etc) via True Copy (charges applicable)</p>
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
																
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
														Support and General  </a>
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
																	<p>NMIMS Global Access School for Continuing Education (NGA-SCE)/ Online is one of the 13 schools of SVKM’s (Deemed to be University). NGA-SCE offers Post Graduate Diploma Programs, Diplomas, Certificate Programs, Professional Programs, MBA (WX) and M.Sc. Applied Finance in distance learning mode.</p>
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
																		data-parent="#195" href="#163">What is the duration of various programs offered by NMIMS Online?</a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="163" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>NMIMS Global Access School For Continuing Education offers 6 months Certificate program, 1-year Diploma Programs, 2 years MBA (Distance) Program, 15-month MBA (WX) program, 1-year Professional Diploma programs and a 2-year M.Sc. Applied Finance. For further information please visit:
																			<a
																			href="http://distance.nmims.edu/ programs.html"
																			target="_blank">
																			http://distance.nmims.edu/ programs.html</a>
																	<br>
																	
																		For our Masters programs, visit 
																		<a
																			href="https://executive.nmims.edu/programs-details/"
																			target="_blank">
																			https://executive.nmims.edu/programs-details/</a>
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
																		data-parent="#195" href="#164"> What is the recognition of programs offered by NGA-SCE? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="164" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>Our programs are approved & recognised by University Grants Commission (UGC) & the Distance Education Bureau (UGC-DEB) of India. In 2018, Narsee Monjee Institute of Management Studies, Mumbai (Deemed to be University) was granted Autonomy Category 1 by UGC (University Grants Commission), thereby giving us a blanket approval to offer programs through open & distance learning modes. For details please refer to- 
																		<a
																			href="https://www.ugc.ac.in/pdfnews/8194522_HEIs-under-cat-I-DEB.pdf"
																			target="_blank">
																			https://www.ugc.ac.in/pdfnews/8194522_HEIs-under-cat-I-DEB.pdf</a>	
																	</p>
																	<p>The National Assessment and Accreditation Council have accredited us with Grade A+ in its 3rd cycle of assessment in 2018, which signifies the highest standards of academic leadership. As per UGC Letter F1No-52/2000(CPP-II) Dated May 05, 2004 it is mentioned that Degree/Diploma/Certificate awarded by Open Universities in conformity with UGC notification of degrees be treated as equivalent to corresponding awards of the traditional universities in the country.</p>
																	
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
																		data-parent="#195" href="#165">What is the ranking/awards for programs under NGA-SCE?</a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="165" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>Our LinkedIn page was recognised as the best page for content on thought leadership by LinkedIn India in 2019.</p>
																	<p>Zee Business ranked us second in its ranking of the Top 10 B-Schools offering Management Programs in Distance Learning Mode consecutively for 2 years.</p>
																	<p>The DNA-Indus Learning 2012 Survey identifies Online School of Distance Learning 5th in the top distance learning institutes in India that have leveraged methodology, technology, faculty and infrastructure to provide best experience.</p>
																	<p>Competition Success Review honored  Global Access School for Continuing Education as “Top Distance Learning Institute of India” award consecutively for 2 years at the CSR Awards for Excellence 2013 and 2014</p>
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
																		data-parent="#195" href="#166"> How many credit points are allotted per subject? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="166" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>The credits per subject are solely upto the discretion of the Institute/agencies (Within India or outside India) to acknowledge these scores as it would differ from State to State and Country to Country.</p>
																	<p>Individuals trying to seek admission/employment are recommended to check with the respective Institute / Country Immigration services. NMIMS programs are WES recognised and Yes, students who have passed out do apply for Transcripts and they are directly sent to WES. We haven’t until today received any complaint stating that credits offered by us are not honoured.
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
																		data-parent="#195" href="#5001"> Post completion of Post Graduate Diploma ( before July 2021) / MBA (Distance) can I enroll or apply for PhD programs?</a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="5001" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>PhD admissions is a prerogative of the Institution to which you are seeking admission for PhD. 
																	Each Institution has their own rules. 
																	It is up to the institutions who are considering your admission to decide whether to consider this PG program or not.
																	Many Institutions do consider this, some don’t. So you would need check with the institutes for the same.</p>
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
																		data-parent="#195" href="#5002">How can I connect with the University ?</a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="5002" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>You can connect with us on our toll-free no. 1800 1025 136 (Monday to Saturday between 9:00 AM to 7:00 PM) or email us at ngasce@nmims.edu or chat with the student counsellors.
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
																		data-parent="#195" href="#5003">What if I feel my query is not responded as per my expectation and want to escalate things further?</a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="5003" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>You can follow the Escalation Matrix
																	Student Portal > Student Support > Escalation Matrix
																	Alternatively,
																	You can connect with us on our Toll-free no. 1800 1025 136 (Monday to Saturday between 9:00 AM to 7:00 PM) 
																	or email us at ngasce@nmims.edu or chat with the student counsellors.</p>
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
																		data-parent="#195" href="#5004">What is the ‘Student Support’ centre working hours?
																	</a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="5004" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>The ‘Student Support’ is operational from Monday to Saturday from 09:00 AM till 07:00 PM.</p>
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
																		data-parent="#195" href="#5006">How soon can I expect a response after raising my query?</a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="5006" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>The query will be responded within 48 working hours. Once your query is received, if required it will be escalated to the concerned team for further assistance post which the response will be shared with the student.</p>
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
																		data-parent="#195" href="#5007">Is there any Whatsapp support group ?</a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="5007" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>No, there is no Whatsapp group created by the University. </p>
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
																		data-parent="#195" href="#5008">Can I change the registered E-mail Id submitted at the time of admission? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="5008" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>If the student wants to update their email ID, they have to send an application to the University with the following supporting documents:<br>

																	1) Address proof of the student’s address submitted to the University.<br>

																	2) Copy of Government ID card<br>

																	On verification of above documents, the e-mail id will be updated in University’s records & student will be able to login to the Student Zone successfully.</p>
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
																		data-parent="#195" href="#332">Can I make changes to Date of Birth after I take admission in Distance Learning?</a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="332" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>Yes, you can change your date of birth after taking admission in our distance learning programs. You are required to apply for the change by raising a service request via your Student Zone account and then upload your SSC mark sheet or any other valid proof of birth.</p>
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
																		data-parent="#195" href="#334">My registered email id is incorrect while I have taken admission for online & distance learning program. What is the process to update my email id? </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="334" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>If you have updated your e-mail id incorrectly at the time of your admission, you need to provide us with the updated e-mail id along with the supporting documents mentioned.<br><br>

																	Billing address proof copy as updated in our system & record<br>
																	Government ID card proof<br>
																	On verification of the above documents, the email id will be updated in our system & you will be able to login to the Student Zone successfully.</p>
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
																		data-parent="#195" href="#335">How do I update my Email id, Shipping address and Mobile number post admision confirmation?</a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="335" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>You can update your Email id, Shipping address and Mobile number from<br> 
																	Student Portal > My Profile > Contact Information</p>
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
																		data-parent="#195" href="#340pu">When can a student access the Alumni Portal ?</a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="340pu" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>Alumni Portal can be accessed from Student portal once a student is eligible for same.</p>
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
																		data-parent="#195" href="#334eg">What is the eligiblity for Alumni Portal ?</a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="334eg" class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																	<p>Post Graduation Diploma Program, MBA (Distance) students in IV semester who have cleared atleast 15 papers.<br>
																	Alumni of Post Graduation Diploma programs, MBA (Distance) and Diploma programs.</p>
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
	</div>


	<jsp:include page="../common/footer.jsp" />


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