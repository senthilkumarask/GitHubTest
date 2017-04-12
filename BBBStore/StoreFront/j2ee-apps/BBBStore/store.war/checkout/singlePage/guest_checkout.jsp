<dsp:page>
	<dsp:importbean bean="/atg/commerce/ShoppingCart" />
    <dsp:importbean bean="/atg/userprofiling/ProfileFormHandler" />
    <dsp:importbean bean="/atg/userprofiling/Profile" />
    <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
    <c:set var="pageWrapper" value="login myAccount useFB" scope="request" />
    <c:set var="pageNameFB" value="guestLogin" scope="request" />
    <dsp:getvalueof var="transientUser" bean="Profile.transient"/>
    <dsp:importbean bean="/com/bbb/commerce/order/purchase/CheckoutProgressStates"/>
    
    
    <c:choose>
        <c:when test="${transientUser}">
            <bbb:pageContainer index="false" follow="false">
           		<jsp:attribute name="pageWrapper">${pageWrapper}</jsp:attribute>
           		<jsp:attribute name="section">accounts</jsp:attribute>
           		<jsp:attribute name="PageType">Login</jsp:attribute>
					<jsp:body>
					
                    <dsp:setvalue bean="CheckoutProgressStates.currentLevel" value="SP_GUEST"/>
					<c:set var="language" value="${pageContext.request.locale.language}" scope="page"/>
					<c:set var="lbl_checkout_checkoutasguest">
						<bbbl:label key="lbl_spc_checkout_checkoutasguest" language="${language}"></bbbl:label>
					</c:set>
	          		<div class="container_12 clearfix" id="content" role="main">
						<div class="grid_12">
							<h1><bbbl:label key="lbl_spc_checkout_welcome" language="<c:out param='${language}'/>"/></h1>
						</div>
						<div class="grid_9">
							<p class="error cb fcConnectErrorMsg hidden"></p>
							<div class="grid_4 alpha" id="newCustomer">
								<h3><bbbl:label key="lbl_spc_checkout_newcustomers" language="<c:out param='${language}'/>"/></h3>
								<p class="bold">${lbl_checkout_checkoutasguest}</p>
								<p><bbbl:label key="lbl_spc_checkout_guestcheckouthint" language="<c:out param='${language}'/>"/></p>
								<div class="button button_active">
									<form method="post" id="guestCheckoutForm" action="${pageContext.request.contextPath}/checkout/shipping/shipping.jsp">
                                        <input type="hidden" name="guestCheckout" id="guestCheckout" value="1"/>
										<c:choose>
											<c:when test="${GoogleAnalyticsOn}">
												<input type="submit" onclick="_gaq.push(['_trackEvent', 'checkout', 'click', 'Begin Checkout Process']);" value="${lbl_checkout_checkoutasguest}" name="" id="CheckoutBtn" role="button" aria-pressed="false" />
											</c:when>
											<c:otherwise>
												<input type="submit" value="${lbl_checkout_checkoutasguest}" name="" id="CheckoutBtn" role="button" aria-pressed="false" aria-labelledby="CheckoutBtn" />
											</c:otherwise>
										</c:choose>
									</form>
								</div>
							</div>
							<dsp:include page="/account/frags/login_frag.jsp">
								<dsp:param name="checkoutFlag" value="1" />
							</dsp:include>
						</div>
						<div class="grid_3">
							<dsp:include page="/common/click2chatlink.jsp">
                                              <dsp:param name="pageId" value="4"/>
                             </dsp:include>
							<div class="teaser_229 benefitsAccountTeaser">
								<bbbt:textArea key="txt_spc_login_benefits_account" language="<c:out param='${language}'/>"/>
							</div>
						</div>
				    </div>				
	 			</jsp:body>
				
			
				<jsp:attribute name="footerContent">

				<c:set var="productIds" scope="request"/>
				<dsp:droplet name="/com/bbb/commerce/order/droplet/LTLCustomItemExclusionDroplet">
					<dsp:param name="order" bean="ShoppingCart.current"/>
					 <dsp:oparam name="output">
						<dsp:getvalueof var="productIds" param="commerceItemList" />
			        </dsp:oparam>     
			    </dsp:droplet>

					 <script type="text/javascript">
			          
							if (typeof s !== 'undefined') {
								s.pageName = 'My Account>Log In';
								s.channel = 'My Account';
								s.prop1 = 'My Account';
								s.prop2 = 'My Account';
								s.prop3 = 'My Account';
								s.events = "event7";
								s.products = '${productIds}';
								var s_code = s.t();
								if (s_code)
									document.write(s_code);
							}
						</script>
			   </jsp:attribute>

            </bbb:pageContainer>
        </c:when>
        <c:otherwise>
            <dsp:include page="/checkout/shipping/shipping.jsp"/>
        </c:otherwise>
    </c:choose>
    
 	<dsp:droplet name="/atg/commerce/order/droplet/BBBOrderInfoDroplet">
	       <dsp:param name="order" bean="ShoppingCart.current" />
	        <dsp:oparam name="output">
	        <c:set var="omni" value=";"/>
	        	<dsp:getvalueof id="items" param="itemIds"/>
	        	<c:forTokens items="${items}" delims=";" var="prod">
	        		<c:set var="products" value="${products}${omni}${prod}"/>
	        		<c:set var="omni" value=",;"/>
		        </c:forTokens>
	        </dsp:oparam>
	 </dsp:droplet>     
	
</dsp:page>