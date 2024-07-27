<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    <%@ taglib uri = "http://java.sun.com/jsp/jstl/core" prefix = "c" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <title>NGASCE payment processing</title>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.4.1/css/bootstrap.min.css">
  <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.4.1/jquery.min.js"></script>
  <script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.16.0/umd/popper.min.js"></script>
  <script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.4.1/js/bootstrap.min.js"></script>
</head>
<body>
	
	<center>
		<div>
			
			
		</div>
	</center>
	<form action="processTransaction" method="POST" name="f1">
		<div class="container"><br/>
			<div class="card">
			  <div class="card-header">
			  	<center>Ngasce payment gateway</center>
			  </div>
			  <div class="card-body">
			  	<div class="row">
			  	<c:forEach var ="payment_option" items="${ payment_options }">
			  		<div class="col-sm-4" >
			  		<div style="height:130px;">
			  			<center>
						<img style="max-width:200px" src="https://studentzone-ngasce.nmims.edu/studentportal/assets/images/${ payment_option.image }" />
						</center>
					</div>
					<div>
						<center><input type="submit" name="payment_option" value="${ payment_option.name }" /></center>
					</div>
					</div>
				</c:forEach>
				</div>
			  </div>
			  <div class="card-footer">
			  	Note: Paytm gateway accept all types of debit card,credit card,netbanking as well as paytm wallet and UPI
			  </div>
			</div>
		</div>
		<input type="hidden" placeholder="trackid" name="track_id" value="${track_id }" /> <br/> <br/>
		<input type="hidden" placeholder="sapid"  name="sapid" value="${sapid }" /> <br/> <br/>
		<input type="hidden" placeholder="type"  name="type" value="${type }" /> <br/> <br/>
		<input type="hidden" placeholder="amount"  name="amount" value="${amount }" /> <br/> <br/>
		<input type="hidden" placeholder="description"  name="description" value="${description }" /> <br/> <br/>
		<input type="hidden" placeholder="source"  name="source" value="${source }" /> <br/>  <br/>
		<input type="hidden" placeholder="portal_return_url"  name="portal_return_url" value="${portal_return_url}" /> <br/> <br/>
		<input type="hidden" placeholder="created_by"  name="created_by" value="${created_by }" /> <br/> <br/>
		<input type="hidden" placeholder="updated_by"  name="updated_by" value="${updated_by }" /> <br/> <br/>
		<input type="hidden" placeholder="mobile"  name="mobile" value="${mobile }" /> <br/> <br/>
		<input type="hidden" placeholder="email_id"  name="email_id" value="${email_id }" /> <br/> <br/>
		<input type="hidden" placeholder="first_name"  name="first_name" value="${first_name }" /> <br/> <br/>
		<input type="hidden" name="response_method" value="${ response_method }" />
		<br/> <br/>
	</form>
</body>
</html>