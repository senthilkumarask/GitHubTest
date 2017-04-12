<dsp:page>
    <dsp:importbean bean="/atg/userprofiling/ProfileFormHandler" />
    <dsp:importbean bean="/atg/userprofiling/Profile" />
    <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
    
    <dsp:getvalueof var="transientUser" bean="Profile.transient"/>
    <c:choose>
        <c:when test="${transientUser}">
            <bbb:pageContainer index="false" follow="false">
           		<jsp:attribute name="pageWrapper">login myAccount</jsp:attribute>
           		<jsp:attribute name="section">accounts</jsp:attribute>
					<jsp:body>
					<c:set var="language" value="${pageContext.request.locale.language}" scope="page"/>
	          		<div class="container_12 clearfix" id="content" role="main">
						<div class="grid_12">
							<h1><bbbl:label key="lbl_checkout_welcome" language="<c:out param='${language}'/>"/></h1>
						</div>
						<div class="grid_9">
							<div class="grid_4 alpha" id="newCustomer">
								<h3><bbbl:label key="lbl_checkout_newcustomers" language="<c:out param='${language}'/>"/></h3>
								<p class="bold"><bbbl:label key="lbl_checkout_checkoutasguest" language="<c:out param='${language}'/>"/></p>
								<p><bbbl:label key="lbl_checkout_guestcheckouthint" language="<c:out param='${language}'/>"/></p>
								<div class="button button_active">
									<form id="guestCheckoutForm" action="${pageContext.request.contextPath}/checkout/shipping/shipping.jsp">
										<input type="submit" value="<bbbl:label key="lbl_checkout_checkoutasguest" language="<c:out param='${language}'/>"/>" name="" id="CheckoutBtn" role="button" aria-pressed="false">
									</form>
								</div>
							</div>
							<dsp:include page="/account/frags/login_frag.jsp">
								<dsp:param name="pageNameFB" value="login"/>
								<dsp:param name="checkoutFlag" value="1" />
							</dsp:include>
						</div>
						<div class="grid_3">
							<div class="teaser_229 benefitsAccountTeaser">
								<bbbt:textArea key="txt_login_benefits_account" language="<c:out param='${language}'/>"/>
							</div>
						</div>
				    </div>
	 			</jsp:body>
            </bbb:pageContainer>
        </c:when>
        <c:otherwise>
            <dsp:include page="/checkout/shipping/shipping.jsp"/>
        </c:otherwise>
    </c:choose>
</dsp:page>