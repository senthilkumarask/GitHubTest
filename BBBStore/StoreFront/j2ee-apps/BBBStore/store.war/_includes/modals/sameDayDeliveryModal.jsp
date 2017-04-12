<%@ taglib uri="/WEB-INF/tld/BBBLblTxt.tld" prefix="bbb"%>
<dsp:page>
	<dsp:importbean bean="/com/bbb/profile/session/SessionBean" />
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath" />	
	<dsp:getvalueof id="currentSiteId" bean="/atg/multisite/Site.id" />
	<dsp:importbean bean="/atg/dynamo/droplet/CurrencyFormatter"/>

	<c:choose>
		<c:when test="${(currentSiteId eq BedBathCanada)}">
			<dsp:getvalueof var="zipName" value="sddCAZip" />
		</c:when>
		<c:otherwise>
			<dsp:getvalueof var="zipName" value="sddZip" />
		</c:otherwise>
	</c:choose>

	<div id="sameDayDeliveryModalWrapper" class="sameDayDeliveryModalWrapper">
		<div id="clock-wrap">
			<div class="clock-img">
				<%-- keeping "alt" blank as this is a decorative image --%>
				<img alt="" src="<bbbc:config key='modalClkImgDsk' configName='SameDayDeliveryKeys'/>">
			</div>
		</div>

		<dsp:getvalueof bean="SessionBean.currentZipcodeVO" var="currentZipCodeVo"/>
		<dsp:getvalueof bean="SessionBean.landingZipcodeVO" var="landingZipCodeVo"/>
		<dsp:getvalueof value="${currentZipCodeVo.zipcode}" var="zipCode"/>
		<dsp:getvalueof value="${currentZipCodeVo.minShipFee}" var="minShipFee"/>
		<dsp:getvalueof value="${currentZipCodeVo.sddEligibility or landingZipCodeVo.sddEligibility}" var="sddEligibility"/>
		<dsp:getvalueof value="${currentZipCodeVo.displayCutoffTime}" var="displayCutoffTime"/>
		 
		<h3 class="same-day-delivery-header">
			<div class="new-tag"></div>
			<bbbl:label key="lbl_same_day_delivery_heading" language="${pageContext.request.locale.language}"/>
		</h3>

		<div class="confirm-shipping-code-text">
			<c:choose>
				<c:when test="${sddEligibility}">
					<jsp:useBean id="placeHolderDisplayTime" class="java.util.HashMap" scope="request"/>
					<c:set target="${placeHolderDisplayTime}" property="displayTime" value="${displayCutoffTime}"/>
					<bbbt:textArea key="txt_sdd_subheading" placeHolderMap="${placeHolderDisplayTime}" language="${pageContext.request.locale.language}"/>
					<a href="${contextPath}<bbbl:label key="lbl_more_details_url" language="${pageContext.request.locale.language}"/>"><bbbl:label key="lbl_sdd_more_details" language="${pageContext.request.locale.language}"/></a>
				</c:when>
				<c:otherwise>
					<bbbt:textArea key="txt_sdd_generic_subheading" language="${pageContext.request.locale.language}"/>
					<a href="${contextPath}<bbbl:label key="lbl_more_details_url" language="${pageContext.request.locale.language}"/>"><bbbl:label key="lbl_sdd_more_details" language="${pageContext.request.locale.language}"/></a>
				</c:otherwise>
			</c:choose>
		</div>

		<div class="confirm-shipping-code-text hidden" id="onAjaxResponse">
			<bbbl:label key="lbl_sdd_unavailable_in_your_area" language="${pageContext.request.locale.language}"/><a href="${contextPath}<bbbl:label key="lbl_more_details_url" language="${pageContext.request.locale.language}"/>"><bbbl:label key="lbl_sdd_more_details" language="${pageContext.request.locale.language}"/></a>
		</div>

		<div id="centerControlWrapper">
			<form id="frmSameDayDeliveryModal" class="form" name="frmSameDayDeliveryModal" method="post" action="${contextPath}/browse/confirm_zipcode_json.jsp?${zipCode}">
				<div class="input textBox">
					<label for="sddZip" id="lblSddZip" class="txtOffScreen"><bbbl:label key="lbl_enter_zip_code" language="${pageContext.request.locale.language}" /></label>
					<input type="text" id="sddZip" maxlength="10" name="${zipName}" value="${zipCode}" />
					<a class="sddziperror hidden" href="${contextPath}<bbbl:label key="lbl_more_details_url" language="${pageContext.request.locale.language}"/>"><bbbl:label key="lbl_sdd_more_details" language="${pageContext.request.locale.language}"/></a>
				</div>	
				<div class="showOnError hidden">
					<span id="lblerrSddNotAvailable"><bbbl:label key="lbl_sdd_validation_error" language="${pageContext.request.locale.language}"/></span>
					<a href="${contextPath}<bbbl:label key="lbl_more_details_url" language="${pageContext.request.locale.language}"/>"><bbbl:label key="lbl_sdd_more_details" language="${pageContext.request.locale.language}"/></a>
				</div>
				<div class="button_sdd">
					<label for="confirmZipCode" class="txtOffScreen"><bbbl:label key="lbl_sdd_confirm_zip_code_verbose" language="${pageContext.request.locale.language}" /></label>
					<input type="submit" id="confirmZipCode" value="<bbbl:label key="lbl_sdd_confirm_zip_code" language="${pageContext.request.locale.language}"/>"/>
				</div>
				<input type="hidden" name="zipCode" value="${zipCode}" />
			</form>
		
			<div class="orBar">
				<span class="orsdd"><bbbl:label key="lbl_sdd_or" language="${pageContext.request.locale.language}"/></span>
			</div>
			<div class="gray-btn">
				<a href="#" id="continueShopping" role="button"><bbbl:label key="lbl_sdd_continue_shopping" language="${pageContext.request.locale.language}"/></a>
			</div>
		</div>

		<div class="bottomTextSmall">
			<dsp:setvalue bean="SessionBean.showSDD" value="false" />
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

			<c:choose>
				<c:when test="${sddEligibility}">
					<jsp:useBean id="placeHolderMapShipFee" class="java.util.HashMap" scope="request"/>
					<c:set target="${placeHolderMapShipFee}" property="minShipFee" value="${formattedShipFee}"/>
					<bbbt:textArea key="txt_sdd_footer_msg" placeHolderMap="${placeHolderMapShipFee}" language="${pageContext.request.locale.language}"/>
				</c:when>
				<c:otherwise>
					<bbbt:textArea key="txt_sdd_generic_msg"  language="${pageContext.request.locale.language}"/>
				</c:otherwise>
			</c:choose>
		</div>
	</div>
</dsp:page>
<script type="text/javascript">
	if (typeof s !== 'undefined') {
	s.pageName ='My Account>Same Day Delivery Modal';
	s.channel = 'My Account';
	s.prop1 = 'My Account';
	s.prop2 = 'My Account';
	s.prop3 = 'My Account';
	s.prop6='${pageContext.request.serverName}'; 
	s.eVar9='${pageContext.request.serverName}';
	var s_code = s.t();
	if (s_code)
		document.write(s_code);
	}
</script>	