
<%@page import="com.nmims.beans.PersonAcads"%>
<%@page import="com.nmims.beans.Page"%>

<html class="no-js">
<!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<jsp:include page="jscss.jsp">
	<jsp:param value="Faculty Review" name="title" />
</jsp:include>

<body class="inside">

	<%@ include file="header.jsp"%>

	<section class="content-container login">
		<div class="container-fluid customTheme">
			<div class="row">
				<legend>Update My Profile</legend>
			</div>
			<%@ include file="messages.jsp"%>
			<form:form id="update-profile" action="saveFacultyProfile"
				method="post" modelAttribute="faculty" enctype="multipart/form-data">


				<form:hidden path="id" />
				<form:hidden path="facultyId" />
				<form:hidden path="cvUrl" />
				<form:hidden path="imgUrl" />
				<div class="panel-body">

					<div class="col-md-9 column" id="page1" style="display: block">
						<div class="form-group">
							<img src="//placehold.it/100" class="avatar img-circle"
								alt="avatar">
							<h6>Upload a different photo...</h6>
							<form:input path="facultyImageFileData" type="file" />
						</div>

						<div class="form-group">
							<form:label for="title" path="title">Title</form:label>
							<form:select id="title" path="title" class="form-control"
								itemValue="${faculty.title}">
								<form:option value="">Select Title</form:option>
								<form:option value="Director">Director</form:option>
								<form:option value="Principal">Principal</form:option>
								<form:option value="Professor & Equivalent">Professor & Equivalent</form:option>
								<form:option value="Associate Professor">Associate Professor</form:option>
								<form:option value="Reader">Reader</form:option>
								<form:option value="Lecturer (Selection Grade)">Lecturer (Selection Grade) </form:option>
								<form:option value="Assistant Professor">Assistant Professor </form:option>
								<form:option value="Lecturer (Senior Scale)">Lecturer (Senior Scale)</form:option>
								<form:option value="Lecturer">Lecturer</form:option>
								<form:option value="Tutor">Tutor</form:option>
								<form:option value="Demonstrator">Demonstrator</form:option>
								<form:option value="Part-Time Teacher">Part-Time Teacher</form:option>
								<form:option value="Ad hoc Teacher">Ad hoc Teacher</form:option>
								<form:option value="Temporary Teacher">Temporary Teacher</form:option>
								<form:option value="Contract Teacher">Contract Teacher</form:option>
								<form:option value="Visiting Teacher">Visiting Teacher</form:option>
								<form:option value="Vice-Chancellor">Vice-Chancellor</form:option>
								<form:option value="Pro-Vice-Chancellor">Pro-Vice-Chancellor</form:option>
								<form:option value="Additional Professor">Additional Professor</form:option>
								<form:option value="Principal In-charge">Principal In-charge</form:option>
							</form:select>
						</div>

						<div class="form-group">

							<div class="col-md-6 column">
								<form:label for="firstName" path="firstName">First Name</form:label>
								<form:input id="firstName" path="firstName" class="form-control"
									placeholder="Enter First Name" />
							</div>

							<div class="col-md-6 column">
								<form:label for="middleName" path="middleName">Middle Name</form:label>
								<form:input id="middleName" path="middleName"
									class="form-control" placeholder="Enter Middle Name" />
							</div>

							<div class="col-md-6 column">
								<form:label for="lastName" path="lastName">Last Name</form:label>
								<form:input id="lastName" path="lastName" class="form-control"
									placeholder="Enter Last Name" />
							</div>

						</div>

						<div class="form-group">

							<div class="col-md-8 column">
								<form:label for="currentOrganization" path="currentOrganization">Current Organization Name</form:label>
								<form:input id="currentOrganization" path="currentOrganization"
									class="form-control"
									placeholder="Enter Current Organization Name" />
							</div>

							<div class="col-md-6 column">
								<form:label for="designation" path="designation">Designation</form:label>
								<form:input id="designation" path="designation"
									class="form-control" placeholder="Enter Designation" />
							</div>

						</div>

						<div class="form-group">
							<div class="col-md-6 column">
								<form:label for="dob" path="dob">Date of Birth</form:label>
								<form:input type="date" id="dob" path="dob" />
							</div>
						</div>

						<div class="form-group">
							<form:label for="email" path="email">Email(*)</form:label>
							<form:input type="email" class="form-control" id="email"
								placeholder="Enter Email" path="email" />
						</div>

						<div class="form-group">
							<form:label for="secondaryEmail" path="secondaryEmail">Secondary Email</form:label>
							<form:input type="email" class="form-control" id="secondaryEmail"
								placeholder="Enter Secondary Email (Optional)"
								path="secondaryEmail" />
						</div>

						<div class="form-group">
							<form:label for="mobile" path="mobile">Mobile No.(*)</form:label>
							<form:input type="text" id="mobile"
								class="form-control tenDigit numonly"
								placeholder="Enter Mobile No." path="mobile" />
						</div>

						<div class="form-group">
							<form:label for="altContact" path="altContact">Alternate Contact No.</form:label>
							<form:input type="text" id="altContact"
								class="form-control tenDigit numonly"
								placeholder="Enter Alternate Contact No." path="altContact" />
						</div>

						<div class="form-group">
							<form:label for="officeContact" path="officeContact">Office Contact No.</form:label>
							<form:input type="text" id="officeContact"
								class="form-control tenDigit numonly"
								placeholder="Enter Office Contact No. (Optional)"
								path="officeContact" />
						</div>

						<div class="form-group">
							<form:label for="homeContact" path="homeContact">Home Contact No.</form:label>
							<form:input type="text" id="homeContact"
								class="form-control tenDigit numonly"
								placeholder="Enter Home Contact No. (Optional)"
								path="homeContact" />
						</div>

						<div class="form-group">
							<form:label for="location" path="location">Location(*)</form:label>
							<form:input type="loction" class="form-control" id="loction"
								placeholder="Enter loction" path="location" />
						</div>

						<div class="form-group">
							<form:label for="address" path="address">Address</form:label>
							<form:textarea rows="5" cols="50" placeholder="Enter Address .."
								path="address" class="form-control" />
						</div>

						<div class='form-group'>
							<button id='next' type='button' onclick='show(this)'
								class='btn btn-primary page1Next'>Next</button>
						</div>

					</div>


					<div id='page2' class="col-md-9 column" style='display: none'>
						<div class="form-group">

							<table>
								<tr>
									<th>Education</th>
									<th>Description</th>
									<th>Year Of Passing</th>
								</tr>
								<tr>
									<td>Graduation</td>
									<td><form:textarea rows="3" cols="30"
											placeholder="BSC/BCom .." path="graduationDetails"
											class="form-control" /></td>
									<td><form:input path="yearOfPassingGraduation"
											id="yearOfPassingGraducation" class="form-control numonly"
											type="text" placeholder="Enter Year of Passing" /></td>
								</tr>
								<tr>
									<td>Phd</td>
									<td><form:textarea rows="3" cols="30"
											placeholder="Enter PHD Details .." path="phdDetails"
											class="form-control" /></td>
									<td><form:input path="yearOfPassingPhd"
											id="yearOfPassingPhd" class="form-control numonly"
											type="text" placeholder="Enter Year of Passing" /></td>
								</tr>
							</table>
						</div>

						<div class="form-group">

							<div style="float: left">
								<label> <form:checkbox path="net" id="net" value="net"
										style="width:25px;height:15px" /> NET
								</label>
							</div>
							<div style="float: left">
								<label> <form:checkbox path="setDetail" id="set"
										value="set" style="width:25px;height:15px" /> SET
								</label>
							</div>
						</div>

						<div class="form-group">

							<div class="col-md-6">
								<form:label for="teachingExp" path="teachingExp">Teaching Experience</form:label>
								<form:input path="teachingExp" id="teachingExp"
									class="form-control numonly" type="text"
									placeholder="Enter Years of Teaching Experience." />
							</div>



							<div class="col-md-6">
								<form:label for="corporateExp" path="corporateExp">Corporate Experience</form:label>
								<form:input path="corporateExp" id="corporateExp"
									class="form-control numonly" type="text"
									placeholder="Enter Years of Corporate Experience." />
							</div>



							<div class="col-md-6">
								<form:label for="ngasceExp" path="ngasceExp">NGASCE Experience</form:label>
								<form:input path="ngasceExp" id="ngasceExp"
									class="form-control numonly" type="text"
									placeholder="Enter Years of NGASCE Experience." />
							</div>


						</div>


						<h2>Subject Preferences</h2>
						<div class="form-group" style="overflow: visible;">
							<form:select id="subjectPref1" path="subjectPref1">
								<form:option value="">Select Subject Preference 1</form:option>
								<form:options items="${subjectList}" />
							</form:select>
						</div>

						<div class="form-group" style="overflow: visible;">
							<form:select id="subjectPref2" path="subjectPref2">
								<form:option value="">Select Subject Preference 2</form:option>
								<form:options items="${subjectList}" />
							</form:select>
						</div>

						<div class="form-group" style="overflow: visible;">
							<form:select id="subjectPref3" path="subjectPref3">
								<form:option value="">Select Subject Preference 3</form:option>
								<form:options items="${subjectList}" />
							</form:select>
						</div>



						<div class="form-group">
							<form:label for="cvFileData" path="cvFileData">Update CV</form:label>
							<form:input path="cvFileData" type="file" />
						</div>

						<div class='form-group'>
							<label class="control-form:label" for="submit"></label>
							<button type="button" class="btn btn-primary page2Previous"
								onclick="show(this)">Previous</button>
							<button id='next' type='button' onclick='show(this)'
								class='btn btn-primary page2Next'>Next</button>

						</div>

					</div>


					<div class="col-md-12 column" id="page3" style="display: none;">
						<div class="form-group">
							<div class="col-md-9" style="padding: inherit;">
								<form:label for="programGroup" path="programGroup">Program Group</form:label>
								<form:select id="programGroup" path="programGroup" type="text"
									placeholder="Program Group" class="form-control"
									multiple="multiple" size="5" required="required"
									itemValue="${faculty.programGroup}">
									<form:option value="">Select Program Group</form:option>
									<form:options items="${programTypes}" />
								</form:select>
							</div>
							<div class="col-md-9"
								style="padding: inherit; padding-left: 1rem;">
								<form:label for="programName" path="programName">Program Name</form:label>
								<form:select id="programName" path="programName" type="text"
									placeholder="Program Name" class="form-control"
									multiple="multiple" size="5" required="required"
									itemValue="${faculty.programName}">
									<form:option value="">Select Program Name</form:option>
									<form:options items="${programNames}" />
								</form:select>
							</div>
						</div>

						<div class="form-group">
							<form:label for="natureOfAppointment" path="natureOfAppointment">Nature of Appointment</form:label>
							<form:select id="natureOfAppointment" path="natureOfAppointment"
								class="form-control" itemValue="${faculty.natureOfAppointment}">
								<form:option value="">Select Nature of Appointment</form:option>
								<form:option value="Visiting - Teaching">Visiting - Teaching</form:option>
								<form:option value="Visiting - Evaluator">Visiting - Evaluator</form:option>
								<form:option value="Visiting - Reviewer">Visiting - Reviewer</form:option>
								<form:option value="Visiting - All">Visiting - All</form:option>
								<form:option value="Visting - AdHoc">Visting - AdHoc</form:option>
							</form:select>
						</div>

						<div class="form-group">
							<div class="col-md-9" style="padding: inherit;">
								<form:label for="areaOfSpecialisation"
									path="areaOfSpecialisation">Select Area of Specialisation</form:label>
								<form:select id="areaOfSpecialisation"
									path="areaOfSpecialisation" class="form-control"
									itemValue="${faculty.areaOfSpecialisation}">
									<c:choose>
    									<c:when test="${empty faculty.areaOfSpecialisation}">
       											<form:option value="">Select Option</form:option>
    									</c:when>
    									<c:otherwise>
        										<form:option value="${faculty.areaOfSpecialisation}">${faculty.areaOfSpecialisation}</form:option>
    									</c:otherwise>
									</c:choose>
									<form:option value="Business-Environment-and-Strategy">Business-Environment-and-Strategy</form:option>
									<form:option value="Finance">Finance</form:option>
									<form:option value="Communication">Communication</form:option>
									<form:option value="Economics">Economics</form:option>
									<form:option value="Human-Resources-And-Behavioral-Sciences">Human-Resources-And-Behavioral-Sciences</form:option>
									<form:option value="Marketing">Marketing</form:option>
									<form:option value="Information-Systems">Information-Systems</form:option>
									<form:option value="Operations and Decision Sciences">Operations and Decision Sciences</form:option>
									<form:option value="Any Other">Any Other</form:option>
								</form:select>
							</div>
							<div class="col-md-9"
								style="padding: inherit; padding-left: 1rem;">
								<c:choose>
    							<c:when test="${empty faculty.otherAreaOfSpecialisation}">
       								<div class="areaOfSpecialisation" style="display: none;">
    							</c:when>
    							<c:otherwise>
    								<div class="areaOfSpecialisation" style="display: block;">
        						</c:otherwise>
							</c:choose>
									<form:label for="otherAreaOfSpecialisation"
										path="otherAreaOfSpecialisation">Other Area of Specialisation</form:label>
									<form:input type="text" id="otherAreaOfSpecialisation"
										class="form-control"
										placeholder="Enter Other Area of Specialisation"
										path="otherAreaOfSpecialisation" />
								</div>
							</div>
						</div>



						<div class="form-group">
							<form:label for="aadharNumber" path="aadharNumber">Aadhar Number</form:label>
							<form:input type="text" id="aadharNumber"
								class="form-control numonly" maxlength="16"
								placeholder="Enter Aadhar Number" path="aadharNumber" />
						</div>

						<%-- <div class="form-group">
							<div class="col-md-9" style="padding: inherit;">
								<form:label for="approvedInSlab" path="approvedInSlab">Approved in Slab</form:label>
								<form:select id="approvedInSlab" path="approvedInSlab"
									class="form-control" itemValue="${faculty.approvedInSlab}">
									<form:option value="">Select Approval In Slab</form:option>
									<form:option value="A (7500)">A (7500)</form:option>
									<form:option value="B (5000)">B (5000)</form:option>
									<form:option value="C (3000)">C (3000)</form:option>
								</form:select>
							</div>
							<div class="col-md-9"
								style="padding: inherit; padding-left: 1em;">
								<div class="approvedInSlab" style="display: none;">
									<form:label for="dateOfECMeetingApprovalTaken"
										path="dateOfECMeetingApprovalTaken">Date of EC Meeting Approval Taken</form:label>
									<form:input type="date" id="dateOfECMeetingApprovalTaken"
										class="form-control"
										placeholder="Enter Date of EC Meeting Approval Taken"
										path="dateOfECMeetingApprovalTaken" />
								</div>
							</div>
						</div> --%>

						<div class="form-group">
							<div class="col-md-9" style="padding: inherit;">
								<form:label
									for="consentForMarketingCollateralsOrPhotoAndProfileRelease"
									path="consentForMarketingCollateralsOrPhotoAndProfileRelease">Consent for Marketing Collaterals / Photo & Profile Release</form:label>
								<form:select
									id="consentForMarketingCollateralsOrPhotoAndProfileRelease"
									path="consentForMarketingCollateralsOrPhotoAndProfileRelease"
									class="form-control"
									itemValue="${faculty.consentForMarketingCollateralsOrPhotoAndProfileRelease}">
									<form:option value="">Select Approval In Slab</form:option>
									<form:option value="Yes">Yes</form:option>
									<form:option value="No">No</form:option>
								</form:select>
							</div>

							<div class="col-md-9"
								style="padding: inherit; padding-left: 1rem;">
								<div
									class="consentForMarketingCollateralsOrPhotoAndProfileRelease"
									style="display: none;">
									<form:label
										for="consentForMarketingCollateralsOrPhotoAndProfileReleaseReason"
										path="consentForMarketingCollateralsOrPhotoAndProfileReleaseReason">Reason</form:label>
									<form:input type="text"
										id="consentForMarketingCollateralsOrPhotoAndProfileReleaseReason"
										class="form-control"
										placeholder="Enter Consent For Marketing Collaterals / Photo & Profile Release Reeason"
										path="consentForMarketingCollateralsOrPhotoAndProfileReleaseReason" />
								</div>
							</div>
						</div>

						<div class="form-group">
							<form:label for="honorsAndAwards" path="honorsAndAwards">Honors and Awards</form:label>
							<form:input type="text" id="honorsAndAwards" class="form-control"
								placeholder="Enter Honors and Awards" path="honorsAndAwards" />
						</div>

						<div class="form-group">
							<form:label for="memberships" path="memberships">Memberships</form:label>
							<form:input type="text" id="memberships" class="form-control"
								placeholder="Enter Memberships" path="memberships" />
						</div>

						<div class="form-group">
							<form:label for="researchInterest" path="researchInterest">Research Interest</form:label>
							<form:input type="text" id="researchInterest"
								class="form-control" placeholder="Enter Research Interest"
								path="researchInterest" />
						</div>

						<div class="form-group">
							<form:label for="articlesPublishedInInternationalJournals"
								path="articlesPublishedInInternationalJournals">Articles Published in International Journals</form:label>
							<form:input type="text"
								id="articlesPublishedInInternationalJournals"
								class="form-control"
								placeholder="Enter Articles Published in International Journals"
								path="articlesPublishedInInternationalJournals" />
						</div>

						<div class="form-group">
							<form:label for="articlesPublishedInNationalJournals"
								path="articlesPublishedInNationalJournals">Articles Published in National Journals</form:label>
							<form:input type="text" id="articlesPublishedInNationalJournals"
								class="form-control"
								placeholder="Enter Articles Published in National Journals"
								path="articlesPublishedInNationalJournals" />
						</div>

						<div class="form-group">
							<form:label for="summaryOfPapersPublishedInABDCJournals"
								path="summaryOfPapersPublishedInABDCJournals">Summary of Papers Published in ABDC Journals</form:label>
							<form:input type="text"
								id="summaryOfPapersPublishedInABDCJournals" class="form-control"
								placeholder="Enter Summary of Papers Published in ABDC Journals"
								path="summaryOfPapersPublishedInABDCJournals" />
						</div>

						<div class="form-group">
							<form:label for="paperPresentationsAtInternationalConference"
								path="paperPresentationsAtInternationalConference">Paper Presentations at International Conference</form:label>
							<form:input type="text"
								id="paperPresentationsAtInternationalConference"
								class="form-control"
								placeholder="Enter Paper Presentations at International Conference"
								path="paperPresentationsAtInternationalConference" />
						</div>



						<div class="form-group">
							<form:label for="paperPresentationAtNationalConference"
								path="paperPresentationAtNationalConference">Paper Presentations at National Conference</form:label>
							<form:input type="text"
								id="paperPresentationAtNationalConference" class="form-control"
								placeholder="Enter Paper Presentation at National Conference"
								path="paperPresentationAtNationalConference" />
						</div>

						<div class="form-group">
							<form:label for="caseStudiesPublished"
								path="caseStudiesPublished">Case Studies Published</form:label>
							<form:input type="text" id="caseStudiesPublished"
								class="form-control" placeholder="Enter Case Studies Published"
								path="caseStudiesPublished" />
						</div>

						<div class="form-group">
							<form:label for="booksPublished" path="booksPublished">Books Published</form:label>
							<form:input type="text" id="booksPublished" class="form-control"
								placeholder="Enter Books Published" path="booksPublished" />
						</div>

						<div class="form-group">
							<form:label for="bookChaptersPublished"
								path="bookChaptersPublished">Book Chapters Published</form:label>
							<form:input type="text" id="bookChaptersPublished"
								class="form-control" placeholder="Enter Book Chapters Published"
								path="bookChaptersPublished" />
						</div>

						<div class="form-group">
							<form:label for="listOfPatents" path="listOfPatents">List of Patents</form:label>
							<form:input type="text" id="listOfPatents" class="form-control"
								placeholder="Enter List of Patents" path="listOfPatents" />
						</div>

						<div class="form-group">
							<form:label for="consultingProjects" path="consultingProjects">Consulting Projects</form:label>
							<form:input type="text" id="consultingProjects"
								class="form-control" placeholder="Enter Consulting Projects"
								path="consultingProjects" />
						</div>

						<div class="form-group">
							<form:label for="researchProjects" path="researchProjects">Research Projects (Research grants)</form:label>
							<form:input type="text" id="researchProjects"
								class="form-control"
								placeholder="Enter Research Projects (Research grants)"
								path="researchProjects" />
						</div>

						<div class="form-group">
							<form:label for="linkedInProfileUrl" path="linkedInProfileUrl">LinkedIn Profile Url</form:label>
							<form:input id="linkedInProfileUrl" path="linkedInProfileUrl"
								type="text" placeholder="LinkedIn Profile Url"
								class="form-control" />
						</div>

						<div class="form-group">
							<label class="control-form:label" for="submit"></label>

							<button type="submit" class="btn btn-primary" id="submit"
								formaction="saveFacultyProfile">Update Information</button>

							<button type="button" class="btn btn-primary page3Previous"
								onclick="show(this)">Previous</button>

							<button type="button" class="btn btn-danger" onclick="cancel()">Cancel</button>

						</div>

					</div>


				</div>


			</form:form>
		</div>


	</section>

	<jsp:include page="footer.jsp" />

	<script>
		$(document)
				.ready(
						function() {
							$(".numonly")
									.keypress(
											function(e) {
												return (e.which != 8
														&& e.which != 0 && (e.which > 57 || e.which < 48)) ? false
														: true;
											});

							$("select")
									.each(
											function() {
												if ($(this).attr("id") == "areaOfSpecialisation"
														&& $(this).val() == "Any Other") {
													$("." + $(this).attr("id"))
															.attr("style",
																	"display:block;");
													$(
															"."
																	+ $(this)
																			.attr(
																					"id")
																	+ " input")
															.attr("required",
																	"required");
												} /* else if ($(this).attr("id") == "approvedInSlab"
														&& ($(this).val() == "A (7500)" || $(
																this).val() == "B (5000)")) {
													$("." + $(this).attr("id"))
															.attr("style",
																	"display:block;");
													$(
															"."
																	+ $(this)
																			.attr(
																					"id")
																	+ " input")
															.attr("required",
																	"required");
												} */ else if ($(this).attr("id") == "consentForMarketingCollateralsOrPhotoAndProfileRelease"
														&& $(this).val() == "No") {
													$("." + $(this).attr("id"))
															.attr("style",
																	"display:block;");
													$(
															"."
																	+ $(this)
																			.attr(
																					"id")
																	+ " input")
															.attr("required",
																	"required");
												}

											});
						});

		var x = document.getElementById("page1");
		var y = document.getElementById("page2");
		var z = document.getElementById("page3");

		function show(obj) {

			if ($(obj).attr("class").split(" ")[2] == "page1Next") {
				x.style.display = "none";
				y.style.display = "block";
				z.style.display = "none";
			} else if ($(obj).attr("class").split(" ")[2] == "page2Previous") {
				x.style.display = "block";
				y.style.display = "none";
				z.style.display = "none";
			} else if ($(obj).attr("class").split(" ")[2] == "page2Next") {
				x.style.display = "none";
				y.style.display = "none";
				z.style.display = "block";
			} else if ($(obj).attr("class").split(" ")[2] == "page3Previous") {
				x.style.display = "none";
				y.style.display = "block";
				z.style.display = "none";
			}
		}

		function cancel() {
			x.style.display = "block";
			y.style.display = "none";
			z.style.display = "none";
		}

		$("select")
				.change(
						function() {
							if ($(this).attr("id") == "areaOfSpecialisation") {
								if ($(this).val() == "Any Other") {
									$("." + $(this).attr("id")).attr("style",
											"display:block;");
									$("." + $(this).attr("id") + " input")
											.attr("required", "required");
								} else {
									$("." + $(this).attr("id")).attr("style",
											"display:none;");
									$("." + $(this).attr("id") + " input")
											.removeAttr("required");
								}
							} /* else if ($(this).attr("id") == "approvedInSlab") {
								if ($(this).val() == "A (7500)") {
									$("." + $(this).attr("id")).attr("style",
											"display:block;");
									$("." + $(this).attr("id") + " input")
											.attr("required", "required");
								} else if ($(this).val() == "B (5000)"
										&& $(this).attr("id") == "approvedInSlab") {
									$("." + $(this).attr("id")).attr("style",
											"display:block;");
									$("." + $(this).attr("id") + " input")
											.attr("required", "required");
								} else {
									$("." + $(this).attr("id")).attr("style",
											"display:none;");
									$("." + $(this).attr("id") + " input")
											.removeAttr("required");
								}
							} */ else if ($(this).attr("id") == "consentForMarketingCollateralsOrPhotoAndProfileRelease") {
								if ($(this).val() == "No") {
									$("." + $(this).attr("id")).attr("style",
											"display:block;");
									$("." + $(this).attr("id") + " input")
											.attr("required", "required");
								} else {
									$("." + $(this).attr("id")).attr("style",
											"display:none;");
									$("." + $(this).attr("id") + " input")
											.removeAttr("required");
								}
							}
						});

		$("#submit")
				.click(
						function() {
							if ($("#areaOfSpecialisation").val() != "Any Other") {
								$("#otherAreaOfSpecialisation").val("");
							}
							/* if ($("#approvedInSlab").val() == "C (3000)") {
								$("#dateOfECMeetingApprovalTaken").val("");
							} */
							if ($(
									"#consentForMarketingCollateralsOrPhotoAndProfileRelease")
									.val() != "No") {
								$(
										"#consentForMarketingCollateralsOrPhotoAndProfileReleaseReason")
										.val("");
							}

							return true;

						});
	</script>

	<script>
	
		
		var selectedProgGroups = "${selectedProgGroups}";
		if (selectedProgGroups.length != 0) {
			var types = selectedProgGroups.split(",");
			for (var i = 0; i < types.length; i++) {
				$('#programGroup option[value="' + types[i] + '"]').prop(
						"selected", true);
			}
		}

		var selectedProgNames = "${selectedProgNames}";
		if (selectedProgNames.length != 0) {
			var types = selectedProgNames.split(",");
			for (var i = 0; i < types.length; i++) {
				$('#programName option[value="' + types[i] + '"]').prop(
						"selected", true);
			}
		}

		$("#programGroup")
				.change(
						function() {
							$("#programName option").remove();
							if ($(this).val() != "") {
								$
										.ajax({
											url : '/acads/admin/getProgramNames/'
													+ $(this).val(),
											type : 'GET',
											success : function(data) {
												console.log("data", data);
												var formOptions = "<option value=''>Select Program Name</option>";
												for (var i = 0; i < data.length; i++) {
													formOptions += "<option value='"+data[i]+"'>"
															+ data[i]
															+ "</option>"
												}
												console.log(formOptions);
												$("#programName").append(
														formOptions);
											},
											error : function(error) {
												alert(error.responseText);
												console.log("Refresh");
											}
										});
							}

						});
	</script>

</body>
</html>