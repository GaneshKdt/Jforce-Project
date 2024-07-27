<!-- modal for payment select -->
<div class="modal fade" id="paymentGatewayOptions">
	<div class="modal-dialog" style="z-index:9999">

		<!-- Modal content-->
		<div class="modal-content">

			<div class="modal-body">

				<h1>Select Payment Gateway</h1>
				<div class="row paymentOptions">
					<center>Loading payment options...</center>
				</div>

			</div>

		</div>

	</div>
</div>
<script>
  $(document).ready(function(){
	  console.log("inside document ready");
	  $.ajax({
		  url: '/studentportal/student/getPaymentOptions',
		  methods: 'GET',
		  success: function(response){
			  try{
				  console.log("In success : " + response);
				  console.log(response);
				  
				  var htmlData = "";
					if(response.length == 0){
						htmlData = "No Payment Option Found";
					}
				  for(var i=0;i < response.length;i++){
					  htmlData = htmlData + '<div class="col-xs-3"> <img style="height:120px;" class="img-responsive" src="${pageContext.request.contextPath}/assets/images/' + response[i].image +'" /> <center> <a data-paymentGateway="'+ response[i].name +'" class="btn btn-large btn-primary submitButton selectPaymentGateway"> '+ response[i].name +' </a> </center> </div>';
				  }
				  $('.paymentOptions').html(htmlData);
			  }
			  catch(e){
				  console.log(e);
				  $('.paymentOptions').html("No Payment Option Found");
			  }
		  },
		  error: function(){
			  alert("Error While Getting Payment Options");
		  }
		  
	  });
  });
  </script>