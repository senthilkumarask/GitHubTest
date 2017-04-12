<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/atg/commerce/ShoppingCart" />
	<dsp:importbean bean="/atg/userprofiling/ProfileFormHandler" />
	<dsp:importbean bean="/com/bbb/commerce/cart/BBBCartItemCountDroplet" />
	<dsp:importbean bean="/atg/dynamo/droplet/CurrencyFormatter"/>
	<%-- Variables --%>
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
	<dsp:getvalueof var="shippingComplete" param="shippingComplete"/>
	<dsp:getvalueof var="ordertype" param="ordertype"/>
	
    <c:set var="TBS_BedBathUSSite">
        <bbbc:config key="TBS_BedBathUSSiteCode" configName="ContentCatalogKeys" />
    </c:set>
    <c:set var="TBS_BuyBuyBabySite">
        <bbbc:config key="TBS_BuyBuyBabySiteCode" configName="ContentCatalogKeys" />
    </c:set>
    <c:set var="TBS_BedBathCanadaSite">
        <bbbc:config key="TBS_BedBathCanadaSiteCode" configName="ContentCatalogKeys" />
    </c:set>
    <dsp:getvalueof var="associateName" value="${sessionScope.associate1}"/>
	<header>
		<dsp:droplet name="BBBCartItemCountDroplet">
			<dsp:param name="shoppingCart" bean="ShoppingCart.current" />
			<dsp:oparam name="output">
				<dsp:getvalueof param="commerceItemCount" var="numItems" />
			</dsp:oparam>
		</dsp:droplet>

		<dsp:getvalueof var="tax" bean="ShoppingCart.current.priceInfo.tax" />
		<dsp:getvalueof bean="ShoppingCart.current.priceInfo.storeSubtotal" var="storeSubtotal"/>
		<dsp:getvalueof bean="ShoppingCart.current.priceInfo.total" var="total"/>
		<c:choose>
			<c:when test="${shippingComplete or tax gt 0.0 or ordertype eq 'paypal'}">
				<c:set var="shippingTitle" value="Shipping" scope="request" />
				<c:set var="totalTitle" value="Total" scope="request" />
				<c:set var="taxAmount" scope="request"><dsp:valueof bean="ShoppingCart.current.priceInfo.tax" converter="currency"/></c:set>
			</c:when>
			<c:otherwise>
				<c:set var="shippingTitle" value="Est. Shipping" scope="request" />
				<c:set var="totalTitle" value="Pre-tax Total" scope="request" />
				<c:set var="taxAmount" value="--" scope="request" />
			</c:otherwise>
		</c:choose>

		<nav class="tab-bar nav">
			<div class="row">
				<c:set var="associateID">${sessionScope.associate1.associateId}</c:set>

				<c:set var="associateID">${fn:substring(associateID,0,7)}</c:set>

			<div class="associateLogout show-for-large-up marLeft_10 <c:if test="${empty associateName}">hidden</c:if>"">
				<p>Hi ${associateID},</p>
				<dsp:form action="" method="post">
                    <dsp:input iclass="logOutAssociateLink no-padding" bean="ProfileFormHandler.clearCart" type="submit" value="Log Out"/>
                </dsp:form>
			</div>
				<div class="checkout-header-left">
					<div class="row checkout-header-left">
                        <c:choose>
                            <c:when test="${currentSiteId == TBS_BedBathCanadaSite}">
                                <a href="${contextPath}" class="home-btn ca"><span></span><div class="show-for-large-up">Continue Shopping</div></a>
                            </c:when>
                            <c:when test="${currentSiteId == TBS_BuyBuyBabySite}">
                                <a href="${contextPath}" class="home-btn baby"><span></span><div class="show-for-large-up">Continue Shopping</div></a>
                            </c:when>
                            <c:otherwise>
                                <a href="${contextPath}" class="home-btn"><span></span><div class="show-for-large-up">Continue Shopping</div></a>
                            </c:otherwise>
                        </c:choose>
                     
						
						<a href="${contextPath}/cart/cart.jsp" class="cart-btn <c:if test="${currentSiteId == TBS_BuyBuyBabySite}">baby</c:if>"><span class="cart-icon <c:if test="${currentSiteId == TBS_BuyBuyBabySite}">baby</c:if>"></span><div class="show-for-large-up">Back to Cart</div></a>
					</div>
				</div>
				<div class="checkout-header-right">
					<span class="price-co pre-tax-total">
						<h5><c:out value="${totalTitle}" /></h5>
						<h3 class="price">
							<strong>
							<c:choose>
								<c:when test="${total gt 0.0 && storeSubtotal gt 0.0}">
									<dsp:valueof value="${total}" converter="currency"/>
								</c:when>
								<c:when test="${total eq 0.0 && storeSubtotal gt 0.0}">
									<dsp:valueof value="${storeSubtotal}" converter="currency"/>
								</c:when>
								<c:otherwise>
									<dsp:valueof value="${total}" converter="currency"/>
								</c:otherwise>
							</c:choose>
						  	</strong>
						 </h3>
					</span>
				</div>
				<div class="checkout-header-middle">
					<div class="price-breakdown">
						<span class="operator equal">=</span>
						<span class="price-co">
							<h5>Tax</h5>
							<c:choose>
									<c:when test="${taxAmount eq '--'}">
									<h3 class="price"><c:out value="${taxAmount}" /></h3>
									</c:when>
									<c:otherwise>
										<h3 class="price">
											<dsp:droplet name="CurrencyFormatter"><dsp:param name="currency" bean="ShoppingCart.current.priceInfo.tax"/>
														    <dsp:param name="locale" bean="/OriginatingRequest.requestLocale.locale"/>
														    <dsp:oparam name="output">
															    <dsp:valueof param="formattedCurrency"/>
														    </dsp:oparam>
											</dsp:droplet>
										</h3>
							</c:otherwise>
						 </c:choose>
						</span>
						<span class="operator">+</span>
						<span class="price-co">
							<h5><c:out value="${shippingTitle}" /></h5>
							<h3 class="price"><dsp:droplet name="CurrencyFormatter"><dsp:param name="currency" bean="ShoppingCart.current.priceInfo.shipping"/>
														    <dsp:param name="locale" bean="/OriginatingRequest.requestLocale.locale"/>
														    <dsp:oparam name="output">
															    <dsp:valueof param="formattedCurrency"/>
														    </dsp:oparam>
												</dsp:droplet></h3>
							<%-- <h3 class="price"><dsp:valueof bean="ShoppingCart.current.priceInfo.shipping" converter="currency"/></h3> --%>
						</span>
						<span class="operator">+</span>
						<span class="price-co">
							<h5>Subtotal (<c:out value="${numItems}" />)</h5>
							<dsp:getvalueof bean="ShoppingCart.current.priceInfo.amount" var="amount"/>
							<h3 class="price">
								<c:choose>
									<c:when test="${amount gt 0.0 && storeSubtotal gt 0.0}">
										<dsp:droplet name="CurrencyFormatter"><dsp:param name="currency" value="${amount}"/>
										    <dsp:param name="locale" bean="/OriginatingRequest.requestLocale.locale"/>
											    <dsp:oparam name="output">
												    <dsp:valueof param="formattedCurrency"/>
											    </dsp:oparam>
										</dsp:droplet>
										<%-- <dsp:valueof value="${amount}" converter="currency"/> --%>
									</c:when>
									<c:when test="${amount eq 0.0 && storeSubtotal gt 0.0}">
										<dsp:droplet name="CurrencyFormatter"><dsp:param name="currency" value="${storeSubtotal}"/>
										    <dsp:param name="locale" bean="/OriginatingRequest.requestLocale.locale"/>
											    <dsp:oparam name="output">
												    <dsp:valueof param="formattedCurrency"/>
											    </dsp:oparam>
										</dsp:droplet>
										<%-- <dsp:valueof value="${storeSubtotal}" converter="currency"/> --%>
									</c:when>
									<c:otherwise>
										<dsp:droplet name="CurrencyFormatter"><dsp:param name="currency" value="${amount}"/>
										    <dsp:param name="locale" bean="/OriginatingRequest.requestLocale.locale"/>
											    <dsp:oparam name="output">
												    <dsp:valueof param="formattedCurrency"/>
											    </dsp:oparam>
										</dsp:droplet>
										<%-- <dsp:valueof value="${amount}" converter="currency"/> --%>
									</c:otherwise>
								</c:choose>
							</h3>
						</span>
					</div>
				</div>
			</div>
		</nav>
	</header>
	<div class="associateLogoutMobile <c:if test="${empty associateName}">hidden</c:if>">
		<p>Hi ${associateID},</p>
		<dsp:form action="" method="post">
            <dsp:input iclass="logOutAssociateLinkMobile no-padding" bean="ProfileFormHandler.clearCart" type="submit" value="Log Out"/>
        </dsp:form>
	</div>
</dsp:page>
