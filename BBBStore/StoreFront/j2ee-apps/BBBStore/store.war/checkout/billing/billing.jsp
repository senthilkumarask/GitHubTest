<dsp:page>
	<dsp:page>
        <dsp:importbean bean="/com/bbb/commerce/order/purchase/CheckoutProgressStates"/>
        <dsp:importbean bean="/com/bbb/commerce/order/paypal/PayPalSessionBean"/>
        <dsp:importbean bean="/atg/commerce/ShoppingCart" />
		<c:set var="firstVisit" scope="session">false</c:set>
        <dsp:getvalueof var="currentState" bean="CheckoutProgressStates.currentLevelAsInt"/>
        <dsp:getvalueof var="state" value="${50}"/>
        <dsp:getvalueof param="colg" var="colg" scope="request"/>
        <dsp:getvalueof id ="payPalOrder" bean="ShoppingCart.current.payPalOrder" />
		<dsp:getvalueof var="isFromPreview" param="isFromPreview"/>
		<dsp:getvalueof var="order" bean="/atg/commerce/ShoppingCart.current"/>
		<dsp:getvalueof var="commerceItemCount" value="${order.commerceItemCount}"/>
		<c:if test = "${payPalOrder eq true && isFromPreview eq true}">
    		<dsp:setvalue value="true" bean = "PayPalSessionBean.fromPayPalPreview"/>
    	</c:if>
    
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
                <dsp:setvalue bean="CheckoutProgressStates.currentLevel" value="BILLING"/>
            </c:otherwise>
        </c:choose>
        <c:choose>
        	<c:when test="${commerceItemCount gt 0}">
        		<dsp:include page="/checkout/billing/billingForm.jsp" />
        	</c:when>
        	<c:otherwise>
        		<dsp:droplet name="/atg/dynamo/droplet/Redirect">
					<dsp:param name="url" value="/cart/cart.jsp" />
				</dsp:droplet>
        	</c:otherwise>
        </c:choose>
        
 <%--R2.2 SCOPE #158 START deviceFingerprint JS call to cybersource--%>
 
        <c:if test="${deviceFingerprintOn}">  
		<c:set var="merchandId"><bbbc:config key="DF_merchandId" configName="ThirdPartyURLs"/></c:set>
		<c:set var="orgId"><bbbc:config key="DF_orgId" configName="ThirdPartyURLs"/></c:set>
		<c:set var="jSessionId">${pageContext.session.id}</c:set>
		<c:set var="jSession_id" value="${fn:split(jSessionId, '!')}" />
		<c:set var="org_jsessionid">${jSession_id[0]}</c:set>
        <div style="position:absolute; top:-1000px; overflow:hidden; height:1px; width:1px;">
		<div style="background:url(<bbbc:config key="deviceFinger_image_url" configName="ThirdPartyURLs"/>?org_id=${orgId}&session_id=${merchandId}${org_jsessionid}&m=1)"></div> 
		<img src="<bbbc:config key="deviceFinger_image_url" configName="ThirdPartyURLs"/>?org_id=${orgId}&session_id=${merchandId}${org_jsessionid}&m=2" alt="device finger-print pixel" /> 
		<object type="application/x-shockwave-flash" data="<bbbc:config key="deviceFinger_flasf_url" configName="ThirdPartyURLs"/>?org_id=${orgId}&session_id=${merchandId}${org_jsessionid}" width="1" height="1"> 
			<param name="movie" value="<bbbc:config key="deviceFinger_flasf_url" configName="ThirdPartyURLs"/>?org_id=${orgId}&session_id=${merchandId}${org_jsessionid}" /> 
			<param name="wmode" value="transparent" /> 
		<div></div>
		</object>
		</div>
		<script src="<bbbc:config key="deviceFinger_js" configName="ThirdPartyURLs"/>?org_id=${orgId}&session_id=${merchandId}${org_jsessionid}"></script> 
	</c:if>
<%--R2.2 SCOPE #158 START deviceFingerprint JS call to cybersource--%>
		</dsp:page>
</dsp:page>