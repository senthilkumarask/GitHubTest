<dsp:page>

<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta charset="utf-8" />
<meta http-equiv="content-type" content="text/html; charset=utf-8" />

<title>Insert title here</title>

</head>

<body>

	<dsp:droplet name="/com/bbb/rest/test/GetItemsFromWorkbookTestDroplet">

	<dsp:oparam name="output">

		<dsp:getvalueof var="lineItems" param="response.lineItems"/>

		

		<c:forEach items="${lineItems}" var="lineItem">
		<br><br>
		************************************
 		<br><br>
			configId: <c:out value="${lineItem.configId}"></c:out> <br><br>

			cost: <c:out value="${lineItem.cost }"></c:out><br><br>

			estimatedShipDate : <c:out value="${lineItem.estimatedShipDate }"></c:out><br><br>

			hasInstallation : <c:out value="${lineItem.hasInstallation }"></c:out><br><br>

			productDesc : <dsp:valueof value='<c:out value="${lineItem.productDesc }"/>' valueishtml="true"/><br><br>

			productImage : <c:out value="${lineItem.productImage }"></c:out><br><br>

			quantity : <c:out value="${lineItem.quantity }"></c:out><br><br>

			retailPrice : <c:out value="${lineItem.retailPrice }"></c:out><br><br>

			sku : <c:out value="${lineItem.sku }"></c:out><br><br>

		</c:forEach>
	</dsp:oparam>
	</dsp:droplet>
</body>

</html>

</dsp:page>