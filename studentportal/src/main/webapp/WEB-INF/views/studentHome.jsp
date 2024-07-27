<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!-->

<html class="no-js">
<!--<![endif]-->

<jsp:include page="jscss.jsp">
	<jsp:param value="Confirm Identity" name="title" />
</jsp:include>

<style>
.section-inner {
	background: #fff;
	padding: 15px;
	padding-bottom: 0px;
	box-shadow: 0 0 5px 0 rgba(0, 0, 0, .2);
	margin-bottom: 10px;
	height: 190px;
	overflow-y: auto;
	overflow-x: hidden;
}

.heading {
	margin-top: 0;
	margin-bottom: 10px;
	padding-bottom: 5px;
	color: #545e69;
	font-size: 24px;
	/* font-weight: 600; */
	border-bottom: 1px solid #e7eaec;
}

.subject-container {
	border-bottom: 1px solid #e7eaec;
	padding-bottom: 5px;
}

.info .fa {
	margin-right: 15px;
	color: #ADB8C3;
}

/* .sr-only {
	    position: absolute;
	    width: 1px;
	    height: 1px;
	    padding: 0;
	    margin: -1px;
	    overflow: hidden;
	    clip: rect(0,0,0,0);
	    border: 0;
	} */
.col-xs-1, .col-sm-1, .col-md-1, .col-lg-1, .col-xs-2, .col-sm-2,
	.col-md-2, .col-lg-2, .col-xs-3, .col-sm-3, .col-md-3, .col-lg-3,
	.col-xs-4, .col-sm-4, .col-md-4, .col-lg-4, .col-xs-5, .col-sm-5,
	.col-md-5, .col-lg-5, .col-xs-6, .col-sm-6, .col-md-6, .col-lg-6,
	.col-xs-7, .col-sm-7, .col-md-7, .col-lg-7, .col-xs-8, .col-sm-8,
	.col-md-8, .col-lg-8, .col-xs-9, .col-sm-9, .col-md-9, .col-lg-9,
	.col-xs-10, .col-sm-10, .col-md-10, .col-lg-10, .col-xs-11, .col-sm-11,
	.col-md-11, .col-lg-11, .col-xs-12, .col-sm-12, .col-md-12, .col-lg-12,
	.col-xs-13, .col-sm-13, .col-md-13, .col-lg-13, .col-xs-14, .col-sm-14,
	.col-md-14, .col-lg-14, .col-xs-15, .col-sm-15, .col-md-15, .col-lg-15,
	.col-xs-16, .col-sm-16, .col-md-16, .col-lg-16, .col-xs-17, .col-sm-17,
	.col-md-17, .col-lg-17, .col-xs-18, .col-sm-18, .col-md-18, .col-lg-18
	{
	position: relative;
	min-height: 1px;
	padding-left: 5px;
	padding-right: 5px;
}

.box {
	width: 20px;
	height: 20px;
}

.row {
	margin-top: 10px;
	padding-left: 5px;
	padding-right: 5px;
}

.section-inner .table {
	font-size: 12px;
	color: #5A6877;
}

.section-inner .section-links {
	color: #5A6877;
}

.section-inner .section-links:hover {
	color: #c72127;
}

.section-inner .my-carousel {
	color: #5A6877;
}

.heading .fa {
	color: #c72127;
	font-size: 20px;
}
</style>

<body class="inside">

	<%@ include file="limitedAccessHeader.jsp"%>

	<section>
		<div class="container-fluid">


			<div class="row">
				<div class="col col-md-6">
					<div class="section-inner">
						<h2 class="heading">
							<i class="fa fa-graduation-cap"></i> My Courses
						</h2>

						<div class="info">
							<ul class="list-unstyled">
								<li><i class="fa fa-graduation-cap"></i><a
									class="section-links" href="#">Management Theory and
										Practise</a></li>
								<li><i class="fa fa-graduation-cap"></i><a
									class="section-links" href="#">Information System for
										Managers</a></li>
								<li><i class="fa fa-graduation-cap"></i><a
									class="section-links" href="#">Business Communication &
										Etiquette</a></li>
								<li><i class="fa fa-graduation-cap"></i><a
									class="section-links" href="#">Coporate Social
										Responsibility</a></li>
								<li><i class="fa fa-graduation-cap"></i><a
									class="section-links" href="#">Business Economics</a></li>
								<li><i class="fa fa-graduation-cap"></i><a
									class="section-links" href="#">Information System for
										Managers</a></li>
							</ul>
						</div>
					</div>
				</div>

				<div class="col col-md-6">
					<div class="section-inner">

						<h2 class="heading">
							<i class="fa fa-pencil-square-o"></i> My Assignments: <span
								style="font-size: 16px;">(Dec-2015)</span>
						</h2>

						<div class="info">
							<ul class="list-unstyled">
								<li><i class="fa fa-pencil-square-o"></i><a
									class="section-links" href="#">Management Theory and
										Practise</a></li>
								<li><i class="fa fa-pencil-square-o"></i><a
									class="section-links" href="#">Information System for
										Managers</a></li>
								<li><i class="fa fa-pencil-square-o"></i><a
									class="section-links" href="#">Business Communication &
										Etiquette</a></li>
								<li><i class="fa fa-pencil-square-o"></i><a
									class="section-links" href="#">Coporate Social
										Responsibility</a></li>
								<li><i class="fa fa-pencil-square-o"></i><a
									class="section-links" href="#">Business Economics</a></li>
								<li><i class="fa fa-pencil-square-o"></i><a
									class="section-links" href="#">Information System for
										Managers</a></li>
							</ul>
						</div>
					</div>
				</div>

				<div class="col col-md-6">
					<div class="section-inner">
						<h2 class="heading">
							<i class="fa fa-user"></i> My Profile
						</h2>

						<div class="info">
							<ul class="list-unstyled">
								<li><i class="fa fa-envelope-o"></i><span class="sr-only">Email:</span><a
									href="#">sanketpanaskar@gmail.com</a></li>
								<li><i class="fa fa-phone"></i><span class="sr-only">Phone:</span><a
									href="#">9920726538</a></li>
								<li><i class="fa fa-map-marker"></i><span class="sr-only">Location:</span>Mahalaxmi,
									1204, Near Mhada, Off Eastern Express Highway, Mulund East,
									Mumbai - 400081</li>
							</ul>
						</div>
					</div>
				</div>



				<div class="col col-md-6">
					<div class="section-inner">
						<h2 class="heading">
							<i class="fa fa-trophy"></i> Results: <span
								style="font-size: 14px;">(Dec-2015)</span>
						</h2>

						<table class="table table-condensed table-hover">
							<thead>
								<tr>
									<th>Subject</th>
									<th>TEE</th>
									<th>Assignment</th>
								</tr>
							</thead>
							<tbody>
								<tr>
									<td>Information System for Managers</td>
									<td>40</td>
									<td style="text-align: center">20</td>
								</tr>

								<tr>
									<td>Business Economics</td>
									<td>40</td>
									<td style="text-align: center">20</td>
								</tr>

								<tr>
									<td>Management Theory and Practise</td>
									<td>40</td>
									<td style="text-align: center">20</td>
								</tr>

								<tr>
									<td>Business Communication & Etiquette</td>
									<td>40</td>
									<td style="text-align: center">20</td>
								</tr>

								<tr>
									<td>Coporate Social Responsibility</td>
									<td>40</td>
									<td style="text-align: center">20</td>
								</tr>

								<tr>
									<td>Management Theory and Practise</td>
									<td>40</td>
									<td style="text-align: center">20</td>
								</tr>
							</tbody>
						</table>
					</div>
				</div>

				<div class="col col-md-6">
					<div class="section-inner">
						<h2 class="heading">
							<i class="fa fa-bookmark"></i> Quick Links
						</h2>

						<div class="info">
							<ul class="list-unstyled">
								<li><i class="fa fa-download"></i><a href="#">Hall
										Ticket</a></li>
								<li><i class="fa fa-download"></i><a href="#">Exam
										Registration Receipt</a></li>
								<li><i class="fa fa-external-link"></i><a href="#">Digital
										Library</a></li>
							</ul>
						</div>
					</div>
				</div>

				<div class="col col-md-6">
					<div class="section-inner">
						<h2 class="heading">
							<i class="fa fa-life-ring"></i> My Service Requests
						</h2>

						<div class="info section-links">
							<ul class="list-unstyled">
								<li><i class="fa fa-envelope-o"></i>2 Open Service Requests</li>
								<li><i class="fa fa-envelope-o"></i>2 Closed Service
									Requests</li>
							</ul>
						</div>

						<a class="btn btn-primary btn-sm"><i class="fa fa-plus"></i>
							Create New</a>
					</div>
				</div>

				<div class="col col-md-6">
					<div class="section-inner">
						<h2 class="heading">
							<i class="fa fa-bullhorn"></i> Announcements
						</h2>

						<div class="my-carousel"
							style="margin-top: 10px; margin-left: 10px; margin-bottom: 0px">
							<div>
								<h4>First Announcement</h4>
								<p>This is the text for my announcements. This is the text
									for my announcements. This is the text for my announcements.
									This is the text for my announcements.
							</div>
							<div>
								<h4>Second Announcement</h4>
								<p>This is the text for my announcements. This is the text
									for my announcements. This is the text for my announcements.
									This is the text for my announcements.
							</div>
							<div>
								<h4>Third Announcement</h4>
								<p>This is the text for my announcements. This is the text
									for my announcements. This is the text for my announcements.
									This is the text for my announcements.
							</div>
						</div>
					</div>
				</div>



			</div>
			<!-- wrapper -->

		</div>
		<!-- /container -->
	</section>

	<div class="container-fluid"></div>

	<jsp:include page="footer.jsp" />
	<script>
    $('.my-carousel').slick({
    	autoplay:true,
    	arrows:false
    });
    
    </script>
</body>
</html>
