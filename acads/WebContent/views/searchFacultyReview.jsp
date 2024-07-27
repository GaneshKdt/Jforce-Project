<%-- <!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!-->

<%@page import="com.nmims.beans.Person"%>
<%@page import="com.nmims.beans.Page"%>

<html class="no-js">
<!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<jsp:include page="jscss.jsp">
	<jsp:param value="Search Faculty" name="title" />
</jsp:include>

<body class="inside">

	<%@ include file="header.jsp"%>

	<section class="content-container login">
		<div class="container-fluid customTheme">
			<div class="row">
				<legend>Search Faculty Review</legend>
			</div>
			<%@ include file="messages.jsp"%>

			<form:form action="searchFacultyReview" method="post"
				modelAttribute="reviewBean">
				<fieldset>
					<div class="panel-body">

						<div class="col-md-6 column">


							<div class="form-group">
								<form:input id="reviewerFacultyId" path="reviewerFacultyId"
									type="text" placeholder="Reviewer Faculty Id"
									class="form-control" />
							</div>

							<div class="form-group">
								<form:input id="reviewerName" path="reviewerName"
									type="text" placeholder="Reviewer Faculty Name"
									class="form-control" />
							</div>
							
							<div class="form-group">
								<form:select id="reviewed" path="reviewed" class="form-control">
									<form:option value="">Select Review Status </form:option>
									<form:option value="Reviewed">Reviewed</form:option>
									<form:option value="Not Reviewed">Not Reviewed</form:option>
								</form:select>
							</div>



							<div class="form-group">
								<label class="control-label" for="submit"></label>
								<button id="submit" name="submit"
									class="btn btn-large btn-primary"
									formaction="searchFacultyReview">Search</button>
								<button id="cancel" name="cancel" class="btn btn-danger"
									formaction="acadsHome" formnovalidate="formnovalidate">Cancel</button>
							</div>
						</div>
					</div>
				</fieldset>
			</form:form>

			<c:choose>
				<c:when test="${rowCount > 0}">

					<legend>
						&nbsp;Session Review Details<font size="2px"> (${rowCount}
							Records Found) &nbsp; <a href="downloadSessionFacultyReviews">Download
								To Excel</a>
						</font>
					</legend>
					<div class="table-responsive">
						<table class="table table-striped" style="font-size: 12px">
							<thead>
								<tr>
									<th>Sr. No.</th>
									<th>Session ID</th>
									<th>Q1 Response(Adhering to the Session plan (Yes/No))</th>
									<th>Q2 Response(Addressing Student queries (Yes/No))</th>
									<th>Q3 Response(Aligning Case study with course content
										(Yes/No))</th>
									<th>Q4 Response(Lecture delivery (Poor/Needs
										Improvement/Good/Excellent))</th>
									<th>Q5 Response(Communicaton- Language (On a scale of
										1-7))</th>
									<th>Q6 Response(Communicaton- Clarity (On a scale of 1-7))</th>
									<th>Q1 Remarks</th>
									<th>Q2 Remarks</th>
									<th>Q3 Remarks</th>
									<th>Q4 Remarks</th>
									<th>Q5 Remarks</th>
									<th>Q6 Remarks</th>
								</tr>
							</thead>
							<tbody>

								<c:forEach var="reviewBeam" items="${reviewListBasedOnCriteria}"
									varStatus="status">
									<tr>
										<td><c:out value="${status.count}" /></td>
										<td><c:out value="${reviewBeam.sessionId}" /></td>

										<td><c:out value="${reviewBeam.q1Response}" /></td>
										<td><c:out value="${reviewBeam.q2Response}" /></td>
										<td><c:out value="${reviewBeam.q3Response}" /></td>
										<td><c:out value="${reviewBeam.q4Response}" /></td>
										<td><c:out value="${reviewBeam.q5Response}" /></td>
										<td><c:out value="${reviewBeam.q6Response}" /></td>

										<td><c:out value="${reviewBeam.q1Remarks}" /></td>
										<td><c:out value="${reviewBeam.q2Remarks}" /></td>
										<td><c:out value="${reviewBeam.q3Remarks}" /></td>
										<td><c:out value="${reviewBeam.q4Remarks}" /></td>
										<td><c:out value="${reviewBeam.q5Remarks}" /></td>
										<td><c:out value="${reviewBeam.q6Remarks}" /></td>
									</tr>
								</c:forEach>


							</tbody>
						</table>
					</div>
					<br>

				</c:when>
			</c:choose>



		</div>


	</section>

	<jsp:include page="footer.jsp" />


</body>
</html> 

stef commented Sep-2017--%>




<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!-->

<%@page import="com.nmims.beans.PersonAcads"%>
<%@page import="com.nmims.beans.PageAcads"%>

<html class="no-js">
<!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<jsp:include page="jscss.jsp">
	<jsp:param value="Search Faculty" name="title" />
</jsp:include>

<body class="inside">

	<%@ include file="header.jsp"%>

	<section class="content-container login">
		<div class="container-fluid customTheme">
			<div class="row">
				<legend>Search Faculty Review</legend>
			</div>
			<%@ include file="messages.jsp"%>

			<form:form action="searchFacultyReview" method="post"
				modelAttribute="reviewBean">
				<fieldset>

					<div class="panel-body">

						<div class="column col-md-9 ">

							<div class="form-group" style="overflow: visible;">
								<form:select id="batch" path="month"
									itemValue="${reviewBean.month}">
									<form:option value="">Select Batch </form:option>
									<form:options items="${monthList}" />
								</form:select>
							</div>

							<div class="form-group" style="overflow: visible;">
								<form:select id="year" path="year"
									itemValue="${reviewBean.year}">
									<form:option value="">Select Academic Year</form:option>
									<form:options items="${yearList}" />
								</form:select>

							</div>

							<div class="form-group" style="overflow: visible;">
								<form:select id="subject" path="subject"
									itemValue="${reviewBean.subject}">
									<form:option value="">Select Subject</form:option>
									<form:options items="${subjectList}" />
								</form:select>
							</div>

							<div class="form-group">
								<form:input id="reviewerFirstName" path="firstName" type="text"
									placeholder="Reviewer Faculty First Name" class="form-control" />
							</div>

							<div class="form-group">
								<form:input id="reviewerLastName" path="lastName" type="text"
									placeholder="Reviewer Faculty Last Name" class="form-control" />
							</div>
							
							<div class="form-group">
								<form:select id="reviewed" path="reviewed" class="form-control">
									<form:option value="">Select Review Status </form:option>
									<form:option value="Reviewed">Reviewed</form:option>
									<form:option value="Not Reviewed">Not Reviewed</form:option>
								</form:select>
							</div>
							
							<div class="form-group">
								<label class="control-label" for="submit"></label>


								<button id="submit" name="submit"
									class="btn btn-large btn-primary"
									formaction="searchFacultyReview">Search</button>

								<button id="submit" name="submit"
									class="btn btn-large btn-primary"
									formaction="downloadSessionFacultyReviews">Download</button>

								<button id="cancel" name="cancel" class="btn btn-danger"
									formaction="home" formnovalidate="formnovalidate">Cancel</button>
							</div>



						</div>

						<div class="column col-md-9 ">
						
						
						
							<div class="form-group">
								Q1 Response(Adhering to the Session plan (Yes/No)):<br>
								
								<div style="float: left">
									<form:checkbox path="q1Response" id="q1Response" value="Yes"
										style="width:15px;height:15px" />
								Yes
								</div>
								<div style="float: left">
									<form:checkbox path="q1Response" id="q1Response" value="No"
										style="width:25px;height:15px" />
									No
								</div>

							</div>



							<div class="form-group">
								Q2 Response(Addressing Student queries (Yes/No)):<br>

								<div style="float: left">
									<form:checkbox path="q2Response" id="q2Response" value="Yes"
										style="width:15px;height:15px" />
									Yes
								</div>
								<div style="float: left">
									<form:checkbox path="q2Response" id="q2Response" value="No"
										style="width:25px;height:15px" />
									No
								</div>

							</div>
						
						<div class="form-group">

								Q3 Response(Aligning Case study with course content (Yes/No):<br>

								<div style="float: left">
									<form:checkbox path="q3Response" id="q3Response" value="Yes"
										style="width:15px;height:15px" />
									Yes
								</div>
								<div style="float: left">
									<form:checkbox path="q3Response" id="q3Response" value="No"
										style="width:25px;height:15px" />
									No
								</div>

							</div>


							<div class="form-group">

								Q4 Response(Lecture delivery (Poor/Needs
								Improvement/Good/Excellent)):<br>

								<div style="float: left">
								<form:checkbox path="q4Response" name="q4Response" value="Poor"
									style="width:15px;height:15px" />
								Poor </div>
								<div style="float: left">
								<form:checkbox path="q4Response" name="q4Response"
									value="Need Improvement " style="width:25px;height:15px" />
								Need Improvement </div>
								<div style="float: left">
								<form:checkbox path="q4Response" name="q4Response" value="Good "
									style="width:25px;height:15px" />
								Good </div>
								<div style="float: left">
								<form:checkbox path="q4Response" name="q4Response"
									value="Excellent" style="width:25px;height:15px" />
								Excellent</div>


							</div>

							<div class="form-group">

								Q5 Response(Communicaton- Language (On a scale of 1-7)):<br>
								
								<div style="float: left">
								<form:checkbox path="q5Response" name="q5Response" value="1"
									style="width:15px;height:15px" />
								1 </div>
								<div style="float: left">
								<form:checkbox path="q5Response" name="q5Response" value="2 "
									style="width:25px;height:15px" />
								2 </div>
								<div style="float: left">
								<form:checkbox path="q5Response" name="q5Response" value="3"
									style="width:25px;height:15px" />
								3 </div>
								<div style="float: left">
								<form:checkbox path="q5Response" name="q5Response" value="4"
									style="width:25px;height:15px" />
								4 </div>
								<div style="float: left">
								<form:checkbox path="q5Response" name="q5Response" value="5"
									style="width:25px;height:15px" />
								5 </div>
					<div style="float: left">
								<form:checkbox path="q5Response" name="q5Response" value="6"
									style="width:25px;height:15px" />
								6 </div>
								<div style="float: left">
								<form:checkbox path="q5Response" name="q5Response" value="7"
									style="width:25px;height:15px" />
								7 </div>

							</div>

							<div class="form-group">

								Q6 Response(Communicaton- Clarity (On a scale of 1-7)):<br>

								<div style="float: left">
								<form:checkbox path="q6Response" name="q6Response" value="1"
									style="width:15px;height:15px" />
								1 </div>
								<div style="float: left">
								<form:checkbox path="q6Response" name="q6Response" value="2 "
								style="width:25px;height:15px" />
								2 </div>
								<div style="float: left">
								<form:checkbox path="q6Response" name="q6Response" value="3"
									style="width:25px;height:15px"/>
								3 </div>
								<div style="float: left">
								<form:checkbox path="q6Response" name="q6Response" value="4"
								style="width:25px;height:15px"/>
								4 </div>
								<div style="float: left">
								<form:checkbox path="q6Response" name="q6Response" value="5"
									style="width:25px;height:15px"/>
								5 </div>
								<div style="float: left">
								<form:checkbox path="q6Response" name="q6Response" value="6"
									style="width:25px;height:15px" />
								6 </div>
								<div style="float: left">
								<form:checkbox path="q6Response" name="q6Response" value="7"
									style="width:25px;height:15px" />
								7 </div>

							</div>

						</div>

						<!-- <div class="column col-md-9">
							<div class="form-group">
								<label class="control-label" for="submit"></label>


								<button id="submit" name="submit"
									class="btn btn-large btn-primary"
									formaction="searchFacultyReview">Search</button>

								<button id="submit" name="submit"
									class="btn btn-large btn-primary"
									formaction="downloadSessionFacultyReviews">Download</button>

								<button id="cancel" name="cancel" class="btn btn-danger"
									formaction="home" formnovalidate="formnovalidate">Cancel</button>
							</div>
						</div>
					</div> -->
				</fieldset>
			</form:form>

			<c:if test="${rowCount > 0}">
				<div class="panel-body">


					<h2>
						&nbsp;Sessions Average Review <font size="2px">
							(${rowCount} Records Found) </font>
					</h2>
					<div class="table-responsive">
						<table class="table table-striped" style="font-size: 12px">
							<thead>
								<tr>
									<th>Q5 Response(Communicaton-Language(On a scale of 1-7))</th>
									<th>Q6 Response(Communicaton-Clarity(On a scale of 1-7))</th>

								</tr>
							</thead>
							<tbody>

								<tr>
									<td><b><fmt:formatNumber type="number"
												maxIntegerDigits="3" value="${q5Average}" /></b></td>
									<td><b><fmt:formatNumber type="number"
												maxIntegerDigits="3" value="${q6Average}" /></b></td>

								</tr>


							</tbody>
						</table>
					</div>

				</div>

			</c:if>





		</div>
	</section>

	<jsp:include page="footer.jsp" />


</body>
</html>
