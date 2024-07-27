
<!DOCTYPE html>


<html lang="en">

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<jsp:include page="../adminCommon/jscss.jsp">
	<jsp:param value="Important Documents" name="title" />
</jsp:include>

<body>
	<%@ include file="../adminCommon/header.jsp"%>
	<div class="sz-main-content-wrapper">

		<jsp:include page="../adminCommon/breadcrum.jsp">
			<jsp:param value="Exam;Webinar" name="breadcrumItems" />
		</jsp:include>

		<div class="sz-main-content menu-closed">
			<div class="sz-main-content-inner">
				<jsp:include page="../adminCommon/left-sidebar.jsp">
					<jsp:param value="" name="activeMenu" />
				</jsp:include>


				<div class="sz-content-wrapper examsPage">
					<%@ include file="../adminCommon/adminInfoBar.jsp"%>
					<div class="sz-content">
						<h2 class="red text-capitalize">Webinar</h2>
						<div class="clearfix"></div>
						<div class="panel-content-wrapper" style="min-height: 450px;">
							<%@ include file="../adminCommon/messages.jsp"%>

							<table class="table table-striped table-hover"
								style="font-size: 12px">
								<thead>
									<tr>

										<th>Topic</th>
										<th>Link</th>
									</tr>
								</thead>
								<tbody>
									<tr>
										<td>Data Analytics in 2020</td>
										<td><a href="https://www.youtube.com/watch?v=mlXbaDyopwM"
											target="_blank">Click Here</a></td>
									</tr>

									<tr>
										<td>Overview of the SAS Data Visualization tool</td>
										<td><a href="https://www.youtube.com/watch?v=xkJBkQWr4aA"
											target="_blank">Click Here</a></td>
									</tr>

									<tr>
										<td>Data Visualization and Machine learning</td>
										<td><a
											href="https://www.youtube.com/watch?v=BNPb48ssGes&list=PLWZrAzQzdJHnfUcrNznixwBxe_Frrr6S0"
											target="_blank">Click Here</a></td>
									</tr>

									<tr>
										<td>Insights into Neural Networks for Machine Learning</td>
										<td><a href="https://www.youtube.com/watch?v=wJnTZKnr048"
											target="_blank">Click Here</a></td>
									</tr>

									<tr>
										<td>Business Visualization : Dashboard & Storyboarding</td>
										<td><a href="https://youtu.be/2yBhGqIDIRQ"
											target="_blank">Click Here</a></td>
									</tr>

									<tr>
										<td>Scope of Data Visualization and Machine Learning</td>
										<td><a href="https://youtu.be/1lJXaM_i5tU"
											target="_blank">Click Here</a></td>
									</tr>

								</tbody>
							</table>

						</div>
					</div>
				</div>
			</div>
			<jsp:include page="../adminCommon/footer.jsp" />
</body>
</html>