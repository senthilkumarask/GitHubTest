<dsp:page>
	<dsp:droplet name="/com/bbb/commerce/shipping/droplet/ShippingMethodDescriptionDroplet">
        <dsp:param name="order" bean="/atg/commerce/ShoppingCart.current"/>
        <dsp:oparam name="output">
			<jsp:useBean id="customKeys" class="java.util.HashMap" scope="page"/>
			<dsp:getvalueof param="shippingMethodDescription" var="description"/>
			<c:set target="${customKeys}" property="shippingMethodName">${description}</c:set>
			<bbbl:label key="txt_shipping_method_with_braces" language ="${pageContext.request.locale.language}" placeHolderMap="${customKeys}"/>
        </dsp:oparam>
    </dsp:droplet>
</dsp:page>