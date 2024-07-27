<!DOCTYPE html>

<html lang="en">
	<head>
		<jsp:include page="/views/common/jscss.jsp">
			<jsp:param value="Learning Portal" name="title"/>
		</jsp:include>
		<link rel="stylesheet" href="assets/johnpolacek-stacktable/stacktable.css">
        <link rel="stylesheet" type="text/css" href="assets/css/fonts.css">
		<style>
			th, .st-key{
   				text-align: center;
			}
		</style>
	</head>
	<body>
		<div class="sz-content">
			<div class="package-details">
				<div class="card p-3">
					<h2 class="header pt-3 pl-3"> Learning Portal </h2>
					
					<div>
						The learning portal supports participants in assessing and identifying key developmental areas and offers suggested modules for individual growth. Also consists library of over 3500 e-learning modules on soft skills required for career growth. The participants can access the library round-the-clock for self-paced learning. Students are advised to spend atleast 2 hours in a week on this portal.
					</div>
					
					<%
						if(request.getAttribute("SAMLResponse") != null){
					%>
						<form method="post" accept-charset="utf-8" action="${ PostTo }">
						    <textarea name="SAMLResponse" hidden="hidden">${ SAMLResponse }</textarea>
						    <button class="btn btn-primary col-lg-3 col-md-5 col-sm-8 col-xs-12 mr-auto mt-3" >Go to the Learning Portal</button>
					  	</form>
					<%
						}else{
					%>
						<h5 style="color:red">There was an unexpected error forming the link to the Learning Portal. Please contact support or try again later.</h5>
					<%
						}
					%>
					
					<div class="mt-5">
						<h5 style="font-size: larger;">Features &amp; Benefits</h5>
						<table class="table table-bordered ">
							<thead class="">
								<tr>
									<th width="20%"><b>Portal Elements</b></th>
									<th width="20%"><b>Features</b></th>
									<th width="60%"><b>Benefits</b></th>
								</tr>
							</thead>
							<tbody>
								<tr>
									<td>Self- Discovery</td>
									<td>
										<ul class="bullet-list pl-3">
											<li>Assessments &amp; Tasks</li>
											<li>Resources</li>
										</ul>
									</td>
									<td>
										Creating insights around your strengths and weaknesses, your dislikes and passions, and how 
										you relate to others, is key to making decisions about your next career move. This section 
										has been designed to help you better understand how you react to the world around you, and 
										what you feel is most important to your well-being and happiness. It's all about knowing 
										yourself better so you can determine the best next step in your career journey!
									</td>
								</tr>
								<tr>
									<td>Career Planning</td>
									<td>
										<ul class="bullet-list pl-3">
											<li>Assessments &amp; Tasks</li>
											<li>Resources</li>
											<li>Tools</li>
										</ul>
									</td>
									<td>
										What work makes you happiest? What type of company would you be proudest to work for? What working conditions enable you to be the most productive? This section will help you think about what’s most important to you in your career and help you identify features and characteristics that will guide your journey - and eventually, your assessment of job offers.
									</td>
								</tr>
								<tr>
									<td>Personal Branding</td>
									<td>
										<ul class="bullet-list pl-3">
											<li>Assessments &amp; Tasks</li>
											<li>Resources</li>
										</ul>
									</td>
									<td>
										To help you clearly communicate your unique value to the marketplace and stand out from the competition, you need to be able to articulate your personal brand. We’ll provide you with tactics and tools that will lead you through steps designed to help you create your personal brand and enable you to clearly communicate that brand – and your value – to everyone you meet.
									</td>
								</tr>
								<tr>
									<td>Job Search</td>
									<td>
										<ul class="bullet-list pl-3">
											<li>Assessments &amp; Tasks</li>
											<li>Resources</li>
										</ul>
									</td>
									<td>
										This section helps you prepare for Job Search and how to approach the Job Market. Here you will also learn effective Interviewing Techniques to help you get your designed job.
									</td>
								</tr>
								<tr>
									<td>Close the Deal</td>
									<td>
										<ul class="bullet-list pl-3">
											<li>Assessments &amp; Tasks</li>
											<li>Resources</li>
											<li>Tools</li>
										</ul>
									</td>
									<td>
										You're well on your way to your next career opportunity. As you prepare for the final phase of your job search – interviewing for positions that interest you, assessing job offers and negotiating with prospective employers – use the resources in this section to help improve your chances for success.
									</td>
								</tr>
							</tbody>
						</table>
					</div>
				</div>
			</div>
		</div>
		
		<script src="assets/js/jquery-1.11.3.min.js"></script>
		<script src="assets/johnpolacek-stacktable/stacktable.js"></script>
		<script>
			$(document).on('ready', function() {
			
				$('table').addClass("table table-bordered");
				$.each($("table"), function(){
					$(this).stacktable();
			   		$(this).css("overflow-x","auto");
			  	});
			});
		</script>
	</body>		
</html>