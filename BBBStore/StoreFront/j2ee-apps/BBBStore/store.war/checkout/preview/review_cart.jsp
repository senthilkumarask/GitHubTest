<dsp:page>
	<dsp:importbean bean="/atg/commerce/ShoppingCart" />
	<dsp:importbean bean="/atg/commerce/order/purchase/CommitOrderFormHandler"/>
	<dsp:importbean bean="/com/bbb/commerce/order/paypal/PayPalSessionBean"/>
    <dsp:importbean bean="/com/bbb/commerce/order/purchase/CheckoutProgressStates"/>
    <dsp:importbean bean="/atg/commerce/order/purchase/CartModifierFormHandler"/>
    <dsp:getvalueof var="currentState" bean="CheckoutProgressStates.currentLevelAsInt"/>
    <dsp:getvalueof id ="payPalOrder" bean="ShoppingCart.current.payPalOrder" />
    
    <dsp:getvalueof var="state" value="${80}"/>
	<dsp:importbean bean="/atg/commerce/order/droplet/BBBOrderRKGInfo" />
    <c:set var="productIds" scope="request"/>
	<dsp:droplet name="/com/bbb/commerce/order/droplet/LTLCustomItemExclusionDroplet">
		<dsp:param name="order" bean="ShoppingCart.current"/>
		 <dsp:oparam name="output">
			<dsp:getvalueof var="productIds" param="commerceItemList" />
        </dsp:oparam>     
    </dsp:droplet>
    <dsp:getvalueof var="url" bean="CheckoutProgressStates.failureURL"/>
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
	<dsp:getvalueof var="failureURL" value="${contextPath}${url}"/>
    <c:choose>
        <c:when test="${currentState lt state or couponMap}">
            <dsp:droplet name="/atg/dynamo/droplet/Redirect">
                <dsp:param name="url" value="${failureURL}"/>
            </dsp:droplet>
        </c:when>
        <c:otherwise>
            <dsp:setvalue bean="CheckoutProgressStates.currentLevel" value="REVIEW"/>
        </c:otherwise>
    </c:choose>
    <dsp:getvalueof id="token" param="token"/>
    <c:if test = "${(payPalOrder eq true) || not empty token}"> 
	    <dsp:droplet name="/com/bbb/commerce/droplet/PaypalDroplet">
				<dsp:param name="order" bean="ShoppingCart.current"/>
				<dsp:param name="PayPalSessionBean" bean="PayPalSessionBean"/>
				<dsp:param name="validateShippingMethod" value="true"/>
				<dsp:param name="isfromReviewPorchPage" value="true"/>
				<dsp:oparam name="output">
					<dsp:getvalueof var="isShippingMethodChanged" param="isShippingMethodChanged" />
				</dsp:oparam>
				<dsp:oparam name="error">
					<dsp:getvalueof param="redirectUrl" var="redirectUrl" />
					<c:if test="${not empty redirectUrl}">
						<dsp:droplet name="/atg/dynamo/droplet/Redirect">
							<dsp:param name="url" value="${redirectUrl}?payPalError=true" />
						</dsp:droplet>
					</c:if>
				</dsp:oparam>
				<dsp:oparam name="setExpressOutput">
					<dsp:getvalueof param="fromCart" var="fromCart" />
					<dsp:getvalueof param="payPalErrorUrl" var="payPalErrorUrl" />
					<dsp:setvalue bean="CartModifierFormHandler.fromCart" value="${fromCart}" />
					<dsp:setvalue bean="CartModifierFormHandler.payPalErrorURL" value="${contextPath}${payPalErrorUrl}" />
					<dsp:setvalue bean="CartModifierFormHandler.checkoutWithPaypal" />	
				</dsp:oparam>
		</dsp:droplet>
	</c:if>
	<bbb:pageContainer index="false" follow="false">
	<jsp:attribute name="PageType">Preview</jsp:attribute>
		<jsp:attribute name="headerRenderer">
	      <dsp:include page="/checkout/checkout_header.jsp" flush="true">
	      	<dsp:param name="pageId" value="7"/>
	        <dsp:param name="step" value="preview"/>
	      </dsp:include>
	    </jsp:attribute>

	     <%--check porch config values if we need to use it --%>
        <c:set var="usePorch" value="usePorch" />

		<jsp:attribute name="pageWrapper">billing chekoutReview findStore usePorch</jsp:attribute>
		<jsp:attribute name="section">checkout</jsp:attribute>
		<jsp:body>
		<dsp:getvalueof id="currentSiteId" bean="/atg/multisite/Site.id" />
			<c:set var="language" value="${pageContext.request.locale.language}" scope="request"/>
			<div class="container_12 clearfix" id="content" role="main">
				<div class="clearfix">
					<dsp:include page="/global/gadgets/errorMessage.jsp">
             			<dsp:param name="formhandler" bean="CommitOrderFormHandler"/>
           			</dsp:include>
					<dsp:include page="/checkout/preview/frag/checkout_review_frag.jsp" flush="true">
						<dsp:param name="order" bean="ShoppingCart.current"/>
						<dsp:param name="hideOrderNumber" value="true"/>
						<dsp:param name="showLinks" value="1"/>
						<dsp:param name="displayTax" value="true"/>
						<dsp:param value="${isShippingMethodChanged}" name="isShippingMethodChanged" />
						<dsp:param name="isFromPreviewPage" value="true"/>
					</dsp:include>
					<dsp:include page="/checkout/preview/frag/checkout_review_right_frag.jsp" >
					</dsp:include>
				</div>
			</div>		
			<%-- Commenting the view directions from review cart page 
			<c:import url="/selfservice/store/find_store_pdp.jsp" ></c:import>
			<dsp:include src="${contextPath}/selfservice/store/p2p_directions_input.jsp" />
			--%>
		</jsp:body>
		<jsp:attribute name="footerContent">

			<script type="text/javascript">
			           if(typeof s !=='undefined') {
			        	s.pageName='Check Out>Preview';
			        	s.channel='Check Out';
			        	s.prop1='Check Out';
			        	s.prop2='Check Out';
			        	s.prop3='Check Out';
			        	s.prop6='${pageContext.request.serverName}'; 
						s.eVar9='${pageContext.request.serverName}';
						s.events = "event12";
						s.products = '${productIds}';
						var s_code=s.t();
			            if(s_code)document.write(s_code);           
			           }
			</script>
		</jsp:attribute>
	</bbb:pageContainer>	
</dsp:page>