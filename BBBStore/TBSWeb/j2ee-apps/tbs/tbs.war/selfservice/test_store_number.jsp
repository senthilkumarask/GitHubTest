<%
   // Create cookie for store number
   String storeNumberParam = request.getParameter("store_number");
   if (storeNumberParam!=null){
	   Cookie storeNumber = new Cookie("store_number",storeNumberParam);
	   // Set expiry date of 30 days for the cookie.
	   storeNumber.setMaxAge(60*60*24*30); 
	   storeNumber.setPath("/"); 
	   // Add the cookie in the response header.
	   response.addCookie( storeNumber );
   }
%>
<html>
<head>
<title>Setting Store Number Cookie</title>
</head>
<body>
<center>
<h1>Setting Store Number Cookie</h1>
</center>
<form action="" method="GET">
Store Number: <input type="text" name="store_number">
<br />
<input type="submit" value="Submit" />
</form>
</body>
</html>