
<style>
	.termsandcon{
		color: black;
	}
</style>

<!-- <div class="bullet-list"> -->
<!-- 	<div class="section-title"> -->
<!-- 		<h1>Terms and Conditions</h1> -->
<!-- 	</div> -->
<!-- 	<div class="section-title"> -->
<!-- 		<h5>General:</h5> -->
<!-- 	</div> -->
<!-- 	<div id="TnC_General"> -->
<!-- 	</div> -->
<!-- 	<div class="section-title"> -->
<!-- 		<h5>Refund Policy for All programs:</h5> -->
<!-- 	</div> -->
<!-- 	<div id="TnC_RefundPolicy"> -->
<!-- 	</div> -->
<!-- </div> -->

<div>
	<div class="section-title">
		<h1>Terms and Conditions</h1>
	</div>
	<div id="termsandcondition" class="termsandcon">
	</div>
</div>

<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
<script>
		
	let body = {
		"packageId":"${packageId}"
	};
	
	$.ajax({
		type: 'POST',
		url: '/careerservices/m/termsAndConditions',
		contentType: "application/json;", 
		data: JSON.stringify(body),
		dataType: "json",
		success: function( data ){
			console.log(data)
			$('#termsandcondition').html(data.termsandconditions);
		},
		error: function (error) {
			console.log(error);
		}
	});
	

	function setTermsAndConditions(tnc){
		$("#TnC_General").html(tnc["general"]);
		$("#TnC_RefundPolicy").html(tnc["refundPolicy"]);
	}
</script>