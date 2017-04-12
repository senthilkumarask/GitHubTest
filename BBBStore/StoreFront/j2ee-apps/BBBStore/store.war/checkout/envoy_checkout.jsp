<dsp:page>
	<dsp:importbean bean="/atg/multisite/Site"/>
	<dsp:getvalueof id="currentSiteId" bean="Site.id" scope="request"/>
	<bbb:pageContainer index="false" follow="false" >
	    <jsp:attribute name="headerRenderer">
	         <div id="header" class="container_12 clearfix">
				<dsp:include page="checkoutLogo.jsp" >
				 <dsp:param name="currentSiteId" value="${currentSiteId}"/>
				</dsp:include>
				<div class="backToCart fl">
					<dsp:a page="/cart/cart.jsp" iclass="returnToCart fl"><bbbl:label key="lbl_shipping_return_cart" language="${pageContext.request.locale.language}" />
						<dsp:param name="envoy2cart" value="true"/>
					</dsp:a>   
				</div>
			</div>
	    </jsp:attribute>
	    <jsp:attribute name="section">checkout</jsp:attribute>
	    <jsp:attribute name="pageWrapper">envoy envoyWrapper envoyCheckoutPage</jsp:attribute>
	    <jsp:attribute name="PageType">EnvoyCheckout</jsp:attribute>
	    <jsp:body>
	    	<c:set var="international_checkout_merchant_js"><bbbc:config key="international_checkout_merchant_js" configName="International_Shipping" /></c:set>
			<c:set var="international_checkout_empty_js"><bbbc:config key="international_checkout_empty_js" configName="International_Shipping" /></c:set>
			
			
			<script type="text/javascript" src="${international_checkout_merchant_js}"></script>
			<div id="content" class="container_12 clearfix" role="main">
				<iframe id="envoyId" name="envoy" src="${envoyURL}" height="450" frameBorder="0" scrolling="no" title="Border Free Envoy Checkout Form" ></iframe>
				<iframe id="__frame" width="0" height="0" frameborder="0" src="${international_checkout_empty_js}" ></iframe>
			</div>
		
        
		</jsp:body>
		 <jsp:attribute name="footerContent">

					 <script type="text/javascript">
							if (typeof s !== 'undefined') {
								s.pageName = 'Check Out>International checkout page';
								s.channel = 'Check Out';
		                        s.prop1 = 'Check Out';
		                        s.prop2 = 'Check Out';
		                        s.prop3 = 'Check Out';
		                        s.prop16 = "non registered user";
		                        s.prop10 ="Check Out>International checkout page";
		                        s.eVar16 = "non registered user";
								var s_code = s.t();
								if (s_code)
									document.write(s_code);
							}
						</script>
			   </jsp:attribute>
            
     </bbb:pageContainer> 
</dsp:page>


