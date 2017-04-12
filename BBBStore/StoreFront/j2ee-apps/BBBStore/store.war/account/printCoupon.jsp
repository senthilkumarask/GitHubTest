<dsp:page>
	<dsp:importbean bean="/com/bbb/commerce/catalog/droplet/GetPromotionDetailsDroplet" />	
	<dsp:importbean bean="/atg/multisite/Site"/>
	<dsp:importbean bean="/com/bbb/profile/session/SessionBean"/>	
	<dsp:getvalueof var="appid" bean="Site.id" />
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath" />

	<dsp:getvalueof var="promotionId" param="promotionId" />
	<dsp:getvalueof var="offerId" param="offerId" />
	<dsp:getvalueof var="expDate" param="expDate" />


	<dsp:droplet name="GetPromotionDetailsDroplet">
		<dsp:param name="promotionId" value="${promotionId}" />
		<dsp:param name="couponId" value="${param.couponId}" /> 
		<dsp:oparam name="output">
			<dsp:getvalueof var="mainImgUrl" param="mainImgUrl" />
			<dsp:getvalueof var="lrgImgUrl" param="lrgImgUrl" />
			<dsp:getvalueof var="promoDesription" param="promoDesription" />
			<dsp:getvalueof var="tAndc" param="tAndc" />
			<%-- Uncomment for debugging 
			mainImgUrl:: ${mainImgUrl } <br />
			lrgImgUrl:: ${lrgImgUrl }<br />
			promoDesription:: ${promoDesription }<br />
			tAndc:: ${tAndc }

			--%>
		</dsp:oparam>
	</dsp:droplet>

	<dsp:include page="/couponWallet/frags/print_coupon_frag.jsp">
		<dsp:param name="lrgImgUrl" value="${lrgImgUrl}"/>
	   <dsp:param name="tAndc" value="${tAndc}"/>
	   <dsp:param name="offerId" value="${offerId}"/>
	   <dsp:param name="expDate" value="${expDate}"/>
	</dsp:include>

	
</dsp:page>