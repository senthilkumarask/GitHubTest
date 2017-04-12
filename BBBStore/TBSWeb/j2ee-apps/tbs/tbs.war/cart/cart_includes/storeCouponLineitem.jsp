<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/atg/commerce/order/purchase/CartModifierFormHandler"/>
	<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
	<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
	<dsp:importbean bean="/atg/commerce/promotion/CouponFormHandler" />
	<dsp:importbean bean="/atg/commerce/ShoppingCart"/>

	<%-- Page Variables --%>
	<dsp:getvalueof var="selected" param="select"/>
	<dsp:getvalueof var="existingPromoCode" bean="ShoppingCart.current.SchoolCoupon" />
	<dsp:getvalueof var="couponId" param="key"/>
	<c:set var="showPromoError" value="${false}" scope="page" />
	<dsp:getvalueof var="exclusionText" param="exclusionText" />
	<dsp:droplet name="Switch">
		<dsp:param name="value" bean="CartModifierFormHandler.formError"/>
		<dsp:oparam name="true">
			<dsp:droplet name="ForEach">
				<dsp:param name="array" bean="CartModifierFormHandler.formExceptions"/>
				<dsp:oparam name="output">
					<dsp:droplet name="Switch">
						<dsp:param name="value" param="element.errorCode"/>
						<dsp:oparam name="err_schoolPromoCode_BLANK">
							<dsp:getvalueof vartype="java.lang.String" var="schoolPromoError" scope="page" value="error"/>
							<dsp:getvalueof vartype="java.lang.String" var="schoolPromoErrorMsg" scope="page" param="element.message"/>
							<c:set var="showPromoError" value="${true}" scope="page" />
						</dsp:oparam>
						<dsp:oparam name="err_schoolPromoCode_UNMATCH">
							<dsp:getvalueof vartype="java.lang.String" var="schoolPromoError" scope="page" value="error"/>
							<dsp:getvalueof vartype="java.lang.String" var="schoolPromoErrorMsg" scope="page" param="element.message"/>
							<c:set var="showPromoError" value="${true}" scope="page" />
						</dsp:oparam>
						<dsp:oparam name="default">
							<dsp:getvalueof var="couponId" param="key"/>
							<dsp:getvalueof id="errorMap" bean="CartModifierFormHandler.couponErrorList.${couponId}"/>
						</dsp:oparam>
					</dsp:droplet>
				</dsp:oparam>
			</dsp:droplet>
		</dsp:oparam>
	</dsp:droplet>

	<dsp:getvalueof var="couponId" param="key"/>
	<dsp:getvalueof var="index" param="index"/>
	
	<c:set var="promoId" value="offer1_off20${couponId}"/>
	<c:if test="${couponId == 'schoolPromo'}">
		<c:set var="promoId" value="${couponId}"/>
	</c:if>
	
	<label class="inline-rc checkbox" for="${promoId}">
	<c:choose>
		<c:when test="${selected}">
			<dsp:input bean="CouponFormHandler.emailCoupons" type="checkbox" checked="true" id="${promoId}" value="${couponId}" >
				<dsp:tagAttribute name="aria-checked" value="true"/>
			    </dsp:input>
				<span></span>
				<bbbl:label key="lbl_coupon_applied_text" language="${language}"/>
		</c:when>
		<c:otherwise>
			<dsp:input bean="CouponFormHandler.emailCoupons" type="checkbox" checked="false" id="${promoId}" value="${couponId}" >
				<dsp:tagAttribute name="aria-checked" value="false"/>
				</dsp:input>
				<span></span>
				<bbbl:label key="lbl_coupon_select_text" language="${language}"/>
		</c:otherwise>
	</c:choose>

		<c:set var="scene7Path">
			<bbbc:config key="scene7_url" configName="ThirdPartyURLs" />
		</c:set>
		<dsp:getvalueof var="promoIMGURL" param="item.media.mainImage.url"></dsp:getvalueof>
		<c:choose>
			<c:when test="${(fn:indexOf(promoIMGURL, 'http') == 0) || (fn:indexOf(promoIMGURL, '//') == 0)}">
				<img src='<dsp:valueof param="item.media.mainImage.url"/>' class="stretch" alt='<dsp:valueof param="item.displayName"/>' />
			</c:when>
			<c:otherwise>
				<img src='${scene7Path}/<dsp:valueof param="item.media.mainImage.url"/>' class="stretch" alt='<dsp:valueof param="item.displayName"/>' />
			</c:otherwise>
		</c:choose>
		<%-- Coupon Description --%>		
	<div class="displayInlineBlock offerCouponsCartPage">
		
		<div>
		<strong><bbbl:label key="lbl_coupon_expires" language ="${language}"/>
			<dsp:valueof param="appliedCpnVO.expiryDate" valueishtml="true"/>
		</strong></div>
	
		<strong><dsp:valueof param="item.displayName" valueishtml="true"/></strong>
		<strong><dsp:valueof param="item.description" valueishtml="true"/></strong>
        <br/>
		<c:set var="title"><bbbl:label key="lbl_coupon_exclusions_title" language="${language}"/></c:set>
		<dsp:droplet name="IsEmpty">
			<dsp:param name="value" param="exclusionText"/>
			<dsp:oparam name="false">
			<div>	<dsp:a iclass="popup" page="/checkout/coupons/exclusions.jsp" title='${title}' onclick="return false"><dsp:param name="item" value="${couponId}"/>
					<bbbl:label key="lbl_coupon_exclusions_title" language="${language}"/>
				</dsp:a>
			</div>
			</dsp:oparam>
		</dsp:droplet>
		</div>
		<c:if test="${empty existingPromoCode}">
			<c:if test="${couponId == 'schoolPromo'}">
				<label id="lblcouponPromoCode" for="couponPromoCode"><bbbl:label key="lbl_coupon_promo_code" language="${language}"/></label>
				<dsp:input id="couponPromoCode" iclass="${schoolPromoError}" name="couponPromoCode" type="text" bean="CartModifierFormHandler.couponClaimCode" disabled="true">
					<dsp:tagAttribute name="aria-required" value="false"/>
					<dsp:tagAttribute name="aria-labelledby" value="lblcouponPromoCode errorcouponPromoCode"/>
				</dsp:input>
				<label id="errorcouponPromoCode" class="error" for="couponPromoCode" generated="true" <c:if test="${showPromoError}"> style="display: block;" </c:if> ><dsp:valueof value="${schoolPromoErrorMsg}"/></label>
			</c:if>
		</c:if>

		<%-- Below If block is for R2 requirement --%>
		<c:if test="${not empty couponErrorList && not selected}">
			<dsp:getvalueof id="errorMap" bean="CartModifierFormHandler.couponErrorList.${couponId}"/>
			<c:if test="${not empty errorMap}">
				<div class="couponErrorMsg">
					<h3><bbbe:error key="err_coupon_cannot_apply" language="${pageContext.request.locale.language}"/></h3>
					<p><bbbe:error key="${errorMap}" language="${pageContext.request.locale.language}"/></p>
				</div>
			</c:if>
		</c:if>
	</label>

</dsp:page>
