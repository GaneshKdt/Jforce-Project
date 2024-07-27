<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>

<html lang="en">
<jsp:include page="../common/jscss.jsp">
	<jsp:param value="FeedBack" name="title" />
</jsp:include>
<body>
	<%@ include file="../common/header.jsp"%>
	<div class="sz-main-content-wrapper">
		<jsp:include page="../common/breadcrum.jsp">
			<jsp:param value="Student Zone;Student Support;FeedBack"
				name="breadcrumItems" />
		</jsp:include>


		<div class="sz-main-content menu-closed">
			<div class="sz-main-content-inner">
				<jsp:include page="../common/left-sidebar.jsp">
					<jsp:param value="FeedBack" name="activeMenu" />
				</jsp:include>


				<div class="sz-content-wrapper examsPage">

					<%@ include file="../common/studentInfoBar.jsp"%>

					<div class="sz-content">

						<h2 class="red text-capitalize">Feedback And Suggestions</h2>
						<div class="clearfix"></div>
						<div class="panel-content-wrapper">
							<%@ include file="../common/messages.jsp"%>
							<form role="form" id="feedbackId" action="saveFeedBack"
								method="post">
								<label for="categoryId">Category</label>
								<div class="form-group">
									<select id="categoryId" required="required" name="category"
										class="form-control">
										<option value="">None</option>
										<option value="Web-Site Navigation">Web-Site
											Navigation</option>
										<option value="Academics-Online sessions">Academics-Online
											sessions</option>
										<option value="Academics-Faculty ">Academics-Faculty</option>
										<option value="Academics-Course content">Academics-Course
											content</option>
										<option value="Admission-Registration and Document process">Admission-
											Registration and Document process</option>
										<option value="Examination- Facilitation">Examination-
											Facilitation</option>
										<option value="Examination- Results">Examination-
											Results</option>
										<option value="Logistics- Book dispatch">Logistics-
											Book dispatch</option>
										<option value="Student support">Student support</option>
									</select>
								</div>
								<label for="required">Rating</label>
								<div class="form-group">
									<select id="ratingId" required="required" name="rating"
										class="form-control">
										<option value="">None</option>
										<option value="1">1</option>
										<option value="2">2</option>
										<option value="3">3</option>
										<option value="4">4</option>
										<option value="5">5</option>
									</select>
								</div>
								<p style="color: red;">(Note:-Help us help you better!!! Use
									this form to send us your Feedback and Suggestions. Ideas which
									can help us improve your current Academic experience with us,
									rating '5' being the best)</p>
								<div class="form-group">
									<label for="description">Comments</label>
									<textarea name="comments" class="form-control"
										required="required" placeholder="Please Leave Your Comments"></textarea>
									<br>
								</div>

								<div class="controls">
									<button id="submit" name="submit"
										class="btn btn-large btn-primary">Submit</button>
									<button id="cancel" name="cancel" class="btn btn-danger"
										formaction="home" formnovalidate="formnovalidate">Cancel</button>
								</div>
							</form>
						</div>

					</div>
				</div>


			</div>
		</div>
	</div>


	<jsp:include page="../common/footer.jsp" />


</body>
</html>