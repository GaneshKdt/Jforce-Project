<!DOCTYPE html>
<%@page import="java.util.*"%>
<%@page import="java.text.DateFormat"%>
<html lang="en">
<% 
String roles = "";
UserAuthorizationExamBean userAuthorization = (UserAuthorizationExamBean)session.getAttribute("userAuthorization");
if(userAuthorization != null){
	roles = (userAuthorization.getRoles() != null && !"".equals(userAuthorization.getRoles())) ? userAuthorization.getRoles() : roles;
}
 %>

<%@page import="com.nmims.beans.Page"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

    <jsp:include page="adminCommon/jscss.jsp">
	<jsp:param value="Exam Results Processing Checklist" name="title"/>
    </jsp:include>
    <body>
    	<%@ include file="adminCommon/header.jsp" %>
        <div class="sz-main-content-wrapper">
        
        	<jsp:include page="adminCommon/breadcrum.jsp">
			<jsp:param value="Exam Results Processing ; Checklist" name="breadcrumItems"/>
			</jsp:include>
        	
            
            <div class="sz-main-content menu-closed">
                <div class="sz-main-content-inner">
          				<jsp:include page="adminCommon/left-sidebar.jsp">
						<jsp:param value="" name="activeMenu"/>
						</jsp:include>
					<div class="sz-content-wrapper examsPage">
							
						<div class="sz-content">
							<h2 class="red text-capitalize">Exam Results Processing Checklist</h2>
							<div class="clearfix"></div>
							<div class="panel-content-wrapper" style="min-height:450px;">
								<%@ include file="adminCommon/messages.jsp" %>
								<div class="col-md-16 column">
									<legend>&nbsp;Check List<font size="2px">  </font></legend>
									<div class="table-responsive">
										<table class="table table-striped" style="font-size:12px">
											<thead>
												<!-- <tr>
													<th><a href="/exam/readSifyDataForm" target="_blank">Pull Sify Results</a> / <a href="/exam/viewSifyMarks" target="_blank">View Sify Results</a> </th>
													<td>Click Link To Retrive Sify Results./ View Sify Results.</td>
												</tr>
												<tr>
													<th ><a href="/exam/transferSifyResultsToOnlineMarksForm" target="_blank">Transfer Sify Results to Marks Table</a></th>
													<td>Click Link to Transfer Sify Results to Marks Table</td>
												</tr> -->

												<tr>
													<th><a href="/exam/admin/teeResultsDashboard" target="_blank">Generate Base Records for TEE Checklist</a>  </th>
													<td>Click to generate TEE results Base Records for TEE checklist</td>
												</tr>
												
												<tr>
													<th><a href="/exam/admin/applyBodForm" target="_blank">Upload Bod Question Ids / Apply Bod</a></th>
													<td>Click Link to Upload Bod Question Ids / Apply Bod</td>
												</tr>
												<tr>
													<th><a href="/exam/admin/pullMettlMarksForTeeExamsForm" target="_blank">Pull Mettl Results</a> / <a href="/exam/pullMarksDataFromTCSForm" target="_blank">Pull TCS Results</a> / <a href="viewTCSMarks" target="_blank">View TCS Results</a> / <a href="/exam/viewSifyMarks" target="_blank">View Sify Results</a> </th>
													<td>Click Link To Retrive Results./ View Download Scores.</td>
												</tr>
												<tr>
													<th><a href="/exam/admin/transferMettlResultsToOnlineMarksForm" target="_blank">Transfer Mettl Results To Marks Table</a> / <a href="/exam/transferTCSResultsToOnlineMarksForm" target="_blank">Transfer TCS Results To Marks Table</a> </th>
													<td>Click Link To Transfer Results To Marks Table.</td>
												</tr>
												<tr>
													<th ><a href="/exam/admin/uploadWrittenMarksForm" target="_blank">Upload Offline Marks</a></th>
													<td>Click Link to Upload Offline Marks</td>
												</tr>
												<tr>
													<th ><a href="/exam/admin/uploadWrittenMarksForm" target="_blank">Upload Project Marks</a>									
													</th>
													<td>Click Link to Upload Project Marks</td>
												</tr>
												<tr>
												<th><a href = "/exam/admin/uploadProjectNotBookedForm" target="_blank">Insert Project Not Booked Students</a></th>
												<td>Click Link to Insert Project Not Booked Students</td>
												</tr>
												<tr>
													<th ><a href="/exam/admin/insertABRecordsForm" target="_blank">Upload Online Exam Absent Students List</a></th>
													<td>Click Link to Upload Online Exam Absent Students List</td>
												</tr>
												<!-- <tr>
													<th > <a href="/exam/admin/markRIANVCasesForm" target="_blank"> Mark RIA/NV Cases</a> </th>
													<td>Click Link to Mark Student for RIA/NV Cases </td>
												</tr> -->
												<tr>
													<th ><a href="/exam/admin/uploadWrittenMarksForm" target="_blank">Upload Student Marks for RIA/NV Cases</a> | 
													<a href="/exam/admin/markedRIANVForUFMForm" target="_blank"> Mark UFM Cases RIA/NV </a> |
													<a href="/exam/admin/markRIANVCasesForm" target="_blank"> Mark RIA/NV Cases</a> </th>
													<td>Click Link to Upload Student Marks for RIA/NV Cases </td>
												</tr>
												<tr>
													<th ><a href="/exam/admin/passFailTriggerSearchForm" target="_blank">Pass Fail Trigger</a></th>
													<td>Click Link to Pass Fail Trigger, Add to Staging & Apply 2 marks Grace</td>
												</tr>
												<!-- <tr>
													<th ><a href="/exam/admin/getGraceEligibleForm" target="_blank">Generate Grace (2-marks) Report</a></th>
													<td>Click Link to Generate Grace (2-marks) Report</td>
												</tr> -->
												<tr>
													<th ><a href="/exam/admin/transferPassFailForm" target="_blank">Transfer Staging to Pass Fail</a></th>
													<td>Transfer from Staging to Passfail &  Apply validity end grace</td>
												</tr>
												<tr>
													<th ><a href="/exam/admin/searchPassFailForm" target="_blank">Apply Grace (2-marks) to Individual Students</a></th>
													<td>Click Link to Apply Grace (2-marks) to Individual Students</td>
												</tr>
												<!-- <tr>
													<th ><a href="/exam/admin/graceToCompleteProgramReportForm" target="_blank">Generate Grace (10-marks) Report</a></th>
													<td>Click Link to Generate Grace (10-marks) Report</td>
												</tr> -->
												<tr>
													<th ><a href="/exam/admin/searchPassFailForm" target="_blank">Apply Grace (10-marks) to Individual Students</a></th>
													<td>Click Link to Apply Grace (10-marks) to Individual Students</td>
												</tr>
												<tr>
													<th ><a href="/exam/admin/makeResultsLiveForm" target="_blank">Make Results Live</a></th>
													<td>Click Link to Make Results Live</td>
												</tr>
												<tr>
													<th ><a href="/studentportal/loginAsForm" target="_blank">Login As Student</a></th>
													<td>Click Link to Login As Student to Verify Results</td>
												</tr>

												<tr>
													<th><a href="/exam/admin/getMettlStudentEvaluationReportForm" target = "_blank">View Mettl Evaluator Report</a></th>
													<td>View Mettl Evaluation report</td>
												</tr>
											</thead>							
										</table>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>    
        <jsp:include page="adminCommon/footer.jsp"/>
	</body>
</html> 