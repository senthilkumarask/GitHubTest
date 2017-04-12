<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/atg/commerce/order/purchase/CartModifierFormHandler"/>
	<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>
	<dsp:importbean bean="/atg/commerce/ShoppingCart" />
	<dsp:importbean bean="/com/bbb/commerce/order/droplet/UserCouponWalletDroplet" />

	<%-- Page Variables --%>
	<dsp:getvalueof id="formAction" param="action" />
	<dsp:getvalueof id="currentLevel" param="currentLevel" />

	<dsp:droplet name="UserCouponWalletDroplet">
		<dsp:param name="profile" bean="/atg/userprofiling/Profile"/>
		<dsp:param name="order" bean="/atg/commerce/ShoppingCart.current"/>
		<dsp:param name="site" value="${currentSiteId}"/>
		<dsp:oparam name="output">
			<dsp:include page="storeCouponLineitem.jsp"/>
		</dsp:oparam>
		<dsp:oparam name="empty">
			<dsp:droplet name="IsEmpty">
				<dsp:param name="value" param="couponPage"/><%--This parameter is sent from the coupon page not the cart page --%>
				<dsp:oparam name="false">
					<p><bbbl:label key="lbl_coupon_no_coupons_message" language="${language}"/></p>
				</dsp:oparam>
			</dsp:droplet>
		</dsp:oparam>
	</dsp:droplet>

</dsp:page>
