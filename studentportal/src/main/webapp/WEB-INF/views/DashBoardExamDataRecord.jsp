<!DOCTYPE html>
<html lang="en">
<%@page import="com.nmims.helpers.PersonStudentPortalBean"%>
<%@page import="java.net.URLEncoder"%>
<%@page import="com.nmims.helpers.AESencrp"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>


<jsp:include page="adminCommon/jscss.jsp">
	<jsp:param value="Welcome to Student Zone" name="title" />
</jsp:include>
<style>
.jumbotron {
	background-color: #fff;
	padding: 0.5px;
}
</style>
<spring:eval expression="@propertyConfigurer.getProperty('SERVER_PATH')"
	var="server_path" />

<body>

	<%@ include file="adminCommon/header.jsp"%>


	<%
			try{
         String encryptedSapId = URLEncoder.encode(AESencrp.encrypt((String)session.getAttribute("userId"))); 
    String examAppSSOUrl = (String)pageContext.getAttribute("server_path") + "exam/loginforSSO?uid="+encryptedSapId;
    String acadsAppSSOUrl = (String)pageContext.getAttribute("server_path") + "acads/loginforSSO?uid="+encryptedSapId;
    
    %>



	<div class="sz-main-content-wrapper">

		<jsp:include page="adminCommon/breadcrum.jsp">
			<jsp:param value="Student Zone;Home" name="breadcrumItems" />
		</jsp:include>


		<div class="sz-main-content menu-closed">
			<div class="sz-main-content-inner">
				<jsp:include page="adminCommon/left-sidebar.jsp">
					<jsp:param value="" name="activeMenu" />
				</jsp:include>


				<div class="sz-content-wrapper examsPage">
					<%@ include file="adminCommon/adminInfoBar.jsp"%>


					<div class="sz-content">
						<div style="color: black; font-size: 32px; padding: 20px 0px">
							ExamData Panel</div>
						<div>
							<form>
								Year : <select name="year">
									<c:forEach var="year" items="${ years }">
										<c:choose>
											<c:when test="${year == selectedYear}">
												<option selected value="${ year }">${ year }</option>
											</c:when>
											<c:otherwise>
												<option value="${ year }">${ year }</option>
											</c:otherwise>
										</c:choose>
									</c:forEach>
								</select> | Month : <select name="month">
									<c:forEach var="month" items="${ months }">
										<c:choose>
											<c:when test="${month == selectedMonth}">
												<option selected value="${ month }">${ month }</option>
											</c:when>
											<c:otherwise>
												<option value="${ month }">${ month }</option>
											</c:otherwise>
										</c:choose>
									</c:forEach>
								</select>
								<button class="btn btn-danger" type="submit">Submit</button>
							</form>
						</div>


						<a href="javascript:void(0)" class="col-sm-3 WidgeBlock B-green">
							<center>
								<h4 style="font-size: 18px !important;">Exam Offline Booked
									Count</h4>
								<hr />
								<div id="ExamOfflineBookedCount"
									style="font-size: 35px; font-weight: bold">${ ExamOfflineBookedCount }</div>
							</center>
							<div style="float: left">
								<div>Year</div>
								<div>${ selectedYear }</div>
							</div>
							<div style="float: right">
								<div>Month</div>
								<div>${ selectedMonth }</div>
							</div>
						</a> <a href="javascript:void(0)" class="col-sm-3 WidgeBlock B-red">
							<center>
								<h4 style="font-size: 18px !important;">Exam Online Booked
									Count</h4>
								<hr />
								<div id="ExamOnlineBookedCount"
									style="font-size: 35px; font-weight: bold">${ ExamOnlineBookedCount }</div>
							</center>
							<div style="float: left">
								<div>Year</div>
								<div>${ selectedYear }</div>
							</div>
							<div style="float: right">
								<div>Month</div>
								<div>${ selectedMonth }</div>
							</div>
						</a> <a href="javascript:void(0)" class="col-sm-3 WidgeBlock B-yellow">
							<center>
								<h4 style="font-size: 18px !important;">Exam Offline
									Release Seat Count</h4>
								<hr />
								<div id="ExamOfflineReleaseSeatCount"
									style="font-size: 35px; font-weight: bold">${ ExamOfflineReleaseSeatCount }</div>
							</center>
							<div style="float: left">
								<div>Year</div>
								<div>${ selectedYear }</div>
							</div>
							<div style="float: right">
								<div>Month</div>
								<div>${ selectedMonth }</div>
							</div>
						</a> <a href="javascript:void(0)" class="col-sm-3 WidgeBlock B-blue">
							<center>
								<h4 style="font-size: 18px !important;">Exam Online Release
									Seat Count</h4>
								<hr />
								<div id="ExamOnlineReleaseSeatCount"
									style="font-size: 35px; font-weight: bold">${ ExamOnlineReleaseSeatCount }</div>
							</center>
							<div style="float: left">
								<div>Year</div>
								<div>${ selectedYear }</div>
							</div>
							<div style="float: right">
								<div>Month</div>
								<div>${ selectedMonth }</div>
							</div>
						</a> <a href="javascript:void(0)" class="col-sm-3 WidgeBlock B-orange">
							<center>
								<h4 style="font-size: 18px !important;">Twice Exam Booking
									Count</h4>
								<hr />
								<div id="TwiceExamBooking"
									style="font-size: 35px; font-weight: bold">${ TwiceExamBooking }</div>
							</center>
							<div style="float: left">
								<div>Year</div>
								<div>${ selectedYear }</div>
							</div>
							<div style="float: right">
								<div>Month</div>
								<div>${ selectedMonth }</div>
							</div>
						</a>

					</div>
				</div>


			</div>
		</div>

		<div id="examApp"></div>
		<div id="acadsApp"></div>

		<jsp:include page="adminCommon/footer.jsp" />
		<script>
			$( "#examApp" ).load( "<%=examAppSSOUrl%>" );
			$( "#acadsApp" ).load( "<%=acadsAppSSOUrl%>" );
		</script>
		<% } 
         	catch(Exception e){
         		System.out.println(e.getMessage());
         	}
         %>
	
</body>
</html>
