
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
			<jsp:param value="Important Documents;Lecture Videos"
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

						<h2 class="red text-capitalize">Lecture Videos</h2>
						<div class="clearfix"></div>
						<div class="panel-content-wrapper">

							<h4>Data Visualization:</h4>
							<div>
								<ul>
									<li><a
										href="https://sas.connectsolutions.com/p7ap0b1uyfc/"
										target="_blank">Lecture 1 </a></li>
									<li><a
										href="https://sas.connectsolutions.com/p1sjn951kc6/"
										target="_blank">Lecture 2 </a></li>
								</ul>
							</div>
							<div class="clearfix"></div>
							<h4>E Miner:</h4>
							<div>
								<ul>
									<li><a
										href="https://sas.connectsolutions.com/p6vt1ttiur8/"
										target="_blank">Lecture 1 </a></li>
									<li><a
										href="https://sas.connectsolutions.com/p7ngej9xj37/"
										target="_blank">Lecture 2 </a></li>
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