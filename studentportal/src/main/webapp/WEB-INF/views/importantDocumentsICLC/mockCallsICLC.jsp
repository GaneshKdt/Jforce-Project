
<!DOCTYPE html>


<html lang="en">

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<jsp:include page="../adminCommon/jscss.jsp">
	<jsp:param value="Lecture Videos" name="title" />
</jsp:include>



<body>

	<%@ include file="../adminCommon/header.jsp"%>
	<div class="sz-main-content-wrapper">

		<jsp:include page="../adminCommon/breadcrum.jsp">
			<jsp:param value="Important Documents;Mock Calls"
				name="breadcrumItems" />
		</jsp:include>


		<div class="sz-main-content menu-closed">
			<div class="sz-main-content-inner">
				<jsp:include page="../adminCommon/left-sidebar.jsp">
					<jsp:param value="" name="activeMenu" />
				</jsp:include>


				<div class="sz-content-wrapper examsPage">
					<%@ include file="../adminCommon/adminInfoBar.jsp"%>
					<div class="sz-content">

						<h2 class="red text-capitalize">Mock Calls</h2>
						<div class="clearfix"></div>
						<div class="panel-content-wrapper">
							<h6>
								<b>Scenarios: </b>
							</h6>
							<div>
								<ul>
									<li><a
										href="resources_2015/importantDocsICLC/Scenarios.xlsx">Download</a></li>
								</ul>
							</div>
							<h6>
								<b>11th December:</b>
							</h6>
							<div>
								<ul>
									<li><a
										href="https://nmims.webex.com/nmims/lsr.php?RCID=56ef87112f3a0e6d9f1fe865ae9dd084 "
										target="_blank">Call 1 </a></li>
								</ul>
							</div>
							<div class="clearfix"></div>
							<h6>
								<b>13th December: </b>
							</h6>
							<div>
								<ul>
									<li><a
										href="https://nmims.webex.com/nmims/lsr.php?RCID=bb47c49f4b70e03375f2774d08f3b1c4 "
										target="_blank">Call 1 </a></li>
								</ul>
							</div>
							<h6>
								<b>19th December: </b>
							</h6>
							<div>
								<ul>
									<li><a
										href="https://nmims.webex.com/nmims/ldr.php?RCID=19c873b8c1edd51803c6a643d0c84a2c"
										target="_blank">Call 1 </a></li>
								</ul>
							</div>

							<h6>
								<b>21st December: </b>
							</h6>
							<div>
								<ul>
									<li><a
										href="https://nmims.webex.com/nmims/ldr.php?RCID=6a119ac7c06d95eb19cc541eb9f9ce28"
										target="_blank">Call 1 </a></li>
								</ul>
							</div>

						</div>


					</div>
				</div>
			</div>
		</div>
	</div>
	<jsp:include page="../adminCommon/footer.jsp" />


</body>
</html>