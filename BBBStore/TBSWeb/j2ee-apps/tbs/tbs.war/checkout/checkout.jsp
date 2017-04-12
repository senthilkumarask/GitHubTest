<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/atg/userprofiling/Profile" />

	<%-- Variables --%>
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
	<dsp:getvalueof var="transientUser" bean="Profile.transient"/>

	<c:choose>
		<c:when test="${transientUser}">
			<bbb:pageContainer index="false" follow="false">
				<jsp:attribute name="pageWrapper">login myAccount</jsp:attribute>
				<jsp:attribute name="section">accounts</jsp:attribute>
				<jsp:body>
					<c:set var="language" value="${pageContext.request.locale.language}" scope="page"/>
					<div class="row guest-login">
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
										<div class="small-12 large-offset-6 large-6 columns">
											<form method="post" id="guestCheckoutForm" action="${URL}">
												<input type="hidden" name="guestCheckout" id="guestCheckout" value="1"/>
												<c:choose>
													<c:when test="${GoogleAnalyticsOn}">
														<input type="submit" value="${lbl_checkout_checkoutasguest}" class="small button service expand" name="CheckoutBtn" id="CheckoutBtn" role="button" aria-pressed="false" aria-labelledby="CheckoutBtn" onclick="_gaq.push(['_trackEvent', 'checkout', 'click', 'Begin Checkout Process']);" />
													</c:when>
													<c:otherwise>
														<input type="submit" value="${lbl_checkout_checkoutasguest}" class="small button service expand" name="CheckoutBtn" id="CheckoutBtn" role="button" aria-pressed="false" aria-labelledby="CheckoutBtn" />
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
			</bbb:pageContainer>
		</c:when>
		<c:otherwise>
			<dsp:include page="/checkout/checkoutType.jsp"/>
		</c:otherwise>
	</c:choose>

</dsp:page>
