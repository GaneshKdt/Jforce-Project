<HTML>
<HEAD>
<TITLE> Sample Pages </TITLE>
</HEAD>

<table border="1" align="center"  width="100%" >
	<tr>
	<td align="left" width="90%"><font  size = 5 color = darkblue face = verdana ><b>Sample Page</td>
	<td align="right"width="10%"><IMG SRC="images/fssnet.JPG" WIDTH="169" HEIGHT="37" BORDER="0" ALT=""></td>
	</tr>
</table>

<br><br>

<BODY>
	<%@ page language="java" session="true"%>
	<%@ page import="java.util.Random" %>
	<%@ page import="java.util.Date" %> 
	<%@ page import="java.io.*" %>
	
	<br><br>

<table border="1" align="center"  width="350">
	<tr>
	<th colspan="50" bgcolor="darkblue" ><font  size = 2 color = White face = verdana >Final Response Parameters</th>
	</tr>
		 <tr>
			<td colspan="35">Transaction Status</td>
			<td><FONT color="green"><b><%= request.getParameter("ResResult") %> </td>
		</tr>		
		<tr>
			<td colspan="35">Merchant TrackID</td>
			<td><b><%= request.getParameter("ResTrackId") %> </td>
		</tr>

		<tr>
			<td colspan="35">Transaction PaymentID</td>
			<td><b><%= request.getParameter("ResPaymentId") %> </td>
		</tr>
		<tr>
			<td colspan="35">Transaction Reference No</td>
			<td><b><%= request.getParameter("ResRef") %> </td>
		</tr>
		<tr>
			<td colspan="35">Transaction ID</td>
			<td><b><%= request.getParameter("ResTranId") %> </td>
		</tr>
		<tr>
			<td colspan="35">Transaction Amount</td>
			<td><b><%= request.getParameter("ResAmount") %> </td>
		</tr>
		<tr>
			<td colspan="35">Error Desc.</td>
			<td><b><%= request.getParameter("ResError") %> </td>
		</tr>
		
			
		<tr>
	<th colspan="50" bgcolor="darkblue" height="15"></th>
	</tr>
</table>

<br>
<center><A href="Index.html"><p style="color:blue"><b>Perform another Txn</b></p></A></center>
<br>
	<table border="1" align="center"  width="100%" >
	<tr>
	<td align="left" width="90%"><font  size = 5 color = darkblue face = verdana ><b>Sample Page</td>
	<td align="right"width="10%"><IMG SRC="images/fssnet.JPG" WIDTH="169" HEIGHT="37" BORDER="0" ALT=""></td>
	</tr>
</table>

	
</BODY>
</HTML>
<!-- Disclaimer:- Important Note in Sample Pages
- This is a sample demonstration page only ment for demonstration, this page should not be used in production
- Transaction data should only be accepted once from a browser at the point of input, and then kept in a way that does not allow others to modify it (example server session, database  etc.)
- Any transaction information displayed to a customer, such as amount, should be passed only as display information and the actual transactional data should be retrieved from the secure source last thing at the point of processing the transaction.
- Any information passed through the customer's browser can potentially be modified/edited/changed/deleted by the customer, or even by third parties to fraudulently alter the transaction data/information. Therefore, all transaction information should not be passed through the browser to Payment Gateway in a way that could potentially be modified (example hidden form fields). 
 -->
