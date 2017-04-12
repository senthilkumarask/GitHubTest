<%@ taglib prefix="dsp"
	uri="http://www.atg.com/taglibs/daf/dspjspTaglib1_0"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<dsp:page>
<dsp:importbean bean="/atg/commerce/order/OrderLookup" />
<dsp:importbean bean="com/bbb/integration/csr/CSRGetOrderListDroplet" />
	
	<html>
<head>
<title>Customer Service Representative - View Shipping Addresses</title>
<link rel="stylesheet" href="/qas/qas.css" />
<style>
.error {
	color: red;
}

.mainContainer {
	width: 1000px;
	text-align: center;
}

.leftContainer {
	width: 500px;
}

.rightContainer {
	margin: 0 auto;
}

table,td,th {
	border: 1px solid green;
	vertical-align: top;
}

th {
	background-color: green;
	color: white;
}

.centerAlign {
	text-align: center
}

.mainContainer {
	margin-left: auto;
	margin-right: auto;
	width: 1000px;
}

.contentContainer {
	float: left;
	width: 1000px;
}
</style>
</head>
<body>
	<div class="centerAlign">
		<h2>Customer Service Representative - View Shipping Addresses</h2>
	</div>
		
		<div class="centerAlign">
		<br/>
		<dsp:droplet name="CSRGetOrderListDroplet">
			<c:set var="orderId"><c:out value="${param.orderId}"/></c:set>
			<dsp:param name="orderId" value="${orderId}" />
			<dsp:oparam name="output">
			<dsp:getvalueof var="order" param="result"/>
						<c:set var="onlineOrderNumber" value="${order.onlineOrderNumber}"/>
						<c:set var="bopusOrderNumber" value="${order.bopusOrderNumber}"/>				
			<div id="reviewFrag">
				<dsp:include page="csr_review_frag.jsp" flush="true" >
					<dsp:param name="order" value="${order}"/>
					<dsp:param name="onlineOrderNum" value="${onlineOrderNumber}"/>
					<dsp:param name="bopusOrderNum" value="${bopusOrderNumber}"/>
				</dsp:include>
			</div>
			</dsp:oparam>
		</dsp:droplet>
		</div>	
	
    </body>
  </html>
	
</dsp:page>