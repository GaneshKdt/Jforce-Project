<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<div class="container-fluid footerWrapper">
	<footer>
		<div class="col-md-6 footerSection"
			style="color: white;; margin-top: 20px;">
			<h2>Social Connect</h2>
			<h3>Connect with us via Social Media and get all our latest news
				and upcoming events.</h3>

			<div class="row">
				<div class="col-lg-18">
					<ul class="footerSocialLinks">
						<li><a href="https://www.facebook.com/NMIMSSCE"
							target="_blank" class="facebook"><i class="fa-brands fa-facebook-f"></i></a></li>
						<li><a href="https://twitter.com/NMIMSGlobal" target="_blank"
							class="twitter"><i class="fa-brands fa-twitter"></i></a></li>
						<li><a
							href="https://plus.google.com/u/0/116325782206816676798/posts"
							target="_blank" class="google-plus"><i class="fa-brands fa-google-plus-g"></i></a></li>
						<li><a href="#https://www.youtube.com/@NMIMSGlobal" target="_blank" class="youtube"><i class="fa-brands fa-youtube"></i></a></li>
					</ul>
				</div>
			</div>
		</div>

		<div class="col-md-6 footerSection"
			style="color: white;; margin-top: 20px;">
			<h2>Contact NGA SCE</h2>

			Address:<br> V.L.Mehta Road, Vile Parle (W), Mumbai, <br>
			Maharashtra - 400056 <br> Email Address: ngasce@nmims.edu <br>
			Tel. No: 7506283418 / 022 65265057 <br> © 2023 NMIMS. All Rights
			Reserved.
		</div>

		<!-- Session Synchronization -->

		<% double random = Math.random(); %>

		<img alt=""
			src="/exam/resources_2015/images/singlePixel.gif?id=<%=random%>">
		<img alt=""
			src="/acads/resources_2015/images/singlePixel.gif?id=<%=random%>">
		<img alt=""
			src="/careerservices/resources_2015/images/singlePixel.gif?id=<%=random%>">
	</footer>
</div>


<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_STUDENTPORTAL_STATIC_RESOURCES')" />resources_2015/js/vendor/jquery-1.11.2.min.js"></script>
<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_STUDENTPORTAL_STATIC_RESOURCES')" />resources_2015/js/vendor/bootstrap.min.js"></script>
<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_STUDENTPORTAL_STATIC_RESOURCES')" />resources_2015/js/vendor/jquery-ui.min.js"></script>
<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_STUDENTPORTAL_STATIC_RESOURCES')" />resources_2015/js/vendor/jquery.validate.min.js"></script>
<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_STUDENTPORTAL_STATIC_RESOURCES')" />resources_2015/js/vendor/additional-methods.min.js"></script>
<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_STUDENTPORTAL_STATIC_RESOURCES')" />resources_2015/js/vendor/fileinput.min.js"></script>
<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_STUDENTPORTAL_STATIC_RESOURCES')" />resources_2015/js/vendor/bootstrap-datepicker.min.js"></script>
<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_STUDENTPORTAL_STATIC_RESOURCES')" />resources_2015/js/vendor/scripts.js"></script>
<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_STUDENTPORTAL_STATIC_RESOURCES')" />resources_2015/js/vendor/slick.js"></script>
<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_STUDENTPORTAL_STATIC_RESOURCES')" />resources_2015/js/main.js?id=2"></script>

<!-- Refersh session on body events -->
<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_STUDENTPORTAL_STATIC_RESOURCES')" />assets/js/refreshSession-v1.js"></script>


