<dsp:page>
        <dsp:importbean bean="/atg/commerce/order/purchase/CartModifierFormHandler"/>
        <dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>
		<dsp:importbean bean="/atg/commerce/ShoppingCart" />
        <c:set var="bopusOnlyText" value="BOPUS_ONLY" />
        <c:set var="hybrid" value="HYBRID" />
        <dsp:getvalueof id="formAction" param="action" />
        <dsp:getvalueof id="currentLevel" param="currentLevel" />
        <dsp:form id="frmCoupons" formid="frmCoupons" action="${formAction}">
       		<!-- Below If block is for R2 requirement -->
			<dsp:getvalueof var="orderStatus" bean="ShoppingCart.current.OnlineBopusItemsStatusInOrder" />
			<dsp:droplet name="/com/bbb/commerce/order/droplet/UserCouponWalletDroplet">
                <dsp:param name="profile" bean="/atg/userprofiling/Profile"/>
                <dsp:param name="order" bean="/atg/commerce/ShoppingCart.current"/>
                <dsp:param name="site" value="${currentSiteId}"/>
                <dsp:oparam name="outputStart">
                    <dsp:setvalue bean="CartModifierFormHandler.couponPage" paramvalue="couponPage"/>
                    <dsp:input bean="CartModifierFormHandler.couponPage" type="hidden" paramvalue="couponPage" />
                    <dsp:droplet name="IsEmpty">
                        <dsp:param name="value" param="couponPage"/><%--This parameter is sent from the coupon page not the cart page --%>
                        <dsp:oparam name="true">
                        	<h3 class="marTop_5"><bbbl:label key="lbl_coupon_pagetitle" language="${language}"/></h3>
							<p><bbbl:label key="lbl_coupon_cart_section" language="${language}"/></p>
							<c:if test="${CouponOn && ((orderStatus eq bopusOnlyText) or (orderStatus eq hybrid))}">
								<div class="couponStoreInfo cb clearfix">
									<h3><bbbl:label key="lbl_online_coupon_store_pickup" language="${language}"/></h3>
									<p><bbbl:label key="lbl_online_coupon_store_pickup_desc" language="${language}"/></p>
								</div>
							</c:if>
						</dsp:oparam>
                    </dsp:droplet>
                </dsp:oparam>
                <dsp:oparam name="output">
                    <dsp:include page="/cart/couponLineitem.jsp"/>
                </dsp:oparam>
                <dsp:oparam name="outputEnd">
                    <div class="button btnApply">
                        <c:set var="applyButton"><bbbl:label key="lbl_coupon_apply" language="${language}"/></c:set>
						<dsp:getvalueof id="cartCheck" param="cartCheck" />
						<c:if test="${not empty cartCheck}">
							<input name="cart" type="hidden" value="CART"/>
						</c:if>
                        <dsp:input bean="CartModifierFormHandler.applyCoupons" type="submit" value='${applyButton}' id="btnApplyCoupons" >
                            <dsp:tagAttribute name="aria-pressed" value="false"/>
                            <dsp:tagAttribute name="aria-labelledby" value="btnApplyCoupons"/>
                            <dsp:tagAttribute name="role" value="button"/>
                        </dsp:input>
                    </div>
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
      </dsp:form>
</dsp:page>