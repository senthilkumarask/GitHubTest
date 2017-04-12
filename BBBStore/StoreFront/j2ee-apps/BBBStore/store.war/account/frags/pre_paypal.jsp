<dsp:page>
	<dsp:importbean bean="/atg/commerce/order/purchase/CartModifierFormHandler"/>
	<dsp:form formid="guestCheckoutPaypalForm" name="guestCheckoutPaypalForm" method="post">
		<dsp:setvalue bean="CartModifierFormHandler.fromCart" value="${true}" />
		<dsp:setvalue bean="CartModifierFormHandler.payPalErrorURL" value="/cart/cart.jsp" />
		<dsp:setvalue bean="CartModifierFormHandler.checkoutWithPaypal" value="true" />
	</dsp:form>
</dsp:page>