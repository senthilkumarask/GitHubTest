<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/atg/commerce/ShoppingCart" />
	<dsp:importbean bean="/atg/userprofiling/ProfileFormHandler" />
	<dsp:importbean bean="/atg/userprofiling/Profile" />
	<dsp:importbean bean="/com/bbb/commerce/order/purchase/CheckoutProgressStates"/>

	<%-- Variables --%>
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
	<c:set var="pageWrapper" value="login myAccount useFB" scope="request" />
	<c:set var="pageNameFB" value="guestLogin" scope="request" />
	<dsp:getvalueof var="transientUser" bean="Profile.transient"/>
	
	<c:choose>
		<c:when test="${transientUser}">
			<bbb:pageContainer index="false" follow="false">
				<jsp:attribute name="pageWrapper">${pageWrapper}</jsp:attribute>
				<jsp:attribute name="section">accounts</jsp:attribute>
				<jsp:attribute name="PageType">Login</jsp:attribute>
				<jsp:body>

					<dsp:getvalueof var="shippingGr" param="shippingGr"/>
					<c:if test="${not empty shippingGr}">
						<c:set var="URL" value="${pageContext.request.contextPath}/checkout/checkoutType.jsp?shippingGr=multi"></c:set>
					</c:if>
					<c:if test="${empty shippingGr}">
						<c:set var="URL" value="${pageContext.request.contextPath}/checkout/checkoutType.jsp"></c:set>
					</c:if>
					<dsp:setvalue bean="CheckoutProgressStates.currentLevel" value="GUEST"/>
					<c:set var="language" value="${pageContext.request.locale.language}" scope="page"/>
					<c:set var="lbl_checkout_checkoutasguest">
						<bbbl:label key="lbl_checkout_checkoutasguest" language="${language}"></bbbl:label>
					</c:set>

					<div class="row guest-login" id="content">
						<div class="small-12 large-offset-1 large-10 columns">
							<h1><bbbl:label key="lbl_checkout_welcome" language="<c:out param='${language}'/>"/></h1>
						</div>
						<div class="small-12 large-6 columns">
							<div class="row">
								<div class="small-12 large-offset-2 large-9 columns">
									<h2 class="divider"><bbbl:label key="lbl_checkout_newcustomers" language="<c:out param='${language}'/>"/></h2>
									<h3>${lbl_checkout_checkoutasguest}</h3>
									<p><bbbl:label key="lbl_checkout_guestcheckouthint" language="<c:out param='${language}'/>"/></p>
									<div class="row">
										<div class="small-12 columns">
											<form method="post" id="guestCheckoutForm" action="${URL}">
												<input type="hidden" name="guestCheckout" id="guestCheckout" value="1"/>
												<c:choose>
													<c:when test="${GoogleAnalyticsOn}">
														<input type="submit" value="${lbl_checkout_checkoutasguest}" class="small button service right" disabled="true" name="CheckoutBtn" id="CheckoutBtn" role="button" aria-pressed="false" aria-labelledby="CheckoutBtn" onclick="_gaq.push(['_trackEvent', 'checkout', 'click', 'Begin Checkout Process']);" />
													</c:when>
													<c:otherwise>
														<input type="submit" value="${lbl_checkout_checkoutasguest}" class="small button service right" disabled="true" name="CheckoutBtn" id="CheckoutBtn" role="button" aria-pressed="false" aria-labelledby="CheckoutBtn" />
													</c:otherwise>
												</c:choose>
											</form>
										</div>
									</div>
								</div>
							</div>
						</div>
						<dsp:include page="/account/frags/login_frag.jsp">
							<dsp:param name="checkoutFlag" value="1" />
							<dsp:param name="guestCheckoutFlag" value="true" />
						</dsp:include>
					</div>

				</jsp:body>
				<jsp:attribute name="footerContent">
					<c:set var="productIds" scope="request"/>
					<dsp:droplet name="/com/bbb/commerce/order/droplet/LTLCustomItemExclusionDroplet">
						<dsp:param name="order" bean="ShoppingCart.current"/>
						<dsp:oparam name="output">
							<dsp:getvalueof var="commerceItemList" param="commerceItemList" />
							<dsp:droplet name="/atg/dynamo/droplet/ForEach">
								<dsp:param name="array" param = "commerceItemList" />
								<dsp:oparam name="output">
									<dsp:getvalueof var="productId" param="element" />
									<dsp:getvalueof var="count" param="count"/>
									<dsp:getvalueof var="size" param="size"/>
									<c:choose>
										<c:when test="${count eq size}">
											<c:set var="productIds" scope="request">
												${productIds};${productId}
											</c:set>
										</c:when>
										<c:otherwise>
											<c:set var="productIds" scope="request">
												${productIds};${productId},
											</c:set>
										</c:otherwise>
									</c:choose>
								</dsp:oparam>
							</dsp:droplet>
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

						$(document).ready(function($) {
							var associate1 = "${sessionScope.associate1}";
			                if (associate1 == null || associate1 == "") {
			                	$('#associateLoginModalCheckout').foundation('reveal', 'open');
			                    var multi_ship = $('#multi_ship').is(':checked');
			                    $("#multi_ship_popup").attr("value", multi_ship);
			                    var express_checkout = $('#express_checkout').is(':checked');
			                    $("#express_checkout_popup").attr("value", express_checkout);
			                } else {
			                    var multi_ship = $('#multi_ship').is(':checked');
			                    $("#multi_ship_popup").attr("value", multi_ship);
			                    var express_checkout = $('#express_checkout').is(':checked');
			                    $("#express_checkout_popup").attr("value", express_checkout);
			                   loginModalForAssociate();            
			              	}
			              	$("#CheckoutBtn").removeAttr("disabled");
			              	function loginModalForAssociate() {
				              	$('#login').ajaxForm({
				                    url: '/tbs/account/idm/idm_login_checkout.jsp',
				                    type: 'POST',
				                    success:  function(data) {
				                        var jsonObj = JSON.parse(data);
				                        if(jsonObj.error == true){
				                            var errors = jsonObj.errors;
				                            var errHtml = "<ul>";
				                            for (i = 0; i < errors.length; i++) {
				                                errHtml += "<li>" +errors[i];
				                            }
				                            errHtml += "</ul>";
				                            if(jsonObj.idmError == true) {
				                                $('#idmErrors').html(errHtml);
				                            } else {
				                                $('#associateLoginModalCheckout').foundation('reveal', 'close');
				                                $('.error').html(errHtml);
				                            }
				                        } else {
				                            var successURL = jsonObj.successURL;
				                            $('#associateLoginModalCheckout').foundation('reveal', 'close');
				                            if(checkOutWithPayPal) {
				                                location.href = $(".paypalCheckoutContainer").find('a').attr('href');
				                            } else {
				                                location.href = successURL;
				                            }
				                            
				                        }
				                    }
				                });
				            }
				    	});




					</script>
				</jsp:attribute>
			</bbb:pageContainer>
		</c:when>
		<c:otherwise>
			<dsp:include page="/checkout/checkoutType.jsp">
				<dsp:param name="shippingGr" param="shippingGr"/>
			</dsp:include>
		</c:otherwise>
	</c:choose>
	<dsp:include page="/account/idm/idm_login_checkout.jsp" />
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
