<dsp:page>
<dsp:importbean bean="/atg/commerce/ShoppingCart" />
<dsp:droplet name="/com/bbb/commerce/cart/BBBCartItemCountDroplet">
	<dsp:param name="shoppingCart" bean="ShoppingCart.last" />
	<dsp:oparam name="output">
		 <dsp:getvalueof var="cartItemCount" param="commerceItemCount"/>
	</dsp:oparam>
</dsp:droplet>	
<dsp:getvalueof var="OracleResponsys_url" param="OracleResponsys_url"/>
<dsp:getvalueof var="onlineOrderNumber" param="onlineOrderNumber"/>
<dsp:getvalueof var="orderAmount" param="orderAmount"/>
<dsp:getvalueof var="profileID" param="profileID"/>
<dsp:getvalueof var="currentSiteId" param="currentSiteId"/>

<img src="${OracleResponsys_url}OrderID=${onlineOrderNumber}&OrderTotal=<fmt:formatNumber pattern="0.00" value="${orderAmount}"/>&NumItems=${cartItemCount}&Cust_ID=${profileID}&Type=purchase&Concept=${currentSiteId} " WIDTH="1" HEIGHT="1">
</dsp:page>
