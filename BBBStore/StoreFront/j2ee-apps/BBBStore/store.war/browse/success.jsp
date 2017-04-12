<%@ taglib uri="http://www.atg.com/taglibs/daf/dspjspTaglib1_0"
	prefix="dsp"%>
<%@ taglib uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" prefix="dspel" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>


<%@ page import="atg.servlet.*" %>
<dsp:page>

<dsp:importbean bean="/atg/dynamo/Configuration"/>
<dsp:importbean
	bean="/atg/commerce/order/purchase/CartModifierFormHandler"/>

<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>
<dsp:importbean bean="/atg/commerce/catalog/ProductLookup"/>
<dsp:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach"/>

<dsp:setvalue bean="CartModifierFormHandler.addItemCount" value="1" />
<head><title>Add to Cart Success Page</title></head>
<body>
<h1>Item added successfully</h1>  

	Session ID: <c:out value="${pageContext.session.id}"></c:out><br/>
	Order Id : <dsp:valueof bean="/atg/commerce/ShoppingCart.current.id"></dsp:valueof><br/> 
	CommerceItem Count: <dsp:valueof bean="/atg/commerce/ShoppingCart.current.commerceItemCount"></dsp:valueof><br/>
	<br/>
	Commerce Items: <br/>
	<table border="1">
	<tr>
	<td>CommerceItemId</td>
	<td>ProductId</td>
	<td>SKUId</td>
	<td>Qty</td>
	<td>Registry ID</td>
	<td>Store Id</td>
	</tr>
	<dsp:droplet name="ForEach">
		<dsp:param name="array" bean="/atg/commerce/ShoppingCart.current.commerceItems" />
		 <dsp:oparam name="output">          
            <tr>
			<td><dsp:valueof param="element.id" /> </td>
			<td><dsp:valueof param="element.auxiliaryData.productId" /> </td>
			<td><dsp:valueof param="element.catalogRefId" /> </td>
			<td><dsp:valueof param="element.quantity" /> </td>
			<td><dsp:valueof param="element.registryId" /> </td>
			<td><dsp:valueof param="element.storeId" /> </td>
			</tr>
        </dsp:oparam>
	</dsp:droplet>
	</table>
</body>
</dsp:page>
<%-- @version $Id: //product/Eclipse/main/plugins/atg.project/templates/index.jsp#1 $$Change: 425088 $--%>
