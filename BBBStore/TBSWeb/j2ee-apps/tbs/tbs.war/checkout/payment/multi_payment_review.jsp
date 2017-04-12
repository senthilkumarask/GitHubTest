<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
	<dsp:importbean bean="/atg/commerce/order/purchase/CommitOrderFormHandler"/>
	<dsp:importbean bean="/atg/commerce/ShoppingCart" />
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
	<dsp:importbean bean="/com/bbb/commerce/checkout/droplet/BBBSkuPropDetailsDroplet" />
	<dsp:importbean bean="/com/bbb/commerce/order/droplet/PromotionExclusionMsgDroplet" />
	<dsp:importbean bean="/com/bbb/commerce/order/paypal/PayPalSessionBean"/>
	<dsp:importbean bean="/com/bbb/commerce/order/droplet/BBBPackNHoldDroplet"/>
	<dsp:importbean bean="/com/bbb/profile/session/SessionBean" />
	<dsp:importbean bean="/atg/multisite/Site" />
	<dsp:importbean bean="/com/bbb/commerce/checkout/droplet/BBBBeddingKitsAddrDroplet"/>
	<dsp:importbean bean="/com/bbb/commerce/cart/droplet/CartRegistryInfoDroplet"/>
	<dsp:importbean bean="/atg/commerce/order/purchase/PaymentGroupFormHandler"/>
	<dsp:importbean bean="/com/bbb/payment/droplet/BBBPaymentGroupDroplet"/>

	<%-- Variables --%>
	<dsp:getvalueof param="order" var="order"/>
	<c:if test="${empty order}">
		<dsp:setvalue beanvalue="ShoppingCart.current" param="order"/>
	</c:if>
	
	<dsp:getvalueof param="isConfirmation" var="isConfirmation" />
	<dsp:getvalueof param="isMultiShip" var="isMultiShip" />
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
	<dsp:getvalueof var="sessionBabyCA" bean="SessionBean.babyCA" />
	<dsp:getvalueof id="currentSiteId" bean="Site.id" />
	<c:set var="internationalCCFlag" value="${sessionScope.internationalCreditCard}"/>
	<dsp:droplet name="BBBPackNHoldDroplet">
		<dsp:param name="order" param="order"/>
		<dsp:oparam name="output">
			<dsp:getvalueof param="isPackHold" var="isPackHold"/>
		</dsp:oparam>
	</dsp:droplet>

	<c:if test="${isPackHold eq true}">
		<dsp:droplet name="BBBBeddingKitsAddrDroplet">
			<dsp:param name="order" param="order"/>
			<dsp:param name="shippingGroup" param="order.shippingGroups"/>
			<dsp:param name="isPackHold" value="${true}"/>
			<dsp:oparam name="beddingKit">
				<dsp:getvalueof var="beddingShipAddrVO" param="beddingShipAddrVO" />
				<c:set var="beddingKit" value="true"/>
			</dsp:oparam>
			<dsp:oparam name="weblinkOrder">
				<dsp:getvalueof var="beddingShipAddrVO" param="beddingShipAddrVO" />
				<c:set var="weblinkOrder" value="true"/>
			</dsp:oparam>
			<dsp:oparam name="notBeddingKit">
				<c:set var="beddingKit" value="false"/>
			</dsp:oparam>
		</dsp:droplet>
	</c:if>
	<c:if test="${beddingShipAddrVO != null && isPackHold eq true }">
		<dsp:getvalueof var="collegeName" value="${beddingShipAddrVO.collegeName}"/>
	</c:if>
	<c:set var="isPayPal" value="${false}"/>
	<dsp:getvalueof var="isShippingMethodChanged" param="isShippingMethodChanged" />
	<dsp:getvalueof var="sessionBeanNull" bean = "PayPalSessionBean.sessionBeanNull"/>
	<dsp:setvalue value="false" bean = "PayPalSessionBean.fromPayPalPreview"/>
	<dsp:droplet name="PromotionExclusionMsgDroplet">
		<dsp:param name="order" bean="ShoppingCart.current"/>
		<dsp:oparam name="output">
			<dsp:getvalueof var="promoExclusionMap" vartype=" java.util.Map" param="promoExclusionMap" scope="request"/>
		</dsp:oparam>
	</dsp:droplet>
	<dsp:getvalueof var="isFromOrderDetail" param="isFromOrderDetail"/>
	<dsp:getvalueof var="orderDate" param="orderDate"/>
	<c:if test="${isFromOrderDetail}">
		<dsp:droplet name="CartRegistryInfoDroplet">
			<dsp:param name="order" param="order"/>
		</dsp:droplet>
	</c:if>
	<dsp:getvalueof var="order" param="order"/>
	<dsp:getvalueof var="onlineOrderNumber" param="order.onlineOrderNumber"/>
	<dsp:getvalueof var="bopusOrderNumber" param="order.bopusOrderNumber"/>
	<dsp:getvalueof var="hideOrderNumber" param="hideOrderNumber"/>

	<c:set var="showLinks" scope="request">
		<dsp:valueof param="showLinks"/>
	</c:set>
	<jsp:useBean id="placeHolderMap" class="java.util.HashMap" scope="request"/>
	<dsp:getvalueof var="cartEmpty" param="ShoppingCart.empty"/>
	
	 <dsp:droplet name="BBBPaymentGroupDroplet">
         <dsp:param bean="ShoppingCart.current" name="order"/>
         <dsp:param name="serviceType" value="GiftCardDetailService"/>
         <dsp:oparam name="output">
             <dsp:getvalueof vartype="java.lang.String" var="totalGCAmt" param="coveredByGC"/>
             <c:set var="totalGCAmt">${totalGCAmt}</c:set>
         </dsp:oparam>
     </dsp:droplet>
     <dsp:getvalueof var="orderTotal" vartype="java.lang.Double" bean="ShoppingCart.current.priceInfo.total"></dsp:getvalueof>
     <dsp:getvalueof var="storeSubtotal" vartype="java.lang.Double" bean="ShoppingCart.current.priceInfo.storeSubtotal"></dsp:getvalueof>
     <dsp:getvalueof var="orderType" vartype="java.lang.String" bean="ShoppingCart.current.onlineBopusItemsStatusInOrder" scope="page"/>
     <c:choose>
         <c:when test="${orderType eq 'BOPUS_ONLY'}">
             <c:set var="priceValue" value="${storeSubtotal}"/>
         </c:when>
         <c:otherwise>
             <c:set var="priceValue" value="${orderTotal - totalGCAmt}"/>
         </c:otherwise>
     </c:choose>

<div id="multiPayReview">
	<c:if test="${not cartEmpty}">
		<%-- payment info --%>
		<h3 class="checkout-title">
			<bbbl:label key="lbl_preview_billingmethod" language="<c:out param='${language}'/>"/><c:if test="${currentSiteId eq 'TBS_BedBathCanada'}"><sup>2</sup></c:if>
			<c:if test="${not isConfirmation && isMultiShip}">	
				<a class="edit-step tiny secondary button" href="#" data-step="payment">Edit</a>
			</c:if>
		</h3>
		<dsp:droplet name="ForEach">
			<dsp:param name="array" param="order.paymentGroups"/>
			<dsp:param name="elementName" value="paymentGroup" />
			<dsp:oparam name="output">
                <input type="hidden" id="ccID" value="ccID"/>
				<dsp:droplet name="Switch">
					<dsp:param name="value" param="paymentGroup.paymentMethod"/>
					<dsp:oparam name="paypal">
						<c:set var="isPayPal" value="${true}"/>
						<ul class="address">
							<li><bbbl:label key="lbl_paypal_title" language="<c:out param='${language}'/>"/></li>
							<li><dsp:valueof param="paymentGroup.payerEmail" /></li>
						</ul>
					</dsp:oparam>
					<dsp:oparam name="creditCard">
						<div id="payReview">
							<ul class="address">
								<li>
									<%-- <dsp:valueof param="paymentGroup.creditCardType"/> --%>
									<dsp:getvalueof param="paymentGroup.creditCardType" var="creditCardType" />
									<dsp:droplet name="ForEach">
										<dsp:param name="array" bean="PaymentGroupFormHandler.creditCardTypes" />
										<dsp:param name="elementName" value="cardlist" />
										<dsp:oparam name="output">
											<dsp:getvalueof id="cardImage" param="cardlist.imageURL" />
											<dsp:getvalueof id="cardName" param="cardlist.name" />
											<c:set var="cardNameNew" value="${fn:replace(cardName,' ','')}" />
											<c:if test="${cardNameNew eq creditCardType}">
												<img class="" src="${imagePath}${cardImage}" width="41" height="26" alt="${cardName}" title="${cardName}" />
												<c:choose>
													<c:when test="${isConfirmation}">
														<dsp:valueof param="paymentGroup.amount"/>
													</c:when>
													<c:otherwise>
														<dsp:valueof value="${priceValue}" converter="currency"/>
													</c:otherwise>
												</c:choose>
											</c:if>
										</dsp:oparam>
									</dsp:droplet>
									<input type="hidden" id="creditCardType" value="${creditCardType }"/>
								</li>
								<li>
									<dsp:valueof param="paymentGroup.creditCardNumber" converter="CreditCard" maskcharacter="*" numcharsunmasked="4"/>
								</li>
								<li>
									<dsp:getvalueof var="expDate" param="paymentGroup.expirationMonth" scope="page" />
									Exp. <c:if test='${expDate lt 10}'><c:out value="0"/></c:if><c:out value="${expDate}" /><bbbl:label key="lbl_checkout_forward_slash" language="<c:out param='${language}'/>"/><dsp:valueof param="paymentGroup.expirationYear" />
								</li>
								<li>
									<dsp:getvalueof var="cardVerificationNumber" param="paymentGroup.cardVerificationNumber"/>
									<dsp:getvalueof var="cardVerNumber" bean="CommitOrderFormHandler.cardVerNumber"/>
									<dsp:getvalueof var="isExpress" bean="ShoppingCart.current.expressCheckOut"/>
									<c:if test="${not empty showLinks}">
										<c:if test="${isExpress}">
											<div class="input">
												<div class="label">
													<label id="lblsecurityCode" for="securityCode"> <bbbl:label key="lbl_payment_securityCode" language="<c:out param='${language}'/>"/>
													<span class="required"><bbbl:label key="lbl_mandatory" language="<c:out param='${language}'/>"/></span> </label>
												</div>
												<div class="text">
													<dsp:input type="text" id="securityCode" name="securityCode" bean="CommitOrderFormHandler.cardVerNumber" autocomplete="off" iclass="textRight" maxlength="4">
														<dsp:tagAttribute name="aria-required" value="true"/>
														<dsp:tagAttribute name="aria-labelledby" value="lblsecurityCode errorsecurityCode"/>
													</dsp:input>
												</div>
												<div class="whatsThis">
													<a class="info">
														<strong><bbbl:label key="lbl_payment_txt" language="<c:out param='${language}'/>"/></strong>
														<bbbl:textArea key="txt_payment_dynamicText" language="${language}"/>
													</a>
												</div>
												<div class="cb label">
													<label id="errorsecurityCode" for="securityCode" class="error" generated="true"></label>
												</div>
											</div>
										</c:if>
									</c:if>
								</li>
							</ul>
						</div>
					</dsp:oparam>
					<dsp:oparam name="payAtRegister">
						<ul class="address">
							<li>Pay At Register</li>
						</ul>
					</dsp:oparam>
				</dsp:droplet>
			</dsp:oparam>
		</dsp:droplet>

		<%-- Gift Card Details --%>
		<dsp:include page="/checkout/preview/frag/giftcard_balance_frag.jsp" flush="true">
			<dsp:param name="order" param="order"/>
		</dsp:include>

		<%-- california prop 65 --%>
		<dsp:droplet name="BBBSkuPropDetailsDroplet">
			<dsp:param name="order" value="${order}"/>
			<dsp:oparam name="output">
				<dsp:getvalueof var="skuList" param="skuProdStatus" scope="request"/>
				<dsp:droplet name="ForEach">
					<dsp:param name="array" value="${skuList}" />
					<dsp:oparam name="output">
						<dsp:getvalueof var="size" param="size" />
						<c:set var="size" value="${size}"/>
					</dsp:oparam>
				</dsp:droplet>
			</dsp:oparam>
		</dsp:droplet>

		<c:if test="${size eq '1' }">
			<h3>
				<bbbl:label key="lbl_skuprod_statename" language="<c:out param='${language}'/>"/>
			</h3>
			<a class="popup cali" href="${contextPath}/_includes/modals/prop65.jsp">
				<bbbl:label key="lbl_skuprod_link" language="<c:out param='${language}'/>"/>
			</a>
		</c:if>
	</c:if>
</div>
</dsp:page>
