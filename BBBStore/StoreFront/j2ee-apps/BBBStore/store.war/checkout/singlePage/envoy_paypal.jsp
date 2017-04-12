<dsp:page>
	<bbb:pageContainer index="false" follow="false" >
	    <jsp:attribute name="headerRenderer">
	         <div id="header" class="container_12 clearfix">
				<dsp:include page="checkoutLogo.jsp" />
				<div class="backToCart fl">
					<dsp:a page="/cart/cart.jsp" iclass="returnToCart fl"><bbbl:label key="lbl_spc_shipping_return_cart" language="${pageContext.request.locale.language}" /></dsp:a>   
				</div>
			</div>
	    </jsp:attribute>
	    <jsp:attribute name="section">checkout</jsp:attribute>
	    <jsp:attribute name="pageWrapper">envoy envoyWrapper envoyCheckoutPage</jsp:attribute>
	    <jsp:attribute name="PageType">EnvoyCheckout</jsp:attribute>
	    <jsp:body>
			<c:set var="ppStatus" value="${param.ppStatus}" />
			<c:set var="token" value="${param.token}" />
			<c:set var="international_checkout_merchant_js"><bbbc:config key="international_checkout_merchant_js" configName="International_Shipping" /></c:set>
			<c:set var="international_checkout_paypal"><bbbc:config key="international_checkout_paypal" configName="International_Shipping" /></c:set>
			<c:set var="paypalurl" value="${international_checkout_paypal}?ppStatus=${ppStatus}&token=${token}" />
			<c:set var="international_checkout_empty_js"><bbbc:config key="international_checkout_empty_js" configName="International_Shipping" /></c:set>
			<script type="text/javascript" src="${international_checkout_merchant_js}"></script>
			<div id="content" class="container_12 clearfix" role="main">
				<iframe name="envoy" id="envoyId" 
				src="${paypalurl}" frameborder="0" border="0" scrolling="no" style="width: 100%;"></iframe>
				<iframe id="__frame" width="0" height="0" frameborder="0" src="${international_checkout_empty_js}" ></iframe>
			</div>
		</jsp:body>
  </bbb:pageContainer>  
</dsp:page>


