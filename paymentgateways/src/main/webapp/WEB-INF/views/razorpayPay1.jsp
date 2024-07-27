<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Razorpay payment processing...</title>
</head>
<body>
	<center>
		<h1>Please do not refresh this page...</h1>
	</center>
	<button id="rzp-button1">Pay</button>
	<script src="https://checkout.razorpay.com/v1/checkout.js"></script>
	<script>
	var options = {
    	"key": "${key}",
    	"amount": "${amount}", 
    	"currency": "${currency}",
    	"name": "${name}",
    	"description": "${description}",
    	"image": "https://d3udzp2n88cf0o.cloudfront.net/css/logo.png",
    	"order_id": "${order_id}", 
    	"callback_url": "${callback_url}",
    	"prefill": ${prefill},
    	"notes": ${notes},
    	"theme": {
        	"color": "#3399cc"
    		}
		};

/*	var rzp1 = new Razorpay(options);
document.getElementById('rzp-button1').onclick = function(e){
    rzp1.open();
    e.preventDefault();
} */

var rzp1 = new Razorpay(options);	
rzp1.on('payment.failed', function (response){
        alert(response.error.code);
        alert(response.error.description);
        alert(response.error.source);
        alert(response.error.step);
        alert(response.error.reason);
        alert(response.error.metadata.order_id);
        alert(response.error.metadata.payment_id);
});
document.addEventListener('DOMContentLoaded', (event) => {
  rzp1.open();
})
</script>
</body>
</html>
