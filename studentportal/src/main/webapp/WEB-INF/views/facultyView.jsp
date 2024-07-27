<!DOCTYPE html>
<%@page import="com.nmims.helpers.*"%>
<%@page import="java.net.URLEncoder"%>
<html lang="en">
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
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<jsp:include page="common/jscss.jsp">
	<jsp:param value="Faculty Profile" name="title" />
</jsp:include>
<body>

	<%@ include file="common/header.jsp"%>
	<div class="sz-main-content-wrapper">
		<%@ include file="common/breadcrum.jsp"%>
		<div class="sz-main-content menu-closed">
			<div class="sz-main-content-inner">
				<%@ include file="common/left-sidebar.jsp"%>
				<div class="sz-content-wrapper examsPage">
					<%@ include file="common/studentInfoBar.jsp"%>
					<div class="sz-content">
						<h2 class="red text-capitalize">Faculty Profile</h2>
						<div class="clearfix"></div>
						<div class="panel-content-wrapper">
							<%@ include file="common/messages.jsp"%>
							<form:form id="faculty-profile" method="get"
								modelAttribute="faculty">
								<div class="row">
									<div class="col-md-8">

										<!--Accordion wrapper-->
										<div class="panel-group" id="accordion" role="tablist"
											aria-multiselectable="true">
											<!-- Accordion card -->
											<div class="panel panel-default">
												<!-- Card header -->

												<div class="panel-heading"
													style="border-left: 5px solid red;">
													<a class="collapsed" data-toggle="collapse"
														data-parent="#accordion" aria-expanded="false" href="#101">
														<h4 class="panel-title">

															Personal <i class="fa fa-arrow-down" aria-hidden="true"
																style="float: right"></i>

														</h4>
													</a>
												</div>

												<!-- Card body -->
												<div id="101" class="panel-collapse collapse out">
													<div class="form-group panel-body">
														<div class="col-md-4">
															<form:label for="title" path="title">Title : </form:label>
														</div>
														<div class="col-md-8">
															<form:label id="title" path="title">${faculty.title}</form:label>
														</div>
													</div>
													<div class="form-group panel-body">
														<div class="col-md-4">
															<form:label for="firstName" path="firstName">Name : </form:label>
														</div>
														<div class="col-md-8">
															<form:label id="firstName" path="firstName">${faculty.firstName} ${faculty.lastName}</form:label>
														</div>
													</div>

													<div class="form-group panel-body">
														<div class="col-md-4">
															<form:label for="dob" path="dob">Date of Birth : </form:label>
														</div>
														<div class="col-md-8">
															<form:label path="dob">${faculty.dob}</form:label>
														</div>
													</div>
													<div class="form-group panel-body">
														<div class="col-md-4">
															<form:label for="email" path="email">Email : </form:label>
														</div>
														<div class="col-md-8">
															<form:label id="email" path="email">${faculty.email}</form:label>
														</div>
													</div>
													<div class="form-group panel-body">
														<div class="col-md-4">
															<form:label for="secondaryEmail" path="secondaryEmail">Secondary Email : </form:label>
														</div>
														<div class="col-md-8">
															<form:label id="secondaryEmail" path="secondaryEmail">${faculty.secondaryEmail}</form:label>
														</div>
													</div>
<!-- 													<div class="form-group panel-body"> -->
<!-- 														<div class="col-md-4"> -->
<%-- 															<form:label for="mobile" path="mobile">Mobile No. : </form:label> --%>
<!-- 														</div> -->
<!-- 														<div class="col-md-8"> -->
<%-- 															<form:label id="mobile" path="mobile">${faculty.mobile}</form:label> --%>
<!-- 														</div> -->
<!-- 													</div> -->
													<div class="form-group panel-body">
														<div class="col-md-4">
															<form:label for="altContact" path="altContact">Alternate Contact No. : </form:label>
														</div>
														<div class="col-md-8">
															<form:label id="altContact" path="altContact">${faculty.altContact}</form:label>
														</div>
													</div>
													<div class="form-group panel-body">
														<div class="col-md-4">
															<form:label for="officeContact" path="officeContact">Office Contact No. : </form:label>
														</div>
														<div class="col-md-8">
															<form:label id="officeContact" path="officeContact">${faculty.officeContact}</form:label>
														</div>
													</div>
													<div class="form-group panel-body">
														<div class="col-md-4">
															<form:label for="homeContact" path="homeContact">Home Contact No. : </form:label>
														</div>
														<div class="col-md-8">
															<form:label id="homeContact" path="homeContact">${faculty.homeContact}</form:label>
														</div>
													</div>
													<div class="form-group panel-body">
														<div class="col-md-4">
															<form:label for="location" path="location">Location : </form:label>
														</div>
														<div class="col-md-8">
															<form:label id="location" path="location">${faculty.location}</form:label>
														</div>
													</div>
													<div class="form-group panel-body">
														<div class="col-md-4">
															<form:label for="address" path="address">Address : </form:label>
														</div>
														<div class="col-md-8">
															<form:label id="address" path="address">${faculty.address}</form:label>
														</div>
													</div>

												</div>
											</div>
											<!-- Accordion card -->
											

											<div class="panel panel-default">
												<!-- Card header -->

												<div class="panel-heading"
													style="border-left: 5px solid red;">
													<a class="collapsed" data-toggle="collapse"
														data-parent="#accordion" aria-expanded="false" href="#102">
														<h4 class="panel-title">

															Education <i class="fa fa-arrow-down" aria-hidden="true"
																style="float: right"></i>

														</h4>
													</a>
												</div>
												<!-- Card body -->
												<div id="102" class="panel-collapse collapse out">

													<c:choose>
														<c:when test="${not empty faculty.phdDetails}">
															<div class="form-group panel-body">
																<div class="col-md-4">
																	<form:label for="phdDetails" path="phdDetails">Educational Qualification : </form:label>
																</div>
																<div class="col-md-8">
																	<form:label id="phdDetails" path="phdDetails">${faculty.phdDetails}</form:label>
																</div>
															</div>
															<div class="form-group panel-body">
																<div class="col-md-4">
																	<form:label for="yearOfPassingPhd"
																		path="yearOfPassingPhd">Year of Passing : </form:label>
																</div>
																<div class="col-md-8">
																	<form:label id="yearOfPassingPhd"
																		path="yearOfPassingPhd">${faculty.yearOfPassingPhd}</form:label>
																</div>
															</div>
														</c:when>
														<c:otherwise>
															<div class="form-group panel-body">
																<div class="col-md-4">
																	<form:label for="graduationDetails"
																		path="graduationDetails">Educational Qualification : </form:label>
																</div>
																<div class="col-md-8">
																	<form:label id="graduationDetails"
																		path="graduationDetails">${faculty.graduationDetails}</form:label>
																</div>
															</div>
															<div class="form-group panel-body">
																<div class="col-md-4">
																	<form:label for="yearOfPassingGraduation"
																		path="yearOfPassingGraduation">Year of Passing : </form:label>
																</div>
																<div class="col-md-8">

																	<form:label id="yearOfPassingGraduation"
																		path="yearOfPassingGraduation">${faculty.yearOfPassingGraduation}</form:label>
																</div>
															</div>
														</c:otherwise>
													</c:choose>


													<c:choose>
														<c:when test="${not empty faculty.phdDetails}">
															<div class="form-group panel-body">
																<div class="col-md-4">
																	<form:label for="phdDetails" path="phdDetails">Highest Qualification : </form:label>
																</div>
																<div class="col-md-8">
																	<form:label id="phdDetails" path="phdDetails">Ph.D.</form:label>
																</div>
															</div>
														</c:when>
														<c:otherwise>
															<div class="form-group panel-body">
																<div class="col-md-4">
																	<form:label for="graduationDetails"
																		path="graduationDetails">Highest Qualification : </form:label>
																</div>
																<div class="col-md-8">
																	<form:label id="graduationDetails"
																		path="graduationDetails">${faculty.graduationDetails}</form:label>
																</div>
															</div>
														</c:otherwise>
													</c:choose>
													<div class="form-group panel-body">
														<div class="col-md-4">
															<form:label for="areaOfSpecialisation"
																path="areaOfSpecialisation">Area of Specialisation : </form:label>
														</div>
														<div class="col-md-8">
															<form:label path="areaOfSpecialisation"
																id="areaOfSpecialisation">${faculty.areaOfSpecialisation}</form:label>
														</div>
													</div>
													<div class="form-group panel-body">
														<div class="col-md-4">
															<form:label for="otherAreaOfSpecialisation"
																path="otherAreaOfSpecialisation">Other Area of Specialisation : </form:label>
														</div>
														<div class="col-md-8">
															<form:label path="otherAreaOfSpecialisation"
																id="otherAreaOfSpecialisation">${faculty.otherAreaOfSpecialisation}</form:label>
														</div>
													</div>
													<div class="form-group panel-body">
														<div class="col-md-4">
															<form:label for="natureOfAppointment"
																path="natureOfAppointment">Nature of Appointment : </form:label>
														</div>
														<div class="col-md-8">
															<form:label id="natureOfAppointment"
																path="natureOfAppointment">${faculty.natureOfAppointment}</form:label>
														</div>
													</div>

												</div>
											</div>
											
											<!-- Accordion card -->

											<div class="panel panel-default">
												<!-- Card header -->

												<div class="panel-heading"
													style="border-left: 5px solid red;">
													<a class="collapsed" data-toggle="collapse"
														data-parent="#accordion" aria-expanded="false" href="#103">
														<h4 class="panel-title">

															Academic/Professional Experience <i
																class="fa fa-arrow-down" aria-hidden="true"
																style="float: right"></i>

														</h4>
													</a>
												</div>

												<!-- Card body -->
												<div id="103" class="panel-collapse collapse out">
													<div class="form-group panel-body">
														<div class="col-md-4">
															<form:label for="programGroup" path="programGroup">Program Group : </form:label>
														</div>
														<div class="col-md-8">
															<form:label id="programGroup" path="programGroup">${faculty.programGroup}</form:label>
														</div>
													</div>

													<div class="form-group panel-body">
														<div class="col-md-4">
															<form:label for="programName" path="programName">Program Name : </form:label>
														</div>
														<div class="col-md-8">
															<form:label id="programName" path="programName">${faculty.programName}</form:label>
														</div>
													</div>

													<div class="form-group panel-body">
														<div class="col-md-4">
															<form:label for="subjectPref1" path="subjectPref1">Subject Preferences : </form:label>
														</div>
														<div class="col-md-8">
															<c:if test="${not empty faculty.subjectPref1}">
																<div class="col-md-3">
																	<form:label id="subjectPref1" path="subjectPref1">
																	1) ${faculty.subjectPref1}
																</form:label>
																</div>
															</c:if>
															<c:if test="${not empty faculty.subjectPref2}">
																<div class="col-md-3">
																	<form:label id="subjectPref2" path="subjectPref2">
																	2) ${faculty.subjectPref2}
																</form:label>
																</div>
															</c:if>
															<c:if test="${not empty faculty.subjectPref3}">
																<div class="col-md-3">
																	<form:label id="subjectPref3" path="subjectPref3">
																	3) ${faculty.subjectPref3}
																</form:label>
																</div>
															</c:if>
														</div>
													</div>



													<div class="form-group panel-body">
														<div class="col-md-4">
															<form:label for="teachingExp" path="teachingExp">Total years of Teaching Experience : </form:label>
														</div>
														<div class="col-md-8">
															<form:label id="teachingExp" path="teachingExp">${faculty.teachingExp}</form:label>
														</div>
													</div>

													<div class="form-group panel-body">
														<div class="col-md-4">
															<form:label for="ngasceExp" path="ngasceExp">Total years of NGASCE Experience : </form:label>
														</div>
														<div class="col-md-8">
															<form:label id="ngasceExp" path="ngasceExp">${faculty.ngasceExp}</form:label>
														</div>
													</div>

													<div class="form-group panel-body">
														<div class="col-md-4">
															<form:label for="corporateExp" path="corporateExp">Total years of Corporate Experience : </form:label>
														</div>
														<div class="col-md-8">
															<form:label id="corporateExp" path="corporateExp">${faculty.corporateExp}</form:label>
														</div>
													</div>

													<div class="form-group panel-body">
														<div class="col-md-4">
															<form:label for="designation" path="designation">Current Designation : </form:label>
														</div>
														<div class="col-md-8">
															<form:label id="designation" path="designation">${faculty.designation}</form:label>
														</div>
													</div>

													<div class="form-group panel-body">
														<div class="col-md-4">
															<form:label for="currentOrganization"
																path="currentOrganization">Current Organization : </form:label>
														</div>
														<div class="col-md-8">
															<form:label id="currentOrganization"
																path="currentOrganization">${faculty.currentOrganization}</form:label>
														</div>
													</div>
												</div>
											</div>											
											<!-- Accordion card -->

											<div class="panel panel-default">
												<!-- Card header -->

												<div class="panel-heading"
													style="border-left: 5px solid red;">
													<a class="collapsed" data-toggle="collapse"
														data-parent="#accordion" aria-expanded="false" href="#104">
														<h4 class="panel-title">

															Additional Information <i class="fa fa-arrow-down"
																aria-hidden="true" style="float: right"></i>

														</h4>
													</a>
												</div>

												<!-- Card body -->
												<div id="104" class="panel-collapse collapse out">

													<div class="form-group panel-body">
														<div class="col-md-4">
															<form:label for="aadharNumber" path="aadharNumber">Aadhar Number : </form:label>
														</div>
														<div class="col-md-8">
															<form:label id="aadharNumber" path="aadharNumber">${faculty.aadharNumber}</form:label>
														</div>
													</div>

													<div class="form-group panel-body">
														<div class="col-md-4">
															<form:label for="approvedInSlab" path="approvedInSlab">Approved in Slab : </form:label>
														</div>
														<div class="col-md-8">
															<form:label id="approvedInSlab" path="approvedInSlab">${faculty.approvedInSlab}</form:label>
														</div>
													</div>

													<div class="form-group panel-body">
														<div class="col-md-4">
															<form:label for="dateOfECMeetingApprovalTaken"
																path="dateOfECMeetingApprovalTaken">Date of EC Meeting Approval Taken : </form:label>
														</div>
														<div class="col-md-8">
															<form:label id="dateOfECMeetingApprovalTaken"
																path="dateOfECMeetingApprovalTaken">${faculty.dateOfECMeetingApprovalTaken}</form:label>
														</div>
													</div>
													<div class="form-group panel-body">
														<div class="col-md-4">
															<form:label
																for="consentForMarketingCollateralsOrPhotoAndProfileRelease"
																path="consentForMarketingCollateralsOrPhotoAndProfileRelease">Consent for Marketing Collaterals / Photo & Profile Release : </form:label>
														</div>
														<div class="col-md-8">
															<form:label
																id="consentForMarketingCollateralsOrPhotoAndProfileRelease"
																path="consentForMarketingCollateralsOrPhotoAndProfileRelease">${faculty.consentForMarketingCollateralsOrPhotoAndProfileRelease}</form:label>
														</div>
													</div>

													<div class="form-group panel-body">
														<div class="col-md-4">
															<form:label
																for="consentForMarketingCollateralsOrPhotoAndProfileReleaseReason"
																path="consentForMarketingCollateralsOrPhotoAndProfileReleaseReason">Consent for Marketing Collaterals / Photo & Profile Release Reason : </form:label>
														</div>
														<div class="col-md-8">
															<form:label
																id="consentForMarketingCollateralsOrPhotoAndProfileReleaseReason"
																path="consentForMarketingCollateralsOrPhotoAndProfileReleaseReason">${faculty.consentForMarketingCollateralsOrPhotoAndProfileReleaseReason}</form:label>
														</div>
													</div>

													<div class="form-group panel-body">
														<div class="col-md-4">
															<form:label for="honorsAndAwards" path="honorsAndAwards">Honors and Awards : </form:label>
														</div>
														<div class="col-md-8">
															<form:label id="honorsAndAwards" path="honorsAndAwards">${faculty.honorsAndAwards}</form:label>
														</div>
													</div>
													<div class="form-group panel-body">
														<div class="col-md-4">
															<form:label for="memberships" path="memberships">Memberships : </form:label>
														</div>
														<div class="col-md-8">
															<form:label id="memberships" path="memberships">${faculty.memberships}</form:label>
														</div>
													</div>
													<div class="form-group panel-body">
														<div class="col-md-4">
															<form:label for="researchInterest"
																path="researchInterest">Research Interest : </form:label>
														</div>
														<div class="col-md-8">
															<form:label id="researchInterest" path="researchInterest">${faculty.researchInterest}</form:label>
														</div>
													</div>
													<div class="form-group panel-body">
														<div class="col-md-4">
															<form:label
																for="articlesPublishedInInternationalJournals"
																path="articlesPublishedInInternationalJournals">Articles Published in International Journals : </form:label>
														</div>
														<div class="col-md-8">
															<form:label id="articlesPublishedInInternationalJournals"
																path="articlesPublishedInInternationalJournals">${faculty.articlesPublishedInInternationalJournals}</form:label>
														</div>
													</div>
													<div class="form-group panel-body">
														<div class="col-md-4">
															<form:label for="articlesPublishedInNationalJournals"
																path="articlesPublishedInNationalJournals">Articles Published in National Journals : </form:label>
														</div>
														<div class="col-md-8">
															<form:label id="articlesPublishedInNationalJournals"
																path="articlesPublishedInNationalJournals">${faculty.articlesPublishedInNationalJournals}</form:label>
														</div>
													</div>
													<div class="form-group panel-body">
														<div class="col-md-4">
															<form:label for="summaryOfPapersPublishedInABDCJournals"
																path="summaryOfPapersPublishedInABDCJournals">Summary of Papers Published in ABDC Journals : </form:label>
														</div>
														<div class="col-md-8">
															<form:label id="summaryOfPapersPublishedInABDCJournals"
																path="summaryOfPapersPublishedInABDCJournals">${faculty.summaryOfPapersPublishedInABDCJournals}</form:label>
														</div>
													</div>
													<div class="form-group panel-body">
														<div class="col-md-4">
															<form:label
																for="paperPresentationsAtInternationalConference"
																path="paperPresentationsAtInternationalConference">Paper Presentations at International Conference : </form:label>
														</div>
														<div class="col-md-8">
															<form:label
																id="paperPresentationsAtInternationalConference"
																path="paperPresentationsAtInternationalConference">${faculty.paperPresentationsAtInternationalConference}</form:label>
														</div>
													</div>

													<div class="form-group panel-body">
														<div class="col-md-4">
															<form:label for="paperPresentationAtNationalConference"
																path="paperPresentationAtNationalConference">Paper Presentations at National Conference : </form:label>
														</div>
														<div class="col-md-8">
															<form:label id="paperPresentationAtNationalConference"
																path="paperPresentationAtNationalConference">${faculty.paperPresentationAtNationalConference}</form:label>
														</div>
													</div>
													<div class="form-group panel-body">
														<div class="col-md-4">
															<form:label for="caseStudiesPublished"
																path="caseStudiesPublished">Case Studies Published : </form:label>
														</div>
														<div class="col-md-8">
															<form:label id="caseStudiesPublished"
																path="caseStudiesPublished">${faculty.caseStudiesPublished}</form:label>
														</div>
													</div>
													<div class="form-group panel-body">
														<div class="col-md-4">
															<form:label for="booksPublished" path="booksPublished">Books Published : </form:label>
														</div>
														<div class="col-md-8">
															<form:label id="booksPublished" path="booksPublished">${faculty.booksPublished}</form:label>
														</div>
													</div>
													<div class="form-group panel-body">
														<div class="col-md-4">
															<form:label for="bookChaptersPublished"
																path="bookChaptersPublished">Book Chapters Published : </form:label>
														</div>
														<div class="col-md-8">
															<form:label id="bookChaptersPublished"
																path="bookChaptersPublished">${faculty.bookChaptersPublished}</form:label>
														</div>
													</div>
													<div class="form-group panel-body">
														<div class="col-md-4">
															<form:label for="listOfPatents" path="listOfPatents">List of Patents : </form:label>
														</div>
														<div class="col-md-8">
															<form:label id="listOfPatents" path="listOfPatents">${faculty.listOfPatents}</form:label>
														</div>
													</div>
													<div class="form-group panel-body">
														<div class="col-md-4">
															<form:label for="consultingProjects"
																path="consultingProjects">Consulting Projects : </form:label>
														</div>
														<div class="col-md-8">
															<form:label id="consultingProjects"
																path="consultingProjects">${faculty.consultingProjects}</form:label>
														</div>
													</div>
													<div class="form-group panel-body">
														<div class="col-md-4">
															<form:label for="researchProjects"
																path="researchProjects">Research Projects (Research grants) : </form:label>
														</div>
														<div class="col-md-8">
															<form:label id="researchProjects" path="researchProjects">${faculty.researchProjects}</form:label>
														</div>
													</div>
													<div class="form-group panel-body">
														<div class="col-md-4">
															<form:label for="linkedInProfileUrl"
																path="linkedInProfileUrl">LinkedIn Profile : </form:label>
														</div>
														<div class="col-md-8">
															<form:label id="linkedInProfileUrl"
																path="linkedInProfileUrl">
																<c:if test='${not empty faculty.linkedInProfileUrl}'>
																	<a href="${faculty.linkedInProfileUrl}" target="_blank">${faculty.linkedInProfileUrl}</a>
																</c:if>
															</form:label>
														</div>
													</div>
												</div>
											</div>
										</div>
									</div>

								</div>
								<!-- Accordion card -->
								
								<div class="form-group">
									<button type="button" class="btn btn-danger"
										onclick="window.close();">Cancel</button>
								</div>

							</form:form>
						</div>

					</div>
				</div>
			</div>
		</div>
	</div>

	<jsp:include page="common/footer.jsp" />


</body>
<script>
	$(document).ready(function() {
		
		$(".panel-body .col-md-8 label").each(function() {						
			if ($(this).html().trim() == "") {
				$(this).parents(".panel-body").remove();
			}					
		});
		$(".panel-body .col-md-8").each(function() {
			if ($(this).html().trim() == "") {
				$(this).parents(".panel-body").remove();
			}
		});
		$(".panel-collapse").each(function(){
			if($(this).html().trim()==""){
				$(this).parent().remove();
			}
			
		});		
		

	});
</script>
</html>
