<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/com/bbb/commerce/droplet/TBSStoreCouponsDroplet"/>
	<dsp:importbean bean="/atg/commerce/order/purchase/CartModifierFormHandler"/>
	<dsp:importbean bean="/atg/userprofiling/Profile"/>
	<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
	<dsp:importbean bean="/atg/commerce/promotion/CouponFormHandler"/>
	<dsp:importbean bean="/atg/commerce/ShoppingCart" />
	<dsp:importbean bean="/com/bbb/common/TBSSessionComponent"/>
	<dsp:importbean bean="/com/bbb/commerce/droplet/TBSEmailCouponsDroplet"/>
	<dsp:importbean bean="/atg/multisite/Site" />

	<%-- Page Variables --%>
	<dsp:getvalueof var="isAnonymous" bean="Profile.transient" />
	<c:if test="${!isAnonymous}">
		<dsp:getvalueof var="userMobile" bean="Profile.mobileNumber" />
	</c:if>
	<dsp:getvalueof var="userphone" bean="Profile.phoneNumber" />
	<dsp:getvalueof var="email" bean="Profile.email" />
	<dsp:getvalueof bean="TBSSessionComponent.emailId" var="emailId"/>
	<dsp:getvalueof bean="TBSSessionComponent.mobileNumber" var="mobileNumber"/>
	<dsp:getvalueof id="currentSiteId" bean="Site.id" />
	
	<c:if test="${not empty emailId}">
		<c:set var="email" value="${emailId}"/>
	</c:if>
	<c:if test="${not empty userMobile}">
		<c:set var="userMobile" value="${userMobile}"/>
	</c:if>
	<c:if test="${not empty userphone}">
		<c:set var="userMobile" value="${userphone}"/>
	</c:if>
	<c:if test="${not empty mobileNumber}">
		<c:set var="userMobile" value="${mobileNumber}"/>
	</c:if>
	<div class="row other-info">
		<div class="small-12 columns">
			<h2 class="divider">Other Information</h2>
		</div>
		<dsp:form id="retrieveCouponsForm" action="" method="post">
			<div class="small-12 large-4 columns">
				<dsp:input type="email" bean="CartModifierFormHandler.emailAddress" id="emails" name="emails" value="${email}">
					<dsp:tagAttribute name="placeholder" value="Email address" />
					<dsp:tagAttribute name="aria-required" value="true"/>
					<dsp:tagAttribute name="aria-labelledby" value="lblemail erroremail"/>
				</dsp:input>

			</div>
			
			<div class="small-12 large-2 columns">
				<dsp:input type="tel" bean="CartModifierFormHandler.mobileNumber" id="basePhoneFull" name="basePhoneFull" value="${userMobile}">
					<dsp:tagAttribute name="placeholder" value="Mobile Number" />
					<dsp:tagAttribute name="maxlength" value="10"/>
					<dsp:tagAttribute name="aria-required" value="false"/>
					<dsp:tagAttribute name="aria-labelledby" value="lblbasePhoneFull errorbasePhoneFull"/>
				</dsp:input>
			</div>
			<div class="small-12 large-2 medium-offset-4 columns">
				<a href="#" class="button tiny service right expand retrieve-coupons">Submit</a>
				<dsp:input bean="CartModifierFormHandler.retrieveCoupons" type="submit" value="Submit" id="retrieveCoupons" iclass="hidden" />
				<%-- <dsp:input type="hidden" bean="CartModifierFormHandler.couponSuccessUrl" value="${contextPath}/cart/cart.jsp" />
				<dsp:input type="hidden" bean="CartModifierFormHandler.couponErrorUrl" value="${contextPath}/cart/cart.jsp" /> --%>
				 <dsp:input bean="CartModifierFormHandler.fromPage"  type="hidden" value="storePromotions" />
			</div>
		</dsp:form>
		<div style="color:red;margin-left:10px;">
			<bbbl:label key="tbs_email_and_mobile" language="${pageContext.request.locale.language}" />	
		</div>
	</div>

	<dsp:getvalueof var="couponFinalList" bean="CartModifierFormHandler.couponFinalList"/>

	<div class="row available-offers">
		<div class="small-12 columns">
			<h3>Available Offers</h3>
		</div>
		<dsp:form id="applyCouponsForm" action="" method="post">
			<%-- displays list of coupons from the response list of email and phone number on submit handler retrieve coupons --%>
			<div class="small-12 large-6 columns offer-container personalized-offers">
				<c:choose>
					<c:when test="${not empty email || not empty userMobile}">
						<%-- This is for anonymous user and logged-in user with another email, displaying the coupon after refresh the page --%>
						<dsp:droplet name="TBSEmailCouponsDroplet">
							<dsp:param name="emailId" value="${email}"/>
							<dsp:param name="mobileNumber" value="${userMobile}"/>
							<dsp:param name="order" bean="ShoppingCart.current"/>
							<dsp:param name="site" value="${currentSiteId}"/>
								<dsp:oparam name="output">
									<dsp:include page="storeCouponLineitem.jsp"/>
								</dsp:oparam>
						</dsp:droplet>
					</c:when>
					<c:when test="${!isAnonymous && empty couponFinalList}">
						<%-- This is for logged in user --%>
						<dsp:droplet name="Switch">
							<dsp:param name="value" bean="Profile.transient"/>
							<dsp:oparam name="false">
								<dsp:getvalueof id="bopusOnly" bean="ShoppingCart.current.OnlineBopusItemsStatusInOrder" />
								<c:if test="${CouponOn}">
									<dsp:include page="storeCouponDisplay.jsp">
										<dsp:param name="action" value="${contextPath}/cart/cart.jsp"/>
										<dsp:param name="cartCheck" value="true"/>
									</dsp:include>
								</c:if>
							</dsp:oparam>
						</dsp:droplet>
					</c:when>
					<c:otherwise>
						<c:if test="${not empty couponFinalList}">
							<dsp:include page="coupons.jsp">
								<dsp:param name="couponList" value="${couponFinalList}"/>
							</dsp:include>
						</c:if>
					</c:otherwise>
				</c:choose>
			</div>

			<div class="small-12 large-4 columns offer-container store-offers">
				<dsp:droplet name="TBSStoreCouponsDroplet">
					<dsp:param name="order" bean="ShoppingCart.current"/>
					<dsp:oparam name="output">
						<dsp:getvalueof var="appliedCoupons" param="appliedCoupons"/>
						<dsp:getvalueof var="siteCoupons" param="siteCoupons"/>
						<dsp:getvalueof var="couponsFromConfigKey" param="couponsFromConfigKey"/>
                        <div id="couponLabels">
                            <c:forEach var="coupon" items="${appliedCoupons}">
                                <dsp:param name="couponItem" value="${coupon}"/>
                                <dsp:getvalueof var="id" param="couponItem.id"/>
                                <%-- KP COMMENT START: this is the old method. need it for bean--%>
                                <%-- KP COMMENT END --%>
                                <label class="inline-rc checkbox checkboxs " id="${id}">
                                    <dsp:input type="checkbox" bean="CouponFormHandler.couponCodes" value="${id}" iclass="coupon-checkboxes" checked="true">
                                        <dsp:valueof param="couponItem.displayName"/>
                                    </dsp:input>
                                    <span data-site-coupon-id="${id}"></span>
                                </label>
                            </c:forEach>
                                <label class="inline-rc checkbox checkboxs hidden" id="dummy">
                                   <dsp:input type="checkbox" bean="CouponFormHandler.couponCodes" value="dummy" iclass="coupon-checkboxes" checked="false">
                                        <dsp:valueof param="couponItem.displayName"/>
                                   </dsp:input>                                
                                    <span data-site-coupon-id="dummy"></span>
                                </label>
                            </div>
							<div class="dropdown-container">
								<label class="inline-rc checkbox" for="siteCoupons">
									<!-- <input type="checkbox" id="siteCoupons">
									<span></span> -->
									<a href="#" data-dropdown="siteCouponsDropdown" class="small secondary radius button dropdown expand not-empty">ADD STORE COUPONS<span></span></a>
									<ul id="siteCouponsDropdown" data-dropdown-content="" class="f-dropdown">
										<li class="selected"><a href="#" id="clear-coupons-default" data-site-coupon-id="clear-coupons-default">ADD STORE COUPONS</a></li>
										<c:forEach var="coupon" items="${siteCoupons}">
											<dsp:param name="couponItem" value="${coupon.key}"/>
											<dsp:getvalueof var="id" param="couponItem.id"/>
											<dsp:getvalueof var="selected" value="${coupon.value}"/>
                                            <dsp:getvalueof var="couponName" param="couponItem.displayName"/>
											<%--KP COMMENT START: this is the old method. need it for bean--%>
											<%--KP COMMENT END--%>
											<c:if test="${not selected}">
												<li id="li_${id}"><a href="#" data-site-coupon-id="${id}" data-site-configkey-coupons-list="${couponsFromConfigKey[id]}" data-site-coupon-name="${couponName}"><dsp:valueof param="couponItem.displayName"/></a></li>
											</c:if>
										</c:forEach>
									</ul>
								</label>
							</div>
					</dsp:oparam>
					<dsp:oparam name="empty">
						<dsp:getvalueof var="siteCoupons" param="siteCoupons"/>
							<div class="dropdown-container">
								<label class="inline-rc checkbox" for="siteCoupons">
									<input type="checkbox" id="siteCoupons" disabled="disabled">
									<span></span>
									<a href="#" data-dropdown="siteCouponsDropdown" class="small secondary radius button dropdown expand empty"><span></span></a>
									<ul id="siteCouponsDropdown" data-dropdown-content="" class="f-dropdown">
										<li class="selected"><a href="#" id="clear-coupons-default" data-site-coupon-id="clear-coupons-default">ADD STORE COUPONS</a></li>
									</ul>
								</label>
							</div>
					</dsp:oparam>
				</dsp:droplet>
			</div>

			<div class="small-12 large-2 columns">
				<div class="row">
					<div class="small-6 hide-for-large-up columns">
						<a href="#" class="button tiny download expand clear-all-coupons">Clear All</a>
					</div>
					<div class="small-6 large-12 columns">
						<a class="button tiny service right expand apply-coupons">APPLY</a>
						<dsp:input type="submit" value="Apply" bean="CouponFormHandler.claimStoreCoupons" id="applyCoupons" iclass="hidden" />
						<dsp:input bean="CouponFormHandler.fromPage" type="hidden" value="storePromotions" />
						<%-- Client DOM XSRF | Part -2
						<dsp:input bean="CouponFormHandler.claimCouponSuccessURL" value="${contextPath}/cart/cart.jsp" type="hidden"/>
						<dsp:input bean="CouponFormHandler.claimCouponErrorURL" value="${contextPath}/cart/cart.jsp" type="hidden"/> --%>
					</div>
					<div class="show-for-large-up large-12 columns">
						<a href="#" class="clear-all-coupons"><span></span>Clear All</a>
						<dsp:input type="submit" value="Clear All" bean="CouponFormHandler.clearStoreCoupons" id="clearCoupons" iclass="hidden" />
					</div>
				</div>
			</div>
		</dsp:form>
	</div>

</dsp:page>
