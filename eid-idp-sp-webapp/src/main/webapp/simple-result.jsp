<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
<title>eID Identity Provider (IdP) - Test Service Provider (SP)</title>
</head>
<body>
<h1>Simple Identification Protocol Results</h1>
<p>Results should be displayed here.</p>
<table>
	<tr>
		<th>Name</th>
		<td><%=session.getAttribute("Name")%></td>
	</tr>
	<tr>
		<th>First name</th>
		<td><%=session.getAttribute("FirstName")%></td>
	</tr>
</table>
<a href="index.jsp">Back</a>
</body>
</html>