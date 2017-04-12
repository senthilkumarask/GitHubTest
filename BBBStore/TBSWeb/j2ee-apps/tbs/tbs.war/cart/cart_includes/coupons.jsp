<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
	<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty" />
	<dsp:importbean var="Profile" bean="/atg/userprofiling/Profile" />
	<dsp:importbean bean="/atg/commerce/promotion/CouponFormHandler" />

	<%-- Page Variables --%>
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath" />

	<dsp:droplet name="/atg/dynamo/droplet/ForEach">
		<dsp:param name="array" param="couponList" />
		<dsp:oparam name="outputStart">
			<p>
				<bbbt:textArea key="txtarea_coupons_description" language="${pageContext.request.locale.language}" />
			</p>
		</dsp:oparam>
		<dsp:oparam name="output">
			<dsp:setvalue param="CouponListVo" paramvalue="element" />
			<dsp:getvalueof var="CouponListVo" vartype="java.lang.Object" param="CouponListVo" />
			<c:if test="${CouponListVo.disqualify ne 'true'}">
				<dsp:getvalueof var="promoIMGURL" value="${CouponListVo.couponsImageUrl}" />
				<dsp:getvalueof var="couponCode" value="${CouponListVo.entryCd}" />
				<dsp:getvalueof var="promoId" value="${CouponListVo.promoItem}" />
				<label class="inline-rc checkbox" for="${couponCode}" data-coupon-code="${couponCode}">
					<dsp:input type="checkbox" bean="CouponFormHandler.emailCoupons" id="${couponCode}" value="${couponCode}" checked="false"></dsp:input>
					<span></span>
					<c:choose>
						<c:when	test="${(fn:indexOf(promoIMGURL, 'http') == 0) || (fn:indexOf(promoIMGURL, '//') == 0)}">
							<dsp:getvalueof var="img" value="${CouponListVo.couponsImageUrl}" />
							<img src="${img}" class="stretch" alt="${CouponListVo.couponsDescription}" />
						</c:when>
						<c:otherwise>
							<dsp:getvalueof var="img1" value="${scene7Path}/${CouponListVo.couponsImageUrl}" />
							<img src="${img1}" class="stretch" alt="${CouponListVo.couponsDescription}" />
						</c:otherwise>
					</c:choose>
					<p><dsp:valueof value="${CouponListVo.description}" /></p>
					<p><dsp:valueof value="${CouponListVo.couponsDescription}" /></p>
					<dsp:droplet name="IsEmpty">
						<dsp:param name="value" value="${CouponListVo.couponsExclusions}" />
						<dsp:oparam name="false">
							<a href="${contextPath}/checkout/coupons/exclusions.jsp?item=${CouponListVo.entryCd}" class="popup" data="${CouponListVo.entryCd}" title="Exclusions">
								<bbbl:label key="lbl_coupon_exclusions_title" language="${pageContext.request.locale.language}" />
							</a>
						</dsp:oparam>
					</dsp:droplet>
				</label>
			</c:if>
		</dsp:oparam>
		<dsp:oparam name="empty">
			<p>
				<bbbt:label key="lbl_coupons_no_coupon" language="${pageContext.request.locale.language}" />
			</p>
		</dsp:oparam>
	</dsp:droplet>

</dsp:page>
