<dsp:importbean bean="/atg/commerce/ShoppingCart" />
<dsp:importbean
	bean="/atg/commerce/order/droplet/BBBContinueShoppingDroplet" />
<c:set var="lbl_checkout_checkout" scope="page">
	<bbbl:label key="lbl_checkout_checkout"
		language="${pageContext.request.locale.language}" />
</c:set>

<dsp:getvalueof var="singlePageEligible" param="singlePageEligible" />
<dsp:getvalueof var="showCheckOutBtn" param="showCheckOutBtn" />
<dsp:getvalueof var="itemNotOOS" param="itemNotOOS" />
<dsp:getvalueof var="orderHasPersonalizedItem"
	param="orderHasPersonalizedItem" />
<dsp:getvalueof var="orderHasErrorPrsnlizedItem"
	param="orderHasErrorPrsnlizedItem" />

<div id="topCheckoutButton"
	class="padTop_5 topCheckoutButton ${topCheckoutBtnClass}">
	<dsp:form name="express_form" method="post"
		action="${contextPath}/cart/cart.jsp">
		<div class="clearfix">
			<dsp:include page="spcEligibility.jsp"></dsp:include>

			<dsp:getvalueof id="shipGrpCount"
				bean="ShoppingCart.current.shippingGroupCount" />
			<c:choose>
				<c:when
					test="${showCheckOutBtn and itemNotOOS and not orderHasPersonalizedItem and not orderHasErrorPrsnlizedItem}">
					<c:choose>
						<c:when test="${not singlePageEligible }">
							<div class="button button_active button_active_orange">
								<input class="triggerSubmit" data-submit-button="botCheckoutBtn"
									type="submit" value="${lbl_checkout_checkout}"
									disabled="disabled" role="button" />
							</div>
						</c:when>
						<c:otherwise>
							<div
								class="button button_active button_active_orange button_disabled singlePageCheckout">
								<input class="triggerSubmit" data-submit-button="botCheckoutBtn"
									type="submit" value="${lbl_checkout_checkout}" role="button" />
							</div>
						</c:otherwise>
					</c:choose>
				</c:when>
				<c:otherwise>
					<div
						class="button button_active button_active_orange button_disabled">
						<input class="triggerSubmit" data-submit-button="botCheckoutBtn"
							type="submit" value="${lbl_checkout_checkout}"
							disabled="disabled" role="button" />
					</div>
				</c:otherwise>
			</c:choose>
			<dsp:droplet name="BBBContinueShoppingDroplet">
				<dsp:oparam name="output">
					<dsp:getvalueof var="linkURL" param="continue_shopping_url" />
					<c:set var="continueshoppinglink">
						<bbbl:label key="lbl_cartdetail_continueshoppinglink"
							language="<c:out param='${language}'/>" />
					</c:set>
					<dsp:a iclass="buttonTextLink" href="${linkURL}"
						title="${continueshoppinglink}">
						<bbbl:label key="lbl_cartdetail_continueshoppinglink"
							language="<c:out param='${language}'/>" />
					</dsp:a>
				</dsp:oparam>
			</dsp:droplet>
			<div class="clear"></div>

		</div>
		<div class="clear"></div>
	</dsp:form>
</div>