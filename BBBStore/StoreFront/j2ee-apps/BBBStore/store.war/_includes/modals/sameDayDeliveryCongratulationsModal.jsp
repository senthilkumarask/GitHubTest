<%@ taglib uri="/WEB-INF/tld/BBBLblTxt.tld" prefix="bbb"%>
<dsp:page>
	<dsp:importbean bean="/com/bbb/profile/session/SessionBean" />
	
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath" />
					
	<dsp:getvalueof bean="SessionBean.currentZipcodeVO" var="currentZipCodeVo"/>
	<dsp:getvalueof value="${currentZipCodeVo.minShipFee}" var="minShipFee"/>
	<dsp:importbean bean="/atg/dynamo/droplet/CurrencyFormatter"/>
	
	<div id="sameDayDeliveryCongratModalWrapper">
		<div id="clock-wrap-congrat">
			<div class="clock-img">
				<%-- keeping "alt" blank as this is a decorative image --%>
				<img alt="" src="<bbbc:config key='modalCongClkImgDsk' configName='SameDayDeliveryKeys'/>" />
			</div>
		</div>
		<h3 class="same-day-delivery-header"><bbbl:label key="lbl_sdd_congratulations" language="${pageContext.request.locale.language}"/></h3>
		<div class="confirm-shipping-code-text">
			<bbbt:textArea key="txt_same_day_delivery" language="${pageContext.request.locale.language}"/>
			<a href="${contextPath}<bbbl:label key="lbl_more_details_url" language="${pageContext.request.locale.language}"/>"><bbbl:label key="lbl_sdd_more_details" language="${pageContext.request.locale.language}"/></a>
		</div>
		<div id="centerControlWrapperCongrat">
			<div class="imgHolder">
				<div class="cong-img">
					<%-- keeping "alt" blank as this is a decorative image --%>
					<img alt="" src="<bbbc:config key='modalCongImgDsk' configName='SameDayDeliveryKeys'/>">
				</div>
				<div class="sdd-cloc-icon-text" aria-hidden="true"><bbbl:label key="lbl_sdd_clock_icon_txt" language="${pageContext.request.locale.language}" /></div>
				<div class="arrow-down"></div>
				<div class="sdd-clock-icon">
					<%-- keeping "alt" blank as this is a decorative image --%>
					<%-- aria-label key: lbl_sdd_clock_icon_info (currently not needed) --%> 
					<img alt="" src="<bbbc:config key='modalMessImgDsk' configName='SameDayDeliveryKeys'/>">
				</div>
			</div>
			<div class="button_sdd">
				<a href="#" id="continueShopping" role="button"><bbbl:label key="lbl_continue_shopping" language="${pageContext.request.locale.language}"/></a>
			</div>	
		</div>
	
		<div class="bottomTextSmall">
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
			
			<jsp:useBean id="placeHolderMapShipFee" class="java.util.HashMap" scope="request"/>
			<c:set target="${placeHolderMapShipFee}" property="minShipFee" value="${formattedShipFee}"/>
			<bbbt:textArea key="txt_delivery_footer_msg" placeHolderMap="${placeHolderMapShipFee}" language="${pageContext.request.locale.language}"/>
		</div>
	</div>
</dsp:page>
<script type="text/javascript">
	if (typeof s !== 'undefined') {
	s.pageName ='My Account>Same Day Delivery Congratulations Modal';
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