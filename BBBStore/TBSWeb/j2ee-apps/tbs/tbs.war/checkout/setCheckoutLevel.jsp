<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/com/bbb/commerce/order/purchase/CheckoutProgressStates"/>
	<dsp:importbean bean="/com/bbb/commerce/order/paypal/PayPalSessionBean"/>
	<dsp:importbean bean="/atg/commerce/ShoppingCart" />

	<%-- Variables --%>
<%-- 	<dsp:getvalueof id ="step" param="step" /> --%>
	<dsp:getvalueof id ="payPalOrder" bean="ShoppingCart.current.payPalOrder" />
	<c:if test="${payPalOrder eq true && isFromPreview eq true}">
		<dsp:setvalue value="true" bean="PayPalSessionBean.fromPayPalPreview"/>
	</c:if>
	
	<c:if test="${payPalOrder eq true && addcheck eq false}">
		<dsp:setvalue value="true" bean="PayPalSessionBean.fromPayPalPreview"/>
	</c:if>
	

	<c:choose>
		<c:when test="${not empty param.step}">
			<dsp:setvalue bean="CheckoutProgressStates.currentLevel" value="${param.step}"/>
			true
		</c:when>
		<c:otherwise>
			false
		</c:otherwise>
	</c:choose>

</dsp:page>
