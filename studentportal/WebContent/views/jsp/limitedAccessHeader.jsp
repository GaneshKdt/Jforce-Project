
<%@page import="com.nmims.helpers.PersonStudentPortalBean"%>
<%@page import="com.nmims.beans.StudentStudentPortalBean"%>

<nav class="navbar navbar-inverse navbar-fixed-top customNavbar"
	role="navigation">
	<div class="container-fluid">

		<div class="social-col">
			<ul class="headerSocialLinks">
				<li><a href="https://www.facebook.com/NMIMSSCE" target="_blank"
					class="facebook"><i class="fa-brands fa-facebook-f"></i></a></li>
				<li><a href="https://twitter.com/NMIMS_SCE" target="_blank"
					class="twitter"><i class="fa-brands fa-twitter"></i></a></li>
				<li><a
					href="https://plus.google.com/u/0/116325782206816676798/posts"
					target="_blank" class="google-plus"><i class="fa-brands fa-google-plus"></i></a></li>
				<li><a href="#" target="_blank" class="youtube"><i class="fa-brands fa-youtube"></i></a></li>
			</ul>
		</div>

		<div class="navbar-header">
			<button type="button" class="navbar-toggle collapsed"
				data-toggle="collapse" data-target="#navbar" aria-expanded="false"
				aria-controls="navbar">
				<span class="sr-only">Toggle navigation</span> <span
					class="icon-bar"></span> <span class="icon-bar"></span> <span
					class="icon-bar"></span>
			</button>
		</div>

		<div id="navbar" class="navbar-collapse collapse">
			<div class="col-md-16 no-padding">


				<%
		            String userId = (String)session.getAttribute("userId");
					PersonStudentPortalBean user = (PersonStudentPortalBean)session.getAttribute("user_studentportal");
					StudentStudentPortalBean studentBean = (StudentStudentPortalBean)session.getAttribute("student_studentportal");
            		String name = "";
            		String roles = "";
            		String program = "";
            		String studentPhotoUrl = "";
            		String userEmail = "";
            		String userMobile = "";
            		String pStructure = "";
            		
            		if(user != null){
            			roles = user.getRoles();
            			name = user.getFirstName() + " " + user.getLastName();
            			program = user.getProgram();
            			userEmail = user.getEmail();
            			userMobile = user.getContactNo();
            		}
            		if(studentBean != null && studentBean.getImageUrl() != null){
            			studentPhotoUrl = studentBean.getImageUrl().trim();
            			name = studentBean.getFirstName() + " " + studentBean.getLastName();
            			pStructure = studentBean.getPrgmStructApplicable();
            		}
            		
		            if(userId != null) { %>


				<ul class="headerLinks">
					<li><a href="logout">Logout</a></li>
				</ul>
				<%} %>
			</div>


		</div>
	</div>
</nav>

<header class="customHeader">
	<div class="logoWrapper">
		<img src="${pageContext.request.contextPath}/resources_2015/images/logo.jpg" width="100%" alt="Logo" />
	</div>

	<div class="rightHeadWrapper">
		<h1>Welcome to NGASCE Student Zone</h1>

		<%if(userId != null) { %>
		<div class="userContainer">
			<div class="userImg">
				<%if(!"".equals(studentPhotoUrl)) {%>
				<img src="<%=studentPhotoUrl%>" alt="Student Photo"
					class="img-responsive" style="height: 100%;" />
				<%}else{ %>
				<img src="${pageContext.request.contextPath}/resources_2015/images/userImg.jpg" alt="Student Photo"
					class="img-responsive" />
				<%} %>
			</div>
			<div class="detailWrapper">
				<h2><%=name.toUpperCase() %></h2>
				<p>
					User ID:
					<%=userId %></p>
				<p>
					Program:
					<%=program %></p>
			</div>
		</div>

		<%} %>
	</div>

</header>

<div class="clearfix"></div>


