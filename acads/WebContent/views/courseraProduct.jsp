<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<!DOCTYPE html>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<html lang="en">
<jsp:include page="/views/common/jscss.jsp">
	<jsp:param value="Coursera - Available Products" name="title" />
</jsp:include>
<head>
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@4.0.0/dist/css/bootstrap.min.css" integrity="sha384-Gn5384xqQ1aoWXA+058RXPxPg6fy4IWvTNh0E263XmFcJlSAwiGgFAW/dAiS6JXm" crossorigin="anonymous">
<link rel="stylesheet" href="path/to/font-awesome/css/font-awesome.min.css">
</head>
	<style>
.box-shadow{
	box-shadow: 0px 2px 10px rgba(0,0,0,0.5)
}
</style>
<body>

	<jsp:include page="/views/common/header.jsp" />
	<div class="sz-main-content-wrapper">
		<jsp:include page="/views/common/breadcrum.jsp">
			<jsp:param value="Home;Skillsets" name="breadcrumItems" />
		</jsp:include>
		<div class="sz-main-content menu-closed">
			<div class="sz-main-content-inner">
				<jsp:include page="/views/common/left-sidebar.jsp">
					<jsp:param value="" name="activeMenu" />
				</jsp:include>
				<div class="sz-content-wrapper dashBoard">
					<jsp:include page="/views/common/studentInfoBar.jsp" />
					<div class="sz-content padding-top">
						<jsp:include page="/views/common/messages.jsp" />
						<div style="text-align:right;">
						<!-- <h3 style="color:Blue; font-family:verdana; text-transform: lowercase; text-align:right;"><b>cousera</b></h3>
						<div class="clearfix"></div>
						<h5 style="color:DeepSkyBlue; font-family:courier; text-transform: lowercase; text-align:right;">for campus</h5> -->
						<img src="https://staticfilesacads.s3.ap-south-1.amazonaws.com/assets/images/Coursera-for-Campus.png" width="200" height="100">
						</div>
						<p style="font-size:20px" class="font-weight-bold mt-4">Keep Pace with Global Trends</p>
					<p style="font-size:20px">Coursera offers online courses in association with leading universities and organisations in various subjects. 
					As a student of NMIMS Global, you have an opportunity to access 5,000+ courses and further upgrade your skills on 
					the go from this portal at a special student fee.</p><br>
					<div class="clearfix"></div>
					<p style="font-size:20px" class="font-weight-bold mt-4">Boost Your Career with Skillsets</p>
					<p style="font-size:20px">SkillSets are an amalgamation of industry skill requisites crafted into your program curriculum. 
					Skillsets use world-class courses from universities and companies from across the globe and provide hands-on learning.
					</p><br>
					<div class="clearfix"></div>
					<p style="font-size:20px" class="font-weight-bold mt-4">Learn from a robust framework</p>
					<p style="font-size:20px">With unlimited access to 5,000+ world-class courses, hands-on projects, and certificate programs, enrich your online learning experience further.</p><br>
					<div class="clearfix"></div>
					<p style="font-size:20px" class="font-weight-bold mt-4">Core Features & Benefits:</p>
					<div class="clearfix"></div>
						<ul>
							<li><p style="font-size:20px"><b>Courses from the world's most illustrious Universities:</b> Cutting-edge in-demand courses from 220+ Universities from across the globe.</p></li>
							<li><p style="font-size:20px"><b>Faculty:</b> Top instructors from the world's leading universities and industry partners.</p></li>
							<li><p style="font-size:20px"><b>Learning that fits your schedule:</b> Learn at your own pace, move between multiple courses, or switch to a different course.</p></li>
							<li><p style="font-size:20px"><b>Unlimited Certificates:</b> Earn a certificate for every program you successfully complete.</p></li>
							<li><p style="font-size:20px"><b>Special Student Discount:</b> Spend less on your learning with a fixed one-time annual subscription fee.</p></li>
						</ul><br>
						<div class="clearfix"></div>
						<h4 style="color:red; text-align:center;">7,000/- + 18% GST</h4>
						<div class="clearfix"></div>
						<h6 style="text-align:center;"><i>Annual Subscription</i></h6>
						<div class="clearfix"></div>
						<p style="font-size:30px; text-align:center; text-decoration: underline;"><a href="${leanersURL }" target="_blank">Click Here To Experience The Platform</a></p><br>
						<div class="clearfix"></div>
						<p style="font-size:20px; text-align:center;">Stay up-to-date with latest developments across streams right from the student portal with Coursera</p>
						<div style="text-align:center;">
							<a href="${courseraPaymentURL}?studentNo=${sapId}&dob=${dob}&IC=true&type=reregistration&request=coursera" class="btn btn-danger" target="_blank" style="width:300px; height:50px; padding:10px; text-transform: none;">Enroll Now</a>
						</div>
						<p style="font-size:17px; text-align:center;">
							You can opt for cancellation of Coursera within 7 days of the payment date only.<br> Activation of your Coursera portal will take a minimum of 7 days from the payment date.</p><br>
					</div>
				</div>
			</div>
		</div>
	</div>
	<jsp:include page="/views/common/footer.jsp" />
<script src="https://code.jquery.com/jquery-3.2.1.slim.min.js" integrity="sha384-KJ3o2DKtIkvYIK3UENzmM7KCkRr/rE9/Qpg6aAZGJwFDMVNA/GpGFF93hXpG5KkN" crossorigin="anonymous"></script>
<script src="https://cdn.jsdelivr.net/npm/popper.js@1.12.9/dist/umd/popper.min.js" integrity="sha384-ApNbgh9B+Y1QKtv3Rn7W3mgPxhU9K/ScQsAP7hUibX39j7fakFPskvXusvfa0b4Q" crossorigin="anonymous"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@4.0.0/dist/js/bootstrap.min.js" integrity="sha384-JZR6Spejh4U02d8jOt6vLEHfe/JQGiRRSQQxSfFWpi1MquVdAyjUar5+76PVCmYl" crossorigin="anonymous"></script>
</body>

</html>