<dsp:importbean bean="/com/bbb/profile/session/SessionBean" />
<dsp:getvalueof bean="SessionBean.currentZipcodeVO"
	var="currentZipCodeVo" />
<dsp:getvalueof value="${currentZipCodeVo.zipcode}" var="zipCode" />
<dsp:getvalueof value="${currentZipCodeVo.minShipFee}"
			var="minShipFee" />
<dsp:getvalueof value="${currentZipCodeVo.sddEligibility}"
			var="sddEligibility" />
<dsp:importbean bean="/atg/dynamo/droplet/CurrencyFormatter"/>

<div id="sameDayDeliveryCongratsWrapper" class="sddwrapper" style="background: url(<bbbc:config key="backgroundImage" configName="SameDayDeliveryKeys"/>) no-repeat 0px 0;" >
	<div id="clock-wrap">
		<div class="clock-img">
			<%-- keeping "alt" blank as this is a decorative image --%>
			<img alt="" src="<bbbc:config key='landCongClockImgDsk' configName='SameDayDeliveryKeys'/>" />
		</div>
	</div>

	<h1 class="same-day-delivery-header"><bbbl:label key="lbl_same_day_congratulations" language="${pageContext.request.locale.language}" /></h1>
	<div class="confirm-shipping-code-text">
		<jsp:useBean id="placeHolderZipCode" class="java.util.HashMap"
						scope="request" />
		<c:set target="${placeHolderZipCode}" property="zipCode"
			value="${zipCode}" />
		<bbbl:label key="lbl_sdd_avaialable_congratulations"
			placeHolderMap="${placeHolderZipCode}"
			language="${pageContext.request.locale.language}" />
	</div>
	<div class="confirm-shipping-code-textSmall">
		<%-- keeping "alt" blank as this is a decorative image --%>
		<img src="<bbbc:config key='landCongSmallClockImgDsk' configName='SameDayDeliveryKeys'/>" class="smallClockImg" alt="">
		<bbbl:label key="lbl_sdd_look_for_clock" language="${pageContext.request.locale.language}" />
	</div>
	<div id="centerControlWrapper">
		<div class="button_sdd">
			<a href="/store" class="continueShopSDD" role="button"><bbbl:label key="lbl_start_shopping_button" language="${pageContext.request.locale.language}" /></a>
		</div>
		<div class="orBar">
			<%-- keeping "alt" blank as this is a decorative image --%>
			<img src="../../_assets/global/images/sdd/orHalfLine.png" class="orHalfBar" alt="">
			<span class="orsdd"><bbbl:label key="lbl_sdd_or" language="${pageContext.request.locale.language}" /></span>
			<%-- keeping "alt" blank as this is a decorative image --%>
			<img src="../../_assets/global/images/sdd/orHalfLine.png" class="orHalfBar" alt="">
		</div>
		<div class="gray-btn">
			<a href="#" id="searchSdd" role="button"><bbbl:label key="lbl_search_again"	language="${pageContext.request.locale.language}" /></a>
		</div>
	</div>
	
	<dsp:droplet name="CurrencyFormatter">
		<dsp:param name="currency" value="${minShipFee}"/>
		<dsp:param name="locale" bean="/OriginatingRequest.requestLocale.locale"/>
		<dsp:oparam name="output">
		<dsp:getvalueof var="formattedShipFee" param="formattedCurrency" ></dsp:getvalueof>
			<c:if test="${fn:contains(formatCurrency, '($0.00)')}">
				<c:set var="formattedShipFee" value="$0.00"></c:set>
			</c:if>
		</dsp:oparam>
	</dsp:droplet>

	<%-- If sdd is not eligible, then we will display the generic footer message  --%>
	<div class="footerTextSDD">
		<dsp:setvalue bean="SessionBean.showSDD" value="false" />
		<c:choose>
			<c:when test="${sddEligibility}">
				<jsp:useBean id="placeHolderMapShipFee" class="java.util.HashMap"
					scope="request" />
				<c:set target="${placeHolderMapShipFee}" property="minShipFee"
					value="${formattedShipFee}" />
				<p><bbbt:textArea key="txt_sdd_footer_msg"
					placeHolderMap="${placeHolderMapShipFee}"
					language="${pageContext.request.locale.language}" />
			</c:when>
			<c:otherwise>
				<p><bbbt:textArea key="txt_sdd_generic_msg"
					language="${pageContext.request.locale.language}" />
			</c:otherwise>
		</c:choose>
	</div>
</div>