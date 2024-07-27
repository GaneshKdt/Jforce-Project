<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Insert title here</title>
</head>
<body>
	<form action="/processTransaction" method="POST">
		<input type="text" placeholder="trackid" name="track_id" value="" /> <br/> <br/>
		<input type="text" placeholder="sapid"  name="sapid" value="" /> <br/> <br/>
		<input type="text" placeholder="type"  name="type" value="" /> <br/> <br/>
		<input type="text" placeholder="amount"  name="amount" value="" /> <br/> <br/>
		<input type="text" placeholder="description"  name="description" value="" /> <br/> <br/>
		<input type="text" placeholder="payment_option"  name="payment_option" value="" /> <br/> <br/>
		<input type="text" placeholder="source"  name="source" value="" /> <br/>  <br/>
		<input type="text" placeholder="portal_return_url"  name="portal_return_url" value="" /> <br/> <br/>
		<input type="text" placeholder="created_by"  name="created_by" value="" /> <br/> <br/>
		<input type="text" placeholder="updated_by"  name="updated_by" value="" /> <br/> <br/>
		<input type="text" placeholder="mobile"  name="mobile" value="" /> <br/> <br/>
		<input type="text" placeholder="email_id"  name="email_id" value="" /> <br/> <br/>
		<input type="text" placeholder="first_name"  name="first_name" value="" /> <br/> <br/>
		<input type="submit" placeholder="trackid"  value="submit" /> <br/> <br/>
	</form> 
</body>
</html>